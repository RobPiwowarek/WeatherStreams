package providers.openweathermap

import com.netaporter.uri.Uri
import com.netaporter.uri.dsl._
import org.asynchttpclient.Dsl._

object OpenWeatherMapClient {
  implicit val apiKey = ApiKey("84ab538642f8529e836220e3232f7940")

  val endpointBase = Uri.parse("api.openweathermap.org/data/2.5/")
  val weatherEndpoint = endpointBase / "weather?"
  val forecastEndpoint = endpointBase / "forecast?"

  val httpClient = asyncHttpClient()

  def getWeatherData(params: Seq[(String, String)]) =
    httpClient.prepareGet(weatherEndpoint.addParams(params) & ("appid", apiKey)).execute()

  def getForecastData(params: Seq[(String, String)]) =
    httpClient.prepareGet(forecastEndpoint.addParams(params) & ("appid", apiKey)).execute()
}
