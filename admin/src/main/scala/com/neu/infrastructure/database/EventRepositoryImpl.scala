package com.neu.infrastructure.database

import com.neu.domain.model.EventRepository

import com.typesafe.scalalogging.LazyLogging
import io.getquill.*
import org.apache.pekko.http.scaladsl.model.{ HttpEntity, HttpHeader }

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class EventRepositoryImpl extends EventRepository with EventStoreConfig with LazyLogging {
  import ctx.*

  // Encoder for HttpEntity
  implicit val httpEntityEncoder: MappedEncoding[HttpEntity.Strict, String] =
    MappedEncoding[HttpEntity.Strict, String] { entity =>
      entity.data.utf8String
    }

  // Encoder for Headers
  implicit val headersEncoder: MappedEncoding[Seq[HttpHeader], String] =
    MappedEncoding[Seq[HttpHeader], String] { headers =>
      headers.map(h => s"${h.name()}: ${h.value()}").mkString("\n")
    }

  // Encoder/Decoder for EventType enum
  implicit val eventTypeEncoder: MappedEncoding[EventType, String] =
    MappedEncoding[EventType, String](_.toString)

  implicit val eventTypeDecoder: MappedEncoding[String, EventType] =
    MappedEncoding[String, EventType](EventType.valueOf)

  // Encoder/Decoder for AggregateType enum
  implicit val aggregateTypeEncoder: MappedEncoding[AggregateType, String] =
    MappedEncoding[AggregateType, String](_.toString)

  implicit val aggregateTypeDecoder: MappedEncoding[String, AggregateType] =
    MappedEncoding[String, AggregateType](AggregateType.valueOf)

  // Encoders for Object type (using JSON serialization)
  implicit val objectEncoder: MappedEncoding[Object, String] =
    MappedEncoding[Object, String](_.toString)

  implicit val objectDecoder: MappedEncoding[String, Object] =
    MappedEncoding[String, Object](s => s)

  implicit val eventEntityMeta: Quoted[EntityQuery[Event]] = quote {
    querySchema[Event]("event")
  }

  private val mapToEntity: com.neu.domain.model.Event => Event = e =>
    Event(
      id = e.id,
      event_type = EventType.valueOf(e.event_type.toString),
      aggregate_id = e.aggregate_id,
      aggregate_type = AggregateType.valueOf(e.aggregate_type.toString),
      create_at = e.create_at,
      data = e.data,
      metadata = e.metadata
    )

  private val mapToDomainObject: Event => com.neu.domain.model.Event = e =>
    com.neu.domain.model.Event(
      id = e.id,
      event_type = com.neu.domain.model.EventType.valueOf(e.event_type.toString),
      aggregate_id = e.aggregate_id,
      aggregate_type = com.neu.domain.model.AggregateType.valueOf(e.aggregate_type.toString),
      create_at = e.create_at,
      data = e.data,
      metadata = e.metadata
    )

  override def findAll(): Future[List[com.neu.domain.model.Event]] = {
    val q = quote {
      query[Event]
    }
    ctx.run(q).map(_.map(mapToDomainObject))
  }

  override def getById(id: UUID): Future[com.neu.domain.model.Event] = {
    val q = quote {
      query[Event].filter(e => e.id == lift(id))
    }
    ctx
      .run(q)
      .map(
        _.headOption
          .map(mapToDomainObject)
          .getOrElse(throw new NoSuchElementException(s"Event with id $id not found"))
      )
  }

  override def create(event: com.neu.domain.model.Event): Future[Unit] = {
    val q = quote {
      query[Event].insertValue(lift(mapToEntity(event)))
    }
    logger.info(s"Attempting to save event: $event")
    ctx.run(q).map(_ => ())
  }

}
