// import store from '../../../store.ts';
let optionsParams = {
  path: "",
  index: "",
  loglevel: "错误日志"
}
const config = {
  type: "WriteToLog",
  created: function (val) {
    optionsParams.path = val.fileName;
    optionsParams.loglevel = val.loglevel;
  },
  dialog: {
    title: "写日志",
    visible: false,
    width: '60%'
  },
  formItemRuleConfig: {
    stepName: [{
      required: true,
      message: "请输入步骤名称",
      trigger: "blur"
    }],
    spreadsheet_type: [{
      required: true,
      message: "请上传文件",
      trigger: "blur"
    }],
    encoding: [{
      required: true,
      message: "请输入编码",
      trigger: "blur"
    }]
  },
  formItemConfigDown:[
    {
      label: "文件名称字段",
      type: "input",
      valueKey: "fileName",
      span: 12
    },
    {
      label: "工作表名称字段",
      type: "input",
      valueKey: "sheetfield",
      span: 12
    },
    {
      label: "表单的行号列",
      type: "input",
      valueKey: "sheetrownumfield",
      span: 12
    },
    {
      label: "行号列",
      type: "input",
      valueKey: "rownumfield",
      span: 12
    },
    {
      label: "文件名字段",
      type: "input",
      valueKey: "shortFileFieldName",
      span: 12
    },
    {
      label: "扩展名字段",
      type: "input",
      valueKey: "extensionFieldName",
      span: 12
    },
    {
      label: "路径字段",
      type: "input",
      valueKey: "pathFieldName",
      span: 12
    },
    {
      label: "文件大小字段",
      type: "input",
      valueKey: "sizeFieldName",
      span: 12
    },
    {
      label: "是否为隐藏文件字段",
      type: "input",
      valueKey: "hiddenFieldName",
      span: 12
    },
    {
      label: "最后修改时间字段",
      type: "input",
      valueKey: "lastModificationTimeFieldName",
      span: 12
    },
    {
      label: "Uri字段",
      type: "input",
      valueKey: "uriNameFieldName",
      span: 12
    },
    {
      label: "Root uri 字段",
      type: "input",
      valueKey: "rootUriNameFieldName",
      span: 12
    },
  ],
  formItemConfig: [{
      label: "步骤名称",
      type: "input",
      valueKey: "stepName",
      span: 24
    }, {
      label: "日志级别",
      type: "select",
      valueKey: "loglevel",
      optionsConfig: {
        name: "label",
        value: "value"
      },
      options: [{
        label: "没有日志",
        value: "log_level_nothing"
      },{
        label: "错误日志",
        value: "log_level_error"
      },{
        label: "最小日志",
        value: "log_level_minimal"
      }, {
        label: "基本日志",
        value: "log_level_basic"
      }, {
        label: "详细日志",
        value: "log_level_detailed"
      }, {
        label: "调试",
        value: "log_level_debug"
      }, {
        label: "行级日志(非常详细)",
        value: "log_level_rowlevel"
      }
    ],
      watch: function (val) {
        optionsParams.spreadsheet_type = val.spreadsheet_type;
      },
      span: 24
    }, {
        label: "打印头",
        type: "select",
        valueKey: "displayHeader",
        span: 12,
        optionsConfig: {
            name: "label",
            value: "value"
        },
        options: [{
            label: "是",
            value: "Y"
            }, {
            label: "否",
            value: "N"
        }],
    },{ label: "limit rows?",
        type: "select",
        valueKey: "limitRows",
        span: 12,
        optionsConfig: {
            name: "label",
            value: "value"
        },
        options: [{
            label: "是",
            value: "Y"
            }, {
            label: "否",
            value: "N"
        }],
    },{ label: "Nr of rows to print?",
        type: "input",
        valueKey: "limitRowsNumber",
        span: 24,
        showCondition: {
            key: "limitRows",
            value: "Y"
        },
    },{ label: "写日志",
        type: "textarea",
        valueKey: "logmessage",
        span: 24
    },
  ],
  ruleForm: {
    stepName: "写日志",
    loglevel: "错误日志",
    logmessage:"",
    limitRowsNumber:"",
    limitRows:"Y",
    displayHeader:"N"
  },
  tableColumnConfig: [{
      label: "字段",
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
        options: "getParamsName",
        optionsParams: optionsParams
      }
    },
  ],
  tableData: [],
  formPreview: false,
  isShowNewBtn: true,
  isShowNewBtnInputType:true,
  isHasPreviewExcel:true,
  isHasPreviewOut:false,
  tabTableVisible:false,
  tabVisible:false,
  activeName:'first'
  // isHasGetFieldBtn: false
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
