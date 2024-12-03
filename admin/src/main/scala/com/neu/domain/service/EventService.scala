package com.neu.domain.service

import com.neu.domain.model.{ Event, EventRepository }

class EventService(resourceRepository: EventRepository) {

  def create(event: Event): Unit = resourceRepository.create(event)
}
