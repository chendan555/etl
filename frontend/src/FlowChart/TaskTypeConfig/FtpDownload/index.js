const config = {
  type: "FtpDownload",
  dialog: {
    title: "Ftp下载",
    visible: false
  },
  formItemRuleConfig: {
    stepName: [{
      required: true,
      message: "请输入步骤名称",
      trigger: "blur"
    }],
    serverName: [{
      required: true,
      message: "请输入主机名称",
      trigger: "blur"
    }],
    serverPort: [{
      required: true,
      message: "请输入端口号",
      trigger: "blur"
    }],
    username: [{
      required: true,
      message: "请输入用户名",
      trigger: "blur"
    }],
    password: [{
      required: true,
      message: "请输入密码",
      trigger: "blur"
    }],
    ftpdirectory: [{
      required: true,
      message: "请输入远程目录",
      trigger: "blur"
    }],
    targetdirectory: [{
      required: true,
      message: "请输入目标目录",
      trigger: "blur"
    }],
  },
  formItemConfig: [{
    label: "步骤名称",
    type: "input",
    valueKey: "stepName",
    span: 24
  }, {
    label: "主机",
    type: "input",
    valueKey: "serverName",
    span: 24
  }, {
    label: "端口",
    type: "input",
    valueKey: "serverPort",
    span: 24
  }, {
    label: "用户名",
    type: "input",
    valueKey: "username",
    span: 24
  }, {
    label: "密码",
    type: "input",
    valueKey: "password",
    span: 24
  }, {
    label: "远程目录",
    type: "input",
    valueKey: "ftpdirectory",
    span: 24
  }, {
    label: "通配符",
    type: "input",
    valueKey: "wildcard",
    span: 24
  }, {
    label: "目标目录",
    type: "input",
    valueKey: "targetdirectory",
    span: 24
  }, {
    label: "控制编码",
    type: "select",
    valueKey: "control_encoding",
    span: 24,
    optionsConfig: {
      name: "label",
      value: "value"
    },
    options: [{
      value: 'ISO-8859-1',
      label: 'ISO-8859-1'
    }, {
      value: 'US-ASCII',
      label: 'US-ASCII'
    }, {
      value: 'UTF-8',
      label: 'UTF-8'
    }, {
      value: 'UTF-16BE',
      label: 'UTF-16BE'
    }, {
      value: 'UTF-16LE',
      label: 'UTF-16LE'
    }, {
      value: 'UTF-16',
      label: 'UTF-16'
    }]
  }, ],
  ruleForm: {
    stepName: "Ftp下载",
    serverName: "",
    serverPort: "21",
    username: "",
    password: "",
    ftpdirectory: "",
    wildcard: "",
    control_encoding: "UTF-8",
    targetdirectory: ""
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
