import API from '@/api/index';

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

// let getExplorerArr = [];
// (function(){
//     API.getInputOutputFields({before: true}).then(res=>{
//     console.log(101);
//     console.log(res);
//   })
// })()

const config = {
  type: 'TableOutput',
  created: function(val) {
    tableChartsNameParams.nodeId = val.table;
    tableChartsNameParams.databaseInfo = val.connection;
    tableChartsNameParams.text = val.connection;
  },
  dialog: {
    title: '表输出',
    visible: false,
    width: '50%'
  },
  tableColumnConfig: [
    {
      label: '表字段',
      prop: 'tableChartsName',
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
        options: 'getStepsFileds',
        optionsParams: {
          before: true
        }
      }
    },
    {
      label: '值类型',
      prop: 'value_type',
      width: 150,
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
      label: '是否新建',
      type: 'radio',
      valueKey: 'isNewTable',
      options: [
        {
          name: '已有表',
          value: 1
        },
        {
          name: '新建表',
          value: 2
        }
      ],
      optionsConfig: {
        name: 'name',
        value: 'value'
      },
      watch: function(val, tableData) {
        this.$eventBus.$emit('clearTable');
      }
    },
    {
      label: '',
      //删除该样式，已有表和新建表根据“是否新建”选项进行切换   --Leo 20210513
      // style: {
      //   'margin-bottom': '0 !important'
      // },
      placeholder: '请输入新建表名并验证',
      type: 'inputBtn',
      valueKey: 'table',
      btnName: '验证',
      span: 24,
      watch: function(val) {
        API.getCheckTableName({
          databaseInfo: val.connection,
          name: val.table
        }).then(res => {
          this.$message({
            type: res.data.success ? 'success' : 'error',
            message: res.data.result
          });
        });
      },
      showCondition: {
        key: 'isNewTable',
        value: 2
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
        // 配置获取表字段接口参数
        tableChartsNameParams.nodeId = val.table;
        tableChartsNameParams.databaseInfo = val.connection;
        tableChartsNameParams.text = val.connection;
        console.log(tableChartsNameParams)
        // 该配置无用  --20210514
        // treeParams.databaseInfo = val.connection;
        // treeParams.text = val.connection;

      },
      showCondition: {
        key: 'isNewTable',
        value: 1
      }
      // width: "260px"
    },
    {
      label: '目标数据集',
      type: 'radio',
      valueKey: 'targetDataCollection',
      options: [
        {
          name: '是',
          value: 1
        },
        {
          name: '否',
          value: 2
        }
      ],
      optionsConfig: {
        name: 'name',
        value: 'value'
      }
      // watch: function (val, tableData) {
      //   this.$eventBus.$emit("clearTable");
      // }
    },
    {
      label: '提交数据数量',
      type: 'input',
      valueKey: 'commit',
      span: 24
    }
  ],
  ruleForm: {
    // tableOutput: true,  //表输出组件专用标示
    stepName: '表输出',
    isNewTable: 1,
    targetDataCollection: 1,
    connection: '',
    schema: '', //数据库id
    table: '', //数据库名字
    commit: '1000'
  },
  formPreview: false,
  isHasTable: true,
  isHasPreviewOut: true,
  handleExtensionVisible: true,
  isHasCreateTableBtn: true,
  otherConfig: {
    getFieldsInterface: 'getStepsFileds'
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
