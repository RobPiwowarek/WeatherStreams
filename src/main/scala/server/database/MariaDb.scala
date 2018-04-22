package server.database

import domain.requests.{UserLoginRequest, UserRegisterRequest}
import slick.basic.DatabaseConfig
import slick.jdbc.MySQLProfile
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object MariaDb {

  val databaseConfig = DatabaseConfig.forConfig[MySQLProfile]("maria-db")
  val db = databaseConfig.db


  class UsersTable(tag: Tag) extends Table[User](tag, "WEATHER_USER") {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)

    def name = column[String]("NAME")

    def surname = column[String]("SURNAME")

    def slackId = column[String]("SLACK")

    def password = column[String]("PASSWORD")

    def email = column[String]("EMAIL")

    def * = (id, email, password, slackId, name, surname).mapTo[User]
  }

  val users = TableQuery[UsersTable]


  // TODO: FIXME:
  def insert(user: UserRegisterRequest) = {
    try {
      Await.result(db.run(DBIO.seq(
        users += user)), Duration.Inf)
    } finally db.close
  }

  def selectUser(userLoginRequest: UserLoginRequest): Option[User] = {
    try {
      Await.result(
        db.run(users
          .withFilter(_.email === userLoginRequest.username.value)
          .result), Duration.Inf)
        .headOption
        .map(x => User(x.id, x.email, x.password, x.slackId, x.name, x.surname))
    } finally db.close
  }

  implicit def userRegisterRequestToUser(userRequest: UserRegisterRequest): User =
    User(1,
      userRequest.name.value,
      userRequest.surname.value,
      userRequest.slackId.map(_.value).getOrElse(""),
      userRequest.password.value,
      userRequest.email.value)
}

case class User(id: Long,
                email: String,
                password: String,
                slackId: String,
                name: String,
                surname: String)

