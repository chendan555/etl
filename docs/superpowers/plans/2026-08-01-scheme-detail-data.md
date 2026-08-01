# 仿真方案详细数据页面 实现计划

> **面向 AI 代理的工作者：** 必需子技能：使用 superpowers:subagent-driven-development（推荐）或 superpowers:executing-plans 逐任务实现此计划。步骤使用复选框（`- [ ]`）语法来跟踪进度。

**目标：** 在仿真方案管理页新增"详细数据"按钮，进入独立页面按 红方/蓝方 → 装备 → 信息分类 展示详细数据（本阶段纯前端 + mock，接口预留）。

**架构：** 新增 `frontend/src/views/SchemeDetailData/index.vue`（自动注册路由 `/SchemeDetailData`，不设 `pageType` 从而不进左侧导航栏），页面内三层 Tab：左侧红蓝方、右侧装备、内容面板内嵌信息分类。mock 数据放同目录 `mock.js`，页面加载处预留 API 调用位置。`SchemeManage` 操作列加"详细数据"按钮跳转。

**技术栈：** Vue 2.6 + element-ui 2.8.2 + vue-router（自动注册 views），vue-cli。前端无测试基础设施，用 `eslint` + `npm run build` + 手动浏览器验证。

**设计规格：** [2026-08-01-scheme-detail-data-design.md](../specs/2026-08-01-scheme-detail-data-design.md)

---

## 文件结构

| 文件 | 动作 | 职责 |
|---|---|---|
| `frontend/src/views/SchemeDetailData/mock.js` | 创建 | mock 数据生成器 `getMockSchemeDetailData(schemeId)` |
| `frontend/src/views/SchemeDetailData/index.vue` | 创建 | 详细数据页面（三层 Tab + 四类信息内容） |
| `frontend/src/api/modules/sample-cascade.ts` | 修改 | 新增 `getSchemeDetailData(schemeId)` 方法签名（接口预留） |
| `frontend/src/views/SchemeManage/index.vue` | 修改 | 操作列新增"详细数据"按钮 + `viewDetailData` 方法 |

注意：element-ui 2.8.2 无 `el-empty`（2.9 才有），空状态用纯文本 div 实现。红蓝配色用自定义 div 模拟 tab（比 el-tabs 更易控制活动态颜色）。

---

## 任务 1：Mock 数据模块

**文件：**
- 创建：`frontend/src/views/SchemeDetailData/mock.js`

- [ ] **步骤 1：创建 mock.js**

