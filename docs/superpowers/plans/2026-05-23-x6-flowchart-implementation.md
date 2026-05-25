# X6 流程图画布模块实现计划

> **面向 AI 代理的工作者：** 必需子技能：使用 superpowers:subagent-driven-development（推荐）或 superpowers:executing-plans 逐任务实现此计划。步骤使用复选框（`- [ ]`）语法来跟踪进度。

**目标：** 用 AntV X6 2.x 完整复刻现有 jsPlumb 流程图画布模块，独立路由并行存在，读写同一份后端数据。

**架构：** 新建 `FlowChartX6/` 画布核心 + `views/FlowChartX6/` 页面视图，公共 API 对齐旧模块接口。路由通过 `router.ts` 的自动发现机制注册（`views/FlowChartX6/index.vue` 声明 `name` + `meta` 即可）。

**技术栈：** Vue 2 + Element UI + AntV X6 2.x + @antv/x6-vue-shape

---

## 文件结构

### 新建文件

| 文件 | 职责 |
|------|------|
| `src/FlowChartX6/const.js` | 常量定义 |
| `src/FlowChartX6/style.js` | 节点/连线样式配置 |
| `src/FlowChartX6/EventCenter.js` | 发布订阅事件中心 |
| `src/FlowChartX6/graph.js` | X6 Graph 实例创建 |
| `src/FlowChartX6/model.js` | 数据模型 + 旧格式双向转换 |
| `src/FlowChartX6/Node.vue` | 节点 Vue 组件 |
| `src/FlowChartX6/editor.js` | 核心引擎：节点/连线增删改 |
| `src/FlowChartX6/Command.js` | 命令模式（撤销/重做） |
| `src/FlowChartX6/contextMenu.js` | 右键菜单 |
| `src/FlowChartX6/index.js` | 公共 API 门面 |
| `src/views/FlowChartX6/index.vue` | 主页面（左/中/右布局） |
| `src/views/FlowChartX6/ComponentsTree.vue` | 左侧组件树（复制+微调） |
| `src/views/FlowChartX6/Terminal.vue` | 底部终端（复制） |
| `src/views/FlowChartX6/Attr.vue` | 右侧属性面板（复制+替换引用） |
| `src/views/FlowChartX6/config.js` | 工具栏配置（复制） |

### 修改文件

| 文件 | 改动 |
|------|------|
| `frontend/package.json` | 添加 `@antv/x6` 和 `@antv/x6-vue-shape` 依赖 |

---

## 任务 1：安装 X6 依赖

**文件：**
- 修改：`frontend/package.json`

- [ ] **步骤 1：安装 X6 核心包和 Vue shape 包**

```bash
cd d:\project\ai\vs\03\frontend
npm install @antv/x6@2.18.1 @antv/x6-vue-shape@2.2.3
```

注意：如果 `@antv/x6-vue-shape@2.2.3` 安装失败或与 Vue 2 不兼容，改用备选方案：用 X6 的 `HTML` shape 手动挂载 Vue 实例（见任务 5 步骤 3 的备选代码）。

- [ ] **步骤 2：验证安装**

```bash
cd d:\project\ai\vs\03\frontend
npm ls @antv/x6 @antv/x6-vue-shape
```

预期：两个包都正确安装，无 peer dependency 错误。

- [ ] **步骤 3：Commit**

```bash
cd d:\project\ai\vs\03
git add frontend/package.json frontend/package-lock.json
git commit -m "feat: install @antv/x6 and @antv/x6-vue-shape for new flow chart module"
```

---

## 任务 2：创建基础模块（const.js + style.js + EventCenter.js）

**文件：**
- 创建：`frontend/src/FlowChartX6/const.js`
- 创建：`frontend/src/FlowChartX6/style.js`
- 创建：`frontend/src/FlowChartX6/EventCenter.js`

- [ ] **步骤 1：创建 const.js**

```js
// frontend/src/FlowChartX6/const.js
export const CONNECTORSEPARATESYMBOL = '&&';
export const PLACEHOLDER = 'placeholder';
```

- [ ] **步骤 2：创建 style.js**

```js
// frontend/src/FlowChartX6/style.js

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
```

- [ ] **步骤 3：创建 EventCenter.js**

