package com.neu.application.model

import com.neu.domain.model.{ AggregateType, EventType }

import java.util.UUID

case class EventDto(
  id: String,
  event_type: String,
  aggregate_id: String,
  aggregate_type: String,
  create_at: Long,
  data: String,
  metadata: String
)
