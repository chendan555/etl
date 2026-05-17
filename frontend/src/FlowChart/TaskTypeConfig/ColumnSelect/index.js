const config = {
  type: "ColumnSelect",
  dialog: {
    title: "列选择",
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
  }, ],
  ruleForm: {
    stepName: "列选择",
  },
  tableColumnConfig: [{
    label: "字段名称",
    prop: "zdmc",
    showOverflowTooltip: true,
    align: "left",
    headerAlign: "left",
    isEdit: true,
    disabled: false,
    editConfig: {
      type: "select",
      optionsConfig: {
        name: "label",
        value: "value"
      },
      options: [{
        value: '1',
        label: 'name'
      }, {
        value: '2',
        label: 'source'
      }]
    }
  }, {
    label: "替代字段",
    prop: "value",
    showOverflowTooltip: true,
    align: "left",
    headerAlign: "left",
    isEdit: true,
    editConfig: {
      type: "input",
    }
  }, {
    label: "类型",
    prop: "lx",
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
        value: '1',
        label: 'BigNumber'
      }, {
        value: '2',
        label: 'Binary'
      }, {
        value: '3',
        label: 'Boolean'
      }, {
        value: '4',
        label: 'Date'
      }, {
        value: '5',
        label: 'Integer'
      }, {
        value: '6',
        label: 'Internet'
      }, {
        value: '7',
        label: 'Address'
      }, {
        value: '8',
        label: 'Number'
      }, {
        value: '9',
        label: 'Date'
      }, {
        value: '10',
        label: 'Timestamp'
      }]
    }
  }, {
    label: "格式",
    prop: "gs",
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
        value: '1',
        label: '#,##0.###'
      }, {
        value: '2',
        label: '0.00'
      }, {
        value: '3',
        label: '0000000000000'
      }, {
        value: '4',
        label: '#.#'
      }, {
        value: '5',
        label: '#'
      }, {
        value: '6',
        label: '###,###,###.#'
      }, {
        value: '7',
        label: '#######.###'
      }, {
        value: '8',
        label: '#####.###%'
      }]
    }
  }, {
    label: "是否删除",
    prop: "isDelete",
    showOverflowTooltip: true,
    align: "left",
    headerAlign: "left",
    isEdit: true,
    default: false,
    editConfig: {
      type: "switch",
      watch: function (val) {
        if (val) {
          // console.log(this.$parent.$parent.$parent)
          // this.$parent.$parent.$parent.tableColumnConfig = this.$parent.$parent.$parent
          //   .tableColumnConfig.map(
          //     item => {
          //       item.disabled = true;
          //       return item;
          //     })
        }
      }
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