```js
// frontend/src/FlowChartX6/EventCenter.js

function Event() {
  this.clientList = {};
}

Event.prototype = {
  on(key, fn) {
    if (!this.clientList[key]) {
      this.clientList[key] = [];
    }
    this.clientList[key].push(fn);
  },
  emit(...args) {
    const key = Array.prototype.shift.call(args);
    const fns = this.clientList[key];
    if (fns && fns.length !== 0) {
      fns.forEach(fn => {
        fn.apply(this, args);
      });
    }
    return false;
  },
  remove(key, fn) {
    const fns = this.clientList[key];
    if (fns) {
      if (!fn) {
        fns.length = 0;
      } else {
        for (let l = fns.length - 1; l >= 0; l -= 1) {
          const func = fns[l];
          if (func === fn) {
            fns.splice(l, 1);
          }
        }
      }
    }
    return false;
  },
};

export const EventCenter = new Event();

export default function extendsEvent(obj) {
  const eventInstance = new Event();
  for (const key in eventInstance) {
    obj[key] = eventInstance[key];
  }
}
```

- [ ] **步骤 4：Commit**

```bash
cd d:\project\ai\vs\03
git add frontend/src/FlowChartX6/const.js frontend/src/FlowChartX6/style.js frontend/src/FlowChartX6/EventCenter.js
git commit -m "feat: add FlowChartX6 base modules (const, style, EventCenter)"
```

---

## 任务 3：创建 graph.js（X6 Graph 实例）

**文件：**
- 创建：`frontend/src/FlowChartX6/graph.js`

- [ ] **步骤 1：创建 graph.js**

```js
// frontend/src/FlowChartX6/graph.js
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
    // Ctrl/Command + 滚轮缩放
    mouseWheel: {
      enabled: true,
      modifiers: ['ctrl', 'meta'],
      factor: 1.1,
    },
    // 空格 + 拖拽平移
    panning: {
      enabled: true,
      modifiers: ['space'],
    },
    // 连线配置
    connecting: {
      connector: 'rounded',
      router: 'manhattan',
      anchor: 'center',
      connectionPoint: 'anchor',
      allowBlank: false,
      allowLoop: false,
      allowMulti: true,
      // 连线中点添加箭头
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
    // 框选
    selecting: {
      enabled: true,
      showNodeSelectionBox: true,
    },
    // 快捷键
    keyboard: {
      enabled: true,
    },
    // 默认节点样式
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
    // 默认连线样式
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
```

- [ ] **步骤 2：Commit**

```bash
cd d:\project\ai\vs\03
git add frontend/src/FlowChartX6/graph.js
git commit -m "feat: add FlowChartX6 graph.js - X6 Graph instance factory"
```

---

## 任务 4：创建 model.js（数据模型 + 双向转换）

**文件：**
- 创建：`frontend/src/FlowChartX6/model.js`

- [ ] **步骤 1：创建 model.js**

```js
// frontend/src/FlowChartX6/model.js
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
        (node.points.targets || []).forEach((targetId, index) => {
          ports.push({
            id: targetId,
            group: 'in',
          });
        });
        (node.points.sources || []).forEach((sourceId, index) => {
          ports.push({
            id: sourceId,
            group: 'out',
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
    const allEndpoints = new Set();

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
          allEndpoints.add(port.id);
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
```

- [ ] **步骤 2：Commit**

```bash
cd d:\project\ai\vs\03
git add frontend/src/FlowChartX6/model.js
git commit -m "feat: add FlowChartX6 model.js with bidirectional data conversion"
```

---

## 任务 5：创建 Node.vue（节点组件）

**文件：**
- 创建：`frontend/src/FlowChartX6/Node.vue`

- [ ] **步骤 1：创建 Node.vue**

