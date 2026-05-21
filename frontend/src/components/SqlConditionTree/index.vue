<template>
  <div>
    <el-tree
      class="common-tree"
      :data="sqlConditionTree"
      node-key="id"
      ref="treeRef"
      default-expand-all
      :expand-on-click-node="false"
    >
      <span class="custom-tree-node" slot-scope="{ node, data }">
        <div class="no-expand" v-if="node.expanded === false">当前过滤还有{{node.childNodes.length}}个子条件</div>
        <div v-for="(config,index) in treeRowConfig" :key="index" class="custom-tree-node-content">
          <div v-if="node.childNodes.length === 0">
            <mixin-components
              v-if="!(config.valueKey === 'operator' && (data.id === 1 || (node.parent.key === 1&&node.previousSibling === null)))"
              class="custom-tree-node-item"
              :width="config.width"
              :ruleForm="data"
              :valueKey="config.valueKey"
              :nameKey="config.nameKey"
              :options="config.options"
              :optionsConfig="config.optionsConfig"
              :optionsParams="config.optionsParams"
              :treeData="config.treeData"
              :treeConfig="config.treeConfig"
              :type="config.type"
              :watch="config.watch"
              :disabled="config.disabled"
            />
            <div v-else class="custom-tree-node-item" :style="{width:config.width}"></div>
          </div>
        </div>
        <div>
          <i v-if="data.id!== 1" @click="deleteRow(node,data)" class="el-icon-remove"></i>
        </div>
        <el-dropdown @command="addRow(...arguments,node,data)">
          <span class="el-dropdown-link">
            <i class="el-icon-circle-plus"></i>
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="1">添加条件</el-dropdown-item>
            <el-dropdown-item command="2" :disabled="node.level !== 1">添加子条件</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </span>
    </el-tree>
  </div>
</template>

<script>
/**
 *
 * Create by Leo
 * 基础组件
 * 基础筛选条件组件
 * 左右两栏式布局
 * 坐侧区域可控制显隐
 * 并减少一定的代码量
 * 统一设置弹框的配置项、确认按钮、取消按钮、关闭按钮等信息
 *
 */
