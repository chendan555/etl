const config = {
  type: "TopNJC",
  // type: "LogicRegressionJC",
  dialog: {
    title: "逻辑回归（集成）",
    visible: false
  },
  formItemRuleConfig: {
    // stepName: [{
    //   required: true,
    //   message: "请输入步骤名称",
    //   trigger: "blur"
    // }],
    // head_name: [{
    //   required: true,
    //   message: "请选择前一个输入",
    //   trigger: "change"
    // }, ],
    // tail_name: [{
    //   required: true,
    //   message: "请选择后一个输入",
    //   trigger: "change"
    // }, ],
  },
  formItemConfig: [{
    label: "步骤名称",
    type: "input",
    valueKey: "stepName",
    span: 24
  }, {
    label: "label",
    type: "select",
    valueKey: "head_name",
    span: 24,
    optionsConfig: {
      name: "name",
      value: "name"
    },
    // options: 'getPreviousSteps',
    options: [{
      name: '1'
    }, {
      name: '2'
    }, {
      name: '3'
    }],
  }, {
    label: "数据拆分比例",
    type: "numberrange",
    valueKey: "dataProportion",
    span: 24,
    inputType: 'number'
  }, {
    label: "容差阈值",
    type: "inputnumber",
    valueKey: "toleranceValue",
    span: 24,
    inputLength: {
      min: 0.01,
      max: 0.1
    }
  }, {
    label: "正则化",
    type: "radio",
    valueKey: "regularization",
    span: 24,
    optionsConfig: {
      name: 'name',
      value: 'name'
    },
    options: [{
      name: 'L1'
    }, {
      name: 'L2'
    }]
  }, {
    label: "训练数据分组",
    type: "inputnumber",
    valueKey: "dataGroup",
    span: 24,
    inputLength: {
      min: 1,
      max: 10
    }
  }, {
    label: "λ值范围",
    type: "numberrange",
    valueKey: "lmdValue",
    span: 24,
    inputType: 'number'
  }, {
    label: "迭代步长",
    type: "inputnumber",
    valueKey: "iterateStep",
    span: 24,
    inputLength: {
      min: 1,
      max: 10
    }
  }],
  ruleForm: {
    stepName: "逻辑回归（集成）",
    head_name: "",
    dataProportion: [1, 10],
    toleranceValue: 0.01,
    regularization: 1,
    dataGroup: 10,
    lmdValue: [1, 10],
    iterateStep: 1
  },
  isHasTable: false,
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
