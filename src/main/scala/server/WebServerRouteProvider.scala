package server

import java.time.Instant

import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.{as, complete, entity, get, path, pathPrefix, post, put, _}
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import domain.Domain._
import domain.api._
import notifications.EmailNotification
import server.WebServer.{notificationSender, weatherClient}
import server.database.model.User
import server.database.{DatabaseInterface, DatabaseProvider}
import spray.json._

class WebServerRouteProvider(databaseProvider: DatabaseProvider) extends JsonSupport {

  val database: DatabaseInterface = databaseProvider.provide()

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
          .toJson.toString
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
        entity = database
          .getAlertHistoryList(id)
          .map(alert => AlertHistoryEntry(Name(alert.parameterName), alert.parameterValue, alert.parameterLimit, alert.parameterValue > alert.parameterLimit)).toJson.toString
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
