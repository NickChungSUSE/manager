// Generated by ScalaTS 0.5.9: https://scala-ts.github.io/scala-ts/

import { Array, isArray } from './Array';

export interface TopThreat {
  source: Array;
  destination: Array;
}

export function isTopThreat(v: any): v is TopThreat {
  return (
    v['source'] &&
    isArray(v['source']) &&
    v['destination'] &&
    isArray(v['destination'])
  );
}
