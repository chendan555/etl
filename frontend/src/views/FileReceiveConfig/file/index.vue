<template>
  <div class="file">
    <base-table-frame>
      <div class="filterdiv" slot="filter">
        <div class="leftdiv">
          <el-button
            size="mini"
            type="primary"
            icon="el-icon-circle-plus-outline"
            @click="addListData"
          >新增</el-button>
        </div>
        <div class="rightdiv">
          <filter-form
            :filterFormConfig="filterFormConfig"
            @search="getListData"
            @reset="resetFilter"
          ></filter-form>
        </div>
      </div>
      <base-table slot="table" v-bind="{...tableConfig}">
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
      <base-pagination
        slot="page"
        :data="pageConfig"
        @current="changePageNum"
        @sizes="changePageSize"
      ></base-pagination>
    </base-table-frame>

    <edit-file
      v-bind="{...dialogConfig}"
      v-if="dialogConfig.dialogConfig.visible"
      @closeDialog="closeEidtFileDialog"
    ></edit-file>

    <!-- txt文件合并 -->
    <base-dialog :dialogConfig="baseDialogConfig" @closeDialog="closeDialog" @save="saveDialog">
      <merge-txt
        ref="mergeTxt"
        :ruleForm="baseDialogConfig.data.ruleForm"
        @closeDialog="closeDialog"
      ></merge-txt>
    </base-dialog>
  </div>
</template>
<script lang="ts">
import Vue from 'vue';
import BaseTableFrame from '@/components/BaseTableFrame/index.vue';
import BaseTable from '@/components/BaseTable/index.vue';
import BasePagination from '@/components/BasePagination/index.vue';
import BaseDialog from '@/components/BaseDialog/index.vue';
import FilterForm from '@/components/FilterForm/index.vue';
import EditFile from './EditFile.vue';
import MergeTxt from './MergeTxt.vue';
import API from '@/api/index';
import config from './config';
export default Vue.extend({
  name: 'file',
  components: { BaseTableFrame, BaseTable, BasePagination, EditFile, BaseDialog, MergeTxt, FilterForm },
  data() {
    return {
      tableConfig: {
        index: true,
        isShowOperation: true,
        operationWidth: '100',
        tableColumnConfig: config.tableColumnConfig,
        tableData: []
      },
      pageConfig: {
        pageSize: 20,
        pageNum: 1,
        total: 0
      },
      handleList: config.handleConfig,
      dialogConfig: {
        fileInfo: null,
        dialogConfig: {
          // icon: 'connection',
          title: '文件解析配置',
          visible: false
        }
      },
      baseDialogConfig: {
        visible: false,
        title: '合并TXT',
        data: {
          ruleForm: this.$utils.deepCopy(config.mergeTxtFormConfig.ruleForm)
        }
      },
      filterFormConfig: {
        ruleForm: {
          configName: ''
        },
        formItemConfig: [
          {
            span: 24,
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
    this.getListData();
  },
  methods: {
    // 重置搜索
    resetFilter() {
      this.filterFormConfig.ruleForm.configName = '';
      // this.filterFormConfig.ruleForm.ddType = '';
      this.pageConfig.pageNum = 1;
      this.getListData();
    },
    // 展示多TXT文件合并弹框
    mergeTXT() {
      this.baseDialogConfig.visible = true;
    },
    // 展示多TXT文件合并弹框
    closeDialog() {
      this.baseDialogConfig.visible = false;
      this.baseDialogConfig.data.ruleForm = this.$utils.deepCopy(config.mergeTxtFormConfig.ruleForm);
    },
    // 执行多TXT文件合并
    saveDialog() {
      // console.log(this.baseDialogConfig.data.ruleForm);
      // (this as any).$API.configRelation
      //   .mergeTxt(this.baseDialogConfig.data.ruleForm)
      //   .then(({ data }: any) => {
      //     console.log(data);
      //   })
      //   .finally(() => {
      //     this.closeDialog();
      //   });
      (this.$refs.mergeTxt as any).submit();
    },
    showEidtFileDialog() {
      this.dialogConfig.dialogConfig.visible = true;
    },
    closeEidtFileDialog(bool?: boolean) {
      this.dialogConfig.dialogConfig.visible = false;
      if (bool) this.getListData();
    },
    addListData() {
      this.dialogConfig.fileInfo = null;
      this.showEidtFileDialog();
    },
    getListData() {
      const { pageSize, pageNum } = this.pageConfig;
      let params = {
        pageSize,
        pageIndex: pageNum,
        searchData: {
          configName: this.filterFormConfig.ruleForm.configName
          // ddType: this.filterFormConfig.ruleForm.ddType
        }
      };
      (this as any).$API.configRelation
        .getFileList(params)
        .then(({ data }: any) => {
          this.tableConfig.tableData = data.data.records;
          this.pageConfig.total = data.data.total;
        })
        .finally(() => {
          /* let ddType: any = this.filterFormConfig.formItemConfig.find(ele => ele.valueKey === 'ddType');
          ddType.refresh = true;
          this.$nextTick(() => {
            ddType.refresh = false;
          }); */
        });
    },
    // 分页切换
    changePageNum(num: number) {
      this.pageConfig.pageNum = num;
      this.getListData();
    },
    changePageSize(size: number) {
      this.pageConfig.pageNum = 1;
      this.pageConfig.pageSize = size;
      this.getListData();
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
    editItem({ row }: any) {
      this.dialogConfig.fileInfo = row;
      this.showEidtFileDialog();
    },
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
          API.configRelation.deleteFileRelation(data).then(({ data }: any) => {
            this.$message.success(data.result);
            this.getListData();
          });
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          });
        });
    }
  }
});
</script>
<style lang="scss" scoped>
.file {
  width: 100%;
  height: 100%;
  // padding: 0px 5px;
  .filterdiv {
    width: 100%;
    display: flex;
    justify-content: space-between;
    .rightdiv {
      width: 600px;
    }
  }
}
</style>
