<template>
  <div class="treeLine">
    <div class="searchdiv" v-if="showSearch">
      <el-input placeholder="请输入内容" v-model="searchVal" size="mini"></el-input>
    </div>
    <div class="navdiv" :style="{height: showSearch?'':'100%'}">
      <el-tree
        ref="treeLine"
        node-key="id"
        v-loading="lineTreeConfig.loading"
        :data="lineTreeConfig.navData"
        :props="lineTreeConfig.defaultProps"
        :default-expand-all="lineTreeConfig.defaultExpandAll"
        :default-expanded-keys="lineTreeConfig.defaultKeys"
        :filter-node-method="filterNode"
        accordion
        @node-click="showInfor"
        class="tree-line"
        :indent="12"
        :expand-on-click-node="true"
      >
        <span class="custom-tree-node" slot-scope="{ node, data }">
          <span class="tree">
            <!-- <img v-if="data.imgUrl" :src="require(`@/assets/images/dataImg/${data.imgUrl}.png`)" alt="">  -->
            <span style="marginRight: 2px;">
              <svg-icon :icon-class="data.type | getIconName"/>
            </span>
            <span>{{ node.label }}</span>
            <!--             <span
              v-if="showStat"
            >{{[data.children && data.children.length || data[lineTreeConfig.defaultProps.children] && data[lineTreeConfig.defaultProps.children].length || 0]}}</span>
            -->
          </span>
          <!--           <span>
            <el-button
              v-if="node.level==1"
              type="text"
              size="mini"
              class="removeNode"
              @click.stop="removeNode(node)"
              icon="el-icon-delete"
            ></el-button>
          </span>
          -->
        </span>
      </el-tree>
    </div>
  </div>
</template>
<script>
/**
 *
 * Create by Leo
 * 基础组件
 * 基础线性树形结构组件
 * 左右两栏式布局
 * 坐侧区域可控制显隐
 * 并减少一定的代码量
 * 统一设置弹框的配置项、确认按钮、取消按钮、关闭按钮等信息
 *
 */
import debounce from 'lodash/debounce';
export default {
  name: 'treeLine',
  /**
   * 配置项
   */
  props: ['lineTreeConfig'],
  data() {
    return {
      /**
       * 配置项
       */
      searchVal: '',
      /**
       * 配置项
       */
      showSearch: true,
      /**
       * 配置项
       */
      showStat: true
    };
  },
  mounted() {
    if (this.lineTreeConfig.showStat != undefined) {
      this.showStat = this.lineTreeConfig.showStat;
    }
    this.showSearch = this.lineTreeConfig.showSearch != undefined ? this.lineTreeConfig.showSearch : true;
  },
  watch: {
    // 菜单筛选值
    searchVal: {
      deep: true,
      handler(val) {
        this.$refs.treeLine.filter(val);
        if (val.showStat != undefined) {
          this.showStat = val.showStat;
        }
      }
    }
  },
  methods: {
    // 删除任务节点
    /**
     * 配置项
     * @param node
     */
    removeNode(node) {
      this.$emit('deleteTree', node);
    },
    /**
     * 展示节点信息
     * @param node
     */
    showInfor: debounce(function(node) {
      // 防抖
      this.$emit('showInfor', node);
    }, 400),
    /**
     * 过滤节点
     * @param value 
     * @param data
     */
    filterNode(value, data) {
      if (!value) return true;
      return data[this.lineTreeConfig.defaultProps.label || 'label'].indexOf(value) !== -1;
    },
    /**
     * 设置已选择节点
     * @param task
     */
    setTreeNodeSelected(task) {
      if (task.id) {
        this.$refs.treeLine.setCurrentKey(task.id);
      }
    }
  },
  filters: {
    /**
     * 筛选icon名称
     * @param type
     */
    getIconName(type) {
      switch (type) {
        case 1:
          return 'tree_icon_task';
        case 2:
          return 'tree_icon_project';
        case 3:
          return 'tree_icon_activity';
        case 4:
          return 'tree_icon_form';
        case 5:
          return 'tree_icon_equip';
        case 6:
          return 'tree_icon_army';
        case 7:
          return 'tree_icon_position';
        case 8:
          return 'tree_icon_device';
        default:
          return 'tree_icon_activity';
      }
    }
  }
};
</script>


<style lang="scss" scoped>
@import '@/styles/theme.scss';
@import '@/styles/dashLineTree.scss';
.treeLine {
  width: 100%;
  height: 100%;
  .searchdiv {
    width: 100%;
    height: 30px;
    // padding: 0 10px;
    ::v-deep .el-input__inner {
      border: 1px solid var(--currentRow);
      background: var(--headerBg);
      color: #fff;
    }
  }
  .navdiv {
    width: 100%;
    height: calc(100% - 30px);
    overflow: hidden;
    overflow-y: auto;
    .tree {
      display: flex;
      align-items: center;
      img {
        width: 14px;
        margin-right: 4px;
      }
    }
    ::v-deep .el-loading-mask {
      background-color: $inputBgClr;
    }
  }
}
</style>