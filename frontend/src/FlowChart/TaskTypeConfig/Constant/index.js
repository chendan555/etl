const config = {
  type: "Constant",
  dialog: {
    title: "列增加",
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
    stepName: "列增加",
  },
  tableColumnConfig: [{
    label: "名称",
    prop: "name",
    width: 150,
    showOverflowTooltip: true,
    align: "left",
    headerAlign: "left",
    isEdit: true,
    editConfig: {
      type: "input",
    }
  }, {
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
  }, {
    label: "格式",
    prop: "format",
    width: 150,
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
      options: [{
          name: 'yyyy-MM-dd HH:mm:ss'
        },
        {
          name: 'yyyy/MM/dd HH:mm:ss'
        },
        {
          name: 'yyyy-MM-dd'
        },
        {
          name: 'yyyy/MM/dd'
        },
        {
          name: 'yyyyMMdd'
        },
        {
          name: 'yyyyMMddHHmmss'
        }
      ],
    }
  }, {
    label: "长度",
    prop: "length",
    width: 150,
    showOverflowTooltip: true,
    align: "left",
    headerAlign: "left",
    isEdit: true,
    editConfig: {
      type: "input",
    }
  }, {
    label: "精度",
    prop: "precision",
    width: 150,
    showOverflowTooltip: true,
    align: "left",
    headerAlign: "left",
    isEdit: true,
    editConfig: {
      type: "input"
    }
  }, {
    label: "当前",
    prop: "currency",
    width: 150,
    showOverflowTooltip: true,
    align: "left",
    headerAlign: "left",
    isEdit: true,
    editConfig: {
      type: "input"
    }
  }, {
    label: "十进制",
    prop: "decimal",
    width: 150,
    showOverflowTooltip: true,
    align: "left",
    headerAlign: "left",
    isEdit: true,
    editConfig: {
      type: "input"
    }
  }, {
    label: "组",
    prop: "group",
    width: 150,
    showOverflowTooltip: true,
    align: "left",
    headerAlign: "left",
    isEdit: true,
    editConfig: {
      type: "input"
    }
  }, {
    label: "值",
    prop: "nullif",
    width: 150,
    showOverflowTooltip: true,
    align: "left",
    headerAlign: "left",
    isEdit: true,
    editConfig: {
      type: "input"
    }
  }, {
    label: "设为空字符串",
    prop: "set_empty_string",
    width: 150,
    showOverflowTooltip: true,
    align: "left",
    headerAlign: "left",
    isEdit: true,
    default: "N",
    editConfig: {
      type: "select",
      optionsConfig: {
        name: "label",
        value: "value"
      },
      options: [{
        value: 'Y',
        label: '是'
      }, {
        value: 'N',
        label: '否'
      }]
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