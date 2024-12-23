package com.neu.api

import com.neu.api.authentication.AuthenticationApi
import com.neu.api.cluster.ClusterApi
import com.neu.api.dashboard.DashboardApi
import com.neu.api.device.DeviceApi
import com.neu.api.event.EventApi
import com.neu.api.group.GroupApi
import com.neu.api.notification.NotificationApi
import com.neu.api.policy.PolicyApi
import com.neu.api.risk.RiskApi
import com.neu.api.sigstore.SigstoreApi
import com.neu.api.workload.WorkloadApi
import com.neu.application.core.{ Core, CoreActors, HttpResponseException }
import com.neu.application.service.authentication.{
  AuthProvider,
  AuthService,
  AuthServiceFactory,
  ExtraAuthService
}
import com.neu.application.service.cluster.ClusterService
import com.neu.application.service.dashboard.DashboardService
import com.neu.application.service.device.DeviceService
import com.neu.application.service.eventstore.EventStoreService
import com.neu.application.service.group.GroupService
import com.neu.application.service.notification.NotificationService
import com.neu.application.service.policy.PolicyService
import com.neu.application.service.risk.RiskService
import com.neu.application.service.sigstore.SigstoreService
import com.neu.application.service.workload.WorkloadService
import com.neu.infrastructure.client.RestClient.handleError
import org.apache.pekko.http.scaladsl.model.{ ContentTypes, HttpEntity, HttpResponse }
import org.apache.pekko.http.scaladsl.server.{ Directives, ExceptionHandler, Route }
import org.apache.pekko.http.scaladsl.server.Directives.*
import org.apache.pekko.http.scaladsl.server.RouteConcatenation.*

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * The REST API layer. It exposes the REST services, but does not provide any web server
 * interface.<br/> Notice that it requires to be mixed in with ``core.CoreActors``, which provides
 * access to the top-level actors that make up the system.
 */
case class ApiServices(
  dashboardService: DashboardService,
  clusterService: ClusterService,
  deviceService: DeviceService,
  groupService: GroupService,
  notificationService: NotificationService,
  policyService: PolicyService,
  riskService: RiskService,
  sigstoreService: SigstoreService,
  workloadService: WorkloadService,
  eventStoreService: EventStoreService
)

// Authentication services container
case class AuthServices(
  openIdAuth: AuthService,
  samlAuth: AuthService,
  suseAuth: AuthService,
  extraAuth: ExtraAuthService
)

// Api builder to construct routes
class ApiBuilder(apiServices: ApiServices, authServices: AuthServices) {
  private def createApis = {
    val authApi = new AuthenticationApi(
      authServices.openIdAuth,
      authServices.samlAuth,
      authServices.suseAuth,
      authServices.extraAuth
    )

    val apis = List(
      new DashboardApi(apiServices.dashboardService),
      new ClusterApi(apiServices.clusterService),
      new DeviceApi(apiServices.deviceService),
      new GroupApi(apiServices.groupService),
      new NotificationApi(apiServices.notificationService),
      new PolicyApi(apiServices.policyService),
      new RiskApi(apiServices.riskService),
      new SigstoreApi(apiServices.sigstoreService),
      new WorkloadApi(apiServices.workloadService),
      new EventApi(apiServices.eventStoreService)
    )

    (authApi, apis)
  }

  def build: Route = {
    val (authApi, apis) = createApis
    apis.foldLeft(authApi.route)((acc, api) => acc ~ api.route)
  }
}

trait Api extends Directives with CoreActors with Core {
  private val timeOutStatus              = "Status: 408"
  private val authenticationFailedStatus = "Status: 401"
  private val serverErrorStatus          = "Status: 503"

  implicit def exceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case e: HttpResponseException =>
        complete(
          HttpResponse(
            status = e.statusCode,
            entity = e.response.entity
          )
        )
      case e: Exception             =>
        val (status, message) =
          handleError(timeOutStatus, authenticationFailedStatus, serverErrorStatus, e)
        complete(
          HttpResponse(
            status = status,
            entity = HttpEntity(ContentTypes.`application/json`, message)
          )
        )
    }

  private val authServiceFactory = new AuthServiceFactory()
  private val authServices       = AuthServices(
    openIdAuth = authServiceFactory.createService(AuthProvider.OPEN_ID),
    samlAuth = authServiceFactory.createService(AuthProvider.SAML),
    suseAuth = authServiceFactory.createService(AuthProvider.SUSE),
    extraAuth = authServiceFactory.createExtraAuthService()
  )

  def routes(using apiServices: ApiServices): Route = {
    val builder = new ApiBuilder(apiServices, authServices)
    handleExceptions(exceptionHandler) {
      builder.build
    }
  }
}
