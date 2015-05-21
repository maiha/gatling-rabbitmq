package io.gatling.amqp.request.builder

import io.gatling.core.session.Expression
import io.gatling.amqp.data._

case class CommonAttributes(
  requestName: Expression[String]
)

case class AmqpAttributes(
  requests: Seq[AmqpRequest]
)

// (implicit configuration: GatlingConfiguration)
case class AmqpRequestBuilder(commonAttributes: CommonAttributes, amqpAttributes: AmqpAttributes) {

}

object AmqpRequestBuilder {
//  implicit def toActionBuilder(requestBuilder: HttpRequestBuilder)(implicit defaultHttpProtocol: DefaultHttpProtocol, httpEngine: HttpEngine) =new HttpRequestActionBuilder(requestBuilder, httpEngine)
}
