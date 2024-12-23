package com.neu.web

import com.neu.api.ApiServices
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
import com.neu.domain.model.EventRepository
import com.neu.domain.service.EventService
import com.neu.infrastructure.database.EventRepositoryImpl

import scala.concurrent.ExecutionContext.Implicits.global

object DependencyConfiguration {
  private val eventRepo: EventRepository = new EventRepositoryImpl()
  private val eventService: EventService = new EventService(eventRepo)

  given ApiServices = ApiServices(
    dashboardService = new DashboardService(),
    clusterService = new ClusterService(),
    deviceService = new DeviceService(),
    groupService = new GroupService(),
    notificationService = new NotificationService(),
    policyService = new PolicyService(),
    riskService = new RiskService(),
    sigstoreService = new SigstoreService(),
    workloadService = new WorkloadService(),
    eventStoreService = new EventStoreService(eventService)
  )
}