```vue
<!-- frontend/src/FlowChartX6/Node.vue -->
<template>
  <el-tooltip :disabled="tooltipDisabled" placement="right">
    <div slot="content" class="fy_tooltip_content">节点信息：{{ text }}</div>
    <div
      ref="nodediv"
      class="x6_node"
      :class="{ 'x6_node_selected': selected }"
      @mousedown="tooltipDisabled = true"
      @mouseup="tooltipDisabled = false"
      @click.stop="setCrtNode"
    >
      <span class="blue_line"></span>
      <div class="content">
        <div class="left">
          <SvgIcon :iconClass="svgName" style="fontSize: 18px;"></SvgIcon>
          <span class="text">{{ text }}</span>
        </div>
        <i v-if="nodeState === 'loading'" class="el-icon-loading node-state"></i>
        <i v-else-if="nodeState === 'success'" class="el-icon-circle-check node-state"></i>
        <i v-else-if="nodeState === 'failed'" class="el-icon-circle-close node-state"></i>
        <i v-else-if="nodeState === 'warning'" class="el-icon-warning-outline node-state"></i>
        <i v-else-if="icon && icon.includes('TOP')" class="el-icon-coin node-state"></i>
        <i v-else></i>
      </div>
    </div>
  </el-tooltip>
</template>

<script>
import SvgIcon from '@/components/SvgIcon/index';

export default {
  name: 'X6Node',
  components: { SvgIcon },
  props: {
    nodeState: { type: String, default: '' },
    icon: { type: String, default: '' },
    text: { type: String, default: '' },
    selected: { type: Boolean, default: false },
  },
  data() {
    return {
      tooltipDisabled: false,
    };
  },
  computed: {
    svgName() {
      if (this.icon) {
        const startStr = 'ui/images/';
        const endStr = '.svg?';
        const startIndex = this.icon.indexOf(startStr);
        const endIndex = this.icon.indexOf(endStr);
        if (startIndex > -1 && endIndex > -1) {
          return this.icon.slice(startIndex + startStr.length, endIndex);
        }
      }
      return 'PTHJB';
    },
  },
  methods: {
    setCrtNode() {
      // 触发自定义事件，由 editor 监听处理
      this.$emit('node-click');
    },
  },
};
</script>

<style lang="scss" scoped>
.x6_node {
  position: relative;
  width: 100%;
  height: 100%;
  border: 1px solid #107eef;
  border-radius: 4px;
  font-size: 14px;
  cursor: move;
  box-sizing: border-box;
  overflow: hidden;
  background: #fff;
}
.x6_node.x6_node_selected {
  background: #0f67c1;
}
.x6_node .blue_line {
  position: absolute;
  top: 2px;
  left: 3px;
  border-radius: 2px;
  padding: 14px 2px;
  background: #1c9bec;
}
.x6_node .content {
  display: flex;
  align-items: center;
  padding-left: 10px;
  justify-content: space-between;
  height: 100%;
}
.x6_node .content .left {
  display: flex;
  align-items: center;
}
.x6_node .content .text {
  margin-left: 5px;
  color: #000;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.x6_node_selected .content .text {
  color: #fff;
}
.x6_node .content i {
  position: relative;
  top: -10px;
  color: rgb(0, 205, 234);
  margin-left: 15px;
  margin-right: 15px;
}
.node-state {
  top: -8px !important;
  font-size: 20px;
  margin-left: 8px !important;
}
.el-icon-loading {
  color: #909399 !important;
}
.el-icon-circle-check {
  color: #67c23a !important;
}
.el-icon-circle-close {
  color: #f56c6c !important;
}
.el-icon-warning-outline {
  color: #e6a23c !important;
}
.el-icon-coin {
  color: #3d9e0c !important;
}
</style>
```

**备选方案（如果 `@antv/x6-vue-shape` 不兼容 Vue 2）：**

改用 X6 的 `HTML` shape，手动挂载 Vue 实例：

```js
// 在 editor.js 中注册节点时使用
import { Shape } from '@antv/x6';
import Vue from 'vue';
import Node from './Node.vue';

const X6VueNode = Shape.HTML.define({
  width: 170,
  height: 34,
  effect: ['data'],
  html(cell) {
    const container = document.createElement('div');
    const Component = Vue.extend(Node);
    const instance = new Component({
      propsData: {
        icon: cell.getData()?.icon || '',
        text: cell.getData()?.text || '',
        nodeState: cell.getData()?.nodeState || '',
      },
    }).$mount();
    container.appendChild(instance.$el);
    return container;
  },
});
```

- [ ] **步骤 2：Commit**

```bash
cd d:\project\ai\vs\03
git add frontend/src/FlowChartX6/Node.vue
git commit -m "feat: add FlowChartX6 Node.vue component"
```

---

## 任务 6：创建 editor.js（核心引擎）

**文件：**
- 创建：`frontend/src/FlowChartX6/editor.js`

