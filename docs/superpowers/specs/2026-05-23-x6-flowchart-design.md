# X6 流程图画布模块设计

## 背景

现有流程图画布基于 jsPlumb 2.x，节点本质是独立 DOM，没有父子/分组概念。后续需要支持复合组件（节点内嵌套子节点+连线），jsPlumb 无法优雅实现。决策：在保留旧模块的前提下，新建独立模块用 AntV X6 2.x 完整复刻。

## 约束

- Vue 2 + Element UI 技术栈不变
- 读写同一份后端数据（旧 FlowChartJson 格式）
- 新旧模块通过独立路由并行存在
- 68 个 TaskTypeConfig 算子配置直接复用，零修改

## 目录结构

```
frontend/src/
├── FlowChartX6/                    # 画布核心
│   ├── index.js                    # 公共 API 门面
│   ├── graph.js                    # X6 Graph 实例
│   ├── model.js                    # 数据模型 + 双向格式转换
│   ├── editor.js                   # 核心引擎：节点/连线增删改
│   ├── Command.js                  # 命令模式（撤销/重做）
│   ├── contextMenu.js              # 右键菜单
│   ├── Node.vue                    # 节点 Vue 组件
│   ├── style.js                    # 样式定义
│   └── const.js                    # 常量
│
├── views/FlowChartX6/              # 页面视图
│   ├── index.vue                   # 主页面（左/中/右布局）
│   ├── ComponentsTree.vue          # 左侧组件树
│   ├── Terminal.vue                # 底部终端
│   ├── Attr.vue                    # 右侧属性面板
│   └── config.js                   # 工具栏配置
```

## 核心架构

### 1. Graph 实例（graph.js）

替代旧 `instance.js` + `config.js`：

```js
import { Graph } from '@antv/x6'

export default function createGraph(container) {
  return new Graph({
    container,
    autoResize: true,
    mouseWheel: { enabled: true, modifiers: ['ctrl', 'meta'] },
    panning: { enabled: true, modifiers: ['space'] },
    connecting: {
      connector: 'rounded',
      router: 'manhattan',
      anchor: 'center',
      connectionPoint: 'anchor',
      allowBlank: false,
      allowLoop: false,
      allowMulti: true,
    },
    selecting: { enabled: true, showNodeSelectionBox: true },
    keyboard: { enabled: true },
  })
}
```

关键配置说明：
- `mouseWheel.modifiers`：Ctrl/Command + 滚轮缩放
- `panning.modifiers`：空格 + 拖拽平移
- `connecting.router: 'manhattan'`：自动绕障连线路由
- `allowLoop: false`：禁止自环

### 2. 数据模型（model.js）

内部维护旧格式 `FlowChartJson`，提供双向转换：

```js
// 旧格式
{ nodes: [...], edges: [...], endpoints: [...], head: '' }

// 转换函数
toX6Data(flowData)    // 旧格式 -> X6 cells（初始化时）
fromX6Data(graph)     // X6 cells -> 旧格式（保存时）
```

映射关系：

| 旧格式 | X6 |
|--------|-----|
| `node.id` | `node.id`（直接复用） |
| `node.position.left/top` | `node.position.x/y` |
| `node.data` | `node.getData()` |
| `node.properties.ruleForm` | 存在 data 中 |
| `edge "sourceUUID&&targetUUID"` | `edge.source.port / edge.target.port` |
| `endpoint` | X6 `ports` |

### 3. 节点渲染（Node.vue + 注册）

使用 `@antv/x6-vue-shape` 将 Vue 组件注册为 X6 自定义节点：

```js
import { VueShape } from '@antv/x6-vue-shape'
import Node from './Node.vue'

// 注册（尺寸与旧模块一致：170x34）
graph.use(VueShape.register({
  component: Node,
  width: 170,
  height: 34,
  ports: {
    groups: {
      in: { position: 'top', attrs: { circle: { r: 6 } } },
      out: { position: 'bottom', attrs: { circle: { r: 4 } } },
    },
    items: [
      { id: 'in-1', group: 'in' },
      { id: 'out-1', group: 'out' },
    ]
  }
}))
```

节点端点数量根据算子类型动态调整（如 MergeJoin 有 2 个输入端点）。

### 4. 公共 API（index.js）

