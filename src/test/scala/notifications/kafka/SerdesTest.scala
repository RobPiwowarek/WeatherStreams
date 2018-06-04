package notifications.kafka

import notifications.{EmailNotification, JsonSupport, SlackNotification}
import org.scalatest.FlatSpec
import providers.openweathermap.Responses.{Clouds, General, Rain, Weather, Wind}
import server.database.model.{AlertDefinition, DefinitionParameter}
import spray.json._

class SerdesTest extends FlatSpec with JsonSupport {
  val alertDef = AlertDefinition(0, 0, "", 0, "", false, false, false)
  val params = Seq(DefinitionParameter(0, 0, "test", 0, 1, "test"), DefinitionParameter(1, 0, "test2", 10, 2, "test2"))
  val email = EmailNotification("test", "test@example.com", alertDef, params)
  val slack = SlackNotification("test", alertDef, params)
  val weather = Weather(
    General(None, 0, 0, None, 30, 0, 0),
    Wind(10, 0),
    Clouds(0),
    Some(Rain(Some(40))),
    None,
    List(),
    0
  )

  behavior of "EmailSerde"
  it should "serialize an EmailNotification to json" in {
    val serde = Serdes.Email.Serializer
    val data = serde.serialize(email)

    val actualEmail = new String(data).parseJson.convertTo[EmailNotification]

    assert(email == actualEmail)
  }

  it should "deserialize json data to an EmailNotification" in {
    val serde = Serdes.Email.Deserializer
    val data = email.toJson.toString.getBytes

    val actualEmail = serde.deserialize(data)

    assert(actualEmail.nonEmpty)
    assert(email == actualEmail.get)
  }

  behavior of "SlackSerde"
  it should "serialize a SlackNotification to json" in {
    val serde = Serdes.Slack.Serializer
    val data = serde.serialize(slack)

    val actualSlack = new String(data).parseJson.convertTo[SlackNotification]

    assert(slack == actualSlack)
  }

  it should "deserialize json data to a SlackNotification" in {
    val serde = Serdes.Slack.Deserializer
    val data = slack.toJson.toString.getBytes

    val actualSlack = serde.deserialize(data)

    assert(actualSlack.nonEmpty)
    assert(slack == actualSlack.get)
  }

  behavior of "WeatherSerde"
  it should "serialize a Weather to json" in {
    val serde = Serdes.Weather.Serializer
    val data = serde.serialize(weather)

    val actualWeather = new String(data).parseJson.convertTo[Weather]

    assert(weather == actualWeather)
  }

  it should "deserialize json data to a Weather" in {
    val serde = Serdes.Weather.Deserializer
    val data = weather.toJson.toString.getBytes

    val actualWeather = serde.deserialize(data)

    assert(actualWeather.nonEmpty)
    assert(weather == actualWeather.get)
  }
}
