package io.gatling.amqp

import akka.actor._
import io.gatling.amqp.action._
import io.gatling.amqp.config._
import io.gatling.amqp.data._
import io.gatling.core.Predef._
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.structure.ScenarioContext

import io.gatling.amqp.Predef._
import scala.concurrent.duration._

class AmqpPublishingSimulation extends Simulation {
  // protocol
  implicit val amqpProtocol: AmqpProtocol = amqp
    .host("localhost")
    .port(5672)
    .auth("guest", "guest")
    .poolSize(5)

//  amqpConf prepare DeclareQueue("q1", autoDelete = false)

  val request = PublishRequest("q1", payload = "{foo:1}")

  /*
   * TODO: IMAGE
   * exec(amqp.publish("q1", payload))
   * exec(amqp.publish("q1", payload).confirm)
   */

  val scn = scenario("RabbitMQ Publishing").repeat(1000) {
    exec(
      amqp("Publish")
        .publish(request)
    )
  }

  setUp(scn.inject(rampUsers(10) over (2 seconds))).protocols(amqpProtocol)
//    .assertions(global.responseTime.max.lessThan(20), global.successfulRequests.percent.is(100))
}