对齐旧 `FlowChart/index.js` 接口，保持 `views/FlowChartX6/index.vue` 业务逻辑与旧模块一致：

```js
export default {
  setContainer(id),
  init(),
  addNode(position, nodeData),
  undo(),
  redo(),
  zoomIn(),
  zoomOut(),
  loadData(data),
  getData(),
  setData(data),
  getNodeDataByNodeId(id),
  on(event, fn),
  emit(event, ...args),
  remove(event, fn),
}
```

### 5. 命令模式（Command.js）

复用旧模块的 7 个命令类：

| Command | exec() | undo() |
|---------|--------|--------|
| AddNodeCommand | `editor.addNodeByDrag(position, data)` | `editor.removeNode(id)` |
| RemoveNodeCommand | `editor.removeNode(id)` | `editor.addNodeByExtraData(...)` |
| AddConnectorCommand | `editor.addConnector(uuids)` | `editor.removeConnectorByUuids(uuids)` |
| RemoveConnectorCommand | `editor.removeConnector(edge)` | `editor.addConnector(edge)` |
| MoveNodeCommand | 记录旧位置 | `editor.changeNodePosition(id, oldPos)` |
| PasteNodeCommand | `editor.addNodeByCopy(position, data)` | `editor.removeNode(id)` |
| RenameNodeCommand | `editor.renameNode(id, value)` | `editor.renameNode(id, oldValue)` |

### 6. 右键菜单（contextMenu.js）

复用旧模块菜单项：
- 节点上：复制、删除、查看数据
- 连线上：删除
- 画布背景：粘贴节点

通过 X6 的 `graph.on('node:contextmenu')` / `graph.on('edge:contextmenu')` 事件绑定。

### 7. 页面布局（views/FlowChartX6/index.vue）

完全复刻旧页面布局：

```
┌─────────────────────────────────────────────────┐
│  工具栏 (back, name, bindTable, zoom, save, run) │
├──────────┬──────────────────────────┬────────────┤
│ 组件树    │                          │  属性面板   │
│ 224px    │     X6 画布 (Graph)       │  300px     │
│          │                          │ (可折叠)    │
│          ├──────────────────────────┤            │
│          │  底部终端 (日志/指标/进度) │            │
└──────────┴──────────────────────────┴────────────┘
```

业务逻辑从旧 `views/FlowChart/index.vue` 复制，替换所有 `FlowChart` / `editor` import 为新模块。

### 8. 路由

```js
// router 中新增
{
  path: '/flowChartX6',
  name: 'FlowChartX6',
  component: () => import('@/views/FlowChartX6/index.vue'),
  meta: { title: '流程设计(X6)' }
}
```

## 复用策略

| 模块 | 策略 |
|------|------|
| `TaskTypeConfig/` 68 个算子配置 | 直接复用，零修改 |
| `ComponentsTree.vue` | 复制，微调事件名 |
| `Terminal.vue` | 复制，无修改 |
| `Attr.vue` | 复制，替换 FlowChart 引用 |
| `config.js` | 复制，无修改 |

## 新增依赖

```json
{
  "@antv/x6": "^2.18.1",
  "@antv/x6-vue-shape": "^2.2.3"
}
```

注意：X6 2.x 后期版本对 Vue 2 的支持可能有变动（Vue 2 已于 2023.12 EOL）。安装时需锁定确认兼容的版本组合。如果 `@antv/x6-vue-shape` 不兼容，备选方案是用 X6 的 `HTML` shape 手动挂载 Vue 实例。

## 后续扩展：复合组件

X6 的 Group / 子图能力天然支持：

```js
graph.addNode({
  id: 'composite-1',
  shape: 'group',
  children: [/* 子节点 */],
  data: { pluginId: 'CompositeTransform', text: '复合转换' }
})
```

这是 jsPlumb 无法优雅实现的核心能力。

## 风险与注意事项

1. **X6 + Vue 2 兼容性**：`@antv/x6-vue-shape` 2.x 支持 Vue 2，需确认版本匹配
2. **数据双向转换**：保存时 `fromX6Data` 必须精确还原旧格式，否则旧模块打开会出错
3. **manhattan 路由性能**：大量节点时路由计算可能较慢，必要时降级为 `normal` 路由
4. **右键菜单定位**：X6 的 contextmenu 坐标是画布坐标，需要转换为页面坐标
