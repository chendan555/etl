const config = {
  type: "DatabaseArray",
  dialog: {
    title: "数据源集合",
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
    sjy: [{
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
      valueKey: "sjy",
      span: 24,
      options: 'getDatabases',
      optionsConfig: {
        name: "name",
        value: "hostName"
      }
    },
    {
      label: "获取sql查询语句",
      type: "inputButtonTree",
      valueKey: "getSql",
      nameKey: "getSqlName",
      treeConfig: {
        propsApi: {
          api: 'getExplorer',
          params: {
            databaseInfo: '本地',
            includeElement: 15,
            transName: 'test1',
            nodeId: '',
            text: '本地',
            node: '9e9a6b6b09a246cd9de270131b1541d3',
          },
        },
        lazy: true,
        loadParentId: 'nodeId',
        valueBy: "nodeId",
        props: {
          label: "text",
          children: "children",
          disabled: false
        }
      },
      span: 24
      // width: "260px"
    },
  ],
  ruleForm: {
    stepName: "数据源集合",
    sjy: "",
    getSql: "",
    getSqlName: "",
    sql: ""
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