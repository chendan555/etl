/* eslint-disable */
const config = {
  type: "MongoDBInput",
  dialog: {
    title: "MongoDB输入",
    visible: false
  },
  formItemRuleConfig: {
    stepName: [{
      required: true,
      message: "请输入步骤名称",
      trigger: "blur"
    }],
    hostname: [{
      required: true,
      message: "请输入连接地址",
      trigger: "blur"
    }],
    port: [{
      required: true,
      message: "请输入连接端口",
      trigger: "blur"
    }],
    db_name: [{
      required: true,
      message: "请输入数据源名称",
      trigger: "blur"
    }],
    collection: [{
      required: true,
      message: "请输入集合",
      trigger: "blur"
    }],
    auth_user: [{
      required: true,
      message: "请输入用户名",
      trigger: "blur"
    }],
    auth_password: [{
      required: true,
      message: "请输入密码",
      trigger: "blur"
    }],
    auth_database: [{
      required: true,
      message: "请输入数据库名称",
      trigger: "blur"
    }]
  },
  formItemConfig: [{
    label: "步骤名称",
    type: "input",
    valueKey: "stepName",
    span: 24
  }, {
    label: "连接地址",
    type: "input",
    valueKey: "hostname",
  }, {
    label: "连接端口",
    type: "input",
    valueKey: "port",
  },{
    label: "数据源",
    type: "input",
    valueKey: "db_name",
    span: 24
  },{
    label: "集合",
    type: "input",
    valueKey: "collection",
    span: 24
  }, {
    label: "数据库名称",
    type: "input",
    valueKey: "auth_database"
  },
  {
    label: "用户名",
    type: "input",
    valueKey: "auth_user"
  },
  {
    label: "密码",
    type: "input",
    inputType: 'password',
    valueKey: "auth_password"
  }],
  ruleForm: {
    stepName: "MongoDB输入"
  },
  isHasTable: false,
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
