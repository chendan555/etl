const config = {
  type: "FilterRows",
  dialog: {
    title: "T/F过滤",
    visible: false
  },
  formItemRuleConfig: {
    stepName: [{
      required: true,
      message: "请输入步骤名称",
      trigger: "blur"
    }, ]
  },
  formItemConfig: [{
    label: "步骤名称",
    type: "input",
    valueKey: "stepName",
    span: 24
  }, {
    label: "步骤True",
    valueKey: "send_true_to",
    span: 24,
    type: "select",
    optionsConfig: {
      name: "code",
      value: "name"
    },
    options: 'getNextSteps',
  }, {
    label: "步骤False",
    // type: "input",
    valueKey: "send_false_to",
    span: 24,
    type: "select",
    optionsConfig: {
      name: "code",
      value: "name"
    },
    options: 'getNextSteps',
  }, ],
  ruleForm: {
    stepName: "T/F过滤",
    send_true_to: "",
    send_false_to: "",
    sqlConditionTree: [{
      id: 1,
      operator: 0,
      negate: [],
      left_valuename: "",
      func: "",
      right_valuename: "",
      value: ""
    }]
  },
  tableColumnConfig: [],
  tableData: [],
  isHasTable: false,
  formPreview: false,
  isHasSqlConditionTree: true
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
