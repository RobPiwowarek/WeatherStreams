package notifications.kafka

import com.lightbend.kafka.scala.streams.{DefaultSerdes, StreamsBuilderS}
import notifications.{EmailNotification, SlackNotification}
import org.apache.kafka.streams.{Consumed, KafkaStreams}
import org.apache.kafka.streams.kstream.Produced
import providers.openweathermap.Responses.Weather
import server.database.MariaDb
import server.database.model.{AlertDefinition, DefinitionParameter, User}

// takes weather data and produces email and slack notifications if alerts were triggered
// also saves alert occurrences in the database
object Stream extends Runnable {
  val conf = Configs.Stream
  val builder = new StreamsBuilderS()

  val mariaDb = MariaDb

  val inputStream = builder.stream[String, Weather](
    conf.inputTopic)(
    Consumed.`with`[String, Weather](DefaultSerdes.stringSerde, Serdes.Weather)
  )

  def getActiveAlerts(location: String): Seq[AlertDefinition] = ???

  def conditionIsMet(alert: AlertDefinition, weather: Weather): Boolean = ???

  def alerts = for {
    record <- inputStream
    weather <- record._2
    allAlerts = getActiveAlerts(record._1).filter(conditionIsMet(_, weather))
    emailAlerts = allAlerts.filter(_.emailNotif)
    slackAlerts = allAlerts.filter(_.slackNotif)
  } yield (emailAlerts, slackAlerts)

  def isEmail(key: String, alertDef: AlertDefinition) = {
    key == "email"
  }

  def isSlack(key: String, alertDef: AlertDefinition) = {
    key == "slack"
  }

  override def run(): Unit = {
    val streams = new KafkaStreams(builder.build(), conf.props)

    val outputStreams = inputStream
      .flatMap {
        case (location: String, weather: Weather) => {
          def alerts = for {
            alert <- getActiveAlerts(location)
              .filter(_.active)
              .filter(conditionIsMet(_, weather))
          } yield (alert)

          alerts.foreach {
            alert => {
              // TODO - save Alert occurrence to database?
            }
          }
          
          def emailAlerts = for {
            alert <- alerts.filter(_.emailNotif)
          } yield ("email", alert)

          def slackAlerts = for {
            alert <- alerts.filter(_.slackNotif)
          } yield ("slack", alert)

          emailAlerts ++ slackAlerts
        }
      }
      .branch(isEmail, isSlack)

    // email
    outputStreams(0)
      .mapValues {
        alert => {
          val user: User = ??? // TODO - get user data?
          val param: DefinitionParameter = ??? // TODO ???
          EmailNotification(s"${user.name} ${user.surname}", user.email, param.toString) // TODO
        }
      }
      .to(conf.emailTopic)(Produced.`with`(DefaultSerdes.stringSerde, Serdes.Email))

    // slack
    outputStreams(1)
      .mapValues {
        alert => {
          val user: User = ??? // TODO - get user data?
          val param: DefinitionParameter = ??? // TODO ???
          SlackNotification(user.slackId, param.toString) // TODO
        }
      }
      .to(conf.slackTopic)(Produced.`with`(DefaultSerdes.stringSerde, Serdes.Slack))

    try {
      streams.start()
    }
    finally {
      streams.close()
    }
  }
}
