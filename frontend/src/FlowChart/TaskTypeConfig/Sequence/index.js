const config = {
  type: "Sequence",
  dialog: {
    title: "增加序列",
    visible: false
  },
  formItemRuleConfig: {
    stepName: [{
      required: true,
      message: "请输入步骤名称",
      trigger: "blur"
    }, ],
  },
  formItemConfig: [{
    label: "步骤名称",
    type: "input",
    valueKey: "stepName",
    span: 24
  }, {
    label: "值的名称",
    type: "input",
    valueKey: "valuename",
    span: 24
  }, {
    label: "使用DB来获取sequence?",
    type: "select",
    valueKey: "useDatabase",
    span: 24,
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
    }],
    watch: function (val) {
      if (val.useDatabase === "Y") {
        val.use_counter = "N";
      }
    }
  }, {
    label: "数据库连接",
    type: "select",
    valueKey: "connection",
    span: 24,
    options: 'getDatabases',
    optionsConfig: {
      name: "name",
      value: "name"
    },
    showCondition: {
      key: "useDatabase",
      value: "Y"
    }
  }, {
    label: "模式名称",
    type: "input",
    valueKey: "schema",
    span: 24,
    showCondition: {
      key: "useDatabase",
      value: "Y"
    },
  }, {
    label: "Sequence名称",
    type: "input",
    valueKey: "seqname",
    span: 24,
    showCondition: {
      key: "useDatabase",
      value: "Y"
    },
  }, {
    label: "使用计数器来计算sequence?",
    type: "select",
    valueKey: "use_counter",
    span: 24,
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
    }],
    watch: function (val) {
      if (val.use_counter === "Y") {
        val.useDatabase = "N";
      }
    }
  }, {
    label: "计算器名称",
    type: "input",
    valueKey: "counter_name",
    span: 24,
    showCondition: {
      key: "use_counter",
      value: "Y"
    },
  }, {
    label: "起始值",
    type: "input",
    valueKey: "start_at",
    span: 24,
    showCondition: {
      key: "use_counter",
      value: "Y"
    },
  }, {
    label: "增长根据",
    type: "input",
    valueKey: "increment_by",
    span: 24,
    showCondition: {
      key: "use_counter",
      value: "Y"
    },
  }, {
    label: "最大值",
    type: "input",
    valueKey: "max_value",
    span: 24,
    showCondition: {
      key: "use_counter",
      value: "Y"
    },
  }, ],
  ruleForm: {
    stepName: "",
    valuename: "",
    useDatabase: "N",
    connection: "",
    schema: "",
    seqname: "",
    use_counter: "Y",
    counter_name: "",
    start_at: "1",
    increment_by: "1",
    max_value: "999999999"
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
