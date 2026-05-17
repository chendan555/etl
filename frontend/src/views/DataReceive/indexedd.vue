<template>
  <div class="dataReceive">
    <div class="batchdiv">
      <p>当前引接批次为第</p>
      <el-input-number v-model="batchNumber" :min="1" size="mini"></el-input-number>
      <p>批次</p>
    </div>
    <div class="content">
      <div class="tabdiv">
        <el-tabs class="flex-tab" v-model="activeTab" @tab-click="getReceiveDataList">
          <el-tab-pane label="数据表引接" name="table"></el-tab-pane>
          <el-tab-pane label="文件引接" name="file"></el-tab-pane>
          <el-tab-pane label="仿真引接" name="emulate"></el-tab-pane>
          <el-tab-pane label="表单引接" name="form"></el-tab-pane>
        </el-tabs>
      </div>
      <div class="tablediv">
        <base-table-frame>
          <div class="btn" slot="filter">
            <el-button size="mini" @click="multiReceiveData()">批量引接</el-button>
          </div>
          <base-table slot="table" v-bind="{...tableConfig}" @selectionChange="selectionChange">
            <div slot="operation" slot-scope="{row}">
              <el-button type="text" @click="receiveData(row)" style="color:#fefa8e">引接</el-button>
            </div>
          </base-table>
          <BasePagination slot="page" :data="pageConfig" @current="currentPage" @sizes="pageSizes"></BasePagination>
        </base-table-frame>
      </div>
    </div>
  </div>
</template>
<script lang="ts">
import Vue from 'vue';
import BaseTableFrame from '@/components/BaseTableFrame/index.vue';
import BaseTable from '@/components/BaseTable/index.vue';
import BasePagination from '@/components/BasePagination/index.vue';
import API from '@/api/index';
// import { tableColumnConfigList } from './config';
export default Vue.extend({
  name: 'DataReceive',
  components: { BaseTableFrame, BaseTable, BasePagination },
  data() {
    return {
      batchNumber: 0,
      activeTab: '1',
      tableConfig: {
        index: true,
        isShowCheckBox: true,
        isShowOperation: true,
        tableData: [],
        tableColumnConfig: <any>[]
      },
      pageConfig: {
        pageSize: 20,
        pageIndex: 1,
        total: 0
      },
      selections: <any>[]
    };
  },
  methods: {
    multiReceiveData() {},
    receiveData(row: any) {},
    getReceiveDataList() {
      this.pageConfig.pageIndex = 1;
      // this.tableConfig.tableColumnConfig = tableColumnConfigList[this.activeTab];
      switch (this.activeTab) {
        case 'table':
          this.getTableDataList();
          break;
        case 'file':
          this.getFileDataList();
          break;
        case 'emulate':
          this.getEmulateataList();
          break;
        case 'form':
          this.getFormDataList();
          break;
      }
    },
    getTableDataList() {
      API.dataReceive.getTableRelationList(this.pageConfig).then(({ data }: any) => {
        this.tableConfig.tableData = data.data.records;
        this.pageConfig.total = data.data.total;
      });
    },
    getFileDataList() {
      API.dataReceive.getFileReceiveList(this.pageConfig).then(({ data }: any) => {
        this.tableConfig.tableData = data.data.records;
        this.pageConfig.total = data.data.total;
      });
    },
    getEmulateataList() {
      API.configRelation.getEmulatedataList(this.pageConfig).then(({ data }: any) => {
        this.tableConfig.tableData = data.data.records;
        this.pageConfig.total = data.data.total;
      });
    },
    getFormDataList() {
      API.dataReceive.getFileReceiveList(this.pageConfig).then(({ data }: any) => {
        this.tableConfig.tableData = data.data.records;
        this.pageConfig.total = data.data.total;
      });
    },
    currentPage(e: number) {
      this.pageConfig.pageIndex = e;
      this.getReceiveDataList();
    },
    pageSizes(e: number) {
      this.pageConfig.pageSize = e;
      this.getReceiveDataList();
    },
    // 多选
    selectionChange(data: any) {
      // this.selections = [...data];
    }
  }
});
</script>
<style lang="scss" scoped>
.dataReceive {
  width: 100%;
  height: 100%;
  .batchdiv {
    width: 100%;
    height: 40px;
    position: relative;
    display: flex;
    align-items: center;
    font-size: 14x;
    padding: 0 10px;
    > p {
      margin: 0 5px;
    }
  }
  .content {
    width: 100%;
    height: calc(100% - 40px);
    .tabdiv {
      width: 100%;
      height: 30px;
    }
    .tablediv {
      width: 100%;
      height: calc(100% - 30px);
    }
  }
}
</style>
