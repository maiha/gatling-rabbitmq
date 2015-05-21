Introduction
============

Support for load testing RabbitMQ endpoints using Gatling 2.2.0-M3 and gatling-sbt plugin


Simulation
==========

- Example: src/test/scala/io/gatling/amqp/AmqpPublishingSimulation.scala

```
implicit val amqpProtocol: AmqpProtocol = amqp
  .host("localhost")
  .port(5672)
  .auth("guest", "guest")
  .poolSize(5)

```


Run
===

- ensure RabbitMQ server is running in localhost or specified server in application.conf

```
% sbt
> test
```

Forked
======

- from: https://github.com/fhalim/gatling-rabbitmq
- changes
    - use sbt rather than gradle (cause I'm newbie to gradle :))
    - delete existing tests because gatling-sbt uses testing framework for its simulations
    - update gatling version from 2.0.0-M3a to 2.2.0-M3
    - payload is now fixed string (just testing purpose)
    - AmqpProtocol
