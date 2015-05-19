package io.gatling.amqp.infra

import akka.actor._
import com.rabbitmq.client.Channel
import io.gatling.amqp.config._
import io.gatling.amqp.data._
import pl.project13.scala.rainbow._
import resource.managed

abstract class AmqpActor(implicit amqp: AmqpProtocol) extends Actor with ActorLogging {
  protected val conn = amqp.newConnection

  def onChannel[A](action: Channel => A) = {
    for (channel <- managed(conn.createChannel())) {
      action(channel)
    }
  }
}
