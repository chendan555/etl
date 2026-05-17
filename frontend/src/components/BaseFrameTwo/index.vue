<template>
  <div class="baseFrame">
    <div class="leftPart sidePart" v-if="showLeftPart">
      <slot name="leftPart"></slot>
    </div>
    <div class="centerPart">
      <slot name="centerPart"></slot>
    </div>
  </div>
</template>
<script>
/**
 *
 * Create by Leo
 * 基础组件
 * 基础页面布局组件
 * 左右两栏式布局
 * 坐侧区域可控制显隐
 * 并减少一定的代码量
 * 统一设置弹框的配置项、确认按钮、取消按钮、关闭按钮等信息
 *
 */

export default {
  name: 'baseFrame',
  props: {
    /**
     * 
     * 配置信息
     * 
     */
    showLeftPart: {
      default: true,
      type: Boolean
    },
    /**
     * 
     * 配置信息
     * 
     */
    showRightPart: {
      default: true,
      type: Boolean
    }
  }
};
</script>
<style lang="scss" scoped>
@import '@/styles/theme.scss';
.baseFrame {
  width: 100%;
  height: 100%;
  display: flex;
  .sidePart {
    // height: 100%;
  }
  .leftPart {
    // border-right: 1px solid $currentRow;
    // margin: 10px 10px;
    width: 280px;
    min-width: 280px;
    max-width: 280px;
  }
  .centerPart {
    flex: 1;
    width: calc(100% - 280px);
    margin-left: 5px;
  }
}
</style>
 