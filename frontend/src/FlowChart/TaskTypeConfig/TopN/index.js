// import API from '@/api';

let treeParams = {
  databaseInfo: '',
  includeElement: 15,
  transName: sessionStorage.getItem('path'),
  nodeId: '',
  text: '',
  node: ''
};


const config = {
  type: 'TopN',
  dialog: {
    title: '归一化',
    visible: false,
    width: '50%'
  },
  tableColumnConfig: [
    {
      label: "字段名称",
      prop: "name",
      width: 248,
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
      label: "类型",
      prop: "type",
      width: 248,
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
      label: "处理选择",
      prop: "dealItem",
      width: 248,
      showOverflowTooltip: true,
      align: "left",
      headerAlign: "left",
      isEdit: true,
      editConfig: {
        type: 'select',
        optionsConfig: {
          name: "name",
          value: "value"
        },
        options: [{
          name: '否',
          value: 0,
        }, {
          name: '是',
          value: 1,
        }],
      },
    }
  ],

  formItemConfig: [{
      label: '步骤名称',
      type: 'input',
      valueKey: 'stepName',
      span: 24
    },
    {
      label: '参数范围',
      type: 'radio',
      valueKey: 'arguments',
      span: 24,
      options: [{
        name: '标准',
        value: '1'
      },
      {
        name: '精准',
        value: '2'
      },
      {
        name: '深度',
        value: '3'
      }
    ],
    optionsConfig: {
      name: 'name',
      value: 'value'
    },
      // watch: function (val) {
      //   treeParams.databaseInfo = val.connection;
      //   treeParams.text = val.connection;
      // }
    },
    // {
    //   label: '参数',
    //   type: 'checkBoxBtn',
    //   valueKey: 'arguments',
    //   options: [{
    //       name: '单位标准差归一化',
    //       value: '1'
    //     },
    //     {
    //       name: '平均数据中心化',
    //       value: '2'
    //     }
    //   ],
    //   optionsConfig: {
    //     name: 'name',
    //     value: 'value'
    //   },
      // watch: function (val, tableData) {
      //   this.$eventBus.$emit("clearTable");
      // }
    // }
  ],
  ruleForm: {
    stepName: '逻辑回归',
    arguments: '1',
  },
  formPreview: false,
  // isHasGetFieldBtn: false,
  isHasDoubleTable: true,
  isHasTable: true,
  // handleExtensionVisible: true,
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
