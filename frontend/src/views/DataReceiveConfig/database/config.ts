let params = {
  connection: '',
  schema: ''
};
export default {
  tableColumnConfig: [
    {
      label: '主表',
      prop: 'tableName'
    },
    {
      label: '数据源',
      prop: 'dataSourceName'
    },
    {
      label: '模式',
      prop: 'schema'
    },
    // {
    //   label: '所属任务',
    //   prop: 'taskName'
    // },
    // {
    //   label: '所属节点',
    //   prop: 'taskNodeName'
    // },
    // {
    //   label: '型号',
    //   prop: 'ddType'
    // },
    {
      label: '解析主子表关系',
      prop: 'tableConfigName'
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
  tableRelationRuleForm: {
    id: '',
    taskId: '',
    taskName: '',
    ddType: '',
    nodeId: '',
    taskNodePath: [],
    dataSourceId: '',
    databaseName: '',
    schemaId: '',
    schema: '',
    targetDataSourceId: '',
    targetDataSourceName: '',
    targetSchema: '',
    tableName: '',
    tableConfigId: '',
    tableConfigName: '',
    remarks: ''
  },
  formItemRuleConfig: {
    dataSourceId: [{ required: true, message: '请输入数据源', trigger: 'blur' }],
    schema: [{ required: true, message: '请输入模式', trigger: 'blur' }],
    tableName: [{ required: true, message: '请输入表名称', trigger: 'blur' }],
    tableConfigId: [{ required: true, message: '请输入表关系名称', trigger: 'blur' }],
    targetDataSourceId: [{ required: true, message: '请输入目标数据源', trigger: 'blur' }],
    targetSchema: [{ required: true, message: '请输入目标模式', trigger: 'blur' }]
  }
};
