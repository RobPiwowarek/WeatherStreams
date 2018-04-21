package domain.requests

import domain.Domain.{Email, Password, SlackId}

case class UserLoginRequest(email: Email,
                            password: Password)

case class UserRegisterRequest(email: Email,
                               password: Password,
                               slackId: Option[SlackId])



