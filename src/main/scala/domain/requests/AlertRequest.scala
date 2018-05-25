package domain.requests

import domain.Domain._

final case class AlertRequest(username: Email,
                              name: Name,
                              date: Date,
                              location: Location,
                              duration: Duration)