<template>
  <div class="filterForm">
    <base-form v-bind="{...filterFormConfig}"></base-form>
    <div class="btndiv">
      <el-button type="primary" size="mini" @click="search">确定</el-button>
      <el-button type="info" size="mini" @click="reset">重置</el-button>
    </div>
  </div>
</template>
<script>
/**
 *
 * Create by Leo
 * 基础组件
 * 基础表格布局组件
 * 左右两栏式布局
 * 坐侧区域可控制显隐
 * 并减少一定的代码量
 * 统一设置弹框的配置项、确认按钮、取消按钮、关闭按钮等信息
 *
 */
import BaseForm from '@/components/BaseForm/index.vue';
export default {
  name: 'filterForm',
  components: { BaseForm },
  props: {
    /**
     * 配置项
     */
    filterFormConfig: {
      type: Object
    }
  },
  methods: {
    /**
     * 查询
     */
    search() {
      this.$emit('search', this.filterFormConfig.ruleForm);
    },
    /**
     * 重置
     */
    reset() {
      this.$emit('reset');
    }
  }
};
</script>
<style lang="scss" scoped>
.filterForm {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  /deep/ .el-form-item--small.el-form-item {
    margin-bottom: 0;
  }
  .btndiv {
    display: flex;
    align-items: center;
    margin-left: 15px;
  }
}
</style>
