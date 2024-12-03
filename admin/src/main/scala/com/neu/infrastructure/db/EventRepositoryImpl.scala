package com.neu.infrastructure.db

import com.neu.domain.model.{ Event, EventRepository }
import com.typesafe.scalalogging.LazyLogging
import zio.{ Runtime, Unsafe, ZIO }
import io.getquill.*

import scala.concurrent.Future

class EventRepositoryImpl extends EventRepository with EventStoreConfig with LazyLogging {
  import ctx.*

  implicit val runtime: Runtime[Any] = Runtime.default

  override def create(event: Event): Future[Unit] = {
    val q = quote {
      query[Event].insertValue(lift(event))
    }
    logger.info(s"Attempting to save event: $event")
    Unsafe.unsafe { implicit u =>
      runtime.unsafe.runToFuture(ctx.run(q).provideLayer(session).unit)

    }
  }
}
