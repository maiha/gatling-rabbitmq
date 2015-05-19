package io.gatling.amqp.infra

import akka.actor._
import com.rabbitmq.client.Channel
import io.gatling.amqp.config._
import io.gatling.amqp.data._
import pl.project13.scala.rainbow._
import resource.managed

import collection.JavaConversions._
import scala.util.{Failure, Success}

class AmqpManager(implicit amqp: AmqpProtocol) extends AmqpActor {
  log.info("Initialized AmqpManager".green)

  override def receive = {
    case DeclareExchange(name, tpe, durable, autoDelete, arguments) =>
      onChannel { channel =>
        log.info(s"Initializing RabbitMQ exchange $name")
        channel.exchangeDeclare(name, tpe, durable, autoDelete, arguments)
      }
    case DeclareQueue(name, durable, exclusive, autoDelete, arguments) =>
      onChannel { channel =>
        log.info(s"Initializing RabbitMQ queue $name")
        channel.queueDeclare(name, durable, exclusive, autoDelete, arguments)
      }
    case DeclareBinding(queue, exchange, routingKey, arguments) =>
      onChannel { channel =>
        log.info(s"Initializing RabbitMQ binding $exchange to $queue")
        channel.queueBind(queue, exchange, routingKey, arguments)
      }
  }
}
