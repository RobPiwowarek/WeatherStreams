package notifications.kafka

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import providers.WeatherClient
import providers.openweathermap.Responses.Weather
import server.database.DatabaseInterface

class Producer(mariaDb: DatabaseInterface, client: WeatherClient, config: WeatherProducerConfig) extends Runnable {
  val producer = new KafkaProducer[String, Weather](config.props)

  def getActiveLocations(): Seq[String] = mariaDb.getLocationsWithActiveAlerts().map(_.value)

  def getLocationData(location: String): Weather = {
    client.getWeatherData(Seq(("q", location)))
  }

  def produceLocationData(location: String) = {
    val weather = getLocationData(location)
    val data = new ProducerRecord[String, Weather](config.topic, location, weather)
    producer.send(data)
  }

  override def run(): Unit = {
    try {
      while (true) {
        // send out weather data for active locations, location name being the message key
        for (location <- getActiveLocations()) {
          produceLocationData(location)
        }
        Thread.sleep(config.delay.toMillis)
      }
    }
    finally {
      producer.close()
    }
  }
}
