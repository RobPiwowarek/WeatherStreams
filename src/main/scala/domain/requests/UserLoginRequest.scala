package domain.requests

import domain.Domain._

case class UserLoginRequest(username: Email,
                            password: Password)

case class UserRegisterRequest(email: Email,
                               password: Password,
                               slackId: Option[SlackId],
                               name: Name,
                               surname: Surname)



