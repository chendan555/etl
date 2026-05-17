let optionsParams = {
  stepName: "",
  before: false
}

const config = {
  type: "MultiwayMergeJoin",
  created: function (val) {
    optionsParams.stepName = val.stepName;
  },
  dialog: {
    title: "多表格连接",
    visible: false
  },
  formItemRuleConfig: {
    stepName: [{
      required: true,
      message: "请输入步骤名称",
      trigger: "blur"
    }],
  },
  formItemConfig: [{
    label: "步骤名称",
    type: "input",
    valueKey: "stepName",
    span: 24
  }, {
    label: "连接方式",
    type: "select",
    valueKey: "join_type",
    span: 24,
    optionsConfig: {
      name: "label",
      value: "value"
    },
    options: [{
      value: "INNER",
      label: 'INNER'
    }, {
      value: "FULL OUTER",
      label: 'FULL OUTER'
    }]
  }],
  ruleForm: {
    stepName: "多表格连接",
  },
  tableColumnConfig: [{
    label: "数据源",
    prop: "stepnames",
    showOverflowTooltip: true,
    align: "left",
    headerAlign: "left",
    isEdit: true,
    editConfig: {
      type: "select",
      optionsConfig: {
        name: "name",
        value: "name"
      },
      options: "getPreviousSteps",
      optionsParams: {
        before: true
      },
    },
  }, {
    label: "keys",
    prop: "keys",
    showOverflowTooltip: true,
    align: "left",
    headerAlign: "left",
    isEdit: true,
    editConfig: {
      type: "selectMultiple",
      optionsConfig: {
        name: "name",
        value: "name"
      },
      options: "getIputOutputFieldsName",
      optionsParams: optionsParams
    },
    default: []
  }],
  tableData: [],
  formPreview: false,
  isHasGetFieldBtn: true
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
