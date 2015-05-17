package io.gatling.amqp.config

import io.gatling.core.filter.{ BlackList, Filters, WhiteList }
import io.gatling.core.session._
import io.gatling.core.session.el.El
import io.gatling.amqp._

/**
 * Builder for AmqpProtocol used in DSL
 *
 * @param protocol the protocol being built
 */
case class AmqpProtocolBuilder(protocol: AmqpProtocol) {
  def build: AmqpProtocol = {
    protocol.validate
    protocol
  }
}

/**
 * AmqpProtocolBuilder class companion
 */
object AmqpProtocolBuilder {
  implicit def toAmqpProtocol(builder: AmqpProtocolBuilder): AmqpProtocol = builder.build

  def default: AmqpProtocolBuilder = new AmqpProtocolBuilder(AmqpProtocol.default)
}


