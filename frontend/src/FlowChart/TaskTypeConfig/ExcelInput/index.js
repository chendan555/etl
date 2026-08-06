// import axios from '@/api/base/http';
let optionsParams = {
  fileName: '',
  index: -1,
  spreadsheet_type: 'Excel 2007 XLSX (Apache POI)'
};
// axios.post('/api/transNew/prevSteps').then((res) => {
//   console.log('獲取字典',res);
// });
let disableType = {
  sheetName: false,
  accept_filenames: false,
  // accept_filenames: false,
  accept_stepname: true,
  accept_field: true
};
const config = {
  type: 'ExcelInput',
  created: function(val) {
    optionsParams.fileName = val.filefield;
    optionsParams.spreadsheet_type = val.spreadsheet_type;
  },
  dialog: {
    title: 'Excel输入',
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
    ],
    spreadsheet_type: [
      {
        required: true,
        message: '请上传文件',
        trigger: 'blur'
      }
    ],
    encoding: [
      {
        required: true,
        message: '请输入编码',
        trigger: 'blur'
      }
    ]
  },
  formItemConfigDown: [
    {
      label: '文件名称字段',
      type: 'input',
      valueKey: 'filefield',
      span: 12
    },
    {
      label: '工作表名称字段',
      type: 'input',
      valueKey: 'sheetfield',
      span: 12
    },
    {
      label: '表单的行号列',
      type: 'input',
      valueKey: 'sheetrownumfield',
      span: 12
    },
    {
      label: '行号列',
      type: 'input',
      valueKey: 'rownumfield',
      span: 12
    },
    {
      label: '文件名字段',
      type: 'input',
      valueKey: 'shortFileFieldName',
      span: 12
    },
    {
      label: '扩展名字段',
      type: 'input',
      valueKey: 'extensionFieldName',
      span: 12
    },
    {
      label: '路径字段',
      type: 'input',
      valueKey: 'pathFieldName',
      span: 12
    },
    {
      label: '文件大小字段',
      type: 'input',
      valueKey: 'sizeFieldName',
      span: 12
    },
    {
      label: '是否为隐藏文件字段',
      type: 'input',
      valueKey: 'hiddenFieldName',
      span: 12
    },
    {
      label: '最后修改时间字段',
      type: 'input',
      valueKey: 'lastModificationTimeFieldName',
      span: 12
    },
    {
      label: 'Uri字段',
      type: 'input',
      valueKey: 'uriNameFieldName',
      span: 12
    },
    {
      label: 'Root uri 字段',
      type: 'input',
      valueKey: 'rootUriNameFieldName',
      span: 12
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
      label: '表格类型',
      type: 'select',
      valueKey: 'spreadsheet_type',
      optionsConfig: {
        name: 'label',
        value: 'value'
      },
      options: [
        {
          label: 'Excel 97-2003 XLS (JXL)',
          value: 'Excel 97-2003 XLS (JXL)'
        },
        {
          label: 'Excel 2007 XLSX (Apache POI)',
          value: 'Excel 2007 XLSX (Apache POI)'
        }
        // , {
        //   label: "Excel 2007 XLSX (Apache POI Streaming)",
        //   value: "Excel 2007 XLSX (Apache POI Streaming)"
        // }, {
        //   label: "Open Office ODS (ODFDOM)",
        //   value: "Open Office ODS (ODFDOM)"
        // }
      ],
      watch: function(val) {
        optionsParams.spreadsheet_type = val.spreadsheet_type;
      },
      span: 24
    },
    {
      //   label: "非空记录",
      //   type: "select",
      //   optionsConfig: {
      //     name: "label",
      //     value: "value"
      //   },
      //   options: [{
      //     label: "是",
      //     value: "Y"
      //   }, {
      //     label: "否",
      //     value: "N"
      //   }],
      //   valueKey: "noempty",
      //   span: 12
      // }, {
      //   label: "头部",
      //   type: "select",
      //   optionsConfig: {
      //     name: "label",
      //     value: "value"
      //   },
      //   options: [{
      //     label: "是",
      //     value: "Y"
      //   }, {
      //     label: "否",
      //     value: "N"
      //   }],
      //   valueKey: "header",
      //   span: 12
      // }, {
      //   label: "停在空记录",
      //   type: "select",
      //   optionsConfig: {
      //     name: "label",
      //     value: "value"
      //   },
      //   options: [{
      //     label: "是",
      //     value: "Y"
      //   }, {
      //     label: "否",
      //     value: "N"
      //   }],
      //   valueKey: "stoponempty",
      //   span: 12
      // }, {
      //   label: "表单的行号列",
      //   type: "input",
      //   valueKey: "sheetrownumfield",
      //   span: 12
      // }, {
      //   label: "行号列",
      //   type: "input",
      //   valueKey: "rownumfield",
      //   span: 12
      // }, {
      //   label: "限制",
      //   type: "input",
      //   valueKey: "limit",
      //   span: 12
      // }, {
      label: '编码',
      type: 'select',
      valueKey: 'encoding',
      optionsConfig: {
        name: 'label',
        value: 'value'
      },
      options: [
        {
          label: 'GBK',
          value: 'GBK'
        },
        {
          label: 'UTF-8',
          value: 'UTF-8'
        }
      ],
      span: 24
    },
    {
      label: '从前步骤获取文件名',
      type: 'select',
      valueKey: 'accept_filenames',
      span: 12,
      optionsConfig: {
        name: 'label',
        value: 'value'
      },
      options: [
        {
          label: '是',
          value: 'Y'
        },
        {
          label: '否',
          value: 'N'
        }
      ]
    },
    {
      label: '从哪个步骤读文件名',
      type: 'select',
      options: 'getStepsName',
      optionsParams: {
        before: true
      },
      optionsConfig: {
        name: 'name',
        value: 'name'
        // list:'prevFields'
      },
      valueKey: 'accept_stepname',
      span: 12,
      showCondition: {
        key: 'accept_filenames',
        value: 'Y'
      }
    },
    {
      label: '保存文件名的字段名',
      type: 'select',
      valueKey: 'accept_field',
      options: 'getStepsFileds',
      optionsParams: {
        before: true
      },
      optionsConfig: {
        name: 'key',
        value: 'key'
      },
      span: 12,
      showCondition: {
        key: 'accept_filenames',
        value: 'Y'
      }
    },
    {
      label: '文件上传',
      type: 'upload',
      span: 24,
      watch: function(val, res) {
        // res = {code: 200, data: {path: "...", name: "...", size: N}}
        var filePath = res && res.data ? res.data.path : (res ? res.path : '');
        if (filePath) {
          val.filefield = filePath;
          optionsParams.fileName = filePath;
        }
      },
      disabled: disableType.accept_filenames,
      showCondition: {
        key: 'accept_filenames',
        value: 'N'
      }
    },
    {
      //   label: "文件/目录",
      //   type: "input",
      //   valueKey: "filefield",
      //   span: 12,
      //   showCondition: {
      //     key: "accept_filenames",
      //     value: "N"
      //   },
      // }, {
      //   label: "通配符号",
      //   type: "input",
      //   valueKey: "filemask",
      //   span: 12,
      //   showCondition: {
      //     key: "accept_filenames",
      //     value: "N"
      //   },
      // }, {
      //   label: "通配符号(排除)",
      //   type: "input",
      //   valueKey: "exclude_filemask",
      //   span: 12,
      //   showCondition: {
      //     key: "accept_filenames",
      //     value: "N"
      //   },
      // }, {
      //   label: "要求",
      //   type: "select",
      //   optionsConfig: {
      //     name: "label",
      //     value: "value"
      //   },
      //   options: [{
      //     label: "是",
      //     value: "Y"
      //   }, {
      //     label: "否",
      //     value: "N"
      //   }],
      //   valueKey: "file_required",
      //   span: 12,
      //   showCondition: {
      //     key: "accept_filenames",
      //     value: "N"
      //   },
      // }, {
      //   label: "包含子目录",
      //   type: "select",
      //   optionsConfig: {
      //     name: "label",
      //     value: "value"
      //   },
      //   options: [{
      //     label: "是",
      //     value: "Y"
      //   }, {
      //     label: "否",
      //     value: "N"
      //   }],
      //   valueKey: "include_subfolders",
      //   span: 12,
      //   showCondition: {
      //     key: "accept_filenames",
      //     value: "N"
      //   },
      // }, {

      label: '工作表名称',
      type: 'select',
      valueKey: 'sheetName',
      optionsConfig: {
        name: 'name',
        value: 'index'
      },
      options: 'getExcelSheet',
      optionsParams: optionsParams,
      span: 24,
      disabled: disableType.sheetName,
      showCondition: {
        key: 'accept_filenames',
        value: 'N'
      },
      watch: function(val) {
        let index = this.selectOptions.findIndex(item => item.name === val.sheetName);
        optionsParams.index = index;
      }
    }
  ],
  ruleForm: {
    ExcelInput: true,
    stepName: 'Excel输入',
    spreadsheet_type: 'Excel 2007 XLSX (Apache POI)',
    noempty: 'Y',
    header: 'Y',
    stoponempty: '',
    sheetrownumfield: '',
    rownumfield: '',
    limit: '',
    encoding: 'UTF-8',
    add_to_result_filenames: '',
    sheetfield: '',
    filefield: '',
    accept_filenames: 'N',
    accept_field: '',
    accept_stepname: '',
    // fileName: '',
    filemask: '',
    exclude_filemask: '',
    file_required: 'N',
    include_subfolders: 'N',
    sheetName: 0,
    startrow: '',
    startcol: ''
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
          name: 'name',
          value: 'name'
        },
        options: 'getExcelFields',
        optionsParams: optionsParams
      }
    },
    {
      label: '类型',
      prop: 'type',
      // width: 250,
      showOverflowTooltip: true,
      align: 'left',
      headerAlign: 'left',
      isEdit: true,
      editConfig: {
        type: 'select',
        optionsConfig: {
          name: 'type',
          value: 'type'
        },
        options: 'getColumnType'
      }
    },
    {
      label: '去除空格类型',
      prop: 'trim_type',
      width: 350,
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
            value: 'none',
            label: '不去掉空格'
          },
          {
            value: 'left',
            label: '去掉左空格'
          },
          {
            value: 'right',
            label: '去掉右空格'
          },
          {
            value: 'both',
            label: '去掉左右两端空格'
          }
        ]
      }
    }
  ],
  tableData: [],
  formPreview: false,
  isShowNewBtn: true,
  isShowNewBtnInputType: true,
  isHasPreviewExcel: true,
  isHasPreviewOut: false,
  tabTableVisible: true,
  tabVisible: true,
  activeName: 'first',
  isHasGetFieldBtn: true,
  otherConfig: {
    getFieldsInterface: 'getExcelFields'
  }
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