export default {
  tableColumnConfig_records: [
    {
      label: '批次',
      prop: 'batch'
    },
    {
      label: '引接时间',
      prop: 'createTime'
    },
    {
      label: '引接人',
      prop: 'createName'
    },
    {
      label: '引接主体',
      prop: 'mainBody'
    },
    {
      label: '入库数据源',
      prop: 'toDataSourceName'
    },
    {
      label: '入库模式',
      prop: 'toSchema'
    },
    {
      label: '入库数据表',
      prop: 'toTable'
    },
    // {
    //   label: '型号',
    //   prop: 'ddType'
    // }
  ],
  handleList_records: [
    {
      name: '查看',
      handle: 'review',
      icon: 'view'
    },
    {
      name: '生成复盘数据',
      handle: 'build',
      icon: 's-data'
    }
  ]
};
