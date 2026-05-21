<template>
  <div class="database">
    <base-table-frame>
      <div class="filterdiv" slot="filter">
        <div class="leftdiv">
          <el-button
            type="primary"
            size="mini"
            icon="el-icon-circle-plus-outline"
            @click="addTableRelation"
          >新增</el-button>
        </div>
        <div class="rightdiv">
          <filter-form
            :filterFormConfig="filterFormConfig"
            @search="getTableRelationList"
            @reset="resetFilter"
          ></filter-form>
        </div>
      </div>
      <div class="tablediv" slot="table">
        <base-table v-bind="{...tableConfig}">
          <div slot="operation" slot-scope="{row}">
            <el-tooltip
              :content="handle.name"
              placement="top"
              effect="dark"
              v-for="handle in handleList"
              :key="handle.handle"
            >
              <el-button
                type="text"
                :style="{color: handle.color}"
                @click="handleFunc(row, handle.handle)"
              >{{handle.name}}</el-button>
            </el-tooltip>
          </div>
        </base-table>
      </div>
      <div class="pagediv" slot="page">
        <base-pagination :data="pageConfig" @current="changePageNum" @sizes="changePageSize"></base-pagination>
      </div>
    </base-table-frame>

    <config-table
      v-bind="{...tableRelationConfig}"
      v-if="tableRelationConfig.dialogConfig.visible"
      @closeDialog="closeTreeRelation"
    ></config-table>
  </div>
</template>
<script lang="ts">
import Vue from 'vue';
import ConfigTable from './ConfigTable.vue';
import BaseTable from '@/components/BaseTable/index.vue';
import FilterForm from '@/components/FilterForm/index.vue';
import BaseTableFrame from '@/components/BaseTableFrame/index.vue';
import BasePagination from '@/components/BasePagination/index.vue';
import config from './config';
import axios from '@/api/base/http';
import API from '@/api/index';
export default Vue.extend({
  name: 'database',
  components: { BaseTable, BasePagination, ConfigTable, BaseTableFrame, FilterForm },
  data() {
    return {
      tableRelationConfig: {
        relationInfo: null,
        dialogConfig: {
          // icon: 'connection',
          title: '表关系配置',
          visible: false
        }
      },
      tableConfig: {
        index: true,
        isShowOperation: true,
        operationWidth: '100',
        tableColumnConfig: config.tableColumnConfig,
        tableData: []
      },
      showTreeRelation: false,
      pageConfig: {
        pageSize: 20,
        pageNum: 1,
        total: 0
      },
      handleList: config.handleConfig,
      filterFormConfig: {
        ruleForm: {
          mainTable: ''
          // ddType: ''
        },
        formItemConfig: [
          /*           {
            span: 12,
            label: '型号',
            valueKey: 'ddType',
            type: 'select',
            labelWidth: '80px',
            options: 'getTypeInputHis',
            optionsConfig: {
              name: 'keyword',
              value: 'keyword'
            },
            refresh: false
          }, */
          {
            span: 24,
            label: '主表',
            valueKey: 'mainTable',
            type: 'input',
            labelWidth: '80px'
          }
        ]
      }
    };
  },
  created() {
    this.getTableRelationList();
    setTimeout(() => {
      console.log(this.$store.state.cancelToken);
      // this.$store.state.cancelToken.forEach((ele: any, index: number) => {
      //   if ([1, 4].includes(index)) {
      //     ele.ct();
      //   }
      // });
    }, 2000);
  },
  methods: {
    // 重置搜索
    resetFilter() {
      this.filterFormConfig.ruleForm.mainTable = '';
      // this.filterFormConfig.ruleForm.ddType = '';
      this.pageConfig.pageNum = 1;
      this.getTableRelationList();
    },
    // 获取表关系列表
    getTableRelationList() {
      const { pageSize, pageNum } = this.pageConfig;
      let data = {
        pageSize,
        pageIndex: pageNum,
        searchData: {
          mainTable: this.filterFormConfig.ruleForm.mainTable
          // ddType: this.filterFormConfig.ruleForm.ddType
        }
      };
      API.configRelation
        .getTableRelationList(data)
        .then(({ data }: any) => {
          console.log(data);
          this.tableConfig.tableData = data.data.records;
          this.pageConfig.total = data.data.total;
        })
        .finally(() => {
          /*  let ddType: any = this.filterFormConfig.formItemConfig.find(ele => ele.valueKey === 'ddType');
          ddType.refresh = true;
          this.$nextTick(() => {
            ddType.refresh = false;
          }); */
        });
    },
    // 显示表关系编辑弹框
    addTableRelation() {
      this.tableRelationConfig.dialogConfig.visible = true;
    },
    // 关闭表关系编辑弹框
    closeTreeRelation(bool?: any) {
      this.tableRelationConfig.dialogConfig.visible = false;
      this.tableRelationConfig.relationInfo = null;
      if (bool) {
        this.getTableRelationList();
      }
    },
    handleFunc(row: any, handle: string) {
      switch (handle) {
        case 'receive':
          this.receiveData(row);
          break;
        case 'update':
          this.editItem(row);
          break;
        case 'delete':
          this.deleteItem(row);
          break;
      }
    },
    // 编辑表关系配置项
    editItem({ row }: any) {
      this.tableRelationConfig.relationInfo = row;
      this.addTableRelation();
    },
    // 删除变关系配置项
    deleteItem({ row }: any) {
      this.$confirm('此操作将永久删除该文件, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          let data = {
            ids: row.id
          };
          API.configRelation.deleteTableRelation(data).then(({ data }: any) => {
            this.$message.success(data.result);
            this.getTableRelationList();
          });
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          });
        });
    },
    // 分页切换
    changePageNum(num: number) {
      this.pageConfig.pageNum = num;
      this.getTableRelationList();
    },
    changePageSize(size: number) {
      this.pageConfig.pageNum = 1;
      this.pageConfig.pageSize = size;
      this.getTableRelationList();
    }
  }
});
</script>
<style lang="scss" scoped>
.database {
  width: 100%;
  height: 100%;
  // padding: 0px 5px;
  .filterdiv,
  .pagediv {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
  }
  .tablediv {
    width: 100%;
    height: 100%;
  }
  .filterdiv {
    justify-content: space-between;
    .rightdiv {
      width: 600px;
    }
  }
}
</style>
