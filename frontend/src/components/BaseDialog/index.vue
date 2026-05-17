<template>
  <div class="baseDialog">
    <el-dialog
      :visible="dialogConfig.visible"
      :width="dialogConfig.width"
      :fullscreen="dialogConfig.fullscreen"
      :show-close="false"
      :top="dialogConfig.top || top"
      :custom-class="dialogConfig.fullscreen?'':customClass"
    >
      <div class="header" slot="title">
        <p class="title">
          <i v-if="dialogConfig.icon" :class="`el-icon-${dialogConfig.icon}`"></i>
          <span>{{dialogConfig.title}}</span>
        </p>
        <p class="close" @click="closeDialog">
          <i class="el-icon-circle-close"></i>
        </p>
      </div>
      <slot></slot>
      <slot name="footer" slot="footer">
        <el-button v-if="showFooter" size="mini" type="primary" icon="el-icon-check" @click="save">确定</el-button>
      </slot>
    </el-dialog>
  </div>
</template>
<script lang="ts">
/**
 * Create by Leo
 * 基础组件
 * 基础弹框组件
 * 用于页面弹框的控制
 * 使用此组件可以统一设置界面弹框样式大小等
 * 并减少一定的代码量
 * 统一设置弹框的配置项、确认按钮、取消按钮、关闭按钮等信息
 */

import Vue from 'vue';
export default Vue.extend({
  name: 'baseDialog',
  props: {
    /**
     * 弹框配置信息
     */
    dialogConfig: {
      type: Object,
      default: {
        title: '',
        visible: true,
        width: '50%',
        fullscreen: false
      }
    },
    // 是否展示弹框底部区域，默认展示
    showFooter: {
      type: Boolean,
      default() {
        return true;
      }
    }
  },
  data() {
    return {
      // 通用样式类名
      customClass: 'maxHeight',
      // margtinTop值
      top: '80px'
    };
  },
  methods: {
    /**
     * 提交关闭弹框事件
     */
    closeDialog() {
      this.$emit('closeDialog');
    },
    /**
     * 提交保存事件
     */
    save() {
      this.$emit('save');
    }
  }
});
</script>
<style lang="scss" scoped>
@import '@/styles/theme.scss';
.baseDialog {
  width: auto;
  height: auto;
  /deep/ .el-dialog {
    // background: #1f2b1f;
    display: flex;
    flex-direction: column;
    .header {
      display: flex;
      justify-content: space-between;
      color: #fff;
      font-size: 16px;
      background: url('~@/assets/mainPage/dialog_header_title_bg.png') left no-repeat;
      height: 100%;
      line-height: $dialogHeaderHei;
      color: #366a94;
      .title {
        span {
          margin-left: 45px;
          font-size: 16px;
          font-weight: bold;
        }
      }
      .close {
        cursor: pointer;
        line-height: $dialogHeaderHei;
        color: #366a94;
        margin-right: 15px;
      }
    }
    .el-dialog__body {
      flex: 1;
      overflow: auto;
      height: calc(100% - 42px);
    }
  }
  .maxHeight {
    margin: 80px auto;
    max-height: calc(100% - 160px);
  }
}
</style>
