<template>
  <svg class="icon-svg" aria-hidden="true">
    <use :xlink:href="iconName"></use>
  </svg>
</template>

<script lang="ts">
/**
 *
 * Create by Leo
 * 基础组件
 * 基础图标组件
 * 左右两栏式布局
 * 坐侧区域可控制显隐
 * 并减少一定的代码量
 * 统一设置弹框的配置项、确认按钮、取消按钮、关闭按钮等信息
 *
 */
import Vue from 'vue';
import { Component, Prop } from 'vue-property-decorator';
@Component({
  name: 'SvgIcon'
})
export default class SvgIcon extends Vue {
  /**
   * 配置项
   */
  @Prop()
  iconClass!: string;
  /**
   * 配置项
   */
  get iconName() {
    return `#icon-${this.iconClass}`;
  }
}
</script>

<style lang="scss" scoped>
.icon-svg {
  width: 1em;
  height: 1em;
  vertical-align: -0.15em;
  fill: currentColor;
  overflow: hidden;
}
</style>
