const config = {
  type: "ValueMapper",
  dialog: {
    title: "值映射",
    visible: false
  },
  formItemRuleConfig: {
    stepName: [{
      required: true,
      message: "请输入步骤名称",
      trigger: "blur"
    }],
    field_to_use: [{
      required: true,
      message: "请选择调整字段",
      trigger: "blur"
    }],
    tzfs: [{
      required: true,
      message: "请选择调整方式",
      trigger: "change"
    }],
  },
  formItemConfig: [{
      label: "步骤名称",
      type: "input",
      valueKey: "stepName",
      span: 24
    },
    {
      label: "调整字段",
      type: "select",
      valueKey: "field_to_use",
      span: 24,
      optionsConfig: {
        name: "name",
        value: "name"
      },
      options: "getInputOutputFields",
      optionsParams: {
        before: true
      },
    },
    // {
    //   label: "调整方式",
    //   type: "radio",
    //   valueKey: "tzfs",
    //   span: 24,
    //   optionsConfig: {
    //     name: "label",
    //     value: "value"
    //   },
    //   options: [{
    //       value: "1",
    //       label: "原字段上修改"
    //     },
    //     {
    //       value: "2",
    //       label: "生成新字段"
    //     }
    //   ]
    // },
    {
      label: "新字段名称",
      type: "input",
      valueKey: "target_field",
      // showCondition: {
      //   key: "tzfs",
      //   value: "2"
      // },
      span: 24
    },
    {
      label: "不匹配时默认值",
      type: "input",
      valueKey: "non_match_default",
      span: 24
    },
  ],
  ruleForm: {
    stepName: "值映射",
    field_to_use: "",
    tzfs: "1",
    target_field: "",
    non_match_default: ""
  },
  tableColumnConfig: [{
    label: "源值",
    prop: "source_value",
    showOverflowTooltip: true,
    align: "left",
    headerAlign: "left",
    isEdit: true,
    editConfig: {
      type: "input"
    }
  }, {
    label: "目标值",
    prop: "target_value",
    showOverflowTooltip: true,
    align: "left",
    headerAlign: "left",
    isEdit: true,
    editConfig: {
      type: "input"
    }
  }, ],
  tableData: [],
  formPreview: false,
  isHasGetFieldBtn: false
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
