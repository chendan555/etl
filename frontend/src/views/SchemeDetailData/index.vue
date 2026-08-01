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
              @click="onInfoTabChange(tab.key)"
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
    this.loadData(this.schemeId);
  },
  methods: {
    loadData(schemeId) {
      return API.sampleCascade.getSchemeDetailData(schemeId).then(res => {
        if (res.data && res.data.success) {
          this.scheme = res.data.data;
          if (this.$route.query.schemeName) {
            this.scheme.schemeName = this.$route.query.schemeName;
          }
          this.initSelection();
        } else {
          this.$message.error((res.data && res.data.msg) || '加载详细数据失败');
        }
      }).catch(err => {
        console.error('load detail data error:', err);
        this.$message.error('加载详细数据失败');
      });
    },
    initSelection() {
      if (this.scheme.sides.length > 0 && !this.scheme.sides.some(s => s.side === this.activeSide)) {
        this.activeSide = this.scheme.sides[0].side;
      }
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
.info-table {
  width: 100%;
  border-collapse: collapse;
}
.info-table td {
  border: 1px solid #3e524b;
  padding: 8px 12px;
  font-size: 13px;
}
.info-label {
  width: 140px;
  background: #233129;
  color: #b7c0c7;
}
.info-value {
  color: #ffffff;
}
.status-cards {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}
.status-card {
  width: 180px;
  border: 1px solid #3e524b;
  border-radius: 4px;
  padding: 12px;
}
.status-label {
  color: #b7c0c7;
  font-size: 12px;
  margin-bottom: 6px;
}
.status-value {
  color: #ffffff;
  font-size: 16px;
  font-weight: 600;
}
.empty-tip {
  text-align: center;
  color: #b7c0c7;
  padding: 40px 0;
  font-size: 14px;
}
</style>
