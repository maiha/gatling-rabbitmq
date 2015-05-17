package io.gatling.amqp.infra

import akka.actor._
import akka.routing._
import io.gatling.amqp.config._
import io.gatling.amqp.data._
import pl.project13.scala.rainbow._

class RmqRouter(implicit amqp: AmqpProtocol) extends Actor with ActorLogging {
//  private val router = context.actorOf(FromConfig.props(Props[RmqPublisher]))
  private var router = Router(RoundRobinRoutingLogic(), Vector[Routee]())

  override def preStart(): Unit = {
    super.preStart()
    for(i <- 1 to amqp.connection.poolSize) { addRoutee() }
  }

  def receive: Receive = {
    case m: PublishRequest =>
      router.route(m, sender())
    case Terminated(ref) =>
      router = router.removeRoutee(ref)
      log.error("RmqPublisher terminated".yellow)
//      addRoutee
  }

  private def addRoutee(): Unit = {

  // TODO: This is probably a stink. Figure out a good way of handling this
  //    Await.result(interactors ask InitializeSubscriber(connection.exchange), timeout)

    val ref = context.actorOf(Props(new RmqPublisher()))
    context watch ref
    router = router.addRoutee(ref)
  }
}
