package integration

import com.typesafe.config.ConfigFactory

trait LoginConfig {
  val username: String
  val password: String
}

object AutoFileLoginConfig extends LoginConfig {
  val conf = ConfigFactory.load()
  val username = conf.getString("api-test.username")
  val password = conf.getString("api-test.password")
}