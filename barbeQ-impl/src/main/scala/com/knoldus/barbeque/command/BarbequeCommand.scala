package com.knoldus.barbeque.command

import akka.Done
import com.knoldus.barbeque.model.BarbequeLatestOrderDetails
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import play.api.libs.json.{Format, Json}

trait BarbequeCommand extends ReplyType[Done]

case class CreateOrder(barbequeOrders: BarbequeLatestOrderDetails) extends BarbequeCommand

object CreateOrder {
  implicit val Format: Format[CreateOrder] = Json.format
}
