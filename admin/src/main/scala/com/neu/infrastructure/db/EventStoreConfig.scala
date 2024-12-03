package com.neu.infrastructure.db

import com.neu.application.core.CommonSettings.*
import com.typesafe.config.{ Config, ConfigFactory, ConfigValueFactory }
import io.getquill.{ CassandraZioContext, CassandraZioSession, SnakeCase }
import zio.ZLayer

import scala.jdk.CollectionConverters.*

trait EventStoreConfig {
  private object SessionHolder {
    private lazy val sharedConfig = ConfigFactory
      .empty()
      .withValue("cassandra.keyspace", ConfigValueFactory.fromAnyRef(cassandraKeyspace))
      .withValue(
        "cassandra.session.basic.contact-points",
        ConfigValueFactory.fromIterable(List(cassandraHosts).asJava)
      )
      .withValue(
        "cassandra.session.basic.request.consistency",
        ConfigValueFactory.fromAnyRef("LOCAL_QUORUM")
      )
      .withValue(
        "cassandra.session.basic.request.page-size",
        ConfigValueFactory.fromAnyRef(pageSize)
      )
      .withValue(
        "cassandra.session.basic.request.timeout",
        ConfigValueFactory.fromAnyRef("5 seconds")
      )
      .withValue(
        "cassandra.session.basic.request.serial-consistency",
        ConfigValueFactory.fromAnyRef("LOCAL_SERIAL")
      )
      .withValue(
        "cassandra.session.basic.config-reload-interval",
        ConfigValueFactory.fromAnyRef("5 minutes")
      )
      .withValue(
        "cassandra.session.advanced.auth-provider.username",
        ConfigValueFactory.fromAnyRef(cassandraUser)
      )
      .withValue(
        "cassandra.session.advanced.auth-provider.password",
        ConfigValueFactory.fromAnyRef(cassandraPassword)
      )
      .withValue(
        "cassandra.session.basic.pool.local.size",
        ConfigValueFactory.fromAnyRef(32)
      )
      .withValue(
        "cassandra.session.basic.pool.remote.size",
        ConfigValueFactory.fromAnyRef(8)
      )
      .withValue(
        "cassandra.session.advanced.reconnection-policy.base-delay",
        ConfigValueFactory.fromAnyRef("1 second")
      )
      .withValue(
        "cassandra.session.advanced.reconnection-policy.max-delay",
        ConfigValueFactory.fromAnyRef("30 seconds")
      )
      .withValue(
        "cassandra.session.advanced.heartbeat.interval",
        ConfigValueFactory.fromAnyRef("30 seconds")
      )
      .withValue(
        "cassandra.session.advanced.heartbeat.timeout",
        ConfigValueFactory.fromAnyRef("5 seconds")
      )

    lazy val sharedSession: ZLayer[Any, Throwable, CassandraZioSession] =
      CassandraZioSession.fromConfig(sharedConfig.getConfig("cassandra"))
  }

  val session: ZLayer[Any, Throwable, CassandraZioSession] = SessionHolder.sharedSession
  val ctx                                                  = new CassandraZioContext(SnakeCase)

  def make: ZLayer[Any, Throwable, CassandraZioSession] =
    SessionHolder.sharedSession.tapError { error =>
      zio.Console.printLineError(s"Failed to create Cassandra session: ${error.getMessage}")
    }

}
