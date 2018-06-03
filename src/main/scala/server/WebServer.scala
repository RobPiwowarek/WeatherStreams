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
import server.database.model.User
import server.database.{DatabaseInterface, MariaDb}
import spray.json._

object WebServer extends JsonSupport with CorsSupport {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val weatherClient: WeatherClient = OpenWeatherMapClient
  val database: DatabaseInterface = MariaDb
  val notificationSender: ActorRef = system.actorOf(Props(classOf[Sender], EmailFileConfig, SlackFileConfig), name = "NotificationSender")

  def main(args: Array[String]): Unit = {
    val route: Route = routeSetup()

    val corsSupportedRoute = corsSupport(route)

    Http().bindAndHandle(corsSupportedRoute, "0.0.0.0", 8090)
  }

  def routeSetup() = {
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
          int =>
            handleAlertListGet(int)
        }
      } ~ post {
        path("api" / "alert" / IntNumber) {
          int =>
            handleAlertDelete(int)
        }
      } ~ get {
        path("api" / "alert" / IntNumber / "history") {
          int =>
            handleAlertHistoryGet(int)
        }
      }
    }
  }

  private def handleAlertListGet(id: Int) = {
    complete(
      HttpResponse(
        StatusCodes.OK,
        entity = database
          .getAlertList(id)
          .map(alert => AlertResponse(ID(alert.id.toInt), Name(alert.name), Date(alert.date.toString), Location(alert.location)))
          .map(_.toJson.toString)
          .reduce(_ + '\n' + _)
      ))
  }

  private def handleAlertDelete(id: Int) = {
    database.deleteAlert(id)
    complete(HttpResponse(StatusCodes.OK))
  }

  private def handleAlertHistoryGet(id: Int) = {
    complete(
      HttpResponse(
        StatusCodes.OK,
        entity = AlertHistoryResponse(database
          .getAlertHistoryList(id)
          .map(alert => AlertHistoryEntry(Name(alert.parameterName), alert.parameterValue, alert.parameterLimit, alert.parameterValue > alert.parameterLimit))).toJson.toString
      ))
  }

  private def handleAlertDefinitionDelete(id: Int) = {
    database.deleteAlertDefinition(id)
    complete(StatusCodes.OK)
  }

  private def handleAlertDefinitionUpdate(request: AlertDefinitionRequest) = {
    database.updateAlertDefinition(request)
    complete(StatusCodes.OK)
  }

  private def handleAlertDefinitionAdd(request: AlertDefinitionRequest) = {
    database.insertAlertDefinition(request)
    complete(StatusCodes.OK)
  }

  private def handleAlertDefinitionGet(id: Int) = {
    complete(
      HttpResponse(
        StatusCodes.OK,
        entity = database
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
          .reduce(_ + '\n' + _))
    )
  }

  private def handleLoginRequest(request: UserLoginRequest) = {
    database.selectUser(request.username) match {
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
    database.updateUser(request)
    complete(HttpResponse(StatusCodes.OK))
  }

  private def sendMail(request: UserLoginRequest) = {
    val body = weatherClient.getWeatherData(Seq(("q", "Warsaw"))).toString

    notificationSender ! EmailNotification("user", request.username.value, body)
  }

  private def userToUserLoginResponse(user: User) = UserLoginResponse(ID(user.id.toInt), Email(user.email), Name(user.name), Surname(user.surname), SlackId(user.slackId))
}
