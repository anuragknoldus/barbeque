package com.knoldus.barbeque.es

import akka.Done
import com.knoldus.barbeque.command.{BarbequeCommand, CreateOrder}
import com.knoldus.barbeque.entity.BarbequeEntity
import com.knoldus.barbeque.model.BarbequeLatestOrderDetails
import com.lightbend.lagom.scaladsl.persistence.{PersistentEntityRef, PersistentEntityRegistry}

import scala.concurrent.{ExecutionContext, Future}

class BarbequeEventSourcingProcessor(persistentEntityRegistry: PersistentEntityRegistry)
                                    (implicit ec: ExecutionContext) {

  def sendOrderInformation(barbequeOrders: BarbequeLatestOrderDetails): Future[Done] = {
    entityRef(s"${barbequeOrders.orderId}-${barbequeOrders.timestamp}").ask(CreateOrder(barbequeOrders)).map { _ =>
      Done.getInstance()
    }
  }

  private def entityRef(id: String): PersistentEntityRef[BarbequeCommand] =
    persistentEntityRegistry.refFor[BarbequeEntity](id)
}

