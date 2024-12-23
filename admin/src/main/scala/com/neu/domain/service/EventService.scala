package com.neu.domain.service

import com.neu.domain.model.{ Event, EventRepository }

import java.util.UUID
import scala.concurrent.Future

class EventService(resourceRepository: EventRepository) {

  def findEvents(): Future[List[Event]] = resourceRepository.findAll()

  def getEventById(id: UUID): Future[Event] = resourceRepository.getById(id)

  def create(event: Event): Unit = resourceRepository.create(event)
}
