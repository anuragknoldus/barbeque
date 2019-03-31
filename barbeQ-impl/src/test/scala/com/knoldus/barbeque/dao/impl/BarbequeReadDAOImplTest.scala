package com.knoldus.barbeque.dao.impl

import com.knoldus.barbeque.dao.api.BarbequeReadDAO
import com.knoldus.barbeque.es.BarbequeEventSourcingProcessor
import com.knoldus.barbeque.{BarbequeSerializerRegistry, BarbequeService, BarbequeServiceImpl}
import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.persistence.cassandra.{CassandraPersistenceComponents, CassandraSession}
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomServer, LocalServiceLocator}
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
import com.softwaremill.macwire.wire
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, Matchers}
import play.api.libs.ws.ahc.AhcWSComponents

import scala.concurrent.Await
import scala.concurrent.duration._

class BarbequeReadDAOImplTest extends AsyncWordSpec with Matchers with BeforeAndAfterAll with MockitoSugar {

  private final val waitingTimeInSeconds = 20.seconds
  private lazy val server = ServiceTest
    .startServer(ServiceTest.defaultSetup.withCassandra(true)) { ctx =>
      new LagomApplication(ctx) with LocalServiceLocator with CassandraPersistenceComponents
        with AhcWSComponents {
        override lazy val serviceLocator: ServiceLocator.NoServiceLocator.type = NoServiceLocator
        override lazy val lagomServer: LagomServer = serverFor[BarbequeService](wire[BarbequeServiceImpl])
        override lazy val jsonSerializerRegistry: BarbequeSerializerRegistry.type = BarbequeSerializerRegistry
        lazy val barbequeReadDAOImpl: BarbequeReadDAO = wire[BarbequeReadDAOImpl]
        lazy val esService: BarbequeEventSourcingProcessor = wire[BarbequeEventSourcingProcessor]
      }
    }

  val session: CassandraSession = server.application.cassandraSession
  val barbequeDAO: BarbequeReadDAO = server.application.barbequeReadDAOImpl

  override protected def beforeAll(): Unit = {
    createSchema(session)
  }

  override protected def afterAll(): Unit = {
    server.stop()
  }

  private val orderId = "e37bbe94-98e8-4459-94eb"
  private val orderIdNotPresent = "e37bbe94-98e8-4459"

  private val status = "accepted"

  private def createSchema(session: CassandraSession): Unit = {
    //Create Keyspace
    val createKeyspace = session.executeWrite("CREATE KEYSPACE IF NOT EXISTS barbeque WITH replication = " +
      "{'class': 'SimpleStrategy','replication_factor': '1'};")
    Await.result(createKeyspace, waitingTimeInSeconds)

    val createType = session.executeWrite("CREATE TYPE IF NOT EXISTS  barbeque.orderType(foodName text," +
      " quantity int, price double);")
    Await.result(createType, waitingTimeInSeconds)

    //Create table
    val ss =
      """CREATE TABLE IF NOT EXISTS barbeque.barbequeOrderStore (
        | order_id TEXT, timestamp TEXT, orderList frozen<list<orderType>>, status Text, "
        | PRIMARY KEY(order_id, timestamp));""".stripMargin
    print(ss)
    val createTable = session.executeCreateTable(
      """CREATE TABLE IF NOT EXISTS barbeque.barbequeOrderStore (
        | order_id TEXT, timestamp TEXT, orderList frozen<list<orderType>>, status Text,
        | PRIMARY KEY(order_id, timestamp));""".stripMargin)
    Await.result(createTable, waitingTimeInSeconds)

    //Insert data
    val insertData = session.executeWrite(
      s"""INSERT INTO barbeque.barbequeOrderStore JSON '{"order_id":"$orderId",
         |"timestamp":"28-03-2018","orderlist":[{"foodName":"food1","quantity":1,"price":250},{"foodName":"food2",
         |"quantity":1,"price":250}],"status":"$status"}';""".stripMargin)
  }

  "Barbeque Read DAO" should {
    "get order status by orderId" in {
      val result = barbequeDAO getOrderStatusByOrderId orderId

      result.map(response => assert(response.head.equals(status)))
    }

    "get order status list empty when orderId is not present" in {
      val result = barbequeDAO getOrderStatusByOrderId orderIdNotPresent

      result.map(response => assert(response.isEmpty))
    }
  }
}
