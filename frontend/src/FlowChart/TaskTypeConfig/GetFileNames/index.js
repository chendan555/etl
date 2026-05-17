import API from '@/api/index';

const config = {
  type: 'GetFileNames',
  dialog: {
    title: '获取文件名',
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
    }
  ],
  ruleForm: {
    stepName: '获取文件名',
    fileName: ''
  },
  tableColumnConfig: [
    {
      label: '文件/目录',
      prop: 'fileName',
      width: 500,
      showOverflowTooltip: true,
      align: 'left',
      headerAlign: 'left',
      isEdit: true,
      lazy: true,
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
            }else {
              resolve([])
            }
          });
        },
      }
    },
    {
      label: '通配符',
      prop: 'fileMask',
      showOverflowTooltip: true,
      align: 'left',
      headerAlign: 'left',
      isEdit: true,
      editConfig: {
        type: 'singleSelect',
        optionsConfig: {
          name: 'label',
          value: 'value'
        },
        options: [{
          label: '.*\.*$',
          value: '.*\.*$'
        }]
      }
    },
    {
      label: '子目录包含',
      prop: 'includeSubFolders',
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
            value: 'Y',
            label: '是'
          },
          {
            value: 'N',
            label: '否'
          }
        ]
      }
    }
  ],
  tableData: [],
  formPreview: false,
  isShowNewBtn: true,
  isHasGetFieldBtn: false,
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
