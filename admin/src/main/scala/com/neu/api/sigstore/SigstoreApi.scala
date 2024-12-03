package com.neu.api.sigstore

import com.neu.api.BaseApi
import com.neu.application.model.{ RootOfTrust, Verifier }
import com.neu.application.service.Utils
import com.neu.application.service.sigstore.SigstoreService
import com.neu.infrastructure.client.RestClient.*
import com.neu.application.model.SigstoreJsonProtocol.given
import org.apache.pekko.http.scaladsl.server.Route

class SigstoreApi(resourceService: SigstoreService) extends BaseApi {

  val route: Route =
    headerValueByName("Token") { tokenId =>
      pathPrefix("sigstore") {
        pathEnd {
          get {
            Utils.respondWithWebServerHeaders() {
              resourceService.getSigstoreList(tokenId)
            }
          } ~
          post {
            entity(as[RootOfTrust]) { rootOfTrust =>
              Utils.respondWithWebServerHeaders() {
                resourceService.createSigstore(tokenId, rootOfTrust)
              }
            }
          } ~
          patch {
            entity(as[RootOfTrust]) { rootOfTrust =>
              Utils.respondWithWebServerHeaders() {
                resourceService.updateSigstore(tokenId, rootOfTrust)
              }
            }
          } ~
          delete {
            parameters(Symbol("rootOfTrustName")) { rootOfTrustName =>
              Utils.respondWithWebServerHeaders() {
                resourceService.removeSigstore(tokenId, rootOfTrustName)
              }
            }
          }
        }
      } ~
      path("verifier") {
        get {
          parameters(Symbol("rootOfTrustName")) { rootOfTrustName =>
            Utils.respondWithWebServerHeaders() {
              resourceService.getVerifiers(tokenId, rootOfTrustName)
            }
          }
        } ~
        post {
          entity(as[Verifier]) { verifier =>
            Utils.respondWithWebServerHeaders() {
              resourceService.createVerifier(tokenId, verifier)
            }
          }
        } ~
        patch {
          entity(as[Verifier]) { verifier =>
            Utils.respondWithWebServerHeaders() {
              resourceService.updateVerifier(tokenId, verifier)
            }
          }
        } ~
        delete {
          parameters(Symbol("rootOfTrustName"), Symbol("verifierName")) {
            (rootOfTrustName, verifierName) =>
              Utils.respondWithWebServerHeaders() {
                resourceService.removeVerifier(tokenId, verifierName, rootOfTrustName)
              }
          }
        }
      }
    }
}
