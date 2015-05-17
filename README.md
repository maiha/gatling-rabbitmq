Introduction
============

Support for load testing RabbitMQ endpoints using Gatling 2.2.0-M2 and gatling-sbt plugin


Setting
=======

- RabbitMQ: src/test/resources/application.conf
- Simulation: src/test/scala/RabbitMQPublishingSimulation.scala


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
    - move settings to typesafe config
    - update gatling version from 2.0.0-M3a to 2.2.0-M2
    - payload is now fixed string (just testing purpose)

TODO
====

- create AmqpProtocol to connect servers and use ActorSystem in warm up phase
