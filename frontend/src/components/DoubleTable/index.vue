<template>
  <div class="double-table-content">
    <div class="double-table-content-item">
      <div class="double-table-content-btn">
        <el-button size="mini" @click="getField('left')">获取字段</el-button>
        <el-button size="mini" @click="addRow('left')">新增行</el-button>
      </div>
      <base-table
        :isShowOperation="true"
        :tableColumnConfig="leftTableColumnConfig"
        :tableData="leftTableData"
      >
        <div slot="operation" slot-scope="scope" @click="deleteRow('left', scope)">删除</div>
      </base-table>
    </div>
    <div class="double-table-content-item">
      <div class="double-table-content-btn">
        <el-button size="mini" @click="getField('right')">获取字段</el-button>
        <el-button size="mini" @click="addRow('right')">新增行</el-button>
      </div>
      <base-table
        :isShowOperation="true"
        :tableColumnConfig="rightTableColumnConfig"
        :tableData="rightTableData"
      >
        <div slot="operation" slot-scope="scope" @click="deleteRow('right', scope)">删除</div>
      </base-table>
    </div>
  </div>
</template>

<script>
/**
 *
 * Create by Leo
 * 基础组件
 * 基础双表格组件
 * 左右两栏式布局
 * 坐侧区域可控制显隐
 * 并减少一定的代码量
 * 统一设置弹框的配置项、确认按钮、取消按钮、关闭按钮等信息
 *
 */

import BaseTable from '@/components/BaseTable';
import API from '@/api/index';
export default {
  components: {
    BaseTable
  },
  props: {
    /**
     * 配置项
     */
    leftTableColumnConfig: {
      type: Array
    },
    /**
     * 配置项
     */
    leftTableData: {
      type: Array
    },
    /**
     * 配置项
     */
    rightTableColumnConfig: {
      type: Array
    },
    /**
     * 配置项
     */
    rightTableData: {
      type: Array
    },
    /**
     * 配置项
     */
    ruleForm: {
      type: Object
    }
  },
  data() {
    return {};
  },
  methods: {
    /**
     * 删除行
     * @param type
     * @param scope
     */
    deleteRow(type, scope) {
      this[`${type}TableData`].splice(scope.row.$index, 1);
    },
    /**
     * 获取字段
     * @param type
     */
    getField(type) {
      let params = {
        nodeId: this.$store.state.nodeId,
        stepName: '',
        before: true
      };
      if (type === 'left') {
        params.stepName = this.ruleForm.step1;
      } else {
        params.stepName = this.ruleForm.step2;
      }
      API.getIputOutputFieldsName(params).then(data => {
        let tableData = [];
        data.data.forEach(dataItem => {
          let addRowData = null;
          if (type === 'left') {
            addRowData = { [this.leftTableColumnConfig[0].prop]: dataItem.name };
          } else {
            addRowData = { [this.rightTableColumnConfig[0].prop]: dataItem.name };
          }
          tableData.push(addRowData);
        });
        this.ruleForm[`${type}TableData`] = tableData;
      });
    },
    /**
     * 添加行
     * @param type
     */
    addRow(type) {
      let addRowData = {};
      this[`${type}TableColumnConfig`].forEach(item => {
        addRowData[item.prop] = '';
      });
      this[`${type}TableData`].push(addRowData);
    }
  }
};
</script>
<style lang="scss" scoped>
.double-table-content {
  display: flex;
  justify-content: space-around;
  .double-table-content-item {
    width: calc(50% - 14px);
    border: 1px dotted;
    padding: 5px;
    .double-table-content-btn {
      margin-bottom: 5px;
    }
    .el-table__body-wrapper {
      height: 100%;
    }
  }
}
</style>
