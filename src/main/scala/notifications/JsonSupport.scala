package notifications

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

trait JsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val emailNotificationFormat = jsonFormat3(EmailNotification)
  implicit val slackNotificationFormat = jsonFormat2(SlackNotification)
}
