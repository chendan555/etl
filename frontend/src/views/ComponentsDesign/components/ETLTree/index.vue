<template>
  <el-tree class="etl-tree" :data="treeData" :props="defaultProps" @node-click="handleNodeClick"></el-tree>
</template>
<script>
export default {
  name: 'ETLTree',
  props: {
    treeData: {
      default: [],
      type: Array
    }
  },
  data() {
    return {
      defaultProps: {
        children: 'children',
        label: 'label'
      }
    };
  },
  methods: {
    handleNodeClick() {}
  }
};
</script>
<style lang="scss" scoped>
.el-tree {
  
}
</style>

