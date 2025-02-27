package com.neu.model

import spray.json.*

/**
 * Process profile entry
 *
 * @param name
 *   the name of the process
 * @param path
 *   the path of the process
 * @param user
 *   the user who run it
 * @param uid
 *   the uid
 * @param action
 *   the action
 */
case class ProcessProfileEntry(
  name: String,
  path: Option[String],
  user: Option[String],
  uid: Option[Int],
  action: String
)

case class ProcessProfileConfig(
  group: String,
  alert_disabled: Option[Boolean] = None,
  hash_enabled: Option[Boolean] = None,
  process_delete_list: Option[Array[ProcessProfileEntry]],
  process_change_list: Option[Array[ProcessProfileEntry]],
  process_replace_list: Option[Array[ProcessProfileEntry]]
)

case class ProcessProfileConfigData(process_profile_config: ProcessProfileConfig)

object ProcessProfileJsonProtocol extends DefaultJsonProtocol {
  given processProfileEntryFormat: RootJsonFormat[ProcessProfileEntry]           = jsonFormat5(
    ProcessProfileEntry.apply
  )
  given processProfileFormat: RootJsonFormat[ProcessProfileConfig]               = jsonFormat6(
    ProcessProfileConfig.apply
  )
  given ProcessProfileConfigDataFormat: RootJsonFormat[ProcessProfileConfigData] =
    jsonFormat1(ProcessProfileConfigData.apply)

  def profileConfigToJson(profileConfig: ProcessProfileConfigData): String =
    profileConfig.toJson.compactPrint
}
