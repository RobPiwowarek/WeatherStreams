package server.database

import domain.Domain.{Email, ID, Location}
import domain.api.{AlertDefinitionParameter, AlertDefinitionRequest, UserUpdateRequest}
import server.database.model._

trait DatabaseInterface {

  def insertAlert(definition: AlertDefinition, params: Seq[(AlertHistory, Boolean)])

  def insertAlertHistory(id: Long, param: AlertHistory, value: Boolean)

  def getAlertsFromLocation(location: Location) : Seq[AlertDefinition]

  def getLocationsWithActiveAlerts() : Seq[Location]

  def getAlertHistoryList(alertId: Int): Seq[AlertHistory]

  def getAlertList(userId: Int): Seq[Alert]

  def deleteAlert(id: Int): Int

  def deleteAlertDefinition(id: Int): Int

  def updateAlertDefinition(alertRequest: AlertDefinitionRequest)

  def updateAlertDefinitionParameter(id: ID, param: AlertDefinitionParameter): Int

  def insertAlertDefinition(alertRequest: AlertDefinitionRequest)

  def insertAlertDefinitionParameter(id: Long, param: AlertDefinitionParameter): Int

  def getAlertDefinitions(userId: Int): Seq[(AlertDefinition, Seq[DefinitionParameter])]

  def getAlertDefinitionParameters(definitionId: Int): Seq[DefinitionParameter]

  def updateUser(updateUserRequest: UserUpdateRequest): Int

  def selectUserById(id: ID): Option[User]

  def selectUser(username: Email): Option[User]


}
