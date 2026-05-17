const config = {
  type: "EquipmentCodeTrans",
  dialog: {
    title: "设备编码转换",
    visible: false
  },
  formItemRuleConfig: {
    stepName: [{
      required: true,
      message: "请输入步骤名称",
      trigger: "blur"
    }],
    fieldFrom: [{
      required: true,
      message: "请选择需要拆分的字段",
      trigger: "change"
    }],
    ignoreFirst: [{
      required: true,
      message: "请选择是否过滤首位",
      trigger: "change"
    }],
    operateType: [{
      required: true,
      message: "请选择操作方式",
      trigger: "change"
    }],
    splitLen: [{
      required: true,
      message: "请输入间隔切分位数",
      trigger: "blur"
    }],
    type: [{
      required: true,
      message: "请选择类型",
      trigger: "change"
    }],
    posSize: [{
      required: true,
      message: "请输入填充/截取位数",
      trigger: "blur"
    }],
    value: [{
      required: true,
      message: "请输入填充值",
      trigger: "blur"
    }],
  },
  formItemConfig: [{
    label: "步骤名称",
    type: "input",
    valueKey: "stepName",
    span: 24
  }, {
    label: "需要拆分的字段",
    type: "select",
    valueKey: "fieldFrom",
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
    label: "是否过滤首位",
    type: "select",
    valueKey: "ignoreFirst",
    optionsConfig: {
      name: "label",
      value: "value"
    },
    options: [{
        label: "是",
        value: "Y"
      },
      {
        label: "否",
        value: "N"
      }
    ],
    span: 24
  }, {
    label: "操作方式",
    type: "select",
    valueKey: "operateType",
    optionsConfig: {
      name: "label",
      value: "value"
    },
    options: [{
        label: "开头",
        value: "0"
      },
      {
        label: "结尾",
        value: "1"
      }
    ],
    span: 24
  }, {
    label: "间隔切分位数",
    type: "input",
    valueKey: "splitLen",
    span: 24
  }, {
    label: "类型",
    type: "select",
    valueKey: "type",
    optionsConfig: {
      name: "label",
      value: "value"
    },
    options: [{
        label: "填充",
        value: "0"
      },
      {
        label: "截取",
        value: "1"
      }
    ],
    span: 24
  }, {
    label: "填充/截取位数",
    type: "input",
    valueKey: "posSize",
    span: 24
  }, {
    label: "填充值",
    type: "input",
    valueKey: "value",
    span: 24
  }, ],
  ruleForm: {
    stepName: "设备编码转换",
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