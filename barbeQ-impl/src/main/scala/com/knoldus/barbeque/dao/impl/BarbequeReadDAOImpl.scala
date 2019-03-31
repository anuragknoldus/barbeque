package com.knoldus.barbeque.dao.impl

import com.datastax.driver.core.Row
import com.knoldus.barbeque.dao.api.BarbequeReadDAO
import com.knoldus.barbeque.util.{Constants, Queries}
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraSession

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

class BarbequeReadDAOImpl(cassandraSession: CassandraSession) extends BarbequeReadDAO {

  override def getOrderStatusByOrderId(orderId: String): Future[List[String]] = {
    implicit val ec: ExecutionContextExecutor = ExecutionContext.global

    val rawMonitoringData: Future[Seq[Row]] = cassandraSession
      .selectAll(Queries.getOrderStatus.replace(Constants.OrderIdToReplace, orderId))

    rawMonitoringData.map(result => result.map(row => row.getString("status")).toList)
  }
}
