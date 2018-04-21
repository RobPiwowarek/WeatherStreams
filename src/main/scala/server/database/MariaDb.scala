package server.database

import slick.basic.DatabaseConfig
import slick.jdbc.MySQLProfile
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global

object MariaDb {

  val databaseConfig = DatabaseConfig.forConfig[MySQLProfile]("maria-db")

  private class UsersTable(tag: Tag) extends Table[(Long, String, String, String, String, String)](tag, "users") {
    def id =  column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("NAME")
    def surname = column[String]("SURNAME")
    def slackId = column[String]("SLACKID")
    def password = column[String]("PASSWORD")
    def email = column[String]("EMAIL")
    def * = (id, email, password, slackId, name, surname)
  }

}

case class User(id: Long,
                email: String,
                password: String,
                slackId: String,
                name: String,
                surname: String)

