<template>
  <el-dialog :title="title" :visible.sync="visible" @closed="closedCallback">
    <div class="content">
      <base-table
        :index="true"
        :isShowCheckBox="true"
        :tableColumnConfig="tableColumnConfig"
        :tableData="tableData"
        @selectionChange="selectionChange"
        :height="Tableheight"
      ></base-table>
      <div class="pageSize" slot="footer">
        <BasePagination :data="page" @current="currentPage" @sizes="pageSizes"></BasePagination>
      </div>
    </div>
    <div slot="footer">
      <el-button type="primary" @click="formValidate">确 定</el-button>
      <el-button type="info" @click="closedCallback">取 消</el-button>
    </div>
  </el-dialog>
</template>

<script>
import BaseTable from '@/components/BaseTable/index.vue';
import BasePagination from '@/components/BasePagination';
import API from '@/api/index';
import { options } from './config';
import { wjtableConfig, sjkTableConfig, fzTableConfig, formTableConfig } from './config.js';
export default {
  name: 'copyFlow',
  components: {
    BaseTable,
    BasePagination
  },
  data() {
    return {
      options: options,
      Tableheight: 500,
      title: '', //标题
      visible: true, // 弹窗的打开和关闭
      tableData: [],
      tableColumnConfig: [],
      selections: [],
      page: {
        pageSize: 20,
        pageIndex: 1,
        sizes: [10, 20, 30, 40, 50, 100],
        total: 0
      } //页码的配置
    };
  },
  props: ['yjType'],
  mounted() {
    this.getColumnConfig();
  },
  methods: {
    getColumnConfig() {
      this.getListData();
      if (this.yjType == 1) {
        this.title = '数据表引接';
        this.tableColumnConfig = sjkTableConfig.tableColumnConfig;
      } else if (this.yjType == 2) {
        this.title = '文件引接';
        this.tableColumnConfig = wjtableConfig.tableColumnConfig;
      } else if (this.yjType == 3) {
        this.title = '仿真引接';
        this.tableColumnConfig = fzTableConfig.tableColumnConfig;
      } else {
        this.title = '表单引接';
        this.tableColumnConfig = formTableConfig.tableColumnConfig;
      }
    },
    getListData() {
      if (this.yjType == 1) {
        this.getAllList(API.dataReceive.getTableRelationList({ ...this.page }));
      } else if (this.yjType == 2) {
        this.getAllList(API.dataReceive.getFileReceiveList({ ...this.page }));
      } else if (this.yjType == 3) {
        this.getAllList(this.$API.configRelation.getEmulatedataList({ ...this.page }));
      } else {
        this.getAllList(API.dataReceive.getTableRelationList({ ...this.page }));
      }
    },
    currentPage(e) {
      this.page.pageIndex = e;
      this.getListData();
    },
    pageSizes(e) {
      this.page.pageSize = e;
      this.getListData();
    },
    //查询列表
    getAllList(postAdr) {
      postAdr.then(({ data }) => {
        this.tableData = data.data.records;
        this.page.total = data.data.total;
      });
    },
    // 添加的字段（用于选择之后的回现）
    addField(yjType, options) {
      let actions = '';
      Object.keys(options).forEach(key => {
        if (parseInt(key) + 1 == yjType) {
          actions = options[key].label;
        }
      });
      return actions;
    },
    // 选择
    selectionChange(data) {
      data.forEach(item => {
        item['type'] = this.yjType;
        item['valueType'] = this.addField(this.yjType, this.options);
      });
      this.selections = data;
    },
    // 隐藏dialog回调
    closedCallback() {
      this.visible = false;
      this.$emit('close');
    },
    // 确定
    formValidate(valid) {
      this.$emit('yjData', this.selections);
      this.closedCallback();
    }
  }
};
</script>