- [ ] **步骤 1：创建 editor.js**

```js
// frontend/src/FlowChartX6/editor.js
import Vue from 'vue';
import { Message } from 'element-ui';
import { createGraph, getGraph, disposeGraph } from './graph';
import model from './model';
import Node from './Node.vue';
import { EventCenter } from './EventCenter';
import { CONNECTORSEPARATESYMBOL } from './const';
import API from '@/api/index';
import { createUuid } from '@/FlowChart/Utils';

let container = null;
let graph = null;

function emitCommandListEmpty() {
  FlowChartX6.emit('commandListEmpty');
}

function emitAddCommand() {
  FlowChartX6.emit('addCommand');
}

function emitShowNodeData(nodeData) {
  const text = nodeData.hasOwnProperty('text') ? nodeData.text : (nodeData.data && nodeData.data.text);
  if (['结束'].includes(text)) {
    Message.warning(`${text}为虚拟点，无详情！`);
    return;
  }
  FlowChartX6.emit('showNodeData', nodeData);
}

// FlowChartX6 引用会在 index.js 中设置
let FlowChartX6 = null;
export function setFlowChartRef(ref) {
  FlowChartX6 = ref;
}

/**
 * 根据算子类型生成端口配置
 */
function getPortsByPluginId(pluginId) {
  const isMultiInput = ['MergeRows', 'MergeJoin'].includes(pluginId);
  const ports = { groups: { in: { position: 'top' }, out: { position: 'bottom' } }, items: [] };

  if (isMultiInput) {
    ports.items.push(
      { id: `target-${createUuid()}`, group: 'in', attrs: { circle: { r: 6, magnet: true, stroke: 'transparent', fill: 'transparent' } } },
      { id: `target-${createUuid()}`, group: 'in', attrs: { circle: { r: 6, magnet: true, stroke: 'transparent', fill: 'transparent' } } }
    );
  } else {
    ports.items.push(
      { id: `target-${createUuid()}`, group: 'in', attrs: { circle: { r: 6, magnet: true, stroke: 'transparent', fill: 'transparent' } } }
    );
  }

  ports.items.push(
    { id: `source-${createUuid()}`, group: 'out', attrs: { circle: { r: 4, magnet: true, stroke: '#00cdea', fill: '#fff', strokeWidth: 2 } } }
  );

  return ports;
}

/**
 * 生成节点并添加到画布
 */
function generateNode(left, top, id, nodeData) {
  const ports = getPortsByPluginId(nodeData.pluginId);

  const node = graph.addNode({
    id,
    x: left,
    y: top,
    width: 170,
    height: 34,
    shape: 'vue-shape',
    data: {
      ...nodeData,
      properties: nodeData.properties || { ruleForm: { stepName: nodeData.text } },
      nodeId: id,
    },
    ports: ports.items,
  });

  return node;
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

  // 从 model 中获取端点数据
  const targetEndpoints = [];
  const sourceEndpoints = [];
  const portConfig = getPortsByPluginId(nodeData.pluginId);
  portConfig.items.forEach(p => {
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
 * 根据数据渲染节点
 */
function addNodeByData(nodeData) {
  const { endpoints } = model.getData();
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
 * 用于 edge:connected 事件和 undo 恢复
 */
function addConnector(edge) {
  model.addEdge(edge);
}

/**
 * 从 graph 和 model 中移除边
 * @param {string} edgeId - X6 edge cell id
 * @returns {string|null} 边字符串 "sourcePort&&targetPort"
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
 * 通过端点 UUID 移除边（用于 undo AddConnectorCommand）
 * @param {string[]} uuids - [sourcePortId, targetPortId]
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
    // 同步更新 model 中的数据
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

function execAddConnectorCommand(uuids) {
  // 仅记录到 model，graph 中已有该边（来自用户拖拽或 undo 恢复）
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
 * 连接线激活动画（运行时高亮）
 */
function blingConnectors(type) {
  const edges = graph.getEdges();
  edges.forEach(edge => {
    const line = edge.getAttrByPath('line');
    if (type === 'add') {
      edge.setAttrByPath('line/stroke', '#00cdea');
      edge.setAttrByPath('line/strokeDasharray', '5');
      edge.setAttrByPath('line/style/animation', 'ring 2s linear infinite');
    } else {
      edge.setAttrByPath('line/stroke', '#b4bdc5');
      edge.setAttrByPath('line/strokeDasharray', undefined);
      edge.setAttrByPath('line/style/animation', undefined);
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
    FlowChartX6.emit('selectNode', data);
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
    FlowChartX6.emit('contextmenu', {
      type: 'node',
      position: { left: e.pageX, top: e.pageY },
      component: node.id,
      nodeData: data,
    });
  });

  graph.on('edge:contextmenu', ({ e, edge }) => {
    e.preventDefault();
    e.stopPropagation();
    FlowChartX6.emit('contextmenu', {
      type: 'edge',
      position: { left: e.pageX, top: e.pageY },
      component: edge,
    });
  });

  graph.on('blank:contextmenu', ({ e }) => {
    e.preventDefault();
    FlowChartX6.emit('contextmenu', {
      type: 'blank',
      position: { left: e.pageX, top: e.pageY },
    });
  });

  graph.on('blank:click', () => {
    EventCenter.emit('document.click');
  });

  // 连线事件（用户拖拽创建连线时触发）
  // 注意：execAddConnectorCommand 内部已调用 model.addEdge，不要重复添加
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
```

