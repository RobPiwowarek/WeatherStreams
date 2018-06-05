package server.database.model

import server.database.model.TableQueries.alerts
import slick.jdbc.MySQLProfile.api._

class AlertHistories(tag: Tag) extends Table[AlertHistory](tag, Some("weather"), "alert_history") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def alertId = column[Long]("alert_id")

  def alertFk = foreignKey("alert", alertId, alerts)(_.id)

  def parameterName = column[String]("parameter_name")

  def parameterValue = column[Int]("parameter_value")

  def parameterLimit = column[Option[Int]]("parameter_limit")

  def * = (id, alertId, parameterName, parameterValue, parameterLimit).mapTo[AlertHistory]
}

final case class AlertHistory(id: Long,
                              alertId: Long,
                              parameterName: String,
                              parameterValue: Int,
                              parameterLimit: Option[Int])