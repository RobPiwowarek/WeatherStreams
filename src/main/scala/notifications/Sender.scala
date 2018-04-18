package notifications

import akka.actor.{Actor, ActorRef, Props}

import scala.language.postfixOps

// use this Actor to send out notifications
// it will choose the right sender for the notification type
class Sender extends Actor {
  val emailSender: ActorRef = context.actorOf(Props[Email], name = "EmailSender")

  // only email rn
  def receive = {
    case email: EmailNotification => emailSender ! email
    case generic: Notification => print(generic.toString()) // debug
  }
}
