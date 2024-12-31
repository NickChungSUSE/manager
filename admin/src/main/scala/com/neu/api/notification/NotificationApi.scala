package com.neu.api.notification

import com.neu.api.BaseApi
import com.neu.application.service.Utils
import com.neu.application.service.notification.NotificationService
import com.neu.infrastructure.client.RestClient.*
import com.neu.application.model.AlertJsonProtocol.given
import com.neu.application.model.EndpointConfigJsonProtocol.given
import com.neu.application.model.{EndpointConfigWrap, GlobalNotificationRequest, UserBlacklist, UserGraphLayout, ViolationBrief}
import com.neu.application.model.JsonProtocol.{*, given}
import org.apache.pekko.http.scaladsl.server.Route

/**
 * Notification rest service
 */
class NotificationApi(resourceService: NotificationService) extends BaseApi {

  private val top = "top"

  val route: Route =
    headerValueByName("Token") { tokenId =>
      {
        pathPrefix("ip-geo") {
          pathEnd {
            patch {
              entity(as[Array[String]]) { ipList =>
                Utils.respondWithWebServerHeaders() {
                  resourceService.getIpLocations(ipList)
                }
              }
            }
          }
        } ~
        pathPrefix("event") {
          pathEnd {
            get {
              Utils.respondWithWebServerHeaders() {
                resourceService.getEventLog(tokenId)
              }
            }
          }
        } ~
        pathPrefix("incident") {
          pathEnd {
            get {
              Utils.respondWithWebServerHeaders() {
                resourceService.getIncidentLog(tokenId)
              }
            }
          }
        } ~
        pathPrefix("violation") {
          pathEnd {
            get {
              Utils.respondWithWebServerHeaders() {
                resourceService.getViolationLog(tokenId)
              }
            }
          } ~
          path(top) {
            get {
              parameter(Symbol("category")) { category =>
                Utils.respondWithWebServerHeaders() {
                  resourceService.getViolationTopWorkloadLog(tokenId, category)
                }
              }
            }
          } ~
          path("track") {
            post {
              entity(as[ViolationBrief]) { violationBrief =>
                Utils.respondWithWebServerHeaders() {
                  resourceService.trackViolationLog(tokenId, violationBrief)
                }
              }
            }
          }
        } ~
        pathPrefix("audit") {
          pathEnd {
            get {
              Utils.respondWithWebServerHeaders() {
                resourceService.getAuditLog(tokenId)
              }
            }
          }
        } ~
        pathPrefix("audit2") {
          pathEnd {
            get {
              parameter(Symbol("start").?, Symbol("limit").?) { (start, limit) =>
                Utils.respondWithWebServerHeaders() {
                  resourceService.getAudit2Log(tokenId, start, limit)
                }
              }
            }
          }
        } ~
        pathPrefix("threat") {
          pathEnd {
            get {
              parameter(Symbol("id").?) { id =>
                Utils.respondWithWebServerHeaders() {
                  resourceService.getThreatLog(tokenId, id)
                }
              }
            }
          } ~
          path("track") {
            post {
              entity(as[ViolationBrief]) { violationBrief =>
                Utils.respondWithWebServerHeaders() {
                  resourceService.trackThreatLog(tokenId, violationBrief)
                }
              }
            }
          } ~
          path(top) {
            get {
              Utils.respondWithWebServerHeaders() {
                resourceService.getThreatTopLog(tokenId)
              }
            }
          }
        } ~
        pathPrefix("network") {
          path("session") {
            get {
              parameter(Symbol("id")) { id =>
                Utils.respondWithWebServerHeaders() {
                  resourceService.getNetworkSessionById(tokenId, id)
                }
              }
            }
          } ~
          path("conversation") {
            delete {
              parameter(Symbol("from"), Symbol("to")) { (from, to) =>
                Utils.respondWithWebServerHeaders() {
                  resourceService.deleteNetworkConversation(tokenId, from, to)
                }
              }
            }
          } ~
          path("endpoint") {
            patch {
              entity(as[EndpointConfigWrap]) { config =>
                Utils.respondWithWebServerHeaders() {
                  resourceService.updateNetworkConversationEndpoint(tokenId, config)
                }
              }
            } ~
            delete {
              parameter(Symbol("id")) { id =>
                Utils.respondWithWebServerHeaders() {
                  resourceService.deleteNetworkConversationEndpoint(tokenId, id)
                }
              }
            }
          } ~
          path("history") {
            get {
              parameter(Symbol("from"), Symbol("to")) { (from, to) =>
                Utils.respondWithWebServerHeaders() {
                  resourceService.getNetworkConversationHistory(tokenId, from, to)
                }
              }
            }
          } ~
          pathPrefix("graph") {
            pathEnd {
              get {
                parameter(Symbol("user")) { user =>
                  Utils.respondWithWebServerHeaders() {
                    resourceService.getNetworkGraph(tokenId, user)
                  }
                }
              } ~
              post {
                entity(as[UserGraphLayout]) { (graphLayout: UserGraphLayout) =>
                  Utils.respondWithWebServerHeaders() {
                    resourceService.createNetworkGraph(graphLayout)
                  }
                }
              }
            } ~
            path("layout") {
              get {
                parameter(Symbol("user")) { user =>
                  Utils.respondWithWebServerHeaders() {
                    resourceService.getNetworkGraphLayout(user)
                  }
                }
              }
            } ~
            path("blacklist") {
              get {
                parameter(Symbol("user")) { user =>
                  Utils.respondWithWebServerHeaders() {
                    resourceService.getNetworkGraphBlacklist(user)
                  }
                }
              } ~
              post {
                entity(as[UserBlacklist]) { (userBlacklist: UserBlacklist) =>
                  Utils.respondWithWebServerHeaders() {
                    resourceService.createNetworkGraphBlacklist(userBlacklist)
                  }
                }
              }
            }
          }
        } ~
        pathPrefix("security-events") {
          pathEnd {
            get {
              Utils.respondWithWebServerHeaders() {
                resourceService.getSecurityEvents(tokenId)
              }
            }
          }
        } ~
        pathPrefix("security-events2") {
          pathEnd {
            get {
              Utils.respondWithWebServerHeaders() {
                resourceService.getSecurityEvents2(tokenId)
              }
            }
          }
        } ~
        pathPrefix("notification") {
          path("accept") {
            post {
              entity(as[GlobalNotificationRequest]) { notificationRequest =>
                Utils.respondWithWebServerHeaders() {
                  resourceService.acceptGlobalNotification(tokenId, notificationRequest)
                }
              }
            }
          }
        }
      }
    }
}
