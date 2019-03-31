package com.knoldus.barbeque.dao.api

import scala.concurrent.Future

trait BarbequeReadDAO {

  def getOrderStatusByOrderId(orderId: String): Future[List[String]]
}
