package providers.openweathermap

import com.netaporter.uri.Uri
import com.netaporter.uri.dsl._
import org.asynchttpclient.Dsl._
import providers.WeatherClient

object OpenWeatherMapClient extends WeatherClient{
  implicit val apiKey = ApiKey("84ab538642f8529e836220e3232f7940")

  val endpointBase = Uri.parse("http://api.openweathermap.org/data/2.5")
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

