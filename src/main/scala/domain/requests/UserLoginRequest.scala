package domain.requests

import domain.Domain._

final case class UserLoginRequest(username: Email,
                                  password: Password)

final case class UserRegisterRequest(email: Email,
                                     password: Password,
                                     slackId: Option[SlackId],
                                     name: Name,
                                     surname: Surname)



