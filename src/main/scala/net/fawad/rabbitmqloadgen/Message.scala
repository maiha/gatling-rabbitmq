package net.fawad.rabbitmqloadgen

import com.rabbitmq.client.AMQP.BasicProperties

case class Message(body: Array[Byte], properties: BasicProperties)

object Message {
  def apply(body: String): Message = {
    val bytes = body.getBytes("UTF-8")
    Message(bytes, props())
  }

  def props(): BasicProperties = {
    val builder = new BasicProperties.Builder()
    builder.build()
  }
}
