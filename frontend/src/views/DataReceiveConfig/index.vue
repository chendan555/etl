<template>
  <div class="tableRelation">
    <el-tabs v-model="activeDataTable" @tab-click="wjClick" class="wjClass">
      <el-tab-pane label="数据表引接配置" name="dataTable">
        <database></database>
      </el-tab-pane>
      <el-tab-pane label="数据表关系" name="tableRelation">
        <table-relation ref="tableRelation" :recordParam="recordParam"></table-relation>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script >
// import Vue from 'vue';
import database from './database/index.vue';
import resolve from '../ResolveProtocol/index.vue';
import FormModel from '../ResolveFormModel/index.vue';
import TableRelation from '@/views/TableRelation/index';
export default {
  name: 'DataReceiveConfig',
  components: { database, resolve, TableRelation, FormModel },
  meta: {
    pageIndex: 10,
    title: '数据引接配置',
    pageType: 'mainNav',
    belongTo: 'receive',
    hidden: true
  },
  data() {
    return {
      recordParam: 0, //解析协议传递的参数
      activeName: 'database',
      activeFile: 'file',
      activeDataTable: 'dataTable',
      activeEmulatedata: 'emulatedata',
      activeFormword: 'formword'
    };
  },
  methods: {
    // 大的tab
    handClick() {
      if (this.activeName == 'wj') {
        this.activeFile = 'file';
        this.recordParam = 2;
      }
      // else if (this.activeName == 'fz') {
      //   this.activeEmulatedata = 'emulatedata';
      //   this.recordParam = 3;
      // }
      else {
        this.activeFormword = 'formword';
        this.recordParam = 4;
      }
    },
    // 文件
    wjClick() {
      if (this.activeFile == 'resolve') {
        this.$refs.wjResolve.getTableRelationList();
      }
    }
    //仿真
    // fzClick() {
    //   if (this.activeEmulatedata == 'resolve') {
    //     this.$refs.fzResolve.getTableRelationList();
    //   }
    // },
    // 表单
    // bdClick() {
    //   if (this.activeFormword == 'resolve') {
    //     this.$refs.bdResolve.getTableRelationList();
    //   }
    // }
  }
};
</script>
<style lang="scss" scoped>
@import '@/styles/theme.scss';
.tableRelation {
  width: 100%;
  height: 100%;
  background: $contBg;
  border-radius: 6px;
  padding: 5px;
  // /deep/.wjClass .el-tabs__nav-wrap {
  //   border-bottom: none;
  // }
  /deep/ .el-tabs {
    width: 100%;
    height: 100%;
    &__header {
      margin-bottom: 0;
    }

    &__content {
      height: calc(100% - 40px);
      .el-tab-pane {
        height: 100%;
      }
    }
  }
  .wjClass {
    // /deep/ .el-tabs__item {
    //   color: #555;
    // }
    // padding-top: 10px;
    // /deep/ .el-tabs__nav-wrap {
    //   background-color: $conterBg !important;
    // }
    // /deep/ .el-tabs__item.is-active {
    //   background-color: $conterBg !important;
    //   color: #7db5ef !important;
    // }
  }
}
</style>
