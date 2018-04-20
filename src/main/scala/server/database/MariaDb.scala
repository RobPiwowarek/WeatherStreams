package server.database

import akka.stream.alpakka.slick.javadsl.SlickSession
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

object MariaDb {

  val databaseConfig = DatabaseConfig.forConfig[JdbcProfile]("slick-mysql")
  implicit val session = SlickSession.forConfig(databaseConfig)

  

}
