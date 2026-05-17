<!-- 数据表下钻详情的弹窗 -->
<template>
  <el-dialog
    :title="title"
    :visible.sync="visible"
    width="80%"
    @closed="closedCallback"
    append-to-body
    modal-append-to-body
    id="previewDataDialog"
  >
    <base-table
      :tableColumnConfig="tableConfig.tableColumnConfig"
      :tableData="tableConfig.tableData"
      :isShowOperation="tableConfig.isShowOperation"
    ></base-table>
    <div class="pageSize">
      <BasePagination :data="page" @current="currentPage" @sizes="pageSizes"></BasePagination>
      <div class="dialog-footer">
        <el-button @click="closedCallback">取 消</el-button>
        <el-button type="primary" @click="formValidate">确 定</el-button>
      </div>
    </div>
  </el-dialog>
</template>
<script >
// import Vue from 'vue';
import BaseTable from '@/components/BaseTable/index.vue';
import BasePagination from '@/components/BasePagination';
export default {
  name: 'sjkTable',
  components: { BaseTable, BasePagination },
  props: ['tableConfigProp'],
  data() {
    return {
      tableConfig: {
        isShowOperation: true,
        tableColumnConfig: [],
        tableData: []
      },
      title: '数据表下钻详情', //标题
      visible: true, // 弹窗的打开和关闭
      page: {
        pageSize: 20,
        pageNum: 1,
        sizes: [10, 20, 30, 40, 50, 100],
        total: 0
      } //页码的配置
    };
  },
  methods: {
    // 隐藏dialog回调
    closedCallback() {
      this.visible = false;
      this.$emit('close');
    },
    // 确定
    formValidate() {
      this.closedCallback();
    },
    currentPage(e) {
      this.page.pageNum = e;
      //   this.dataUpda();
    },
    pageSizes(e) {
      this.page.pageSize = e;
      //   this.dataUpda();
    }
  }
};
</script>
<style lang="scss" scoped>
</style>
