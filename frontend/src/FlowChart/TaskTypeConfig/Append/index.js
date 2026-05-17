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

const config = {
  /**
   * 组件名称
   */
  type: 'Append',
  /**
   * 弹框配置
   */
  dialog: {
    title: '追加流',
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
    ],
    head_name: [
      {
        required: true,
        message: '请选择前一个输入',
        trigger: 'change'
      }
    ],
    tail_name: [
      {
        required: true,
        message: '请选择后一个输入',
        trigger: 'change'
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
    },
    {
      label: '前',
      type: 'select',
      valueKey: 'head_name',
      span: 24,
      optionsConfig: {
        name: 'name',
        value: 'name'
      },
      options: 'getPreviousSteps'
    },
    {
      label: '后',
      type: 'select',
      valueKey: 'tail_name',
      span: 24,
      optionsConfig: {
        name: 'name',
        value: 'name'
      },
      options: 'getPreviousSteps'
    }
  ],
  /**
   * 配置项
   */
  ruleForm: {
    stepName: '追加流',
    head_name: '',
    tail_name: ''
  },
  /**
   * 配置项
   */
  isHasTable: false,
  /**
   * 配置项
   */
  formPreview: false
};

export default config;
