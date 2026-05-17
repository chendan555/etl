<template>
  <div class="taskSchedule">
    <base-table-frame>
      <div class="filterdiv" slot="filter">
        <div class="leftdiv">
          <el-button icon="el-icon-connection" type="primary" size="mini" @click="newTaskSchedule">新建</el-button>
        </div>
        <div class="rightdiv">
          <filter-form
            :filterFormConfig="filterFormConfig"
            @search="filterTableData"
            @reset="resetFilter"
          ></filter-form>
        </div>
      </div>
      <div class="tablediv" slot="table" v-loading="loading">
        <base-table
          :tableColumnConfig="tableConfig.tableColumnConfig"
          :tableData="tableConfig.tableData"
          :isShowOperation="tableConfig.isShowOperation"
          :operationWidth="'150'"
        >
          <div slot="operation" slot-scope="{row}">
            <el-tooltip class="item" effect="dark" content="编辑" placement="top">
              <el-button
                type="text"
                @click="handleView(row)"
                style="color:#fefa8e"
                icon="el-icon-edit"
              ></el-button>
            </el-tooltip>
            <el-tooltip class="item" effect="dark" content="修改调度任务状态" placement="top">
              <el-button
                type="text"
                @click="changeStatus(row)"
                style="color:#15dea6"
                icon="el-icon-set-up"
              ></el-button>
            </el-tooltip>
            <el-tooltip class="item" effect="dark" content="执行一次调度任务" placement="top">
              <el-button
                type="text"
                @click="runJobOnce(row)"
                style="color:#2dd20f"
                icon="el-icon-video-play"
              ></el-button>
            </el-tooltip>
            <el-tooltip class="item" effect="dark" content="查看日志" placement="top">
              <el-button
                type="text"
                @click="showJobLog(row)"
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
      <div class="pagediv" slot="page">
        <base-pagination :data="pageConfig" @current="changePageNum" @sizes="changePageSize"></base-pagination>
      </div>
    </base-table-frame>
    <!-- 弹窗 -->

    <ConfigDialog
      v-if="dialogConfig.visible"
      :dialogConfig="dialogConfig"
      :tableConfigProp="dataTableConfig"
      :ruleForm="ruleForm"
      @closeDialog="closeDialog"
    ></ConfigDialog>

    <LogList
      v-if="dialogLogList.visible"
      :dialogConfig="dialogLogList"
      :jobId="crtJob.jobId"
      @closeDialog="closeLogListDialog"
    ></LogList>
  </div>
