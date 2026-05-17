// 查询配置
export const queryConfig = {
  queryFormItemConfig: [
    {
      label: '',
      type: 'input',
      valueKey: 'name',
      width: '200px',
      span: 6
    },
    {
      label: '',
      type: 'datetime',
      valueKey: 'date',
      width: '200px',
      span: 6
    }
  ],
  queryRuleForm: {
    name: ''
  }
};

// 新增、编辑弹框配置
export const editDialogConfig = {
  dialog: {
    title: '新增任务组',
    visible: false
  },
  formItemRuleConfig: {
    taskGroupName: [
      {
        required: true,
        message: '名称',
        trigger: 'blur'
      }
    ],
    taskGroupCode: [
      {
        required: true,
        message: '编码',
        trigger: 'blur'
      }
    ]
  },
  formItemConfig: [
    {
      label: '名称',
      type: 'input',
      valueKey: 'taskGroupName',
      span: 24
    },
    {
      label: '编码',
      type: 'input',
      valueKey: 'taskGroupCode',
      span: 24
    },
    {
      label: '介绍',
      type: 'textarea',
      valueKey: 'taskGroupIntro',
      span: 24
    },
    {
      label: '标签',
      type: 'newTags',
      btnName: '添加',
      valueKey: 'taskGroupTag',
      span: 24
    }
  ],
  ruleForm: {
    dir: '/',
    taskGroupName: '',
    taskGroupCode: '',
    taskGroupIntro: '',
    taskGroupTag: []
  },
  isHasTable: false,
  isHasTableScheduler: false,
  isHasPreviewtaskGroup: true
};

// 表格内容配置
export const tableConfig = {
  isShowOperation: true,
  isShowCheckBox: true,
  tableColumnConfig: [
    {
      label: '名称',
      prop: 'taskGroupName'
    },
    {
      label: '编码',
      prop: 'taskGroupCode'
    },
    {
      label: '标签',
      prop: 'taskGroupTag'
    },
    {
      label: '创建用户',
      prop: 'createUser'
    },
    {
      label: '创建时间',
      prop: 'createDate'
    }
  ],
  tableData: []
};

