# 仿真方案详细数据页面 — 设计规格

日期：2026-08-01
状态：已批准（方案 B，两层结构）

## 背景与目标

仿真方案管理页面（`frontend/src/views/SchemeManage/index.vue`）目前已有"查看详情"按钮，仅展示方案的基本信息和样本明细列表。用户需要一个入口查看方案的**详细数据**——以红蓝双方阵营组织装备信息。

目标：

1. 在仿真方案管理页面操作列新增"详细数据"按钮。
2. 点击后进入独立页面，左侧以 红方/蓝方 Tab 区分阵营，右侧按装备展示信息。
3. 本阶段**只做前端 + mock 假数据**，接口和数据模型预留，后续对接真实数据。

非目标：

- 不新增数据库表，不改后端。
- 不做红蓝方/装备数据的增删改。

## 现状梳理

### 相关代码

| 文件 | 说明 |
|---|---|
| `frontend/src/views/SchemeManage/index.vue` | 仿真方案管理页，操作列有"查看详情/编辑/引接/设计流程/删除" |
| `frontend/src/api/modules/sample-cascade.ts` | 方案相关的 API 模块（`/api/scheme/*`） |
| `backend/.../scheme/controller/SchemeController.java` | 后端 `/api/scheme` 接口 |
| `frontend/src/router.ts` | 自动注册 `views/*/index.vue` 为路由，导航栏按 `meta.pageType` 过滤 |

### 关键机制

- 路由：所有 `views/*/index.vue` 按组件 `name` 自动注册为 `/${name}` 路由；页面在左侧导航栏的显隐由 `meta.pageType`（`mainNav`/`viceNav`）决定。详情页不设置 `pageType` 即可路由访问但不出现在导航栏。
- 现有样本四级联动：仿真方案(SamplePlan) → 大样本(SampleData) → 样本子集(SampleSubData) → 文件明细(SampleDetail)。与本次红蓝方/装备展示无直接依赖，mock 数据独立定义。

## 设计

### 整体布局（两层）

```
┌──────────────────────────────────────────────────────────┐
│  ← 返回    仿真方案：XX红蓝对抗演练    状态:已完成   2026-08-01 │  ← 顶部信息条
├──────────────┬───────────────────────────────────────────┤
│  阵营（第1层）│   装备 + 信息（第2层，合并为一块内容区）             │
│              │  [99式坦克] [歼-20] [预警机] [雷达站]   ← 装备Tab │
│   🔴 红方 (12)│  ┌─────────────────────────────────────┐   │
│   🔵 蓝方 (15)│  │ [基本信息|实时状态|作战指标|数据记录]    │   │  ← 信息分类Tab（面板内）
│              │  │ ┌─────────────────────────────────┐ │   │
│              │  │ │  选中的信息内容                    │ │   │
│              │  │ └─────────────────────────────────┘ │   │
│              │  └─────────────────────────────────────┘   │
└──────────────┴───────────────────────────────────────────┘
```

用户仅经历两个导航步骤：**选阵营 → 选装备**；信息分类 Tab 内嵌于装备内容面板，不占导航层级。

### 新页面

- 路径：`frontend/src/views/SchemeDetailData/index.vue`
- 组件 `name: 'SchemeDetailData'`，自动注册路由 `/SchemeDetailData`
- `meta`：`title: '方案详细数据'`，**不设置 `pageType`**（不在左侧导航栏显示）
- 路由参数：`query` 携带 `schemeId`、`schemeName`、`schemeStatus`

### 页面结构

#### 顶部信息条
返回按钮（`$router.back()` 或跳转 `/SchemeManage`）+ 方案名称 + 状态标签 + 创建时间。

#### 左侧阵营 Tab（`el-tabs` 纵向）
- 两个 Tab：红方 / 蓝方，Tab 名带装备数量角标（如 `红方 (12)`）。
- 红方使用红色系高亮，蓝方使用蓝色系高亮；装备 Tab 与状态标签跟随阵营配色。
- 切换阵营时，右侧装备列表切换为该方装备，信息内容重置为第一个装备的"基本信息"。

