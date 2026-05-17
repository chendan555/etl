let treeParams = {
  databaseInfo: '',
  includeElement: 15,
  transName: sessionStorage.getItem('path'),
  nodeId: '',
  text: '',
  node: ''
};

let tableChartsNameParams = {
  nodeId: 'asdf',
  databaseInfo: sessionStorage.getItem('text'),
  text: sessionStorage.getItem('text'),
  includeElement: 15,
  transName: sessionStorage.getItem('path')
};

const config = {
  type: 'InsertUpdate',
  created: function(val) {
    tableChartsNameParams.nodeId = val.table ? val.table : null;
    tableChartsNameParams.databaseInfo = val.connection ? val.connection : null;
    tableChartsNameParams.text = val.connection ? val.connection : null;
  },
  dialog: {
    title: '插入更新',
    visible: false,
    width: '80%'
  },
  tableColumnConfigList: [
    {
      key: 'searchFields',
      title: '查询字段',
      tableColumnConfig: [
        {
          label: '表字段',
          prop: 'keyLookup',
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
            options: 'getExplorer',
            optionsParams: tableChartsNameParams
            // options: 'getInputOutputFields',
            // optionsParams: {
            //   before: true
            // }
          }
        },
        {
          label: '比较符',
          prop: 'keyCondition',
          showOverflowTooltip: true,
          align: 'left',
          headerAlign: 'left',
          isEdit: true,
          default: '=',
          editConfig: {
            type: 'select',
            optionsConfig: {
              name: 'name',
              value: 'name'
            },
            options: [
              {
                name: '<'
              },
              {
                name: '>'
              },
              {
                name: '='
              },
              {
                name: '<='
              },
              {
                name: '>='
              }
            ],
            optionsParams: {
              before: true
            }
          }
        },
        {
          label: '流字段',
          prop: 'keyStream1',
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
            options: 'getStepsFileds',
            optionsParams: {
              before: true
            }
          }
        }
        // {
        //   label: '流字段2',
        //   prop: 'keyStream2',
        //   showOverflowTooltip: true,
        //   align: 'left',
        //   headerAlign: 'left',
        //   isEdit: true,
        //   editConfig: {
        //     type: 'select',
        //     optionsConfig: {
        //       name: 'name',
        //       value: 'name'
        //     },
        //     options: 'getInputOutputFields',
        //     optionsParams: {
        //       before: true
        //     }
        //   }
        // },
      ]
    },
    {
      key: 'updateFields',
      title: '更新字段',
      tableColumnConfig: [
        {
          label: '表字段',
          prop: 'updateLookup',
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
            options: 'getExplorer',
            optionsParams: tableChartsNameParams
          }
        },
        {
          label: '流字段',
          prop: 'updateStream',
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
            options: 'getStepsFileds',
            optionsParams: {
              before: true
            }
          }
        },
        {
          label: '更新',
          prop: 'update',
          showOverflowTooltip: true,
          align: 'left',
          headerAlign: 'left',
          isEdit: true,
          default: 'N',
          editConfig: {
            type: 'select',
            options: [
              {
                name: '是',
                value: 'Y'
              },
              {
                name: '否',
                value: 'N'
              }
            ],
            optionsConfig: {
              name: 'name',
              value: 'value'
            }
          }
        }
      ]
    }
  ],

  formItemConfig: [
    {
      label: '步骤名称',
      type: 'input',
      valueKey: 'stepName',
      span: 24
    },
    {
      label: '数据源',
      type: 'select',
      valueKey: 'connection',
      span: 24,
      options: 'getDatabases',
      optionsConfig: {
        name: 'name',
        value: 'name'
      },
      watch: function(val) {
        treeParams.databaseInfo = val.connection;
        treeParams.text = val.connection;
      }
    },
    {
      label: '',
      placeholder: '请选择表',
      type: 'inputButtonTree',
      valueKey: 'schema',
      nameKey: 'table',
      treeConfig: {
        title: '数据库浏览',
        inputId: 'tableOut',
        propsApi: {
          api: 'getExplorer',
          params: treeParams
        },
        nodeKey: 'id',
        lazy: true,
        filterable: true,
        loadParentId: 'nodeId',
        valueBy: 'nodeId',
        props: {
          label: 'text',
          children: 'children',
          disabled: false
        }
      },
      span: 24,
      watch: function(val) {
        treeParams.databaseInfo = val.connection;
        treeParams.text = val.connection;
        console.log(val.table);
        tableChartsNameParams.nodeId = val.table;
        tableChartsNameParams.databaseInfo = val.connection;
        tableChartsNameParams.text = val.connection;
      }
    },
    {
      label: '提交数据数量',
      type: 'input',
      valueKey: 'commit',
      span: 24
    },
    {
      label: '不执行任何更新',
      type: 'radio',
      valueKey: 'update_bypassed',
      options: [
        {
          name: '是',
          value: 'Y'
        },
        {
          name: '否',
          value: 'N'
        }
      ],
      optionsConfig: {
        name: 'name',
        value: 'value'
      }
    }
  ],
  ruleForm: {
    stepName: '插入更新',
    update_bypassed: 'N',
    connection: '',
    schema: '', //数据库id
    table: '', //数据库名字
    commit: '100',
    searchFields: [],
    updateFields: []
  },
  formPreview: false,
  isHasTable: true,
  isShowBtn: true,
  handleExtensionVisible: true
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
