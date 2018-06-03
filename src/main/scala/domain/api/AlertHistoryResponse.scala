package domain.api

import com.sun.org.apache.xalan.internal.utils.XMLSecurityManager.Limit
import domain.Domain._

final case class AlertHistoryEntry(name: Name,
                                   value: Int,
                                   limit: Limit,
                                   outOfLimit: Boolean)

final case class AlertHistoryResponse(history: Seq[AlertHistoryEntry])