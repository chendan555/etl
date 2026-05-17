export default {
  tableColumnConfig: [
    {
      label: '名称',
      prop: 'configName'
    },
    {
      label: '主表名称',
      prop: 'mainTableName'
    },
    {
      label: '主子表数量',
      prop: 'tableCount'
    },
    {
      label: '创建时间',
      prop: 'createDate'
    },
    {
      label: '备注',
      prop: 'remark'
    }
  ],
  handleConfig: [
    {
      name: '修改',
      handle: 'update',
      icon: 'edit',
      color: '#409eff'
    },
    {
      name: '删除',
      handle: 'delete',
      icon: 'delete',
      color: '#e96a54'
    }
  ],
  fileTypeList: [
    {
      label: 'txt',
      value: 'txt'
    },
    {
      label: 'csv',
      value: 'csv'
    },
    {
      label: 'xml',
      value: 'xml'
    }
  ],
  deviceTypeList: [
    {
      label: '遥测车',
      value: '1'
    },
    {
      label: '雷达',
      value: '2'
    },
    {
      label: '干扰器',
      value: '3'
    }
  ],
  separatorList: [{ value: '制表符', label: '\t' }, { value: '空格', label: ' ' }]
};
