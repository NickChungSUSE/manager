// Generated by ScalaTS 0.5.9: https://scala-ts.github.io/scala-ts/

import { Array, isArray } from './Array';

export interface ComplianceProfileConfig {
  name: string;
  disable_system?: boolean;
  entries?: Array;
}

export function isComplianceProfileConfig(
  v: any
): v is ComplianceProfileConfig {
  return (
    typeof v['name'] === 'string' &&
    (!v['disable_system'] || typeof v['disable_system'] === 'boolean') &&
    (!v['entries'] || (v['entries'] && isArray(v['entries'])))
  );
}
