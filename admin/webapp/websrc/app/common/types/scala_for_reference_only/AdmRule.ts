// Generated by ScalaTS 0.5.9: https://scala-ts.github.io/scala-ts/

import { Array, isArray } from './Array';

export interface AdmRule {
  id: number;
  category: string;
  comment?: string;
  criteria?: Array;
  disable?: boolean;
  cfg_type?: string;
  rule_type?: string;
}

export function isAdmRule(v: any): v is AdmRule {
  return (
    typeof v['id'] === 'number' &&
    typeof v['category'] === 'string' &&
    (!v['comment'] || typeof v['comment'] === 'string') &&
    (!v['criteria'] || (v['criteria'] && isArray(v['criteria']))) &&
    (!v['disable'] || typeof v['disable'] === 'boolean') &&
    (!v['cfg_type'] || typeof v['cfg_type'] === 'string') &&
    (!v['rule_type'] || typeof v['rule_type'] === 'string')
  );
}
