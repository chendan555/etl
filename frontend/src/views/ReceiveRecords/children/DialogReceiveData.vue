<template>
  <base-dialog v-bind="$attrs" v-on="$listeners" @save="saveTableConfig">
    <div class="tablediv">
      <base-table
        :tableColumnConfig="tableConfigProp.tableColumnConfig"
        :tableData="tableConfigProp.tableData"
        :isShowOperation="isShowOperation"
        :isShowCheckBox="isShowCheckBox"
      >
        <div slot="operation" slot-scope="{row}">
          <!-- <el-button type="text" @click="createJsonData(row)" style="color:#fefa8e">生成复盘数据</el-button> -->
          <el-tooltip class="item" effect="dark" content="生成复盘数据" placement="top">
            <el-button
              type="text"
              @click="createJsonData(row)"
              style="color:#fefa8e"
              icon="el-icon-data-line"
            ></el-button>
          </el-tooltip>
        </div>
      </base-table>
    </div>
    <div class="pagediv">
      <BasePagination v-if="page.total" :data="page" @current="currentPage" @sizes="pageSizes"></BasePagination>
    </div>
  </base-dialog>
</template>
<script >
// import Vue from 'vue';
import BaseDialog from '@/components/BaseDialog/index.vue';
import BaseTable from '@/components/BaseTable/index.vue';
import BasePagination from '@/components/BasePagination/index.vue';
// import { param } from '@/utils';
export default {
  name: 'dialogReceiveData',
  components: { BaseDialog, BaseTable, BasePagination },
  props: ['tableConfigProp'],
  data() {
    return {
      showTable: false,
      isShowCheckBox: false,
      isShowOperation: false,
      rowDate: [],
      page: {
        pageSize: 20,
        pageNum: 1,
        sizes: [10, 20, 30, 40, 50, 100],
        total: 0
      } //页码的配置
    };
  },
  mounted() {
    this.getTatol();
    console.log(this.tableConfigProp);
    if (this.tableConfigProp.row.receiveType === '2') {
      // this.isShowCheckBox = true;
      this.isShowOperation = true;
    } else {
      // this.isShowCheckBox = false;
      this.isShowOperation = false;
    }
  },
  methods: {
    saveTableConfig() {
      this.$emit('closeDialog');
    },
    createJsonData({ row }) {
      this.$emit('showCreateJsonData', row);
    },
    currentPage(e) {
      let param = {
        row: this.tableConfigProp.row,
        pageNum: e
      };
      this.$emit('pagenum', param);
    },
    pageSizes(e) {
      let param = {
        row: this.tableConfigProp.row,
        pageSize: e
      };
      this.$emit('pagesize', param);
    },
    // // 分页的数量
    getTatol() {
      console.log(this.tableConfigProp);
      this.page.total = this.tableConfigProp.size;
    },
    //查看
    handleView(row) {
      this.showTable = true;
    }
  }
};
</script>
<style lang="scss" scoped>
.tablediv {
  width: 100%;
  height: calc(100% - 40px);
}
.pagediv {
  width: 100%;
  height: 40px;
}
</style>
