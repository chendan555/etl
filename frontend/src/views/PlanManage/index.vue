<template>
  <div class="projectManage">
    <BaseFrameTwo>
      <NavPage slot="leftPart" @navListTree="navListTree"></NavPage>
      <!-- <div slot="rightPart" class="rightSide">
                <ProjConfig></ProjConfig>
      </div>-->
      <div class="centerDiv" slot="centerPart">
        <PlanList :treeData="treeData"></PlanList>
      </div>
    </BaseFrameTwo>
  </div>
</template>
<script>
import BaseFrameTwo from '@/components/BaseFrameTwo/index.vue';
import NavPage from './children/NavPage';
import ProjConfig from './children/ProjConfig';
import PlanList from './children/PlanList';
export default {
  name: 'PlanManage',
  meta: {
    pageIndex: 60,
    title: '处理方案管理',
    pageType: 'mainNav',
    belongTo: 'process',
    hidden: false
  },
  components: {
    BaseFrameTwo,
    NavPage,
    ProjConfig,
    PlanList
  },
  data() {
    return {
      treeData: {} // 树状图方案管理的的数据
    };
  },
  methods: {
    //   树状图方案管理的的数据
    navListTree(data) {
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
 