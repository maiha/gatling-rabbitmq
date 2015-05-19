package io.gatling.amqp.infra

import akka.actor._
import com.rabbitmq.client.Channel
import io.gatling.amqp.config._
import io.gatling.amqp.data._
import pl.project13.scala.rainbow._
import resource.managed
import scala.util.{Failure, Success}

abstract class AmqpActor(implicit amqp: AmqpProtocol) extends Actor with ActorLogging {
  protected lazy val className = getClass().getSimpleName
  protected lazy val conn = amqp.newConnection

  override def preStart(): Unit = {
    super.preStart()
    log.info(s"amqp: Start actor `$className'".yellow)
  }

  override def postStop(): Unit = {
    log.info(s"amqp: Stop actor `$className'".yellow)
    super.preStart()
  }

  protected def onChannel[A](action: Channel => A) = {
    for (channel <- managed(conn.createChannel())) {
      action(channel)
    }
  }

  protected def interact[A](successMsg: Any)(action: Channel => A) = {
    onChannel { channel =>
      try {
        action(channel)
        sender() ! Success(successMsg)
      } catch {
        case e: Throwable => sender() ! Failure(e)
      }
    }
  }
}
