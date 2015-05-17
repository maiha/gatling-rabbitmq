package net.fawad.rabbitmqloadgen

import com.rabbitmq.client.{Channel, ConnectionFactory}
import akka.actor._
import resource.managed
import scala.util.{Failure, Success}

class RabbitMQInteractor(factory: ConnectionFactory) extends Actor with ActorLogging {
  val conn = factory.newConnection

  override def receive = {
    case Publish(msg, exchangeInfo) => onChannel {
      channel =>
        log.debug("Publishing message")
        try {
          channel.basicPublish(exchangeInfo.name, "", msg.properties, msg.body)
          sender ! Success(null)
        } catch {
          case e: Throwable => sender ! Failure(e)
        }

    }
    case InitializeSubscriber(exchangeInfo) =>
      onChannel {
        channel =>
          log.info(s"Initializing RabbitMQ exchange ${exchangeInfo.name}")
          channel.exchangeDeclare(exchangeInfo.name, exchangeInfo.exchangeType, true)
      }
      onChannel {
        channel =>
          log.info(s"Initializing RabbitMQ queue ${exchangeInfo.name}")
          channel.queueDeclare(exchangeInfo.name, true, false, false, null)
      }
      onChannel {
        channel =>
          log.info(s"Initializing RabbitMQ binding ${exchangeInfo.name}")
          channel.queueBind(exchangeInfo.name, exchangeInfo.name, "")
      }
      sender ! Success()
  }

  def onChannel[A](action: Channel => A) = {
    for (channel <- managed(conn.createChannel())) {
      action(channel)
    }
  }

}
