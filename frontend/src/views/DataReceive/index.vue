<template>
  <div class="data-center">
    <div class="Tabdiv">
      <el-tabs v-model="activeTab" @tab-click="tabClickEvent">
        <el-tab-pane label="数据表引接" name="1">
          <sjkTable ref="sjkActive" :activeTab="activeTab"></sjkTable>
        </el-tab-pane>
        <el-tab-pane label="文件引接" name="2">
          <wjTable ref="wjActive" :activeTab="activeTab"></wjTable>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>
<script>
import sjkTable from './children/sjkTable';
import wjTable from './children/wjTable';
// import ElementUI from 'element-ui';
import { pcOptions } from './config';
// import API from '@/api/index';
export default {
  name: 'DataReceive',
  meta: {
    pageIndex: 20,
    title: '批量数据引接',
    pageType: 'mainNav',
    belongTo: 'receive',
    hidden: true
  },
  components: { wjTable, sjkTable },
  data() {
    return {
      pcOptions: pcOptions,
      filterForm: {
        xzBatch: ''
      },
      activeTab: '1' //tab的默认选择，
    };
  },
  methods: {
    // tab的切换
    tabClickEvent() {
      if (this.activeTab == 1) {
        this.$refs.sjkActive.getList();
      } else if (this.activeTab == 2) {
        this.$refs.wjActive.getList();
      } else if (this.activeTab == 3) {
        this.$refs.fzActive.getList();
      } else {
        this.$refs.formActive.getList();
      }
    }
  },
  mounted() {
    this.tabClickEvent();
  }
};
</script>

<style lang="scss" scoped>
@import '@/styles/theme.scss';
.data-center {
  width: 100%;
  height: 100%;
  background: $contBg;
  border-radius: 6px;
  padding: 5px;
  .filterdiv {
    height: 4%;
    display: flex;
    align-items: center;
    font-size: 14x;
    padding: 0 10px;
    > p {
      margin: 0 5px;
    }
  }
  .Tabdiv {
    width: 100%;
    height: 100%;
    // padding-top: 5px;
    /deep/ .el-tabs {
      width: 100%;
      height: 100%;
      &__header {
        margin: 0;
      }
      &__content {
        height: calc(100% - 40px);
        .el-tab-pane {
          height: 100%;
        }
      }
    }
  }
  /deep/ .el-input-number__decrease,
  /deep/ .el-input-number__increase {
    background: $defaultBtnBgClr;
    color: #fff;
    border: none;
  }
}
</style>
