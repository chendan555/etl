const config = {
  type: "TimeFormat",
  dialog: {
    title: "时间格式转换",
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
  }, ],
  ruleForm: {
    stepName: "时间格式转换",
  },
  tableColumnConfig: [{
    label: "数据字段",
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
  }, {
    label: "类型",
    prop: "type",
    showOverflowTooltip: true,
    align: "left",
    headerAlign: "left",
    isEdit: true,
    editConfig: {
      type: "select",
      optionsConfig: {
        name: "name",
        value: "value"
      },
      options: [
        { name: "yyyy/MM/dd HH:mm:ss.SSS", value: 0 }, 
        { name: "yyyy/MM/dd HH:mm:ss.SSS XXX", value: 1 }, 
        { name: "yyyy/MM/dd HH:mm:ss", value: 2 }, 
        { name: "yyyy/MM/dd HH:mm:ss XXX", value: 3 }, 
        { name: "yyyyMMddHHmmss", value: 4 }, 
        { name: "yyyy/MM/dd", value: 5 }, 
        { name: "yyyy-MM-dd", value: 6 },
        { name: "yyyy-MM-dd HH:mm:ss", value: 7 },
        { name: "yyyy-MM-dd HH:mm:ss XXX", value: 8 },
        { name: "yyyyMMdd", value: 9 },
        { name: "MM/dd/yyyy", value: 10 },
        { name: "MM/dd/yyyy HH:mm:ss", value: 11 },
        { name: "MM-dd-yyyy", value: 12 },
        { name: "MM-dd-yyyy HH:mm:ss", value: 13 },
        { name: "MM/dd/yy", value: 14 },
        { name: "MM-dd-yy", value: 15 },
        { name: "dd-MM-yyyy", value: 16 },
        { name: "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", value: 17 },
      ], 
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
