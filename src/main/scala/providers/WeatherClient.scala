package providers

trait WeatherClient {
  def getWeatherData(params: Seq[(String, String)]): WeatherResponse
}

trait WeatherResponse