package com.neu.domain.model

import com.neu.domain.model.Event

import scala.concurrent.Future

trait EventRepository {
  def create(event: Event): Future[Unit]
}
