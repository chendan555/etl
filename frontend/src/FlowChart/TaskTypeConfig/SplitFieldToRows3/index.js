const config = {
  type: "SplitFieldToRows3",
  dialog: {
    title: "列拆多行",
    visible: false
  },
  formItemRuleConfig: {
    stepName: [{
      required: true,
      message: "请输入步骤名称名称",
      trigger: "blur"
    }, ],
    splitfield: [{
      required: true,
      message: "请输入拆分的字段",
      trigger: "change"
    }, ],
    delimiter: [{
      required: true,
      message: "请输入分隔符",
      trigger: "blur"
    }, ],
  },
  formItemConfig: [{
    label: "步骤名称",
    type: "input",
    valueKey: "stepName",
    span: 24
  }, {
    label: "拆分的字段",
    type: "select",
    valueKey: "splitfield",
    optionsConfig: {
      name: "name",
      value: "name"
    },
    options: "getInputOutputFields",
    optionsParams: {
      before: true
    },
    span: 24
  }, {
    label: "分隔符",
    type: "input",
    valueKey: "delimiter",
    span: 24
  }, {
    label: "分隔符为正则",
    valueKey: "delimiter_is_regex",
    span: 24,
    type: "select",
    optionsConfig: {
      name: "label",
      value: "value"
    },
    options: [{
      value: "Y",
      label: '是'
    }, {
      value: "N",
      label: '否'
    }]
  }, {
    label: "新字段名称",
    type: "input",
    valueKey: "newfield",
    span: 24
  }, {
    label: "输出是否包含行号",
    valueKey: "rownum",
    span: 24,
    type: "select",
    optionsConfig: {
      name: "label",
      value: "value"
    },
    options: [{
      value: "Y",
      label: '是'
    }, {
      value: "N",
      label: '否'
    }]
  }, {
    label: "行号字段",
    type: "input",
    valueKey: "rownum_field",
    showCondition: {
      key: "rownum",
      value: "Y"
    },
    span: 24
  }, {
    label: "是否重置接收行号",
    valueKey: "resetrownumber",
    span: 24,
    type: "select",
    optionsConfig: {
      name: "label",
      value: "value"
    },
    options: [{
      value: "Y",
      label: '是'
    }, {
      value: "N",
      label: '否'
    }]
  }, ],
  ruleForm: {
    stepName: "列拆多行",
    splitfield: "",
    delimiter: "",
    newfield: "",
    rownum: "",
    rownum_field: "",
    resetrownumber: "",
    delimiter_is_regex: ""
  },
  formPreview: false,
  isHasTable: false
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
