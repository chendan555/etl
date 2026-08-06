# FlowChartX6 画布模块问题记录

## 当前状态
- 已从导航菜单隐藏（`hidden: true`）
- 路由仍可通过 `/FlowChartX6?path=...` 直接访问
- 旧版 FlowChart（基于 jsPlumb）正常工作

## 已完成的修复

### 1. 路由冲突问题 ✅
- **问题**：旧 FlowChart 和新 FlowChartX6 都注册了路由，`/flowChart` 匹配到旧组件
- **修复**：在 `router.ts` 添加重定向 `/flowChart` → `/FlowChartX6`（后已移除）
- **当前状态**：已回退，旧 FlowChart 正常使用

### 2. 节点渲染问题 ✅
- **问题**：`addNodeByData` 未将 `nodeData.data` 属性合并到顶层
- **修复**：`editor.js` 中 `addNodeByData` 合并 `{ ...nodeData, ...(nodeData.data || {}) }`
- **文件**：`FlowChartX6/editor.js`

### 3. 节点形状问题 ✅
- **问题**：`generateNode` 使用 `shape: 'x6-vue-node'` 但未正确注册
- **修复**：`@antv/x6-vue-shape` 的 `register` 已在 editor.js 顶部注册
- **文件**：`FlowChartX6/editor.js`

### 4. 路由切换数据缓存 ✅
- **问题**：在 FlowChartX6 内切换流程时，组件不重新挂载，显示旧数据
- **修复**：添加 `$route` watcher，路由变化时 `clearAll()` + `init()`
- **文件**：`FlowChartX6/index.vue`

### 5. beforeRouteLeave 覆盖数据 ✅
- **问题**：`beforeRouteLeave` 在切换流程时保存空画布，覆盖原始数据
- **修复**：目标为 FlowChartX6 时跳过保存，直接 `next()`
- **文件**：`FlowChartX6/index.vue`

## 待解决的问题

### 1. 连线功能不工作 🔴
- **现象**：从端口拖出线后，无法连接到其他节点的端口
- **已尝试**：
  - `magnet: true` 放在端口定义顶层（X6 文档推荐方式）
  - 添加 `validateConnection` 函数验证连接
  - 端口 `attrs` 中设置 `stroke`、`fill` 使端口可见
  - 添加 `highlight: true` 到 connecting 配置
- **可能原因**：
  - 端口组（portGroups）配置与 X6 Vue 节点形状不兼容
  - `shape: 'x6-vue-node'` 的自定义渲染可能阻止了端口交互
  - X6 版本与 `@antv/x6-vue-shape` 版本不匹配

### 2. 连线后节点拖不动 🔴
- **现象**：拖出线后，画布上所有节点都无法拖动
- **已尝试**：
  - 添加 `interacting: { nodeMovable: true }` 到 graph 配置
  - 设置 `selecting.rubberband: false`
- **可能原因**：
  - `selecting` 和 `connecting` 交互模式冲突
  - X6 的交互状态管理问题

### 3. 节点样式待优化 🟡
- **当前样式**：暗色背景 `#0b2244`、蓝色边框 `#107eef`、浅色文字
- **待改进**：
  - Node.vue 中的 `background: $contBg` 在某些主题下可能不正确
  - 端口颜色需要与主题联动
  - 选中状态样式需要适配暗色模式

### 4. HMR 热更新问题 🟡
- **现象**：修改 editor.js/graph.js 后，HMR 重新初始化 graph 但不重载数据
- **影响**：开发时需要手动刷新页面
- **可能方案**：在 graph.js 中检测 HMR 并触发数据重载

## 关键文件

| 文件 | 作用 |
|------|------|
| `FlowChartX6/editor.js` | 画布操作（节点创建、连线、渲染） |
| `FlowChartX6/graph.js` | X6 Graph 实例配置 |
| `FlowChartX6/model.js` | 数据模型（节点、边、端口） |
| `FlowChartX6/Node.vue` | 自定义 Vue 节点组件 |
| `FlowChartX6/index.js` | FlowChart 门面（setContainer、loadData） |
| `FlowChartX6/const.js` | 常量定义 |
| `views/FlowChartX6/index.vue` | Vue 主组件 |

## X6 端口配置参考

```javascript
// 正确的端口定义方式（magnet 在顶层）
{
  id: 'target-xxx',
  group: 'in',
  magnet: true,  // 必须在顶层
  attrs: {
    circle: {
      r: 6,
      stroke: '#107eef',
      fill: '#0b2244',
      strokeWidth: 2
    }
  }
}

// 端口组配置（graph 级别）
defaultPortGroup: {
  in: { position: 'left' },
  out: { position: 'right' }
}
```

## X6 版本信息
- `@antv/x6`: 项目中使用的版本
- `@antv/x6-vue-shape`: Vue 节点形状支持
