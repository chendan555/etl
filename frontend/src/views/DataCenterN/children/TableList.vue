<template>
  <div class="tableList">
    <div class="headerDiv">
      <div class="btndiv">
        <el-button
          icon="el-icon-upload"
          size="mini"
          @click="getUploadFile"
          v-if="crtDataType === fileType.file"
        >上传文件</el-button>
        <!-- <el-button
          icon="el-icon-tickets"
          size="mini"
          @click="showRecords"
          v-if="crtDataType === fileType.file"
        >操作记录</el-button> -->
      </div>
      <div class="filterdiv">
        <el-form
          ref="form"
          :model="filterForm"
          label-width="100px"
          size="mini"
          :inline="true"
          v-if="crtDataType !== fileType.file"
        >
          <el-form-item label="数据表名称" prop="tableName">
            <el-input v-model="filterForm.tableName" placeholder="请输入检索关键字"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="getListdate">查询</el-button>
          </el-form-item>
          <el-form-item>
            <el-button type="info" @click="resetFilter">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
    <div class="listdiv">
      <base-table
        rowKey="id"
        :isShowOperation="true"
        :isShowExpand="crtDataType === fileType.database"
        :tableColumnConfig="tableColumnConfig"
        :tableData="tableData"
        :operationWidth="'80'"
        @expandChange="expandChange"
      >
        <div class="sheettable" slot="expand" slot-scope="{props}">
          <base-table
            :tableColumnConfig="lmTableColumnConfig"
            :tableData="nbData"
            :isShowCheckBox="false"
          ></base-table>
        </div>
        <div class="operation" slot="operation" slot-scope="{row}">
          <el-tooltip
            class="item"
            effect="dark"
            content="查看"
            placement="top"
            v-if="crtDataType === fileType.database"
          >
            <p @click="sjgzAddOrUpdate(row)">
              <!-- <i class="el-icon-view"></i> -->
              查看
            </p>
          </el-tooltip>
          <el-tooltip
            class="item"
            effect="dark"
            content="下载查看文件"
            placement="top"
            v-if="crtDataType === fileType.file"
          >
            <p @click="viewFile(row)">
              <!-- <i class="el-icon-download"></i> -->
              下载
            </p>
          </el-tooltip>
          <!-- <el-button
            size="small"
            v-if="crtDataType === fileType.database"
            @click="sjgzAddOrUpdate(row)"
            style="color:#fefa8e"
          >查看</el-button>
          <el-button
            size="small"
            v-else-if="crtDataType === fileType.file"
            @click="viewFile(row)"
            style="color:#fefa8e"
          >下载查看</el-button>-->
        </div>
      </base-table>
    </div>
    <div class="pageSize">
      <BasePagination
        :data="page"
        @current="currentPage"
        @sizes="pageSizes"
        v-if="crtDataType !== fileType.file"
      ></BasePagination>
    </div>
    <!-- 弹窗 -->
    <TableUpdate v-if="showTable" ref="add" @close="showTable=false" :rowDate="rowDate"></TableUpdate>
    <!-- 上传文件 -->
    <upload-file v-if="showUploadFile" @closeDialog="closeUploadFile" :dataParams="dataParams"></upload-file>
    <!-- 操作记录 -->
    <file-handle-record v-if="isShowRecords" @closeDialog="closeDialog" :dataParams="dataParams"></file-handle-record>
  </div>
</template>

