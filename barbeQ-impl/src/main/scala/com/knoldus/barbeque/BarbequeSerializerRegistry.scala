package com.knoldus.barbeque

import com.knoldus.barbeque.command.CreateOrder
import com.knoldus.barbeque.event.BarbequeOrderDataCreated
import com.knoldus.barbeque.models.BarbequeOrders
import com.knoldus.barbeque.state.BarbequeState
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}

import scala.collection.immutable.Seq

object BarbequeSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: Seq[JsonSerializer[_]] = Seq(
    //Case Class
    JsonSerializer[BarbequeOrders],
    // State
    JsonSerializer[BarbequeState],
    // Commands and replies
    JsonSerializer[CreateOrder],
    // Events
    JsonSerializer[BarbequeOrderDataCreated])
}

