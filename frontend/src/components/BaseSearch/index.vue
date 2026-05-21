<template>
  <div class="base-search">
    <el-row :gutter="16">
      <el-col :span="18">
        <base-form ref="baseForm" :formItemConfig="queryFormItemConfig" :ruleForm="queryRuleForm" :formItemRuleConfig="formItemRuleConfig" />
      </el-col>
      <el-col class="query-operation" :span="6">
        <el-button type="primary" size="small" @click="search">查询</el-button>
        <el-button size="small" @click="clearQueryRuleForm">清空</el-button>
      </el-col>
    </el-row>
  </div>
</template>

<script>
/**
 *
 * Create by Leo
 * 基础组件
 * 基础查询组件
 * 左右两栏式布局
 * 坐侧区域可控制显隐
 * 并减少一定的代码量
 * 统一设置弹框的配置项、确认按钮、取消按钮、关闭按钮等信息
 *
 */

import BaseForm from "@/components/BaseForm";
export default {
  components: {
    BaseForm
  },
  props: {
    /**
     * 
     * 配置信息
     * 
     */
    queryFormItemConfig: {
      type: Array,
      default: () => {
        return [];
      }
    },
    /**
     * 
     * 配置信息
     * 
     */
    formItemRuleConfig: {
      type: Object,
      default: () => {
        return {};
      }
    },
    /**
     * 
     * 配置信息
     * 
     */
    queryRuleForm: {
      type: Object,
      default: () => {
        return {};
      }
    }
  },
  methods: {
    /**
     * 
     * 配置信息
     * 
     */
    clearQueryRuleForm() {
      this.$emit("clearQueryRuleForm");
    },
    /**
     * 
     * 配置信息
     * 
     */
    search() {
      this.$emit("search");
    }
  }
};
</script>

<style lang="scss" scoped>
.base-search {
  background: rgba(58, 80, 59, 0.5);
  border: 1px solid #435b44;
  padding-top: 15px;
}
.base-search ::v-deep .el-form-item {
  margin-bottom: 15px;
}
</style>
