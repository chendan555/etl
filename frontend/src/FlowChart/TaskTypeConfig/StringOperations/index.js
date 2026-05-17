let treeParams = {
  databaseInfo: '',
  includeElement: 15,
  transName: sessionStorage.getItem('path'),
  nodeId: '',
  text: '',
  node: ''
};

const config = {
  type: 'StringOperations',
  dialog: {
    title: '字符串操作',
    visible: false
  },
  formItemRuleConfig: {
    stepName: [
      {
        required: true,
        message: '请输入步骤名称',
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
    },
    {
      label: '对照数据源',
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
      label: '对照数据表',
      type: 'inputButtonTree',
      valueKey: 'dataBase',
      nameKey: 'dataTable',
      treeConfig: {
        title: '数据库浏览',
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
      }
      // width: "260px"
    }
  ],
  ruleForm: {
    stepName: '字符串操作',
    connection: '',
    dataBase: ''
  },
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
      label: '输出字段',
      prop: 'out_stream_name',
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
      label: '对照字段',
      prop: 'complete_name',
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
      label: '修剪类型',
      prop: 'trim_type',
      width: 150,
      showOverflowTooltip: true,
      align: 'left',
      headerAlign: 'left',
      isEdit: true,
      default: 0,
      editConfig: {
        type: 'select',
        optionsConfig: {
          name: 'label',
          value: 'value'
        },
        options: [
          {
            value: 0,
            label: '不修剪'
          },
          {
            value: 1,
            label: '左修剪'
          },
          {
            value: 2,
            label: '右修剪'
          },
          {
            value: 3,
            label: '左右修剪'
          }
        ]
      }
    },
    {
      label: '大小写',
      prop: 'lower_upper',
      width: 150,
      showOverflowTooltip: true,
      align: 'left',
      headerAlign: 'left',
      isEdit: true,
      default: 0,
      editConfig: {
        type: 'select',
        optionsConfig: {
          name: 'label',
          value: 'value'
        },
        options: [
          {
            value: 0,
            label: '不使用'
          },
          {
            value: 1,
            label: '小写'
          },
          {
            value: 2,
            label: '大写'
          }
        ]
      }
    },
    {
      label: '填充',
      prop: 'padding_type',
      width: 150,
      showOverflowTooltip: true,
      align: 'left',
      headerAlign: 'left',
      isEdit: true,
      default: 0,
      editConfig: {
        type: 'select',
        optionsConfig: {
          name: 'label',
          value: 'value'
        },
        options: [
          {
            value: 0,
            label: '不填充'
          },
          {
            value: 1,
            label: '左填充'
          },
          {
            value: 2,
            label: '右填充'
          }
        ]
      }
    },
    {
      label: '填充值',
      prop: 'pad_char',
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
      label: '填充值长度',
      prop: 'pad_len',
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
      label: '首字母大写',
      prop: 'init_cap',
      width: 150,
      showOverflowTooltip: true,
      align: 'left',
      headerAlign: 'left',
      isEdit: true,
      default: 0,
      editConfig: {
        type: 'select',
        optionsConfig: {
          name: 'label',
          value: 'value'
        },
        options: [
          {
            value: 1,
            label: '是'
          },
          {
            value: 0,
            label: '否'
          }
        ]
      }
    },
    {
      label: '转换',
      prop: 'mask_xml',
      width: 150,
      showOverflowTooltip: true,
      align: 'left',
      headerAlign: 'left',
      isEdit: true,
      default: 0,
      editConfig: {
        type: 'select',
        optionsConfig: {
          name: 'label',
          value: 'value'
        },
        options: [
          {
            value: 0,
            label: 'none'
          },
          {
            value: 1,
            label: 'Escape XML'
          },
          {
            value: 2,
            label: 'Use CDATA'
          },
          {
            value: 3,
            label: 'Unsecape XML'
          },
          {
            value: 4,
            label: 'Escape SQL'
          },
          {
            value: 5,
            label: 'Unsecape HTML'
          },
          {
            value: 6,
            label: 'Unsecape'
          }
        ]
      }
    },
    {
      label: '数字处理',
      prop: 'digits',
      width: 150,
      showOverflowTooltip: true,
      align: 'left',
      headerAlign: 'left',
      isEdit: true,
      default: 0,
      editConfig: {
        type: 'select',
        optionsConfig: {
          name: 'label',
          value: 'value'
        },
        options: [
          {
            value: 0,
            label: '不处理'
          },
          {
            value: 1,
            label: '仅保留数字'
          },
          {
            value: 2,
            label: '移除数字'
          }
        ]
      }
    },
    {
      label: '移除特殊字符',
      prop: 'remove_special_characters',
      width: 150,
      showOverflowTooltip: true,
      align: 'left',
      headerAlign: 'left',
      isEdit: true,
      default: 0,
      editConfig: {
        type: 'select',
        optionsConfig: {
          name: 'label',
          value: 'value'
        },
        options: [
          {
            value: 0,
            label: '回车(\r)'
          },
          {
            value: 1,
            label: '换行(\n)'
          },
          {
            value: 2,
            label: '回车&换行(\r\n)'
          },
          {
            value: 3,
            label: '水平制表符(\t)'
          },
          {
            value: 4,
            label: '空格(&npsb;)'
          }
        ]
      }
    }
  ],
  tableData: [],
  formPreview: false
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
