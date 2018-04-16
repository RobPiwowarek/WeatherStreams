package server.database

import com.typesafe.config.ConfigFactory
import domain.Domain.{Password, Username}
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object MariaDb {
  val db = Database.forConfig("mariadb", ConfigFactory.parseResources("application.conf"))

  val setup = DBIO.seq()

  // tables:

  class Users(tag: Tag) extends Table[(String, String)](tag, "USERS") {
    def login = column[String]("LOGIN", O.PrimaryKey)

    def password = column[String]("PASSWORD")

    def * = (login, password)
  }

  val users = TableQuery[Users]

  def insert(username: Username, password: Password) = {
    try {
      Await.result(
        db.run(
          DBIO.seq(
            users ++= Seq(
              (username.name, password.password)))),
        Duration.Inf) //todo: inny duration
    } finally db.close
  }

  def query(username: Username, password: Password) = {
    db.run(
      users.result)
      .map(_.foreach {
        case (login, pass) =>
          if (login.equals(username.name) && pass.equals(password.password))
            println("query successful")
        case _ =>
          println("not found")
      })
  }

}
