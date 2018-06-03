package server.database.model

import server.database.model.TableQueries._
import slick.jdbc.MySQLProfile.api._

class DefinitionParameters(tag: Tag) extends Table[DefinitionParameter](tag, Some("weather"), "alert_definition") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def alertDefinitionId = column[Long]("alert_definition_id")

  def alertDefinitionFk = foreignKey("alert_definition", alertDefinitionId, alertDefinitions)(_.id)

  def parameterName = column[String]("parameter_name")

  def parameterLimit = column[Int]("parameter_limit")

  def comparisonType = column[Int]("comparison_type")

  def unit = column[String]("unit")

  def * = (id, alertDefinitionId, parameterName, parameterLimit, comparisonType, unit).mapTo[DefinitionParameter]
}

final case class DefinitionParameter(id: Long,
                                     alertDefinitionId: Long,
                                     parameterName: String,
                                     parameterLimit: Int,
                                     comparisonType: Int,
                                     unit: String)