/**
 * 旅团组织机构。树涉及到的部分配置,配置和tree里面的配置参数对应。
 */
const sysOrganizationInfo = sessionStorage.getItem("user") ?
  JSON.parse(sessionStorage.getItem("user")).userDetails.sysOrganizationInfo : {};
export default {
  propsApi: {
    api: "api_common",
    fn: "apiGetJgTreeLv",
    onceParams: {
      parentOrgCode: sysOrganizationInfo ? sysOrganizationInfo.gsDwnm : '',
      flag: 0
    },
    params: {}
  },
  lazy: true,
  loadParentId: 'pid',
  valueBy: 'id',
  props: {
    label: "name",
    children: "children",
    disabled: false
  },
  defaultExpandedKeys: ['110']
}
