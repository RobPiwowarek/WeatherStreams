package domain.requests

import domain.Domain.{Email, Password, SlackId, Username}

case class UserRequest(username: Username,
                       password: Password,
                       email: Email,
                       slackId: SlackId)


