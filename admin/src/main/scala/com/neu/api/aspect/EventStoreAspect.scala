package com.neu.api.aspect

import com.neu.application.service.eventstore.EventStoreService
import com.neu.domain.model.{ Event, EventRepository }
import com.neu.domain.service.EventService
import com.neu.infrastructure.db.EventRepositoryImpl
import com.typesafe.scalalogging.LazyLogging
import org.apache.pekko.http.scaladsl.server.{ Directives, Route }

import scala.language.postfixOps

trait EventStoreAspect extends Directives with LazyLogging {

  def withEventStore(route: Route, eventTypePrefix: String): Route =
    extractRequest { request =>
      logger.info(s"${eventTypePrefix}Started request start event store...")

      lazy val eventRepository: EventRepository     = new EventRepositoryImpl()
      lazy val eventService: EventService           = new EventService(eventRepository)
      lazy val eventStoreService: EventStoreService = new EventStoreService(eventService)

      val startEvent = Event(
        java.util.UUID.randomUUID().toString,
        s"${eventTypePrefix}Started",
        System.currentTimeMillis()
      )

      val correlationId = request.getHeader("X-Correlation-Id")

      if (correlationId.isEmpty) {
        throw new Exception(
          "Event sourcing request needs a correlation id for X-Correlation-Id header..."
        )
      }

      eventStoreService.create(correlationId.get().toString, startEvent)

      mapResponse { response =>
        logger.info(
          s"${eventTypePrefix}Finished Request finished event store with status ${response.status.value}..."
        )

        logger.info(
          s"${response.status.intValue}..."
        )

        if (response.status.intValue >= 400) {
          val failureEvent = Event(
            java.util.UUID.randomUUID().toString,
            s"${eventTypePrefix}Failed",
            System.currentTimeMillis()
          )

          eventStoreService.create(correlationId.get().toString, failureEvent)
        } else {
          val finishEvent = Event(
            java.util.UUID.randomUUID().toString,
            s"${eventTypePrefix}Finished",
            System.currentTimeMillis()
          )

          eventStoreService.create(correlationId.get().toString, finishEvent)
        }

        response
      }(route)
    }
}