- [ ] **步骤 2：Commit**

```bash
cd d:\project\ai\vs\03
git add frontend/src/FlowChartX6/editor.js
git commit -m "feat: add FlowChartX6 editor.js - core rendering engine"
```

---

## 任务 7：创建 Command.js（撤销/重做）

**文件：**
- 创建：`frontend/src/FlowChartX6/Command.js`

- [ ] **步骤 1：创建 Command.js**

```js
// frontend/src/FlowChartX6/Command.js
import editor from './editor';

const commandStack = {
  commandList: [],
  undo() {
    const command = this.commandList.pop();
    if (command) {
      command.undo();
      if (this.commandList.length === 0) {
        editor.emitCommandListEmpty();
      }
    }
  },
};

class Command {
  exec() { throw new Error('请实现exec方法！'); }
  undo() { throw new Error('请实现undo方法！'); }
}

class AddNodeCommand extends Command {
  exec(position, elId) {
    this.id = editor.addNodeByDrag(position, elId);
  }
  undo() {
    editor.removeNode(this.id);
  }
}

class RemoveNodeCommand extends Command {
  exec(nodeId) {
    const [nodeData, nodeEdgesData, nodeEndpointsData] = editor.removeNode(nodeId);
    this.nodeData = nodeData;
    this.nodeEdgesData = nodeEdgesData;
    this.nodeEndpointsData = nodeEndpointsData;
  }
  undo() {
    if (this.nodeData) {
      editor.addNodeByExtraData(this.nodeData, this.nodeEdgesData, this.nodeEndpointsData);
    }
  }
}

class AddConnectorCommand extends Command {
  exec(uuids) {
    this.uuids = uuids;
  }
  undo() {
    editor.removeConnectorByUuids(this.uuids);
  }
}

class RemoveConnectorCommand extends Command {
  exec(connector) {
    this.edge = editor.removeConnector(connector);
  }
  undo() {
    editor.addConnector(this.edge);
  }
}

class MoveNodeCommand extends Command {
  exec(id, oldPos) {
    this.id = id;
    this.oldPos = oldPos;
  }
  undo() {
    editor.changeNodePosition(this.id, this.oldPos);
  }
}

class PasteNodeCommand extends Command {
  exec(position, nodeData) {
    this.id = editor.addNodeByCopy(position, nodeData);
  }
  undo() {
    editor.removeNode(this.id);
  }
}

class RenameNodeCommand extends Command {
  exec(nodeId, oldValue, newValue) {
    this.nodeId = nodeId;
    this.oldValue = oldValue;
    editor.renameNode(nodeId, newValue);
  }
  undo() {
    editor.renameNode(this.nodeId, this.oldValue);
  }
}

function undo() {
  commandStack.undo();
}

export default function exec(Cmd, ...args) {
  const cmd = new Cmd();
  cmd.exec(...args);
  commandStack.commandList.push(cmd);
  editor.emitAddCommand();
}

export {
  undo,
  AddNodeCommand,
  RemoveNodeCommand,
  AddConnectorCommand,
  RemoveConnectorCommand,
  MoveNodeCommand,
  RenameNodeCommand,
  PasteNodeCommand,
};
```

