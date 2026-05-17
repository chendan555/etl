<template>
  <el-dialog
    :title="title"
    :visible.sync="visible"
    width="80%"
    @closed="closedCallback"
    append-to-body
    modal-append-to-body
    id="previewDataDialog"
    :show-close="false"
  >
    <div class="content">
      <div class="tableDiv">
        <base-table :tableColumnConfig="dataColumn" :tableData="nbData"></base-table>
      </div>
      <div class="pageSize">
        <BasePagination :data="page" @current="currentPage" @sizes="pageSizes"></BasePagination>
      </div>
    </div>
    <div class="dialog-footer">
      <el-button type="primary" @click="formValidate">确 定</el-button>
      <el-button type="info" @click="closedCallback">取 消</el-button>
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
      let params = {
        ...this.rowDate,
        pageSize: this.page.pageSize,
        pageIndex: this.page.pageNum
      };
      this.$API
        .getTableDataed(params)
        .then(({ data }) => {
          if (data.code == 200) {
            this.dataColumn = data.data.column.map(item => {
              return { label: `${item.comments ? item.comments + '/' : ''}` + item.columnName, prop: item.columnName };
            });
            this.page.total = data.data.page.total;
            this.nbData = data.data.page.records;
          }
        })
        .catch((this.nbData = []));
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
        height: calc(100% - 50px);
        .tableDiv {
          width: 100%;
          height: calc(100% - 32px);
        }
        #list {
          // height: calc(100% - 32px);
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
