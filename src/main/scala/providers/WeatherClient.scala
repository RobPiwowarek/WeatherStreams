package providers

import org.asynchttpclient.Response

trait WeatherClient {
  def getWeatherData(params: Seq[(String, String)]): Response

  def getForecastData(params: Seq[(String, String)]): Response
}
