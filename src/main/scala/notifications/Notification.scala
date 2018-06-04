package notifications

import server.database.model.{AlertDefinition, DefinitionParameter}

sealed trait Notification

object Helpers {
  def alertParamToString(location: String, parameters: Seq[DefinitionParameter]) = {
    val paramStrings = parameters
      .map {
        parameter => {
          val comparison = parameter.comparisonType match {
            case 1 => "<"
            case 2 => ">"
          }

          s"${parameter.parameterName} ${comparison} ${parameter.parameterLimit} ${parameter.unit}"
        }
      }
      .mkString("\n")
    s"$paramStrings\nin ${location}"

  }
}

case class EmailNotification(user: String,
                             email: String,
                             location: String,
                             parameters: Seq[DefinitionParameter]) extends Notification {

  override def toString = {
    val alertString = Helpers.alertParamToString(location, parameters)
    s"""
       | Hi $user!\n\n
       | You are receiving this notification because conditions for one of your alerts were met:\n
       | $alertString\n\n
       | Best regards,\nWeather Streams
    """.stripMargin
  }
}

case class SlackNotification(slackUsername: String,
                             location: String,
                             parameters: Seq[DefinitionParameter]) extends Notification {


  override def toString = {
    val alertString = Helpers.alertParamToString(location, parameters)
    s"""
       | Hi $slackUsername!\n\n
       | Conditions for one of your alerts were met:\n
       | $alertString
    """.stripMargin
  }
}