#### 右侧装备区
- 装备横向 `el-tabs`：列出当前阵营所有装备（名称）。
- 内容面板内嵌信息分类 `el-tabs`：基本信息 / 实时状态 / 作战指标 / 数据记录。
- 四类信息内容：
  - **基本信息**：属性表（属性名/属性值），如 型号、类型、状态、位置、速度、航向、武器配置、油弹量。
  - **实时状态**：状态标签（作战中/待命/损毁，按状态着色）+ 关键指标（如 航速、油量百分比、电量）展示。
  - **作战指标**：指标卡片或表格（命中率、侦测范围、载弹量等）。
  - **数据记录**：时间序列表格（时间 / 数据项 / 数值）。

### 交互与数据流

1. `created`：从 `$route.query` 读取 `schemeId` 等参数；加载 mock 数据（预留 API 调用位置）。
2. 状态：`scheme`（整体方案信息）、`activeSide`（red/blue）、`activeEquipId`、`activeInfoTab`（默认 'basic'）。
3. 切换阵营 → `activeSide` 变化 → 计算当前方装备列表，`activeEquipId` 重置为第一个装备，`activeInfoTab` 重置为 'basic'。
4. 切换装备 → `activeEquipId` 变化 → `activeInfoTab` 重置为 'basic'。
5. 切换信息分类 → `activeInfoTab` 变化 → 渲染对应内容。

### Mock 数据

文件：`frontend/src/views/SchemeDetailData/mock.js`，导出 `getMockSchemeDetailData(schemeId)`。

数据结构：

```js
{
  schemeId: 1,
  schemeName: 'XX红蓝对抗演练',
  status: 1,
  createTime: '2026-08-01 10:00:00',
  sides: [
    {
      side: 'red',
      name: '红方',
      color: '#E54D42',
      equipmentList: [
        {
          id: 1,
          name: '99式主战坦克',
          type: '主战坦克',
          info: {
            basic:  [ { label: '型号', value: '99式' }, ... ],
            status: [ { label: '状态', value: '作战中', tag: 'success' }, ... ],
            combat: [ { label: '命中率', value: '92%' }, ... ],
            records: [ { time: '2026-08-01 10:00:00', item: '油耗', value: '45 L/h' }, ... ]
          }
        },
        ...
      ]
    },
    {
      side: 'blue',
      name: '蓝方',
      color: '#2F7DE1',
      equipmentList: [ ... ]
    }
  ]
}
```

Mock 数据量示例：红方 12 台、蓝方 15 台装备，覆盖不同类型（坦克/战机/舰船/雷达/无人机等）。每台装备四类信息各若干条。

### 接口预留

`frontend/src/api/modules/sample-cascade.ts` 新增方法签名：

```js
getSchemeDetailData(schemeId) {
  return new Promise((resolve, reject) => {
    this.$http.get('/api/scheme/detailData', { params: { schemeId } }).then(resolve).catch(reject);
  });
}
```

页面加载处用注释标注"接入真实数据时替换为 `API.sampleCascade.getSchemeDetailData(schemeId)`"。后端接口 `/api/scheme/detailData` 本阶段不实现。

### 错误处理与边界

- `schemeId` 缺失：页面显示"缺少方案参数"提示并提供返回。
- Mock 数据加载为空 / 某方无装备：显示空状态文案，不崩溃。
- 返回按钮：`$router.back()`，无历史时兜底跳转 `/SchemeManage`。

### 测试

- 手动：进入页面 → 切换红蓝方（右侧装备变化、配色变化）→ 切换装备（信息重置为基本信息）→ 切换四个信息分类（内容变化）→ 返回。
- 边界：无参数进入、单方无装备。
- 本阶段为纯前端 mock，不涉及后端接口联调测试。

## 实施范围（文件清单）

| 文件 | 动作 |
|---|---|
| `frontend/src/views/SchemeManage/index.vue` | 修改：操作列新增"详细数据"按钮 + 跳转方法 |
| `frontend/src/views/SchemeDetailData/index.vue` | 新增：详情页 |
| `frontend/src/views/SchemeDetailData/mock.js` | 新增：mock 数据 |
| `frontend/src/api/modules/sample-cascade.ts` | 修改：新增 `getSchemeDetailData` 方法签名 |

## 后续扩展（非本阶段）

- 后端实现 `/api/scheme/detailData`，返回真实红蓝方/装备数据。
- 页面接入真实接口，mock 移除。
- 如需增删改红蓝方/装备，另立数据模型与页面。
