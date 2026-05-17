export default {
  tableColumnConfig: [
    {
      label: '名称',
      prop: 'name'
    },
    {
      label: '创建时间',
      prop: 'createDate'
    },
    // {
    //   label: '创建人',
    //   prop: 'createByName'
    // },
    // {
    //   label: '设备类型',
    //   prop: 'deviceType'
    // },
    {
      label: '文件类型',
      prop: 'fileTypeName'
    },
    {
      label: '分隔符',
      prop: 'separatorName'
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
      label: 'TXT',
      value: 'txt'
    },
    {
      label: 'DAT',
      value: 'dat'
    },
    {
      label: 'CSV',
      value: 'csv'
    },
    {
      label: 'XML',
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
  tableIdTypeList: [
    {
      label: '自增ID',
      value: '1'
    },
    {
      label: 'UUID',
      value: '2'
    },
    {
      label: '列配置项',
      value: '3'
    }
  ],
  separatorList: [{ value: '制表符', label: '\t' }, { value: '空格', label: ' ' }]
};
