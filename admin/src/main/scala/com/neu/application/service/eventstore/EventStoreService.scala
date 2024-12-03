package com.neu.application.service.eventstore

import com.neu.domain.model.{ AggregateType, Event, EventType }
import com.neu.domain.service.EventService
import com.typesafe.scalalogging.LazyLogging
import io.opentelemetry.api.baggage.Baggage
import org.apache.pekko.http.scaladsl.model.{ HttpEntity, HttpRequest, HttpResponse }

import java.util.UUID
import scala.concurrent.Future

class EventStoreService(eventService: EventService) extends LazyLogging {

  def create(
    correlationId: String,
    aggregateId: UUID,
    aggregateType: String,
    request: HttpRequest
  ): Unit = {
    val currentBaggage        = Baggage.current()
    val existingCorrelationId = Option(currentBaggage.getEntryValue("correlation-id"))

    if (existingCorrelationId.isDefined && existingCorrelationId.get == correlationId) {
      logger.warn(s"Duplicate correlation id detected: $correlationId")
      throw new Exception(s"Duplicate correlation id: $correlationId")
    }

    val baggage = Baggage
      .current()
      .toBuilder
      .put("correlation-id", correlationId)
      .build()

    val scope = baggage.makeCurrent()

    try {
      // Get current baggage and check attributes
      val currentBaggage = Baggage.current()
      logger.info(s"Current baggage - event.id: ${currentBaggage.getEntryValue("event.id")}")
      logger.info(s"Current baggage - event.type: ${currentBaggage.getEntryValue("event.type")}")

      val event = Event(
        java.util.UUID.randomUUID(),
        EventType.STARTED,
        aggregateId,
        AggregateType.valueOf(aggregateType),
        System.currentTimeMillis(),
        request.entity.asInstanceOf[HttpEntity.Strict].data.utf8String,
        request.getHeaders
      )

      eventService.create(event)
    } finally scope.close()
  }

  def create(
    correlationId: String,
    aggregateId: UUID,
    aggregateType: String,
    response: HttpResponse
  ): Unit = {
    val currentBaggage        = Baggage.current()
    val existingCorrelationId = Option(currentBaggage.getEntryValue("correlation-id"))

    if (existingCorrelationId.isDefined && existingCorrelationId.get == correlationId) {
      logger.warn(s"Duplicate correlation id detected: $correlationId")
      throw new Exception(s"Duplicate correlation id: $correlationId")
    }

    val baggage = Baggage
      .current()
      .toBuilder
      .put("correlation-id", correlationId)
      .build()

    val scope = baggage.makeCurrent()

    try {
      // Get current baggage and check attributes
      val currentBaggage = Baggage.current()
      logger.info(s"Current baggage - event.id: ${currentBaggage.getEntryValue("event.id")}")
      logger.info(s"Current baggage - event.type: ${currentBaggage.getEntryValue("event.type")}")

      val eventType = if (response.status.intValue >= 400) EventType.FAILED else EventType.FINISHED

      val event = Event(
        java.util.UUID.randomUUID(),
        eventType,
        aggregateId,
        AggregateType.valueOf(aggregateType),
        System.currentTimeMillis(),
        response.entity.asInstanceOf[HttpEntity.Strict].data.utf8String,
        response.getHeaders
      )

      eventService.create(event)
    } finally scope.close()
  }

  private def extractBody(entity: HttpEntity.Strict): Future[String] =
    Future.successful(entity.data.utf8String)
}
