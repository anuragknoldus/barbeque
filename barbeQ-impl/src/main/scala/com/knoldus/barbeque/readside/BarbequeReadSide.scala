package com.knoldus.barbeque.readside

import akka.Done
import com.datastax.driver.core.BoundStatement
import com.knoldus.barbeque.event.{BarbequeEvent, BarbequeOrderDataCreated}
import com.knoldus.barbeque.model.BarbequeLatestOrderDetails
import com.knoldus.barbeque.util.{Constants, Queries}
import com.lightbend.lagom.scaladsl.persistence.cassandra.{CassandraReadSide, CassandraSession}
import com.lightbend.lagom.scaladsl.persistence.{AggregateEventTag, ReadSideProcessor}
import play.api.libs.json.Json

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

class BarbequeReadSide(cassandraSession: CassandraSession, readSide: CassandraReadSide)
                      (implicit ec: ExecutionContext) extends ReadSideProcessor[BarbequeEvent] {

  override def aggregateTags: Set[AggregateEventTag[BarbequeEvent]] = BarbequeEvent.TAG.allTags

  override def buildHandler(): ReadSideProcessor.ReadSideHandler[BarbequeEvent] =
    readSide.builder[BarbequeEvent]("BarbequeEventReadSide")
      .setGlobalPrepare(createTable)
      .setEventHandler[BarbequeOrderDataCreated](streamElement => insertBarbequeOrder(streamElement.event.barbequeOrders)).build()


  private def createTable(): Future[Done] = {
    Await.result(cassandraSession.executeWrite(Queries.createType), 10.seconds)
    cassandraSession.executeCreateTable(Queries.CreateTable)
  }

  private def insertBarbequeOrder(barbequeOrders: BarbequeLatestOrderDetails): Future[List[BoundStatement]] = {
    val value = Json.stringify(Json.toJson(barbequeOrders.copy(status = Constants.AcceptedStatus)))
    val insertQuery = Queries.InsertOrder.replace("Replace_JSON", value)
    cassandraSession.executeWrite(insertQuery)
    Future.successful(List.empty)
  }
}
