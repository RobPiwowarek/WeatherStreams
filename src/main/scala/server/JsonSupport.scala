package server

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import domain.Domain._
import domain.requests.{AlertRequest, UserLoginRequest, UserRegisterRequest}
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, JsonFormat}

trait JsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit object UsernameFormat extends JsonFormat[ID] {
    def write(username: ID) = JsString(username.value)
    def read(json: JsValue) = json match {
      case JsString(username) => ID(username)
      case _ => throw DeserializationException("Username expected")
    }
  }

  implicit object PasswordFormat extends JsonFormat[Password] {
    def write(password: Password) = JsString(password.value)
    def read(json: JsValue) = json match {
      case JsString(password) => Password(password)
      case _ => throw DeserializationException("Password expected")
    }
  }

  implicit object EmailFormat extends JsonFormat[Email] {
    def write(email: Email) = JsString(email.value)
    def read(json: JsValue) = json match {
      case JsString(email) => Email(email)
      case _ => throw DeserializationException("Email expected")
    }
  }

  implicit object SlackIdFormat extends JsonFormat[SlackId] {
    def write(slackId: SlackId) = JsString(slackId.value)
    def read(json: JsValue) = json match {
      case JsString(slackId) => SlackId(slackId)
      case _ => throw DeserializationException("SlackId expected")
    }
  }

  implicit object NameFormat extends JsonFormat[Name] {
    def write(name: Name) = JsString(name.value)
    def read(json: JsValue) = json match {
      case JsString(name) => Name(name)
      case _ => throw DeserializationException("Name expected")
    }
  }

  implicit object SurnameFormat extends JsonFormat[Surname] {
    def write(surname: Surname) = JsString(surname.value)
    def read(json: JsValue) = json match {
      case JsString(surname) => Surname(surname)
      case _ => throw DeserializationException("Surname expected")
    }
  }

  implicit val AlertRequestFormat = jsonFormat2(AlertRequest)
  implicit val UserRegisterFormat = jsonFormat5(UserRegisterRequest)
  implicit val UserLoginFormat = jsonFormat2(UserLoginRequest)
}
