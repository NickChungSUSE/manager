package com.neu.domain.model

import java.util.UUID

enum EventType:
  case STARTED, FINISHED, FAILED

enum AggregateType:
  case AUTHENTICATION, CLUSTER, DASHBOARD, DEVICE, GROUP, NOTIFICATION, POLICY, RISK, SIGSTORE,
    WORKLOAD

case class Event(
  id: UUID,
  event_type: EventType,
  aggregate_id: UUID,
  aggregate_type: AggregateType,
  create_at: Long,
  data: Object,
  metadata: Object
)