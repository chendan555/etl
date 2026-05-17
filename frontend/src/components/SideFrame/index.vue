<template>
  <div class="sideFrame">
    <div class="headTitle" v-if="headTitle">
      <div class="name">
        <img :src="imgUrl" alt>
        <p>{{headTitle.title}}</p>
      </div>
      <div class="handle">
        <slot name="titleButton"></slot>
      </div>
    </div>
    <!-- 数据中心左边菜单 -->
    <slot name="sideMenu"></slot>

    <div class="content">
      <slot name="sideContent"></slot>
    </div>
  </div>
</template>
<script>
/**
 *
 * Create by Leo
 * 基础组件
 * 基础边框布局组件
 * 左右两栏式布局
 * 坐侧区域可控制显隐
 * 并减少一定的代码量
 * 统一设置弹框的配置项、确认按钮、取消按钮、关闭按钮等信息
 *
 */
export default {
  name: 'sideFrame',
  /**
   * 配置项
   */
  props: ['headTitle'],
  computed: {
    /**
     * 配置项
     */
    imgUrl() {
      return require(`@/assets/mainPage/${this.headTitle.imgUrl}.png`);
    }
  }
};
</script>
<style lang="scss" scoped>
@import './src/styles/theme.scss';
.sideFrame {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  .headTitle {
    width: 100%;
    height: 40px;
    display: flex;
    justify-content: space-between;
    padding: 0 5px;
    background: $navBg;
    color: $whiteWordClr;
    > div {
      display: flex;
      align-items: center;
    }
    .name p {
      font-weight: bold;
      margin-left: 5px;
    }
    .handle {
      cursor: pointer;
    }
  }
  .content {
    flex: 1;
    padding: 5px;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    overflow-y: auto;
  }
}
</style>
