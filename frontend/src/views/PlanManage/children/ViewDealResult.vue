<template>
  <base-dialog
    :dialogConfig="dialogConfig"
    :showFooter="dialogConfig.showFooter"
    @closeDialog="closeDialog"
  >
    <div class="content">
      <!-- start 方案执行结果列表区域 -->
      <div class="resultList">
        <h3>方案执行结果列表</h3>
        <div class="listdiv">
          <base-table v-bind="{...resultTableConfig}">
            <div class="operation" slot="operation" slot-scope="{row}">
              <!-- 根据方案执行结果类型展示操作按钮 -->
              <div v-if="row.row.outputType===2">
                <el-button size="mini" @click="viewTableData(row)">预览</el-button>
              </div>
              <div v-if="row.row.outputType===1">
                <el-button size="mini" @click="downloadFile(row)">下载</el-button>
              </div>
            </div>
          </base-table>
        </div>
      </div>
      <!-- end -->
      <!-- start 方案执行结果数据展示区域 -->
      <div class="resultDataList" v-show="showResultData">
        <h3>数据表结果查看</h3>
        <div class="listdiv">
          <base-table v-bind="{...resultDataTableConfig}"></base-table>
        </div>
        <base-pagination :data="pageConfig" @current="currentPage" @sizes="pageSizes"></base-pagination>
      </div>
      <!-- end -->
    </div>
  </base-dialog>
</template>
<script>
/**
 * Create By Leo 2022/10/20
 * 用于方案执行后的结果查询和查看
 * 执行结果包括表输出及文件输出
 * 以弹框形式在方案列表也展示
 */
import BaseDialog from '@/components/BaseDialog/index.vue';
import BaseTable from '@/components/BaseTable/index.vue';
import BasePagination from '@/components/BasePagination/index.vue';
import { viewDealResultConfig } from './config.js';
export default {
  name: 'viewDealResult',
  components: {
    BaseDialog,
    BaseTable,
    BasePagination
  },
  props: {
    /**
     * 父组件传递方案ID参数
     * 用于根据方案id获取该方案的执行结果列表
     */
    planId: {
      type: String
    }
  },
  data() {
    return {
      /**
       * 弹框配置信息
       */
      dialogConfig: {
        title: '结果查看',
        visible: true,
        width: '80%',
        showFooter: false
      },
      /**
       * 表输出结果列表分页配置信息
       */
      pageConfig: {
        total: 0,
        pageIndex: 1,
        pageSize: 20
      },
      /**
       * 方案执行结果列表配置信息
       */
      resultTableConfig: {
        tableColumnConfig: viewDealResultConfig.tableColumnConfig,
        tableData: [],
        isShowOperation: true,
        operationWidth: '100'
      },
      /**
       * 方案执行结果数据表列表配置信息
       */
      resultDataTableConfig: {
        tableColumnConfig: [],
        tableData: []
      },
      /**
       * 当前结果行数据信息
       */
      resultRow: null
    };
  },
  created() {
    this.getDealResultList();
  },
  methods: {
    /**
     * 关闭弹框
     * @description 向父组件提交关闭弹框事件，由父组件控制本组件显示隐藏效果
     */
    closeDialog() {
      this.$emit('closeDialog');
    },
    /**
     * 获取方案执行结果列表
     * @description 方案中可能包含多个执行流程，形成多个结果输出
     */
    getDealResultList() {
      /**
       * 接口参数中pageIndex默认只有1页，pageSize为1000，以期全部返回方案执行结果列表
       */
      const params = {
        source: '方案',
        planId: this.planId,
        pageIndex: 1,
        pageSize: 1000
      };
      this.$API.DealResult(params).then(({ data }) => {
        this.resultTableConfig.tableData = data.data.records;
      });
    },
    /**
     * 查看方案执行结果数据表数据
     * @param row 方案执行结果列表行信息
     */
    viewTableData({ row }) {
      // 接口加载动画
      let loading = this.$utils.setLoading();
      // 方案结果列表行信息存储，以备分页切换时作为参数传递到本方法
      this.resultRow = { row };
      // 接口参数
      let params = {
        transId: row.transId,
        path: row.path,
        nodeId: row.nodeId,
        pageSize: this.pageConfig.pageSize,
        pageIndex: this.pageConfig.pageIndex
      };
      // 请求接口
      this.$API
        .getPlanResultData(params)
        .then(res => {
          if (res.data) {
            // 整理结构数据表返回的字段信息作为列表表头信息展示
            this.resultDataTableConfig.tableColumnConfig = res.data.columnsList.map(item => {
              return {
                label: item.columnName,
                prop: item.columnName || item.comments
              };
            });
            // 设置列表数据
            this.resultDataTableConfig.tableData = res.data.firstRecords;
            // 设置列表分页数据总数
            this.pageConfig.total = res.data.total;
          }
        })
        .finally(() => {
          // 关闭接口加载动画
          loading.close();
        });
    },
    /**
     * 查看方案执行结果文件下载预览
     * @param row 方案执行结果列表行信息
     */
    downloadFile({ row }) {
      // 设置下载路径
      let url = encodeURI(row.excelPath);
      let href = `api/file/download.do?url=${url}`;
      this.$utils.downloadFile(href, row.excelPath);
    },
    /**
     * 设置分页配置信息，重新获取结果数据表数据
     * @param e 页码
     */
    currentPage(e) {
      this.pageConfig.pageIndex = e;
      this.viewTableData(this.resultRow);
    },
    /**
     * 设置分页配置信息，重新获取结果数据表数据
     * @param e 数据条数
     */
    pageSizes(e) {
      this.pageConfig.pageSize = e;
      this.viewTableData(this.resultRow);
    }
  },
  computed: {
    /**
     * 判断结果数据展示区域是否展示
     */
    showResultData() {
      return this.resultDataTableConfig.tableColumnConfig.length && this.resultDataTableConfig.tableData.length;
    }
  }
};
</script>
<style lang="scss" scoped>
.content {
  width: 100%;
  height: 100%;
  .resultList {
    width: 100%;
    height: 300px;
    .listdiv {
      width: 100%;
      height: calc(100% - 40px);
    }
  }
  .resultDataList {
    width: 100%;
    margin-top: 20px;
    height: 408px;
    .listdiv {
      width: 100%;
      height: calc(100% - 80px);
    }
  }
  h3 {
    color: #fff;
    line-height: 40px;
  }
}
</style>
