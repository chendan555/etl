/**
 * 机构仓库，非懒加载。树涉及到的部分配置,配置和tree里面的配置参数对应。
 */
const sysOrganizationInfo = sessionStorage.getItem("user") ?
  JSON.parse(sessionStorage.getItem("user")).userDetails.sysOrganizationInfo : {};
export default {
  propsApi: {
    api: 'api_common',
    fn: 'getUserMechanism',
    onceParams: {
      parentOrgCode: sysOrganizationInfo ? sysOrganizationInfo.gsDwnm : '',
      lx: 1
    },
  },
  loadParentId: 'pid',
  valueBy: 'aid',
  needCludeArray: true,
  props: {
    label: "dwmc",
    children: "children",
    disabled: false
  },
  defaultExpandedKeys: ['110']
}
