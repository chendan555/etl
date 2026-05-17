<template>
  <div id="list">
    <el-table
      v-if="finishInit"
      :data="tableData"
      ref="multipleTable"
      border
      :highlight-current-row="highlightCurrentRow"
      @selection-change="handleSelectionChange"
      @row-click="rowClick"
      @expand-change="expandChange"
      @row-dblclick="rowdblClick"
      @current-change="currentChange"
      :class="`${resizable ? 'table-resizable' : ''}`"
      :stripe="striped"
      :style="{width: width}"
      :height="height"
    >
      <el-table-column v-if="isShowCheckBox" type="selection" width="55" align="center"></el-table-column>
      <el-table-column v-if="index" type="index" width="65" label="序号" align="center"></el-table-column>
      <el-table-column type="expand" v-if="isShowExpand">
        <template slot-scope="props">
          <div class="expanddiv">
            <slot name="expand" :props="props"></slot>
          </div>
        </template>
      </el-table-column>
      <!-- <base-table :tableColumnConfig="{}" /> $attrs -->
      <el-table-column
        v-for="(item,index) in tableColumnConfig"
        :show-overflow-tooltip="item.showOverFlowTooltip"
        :key="index"
        :width="item.width"
        :label="item.label"
        :prop="item.prop"
        :align="item.align"
        :headerAlign="item.headerAlign"
      >
        <template slot-scope="scope">
          <!-- 非编辑状态通过文字展示 -->
          <div
            v-if="!item.isEdit"
            :class="{'can-link':item.linkMethod}"
            @click="emitLinkMethod(item.linkMethod,scope.row)"
          >
            <!-- {{item.isGetPropInDict?getPropInDict(item.prop,scope.row[item.prop]):scope.row[item.prop]}} -->
            <span v-if="item.propToNameFn">{{item.propToNameFn(scope.row[item.prop])}}</span>
            <span v-else>{{scope.row[item.prop] || '-'}}</span>
          </div>
          <!-- 编辑状态用配置的编辑类型展示 -->
          <div v-else>
            <mixin-components
              :width="item.editConfig.width"
              :optionsConfig="item.editConfig.optionsConfig"
              :watch="item.watch"
              :ruleForm="scope.row"
              :valueKey="item.prop"
              :nameKey="item.prop"
              :lazy="item.editConfig.lazy"
              :treeProps="item.editConfig.treeProps"
              :loadNode="item.editConfig.loadNode"
              :defaultValue="item.editConfig.defaultValue"
              :type="item.editConfig.type"
              :lazyLoadProps="item.editConfig.lazyLoadProps"
              :options="item.editConfig.options"
              :optionsList="item.editConfig.optionsList"
              :otherOptions="item.editConfig.otherOptions"
              :optionsParams="item.editConfig.optionsParams"
              :disabled="item.disabled"
              :valueFormat="item.editConfig.valueFormat"
              :index="scope.$index"
              :tableData="tableData"
              :placeholder="item.placeholder"
              :autoUpload="item.autoUpload"
            />
          </div>
        </template>
      </el-table-column>
      <!--操作-->
      <el-table-column
        v-if="isShowOperation"
        label="操作"
        header-align="center"
        align="center"
        :width="operationWidth"
      >
        <template slot-scope="scope">
          <slot name="operation" :row="scope"></slot>
        </template>
      </el-table-column>
      <el-table-column v-if="isShowColumn" label="查看结果" header-align="center" align="center">
        <template slot-scope="scope">
          <slot name="operation" :row="scope"></slot>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
/**
 *
 * Create by Leo
 * 基础组件
 * 基础表格组件
 * 左右两栏式布局
 * 坐侧区域可控制显隐
 * 并减少一定的代码量
 * 统一设置弹框的配置项、确认按钮、取消按钮、关闭按钮等信息
 *
 */

