package com.neu.api.aspect

import com.neu.application.service.eventstore.EventStoreService
import com.neu.domain.model.{ Event, EventRepository }
import com.neu.domain.service.EventService
import com.neu.infrastructure.database.EventRepositoryImpl
import com.typesafe.scalalogging.LazyLogging
import org.apache.pekko.http.scaladsl.server.{ Directives, Route }

import scala.language.postfixOps
import java.util.UUID

trait EventStoreAspect extends Directives with LazyLogging {

  def withEventStore(route: Route, aggregateType: String, eventTypePrefix: String): Route =
    extractRequest { request =>
      logger.info(s"${eventTypePrefix}Started request start event store...")

      lazy val eventRepository: EventRepository     = new EventRepositoryImpl()
      lazy val eventService: EventService           = new EventService(eventRepository)
      lazy val eventStoreService: EventStoreService = new EventStoreService(eventService)

      val correlationId = request.getHeader("X-Correlation-Id")

      if (correlationId.isEmpty) {
        throw new Exception(
          "Event sourcing request needs a correlation id for X-Correlation-Id header..."
        )
      }

      val aggregateId = UUID.randomUUID()

      eventStoreService.create(correlationId.get().toString, aggregateId, aggregateType, request)

      mapResponse { response =>
        logger.info(
          s"${eventTypePrefix}Finished Request finished event store with status ${response.status.value}..."
        )

        logger.info(
          s"${response.status.intValue}..."
        )

        eventStoreService.create(correlationId.get().toString, aggregateId, aggregateType, response)

        response
      }(route)
    }
}
