package com.knoldus.barbeque.models

import play.api.libs.json.{Format, Json}

case class BarbequeOrderResponse (order_id: String, status: String)

object BarbequeOrderResponse {
  implicit val Format: Format[BarbequeOrderResponse] = Json.format
}
