package domain.requests

import domain.Domain.{ID, Password}

case class LoginRequest(username: ID, password: Password)
