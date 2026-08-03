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
          <div class="info-tabs" v-if="currentInfoCategories.length > 0">
            <div
              v-for="cat in currentInfoCategories"
              :key="cat.key"
              class="info-tab"
              :class="{ active: activeInfoTab === cat.key }"
              :style="activeInfoTab === cat.key ? { color: currentSideColor, borderBottomColor: currentSideColor } : {}"
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
          <div v-else class="empty-tip">暂无装备数据</div>
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
      activeSide: 'red',
      activeEquipId: null,
      activeInfoTab: null
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
    },
    currentInfoCategories() {
      return (this.currentEquip && this.currentEquip.info && this.currentEquip.info.categories) || [];
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
    this.loadData(this.schemeId);
  },
  methods: {
    loadData(schemeId) {
      return API.sampleCascade.getSchemeDetailData(schemeId).then(res => {
        // 拦截器已保证此处为成功响应（业务失败由拦截器统一提示并 reject）
        this.scheme = (res.data && res.data.data) || { schemeName: '', status: 0, createTime: '', sides: [] };
        if (this.$route.query.schemeName) {
          this.scheme.schemeName = this.$route.query.schemeName;
        }
        this.initSelection();
      }).catch(err => {
        console.error('load detail data error:', err);
      });
    },
    initSelection() {
      if (this.scheme.sides.length > 0 && !this.scheme.sides.some(s => s.side === this.activeSide)) {
        this.activeSide = this.scheme.sides[0].side;
      }
      if (this.currentEquips.length > 0) {
        this.activeEquipId = this.currentEquips[0].id;
        this.resetInfoTab();
      }
    },
    resetInfoTab() {
      const cats = this.currentInfoCategories;
      if (cats.length === 0) {
        this.activeInfoTab = null;
      } else if (!cats.some(c => c.key === this.activeInfoTab)) {
        this.activeInfoTab = cats[0].key;
      }
    },
    onSideChange(side) {
      this.activeSide = side;
      this.activeEquipId = this.currentEquips.length > 0 ? this.currentEquips[0].id : null;
      this.resetInfoTab();
    },
    onEquipChange(id) {
      this.activeEquipId = id;
      this.resetInfoTab();
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
.side-tabs {
  width: 120px;
  flex-shrink: 0;
  border-right: 1px solid #3e524b;
  padding-right: 12px;
}
.side-tab {
  padding: 10px 12px;
  margin-bottom: 8px;
  border-radius: 4px;
  border: 1px solid #3e524b;
  color: #ffffff;
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
  border-bottom: 1px solid #3e524b;
}
.equip-tab {
  padding: 8px 14px;
  border: 1px solid #3e524b;
  border-radius: 4px;
  color: #ffffff;
  cursor: pointer;
  font-size: 13px;
}
.equip-tab.active {
  font-weight: 600;
}
.info-panel {
  margin-top: 10px;
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
