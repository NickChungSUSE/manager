package com.neu.application.service.eventstore

import com.neu.application.model.EventDto
import com.neu.application.service.DefaultJsonFormats
import org.apache.pekko.http.scaladsl.model.ContentTypes
import com.neu.application.model.EventDtoJsonProtocol.*
import com.neu.domain.model.{ AggregateType, Event, EventType }
import com.neu.domain.service.EventService
import com.typesafe.scalalogging.LazyLogging
import io.opentelemetry.api.baggage.Baggage
import org.apache.pekko.http.scaladsl.model.{ HttpEntity, HttpRequest, HttpResponse, StatusCodes }
import org.apache.pekko.http.scaladsl.server.{ Directives, Route }
import spray.json.*

import java.util.UUID
import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success }
import scala.concurrent.ExecutionContext.Implicits.global

class EventStoreService(eventService: EventService)
    extends Directives
    with DefaultJsonFormats
    with LazyLogging {

  private val map: Event => EventDto = e =>
    EventDto(
      id = e.id.toString,
      event_type = e.event_type.toString,
      aggregate_id = e.aggregate_id.toString,
      aggregate_type = e.aggregate_type.toString,
      create_at = e.create_at,
      data = e.data.toString,
      metadata = e.metadata.toString
    )

  def findEvents(): Route =
    onComplete(eventService.findEvents().map(_.map(map))) {
      case Success(dtos) =>
        val jsonResponse = JsArray(dtos.map(_.toJson).toVector)
        complete(HttpEntity(ContentTypes.`application/json`, jsonResponse.compactPrint))
      case Failure(ex)   =>
        complete(StatusCodes.InternalServerError -> ex.getMessage)
    }

  def getEventById(id: String): Route =
    onComplete(eventService.getEventById(UUID.fromString(id)).map(map)) {
      case Success(dto) =>
        complete(HttpEntity(ContentTypes.`application/json`, dto.toJson.compactPrint))
      case Failure(ex)  =>
        complete(StatusCodes.InternalServerError -> ex.getMessage)
    }

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
}
