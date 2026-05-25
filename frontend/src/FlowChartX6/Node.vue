<template>
  <el-tooltip :disabled="tooltipDisabled" placement="right">
    <div slot="content" class="fy_tooltip_content">节点信息：{{ text }}</div>
    <div
      ref="nodediv"
      class="x6_node"
      @mousedown="tooltipDisabled = true"
      @mouseup="tooltipDisabled = false"
      @click.stop="onNodeClick"
    >
      <span class="blue_line"></span>
      <div class="content">
        <div class="left">
          <SvgIcon :iconClass="svgName" style="fontSize: 18px;"></SvgIcon>
          <span class="text">{{ text }}</span>
        </div>
        <i v-if="nodeState === 'loading'" class="el-icon-loading node-state"></i>
        <i v-else-if="nodeState === 'success'" class="el-icon-circle-check node-state"></i>
        <i v-else-if="nodeState === 'failed'" class="el-icon-circle-close node-state"></i>
        <i v-else-if="nodeState === 'warning'" class="el-icon-warning-outline node-state"></i>
        <i v-else-if="icon && icon.includes('TOP')" class="el-icon-coin node-state"></i>
        <i v-else></i>
      </div>
    </div>
  </el-tooltip>
</template>

<script>
import SvgIcon from '@/components/SvgIcon/index';

export default {
  name: 'X6Node',
  components: { SvgIcon },
  inject: ['getNode', 'getGraph'],
  data() {
    return {
      tooltipDisabled: false,
    };
  },
  computed: {
    node() {
      return this.getNode();
    },
    graph() {
      return this.getGraph();
    },
    data() {
      return this.node.getData() || {};
    },
    icon() {
      return this.data.icon || '';
    },
    text() {
      return this.data.text || '';
    },
    nodeState() {
      return this.data.nodeState || '';
    },
    svgName() {
      if (this.icon) {
        const startStr = 'ui/images/';
        const endStr = '.svg?';
        const startIndex = this.icon.indexOf(startStr);
        const endIndex = this.icon.indexOf(endStr);
        if (startIndex > -1 && endIndex > -1) {
          return this.icon.slice(startIndex + startStr.length, endIndex);
        }
      }
      return 'PTHJB';
    },
  },
  methods: {
    onNodeClick() {
      // 通过 node.setData 触发 X6 的数据变更通知
      // 选中状态由 graph.select(node) 在外部处理
    },
  },
};
</script>

<style lang="scss" scoped>
.x6_node {
  position: relative;
  width: 100%;
  height: 100%;
  border: 1px solid #107eef;
  border-radius: 4px;
  font-size: 14px;
  cursor: move;
  box-sizing: border-box;
  overflow: hidden;
  background: #fff;
}
.x6_node .blue_line {
  position: absolute;
  top: 2px;
  left: 3px;
  border-radius: 2px;
  padding: 14px 2px;
  background: #1c9bec;
}
.x6_node .content {
  display: flex;
  align-items: center;
  padding-left: 10px;
  justify-content: space-between;
  height: 100%;
}
.x6_node .content .left {
  display: flex;
  align-items: center;
}
.x6_node .content .text {
  margin-left: 5px;
  color: #000;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.x6_node .content i {
  position: relative;
  top: -10px;
  color: rgb(0, 205, 234);
  margin-left: 15px;
  margin-right: 15px;
}
.node-state {
  top: -8px !important;
  font-size: 20px;
  margin-left: 8px !important;
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
</style>
