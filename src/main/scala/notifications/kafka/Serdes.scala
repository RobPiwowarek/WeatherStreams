package notifications.kafka

import com.lightbend.kafka.scala.streams.{Deserializer, ScalaSerde, Serializer}
import notifications.{EmailNotification, JsonSupport, SlackNotification}
import spray.json.JsonParser.ParsingException
import spray.json._

object Serdes {
  object Email extends ScalaSerde[EmailNotification] with JsonSupport {

    final object Deserializer extends Deserializer[EmailNotification] {
      override def deserialize(data: Array[Byte]): Option[EmailNotification] = {
        try {
          val jsonData = new String(data).parseJson
          Some(jsonData.convertTo[EmailNotification])
        }
        catch {
          case e: ParsingException => None
        }
      }
    }

    final object Serializer extends Serializer[EmailNotification] {
      override def serialize(data: EmailNotification): Array[Byte] = {
        data.toJson.toString.getBytes
      }
    }

    override def deserializer() = Deserializer

    override def serializer() = Serializer
  }

  object Slack extends ScalaSerde[SlackNotification] with JsonSupport {

    final object Deserializer extends Deserializer[SlackNotification] {
      override def deserialize(data: Array[Byte]): Option[SlackNotification] = {
        try {
          val jsonData = new String(data).parseJson
          Some(jsonData.convertTo[SlackNotification])
        }
        catch {
          case e: ParsingException => {
            None
          }
        }
      }
    }

    final object Serializer extends Serializer[SlackNotification] {
      override def serialize(data: SlackNotification): Array[Byte] = {
        data.toJson.toString.getBytes
      }
    }

    override def deserializer() = Deserializer

    override def serializer() = Serializer
  }
}
