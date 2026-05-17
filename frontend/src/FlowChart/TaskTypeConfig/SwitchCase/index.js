const config = {
  type: "SwitchCase",
  dialog: {
    title: "分支",
    visible: false
  },
  formItemRuleConfig: {
    stepName: [{
      required: true,
      message: "请输入步骤名称",
      trigger: "blur"
    }, ],
    fieldname: [{
      required: true,
      message: "请选择要Switch的字段",
      trigger: "change"
    }, ],
    case_value_type: [{
      required: true,
      message: "请选择数据类型",
      trigger: "change"
    }, ]
  },
  formItemConfig: [{
    label: "步骤名称",
    type: "input",
    valueKey: "stepName",
    span: 24
  }, {
    label: "Switch字段",
    type: "select",
    valueKey: "fieldname",
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
    label: "默认目标步骤",
    type: "select",
    valueKey: "default_target_step",
    optionsConfig: {
      name: "name",
      value: "name"
    },
    options: 'getNextSteps',
    optionsParams: {
    },
    span: 24
  }, {
    label: "Case值数据类型",
    // type: "select",
    valueKey: "case_value_type",
    optionsConfig: {
      name: "name",
      value: "id"
    },
    type: "select",
    options: "getValueMeta",
    span: 24
  }, {
    label: "Case转换掩码",
    type: "input",
    valueKey: "case_value_format",
    span: 24
  }, {
    label: "使用字符串包含比较",
    type: "switch",
    valueKey: "use_contains",
    span: 24
  }, ],
  ruleForm: {
    stepName: "Switch/Case",
    fieldname: "",
    default_target_step: "",
    case_value_type: "",
    case_value_format: "",
    use_contains: "N"
  },
  tableColumnConfig: [{
    label: "值",
    prop: "value",
    showOverflowTooltip: true,
    align: "left",
    headerAlign: "left",
    isEdit: true,
    editConfig: {
      type: "input"
    }
  }, {
    label: "目标步骤",
    prop: "target_step",
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
      options: 'getNextSteps',
      optionsParams: {
      },
    }
  }, ],
  tableData: [],
  isHasGetFieldBtn: false,
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
