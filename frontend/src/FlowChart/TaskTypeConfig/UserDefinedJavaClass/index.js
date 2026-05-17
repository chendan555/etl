import API from '@/api/index';
const dataList = {};
const typeList = [];
const config = {
  type: 'UserDefinedJavaClass',
  dialog: {
    title: '执行JAVA脚本',
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
      label: '关联代码',
      type: 'treeSelect',
      optionsConfig: {
        name: 'name',
        value: 'code'
      },
      options: 'getConnectCodeTree',
      optionsParams: {},
      lazy: false,
      treeProps: {
        label: 'name',
        value: 'code',
        isLeaf: 'leaf'
      },
      loadNode(node, resolve) {
        if (!typeList.length) {
          API.getConnectCodeTree().then((res) => {
            res.data.forEach((ele) => {
              ele.leaf = true;
              if (typeList.includes(ele.category)) {
                dataList[ele.category].children.push(ele);
              } else {
                typeList.push(ele.category);
                dataList[ele.category] = {
                  name: ele.category,
                  children: [ele]
                };
              }
            });
            const data = [];
            for (let pr in dataList) {
              data.push(dataList[pr]);
            }
            console.log(data);
            resolve(data);
          });
        }
      },
      span: 24
      // width: "260px"
    }
  ],
  ruleForm: {
    stepName: '执行JAVA脚本'
  },
  tableColumnConfig: [],
  tableColumnConfigList: [
    {
      key: 'fileds',
      title: '字段',
      tableColumnConfig: [
        {
          label: '字段名',
          prop: 'filed_name',
          showOverflowTooltip: true,
          align: 'left',
          headerAlign: 'left',
          isEdit: true,
          editConfig: {
            type: 'input'
          }
        },
        {
          label: '类型',
          prop: 'filed_type',
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
          label: '长度',
          prop: 'filed_length',
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
          prop: 'filed_precision',
          showOverflowTooltip: true,
          align: 'left',
          headerAlign: 'left',
          isEdit: true,
          editConfig: {
            type: 'input'
          }
        }
      ]
    },
    {
      key: 'parammeters',
      title: '参数',
      tableColumnConfig: [
        {
          label: '标签',
          prop: 'parammeter_tag',
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
          prop: 'parammeter_value',
          showOverflowTooltip: true,
          align: 'left',
          headerAlign: 'left',
          isEdit: true,
          editConfig: {
            type: 'input'
          }
        },
        {
          label: '描述',
          prop: 'parammeter_description',
          showOverflowTooltip: true,
          align: 'left',
          headerAlign: 'left',
          isEdit: true,
          default: 'N',
          editConfig: {
            type: 'input'
          }
        }
      ]
    },
    {
      key: 'messageSteps',
      title: '消息步骤',
      tableColumnConfig: [
        {
          label: '标签',
          prop: 'step_tag',
          showOverflowTooltip: true,
          align: 'left',
          headerAlign: 'left',
          isEdit: true,
          editConfig: {
            type: 'input'
          }
        },
        {
          label: '步骤',
          prop: 'step_name',
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
          label: '描述',
          prop: 'step_description',
          showOverflowTooltip: true,
          align: 'left',
          headerAlign: 'left',
          isEdit: true,
          default: 'N',
          editConfig: {
            type: 'input'
          }
        }
      ]
    },
    {
      key: 'targetSteps',
      title: '目标步骤',
      tableColumnConfig: [
        {
          label: '标签',
          prop: 'step_tag',
          showOverflowTooltip: true,
          align: 'left',
          headerAlign: 'left',
          isEdit: true,
          editConfig: {
            type: 'input'
          }
        },
        {
          label: '步骤',
          prop: 'step_name',
          showOverflowTooltip: true,
          align: 'left',
          headerAlign: 'left',
          isEdit: true,
          editConfig: {
            ttype: 'select',
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
          label: '描述',
          prop: 'step_description',
          showOverflowTooltip: true,
          align: 'left',
          headerAlign: 'left',
          isEdit: true,
          default: 'N',
          editConfig: {
            type: 'input'
          }
        }
      ]
    }
  ],

  formPreview: false,
  isHasMonaco: true,
  isHasTable: false,
  isHasGetFieldBtn: false,
  isHasPreview: false,
  isShowNewBtn: false,
  monacoType: 'java'
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
