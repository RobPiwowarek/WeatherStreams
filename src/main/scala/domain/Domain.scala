package domain

object Domain {
  final case class Username(name: String)
  final case class Password(password: String)
  final case class Email(email: String)
  final case class SlackId(id: String)
}
