<template>
  <el-tooltip :disabled="tooltipDisabled" placement="right">
    <div slot="content" class="fy_tooltip_content">节点信息：{{ text }}</div>
    <div
      ref="nodediv"
      @mousedown="tooltipDisabled = true"
      @mouseup="tooltipDisabled = false"
      @click="setCrtNode"
      @keyup.delete="deleteNode"
      @keydown="handleNode"
    >
      <span class="blue_line"></span>
      <div class="content">
        <div class="left">
          <SvgIcon :iconClass="icon | getSvgName" style="fontSize: 18px;"></SvgIcon>
          <!-- <img class="icon" :src="icon" alt />  -->
          <span class="text">{{ text }}</span>
        </div>
        <i v-if="state === 'loading'" class="el-icon-loading node-state"></i>
        <i v-else-if="state === 'success'" class="el-icon-circle-check node-state"></i>
        <i v-else-if="state === 'failed'" class="el-icon-circle-close node-state"></i>
        <i v-else-if="state === 'warning'" class="el-icon-warning-outline node-state"></i>
        <i v-else-if="icon && icon.includes('TOP')" class="el-icon-coin node-state"></i>
        <i v-else></i>
      </div>
    </div>
  </el-tooltip>
</template>
<script>
/**
 *
 * 画布节点文件
 * 定义画布中节点信息
 * 包括样式、功能等
 *
 */
import SvgIcon from '@/components/SvgIcon/index';
import store from '@/store';
import exec, { RemoveNodeCommand } from './Command';
import editor from '@/FlowChart/editor.js';
export default {
  data() {
    return {
      /**
       * 提示框
       */
      tooltipDisabled: false,
      /**
       * 状态
       */
      state: '',
      /**
       * 复制节点
       */
      copyNodeData: {}
    };
  },
  components: { SvgIcon },
  props: ['nodeState', 'icon', 'text'],
  methods: {
    /**
     * 节点控制
     */
    handleNode(ev) {
      ev.stopPropagation();
      this.copyNodeData = store.state.nodeData;
      if ([37, 38, 39, 40].includes(ev.keyCode) && this.copyNodeData.hasOwnProperty('position')) {
        let left = this.copyNodeData.position.left;
        let top = this.copyNodeData.position.top;
        /**
         * 功能分发
         */
        switch (ev.keyCode) {
          case 37:
            left -= 1;
            break;
          case 38:
            top -= 1;
            break;
          case 39:
            left += 1;
            break;
          case 40:
            top += 1;
            break;
          default:
            return;
        }
        editor.changeNodePosition(this.copyNodeData.id, {
          left,
          top
        });
      } else if (ev.keyCode === 67 && ev.ctrlKey) {
        this.$eventBus.$emit('copyNodeData', this.copyNodeData);
      }
    },
    /**
     * 设置当前节点
     */
    setCrtNode() {
      this.$refs.nodediv.setAttribute('tabindex', '0');
      this.$refs.nodediv.focus();
    },
    /**
     * 删除节点
     */
    deleteNode() {
      let nodeId = store.state.nodeId;
      exec(RemoveNodeCommand, nodeId);
    }
  },
  filters: {
    /**
     * 设置节点图标
     */
    getSvgName(icon) {
      if (icon) {
        let startStr = 'ui/images/';
        let endStr = '.svg?';
        let startIndex = icon.indexOf(startStr);
        let endIndex = icon.indexOf(endStr);
        return icon.slice(startIndex + startStr.length, endIndex);
      } else {
        return 'PTHJB';
      }
    }
  }
};
</script>
<style lang="scss" scoped>
.content {
  display: flex;
  align-items: center;
  padding-left: 10px;
  justify-content: space-between;
  .left {
    display: flex;
    align-items: center;
  }
  .text {
    margin-left: 5px;
    color: #fff;
  }
  i {
    position: static;
    margin-right: 5px !important;
  }
}
.el-icon-loading {
  color: #909399 !important;
}
.el-icon-circle-check {
  color: #67c23a !important;
}
.el-icon-circle-close {
  color: #f56c6c !important;
}
.el-icon-warning-outline {
  color: #e6a23c !important;
}
.el-icon-coin {
  color: #3d9e0c !important;
}
.icon {
  margin-left: 10px;
}
</style>
