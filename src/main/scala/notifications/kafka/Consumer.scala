package notifications.kafka

import java.util.Collections

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.stream.ActorMaterializer
import notifications._
import org.apache.kafka.clients.consumer.KafkaConsumer

class Consumer[Notification](sender: ActorRef, config: Config) extends Runnable {
  val consumer = new KafkaConsumer[String, Notification](config.props)

  override def run(): Unit = {
    consumer.subscribe(Collections.singletonList(config.topic))
    try {
      while (true) {
        val records = consumer.poll(1000).records(config.topic)

        records.forEach {
          record => {
            sender ! record.value()
          }
        }
      }
    }
    finally {
      consumer.close()
    }
  }
}

// provides ready-to-run Runnables for Email and Slack
// simply run them in a Java-style thread pool
object Consumers {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val emailSender = system.actorOf(Props(classOf[Email], EmailFileConfig), name = "EmailSender")
  val slackSender = system.actorOf(Props(classOf[Slack], SlackFileConfig), name = "SlackSender")

  object Email extends Consumer[EmailNotification](emailSender, Configs.Email)
  object Slack extends Consumer[SlackNotification](slackSender, Configs.Slack)
}
