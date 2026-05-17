import {
  table_column_config
}
from './table_column_config'
export const components = [{
    name: '选项卡',
    key: 'tabs',
    config: {
      name: '选项卡',
      valueKey: [],
      tabList: [{
        label: '测试1',
        name: 'cs1',
      }, {
        label: '测试2',
        name: 'cs2',
      }],
      eleList: [],
      width: '100%',
      labelWidth: 150,
      isShowFormLabel: false,
      span: 24,
      choosed: false,
      showCondition: []
    }
  },
  {
    name: '输入框',
    key: 'input',
    config: {
      name: '输入框',
      valueKey: [],
      value: '',
      width: '100%',
      labelWidth: 150,
      placeholder: '',
      isShowFormLabel: true,
      span: 24,
      choosed: false,
      events: [],
      isChooseValueKey: true,
      showCondition: [],
    }
  },
  {
    name: '文本域',
    key: 'textarea',
    config: {
      name: '文本域',
      valueKey: [],
      value: '',
      width: '100%',
      labelWidth: 150,
      isShowFormLabel: true,
      span: 24,
      choosed: false,
      events: [],
      isChooseValueKey: true,
      showCondition: []
    }
  },
  {
    name: '下拉框',
    key: 'select',
    config: {
      name: '下拉框',
      valueKey: [],
      value: '',
      isHttpGetOptions: false,
      httpUrl: "",
      params: "{}",
      options: [{
        name: '测试1',
        value: '1'
      }, {
        name: '测试2',
        value: '2'
      }],
      optionsConfig: {
        name: "name",
        value: "value",
      },
      width: '100%',
      labelWidth: 150,
      isShowFormLabel: true,
      span: 24,
      choosed: false,
      events: [],
      isChooseValueKey: true,
      showCondition: [],
      multiple: false
    }
  },
  {
    name: '复选框',
    key: 'checkbox',
    config: {
      name: '复选框',
      valueKey: [],
      value: [],
      isHttpGetOptions: false,
      httpUrl: "",
      params: "{}",
      options: [{
        name: '测试1',
        value: '1'
      }, {
        name: '测试2',
        value: '2'
      }],
      optionsConfig: {
        name: "name",
        value: "value"
      },
      width: '100%',
      labelWidth: 150,
      isShowFormLabel: true,
      span: 24,
      choosed: false,
      events: [],
      isChooseValueKey: true,
      showCondition: []
    }
  },
  {
    name: '单选框',
    key: 'radio',
    config: {
      name: '单选框',
      valueKey: [],
      value: '1',
      isHttpGetOptions: false,
      httpUrl: "",
      params: "{}",
      options: [{
        name: '测试1',
        value: '1'
      }, {
        name: '测试2',
        value: '2'
      }],
      optionsConfig: {
        name: "name",
        value: "value"
      },
      width: '100%',
      labelWidth: 150,
      isShowFormLabel: true,
      span: 24,
      choosed: false,
      events: [],
      isChooseValueKey: true,
      showCondition: []
    }
  },
  {
    name: '按钮',
    key: 'button',
    config: {
      name: '按钮',
      valueKey: [],
      value: '',
      events: [],
      width: '100%',
      labelWidth: 150,
      isShowFormLabel: false,
      span: 24,
      choosed: false,
      isChooseValueKey: true,
      showCondition: []
    }
  },
  {
    name: '文件上传',
    key: 'inputFile',
    config: {
      name: '文件上传',
      valueKey: [],
      value: '',
      events: [],
      width: '100%',
      labelWidth: 150,
      isShowFormLabel: true,
      span: 24,
      choosed: false,
      isChooseValueKey: true,
      showCondition: []
    }
  },
  {
    name: '代码编辑',
    key: 'code',
    config: {
      name: '代码编辑',
      valueKey: [],
      value: '',
      width: '100%',
      labelWidth: 150,
      isShowFormLabel: false,
      span: 24,
      choosed: false,
      events: [],
      isChooseValueKey: true,
      showCondition: []
    }
  },
  {
    name: '公式编辑',
    key: 'formula',
    config: {
      name: '公式编辑',
      valueKey: [],
      value: '',
      width: '100%',
      labelWidth: 150,
      isShowFormLabel: false,
      span: 24,
      choosed: false,
      events: [],
      isChooseValueKey: true,
      showCondition: []
    }
  },
  {
    name: '开关',
    key: 'switch',
    config: {
      name: '开关',
      valueKey: [],
      value: false,
      width: '100%',
      labelWidth: 150,
      isShowFormLabel: true,
      span: 24,
      choosed: false,
      events: [],
      isChooseValueKey: true,
      showCondition: []
    }
  },
  {
    name: '表格',
    key: 'table',
    config: {
      name: '表格',
      valueKey: [],
      value: [],
      tableColumnConfig: [table_column_config],
      width: '100%',
      labelWidth: 150,
      isShowFormLabel: false,
      isShowOperation: true,
      span: 24,
      choosed: false,
      events: [],
      isChooseValueKey: true,
      showCondition: []
    }
  },
  {
    name: '日期选择',
    key: 'date',
    config: {
      name: '日期选择',
      valueKey: [],
      value: '',
      isShowFormLabel: true,
      width: '100%',
      labelWidth: 150,
      span: 24,
      choosed: false,
      events: [],
      isChooseValueKey: true,
      showCondition: []
    }
  },
  {
    name: '树',
    key: 'tree',
    config: {
      name: '树',
      valueKey: [],
      value: '',
      treeData: [{
          label: '一级 1',
          children: [{
            label: '二级 1-1',
            children: []
          }]
        },
        {
          label: '一级 2',
          children: []
        }
      ],
      optionsConfig: {
        children: 'children',
        label: 'label'
      },
      isHttpGetTreeData: false,
      httpUrl: "",
      params: "{}",
      isLazyLoad: false,
      isShowFormLabel: false,
      width: '100%',
      labelWidth: 150,
      span: 24,
      choosed: false,
      events: [],
      isChooseValueKey: true,
      showCondition: []
    }
  },
  {
    name: '下拉树',
    key: 'selectTree',
    config: {
      name: '下拉树',
      valueKey: [],
      value: '',
      disabled: false,
      placeholder: '',
      filterable: true,
      treeData: [{
          label: '一级 1',
          children: [{
            label: '二级 1-1',
            children: []
          }]
        },
        {
          label: '一级 2',
          children: []
        }
      ],
      optionsConfig: {
        children: 'children',
        label: 'label',
      },
      isHttpGetTreeData: false,
      httpUrl: "",
      params: "{}",
      isLazyLoad: false,
      width: '100%',
      labelWidth: 150,
      isShowFormLabel: true,
      span: 24,
      choosed: false,
      events: [],
      isChooseValueKey: true,
      showCondition: []
    }
  }, {
    name: '条件树',
    key: 'sqlConditionTree',
    config: {
      name: '条件树',
      valueKey: [],
      value: [{
        id: 1,
        operator: 0,
        negate: false,
        left_valuename: '',
        func: '',
        right_valuename: '',
        value: '',
      }],
      treeData: [],
      params: "{}",
      isShowFormLabel: false,
      width: '100%',
      labelWidth: 150,
      span: 24,
      choosed: false,
      events: [],
      isChooseValueKey: true,
      showCondition: []
    }
  },
]

export const customComponents = [{
  name: '步骤名称',
  key: 'input',
  config: {
    name: '步骤名称',
    valueKey: 'stepName',
    value: '',
    width: '100%',
    labelWidth: 150,
    isShowFormLabel: true,
    span: 24,
    choosed: false,
    events: [],
    isChooseValueKey: false,
    showCondition: [],
  }
}, ]
