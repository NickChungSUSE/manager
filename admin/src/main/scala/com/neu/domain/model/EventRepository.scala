package com.neu.domain.model

import com.neu.domain.model.Event

import java.util.UUID
import scala.concurrent.Future

trait EventRepository {

  def findAll(): Future[List[Event]]

  def getById(id: UUID): Future[Event]

  def create(event: Event): Future[Unit]
}
