package com.knoldus.barbeque

import java.time.LocalDateTime
import java.util.Calendar

import akka.{Done, NotUsed}
import com.knoldus.barbeque.dao.api.BarbequeReadDAO
import com.knoldus.barbeque.es.BarbequeEventSourcingProcessor
import com.knoldus.barbeque.exception.{DataNotFoundException, InternalServerError, UnprocessableEntityException}
import com.knoldus.barbeque.model.BarbequeLatestOrderDetails
import com.knoldus.barbeque.models.{BarbequeOrderResponse, BarbequeOrders}
import com.knoldus.barbeque.util.BarbequeUtils.getRandomUUID
import com.knoldus.barbeque.util.Constants.Error
import com.knoldus.barbeque.util.DateTimeUtils.currentDateTime
import com.knoldus.barbeque.util.{BarbequeUtils, ConfigManager, DateTimeUtils}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.transport.TransportErrorCode
import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContext, Future}

class BarbequeServiceImpl(barbequeEventSourcing: BarbequeEventSourcingProcessor,
                          barbequeReadDAO: BarbequeReadDAO)(implicit ec: ExecutionContext)
  extends BarbequeService {

  private final val log = LoggerFactory.getLogger(classOf[BarbequeServiceImpl])

  override def health(): ServiceCall[NotUsed, String] = ServiceCall { _ =>
    log.info(s"Retrieving health of the service.")
    Future.successful("Health is up!")
  }

  override def version(): ServiceCall[NotUsed, String] = ServiceCall { _ =>
    log.info(s"Retrieving version of the service.")
    val version = ConfigManager.getValue("service.version")
    Future.successful(s"Version is $version")
  }

  override def addOrder(): ServiceCall[BarbequeOrders, BarbequeOrderResponse] = ServiceCall {
    request => {
      val id: String = getRandomUUID()
      if (true) {
        val barbequeLatestOrderDetails = BarbequeLatestOrderDetails(id, currentDateTime(),
          request.orderList)

        println(barbequeLatestOrderDetails)
        barbequeEventSourcing.sendOrderInformation(barbequeLatestOrderDetails)
          .map {
            case Done => BarbequeOrderResponse(id, barbequeLatestOrderDetails.status)
            case _ => throw InternalServerError(TransportErrorCode.fromHttp(Error.InternalServerErrorCode),
              Error.InternalServerErrorMessage)
          }
        Future.successful(BarbequeOrderResponse(id, barbequeLatestOrderDetails.status))
      }
      else {
        throw UnprocessableEntityException(TransportErrorCode
          .fromHttp(Error.UnprocessableErrorCode), Error.UnprocessableEntityErrorMessage)
      }
    }
  }

  override def getStatus(orderId: String): ServiceCall[NotUsed, String] = ServiceCall {
    _ => {
      barbequeReadDAO.getOrderStatusByOrderId(orderId).map { response =>
        if (response.isEmpty) {
          throw DataNotFoundException(TransportErrorCode
            .fromHttp(Error.DataNotFoundErrorCode), Error.DataNotFoundErrorMessage)
        } else {
          response.head
        }
      }
    }
  }
}
