package server.database

import com.typesafe.config.ConfigFactory
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global

object MariaDb
{
  val config = ConfigFactory.parseResources("application.conf")

  val db = Database.forConfig("mariadb")

}
