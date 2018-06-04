package notifications.kafka

import com.lightbend.kafka.scala.streams.DefaultSerdes
import domain.Domain
import domain.Domain.{ID, Location}
import domain.api.{AlertDefinitionParameter, AlertDefinitionRequest, UserUpdateRequest}
import net.manub.embeddedkafka.{EmbeddedKafka, EmbeddedKafkaConfig}
import notifications.{EmailNotification, JsonSupport, SlackNotification}
import org.apache.kafka.common.serialization.{Deserializer, Serializer}
import org.scalamock.scalatest.MockFactory
import org.scalatest.FlatSpec
import providers.openweathermap.Responses.{Clouds, General, Rain, Weather, Wind}
import server.database.DatabaseInterface
import server.database.model._
import spray.json._

class StreamIntegrationTest extends FlatSpec with EmbeddedKafka with MockFactory with JsonSupport {
  // temp = 25
  // wind = 10
  // rain = 40
  // humi = 0
  val fakeWeather = Weather(
    General(None, 0, 0, None, 25, 0, 0),
    Wind(10, 0),
    Clouds(0),
    Some(Rain(Some(40))),
    None,
    List(),
    0
  )

  val user = User(0, "test@example.com", "test", "testSlack", "Test", "User")

  // run embedded kafka on an arbitrary port that is available
  val userDefinedConfig = EmbeddedKafkaConfig(kafkaPort = 0, zooKeeperPort = 0)

  behavior of "Stream"
  it should "fetch data from the weather-data topic and send notifications accordingly" in {
    val alerts = Seq(AlertDefinition(0, 0, "testEmailSlack", 0, "Test", true, true, true))

    val params = Seq(DefinitionParameter(3, 2, "RAIN", 50, 1, "mm"))

    val expectedEmail = EmailNotification(s"${user.name} ${user.surname}", user.email, alerts.head.location, params)
    val expectedSlack = SlackNotification(user.slackId, alerts.head.location, params)

    val mariaDbStub = stub[DatabaseInterface]
    mariaDbStub.getAlertsFromLocation _ when Location("Test") returns alerts
    mariaDbStub.selectUserById _ when ID(0) returns Some(user)
    mariaDbStub.getAlertDefinitionParameters _ when 0 returns params

    withRunningKafkaOnFoundPort(userDefinedConfig) { implicit actualConfig =>
      implicit val keySerializer = DefaultSerdes.stringSerde.serializer()
      implicit val keyDeserializer = DefaultSerdes.stringSerde.deserializer()
      implicit val valueSerializer: Serializer[Weather] = Serdes.Weather.Serializer
      implicit val valueDeserializer: Deserializer[Weather] = Serdes.Weather.Deserializer

      val config = new AlertStreamConfig {
        override val server: String = s"localhost:${actualConfig.kafkaPort}"
        override val appId: String = "streamTest"
        override val emailTopic: String = "emailTest"
        override val slackTopic: String = "slackTest"
        override val inputTopic: String = "weatherTest"
      }

      publishToKafka[String, Weather](config.inputTopic, "Test", fakeWeather)

      val stream = new Stream(mariaDbStub, config)
      val kafkaStream = stream.createStream()

      kafkaStream.start()

      val emailMsg = consumeFirstStringMessageFrom(config.emailTopic, true)
      val actualEmail = emailMsg.parseJson.convertTo[EmailNotification]

      val slackMsg = consumeFirstStringMessageFrom(config.slackTopic, true)
      val actualSlack = slackMsg.parseJson.convertTo[SlackNotification]

      assert(actualEmail == expectedEmail)
      assert(actualSlack == expectedSlack)

      kafkaStream.close()
    }
  }

  it should "fetch data from the weather-data topic and save alerts in the database" in {
    val alerts = Seq(
      AlertDefinition(0, 0, "testEmailSlack", 0, "Test", true, true, true)
    )

    val params = Seq(
      DefinitionParameter(3, 2, "RAIN", 50, 1, "mm")
    )

    // very ugly way to workaround mocks/stubs not verifying calls from other threads
    val mariaDbStub = new DatabaseInterface {
      var insertCalled = false

      override def insertAlert(definition: AlertDefinition, parameters: Seq[(DefinitionParameter, Int)]): Unit = {
        assert(definition == alerts.head)
        assert(parameters.length == 1)
        assert(parameters.head == (params.head, 40))
        insertCalled = true
      }

      override def getAlertsFromLocation(location: Location): Seq[AlertDefinition] = alerts

      override def selectUserById(id: ID): Option[User] = Some(user)

      override def getAlertDefinitionParameters(definitionId: Int): Seq[DefinitionParameter] = params

      override def updateAlertDefinitionParameter(id: ID, param: AlertDefinitionParameter): Int = 0

      override def insertAlertDefinitionParameter(id: Long, param: AlertDefinitionParameter): Int = 0

      override def deleteAlert(id: Int): Int = 0

      override def getAlertList(userId: Int): Seq[Alert] = Seq()

      override def insertAlertHistory(id: Long, param: DefinitionParameter, value: Int): Unit = {}

      override def updateAlertDefinition(alertRequest: AlertDefinitionRequest): Unit = {}

      override def updateUser(updateUserRequest: UserUpdateRequest): Int = 0

      override def selectUser(username: Domain.Email): Option[User] = None

      override def getAlertHistoryList(alertId: Int): Seq[AlertHistory] = Seq()

      override def getAlertDefinitions(userId: Int): Seq[(AlertDefinition, Seq[DefinitionParameter])] = Seq()

      override def insertAlertDefinition(alertRequest: AlertDefinitionRequest): Unit = {}

      override def deleteAlertDefinition(id: Int): Int = 0

      override def getLocationsWithActiveAlerts(): Seq[Location] = Seq(Location("Test"))
    }

    withRunningKafkaOnFoundPort(userDefinedConfig) { implicit actualConfig =>
      implicit val keySerializer = DefaultSerdes.stringSerde.serializer()
      implicit val keyDeserializer = DefaultSerdes.stringSerde.deserializer()
      implicit val valueSerializer: Serializer[Weather] = Serdes.Weather.Serializer
      implicit val valueDeserializer: Deserializer[Weather] = Serdes.Weather.Deserializer

      val config = new AlertStreamConfig {
        override val server: String = s"localhost:${actualConfig.kafkaPort}"
        override val appId: String = "streamTest"
        override val emailTopic: String = "emailTest"
        override val slackTopic: String = "slackTest"
        override val inputTopic: String = "weatherTest"
      }

      publishToKafka[String, Weather](config.inputTopic, "Test", fakeWeather)

      val stream = new Stream(mariaDbStub, config)
      val kafkaStream = stream.createStream()

      kafkaStream.start()
      Thread.sleep(10 * 1000)
      assert(mariaDbStub.insertCalled)

      kafkaStream.close()
    }
  }
}
