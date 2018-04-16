package notifications

sealed trait Notification {
  val alert: String // todo - different parameters for different alert conditions
  val user: String

  val title = "Weather Streams Alert"

  override def toString = s"Hi $user!\n\n" +
    s"You are receiving this notification because conditions for one of your alerts were met:\n" +
    s"$alert\n\n" +
    s"Best regards,\nWeather Streams"
}

case class EmailNotification(override val user: String, email: String, override val alert: String) extends Notification {}
