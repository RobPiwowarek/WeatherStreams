package notifications

import akka.actor.{Actor, ActorSystem}
import com.typesafe.config.ConfigFactory
import notifications.Errors.{InternalError, SendError}
import slack.api.BlockingSlackApiClient

sealed trait SlackConfig {
  val token: String
}

object SlackFileConfig extends SlackConfig {
  val conf = ConfigFactory.load()
  val token = conf.getString("slack.token")
}

class Slack(config: SlackConfig) extends BlockingSlackApiClient(token = config.token) with Actor {
  implicit val system = ActorSystem()

  def getUserId(slackUsername: String) = {
    val matchingUsers = listUsers().filter(_.name == slackUsername)
    if (matchingUsers.length < 1) throw SendError(s"Slack user $slackUsername not found")

    matchingUsers.head.id
  }

  def getImChannelId(userId: String) = {
    listIms().filter(_.user == userId).head.id
  }

  def sendMessage(slackUsername: String, text: String) = {
    val userId = getUserId(slackUsername)
    val channelId = getImChannelId(userId)

    try {
      postChatMessage(channelId = channelId, text, asUser = Some(false))
    }
    catch {
      case e: slack.api.ApiError => throw InternalError(e.toString)
    }
  }

  def receive = {
    case e: SlackNotification =>
      sendMessage(e.slackUsername, e.toString)
  }
}
