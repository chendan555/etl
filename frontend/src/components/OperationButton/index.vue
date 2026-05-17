<template>
  <div class="operationButton">
    <el-tooltip :content="buttonInfo.name" placement="top" effect="dark">
      <el-button
        size="mini"
        type="text"
        :icon="`el-icon-${buttonInfo.icon}`"
        :style="{color: buttonInfo.color}"
        @click="clickFunc(buttonInfo.handle)"
      ></el-button>
    </el-tooltip>
  </div>
</template>
<script lang="ts">
/**
 *
 * Create by Leo
 * 基础组件
 * 基础操作按钮组件
 * 左右两栏式布局
 * 坐侧区域可控制显隐
 * 并减少一定的代码量
 * 统一设置弹框的配置项、确认按钮、取消按钮、关闭按钮等信息
 *
 */
import Vue from 'vue';
export default Vue.extend({
  name: 'operationButton',
  props: {
    /**
     * 按钮信息
     */
    buttonInfo: {
      type: Object,
      default: {}
    }
  },
  methods: {
    /**
     * 按钮事件
     * @param row
     * @param handle
     */
    clickFunc(row: any, handle: string) {
      this.$emit('handleFunc');
    }
  }
});
</script>
