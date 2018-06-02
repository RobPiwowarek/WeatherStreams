package notifications

case class EmailNotification(user: String,
                             email: String,
                             alert: String) {
  override def toString =
    s"""
       | Hi $user!\n\n
       | You are receiving this notification because conditions for one of your alerts were met:\n
       | $alert\n\n
       | Best regards,\nWeather Streams
    """.stripMargin
}

case class SlackNotification(slackUsername: String,
                             alert: String) {
  override def toString =
    s"""
       | Hi $slackUsername!\n\n
       | Conditions for one of your alerts were met:\n
       | $alert
    """.stripMargin
}