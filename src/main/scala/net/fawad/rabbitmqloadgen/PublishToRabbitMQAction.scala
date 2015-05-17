package net.fawad.rabbitmqloadgen

import akka.actor.ActorRef
import scala.Some
import io.gatling.core.action.Chainable
import io.gatling.core.result.message.{ RequestTimings, KO, OK, Status }
import io.gatling.core.result.writer.DataWriters
import io.gatling.core.session.Session
import io.gatling.core.structure.ScenarioContext
import io.gatling.core.util.TimeHelper.nowMillis
import akka.pattern.ask
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Failure
import akka.util.Timeout

class PublishToRabbitMQAction(val next: ActorRef, ctx: ScenarioContext, interactor: ActorRef, exchangeInfo: ExchangeInfo, gen: Iterator[Message]) extends Chainable {
  override def execute(session: Session) {
    var startedAt : Long = 0L
    var finishedAt: Long = 0L
    val timeout = Timeout(10 seconds)
    var status: Status = OK
    var errorMessage: Option[String] = None
    try {
      startedAt = nowMillis
      val msg = gen.next()
      Await.result((interactor ask Publish(msg, exchangeInfo))(timeout), Duration.Inf) match {
        case Failure(e) => throw e
        case _ =>
      }
    } catch {
      case e: Exception =>
        errorMessage = Some(e.getMessage)
        logger.error("Unable to publish", e)
        status = KO
    } finally {
      finishedAt = nowMillis

      val timings = RequestTimings(startedAt, finishedAt, finishedAt, finishedAt)
      val requestName = "RabbitMQ Publishing"
      ctx.dataWriters.logResponse(session, requestName, timings, status, None, errorMessage)

      next ! session
    }
  }
}
