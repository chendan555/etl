<template>
  <el-dialog
    :title="title"
    :visible.sync="visible"
    width="80%"
    :show-close="false"
    @closed="closedCallback"
    append-to-body
    modal-append-to-body
    id="previewDataDialog"
  >
    <div class="content">
      <div class="list">
        <base-table :tableColumnConfig="dataColumn" :tableData="nbData" height="550"></base-table>
      </div>
      <div class="pageSize">
        <BasePagination :data="page" @current="currentPage" @sizes="pageSizes"></BasePagination>
      </div>
      <div class="dialog-footer">
        <el-button size="mini" type="primary" @click="formValidate">确 定</el-button>
        <el-button size="mini" type="info" @click="closedCallback">取 消</el-button>
      </div>
    </div>
  </el-dialog>
</template>

<script>
import BaseTable from '@/components/BaseTable/index.vue';
import BasePagination from '@/components/BasePagination';
export default {
  name: 'tableUpdate',
  components: { BaseTable, BasePagination },
  data() {
    return {
      title: '数据库查看', //标题
      visible: true, // 弹窗的打开和关闭
      nbData: [],
      dataColumn: [],
      page: {
        pageSize: 20,
        pageNum: 1,
        sizes: [10, 20, 30, 40, 50, 100],
        total: 0
      } //页码的配置
    };
  },
  props: ['rowDate'],
  methods: {
    // 隐藏dialog回调
    closedCallback() {
      this.visible = false;
      this.$emit('close');
    },
    // 修改数据的回现
    dataUpda() {
      let loading = this.$utils.setLoading();
      let params = {
        transId: this.rowDate.transId,
        path: this.rowDate.path,
        nodeId: this.rowDate.nodeId,
        pageSize: this.page.pageSize,
        pageIndex: this.page.pageNum
      };
      this.$API
        .getPlanResultData(params)
        .then(res => {
          if (res.data && res.data.columnsList) {
            this.dataColumn = res.data.columnsList.map(item => {
              return {
                label: item.columnName,
                prop: item.columnName || item.comments
              };
            });
            this.page.total = res.data.total;
            this.nbData = res.data.firstRecords;
          }
        })
        .finally(() => {
          loading.close();
        });
    },
    // 确定
    formValidate() {
      this.closedCallback();
    },
    currentPage(e) {
      this.page.pageNum = e;
      this.dataUpda();
    },
    pageSizes(e) {
      this.page.pageSize = e;
      this.dataUpda();
    }
  },
  mounted() {
    this.dataUpda();
  }
};
</script>
<style lang="scss" scoped>
#previewDataDialog {
  /deep/ .el-dialog {
    margin-top: 10vh !important;
    height: calc(100% - 20vh);
    .el-dialog__body {
      height: calc(100% - 45px);
      overflow: hidden;
      overflow-y: auto;
      .content {
        height: 100%;
        .list {
          height: calc(100% - 80px);
        }
      }
    }
  }
  .dialog-footer {
    margin-top: 10px;
    float: right;
  }
}
</style>