<script>
import BaseTable from '@/components/BaseTable/index.vue';
import BasePagination from '@/components/BasePagination';
import TableUpdate from './TableUpdate';
import UploadFile from './UploadFile';
import FileHandleRecord from './FileHandleRecord';
import config from './config';
import { dataCenterService } from '@/api/index.ts';
export default {
  name: 'tableList',
  components: { BaseTable, BasePagination, TableUpdate, UploadFile, FileHandleRecord },
  data() {
    return {
      showUploadFile: false,
      isShowRecords: false,
      crtDataType: '',
      fileType: config.fileType,
      tableData: [], //表格的数据
      nbData: [], //内部表格
      tableColumnConfig: config.tableConfig.tableColumnConfig, //外部的表格的头部
      lmTableColumnConfig: config.lmTableConfig.tableColumnConfig, //内部表格的头部
      page: {
        pageSize: 20,
        pageNum: 1,
        sizes: [10, 20, 30, 40, 50, 100],
        total: 0
      }, //页码的配置
      showTable: false, //弹窗的展开和关闭
      rowDate: '',
      filterForm: {},
      dataParams: {} //兄弟组件传递过来的数据
    };
  },
  methods: {
    // 选择上传文件
    getUploadFile() {
      this.showUploadFile = true;
    },
    closeDialog(bool) {
      this.isShowRecords = false;
    },
    closeUploadFile(bool) {
      this.showUploadFile = false;
      if (bool) {
        this.getFileDataList(this.dataParams);
      }
    },
    showRecords() {
      this.isShowRecords = true;
    },
    //数据表查看
    sjgzAddOrUpdate(data) {
      this.showTable = true;
      this.rowDate = {
        tableName: data.row.tableName,
        ...this.dataParams
      };
    },
    // 文件下载预览
    viewFile({ row }) {
      /* const params = {
        id: this.dataParams.id,
        fileName: row.fileName,
        directory: row.directory
      };
      dataCenterService.downloadFile(params).then(res => {
        console.log(res);
        if (!res.data) {
          this.$utils.setMessage(false, '下载失败');
        } else {
        }
      }); */
      let href = `api/data/source/fileMessage/download.do?id=${this.dataParams.id}&fileName=${row.fileName}&directory=${row.directory}`;
      this.$utils.downloadFile(href, row.fileName);
    },
    // 查询
    getListdate() {
      this.page.pageNum = 1;
      this.getList();
    },
    // 重置筛选查询
    resetFilter() {
      this.filterForm.tableName = '';
      this.getListdate();
    },
    //查询表格的数据
    getList(param) {
      let data = {
        pageIndex: this.page.pageNum,
        pageSize: this.page.pageSize,
        ...this.dataParams,
        ...this.filterForm,
        ...param
      };
      this.$API
        .getTableDatas(data)
        .then(({ data }) => {
          if (data.code === 200) {
            this.page.total = data.data.total;
            this.tableData = data.data.records;
          }
        })
        .catch(() => { this.tableData = []; });
    },
    //table展开后的数据
    expandChange(row, expandedRows) {
      let params = {
        tableName: row.tableName,
        ...this.dataParams
      };
      this.$API.getColumnsByTable(params).then(({ data }) => {
        this.nbData = data.data || data;
      });
    },

    // 重置
    cancelSearch() {
      this.filterForm.tableName = '';
      this.getList();
    },
    currentPage(e) {
      this.page.pageNum = e;
      this.getList();
    },
    pageSizes(e) {
      this.page.pageSize = e;
      this.getList();
    },
    // 获取文件数据列表
    getFileDataList(params) {
      dataCenterService.getFileDataList(params).then(({ data }) => {
        this.tableData = data.data;
      });
    }
  },
  mounted() {
    this.$eventBus.$on('showDataTableList', param => {
      this.crtDataType = config.fileType.database;
      this.tableColumnConfig = config.tableConfig.tableColumnConfig;
      this.dataParams = param;
      this.page.pageNum = 1;
      this.getList(this.dataParams);
    });
    this.$eventBus.$on('showFileDatalist', param => {
      this.crtDataType = config.fileType.file;
      this.tableColumnConfig = config.fileDataTableConfig.tableColumnConfig;
      this.dataParams = param;
      this.page.pageNum = 1;
      this.getFileDataList(param);
    });
  }
};
</script>

<style lang="scss" scoped>
@import './src/styles/theme.scss';
@import './src/styles/_mixin.scss';
.tableList {
  width: 100%;
  @include h(100%);
  /deep/ .el-table__body-wrapper {
    height: calc(100% - 40px) !important;
    overflow: auto;
  }
  .headerDiv {
    display: flex;
    justify-content: space-between;
    padding: 0 5px;
    .btndiv {
      line-height: 40px;
      // padding-left: 5px;
    }
    @include h(40px);
    // @include bc($centerTtlBgClr);
    .filterdiv {
      @include flex(column, flex-end);
      @include float(right);
      @include padding(5px 0);
      // @include marginBottom(5px);
      .el-form-item--mini.el-form-item,
      .el-form-item--small.el-form-item {
        @include marginBottom(0px);
      }
    }
  }
  .listdiv {
    width: 100%;
    height: calc(100% - 80px);
    padding: 0 5px;
    .operation p {
      color: #409eff;
      cursor: pointer;
    }
  }
  .pageSize {
    height: 40px;
    @include w(100%);
    display: flex;
    justify-content: flex-end;
    align-items: center;
    padding: 0 10px;
    // margin-top: 5px;
  }
}
</style>

