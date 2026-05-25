/**
 * 画布样式及配置文件
 * 定义画布中节点各类样式信息
 */

// 节点默认样式（与旧模块 fy_node 一致：170x34，蓝色边框）
export const nodeConfig = {
  width: 170,
  height: 34,
  attrs: {
    body: {
      fill: '#fff',
      stroke: '#107eef',
      strokeWidth: 1,
      rx: 4,
      ry: 4,
    },
    label: {
      fill: '#000',
      fontSize: 14,
      refX: 0.5,
      refY: 0.5,
      textAnchor: 'middle',
      textVerticalAnchor: 'middle',
    },
  },
};

// 端点样式（对齐旧模块 sourceEndpoint / targetEndpoint）
export const portConfig = {
  groups: {
    in: {
      position: 'top',
      attrs: {
        circle: {
          r: 6,
          magnet: true,
          stroke: 'transparent',
          fill: 'transparent',
          strokeWidth: 1,
        },
      },
    },
    out: {
      position: 'bottom',
      attrs: {
        circle: {
          r: 4,
          magnet: true,
          stroke: '#00cdea',
          fill: '#fff',
          strokeWidth: 2,
        },
      },
    },
  },
};

// 连线样式（对齐旧模块 connectorPaintStyle）
export const edgeConfig = {
  attrs: {
    line: {
      stroke: '#b4bdc5',
      strokeWidth: 2,
      targetMarker: {
        name: 'block',
        width: 8,
        height: 8,
      },
    },
  },
  connector: {
    name: 'rounded',
    args: {
      radius: 8,
    },
  },
  router: {
    name: 'manhattan',
    args: {
      padding: 20,
    },
  },
};
