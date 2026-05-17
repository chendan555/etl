// import API from '@/api/index';

let optionsParams = {
  path: '/'
};

const config = {
  type: 'ExcelOutput',
  dialog: {
    title: 'Excel输出',
    visible: false,
    width: '60%'
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
      label: '文件名',
      type: 'inputButtonTree',
      nameKey: 'filePath',
      treeConfig: {
        title: '选择文件地址',
        inputId: '',
        propsApi: {
          api: 'getFilesName',
          params: optionsParams,
          onceParams: {
            path: '/'
          },
        },
        nodeKey: 'id',
        lazy: true,
        filterable: true,
        loadParentId: 'path',
        valueBy: 'path',
        props: {
          label: 'name',
          children: 'children',
          disabled: false
        }
      },
      valueKey: 'fileName',
      span: 24,
      watch: function(val) {
        if (val.fileName) {
          optionsParams.path = val.fileName;
        }
      }
    },
    {
      label: '创建父目录',
      type: 'checkbox',
      valueKey: 'createParentFolder',
      span: 24
    },
    {
      label: '启动时不创建文件',
      type: 'checkbox',
      valueKey: 'notCreateFile',
      span: 24
    },
    {
      label: '扩展名',
      type: 'select',
      valueKey: 'fileType',
      span: 24,
      options: [
        {
          name: 'xls'
        },
        {
          name: 'xlsx'
        }
      ],
      optionsConfig: {
        name: 'name',
        value: 'name'
      }
    }
  ],
  ruleForm: {
    stepName: 'Excel输出',
    fileName: '',
    filePath: '',
    createParentFolder: false,
    notCreateFile: false,
    fileType: '.xls',
    connection: true
  },
  tableColumnConfig: [
    {
      label: '名称',
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
          before: true
        }
      }
    },
    {
      label: '类型',
      prop: 'value_type',
      // width: 150,
      showOverflowTooltip: true,
      align: 'left',
      headerAlign: 'left',
      isEdit: true,
      default: 2,
      editConfig: {
        type: 'select',
        optionsConfig: {
          name: 'type',
          value: 'code'
        },
        options: 'getColumnType'
      }
    }
  ],
  tableData: [],
  formPreview: false,
  isShowNewBtn: true,
  // isHasGetFieldBtn: true,
  isFilePreview: true
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
