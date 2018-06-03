package notifications

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import providers.openweathermap.Responses
import server.database.model.{AlertDefinition, DefinitionParameter}
import spray.json.DefaultJsonProtocol

trait JsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val alertDefinitionFormat = jsonFormat8(AlertDefinition)
  implicit val definitionParameterFormat = jsonFormat6(DefinitionParameter)
  implicit val emailNotificationFormat = jsonFormat4(EmailNotification)
  implicit val slackNotificationFormat = jsonFormat3(SlackNotification)
  implicit val weatherFormat = Responses.weatherFormat
}
