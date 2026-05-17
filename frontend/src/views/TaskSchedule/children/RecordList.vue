<template>
  <base-dialog v-bind="$attrs" v-on="$listeners" @save="saveConfig">
    <div class="recordList">
      <div class="tableDiv" v-loading="loading">
        <base-table
          :tableColumnConfig="tableConfig.tableColumnConfig"
          :tableData="tableConfig.tableData"
          :isShowOperation="tableConfig.isShowOperation"
          :operationWidth="'80'"
        >
          <div slot="operation" slot-scope="{row}">
            <el-tooltip
              v-if="recordType=== 5 && row.row.outputType===1"
              class="item"
              effect="dark"
              content="下载文件"
              placement="top"
            >
              <el-button
                type="text"
                @click="downloadFile(row)"
                style="color:#409eff"
                icon="el-icon-download"
              ></el-button>
            </el-tooltip>
            <el-tooltip v-else class="item" effect="dark" content="查看详情" placement="top">
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

    <DialogReceiveData
      v-if="dialogConfig.visible"
      :dialogConfig="dialogConfig"
      :recordObj="recordObj"
      :recordType="recordType"
      @closeDialog="closeRecordDialog"
    ></DialogReceiveData>
  </base-dialog>
</template>
<script>
import BaseDialog from '@/components/BaseDialog/index.vue';
import BaseTable from '@/components/BaseTable/index.vue';
import BasePagination from '@/components/BasePagination/index.vue';
import DialogReceiveData from './DialogReceiveData.vue';
import config from '../config';
export default {
  name: 'recordList',
  components: { BaseDialog, BaseTable, BasePagination, DialogReceiveData },
  props: {
    planId: {
      type: String
    },
    sourceId: {
      type: Number
    },
    logId: {
      type: String
    },
    recordType: {
      type: Number
    }
  },
  data() {
    return {
      tableConfig: {
        tableColumnConfig: this.recordType === 5 ? config.tableColumnConfig_process : config.tableColumnConfig_receive,
        tableData: [],
        isShowOperation: true
      },
      loading: false,
      pageConfig: {
        pageSize: 20,
        pageNum: 1,
        total: 0
      },
      dialogConfig: {
        width: '80%',
        title: '数据查看',
        visible: false
      },
      recordObj: null
    };
  },
  mounted() {
    this.getRecordList();
  },
  methods: {
    /**
     * @description 判断记录类型
     */
    getRecordList() {
      switch (this.recordType) {
        case 1:
          this.getReceiveData();
          return;
        case 2:
          this.getReceiveData();
          return;
        case 5:
          this.getProcessData();
          return;
      }
    },
    /**
     * @description 获取记录列表
     */
    getReceiveData() {
      this.loading = true;
      const params = {
        pageIndex: this.pageConfig.pageNum,
        pageSize: this.pageConfig.pageSize,
        searchData: { tableName: '', planId: this.planId, yjMappingId: this.sourceId, batchNum: this.logId }
      };
      this.$axios
        .post(`/api/yj/record/history/page.do`, params)
        .then(({ data }) => {
          this.tableConfig.tableData = data.data.records;
          this.pageConfig.total = data.data.total;
        })
        .finally(() => {
          this.loading = false;
        });
    },
    getProcessData() {
      this.loading = true;
      const params = {
        pageIndex: this.pageConfig.pageNum,
        pageSize: this.pageConfig.pageSize,
        planId: this.planId,
        transId: this.sourceId,
        batchNum: this.logId
      };
      let newConfig = this.$utils.transferToFormData(params);
      this.$axios
        .post(`/api/transNew/log/page.do`, newConfig.formData, newConfig.config)
        .then(({ data }) => {
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
      this.recordObj = row;
      this.dialogConfig.visible = true;
    },
    // 文件下载
    downloadFile({ row }) {
      let url = encodeURI(row.excelPath);
      let href = `api/file/download.do?url=${url}`; // 设置下载路径
      this.$utils.downloadFile(href, row.excelPath);
    },
    closeRecordDialog() {
      this.recordObj = null;
      this.dialogConfig.visible = false;
    },
    /**
     * @description 删除
     * @param row
     */
    removeItem({ row }) {
      this.$utils.confirm().then(() => {
        const loading = this.$utils.setLoading('删除中...');
        let params = {};
        let url = '';
        if (this.recordType === 5) {
          params = { nodeId: row.nodeId, transId: row.transId, fireId: row.batchNum };
          url = '/api/transNew/log/del.do';
        } else {
          params.id = row.id;
          url = '/api/yj/record/history/del.do';
        }
        this.$axios
          .get(url, { params })
          .then(({ data }) => {
            this.$utils.setMessage(data.success, data.result);
            if (data.success) {
              this.getRecordList();
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
          jobId: this.crtJobId
        };
        this.$axios
          .delete('/api/quartz/log/clean.do', { params })
          .then(({ data }) => {
            this.$utils.setMessage(data.success, data.result);
            if (data.success) {
              this.getRecordList();
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
      this.getRecordList();
    },
    /**
     * @description 分页切换
     * @param size
     */
    changePageSize(size) {
      this.pageConfig.pageNum = 1;
      this.pageConfig.pageSize = size;
      this.getRecordList();
    },
    /**
     * @description 关闭弹窗
     */
    saveConfig() {
      this.$emit('closeDialog');
    }
  }
};
</script>
<style lang="scss" scoped>
.recordList {
  width: 100%;
  height: calc(100% - 40px);
  .tableDiv {
    width: 100%;
    height: calc(100% - 40px);
  }
  .pageDiv {
    width: 100%;
    height: 40px;
    display: flex;
    align-items: center;
  }
}
</style>
