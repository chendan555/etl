<template>
  <div class="tableList">
    <base-table-frame>
      <div class="btn" slot="filter">
        <el-button
          size="mini"
          type="primary"
          icon="el-icon-receiving"
          @click="showBatchConfigDialog"
        >批量引接</el-button>
        <!-- <el-button size="mini" icon="el-icon-receiving" @click="batchYj">批量引接</el-button> -->
        <div class="rightdiv">
          <!-- <filter-form
            :filterFormConfig="filterFormConfig"
            @search="getListdate"
            @reset="resetFilter"
          ></filter-form>-->
        </div>
      </div>
      <base-table
        slot="table"
        :index="true"
        :isShowCheckBox="true"
        :isShowOperation="true"
        :operationWidth="'100'"
        :tableColumnConfig="tableColumnConfig"
        :tableData="tableData"
        @selectionChange="selectionChange"
      >
        <div slot="operation" slot-scope="{row}">
          <!-- <el-button
            type="text"
            icon="el-icon-receiving"
            @click="yjTable(row)"
            style="color:#fefa8e"
          >引接</el-button>-->
          <el-tooltip class="item" effect="dark" content="引接" placement="top">
            <p style="color:#409eff;cursor: pointer;" @click="yjTable(row)">
              <!-- <i class="el-icon-receiving"></i> -->
              引接
            </p>
          </el-tooltip>
        </div>
      </base-table>
      <BasePagination slot="page" :data="page" @current="currentPage" @sizes="pageSizes"></BasePagination>
    </base-table-frame>

    <base-dialog :dialogConfig="dialogConfig" @closeDialog="closeDialog" @save="saveDialog">
      <batch-config ref="batchConfig" v-if="dialogConfig.visible" @submitBatch="submitBatch"></batch-config>
    </base-dialog>
  </div>
</template>

<script>
import BaseTable from '@/components/BaseTable/index.vue';
import BasePagination from '@/components/BasePagination';
import BaseTableFrame from '@/components/BaseTableFrame/index.vue';
import BaseDialog from '@/components/BaseDialog/index.vue';
import FilterForm from '@/components/FilterForm/index.vue';
import BatchConfig from './BatchConfig.vue';
import API from '@/api/index';
import { sjkTableConfig } from '../config';
import utils from '../../../utils';
export default {
  name: 'sjkTable',
  components: { BaseTable, BasePagination, BaseTableFrame, BaseDialog, BatchConfig, FilterForm },
  props: ['activeTab'],
  data() {
    return {
      Tableheight: document.body.clientHeight - 180,
      tableData: [], //表格的数据
      tableColumnConfig: sjkTableConfig.tableColumnConfig, //表格的头部
      page: {
        pageSize: 20,
        pageNum: 1,
        sizes: [10, 20, 30, 40, 50, 100],
        total: 0
      }, //页码的配置
      dataTable: {}, //兄弟组件传递过来的数据
      selections: [],
      dialogConfig: {
        visible: false,
        title: '数据表引接',
        currentComp: ''
      },
      // filterFormConfig: {
      //   ruleForm: {
      //     ddType: ''
      //   },
      //   formItemConfig: [
      //     {
      //       label: '型号',
      //       valueKey: 'ddType',
      //       type: 'select',
      //       labelWidth: '80px',
      //       options: 'getTypeInputHis',
      //       optionsConfig: {
      //         name: 'keyword',
      //         value: 'keyword'
      //       },
      //       refresh: false
      //     }
      //   ]
      // },
      batchNumber: '',
      isNewBatchNum: 0
    };
  },
  mounted() {
    this.getList();
  },
  methods: {
    showDialog() {
      this.dialogConfig.visible = true;
    },
    closeDialog() {
      this.dialogConfig.visible = false;
      this.dialogConfig.title = '';
      this.dialogConfig.currentComp = '';
    },
    // 弹框内容保存提交，弹框组件项保存方法统一使用saveDialog
    saveDialog() {
      new Promise((resolve, reject) => {
        this.$refs[this.dialogConfig.currentComp].saveDialog(resolve);
      })
        .then(() => {
          this.closeDialog();
        })
        .catch(() => {});
    },
    // 展示批次配置弹框
    showBatchConfigDialog() {
      if (this.selections.length) {
        this.dialogConfig.title = '设置引接批次';
        this.dialogConfig.currentComp = 'batchConfig';
        this.showDialog();
      } else {
        this.$utils.setMessage(false, '请选择要引接的数据表！');
      }
    },
    // 返回批次数据，执行引接
    submitBatch(batch, isNewBatchNum) {
      this.batchNumber = batch;
      this.isNewBatchNum = isNewBatchNum;
      this.$nextTick(() => {
        this.batchYj();
      });
    },
    batchYj() {
      this.selections.forEach(ele => {
        ele.batchNum = this.batchNumber;
        ele.isNewBatchNum = this.isNewBatchNum;
        ele.mainTable = ele.tableName;
        ele.yjMappingId = ele.id;
      });
      console.log(this.selections);
      this.dataReceive(this.selections);
    },
    yjTable({ row }) {
      this.selections = [];
      this.selections.push(row);

      // this.batchYj();
      this.showBatchConfigDialog();
    },
    dataReceive(params) {
      const loading = this.$utils.setLoading('引接中...');
      API.dataReceive.receiveExec(params).then(({ data }) => {
        this.$utils.setMessage(data.data.success, data.data.result);
        this.getList();
      }).finally(() => {
        loading.close();
      });
    },
    // 查询
    getListdate() {
      this.page.pageNum = 1;
      this.getList();
    },
    resetFilter() {
      // this.filterFormConfig.ruleForm.ddType = '';
      this.getListdate();
    },
    // 多选
    selectionChange(data) {
      this.selections = [...data];
    },
    //查询表格的数据
    getList() {
      const loading = this.$utils.setLoading('查询中...');
      const { pageSize, pageNum } = this.page;
      let data = { pageSize, pageIndex: pageNum, searchData: {} };
      API.dataReceive
        .getTableRelationList(data)
        .then(({ data }) => {
          this.tableData = data.data.records;
          this.page.total = data.data.total;
        })
        .finally(() => {
          loading.close();
          // let ddType = this.filterFormConfig.formItemConfig.find(ele => ele.valueKey === 'ddType');
          // ddType.refresh = true;
          // this.$nextTick(() => {
          //   ddType.refresh = false;
          // });
        });
    },
    currentPage(e) {
      this.page.pageNum = e;
      this.getList();
    },
    pageSizes(e) {
      this.page.pageSize = e;
      this.getList();
    }
  }
};
</script>

<style lang="scss" scoped>
.tableList {
  height: 100%;
  // padding: 5px;
  .btn {
    width: 100%;
    // margin-bottom: 5px;
    display: flex;
    justify-content: space-between;
  }
  .tableBox {
    height: calc(100% - 70px);
  }
  .pageSize {
    width: 100%;
    height: 40px;
    margin: 0 5px;
    // position: fixed;
    // bottom: 0;
  }
}
</style>

