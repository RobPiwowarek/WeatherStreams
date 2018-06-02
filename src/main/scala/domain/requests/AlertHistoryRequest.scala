package domain.requests

import domain.Domain._

final case class AlertHistoryRequest(username: Email,
                                     name: Name,
                                     date: Date,
                                     location: Location,
                                     duration: Duration)