- [ ] **步骤 2：Commit**

```bash
cd d:\project\ai\vs\03
git add frontend/src/FlowChartX6/Command.js
git commit -m "feat: add FlowChartX6 Command.js - undo/redo pattern"
```

---

## 任务 8：创建 contextMenu.js（右键菜单）

**文件：**
- 创建：`frontend/src/FlowChartX6/contextMenu.js`

- [ ] **步骤 1：创建 contextMenu.js**

```js
// frontend/src/FlowChartX6/contextMenu.js
import { Message } from 'element-ui';
import { EventCenter } from './EventCenter';
import exec, { PasteNodeCommand, RemoveNodeCommand, RemoveConnectorCommand } from './Command';
import editor from './editor';

let ul = null;
let copiedNodeData = '';
let contextMenuPos = {};

class MenuItem {
  constructor(opt) {
    this.text = opt.text;
    this.icon = opt.icon;
    this.role = opt.role;
    this.clickHandle = opt.clickHandle;
    this.init();
  }

  init() {
    const li = document.createElement('li');
    li.className = 'fy_contextMenu_item';
    li.dataset.role = this.role;
    li.innerHTML = `<i class="${this.icon}"></i><span>${this.text}</span>`;
    this.el = li;
    this.el.onclick = (e) => {
      e.stopPropagation();
      this.clickHandle(e);
    };
  }

  getEl() { return this.el; }
}

function hide() {
  ul.style.display = 'none';
}

function generateMenuDivideLine() {
  const divide = document.createElement('div');
  divide.style.cssText = 'height:1px;background:#efefef;';
  ul.appendChild(divide);
}

function generateMenuItem(opt) {
  const item = new MenuItem(opt);
  ul.appendChild(item.getEl());
}

function deleteHandle(comp) {
  if (comp) {
    if (typeof comp === 'object') {
      exec(RemoveConnectorCommand, comp);
    } else if (typeof comp === 'string') {
      exec(RemoveNodeCommand, comp);
    }
  }
}

function generateNodeMenu(currentComponent, currentComponentData) {
  generateMenuItem({
    text: '复制',
    icon: 'el-icon-copy-document',
    role: 'copy',
    clickHandle: () => {
      copiedNodeData = currentComponentData;
      Message.success({ message: '复制成功！' });
    },
  });
  generateMenuItem({
    text: '删除',
    icon: 'el-icon-delete',
    role: 'delete',
    clickHandle: () => deleteHandle(currentComponent),
  });
  generateMenuDivideLine();
  generateMenuItem({
    text: '查看数据',
    icon: 'el-icon-view',
    role: 'view',
    clickHandle: () => editor.emitShowNodeData(currentComponentData),
  });
}

function generateConnectorMenu(currentComponent) {
  generateMenuItem({
    text: '删除',
    icon: 'el-icon-delete',
    role: 'delete',
    clickHandle: () => deleteHandle(currentComponent),
  });
}

function generateBgMenu() {
  generateMenuItem({
    text: '粘贴节点',
    icon: 'el-icon-download',
    role: 'paste',
    clickHandle: () => {
      if (copiedNodeData) {
        exec(PasteNodeCommand, { pageX: contextMenuPos.left, pageY: contextMenuPos.top }, copiedNodeData);
      } else {
        Message({ type: 'warning', message: '请复制节点后再粘贴。' });
      }
    },
  });
}

function show(menuInfo) {
  const { type, position, component, nodeData } = menuInfo;
  ul.innerHTML = '';
  ul.style.top = `${position.top}px`;
  ul.style.left = `${position.left}px`;
  ul.style.display = 'block';
  contextMenuPos = position;

  if (type === 'node') generateNodeMenu(component, nodeData);
  if (type === 'edge') generateConnectorMenu(component);
  if (type === 'blank') generateBgMenu();
}

function init() {
  ul = document.createElement('ul');
  ul.className = 'fy_contextMenu';
  document.body.appendChild(ul);
  ul.onmouseup = hide;

  hide();

  EventCenter.on('document.click', () => {
    hide();
  });
}

export default { init, show };
```

- [ ] **步骤 2：Commit**