// tab弹框配置
export const editDialogWithTabConfig = {
  dialog: {
    title: '新增调度',
    visible: false
  },
  isShowTab: true,
  tabActiveInit: 'schedulerConfig',
  tabList: [
    {
      label: '调度配置',
      name: 'schedulerConfig',
      formItemConfig: [
        {
          label: '调度方式',
          type: 'select',
          valueKey: 'typeChoose',
          optionsConfig: {
            name: 'label',
            value: 'value'
          },
          options: [
            {
              label: '间隔重复',
              value: '间隔重复'
            },
            {
              label: '每天执行',
              value: '每天执行'
            },
            {
              label: '每周执行',
              value: '每周执行'
            },
            {
              label: '每月执行',
              value: '每月执行'
            }
          ],
          span: 24
        },
        {
          label: '时间间隔(单位/分钟)',
          type: 'input',
          valueKey: 'intervalminute',
          span: 24,
          showCondition: {
            key: 'typeChoose',
            value: '间隔重复'
          }
        },
        {
          label: '时',
          type: 'input',
          valueKey: 'hour',
          span: 24,
          showCondition: {
            key: 'typeChoose',
            value: '每天执行'
          }
        },
        {
          label: '分',
          type: 'input',
          valueKey: 'minute',
          span: 24,
          showCondition: {
            key: 'typeChoose',
            value: '每天执行'
          }
        },
        {
          label: '时',
          type: 'input',
          valueKey: 'hour',
          span: 24,
          showCondition: {
            key: 'typeChoose',
            value: '每周执行'
          }
        },
        {
          label: '分',
          type: 'input',
          valueKey: 'minute',
          span: 24,
          showCondition: {
            key: 'typeChoose',
            value: '每周执行'
          }
        },
        {
          label: '周几',
          type: 'select',
          valueKey: 'weekChoose',
          span: 24,
          optionsConfig: {
            name: 'label',
            value: 'value'
          },
          options: [
            {
              label: '周一',
              value: '周一'
            },
            {
              label: '周二',
              value: '周二'
            },
            {
              label: '周三',
              value: '周三'
            },
            {
              label: '周四',
              value: '周四'
            },
            {
              label: '周五',
              value: '周五'
            },
            {
              label: '周六',
              value: '周六'
            },
            {
              label: '周天',
              value: '周天'
            }
          ],
          showCondition: {
            key: 'typeChoose',
            value: '每周执行'
          }
        },
        {
          label: '时',
          type: 'input',
          valueKey: 'hour',
          span: 24,
          showCondition: {
            key: 'typeChoose',
            value: '每月执行'
          }
        },
        {
          label: '分',
          type: 'input',
          valueKey: 'minute',
          span: 24,
          showCondition: {
            key: 'typeChoose',
            value: '每月执行'
          }
        },
        {
          label: '日期',
          type: 'select',
          valueKey: 'monthChoose',
          span: 24,
          optionsConfig: {
            name: 'label',
            value: 'value'
          },
          options: [
            {
              label: '1号',
              value: '1号'
            },
            {
              label: '2号',
              value: '2号'
            },
            {
              label: '3号',
              value: '3号'
            },
            {
              label: '4号',
              value: '4号'
            },
            {
              label: '5号',
              value: '5号'
            },
            {
              label: '6号',
              value: '6号'
            },
            {
              label: '7号',
              value: '7号'
            },
            {
              label: '8号',
              value: '8号'
            },
            {
              label: '9号',
              value: '9号'
            },
            {
              label: '10号',
              value: '10号'
            },
            {
              label: '11号',
              value: '11号'
            },
            {
              label: '12号',
              value: '12号'
            },
            {
              label: '13号',
              value: '13号'
            },
            {
              label: '14号',
              value: '14号'
            },
            {
              label: '15号',
              value: '15号'
            },
            {
              label: '16号',
              value: '16号'
            },
            {
              label: '17号',
              value: '17号'
            },
            {
              label: '18号',
              value: '18号'
            },
            {
              label: '19号',
              value: '19号'
            },
            {
              label: '20号',
              value: '20号'
            },
            {
              label: '21号',
              value: '21号'
            },
            {
              label: '22号',
              value: '22号'
            },
            {
              label: '23号',
              value: '23号'
            },
            {
              label: '24号',
              value: '24号'
            },
            {
              label: '25号',
              value: '25号'
            },
            {
              label: '26号',
              value: '26号'
            },
            {
              label: '27号',
              value: '27号'
            },
            {
              label: '28号',
              value: '28号'
            },
            {
              label: '29号',
              value: '29号'
            },
            {
              label: '30号',
              value: '30号'
            },
            {
              label: '31号',
              value: '31号'
            }
          ],
          showCondition: {
            key: 'typeChoose',
            value: '每月执行'
          }
        },
        {
          label: '是否执行',
          type: 'select',
          valueKey: 'status',
          span: 24,
          default_value: 0,
          optionsConfig: {
            name: 'label',
            value: 'value'
          },
          options: [
            {
              label: '是',
              value: '1'
            },
            {
              label: '否',
              value: '0'
            }
          ]
        }
      ],
      formItemRuleConfig: {
        typeChoose: [
          {
            required: true,
            message: '请选择调度方式',
            trigger: 'change'
          }
        ],
        intervalminute: [
          {
            required: true,
            message: '请输入时间间隔',
            trigger: 'blur'
          }
        ],
        hour: [
          {
            required: true,
            message: '请输入时',
            trigger: 'blur'
          }
        ],
        minute: [
          {
            required: true,
            message: '请输入分',
            trigger: 'blur'
          }
        ],
        weekChoose: [
          {
            required: true,
            message: '请选择周几',
            trigger: 'change'
          }
        ],
        monthChoose: [
          {
            required: true,
            message: '请选择日期',
            trigger: 'blur'
          }
        ],
        status: [
          {
            required: true,
            message: '请选择是否执行',
            trigger: 'blur'
          }
        ]
      },
      ruleForm: {
        path: '',
        type: '',
        executionConfiguration: '',
        taskGroupId: '',
        taskGroupName: '',
        status: '',
        typeChoose: '',
        intervalminute: '',
        weekChoose: '',
        monthChoose: '',
        hour: '',
        minute: ''
      },
      isHasTable: false
    },
    {
      label: '基本配置',
      name: 'baseConfig',
      formItemConfig: [
        {
          label: '本地执行',
          type: 'select',
          valueKey: 'exec_local',
          optionsConfig: {
            name: 'label',
            value: 'value'
          },
          options: [
            {
              label: '是',
              value: 'Y'
            },
            {
              label: '否',
              value: 'N'
            }
          ],
          span: 24,
          watch: function (val) {
            if (val.exec_local) {
              val.exec_remote = 'N';
              val.exec_cluster = 'N';
            }
          }
        },
        {
          label: '远程执行',
          type: 'select',
          valueKey: 'exec_remote',
          optionsConfig: {
            name: 'label',
            value: 'value'
          },
          options: [
            {
              label: '是',
              value: 'Y'
            },
            {
              label: '否',
              value: 'N'
            }
          ],
          span: 24,
          watch: function (val) {
            if (val.exec_remote) {
              val.exec_local = 'N';
              val.exec_cluster = 'N';
            }
          }
        },
        {
          label: '远程主机',
          type: 'select',
          valueKey: 'remote_server',
          optionsConfig: {
            name: 'label',
            value: 'value'
          },
          options: [],
          span: 24,
          showCondition: {
            key: 'exec_remote',
            value: 'Y'
          }
        },
        {
          label: '将文件发送服务器',
          type: 'select',
          valueKey: 'pass_export',
          optionsConfig: {
            name: 'label',
            value: 'value'
          },
          options: [
            {
              label: '是',
              value: 'Y'
            },
            {
              label: '否',
              value: 'N'
            }
          ],
          span: 24,
          showCondition: {
            key: 'exec_remote',
            value: 'Y'
          }
        },
        {
          label: '集群方式执行',
          type: 'select',
          valueKey: 'exec_cluster',
          optionsConfig: {
            name: 'label',
            value: 'value'
          },
          options: [
            {
              label: '是',
              value: 'Y'
            },
            {
              label: '否',
              value: 'N'
            }
          ],
          span: 24,
          watch: function (val) {
            if (val.exec_cluster) {
              val.exec_local = 'N';
              val.exec_remote = 'N';
            }
          }
        },
        {
          label: '提交转换',
          type: 'select',
          valueKey: 'cluster_post',
          optionsConfig: {
            name: 'label',
            value: 'value'
          },
          options: [
            {
              label: '是',
              value: 'Y'
            },
            {
              label: '否',
              value: 'N'
            }
          ],
          span: 24,
          showCondition: {
            key: 'exec_cluster',
            value: 'Y'
          }
        },
        {
          label: '准备执行',
          type: 'select',
          valueKey: 'cluster_prepare',
          optionsConfig: {
            name: 'label',
            value: 'value'
          },
          options: [
            {
              label: '是',
              value: 'Y'
            },
            {
              label: '否',
              value: 'N'
            }
          ],
          span: 24,
          showCondition: {
            key: 'exec_cluster',
            value: 'Y'
          }
        },
        {
          label: '开始执行',
          type: 'select',
          valueKey: 'cluster_start',
          optionsConfig: {
            name: 'label',
            value: 'value'
          },
          options: [
            {
              label: '是',
              value: 'Y'
            },
            {
              label: '否',
              value: 'N'
            }
          ],
          span: 24,
          showCondition: {
            key: 'exec_cluster',
            value: 'Y'
          }
        },
        {
          label: '显示转换',
          type: 'select',
          valueKey: 'cluster_show_taskGroup',
          optionsConfig: {
            name: 'label',
            value: 'value'
          },
          options: [
            {
              label: '是',
              value: 'Y'
            },
            {
              label: '否',
              value: 'N'
            }
          ],
          span: 24,
          showCondition: {
            key: 'exec_cluster',
            value: 'Y'
          }
        },
        {
          label: '使用安全模式',
          type: 'select',
          valueKey: 'safe_mode',
          optionsConfig: {
            name: 'label',
            value: 'value'
          },
          options: [
            {
              label: '是',
              value: 'Y'
            },
            {
              label: '否',
              value: 'N'
            }
          ],
          span: 24
        },
        {
          label: '收集性能指标',
          type: 'select',
          valueKey: 'gather_metrics',
          optionsConfig: {
            name: 'label',
            value: 'value'
          },
          options: [
            {
              label: '是',
              value: 'Y'
            },
            {
              label: '否',
              value: 'N'
            }
          ],
          span: 24
        },
        {
          label: '日志级别',
          type: 'select',
          valueKey: 'log_level',
          optionsConfig: {
            name: 'label',
            value: 'value'
          },
          options: [
            {
              label: '没有日志',
              value: 'Nothing'
            },
            {
              label: '错误日志',
              value: 'Error'
            },
            {
              label: '最小日志',
              value: 'Minimal'
            },
            {
              label: '基本日志',
              value: 'Basic'
            },
            {
              label: '详细日志',
              value: 'Detailed'
            },
            {
              label: '调试日志',
              value: 'Debug'
            },
            {
              label: '行级日志',
              value: 'Rowlevel'
            }
          ],
          span: 24
        },
        {
          label: '重放日期',
          type: 'datetime',
          valueFormat: 'yyyy/MM/dd HH:mm:ss',
          valueKey: 'replay_date',
          span: 24
        }
      ],
      formItemRuleConfig: {
        exec_local: [
          {
            required: true,
            message: '请选择执行方式',
            trigger: 'change'
          }
        ],
        exec_remote: [
          {
            required: true,
            message: '请选择执行方式',
            trigger: 'change'
          }
        ],
        exec_cluster: [
          {
            required: true,
            message: '请选择执行方式',
            trigger: 'change'
          }
        ]
      },
      ruleForm: {
        exec_local: '',
        exec_remote: '',
        pass_export: '',
        exec_cluster: '',
        cluster_post: '',
        cluster_prepare: '',
        cluster_start: '',
        cluster_show_taskGroup: '',
        safe_mode: '',
        log_level: '',
        clear_log: '',
        gather_metrics: '',
        log_file: '',
        log_file_append: '',
        show_subcomponents: '',
        create_parent_folder: '',
        remote_server: '',
        replay_date: ''
      },
      isHasTable: false
    },
    // {
    //   label: '命名参数',
    //   name: 'nameParam',
    //   formItemConfig: [],
    //   formItemRuleConfig: {},
    //   ruleForm: {},
    //   isHasTable: true,
    //   tableColumnConfig: [
    //     {
    //       label: '命名参数',
    //       prop: 'name',
    //       showOverflowTooltip: true,
    //       align: 'left',
    //       headerAlign: 'left',
    //       isEdit: true,
    //       editConfig: {
    //         type: 'input'
    //       }
    //     },
    //     {
    //       label: '值',
    //       prop: 'value',
    //       showOverflowTooltip: true,
    //       align: 'left',
    //       headerAlign: 'left',
    //       isEdit: true,
    //       editConfig: {
    //         type: 'input'
    //       }
    //     },
    //     {
    //       label: '默认值',
    //       prop: 'default_value',
    //       showOverflowTooltip: true,
    //       align: 'left',
    //       headerAlign: 'left',
    //       isEdit: true,
    //       editConfig: {
    //         type: 'input'
    //       }
    //     }
    //   ],
    //   tableData: []
    // },
    // {
    //   label: '变量',
    //   name: 'variable',
    //   formItemConfig: [],
    //   formItemRuleConfig: {},
    //   ruleForm: {},
    //   isHasTable: true,
    //   tableColumnConfig: [
    //     {
    //       label: '变量',
    //       prop: 'name',
    //       showOverflowTooltip: true,
    //       align: 'left',
    //       headerAlign: 'left',
    //       isEdit: true,
    //       editConfig: {
    //         type: 'input'
    //       }
    //     },
    //     {
    //       label: '值',
    //       prop: 'value',
    //       showOverflowTooltip: true,
    //       align: 'left',
    //       headerAlign: 'left',
    //       isEdit: true,
    //       editConfig: {
    //         type: 'input'
    //       }
    //     }
    //   ],
    //   tableData: []
    // },
    // {
    //   label: '位置参数',
    //   name: 'posParam',
    //   formItemConfig: [],
    //   formItemRuleConfig: {},
    //   ruleForm: {},
    //   isHasTable: true,
    //   tableColumnConfig: [
    //     {
    //       label: '值',
    //       prop: 'value',
    //       showOverflowTooltip: true,
    //       align: 'left',
    //       headerAlign: 'left',
    //       isEdit: true,
    //       editConfig: {
    //         type: 'input'
    //       }
    //     }
    //   ],
    //   tableData: []
    // }
  ],
  formItemRuleConfig: {},
  formItemConfig: [],
  ruleForm: {},
  tableColumnConfig: [],
  tableData: []
};
// options

export const zjoptions = [
  {
    value: '0',
    label: '共用'
  }, {
    value: '1',
    label: '数据处理'
  }, {
    value: '2',
    label: '数据挖掘'
  }]