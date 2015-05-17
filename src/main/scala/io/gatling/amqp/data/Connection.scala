package io.gatling.amqp.data

case class Connection(
  host    : String   = "localhost",
  port    : Int      = 5672,
  vhost   : String   = "/",
  user    : String   = "guest",
  password: String   = "guest",
  poolSize: Int      = 3
) {

  def validate: Unit = {
    require(host.nonEmpty    ,  "RabbitMQ host is not set")
    require(port > 0         , s"RabbitMQ port is invalid: $port")
    require(vhost.nonEmpty   ,  "RabbitMQ vhost is not set")
    require(user.nonEmpty    ,  "RabbitMQ user is not set")
    require(password.nonEmpty,  "RabbitMQ password is not set")
    require(poolSize > 0     , s"RabbitMQ poolSize is invalid: $poolSize")
  }
}

object Connection {
  def default = new Connection
}
