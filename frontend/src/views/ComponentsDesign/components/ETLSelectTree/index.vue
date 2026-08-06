<template>
  <el-select
    ref="select"
    v-model="config.value"
    :disabled="config.disabled"
    :placeholder="config.placeholder"
    :filterable="config.filterable"
    :filter-method="filterTree"
    @change="valueChange"
    default-expand-all
  >
    <el-option class="select-tree-options" label="selectTree" value="selectTree">
      <el-tree
        class="select-tree"
        :data="config.treeData"
        :props="config.optionsConfig"
        :filter-node-method="filterNode"
        :load="loadNode"
        :lazy="config.isLazyLoad"
        icon-class="el-icon-arrow-right"
        ref="selectTree"
        @click.native.stop
        @node-click="nodeClick"
      >
      </el-tree>
    </el-option>
  </el-select>
</template>
<script >
import Vue from 'vue';
import etlHttpMixins from '@/views/ComponentsDesign/mixins/etlHttpMixins.js';
export default Vue.extend({
  name: 'ETLSelectTree',
  mixins: [etlHttpMixins],
  props: {
    config: {
      default: {},
      type: Object
    }
  },
  data() {
    return {
      filterText: ''
    };
  },
  watch: {
    filterText(val) {
      this.$refs.selectTree.filter(val);
    }
  },
  created() {
    // todo 下拉树功能待配置优化
    this.config.optionsConfig.isLeaf = function(data, node) {
      return data.isDir === false || data.fileType === 'FILE';
    };
  },
  methods: {
    filterTree(text) {
      this.filterText = text;
    },
    filterNode(value, data) {
      if (!value) return true;
      return data[this.config.optionsConfig.label].indexOf(value) !== -1;
    },
    nodeClick(data) {
      if (data && data.children && data.children.length) {
        return;
      }
      this.config.value = data[this.config.optionsConfig.label];
      this.$refs.select.blur();
    },
    valueChange() {
      this.$emit('change');
    },
    updateOptionsTree() {
      this.$eventBus.$emit('updateOptions', this.config);
    },
    loadNode(node, resolve) {
      if (node.level === 0) {
        // 懒加载时从 API 获取根节点数据（文件/盘符列表），不走 demo treeData
        if (this.config.httpUrl && this.config.isLazyLoad) {
          // 解析初始参数（如 { path: '/' }）
          let initParams = {};
          try {
            initParams = JSON.parse(this.config.params || '{}');
          } catch (e) { /* ignore */ }
          this.http(this.config.httpUrl, initParams).then(res => {
            const data = res.data;
            if (Array.isArray(data)) {
              resolve(data);
            } else if (data && Array.isArray(data.data)) {
              resolve(data.data);
            } else {
              resolve([]);
            }
          }).catch(() => resolve([]));
        } else {
          resolve(node.data || []);
        }
        return;
      }
      if (node.data.isDir !== false) {
        this.http(this.config.httpUrl, node.data).then(res => {
          const data = res.data;
          if (Array.isArray(data)) {
            resolve(data);
          } else if (data && Array.isArray(data.data)) {
            resolve(data.data);
          } else {
            resolve([]);
          }
        }).catch(() => resolve([]));
      } else {
        resolve([]);
      }
    },
    getValue() {
      return this.config.value;
    }
  }
});
</script>

<style lang="scss" scoped>
.select-tree-options {
  height: auto;
  line-height: auto;
  padding: 0;
}
.el-tree ::v-deep .el-tree-node__expand-icon {
  display: inline-block;
}
</style>
