package providers.openweathermap

import spray.json.DefaultJsonProtocol

object Responses extends DefaultJsonProtocol {

  final case class Weather(main: General,
                           wind: Wind,
                           clouds: Clouds,
                           rain: Option[Rain],
                           snow: Option[Snow],
                           weather: List[WeatherCondition],
                           dt: BigInt) //dt == timestamp

  final case class General(grnd_level: Option[BigDecimal],
                           humidity: BigDecimal,
                           pressure: BigDecimal,
                           sea_level: Option[BigDecimal],
                           temp: BigDecimal,
                           temp_min: BigDecimal,
                           temp_max: BigDecimal)

  final case class WeatherCondition(main: String,
                                    description: String,
                                    id: Int,
                                    icon: String)

  final case class Wind(speed: BigDecimal,
                        deg: BigDecimal)

  // longitutde, latitude
  final case class Coordinates(lon: BigDecimal,
                               lat: BigDecimal)

  // cloudines == all
  final case class Clouds(all: BigInt)

  final case class Snow(`3h`: Option[BigDecimal])

  final case class Rain(`3h`: Option[BigDecimal])

  implicit val rainFormat = jsonFormat1(Rain)
  implicit val snowFormat = jsonFormat1(Snow)
  implicit val cloudsFormat = jsonFormat1(Clouds)
  implicit val coordinatesFormat = jsonFormat2(Coordinates)
  implicit val windFormat = jsonFormat2(Wind)
  implicit val weatherConditionFormat = jsonFormat4(WeatherCondition)
  implicit val generalFormat = jsonFormat7(General)
  implicit val weatherFormat = jsonFormat7(Weather)
}