package notifications.kafka

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import providers.openweathermap.OpenWeatherMapClient
import providers.openweathermap.Responses.Weather
import server.database.MariaDb

object Producer extends Runnable {
  val config = Configs.Producer
  val producer = new KafkaProducer[String, Weather](config.props)

  val client = OpenWeatherMapClient
  val mariaDb = MariaDb

  def getActiveLocations(): Seq[String] = ??? // TODO - fetch locations from mariadb that have active alerts
  def getLocationData(location: String): Weather = ??? // TODO - use client to fetch weather data for given location

  override def run(): Unit = {
    try {
      while (true) {
        // send out weather data for active locations, location name being the message key
        for (location <- getActiveLocations()) {
          val weather = getLocationData(location)
          val data = new ProducerRecord[String, Weather](config.topic, location, weather)
          producer.send(data)
        }
      }
    }
    finally {
      producer.close()
    }
  }
}
