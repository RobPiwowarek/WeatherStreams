package server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
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
          } ~
          post {
            pathPrefix("api/user/login") {
              entity(as[UserLoginRequest]) {
                request =>
                  handleLoginRequest(request)
              }
            }
          }
      }

    val corsSupportedRoute = corsSupport(route)

    Http().bindAndHandle(corsSupportedRoute, "localhost", 8090)
  }

  // todo:
  private def handleAlertRequest(request: AlertRequest) = {
    complete(StatusCodes.OK)
  }

  // todo:
  private def handleUserRequest(request: UserRegisterRequest) = {


    complete(StatusCodes.OK)
  }

  // todo:
  private def handleLoginRequest(request: UserLoginRequest) = {
    complete(StatusCodes.OK)
  }
}