```js
/**
 * 仿真方案详细数据 —— mock 数据
 * 接入真实数据后：删除本文件，页面改用 API.sampleCascade.getSchemeDetailData(schemeId)
 */

const RED = '#E54D42';
const BLUE = '#2F7DE1';

/** 生成一台装备的四类信息 */
function buildInfo(name, type, overrides = {}) {
  return {
    basic: [
      { label: '型号', value: overrides.model || name },
      { label: '类型', value: type },
      { label: '状态', value: overrides.status || '作战中', tag: overrides.tag || 'danger' },
      { label: '位置', value: overrides.pos || '东经120° 北纬35°' },
      { label: '速度', value: overrides.speed || '60 km/h' },
      { label: '航向', value: overrides.course || '东北 45°' },
      { label: '武器配置', value: overrides.weapon || '制式装备' },
      { label: '油弹量', value: overrides.ammo || '85%' }
    ],
    status: [
      { label: '当前状态', value: overrides.status || '作战中', tag: overrides.tag || 'danger' },
      { label: '航速', value: overrides.speed || '60 km/h' },
      { label: '油量', value: overrides.ammo || '85%' },
      { label: '电子系统', value: '正常', tag: 'success' }
    ],
    combat: [
      { label: '命中率', value: '92%' },
      { label: '侦测范围', value: '80 km' },
      { label: '载弹量', value: '42 枚' },
      { label: '持续作战时间', value: '6 h' }
    ],
    records: [
      { time: '2026-08-01 10:30:00', item: '航速', value: '58 km/h' },
      { time: '2026-08-01 10:20:00', item: '油量', value: '86%' },
      { time: '2026-08-01 10:10:00', item: '航向', value: '45°' },
      { time: '2026-08-01 10:00:00', item: '海拔', value: '320 m' },
      { time: '2026-08-01 09:50:00', item: '航速', value: '55 km/h' },
      { time: '2026-08-01 09:40:00', item: '油量', value: '88%' }
    ]
  };
}

function equip(id, name, type, overrides) {
  return { id, name, type, info: buildInfo(name, type, overrides) };
}

const RED_EQUIPS = [
  equip(1, '99式主战坦克', '主战坦克', { model: '99A', weapon: '125mm滑膛炮', ammo: '82%' }),
  equip(2, '歼-20', '隐身战斗机', { model: '歼-20A', speed: '2.2 马赫', status: '巡逻中', tag: 'warning', ammo: '90%' }),
  equip(3, '空警-2000', '预警机', { model: '空警-2000', speed: '850 km/h' }),
  equip(4, '红旗-9', '防空导弹车', { model: 'HQ-9', status: '待命', tag: 'warning' }),
  equip(5, '东风-17', '弹道导弹车', { model: 'DF-17', weapon: 'DF-17高超音速导弹' }),
  equip(6, '武直-10', '武装直升机', { model: '直-10', speed: '270 km/h', status: '巡逻中', tag: 'warning' }),
  equip(7, '山猫侦察车', '侦察车', { model: '山猫', speed: '80 km/h', status: '侦察中', tag: 'warning' }),
  equip(8, '指挥方舱', '指挥车', { model: '指挥方舱', speed: '40 km/h', status: '待命', tag: 'warning' })
];

const BLUE_EQUIPS = [
  equip(11, 'M1A2主战坦克', '主战坦克', { model: 'M1A2 SEPv3', weapon: '120mm滑膛炮', ammo: '78%' }),
  equip(12, 'F-35战机', '隐身战斗机', { model: 'F-35B', speed: '1.6 马赫', status: '巡逻中', tag: 'warning', ammo: '88%' }),
  equip(13, '宙斯盾驱逐舰', '驱逐舰', { model: 'DDG-51', speed: '30 节', status: '巡航中', tag: 'warning' }),
  equip(14, '全球鹰无人机', '无人机', { model: 'RQ-4', speed: '575 km/h', status: '侦察中', tag: 'warning' }),
  equip(15, '爱国者导弹', '防空导弹车', { model: 'MIM-104', status: '待命', tag: 'warning' }),
  equip(16, 'CH-53直升机', '运输直升机', { model: 'CH-53K', speed: '315 km/h' }),
  equip(17, '斯特赖克装甲车', '装甲运兵车', { model: 'LAV III', speed: '97 km/h' }),
  equip(18, '两栖登陆舰', '登陆舰', { model: 'LHD-1', speed: '22 节', status: '待命', tag: 'warning' })
];

/**
 * 生成方案详细数据 mock
 * @param {number|string} schemeId 方案 id
 */
export function getMockSchemeDetailData(schemeId) {
  return {
    schemeId: Number(schemeId) || 1,
    schemeName: 'XX红蓝对抗演练-方案' + (schemeId || 1),
    status: 1,
    createTime: '2026-08-01 10:00:00',
    sides: [
      { side: 'red', name: '红方', color: RED, equipmentList: RED_EQUIPS },
      { side: 'blue', name: '蓝方', color: BLUE, equipmentList: BLUE_EQUIPS }
    ]
  };
}
```

- [ ] **步骤 2：语法校验**

运行：`npx eslint frontend/src/views/SchemeDetailData/mock.js`
预期：无报错。

- [ ] **步骤 3：Commit**

```bash
git add frontend/src/views/SchemeDetailData/mock.js
git commit -m "feat: add mock data for scheme detail data page"
```

---

## 任务 2：详细数据页面

**文件：**
- 创建：`frontend/src/views/SchemeDetailData/index.vue`

- [ ] **步骤 1：创建 index.vue 模板与脚本**

