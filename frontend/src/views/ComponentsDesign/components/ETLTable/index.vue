<template>
  <div>
    <el-table
      :data="tableData"
      ref="multipleTable"
      border
      @selection-change="handleSelectionChange"
      @row-click="rowClick"
      @row-dblclick="rowdblClick"
      stripe
      :style="{width: width}"
      :height="height"
    >
      <el-table-column v-if="isShowCheckBox" type="selection" width="55" align="center"></el-table-column>
      <el-table-column
        v-for="(item) in tableColumnConfig"
        :show-overflow-tooltip="item.showOverFlowTooltip"
        :key="item.prop"
        :width="item.width"
        :label="item.label"
        :prop="item.prop"
        :align="item.align"
        :headerAlign="item.headerAlign"
      >
        <template slot-scope="scope">
          <div
            @click="editTd(scope,item.prop,$event,item)"
          >{{getOptionsName(scope.row,item) || '-'}}</div>
        </template>
      </el-table-column>
      <!--操作-->
      <el-table-column
        v-if="isShowOperation"
        fixed="right"
        label="操作"
        header-align="center"
        align="center"
        :width="operationWidth"
      >
        <template slot-scope="scope">
          <div class="table-column-operation">
            <el-button size="mini" @click="remove(scope)">删除</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>
    <el-input
      :style="{left:style.left+'px',top:style.top+'px',height:style.height+'px',width:style.width+'px'}"
      class="editInput"
      ref="editInput"
      v-if="showEditInput&&editType === 'input'"
      v-model="currentTdContent"
      @blur="changeTdContent"
    ></el-input>
    <el-select
      v-if="showEditInput&&editType === 'select'"
      filterable
      :style="{left:style.left+'px',top:style.top+'px',height:style.height+'px',width:style.width+'px'}"
      class="editInput"
      ref="editInput"
      :multiple="optionsConfig.multiple"
      v-model="currentTdContent"
      @visible-change="changeTdContent"
      placeholder="请选择"
    >
      <el-option
        v-for="option in currentTdOptions"
        :key="option[optionsConfig.value]"
        :label="option[optionsConfig.name]"
        :value="option[optionsConfig.value]"
      ></el-option>
    </el-select>
    <el-cascader
      v-if="showEditInput&&editType === 'cascader'"
      filterable
      :style="{left:style.left+'px',top:style.top+'px',height:style.height+'px',width:style.width+'px'}"
      class="editInput"
      ref="editInput"
      v-model="currentTdContent"
      :options="currentTdOptions"
      @change="changeTdContent"
    ></el-cascader>
  </div>
</template>

<script>
// import resOptionsDeal from '@/views/ComponentsDesign/res_options_deal.js';
export default {
  name: 'ETLTable',
  props: {
    config: {
      type: Object
    },
    operationWidth: {
      type: String,
      default: '150'
    },
    isShowOperation: {
      type: Boolean,
      default: false
    },
    isShowCheckBox: {
      type: Boolean,
      default: false
    },
    height: {
      type: String,
      default: '100%'
    },
    width: {
      type: String,
      default: '100%'
    },
    tableColumnConfig: {
      type: Array,
      default: []
    },
    tableData: {
      type: Array,
      default: () => {
        return [];
      }
    }
  },
  data() {
    return {
      checkboxList: [],
      style: {
        left: 0,
        top: 0,
        width: 0,
        height: 0
      },
      showEditInput: false,
      editType: 'input',
      currentTdContent: '',
      currentTdIndex: 0,
      currentTdKey: 0,
      currentTdOptions: [],
      optionsConfig: {
        name: 'name',
        value: 'value'
      }
    };
  },
  mounted() {
    const previewDialog = document.getElementById('previewDialog');
    previewDialog && previewDialog.addEventListener('scroll', this.closeEditInput, { capture: true });
  },
  beforeDestroy() {
    const previewDialog = document.getElementById('previewDialog');
    previewDialog && previewDialog.removeEventListener('scroll', this.closeEditInput, { capture: true });
  },
  methods: {
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
    // 点击某一行事件
    rowClick(row, event, column) {
      this.$emit('rowClick', row);
    },
    // 双击某一行事件
    rowdblClick(row, event, column) {
      this.$emit('rowdblClick', row);
    },
    editTd(row, key, event, columnConfig) {
      if (!columnConfig.isEdit) return;
      let rect = event.target.offsetParent.getBoundingClientRect();
      this.style = {
        left: rect.left, // 单元格padding10
        top: rect.top,
        height: rect.height,
        width: rect.width
      };
      this.showEditInput = true;
      this.editType = columnConfig.editType;
      this.currentTdContent = row.row[key];
      this.currentTdIndex = row.$index;
      this.currentTdKey = key;
      // 是否配置过滤字段方法
      if (columnConfig.optionsConfig.isRelyOnRowData) {
        let config = { httpUrl: columnConfig.httpUrl };
        // 行数据依赖参数
        let params = JSON.parse(columnConfig.params);
        for (let key in params) {
          if (row.row[params[key]]) {
            params[key] = row.row[params[key]];
          }
        }
        this.$eventBus.$emit('updateOptions', config, params, options => {
          this.currentTdOptions = options;
        });
      } else {
        this.currentTdOptions = columnConfig.options;
      }
      this.optionsConfig = columnConfig.optionsConfig;
      this.$nextTick(() => {
        console.log(this.$refs.editInput);
        this.$refs.editInput.focus();
      });
    },
    closeEditInput() {
      this.showEditInput = false;
    },
    changeTdContent(status) {
      // 兼容Input和blur的事件参数
      if (status === false || status !== true) {
        if (this.currentTdContent !== this.tableData[this.currentTdIndex][this.currentTdKey]) {
          this.tableData[this.currentTdIndex][this.currentTdKey] = this.currentTdContent;
          this.tableData[this.currentTdIndex].isEdited = true;
        }
        this.showEditInput = false;
      }
    },
    remove(row) {
      this.tableData.splice(row.$index, 1);
    },
    add(row) {
      // 传入多个行为清空替换操作
      if (Array.isArray(row)) {
        this.config.tableData = [];
        this.tableData.push(...row);
      } else {
        let addRow = {};
        if (row) {
          addRow = row;
        } else {
          this.tableColumnConfig.forEach(config => {
            addRow[config.prop] = config.defaultValue.length > 0 ? config.defaultValue : '';
          });
        }
        this.tableData.push(addRow);
      }
    },
    getOptionsName(row, item) {
      let value = row[item.prop];
      if (Array.isArray(value)) {
        return value;
      } else {
        if (item.editType === 'select' && item.options && item.options.length) {
          item.options.some(option => {
            if (option[item.optionsConfig.value] === value) {
              value = option[item.optionsConfig.name];
              return true;
            }
          });
        }
      }
      if (value === null || value === '' || value === undefined) {
        return value;
      } else {
        return value.toString();
      }
    }
  }
};
</script>

<style lang="scss" scoped>
.editInput {
  position: fixed;
  width: 300px;
  margin-top: 0;
  ::v-deep .el-input {
    height: 100%;
  }
  ::v-deep .el-input__inner {
    height: 100%;
  }
}
</style>


