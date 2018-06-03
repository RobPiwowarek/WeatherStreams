package notifications.kafka

import java.util.concurrent.Executors

import server.database.MariaDb

object Main extends App {
  val mariaDb = new MariaDb()
  val pool = Executors.newFixedThreadPool(4)
  pool.submit(Consumers.Email)
  pool.submit(Consumers.Slack)
  pool.submit(new Producer(mariaDb))
  pool.submit(new Stream(mariaDb))
}

