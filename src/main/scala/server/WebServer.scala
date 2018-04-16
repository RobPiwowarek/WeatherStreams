package server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import domain.requests.{AlertRequest, LoginRequest, UserRequest}

object WebServer extends JsonSupport with CorsSupport {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  def main(args: Array[String]): Unit = {
    val route: Route =
      ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors() {
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
              entity(as[UserRequest]) {
                request =>
                  handleUserRequest(request)
              }
            }
          } ~
          post {
            pathPrefix("login") {
              entity(as[LoginRequest]) {
                request =>
                  handleLoginRequest(request)
              }
            }
          }
      }

    val corsSupportedRoute = corsSupport(route)

    val bindingFuture = Http().bindAndHandle(corsSupportedRoute, "localhost", 8090)
  }

  // todo:
  private def handleAlertRequest(request: AlertRequest) = {
    complete(StatusCodes.OK)
  }

  // todo:
  private def handleUserRequest(request: UserRequest) = {
    complete(StatusCodes.OK)
  }

  // todo:
  private def handleLoginRequest(request: LoginRequest) = {
    complete(StatusCodes.OK)
  }
}
