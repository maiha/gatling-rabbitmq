package io.gatling.amqp.config

import com.rabbitmq.client.ConnectionFactory
import akka.actor._
import io.gatling.core.config.Protocol
import io.gatling.core.controller.throttle.Throttler
import io.gatling.core.result.writer.DataWriters
import io.gatling.core.session.Session
import io.gatling.amqp.data._
import io.gatling.amqp.infra._

/**
 * Wraps a AMQP protocol configuration
 */
case class AmqpProtocol(
  connection: Connection       = Connection.default,
  _exchange : Option[Exchange] = None
) extends Protocol {

  /**
   * mutable variables (initialized in warmUp)
   */
  private var routerOpt: Option[ActorRef]    = None
  private var systemOpt: Option[ActorSystem] = None

  def router  : ActorRef    = routerOpt.getOrElse{ throw new RuntimeException("router is not defined yet") }
  def system  : ActorSystem = systemOpt.getOrElse{ throw new RuntimeException("ActorSystem is not defined yet") }
  def exchange: Exchange    = _exchange.getOrElse{ throw new RuntimeException("exchange not defined") }

  override def warmUp(system: ActorSystem, dataWriters: DataWriters, throttler: Throttler): Unit = {
    super.warmUp(system, dataWriters, throttler)
    systemOpt = Some(system)
    routerOpt = Some(system.actorOf(Props(new RmqRouter()(this))))
  }

  override def userEnd(session: Session): Unit = {
    super.userEnd(session)
  }


  def validate(): Unit = {
    connection.validate
    _exchange.foreach(_.validate)
  }

  def newConnection: com.rabbitmq.client.Connection = {
    import connection._
    val factory = new ConnectionFactory()
    factory.setHost(host)
    factory.setPort(port)
    factory.setUsername(user)
    factory.setPassword(password)
    factory.setVirtualHost(vhost)
    factory.newConnection
  }

  override def toString: String = {
    s"AmqpProtocol(hashCode=$hashCode)"
  }
}

object AmqpProtocol {
  def default: AmqpProtocol = new AmqpProtocol
}
