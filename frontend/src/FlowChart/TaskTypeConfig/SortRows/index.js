const config = {
  type: "SortRows",
  dialog: {
    title: "行排序",
    visible: false
  },
  formItemRuleConfig: {
    stepName: [{
      required: true,
      message: "请输入步骤名称",
      trigger: "blur"
    }, ]
  },
  formItemConfig: [{
    label: "步骤名称",
    type: "input",
    valueKey: "stepName",
    span: 24
  }],
  ruleForm: {
    stepName: "行排序",
    sort_size:1000000,
    directory:"java.io.tmpdir",
    prefix:"out",
    free_memory:"",
    compress:"N",
    unique_rows:"N"
  },
  tableColumnConfig: [{
    label: "字段名称",
    prop: "name",
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
    label: "排序方式",
    prop: "ascending",
    showOverflowTooltip: true,
    align: "left",
    headerAlign: "left",
    isEdit: true,
    editConfig: {
      type: "select",
      optionsConfig: {
        name: "label",
        value: "value"
      },
      options: [{
        value: 'Y',
        label: '升序'
      }, {
        value: 'N',
        label: '降序'
      }]
    }
  }, {
    label: "大小写敏感",
    prop: "case_sensitive",
    showOverflowTooltip: true,
    align: "left",
    headerAlign: "left",
    isEdit: true,
    editConfig: {
      type: "select",
      optionsConfig: {
        name: "label",
        value: "value"
      },
      options: [{
        value: 'Y',
        label: '是'
      }, {
        value: 'N',
        label: '否'
      }]
    }
  },  {
    label: "预排序",
    prop: "presorted",
    showOverflowTooltip: true,
    align: "left",
    headerAlign: "left",
    isEdit: true,
    editConfig: {
      type: "select",
      optionsConfig: {
        name: "label",
        value: "value"
      },
      options: [{
        value: 'Y',
        label: '是'
      }, {
        value: 'N',
        label: '否'
      }]
    }
  }, ],
  tableData: [],
  formPreview: false
}

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
