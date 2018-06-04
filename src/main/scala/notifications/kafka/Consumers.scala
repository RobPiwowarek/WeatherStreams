package notifications.kafka

import java.util.Collections

import notifications._
import org.apache.kafka.clients.consumer.KafkaConsumer

class EmailConsumer(sender: Email, config: NotifConsumerConfig) extends Runnable {
  val consumer = new KafkaConsumer[String, EmailNotification](config.props)

  override def run(): Unit = {
    consumer.subscribe(Collections.singletonList(config.topic))
    try {
      while (true) {
        val records = consumer.poll(1000).records(config.topic)

        records.forEach {
          record => {
            sender.send(record.value())
          }
        }
      }
    }
    finally {
      consumer.close()
    }
  }
}

class SlackConsumer(sender: Slack, config: NotifConsumerConfig) extends Runnable {
  val consumer = new KafkaConsumer[String, SlackNotification](config.props)

  override def run(): Unit = {
    consumer.subscribe(Collections.singletonList(config.topic))
    try {
      while (true) {
        val records = consumer.poll(1000).records(config.topic)

        records.forEach {
          record => {
            sender.send(record.value())
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
  val emailSender = new Email(EmailFileConfig)
  val slackSender = new Slack(SlackFileConfig)

  object Email extends EmailConsumer(emailSender, Configs.Email)
  object Slack extends SlackConsumer(slackSender, Configs.Slack)
}
