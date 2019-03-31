package com.knoldus.barbeque.models

import play.api.libs.json.{Format, Json}

case class BarbequeOrderInformation (foodName: String, quantity: Int, price: Double)

object BarbequeOrderInformation {
  implicit val Format: Format[BarbequeOrderInformation] = Json.format
}
