package utils

import akka.actor.ActorSystem
import akka.stream._

object Implicits {

  object Akka {
    implicit val system = ActorSystem("TestSystem")
    implicit val materializer = ActorMaterializer()
  }

}
