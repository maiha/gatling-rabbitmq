package io.gatling.amqp.config

import com.typesafe.config.ConfigFactory

trait AmqpConfig {
  private   lazy val config     = ConfigFactory.load
  protected lazy val rabbit     = RabbitConfig(config)
  protected lazy val simulation = SimulationConfig(config)
}
