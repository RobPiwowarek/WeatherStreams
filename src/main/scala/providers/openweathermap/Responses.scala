package providers.openweathermap

object Responses {

  sealed trait OpenWeatherMapResponse

  final case class Weather(general: General,
                           wind: Wind,
                           clouds: Clouds,
                           rain: Option[Rain],
                           snow: Option[Snow],
                           weather: List[WeatherCondition],
                           timestamp: BigInt) extends OpenWeatherMapResponse


  case class General(groundLevel: Option[BigDecimal],
                     humidity: BigDecimal,
                     pressure: BigDecimal,
                     seaLevel: Option[BigDecimal],
                     temp: BigDecimal,
                     tempMin: BigDecimal,
                     tempMax: BigDecimal)

  case class WeatherCondition(main: String,
                              description: String,
                              id: Int,
                              icon: String)

  case class Wind(speed: BigDecimal,
                  degrees: BigDecimal)

  case class Coordinates(longitude: BigDecimal,
                         latitude: BigDecimal)

  // cloudines == all
  case class Clouds(cloudiness: BigInt)

  case class Snow(lastThreeHours: Option[BigDecimal])

  case class Rain(lastThreeHours: Option[BigDecimal])
}