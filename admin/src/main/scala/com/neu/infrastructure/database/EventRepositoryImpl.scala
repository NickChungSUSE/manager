package com.neu.infrastructure.database

import com.neu.domain.model.{ AggregateType, Event, EventRepository, EventType }
import com.typesafe.scalalogging.LazyLogging
import io.getquill.*
import org.apache.pekko.http.scaladsl.model.{ HttpEntity, HttpHeader }

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

  override def create(event: Event): Future[Unit] = {
    val q = quote {
      query[Event].insertValue(lift(event))
    }
    logger.info(s"Attempting to save event: $event")
    ctx.run(q).map(_ => ())
  }
}
