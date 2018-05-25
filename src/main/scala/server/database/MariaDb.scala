package server.database

import domain.Domain.Email
import domain.requests.{AlertRequest, UserRegisterRequest}
import server.database.model.TableQueries._
import server.database.model.{Alert, User}
import slick.basic.DatabaseConfig
import slick.jdbc.MySQLProfile
import slick.jdbc.MySQLProfile.api._
import utils.Implicits.Convertions._

import scala.concurrent.Await
import scala.concurrent.duration._

object MariaDb {
  val databaseConfig = DatabaseConfig.forConfig[MySQLProfile]("maria-db")
  val db = databaseConfig.db

  // TODO: FIXME:
  def insert(user: UserRegisterRequest) = {
    Await.result(db.run(DBIO.seq(
      users += user)), 10 seconds)
  }

  def insertNewAlert(alertRequest: AlertRequest) = {
    val user = selectUser(alertRequest.username).get // todo: some kind of throw

    val action = alerts += Alert(
      user.id,
      0,
      alertRequest.name.value,
      java.sql.Date.valueOf(alertRequest.date.value),
      alertRequest.location.value,
      alertRequest.duration.value)

    db.run(action)
  }

  def selectUser(username: Email): Option[User] = {
    Await.result(
      db.run(users
        .filter(_.email >= username.value).result), 10 seconds)
      .headOption
      .map(x => User(x.id, x.email, x.password, x.slackId, x.name, x.surname))
  }

  def close() = db.close
}