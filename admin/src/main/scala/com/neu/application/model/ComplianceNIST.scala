package com.neu.application.model

case class ComplianceNIST(
  name: String,
  subcontrol: String,
  control_id: String,
  title: String
)

case class ComplianceNISTMap(
  nist_map: Map[String, ComplianceNIST]
)
