package com.knoldus.barbeque

import akka.Done
import com.knoldus.barbeque.dao.api.BarbequeReadDAO
import com.knoldus.barbeque.es.BarbequeEventSourcingProcessor
import com.knoldus.barbeque.exception.DataNotFoundException
import com.knoldus.barbeque.model.BarbequeLatestOrderDetails
import com.knoldus.barbeque.models.{BarbequeOrderInformation, BarbequeOrders}
import org.mockito
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, Matchers}

import scala.concurrent.Future

class BarbequeServiceImplTest extends AsyncWordSpec with Matchers with BeforeAndAfterAll with MockitoSugar {

  val mockBarbequeEventSourcingProcessor: BarbequeEventSourcingProcessor = mock[BarbequeEventSourcingProcessor]
  val mockBarbequeReadDAO: BarbequeReadDAO = mock[BarbequeReadDAO]
  val barbequeServiceImpl = new BarbequeServiceImpl(mockBarbequeEventSourcingProcessor, mockBarbequeReadDAO)

  "Barbeque service" should {
    "retrieve health of the service" in {
      barbequeServiceImpl.health().invoke.map { response =>
        response shouldBe "Health is up!"
      }
    }

    "retrieve version of the service" in {
      barbequeServiceImpl.version().invoke.map { response =>
        response shouldBe "Version is 1.0.0-SNAPSHOT"
      }
    }

    "be able to get the response for the given orderId" in {
      when(mockBarbequeReadDAO.getOrderStatusByOrderId("order1"))
        .thenReturn(Future.successful(List("accepted")))
      val dd = barbequeServiceImpl.getStatus("order1").invoke()
      dd.map(s => assert(s == "accepted"))
    }

    "not be able to get the response for the given orderId" in {
      when(mockBarbequeReadDAO.getOrderStatusByOrderId("order1"))
        .thenReturn(Future.successful(List.empty))

      recoverToSucceededIf[DataNotFoundException] {
        barbequeServiceImpl.getStatus("order1").invoke()
      }
    }

    "be able to successfully save event manually" in {
      val information = BarbequeOrderInformation("food1", 1, 20.2)
      val details = BarbequeOrders(List(information))
      val mockAnyString = mockito.Matchers.anyString()
      when(mockBarbequeEventSourcingProcessor.sendOrderInformation(BarbequeLatestOrderDetails(
        mockAnyString, mockAnyString,List(information)))).thenReturn(Future.successful(Done))

      val ss = barbequeServiceImpl.addOrder().invoke(details)
      ss.map { response =>
        print(response.order_id)
        assert(response.order_id != null)
        assert(response.status == "pending")
      }
    }
  }
}