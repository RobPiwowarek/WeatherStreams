package notifications.kafka

import com.lightbend.kafka.scala.streams.{DefaultSerdes, StreamsBuilderS}
import domain.Domain.{ID, Location}
import notifications.{EmailNotification, SlackNotification}
import org.apache.kafka.streams.kstream.Produced
import org.apache.kafka.streams.{Consumed, KafkaStreams}
import providers.openweathermap.Responses.Weather
import server.database.DatabaseInterface
import server.database.model.{AlertDefinition, AlertHistory, DefinitionParameter, User}

// takes weather data and produces email and slack notifications if alerts were triggered
// also saves alert occurrences in the database
class Stream(mariaDb: DatabaseInterface, conf: AlertStreamConfig) extends Runnable {
  def getActiveAlerts(location: String): Seq[AlertDefinition] = mariaDb.getAlertsFromLocation(Location(location))

  def getValue(paramName: String, weather: Weather): BigDecimal = {
    paramName match {
      case "TEMP" => weather.main.temp
      case "WIND" => weather.wind.speed
      case "RAIN" => weather.rain match {
        case Some(rain) => rain.`3h`.get
        case None => BigDecimal("0")
      }
      case "HUMI" => weather.main.humidity
      case "PRES" => weather.main.pressure
      case "CLOU" => BigDecimal(weather.clouds.all)
    }
  }

  def conditionIsMet(param: DefinitionParameter, weather: Weather): Boolean = {
    val value = getValue(param.parameterName, weather)
    param.comparisonType match {
      case 1 => value < param.parameterLimit
      case 2 => value > param.parameterLimit
      case _ => throw new IllegalArgumentException("Invalid comparisonType, expected 1 or 2")
    }
  }

  def getAlertHistory(parameters: Seq[DefinitionParameter], weather: Weather) = {
    def params = Seq("TEMP", "WIND", "RAIN", "HUMI", "PRES", "CLOU")

    val limitMap = params.map {
      paramName => {
        val parameter = parameters.find(_.parameterName == paramName)
        parameter match {
          case Some(defParam) => paramName -> (Some(defParam.parameterLimit), conditionIsMet(defParam, weather))
          case None => paramName -> (None, false)
        }
      }
    }.toMap[String, (Option[Int], Boolean)]

    for {
      paramName <- params
      historyEntry = AlertHistory(-1, -1, paramName, getValue(paramName, weather).toInt, limitMap(paramName)._1)
      isOutOfLimit = limitMap(paramName)._2
    } yield (historyEntry, isOutOfLimit)
  }

  def isEmail(key: String, dummy: Any) = {
    key == "email"
  }

  def isSlack(key: String, dummy: Any) = {
    key == "slack"
  }

  def createStream() = {
    val builder = new StreamsBuilderS()

    val inputStream = builder.stream[String, Weather](
      conf.inputTopic)(
      Consumed.`with`(DefaultSerdes.stringSerde, Serdes.Weather)
    )

    val outputStreams = inputStream
      .flatMap {
        case (location: String, weather: Weather) => {
          def alerts = for {
            alert <- getActiveAlerts(location)
            parameters = mariaDb
              .getAlertDefinitionParameters(alert.id.toInt)
          } yield (alert, parameters)

          val triggeredAlerts = alerts.filter(_._2.forall(conditionIsMet(_, weather)))

          triggeredAlerts
            .foreach {
              case (alert, parameters) => {
                val history = getAlertHistory(parameters, weather)
                // save alert in the database
                // history = Seq[(AlertHistory, Boolean)], Boolean means outOfLimit
                mariaDb.insertAlert(alert, history)
              }
            }

          def emailAlerts = for {
            tuple <- triggeredAlerts.filter(_._1.emailNotif)
            alert = tuple._1
            params = tuple._2
          } yield ("email", (alert, params))

          def slackAlerts = for {
            tuple <- triggeredAlerts.filter(_._1.slackNotif)
            alert = tuple._1
            params = tuple._2
          } yield ("slack", (alert, params))

          emailAlerts ++ slackAlerts
        }
      }
      .branch(isEmail, isSlack)

    // email
    outputStreams(0)
      .mapValues {
        case (alert, parameters) => {
          val user: User = mariaDb.selectUserById(ID(alert.weatherUserId.toInt)).get
          EmailNotification(s"${user.name} ${user.surname}", user.email, alert.location, parameters)
        }
      }
      .to(conf.emailTopic)(Produced.`with`(DefaultSerdes.stringSerde, Serdes.Email))

    // slack
    outputStreams(1)
      .mapValues {
        case (alert, parameters) => {
          val user: User = mariaDb.selectUserById(ID(alert.weatherUserId.toInt)).get
          SlackNotification(user.slackId, alert.location, parameters)
        }
      }
      .to(conf.slackTopic)(Produced.`with`(DefaultSerdes.stringSerde, Serdes.Slack))

    new KafkaStreams(builder.build(), conf.props)
  }

  override def run(): Unit = {
    val streams = createStream()
    streams.start()

    Runtime.getRuntime.addShutdownHook(new Thread {
      override def run(): Unit = streams.close()
    })
  }
}
