let optionsParams = {
  before: true,
  columnStr: '',
  fulfillType: 0
}
const FIELDStableColumnConfig = [{
    label: '字段',
    prop: 'name',
    showOverflowTooltip: true,
    align: 'left',
    headerAlign: 'left',
    isEdit: true,
    editConfig: {
      type: 'select',
      optionsConfig: {
        name: 'code',
        value: 'name'
      },
      options: 'getInputOutputFields',
      optionsParams: {
        before: true,
      }
    },
    watch: function (val) {
      optionsParams.columnStr = val.name;
      optionsParams.fulfillType = val.fulfillType || 0;
    }
  },
  {
    label: '补全方式',
    prop: 'fulfillType',
    showOverflowTooltip: true,
    align: 'left',
    headerAlign: 'left',
    isEdit: true,
    editConfig: {
      type: 'select',
      optionsConfig: {
        name: 'name',
        value: 'value'
      },
      options: [{
        name: '空值补全',
        value: 0
      }, {
        name: '竞争式补全',
        value: 1
      }],
      // optionsParams: {
      //   before: true,
      // }
    },
    watch: function (val) {
      optionsParams.fulfillType = val.fulfillType;
    }
  },
  {
    label: "替换值",
    prop: "replaceValue",
    showOverflowTooltip: true, 
    align: "left",
    headerAlign: "left",
    isEdit: true,
    editConfig: {
      type: "singleSelect",
      defaultValue:'',
      optionsConfig: {
        name: "name",
        value: "value"
      },
      optionsParams: optionsParams,
      options: "getReplaceValue",
    }
  }, {
    label: "转换掩码（日期）",
    prop: "replaceMask",
    showOverflowTooltip: true,
    align: "left",
    headerAlign: "left",
    isEdit: true,
    editConfig: {
      type: "select",
      optionsConfig: {
        name: "code",
        value: "name"
      },
      options: "getLocalDateTimeFormat",
      optionsParams: {
        before: true,
      },
    }
  }, {
    label: "设置成空字符串",
    prop: "emptyString",
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
        value: 'Y',
        label: '是'
      }, {
        value: 'N',
        label: '否'
      }]
    }
  }
]

const VALUESTYPEtableColumnConfig = [{
    label: "类型",
    prop: "name",
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
    label: "替换值",
    prop: "replaceValue",
    showOverflowTooltip: true,
    align: "left",
    headerAlign: "left",
    isEdit: true,
    editConfig: {
      type: "input",
    }
  }, {
    label: "转换掩码（日期）",
    prop: "replaceMask",
    showOverflowTooltip: true,
    align: "left",
    headerAlign: "left",
    isEdit: true,
    editConfig: {
      type: "select",
      optionsConfig: {
        name: "code",
        value: "name"
      },
      options: "getLocalDateTimeFormat",
      optionsParams: {
        before: true
      },
    }
  }, {
    label: "设置成空字符串",
    prop: "emptyString",
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
        value: 'Y',
        label: '是'
      }, {
        value: 'N',
        label: '否'
      }]
    }
  }
]


const config = {
  type: 'IfNull',
  created: function (val) {
    if (val.selectTypes === "FIELDS") {
      this.$eventBus.$emit('refreshConfig', 'tableColumnConfig', FIELDStableColumnConfig);
    } else if (val.selectTypes === "VALUESTYPE") {
      this.$eventBus.$emit('refreshConfig', 'tableColumnConfig',
        VALUESTYPEtableColumnConfig);
    }
  },
  dialog: {
    title: '替换NULL值',
    visible: false,
    width: '50%'
  },
  tableColumnConfig: FIELDStableColumnConfig,

  formItemConfig: [{
      label: '步骤名称',
      type: 'input',
      valueKey: 'stepName',
      span: 24
    },
    {
      label: "类型",
      type: "radio",
      valueKey: "selectTypes",
      nameKey: "selectTypes",
      span: 24,
      options: [{
          name: '所有字段',
          value: 'ALL'
        },
        {
          name: '选择字段',
          value: 'FIELDS'
        },
        {
          name: '值类型',
          value: 'VALUESTYPE'
        }
      ],
      watch: function (val) {
        if (val.selectTypes === "ALL") {
          val.isHasTable = false;
          this.$eventBus.$emit('clearTable')
        } else if (val.selectTypes === "FIELDS") {
          val.isHasTable = true;
          this.$eventBus.$emit('refreshConfig', 'tableColumnConfig', FIELDStableColumnConfig);
          this.$eventBus.$emit('clearTable');
        } else if (val.selectTypes === "VALUESTYPE") {
          val.isHasTable = true;
          this.$eventBus.$emit('refreshConfig', 'tableColumnConfig',
            VALUESTYPEtableColumnConfig);
          this.$eventBus.$emit('clearTable');
        }
      }
    },
    {
      label: '替换值',
      type: 'input',
      valueKey: 'replaceAllByValue',
      span: 24,
      showCondition: {
        key: "selectTypes",
        value: "ALL"
      },
    },
    {
      label: "掩码（日期）",
      type: "select",
      valueKey: "replaceAllMask",
      optionsConfig: {
        name: "name",
        value: "name"
      },
      options: 'getLocalDateTimeFormat',
      span: 24,
      showCondition: {
        key: "selectTypes",
        value: "ALL"
      },
    },
    {
      label: "是否空字符串",
      type: "select",
      valueKey: "setEmptyStringAll",
      optionsConfig: {
        name: "name",
        value: "value"
      },
      options: [{
        name: "是",
        value: "Y"
      }, {
        name: "否",
        value: "N"
      }, ],
      span: 24,
      showCondition: {
        key: "selectTypes",
        value: "ALL"
      },
    },
  ],
  ruleForm: {
    stepName: '替换NULL值',
    selectTypes: "ALL",
    replaceAllMask: "",
    replaceValue: '',
    setEmptyStringAll: "",
    isHasTable: false
  },
  formPreview: false,
  isHasTable: true,
  handleExtensionVisible: true,
  isHasCreateTableBtn: true
};

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
