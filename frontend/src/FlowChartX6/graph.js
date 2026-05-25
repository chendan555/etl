import { Graph } from '@antv/x6';

let graphInstance = null;

/**
 * 创建或获取 X6 Graph 实例
 * @param {HTMLElement} container - 画布容器 DOM 元素
 * @returns {Graph}
 */
export function createGraph(container) {
  if (graphInstance) {
    graphInstance.dispose();
  }

  graphInstance = new Graph({
    container,
    autoResize: true,
    mouseWheel: {
      enabled: true,
      modifiers: ['ctrl', 'meta'],
      factor: 1.1,
    },
    panning: {
      enabled: true,
      modifiers: ['space'],
    },
    connecting: {
      connector: 'rounded',
      router: 'manhattan',
      anchor: 'center',
      connectionPoint: 'anchor',
      allowBlank: false,
      allowLoop: false,
      allowMulti: true,
      connectionEdgeAttr: {
        stroke: '#b4bdc5',
        strokeWidth: 2,
        targetMarker: {
          name: 'block',
          width: 8,
          height: 8,
        },
      },
    },
    selecting: {
      enabled: true,
      showNodeSelectionBox: true,
    },
    keyboard: {
      enabled: true,
    },
    defaultNode: {
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
    },
    defaultEdge: {
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
      connector: { name: 'rounded', args: { radius: 8 } },
      router: { name: 'manhattan', args: { padding: 20 } },
    },
  });

  return graphInstance;
}

/**
 * 获取当前 Graph 实例
 * @returns {Graph|null}
 */
export function getGraph() {
  return graphInstance;
}

/**
 * 销毁 Graph 实例
 */
export function disposeGraph() {
  if (graphInstance) {
    graphInstance.dispose();
    graphInstance = null;
  }
}
