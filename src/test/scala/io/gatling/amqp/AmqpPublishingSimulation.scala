package io.gatling.amqp

import akka.actor._
import io.gatling.amqp.action._
import io.gatling.amqp.config.AmqpConfig
import io.gatling.amqp.data._
import io.gatling.core.Predef._
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.structure.ScenarioContext

import scala.concurrent.duration._

class AmqpPublishingSimulation extends Simulation with AmqpConfig {
  // protocol
  implicit val amqpProtocol = rabbit.toAmqpProtocol

  val request   = PublishRequest(amqpProtocol.exchange, simulation.payload)
  val generator = Stream.continually(request).iterator

  val publish = new ActionBuilder {
    def build(system: ActorSystem, next: ActorRef, ctx: ScenarioContext): ActorRef = {
      system.actorOf(Props(new PublishAction(next, ctx, generator)))
    }
  }

  val scn = scenario(simulation.name).repeat(simulation.requests) {
    exec(publish)
  }

  setUp(scn.inject(rampUsers(simulation.concurrency) over (2 seconds))).protocols(amqpProtocol)
//    .assertions(global.responseTime.max.lessThan(20), global.successfulRequests.percent.is(100))
}


