<template>
  <base-dialog v-bind="$attrs" v-on="$listeners" @save="saveConfig">
    <div class="dataList">
      <div class="tableDiv" v-loading="loading">
        <base-table
          :tableColumnConfig="tableConfig.tableColumnConfig"
          :tableData="tableConfig.tableData"
        ></base-table>
      </div>
      <div class="pageDiv">
        <base-pagination :data="pageConfig" @current="changePageNum" @sizes="changePageSize"></base-pagination>
      </div>
    </div>
  </base-dialog>
</template>
<script>
import BaseDialog from '@/components/BaseDialog/index.vue';
import BaseTable from '@/components/BaseTable/index.vue';
import BasePagination from '@/components/BasePagination/index.vue';
export default {
  name: 'dataList',
  components: { BaseDialog, BaseTable, BasePagination },
  props: {
    recordObj: {
      type: Object
    },
    recordType: {
      type: Number
    }
  },
  data() {
    return {
      tableConfig: {
        tableColumnConfig: [],
        tableData: []
      },
      loading: false,
      pageConfig: {
        pageNum: 1,
        pageSize: 10,
        total: 0
      }
    };
  },
  mounted() {
    this.getDataList();
  },
  methods: {
    getDataList() {
      if (this.recordType == 5) {
        this.getProcessData();
      } else {
        this.getReceiveList();
      }
    },
    /**
     * @description 获取处理流程数据
     */
    getProcessData() {
      this.loading = true;
      const params = {
        pageIndex: this.pageConfig.pageNum,
        pageSize: this.pageConfig.pageSize,
        transId: this.recordObj.transId,
        path: this.recordObj.path,
        nodeId: this.recordObj.nodeId,
        batchNum: this.recordObj.batchNum
      };
      this.$axios
        .get('api/trans/plan/yulan', { params })
        .then(({ data }) => {
          console.log(data);
          this.tableConfig.tableData = data.firstRecords;
          this.tableConfig.tableColumnConfig = data.columnsList.map(item => {
            return {
              label: `${item.columnName}${item.comments ? '/' + item.comments : ''}`,
              prop: item.columnName
            };
          });
          this.pageConfig.total = data.total;
        })
        .finally(() => {
          this.loading = false;
        });
    },
    /**
     * @description 获取数据
     */
    getReceiveList() {
      const params = {
        pageIndex: this.pageConfig.pageNum,
        pageSize: this.pageConfig.pageSize,
        tableName: this.recordObj.toTable,
        connection: this.recordObj.toDataSourceName,
        schema: this.recordObj.toSchema,
        batchNum: this.recordObj.batch
      };
      let newConfig = this.$utils.transferToFormData(params);
      this.$axios.post('api/database/yj/getTableDatas', newConfig.formData, newConfig.config).then(({ data }) => {
        console.log(data);
        if (data.success) {
          this.tableConfig.tableData = data.data.page.records;
          this.tableConfig.tableColumnConfig = data.data.column.map(item => {
            return {
              label: `${item.columnName}${item.comments ? '/' + item.comments : ''}`,
              prop: item.columnName
            };
          });
          this.pageConfig.total = data.data.page.total;
        }
      });
    },
    /**
     * @description 分页切换
     * @param num
     */
    changePageNum(num) {
      this.pageConfig.pageNum = num;
      this.getDataList();
    },
    /**
     * @description 分页切换
     * @param size
     */
    changePageSize(size) {
      this.pageConfig.pageNum = 1;
      this.pageConfig.pageSize = size;
      this.getDataList();
    },
    /**
     * @description 关闭
     */
    saveConfig() {
      this.$emit('closeDialog');
    }
  }
};
</script>

<style lang="scss" scoped>
.dataList {
  width: 100%;
  height: calc(100% - 40px);
  .tableDiv {
    width: 100%;
    height: calc(100% - 40px);
  }
  .pageDiv {
    width: 100%;
    height: 40px;
    display: flex;
    align-items: center;
  }
}
</style>
