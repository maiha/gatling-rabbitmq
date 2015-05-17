package io.gatling.amqp.data

import com.rabbitmq.client.AMQP.BasicProperties

case class PublishRequest(exchange: Exchange, properties: BasicProperties, payload: Array[Byte])

object PublishRequest {
  def apply(exchange: Exchange, payload: String)   : PublishRequest = new PublishRequest(exchange, props(), payload.getBytes("UTF-8"))
  def apply(exchange: Exchange, bytes: Array[Byte]): PublishRequest = new PublishRequest(exchange, props(), bytes)

  def props(): BasicProperties = {
    val builder = new BasicProperties.Builder()
    builder.build()
  }
}
