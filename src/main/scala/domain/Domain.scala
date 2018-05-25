package domain

object Domain {

  final case class ID(value: String)

  final case class Password(value: String)

  final case class Email(value: String)

  final case class SlackId(value: String)

  final case class Name(value: String)

  final case class Surname(value: String)

  final case class Username(value: String)

  final case class Date(value: String)

  final case class Location(value: String)

  final case class Duration(value: Int)
}
