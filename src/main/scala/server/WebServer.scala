package server

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import domain.requests.{AlertRequest, LoginRequest, UserRequest}

object WebServer extends JsonSupport {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  def main(args: Array[String]): Unit = {
    val route: Route =
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
