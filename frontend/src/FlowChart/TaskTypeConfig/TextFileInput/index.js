/* eslint-disable */
import API from '@/api/index';
let optionsParams = {
  path: '',
  index: '',
  spreadsheet_type: 'Excel 97-2003 XLS (JXL)'
};

let optionsDisabled = {
  nrFooterLines: false
};

const config = {
  type: 'TextFileInput',
  dialog: {
    title: '文本文件输入',
    visible: false
  },
  isShowTab: true,
  tabActiveInit: 'files',
  tabList: [{
      label: '文件',
      name: 'files',
      formItemConfig: [{
          label: '步骤名称',
          type: 'input',
          valueKey: 'stepName',
          span: 24
        },
        /* {
          label: '文件或目录',
          type: 'file',
          valueKey: 'fileName',
          watch: function(val, res) {
            val.fileName = res.path;
            // console.log(val);
            // console.log(res);
            optionsParams.path = res.path;
          }
        }, */
        {
          label: '规则表达式',
          type: 'input',
          valueKey: 'filemask'
        },
        {
          label: '正则表达式（排除）',
          type: 'input',
          valueKey: 'excludeFileMask'
        },
        {
          label: '从以前的步骤接受文件名',
          type: 'checkbox',
          valueKey: 'getFileName',
          span: 24,
          watch(val) {
            if (val.getFileName) {
              val.isHasTable = false;
              val.acceptingFilenames = 'Y';
            } else {
              val.isHasTable = true;
              val.acceptingFilenames = 'N';
            }
          }
        },
        {
          label: '从以前的步骤接受字段名',
          type: 'checkbox',
          valueKey: 'passingThruFields',
          span: 24,
          showCondition: {
            key: 'getFileName',
            value: true
          }
        },
        {
          label: '步骤读取的文件名来自',
          type: 'select',
          valueKey: 'acceptingStepName',
          span: 24,
          options: 'getStepsName',
          optionsParams: {
            before: true
          },
          optionsConfig: {
            name: 'name',
            value: 'name'
          },
          isEdit: true,
          disabled: false,
          showCondition: {
            key: 'getFileName',
            value: true
          }
        },
        {
          label: '输入的字段作为文件名',
          type: 'input',
          valueKey: 'acceptingField',
          showCondition: {
            key: 'getFileName',
            value: true
          }
        }
        /* {
          label: '选中的文件',
          type: 'table',
          valueKey: 'auth_database',
          showCondition: {
            key: "getFileName",
            value: false
          },
        } */
      ],
      formItemRuleConfig: {
        // hostname: [{
        //   required: true,
        //   message: "请输入连接地址",
        //   trigger: "blur"
        // }],
        // port: [{
        //   required: true,
        //   message: "请输入连接端口",
        //   trigger: "blur"
        // }],
        // db_name: [{
        //   required: true,
        //   message: "请输入名称",
        //   trigger: "blur"
        // }],
        // auth_user: [{
        //   required: true,
        //   message: "请输入用户名",
        //   trigger: "blur"
        // }],
        // auth_password: [{
        //   required: true,
        //   message: "请输入密码",
        //   trigger: "blur"
        // }],
        // auth_database: [{
        //   required: true,
        //   message: "请输入数据库名称",
        //   trigger: "blur"
        // }],
      },
      ruleForm: {
        stepName: '文本文件输入',
        isHasTable: true,
        fileName: '',
        filemask: '',
        excludeFileMask: '',
        getFileName: '',
        acceptingFilenames: '',
        passingThruFields: '',
        acceptingField: '',
        acceptingStepName: ''
      },
      isHasTable: true,
      formPreview: false,
      isHasGetFieldBtn: false,
      tableColumnConfig: [{
          label: '文件/目录',
          prop: 'fileName',
          showOverflowTooltip: true,
          align: 'left',
          headerAlign: 'left',
          isEdit: true,
          editConfig: {
            type: 'treeSelect',
            optionsConfig: {
              name: 'label',
              value: 'value'
            },
            options: 'getFilesName',
            optionsParams: {
              path: '/'
            },
            lazy: true,
            treeProps: {
              label: 'label',
              isLeaf: 'leaf'
            },
            loadNode(node, resolve) {
              let params = {
                path: node.data ? node.data.value : '/'
              };
              API.getFilesName(params).then(res => {
                if (typeof res.data !== 'string') {
                  res.data.forEach(ele => {
                    if (ele.fileType === 'FILE') {
                      ele.leaf = true;
                    }
                  });
                  resolve(res.data);
                } else {
                  resolve([]);
                }
              });
            }
          }
        },
        {
          label: '通配符',
          prop: 'filemask',
          showOverflowTooltip: true,
          align: 'left',
          headerAlign: 'left',
          isEdit: true,
          editConfig: {
            type: 'input'
          }
        },
        {
          label: '通配符号(排除)',
          prop: 'excludeFileMask',
          showOverflowTooltip: true,
          align: 'left',
          headerAlign: 'left',
          isEdit: true,
          editConfig: {
            type: 'input'
          }
        }
      ],
      tableData: [],
      alias: 'file'
    },
    {
      label: '内容',
      name: 'content',
      isHasTable: false,
      formItemConfig: [{
          label: '步骤名称',
          type: 'input',
          valueKey: 'stepName',
          span: 24
        },
        {
          label: '文件类型',
          type: 'select',
          valueKey: 'fileType',
          span: 24,
          options: [{
            name: 'Fixed',
            value: 'Fixed'
          }, {
            name: 'CSV',
            value: 'CSV'
          }],
          optionsConfig: {
            name: 'name',
            value: 'value'
          }
        },
        {
          label: '分隔符',
          type: 'input',
          valueKey: 'separator',
          span: 24
        },
        {
          label: '文本限定符',
          type: 'input',
          valueKey: 'enclosure',
          span: 24
        },
        {
          label: '逃逸字符',
          type: 'input',
          valueKey: 'escapeCharacter',
          span: 24
        },
        {
          label: '头部',
          type: 'checkbox',
          valueKey: 'header',
          span: 5
        },
        {
          label: '头部行数',
          labelWidth: '100px',
          type: 'input',
          valueKey: 'nrHeaderLines',
          span: 19,
          disabledKey: 'header'
        },
        {
          label: '尾部',
          type: 'checkbox',
          valueKey: 'footer',
          span: 5
        },
        {
          label: '尾部行数',
          labelWidth: '100px',
          type: 'input',
          valueKey: 'nrFooterLines',
          span: 19,
          disabledKey: 'footer'
        },
        {
          label: '包装行',
          type: 'checkbox',
          valueKey: 'lineWrapped',
          span: 5
        },
        {
          label: '以时间包装的行数',
          labelWidth: '140px',
          type: 'input',
          valueKey: 'nrWraps',
          span: 19,
          disabledKey: 'lineWrapped'
        },
        {
          label: '分页布局（printout）',
          type: 'checkbox',
          valueKey: 'layoutPaged',
          span: 5
        },
        {
          label: '每页记录行数',
          labelWidth: '110px',
          type: 'input',
          valueKey: 'nrLinesPerPage',
          span: 19,
          disabledKey: 'layoutPaged'
        },
        {
          label: '文档头部行',
          labelWidth: '320px',
          type: 'input',
          valueKey: 'nrLinesDocHeader',
          span: 24,
          disabledKey: 'layoutPaged'
        },
        {
          label: '压缩',
          type: 'select',
          valueKey: 'fileCompression',
          span: 24,
          options: 'getCompressionProviderNames',
          optionsConfig: {
            name: 'name',
            value: 'name'
          }
        },
        {
          // label: "没有空行",
          // type: "select",
          // valueKey: "noEmptyLines",
          // span: 24,
          // options: [
          //   {name: '是', value: true},
          //   {name: '否', value: false}
          // ],
          // optionsConfig: {
          //   name: "name",
          //   value: "value"
          // }
          label: '没有空行',
          type: 'checkbox',
          valueKey: 'noEmptyLines',
          span: 24
        },
        {
          label: '在输出包含字段名',
          type: 'checkbox',
          valueKey: 'includeFilename',
          span: 5
        },
        {
          label: ' 包含字段名的文件名称',
          labelWidth: '180px',
          type: 'input',
          valueKey: 'filenameField',
          span: 19,
          disabledKey: 'includeFilename'
        },
        {
          label: '输出包含行数',
          type: 'checkbox',
          valueKey: 'includeRowNumber',
          span: 5
        },
        {
          label: '行数字段名称',
          labelWidth: '140px',
          type: 'input',
          valueKey: 'rowNumberField',
          span: 19,
          disabledKey: 'includeRowNumber'
        },
        {
          label: '格式',
          type: 'select',
          valueKey: 'fileFormat',
          span: 24,
          options: [{
            name: 'mixed',
            value: 'mixed'
          }, {
            name: 'DOS',
            value: 'DOS'
          }, {
            name: 'Unix',
            value: 'Unix'
          }],
          optionsConfig: {
            name: 'name',
            value: 'value'
          }
        },
        {
          label: '编码方式',
          type: 'select',
          valueKey: 'encoding',
          span: 24,
          options: 'getAvailableCharsets',
          optionsConfig: {
            name: 'name',
            value: 'name'
          }
        },
        {
          label: 'Length',
          type: 'select',
          valueKey: 'length',
          span: 24,
          options: [{
            name: 'Characters',
            value: 'Characters'
          }, {
            name: 'Bytes',
            value: 'Bytes'
          }],
          optionsConfig: {
            name: 'name',
            value: 'value'
          }
        },
        {
          label: '记录数量限制',
          type: 'input',
          valueKey: 'rowLimit',
          span: 24
        },
        {
          label: '解析日期的时候是否严格要求',
          type: 'checkbox',
          valueKey: 'dateFormatLenient',
          span: 24
        },
        {
          label: '本地日期格式',
          type: 'select',
          valueKey: 'dateFormatLocale',
          span: 24,
          options: 'getLocalDateTimeFormat',
          optionsConfig: {
            name: 'name',
            value: 'name'
          }
        },
        {
          label: '添加文件名',
          type: 'checkbox',
          valueKey: 'isaddresult',
          span: 24
        }
      ],
      formItemRuleConfig: {},
      ruleForm: {
        stepName: '文本文件输入',
        fileType: 'Fixed',
        separator: ';',
        enclosure: "''",
        header: true,
        footer: false,
        nrHeaderLines: 1,
        nrFooterLines: 1,
        nrWraps: 1,
        lineWrapped: false,
        layoutPaged: false,
        includeFilename: false,
        nrLinesPerPage: 80,
        nrLinesDocHeader: 1,
        fileCompression: 'None',
        noEmptyLines: true,
        fileFormat: 'mixed',
        length: 'Characters',
        rowLimit: 0,
        dateFormatLenient: true,
        isaddresult: true // 此字段后端暂无提供
      },
      tableColumnConfig: [],
      tableData: []
    },
    /* {
      label: '错误处理',
      name: 'error',
      formItemConfig: [
        {
          label: '忽略错误',
          type: 'select',
          valueKey: 'errorIgnored',
          span: 24,
          options: [{ name: '是', value: true }, { name: '否', value: false }],
          optionsConfig: {
            name: 'name',
            value: 'value'
          }
        },
        {
          label: '忽略错误文件',
          type: 'select',
          valueKey: 'skipBadFiles',
          span: 24,
          options: [{ name: '是', value: true }, { name: '否', value: false }],
          optionsConfig: {
            name: 'name',
            value: 'value'
          }
        },
        {
          label: '错误文件字段名',
          type: 'input',
          valueKey: 'fileErrorField',
          span: 24
        },
        {
          label: '文件错误信息字段名',
          type: 'input',
          valueKey: 'fileErrorMessageField',
          span: 24
        }
      ],
      formItemRuleConfig: {},
      ruleForm: {},
      isHasTable: false,
      formPreview: false,
      tableColumnConfig: [
        {
          label: '变量',
          prop: 'name',
          showOverflowTooltip: true,
          align: 'left',
          headerAlign: 'left',
          isEdit: true,
          editConfig: {
            type: 'input'
          }
        },
        {
          label: '值',
          prop: 'value',
          showOverflowTooltip: true,
          align: 'left',
          headerAlign: 'left',
          isEdit: true,
          editConfig: {
            type: 'input'
          }
        }
      ],
      tableData: []
    }, */
    {
      label: '过滤',
      name: 'posParam',
      formItemConfig: [{
        label: '步骤名称',
        type: 'input',
        valueKey: 'stepName',
        span: 24
      }],
      formItemRuleConfig: {},
      ruleForm: {
        stepName: '文本文件输入'
      },
      isHasTable: true,
      isShowBtn: false,
      formPreview: false,
      isShowNewBtn: true,
      tableColumnConfig: [{
          label: '过滤字符串',
          prop: 'filterString',
          showOverflowTooltip: true,
          align: 'left',
          headerAlign: 'left',
          isEdit: true,
          editConfig: {
            type: 'input'
          }
        },
        {
          label: '过滤器位置',
          prop: 'filterPosition',
          showOverflowTooltip: true,
          align: 'left',
          headerAlign: 'left',
          isEdit: true,
          editConfig: {
            type: 'input'
          }
        },
        {
          label: '停止再过滤器',
          prop: 'filterLastLine',
          showOverflowTooltip: true,
          align: 'left',
          headerAlign: 'left',
          isEdit: true,
          editConfig: {
            type: 'input'
          }
        },
        {
          label: '积极匹配',
          prop: 'filterPositive',
          showOverflowTooltip: true,
          align: 'left',
          headerAlign: 'left',
          isEdit: true,
          editConfig: {
            type: 'input'
          }
        }
      ],
      tableData: [],
      alias: 'filter'
    },
    {
      label: '字段',
      name: 'key',
      formItemConfig: [{
        label: '步骤名称',
        type: 'input',
        valueKey: 'stepName',
        span: 24
      }],
      formItemRuleConfig: {},
      ruleForm: {
        stepName: '文本文件输入'
      },
      isHasTable: true,
      isShowBtn: false,
      formPreview: false,
      isShowNewBtn: true,
      isHasGetFieldBtn: true,
      tableColumnConfig: [{
          label: '名称',
          prop: 'name',
          showOverflowTooltip: true,
          align: 'left',
          headerAlign: 'left',
          isEdit: true,
          editConfig: {
            type: 'input'
          }
        },
        {
          label: "类型",
          prop: "type",
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
        },
        {
          label: '格式',
          prop: 'format',
          showOverflowTooltip: true,
          align: 'left',
          headerAlign: 'left',
          isEdit: true,
          editConfig: {
            type: 'input'
          }
        },
        {
          label: '位置',
          prop: 'position',
          showOverflowTooltip: true,
          align: 'left',
          headerAlign: 'left',
          isEdit: true,
          editConfig: {
            type: 'input'
          }
        },
        {
          label: '长度',
          prop: 'length',
          showOverflowTooltip: true,
          align: 'left',
          headerAlign: 'left',
          isEdit: true,
          editConfig: {
            type: 'input'
          }
        },
        {
          label: '精度',
          prop: 'precision',
          showOverflowTooltip: true,
          align: 'left',
          headerAlign: 'left',
          isEdit: true,
          editConfig: {
            type: 'input'
          }
        },
        {
          label: '货币类型',
          prop: 'currency',
          showOverflowTooltip: true,
          align: 'left',
          headerAlign: 'left',
          isEdit: true,
          editConfig: {
            type: 'input'
          }
        },
        {
          label: '小数',
          prop: 'decimal',
          showOverflowTooltip: true,
          align: 'left',
          headerAlign: 'left',
          isEdit: true,
          editConfig: {
            type: 'input'
          }
        },
        {
          label: '分组',
          prop: 'group',
          showOverflowTooltip: true,
          align: 'left',
          headerAlign: 'left',
          isEdit: true,
          editConfig: {
            type: 'input'
          }
        },
        {
          label: 'Null if',
          prop: 'trim_type',
          showOverflowTooltip: true,
          align: 'left',
          headerAlign: 'left',
          isEdit: true,
          editConfig: {
            type: 'input'
          }
        },
        {
          label: '默认',
          prop: 'nullif',
          showOverflowTooltip: true,
          align: 'left',
          headerAlign: 'left',
          isEdit: true,
          editConfig: {
            type: 'input'
          }
        },
        {
          label: '去除空字符串方式',
          prop: 'ifnull',
          showOverflowTooltip: true,
          align: 'left',
          headerAlign: 'left',
          isEdit: true,
          editConfig: {
            type: 'input'
          }
        },
        {
          label: '重复',
          prop: 'repeat',
          showOverflowTooltip: true,
          align: 'left',
          headerAlign: 'left',
          isEdit: true,
          editConfig: {
            type: 'input'
          }
        }
      ],
      tableData: [],
      alias: 'fields'
    },
    {
      label: '其他输出字段',
      name: 'otherKey',
      formItemConfig: [{
          label: '步骤名称',
          type: 'input',
          valueKey: 'stepName',
          span: 24
        },
        {
          label: '文件名字段',
          type: 'input',
          valueKey: 'shortFileFieldName',
          span: 24
        },
        {
          label: '扩展名字段',
          type: 'input',
          valueKey: 'extensionFieldName',
          span: 24
        },
        {
          label: '路径字段',
          type: 'input',
          valueKey: 'pathFieldName',
          span: 24
        },
        {
          label: '文件大小字段',
          type: 'input',
          valueKey: 'sizeFieldName',
          span: 24
        },
        {
          label: '是否为隐藏文件字段',
          type: 'input',
          valueKey: 'hiddenFieldName',
          span: 24
        },
        {
          label: '最后修改时间字段',
          type: 'input',
          valueKey: 'lastModificationTimeFieldName',
          span: 24
        },
        {
          label: 'Uri 字段',
          type: 'input',
          valueKey: 'uriNameFieldName',
          span: 24
        },
        {
          label: 'Root uri字段',
          type: 'input',
          valueKey: 'rootUriNameFieldName',
          span: 24
        }
      ],
      formItemRuleConfig: {},
      ruleForm: {
        stepName: '文本文件输入',
        shortFileFieldName: '',
        sizeFieldName: '',
        hiddenFieldName: '',
        lastModificationTimeFieldName: '',
        uriNameFieldName: '',
        rootUriNameFieldName: '',
        extensionFieldName: '',
        pathFieldName: ''
      },
      isHasTable: false,
      formPreview: false,
      tableColumnConfig: [{
          label: '变量',
          prop: 'name',
          showOverflowTooltip: true,
          align: 'left',
          headerAlign: 'left',
          isEdit: true,
          editConfig: {
            type: 'input'
          }
        },
        {
          label: '值',
          prop: 'value',
          showOverflowTooltip: true,
          align: 'left',
          headerAlign: 'left',
          isEdit: true,
          editConfig: {
            type: 'input'
          }
        }
      ],
      tableData: []
    }
  ],
  formItemRuleConfig: {},
  formItemConfig: [],
  ruleForm: {
    stepName: '文本文件输入',
  },
  tableColumnConfig: [],
  tableData: [],
  isShowBtn: true
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
