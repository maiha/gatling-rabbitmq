package io.gatling.amqp.data

case class DeclareBinding(
  queue: String,
  exchange: String,
  routingKey: String,
  arguments: Arguments
)
