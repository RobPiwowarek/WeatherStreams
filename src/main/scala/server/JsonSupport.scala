package server

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import domain.Domain.{Email, Password, SlackId, Username}
import domain.requests.{AlertRequest, LoginRequest, UserRequest}
import spray.json.DefaultJsonProtocol

trait JsonSupport extends DefaultJsonProtocol with SprayJsonSupport {

  implicit val UsernameFormat = jsonFormat1(Username)
  implicit val PasswordFormat = jsonFormat1(Password)
  implicit val EmailFormat = jsonFormat1(Email)
  implicit val SlackIdFormat = jsonFormat1(SlackId)

  implicit val AlertRequestFormat = jsonFormat2(AlertRequest)
  implicit val UserRequestFormat = jsonFormat4(UserRequest)
  implicit val LoginRequestFormat = jsonFormat2(LoginRequest)
}
