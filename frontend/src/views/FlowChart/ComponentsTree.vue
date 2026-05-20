<template>
  <div class="tabdiv">
    <el-tabs type="border-card">
      <el-tab-pane>
        <span slot="label">
          <img src="~@/assets/indexPage/tab_cz.png" alt>
          操作
        </span>
        <div class="tab_cz_div">
          <div class="search">
            <el-input placeholder="搜索" size="small" v-model="filterText">
              <i slot="prefix" class="el-input__icon el-icon-search"></i>
            </el-input>
          </div>
          <div class="navdiv" :style="{ height: bindTableNames ? 'calc(50% - 20px)' : 'calc(100% - 36px)' }">
            <el-tree
              class="components-tree"
              :data="data"
              node-key="id"
              :default-expanded-keys="['source','preHandle','sign','learn']"
              icon-class="el-icon-arrow-right"
              :default-expand-all="false"
              :filter-node-method="filterNode"
              :highlight-current="true"
              ref="filterTree"
              :props="defaultProps"
            >
              <span class="custom-tree-node" slot-scope="{ node, data }">
                <span
                  :draggable="!(data.children && data.children.length)"
                  @dragstart="dragHandle($event, data)"
                  class="dragNode"
                >
                  <SvgIcon :iconClass="data.icon | getSvgName" style="marginTop: 3px;"></SvgIcon>
                  <span style="marginLeft: 5px;">{{ node.label }}</span>
                </span>
              </span>
            </el-tree>
          </div>
          <!-- 绑定表字段列表 -->
          <div class="bind-fields-section" v-if="bindTableNames">
            <div class="bind-fields-title">
              <i class="el-icon-s-grid"></i> 目标表字段
            </div>
            <div class="bind-fields-content">
              <div
                class="bind-table-group"
                v-for="(columns, tableName) in bindTableColumns"
                :key="tableName"
              >
                <div class="bind-table-name" @click="toggleTableExpand(tableName)">
                  <i :class="expandedTables[tableName] ? 'el-icon-arrow-down' : 'el-icon-arrow-right'"></i>
                  {{ tableName }}
                </div>
                <div class="bind-fields-grid" v-show="expandedTables[tableName]">
                  <div
                    class="bind-field-item"
                    v-for="col in columns"
                    :key="col.name"
                    :title="col.comment || col.name"
                  >
                    <span class="bind-field-name">{{ col.name }}</span>
                    <span class="bind-field-comment" v-if="col.comment">{{ col.comment }}</span>
                  </div>
                </div>
              </div>
              <div v-if="Object.keys(bindTableColumns).length === 0 && loading" class="bind-fields-loading">
                加载中...
              </div>
            </div>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script lang="js">
