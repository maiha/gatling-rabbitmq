package net.fawad.rabbitmqloadgen

case class Publish(msg: Message, exchangeInfo: ExchangeInfo)

case class ExchangeInfo(name: String, exchangeType: String)

case class InitializeSubscriber(exchangeInfo: ExchangeInfo)
