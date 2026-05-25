/**
 * 数据处理流程画布模型文件
 * 定义画布中使用各类方法
 * 兼容旧 FlowChartJson 格式，提供双向转换
 */

import { CONNECTORSEPARATESYMBOL } from './const';

let FlowChartJson = {
  nodes: [],
  endpoints: [],
  edges: [],
  head: '',
};

export default {
  getData() {
    return FlowChartJson;
  },

  setData(data) {
    FlowChartJson = data;
    return FlowChartJson;
  },

  getEndpoints() {
    return FlowChartJson.endpoints;
  },

  addNode(nodeData) {
    if (!nodeData.properties) {
      nodeData.properties = {};
    }
    if (!nodeData.properties.ruleForm) {
      nodeData.properties.ruleForm = {
        stepName: nodeData.data ? nodeData.data.text : '',
      };
    }
    FlowChartJson.nodes.push(nodeData);
  },

  removeNodeByNodeId(nodeId) {
    FlowChartJson.nodes = FlowChartJson.nodes.filter(node => node.id !== nodeId);
  },

  getNodeDataByNodeId(nodeId) {
    return FlowChartJson.nodes.find(n => n.id === nodeId);
  },

  getSourceNodeIdsByNodeId(nodeId) {
    const node = this.getNodeDataByNodeId(nodeId);
    if (!node || !node.points) return [];
    const targets = node.points.targets;
    const edges = this.getEdgesByPointIds(targets);
    const sources = edges.map(edge => edge.split(CONNECTORSEPARATESYMBOL)[0]);
    const data = [];
    FlowChartJson.nodes.forEach(n => {
      sources.forEach(source => {
        if (n.points && n.points.sources) {
          n.points.sources.forEach(itemSource => {
            if (itemSource === source) {
              data.push(n.id);
            }
          });
        }
      });
    });
    return data;
  },

  addEdge(edge) {
    FlowChartJson.edges.push(edge);
  },

  removeEdge(edge) {
    FlowChartJson.edges = FlowChartJson.edges.filter(item => item !== edge);
  },

  getEdgesByPointIds(ids) {
    const data = [];
    FlowChartJson.edges.forEach(edge => {
      ids.forEach(id => {
        if (edge.indexOf(id) > -1) {
          data.push(edge);
        }
      });
    });
    return data;
  },

  removeEdgesByPointIds(ids) {
    ids.forEach(id => {
      FlowChartJson.edges = FlowChartJson.edges.filter(edge => edge.indexOf(id) === -1);
    });
  },

  addEndpoint(point) {
    FlowChartJson.endpoints.push(point);
  },

  getEndpointsByPointIds(ids) {
    return FlowChartJson.endpoints.filter(point => ids.indexOf(point.id) > -1);
  },

  removeEndpointsByPointIds(ids) {
    ids.forEach(id => {
      FlowChartJson.endpoints = FlowChartJson.endpoints.filter(point => point.id !== id);
    });
  },

  changeNodePos(nodeId, newPosition) {
    const node = FlowChartJson.nodes.find(n => n.id === nodeId);
    if (node) {
      node.position = newPosition;
    }
  },

  changeNodeValue(nodeId, value) {
    const node = FlowChartJson.nodes.find(n => n.id === nodeId);
    if (node && node.data) {
      node.data.text = value;
    }
  },

  getHead() {
    return FlowChartJson.head;
  },

  /**
   * 旧格式 FlowChartJson -> X6 cells 数据
   * @param {object} flowData - 旧格式数据
   * @returns {object} { nodes: [], edges: [] } X6 格式
   */
  toX6Data(flowData) {
    const x6Nodes = (flowData.nodes || []).map(node => {
      const pos = node.position || { left: 0, top: 0 };
      const ports = [];
      if (node.points) {
        (node.points.targets || []).forEach(targetId => {
          ports.push({
            id: targetId,
            group: 'in',
            attrs: {
              circle: {
                r: 6,
                magnet: true,
                stroke: 'transparent',
                fill: 'transparent',
                strokeWidth: 1,
              },
            },
          });
        });
        (node.points.sources || []).forEach(sourceId => {
          ports.push({
            id: sourceId,
            group: 'out',
            attrs: {
              circle: {
                r: 4,
                magnet: true,
                stroke: '#00cdea',
                fill: '#fff',
                strokeWidth: 2,
              },
            },
          });
        });
      }
      return {
        id: node.id,
        x: pos.left,
        y: pos.top,
        width: 170,
        height: 34,
        shape: 'vue-shape',
        data: {
          ...node.data,
          properties: node.properties || {},
          nodeId: node.id,
        },
        ports: ports.length > 0 ? ports : undefined,
      };
    });

    const x6Edges = (flowData.edges || []).map((edgeStr, index) => {
      const parts = edgeStr.split(CONNECTORSEPARATESYMBOL);
      return {
        id: `edge-${index}`,
        source: { port: parts[0] },
        target: { port: parts[1] },
        attrs: {
          line: {
            stroke: '#b4bdc5',
            strokeWidth: 2,
            targetMarker: { name: 'block', width: 8, height: 8 },
          },
        },
        connector: { name: 'rounded', args: { radius: 8 } },
        router: { name: 'manhattan', args: { padding: 20 } },
      };
    });

    return { nodes: x6Nodes, edges: x6Edges };
  },

  /**
   * X6 cells 数据 -> 旧格式 FlowChartJson
   * @param {Graph} graph - X6 Graph 实例
   * @returns {object} 旧格式 FlowChartJson
   */
  fromX6Data(graph) {
    const cells = graph.getCells();
    const nodes = [];
    const edges = [];
    const endpoints = [];

    cells.forEach(cell => {
      if (cell.isNode()) {
        const data = cell.getData() || {};
        const props = data.properties || {};
        const cellPorts = cell.getPorts();
        const targets = [];
        const sources = [];

        cellPorts.forEach(port => {
          if (port.group === 'in') {
            targets.push(port.id);
          } else if (port.group === 'out') {
            sources.push(port.id);
          }
          endpoints.push({
            id: port.id,
            data: { value: port.group === 'in' ? '输入' : '输出' },
          });
        });

        nodes.push({
          id: cell.id,
          position: { left: cell.position.x, top: cell.position.y },
          points: { targets, sources },
          data: {
            icon: data.icon || '',
            text: data.text || '',
            pluginId: data.pluginId || '',
            ...data,
          },
          properties: props,
        });
      }

      if (cell.isEdge()) {
        const sourcePort = cell.getSourcePort();
        const targetPort = cell.getTargetPort();
        if (sourcePort && targetPort) {
          edges.push(`${sourcePort}${CONNECTORSEPARATESYMBOL}${targetPort}`);
        }
      }
    });

    return {
      nodes,
      endpoints,
      edges,
      head: FlowChartJson.head || '',
    };
  },
};
