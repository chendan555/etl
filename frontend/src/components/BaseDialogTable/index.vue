<template>
  <div class="usersModel">
    <!--选择机构-->
    <dialog-model :dialog="dialog" :tableColumnConfig="tableColumnConfig">
      <div slot="slot-form" style="height: 100%;">
        <zg-content>
          <div slot="slot-form-list">
            <base-table
              :resizable="resizable"
              :isShowOperation="isShowOperation"
              :isShowCheckBox="isShowCheckBox"
              :tableColumnConfig="tableColumnConfig"
              :tableData="showTableData"
              :tableConfig="tableConfig"
              @selectionChange="selectionChange"
            />
          </div>
          <!--分页-->
          <div slot="slot-form-page">
            <BasePagination :data="pageInfo" @current="currentPage" @sizes="pageSizes"></BasePagination>
          </div>
        </zg-content>
      </div>
      <div slot="slot-footer">
        <el-button size="mini" @click="confirm" type="primary">确定</el-button>
        <el-button size="mini" @click="close">关 闭</el-button>
      </div>
    </dialog-model>
  </div>
</template>
<script>
/**
 * Create by Leo
 * 基础组件
 * 基础弹框表单组件
 * 用于页面弹框显示输入表单的控制
 */

// 引入zgContent组件
import zgContent from "@/components/zgContent";
// 引入DialogModel组件
import DialogModel from "@/components/DialogModel";
// 引入BasePagination组件
import BasePagination from "@/components/BasePagination";
// 引入BaseTable组件
import BaseTable from "@/components/BaseTable";
export default {
  name: "usersModel",
  props: {
    /**
     * 弹框配置信息
     */
    dialog: {
      type: Object
    },
    /**
     * 是否可控制大小
     */
    resizable: {
      type: Boolean,
      default: true
    },
    /**
     * 是否展示操作按钮
     */
    isShowOperation: {
      type: Boolean,
      default: false
    },
    /**
     * 是否展示勾选框
     */
    isShowCheckBox: {
      type: Boolean,
      default: false
    },
    /**
     * 表格高度
     */
    height: {
      type: String
    },
    /**
     * 表格配置项
     */
    tableColumnConfig: {
      type: Array,
      default: []
    },
    /**
     * 表格数据
     */
    tableData: {
      type: Array,
      default: []
    },
    /**
     * 数据键名
     */
    valueKey: {
      type: String,
      default: ""
    },
    /**
     * 名称键名
     */
    nameKey: {
      type: String,
      default: ""
    },
    /**
     * 表格配置
     */
    tableConfig: {
      type: Object,
      default: () => {
        return {};
      }
    },
    /**
     * 分页信息
     */
    pageInfo: {
      type: Object,
      default: () => ({
        total: 0,
        pageNum: 1,
        pageSize: 10
      })
    }
  },
  components: {
    zgContent,
    DialogModel,
    BasePagination,
    BaseTable
  },
  data() {
    return {
      checkboxList: [],
    };
  },
  computed: {
    /**
     * 整理表格数据
     */
    showTableData() {
      this.pageInfo.total = this.tableData.length;
      let arr = [];
      this.tableData.forEach((ele, index)=>{
        if((this.pageInfo.pageNum-1)*this.pageInfo.pageSize<=index && index<this.pageInfo.pageNum*this.pageInfo.pageSize) {
          arr.push(ele);
        }
      })
      // this.tableData.forEach((element, index) => {
      //   if(this.pageInfo.pageNum*this.pageInfo.pageSize-10<index && index<=this.pageInfo.pageNum*this.pageInfo.pageSize) {
      //     arr.push(element);
      //     console.log(arr)
      //   }
      // });
      // console.log('arr', arr)
      // for(let i = this.pageInfo.pageNum*this.pageInfo.pageSize-10; i<this.pageInfo.pageNum*this.pageInfo.pageSize; i++) {
      //   arr.push(this.tableData[i]);
      //   console.log(arr)
      // }

      return arr;
    }
  },
  mounted() {
    this.pageInfo.total = this.tableData?this.tableData.length:0;
  },
  methods: {
    /**
     * 设置分页页面
     */
    currentPage(e) {
      this.pageInfo.pageNum = e;
    },
    /**
     * 设置每页数据数量
     */
    pageSizes(e) {
      this.pageInfo.pageSize = e;
    },
    /**
     * 勾选多选框
     */
    selectionChange(val) {
      this.checkboxList = val;
      this.$emit("selectionChange", val);
    },
    /**
     * 保存确认
     */
    confirm() {
      this.dialog.visible = false;
      if (this.isShowCheckBox) {
        this.$emit(
          "change",
          this.checkboxList[0][this.valueKey],
          this.checkboxList[0][this.nameKey]
        );
      }
    },
    /**
     * 关闭弹框
     */
    close() {
      this.dialog.visible = false;
      this.$emit("close");
    }
  }
};
</script>
<style scoped>
</style>
