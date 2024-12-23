package com.neu.api

import com.neu.application.service.DefaultJsonFormats
import org.apache.pekko.http.scaladsl.server.{ Directives, Route }

trait BaseApi extends Directives with DefaultJsonFormats {
  def route: Route
}
