<template>
  <div class="operationBtn">
    <el-tooltip content="修改" placement="top">
      <el-button type="text" @click.stop="editProc(projectItem)">修改</el-button>
      <!-- icon="el-icon-edit-outline" -->
    </el-tooltip>
    <!-- <el-tooltip content="设置模板" placement="top">
      <el-button
        size="mini"
        type="text"
        icon="el-icon-document"
        v-show="projectItem.isModel === 0"
        @click="setProjectToModel(projectItem)"
      ></el-button>
    </el-tooltip>
    <el-tooltip content="取消模板" placement="top">
      <el-button
        size="mini"
        type="text"
        icon="el-icon-document-remove"
        v-show="projectItem.isModel === 1"
        @click="setItemModel(projectItem)"
      ></el-button>
    </el-tooltip>
    <el-tooltip content="复制模板" placement="top">
      <el-button
        size="mini"
        type="text"
        icon="el-icon-document-copy"
        v-show="projectItem.isModel === 1"
        @click="copyItem(projectItem)"
      ></el-button>
    </el-tooltip>-->
    <el-tooltip content="删除" placement="top">
      <el-button class="delete" type="text" @click.stop="removeProc(projectItem)">删除</el-button>
      <!-- icon="el-icon-delete" -->
    </el-tooltip>
  </div>
</template>
<script>
export default {
  name: 'operationBtn',
  props: {
    projectItem: {
      type: Object
    }
  },
  methods: {
    removeProc(item) {
      this.$emit('removeProc', item);
    },
    copyItem(item) {
      this.$emit('copyItem', item);
    },
    setItemModel(item) {
      this.$emit('setItemModel', item);
    },
    setProjectToModel(item) {
      this.$emit('setProjectToModel', item);
    },
    editProc(item) {
      this.$emit('editProc', item);
    }
  }
};
</script>
<style lang="scss" scoped>
.operationBtn {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  ::v-deep .el-button {
    .el-icon-edit-outline {
      color: #409eff;
    }
    .el-icon-delete {
      color: #e96a54;
    }
    &.delete {
      color: #e96a54;
    }
  }
}
</style>
