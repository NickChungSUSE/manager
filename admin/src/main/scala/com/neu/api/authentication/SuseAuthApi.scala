package com.neu.api.authentication

import com.neu.api.*
import com.neu.api.aspect.EventStoreAspect
import com.neu.application.service.Utils
import com.neu.application.service.authentication.AuthService
import org.apache.pekko.http.scaladsl.server.Route

//noinspection UnstableApiUsage
class SuseAuthApi(
  authService: AuthService
) extends BaseApi
    with EventStoreAspect {

  private val auth       = "auth"
  private val suseCookie = "R_SESS"

  val route: Route =
    (post & path(auth)) {
      withEventStore(
        extractClientIP { ip =>
          extractRequestContext { ctx =>
            Utils.respondWithWebServerHeaders() {
              authService.login(ip, "", ctx)
            }
          }
        },
        "AUTHENTICATION",
        "Login"
      )
    } ~
    headerValueByName("Token") { tokenId =>
      pathPrefix(auth) {
        delete {
          Utils.respondWithWebServerHeaders() {
            authService.logout(None, tokenId)
          }
        }
      } ~
      pathPrefix("heartbeat") {
        patch {
          Utils.respondWithWebServerHeaders() {
            authService.validateToken(Some(tokenId), None)
          }
        }
      } ~
      pathPrefix("self") {
        get {
          parameter(Symbol("isOnNV").?, Symbol("isRancherSSOUrl").?) { (isOnNV, isRancherSSOUrl) =>
            Utils.respondWithWebServerHeaders() {
              optionalCookie(suseCookie) {
                case Some(sCookie) =>
                  authService.getSelf(
                    isOnNV,
                    isRancherSSOUrl,
                    sCookie.value,
                    tokenId
                  )
                case None          =>
                  authService.getSelf(isOnNV, isRancherSSOUrl, "", tokenId)
              }
            }
          }
        }
      }
    }
}
