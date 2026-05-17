let treeParams = {
  databaseInfo: '',
  includeElement: 15,
  transName: sessionStorage.getItem("path"),
  nodeId: '',
  text: '',
  node: '',
};

const config = {
  type: "TableInput",
  dialog: {
    title: "表输入",
    visible: false
  },
  formItemRuleConfig: {
    stepName: [{
      required: true,
      message: "请输入步骤名称",
      trigger: "blur"
    }],
    getSql: [{
      required: true,
      message: "请选择数据表",
      trigger: "blur"
    }],
    connection: [{
      required: true,
      message: "请选择数据源",
      trigger: ["blur", "change", "focus"]
    }]
  },
  formItemConfig: [{
      label: "步骤名称",
      type: "input",
      valueKey: "stepName",
      span: 24
    },
    {
      label: "数据源",
      type: "select",
      valueKey: "connection",
      span: 24,
      options: 'getDatabases',
      optionsConfig: {
        name: "name",
        value: "name"
      },
      watch: function (val) {
        treeParams.databaseInfo = val.connection;
        treeParams.text = val.connection;
      }
    },
    {
      label: "数据表",
      type: "inputButtonTree",
      valueKey: "dataBase",
      nameKey: "dataTable",
      treeConfig: {
        title: "数据库浏览",
        propsApi: {
          api: 'getExplorer',
          params: treeParams,
        },
        nodeKey: 'id',
        lazy: true,
        filterable: true,
        loadParentId: 'nodeId',
        valueBy: "nodeId",
        props: {
          label: "text",
          children: "children",
          disabled: false
        }
      },
      span: 24,
      watch: function (val) {
        treeParams.databaseInfo = val.connection;
        treeParams.text = val.connection;
      }
      // width: "260px"
    },
  ],
  ruleForm: {
    stepName: "表输入",
    connection: '',
    dataBase: "",
    dataTable: "",
    sql: ""
  },
  formPreview: false,
  isHasMonaco: true,
  isHasTable: false,
  isHasPreview: true
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
