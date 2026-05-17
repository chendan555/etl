<template>
  <div class="mainPage">
    <div class="middlediv">
      <div class="itemdiv">
        <topOne class="itemEcharts"></topOne>
      </div>
      <div class="itemdiv">
        <topTwo class="itemEcharts"></topTwo>
      </div>
    <!-- </div>
    <div class="bottomdiv"> -->
      <div class="itemdiv">
        <buttomOne></buttomOne>
      </div>
      <div class="itemdiv">
        <buttomTwo></buttomTwo>
      </div>
    </div>
  </div>
</template>
<script>
import topOne from './children/topOne';
import topTwo from './children/topTwo';
import buttomOne from './children/buttomOne';
import buttomTwo from './children/buttomTwo';
export default {
  name: 'MainReceive',
  /* meta: {
    pageIndex: 0,
    title: '首页',
    pageType: 'viceNav',
    belongTo: 'receive'
  }, */
  components: { topOne, topTwo, buttomOne, buttomTwo },
  data() {
    return {
      isShow: true
    };
  }
  /* methods: {},
  created() {
    sessionStorage.setItem('rootPath', 'receive');
  } */
};
</script>
<style lang="scss" scoped>
@import '@/styles/theme.scss';
@mixin common() {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  padding: 10px;
  align-content: space-between;
  .itemdiv {
    background: var(--mainPageItemBgClr, #132f20);
    width: calc(50% - 5px);
    height: calc(50% - 5px);
  }
}
.mainPage {
  height: 100%;
  width: 100%;
  text-align: center;
  overflow: hidden;
  img {
    margin-top: 100px;
  }
  .middlediv {
    @include common;
  }
  .bottomdiv {
    @include common;
  }
}
</style>
