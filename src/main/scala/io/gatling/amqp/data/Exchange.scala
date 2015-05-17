package io.gatling.amqp.data

case class Exchange(name: String, tpe: String) {
  def validate: Unit = {
    require(tpe.nonEmpty,  "RabbitMQ exchange.type is not set")
  }
}

object Exchange {
  def default: Exchange = new Exchange(name = "", tpe = "direct")
}
