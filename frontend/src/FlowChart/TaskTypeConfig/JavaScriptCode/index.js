let treeParams = {
  databaseInfo: '',
  includeElement: 15,
  transName: sessionStorage.getItem("path"),
  nodeId: '',
  text: '',
  node: '',
};
let tableChartsNameParams = {
  nodeId: 'asdf',
  databaseInfo: sessionStorage.getItem('text'),
  text: sessionStorage.getItem('text'),
  includeElement: 15,
  transName: sessionStorage.getItem('path')
}
const config = {
  type: "ScriptValueMod",
  dialog: {
    title: "JavaScript代码",
    visible: false
  },
  tableColumnConfig: [
    {
      label: "字段名称",
      prop: "name",
      showOverflowTooltip: true,
      align: "left",
      headerAlign: "left",
      isEdit: true,
      editConfig: {
        type: "input"
      }
    },
    {
      label: "改名为",
      prop: "rename",
      showOverflowTooltip: true,
      align: "left",
      headerAlign: "left",
      isEdit: true,
      editConfig: {
        type: "input"
      }
    },
    {
      label: "类型",
      prop: "type",
      width: 150,
      showOverflowTooltip: true,
      align: "left",
      headerAlign: "left",
      isEdit: true,
      editConfig: {
        type: "select",
        optionsConfig: {
          name: "type",
          value: "type"
        },
        options: "getColumnType",
      }
    },
    {
      label: "长度",
      prop: "length",
      showOverflowTooltip: true,
      align: "left",
      headerAlign: "left",
      isEdit: true,
      editConfig: {
        type: "input"
      }
    },
    {
      label: "精度",
      prop: "precision",
      showOverflowTooltip: true,
      align: "left",
      headerAlign: "left",
      isEdit: true,
      editConfig: {
        type: "input"
      }
    },
    {
      label: "替换“fieldName”或“rename to”的值",
      prop: "replace",
      showOverflowTooltip: true,
      align: "left",
      headerAlign: "left",
      isEdit: true,
      editConfig: {
        type: "input"
      }
    },
  ],
  formItemRuleConfig: {
    stepName: [{
      required: true,
      message: "请输入步骤名称",
      trigger: "blur"
    }],
    getSql: [{
      required: true,
      message: "请选择字段",
      trigger: "blur"
    }]
  },
  formItemConfig: [
    {
      label: "步骤名称",
      type: "input",
      valueKey: "stepName",
      span: 24
    }, {
      label: "字段",
      type: "select",
      valueKey: "connection",
      span: 24,
      options: 'getInputOutputFields',
      optionsParams: {
        before: true
      },
      optionsConfig: {
        name: "name",
        value: "name"
      },
      watch: function (val) {
        console.log(this.ruleForm.sql);
        treeParams.databaseInfo = val.connection;
        treeParams.text = val.connection;
      }
    }
  ],
  ruleForm: {
    scriptName: 'Script 1',
    stepName: "JavaScript代码",
    connection: "",
    dataBase: "",
    dataTable: "",
    sql: "123465"
  },
  formPreview: false,
  isHasMonaco: true,
  isShowRun: true,
  // isHasTable: true,
  // handleExtensionVisible: true,
  // isHasPreview: false
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
