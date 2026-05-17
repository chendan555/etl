const config = {
  type: "MergeRows",
  dialog: {
    title: "行追加",
    visible: false
  },
  formItemRuleConfig: {
    stepName: [{
      required: true,
      message: "请输入步骤名称",
      trigger: "blur"
    }, ],
    reference: [{
      required: true,
      message: "请选择旧数据源",
      trigger: "change"
    }, ],
    compare: [{
      required: true,
      message: "请选择新数据源",
      trigger: "change"
    }, ],
    flag_field: [{
      required: true,
      message: "请输入标志字段",
      trigger: "blur"
    }, ],
  },
  formItemConfig: [{
    label: "步骤名称",
    type: "input",
    valueKey: "stepName",
    span: 24
  }, {
    label: "旧数据源",
    type: "select",
    valueKey: "step1",
    span: 24,
    optionsConfig: {
      name: "name",
      value: "name"
    },
    options: 'getPreviousSteps',
  }, {
    label: "新数据源",
    type: "select",
    valueKey: "step2",
    span: 24,
    optionsConfig: {
      name: "name",
      value: "name"
    },
    options: 'getPreviousSteps',
  }, {
    label: "标志字段",
    type: "input",
    valueKey: "flag_field",
    span: 24
  }, ],
  ruleForm: {
    stepName: "行追加",
    reference: "",
    compare: "",
    flag_field: "",
    leftTableData: [],
    rightTableData: []
  },
  doubleTableConfig: {
    leftTableColumnConfig: [{
      label: "主字段",
      prop: "key",
      showOverflowTooltip: true,
      align: "left",
      headerAlign: "left",
      isEdit: true,
      editConfig: {
        type: "input"
      }
    }],
    rightTableColumnConfig: [{
      label: "比较字段",
      prop: "value",
      showOverflowTooltip: true,
      align: "left",
      headerAlign: "left",
      isEdit: true,
      editConfig: {
        type: "input"
      }
    }],
  },
  isHasTable: false,
  isHasDoubleTable: true,
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
