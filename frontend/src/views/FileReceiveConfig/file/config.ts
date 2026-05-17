let params = {
  connection: ''
};
export default {
  tableColumnConfig: [
    {
      label: '名称',
      prop: 'name'
    },
    {
      label: '文件路径',
      prop: 'filePath'
    },
    /* {
      label: '所属任务',
      prop: 'taskName'
    },
    {
      label: '所属节点',
      prop: 'taskNodeName'
    },
    {
      label: '型号',
      prop: 'ddType'
    }, */
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
  formConfig: {
    ruleForm: {
      name: '',
      fileMulti: '1',
      filePath: '',
      folderPath: '',
      // taskId: '',
      // taskName: '',
      // ddType: '',
      // taskNodePath: [],
      analysisProtocolId: '',
      analysisProtocolName: '',
      targetDataSourceId: '',
      targetDataSourceName: '',
      targetSchema: '',
      taskDataFlag: 0,
      jumpRow: [],
      remark: ''
    },
    formItemConfig: [
      {
        label: '名称',
        type: 'input',
        labelWidth: '100px',
        valueKey: 'name',
        span: 24
      },
      {
        label: '文件单选',
        type: 'radio',
        labelWidth: '100px',
        valueKey: 'fileMulti',
        options: [
          {
            name: '是',
            value: '1'
          },
          {
            name: '否',
            value: '0'
          }
        ],
        span: 24
      },
      {
        label: '文件路径',
        type: 'file',
        labelWidth: '100px',
        valueKey: 'filePath',
        fileType: '.txt,.dat,.csv,.xml',
        span: 24,
        showCondition: {
          key: 'fileMulti',
          value: '1'
        }
      },
      {
        label: '文件夹路径',
        type: 'input',
        labelWidth: '100px',
        valueKey: 'folderPath',
        placeholder: '请输入文件夹路径',
        span: 24,
        showCondition: {
          key: 'fileMulti',
          value: '0'
        }
      },
      {
        label: '解析协议',
        type: 'select',
        labelWidth: '100px',
        valueKey: 'analysisProtocolId',
        span: 24,
        options: 'getAllProtocolList',
        optionsConfig: {
          name: 'name',
          value: 'id'
        },
        watch: function(ruleForm: any, selectOptions: any) {
          let protocol = selectOptions.find((ele: any) => ele.id === ruleForm.analysisProtocolId);
          ruleForm.analysisProtocolName = protocol.name;
        }
      }
      // {
      //   label: '目标数据源',
      //   type: 'select',
      //   labelWidth: '100px',
      //   valueKey: 'targetDataSourceName',
      //   options: 'getDatabases',
      //   optionsConfig: {
      //     name: 'name',
      //     value: 'name'
      //   },
      //   watch: function(val: any) {
      //     params.connection = val.targetDataSourceName;
      //     val.targetSchema = '';
      //   },
      //   span: 24
      // },
      // {
      //   label: '目标模式',
      //   type: 'select',
      //   labelWidth: '100px',
      //   valueKey: 'targetSchema',
      //   options: 'getSchema',
      //   optionsConfig: {
      //     name: 'text',
      //     value: 'text'
      //   },
      //   optionsParams: params,
      //   watch: function(val: any) {},
      //   span: 24
      // }
      // {
      //   label: '备注',
      //   type: 'textarea',
      //   labelWidth: '100px',
      //   valueKey: 'remark',
      //   span: 24
      // }
    ],
    formItemRuleConfig: {
      name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
      // filePath: [{ required: true, message: '请选择文件', trigger: 'blur' }],
      // folderPath: [{ required: true, message: '请输入文件夹地址', trigger: 'blur' }],
      analysisProtocolId: [{ required: true, message: '请选择解析协议', trigger: 'blur' }]
      // taskId: [{ required: true, message: '请选择任务', trigger: 'blur' }],
      // taskNodePath: [{ required: true, message: '请选择任务节点', trigger: 'blur' }]
    }
  },
  mergeTxtFormConfig: {
    ruleForm: {
      deviceSign: '1',
      fillSign: '0',
      resultName: 'result'
    },
    formItemConfig: [
      {
        label: '分隔符',
        type: 'select',
        labelWidth: '100px',
        valueKey: 'deviceSign',
        options: [
          {
            name: '空格',
            value: '1'
          },
          {
            name: '逗号',
            value: '2'
          },
          {
            name: '制表符',
            value: '3'
          }
        ],
        span: 24
      },
      {
        label: '缺省填充字符',
        type: 'input',
        labelWidth: '100px',
        valueKey: 'fillSign',
        span: 24
      },
      {
        label: '输出文件名称',
        type: 'input',
        labelWidth: '100px',
        valueKey: 'resultName',
        span: 24
      }
    ]
  }
};
