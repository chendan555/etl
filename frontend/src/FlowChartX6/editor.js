/* eslint-disable import/no-cycle */
import { Message } from 'element-ui';
import { register } from '@antv/x6-vue-shape';
import { createGraph, getGraph, disposeGraph } from './graph';
import model from './model';
import Node from './Node.vue';
import { EventCenter } from './EventCenter';
import { CONNECTORSEPARATESYMBOL } from './const';
import API from '@/api/index';
import { createUuid } from '@/FlowChart/Utils';

// 注册 X6 Vue 节点形状
register({
  shape: 'x6-vue-node',
  component: Node,
  width: 170,
  height: 34,
});

let container = null;
let graph = null;

// FlowChartX6 引用会在 index.js 中设置
let FlowChartX6 = null;
export function setFlowChartRef(ref) {
  FlowChartX6 = ref;
}

function emitCommandListEmpty() {
  if (FlowChartX6) FlowChartX6.emit('commandListEmpty');
}

function emitAddCommand() {
  if (FlowChartX6) FlowChartX6.emit('addCommand');
}

function emitShowNodeData(nodeData) {
  const text = nodeData.hasOwnProperty('text')
    ? nodeData.text
    : nodeData.data && nodeData.data.text;
  if (['结束'].includes(text)) {
    Message.warning(`${text}为虚拟点，无详情！`);
    return;
  }
  if (FlowChartX6) FlowChartX6.emit('showNodeData', nodeData);
}

/**
 * 根据算子类型生成端口配置
 */
function getPortsByPluginId(pluginId) {
  const isMultiInput = ['MergeRows', 'MergeJoin'].includes(pluginId);
  const ports = [];

  if (isMultiInput) {
    ports.push(
      { id: `target-${createUuid()}`, group: 'in', attrs: { circle: { r: 6, magnet: true, stroke: 'transparent', fill: 'transparent', strokeWidth: 1 } } },
      { id: `target-${createUuid()}`, group: 'in', attrs: { circle: { r: 6, magnet: true, stroke: 'transparent', fill: 'transparent', strokeWidth: 1 } } }
    );
  } else {
    ports.push(
      { id: `target-${createUuid()}`, group: 'in', attrs: { circle: { r: 6, magnet: true, stroke: 'transparent', fill: 'transparent', strokeWidth: 1 } } }
    );
  }

  ports.push(
    { id: `source-${createUuid()}`, group: 'out', attrs: { circle: { r: 4, magnet: true, stroke: '#00cdea', fill: '#fff', strokeWidth: 2 } } }
  );

  return ports;
}

/**
 * 生成节点并添加到画布
 */
function generateNode(left, top, id, nodeData) {
  const ports = getPortsByPluginId(nodeData.pluginId);

  graph.addNode({
    id,
    x: left,
    y: top,
    width: 170,
    height: 34,
    shape: 'x6-vue-node',
    data: {
      ...nodeData,
      properties: nodeData.properties || { ruleForm: { stepName: nodeData.text } },
      nodeId: id,
    },
    ports,
  });
}

/**
 * 根据拖放动作添加节点
 */
function addNodeByAction(action, position, nodeData, properties) {
  const scale = graph.zoom();
  const graphContainer = graph.container;
  const containerRect = graphContainer.getBoundingClientRect();
  const id = `node-${createUuid()}`;
  let left = (position.pageX - containerRect.left) / scale;
  let top = (position.pageY - containerRect.top) / scale;

  if (action === 'drag') {
    left -= 86;
    top -= 18;
  }

  const targetEndpoints = [];
  const sourceEndpoints = [];
  const portConfig = getPortsByPluginId(nodeData.pluginId);
  portConfig.forEach(p => {
    if (p.group === 'in') {
      targetEndpoints.push({ id: p.id, data: { value: '输入' } });
    } else {
      sourceEndpoints.push({ id: p.id, data: { value: '输出' } });
    }
  });

  return API.addStepName({
    nodeData: nodeData.hasOwnProperty('data') ? nodeData.data : nodeData,
  }).then(data => {
    const stepName = data.data.data || data.data.message || '步骤';
    if (nodeData.hasOwnProperty('data')) {
      nodeData.data.text = stepName;
      if (nodeData.ruleForm) {
        nodeData.ruleForm.stepName = stepName;
      }
    } else {
      nodeData.text = stepName;
    }

    generateNode(left, top, id, nodeData.hasOwnProperty('data') ? nodeData.data : nodeData);

    model.addNode({
      id,
      points: {
        targets: targetEndpoints.map(p => p.id),
        sources: sourceEndpoints.map(p => p.id),
      },
      position: { left, top },
      data: nodeData.hasOwnProperty('data') ? nodeData.data : nodeData,
      properties: properties ? properties : {},
    });

    [...targetEndpoints, ...sourceEndpoints].forEach(point => {
      model.addEndpoint(point);
    });

    return id;
  });
}

