<template>
  <div class="receiveRecords">
    <base-table-frame>
      <div class="filterdiv" slot="filter">
        <div class="rightdiv">
          <filter-form
            :filterFormConfig="filterFormConfig"
            @search="filterTableData"
            @reset="resetFilter"
          ></filter-form>
        </div>
      </div>
      <div class="tablediv" slot="table" v-loading="loading">
        <base-table
          :tableColumnConfig="tableConfig.tableColumnConfig"
          :tableData="tableConfig.tableData"
          :isShowOperation="tableConfig.isShowOperation"
          :operationWidth="'100'"
        >
          <div slot="operation" slot-scope="{row}">
            <el-tooltip class="item" effect="dark" content="查看" placement="top">
              <el-button
                type="text"
                @click="handleView(row)"
                style="color:#409eff"
              >查看</el-button>
                <!-- icon="el-icon-view" -->
            </el-tooltip>
            <el-tooltip class="item" effect="dark" content="删除" placement="top">
              <el-button
                type="text"
                @click="removeRecordData(row)"
                style="color:#ff0000"
              >删除</el-button>
                <!-- icon="el-icon-delete" -->
            </el-tooltip>
          </div>
        </base-table>
      </div>
      <div class="pagediv" slot="page">
        <base-pagination :data="pageConfig" @current="changePageNum" @sizes="changePageSize"></base-pagination>
      </div>
    </base-table-frame>
    <!-- 弹窗 -->
    <tableUpdate v-if="showTable" ref="add" @close="showTable=false" :rowDate="rowDate"></tableUpdate>

    <DialogReceiveData
      v-if="dialogConfig.visible"
      :dialogConfig="dialogConfig"
      :tableConfigProp="dataTableConfig"
      @closeDialog="closeDialog"
      @showCreateJsonData="showCreateJsonData"
      @pagenum="pagenum"
      @pagesize="pagesize"
    ></DialogReceiveData>

    <build-replay-data
      v-if="replayDataDialogConfig.visible"
      :dialogConfig="replayDataDialogConfig"
      :crtTableData="crtTableData"
      :crtFileRecord="crtFileRecord"
      @closeDialog="closeRdDialog"
    ></build-replay-data>
  </div>
