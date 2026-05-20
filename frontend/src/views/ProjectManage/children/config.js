// 流程分组信息
export const projInforLabel = [
  {
    label: '项目名称',
    prop: 'taskGroupName',
    value: ''
  },
  {
    label: '流程总数',
    prop: 'countTrans',
    value: ''
  },
  {
    label: '创建时间',
    prop: 'createDate',
    value: ''
  },
  {
    label: '更新时间',
    prop: 'updateDate',
    value: ''
  },
  {
    label: '描述',
    prop: 'taskDesc',
    value: ''
  }
];

// 流程分组根目录统计信息
export const groupInforLabel = [
  {
    label: '项目名称',
    prop: 'taskGroupName',
    value: ''
  },
  {
    label: '流程总数',
    prop: 'countTrans',
    value: ''
  }
];

export const procInforLabel = [
  {
    label: '流程名称',
    prop: 'name',
    value: ''
  },
  {
    label: '创建时间',
    prop: 'createTime',
    value: ''
  },
  {
    label: '修改时间',
    prop: 'updateTime',
    value: ''
  },
  {
    label: '描述',
    prop: 'describe',
    value: ''
  }
];

export const detailInforLabel = [
  {
    label: '流程名称',
    prop: 'name',
    value: ''
  },
  {
    label: '所属分组',
    prop: 'belongToTaskGroup',
    value: ''
  },
  {
    label: '数据源',
    prop: 'dataBaseName',
    value: ''
  },
  {
    label: '创建人',
    prop: 'createBy',
    value: ''
  },
  {
    label: '创建时间',
    prop: 'createDate',
    value: ''
  },
  {
    label: '描述',
    prop: 'describe',
    value: ''
  }
];

export const dataSource = {
  isShowOperation: true,
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
  isShowOperation: true,
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
// form表单
export const copyRules = {
  name: [
    {
      required: true,
      message: '请输入流程名称',
      trigger: 'blur'
    }
  ],
  secretLevel: [
    {
      required: true,
      message: '请选择流程分组',
      trigger: 'blur'
    }
  ]
};

export const mergeTxtFormConfig = {
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
};
