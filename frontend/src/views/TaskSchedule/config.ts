import mapping from './mapping';
export default {
  tableColumnConfig_schedule: [
    {
      label: '任务名称',
      prop: 'jobName'
    },
    {
      label: '处理方案名称',
      prop: 'planName'
    },
    {
      label: 'CRON执行表达式',
      prop: 'cronExpression'
    },
    {
      label: '状态',
      prop: 'status',
      propToNameFn: (val: string) => {
        return (mapping.jobStatus as any)[val];
      }
    },
    {
      label: '备注',
      prop: 'remarks'
    }
  ],
  handleList_schedule: [
    {
      name: '编辑',
      handle: 'edit',
      icon: 'edit'
    },
    {
      name: '删除',
      handle: 'delete',
      icon: 'delete'
    }
  ],
  formConfig: {
    ruleForm: {
      jobName: '',
      cronExpression: '* * * * * ? *',
      detailVoList: [],
      status: '0',
      misfirePolicy: '0',
      concurrent: '0',
      remarks: ''
    },
    formItemRuleConfig: {
      jobName: [{ required: true, message: '请输入调度任务名称', trigger: 'blur' }],
      cronExpression: [{ required: true, message: '请配置调度规则', trigger: 'blur' }],
      detailVoList: [{ required: true, message: '请选择需要执行的处理方案', trigger: 'blur' }],
      status: [{ required: true, message: '请选择调度任务状态', trigger: 'blur' }],
      misfirePolicy: [{ required: true, message: '请选择计划策略', trigger: 'blur' }],
      concurrent: [{ required: true, message: '请选择是否并发执行', trigger: 'blur' }]
    },
    formItemConfig: [
      {
        label: '调度任务名称',
        type: 'input',
        labelWidth: '140px',
        valueKey: 'jobName',
        placeholder: '请输入调度任务名称（示例：XXX方案调度）'
      },
      {
        label: '计划策略',
        type: 'radio',
        labelWidth: '140px',
        valueKey: 'misfirePolicy',
        options: Object.keys(mapping.misfirePolicy).map(key => {
          return {
            name: (mapping.misfirePolicy as any)[key],
            value: key
          };
        }),
        span: 24
      },
      {
        label: '是否并发执行',
        type: 'radio',
        labelWidth: '140px',
        valueKey: 'concurrent',
        options: Object.keys(mapping.concurrent).map(key => {
          return {
            name: (mapping.concurrent as any)[key],
            value: key
          };
        }),
        span: 12
      },
      {
        label: '状态',
        type: 'radio',
        labelWidth: '140px',
        valueKey: 'status',
        options: Object.keys(mapping.jobStatus).map(key => {
          return {
            name: (mapping.jobStatus as any)[key],
            value: key
          };
        }),
        span: 12
      }
      /* {
        label: '处理方案',
        type: 'select',
        labelWidth: '140px',
        valueKey: 'planList',
        options: 'getDatabases',
        optionsConfig: {
          name: 'name',
          value: 'name'
        },
        placeholder: '请输入调度任务名称（示例：XXX方案调度）'
      } */
    ]
  },
  tableColumnConfig: [
    {
      label: '方案名称',
      prop: 'name'
    },
    {
      label: '创建时间',
      prop: 'createdDate'
    },
    {
      label: '状态',
      prop: 'statusInfo'
    }
  ],
  filterFormConfig: {
    ruleForm: {
      jobName: '',
      status: ''
    },
    formItemConfig: [
      {
        span: 12,
        label: '名称',
        valueKey: 'jobName',
        type: 'input',
        labelWidth: '80px'
      },
      {
        span: 12,
        label: '状态',
        valueKey: 'status',
        type: 'select',
        options: [{ name: '暂停', value: '1' }, { name: '正常', value: '0' }],
        labelWidth: '80px'
      }
    ]
  },
  logListTableColumnConfig: [
    {
      label: '流程名称',
      prop: 'exeName'
    },
    {
      label: '处理方案名称',
      prop: 'planName'
    },
    {
      label: '调度任务名称',
      prop: 'jobName'
    },
    {
      label: '执行时间',
      prop: 'startTime'
    },
    {
      label: '结束时间',
      prop: 'stopTime'
    },
    {
      label: '执行信息',
      prop: 'jobMessage'
    }
  ],
  tableColumnConfig_receive: [
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
    }
  ],
  tableColumnConfig_process: [
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
    }
  ]
};
