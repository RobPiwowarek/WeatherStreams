package server.database.model

import slick.lifted.TableQuery

object TableQueries {
  val users = TableQuery[Users]
  val alertHistories = TableQuery[AlertHistories]
  val alerts = TableQuery[Alerts]
  val alertDefinitions = TableQuery[AlertDefinitions]
  val definitionParameters = TableQuery[DefinitionParameters]
}