```vue
<template>
  <div class="scheme-detail-data">
    <!-- 顶部信息条 -->
    <div class="detail-header">
      <el-button size="small" @click="goBack">← 返回</el-button>
      <span class="header-title">{{ scheme.schemeName || '方案详细数据' }}</span>
      <el-tag :type="scheme.status === 1 ? 'success' : 'info'" size="small">
        {{ scheme.status === 1 ? '已完成' : '未完成' }}
      </el-tag>
      <span class="header-time">{{ scheme.createTime || '' }}</span>
    </div>

    <div class="detail-body">
      <!-- 左侧阵营 Tab -->
      <div class="side-tabs">
        <div
          v-for="side in scheme.sides"
          :key="side.side"
          class="side-tab"
          :class="{ active: activeSide === side.side }"
          :style="activeSide === side.side ? { background: side.color, borderColor: side.color } : {}"
          @click="onSideChange(side.side)"
        >
          <span class="side-name">{{ side.name }}</span>
          <span class="side-count">({{ side.equipmentList.length }})</span>
        </div>
      </div>

      <!-- 右侧装备 + 信息 -->
      <div class="equip-area">
        <div class="equip-tabs">
          <div
            v-for="eq in currentEquips"
            :key="eq.id"
            class="equip-tab"
            :class="{ active: activeEquipId === eq.id }"
            :style="activeEquipId === eq.id ? { color: currentSideColor, borderColor: currentSideColor } : {}"
            @click="onEquipChange(eq.id)"
          >
            {{ eq.name }}
          </div>
        </div>

        <div class="info-panel">
          <div class="info-tabs" v-if="currentEquip">
            <div
              v-for="tab in infoTabs"
              :key="tab.key"
              class="info-tab"
              :class="{ active: activeInfoTab === tab.key }"
              :style="activeInfoTab === tab.key ? { color: currentSideColor, borderBottomColor: currentSideColor } : {}"
              @click="activeInfoTab = tab.key"
            >
              {{ tab.label }}
            </div>
          </div>

          <div class="info-content" v-if="currentEquip">
            <!-- 基本信息：属性表 -->
            <table v-if="activeInfoTab === 'basic'" class="info-table">
              <tr v-for="(item, i) in currentEquip.info.basic" :key="i">
                <td class="info-label">{{ item.label }}</td>
                <td class="info-value">
                  <el-tag v-if="item.tag" :type="item.tag" size="small">{{ item.value }}</el-tag>
                  <span v-else>{{ item.value }}</span>
                </td>
              </tr>
            </table>

            <!-- 实时状态：卡片 -->
            <div v-else-if="activeInfoTab === 'status'" class="status-cards">
              <div v-for="(item, i) in currentEquip.info.status" :key="i" class="status-card">
                <div class="status-label">{{ item.label }}</div>
                <div class="status-value">
                  <el-tag v-if="item.tag" :type="item.tag" size="small">{{ item.value }}</el-tag>
                  <span v-else>{{ item.value }}</span>
                </div>
              </div>
            </div>

            <!-- 作战指标：表格 -->
            <el-table v-else-if="activeInfoTab === 'combat'" :data="currentEquip.info.combat" border size="small" class="info-content-table">
              <el-table-column prop="label" label="指标" />
              <el-table-column prop="value" label="数值" />
            </el-table>

            <!-- 数据记录：表格 -->
            <el-table v-else-if="activeInfoTab === 'records'" :data="currentEquip.info.records" border size="small" class="info-content-table">
              <el-table-column prop="time" label="时间" width="180" />
              <el-table-column prop="item" label="数据项" />
              <el-table-column prop="value" label="数值" />
            </el-table>
          </div>

          <!-- 空状态（element-ui 2.8.2 无 el-empty，用纯文本） -->
          <div v-else class="empty-tip">暂无装备数据</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getMockSchemeDetailData } from './mock';

export default {
  name: 'SchemeDetailData',
  meta: {
    title: '方案详细数据'
  },
  data() {
    return {
      schemeId: null,
      scheme: { schemeName: '', status: 0, createTime: '', sides: [] },
      activeSide: 'red',
      activeEquipId: null,
      activeInfoTab: 'basic',
      infoTabs: [
        { key: 'basic', label: '基本信息' },
        { key: 'status', label: '实时状态' },
        { key: 'combat', label: '作战指标' },
        { key: 'records', label: '数据记录' }
      ]
    };
  },
  computed: {
    currentSide() {
      return this.scheme.sides.find(s => s.side === this.activeSide) || null;
    },
    currentSideColor() {
      return this.currentSide ? this.currentSide.color : '#409EFF';
    },
    currentEquips() {
      return this.currentSide ? this.currentSide.equipmentList : [];
    },
    currentEquip() {
      return this.currentEquips.find(e => e.id === this.activeEquipId) || null;
    }
  },
  created() {
    this.schemeId = this.$route.query.schemeId;
    if (!this.schemeId) {
      this.$message.warning('缺少方案参数');
      return;
    }
    // TODO: 接入真实数据时替换为 API.sampleCascade.getSchemeDetailData(this.schemeId)
    this.scheme = getMockSchemeDetailData(this.schemeId);
    this.initSelection();
  },
  methods: {
    initSelection() {
      if (this.currentEquips.length > 0) {
        this.activeEquipId = this.currentEquips[0].id;
        this.activeInfoTab = 'basic';
      }
    },
    onSideChange(side) {
      this.activeSide = side;
      this.activeEquipId = this.currentEquips.length > 0 ? this.currentEquips[0].id : null;
      this.activeInfoTab = 'basic';
    },
    onEquipChange(id) {
      this.activeEquipId = id;
      this.activeInfoTab = 'basic';
    },
    goBack() {
      if (window.history.length > 1) {
        this.$router.back();
      } else {
        this.$router.push('/SchemeManage');
      }
    }
  }
};
</script>

<style scoped>
.scheme-detail-data {
  padding: 20px;
  height: 100%;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}
.detail-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e6e6e6;
}
.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}
.header-time {
  margin-left: auto;
  color: #909399;
  font-size: 13px;
}
.detail-body {
  flex: 1;
  display: flex;
  margin-top: 12px;
  min-height: 0;
}
.side-tabs {
  width: 120px;
  flex-shrink: 0;
  border-right: 1px solid #e6e6e6;
  padding-right: 12px;
}
.side-tab {
  padding: 10px 12px;
  margin-bottom: 8px;
  border-radius: 4px;
  border: 1px solid #dcdfe6;
  color: #606266;
  cursor: pointer;
  text-align: center;
  font-size: 14px;
}
.side-tab.active {
  color: #fff;
}
.side-count {
  font-size: 12px;
  opacity: 0.9;
}
.equip-area {
  flex: 1;
  margin-left: 12px;
  display: flex;
  flex-direction: column;
  min-width: 0;
}
.equip-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding-bottom: 8px;
  border-bottom: 1px solid #e6e6e6;
}
.equip-tab {
  padding: 8px 14px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  color: #606266;
  cursor: pointer;
  font-size: 13px;
}
.equip-tab.active {
  font-weight: 600;
}
.info-panel {
  margin-top: 10px;
  border: 1px solid #e6e6e6;
  border-radius: 4px;
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}
.info-tabs {
  display: flex;
  border-bottom: 1px solid #e6e6e6;
}
.info-tab {
  padding: 10px 16px;
  color: #606266;
  cursor: pointer;
  font-size: 13px;
  border-bottom: 2px solid transparent;
  margin-bottom: -1px;
}
.info-tab.active {
  font-weight: 600;
}
.info-content {
  flex: 1;
  overflow: auto;
  padding: 12px;
}
.info-content-table {
  width: 100%;
}
.info-table {
  width: 100%;
  border-collapse: collapse;
}
.info-table td {
  border: 1px solid #e6e6e6;
  padding: 8px 12px;
  font-size: 13px;
}
.info-label {
  width: 140px;
  background: #f5f7fa;
  color: #909399;
}
.info-value {
  color: #303133;
}
.status-cards {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}
.status-card {
  width: 180px;
  border: 1px solid #e6e6e6;
  border-radius: 4px;
  padding: 12px;
}
.status-label {
  color: #909399;
  font-size: 12px;
  margin-bottom: 6px;
}
.status-value {
  color: #303133;
  font-size: 16px;
  font-weight: 600;
}
.empty-tip {
  text-align: center;
  color: #909399;
  padding: 40px 0;
  font-size: 14px;
}
</style>
```

