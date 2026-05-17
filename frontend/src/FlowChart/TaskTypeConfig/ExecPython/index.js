let treeParams = {
  databaseInfo: '',
  includeElement: 15,
  transName: sessionStorage.getItem("path"),
  nodeId: '',
  text: '',
  node: '',
};

const config = {
  type: "PythonFunction",
  dialog: {
    title: "Python脚本",
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
      label: "数据库连接",
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
      label: "执行每一行",
      type: "radio",
      valueKey: "executedEachInputRow",
      nameKey: "executedEachInputRow",
      span: 24,
      options: [{
          name: '是',
          value: 'Y'
        },
        {
          name: '否',
          value: 'N'
        }
      ],
      optionsConfig: {
        name: 'name',
        value: 'value'
      }
    },
    {
      label: "SingleStatement",
      type: "radio",
      valueKey: "singleStatement",
      nameKey: "singleStatement",
      span: 24,
      options: [{
          name: '是',
          value: 'Y'
        },
        {
          name: '否',
          value: 'N'
        }
      ],
      optionsConfig: {
        name: 'name',
        value: 'value'
      }
    },
    {
      label: "变量替换",
      type: "radio",
      valueKey: "replaceVariables",
      nameKey: "replaceVariables",
      span: 24,
      options: [{
          name: '是',
          value: 'Y'
        },
        {
          name: '否',
          value: 'N'
        }
      ],
      optionsConfig: {
        name: 'name',
        value: 'value'
      }
    },
    {
      label: "SetParams",
      type: "radio",
      valueKey: "setParams",
      nameKey: "setParams",
      span: 24,
      options: [{
          name: '是',
          value: 'Y'
        },
        {
          name: '否',
          value: 'N'
        }
      ],
      optionsConfig: {
        name: 'name',
        value: 'value'
      }
    },
    {
      label: "QuoteString",
      type: "radio",
      valueKey: "quoteString",
      nameKey: "quoteString",
      span: 24,
      options: [{
          name: '是',
          value: 'Y'
        },
        {
          name: '否',
          value: 'N'
        }
      ],
      optionsConfig: {
        name: 'name',
        value: 'value'
      }
    },
    {
      label: "包含插入状态的字段",
      type: "input",
      valueKey: "insert_field",
      nameKey: "insert_field",
      span: 24
    },
    {
      label: "包含更新状态的字段",
      type: "input",
      valueKey: "update_field",
      nameKey: "update_field",
      span: 24
    },
    {
      label: "包含删除状态的字段",
      type: "input",
      valueKey: "delete_field",
      nameKey: "delete_field",
      span: 24
    },
    {
      label: "包含读状态的字段",
      type: "input",
      valueKey: "read_field",
      nameKey: "read_field",
      span: 24
    },
  ],
  ruleForm: {
    stepName: "Python脚本",
    connection: "",
    executedEachInputRow: "",
    SingleStatement: "",
    replaceVariables: "",
    setParams: "",
    quoteString: "",
    insert_field: "",
    update_field: "",
    delete_field: "",
    read_field: "",
  },
  tableColumnConfig: [{
    label: "作为参数的字段",
    prop: "name",
    showOverflowTooltip: true,
    align: "left",
    headerAlign: "left",
    isEdit: true,
    editConfig: {
      type: "input",
      optionsConfig: {
        name: "name",
        value: "name"
      },
      options: "getPreviousSteps",
      optionsParams: {
        before: true
      },
    },
  }],
  formPreview: false,
  isHasMonaco: true,
  isHasTable: true,
  isHasGetFieldBtn: false,
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
