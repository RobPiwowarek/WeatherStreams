package notifications.kafka

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import providers.WeatherClient
import providers.openweathermap.OpenWeatherMapClient
import providers.openweathermap.Responses.Weather
import server.database.DatabaseInterface

class Producer(mariaDb: DatabaseInterface, client: WeatherClient) extends Runnable {
  val config = Configs.Producer
  val producer = new KafkaProducer[String, Weather](config.props)

  def getActiveLocations(): Seq[String] = mariaDb.getLocationsWithActiveAlerts().map(_.value)

  def getLocationData(location: String): Weather = {
    client.getWeatherData(Seq(("q", location)))
  }

  override def run(): Unit = {
    try {
      while (true) {
        // send out weather data for active locations, location name being the message key
        for (location <- getActiveLocations()) {
          val weather = getLocationData(location)
          val data = new ProducerRecord[String, Weather](config.topic, location, weather)
          producer.send(data)
        }
        Thread.sleep(10 * 1000) // every 10 minutes
      }
    }
    finally {
      producer.close()
    }
  }
}
