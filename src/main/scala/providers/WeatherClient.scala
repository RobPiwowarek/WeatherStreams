package providers

import providers.openweathermap.Responses.Weather

trait WeatherClient {
  def getWeatherData(params: Seq[(String, String)]): Weather
}