- [ ] **步骤 2：eslint 校验**

运行：`npx eslint frontend/src/views/SchemeDetailData/index.vue`
预期：无报错。

- [ ] **步骤 3：Commit**

```bash
git add frontend/src/views/SchemeDetailData/index.vue
git commit -m "feat: add scheme detail data page with red/blue side and equipment tabs"
```

---

## 任务 3：API 方法签名（接口预留）

**文件：**
- 修改：`frontend/src/api/modules/sample-cascade.ts`（在 `getSampleDetailBySubDataId` 之后、`引接处理` 注释之前插入）

- [ ] **步骤 1：新增 getSchemeDetailData 方法**

在 `frontend/src/api/modules/sample-cascade.ts` 中 `getSampleDetailBySubDataId(subDataId)` 方法之后插入：

```ts
  // ========== 方案详细数据（接口预留，本阶段后端未实现） ==========
  getSchemeDetailData(schemeId: number) {
    return new Promise((resolve, reject) => {
      this.$http.get('/api/scheme/detailData', { params: { schemeId } }).then(resolve).catch(reject);
    });
  }
```

- [ ] **步骤 2：eslint 校验**

运行：`npx eslint frontend/src/api/modules/sample-cascade.ts`
预期：无报错。

- [ ] **步骤 3：Commit**

