package com.neu.domain.model

import java.util.UUID

case class Event(
  id: String,
  event_type: String,
  timestamp: Long
)
