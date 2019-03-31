package com.knoldus.barbeque.event

import com.knoldus.barbeque.model.BarbequeLatestOrderDetails
import com.knoldus.barbeque.util.Constants.Lagom
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventShards, AggregateEventTag, AggregateEventTagger}
import play.api.libs.json.{Format, Json}

class BarbequeEvent extends AggregateEvent[BarbequeEvent] {
  override def aggregateTag: AggregateEventTagger[BarbequeEvent] = BarbequeEvent.TAG
}

object BarbequeEvent {
  final val NumShards = Lagom.NumberOfShards
  final val TAG: AggregateEventShards[BarbequeEvent] = AggregateEventTag.sharded[BarbequeEvent](NumShards)
}

case class BarbequeOrderDataCreated(barbequeOrders: BarbequeLatestOrderDetails) extends BarbequeEvent

object BarbequeOrderDataCreated {
  implicit val Format: Format[BarbequeOrderDataCreated] = Json.format
}
