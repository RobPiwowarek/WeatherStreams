package providers.openweathermap

object Errors {
  sealed trait WeatherError {
    val message: String
    override def toString = s"OpenWeatherMap ${this.getClass.getSimpleName} " + message
  }

  case class TimeoutError(message: String) extends java.util.concurrent.TimeoutException(message) with WeatherError

  case class InternalError(message: String) extends WeatherError

  case class ParseError(message: String) extends WeatherError
}
