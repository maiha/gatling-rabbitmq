package io.gatling.amqp.infra

import akka.actor._
import com.rabbitmq.client.Channel
import io.gatling.amqp.config._
import io.gatling.amqp.data._
import resource.managed

import collection.JavaConversions._
import scala.util.{Failure, Success}

class AmqpManager(implicit amqp: AmqpProtocol) extends AmqpActor {
  log.debug("Initialized AmqpManager")

  override def receive = {
    case msg@ DeclareExchange(name, tpe, durable, autoDelete, arguments) =>
      onChannel { channel =>
        log.info(s"Initializing RabbitMQ exchange $name")
        try {
          channel.exchangeDeclare(name, tpe, durable, autoDelete, arguments)
          sender() ! Success(msg)
        } catch {
          case e: Throwable => sender() ! Failure(e)
        }
      }

    case msg@ DeclareQueue(name, durable, exclusive, autoDelete, arguments) =>
      onChannel { channel =>
        log.info(s"Initializing RabbitMQ queue $name")
        try {
          channel.queueDeclare(name, durable, exclusive, autoDelete, arguments)
          sender() ! Success(msg)
        } catch {
          case e: Throwable => sender() ! Failure(e)
        }

      }
    case msg@ BindQueue(exchange, queue, routingKey, arguments) =>
      onChannel { channel =>
        log.info(s"Initializing RabbitMQ binding $exchange to $queue")
        try {
          channel.queueBind(queue, exchange, routingKey, arguments)
          sender() ! Success(msg)
        } catch {
          case e: Throwable => sender() ! Failure(e)
        }
      }
  }
}
