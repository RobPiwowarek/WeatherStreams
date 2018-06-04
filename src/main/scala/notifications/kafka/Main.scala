package notifications.kafka

import java.util.concurrent.Executors
import providers.openweathermap.OpenWeatherMapClient
import server.database.MariaDb

object Main extends App {
  val mariaDb = new MariaDb()
  val client = OpenWeatherMapClient

  val pool = Executors.newFixedThreadPool(4)
  pool.submit(Consumers.Email)
  pool.submit(Consumers.Slack)
  pool.submit(new Producer(mariaDb, client, Configs.Producer))
  pool.submit(new Stream(mariaDb, Configs.Stream))
}

