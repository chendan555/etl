const config = {
  type: "DoubleTableLink",
  dialog: {
    title: "双表格链接",
    visible: false
  },
  formItemRuleConfig: {
    stepName: [{
      required: true,
      message: "请输入步骤名称",
      trigger: "blur"
    }, ],
    table1: [{
      required: true,
      message: "请输入表1名称",
      trigger: "blur"
    }, ],
    table2: [{
      required: true,
      message: "请输入表2名称",
      trigger: "blur"
    }, ],
    ljlx: [{
      required: true,
      message: "请输入标志字段",
      trigger: "change"
    }, ],
  },
  formItemConfig: [{
    label: "步骤名称",
    type: "input",
    valueKey: "stepName",
    span: 24
  }, {
    label: "表1",
    type: "input",
    valueKey: "table1",
    span: 24,
    disabled: true
  }, {
    label: "表2",
    type: "input",
    valueKey: "table2",
    span: 24,
    disabled: true
  }, {
    label: "连接类型",
    type: "select",
    valueKey: "ljlx",
    span: 24,
    optionsConfig: {
      name: "label",
      value: "value"
    },
    options: [{
      value: '1',
      label: 'INNER'
    }, {
      value: '2',
      label: 'LEFT OUTER'
    }, {
      value: '3',
      label: 'RIGHT OUTER'
    }, {
      value: '4',
      label: 'FULL OUTER'
    }, ]
  }, ],
  ruleForm: {
    stepName: "双表格链接",
    table1: "",
    table2: "",
    ljlx: ""
  },
  tableColumnConfig: [{
    label: "表1字段",
    prop: "b1zd",
    showOverflowTooltip: true,
    align: "left",
    headerAlign: "left",
    isEdit: true,
    editConfig: {
      type: "select",
      optionsConfig: {
        name: "label",
        value: "value"
      },
      options: [{
        value: '1',
        label: 'name'
      }, {
        value: '2',
        label: 'source'
      }]
    }
  }, {
    label: "表2字段",
    prop: "b2zd",
    showOverflowTooltip: true,
    align: "left",
    headerAlign: "left",
    isEdit: true,
    editConfig: {
      type: "select",
      optionsConfig: {
        name: "label",
        value: "value"
      },
      options: [{
        value: '1',
        label: 'name'
      }, {
        value: '2',
        label: 'source'
      }]
    }
  }, ],
  tableData: [],
  formPreview: false
}

export default config;
/**
 *
 * Create by Leo
 * 流程基础算子组件
 * 用于数据处理流程搭建
 * 构建单个数据处理功能单元
 * 连线形成线性处理线程
 * 根据组件功能构成不同算子组件
 * 可对组件进行功能配置
 * 实现数据处理功能
 * 并减少一定的代码量
 * 统一设置弹框的配置项、确认按钮、取消按钮、关闭按钮等信息
 * 所有组件以弹框形式展示
 * 流程基础算子组件
 * 用于数据处理流程搭建
 * 构建单个数据处理功能单元
 * 连线形成线性处理线程
 * 根据组件功能构成不同算子组件
 * 可对组件进行功能配置
 * 实现数据处理功能
 * 并减少一定的代码量
 * 统一设置弹框的配置项、确认按钮、取消按钮、关闭按钮等信息
 * 所有组件以弹框形式展示
 *
 */
/**
 *
 * Create by Leo
 * 流程基础算子组件
 * 用于数据处理流程搭建
 * 构建单个数据处理功能单元
 * 连线形成线性处理线程
 * 根据组件功能构成不同算子组件
 * 可对组件进行功能配置
 * 实现数据处理功能
 * 并减少一定的代码量
 * 统一设置弹框的配置项、确认按钮、取消按钮、关闭按钮等信息
 * 所有组件以弹框形式展示
 * 流程基础算子组件
 * 用于数据处理流程搭建
 * 构建单个数据处理功能单元
 * 连线形成线性处理线程
 * 根据组件功能构成不同算子组件
 * 可对组件进行功能配置
 * 实现数据处理功能
 * 并减少一定的代码量
 * 统一设置弹框的配置项、确认按钮、取消按钮、关闭按钮等信息
 * 所有组件以弹框形式展示
 *
 */