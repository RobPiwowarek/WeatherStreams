package providers.openweathermap

object Requests {
  sealed trait OpenWeatherMapRequest {
    def query(implicit apiKey: ApiKey)
  }




}

final case class ApiKey(value: String)