package io.gatling.amqp.infra

import akka.actor._
import com.rabbitmq.client.Channel
import io.gatling.amqp.config._
import io.gatling.amqp.data._
import resource.managed

import scala.util.{Failure, Success}

class RmqPublisher(implicit amqp: AmqpProtocol) extends Actor with ActorLogging {
  private val conn = amqp.newConnection

  override def receive = {
    case PublishRequest(ex, props, payload) => onChannel {
      channel =>
        log.debug("Publishing message")
        try {
          channel.basicPublish(ex.name, "", props, payload)
          sender ! Success(null)
        } catch {
          case e: Throwable => sender ! Failure(e)
        }
    }
  }

  def onChannel[A](action: Channel => A) = {
    for (channel <- managed(conn.createChannel())) {
      action(channel)
    }
  }

}
