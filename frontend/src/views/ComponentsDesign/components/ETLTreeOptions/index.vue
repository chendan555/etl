<template>
  <div class="tree-options-config">
    <div class="title">{{title}}</div>
    <el-button size="mini" @click="addTreeNode(null)">增加根节点</el-button>
    <el-tree
      class="select-tree"
      :data="treeData"
      :props="defaultProps"
      ref="selectTree"
      default-expand-all
      @node-click="nodeClick"
    >
      <template slot-scope="{ node, data }">
        <el-col :span="24">
          <el-col class="node-input">
            <el-input v-model="data.label" size="mini"></el-input>
          </el-col>
          <el-col class="node-icon">
            <div class="dynamic-options-col-close" @click.stop="addTreeNode(node)">
              <i class="el-icon-circle-plus-outline"></i>
            </div>
          </el-col>
          <el-col class="node-icon">
            <div class="dynamic-options-col-close" @click.stop="removeTreeNode(node)">
              <i class="el-icon-circle-close"></i>
            </div>
          </el-col>
        </el-col>
      </template>
    </el-tree>
  </div>
</template>
<script>
export default {
  name: 'ETLTreeOptions',
  props: {
    treeData: {
      default: [],
      type: Array
    },
    title: String,
    attrName: String
  },
  data() {
    return {
      defaultProps: {
        children: 'children',
        label: 'label'
      },
      curTreeNode: null
    };
  },
  methods: {
    addTreeNode(parentNode) {
      this.$refs.selectTree.append({ label: '节点', children: [] }, parentNode);
      this.setEleAttr();
    },
    removeTreeNode(node) {
      this.$refs.selectTree.remove(node);
      this.setEleAttr();
    },
    nodeClick(node) {
      this.curTreeNode = node;
    },
    setEleAttr() {
      this.$emit('setEleAttr', this.attrName);
    },
  }
};
</script>
<style lang="scss" scoped>
.tree-options-config {
  border-top: 1px solid #808353;
  padding: 10px 0;
  .title {
    width: 80px;
    font-size: 14px;
    text-align: center;
    margin-bottom: 10px;
  }
  .el-button {
    margin-bottom: 10px;
  }
  .node-input {
    width: calc(100% - 40px);
  }
  .node-icon {
    width: 20px;
  }
  /deep/ .el-tree {
    background: transparent;
  }

  /deep/ .el-tree-node__content {
    height: auto;
    margin-top: 5px;
  }
}
</style>