```bash
cd d:\project\ai\vs\03
git add frontend/src/FlowChartX6/contextMenu.js
git commit -m "feat: add FlowChartX6 contextMenu.js - right-click menu"
```

---

## 任务 9：创建 index.js（公共 API 门面）

**文件：**
- 创建：`frontend/src/FlowChartX6/index.js`

- [ ] **步骤 1：创建 index.js**

```js
// frontend/src/FlowChartX6/index.js
import extendsEvent, { EventCenter } from './EventCenter';
import editor from './editor';
import model from './model';
import exec, { undo, AddNodeCommand } from './Command';
import contextMenu from './contextMenu';
import { setFlowChartRef } from './editor';

const FlowChartX6 = {
  setContainer(id) {
    const el = document.getElementById(id);
    if (el) el.innerHTML = '';
    this.init();
  },

  clearContainer(id) {
    const el = document.getElementById(id);
    if (el) el.innerHTML = '';
  },

  addNode(position, elId) {
    exec(AddNodeCommand, position, elId);
  },

  undo() {
    undo();
  },

  init() {
    setFlowChartRef(FlowChartX6);
    editor.init();
    contextMenu.init();
  },

  zoomIn() {
    const graph = editor.getGraph();
    if (graph) {
      const zoom = graph.zoom();
      graph.zoom(Math.max(zoom * 0.8, 0.2));
    }
  },

  zoomOut() {
    const graph = editor.getGraph();
    if (graph) {
      const zoom = graph.zoom();
      graph.zoom(Math.min(zoom * 1.2, 3));
    }
  },

  loadData(data) {
    model.setData(data);
    const graph = editor.getGraph();
    if (graph) {
      graph.clearCells();
    }
    editor.render();
  },

  getData() {
    const graph = editor.getGraph();
    if (graph) {
      return model.fromX6Data(graph);
    }
    return model.getData();
  },

  setData(data) {
    return model.setData(data);
  },

  getNodeDataByNodeId(id) {
    return model.getNodeDataByNodeId(id);
  },

  execModel() {
    return editor.execModel();
  },
};

extendsEvent(FlowChartX6);

// 监听右键菜单事件
FlowChartX6.on('contextmenu', (menuInfo) => {
  contextMenu.show(menuInfo);
});

document.addEventListener('click', (ev) => {
  EventCenter.emit('document.click', ev);
});

export default FlowChartX6;
```

- [ ] **步骤 2：Commit**

```bash
cd d:\project\ai\vs\03
git add frontend/src/FlowChartX6/index.js
git commit -m "feat: add FlowChartX6 index.js - public API facade"
```

---

## 任务 10：复制视图层文件（ComponentsTree + Terminal + Attr + config）

**文件：**
- 创建：`frontend/src/views/FlowChartX6/ComponentsTree.vue`
- 创建：`frontend/src/views/FlowChartX6/Terminal.vue`
- 创建：`frontend/src/views/FlowChartX6/Attr.vue`
- 创建：`frontend/src/views/FlowChartX6/config.js`

- [ ] **步骤 1：复制 ComponentsTree.vue（无需修改）**

```bash
cp "d:/project/ai/vs/03/frontend/src/views/FlowChart/ComponentsTree.vue" \
   "d:/project/ai/vs/03/frontend/src/views/FlowChartX6/ComponentsTree.vue"
```

- [ ] **步骤 2：复制 Terminal.vue（无需修改）**

```bash
cp "d:/project/ai/vs/03/frontend/src/views/FlowChart/Terminal.vue" \
   "d:/project/ai/vs/03/frontend/src/views/FlowChartX6/Terminal.vue"
```

- [ ] **步骤 3：复制 Attr.vue 并替换引用**

```bash
cp "d:/project/ai/vs/03/frontend/src/views/FlowChart/Attr.vue" \
   "d:/project/ai/vs/03/frontend/src/views/FlowChartX6/Attr.vue"
```

然后在 `Attr.vue` 中替换 FlowChart 引用：

```bash
# 在 Attr.vue 中将 import FlowChart from '@/FlowChart/index' 替换为
# import FlowChart from '@/FlowChartX6/index'
```

具体替换（sed 或手动编辑）：

将第 1 行附近：
```js
import FlowChart from '@/FlowChart/index';
```
改为：
```js
import FlowChart from '@/FlowChartX6/index';
```

