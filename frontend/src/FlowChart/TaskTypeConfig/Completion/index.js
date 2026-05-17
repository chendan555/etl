let treeParams = {
  databaseInfo: '',
  includeElement: 15,
  transName: sessionStorage.getItem('path'),
  nodeId: '',
  text: '',
  node: ''
};

const config = {
  type: 'Completion',
  dialog: {
    title: '数据补全',
    visible: false
  },
  formItemRuleConfig: {
    stepName: [
      {
        required: true,
        message: 'completeKey',
        trigger: 'blur'
      }
    ]
  },
  formItemConfig: [
    {
      label: '步骤名称',
      type: 'input',
      valueKey: 'stepName',
      span: 24
    }
  ],
  tableColumnConfig: [
    {
      label: '输入字段',
      prop: 'in_stream_name',
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
        optionsParams: {
          before: true
        }
      }
    },
    {
      label: '策略',
      prop: 'strategy',
      // width: 150,
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
            value: 0,
            label: '空值补全'
          },
          {
            value: 1,
            label: '竞争式补全'
          }
        ]
      }
    },
    {
      label: '阈值',
      prop: 'threshold',
      // width: 150,
      showOverflowTooltip: true,
      align: 'left',
      headerAlign: 'left',
      isEdit: true,
      editConfig: {
        type: 'input'
      }
    },
    {
      label: '默认值',
      prop: 'defaultVal',
      // width: 150,
      showOverflowTooltip: true,
      align: 'left',
      headerAlign: 'left',
      isEdit: true,
      editConfig: {
        type: 'input'
      }
    }
  ],
  ruleForm: {
    stepName: '数据补全',
    completeKey: '',
    completeStr: ''
  },
  formPreview: false,
  isHasMonaco: false,
  isHasTable: true,
  isHasPreview: false
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