function addNodeByDrag(position, nodeData) {
  return addNodeByAction('drag', position, nodeData);
}

function addNodeByCopy(position, nodeData) {
  const originalNode = model.getNodeDataByNodeId(nodeData.nodeId);
  const properties = originalNode && originalNode.properties
    ? JSON.parse(JSON.stringify(originalNode.properties))
    : {};
  return addNodeByAction('copy', position, nodeData, properties);
}

/**
 * 根据数据渲染节点（已有端点数据，不调用 API）
 */
function addNodeByData(nodeData) {
  generateNode(nodeData.position.left, nodeData.position.top, nodeData.id, nodeData);
}

function addNodeByExtraData(nodeData, nodeEdgesData, nodeEndpointsData) {
  model.addNode(nodeData);
  nodeEdgesData.forEach(edge => model.addEdge(edge));
  nodeEndpointsData.forEach(endpoint => model.addEndpoint(endpoint));
  addNodeByData(nodeData);
  addConnectorsByEdges(nodeEdgesData);
}

function addNodesByData() {
  const { nodes } = model.getData();
  nodes.forEach(n => addNodeByData(n));
}

/**
 * 根据边数据生成连线
 */
function addConnectorsByEdges(edges) {
  edges.forEach(str => {
    const parts = str.split(CONNECTORSEPARATESYMBOL);
    graph.addEdge({
      source: { port: parts[0] },
      target: { port: parts[1] },
    });
  });
}

function addConnectorsByData() {
  const { edges } = model.getData();
  addConnectorsByEdges(edges);
}

/**
 * 移除节点
 */
function removeNode(nodeId) {
  const nodeData = model.getNodeDataByNodeId(nodeId);
  if (!nodeData) return [null, [], []];
  const relatedPointIds = [...(nodeData.points.sources || []), ...(nodeData.points.targets || [])];
  const nodeEdgesData = model.getEdgesByPointIds(relatedPointIds);
  const nodeEndpointsData = model.getEndpointsByPointIds(relatedPointIds);
  model.removeNodeByNodeId(nodeId);
  model.removeEdgesByPointIds(relatedPointIds);
  model.removeEndpointsByPointIds(relatedPointIds);
  graph.removeNode(nodeId);
  return [nodeData, nodeEdgesData, nodeEndpointsData];
}

function clearAll() {
  if (graph) {
    graph.clearCells();
  }
}

/**
 * 仅记录边到 model（不操作 graph，因为 X6 已经在画布上创建了边）
 */
function addConnector(edge) {
  model.addEdge(edge);
}

/**
 * 从 graph 和 model 中移除边
 */
function removeConnector(edgeId) {
  const edge = graph.getCellById(edgeId);
  if (edge) {
    const sourcePort = edge.getSourcePort();
    const targetPort = edge.getTargetPort();
    const edgeStr = `${sourcePort}${CONNECTORSEPARATESYMBOL}${targetPort}`;
    model.removeEdge(edgeStr);
    graph.removeEdge(edgeId);
    return edgeStr;
  }
  return null;
}

/**
 * 通过端点 UUID 移除边
 */
function removeConnectorByUuids(uuids) {
  const edgeStr = uuids.join(CONNECTORSEPARATESYMBOL);
  const cells = graph.getEdges();
  const edge = cells.find(c => {
    const sp = c.getSourcePort();
    const tp = c.getTargetPort();
    return sp && tp && `${sp}${CONNECTORSEPARATESYMBOL}${tp}` === edgeStr;
  });
  if (edge) {
    model.removeEdge(edgeStr);
    graph.removeEdge(edge.getId());
  }
}

