package domain.api

import domain.Domain._

final case class UserLoginRequest(username: Email,
                                  password: Password)

final case class UserRegisterRequest(username: Email,
                                     password: Password,
                                     slack: Option[SlackId],
                                     name: Name,
                                     surname: Surname)

final case class UserUpdateRequest(id: ID,
                                   username: Email,
                                   name: Name,
                                   surname: Surname,
                                   slack: SlackId)


final case class UserLoginResponse(id: ID,
                                   username: Email,
                                   name: Name,
                                   surname: Surname,
                                   slack: SlackId)