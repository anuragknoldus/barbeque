package com.knoldus.barbeque.entity

import akka.Done
import akka.actor.ActorSystem
import akka.actor.setup.ActorSystemSetup
import com.knoldus.barbeque.command.CreateOrder
import com.knoldus.barbeque.event.BarbequeOrderDataCreated
import com.knoldus.barbeque.BarbequeSerializerRegistry
import com.knoldus.barbeque.model.BarbequeLatestOrderDetails
import com.knoldus.barbeque.models.BarbequeOrderInformation
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import com.lightbend.lagom.scaladsl.testkit.PersistentEntityTestDriver
import org.scalatest.{FunSuite, Matchers, WordSpecLike}

class BarbequeEntityTest extends WordSpecLike with Matchers {

  val orderId = "97d7ea5e-1ba0-4291-8ea5-a35aee6d0cfd"
  val system = ActorSystem("PostSpec", ActorSystemSetup(JsonSerializerRegistry
    .serializationSetupFor(BarbequeSerializerRegistry)))


  private val latestOrderDetails = BarbequeLatestOrderDetails(orderId, "20-03-2018",
    List(BarbequeOrderInformation("food1", 1, 200.0)))

  "Barbeque Entity" should {
    "handle Create Barbeque Order Data" in {
      val driver = new PersistentEntityTestDriver(system, new BarbequeEntity, orderId)
      val outcome = driver.run(CreateOrder(latestOrderDetails))
      outcome.events should ===(Seq(BarbequeOrderDataCreated(latestOrderDetails)))
      outcome.state.id should ===(orderId)
      outcome.replies should ===(Seq(Done))
      outcome.issues.toList should be(List())
    }
  }
}
