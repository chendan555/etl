export default {
  tableColumnConfig: [
    {
      label: '主表名称',
      prop: 'tableName'
    },
    {
      label: '模式',
      prop: 'modelId'
    },
    {
      label: '数据源',
      prop: 'databaseId'
    },
    {
      label: '表关系名称',
      prop: 'relationName'
    }
  ],
  handleList: [
    {
      name: '引接',
      handle: 'receive',
      icon: 'connection'
    }
  ],
  tableColumnConfig_receive: [
    {
      label: '列名称',
      prop: 'name'
    },
    {
      label: '别名',
      prop: 'comments'
    },
    {
      label: '数据类型',
      prop: 'type'
    },
    {
      label: '数据长度',
      prop: 'lenght'
    }
  ]
};
// 文件表格配置
export const wjtableConfig = {
  tableColumnConfig: [
    {
      label: '名称',
      prop: 'name'
    },
    {
      label: '文件路径',
      prop: 'filePath'
    },
    {
      label: '所属任务',
      prop: 'taskName'
    },
    {
      label: '所属节点',
      prop: 'taskNodeName'
    },
    {
      label: '解析协议',
      prop: 'analysisProtocolName'
    },
    {
      label: '目标数据源',
      prop: 'targetDataSourceName'
    },
    {
      label: '目标模式',
      prop: 'targetSchema'
    },
    {
      label: '引接批次号',
      prop: 'batch'
    },
    // {
    //   label: '型号',
    //   prop: 'ddType'
    // }
    // {
    //   label: '备注',
    //   prop: 'remark'
    // }
  ]
};
// 数据库表格配置
export const sjkTableConfig = {
  tableColumnConfig: [
    {
      label: '主表名称',
      prop: 'tableName'
    },
    {
      label: '源数据源',
      prop: 'dataSourceName'
    },
    {
      label: '源模式',
      prop: 'schema'
    },
    {
      label: '目标数据源',
      prop: 'targetDataSourceName'
    },
    {
      label: '目标模式',
      prop: 'targetSchema'
    },
    {
      label: '表关系名称',
      prop: 'tableConfigName'
    },
    {
      label: '引接批次号',
      prop: 'batch'
    },
    // {
    //   label: '型号',
    //   prop: 'ddType'
    // }
  ]
};
// 仿真表格配置
export const fzTableConfig = {
  tableColumnConfig: [
    {
      label: '名称',
      prop: 'name'
    },
    {
      label: '接口IP',
      prop: 'ipInterface'
    },
    {
      label: '所属任务',
      prop: 'taskName'
    },
    {
      label: '所属节点',
      prop: 'taskNodeName'
    },
    {
      label: '目标数据源',
      prop: 'targetDataSourceName'
    },
    {
      label: '目标模式',
      prop: 'targetSchema'
    },
    {
      label: '备注',
      prop: 'remarks'
    }
  ]
};
// 表单表格配置
export const formTableConfig = {
  tableColumnConfig: [
    {
      label: '表单名称',
      prop: 'tableName'
    },
    {
      label: '表单路径',
      prop: 'filePath'
    },
    {
      label: '所属任务',
      prop: 'taskName'
    },
    {
      label: '所属节点',
      prop: 'taskNodeName'
    },
    {
      label: '解析模板',
      prop: 'analysisProtocolName'
    },
    {
      label: '目标数据源',
      prop: 'targetDataSourceName'
    },
    {
      label: '目标模式',
      prop: 'targetSchema'
    },
    {
      label: '备注',
      prop: 'remark'
    }
  ]
};
// 选择批次
export const pcOptions = [
  {
    value: '1',
    label: '批次1'
  },
  {
    value: '2',
    label: '批次2'
  },
  {
    value: '3',
    label: '批次3'
  },
  {
    value: '4',
    label: '批次4'
  }
];

export const tableColumnConfigList = {
  table: sjkTableConfig.tableColumnConfig,
  file: wjtableConfig.tableColumnConfig,
  emulate: fzTableConfig.tableColumnConfig,
  form: formTableConfig.tableColumnConfig
};
