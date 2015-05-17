package net.fawad.rabbitmqloadgen

import com.typesafe.config._
import com.github.kxbmap.configs._

case class SimulationConfig(config: Config) {
  private val c = config.getConfig("simulation")

  def name       : String = c.get[String]("name")
  def concurrency: Int    = c.get[Int]("concurrency")
  def requests   : Int    = c.get[Int]("requests")
  def payload    : String = c.get[String]("payload")
}
