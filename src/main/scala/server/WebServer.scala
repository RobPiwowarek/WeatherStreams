package server

import java.time.Instant

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import domain.Domain._
import domain.api._
import notifications.{EmailFileConfig, EmailNotification, Sender, SlackFileConfig}
import providers.WeatherClient
import providers.openweathermap._
import server.database.MariaDb
import server.database.model.User
import spray.json._

object WebServer extends JsonSupport with CorsSupport {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val weatherClient: WeatherClient = OpenWeatherMapClient

  val notificationSender: ActorRef = system.actorOf(Props(classOf[Sender], EmailFileConfig, SlackFileConfig), name = "NotificationSender")

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
        } ~ post {
          pathPrefix("api") {
            pathPrefix("user") {
              entity(as[UserUpdateRequest]) {
                request =>
                  handleUpdateUserRequest(request)
              }
            }
          }
        } ~ get {
          path("api" / "config" / "definition" / "user" / IntNumber) {
            int =>
              handleAlertDefinitionGet(int)
          }
        } ~ put {
          path("api" / "config" / "definition") {
            entity(as[AlertDefinitionRequest]) {
              request =>
                handleAlertDefinitionAdd(request)
            }
          }
        } ~ post {
          path("api" / "config" / "definition") {
            entity(as[AlertDefinitionRequest]) {
              request =>
                handleAlertDefinitionUpdate(request)
            }
          }
        } ~ post {
          path("api" / "config" / "definition" / IntNumber) {
            int =>
              handleAlertDefinitionDelete(int)
          }
        } ~ get {
          path("api" / "alert" / "user" / IntNumber) {
            int => handleGetAlertList(int)
          }
        } ~ post {
          path ("api" / )
        }

        put {
          pathPrefix("alert") {
            entity(as[AlertHistoryResponse]) {
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

  private def handleAlertDefinitionDelete(id: Int) = {
    MariaDb.deleteAlertDefinition(id)
    complete(StatusCodes.OK)
  }

  private def handleAlertDefinitionUpdate(request: AlertDefinitionRequest) = {
    MariaDb.updateAlertDefinition(request)
    complete(StatusCodes.OK)
  }

  private def handleAlertDefinitionAdd(request: AlertDefinitionRequest) = {
    MariaDb.insertAlertDefinition(request)
    complete(StatusCodes.OK)
  }

  private def handleAlertDefinitionGet(id: Int) = {
    complete(
      StatusCodes.OK,
      MariaDb
        .getAlertDefinitions(id)
        .map {
          case (defi, params) =>
            AlertDefinitionResponse(
              ID(defi.id.toInt),
              ID(defi.weatherUserId.toInt),
              Name(defi.alertName),
              Duration(defi.duration),
              Location(defi.location),
              defi.active,
              defi.emailNotif,
              defi.slackNotif,
              Instant.now.getEpochSecond.toInt,
              params.map(param => AlertDefinitionParameter(ID(param.id.toInt), Name(param.parameterName), param.parameterLimit, param.comparisonType, param.unit))
            )
        }
        .map(_.toJson.toString)
    )
  }

  // todo:
  private def handleAlertRequest(request: AlertHistoryResponse) = {
    complete(HttpResponse(StatusCodes.OK))
  }

  // todo:
  private def handleUserRequest(request: UserRegisterRequest) = {
    complete(HttpResponse(StatusCodes.OK))
  }

  // todo:
  private def handleLoginRequest(request: UserLoginRequest) = {
    MariaDb.selectUser(request.username) match {
      case Some(user) =>
        if (user.password.equals(request.password.value))
          complete(HttpResponse(StatusCodes.OK, entity = userToUserLoginResponse(user).toJson.toString))
        else
          complete(HttpResponse(StatusCodes.Unauthorized))
      case None =>
        complete(HttpResponse(StatusCodes.Unauthorized))
    }
  }

  private def handleUpdateUserRequest(request: UserUpdateRequest) = {
    MariaDb.updateUser(request)
    complete(HttpResponse(StatusCodes.OK))
  }

  private def sendMail(request: UserLoginRequest) = {
    val body = weatherClient.getWeatherData(Seq(("q", "Warsaw"))).toString

    notificationSender ! EmailNotification("user", request.username.value, body)
  }

  private def userToUserLoginResponse(user: User) = UserLoginResponse(ID(user.id.toInt), Email(user.email), Name(user.name), Surname(user.surname), SlackId(user.slackId))
}