</template>
<script lang="ts">
import Vue from 'vue';
import BaseTableFrame from '@/components/BaseTableFrame/index.vue';
import BaseTable from '@/components/BaseTable/index.vue';
import BasePagination from '@/components/BasePagination/index.vue';
import FilterForm from '@/components/FilterForm/index.vue';
import tableUpdate from './children/tableUpdate.vue';
import DialogReceiveData from './children/DialogReceiveData.vue';
import BuildReplayData from './children/BuildReplayData.vue';
import config from './config';
import axios from '@/api/base/http';
import API from '@/api/index';
export default Vue.extend({
  name: 'ReceiveRecords',
  components: { BaseTableFrame, BaseTable, BasePagination, tableUpdate, DialogReceiveData, BuildReplayData, FilterForm },
  meta: {
    pageIndex: 30,
    title: '引接记录查询',
    pageType: 'mainNav',
    belongTo: 'receive',
    hidden: true
  },
  data() {
    return {
      filterForm: {
        tableName: ''
      },
      filterFormConfig: {
        ruleForm: {
          ddType: '',
          tableName: ''
        },
        formItemConfig: [
          // {
          //   span: 12,
          //   label: '型号',
          //   valueKey: 'ddType',
          //   type: 'select',
          //   labelWidth: '80px',
          //   options: 'getTypeInputHis',
          //   optionsConfig: {
          //     name: 'keyword',
          //     value: 'keyword'
          //   },
          //   refresh: false
          // },
          {
            span: 24,
            label: '引接主体',
            valueKey: 'tableName',
            type: 'input',
            labelWidth: '80px'
          }
        ]
      },
      tableConfig: {
        isShowOperation: true,
        tableColumnConfig: config.tableColumnConfig_records,
        tableData: []
      },
      // handleList: config.handleList_records,
      pageConfig: {
        pageSize: 20,
        pageNum: 1,
        total: 0
      },
      pageTable: {
        pageSize: 20,
        pageNum: 1
      },
      showTable: false,
      rowDate: <any>{},
      loading: false,
      dialogConfig: {
        width: '80%',
        title: '',
        visible: false
      },
      dataTableConfig: {
        tableColumnConfig: <any>[],
        tableData: [],
        isShowOperation: true,
        size: 0,
        row: {}
      },
      crtFileRecord: <any>{},
      crtTableData: <any>{},
      replayDataDialogConfig: {
        visible: false,
        title: '配置复盘数据'
      }
    };
  },
  created() {
    this.getMainTableList();
  },
  methods: {
    closeRdDialog() {
      this.replayDataDialogConfig.visible = false;
    },
    showCreateJsonData(row: any) {
      this.crtTableData = row;
      this.buildReplayData();
    },
    buildReplayData() {
      this.replayDataDialogConfig.visible = true;
    },
    getMainTableList() {
      this.loading = true;
      let params = {
        pageIndex: this.pageConfig.pageNum,
        pageSize: this.pageConfig.pageSize,
        searchData: {
          tableName: this.filterFormConfig.ruleForm.tableName,
          // ddType: this.filterFormConfig.ruleForm.ddType
        }
      };
      API.dataReceive
        .yjRecordData(params)
        .then(({ data }: any) => {
          if (data.code == 200) {
            this.loading = false;
            this.tableConfig.tableData = data.data.records;
            this.pageConfig.total = data.data.total;
            this.pageConfig.pageNum = data.data.current;
          }
        })
        .finally(() => {
          this.loading = false;
          /* let ddType: any = this.filterFormConfig.formItemConfig.find(ele => ele.valueKey === 'ddType');
          ddType.refresh = true;
          this.$nextTick(() => {
            ddType.refresh = false;
          }); */
        });
    },
    filterTableData() {
      this.pageConfig.pageNum = 1;
      this.getMainTableList();
    },
    resetFilter() {
      this.filterFormConfig.ruleForm.tableName = '';
      // this.filterFormConfig.ruleForm.ddType = '';
      this.pageConfig.pageNum = 1;
      this.getMainTableList();
    },
    // 分页切换
    changePageNum(num: number) {
      this.pageConfig.pageNum = num;
      this.getMainTableList();
    },
    changePageSize(size: number) {
      this.pageConfig.pageNum = 1;
      this.pageConfig.pageSize = size;
      this.getMainTableList();
    },
    handleView({ row }: any) {
      this.crtFileRecord = row;
      switch (row.receiveType) {
        case '1':
          this.dialogConfig.title = '数据表引接表查看';
          break;
        case '2':
          this.dialogConfig.title = '文件引接表查看';
          break;
        case '3':
          this.dialogConfig.title = '仿真引接表查看';
          this.getFzDataByNodeId(row);
          break;
        case '4':
          this.dialogConfig.title = '表单引接表查看';
          break;
      }
      this.getResultData(row);
    },
    getFzDataByNodeId(row: any) {
      console.log(row);
      const params = {
        taskNodeId: '',
        imitateName: row.toTable
      };
      this.$API.getFzDataByNodeId(params).then(({ data }: any) => {
        console.log(data);
        this.dataTableConfig.tableColumnConfig = Object.keys(data.data[0]).map((key: string) => {
          return {
            label: key,
            prop: key
          };
        });
        this.dataTableConfig.tableData = data.data;
        this.dialogConfig.visible = true;
      });
    },
    removeRecordData({ row }: any) {
      console.log(row);
      this.$utils.confirm().then(() => {
        const loading = this.$utils.setLoading('删除中...');
        const params = {
          id: row.id
        };
        API.dataReceive
          .removeRecordData(params)
          .then(({ data }: any) => {
            this.$utils.setMessage(data.success, data.result);
            if (data.success) {
              this.getMainTableList();
            }
          })
          .finally(() => {
            loading.close();
          });
      });
    },
    // 第一层弹窗的数据的分页
    pagesize(param: any) {
      this.pageTable.pageSize = param.pageSize;
      this.getResultData(param.row);
    },
    pagenum(param: any) {
      this.pageTable.pageNum = param.pageNum;
      this.getResultData(param.row);
    },
    getResultData(row: any) {
      this.dataTableConfig.row = row;
      let params = {
        pageIndex: this.pageTable.pageNum,
        pageSize: this.pageTable.pageSize,
        tableName: row.toTable,
        connection: row.toDataSourceName,
        schema: row.toSchema
      };
      (this as any).$API
        .getTableDataed(params)
        .then(({ data }: any) => {
          if (data.code === 200) {
            if (data.data.column) {
              this.dataTableConfig.tableColumnConfig = data.data.column.map((ele: any) => {
                return {
                  label: ele.columnName,
                  prop: ele.columnName
                };
              });
              console.log(data.data.page);
              this.dataTableConfig.tableData = data.data.page.records;
              this.dataTableConfig.size = data.data.page.total;
              this.dialogConfig.visible = true;
            } else {
              this.$message.warning('未查询到数据信息！');
            }
          }
        })
        .catch((this.dataTableConfig.tableData = []));
    },
    getEmulateData(row: any) {
      let params = {
        pageIndex: this.pageTable.pageNum,
        pageSize: this.pageTable.pageSize,
        searchData: {
          receiveType: row.receiveType,
          ipInterfaceName: row.ipInterfaceName,
          tableName: row.mainBody
        }
      };
      // 传递的参数在仿真的时候暂时写死
      if (row.receiveType == '3') {
        params.searchData.ipInterfaceName = '仿真外部服务器';
      } else if (row.receiveType == '1') {
        params.searchData.tableName = row.mainBody;
      } else if (row.receiveType == '2') {
        let params = {
          pageIndex: this.pageTable.pageNum,
          pageSize: this.pageTable.pageSize,
          tableName: row.toTable,
          connection: row.toDataSourceName,
          schema: row.toSchema
        };
        (this as any).$API
          .getTableDataed(params)
          .then(({ data }: any) => {
            if (data.code === 200) {
              if (data.data.column) {
                this.dataTableConfig.tableColumnConfig = data.data.column.map((ele: any) => {
                  return {
                    label: ele.columnName,
                    prop: ele.columnName
                  };
                });
                this.dataTableConfig.tableData = data.data.page.records;
                this.dataTableConfig.size = data.data.total;
                this.dialogConfig.visible = true;
              } else {
                this.$message.warning('未查询到数据信息！');
              }
            }
          })
          .catch((this.dataTableConfig.tableData = []));
        // API.dataReceive.getFileReceiveData({ historyId: row.id }).then(({ data }: any) => {
        //   console.log(data);

        // });
        return;
      }
      // 判断数据库表弹窗的按钮显示
      // if (row.receiveType == 1) {
      //   this.dataTableConfig.isShowOperation = false;
      // } else {
      // }
      this.dataTableConfig.isShowOperation = false;
      // 弹窗需要给父组件传的参数
      this.dataTableConfig.row = row;

      API.dataReceive.seeYjData(params).then(({ data }: any) => {
        if (data.code == 200) {
          let keysList: any = [];
          let columnsList: any = [];
          if (row.receiveType == 1) {
            data.data.tableHeader.forEach((ele: any) => {
              columnsList.push({
                label: ele.columnKey,
                prop: ele.columnKey
              });
            });
            this.dataTableConfig.tableColumnConfig = columnsList;
            this.dataTableConfig.tableData = data.data.tableBody.records;
          } else {
            data.data.records.forEach((ele: any) => {
              Object.keys(ele).forEach((key: any) => {
                if (!keysList.includes(key)) {
                  keysList.push(key);
                  columnsList.push({
                    label: key,
                    prop: key
                  });
                }
              });
            });
            this.dataTableConfig.tableColumnConfig = columnsList;
            this.dataTableConfig.tableData = data.data.records;
          }
          this.dataTableConfig.size = data.data.total;
          this.dialogConfig.visible = true;
        }
      });
    },
    closeDialog(row?: any) {
      this.dialogConfig.visible = false;
      if (row) {
        this.buildReplayData();
      }
    }
  }
});
</script>
<style lang="scss" scoped>
@import '@/styles/theme.scss';
.receiveRecords {
  width: 100%;
  height: 100%;
  background: $contBg;
  border-radius: 6px;
  padding: 5px;
  .filterdiv {
    width: 100%;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: flex-end;
    .rightdiv {
      width: 600px;
    }
  }
  .pagediv {
    width: 100%;
    height: 40px;
  }
  .tablediv {
    width: 100%;
    height: 100%;
  }
}
</style>
