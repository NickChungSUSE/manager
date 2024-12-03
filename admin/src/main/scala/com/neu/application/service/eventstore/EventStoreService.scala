package com.neu.application.service.eventstore

import com.neu.domain.model.Event
import com.neu.domain.service.EventService
import com.typesafe.scalalogging.LazyLogging
import io.opentelemetry.api.baggage.Baggage

class EventStoreService(eventService: EventService) extends LazyLogging {

  def create(correlationId: String, event: Event): Unit = {
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
      .put("event.id", event.id)
      .put("event.type", event.event_type)
      .build()

    val scope = baggage.makeCurrent()

    try {
      // Get current baggage and check attributes
      val currentBaggage = Baggage.current()
      logger.info(s"Current baggage - event.id: ${currentBaggage.getEntryValue("event.id")}")
      logger.info(s"Current baggage - event.type: ${currentBaggage.getEntryValue("event.type")}")

      eventService.create(event)
    } finally scope.close()
  }
}
