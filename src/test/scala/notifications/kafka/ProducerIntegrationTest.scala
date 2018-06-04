package notifications.kafka

import java.util.concurrent.Executors

import domain.Domain.Location
import net.manub.embeddedkafka.{EmbeddedKafka, EmbeddedKafkaConfig}
import notifications.JsonSupport
import org.apache.kafka.common.serialization.Deserializer
import org.scalamock.scalatest.MockFactory
import org.scalatest.FlatSpec
import providers.WeatherClient
import providers.openweathermap.Responses._
import server.database.DatabaseInterface

class ProducerIntegrationTest extends FlatSpec with EmbeddedKafka with MockFactory with JsonSupport {
  val fakeWeather = Weather(
    General(None, 0, 0, None, 30, 0, 0),
    Wind(10, 0),
    Clouds(0),
    Some(Rain(Some(40))),
    None,
    List(),
    0
  )

  behavior of "Producer"
  it should "fetch data from the client and send it to the topic" in {
    val mariaDbStub = stub[DatabaseInterface]
    mariaDbStub.getLocationsWithActiveAlerts _ when() returns Seq(Location("Test"))

    val clientStub = stub[WeatherClient]
    clientStub.getWeatherData _ when Seq(("q", "Test")) returns fakeWeather

    // run embedded kafka on an arbitrary port that is available
    val userDefinedConfig = EmbeddedKafkaConfig(kafkaPort = 0, zooKeeperPort = 0)

    withRunningKafkaOnFoundPort(userDefinedConfig) { implicit actualConfig =>
      implicit val deserializer: Deserializer[Weather] = Serdes.Weather.Deserializer

      val config = new WeatherProducerConfig {
        override val server: String = s"localhost:${actualConfig.kafkaPort}"
        override val clientId: String = "producerTest"
        override val serde: AnyRef = Serdes.Weather.Serializer
        override val topic: String = "producerTest"
      }

      val producer = new Producer(mariaDbStub, clientStub, config)
      val pool = Executors.newFixedThreadPool(1)
      pool.submit(producer)

      val weather = consumeFirstMessageFrom(config.topic, true)

      pool.shutdown()
      assert(weather == fakeWeather)
    }
  }
}
