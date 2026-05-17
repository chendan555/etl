export const funcIcon = [
  //     {
  //     name: '导入',
  //     imgUrl: require('@/assets/indexPage/func_dr.png'),
  //     // icon: 'download',
  //     function: ''
  //   }, 
  //    {
  //     name: '移动',
  //     imgUrl: require('@/assets/indexPage/func_move.png'),
  //     // icon: 'rank',
  //     function: ''
  //   }, {
  //     name: '拖动',
  //     imgUrl: require('@/assets/indexPage/func_push.png'),
  //     // icon: 'thumb',
  //     function: ''
  //   },
  {
    name: '放大',
    imgUrl: require('@/assets/indexPage/func_big.png'),
    // icon: 'zoom-in',
    function: 'zoomOut'
  }, {
    name: '缩小',
    imgUrl: require('@/assets/indexPage/func_small.png'),
    // icon: 'zoom-out',
    function: 'zoomIn'
  },
  //   {
  //     name: '删除',
  //     imgUrl: require('@/assets/indexPage/func_delete.png'),
  //     // icon: 'delete',
  //     function: ''
  //   },
  //  {
  //     name: '前一页',
  //     imgUrl: require('@/assets/indexPage/func_qyb.png'),
  //     // icon: 'refresh-left',
  //     function: ''
  // }, {
  //     name: '后一页',
  //     imgUrl: require('@/assets/indexPage/func_hyb.png'),
  //     // icon: 'refresh-right',
  //     function: ''
  // },
  // {
  //   name: '生成方案',
  //   imgUrl: require('@/assets/indexPage/func_mxsc.png'),
  //   // icon: 'box',
  //   function: 'createModel'
  // },
  // {
  //   name: '保存自定义算子',
  //   imgUrl: require('@/assets/indexPage/func_save.png'),
  //   // icon: 'refresh-left',
  //   function: 'openAutoDialog'
  // },
  {
    name: '保存',
    imgUrl: require('@/assets/indexPage/func_save.png'),
    // icon: 'refresh-left',
    function: 'save'
  },
  {
    name: '执行',
    imgUrl: require('@/assets/indexPage/func_zx.png'),
    // icon: 'refresh-right',
    function: 'execModel'
  },
  {
    name: '参数执行',
    imgUrl: require('@/assets/indexPage/func_zx.png'),
    function: 'execModelOptions'
  }
];

export const execModelConfig = {
  type: "execModelOptions",
  dialog: {
    title: "参数执行",
    visible: false
  },
  formItemConfig: [],
  ruleForm: {
    stepName: "执行参数",
  },
  tableColumnConfig: [{
    label: "变量",
    prop: "variable",
    showOverflowTooltip: true,
    align: "left",
    headerAlign: "left",
    isEdit: true,
    editConfig: {
      type: "input",
    }
  }, {
    label: "值",
    prop: "value",
    showOverflowTooltip: true,
    align: "left",
    headerAlign: "left",
    isEdit: true,
    editConfig: {
      type: "input",
    }
  }],
  tableData: [],
  formPreview: true,
  isHasTable: true,
  isHasExec: true
}