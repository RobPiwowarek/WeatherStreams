package server.database

import domain.Domain.{Email, ID}
import domain.api.{AlertDefinitionParameter, AlertDefinitionRequest, UserUpdateRequest}
import server.database.model.TableQueries._
import server.database.model._
import slick.basic.DatabaseConfig
import slick.jdbc.MySQLProfile
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Await
import scala.concurrent.duration._

object MariaDb {
  val databaseConfig = DatabaseConfig.forConfig[MySQLProfile]("maria-db")
  val db = databaseConfig.db

  def getAlertHistoryList(alertId: Int) = {
    val query = alertHistories.filter(_.alertId === alertId.toLong)

    Await
      .result(db.run(query.result), 10 seconds)
      .map(alert => AlertHistory(alert.id, alert.alertId, alert.parameterName, alert.parameterValue, alert.parameterLimit))
  }

  def getAlertList(userId: Int) = {
    val query = alerts.filter(_.weatherUserId === userId.toLong)

    Await
      .result(db.run(query.result), 10 seconds)
      .map(alert => Alert(alert.id, alert.weatherUserId, alert.name, alert.date, alert.location, alert.duration))
  }

  def deleteAlert(id: Int) = {
    Await.result(db.run(alerts.filter(_.id === id.toLong).delete), 10 seconds)
  }

  def deleteAlertDefinition(id: Int) = {
    val query = alertDefinitions.filter(_.id === id.toLong)

    Await.result(db.run(query.delete), 10 seconds)
  }

  def updateAlertDefinition(alertRequest: AlertDefinitionRequest) = {
    val query = alertDefinitions
      .filter(_.id === alertRequest.id.value.toLong)
      .map(alert => (alert.weatherUserId, alert.alertName, alert.duration, alert.location, alert.active, alert.emailNotif, alert.slackNotif))

    Await.result(
      db.run(query
        .update(
          (alertRequest.userId.value,
            alertRequest.alertName.value,
            alertRequest.duration.value,
            alertRequest.location.value,
            alertRequest.active,
            alertRequest.emailNotif,
            alertRequest.slackNotif))), 10 seconds)

    alertRequest.parameters.foreach(updateAlertDefinitionParameter(alertRequest.id, _))
  }

  def updateAlertDefinitionParameter(id: ID, param: AlertDefinitionParameter) = {
    val query = definitionParameters
      .filter(_.id === param.id.value.toLong)
      .map(param => (param.alertDefinitionId, param.parameterName, param.parameterLimit, param.comparisonType, param.unit))

    Await.result(
      db.run(query
        .update((id.value.toLong, param.parameterName.value, param.parameterLimit, param.comparisonType, param.unit))), 10 seconds)
  }

  def insertAlertDefinition(alertRequest: AlertDefinitionRequest) = {
    val action = alertDefinitions returning alertDefinitions.map(_.id) += AlertDefinition(
      alertRequest.id.value,
      alertRequest.userId.value,
      alertRequest.alertName.value,
      alertRequest.duration.value,
      alertRequest.location.value,
      alertRequest.active,
      alertRequest.emailNotif,
      alertRequest.slackNotif
    )

    val id = Await.result(db.run(action), 10 seconds)
    alertRequest.parameters.foreach(insertAlertDefinitionParameter(id, _))
  }

  def insertAlertDefinitionParameter(id: Long, param: AlertDefinitionParameter) = {
    val action = definitionParameters +=
      DefinitionParameter(
        param.id.value,
        id, param.parameterName.value,
        param.parameterLimit,
        param.comparisonType,
        param.unit)

    Await.result(db.run(action), 10 seconds)
  }

  def getAlertDefinitions(userId: Int) = {
    val query = alertDefinitions
      .filter(_.weatherUserId === userId.toLong)

    Await
      .result(db.run(query.result), 10 seconds)
      .map(definition =>
        AlertDefinition(
          definition.id,
          definition.weatherUserId,
          definition.alertName,
          definition.duration,
          definition.location,
          definition.active,
          definition.emailNotif,
          definition.slackNotif
        ))
      .map(defi => defi -> getAlertDefinitionParameters(defi.id.toInt))
  }

  def getAlertDefinitionParameters(definitionId: Int) = {
    Await
      .result(db.run(definitionParameters.filter(_.id === definitionId.toLong).result), 10 seconds)
      .map(param => DefinitionParameter(param.id, param.alertDefinitionId, param.parameterName, param.parameterLimit, param.comparisonType, param.unit))
  }

  def updateUser(updateUserRequest: UserUpdateRequest) = {
    val query = users
      .filter(_.id === updateUserRequest.id.value.toLong)
      .map(user => (user.email, user.slackId, user.name, user.surname))

    Await.result(
      db.run(query
        .update((updateUserRequest.username.value, updateUserRequest.slack.value, updateUserRequest.name.value, updateUserRequest.surname.value))), 10 seconds)
  }

  def selectUserById(id: ID): Option[User] = {
    Await.result(
      db.run(users
        .filter(_.id === id.value.toLong).result), 10 seconds)
      .headOption
      .map(x => User(x.id, x.email, x.password, x.slackId, x.name, x.surname))
  }

  def selectUser(username: Email): Option[User] = {
    Await.result(
      db.run(users
        .filter(_.email >= username.value).result), 10 seconds)
      .headOption
      .map(user => User(user.id, user.email, user.password, user.slackId, user.name, user.surname))
  }

  def close() = db.close
}