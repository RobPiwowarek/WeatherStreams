package notifications

import server.database.model.DefinitionParameter

sealed trait Notification

object Helpers {
  def alertParamToString(location: String, parameters: Seq[DefinitionParameter]) = {
    val paramStrings = parameters
      .map {
        parameter => {
          val comparison = parameter.comparisonType match {
            case 1 => "<"
            case 2 => ">"
            case _ => throw new IllegalArgumentException("Invalid comparison value, 1 or 2 expected")
          }

          s"${parameter.parameterName} $comparison ${parameter.parameterLimit} ${parameter.unit}"
        }
      }
      .mkString("\n")
    s"$paramStrings\nin $location"

  }
}

final case class EmailNotification(user: String,
                             email: String,
                             location: String,
                             parameters: Seq[DefinitionParameter]) extends Notification {

  override def toString =
    s"""
       | Hi $user!\n\n
       | You are receiving this notification because conditions for one of your alerts were met:\n
       | ${Helpers.alertParamToString(location, parameters)}\n\n
       | Best regards,\nWeather Streams
    """.stripMargin
}

final case class SlackNotification(slackUsername: String,
                             location: String,
                             parameters: Seq[DefinitionParameter]) extends Notification {


  override def toString =
    s"""
       | Hi $slackUsername!\n\n
       | Conditions for one of your alerts were met:\n
       | ${Helpers.alertParamToString(location, parameters)}
    """.stripMargin
}