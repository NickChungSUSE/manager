package com.neu.application.model

import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.*

object EventDtoJsonProtocol extends DefaultJsonProtocol {
  implicit val eventDtoFormat: RootJsonFormat[EventDto] = jsonFormat7(EventDto.apply)
}
