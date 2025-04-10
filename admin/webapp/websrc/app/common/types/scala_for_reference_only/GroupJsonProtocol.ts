// Generated by ScalaTS 0.5.9: https://scala-ts.github.io/scala-ts/

export class GroupJsonProtocol {
  public equalOp: string = '=';

  public notEqualOp: string = '!=';

  public containsOp: string = '@';

  public prefixOp: string = '^';

  public regexOp: string = '~';

  public notRegexOp: string = '!~';

  public containsSign: string = 'contains';

  public prefixSign: string = 'prefix';

  public regexSign: string = 'regex';

  public notRegexSign: string = '!regex';

  public ops: ReadonlyArray<string> = [
    this.equalOp,
    this.notEqualOp,
    this.containsOp,
    this.prefixOp,
    this.regexOp,
    this.notRegexOp,
  ];

  private static instance: GroupJsonProtocol;

  private constructor() {}

  public static getInstance() {
    if (!GroupJsonProtocol.instance) {
      GroupJsonProtocol.instance = new GroupJsonProtocol();
    }

    return GroupJsonProtocol.instance;
  }
}

export const GroupJsonProtocolInhabitant: GroupJsonProtocol =
  GroupJsonProtocol.getInstance();

export function isGroupJsonProtocol(v: any): v is GroupJsonProtocol {
  return v instanceof GroupJsonProtocol && v === GroupJsonProtocolInhabitant;
}
