package notifications.kafka

import org.scalamock.scalatest.MockFactory
import org.scalatest.FlatSpec
import providers.openweathermap.Responses.{Clouds, General, Rain, Weather, Wind}
import server.database.DatabaseInterface
import server.database.model.DefinitionParameter

class StreamTest extends FlatSpec with MockFactory {
  // temp = 25
  // wind = 10
  // rain = 40
  // humi = 0
  val fakeWeather = Weather(
    General(None, 0, 0, None, 25, 0, 0),
    Wind(10, 0),
    Clouds(0),
    Some(Rain(Some(40))),
    None,
    List(),
    0
  )

  behavior of "Stream"
  it should "detect if conditions for a TEMP parameter were met" in {
    val mariaDbStub = stub[DatabaseInterface]
    val stream = new Stream(mariaDbStub, Configs.Stream)

    assert(!stream.conditionIsMet(DefinitionParameter(0, 0, "TEMP", 0, 1, "C"), fakeWeather)) // temp < 0
    assert(stream.conditionIsMet(DefinitionParameter(0, 0, "TEMP", 0, 2, "C"), fakeWeather)) // temp > 0
  }

  it should "detect if conditions for a WIND parameter were met" in {
    val mariaDbStub = stub[DatabaseInterface]
    val stream = new Stream(mariaDbStub, Configs.Stream)

    assert(!stream.conditionIsMet(DefinitionParameter(0, 0, "WIND", 10, 2, "mph"), fakeWeather)) // wind > 10
    assert(stream.conditionIsMet(DefinitionParameter(0, 0, "WIND", 20, 1, "mph"), fakeWeather)) // wind < 20
  }

  it should "detect if conditions for a RAIN parameter were met" in {
    val mariaDbStub = stub[DatabaseInterface]
    val stream = new Stream(mariaDbStub, Configs.Stream)

    assert(!stream.conditionIsMet(DefinitionParameter(0, 0, "RAIN", 50, 2, "mm"), fakeWeather)) // rain > 50
    assert(stream.conditionIsMet(DefinitionParameter(0, 0, "RAIN", 10, 2, "mm"), fakeWeather)) // rain > 10
  }

  it should "detect if conditions for a HUMI parameter were met" in {
    val mariaDbStub = stub[DatabaseInterface]
    val stream = new Stream(mariaDbStub, Configs.Stream)

    assert(!stream.conditionIsMet(DefinitionParameter(0, 0, "HUMI", 50, 2, "%"), fakeWeather)) // humi > 50
    assert(stream.conditionIsMet(DefinitionParameter(0, 0, "HUMI", 10, 1, "%"), fakeWeather)) // humi < 10
  }
}
