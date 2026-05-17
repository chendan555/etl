const tableColumnConfig = [
  {
    label: '名称',
    prop: 'name'
  },
  {
    label: '解析表单类型',
    prop: 'formName'
  },
  {
    label: '创建时间',
    prop: 'createTime'
  },
  {
    label: '备注',
    prop: 'remarks'
  }
];

const formConfig = {
  ruleForm: {
    name: '',
    formName: '',
    createTime: '',
    remarks: '',
    formModel: <any>[],
    allConfig: <any>{}
  },
  formItemConfig: [
    {
      label: '名称',
      valueKey: 'name',
      type: 'input',
      labelWidth: '100px',
      span: 12
    },
    // {
    //   label: '解析表单类型',
    //   valueKey: 'formName',
    //   type: 'input',
    //   labelWidth: '100px'
    // },
    {
      label: '备注',
      valueKey: 'remarks',
      type: 'textarea',
      labelWidth: '100px',
      span: 12
    }
  ]
};

const handleList = [
  // {
  //   name: '配置',
  //   icon: 'setting',
  //   handle: 'setting'
  // },
  {
    name: '编辑',
    icon: 'edit',
    handle: 'edit'
  },
  {
    name: '删除',
    icon: 'delete',
    handle: 'delete'
  }
];

export default {
  tableColumnConfig,
  formConfig,
  handleList
};
