// Generated by ScalaTS 0.5.9: https://scala-ts.github.io/scala-ts/

import { Group, isGroup } from './Group';

export interface GroupWrap {
  group: Group;
}

export function isGroupWrap(v: any): v is GroupWrap {
  return (
    (v['group'] && isGroup(v['group']))
  );
}