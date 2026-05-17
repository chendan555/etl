<template>
  <div class="resovleFormModel">
    <base-table-frame>
      <div class="filter" slot="filter">
        <el-button size="mini" icon="el-icon-circle-plus-outline" @click="addModelData">新增</el-button>
      </div>
      <base-table slot="table" v-bind="{...tableConfig}">
        <div class="operation" slot="operation" slot-scope="{row}">
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
      <base-pagination
        slot="page"
        :data="pageConfig"
        @current="changePageNum"
        @sizes="changePageSize"
      ></base-pagination>
    </base-table-frame>

    <!-- 配置表单解析模板信息 -->
    <edit-form-model
      v-if="formModelDialogConfig.visible"
      :formModelInfo="formModelInfo"
      :dialogConfig="formModelDialogConfig"
      @closeDialog="closeFormModelDialog"
    ></edit-form-model>

    <!-- 配置表单解析模板规则 -->
    <set-form-model
      :formModelInfo="formModelInfo"
      :dialogConfig="setFormModelDialog"
      @closeDialog="closeSetFormModel"
    ></set-form-model>
  </div>
</template>
<script lang="ts">
import Vue from 'vue';
import BaseTable from '@/components/BaseTable/index.vue';
import BaseTableFrame from '@/components/BaseTableFrame/index.vue';
import BasePagination from '@/components/BasePagination/index.vue';
import OperationButton from '@/components/OperationButton/index.vue';
import EditFormModel from './EditFormModel.vue';
import SetFormModel from './SetFormModel.vue';
import API from '@/api';
import config from './config';
export default Vue.extend({
  name: 'resovleFormModel',
  components: { BaseTable, BaseTableFrame, BasePagination, OperationButton, EditFormModel, SetFormModel },
  data() {
    return {
      tableConfig: {
        index: true,
        isShowOperation: true,
        operationWidth: '150',
        tableColumnConfig: config.tableColumnConfig,
        tableData: <any>[]
      },
      handleList: config.handleList,
      pageConfig: {
        pageSize: 20,
        pageNum: 1,
        total: 0
      },
      formModelDialogConfig: {
        title: '编辑解析模板信息',
        visible: false,
        fullscreen: true
      },
      setFormModelDialog: {
        visible: false,
        title: '配置模板',
        fullscreen: true
      },
      formModelInfo: {}
    };
  },
  mounted() {
    this.getFormModelList();
  },
  methods: {
    // 获取解析模板列表
    getFormModelList() {
      const { pageSize, pageNum } = this.pageConfig;
      let params = {
        pageIndex: pageNum,
        pageSize
      };
      API.resolveFormModel.getResolveFormModelList(params).then(({ data }: any) => {
        this.tableConfig.tableData = data.data.records;
        this.pageConfig.total = data.data.total;
      });
    },
    // 分页切换
    changePageNum(num: number) {
      this.pageConfig.pageNum = num;
      this.getFormModelList();
    },
    changePageSize(size: number) {
      this.pageConfig.pageNum = 1;
      this.pageConfig.pageSize = size;
      this.getFormModelList();
    },
    // 新增解析模板
    addModelData() {
      this.formModelDialogConfig.visible = true;
    },
    closeFormModelDialog(bool?: boolean) {
      this.formModelDialogConfig.visible = false;
      bool && this.getFormModelList();
    },
    // 表单操作分发
    handleFunc(row: any, handle: string) {
      switch (handle) {
        case 'setting':
          this.setFormModel(row);
          break;
        case 'edit':
          this.editFormModelInfo(row);
          break;
        case 'delete':
          this.deleteFormModel(row);
          break;
      }
    },
    // 配置表单解析模板
    setFormModel({ row }: any) {
      this.setFormModelDialog.visible = true;
    },
    // 关闭表单解析模板规则配置弹框
    closeSetFormModel() {
      this.setFormModelDialog.visible = false;
    },
    // 编辑表单解析模板信息
    editFormModelInfo({ row }: any) {
      this.formModelInfo = row;
      this.addModelData();
    },
    // 删除表单解析模板信息
    deleteFormModel({ row }: any) {
      this.$utils.confirm().then(() => {
        let params = {
          ids: row.id
        };
        API.resolveFormModel.deleteResolveFormModel(params).then(({ data }: any) => {
          this.$utils.setMessage(data.success, data.result);
          this.getFormModelList();
        });
      });
    }
  }
});
</script>
<style lang="scss" scoped>
.resovleFormModel {
  width: 100%;
  height: 100%;
  // margin: 0 5px;
  .operation {
    display: flex;
    align-items: center;
    justify-content: space-around;
  }
}
</style>
