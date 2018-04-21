package server.database

import domain.requests.UserRegisterRequest
import slick.basic.DatabaseConfig
import slick.jdbc.MySQLProfile
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

object MariaDb {

  val databaseConfig = DatabaseConfig.forConfig[MySQLProfile]("maria-db")
  val db = databaseConfig.db

  private class UsersTable(tag: Tag) extends Table[(Long, String, String, String, String, String)](tag, "users") {
    def id =  column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("NAME")
    def surname = column[String]("SURNAME")
    def slackId = column[String]("SLACKID")
    def password = column[String]("PASSWORD")
    def email = column[String]("EMAIL")
    def * = (id, email, password, slackId, name, surname)
  }
  val users = TableQuery[UsersTable]

  def insert(user: UserRegisterRequest) = {
    try {
      Await.result(db.run(DBIO.seq(
        users += (1, user.name.value, user.surname.value, user.slackId.map(_.value).getOrElse(""), user.password.value, user.email.value))), Duration.Inf)
    } finally db.close
  }

}

case class User(id: Long,
                email: String,
                password: String,
                slackId: String,
                name: String,
                surname: String)

