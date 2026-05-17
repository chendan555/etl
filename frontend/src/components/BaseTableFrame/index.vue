<template>
  <div class="baseTableFrame">
    <div class="filterPart" v-if="showFilter" :style="{height: `${filterHeight}px`}">
      <slot name="filter"></slot>
    </div>
    <div
      class="tablePart"
      :style="{height: `calc(100% - ${this.filterHeight + this.filterHeight}px`}"
    >
      <slot name="table"></slot>
    </div>
    <div class="pagePart" v-if="showPage" :style="{height: `${pageHeight}px`}">
      <slot name="page"></slot>
    </div>
  </div>
</template>
<script lang="ts">
/**
 *
 * Create by Leo
 * 基础组件
 * 基础表格布局组件
 * 左右两栏式布局
 * 坐侧区域可控制显隐
 * 并减少一定的代码量
 * 统一设置弹框的配置项、确认按钮、取消按钮、关闭按钮等信息
 *
 */
import Vue from "vue";
export default Vue.extend({
  name: "baseTableFrame",
  props: {
    /**
     * 配置项
     */
    showFilter: {
      type: Boolean,
      default: true
    },
    /**
     * 配置项
     */
    showPage: {
      type: Boolean,
      default: true
    },
    /**
     * 配置项
     */
    filterHeight: {
      type: Number,
      default: 40
    },
    /**
     * 配置项
     */
    pageHeight: {
      type: Number,
      default: 40
    }
  }
});
</script>
<style lang="scss" scoped>
.baseTableFrame {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  // padding: 5px;
  .filterPart,
  .pagePart {
    width: 100%;
    display: flex;
    align-items: center;
  }
  .tablePart {
    flex: 1;
    overflow: hidden;
    overflow-y: auto;
  }
}
</style>
