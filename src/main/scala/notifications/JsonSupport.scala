package notifications

import providers.openweathermap.Responses

trait JsonSupport extends server.JsonSupport {
  implicit val emailNotificationFormat = jsonFormat4(EmailNotification)
  implicit val slackNotificationFormat = jsonFormat3(SlackNotification)
  implicit val weatherFormat = Responses.weatherFormat
}
