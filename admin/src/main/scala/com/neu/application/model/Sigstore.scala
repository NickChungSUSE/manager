package com.neu.application.model

case class RootOfTrust(
  name: Option[String],
  comment: Option[String],
  is_private: Option[Boolean],
  rootless_keypairs_only: Option[Boolean],
  rekor_public_key: Option[String],
  root_cert: Option[String],
  sct_public_key: Option[String],
  verifiers: Option[Array[Verifier]],
  cfg_type: Option[String]
)

case class Verifier(
  name: Option[String],
  root_of_trust_name: Option[String],
  comment: Option[String],
  is_private: Option[Boolean],
  verifier_type: Option[String],
  ignore_tlog: Option[Boolean],
  ignore_sct: Option[Boolean],
  public_key: Option[String],
  cert_issuer: Option[String],
  cert_subject: Option[String]
)
