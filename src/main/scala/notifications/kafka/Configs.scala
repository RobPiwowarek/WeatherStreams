package notifications.kafka

import java.util.Properties

import com.typesafe.config.ConfigFactory
import org.apache.kafka.clients.consumer.ConsumerConfig

trait NotifConsumerConfig {
  val groupId: String
  val server: String
  val topic: String
  val serde: AnyRef

  def props = {
    val properties = new Properties()
    properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId)

    properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, server)

    properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
    properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000")
    properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000")

    properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")

    properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, serde.getClass)

    properties
  }
}

object Configs {
  val conf = ConfigFactory.load()

  object Email extends NotifConsumerConfig {
    val groupId = conf.getString("kafka.email.groupId")
    val server = conf.getString("kafka.server")
    val topic = conf.getString("kafka.email.topic")
    val serde = Serdes.Email.Deserializer
  }

  object Slack extends NotifConsumerConfig {
    val groupId = conf.getString("kafka.slack.groupId")
    val server = conf.getString("kafka.server")
    val topic = conf.getString("kafka.slack.topic")
    val serde = Serdes.Slack.Deserializer
  }
}