```bash
git add frontend/src/api/modules/sample-cascade.ts
git commit -m "feat: reserve getSchemeDetailData API signature"
```

---

## 任务 4：方案管理页入口按钮

**文件：**
- 修改：`frontend/src/views/SchemeManage/index.vue`

- [ ] **步骤 1：操作列新增"详细数据"按钮并加宽列**

修改操作列（第 35 行附近）`width="280"` → `width="360"`，并在"查看详情"按钮后新增：

```vue
<el-table-column label="操作" width="360" fixed="right">
  <template slot-scope="scope">
    <el-button type="text" size="small" @click="openDialog(scope.row)">编辑</el-button>
    <el-button type="text" size="small" @click="viewDetail(scope.row)">查看详情</el-button>
    <el-button type="text" size="small" @click="viewDetailData(scope.row)">详细数据</el-button>
    <el-button type="text" size="small" @click="startProcess(scope.row)">引接</el-button>
    <el-button v-if="scope.row.transId" type="text" size="small" @click="goFlowChart(scope.row)">设计流程</el-button>
    <el-button type="text" size="small" style="color: #F56C6C" @click="deleteScheme(scope.row)">删除</el-button>
  </template>
</el-table-column>
```

- [ ] **步骤 2：新增 viewDetailData 方法**

在 `viewDetail(row)` 方法之后新增：

```js
    // ========== 详细数据 ==========
    viewDetailData(row) {
      this.$router.push({
        path: '/SchemeDetailData',
        query: {
          schemeId: row.id,
          schemeName: row.schemeName
        }
      });
    },
```

- [ ] **步骤 3：eslint 校验**

运行：`npx eslint frontend/src/views/SchemeManage/index.vue`
预期：无报错。

- [ ] **步骤 4：Commit**

```bash
git add frontend/src/views/SchemeManage/index.vue
git commit -m "feat: add detail data entry button to scheme manage page"
```

---

## 任务 5：整体验证

**文件：** 无

- [ ] **步骤 1：全量 build 校验**

运行：`npm run build`（在 `frontend/` 目录）
预期：构建成功，无编译错误。

- [ ] **步骤 2：手动浏览器验证**

运行 `npm run dev` 后逐一验证：
1. 方案管理页操作列出现"详细数据"按钮，点击进入 `/SchemeDetailData`，顶部显示方案名/状态/时间。
2. 左侧红方/蓝方 Tab 可切换，活动项为红/蓝色底，带装备数量角标。
3. 切换阵营后右侧装备列表随之变化，配色跟随阵营色。
4. 切换装备后内容重置为"基本信息"。
5. 四个信息分类（基本信息/实时状态/作战指标/数据记录）切换正常，内容形态分别：属性表/状态卡片/指标表格/数据表格。
6. 无历史时点返回能回到方案管理页。
7. 直接访问 `/SchemeDetailData`（无参数）提示"缺少方案参数"且不崩溃。

---

## 自检记录

- **规格覆盖度：** 按钮（任务4）、独立页面（任务2）、红蓝方 Tab（任务2）、装备 Tab（任务2）、信息分类 Tab（任务2）、mock 数据（任务1）、接口预留（任务3）、返回（任务2）、边界处理（任务2 空状态与无参数）均有对应任务。✓
- **占位符扫描：** 无"待定/TODO"占位（代码中的 `TODO` 注释是真实数据接入标记，附说明）。✓
- **类型一致性：** `getMockSchemeDetailData(schemeId)` 在 mock.js 定义、index.vue 引用；`currentSide/currentEquips/currentEquip/currentSideColor` 在 computed 与模板中使用一致；`activeSide/activeEquipId/activeInfoTab` 命名全程一致。✓
