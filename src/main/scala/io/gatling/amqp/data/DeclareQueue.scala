package io.gatling.amqp.data

case class DeclareQueue(
  name: String,
  durable: Boolean,
  exclusive: Boolean,
  autoDelete: Boolean,
  arguments: Arguments
)
