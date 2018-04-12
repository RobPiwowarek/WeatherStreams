package notifications

import akka.actor.{Actor, ActorRef, Props}

import language.postfixOps

class Sender extends Actor {
  val emailSender: ActorRef = context.actorOf(Props[Email], name="EmailSender")

  // wybierze odp. sendera, na razie tylko email
  def receive = {
    case email: EmailNotification => emailSender ! email
    case generic: Notification => print(generic.toString()) // debug
  }
}
