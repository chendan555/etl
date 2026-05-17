import utils from '@/utils/index';
import API from '@/api/index';
import { Message } from 'element-ui';

const treeParams = {
  databaseInfo: '',
  includeElement: 15,
  transName: sessionStorage.getItem('path'),
  nodeId: 'schema',
  text: '',
  node: '',
};

const config = {
  type: 'TableExists',
  dialog: {
    title: '检查数据表',
    visible: false,
  },
  formItemRuleConfig: {
    stepName: [
      {
        required: true,
        message: '请输入步骤名称',
        trigger: 'blur',
      },
    ],
    dataTable: [
      {
        required: true,
        message: '请选择数据表',
        trigger: 'blur',
      },
    ],
    schemaname: [
      {
        required: true,
        message: '请选择数据模式',
        trigger: 'blur',
      },
    ],
    connection: [
      {
        required: true,
        message: '请选择数据源',
        trigger: ['blur', 'change', 'focus'],
      },
    ],
    resultfieldname: [
      {
        required: true,
        message: '请输入结果字段名。字段名由字母、数字、下划线组成，只能由字母或者下划线开头。',
        trigger: ['blur', 'change'],
      },
    ]
  },
  formItemConfig: [
    {
      label: '步骤名称',
      type: 'input',
      valueKey: 'stepName',
      span: 24,
    },
    {
      label: '数据源',
      type: 'select',
      valueKey: 'connection',
      span: 24,
      options: 'getDatabases',
      optionsConfig: {
        name: 'name',
        value: 'name',
      },
      watch(val) {
        treeParams.databaseInfo = val.connection;
        treeParams.text = val.connection;
      },
    },
    {
      label: '数据模式',
      type: 'inputButtonTree',
      valueKey: 'schemaname',
      nameKey: 'dataModel',
      treeConfig: {
        title: '数据库浏览',
        propsApi: {
          api: 'getExplorer',
          params: treeParams,
        },
        nodeKey: 'id',
        lazy: true,
        filterable: true,
        loadParentId: 'nodeId',
        valueBy: 'nodeId',
        props: {
          label: 'text',
          children: 'children',
          disabled: false,
        },
      },
      span: 24,
      watch(val) {
        treeParams.databaseInfo = val.connection;
        treeParams.text = val.connection;
      },
    },
    // {
    //   label: '数据表',
    //   type: 'inputButtonTree',
    //   valueKey: 'tablenamefield',
    //   nameKey: 'dataTable',
    //   treeConfig: {
    //     title: '数据库浏览',
    //     propsApi: {
    //       api: 'getExplorer',
    //       params: treeParams,
    //     },
    //     nodeKey: 'id',
    //     lazy: true,
    //     filterable: true,
    //     loadParentId: 'nodeId',
    //     valueBy: 'nodeId',
    //     props: {
    //       label: 'text',
    //       children: 'children',
    //       disabled: false,
    //     },
    //   },
    //   span: 24,
    //   watch(val) {
    //     treeParams.databaseInfo = val.connection;
    //     treeParams.text = val.connection;
    //   }
    // },
    {
      label: '结果字段名',
      type: 'input',
      valueKey: 'resultfieldname',
      span: 24,
      watch(val) {
        if (val.resultfieldname) {
          let copy = false;
          API.getStepsFileds({ before: true }).then(res => {
            res.data.forEach(ele => {
              if (ele.code === val.resultfieldname) {
                copy = true
              }
            });
          });
          if (!utils.checkFieldFormat(val.resultfieldname)) {
            Message.warning('结果字段名格式错误，请重新输入！');
          }
          if (copy) {
            Message.warning('结果字段名与前节点字段重复，请重新输入！');
          }
        }
      },
    },
  ],
  ruleForm: {
    stepName: '检查数据表',
    connection: '',
    schemaname: '',
    dataModel: '',
    tablenamefield: '',
    dataTable: '',
    resultfieldname: 'result',
  },
  formPreview: false,
  isHasMonaco: false,
  isHasTable: false,
  isHasPreview: true,
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
