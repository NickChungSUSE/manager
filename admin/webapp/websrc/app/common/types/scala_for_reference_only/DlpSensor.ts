// Generated by ScalaTS 0.5.9: https://scala-ts.github.io/scala-ts/

import { DlpRule, isDlpRule } from './DlpRule';

export interface DlpSensor {
  name: string;
  comment: string;
  groups: ReadonlyArray<string>;
  rules: ReadonlyArray<DlpRule>;
}

export function isDlpSensor(v: any): v is DlpSensor {
  return (
    typeof v['name'] === 'string' &&
    typeof v['comment'] === 'string' &&
    Array.isArray(v['groups']) &&
    v['groups'].every(elmt => typeof elmt === 'string') &&
    Array.isArray(v['rules']) &&
    v['rules'].every(elmt => elmt && isDlpRule(elmt))
  );
}
