export default {
  // 节点类型列表
  nodeList: [
    // {
    //   name: "shape-task",
    //   content: "根节点",
    //   shape: "circle",
    //   nodeType: "root"
    // },
    {
      name: 'shape-flow',
      content: '菱形节点',
      shape: 'hexagon',
      nodeType: 'hexagon'
    },
    {
      name: 'shape-activity',
      content: '圆弧节点',
      shape: 'circleRect',
      nodeType: 'circleRect'
    },
    {
      name: 'shape-equip-cszb',
      content: '圆角节点',
      shape: 'rect',
      nodeType: 'rect'
    },
    {
      name: 'shape-equip',
      content: '下划线',
      shape: 'BorderBottom',
      nodeType: 'BorderBottom'
    }
  ],
  // 控制按钮列表
  handleList: [
    {
      content: '复制',
      handle: 'copy',
      svgIcon: 'flow-copy'
    },
    {
      content: '粘贴',
      handle: 'paste',
      svgIcon: 'flow-paste'
    },
    {
      content: '概要',
      handle: 'createOutLine',
      svgIcon: 'flow-outline'
    },
    {
      content: '展布方式',
      svgIcon: 'flow-layout',
      children: [
        {
          label: '平衡型',
          value: 'default'
        },
        {
          label: '结构型',
          value: 'structure'
        },
        {
          label: '文件型',
          value: 'filetree'
        },
        {
          label: '右展型',
          value: 'right'
        },
        {
          label: '鱼骨型',
          value: 'fish-bone'
        },
        {
          label: '天盘型',
          value: 'tianpan'
        }
      ]
    },
    {
      content: '一键展收',
      handle: 'spreadNode',
      svgIcon: 'flow-hide'
    },
    {
      content: '删除',
      handle: 'removeNode',
      svgIcon: 'flow-del'
    },
    {
      content: '保存',
      handle: 'save',
      svgIcon: 'flow-save'
    }
  ],
  // 画布初始化数据
  importJson: <any>{
    background: '#1d2822',
    template: 'structure',
    root: {
      data: {
        id: 'root',
        text: '指标体系',
        connect: 'poly',
        shape: 'circle',
        background: '#01162D',
        color: '#fff',
        nodeType: 'root'
      },
      children: []
    }
  },
  // kityminder配置项
  minderOptions: <any>{
    blankSVG: false
  },
  // 节点基础信息对象
  nodeStyle: {
    text: '菱形节点',
    shape: 'hexagon',
    nodeType: 'hexagon',
    background: '#138A86',
    'selected-stroke': '#648ac5',
    'stroke-width': '1',
    'font-size': '14',
    'selected-stroke-width': '4',
    'padding-left': 26,
    'padding-right': 26,
    'connect-color': '#ffffff',
    'connect-width': '1',
    'padding-top': 8,
    'padding-bottom': 8,
    color: '#fff',
    stroke: '#648ac5'
  },
  // 各类型节点对应背景颜色
  backgroundList: <any>{
    circle: '#648ac5',
    hexagon: '#648ac5',
    circleRect: '#648ac5',
    rect: '#648ac5',
    BorderBottom: 'transparent'
  },
  outline: {
    id: createNodeId(),
    text: '概要',
    color: '#fff',
    shape: 'circleRect',
    background: '#d78c4f',
    'selected-stroke': '#FBF26F',
    'stroke-width': '1',
    'font-size': '14',
    'selected-stroke-width': '4',
    'padding-left': 5,
    'padding-right': 5,
    'connect-color': '#d78c4f',
    'connect-width': '1',
    'padding-top': 8,
    'padding-bottom': 8,
    stroke: '#fff',
    nodeType: 'Outline',
    isOutline: 'outline'
  }
};

function createNodeId() {
  let date: any = new Date();
  return (date * 1e6 + Math.floor(Math.random() * 1e6)).toString(36);
}
