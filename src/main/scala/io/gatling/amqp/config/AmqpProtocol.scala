package io.gatling.amqp.config

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import com.rabbitmq.client.ConnectionFactory
import com.typesafe.scalalogging.StrictLogging
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.collection.mutable.ArrayBuffer
import scala.util._

import io.gatling.core.config.Protocol
import io.gatling.core.controller.throttle.Throttler
import io.gatling.core.result.writer.DataWriters
import io.gatling.core.session.Session
import io.gatling.amqp.data._
import io.gatling.amqp.infra._

import pl.project13.scala.rainbow._

/**
 * Wraps a AMQP protocol configuration
 */
case class AmqpProtocol(
  connection: Connection               = Connection.default,
  _exchange : Option[Exchange]         = None
) extends Protocol with StrictLogging {

  private val preparings : ArrayBuffer[AmqpMessage] = ArrayBuffer[AmqpMessage]()

  /**
   * mutable variables (initialized in warmUp)
   */
  private var systemOpt: Option[ActorSystem] = None
  private var manageOpt: Option[ActorRef]    = None
  private var routerOpt: Option[ActorRef]    = None

  def system  : ActorSystem = systemOpt.getOrElse{ throw new RuntimeException("ActorSystem is not defined yet") }
  def manager : ActorRef    = manageOpt.getOrElse{ throw new RuntimeException("manager is not defined yet") }
  def router  : ActorRef    = routerOpt.getOrElse{ throw new RuntimeException("router is not defined yet") }
  def exchange: Exchange    = _exchange.getOrElse{ throw new RuntimeException("exchange not defined") }

  /**
   * warmUp AMQP protocol (invoked by gatling framework)
   */
  override def warmUp(system: ActorSystem, dataWriters: DataWriters, throttler: Throttler): Unit = {
    super.warmUp(system, dataWriters, throttler)
    systemOpt = Some(system)
    routerOpt = Some(system.actorOf(Props(new RmqRouter()(this))))
    manageOpt = Some(system.actorOf(Props(new AmqpManager()(this))))

    val timeout = Timeout(3 seconds)

    for (msg <- preparings) {
      Await.result((manager ask msg)(timeout), Duration.Inf) match {
        case Success(m) => logger.info(s"amqp: $m".green)
        case Failure(e) => throw e
      }
    }
  }

  /**
   * finalize user session about AMQP (invoked by gatling framework)
   */
  override def userEnd(session: Session): Unit = {
    super.userEnd(session)
  }

  def prepare(msg: AmqpMessage): Unit = {
    preparings += msg
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