function changeNodePosition(id, pos) {
  const node = graph.getCellById(id);
  if (node) {
    node.position(pos.left, pos.top);
    model.changeNodePos(id, pos);
  }
}

function renameNode(nodeId, value) {
  const node = graph.getCellById(nodeId);
  if (node) {
    const data = node.getData() || {};
    data.text = value;
    node.setData(data, { overwrite: true });
    model.changeNodeValue(nodeId, value);
    const modelNode = model.getNodeDataByNodeId(nodeId);
    if (modelNode) {
      if (modelNode.data) modelNode.data.text = value;
      if (modelNode.properties && modelNode.properties.ruleForm) {
        modelNode.properties.ruleForm.stepName = value;
      }
    }
  }
}

function getScale() {
  return graph ? graph.zoom() : 1;
}

/**
 * 仅记录边到 model（用于 execAddConnectorCommand）
 */
function execAddConnectorCommand(uuids) {
  const edgeStr = uuids.join(CONNECTORSEPARATESYMBOL);
  model.addEdge(edgeStr);
}

function execModel(nodeId, nodeState) {
  const node = graph.getCellById(nodeId);
  if (node) {
    const data = node.getData() || {};
    data.nodeState = nodeState;
    node.setData(data, { overwrite: true });
  }
}

/**
 * 连接线激活动画
 */
function blingConnectors(type) {
  const edges = graph.getEdges();
  edges.forEach(edge => {
    if (type === 'add') {
      edge.setAttrByPath('line/stroke', '#00cdea');
      edge.setAttrByPath('line/strokeDasharray', '5');
    } else {
      edge.setAttrByPath('line/stroke', '#b4bdc5');
      edge.setAttrByPath('line/strokeDasharray', undefined);
    }
  });
}

/**
 * 初始化
 */
function init() {
  container = document.getElementById('mainContainer');
  graph = createGraph(container);

  // 绑定画布事件
  graph.on('node:click', ({ node }) => {
    const data = node.getData() || {};
    data.nodeId = node.id;
    if (FlowChartX6) FlowChartX6.emit('selectNode', data);
  });

  graph.on('node:dblclick', ({ node }) => {
    const data = node.getData() || {};
    data.nodeId = node.id;
    emitShowNodeData(data);
  });

  graph.on('node:contextmenu', ({ e, node }) => {
    e.preventDefault();
    e.stopPropagation();
    const data = node.getData() || {};
    data.nodeId = node.id;
    if (FlowChartX6) {
      FlowChartX6.emit('contextmenu', {
        type: 'node',
        position: { left: e.pageX, top: e.pageY },
        component: node.id,
        nodeData: data,
      });
    }
  });

  graph.on('edge:contextmenu', ({ e, edge }) => {
    e.preventDefault();
    e.stopPropagation();
    if (FlowChartX6) {
      FlowChartX6.emit('contextmenu', {
        type: 'edge',
        position: { left: e.pageX, top: e.pageY },
        component: edge,
      });
    }
  });

  graph.on('blank:contextmenu', ({ e }) => {
    e.preventDefault();
    if (FlowChartX6) {
      FlowChartX6.emit('contextmenu', {
        type: 'blank',
        position: { left: e.pageX, top: e.pageY },
      });
    }
  });

  graph.on('blank:click', () => {
    EventCenter.emit('document.click');
  });

  // 连线事件
  graph.on('edge:connected', ({ edge }) => {
    const sourcePort = edge.getSourcePort();
    const targetPort = edge.getTargetPort();
    if (sourcePort && targetPort) {
      execAddConnectorCommand([sourcePort, targetPort]);
    }
  });

  // 节点移动事件
  graph.on('node:moved', ({ node }) => {
    const pos = node.position();
    model.changeNodePos(node.id, { left: pos.x, top: pos.y });
  });
}

const editor = {
  emitCommandListEmpty,
  emitAddCommand,
  emitShowNodeData,
  addNodeByDrag,
  addNodeByCopy,
  addNodeByExtraData,
  addConnector,
  changeNodePosition,
  init,
  render() {
    addNodesByData();
    addConnectorsByData();
  },
  getScale,
  clearAll,
  removeNode,
  removeConnector,
  removeConnectorByUuids,
  renameNode,
  execAddConnectorCommand,
  execModel,
  blingConnectors,
  getGraph() {
    return graph;
  },
};

export default editor;