- [ ] **步骤 4：复制 config.js（无需修改）**

```bash
cp "d:/project/ai/vs/03/frontend/src/views/FlowChart/config.js" \
   "d:/project/ai/vs/03/frontend/src/views/FlowChartX6/config.js"
```

- [ ] **步骤 5：Commit**

```bash
cd d:\project\ai\vs\03
git add frontend/src/views/FlowChartX6/
git commit -m "feat: add FlowChartX6 view layer (copy from FlowChart with import adjustments)"
```

---

## 任务 11：创建主页面 views/FlowChartX6/index.vue

**文件：**
- 创建：`frontend/src/views/FlowChartX6/index.vue`

这是最复杂的文件，从旧 `views/FlowChart/index.vue` 复制并替换所有 FlowChart/editor 引用。

- [ ] **步骤 1：复制旧 index.vue**

```bash
cp "d:/project/ai/vs/03/frontend/src/views/FlowChart/index.vue" \
   "d:/project/ai/vs/03/frontend/src/views/FlowChartX6/index.vue"
```

- [ ] **步骤 2：修改 script 部分的 import**

在 `views/FlowChartX6/index.vue` 的 `<script>` 部分，替换以下 import：

```js
// 旧：
import FlowChart from '@/FlowChart/index';
import editor from '@/FlowChart/editor.js';
import exec, { RenameNodeCommand, PasteNodeCommand } from '@/FlowChart/Command.js';

// 新：
import FlowChart from '@/FlowChartX6/index';
import editor from '@/FlowChartX6/editor.js';
import exec, { RenameNodeCommand, PasteNodeCommand } from '@/FlowChartX6/Command.js';
```

- [ ] **步骤 3：修改 TaskTypeConfig 加载路径**

将：
```js
const req = require.context('@/FlowChart/TaskTypeConfig', true, /\index.js$/);
```
改为：
```js
// 复用旧模块的 TaskTypeConfig
const req = require.context('@/FlowChart/TaskTypeConfig', true, /\index.js$/);
```

注意：TaskTypeConfig 保持引用旧路径，因为算子配置是共享的。

- [ ] **步骤 4：修改组件名和 meta**

```js
// 旧：
export default Vue.extend({
  name: 'FlowChart',
  meta: {
    title: '流程设计'
  },

// 新：
export default Vue.extend({
  name: 'FlowChartX6',
  meta: {
    title: '流程设计(X6)'
  },
```

- [ ] **步骤 5：修改样式引用**

如果有 `@import` 引用旧样式，保持不变或指向共享样式。

- [ ] **步骤 6：Commit**

```bash
cd d:\project\ai\vs\03
git add frontend/src/views/FlowChartX6/index.vue
git commit -m "feat: add FlowChartX6 main page with X6 imports and route meta"
```

---

## 任务 12：验证路由自动注册和整体功能

- [ ] **步骤 1：启动开发服务器**

```bash
cd d:\project\ai\vs\03\frontend
npm run serve
```

- [ ] **步骤 2：验证路由注册**

打开浏览器访问 `http://127.0.0.1:8689/#/FlowChartX6`，确认页面能加载（即使有报错也算路由注册成功）。

如果路由未注册，检查 `router.ts` 的 `setRoute()` 是否扫描到了 `views/FlowChartX6/index.vue`。自动发现机制要求：
- 文件路径为 `@/views/FlowChartX6/index.vue`
- 组件导出 `name: 'FlowChartX6'` 和 `meta: { title: '流程设计(X6)' }`

- [ ] **步骤 3：修复编译错误**

根据浏览器控制台和终端的报错逐一修复。常见问题：
- `@antv/x6-vue-shape` 导入路径不对
- Vue 2 兼容性问题（改用 HTML shape 备选方案）
- SVG 图标组件路径

- [ ] **步骤 4：验证基本功能**

1. 左侧组件树显示正常
2. 从左侧拖拽节点到画布，节点正确显示
3. 节点之间可以连线
4. 双击节点打开配置弹窗
5. 右键菜单正常工作
6. 保存按钮能调用 API

- [ ] **步骤 5：Commit 最终修复**

```bash
cd d:\project\ai\vs\03
git add -A
git commit -m "feat: complete FlowChartX6 module - full feature parity with jsPlumb version"
```
