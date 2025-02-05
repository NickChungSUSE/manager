// Generated by ScalaTS 0.5.9: https://scala-ts.github.io/scala-ts/

export interface PolicyOutput {
  groupSet: ReadonlyArray<string>;
  applicationsInPolicy: ReadonlyArray<[string, number]>;
}

export function isPolicyOutput(v: any): v is PolicyOutput {
  return (
    Array.isArray(v['groupSet']) &&
    v['groupSet'].every(elmt => typeof elmt === 'string') &&
    Array.isArray(v['applicationsInPolicy']) &&
    v['applicationsInPolicy'].every(
      elmt =>
        Array.isArray(elmt) &&
        elmt.length == 2 &&
        typeof elmt[0] === 'string' &&
        typeof elmt[1] === 'number'
    )
  );
}
