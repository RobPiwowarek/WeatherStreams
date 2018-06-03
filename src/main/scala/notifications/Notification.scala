package notifications

import server.database.model.{AlertDefinition, DefinitionParameter}

object Helpers {
  def alertParamToString(alert: AlertDefinition, parameters: Seq[DefinitionParameter]) = {
    val paramStrings = parameters
      .map {
        parameter => {
          val comparison = parameter.comparisonType.match {
            case 1 => "<"
            case 2 => ">"
          }

          s"${parameter.parameterName} ${comparison} ${parameter.parameterLimit} ${parameter.unit}"
        }
      }
        .mkString("\n")
    s"$paramStrings\nin ${alert.location}"

  }
}

case class EmailNotification(user: String,
                             email: String,
                             alert: AlertDefinition,
                             parameters: Seq[DefinitionParameter]) {
  val alertString = Helpers.alertParamToString(alert, parameters)

  override def toString =
    s"""
       | Hi $user!\n\n
       | You are receiving this notification because conditions for one of your alerts were met:\n
       | $alertString\n\n
       | Best regards,\nWeather Streams
    """.stripMargin
}

case class SlackNotification(slackUsername: String,
                             alert: AlertDefinition,
                             parameters: Seq[DefinitionParameter]) {
  val alertString = Helpers.alertParamToString(alert, parameters)

  override def toString =
    s"""
       | Hi $slackUsername!\n\n
       | Conditions for one of your alerts were met:\n
       | $alertString
    """.stripMargin
}