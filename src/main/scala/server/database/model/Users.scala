package server.database.model

import slick.jdbc.MySQLProfile.api._
import slick.sql.SqlProfile.ColumnOption.Nullable

class Users(tag: Tag) extends Table[User](tag, Some("weather"), "weather_user") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def name = column[String]("name")

  def surname = column[String]("surname")

  def slackId = column[String]("slack")

  def password = column[String]("password")

  def email = column[String]("email")

  def * = (id, email, password, slackId, name, surname).mapTo[User]
}

final case class User(id: Long,
                      email: String,
                      password: String,
                      slackId: String,
                      name: String,
                      surname: String)