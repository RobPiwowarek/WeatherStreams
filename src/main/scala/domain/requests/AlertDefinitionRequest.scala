package domain.requests

import domain.Domain._

final case class AlertDefinitionRequest(id: ID,
                                        userId: ID,
                                        alertName: Name,
                                        duration: Duration,
                                        location: Location,
                                        active: Boolean,
                                        emailNotif: Boolean,
                                        slackNotif: Boolean,
                                        timestamp: Int,
                                        parameters: Seq[AlertDefinitionParameter])

final case class AlertDefinitionParameter(id: ID,
                                          parameterName: Name,
                                          parameterLimit: Int,
                                          comparisonType: Int,
                                          unit: String)
