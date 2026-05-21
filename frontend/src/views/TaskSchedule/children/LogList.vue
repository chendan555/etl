<template>
  <base-dialog v-bind="$attrs" v-on="$listeners" @save="saveConfig">
    <div class="logList">
      <div class="filterDiv">
        <el-button type="primary" @click="removeAll" size="mini" icon="el-icon-delete">清空全部日志</el-button>
      </div>
      <div class="tableDiv" v-loading="loading">
        <base-table
          :tableColumnConfig="tableConfig.tableColumnConfig"
          :tableData="tableConfig.tableData"
          :isShowOperation="tableConfig.isShowOperation"
          :operationWidth="'80'"
        >
          <div slot="operation" slot-scope="{row}">
            <el-tooltip class="item" effect="dark" content="查看详情" placement="top">
              <el-button
                type="text"
                @click="handleView(row)"
                style="color:#409eff"
                icon="el-icon-view"
              ></el-button>
            </el-tooltip>
            <el-tooltip class="item" effect="dark" content="删除" placement="top">
              <el-button
                type="text"
                @click="removeItem(row)"
                style="color:#ff0000"
                icon="el-icon-delete"
              ></el-button>
            </el-tooltip>
          </div>
        </base-table>
      </div>
      <div class="pageDiv">
        <base-pagination :data="pageConfig" @current="changePageNum" @sizes="changePageSize"></base-pagination>
      </div>
    </div>

    <RecordList
      v-if="dialogConfig_record.visible"
      :dialogConfig="dialogConfig_record"
      :jobId="jobId"
      :planId="crtLog.planId"
      :sourceId="crtLog.sourceId"
      :logId="crtLog.logId"
      :recordType="crtLog.type"
      @closeDialog="closeRecordDialog"
    ></RecordList>
  </base-dialog>
</template>
<script>
import BaseDialog from '@/components/BaseDialog/index.vue';
import BaseTable from '@/components/BaseTable/index.vue';
import BasePagination from '@/components/BasePagination/index.vue';
import RecordList from './RecordList.vue';
import config from '../config';
export default {
  name: 'logList',
  components: { BaseDialog, BaseTable, BasePagination, RecordList },
  props: {
    jobId: {
      tyep: String
    }
  },
  data() {
    return {
      tableConfig: {
        tableColumnConfig: config.logListTableColumnConfig,
        tableData: [],
        isShowOperation: true
      },
      loading: false,
      pageConfig: {
        pageSize: 20,
        pageNum: 1,
        total: 0
      },
      dialogConfig_record: {
        width: '80%',
        title: '执行记录列表',
        visible: false
      },
      crtLog: null
    };
  },
  mounted() {
    this.getLogList();
  },
  methods: {
    /**
     * @description 获取日志列表
     */
    getLogList() {
      this.loading = true;
      const params = {
        pageIndex: this.pageConfig.pageNum,
        pageSize: this.pageConfig.pageSize,
        searchData: { jobId: this.jobId }
      };
      this.$axios
        .post(`/api/quartz/plan/list/by/jobId`, params)
        .then(({ data }) => {
          console.log(data);
          this.tableConfig.tableData = data.data.records;
          this.pageConfig.total = data.data.total;
        })
        .finally(() => {
          this.loading = false;
        });
    },
    /**
     * @description 获取调度任务详情
     * @param row
     */
    handleView({ row }) {
      this.crtLog = row;
      this.dialogConfig_record.visible = true;
    },
    /**
     * @description 删除
     * @param row
     */
    removeItem({ row }) {
      this.$utils.confirm().then(() => {
        const loading = this.$utils.setLoading('删除中...');
        const params = {
          jobLogIds: row.logId
        };
        this.$axios
          .delete('/api/quartz/log/delete', { params })
          .then(({ data }) => {
            this.$utils.setMessage(data.success, data.result);
            if (data.success) {
              this.getLogList();
            }
          })
          .finally(() => {
            loading.close();
          });
      });
    },
    /**
     * @description 清空日志
     */
    removeAll() {
      this.$utils.confirm().then(() => {
        const loading = this.$utils.setLoading('删除中...');
        const params = {
          jobId: this.jobId
        };
        this.$axios
          .delete('/api/quartz/log/clean', { params })
          .then(({ data }) => {
            this.$utils.setMessage(data.success, data.result);
            if (data.success) {
              this.getLogList();
            }
          })
          .finally(() => {
            loading.close();
          });
      });
    },
    /**
     * @description 分页切换
     * @param num
     */
    changePageNum(num) {
      this.pageConfig.pageNum = num;
      this.getLogList();
    },
    /**
     * @description 分页切换
     * @param size
     */
    changePageSize(size) {
      this.pageConfig.pageNum = 1;
      this.pageConfig.pageSize = size;
      this.getLogList();
    },
    /**
     * @description 关闭弹窗
     */
    saveConfig() {
      this.$emit('closeDialog');
    },
    closeRecordDialog() {
      this.crtLog = null;
      this.dialogConfig_record.visible = false;
    }
  }
};
</script>
<style lang="scss" scoped>
.logList {
  width: 100%;
  height: calc(100% - 40px);
  .tableDiv {
    width: 100%;
    height: calc(100% - 80px);
  }
  .filterDiv,
  .pageDiv {
    width: 100%;
    height: 40px;
    display: flex;
    align-items: center;
  }
}
</style>
