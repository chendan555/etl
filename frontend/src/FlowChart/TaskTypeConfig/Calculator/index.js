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
 * 配置项
 */
let otherOptions = {
  data: []
};

const config = {
  /**
   * 配置项
   */
  type: 'Calculator',
  /**
   * 配置项
   */
  dialog: {
    title: '计算器',
    visible: false
  },
  /**
   * 配置项
   */
  formItemRuleConfig: {
    stepName: [
      {
        required: true,
        message: '请输入步骤名称',
        trigger: 'blur'
      }
    ]
  },
  /**
   * 配置项
   */
  formItemConfig: [
    {
      label: '步骤名称',
      type: 'input',
      valueKey: 'stepName',
      span: 24
    }
  ],
  /**
   * 配置项
   */
  ruleForm: {
    stepName: '列运算与编辑'
  },
  /**
   * 配置项
   */
  tableColumnConfig: [
    {
      /**
       * 配置项
       */
      label: '新字段',
      prop: 'field_name',
      width: 150,
      showOverflowTooltip: true,
      align: 'left',
      headerAlign: 'left',
      isEdit: true,
      editConfig: {
        type: 'input'
      },
      watch: function(val, tableData) {
        otherOptions.data = tableData.map(item => {
          return {
            name: item.field_name
          };
        });
      }
    },
    {
      /**
       * 配置项
       */
      label: '计算',
      prop: 'calc_type',
      width: 150,
      showOverflowTooltip: true,
      default: 0,
      align: 'left',
      headerAlign: 'left',
      isEdit: true,
      editConfig: {
        type: 'select',
        optionsConfig: {
          name: 'calcDesc',
          value: 'calcType'
        },
        options: 'getCalcDesc'
      }
    },
    {
      /**
       * 配置项
       */
      label: '字段A',
      prop: 'field_a',
      width: 150,
      showOverflowTooltip: true,
      align: 'left',
      headerAlign: 'left',
      isEdit: true,
      editConfig: {
        type: 'select',
        optionsConfig: {
          name: 'name',
          value: 'name'
        },
        options: 'getInputOutputFields',
        // otherOptions: otherOptions,
        optionsParams: {
          before: true
        }
      }
    },
    {
      /**
       * 配置项
       */
      label: '字段B',
      prop: 'field_b',
      width: 150,
      showOverflowTooltip: true,
      align: 'left',
      headerAlign: 'left',
      isEdit: true,
      editConfig: {
        type: 'select',
        optionsConfig: {
          name: 'name',
          value: 'name'
        },
        options: 'getInputOutputFields',
        // otherOptions: otherOptions,
        optionsParams: {
          before: true
        }
      }
    },
    {
      /**
       * 配置项
       */
      label: '字段C',
      prop: 'field_c',
      width: 150,
      showOverflowTooltip: true,
      align: 'left',
      headerAlign: 'left',
      isEdit: true,
      editConfig: {
        type: 'select',
        optionsConfig: {
          name: 'name',
          value: 'name'
        },
        options: 'getInputOutputFields',
        // otherOptions: otherOptions,
        optionsParams: {
          before: true
        }
      }
    },
    {
      /**
       * 配置项
       */
      label: '值类型',
      prop: 'value_type',
      width: 150,
      showOverflowTooltip: true,
      align: 'left',
      headerAlign: 'left',
      isEdit: true,
      editConfig: {
        type: 'select',
        optionsConfig: {
          name: 'type',
          value: 'code'
        },
        options: 'getColumnType'
      }
    },
    {
      /**
       * 配置项
       */
      label: '长度',
      prop: 'value_length',
      width: 150,
      showOverflowTooltip: true,
      align: 'left',
      headerAlign: 'left',
      isEdit: true,
      editConfig: {
        type: 'input'
      }
    },
    {
      /**
       * 配置项
       */
      label: '货币符号',
      prop: 'currency_symbol',
      width: 150,
      showOverflowTooltip: true,
      align: 'left',
      headerAlign: 'left',
      isEdit: true,
      editConfig: {
        type: 'input'
      }
    },
    {
      /**
       * 配置项
       */
      label: '转换标志',
      prop: 'conversion_mask',
      width: 150,
      showOverflowTooltip: true,
      align: 'left',
      headerAlign: 'left',
      isEdit: true,
      editConfig: {
        type: 'input'
      }
    },
    {
      /**
       * 配置项
       */
      label: '小数点符号',
      prop: 'decimal_symbol',
      width: 150,
      showOverflowTooltip: true,
      align: 'left',
      headerAlign: 'left',
      isEdit: true,
      editConfig: {
        type: 'input'
      }
    },
    {
      /**
       * 配置项
       */
      label: '分组符号',
      prop: 'grouping_symbol',
      width: 150,
      showOverflowTooltip: true,
      align: 'left',
      headerAlign: 'left',
      isEdit: true,
      editConfig: {
        type: 'input'
      }
    },
    {
      /**
       * 配置项
       */
      label: '是否移除',
      prop: 'remove',
      width: 150,
      showOverflowTooltip: true,
      align: 'left',
      headerAlign: 'left',
      isEdit: true,
      editConfig: {
        type: 'select',
        optionsConfig: {
          name: 'label',
          value: 'value'
        },
        options: [
          {
            value: 'Y',
            label: '是'
          },
          {
            value: 'N',
            label: '否'
          }
        ]
      }
    }
  ],
  /**
   * 配置项
   */
  tableData: [],
  /**
   * 配置项
   */
  formPreview: false,
  /**
   * 配置项
   */
  isHasGetFieldBtn: false
};

export default config;
