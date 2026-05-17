<template>
  <div class="dataCenter">
    <!-- 
      start
      数据源类型标签区域
    -->
    <!-- <el-tabs v-model="dataCenterType">
      <el-tab-pane
        :label="source.sourceName"
        :name="`source${source.id}`"
        v-for="source in serverSourceList"
        :key="source.id"
      ></el-tab-pane>
    </el-tabs>-->
    <!-- end -->
    <!-- 
      start
      数据源管理内容区域
    -->
    <div class="dataContainer">
      <div class="navdiv">
        <nav-part :sourceId="sourceId" @changeShowType="changeShowType"></nav-part>
      </div>
      <div class="contentdiv">
        <TableList v-if="showType === 'dataTable'"></TableList>
        <EmulateInfor v-if="showType === 'emulate'"></EmulateInfor>
      </div>
    </div>
    <!-- end -->
  </div>
</template>
<script lang="ts">
import Vue from 'vue';
import NavPart from './children/NavPart.vue';
import TableList from './children/TableList.vue';
import EmulateInfor from './children/EmulateInfor.vue';
export default Vue.extend({
  name: 'DataCenterN',
  components: { NavPart, TableList, EmulateInfor },
  meta: {
    pageIndex: 1,
    title: '数据源管理',
    pageType: 'viceNav',
    belongTo: 'receive'
  },
  data() {
    return {
      dataCenterType: 'source1',
      showType: 'dataTable',
      serverSourceList: this.$store.state.serverSourceList
    };
  },
  computed: {
    sourceId(): number {
      return Number(this.dataCenterType.slice(-1));
    }
  },
  mounted() {
    // this.getServerSourceList();
  },
  methods: {
    changeShowType(type: string) {
      this.showType = type;
    }
    // getServerSourceList() {
    //   this.$API.getServerSourceList().then(({ data }: any) => {
    //     this.serverSourceList = data.data;
    //   });
    // }
  }
});
</script>
<style lang="scss" scoped>
@import '@/styles/theme.scss';
.dataCenter {
  width: 100%;
  height: 100%;

  // background-color: $conterBg !important;
  /deep/ .el-tabs {
    height: 40px;
    &__header {
      margin-bottom: 0;
    }
  }
  .dataContainer {
    width: 100%;
    height: 100%;
    display: flex;
    // border-top: 1px solid var(--currentRow, #061730);
    .navdiv {
      width: 280px;
      height: 100%;
      background: #fff;
      border-radius: 6px;

      // background:$conterBg;
    }
    .contentdiv {
      width: calc(100% - 290px);
      height: 100%;
      padding: 0px;
      margin-left: 10px;
      background: #fff;
      border-radius: 6px;
    }
  }
}
</style>
