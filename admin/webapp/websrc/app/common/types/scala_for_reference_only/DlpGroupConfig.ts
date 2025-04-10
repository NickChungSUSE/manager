// Generated by ScalaTS 0.5.9: https://scala-ts.github.io/scala-ts/

import { DlpSetting, isDlpSetting } from './DlpSetting';

export interface DlpGroupConfig {
  name: string;
  status?: boolean;
  delete?: ReadonlyArray<string>;
  sensors?: ReadonlyArray<DlpSetting>;
  replace?: ReadonlyArray<DlpSetting>;
}

export function isDlpGroupConfig(v: any): v is DlpGroupConfig {
  return (
    typeof v['name'] === 'string' &&
    (!v['status'] || typeof v['status'] === 'boolean') &&
    (!v['delete'] ||
      (Array.isArray(v['delete']) &&
        v['delete'].every(elmt => typeof elmt === 'string'))) &&
    (!v['sensors'] ||
      (Array.isArray(v['sensors']) &&
        v['sensors'].every(elmt => elmt && isDlpSetting(elmt)))) &&
    (!v['replace'] ||
      (Array.isArray(v['replace']) &&
        v['replace'].every(elmt => elmt && isDlpSetting(elmt))))
  );
}
