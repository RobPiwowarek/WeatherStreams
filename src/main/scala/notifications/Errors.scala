package notifications


object Errors {

  sealed trait NotificationError {
    val message: String

    override def toString = s"Notification ${this.getClass.getSimpleName}: " + message
  }

  final case class TimeoutError(message: String) extends java.util.concurrent.TimeoutException(message) with NotificationError

  final case class AuthorizationError(message: String) extends Throwable with NotificationError

  final case class SendError(message: String) extends Throwable with NotificationError

  final case class InternalError(message: String) extends Throwable with NotificationError

}