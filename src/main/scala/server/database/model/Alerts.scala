package server.database.model

import java.sql.Date

import server.database.model.TableQueries.users
import slick.jdbc.MySQLProfile.api._

class Alerts(tag: Tag) extends Table[Alert](tag, Some("weather"), "alert") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def weatherUserId = column[Long]("weather_user_id")

  def weatherUserFk = foreignKey("weather_user", weatherUserId, users)(_.id)

  def name = column[String]("name")

  def date = column[Date]("date")

  def location = column[String]("location")

  def duration = column[Int]("duration")

  def * = (id, weatherUserId, name, date, location, duration).mapTo[Alert]
}

final case class Alert(id: Long,
                       weatherUserId: Long,
                       name: String,
                       date: Date,
                       location: String,
                       duration: Int)