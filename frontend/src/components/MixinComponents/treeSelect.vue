<template>
  <div class="treeSelectdiv">
    <el-input
      v-model="chooseValue"
      type="text"
      clearable
      :style="{ width: width }"
      :disabled="disabled"
      @click.native.stop="showTreediv"
    ></el-input>
    <div class="treediv" v-show="showTree">
      <el-tree
        :props="treeProps"
        :load="loadNode"
        :lazy="lazy"
        @node-click="chooseNode"
        :data="selectOptions"
      ></el-tree>
    </div>
  </div>
</template>
<script lang="ts">
import Vue from 'vue';
export default Vue.extend({
  name: 'treeSelectdiv',
  props: {
    value: {
      type: String,
      default: ''
    },
    treeProps: {
      type: Object,
      default: () => {
        return {};
      }
    },
    loadNode: {
      type: Function
    },
    lazy: {
      type: Boolean,
      default: false
    },
    width: {
      type: String,
      default: ''
    },
    disabled: {
      type: Boolean,
      default: false
    },
    selectOptions: {
      type: Array,
      default: () => {
        return [];
      }
    },
    options: {
      type: String
    }
  },
  data() {
    return {
      showTree: false,
      chooseValue: this.value
    };
  },
  mounted() {
    this.$eventBus.$on('hideTree', () => {
      this.hideTreediv();
    });
  },
  methods: {
    showTreediv() {
      this.showTree = true;
    },
    hideTreediv() {
      this.showTree = false;
    },
    chooseNode(data: any) {
      if (this.options === 'getConnectCodeTree' && !this.lazy && data[this.treeProps.value]) {
        this.$emit('getSelectValue', data[this.treeProps.value]);
        this.chooseValue = data.value || data[this.treeProps.label];
      } else {
        this.chooseValue = data.value || data[this.treeProps.value];
      }
    }
  }
});
</script>
<style lang="scss" scoped>
@import '@/styles/theme.scss';
.treeSelectdiv {
  width: 100%;
  height: 100%;
  position: relative;
  .treediv {
    width: 100%;
    height: 200px;
    overflow: hidden;
    overflow-y: auto;
    z-index: 1000;
    position: absolute;
    top: 40px;
    left: 0;
    background: #1d2b1a;
    ::v-deep .el-tree {
      background: transparent;
      color: $normalWordsClr;
    }
    ::v-deep .el-tree-node__content:hover,
    ::v-deep .el-tree-node:focus > .el-tree-node__content {
      background: $crtTreeNode;
    }
  }
}
</style>
