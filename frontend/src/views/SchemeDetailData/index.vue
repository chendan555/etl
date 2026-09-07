<template>
  <div class="scheme-detail-data">
    <!-- 顶部信息条 -->
    <div class="detail-header">
      <el-button size="small" @click="goBack">← 返回</el-button>
      <span class="header-title">{{ scheme.schemeName || '方案详细数据' }}</span>
      <el-tag :type="scheme.status === 1 ? 'success' : 'info'" size="small">
        {{ scheme.status === 1 ? '已完成' : '未完成' }}
      </el-tag>

      <!-- 一级：样本（task_design_example） -->
      <el-select
        v-model="selectedSampleId"
        placeholder="选择样本"
        size="small"
        style="width: 180px; margin-left: 12px;"
        @change="onSampleChange"
      >
        <el-option
          v-for="item in sampleOptions"
          :key="item.ybId"
          :label="item.ybName"
          :value="item.ybId"
        />
      </el-select>

      <!-- 二级：实例（deduce_task_id） -->
      <el-select
        v-model="selectedInstanceId"
        placeholder="选择实例"
        size="small"
        style="width: 180px;"
        :disabled="!selectedSampleId"
        @change="onInstanceChange"
      >
        <el-option
          v-for="id in instanceOptions"
          :key="id"
          :label="'实例 ' + id"
          :value="id"
        />
      </el-select>

      <span class="header-time">{{ scheme.createTime || '' }}</span>
    </div>

    <div class="detail-body">
      <!-- 主题数据 tabs -->
      <el-tabs v-model="activeTopicTab" type="border-card" v-if="topicDataList.length > 0">
        <el-tab-pane
          v-for="(topicData, index) in topicDataList"
          :key="index"
          :label="topicData.topicName"
          :name="'topic_' + index"
        >
          <el-table
            :data="paginatedRows(topicData)"
            border
            size="small"
            style="width: 100%"
            max-height="500"
          >
            <el-table-column
              v-for="col in topicData.columns"
              :key="col.name"
              :prop="col.name"
              :label="col.alias"
              show-overflow-tooltip
            />
          </el-table>
          <el-pagination
            v-if="topicData.total > topicPageSize"
            layout="total, prev, pager, next"
            :total="topicData.total"
            :page-size="topicPageSize"
            :current-page="topicPage[index] || 1"
            small
            style="margin-top: 5px; text-align: right;"
            @current-change="val => onTopicPageChange(index, val)"
          />
          <div v-if="topicData.rows.length === 0" class="empty-tip">暂无数据</div>
        </el-tab-pane>
      </el-tabs>

      <div v-else-if="selectedInstanceId" class="empty-tip">暂无引接数据</div>
      <div v-else class="empty-tip">请选择样本和实例查看数据</div>
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
      scheme: { schemeName: '', status: 0, createTime: '' },
      schemeDetails: [],
      selectedSampleId: null,
      selectedInstanceId: null,
      sampleOptions: [],    // [{ybId, ybName, instanceIds: [1,2,3]}]
      instanceOptions: [],  // [1, 2, 3]
      topicDataList: [],    // [{topicName, topicCode, columns, rows, total}]
      activeTopicTab: 'topic_0',
      topicPage: {},         // {topicIndex: currentPage}
      topicPageSize: 20
    };
  },
  created() {
    this.schemeId = this.$route.query.schemeId;
    if (!this.schemeId) {
      this.$message.warning('缺少方案参数');
      return;
    }
    this.loadScheme();
  },
  methods: {
    loadScheme() {
      API.sampleCascade.getSchemeById(this.schemeId).then(res => {
        if (res.data && res.data.success) {
          const data = res.data.data;
          this.scheme = data;
          this.schemeDetails = data.detailList || [];

          // 构建一级下拉：按 ybName 去重，收集每个样本下的实例列表
          const sampleMap = new Map();
          for (const detail of this.schemeDetails) {
            const name = detail.ybName || '未知';
            const ids = detail.ybList ? detail.ybList.split(',').map(Number).filter(n => !isNaN(n)) : [];
            if (sampleMap.has(name)) {
              sampleMap.get(name).instanceIds.push(...ids);
            } else {
              sampleMap.set(name, { ybId: detail.ybId, ybName: name, instanceIds: ids });
            }
          }
          this.sampleOptions = Array.from(sampleMap.values());
        }
      });
    },
    onSampleChange(ybId) {
      this.selectedInstanceId = null;
      this.topicDataList = [];
      this.topicPage = {};
      const sample = this.sampleOptions.find(s => s.ybId === ybId);
      this.instanceOptions = sample ? sample.instanceIds : [];
    },
    onInstanceChange(instanceId) {
      if (!instanceId) {
        this.topicDataList = [];
        this.topicPage = {};
        return;
      }
      this.topicPage = {};
      this.loadTopicData(1);
    },
    loadTopicData(pageIndex) {
      API.sampleCascade.getTopicData({
        schemeId: this.schemeId,
        instanceId: this.selectedInstanceId,
        pageIndex,
        pageSize: this.topicPageSize
      }).then(res => {
        if (res.data && res.data.success) {
          const raw = res.data.data || [];
          this.topicDataList = raw.map(topic => {
            const cols = topic.columns || [];
            const rows = (topic.rows || []).map(arr => {
              const obj = {};
              cols.forEach((col, i) => { obj[col.name] = arr[i] != null ? arr[i] : ''; });
              return obj;
            });
            return { topicName: topic.topicName, topicCode: topic.topicCode, columns: cols, rows, total: topic.total || 0 };
          });
          this.activeTopicTab = this.topicDataList.length > 0 ? 'topic_0' : '';
        } else {
          this.$message.error(res.data.msg || '查询失败');
          this.topicDataList = [];
        }
      }).catch(err => {
        console.error('getTopicData error:', err);
        this.$message.error('查询失败');
        this.topicDataList = [];
      });
    },
    paginatedRows(topicData) {
      // 后端已分页，直接返回 rows
      return topicData.rows;
    },
    onTopicPageChange(index, page) {
      this.$set(this.topicPage, index, page);
      // 只刷新当前 tab 的数据
      const topic = this.topicDataList[index];
      if (!topic) return;
      API.sampleCascade.getTopicData({
        schemeId: this.schemeId,
        instanceId: this.selectedInstanceId,
        pageIndex: page,
        pageSize: this.topicPageSize,
        topicIndex: index
      }).then(res => {
        if (res.data && res.data.success) {
          const raw = res.data.data || [];
          if (raw.length > 0) {
            const t = raw[0];
            const cols = t.columns || [];
            topic.rows = (t.rows || []).map(arr => {
              const obj = {};
              cols.forEach((col, i) => { obj[col.name] = arr[i] != null ? arr[i] : ''; });
              return obj;
            });
            topic.total = t.total || 0;
            // 强制触发 vue 响应式更新
            this.$set(this.topicDataList, index, { ...topic });
          }
        }
      });
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
  margin-top: 12px;
  min-height: 0;
}
.empty-tip {
  text-align: center;
  color: #b7c0c7;
  padding: 40px 0;
  font-size: 14px;
}
.detail-body ::v-deep .el-tabs--border-card {
  background: transparent;
  border-color: #3e524b;
}
.detail-body ::v-deep .el-tabs--border-card > .el-tabs__header {
  background: #1a2e1f;
  border-color: #3e524b;
}
.detail-body ::v-deep .el-tabs--border-card > .el-tabs__header .el-tabs__item {
  color: #b7c0c7;
}
.detail-body ::v-deep .el-tabs--border-card > .el-tabs__header .el-tabs__item.is-active {
  color: #409EFF;
  background: transparent;
}
.detail-body ::v-deep .el-tabs__content {
  background: transparent;
}
.detail-body ::v-deep .el-table {
  background: transparent;
}
.detail-body ::v-deep .el-table .cell {
  color: #c8ddf0;
}
.detail-body ::v-deep .el-table th .cell {
  color: #a0c4e8;
}
</style>
