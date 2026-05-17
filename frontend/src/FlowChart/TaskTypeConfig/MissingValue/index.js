const config = {
  type: "MissingValue",
  dialog: {
    title: "缺失值处理",
    visible: false
  },
  formItemRuleConfig: {
    stepName: [{
      required: true,
      message: "请输入步骤名称",
      trigger: "blur"
    }, ],
    type: [{
      required: true,
      message: "请选择类型",
      trigger: "change"
    }, ],
    percent: [{
      required: true,
      message: "请输入空值百分比",
      trigger: "blur"
    }, ],
    sepcify: [{
      required: true,
      message: "请输入指定值",
      trigger: "blur"
    }, ]
  },
  formItemConfig: [{
    label: "步骤名称",
    type: "input",
    valueKey: "stepName",
    span: 24
  }, {
    label: "类型",
    valueKey: "type",
    type: "select",
    optionsConfig: {
      name: "name",
      value: "value"
    },
    options: [{
      name: "最大值",
      value: 1
    }, {
      name: "最小值",
      value: 2
    }, {
      name: "平均值",
      value: 3
    }, {
      name: "中位数",
      value: 4
    }, {
      name: "按空值百分百删除列",
      value: 5
    }, {
      name: "出现频率最多替换",
      value: 6
    }, {
      name: "指定值",
      value: 7
    }, ],
  }, {
    label: "空值百分比",
    valueKey: "percent",
    type: "input",
    span: 24,
    showCondition: {
      key: "type",
      value: 5
    }
  }, {
    label: "指定值",
    valueKey: "sepcify",
    type: "input",
    span: 24,
    showCondition: {
      key: "type",
      value: 7
    }
  }],
  ruleForm: {
    stepName: "缺失值处理",
    type: "",
    percent: "",
    sepcify: ""
  },
  tableColumnConfig: [{
    label: "字段名称",
    prop: "name",
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
      options: "getInputOutputFields",
      optionsParams: {
        before: true
      },
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
