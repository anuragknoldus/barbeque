package com.knoldus.barbeque

import akka.NotUsed
import com.knoldus.barbeque.models.{BarbequeOrderResponse, BarbequeOrders}
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}

trait BarbequeService extends Service {

  /**
    * Gets the health of the service.
    *
    * @return ServiceCall to return the health of the service.
    */
  def health(): ServiceCall[NotUsed, String]

  /**
    * Get the current version of the service.
    *
    * @return ServiceCall to return the version of the service.
    */
  def version(): ServiceCall[NotUsed, String]

  /**
    * Inserts the order in the database.
    *
    * @return ServiceCall to store the data.
    */
  def addOrder(): ServiceCall[BarbequeOrders, BarbequeOrderResponse]

  /**
    * Gets the health of the service.
    *
    * @return ServiceCall to return the health of the service.
    */
  def getStatus(orderId: String): ServiceCall[NotUsed, String]

  override final def descriptor: Descriptor = {
    import Service._
    // @formatter:off
    named("barbeque")
      .withCalls(
        restCall(Method.GET, "/api/barbeque/health", health _),
        restCall(Method.GET, "/api/barbeque/version", version _),
        restCall(Method.POST, "/barbeque/order", addOrder _),
        restCall(Method.GET, "/barbeque/order/:orderId/status", getStatus _)
      )
      .withAutoAcl(true)
    // @formatter:on
  }
}
