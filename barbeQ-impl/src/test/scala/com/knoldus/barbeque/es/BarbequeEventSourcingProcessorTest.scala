package com.knoldus.barbeque.es

import akka.Done
import com.knoldus.barbeque.command.BarbequeCommand
import com.knoldus.barbeque.dao.api.BarbequeReadDAO
import com.knoldus.barbeque.dao.impl.BarbequeReadDAOImpl
import com.knoldus.barbeque.entity.BarbequeEntity
import com.knoldus.barbeque.model.BarbequeLatestOrderDetails
import com.knoldus.barbeque.models.BarbequeOrderInformation
import com.knoldus.barbeque.{BarbequeSerializerRegistry, BarbequeService, BarbequeServiceImpl}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRef
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomServer, LocalServiceLocator}
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
import com.softwaremill.macwire.wire
import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll}
import play.api.libs.ws.ahc.AhcWSComponents

class BarbequeEventSourcingProcessorTest extends AsyncWordSpec with BeforeAndAfterAll {

  private val server = ServiceTest.startServer(ServiceTest.defaultSetup
    .withCassandra(true)) { ctx =>
    new LagomApplication(ctx) with LocalServiceLocator with CassandraPersistenceComponents with AhcWSComponents {
      override lazy val lagomServer: LagomServer = serverFor[BarbequeService](wire[BarbequeServiceImpl])
      override lazy val jsonSerializerRegistry: BarbequeSerializerRegistry.type = BarbequeSerializerRegistry
      lazy val barbequeReadDAOImpl: BarbequeReadDAO = wire[BarbequeReadDAOImpl]
      lazy val barbequeEsProcessor: BarbequeEventSourcingProcessor = wire[BarbequeEventSourcingProcessor]
      persistentEntityRegistry.register(wire[BarbequeEntity])
    }
  }

  private val latestOrderDetails = BarbequeLatestOrderDetails("97d7ea5e-1ba0-4291-8ea5-a35aee6d0cfd",
    "20-03-2018", List(BarbequeOrderInformation("food1", 1, 200.0)))
  private val entityRegistry = server.application.persistentEntityRegistry
  val id = s"${latestOrderDetails.orderId}-${latestOrderDetails.timestamp}"
  val persistenceEntityRef: PersistentEntityRef[BarbequeCommand] = entityRegistry.refFor[BarbequeEntity](id)

  "BarbequeEsProcessor" should {
    "send barbeque order data to barbeque entity" in {

      server.application.barbequeEsProcessor.sendOrderInformation(latestOrderDetails).map { res =>
        assert(res == Done)
      }
    }
  }

  override protected def afterAll(): Unit = server.stop()
}
