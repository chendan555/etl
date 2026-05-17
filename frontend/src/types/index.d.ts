export interface reviewRoute {
  path: string;
  pathadg: string;
  name: string;
  meta: object;
  hidden: boolean;
  redirect: string;
  children: object;
  component?: any;
}
