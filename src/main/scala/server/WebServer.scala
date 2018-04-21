package server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import domain.requests.{AlertRequest, UserLoginRequest, UserRegisterRequest}

object WebServer extends JsonSupport with CorsSupport {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  def main(args: Array[String]): Unit = {
    val route: Route =
      cors() {
        post {
          pathPrefix("api") {
            pathPrefix("user") {
              pathPrefix("login") {
                entity(as[UserLoginRequest]) {
                  request =>
                    handleLoginRequest(request)
                }
              }
            }
          }
        } ~
          put {
            pathPrefix("alert") {
              entity(as[AlertRequest]) {
                request =>
                  handleAlertRequest(request)
              }
            }
          } ~
          put {
            pathPrefix("user") {
              entity(as[UserRegisterRequest]) {
                request =>
                  handleUserRequest(request)
              }
            }
          }

      }

    val corsSupportedRoute = corsSupport(route)

    Http().bindAndHandle(corsSupportedRoute, "0.0.0.0", 8090)
  }

  // todo:
  private def handleAlertRequest(request: AlertRequest) = {
    complete(HttpResponse(StatusCodes.OK, entity = ""))
  }

  // todo:
  private def handleUserRequest(request: UserRegisterRequest) = {


    complete(HttpResponse(StatusCodes.OK, entity = ""))
  }

  // todo:
  private def handleLoginRequest(request: UserLoginRequest) = {
    complete(HttpResponse(StatusCodes.OK, entity = ""))
  }
}
