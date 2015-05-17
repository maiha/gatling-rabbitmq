package net.fawad.rabbitmqloadgen

import io.gatling.core.Predef._
import scala.concurrent.duration._
import akka.actor._
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.structure.ScenarioContext
import akka.pattern.ask
import net.fawad.rabbitmqloadgen._
import akka.routing.RoundRobinPool
import akka.util.Timeout
import scala.concurrent.Await
import java.io.File
import MessageTransformers._
import com.typesafe.config.ConfigFactory

class RabbitMQPublishingSimulation extends Simulation {
  implicit val timeout = Timeout(5 seconds)

  // configs
  private val config     = ConfigFactory.load
  private val simulation = SimulationConfig(config)
  private val rmq        = RmqConfig(config)

  // define ActorSystem lazily because it's not available yet
  // TODO: define AMQP Protocol to warm up interactors with ActorSystem
  private var systemOption: Option[ActorSystem] = None
  private def setSystem(system: ActorSystem): Unit = systemOption = Some(system)
  private def system: ActorSystem = systemOption.get

  val rmqPoolProps = Props(new RabbitMQInteractor(rmq.connection)).withRouter(RoundRobinPool(nrOfInstances = simulation.concurrency))

  lazy val interactors: ActorRef = system.actorOf(rmqPoolProps)
  // TODO: This is probably a stink. Figure out a good way of handling this
  //    Await.result(interactors ask InitializeSubscriber(connection.exchange), timeout)

  val gen = Stream.continually(Message(simulation.payload)).iterator

  val publishToRabbitMQ = new ActionBuilder {
    def build(system: ActorSystem, next: ActorRef, ctx: ScenarioContext): ActorRef = {
      setSystem(system)
      system.actorOf(Props(new PublishToRabbitMQAction(next, ctx, interactors, rmq.exchange, gen)))
    }
  }

  val scn = scenario(simulation.name).repeat(simulation.requests) {
    exec(publishToRabbitMQ)
  }

  setUp(scn.inject(rampUsers(simulation.concurrency) over (2 seconds)))
//    .assertions(global.responseTime.max.lessThan(20), global.successfulRequests.percent.is(100))
}


