package com.knoldus.barbeque.model

import com.knoldus.barbeque.models.BarbequeOrderInformation
import play.api.libs.json.{Format, Json}

case class BarbequeLatestOrderDetails (orderId: String,
                                       timestamp: String,
                                       orderList: List[BarbequeOrderInformation],
                                       status: String = "pending")


object BarbequeLatestOrderDetails {
  implicit val Format: Format[BarbequeLatestOrderDetails] = Json.format
}