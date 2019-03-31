package com.knoldus.barbeque.entity

import akka.Done
import com.knoldus.barbeque.command.{BarbequeCommand, CreateOrder}
import com.knoldus.barbeque.event.{BarbequeEvent, BarbequeOrderDataCreated}
import com.knoldus.barbeque.state.BarbequeState
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity

class BarbequeEntity extends PersistentEntity {
  override type State = BarbequeState
  override type Command = BarbequeCommand
  override type Event = BarbequeEvent

  override def initialState: BarbequeState = BarbequeState.NotStarted

  override def behavior: Behavior = {
    case BarbequeState(_, _) => Actions().onCommand[CreateOrder, Done] {
      case (CreateOrder(barbequeOrders), ctx, _) =>
        ctx.thenPersist(BarbequeOrderDataCreated(barbequeOrders))(_ => ctx.reply(Done))
    }.onEvent {
      case (BarbequeOrderDataCreated(barbequeOrders), _: State) =>
        BarbequeState(barbequeOrders.orderId, barbequeOrders.timestamp)
    }
  }
}

