<template>
  <div class="pagination">
    <el-pagination
      background
      @current-change="currentPage"
      @size-change="pageSize"
      :layout="layout"
      :pagerCount="data.pageCount"
      :page-size="data.pageSize"
      :current-page="data.pageNum"
      :page-sizes="data.sizes || pageSizes"
      :total="data.total"
    ></el-pagination>
  </div>
</template>

<script>
/**
 *
 * Create by Leo
 * 基础组件
 * 基础分页组件
 * 左右两栏式布局
 * 坐侧区域可控制显隐
 * 并减少一定的代码量
 * 统一设置弹框的配置项、确认按钮、取消按钮、关闭按钮等信息
 *
 */

export default {
  name: 'pagination',
  props: {
    /**
     *
     * 配置信息
     *
     */
    data: {
      type: Object,
      default: {
        pageCount: 7
      }
    },
    /**
     *
     * 配置信息
     *
     */
    layout: {
      type: String,
      default: 'total, sizes, prev, pager, next, jumper'
    }
  },
  data() {
    return {
      pageSizes: [10, 20, 30, 40, 50, 100]
    };
  },
  mounted() {
    this.inputHandler();
  },
  methods: {
    /**
     *
     * 设置分页
     * @param e
     *
     */
    currentPage(e) {
      this.$emit('current', e);
    },
    /**
     *
     * 设置分页
     * @param e
     *
     */
    pageSize(e) {
      this.$emit('sizes', e);
    },
    /**
     *
     * 设置分页样式
     * @param e
     *
     */
    inputHandler(e) {
      let allDom = document.querySelectorAll('.el-pagination__editor .el-input__inner');
      for (let i = 0; i < allDom.length; i++) {
        allDom[i].setAttribute('onkeyup', 'this.value = this.value.replace(/[^0-9]/g,"")');
      }
    }
  }
};
</script>

<style lang="scss">
@import './src/styles/theme.scss';
.pagination {
  width: 100% !important;
  height: 100%;
  text-align: right !important;
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

.pagination .el-pagination.is-background .el-pager li:not(.disabled).active {
  background-color: $primaryBtnBg;
  border: 1px solid $primaryBtnBg;
}

.pagination .el-pagination.is-background .el-pager li,
.pagination .el-pagination.is-background .btn-prev,
.pagination .el-pagination.is-background .btn-next {
  background: $pageCardBg;
  /*border: 1px solid #1E9FFF;*/
  border: 1px solid $pageCardBg;
  color: $tableWordClr;
  border-radius: 0;
}

// .pagination .el-pagination__total,
// .pagination .el-pagination__jump {
//   color: #fff !important;
// }
.pagination {
  .el-pagination__total {
    color: $tableWordClr !important;
  }
  .el-pagination__jump {
    color: $tableWordClr !important;
  }
  .el-pagination__editor {
    padding: 0;
    margin: 0 5px;
  }
}

.pagination .el-pagination.is-background .btn-prev,
.pagination .el-pagination.is-background .btn-next {
  background: $pageCardBg;
  /*border: 1px solid #1E9FFF;*/
  border: 1px solid $pageCardBg;
}

.pagination .el-input__inner {
  background: $pageCardBg;
  color: $tableWordClr;
  border-color: $pageCardBg;
  width: 100px !important;
}

.pagination .el-pagination__jump .el-input__inner {
  background: $pageCardBg;
  color: $tableWordClr !important;
  width: 50px !important;
}

.pagination .el-pagination__jump {
  margin: 0;
}
</style>
