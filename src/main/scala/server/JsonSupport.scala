package server

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import domain.Domain._
import domain.requests._
import spray.json.{DefaultJsonProtocol, DeserializationException, JsArray, JsBoolean, JsNumber, JsObject, JsString, JsValue, JsonFormat, enrichAny}

trait JsonSupport extends DefaultJsonProtocol with SprayJsonSupport {

  implicit object UsernameFormat extends JsonFormat[ID] {
    def write(username: ID) = JsString(username.value.toString)

    def read(json: JsValue) = json match {
      case JsString(username) => ID(username.toInt)
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

  implicit object AlertRequestFormat extends JsonFormat[AlertHistoryRequest] {
    override def read(json: JsValue): AlertHistoryRequest =
      json.asJsObject.getFields("")

    override def write(obj: AlertHistoryRequest): JsValue = JsString(s"${obj.username} ${obj.duration} ${obj.location} ${obj.date} ${obj.name}")
  }

  implicit object AlertDefinitionParameterFormat extends JsonFormat[AlertDefinitionParameter] {
    override def read(value: JsValue): AlertDefinitionParameter =
      value.asJsObject.getFields("id", "parameterName", "parameterLimit", "comparisonType", "unit") match {
        case Seq(JsNumber(id), JsString(paramName), JsNumber(limit), JsNumber(comparison), JsString(unitt)) =>
          AlertDefinitionParameter(ID(id.intValue()), Name(paramName), limit.intValue(), comparison.intValue(), unitt)
        case _ =>
          throw new DeserializationException("AlertDefinitionParameter expected")
      }

    override def write(obj: AlertDefinitionParameter): JsValue = JsObject(
      "id" -> JsNumber(obj.id.value),
      "parameterName" -> JsString(obj.parameterName.value),
      "parameterLimit" -> JsNumber(obj.parameterLimit),
      "comparisonType" -> JsNumber(obj.comparisonType),
      "unit" -> JsString(obj.unit)
    )
  }

  implicit object AlertDefinitionFormat extends JsonFormat[AlertDefinitionRequest] {
    override def read(value: JsValue): AlertDefinitionRequest =
      value.asJsObject.getFields("id", "userId", "alertName", "duration", "location", "active", "slackNotif", "emailNotif", "timestamp", "parameters") match {
        case Seq(JsNumber(id), JsNumber(userId), JsString(name), JsNumber(duration), JsString(location), JsBoolean(active), JsBoolean(slack), JsBoolean(email), JsNumber(timestamp), JsArray(parameters)) =>
          AlertDefinitionRequest(
            ID(id.intValue()),
            ID(userId.intValue()),
            Name(name),
            Duration(duration.intValue()),
            Location(location),
            active,
            email,
            slack,
            timestamp.intValue(),
            parameters.map(_.convertTo[AlertDefinitionParameter])
          )
        case _ =>
          throw new DeserializationException("AlertDefinitionParameter expected")
      }

    override def write(obj: AlertDefinitionRequest): JsValue = JsObject(
      "id" -> JsNumber(obj.id.value),
      "userId" -> JsNumber(obj.userId.value),
      "alertName" -> JsString(obj.alertName.value),
      "duration" -> JsNumber(obj.duration.value),
      "location" -> JsString(obj.location.value),
      "active" -> JsBoolean(obj.active),
      "slackNotif" -> JsBoolean(obj.slackNotif),
      "emailNotif" -> JsBoolean(obj.emailNotif),
      "timestamp" -> JsNumber(obj.timestamp),
      "parameters" -> JsArray(obj.parameters.map(_.toJson).toVector)
    )
  }


  implicit val UserRegisterFormat = jsonFormat5(UserRegisterRequest)
  implicit val UserLoginFormat = jsonFormat2(UserLoginRequest)
}
