import { Injectable } from '@angular/core';
import { PathConstant } from '@common/constants/path.constant';
import { InternalSystemInfo, Metrics, RbacStatus, Score } from '@common/types';
import { GlobalVariable } from '@common/variables/global.variable';
import { Observable } from 'rxjs';

@Injectable()
export class DashboardHttpService {
  patchScores(
    metrics: Metrics,
    isGlobalUser: boolean,
    totalRunningPods: number
  ): Observable<Score> {
    return GlobalVariable.http.patch<Score>(
      PathConstant.DASHBOARD_SCORES_URL,
      metrics,
      { params: { isGlobalUser, totalRunningPods } }
    );
  }

  getScores(
    isGlobalUser: boolean,
    domain: any
  ): Observable<InternalSystemInfo> {
    return GlobalVariable.http.get<InternalSystemInfo>(
      PathConstant.DASHBOARD_SCORES_URL,
      {
        params: {
          isGlobalUser,
          domain,
        },
      }
    );
  }

  getSystemRBAC() {
    return GlobalVariable.http.get<RbacStatus>(PathConstant.SYSTEM_RBAC_URL);
  }

  getDashboardSecurityEventData(domain: string) {
    return GlobalVariable.http.get(PathConstant.DASHBOARD_NOTIFICATIONS_URL, {params: {domain: domain}});
  }

  getDashboardDetailsData(domain: string) {
    return GlobalVariable.http.get(PathConstant.DASHBOARD_DETAILS_URL, {params: {domain: domain}});
  }

  getSummaryData(domain: string) {
    return GlobalVariable.http.get(PathConstant.DASHBOARD_SUMMARY_URL, {params: {domain: domain}});
  }
}
