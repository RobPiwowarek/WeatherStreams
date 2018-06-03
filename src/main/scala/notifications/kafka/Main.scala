package notifications.kafka

import java.util.concurrent.Executors

object Main extends App {
  val pool = Executors.newFixedThreadPool(2)
  pool.submit(Consumers.Email)
  pool.submit(Consumers.Slack)
}

