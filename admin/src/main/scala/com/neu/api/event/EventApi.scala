package com.neu.api.event

import com.neu.api.BaseApi
import com.neu.application.service.eventstore.EventStoreService
import org.apache.pekko.http.scaladsl.server.Route

class EventApi(resourceService: EventStoreService) extends BaseApi {

  val route: Route = pathPrefix("events") {
    get {
      parameter(Symbol("id").?) {
        case Some(id) => resourceService.getEventById(id)
        case None     => resourceService.findEvents()
      }
    }
  }
}
