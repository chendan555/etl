export const projInforLabel = [
  {
    name: '项目名称',
    prop: 'projName',
    value: ''
  },
  {
    name: '流程总数',
    prop: 'scheduleNum',
    value: ''
  },
  {
    name: '已提交流程数',
    prop: 'subScheNum',
    value: ''
  },
  {
    name: '创建人',
    prop: 'createBy',
    value: ''
  },
  {
    name: '创建时间',
    prop: 'createTime',
    value: ''
  },
  {
    name: '更新时间',
    prop: 'updateTime',
    value: ''
  },
  {
    name: '描述',
    prop: 'description',
    value: ''
  },
  {
    name: '标签',
    prop: 'projLabel',
    value: ''
  }
];

export const procInforLabel = [
  {
    label: '方案名称',
    prop: 'name',
    value: ''
  },
  {
    label: '创建时间',
    prop: 'createdDate',
    width: 180,
    value: ''
  },
  {
    label: '状态',
    prop: 'statusInfo',
    width: 80,
    value: ''
  }
];

export const dataSource = {
  tableColumnConfig: [
    {
      label: '名称',
      prop: 'name',
      width: '60'
    },
    {
      label: '类型',
      prop: 'type',
      width: '60'
    },
    {
      label: '更新时间',
      prop: 'updateTime',
      width: '90'
    }
  ],
  tableData: []
};

export const dataCollection = {
  tableColumnConfig: [
    {
      label: '名称',
      prop: 'name',
      width: '60'
    },
    {
      label: '创建人',
      prop: 'createBy',
      width: '60'
    },
    {
      label: '更新时间',
      prop: 'updateTime',
      width: '90'
    }
  ],
  tableData: []
};
export const options = [
  {
    type: '1',
    label: '数据库'
  },
  {
    type: '2',
    label: '文件'
  }
  /* {
    type: '3',
    label: '仿真'
  }, */
  // {
  //   type: '4',
  //   label: '表单'
  // }
];
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
      label: '备注',
      prop: 'remark'
    }
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
      label: '数据源',
      prop: 'targetDataSourceName'
    },
    {
      label: '模式',
      prop: 'targetSchema'
    },
    {
      label: '表关系名称',
      prop: 'tableConfigName'
    }
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
      label: '表单地址',
      prop: 'keyNums'
    },
    {
      label: '解析模板',
      prop: 'tableName'
    }
  ]
};
// 添加引接
export const addYj = [
  // {
  //   label: '引接名称',
  //   prop: 'name',
  //   value: ''
  // },
  {
    label: '引接主体',
    prop: 'mainBody',
    value: ''
  },
  {
    label: '解析协议',
    prop: 'analysisProtocolName',
    value: ''
  },
  {
    label: '目标数据源',
    prop: 'targetDataSourceName',
    value: ''
  },
  {
    label: '目标模式',
    prop: 'targetSchema',
    value: ''
  }
];

export const viewDealResultConfig = {
  tableColumnConfig: [
    {
      label: '输出数据名称',
      prop: 'stepName',
      align: 'center'
    },
    {
      label: '处理时间',
      prop: 'opDateString',
      align: 'center'
    },
    {
      label: '处理流程名称',
      prop: 'jobName',
      align: 'center'
    },
    {
      label: '执行人',
      prop: 'opUserName',
      align: 'center'
    },
    // {
    //   label: '执行批次',
    //   prop: 'runBatch',
    //   align: 'center'
    // }
  ]
};
