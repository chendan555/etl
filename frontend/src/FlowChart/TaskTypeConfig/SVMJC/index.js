const config = {
  type: "SVMJC",
  dialog: {
    title: "SVM（集成）",
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
    label: "阈值",
    type: "inputnumber",
    valueKey: "SVMLCValue",
    span: 24,
    inputLength: {
      min: 1,
      max: 20
    }
  }, {
    label: "L1正则化权重",
    type: "inputnumber",
    valueKey: "weight",
    span: 24,
    inputLength: {
      min: 0.1,
      max: 1
    }
  }, {
    label: "训练循环次数",
    type: "inputnumber",
    valueKey: "trainTime",
    span: 24,
    inputLength: {
      min: 1,
      max: 10
    }
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
    stepName: "SVM（集成）",
    head_name: "",
    dataProportion: [1, 10],
    SVMLCValue: 1,
    weight: 1,
    trainTime: 10,
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
