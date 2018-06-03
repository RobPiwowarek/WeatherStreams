package domain.api

import domain.Domain._

final case class AlertResponse(id: ID,
                               name: Name,
                               date: Date,
                               location: Location)

