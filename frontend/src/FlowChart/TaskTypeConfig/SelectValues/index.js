const config = {
  type: "SelectValues",
  dialog: {
    title: "字段选择",
    visible: false
  },
  formItemRuleConfig: {
    stepName: [{
      required: true,
      message: "请输入步骤名称",
      trigger: "blur"
    }],

  },
  formItemConfig: [{
    label: "步骤名称",
    type: "input",
    valueKey: "stepName",
    span: 24
  },
  // {
  //   label: "移除字段",
  //   valueKey: "remove",
  //   type: "selectMultiple",
  //   optionsConfig: {
  //     name: "name",
  //     value: "name"
  //   },
  //   options: "getInputOutputFields",
  //   optionsParams: {
  //     before: true
  //   },
  // },
  ],
  ruleForm: {
    stepName: "字段选择",
    remove: [],
  },
  tableColumnConfig: [{
    label: "字段名称",
    prop: "name",
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
      options: "getInputOutputFields",
      optionsParams: {
        before: true
      },
    }
  }, {
    label: "改名成",
    prop: "rename",
    width: 150,
    showOverflowTooltip: true,
    align: "left",
    headerAlign: "left",
    isEdit: true,
    editConfig: {
      type: "input"
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
      type: "input",
    }
  }, {
    label: "二进制转十进制",
    prop: "storage_type",
    width: 150,
    showOverflowTooltip: true,
    align: "left",
    headerAlign: "left",
    isEdit: true,
    editConfig: {
      type: "select",
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
      }]
    }
  }, {
    label: "格式",
    prop: "conversion_mask",
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
    label: "日期格式",
    prop: "date_format_lenient",
    width: 150,
    showOverflowTooltip: true,
    align: "left",
    headerAlign: "left",
    isEdit: true,
    editConfig: {
      type: "select",
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
      }]
    }
  }, {
    label: "日期区域设置",
    prop: "date_format_locale",
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
          name: "ar"
        },
        {
          name: "ar_AE"
        },
        {
          name: "ar_BH"
        },
        {
          name: "ar_DZ"
        },
      ]
    }
  }, {
    label: "日期时区",
    prop: "date_format_timezone",
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
          name: "ACT"
        },
        {
          name: "AET"
        },
        {
          name: "AGT"
        },
        {
          name: "ART"
        },
      ]
    }
  }, {
    label: "字符串转数值",
    prop: "lenient_string_to_number",
    width: 150,
    showOverflowTooltip: true,
    align: "left",
    headerAlign: "left",
    isEdit: true,
    editConfig: {
      type: "select",
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
      }]
    }
  }, {
    label: "去除空字符",
    prop: "encoding",
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
          name: "不去除字符串首位空字符"
        },
        {
          name: "去除字符串首部空字符"
        },
        {
          name: "去除字符串尾部空字符"
        },
        {
          name: "去除字符串首尾空字符"
        },
      ]
    }
  }, {
    label: "十进制",
    prop: "decimal_symbol",
    width: 150,
    showOverflowTooltip: true,
    align: "left",
    headerAlign: "left",
    isEdit: true,
    editConfig: {
      type: "input",
    }
  }, {
    label: "分组",
    prop: "grouping_symbol",
    width: 150,
    showOverflowTooltip: true,
    align: "left",
    headerAlign: "left",
    isEdit: true,
    editConfig: {
      type: "input",
    }
  }, {
    label: "货币",
    prop: "currency_symbol",
    width: 150,
    showOverflowTooltip: true,
    align: "left",
    headerAlign: "left",
    isEdit: true,
    editConfig: {
      type: "input",
    }
  }],
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
