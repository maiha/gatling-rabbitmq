package io.gatling.amqp

import akka.actor._
import io.gatling.amqp.action._
import io.gatling.amqp.config._
import io.gatling.amqp.data._
import io.gatling.core.Predef._
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.structure.ScenarioContext

import scala.concurrent.duration._

class AmqpPublishingSimulation extends Simulation with AmqpSupport {
  // protocol
  implicit val amqpProtocol = config.toAmqpProtocol

  amqpProtocol prepare DeclareQueue("q1", autoDelete = false)

  val request   = PublishRequest("q1", payload = "{foo:1}")
  val generator = Stream.continually(request).iterator

  val publish = new ActionBuilder {
    def build(system: ActorSystem, next: ActorRef, ctx: ScenarioContext): ActorRef = {
      system.actorOf(Props(new PublishAction(next, ctx, generator)))
    }
  }

  /*
   * TODO: IMAGE
   * exec(amqp.publish("q1", payload))
   * exec(amqp.publish("q1", payload).confirm)
   */

  val scn = scenario("RabbitMQ Publishing").repeat(10000) {
    exec(publish)
  }

  setUp(scn.inject(rampUsers(10) over (2 seconds))).protocols(amqpProtocol)
//    .assertions(global.responseTime.max.lessThan(20), global.successfulRequests.percent.is(100))
}


