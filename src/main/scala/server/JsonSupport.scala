package server

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import domain.Domain._
import domain.requests.{AlertRequest, UserLoginRequest, UserRegisterRequest}
import spray.json.DefaultJsonProtocol

trait JsonSupport extends DefaultJsonProtocol with SprayJsonSupport {

  implicit val UsernameFormat = jsonFormat1(ID)
  implicit val PasswordFormat = jsonFormat1(Password)
  implicit val EmailFormat = jsonFormat1(Email)
  implicit val SlackIdFormat = jsonFormat1(SlackId)
  implicit val NameFormat = jsonFormat1(Name)
  implicit val SurnameFormat = jsonFormat1(Surname)

  implicit val AlertRequestFormat = jsonFormat2(AlertRequest)
  implicit val UserRegisterFormat = jsonFormat3(UserRegisterRequest)
  implicit val UserLoginFormat = jsonFormat2(UserLoginRequest)
}
