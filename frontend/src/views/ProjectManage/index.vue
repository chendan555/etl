<template>
  <div class="projectManage">
    <BaseFrame>
      <NavPage slot="leftPart" @navListTree="navListTree"></NavPage>
      <div slot="rightPart" class="rightSide">
        <ProjConfig></ProjConfig>
      </div>
      <div class="centerDiv" slot="centerPart">
        <ProcessList :treeData="treeData"></ProcessList>
      </div>
    </BaseFrame>
  </div>
</template>
<script>
import BaseFrame from '@/components/BaseFrame/index.vue';
import NavPage from './children/NavPage';
import ProjConfig from './children/ProjConfig';
import ProcessList from './children/ProcessList';
export default {
  name: 'ProjectManage',
  meta: {
    pageIndex: 50,
    title: '处理流程设计',
    pageType: 'mainNav',
    belongTo: 'process'
  },
  components: { BaseFrame, NavPage, ProjConfig, ProcessList },
  data() {
    return {
      treeData: {} // 树状图方案管理的的数据
    };
  },
  methods: {
    //   选择分组的的数据
    navListTree(data) {
      data.forEach(item => {
        item.children.forEach(x => {
          delete x.children;
        })
      })
      this.treeData = data;
    }
  }
};
</script>
<style lang="scss" scoped>
@import '@/styles/theme.scss';
.projectManage {
  width: 100%;
  height: 100%;
  background: $contBg;
  border-radius: 6px;
  padding: 5px;
  .rightSide {
    width: 100%;
    height: 100%;
  }
  .centerDiv {
    width: 100%;
    height: 100%;
  }
}
</style>
 