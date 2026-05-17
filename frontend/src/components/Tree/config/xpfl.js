/**
 * 型谱目录，非懒加载。树涉及到的部分配置,配置和tree里面的配置参数对应。
 */
export default {
  propsApi: {
    api: 'api_singleMenu',
    fn: 'apiGetEquipMenu',
    onceParams: {

    },
    params: {

    }
  },
  loadParentId: 'pid',
  needCludeArray: true,
  valueBy: 'id',
  props: {
    label: "name",
    children: "children",
    disabled: false
  },
  defaultExpandedKeys: []
}