import Vue from "vue";
import SvgIcon from "@/components/SvgIcon/index";
import API from "@/api/index";
export default Vue.extend({
  components: {SvgIcon},
  props: {
    data: {
      type: Array,
      default: () => {
        return [];
      }
    },
    defaultProps: {
      type: Object,
      default: () => {
        return {
          children: "children",
          label: "text"
        };
      }
    },
    procType: {
      type: String,
      default: ''
    },
    bindTableNames: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      filterText: "",
      planList: [],
      bindTableColumns: {},
      expandedTables: {},
      loading: false
    };
  },
  filters: {
    getSvgName(icon) {
      if(icon) {
        let startStr = 'ui/images/';
        let endStr = '.svg?';
        let startIndex = icon.indexOf(startStr);
        let endIndex = icon.indexOf(endStr);
        return icon.slice(startIndex+startStr.length, endIndex);
      }else {
        return 'PTHJB'
      }
    }
  },
  mounted() {
    this.getData();
  },
  watch: {
    filterText(val) {
      this.$refs.filterTree.filter(val);
    },
    bindTableNames: {
      immediate: true,
      handler(val) {
        if (val) {
          this.loadBindTableColumns();
        } else {
          this.bindTableColumns = {};
          this.expandedTables = {};
        }
      }
    }
  },
  methods: {
    showPlanData(id){
      this.$emit('showPlanData', id, )
    },
    getData(){
      API.getCreatePlanList({
        pageIndex: 1,
        pageSize: 1000,
        flag: 1
      }).then(res => {
        this.planList = res.data.data.records;
      });
    },
    dragHandle(ev,data) {
      this.$emit("dragHandle", ev, data);
    },
    filterNode(value, data) {
      if (!value) return true;
      return data.text.toLocaleLowerCase().indexOf(value.toLocaleLowerCase()) !== -1;
    },
    loadBindTableColumns() {
      const tables = this.bindTableNames.split(',').filter(Boolean);
      if (tables.length === 0) return;
      this.loading = true;
      this.bindTableColumns = {};
      this.expandedTables = {};
      const promises = tables.map(tableName => {
        return API.getBindTableColumns({ tableName }).then(res => {
          const data = res.data.data || res.data;
          if (Array.isArray(data)) {
            this.$set(this.bindTableColumns, tableName, data);
            this.$set(this.expandedTables, tableName, true);
          }
        }).catch(() => {});
      });
      Promise.all(promises).finally(() => {
        this.loading = false;
      });
    },
    toggleTableExpand(tableName) {
      this.$set(this.expandedTables, tableName, !this.expandedTables[tableName]);
    }
  }
});
</script>
<style lang="scss">
@import '@/styles/theme.scss';
.tabdiv {
  width: 100%;
  height: 100%;
  position: relative;
}
.tabdiv .adddiv {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 10;
  cursor: pointer;
}
.tabdiv /deep/ .el-tabs--border-card > .el-tabs__header .el-tabs__item {
  color: $normalWordsClr !important;
}
.tabdiv /deep/ .el-tabs__nav {
  padding-top: 4px;
}
.search {
  background: #314a40;
}
.components-tree {
  background: transparent;
  color: #eddbdb !important;
  .el-tree-node__content > div {
    color: $normalWordsClr;
  }
  .dragNode {
    font-size: 16px;
    display: flex;
    align-items: center;
  }
}
</style>
<style lang="scss">
@import '@/styles/theme.scss';
.components-tree .el-tree-node__content > div {
  color: #fff !important;
}
.components-tree .el-tree-node__content:hover {
  background: transparent !important;
}
.components-tree /deep/ .el-tree-node__expand-icon {
  display: inline-block !important;
}
.tabdiv .el-tabs {
  height: 100%;
  border: none;
}
.tabdiv .el-tree--highlight-current .el-tree-node.is-current > .el-tree-node__content {
  background: transparent !important;
}
.tabdiv .el-tabs .el-tabs__header {
  background: $centerTtlBgClr;
  height: 40px;
  border-bottom: none;
}
.tabdiv .el-tabs .el-tabs__content {
  height: calc(100% - 40px);
  padding: 0;
  overflow: hidden;
  overflow-y: auto;
  background: transparent;
  color: $normalWordsClr;
}
.tabdiv .el-tabs .el-tabs__content .el-tab-pane,
.tabdiv .el-tabs .el-tabs__content .tab_cz_div {
  height: 100%;
  background: var(--flowBgClr, #082b5f);
  /deep/ .el-tree {
    background: transparent;
  }
}
.tabdiv .el-tabs .el-tabs__content .search {
  margin-bottom: 4px;
}
.tabdiv .el-tabs .el-tabs__content .navdiv {
  overflow: hidden;
  overflow-y: auto;
}
.tabdiv .el-tabs .el-tabs__item {
  height: 36px;
  line-height: 36px;
  margin-top: 6px;
  border: none;
}
.tabdiv .el-tabs .el-tabs__item span {
  display: flex;
  align-items: center;
}
.tabdiv .el-tabs--border-card {
  background: transparent;
}
.tabdiv .el-tree-node:focus > .el-tree-node__content {
  background: transparent;
}
.tabdiv .el-tabs--border-card > .el-tabs__header .el-tabs__item.is-active {
  background: transparent;
  border: none;
  color: $normalWordsClr;
  border-top-left-radius: 5px;
  border-top-right-radius: 5px;
  height: 34px;
  padding-left: 10px;
}
.tabdiv .el-tabs--border-card > .el-tabs__header .el-tabs__item:not(.is-disabled):hover {
  color: $normalWordsClr;
}
.plan__List {
  line-height: 32px;
  width: 150px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  cursor: pointer;
}

/* 绑定表字段区域样式 */
.bind-fields-section {
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  height: calc(50% - 20px);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}
.bind-fields-title {
  padding: 6px 10px;
  font-size: 16px;
  color: #fff;
  background: rgba(255, 255, 255, 0.05);
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  i {
    margin-right: 4px;
  }
}
.bind-fields-content {
  flex: 1;
  overflow-y: auto;
  padding: 4px 0;
}
.bind-table-group {
  margin-bottom: 2px;
}
.bind-table-name {
  padding: 4px 10px;
  font-size: 15px;
  color: #67c23a;
  cursor: pointer;
  user-select: none;
  &:hover {
    background: rgba(255, 255, 255, 0.05);
  }
  i {
    margin-right: 4px;
    font-size: 10px;
  }
}
.bind-fields-grid {
  display: flex;
  flex-wrap: wrap;
  padding: 2px 8px;
  align-items: flex-start;
}
.bind-field-item {
  width: calc(50% - 4px);
  padding: 4px 6px;
  margin: 2px;
  font-size: 14px;
  color: #fff;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 3px;
  overflow: hidden;
  box-sizing: border-box;
}
.bind-field-name {
  display: block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-weight: bold;
}
.bind-field-comment {
  display: block;
  font-size: 12px;
  color: #aaa;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.bind-fields-loading {
  padding: 10px;
  text-align: center;
  font-size: 12px;
  color: #999;
}
</style>
