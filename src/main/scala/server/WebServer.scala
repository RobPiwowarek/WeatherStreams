package server

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import providers.WeatherClient
import providers.openweathermap._
import server.database.MariaDbProvider

object WebServer extends CorsSupport {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val routeProvider = new WebServerRouteProvider(new MariaDbProvider)
  val weatherClient: WeatherClient = OpenWeatherMapClient
  def main(args: Array[String]): Unit = {
    val route: Route = routeProvider.routeSetup()

    val corsSupportedRoute = corsSupport(route)

    Http().bindAndHandle(corsSupportedRoute, "0.0.0.0", 8090)
  }
}
