<template>
  <div class="scheme-detail-data">
    <!-- 顶部信息条 -->
    <div class="detail-header">
      <el-button size="small" @click="goBack">← 返回</el-button>
      <span class="header-title">{{ scheme.schemeName || '方案详细数据' }}</span>
      <el-tag :type="scheme.status === 1 ? 'success' : 'info'" size="small">
        {{ scheme.status === 1 ? '已完成' : '未完成' }}
      </el-tag>
      <el-select
        v-model="selectedDetailId"
        placeholder="请选择方案明细"
        size="small"
        style="width: 200px; margin-left: 12px;"
        @change="onDetailChange"
      >
        <el-option
          v-for="item in schemeDetails"
          :key="item.id"
          :label="item.ybName || item.id"
          :value="item.id"
        />
      </el-select>
      <span class="header-time">{{ scheme.createTime || '' }}</span>
    </div>

    <div class="detail-body">
      <!-- 左侧装备树 -->
      <div class="equip-tree-panel">
        <div class="panel-title">装备列表</div>
        <el-tree
          :data="equipTree"
          :props="treeProps"
          node-key="id"
          :highlight-current="true"
          :expand-on-click-node="false"
          @node-click="onEquipNodeClick"
        />
      </div>

      <!-- 右侧装备详情 -->
      <div class="equip-detail-panel">
        <div class="info-panel">
          <div class="info-tabs" v-if="currentInfoCategories.length > 0">
            <div
              v-for="cat in currentInfoCategories"
              :key="cat.key"
              class="info-tab"
              :class="{ active: activeInfoTab === cat.key }"
              @click="onInfoTabChange(cat.key)"
            >
              {{ cat.label }}
            </div>
          </div>

          <div class="info-content" v-if="activeCategory && activeCategory.items.length > 0">
            <!-- 统一表格：表头来自 items 的字段名 -->
            <el-table :data="activeCategory.items" border size="small" class="info-content-table">
              <el-table-column
                v-for="key in infoTableColumns"
                :key="key"
                :prop="key"
                :label="key"
              />
            </el-table>
          </div>

          <!-- 空状态（element-ui 2.8.2 无 el-empty，用纯文本） -->
          <div v-else class="empty-tip">请选择装备查看详情</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import API from '@/api/index';

export default {
  name: 'SchemeDetailData',
  meta: {
    title: '方案详细数据'
  },
  data() {
    return {
      schemeId: null,
      scheme: { schemeName: '', status: 0, createTime: '', sides: [] },
      schemeDetails: [],
      selectedDetailId: null,
      selectedYbList: null,
      equipTree: [],
      activeEquipId: null,
      activeInfoTab: null,
      treeProps: {
        children: 'children',
        label: 'equipName'
      }
    };
  },
  computed: {
    currentInfoCategories() {
      // 从方案详情数据中获取信息分类（TABLE 表数据）
      return (this.scheme && this.scheme.sides && this.scheme.sides.length > 0 && this.scheme.sides[0].equipmentList && this.scheme.sides[0].equipmentList[0] && this.scheme.sides[0].equipmentList[0].info && this.scheme.sides[0].equipmentList[0].info.categories) || [];
    },
    activeCategory() {
      const cat = this.currentInfoCategories.find(c => c.key === this.activeInfoTab) || null;
      return cat ? { ...cat, items: cat.items || [] } : null;
    },
    infoTableColumns() {
      if (!this.activeCategory || this.activeCategory.items.length === 0) return [];
      return Object.keys(this.activeCategory.items[0]);
    }
  },
  created() {
    this.schemeId = this.$route.query.schemeId;
    if (!this.schemeId) {
      this.$message.warning('缺少方案参数');
      return;
    }
    this.loadSchemeDetails(this.schemeId);
    this.loadEquipTree(this.schemeId);
  },
  methods: {
    loadSchemeDetails(schemeId) {
      return API.sampleCascade.getSchemeById(schemeId).then(res => {
        if (res.data && res.data.success) {
          this.schemeDetails = res.data.data.detailList || [];
        }
      }).catch(err => {
        console.error('load scheme details error:', err);
      });
    },
    loadEquipTree(schemeId) {
      return API.sampleCascade.getEquipTree(schemeId).then(res => {
        if (res.data && res.data.success) {
          this.equipTree = res.data.data || [];
        }
      }).catch(err => {
        console.error('load equip tree error:', err);
      });
    },
    onDetailChange(detailId) {
      const detail = this.schemeDetails.find(d => d.id === detailId);
      if (detail) {
        this.selectedYbList = detail.ybList;
        this.loadEquipTree(this.schemeId);
      }
    },
    onEquipNodeClick(data) {
      console.log('clicked equip:', data);
      this.activeEquipId = data.equipId;
    },
    resetInfoTab() {
      const cats = this.currentInfoCategories;
      if (cats.length === 0) {
        this.activeInfoTab = null;
      } else if (!cats.some(c => c.key === this.activeInfoTab)) {
        this.activeInfoTab = cats[0].key;
      }
    },
    onInfoTabChange(key) {
      this.activeInfoTab = key;
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
  border-bottom: 1px solid #3e524b;
}
.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #ffffff;
}
.header-time {
  margin-left: auto;
  color: #b7c0c7;
  font-size: 13px;
}
.detail-body {
  flex: 1;
  display: flex;
  margin-top: 12px;
  min-height: 0;
}
.equip-tree-panel {
  width: 240px;
  flex-shrink: 0;
  border-right: 1px solid #3e524b;
  padding-right: 12px;
  overflow: auto;
}
.equip-tree-panel ::v-deep .el-tree {
  background: transparent;
}
.equip-tree-panel ::v-deep .el-tree-node__content {
  color: #ffffff;
  background: transparent;
}
.equip-tree-panel ::v-deep .el-tree-node__content:hover {
  background: rgba(255, 255, 255, 0.1);
}
.equip-tree-panel ::v-deep .el-tree-node.is-current > .el-tree-node__content {
  background: #409EFF !important;
  color: #ffffff !important;
}
.equip-tree-panel ::v-deep .el-tree-node__expand-icon {
  color: #ffffff;
}
.equip-tree-panel ::v-deep .el-tree-node__expand-icon.is-leaf {
  color: transparent;
}
.panel-title {
  font-size: 14px;
  font-weight: 600;
  color: #ffffff;
  padding-bottom: 8px;
  border-bottom: 1px solid #3e524b;
  margin-bottom: 8px;
}
.equip-detail-panel {
  flex: 1;
  margin-left: 12px;
  display: flex;
  flex-direction: column;
  min-width: 0;
}
.info-panel {
  border: 1px solid #3e524b;
  border-radius: 4px;
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}
.info-tabs {
  display: flex;
  border-bottom: 1px solid #3e524b;
}
.info-tab {
  padding: 10px 16px;
  color: #ffffff;
  cursor: pointer;
  font-size: 13px;
  border-bottom: 2px solid transparent;
  margin-bottom: -1px;
}
.info-tab.active {
  font-weight: 600;
  border-bottom-color: #409EFF;
  color: #409EFF;
}
.info-content {
  flex: 1;
  overflow: auto;
  padding: 12px;
}
.info-content-table {
  width: 100%;
}
.empty-tip {
  text-align: center;
  color: #b7c0c7;
  padding: 40px 0;
  font-size: 14px;
}
</style>
