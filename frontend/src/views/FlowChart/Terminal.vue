<template>
  <el-tabs type="border-card">
    <el-tab-pane label="步骤度量">
      <base-table height="243px" :tableColumnConfig="stepTableConfig" :tableData="stepTableData"></base-table>
    </el-tab-pane>
    <el-tab-pane label="日志">
      <textarea v-model="runLog" class="log-area"></textarea>
    </el-tab-pane>
    <el-tab-pane label="批量进度">
      <div class="batch-progress-container">
        <!-- 进度条区域 -->
        <div class="progress-section">
          <div class="progress-header">
            <span class="progress-title">批量执行进度</span>
            <span class="progress-percent">{{ progressPercent }}%</span>
          </div>
          <el-progress
            :percentage="progressPercent"
            :status="progressStatus"
            :stroke-width="20"
            :text-inside="true"
          ></el-progress>
        </div>

        <!-- 统计信息区域 -->
        <div class="stats-section">
          <el-row :gutter="20">
            <el-col :span="3">
              <div class="stat-item">
                <div class="stat-label">总执行次数</div>
                <div class="stat-value">{{ batchProgress.total }}</div>
              </div>
            </el-col>
            <el-col :span="3">
              <div class="stat-item">
                <div class="stat-label">已完成</div>
                <div class="stat-value">{{ batchProgress.current }}</div>
              </div>
            </el-col>
            <el-col :span="3">
              <div class="stat-item">
                <div class="stat-label">成功</div>
                <div class="stat-value success">{{ batchProgress.successCount }}</div>
              </div>
            </el-col>
            <el-col :span="3">
              <div class="stat-item">
                <div class="stat-label">失败</div>
                <div class="stat-value danger">{{ batchProgress.failCount }}</div>
              </div>
            </el-col>
            <el-col :span="3">
              <div class="stat-item">
                <div class="stat-label">已用时间</div>
                <div class="stat-value">{{ formatTime(batchProgress.elapsedTime) }}</div>
              </div>
            </el-col>
            <el-col :span="3">
              <div class="stat-item">
                <div class="stat-label">预计剩余</div>
                <div class="stat-value">{{ formatTime(batchProgress.estimatedRemaining) }}</div>
              </div>
            </el-col>
            <el-col :span="3">
              <div class="stat-item">
                <div class="stat-label">总处理行数</div>
                <div class="stat-value">{{ batchProgress.totalRows }}</div>
              </div>
            </el-col>
            <el-col :span="3">
              <div class="stat-item">
                <el-button
                  type="danger"
                  size="small"
                  :disabled="!canCancel"
                  @click="cancelBatch"
                >
                  取消执行
                </el-button>
              </div>
            </el-col>
          </el-row>
        </div>

        <!-- 批次详情表格 -->
        <div class="batch-details-section">
          <div class="details-header" @click="toggleDetails">
            <span>批次详情</span>
            <i :class="showDetails ? 'el-icon-arrow-up' : 'el-icon-arrow-down'"></i>
          </div>
          <el-collapse-transition>
            <div v-show="showDetails">
              <base-table
                height="180px"
                :tableColumnConfig="batchTableConfig"
                :tableData="batchProgress.batchDetails"
              ></base-table>
            </div>
          </el-collapse-transition>
        </div>
      </div>
    </el-tab-pane>
  </el-tabs>
