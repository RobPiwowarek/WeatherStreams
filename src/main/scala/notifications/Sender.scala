package notifications

import akka.actor.{Actor, ActorRef, Props}

import scala.language.postfixOps

// use this Actor to send out notifications
// it will choose the right sender for the notification type
class Sender(emailConfig: EmailConfig, slackConfig: SlackConfig) extends Actor {
  val emailSender: ActorRef = context.actorOf(Props(classOf[Email], emailConfig), name = "EmailSender")
  val slackSender: ActorRef = context.actorOf(Props(classOf[Slack], slackConfig), name = "SlackSender")

  def receive = {
    case email: EmailNotification =>
      emailSender ! email
    case slackMsg: SlackNotification =>
      slackSender ! slackMsg
    case generic: Notification =>
      print(generic.toString()) // debug
  }
}
