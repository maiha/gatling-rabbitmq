package io.gatling.amqp.infra

import akka.actor._
import com.rabbitmq.client.Channel
import io.gatling.amqp.config._
import io.gatling.amqp.data._
import pl.project13.scala.rainbow._
import resource.managed

import scala.util.{Failure, Success}

class RmqPublisher(implicit amqp: AmqpProtocol) extends Actor with ActorLogging {
  private val conn = amqp.newConnection

  override def receive = {
    case PublishRequest(ex, routingKey, props, payload) => onChannel { channel =>
      log.info(s"PublishRequest(${ex.name}, $routingKey)".green)
      try {
        channel.basicPublish(ex.name, routingKey, props, payload)
        sender ! Success(null)
      } catch {
        case e: Throwable =>
          log.error(e.toString.red)
          sender ! Failure(e)
      }
    }
  }

  def onChannel[A](action: Channel => A) = {
    for (channel <- managed(conn.createChannel())) {
      action(channel)
    }
  }

}
