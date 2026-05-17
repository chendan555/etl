/**
 * 管理分类。树涉及到的部分配置,配置和tree里面的配置参数对应。
 */
export default {
  propsApi: {
    api: 'api_common',
    fn: 'apiGetGlflTree',
    onceParams: {

    },
    params: {

    }
  },
  lazy: true,
  loadParentId: 'pid',
  valueBy: 'id',
  props: {
    label: "name",
    children: "children",
    disabled: false
  },
  defaultExpandedKeys: ['0']
}
