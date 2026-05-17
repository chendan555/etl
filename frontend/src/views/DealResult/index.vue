<template>
  <div class="tableList">
    <div class="formDataBox">
      <div class="formData">
        <el-form ref="form" :model="filterForm" label-width="6rem" size="mini" :inline="true">
          <el-form-item label="输出关键字">
            <el-input v-model="filterForm.keyword" placeholder="请输入检索关键字"></el-input>
          </el-form-item>
          <!-- <el-form-item label="处理时间">
            <el-input v-model="filterForm.opDateString" placeholder="请输入检索关键字"></el-input>
          </el-form-item>-->
          <el-form-item>
            <el-button type="primary" @click="getListdate">查询</el-button>
            <el-button type="info" @click="cancelSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
    <div class="tableBox">
      <base-table
        :tableColumnConfig="tableColumnConfig"
        :tableData="tableData"
        :isShowOperation="true"
        :operationWidth="'100'"
      >
        <div class="operation" slot="operation" slot-scope="{row}">
          <div v-if="row.row.outputType===3">{{row.row.excelPath}}</div>
          <div v-if="row.row.outputType===2">
            <el-button type="text" @click="ylTable(row)">预览</el-button>
          </div>
          <div v-if="row.row.outputType===1">
            <el-button type="text" @click="download(row)">下载</el-button>
          </div>
          <div>
            <el-button class="delete" type="text" @click="removeDealRecord(row)">删除</el-button>
          </div>
        </div>
      </base-table>
    </div>
    <div class="pageSize">
      <BasePagination :data="page" @current="currentPage" @sizes="pageSizes"></BasePagination>
    </div>
    <!-- 弹窗 -->
    <ylTable v-if="showTable" ref="add" @close="showTable=false" :rowDate="rowDate"></ylTable>
  </div>
</template>

<script>
import BaseTable from '@/components/BaseTable/index.vue';
import BasePagination from '@/components/BasePagination';
import { cljgTableConfig } from './config';
import ylTable from './ylTable';
// import http from '@/api/base/http';
// import { Header } from 'element-ui';
export default {
  name: 'DealResult',
  meta: {
    pageIndex: 70,
    title: '数据处理结果',
    pageType: 'mainNav',
    belongTo: 'process'
  },
  components: { BaseTable, BasePagination, ylTable },
  data() {
    return {
      Tableheight: document.body.clientHeight - 200,
      rowDate: {},
      filterForm: {}, //form表单的数据
      tableData: [], //表格的数据
      tableColumnConfig: cljgTableConfig.tableColumnConfig, //表格的头部
      page: {
        pageSize: 20,
        pageNum: 1,
        sizes: [10, 20, 30, 40, 50, 100],
        total: 0
      }, //页码的配置
      showTable: false //弹窗的展开和关闭
    };
  },
  methods: {
    // 重置
    cancelSearch() {
      this.page.pageNum = 1;
      this.filterForm.keyword = '';
      this.getList();
    },
    // 预览的表格
    ylTable(row) {
      this.showTable = true;
      this.rowDate = row.row;
    },
    // 查询
    getListdate() {
      this.page.pageNum = 1;
      this.getList();
    },
    // 文件下载
    download({ row }) {
      let url = encodeURI(row.excelPath);
      let href = `api/file/download.do?url=${url}`; // 设置下载路径
      this.$utils.downloadFile(href, row.excelPath);
    },
    removeDealRecord({ row }) {
      console.log(row);
      this.$utils.confirm().then(() => {
        const loading = this.$utils.setLoading('删除中...');
        this.$API
          .removeDealRecord({ nodeId: row.nodeId, transId: row.transId, fireId: row.batchNum })
          .then(({ data }) => {
            this.$utils.setMessage(data.success, data.result);
            if (data.success) {
              this.getList();
            }
          })
          .finally(() => {
            loading.close();
          });
      });
    },
    //查询表格的数据
    getList(param) {
      const loading = this.$utils.setLoading('查询中...');
      let data = {
        ...this.filterForm,
        // type: 1,
        pageIndex: this.page.pageNum,
        pageSize: this.page.pageSize
      };
      this.$API
        .DealResult(data)
        .then(({ data }) => {
          if (data.code === 200) {
            this.page.total = data.data.total;
            this.tableData = data.data.records;
            // this.tableData.forEach(item => {
            //   item.runBatch = 'A流程_20211115_第1次执行';
            // });
          }
        })
        .catch((this.tableData = []))
        .finally(loading.close());
    },
    currentPage(e) {
      this.page.pageNum = e;
      this.getList();
    },
    pageSizes(e) {
      this.page.pageSize = e;
      this.getList();
    }
  },
  mounted() {
    this.getList();
  }
};
</script>

<style lang="scss" scoped>
@import './src/styles/_mixin.scss';
@import './src/styles/theme.scss';
.tableList {
  background: $contBg;
  border-radius: 6px;
  padding: 5px;
  @include margin(0 10px);
  @include h(100%);
  .formDataBox {
    @include w(100%);
    // @include bc($centerTtlBgClr);
    @include h(40px);
    .formData {
      @include flex(column, flex-end);
      @include float(right);
      @include padding(5px 0);
      @include marginBottom(5px);
      .el-form-item--mini.el-form-item,
      .el-form-item--small.el-form-item {
        @include marginBottom(0px);
      }
    }
  }
  .tableBox {
    height: calc(100% - 80px);
  }
  .pageSize {
    @include w(100%);
    @include h(40px);
    @include paddingRight(7px);
    @include paddingTop(7px);
    // position: fixed;
    // bottom: 5px;
  }
}
.operation {
  display: flex;
  justify-content: space-around;
  &:hover {
    /deep/ .el-button {
      color: #fff;
    }
    .delete {
      color: #e96a54;
    }
  }
  .delete {
    color: #e96a54;
  }
}
</style>

