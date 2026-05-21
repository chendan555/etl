<template>
  <div class="resolveProtocol">
    <div class="filterdiv">
      <div class="leftdiv">
        <el-button
          size="mini"
          type="primary"
          icon="el-icon-circle-plus-outline"
          @click="newTableRelation"
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
    <div class="tablediv">
      <base-table v-bind="{...tableConfig}">
        <div class="operation" slot="operation" slot-scope="{row}">
          <!-- <operation-button
            v-for="handle in handleList"
            :key="handle.handle"
            :buttonInfo="handle"
            @handleFunc="handleFunc(row, handle.handle)"
          ></operation-button> -->
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
    <div class="pagediv">
      <base-pagination :data="pageConfig" @current="changePageNum" @sizes="changePageSize"></base-pagination>
    </div>

    <config-table
      v-bind="{...tableRelationConfig}"
      v-if="tableRelationConfig.dialogConfig.visible"
      @closeDialog="closeTreeRelation"
      @showConfigKey="showConfigKey"
    ></config-table>
    <config-key :dialogConfig="keyDialog" v-if="keyDialog.visible" @closeDialog="closeConfigKey"></config-key>
  </div>
</template>
<script lang="ts">
import Vue from 'vue';
import ConfigTable from './ConfigTable.vue';
import ConfigKey from './ConfigKey.vue';
import BaseTable from '@/components/BaseTable/index.vue';
import BasePagination from '@/components/BasePagination/index.vue';
import OperationButton from '@/components/OperationButton/index.vue';
import FilterForm from '@/components/FilterForm/index.vue';
import config from './config';
import API from '@/api/index';
export default Vue.extend({
  name: 'TableRelation',
  meta: {
    // pageIndex: 50,
    // title: '主子表关系',
    // pageType: 'mainNav',
    // belongTo: 'receive'
  },
  props: ['recordParam'],
  components: { BaseTable, BasePagination, ConfigTable, OperationButton, ConfigKey, FilterForm },
  data() {
    return {
      tableRelationConfig: {
        relationInfo: null,
        dialogConfig: {
          width: '60%',
          // icon: 'connection',
          title: '主子表关系配置',
          visible: false
        }
      },
      keyDialog: {
        width: '80%',
        icon: 'connection',
        title: '配置字段名称',
        visible: false
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
          configName: ''
        },
        formItemConfig: [
          {
            label: '名称',
            valueKey: 'configName',
            type: 'input',
            labelWidth: '80px'
          }
        ]
      }
    };
  },
  mounted() {
    this.getTableRelationList();
  },
  methods: {
    // 重置搜索
    resetFilter() {
      this.filterFormConfig.ruleForm.configName = '';
      this.pageConfig.pageNum = 1;
      this.getTableRelationList();
    },
    // 获取表关系列表
    getTableRelationList() {
      // console.log(this.recordParam, '传递的参数，目前没定和后端定参数的类型');
      const { pageSize, pageNum } = this.pageConfig;
      let data = {
        pageSize,
        pageIndex: pageNum,
        searchData: {
          configName: this.filterFormConfig.ruleForm.configName
        }
      };
      API.tableRelation.getTableRelationList(data).then(({ data }: any) => {
        this.tableConfig.tableData = data.data.records;
        this.pageConfig.total = data.data.total;
      });
    },
    newTableRelation() {
      this.tableRelationConfig.relationInfo = null;
      this.addTableRelation();
    },
    // 显示表关系编辑弹框
    addTableRelation() {
      this.tableRelationConfig.dialogConfig.visible = true;
    },
    // 关闭表关系编辑弹框
    closeTreeRelation(bool?: boolean) {
      this.tableRelationConfig.dialogConfig.visible = false;
      if (bool) {
        this.getTableRelationList();
      }
    },
    showConfigKey() {
      this.keyDialog.visible = true;
    },
    closeConfigKey() {
      this.keyDialog.visible = false;
    },
    handleFunc(row: any, handle: string) {
      switch (handle) {
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
      this.$confirm('此操作将永久删除该协议, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          (this as any).$API.tableRelation.deleteTableRelation(row.id.toString()).then(({ data }: any) => {
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
.resolveProtocol {
  width: 100%;
  height: 100%;
  // padding: 5px 10px;
  .filterdiv,
  .pagediv {
    width: 100%;
    height: 40px;
    display: flex;
    align-items: center;
  }
  .tablediv {
    width: 100%;
    height: calc(100% - 80px);
    overflow: auto;
    .operation {
      display: flex;
      justify-content: space-around;
      ::v-deep .el-button {
        color: #fff;
      }
    }
  }
  .filterdiv {
    justify-content: space-between;
    .rightdiv {
      width: 400px;
    }
  }
}
</style>
