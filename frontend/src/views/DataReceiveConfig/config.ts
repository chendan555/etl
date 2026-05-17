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
    {
      label: '创建人',
      prop: 'createBy'
    },
    {
      label: '数据源',
      prop: 'databaseName'
    },
    {
      label: '模式',
      prop: 'modeName'
    },
    {
      label: '修改时间',
      prop: 'updateDate'
    },
    {
      label: '修改人',
      prop: 'updateBy'
    },
    {
      label: '备注',
      prop: 'remarks'
    }
  ],
  handleConfig: [
/*     {
      name: '引接',
      handle: 'receive',
      icon: 'connection'
    }, */
    {
      name: '修改',
      handle: 'update',
      icon: 'edit'
    },
    {
      name: '删除',
      handle: 'delete',
      icon: 'delete'
    }
  ]
};
