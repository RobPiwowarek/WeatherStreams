package utils

import akka.actor.ActorSystem
import akka.stream._
import domain.api.UserRegisterRequest
import server.database.model.User

object Implicits {

  object Akka {
    implicit val system = ActorSystem("TestSystem")
    implicit val materializer = ActorMaterializer()
  }

  object Convertions {
    implicit def userRegisterRequestToUser(userRequest: UserRegisterRequest): User =
      User(1,
        userRequest.name.value,
        userRequest.surname.value,
        userRequest.slack.map(_.value).getOrElse(""),
        userRequest.password.value,
        userRequest.username.value)
  }

}
