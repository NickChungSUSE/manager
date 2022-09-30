export class PathConstant {
  // public static TOKEN_AUTH = 'assets/mockdata/token_auth_server.json';
  public static VERSION_URL = 'app/metadata/js-version.json';

  // Load from remote server, replace with remote API once server is ready
  public static KEEP_ALIVE_URL = 'heartbeat'; //auth
  public static CONFIG_URL = 'config'; //config
  public static CONFIG_V2_URL = 'config-v2'; //config
  public static CONFIG_IMPORT_URL = 'file/config'; //config
  public static DASHBOARD_SUMMARY_URL = 'summary'; //common
  public static NETWORK_INFO_URL = 'network/graph'; //graph
  public static NETWORK_LAYOUT_URL = 'network/graph/layout'; //graph
  public static NETWORK_BLACKLIST_URL = '/network/graph/blacklist'; //graph
  public static POLICY_GRAPH_URL = 'policy/graph'; //graph
  public static NODES_URL = 'host'; //assets
  public static NODE_WORKLOADS_URL = 'host/workload'; //assets
  public static ENFORCER_URL = 'enforcer'; //assets
  public static CONTROLLER_URL = 'controller'; //assets
  public static SCANNER_URL = 'scanner'; //assets
  public static PLAIN_CONTAINER_URL = 'container'; //assets
  public static CONTAINER_URL = 'workload'; //assets
  public static SCANNED_CONTAINER_URL = 'workload/scanned'; //assets
  public static CONTAINER_COMPLIANCE_URL = 'workload/compliance'; //risks
  public static NODE_COMPLIANCE_URL = 'host/compliance'; //risks
  public static CONTAINER_BY_ID = 'workload/workload-by-id'; //assets
  public static CONTAINER_PROCESS_URL = 'container/process'; //assets
  public static CONTAINER_PROCESS_HISTORY_URL = 'container/processHistory'; //assets
  public static EVENT_URL = 'event'; //events
  public static USERS_URL = 'user'; //auth
  public static LICENSE_URL = 'license'; //auth
  public static LDAP_URL = 'server'; //auth
  public static LOGIN_URL = 'auth'; //auth
  public static EULA_URL = 'eula'; //auth
  public static TOKEN_AUTH = 'token_auth_server'; //auth
  public static OIDC_AUTH = 'openId_auth'; //auth
  public static LICENSE_LOAD_URL = 'license/update'; //auth
  public static GROUP_URL = 'group'; //poilcy
  public static SERVICE_URL = 'service'; //poilcy
  public static PROCESS_PROFILE_URL = 'processProfile'; //poilcy
  public static FILE_PROFILE_URL = 'fileProfile'; //poilcy
  public static FILE_PREDEFINED_PROFILE_URL = 'filePreProfile'; //poilcy
  public static POLICY_URL = 'policy'; //poilcy
  public static POLICY_APP_URL = 'policy/application'; //poilcy
  public static POLICY_RULE_URL = 'policy/rule'; //poilcy
  public static RISK_CVE_URL = 'risk/cve'; //risks
  public static RISK_COMPLIANCE_URL = 'risk/compliance'; //risks
  public static COMPLIANCE_PROFILE_URL = 'risk/compliance/profile'; //risks
  public static COMPLIANCE_TEMPLATE_URL = 'risk/compliance/template'; //risks
  public static SCAN_URL = 'scan/workload'; //assets
  public static SCAN_HOST_URL = 'scan/host'; //assets
  public static SCAN_PLATFORM_URL = 'scan/platform'; //assets
  public static DOMAIN_URL = 'domain'; //assets
  public static SCAN_CONFIG_URL = 'scan/config'; //assets
  public static SCAN_CONTAINER_URL = 'scan/workload'; //assets
  public static REGISTRY_SCAN_URL = 'scan/registry'; //assets
  public static REGISTRY_SCAN_REPO_URL = 'scan/registry/repo'; //assets
  public static REGISTRY_SCAN_IMAGE_URL = 'scan/registry/image'; //assets
  public static REGISTRY_TYPE_URL = 'scan/registry/type'; //assets
  public static REGISTRY_TEST = 'scan/registry/test'; //assets
  public static SESSION_URL = 'network/session'; //graph
  public static CONVERSATION_HISTORY_URL = 'network/history'; //graph
  public static CONVERSATION_SNAPSHOT_URL = 'network/conversation'; //graph
  public static CONVERSATION_ENDPOINT_URL = 'network/endpoint'; //graph
  public static SYSTEM_CONFIG_URL = 'file/config'; //config
  public static SYSTEM_DEBUG_URL = 'file/debug'; //config
  public static DEBUG_CHECK_URL = 'file/debug/check'; //config
  public static SNIFF_URL = 'sniffer'; //graph
  public static SNIFF_PCAP_URL = 'sniffer/pcap'; //graph
  public static RESPONSE_POLICY_URL = 'responsePolicy'; //policy
  public static UNQUARANTINE_URL = 'unquarantine'; //policy
  public static CONDITION_OPTION_URL = 'conditionOption'; //policy
  public static RESPONSE_RULE_URL = 'responseRule'; //policy
  public static GROUP_LIST_URL = 'group-list'; //policy
  public static AUDIT_URL = 'audit'; //events
  public static DASHBOARD_SCORES_URL = 'dashboard/scores2'; //dashboard
  public static DASHBOARD_DETAILS_URL = 'dashboard/details'; //dashboard
  public static DASHBOARD_NOTIFICATIONS_URL = 'dashboard/notifications2'; //dashboard
  public static SECURITY_EVENTS_URL_2 = 'security-events2'; //events
  public static ADMISSION_URL = 'admission/rules'; //policy
  public static ADMISSION_SINGLE_URL = 'admission/rule'; //policy
  public static ADMCTL_CONDITION_OPTION_URL = 'admission/options'; //policy
  public static ADMCTL_STATE_URL = 'admission/state'; //policy
  public static ADM_CTRL_K8S_TEST = 'admission/test'; //policy
  public static LAYER_URL = 'scan/registry/layer'; //assets
  public static FED_MEMBER_URL = 'fed/member'; //multi-cluster
  public static FED_DEPLOY = 'fed/deploy'; //multi-cluster
  public static FED_SUMMARY = 'fed/summary'; //multi-cluster
  public static FED_PROMOTE_URL = 'fed/promote'; //multi-cluster
  public static FED_DEMOTE_URL = 'fed/demote'; //multi-cluster
  public static FED_JOIN_TOKEN = 'fed/join_token'; //multi-cluster
  public static FED_JOIN_URL = 'fed/join'; //multi-cluster
  public static FED_LEAVE_URL = 'fed/leave'; //multi-cluster
  public static FED_REMOVE_URL = 'fed'; //multi-cluster
  public static FED_CFG_URL = 'fed/config'; //multi-cluster
  public static FED_REDIRECT_URL = 'fed/switch'; //multi-cluster
  public static DLP_SENSORS_URL = 'dlp/sensor'; //policy
  public static DLP_SENSORS_EXPORT_URL = 'dlp/sensor/export'; //policy
  public static DLP_SENSORS_IMPORT_URL = 'dlp/sensor/import'; //policy
  public static DLP_GROUPS_URL = 'dlp/group'; //policy
  public static WAF_SENSORS_URL = 'waf/sensor'; //policy
  public static WAF_SENSORS_EXPORT_URL = 'waf/sensor/export'; //policy
  public static WAF_SENSORS_IMPORT_URL = 'waf/sensor/import'; //policy
  public static WAF_GROUPS_URL = 'waf/group'; //policy
  public static GROUP_EXPORT_URL = 'group/export'; //policy
  public static GROUP_SCRIPT_URL = 'group/custom_check'; //policy
  public static IP_GEO_URL = 'ip-geo'; //common
  public static IBM_SETUP_URL = 'ibmsa_setup'; //config
  public static PERMISSION_OPTIONS = 'role2/permission-options'; //auth
  public static ROLES = 'role2'; //auth
  public static SINGLE_ENFORCER = 'single-enforcer'; //assets
  public static USAGE_REPORT_URL = 'usage'; //config
  public static MULTI_CLUSTER_SUMMARY = 'multi-cluster-summary'; //multi-cluster
  public static PASSWORD_PROFILE = 'password-profile'; //auth
  public static PUBLIC_PASSWORD_PROFILE = 'password-profile/public'; //auth
  public static USER_BLOCK_URL = 'password-profile/user'; //auth
  public static SELF_URL = 'self'; //auth
  public static WEBHOOK = 'webhook'; //config
  public static IMPORT_GROUP_URL = 'group/import'; //policy
  public static ADMISSION_TEST_URL = 'admission/matching-test'; //policy
  public static MGR_VERSION = 'version'; //common
  public static EXPORT_ADM_CTRL = 'admission/export'; //policy
  public static IMPORT_ADM_CTRL = 'admission/import'; //policy
  public static PROMOTE_NETWORK_RULE = 'policy/promote'; //policy
  public static PROMOTE_ADMISSION_RULE = 'admission/promote'; //policy
  public static CVE_PROFILE = 'risk/cve/profile'; //risks
  public static CVE_PROFILE_ENTRY = 'risk/cve/profile/entry'; //risks
  public static SYSTEM_RBAC_URL = 'dashboard/rbac'; //dashboard
  public static SERVICE_ALL = 'service/all'; //policy
  public static DEBUG_URL = 'debug'; //config
  public static THREAT_URL = 'threat';
  public static HEART_BEAT_URL = "heartbeat";
}