package com.knoldus.barbeque.models

import play.api.libs.json.{Format, Json}

case class BarbequeOrders(orderList: List[BarbequeOrderInformation])

object BarbequeOrders {
  implicit val Format: Format[BarbequeOrders] = Json.format
}