import API from '@/api/index';
export default {
  name: 'tablelist',

  props: {
    /**
     * 配置项
     */
    isShowExpand: {
      type: Boolean,
      default: false
    },
    /**
     * 配置项
     */
    operationWidth: {
      type: String,
      default: '150'
    },
    /**
     * 配置项
     */
    resizable: {
      type: Boolean,
      default: true
    },
    /**
     * 配置项
     */
    isShowOperation: {
      type: Boolean,
      default: false
    },
    /**
     * 配置项
     */
    isShowColumn: {
      type: Boolean,
      default: false
    },
    /**
     * 配置项
     */
    isShowCheckBox: {
      type: Boolean,
      default: false
    },
    /**
     * 配置项
     */
    index: {
      type: Boolean,
      default: false
    },
    /**
     * 配置项
     */
    height: {
      type: String | Number,
      default: '100%'
    },
    /**
     * 配置项
     */
    width: {
      type: String,
      default: '100%'
    },
    /**
     * 配置项
     */
    tableColumnConfig: {
      type: Array,
      default: []
    },
    /**
     * 配置项
     */
    tableData: {
      type: Array,
      default: () => {
        return [];
      }
    },
    /**
     * 配置项
     */
    tableConfig: {
      type: Object,
      default: () => {
        return {};
      }
    },
    /**
     * 配置项
     */
    highlightCurrentRow: {
      type: Boolean,
      default: () => {
        return false;
      }
    }
  },
  data() {
    return {
      /**
       * 配置项
       */
      checkboxList: [],
      /**
       * 配置项
       */
      finishInit: false,
      /**
       * 配置项
       */
      striped: true
    };
  },
  beforeCreate() {
    this.$options.components.MixinComponents = () => import('../MixinComponents/index.vue');
  },
  created() {
    this.initTableData();
  },
  watch: {
    /**
     * 监听tableColumnConfig的值变化，并更新表格内需要请求后端的数据，主要解决接口参数需要更新的情况，如选择数据源和数据表  --Leo 20210518
     */
    tableColumnConfig: {
      deep: true,
      handler(val) {
        this.tableColumnConfig = val;
        this.initTableData();
      }
    }
  },
  mounted() {
    this.$eventBus.$on('setTableData', data => {
      // console.log(data);
      this.tableData = data;
    });
  },
  methods: {
    /*
     *该方法为解决表格数据中下拉框重复请求接口导致的性能问题
     *该方法未修改组件配置文件（js文件）内容，保留原始设计
     *该方法仅在配置文件加载时对表格配置进行遍历查询，将下拉框配置项接口进行一次请求，并将结果绑定到新增属性optionsList上，避免下拉框重复加载导致的重复请求问题
     *MixinComponents组件加载不同元素时需先判断元素是否含有options接口，并判断是否为select下拉框元素，为true则绑定optionsList属性值
     *方法在本组件created周期中调用，即在数据绑定到dom节点之前完成数据的请求和绑定
     *修改时间：20210114
     *修改人：Leo
     */
    initTableData() {
      if (this.tableColumnConfig.length) {
        this.getTableData(this.tableColumnConfig);
      }
    },
    /**
     * 配置项
     */
    getTableData(tableColumnConfig) {
      const loading = this.$utils.setLoading('查询中...');
      let promList = [];
      tableColumnConfig.forEach((ele, index) => {
        if (ele.editConfig && ele.editConfig.type === 'select' && ele.editConfig.options && typeof ele.editConfig.options === 'string') {
          if (ele.editConfig.optionsParams && !ele.editConfig.optionsParams.hasOwnProperty('nodeId')) {
            ele.editConfig.optionsParams.nodeId = this.$store.state.nodeId;
          }
          let p = API[ele.editConfig.options](ele.editConfig.optionsParams).then(data => {
            ele.editConfig.optionsList = data.data.data ? data.data.data : data.data;
          });
          promList.push(p);
        }
        if (index === tableColumnConfig.length - 1) {
          Promise.all(promList).finally(() => {
            this.finishInit = true;
            loading.close();
          });
        }
      });
    },
    /**
     * 配置项
     * @param item
     * @param val
     */
    getFormItemValue(item, val) {
      if (item.isGetPropInDict) {
        return this.getPropInDict(item.prop, val);
      } else if (item.hasOwnProperty('formatTime')) {
        return this.$common.common.formatTime(val, item.formatTime);
      } else {
        return val;
      }
    },
    /**
     * @param method 连接触发的方法
     * @param row 当前行的数据
     */
    emitLinkMethod(method, row) {
      console.log(row);
      if (!method) {
        return;
      }
      this.$emit(method, row);
    },
    /**
     * 获取字典值
     * @param key 字段名
     * @param val 字段值
     */
    getPropInDict(key, val) {
      let dictType = this.$common.common.getStorageOption(key, val);
      if (dictType) {
        return dictType.aname;
      }
    },
    /**
     * 清除多选框
     */
    handleClearSelection() {
      this.$refs.multipleTable.clearSelection();
    },
    /**
     * 多选框选择事件
     * @param val
     */
    handleSelectionChange(val) {
      this.checkboxList = val;
      this.$emit('selectionChange', val);
    },
    /**
     * 点击某一行事件
     * @param row
     * @param event
     * @param column
     */
    rowClick(row, event, column) {
      this.$emit('rowClick', row);
    },
    /**
     * 双击某一行事件
     * @param row
     * @param event
     * @param column
     */
    rowdblClick(row, event, column) {
      this.$emit('rowdblClick', row);
    },
    /**
     * 点击某一行事件
     * @param val
     */
    currentChange(val) {
      this.$refs.multipleTable.clearSelection();
      this.$nextTick(() => {
        this.$refs.multipleTable.toggleRowSelection(val);
        this.$emit('current-change', val);
      });
    },
    /**
     * 行触发展开或关闭事件
     * @param row
     * @param expandedRows
     */
    expandChange(row, expandedRows) {
      this.$emit('expandChange', row, expandedRows);
    },
    /**
     * 切换显示状态
     */
    changeEditStatus() {
      this.tableColumnConfig = this.tableColumnConfig.map(item => {
        if (item.editConfig && item.editConfig.type) {
          item.isEdit = !item.isEdit;
        }
        return item;
      });
    },
    /**
     * 新增行数据
     */
    // addRowData() {},
    /**
     * 删除行数据
     */
    deleteRowDataByIndex(index) {
      this.tableData.splice(index, 1);
    }
  }
};
</script>

<style scoped lang="scss">
.expanddiv {
  padding: 10px;
}
#list {
  height: 100%;
}
.can-link {
  color: rgb(64, 158, 255) !important;
  cursor: pointer;
}

.table-resizable >>> th,
.table-resizable >>> tr th {
  position: relative;
  overflow: inherit;
  cursor: default;
}
.table-resizable >>> th > div,
.table-resizable >>> tr th > div {
  cursor: default;
}
.baseTable >>> .el-table__expanded-cell {
  background: none;
}
.operation {
  font-size: 14px;
}
/* .table-resizable >>> th:not(:last-child):before,
.table-resizable >>> tr th:not(:last-child):before {
  position: absolute;
  right: -6px;
  top: 0;
  content: "";
  width: 0;
  height: 0;
  z-index: 1;
  border-right: 6px solid transparent;
  border-left: 6px solid transparent;
  border-top: 6px solid #729c75;
  cursor: default;
} */
</style>

