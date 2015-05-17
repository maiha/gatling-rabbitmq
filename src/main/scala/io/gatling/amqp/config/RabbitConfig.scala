package io.gatling.amqp.config

import com.typesafe.config._
import com.github.kxbmap.configs._
import io.gatling.amqp.data._

case class RabbitConfig(config: Config) {
  // configs
  private val rabbit   = config.getConfig("rabbitmq")
  private val exchange = rabbit.getConfig("exchange")

  // variables
  private val host     = rabbit.get[String]("host")
  private val port     = rabbit.get[Int]("port")
  private val vhost    = rabbit.get[String]("vhost")
  private val user     = rabbit.get[String]("user")
  private val password = rabbit.get[String]("password")
  private val xName    = exchange.get[String]("name")
  private val xType    = exchange.get[String]("type")

  def toAmqpProtocol: AmqpProtocol = {
    AmqpProtocol.default.copy(
      connection = Connection(host = host, port = port, vhost = vhost, user = user, password = password),
      _exchange  = Some(Exchange(name = xName, tpe = xType))
    )
  }
}
