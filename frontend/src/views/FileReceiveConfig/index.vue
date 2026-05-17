<template>
  <div class="tableRelation">
    <el-tabs v-model="activeFile" @tab-click="wjClick" class="wjClass">
      <el-tab-pane label="文件引接配置" name="file">
        <file></file>
      </el-tab-pane>
      <el-tab-pane label="解析协议" name="resolve">
        <resolve ref="wjResolve" :recordParam="recordParam"></resolve>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script >
// import Vue from 'vue';
import file from './file/index.vue';
import resolve from '../ResolveProtocol/index.vue';
export default {
  name: 'FileReceiveConfig',
  components: { file, resolve },
  meta: {
    pageIndex: 10,
    title: '文件引接配置',
    pageType: 'mainNav',
    belongTo: 'receive'
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
  //   // border-bottom: none;
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
}
</style>
