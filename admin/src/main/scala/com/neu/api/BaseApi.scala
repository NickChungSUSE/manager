package com.neu.api

import com.neu.application.service.DefaultJsonFormats
import org.apache.pekko.http.scaladsl.server.Directives

trait BaseApi extends Directives with DefaultJsonFormats {}
