package notifications


object Errors {

  sealed trait NotificationError {
    val message: String

    override def toString = s"Notification ${this.getClass.getSimpleName}: " + message
  }

  case class TimeoutError(message: String) extends java.util.concurrent.TimeoutException(message) with NotificationError

  case class AuthorizationError(message: String) extends Throwable with NotificationError

  case class SendError(message: String) extends Throwable with NotificationError

  case class InternalError(message: String) extends Throwable with NotificationError

}