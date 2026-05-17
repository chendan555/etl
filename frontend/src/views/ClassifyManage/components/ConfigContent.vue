<template>
  <el-container class="item">
    <el-container class="content">
      <el-aside v-if="showAside" :width="asideWidth">
        <slot name="config-aside"></slot>
      </el-aside>
      <el-main>
        <slot name="config-main"></slot>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
/**
 * create by Leo
 * 主界面组件
 */
export default {
  props: {
    /**
     * 配置项
     */
    asideWidth: {
      type: String,
      default: '240px'
    },
    /**
     * 配置项
     */
    showAside: {
      type: Boolean,
      default: true
    }
  }
};
</script>


<style lang="scss" scoped>
.el-container {
  width: 100%;
  height: 100%;
  background-color: #232e24;
  .el-header {
    height: auto !important;
    padding: 10px;
    font-size: 12px;
    border: 1px solid #435b44;
    background: var(--base-search-bg);
    color: #fff;
  }
  .content {
    height: 100%;
  }
  .el-aside {
    border-right: 1px solid #435b44;
  }
  .el-main {
    padding: 10px;
  }
}
</style>

