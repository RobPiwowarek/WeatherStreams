package domain.api

import domain.Domain._

final case class AlertHistoryEntry(name: Name,
                                   value: Int,
                                   limit: Option[Int],
                                   outOfLimit: Boolean)

final case class AlertHistoryResponse(history: Seq[AlertHistoryEntry])