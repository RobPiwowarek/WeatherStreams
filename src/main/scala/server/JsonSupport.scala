package server

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import domain.Domain._
import domain.api._
import spray.json.{DefaultJsonProtocol, DeserializationException, JsNumber, JsString, JsValue, JsonFormat}

trait JsonSupport extends DefaultJsonProtocol with SprayJsonSupport {

  implicit object IdFormat extends JsonFormat[ID] {
    def write(id: ID) = JsString(id.value.toString)

    def read(json: JsValue) = json match {
      case JsString(username) => ID(username.toInt)
      case _ => throw DeserializationException("Id expected")
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

  implicit object DateFormat extends JsonFormat[Date] {
    def write(date: Date) = JsString(date.value)

    def read(json: JsValue) = json match {
      case JsString(date) => Date(date)
      case _ => throw DeserializationException("Date expected")
    }
  }

  implicit object LocationFormat extends JsonFormat[Location] {
    def write(location: Location) = JsString(location.value)

    def read(json: JsValue) = json match {
      case JsString(location) => Location(location)
      case _ => throw DeserializationException("Location expected")
    }
  }

  implicit object DurationFormat extends JsonFormat[Duration] {
    def write(duration: Duration) = JsNumber(duration.value)

    def read(json: JsValue) = json match {
      case JsNumber(duration) => Duration(duration.intValue())
      case _ => throw DeserializationException("Duration expected")
    }
  }

  implicit val AlertResponseFormat = jsonFormat4(AlertResponse)

  implicit val AlertHistoryEntryFormat = jsonFormat4(AlertHistoryEntry)
  implicit val AlertHistoryRequestFormat = jsonFormat1(AlertHistoryResponse)

  implicit val AlertDefinitionParameterFormat = jsonFormat5(AlertDefinitionParameter)
  implicit val AlertDefinitionFormat = jsonFormat10(AlertDefinitionRequest)
  implicit val AlertDefinitionResponseFormat = jsonFormat10(AlertDefinitionResponse)

  implicit val UserRegisterFormat = jsonFormat5(UserRegisterRequest)
  implicit val UserUpdateRequestFormat = jsonFormat5(UserUpdateRequest)
  implicit val UserLoginFormat = jsonFormat2(UserLoginRequest)
  implicit val UserLoginResponseFormat = jsonFormat5(UserLoginResponse)

}
