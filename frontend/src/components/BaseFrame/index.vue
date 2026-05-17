<template>
  <div class="baseFrame">
    <div class="leftPart sidePart" v-if="showLeftPart">
      <slot name="leftPart"></slot>
    </div>
    <div class="centerPart">
      <slot name="centerPart"></slot>
    </div>
    <div class="rightPart sidePart" v-if="showRightPart">
      <slot name="rightPart"></slot>
    </div>
  </div>
</template>
<script>
/**
 * 
 * Create by Leo
 * 基础组件
 * 基础页面布局组件
 * 左中右三栏式布局
 * 左右两个区域可控制显隐
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
    // border-right: 1px solid  $currentRow;
    // margin: 10px 5px 10px 10px;
    width: 280px;
    min-width: 280px;
    max-width: 280px;
  }
  .rightPart {
    // border-left: 1px solid $currentRow;
    width: 220px;
    min-width: 220px;
    max-width: 220px;
  }
  .centerPart {
    flex: 1;
    width: calc(100% - 560px);
    padding: 0 5px;
  }
}
</style>
 