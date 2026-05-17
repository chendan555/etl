<template>
  <div class="tree-handle">
    <div v-show="_props.filterVisile" class="filter-bar">
      <el-input v-model="filter" size="mini" placeholder="请输入关键字检索"></el-input>
    </div>
    <!-- 树形控件 -->
    <el-tree ref="tree" v-bind="_attrs" v-on="$listeners">
      <span class="custom-tree-node" slot-scope="{ node, data }">
        <span>{{ node.label }}</span>
        <span>
          <i
            @click.stop="$emit('insert-node', data)"
            class="el-icon-circle-plus-outline"
            style="color: #319B85"
          ></i>
          <i
            @click.stop="$emit('edit-node', data)"
            class="el-icon-edit-outline"
            style="color: #0099CC"
          ></i>
          <i @click.stop="$emit('delete-node', data)" class="el-icon-delete" style="color: #FF9900"></i>
        </span>
      </span>
    </el-tree>
  </div>
</template>
<script>
/**
 *
 * Create by Leo
 * 基础组件
 * 基础树形结果组件
 * 左右两栏式布局
 * 坐侧区域可控制显隐
 * 并减少一定的代码量
 * 统一设置弹框的配置项、确认按钮、取消按钮、关闭按钮等信息
 *
 */
export default {
  inheritAttrs: false,
  name: 'tree-handle',
  props: {
    /**
     * 配置项
     */
    filterVisile: {
      type: Boolean,
      default: false
    }
  },
  computed: {
    /**
     * 配置项
     */
    _attrs() {
      const defaultAttrs = {
        'default-expand-all': true,
        'node-key': 'id',
        'highlight-current': true,
        'filter-node-method': this.filterNode
      };
      return { ...defaultAttrs, ...this.$attrs };
    }
  },
  watch: {
    /**
     * 配置项
     */
    filter(val) {
      this.$refs.tree.filter(val);
    }
  },
  mounted() {},
  data() {
    return {
      /**
       * 配置项
       */
      filter: ''
    };
  },
  methods: {
    /**
     * 配置项
     */
    filterNode(value, data) {
      if (!value) return true;
      return data.label.indexOf(value) !== -1;
    }
  }
};
</script>
<style lang="scss" scoped>
.tree-handle {
  display: flex;
  flex-direction: column;
  height: 100%;
  /deep/ .el-tree {
    flex-grow: 1;
    height: 2px;
    color: #fff;
    background-color: transparent;
    .custom-tree-node {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: space-between;
      font-size: 14px;
      padding-right: 8px;
      i {
        border: 3px solid transparent;
      }
    }
  }
  /deep/ .el-tree--highlight-current .el-tree-node.is-current > .el-tree-node__content {
    background-color: #1d6641;
  }
  /deep/ .el-tree-node__content {
    background-color: transparent;
  }
  /deep/ .el-tree-node__content:hover {
    background-color: #345740;
  }
  /deep/ .el-tree-node__content:focus {
    background-color: #345740;
  }
}
</style>
