package server.database.model

import server.database.model.TableQueries._
import slick.jdbc.MySQLProfile.api._

class AlertDefinitions(tag: Tag) extends Table[AlertDefinition](tag, Some("weather"), "alert_definition") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def weatherUserId = column[Long]("weather_user_id")

  def weatherUserFk = foreignKey("weather_user", weatherUserId, users)(_.id)

  def alertName = column[String]("alert_name")

  def duration = column[Int]("duration")

  def location = column[String]("location")

  def active = column[Boolean]("active")

  def emailNotif = column[Boolean]("email_notif")

  def slackNotif = column[Boolean]("slack_notif")

  def * = (id, weatherUserId, alertName, duration, location, active, emailNotif, slackNotif).mapTo[AlertDefinition]
}

final case class AlertDefinition(id: Long,
                                 weatherUserId: Long,
                                 alertName: String,
                                 duration: Int,
                                 location: String,
                                 active: Boolean,
                                 emailNotif: Boolean,
                                 slackNotif: Boolean)