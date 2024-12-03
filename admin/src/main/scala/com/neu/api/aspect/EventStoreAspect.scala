package com.neu.api.aspect

import com.neu.domain.model.{ Event, EventRepository }
import com.neu.domain.service.EventService
import com.neu.infrastructure.db.{ EventRepositoryImpl, EventStoreConfig }
import com.typesafe.scalalogging.LazyLogging
import org.apache.pekko.http.scaladsl.server.{ Directives, Route }

trait EventStoreAspect extends Directives with LazyLogging {

  def withEventStore(route: Route, eventTypePrefix: String): Route =
    extractRequest { request =>
      logger.info(s"${eventTypePrefix}Started request start event store...")

      lazy val eventRepository: EventRepository = new EventRepositoryImpl()
      lazy val eventService: EventService       = new EventService(eventRepository)

      val startEvent = Event(
        java.util.UUID.randomUUID().toString,
        s"${eventTypePrefix}Started",
        System.currentTimeMillis()
      )

      eventService.create(startEvent)

      mapResponse { response =>
        logger.info(
          s"${eventTypePrefix}Finished Request finished event store with status ${response.status.value}..."
        )

        val finishEvent = Event(
          java.util.UUID.randomUUID().toString,
          s"${eventTypePrefix}Finished",
          System.currentTimeMillis()
        )

        eventService.create(finishEvent)

        response
      }(route)
    }
}