import MixinComponents from '@/components/MixinComponents';
export default {
  components: {
    MixinComponents
  },
  props: {
    /**
     * 配置项
     */
    sqlConditionTree: {
      type: Array,
      default: () => {
        return [];
      }
    }
  },
  data() {
    return {
      /**
       * id
       */
      id: 2,
      /**
       * 树形组件数据
       */
      treeData: [],
      /**
       * 树形组件配置
       */
      defaultProps: {
        children: 'list',
        label: 'label'
      },
      /**
       * id
       */
      treeRowConfig: [
        {
          type: 'select',
          valueKey: 'operator',
          width: '100px',
          optionsConfig: {
            name: 'label',
            value: 'value'
          },
          options: [
            {
              value: 0,
              label: '-'
            },
            {
              value: 1,
              label: 'OR'
            },
            {
              value: 2,
              label: 'AND'
            },
            {
              value: 3,
              label: 'NOT'
            },
            {
              value: 4,
              label: 'OR NOT'
            },
            {
              value: 5,
              label: 'AND NOT'
            },
            {
              value: 6,
              label: 'XOR'
            }
          ]
        },
        {
          type: 'select',
          valueKey: 'negate',
          width: '100px',
          optionsConfig: {
            name: 'label',
            value: 'value'
          },
          options: [
            {
              value: false,
              label: '-'
            },
            {
              value: true,
              label: 'NOT'
            }
          ]
        },
        {
          type: 'select',
          valueKey: 'left_valuename',
          width: '100px',
          optionsConfig: {
            name: 'name',
            value: 'name'
          },
          options: 'getInputOutputFields',
          optionsParams: {
            before: true
          }
        },
        {
          type: 'select',
          valueKey: 'func',
          width: '100px',
          optionsConfig: {
            name: 'name',
            value: 'code'
          },
          options: 'getfuncType'
        },
        {
          type: 'select',
          valueKey: 'right_valuename',
          width: '100px',
          optionsConfig: {
            name: 'name',
            value: 'name'
          },
          options: 'getInputOutputFields',
          optionsParams: {
            before: true
          },
          watch: function(val) {
            if (val.right_valuename) {
              val.value = '';
            }
          }
        },
        {
          type: 'input',
          valueKey: 'value',
          width: '100px',
          watch: function(val) {
            if (val.value) {
              val.right_valuename = '';
            }
          }
        }
      ]
    };
  },
  methods: {
    /**
     * 删除行
     * @param node
     * @param data
     */
    deleteRow(node, data) {
      if (node.parent.childNodes.length === 1) {
        if (node.nextSibling) {
          this.$refs.treeRef.remove(node.nextSibling);
        } else if (node.previousSibling) {
          this.$refs.treeRef.remove(node.previousSibling);
        }
        this.$refs.treeRef.remove(node);
      } else {
        this.$refs.treeRef.remove(node);
      }
    },
    /**
     * 获取子节点
     */
    getChild() {
      return {
        id: this.id++,
        operator: 2,
        negate: false,
        left_valuename: '',
        func: 0,
        right_valuename: '',
        value: ''
      };
    },
    /**
     * 添加行
     */
    addRow(type, vm, node, data) {
      console.log(type);
      console.log(data);

      if (type == 1) {
        if (node.level === 1) {
          // 非子树内增加
          this.sqlConditionTree.push(this.getChild());
        } else {
          // 子树内增加
          let child = this.getChild();
          this.$refs.treeRef.append(child, node.parent);
        }
      } else {
        /**
         * 设置子节点
         */
        let child = this.getChild();
        child.operator = data.operator;
        child.negate = data.negate;
        child.left_valuename = data.left_valuename;
        child.right_valuename = data.right_valuename;
        child.func = data.func;
        child.value = data.right_valuename;
        let childNew = this.getChild();
        this.$refs.treeRef.append(child, node);
        this.$refs.treeRef.append(childNew, node);
      }
    },
    /**
     * 获取数据
     */
    getData() {
      return this.sqlConditionTree;
    }
  }
};
</script>

<style lang="scss" scoped>
.custom-tree-node {
  display: flex;
  align-items: center;
  .no-expand {
    margin-right: 5px;
    color: #fff;
  }
  i {
    font-size: 18px;
    margin-right: 5px;
  }
  .custom-tree-node-content {
    display: flex;
  }
  .custom-tree-node-item {
    margin-right: 10px;
  }
}
.common-tree {
  background: transparent;
  padding: 0px 10px;
  position: relative;
  height: 100%;
  & ::v-deep .el-checkbox-button__inner {
    padding: 7px 11px;
  }
  & ::v-deep .el-checkbox__input.is-checked .el-checkbox__inner,
  & ::v-deep .el-checkbox__input.is-indeterminate .el-checkbox__inner {
    background: var(--tree-checked);
    border-color: var(--tree-checked);
  }
  & ::v-deep .el-tree {
    background: none;
  }
  & ::v-deep .el-tree-node__content {
    height: 34px;
  }
  & ::v-deep .el-tree-node__content:hover {
    background-color: var(--tree-hover-bg);
  }
  & ::v-deep .is-current > .el-tree-node__content {
    background-color: var(--tree-hover-bg);
  }
  & ::v-deep .el-tree-node:focus > .el-tree-node__content {
    background: var(--tree-hover-bg);
  }
  & ::v-deep .tree-search {
    padding: 5px 5px;
    position: absolute;
    left: 0;
    top: 0;
    width: 97%;
  }
  .tree-search ::v-deep .el-input__inner {
    background: none;
    border-color: var(--tree-bg);
    color: #fff;
    width: 100% !important;
  }
  .tree-search >>> .el-input__icon {
    line-height: 30px !important;
  }
}

.common-tree-el {
  position: absolute;
  left: 0;
  top: 50px;
  width: 100%;
  height: 90%;
  overflow-y: auto;
}
</style>


