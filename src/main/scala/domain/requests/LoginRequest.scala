package domain.requests

import domain.Domain.{Password, Username}

case class LoginRequest(username: Username, password: Password)