</template>
<script>
import BaseTable from "@/components/BaseTable/index.vue";
export default {
  components: { BaseTable },
  props: {
    runLog: {
      type: String,
      default: ""
    },
    stepTableData: {
      type: Array,
      default: () => {
        return [];
      }
    },
    batchProgress: {
      type: Object,
      default: () => {
        return {
          taskId: '',
          current: 0,
          total: 0,
          successCount: 0,
          failCount: 0,
          elapsedTime: 0,
          estimatedRemaining: 0,
          totalRows: 0,
          status: 'idle', // idle, running, completed, cancelled, error
          batchDetails: []
        };
      }
    }
  },
  data() {
    return {
      showDetails: false,
      stepTableConfig: [
        {
          label: "步骤名称",
          prop: "0",
          showOverflowTooltip: true
        },
        {
          label: "读",
          prop: "2",
          showOverflowTooltip: true
        },
        {
          label: "写",
          prop: "3",
          showOverflowTooltip: true
        },
        {
          label: "输入",
          prop: "4",
          showOverflowTooltip: true
        },
        {
          label: "输出",
          prop: "5",
          showOverflowTooltip: true
        },
        {
          label: "更新",
          prop: "6",
          showOverflowTooltip: true
        },
        {
          label: "拒绝",
          prop: "7",
          showOverflowTooltip: true
        },
        {
          label: "错误",
          prop: "8",
          showOverflowTooltip: true
        },
        {
          label: "状态",
          prop: "9",
          showOverflowTooltip: true
        },
        {
          label: "时间",
          prop: "10",
          showOverflowTooltip: true
        },
        {
          label: "速度（条/秒）",
          prop: "11",
          width: '130',
          showOverflowTooltip: true
        }
      ],
      batchTableConfig: [
        {
          label: "批次",
          prop: "index",
          width: "80",
          showOverflowTooltip: true
        },
        {
          label: "参数值",
          prop: "params",
          showOverflowTooltip: true,
          propToNameFn: (val) => {
            return val ? JSON.stringify(val) : '-';
          }
        },
        {
          label: "状态",
          prop: "status",
          width: "80",
          showOverflowTooltip: true,
          propToNameFn: (val) => {
            return val === 'success' ? '成功' : '失败';
          }
        },
        {
          label: "耗时",
          prop: "elapsedTime",
          width: "100",
          showOverflowTooltip: true,
          propToNameFn: (val) => {
            return this.formatTime(val);
          }
        },
        {
          label: "影响行数",
          prop: "rowCount",
          width: "100",
          showOverflowTooltip: true
        },
        {
          label: "错误信息",
          prop: "errorMsg",
          showOverflowTooltip: true
        }
      ]
    };
  },
  computed: {
    progressPercent() {
      if (this.batchProgress.total === 0) return 0;
      return Math.round((this.batchProgress.current / this.batchProgress.total) * 100);
    },
    progressStatus() {
      if (this.batchProgress.status === 'completed') return 'success';
      if (this.batchProgress.status === 'cancelled') return 'warning';
      if (this.batchProgress.status === 'error') return 'exception';
      return undefined;
    },
    canCancel() {
      return this.batchProgress.status === 'running';
    }
  },
  methods: {
    formatTime(ms) {
      if (!ms || ms === 0) return '0s';
      const seconds = Math.floor(ms / 1000);
      const minutes = Math.floor(seconds / 60);
      const hours = Math.floor(minutes / 60);

      if (hours > 0) {
        return `${hours}h${minutes % 60}m${seconds % 60}s`;
      } else if (minutes > 0) {
        return `${minutes}m${seconds % 60}s`;
      } else {
        return `${seconds}s`;
      }
    },
    toggleDetails() {
      this.showDetails = !this.showDetails;
    },
    cancelBatch() {
      this.$emit('cancel-batch', this.batchProgress.taskId);
    },
    resetProgress() {
      this.batchProgress = {
        taskId: '',
        current: 0,
        total: 0,
        successCount: 0,
        failCount: 0,
        elapsedTime: 0,
        estimatedRemaining: 0,
        totalRows: 0,
        status: 'idle',
        batchDetails: []
      };
    }
  }
};
</script>

<style scoped>
.batch-progress-container {
  padding: 10px;
  height: 100%;
  overflow-y: auto;
}

.progress-section {
  margin-bottom: 15px;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.progress-title {
  font-size: 14px;
  font-weight: bold;
  color: #303133;
}

.progress-percent {
  font-size: 16px;
  font-weight: bold;
  color: #409eff;
}

.stats-section {
  margin-bottom: 15px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.stat-item {
  text-align: center;
}

.stat-label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.stat-value.success {
  color: #67c23a;
}

.stat-value.danger {
  color: #f56c6c;
}

.batch-details-section {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  overflow: hidden;
}

.details-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 15px;
  background-color: #f5f7fa;
  cursor: pointer;
  font-size: 14px;
  font-weight: bold;
  color: #303133;
}

.details-header:hover {
  background-color: #e6e8eb;
}

.log-area {
  width: 100%;
  height: 243px;
  padding: 10px;
  box-sizing: border-box;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  resize: none;
  font-family: monospace;
  font-size: 14px;
  line-height: 1.6;
  color: #606266;
  background-color: #fafafa;
}
</style>

