package com.knoldus.barbeque.state

import com.knoldus.barbeque.util.Constants
import play.api.libs.json.{Format, Json}

case class BarbequeState(id: String, timestamp: String)

object BarbequeState {
  implicit val Format: Format[BarbequeState] = Json.format
  val NotStarted = BarbequeState(Constants.Empty, Constants.Empty)
}

