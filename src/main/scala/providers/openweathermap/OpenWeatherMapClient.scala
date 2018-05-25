package providers.openweathermap

import com.netaporter.uri.Uri
import com.netaporter.uri.dsl._
import com.typesafe.config.ConfigFactory
import org.asynchttpclient.Dsl._
import providers.WeatherClient
import providers.openweathermap.Responses.Weather
import spray.json._

object OpenWeatherMapClient extends WeatherClient {
  val config = ConfigFactory.load()

  val endpointBase = Uri.parse(config.getString("open-weather-map.endpoint"))
  val apiKey = ApiKey(config.getString("open-weather-map.api-key"))
  val weatherEndpoint = endpointBase / "weather"
  val forecastEndpoint = endpointBase / "forecast"

  val httpClient = asyncHttpClient()

  def getWeatherData(params: Seq[(String, String)]) = {
    val url = weatherEndpoint.addParams(params) & ("appid", apiKey.value)
    val response = httpClient.prepareGet(weatherEndpoint.addParams(params) & ("appid", apiKey.value)).execute().get()
    response.getResponseBody().parseJson.convertTo[Weather]
  }
}