</template>
<script lang="ts">
import Vue from 'vue';
import BaseTableFrame from '@/components/BaseTableFrame/index.vue';
import BaseTable from '@/components/BaseTable/index.vue';
import BasePagination from '@/components/BasePagination/index.vue';
import FilterForm from '@/components/FilterForm/index.vue';
import ConfigDialog from './children/ConfigDialog.vue';
import LogList from './children/LogList.vue';
import config from './config';
import API from '@/api/index';
export default Vue.extend({
  name: 'TaskSchedule',
  components: { BaseTableFrame, BaseTable, BasePagination, ConfigDialog, FilterForm, LogList },
  meta: {
    pageIndex: 110,
    title: '调度任务管理',
    pageType: 'mainNav',
    belongTo: 'receive',
    hidden: true
  },
  data() {
    return {
      crtJob: null,
      ruleForm: null,
      filterFormConfig: {
        ruleForm: this.$utils.deepCopy(config.filterFormConfig.ruleForm),
        formItemConfig: config.filterFormConfig.formItemConfig
      },
      tableConfig: {
        isShowOperation: true,
        tableColumnConfig: config.tableColumnConfig_schedule,
        tableData: []
      },
      pageConfig: {
        pageSize: 20,
        pageNum: 1,
        total: 0
      },
      loading: false,
      dialogConfig: {
        width: '60%',
        title: '调度任务配置',
        visible: false
      },
      dialogLogList: {
        width: '80%',
        title: '调度任务执行日志',
        visible: false
      },
      dataTableConfig: {
        tableColumnConfig: <any>[],
        tableData: [],
        isShowOperation: true,
        size: 0,
        row: {}
      }
    };
  },
  created() {
    this.getJobList();
  },
  methods: {
    /**
     * description 新建任务调度
     */
    newTaskSchedule() {
      this.dialogConfig.visible = true;
    },
    getJobList() {
      this.loading = true;
      let params = {
        pageIndex: this.pageConfig.pageNum,
        pageSize: this.pageConfig.pageSize,
        searchData: { ...this.filterFormConfig.ruleForm }
      };

      this.$axios
        .post('/api/quartz/task/list.do', params)
        .then(({ data }: any) => {
          if (data.code == 200) {
            this.loading = false;
            this.tableConfig.tableData = data.data.records;
            this.pageConfig.total = data.data.total;
            this.pageConfig.pageNum = data.data.current;
          }
        })
        .finally(() => {
          this.loading = false;
        });
    },
    filterTableData() {
      this.pageConfig.pageNum = 1;
      this.getJobList();
    },
    resetFilter() {
      this.filterFormConfig.ruleForm = this.$utils.deepCopy(config.filterFormConfig.ruleForm);
      this.pageConfig.pageNum = 1;
      this.getJobList();
    },
    // 分页切换
    changePageNum(num: number) {
      this.pageConfig.pageNum = num;
      this.getJobList();
    },
    changePageSize(size: number) {
      this.pageConfig.pageNum = 1;
      this.pageConfig.pageSize = size;
      this.getJobList();
    },
    /**
     * @description 获取调度任务详情
     * @param row
     */
    handleView({ row }: any) {
      this.$axios.get(`/api/quartz/task/${row.jobId}.do`).then(({ data }: any) => {
        console.log(data);
        this.ruleForm = data.data;
        this.dialogConfig.visible = true;
      });
    },
    removeItem({ row }: any) {
      this.$utils.confirm().then(() => {
        const loading = this.$utils.setLoading('删除中...');
        const params = {
          jobIds: row.jobId
        };
        this.$axios
          .delete('/api/quartz/task/delete.do', { params })
          .then(({ data }: any) => {
            this.$utils.setMessage(data.success, data.result);
            if (data.success) {
              this.getJobList();
            }
          })
          .finally(() => {
            loading.close();
          });
      });
    },
    /**
     * @description 修改调度任务状态
     * @param row
     */
    changeStatus({ row }: any) {
      this.$utils.confirm(`确认修改${row.jobName}调度任务的状态？`).then(() => {
        const loading = this.$utils.setLoading('修改中...');
        const params = {
          jobId: row.jobId,
          status: row.status === '0' ? '1' : '0'
        };
        this.$axios
          .put('/api/quartz/task/changeStatus.do', params)
          .then(({ data }: any) => {
            this.$utils.setMessage(data.success, data.result);
            if (data.success) {
              this.getJobList();
            }
          })
          .finally(() => {
            loading.close();
          });
      });
    },
    /**
     * @description 执行一次调度任务
     * @param row
     */
    runJobOnce({ row }: any) {
      this.$utils.confirm(`确认执行一次${row.jobName}调度任务？`).then(() => {
        const loading = this.$utils.setLoading('执行中...');
        const params = {
          jobId: row.jobId
        };
        this.$axios
          .post('/api/quartz/task/run.do', params)
          .then(({ data }: any) => {
            this.$utils.setMessage(data.success, data.result);
            if (data.success) {
              this.getJobList();
            }
          })
          .finally(() => {
            loading.close();
          });
      });
    },
    /**
     * @description 查看调度任务执行日志
     * @param row
     */
    showJobLog({ row }: any) {
      this.crtJob = row;
      this.dialogLogList.visible = true;
    },
    closeLogListDialog() {
      this.crtJob = null;
      this.dialogLogList.visible = false;
    },
    closeDialog(bool?: boolean) {
      this.dialogConfig.visible = false;
      this.ruleForm = null;
      if (bool) {
        this.getJobList();
      }
    }
  }
});
</script>
<style lang="scss" scoped>
@import '@/styles/theme.scss';
.taskSchedule {
  width: 100%;
  height: 100%;
  background: $contBg;
  border-radius: 6px;
  padding: 5px;
  .filterdiv {
    width: 100%;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    .leftdiv {
      flex: 1;
    }
    .rightdiv {
      width: 600px;
    }
  }
  .pagediv {
    width: 100%;
    height: 40px;
  }
  .tablediv {
    width: 100%;
    height: 100%;
  }
}
</style>
