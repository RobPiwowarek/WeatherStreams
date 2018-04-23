package providers.openweathermap

import com.netaporter.uri.Uri
import com.netaporter.uri.dsl._
import com.typesafe.config.ConfigFactory
import org.asynchttpclient.Dsl._
import providers.WeatherClient

object OpenWeatherMapClient extends WeatherClient{
  val config = ConfigFactory.load()

  val endpointBase = Uri.parse(config.getString("open-weather-map.endpoint"))
  val apiKey = ApiKey(config.getString("open-weather-map.api-key"))
  val weatherEndpoint = endpointBase / "weather"
  val forecastEndpoint = endpointBase / "forecast"

  val httpClient = asyncHttpClient()

    def getWeatherData(params: Seq[(String, String)]) = {
      val url = weatherEndpoint.addParams(params) & ("appid", apiKey.value)
      val whenResponse = httpClient.prepareGet(weatherEndpoint.addParams(params) & ("appid", apiKey.value)).execute()
      println(url.toString)
      whenResponse.get
    }

  def getForecastData(params: Seq[(String, String)]) = {
    val whenResponse = httpClient.prepareGet(forecastEndpoint.addParams(params) & ("appid", apiKey)).execute()
    whenResponse.get
  }
}