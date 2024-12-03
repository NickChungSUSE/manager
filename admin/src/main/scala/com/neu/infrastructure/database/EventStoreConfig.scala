package com.neu.infrastructure.database

import com.neu.application.core.CommonSettings.*
import com.typesafe.config.{ ConfigFactory, ConfigValueFactory }
import io.getquill.{ CassandraAsyncContext, SnakeCase }

import scala.jdk.CollectionConverters.*

trait EventStoreConfig {
  private lazy val config = ConfigFactory
    .empty()
    .withValue("keyspace", ConfigValueFactory.fromAnyRef(cassandraKeyspace))
    .withValue(
      "session.basic.contact-points",
      ConfigValueFactory.fromIterable(List(cassandraHosts).asJava)
    )
    .withValue(
      "session.basic.request.consistency",
      ConfigValueFactory.fromAnyRef("LOCAL_QUORUM")
    )
    .withValue(
      "session.basic.request.page-size",
      ConfigValueFactory.fromAnyRef(pageSize)
    )
    .withValue(
      "session.basic.request.timeout",
      ConfigValueFactory.fromAnyRef("5 seconds")
    )
    .withValue(
      "session.basic.request.serial-consistency",
      ConfigValueFactory.fromAnyRef("LOCAL_SERIAL")
    )
    .withValue(
      "session.basic.config-reload-interval",
      ConfigValueFactory.fromAnyRef("5 minutes")
    )
    .withValue(
      "session.advanced.auth-provider.username",
      ConfigValueFactory.fromAnyRef(cassandraUser)
    )
    .withValue(
      "session.advanced.auth-provider.password",
      ConfigValueFactory.fromAnyRef(cassandraPassword)
    )
    .withValue(
      "session.basic.pool.local.size",
      ConfigValueFactory.fromAnyRef(32)
    )
    .withValue(
      "session.basic.pool.remote.size",
      ConfigValueFactory.fromAnyRef(8)
    )
    .withValue(
      "session.advanced.reconnection-policy.base-delay",
      ConfigValueFactory.fromAnyRef("1 second")
    )
    .withValue(
      "session.advanced.reconnection-policy.max-delay",
      ConfigValueFactory.fromAnyRef("30 seconds")
    )
    .withValue(
      "session.advanced.heartbeat.interval",
      ConfigValueFactory.fromAnyRef("30 seconds")
    )
    .withValue(
      "session.advanced.heartbeat.timeout",
      ConfigValueFactory.fromAnyRef("5 seconds")
    )

  val ctx = new CassandraAsyncContext(SnakeCase, config)
}
