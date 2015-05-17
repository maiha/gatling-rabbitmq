package net.fawad.rabbitmqloadgen

import com.typesafe.config._
import com.github.kxbmap.configs._
import com.rabbitmq.client.ConnectionFactory

case class RmqConfig(config: Config) {
  private val rmq = config.getConfig("rabbitmq")
  private val ex  = rmq.getConfig("exchange")

  private val host = rmq.get[String]("host")
  private val port = rmq.get[Int]("port")
  private val vhost = rmq.get[String]("vhost")
  private val user = rmq.get[String]("user")
  private val password = rmq.get[String]("password")
  private val xType = ex.get[String]("type")
  private val xName = ex.get[String]("name")

  def exchange: ExchangeInfo = ExchangeInfo(xName, xType)

  def connection: ConnectionFactory = {
    val factory = new ConnectionFactory()
    factory.setHost(host)
    factory.setPort(port)
    factory.setUsername(user)
    factory.setPassword(password)
    factory.setVirtualHost(vhost)
    factory
  }
}
