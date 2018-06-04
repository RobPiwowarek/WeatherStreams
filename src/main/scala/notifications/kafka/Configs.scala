package notifications.kafka

import java.util.Properties

import com.lightbend.kafka.scala.streams.DefaultSerdes
import com.typesafe.config.ConfigFactory
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.streams.StreamsConfig
import providers.WeatherClient
import providers.openweathermap.OpenWeatherMapClient
import server.database.{DatabaseInterface, MariaDb}

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

trait WeatherProducerConfig {
  val clientId: String
  val server: String
  val topic: String
  val serde: AnyRef

  def props = {
    val properties = new Properties()
    properties.put(ProducerConfig.CLIENT_ID_CONFIG, clientId)

    properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, server)

    properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")

    properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, serde.getClass)

    properties
  }
}

trait AlertStreamConfig {
  val appId: String
  val server: String
  val inputTopic: String
  val emailTopic: String
  val slackTopic: String

  def props = {
    val properties = new Properties()
    properties.put(StreamsConfig.APPLICATION_ID_CONFIG, appId)

    properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, server)

    properties.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, "500")

    properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, DefaultSerdes.stringSerde.getClass)

    properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, DefaultSerdes.stringSerde.getClass)

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

  object Producer extends WeatherProducerConfig {
    val clientId = conf.getString("kafka.fetcher.clientId")
    val server = conf.getString("kafka.server")
    val topic = conf.getString("kafka.fetcher.topic")
    val serde = Serdes.Weather.Serializer
  }

  object Stream extends AlertStreamConfig {
    val appId = conf.getString("kafka.stream.appId")
    val server = conf.getString("kafka.server")
    val inputTopic = Producer.topic
    val emailTopic = Email.topic
    val slackTopic = Slack.topic
  }
}
