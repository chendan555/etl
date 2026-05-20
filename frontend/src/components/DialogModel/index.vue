<template>
  <div class="dialogModel1">
    <el-dialog
      :title="dialog.title"
      :close-on-click-modal="dialog.model ? dialog.model : false"
      :fullscreen="fullscreen"
      :close-on-press-escape="dialog.closeOnPressEscape ? dialog.closeOnPressEscape : false"
      :append-to-body="dialog.append"
      :show-close="false"
      :visible.sync="dialog.visible"
      :width="dialog.width ? dialog.width: '80%'"
      :height="tree?dialog.height?dialog.height:'80%':'auto'"
      :close="modelClose"
      @open="modelOpen"
      :class="`${tableColumnConfig ? 'setHeight' : ''} ${dialog.height ? 'slot-height' : ''} ${dialog.append ? 'dialogAppend' : ''} ${dialog.noMinHeight ? 'no-min-height' : ''} ${dialog.noMaxHeight ? 'no-max-height' : ''} ${dialog.class} dialogModelNew`"
    >
      <div slot="title" v-if="dialog.title" class="title">
        <span>{{dialog.title}}</span>
        <span
          :style="`cursor: pointer;display: ${fullscreen ? 'flex': 'inline-block'};justify-content: center;align-items: center;`"
        >
          <!--<i class="fa fa-external-link" @click="fullscreen = !fullscreen"></i>-->
          <i
            class="fa fa-external-link"
            @click="fullscreen = !fullscreen"
            v-if="!noDialogExpand"
            :style="{'transform':fullscreen ?  'rotate(180deg)' : 'rotate(0deg)','margin-bottom': fullscreen ? '1px' : 'px'}"
          ></i>
          <i class="el-icon-close" @click="modelClose" v-if="dialog.close !=false"></i>
        </span>
      </div>
      <div
        class="dialog-form-body"
        :style="{'height': trees ? fullscreen ? '100%' : '600px'  : fullscreen ? '100%' :'auto'}"
      >
        <div class="dialog-form-tree" v-if="trees">
          <slot name="slot-trees"></slot>
        </div>
        <!--tabs-->
        <slot name="slot-tabs" v-else-if="tabs.length"></slot>
        <!--调用页面-->
        <slot name="slot-page" v-else-if="pages"></slot>

        <!--纯内容区-->
        <div class="dialog-form-content" v-else>
          <div v-if="tree" class="dialog-form-tree1" style="width: 20%">
            <slot name="slot-tree"></slot>
          </div>
          <div :style="{'width': tree ? '80%' : '100%'}">
            <slot name="slot-form"></slot>
            <slot name="slot-monaco"></slot>
            <slot name="slot-table-operation"></slot>
            <slot name="slot-table"></slot>
          </div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer" v-if="$slots['slot-footer']">
        <slot name="slot-footer"></slot>
      </div>
    </el-dialog>
  </div>
</template>

<script>
/**
 *
 * Create by Leo
 * 基础组件
 * 基础弹框组件
 * 左右两栏式布局
 * 坐侧区域可控制显隐
 * 并减少一定的代码量
 * 统一设置弹框的配置项、确认按钮、取消按钮、关闭按钮等信息
 *
 */

export default {
  name: 'dialogModel1',
  components: {},
  computed: {},

  props: {
    /**
     * 配置项
     */
    tableColumnConfig: {
      type: Array
    },
    /**
     * 配置项
     */
    noDialogExpand: {
      type: Boolean
    },
    /**
     * 配置项
     */
    dialog: {
      type: Object,
      default: {
        close: true
      }
    },
    /**
     * 配置项
     */
    tree: {
      type: Boolean,
      default: false
    },
    /**
     * 配置项
     */
    trees: {
      type: Boolean,
      default: false
    },
    /**
     * 配置项
     */
    tabs: {
      default: function() {
        return [];
      }
    },
    /**
     * 配置项
     */
    pages: false,
    /**
     * 配置项
     */
    disabled: {
      type: Boolean
    }
  },
  watch: {
    /**
     * 配置项
     */
    dialog: {
      handler(v) {
        let dom = this.$el.querySelector('.el-dialog__header');
        if (!dom) return;
        if (v.hideTitle) {
          dom.style.display = 'none';
        } else {
          dom.style.display = 'block';
        }
      },
      deep: true
    }
  },
  data() {
    return {
      /**
       * 配置项
       */
      activeName2: '0',
      /**
       * 配置项
       */
      fullscreen: false
    };
  },
  methods: {
    /**
     * 点击控制
     * @param e
     */
    handleClick(e) {
      //返回对象
      this.$emit('tabs-current', e);
    },
    /**
     * 切换前
     * @param e
     */
    tabsBefore(e) {
      return new Promise((resolve, reject) => {
        console.log(e);
        resolve();
      });
    },
    /**
     * 弹框关闭
     * @param e
     */
    modelClose(e) {
      this.dialog.visible = false;
      this.fullscreen = false;
      this.$emit('onClose', e);
    },
    /**
     * 弹框打开
     * @param e
     */
    modelOpen(e) {
      this.$emit('on-open', e);
    }
  }
};
</script>

<style scoped lang="scss">
@import '@/styles/theme.scss';
.dialogModel1 >>> .el-dialog,
.dialogAppend >>> .el-dialog {
  background-color: $inputBgClr;
  margin-top: 0 !important;
  margin-bottom: 0;
  height: 90%;
}
.dialogAppend >>> .el-dialog,
.setHeight >>> .el-dialog {
  height: 80%;
}
.dialogAppend >>> .el-dialog .el-dialog__body {
  height: calc(100% - 100px);
  overflow: auto;
}

.el-dialog__wrapper,
.dialogAppend >>> .el-dialog__wrapper {
  /* flex-direction: column; */
  display: flex;
  align-items: center;
}

/**
  设置是否最小高度
  **/
.no-min-height >>> .el-dialog__body {
  min-height: auto !important;
  height: auto !important;
}

.no-max-height >>> .el-dialog__body {
  max-height: 100% !important;
  height: auto;
}
.dialogModel1 >>> .el-dialog__wrapper .el-dialog__body {
  height: calc(100% - 100px);
  overflow: auto;
}

.dialogModel1 >>> .el-dialog.is-fullscreen .el-dialog__body,
.dialogAppend >>> .el-dialog.is-fullscreen .el-dialog__body,
.dialogAppend >>> .el-dialog.is-fullscreen .dialog-form-body,
.dialogModel1 >>> .el-dialog.is-fullscreen .dialog-form-body,
.dialogModel1 >>> .el-dialog.is-fullscreen .dialog-form-content,
.dialogAppend >>> .el-dialog.is-fullscreen .dialog-form-content {
  height: 100% !important;
  max-height: 100% !important;
}

.dialogAppend >>> .el-dialog.is-fullscreen .el-tabs__content,
.dialogModel1 >>> .el-dialog.is-fullscreen .el-tabs__content {
  height: 100% !important;
  max-height: 90% !important;
}

.dialogModel1 >>> .form-line,
.dialogAppend >>> .form-line {
  width: 100%;
  border-bottom: 1px $tableBorderClr dashed;
  height: 1px;
  margin-bottom: 17px;
  padding: 0;
}

.dialogModel1 .el-dialog.is-fullscreen .fa-external-link {
  transform: rotate(180deg);
}

/*头部样式*/
.dialogModel1 >>> .el-dialog__header .title,
.dialogAppend >>> .el-dialog__header .title {
  display: flex;
  justify-content: space-between;
}

.dialogModel1 >>> .el-dialog__header .title span,
.dialogAppend >>> .el-dialog__header .title span {
  color: #fff;
}

.dialogModel1 >>> .el-dialog__header,
.dialogAppend >>> .el-dialog__header {
  padding: 10px 20px 10px;
  border-bottom: 1px solid $dialogTtlBgClr;
}

.dialogModel1 .dialog-form-body,
.dialogAppend .dialog-form-body {
  display: flex;
  justify-content: space-between;
  width: 100% !important;
  height: 100% !important;
  /*min-height: 50vh;*/
  /*max-height: 80vh;*/
}

.dialogModel1 .dialog-form-body .dialog-form-tree,
.dialogAppend .dialog-form-body .dialog-form-tree {
  width: 100%;
  /*border-right: 1px solid $tableBorderClr;*/
  height: 100%;
  overflow: auto;
}

.dialogModel1 .dialog-form-body .dialog-form-tree .common-tree,
.dialogAppend .dialog-form-body .dialog-form-tree .common-tree {
  overflow: hidden !important;
  height: 100%;
}

.dialogModel1 .dialog-form-body .dialog-form-tree >>> .common-tree-el,
.dialogAppend .dialog-form-body .dialog-form-tree >>> .common-tree-el {
  overflow-y: auto !important;
  height: 100%;
}

.dialogModel1 .dialog-form-body .dialog-form-tree >>> .tree-search ~ .common-tree-el,
.dialogAppend .dialog-form-body .dialog-form-tree >>> .tree-search ~ .common-tree-el {
  height: 90% !important;
}

.dialogModel1 .dialog-form-body .dialog-form-content,
.dialogAppend .dialog-form-body .dialog-form-content {
  width: 100%;
  height: 100%;
  /* min-height: 600px;
      max-height: 600px; */
  overflow: auto;
  padding: 5px 10px 0px 10px;
  display: flex;
  justify-content: space-between;
}

.dialogModel1 .dialog-form-body .dialog-form-content .dialog-form-tree1 .dialogAppend .dialog-form-body .dialog-form-content .dialog-form-tree1 {
  border-right: 1px solid $tableBorderClr;
}

/*表单颜色*/
.dialogModel1 >>> .el-form .el-input__inner,
.dialogAppend >>> .el-form .el-input__inner {
  background: none;
  border-color: $tableBorderClr;
  color: #fff;
}

.dialogModel1 >>> .el-input-group__append .el-button,
.dialogModel1 >>> .el-input-group__append,
.dialogAppend >>> .el-input-group__append .el-button,
.dialogAppend >>> .el-input-group__append {
  background-color: $tableBorderClr;
  border-color: $tableBorderClr;
  color: #fff;
}

.dialogModel1 >>> .el-input-group__append .el-button--small,
.dialogAppend >>> .el-input-group__append .el-button--small {
  padding: 5px 8px;
}

.dialogModel1 >>> .el-form-item__label,
.dialogAppend >>> .el-form-item__label {
  color: #fff;
}

.dialogModel1 >>> .el-checkbox__label,
.dialogModel1 >>> .el-radio__label,
.dialogAppend >>> .el-checkbox__label,
.dialogAppend >>> .el-radio__label {
  color: #fff;
}

.dialogModel1 >>> .el-form--inline .el-form-item__content,
.dialogAppend >>> .el-form--inline .el-form-item__content {
  color: #fff;
}
/*弹出窗 表单详情*/
.dialogModel1 >>> .el-form-detail .el-form-item .el-form-item__content,
.dialogAppend >>> .el-form-detail .el-form-item .el-form-item__content {
  width: 260px;
  /*line-height: 30px;*/
}

.dialogModel1 >>> .couromTextarea,
.dialogAppend >>> .couromTextarea {
  display: flex;
  flex-wrap: nowrap;
}

.dialogModel1 >>> .couromTextarea .el-form-item__content,
.dialogAppend >>> .couromTextarea .el-form-item__content {
  /*width: 86.5%;*/
  flex: 1;
  word-break: break-all;
}

.dialogModel1 >>> .el-textarea__inner,
.dialogAppend >>> .el-textarea__inner {
  background: none;
  color: #fff;
  border-color: $tableBorderClr;
  height: 100px;
  resize: none;
  width: 100%;
}

.dialogModel1 >>> .el-select,
.dialogAppend >>> .el-select {
  width: 100%;
}

.dialogModel1 >>> .el-select-dropdown__item.hover,
.dialogModel1 >>> .el-select-dropdown__item:hover,
.dialogAppend >>> .el-select-dropdown__item.hover,
.dialogAppend >>> .el-select-dropdown__item:hover {
  background-color: $tableBorderClr;
}

.dialogModel1 >>> .el-input-number .el-input__inner,
.dialogAppend >>> .el-input-number .el-input__inner {
  text-align: left;
}

.dialogModel1 >>> .el-checkbox__inner,
.dialogAppend >>> .el-checkbox__inner {
  background-color: $inputBgClr;
  border-color: #557656;
}

.dialogModel1 >>> .el-checkbox__inner::after,
.dialogAppend >>> .el-checkbox__inner::after {
  transition: none;
}

.dialogModel1 >>> .el-checkbox__input.is-checked .el-checkbox__inner,
.dialogModel1 >>> .el-checkbox__input.is-indeterminate .el-checkbox__inner,
.dialogAppend >>> .el-checkbox__input.is-checked .el-checkbox__inner,
.dialogAppend >>> .el-checkbox__input.is-indeterminate .el-checkbox__inner {
  background: #40ab6d;
  border-color: #40ab6d;
}

.dialogModel1 >>> .el-switch.is-checked .el-switch__core,
.dialogAppend >>> .el-switch.is-checked .el-switch__core {
  background-color: $tableBorderClr;
  border-color: $tableBorderClr;
}

.dialogModel1 >>> .el-radio__input.is-checked .el-radio__inner,
.dialogAppend >>> .el-radio__input.is-checked .el-radio__inner {
  background-color: $tableBorderClr;
  border-color: $tableBorderClr;
}

.dialogModel1 >>> .el-form-item,
.dialogAppend >>> .el-form-item {
  margin-bottom: 17px;
}

.dialogModel1 >>> .el-form-item.is-success .el-input__inner,
.dialogModel1 >>> .el-form-item.is-success .el-input__inner:focus,
.dialogModel1 >>> .el-form-item.is-success .el-textarea__inner,
.dialogModel1 >>> .el-form-item.is-success .el-textarea__inner:focus,
.dialogAppend >>> .el-form-item.is-success .el-input__inner,
.dialogAppend >>> .el-form-item.is-success .el-input__inner:focus,
.dialogAppend >>> .el-form-item.is-success .el-textarea__inner,
.dialogAppend >>> .el-form-item.is-success .el-textarea__inner:focus {
  border-color: $tableBorderClr;
}

.dialogAppend >>> .el-range-editor .el-range-input,
.dialogModel1 >>> .el-range-editor .el-range-input {
  background: none;
  color: #fff;
}

.dialogAppend >>> .el-date-editor .el-range__icon,
.dialogModel1 >>> .el-date-editor .el-range__icon {
  line-height: 25px;
}

.dialogModel1 >>> .el-date-editor .el-range__close-icon,
.dialogAppend >>> .el-date-editor .el-range__close-icon {
  line-height: 25px;
}

/*列表样式*/
.dialogModel1 >>> .el-table th,
.dialogAppend >>> .el-table th {
  // background-color: $dialogTtlBgClr;
  // color: #fff;
  // border-color: $tableBorderClr;
}

.dialogModel1 >>> .el-table tr,
.dialogAppend >>> .el-table tr {
  background: none;
}

.dialogModel1 >>> .el-table,
.dialogModel1 >>> .el-table__expanded-cell,
.dialogAppend >>> .el-table,
.dialogAppend >>> .el-table__expanded-cell {
  // background: none;
  // border-color: $tableBorderClr;
}

.dialogModel1 >>> .el-table .cell,
.dialogAppend >>> .el-table .cell {
  color: #fff;
  overflow: visible;
}

/*.dialogModel1 >>> .el-table--enable-row-transition .el-table__body td,
.dialogAppend >>> .el-table--enable-row-transition .el-table__body td {
  height: 32px;
}*/

.dialogModel1 >>> .el-table td,
.dialogModel1 >>> .el-table th,
.dialogAppend >>> .el-table td,
.dialogAppend >>> .el-table th {
  padding: 10px 0;
}

.dialogModel1 >>> .el-table--striped .el-table__body tr.el-table__row--striped td,
.dialogAppend >>> .el-table--striped .el-table__body tr.el-table__row--striped td {
  background-color: $tableOddBgClr;
}

.dialogModel1 >>> .el-table--enable-row-hover .el-table__body tr:hover > td,
.dialogAppend >>> .el-table--enable-row-hover .el-table__body tr:hover > td {
  background-color: #2f442fa1;
}

.dialogModel1 >>> .el-table__footer-wrapper tbody td,
.el-table__header-wrapper tbody td,
.dialogAppend >>> .el-table__footer-wrapper tbody td,
.el-table__header-wrapper tbody td {
  background: none;
}

.dialogModel1 >>> .el-table__body tr.hover-row > td,
.dialogAppend >>> .el-table__body tr.hover-row > td {
  background-color: #2f442fa1;
  color: #fff;
}

.dialogModel1 >>> .el-table__body tr.el-table__row--striped.hover-row > td,
.dialogAppend >>> .el-table__body tr.el-table__row--striped.hover-row > td {
  background-color: #2f442fa2;
  color: #fff;
}

.dialogModel1 >>> .el-table__fixed,
.dialogAppend >>> .el-table__fixed {
  background-color: $inputBgClr;
}

.dialogModel1 >>> .el-table__fixed .el-table__row td,
.dialogAppend >>> .el-table__fixed .el-table__row td {
  background-color: $inputBgClr;
}

.dialogModel1 >>> .el-table__fixed .el-table__row--striped td,
.dialogAppend >>> .el-table__fixed .el-table__row--striped td {
  background-color: $tableOddBgClr;
}

.dialogModel1 >>> .el-table__footer-wrapper tbody td .cell,
.dialogAppend >>> .el-table__footer-wrapper tbody td .cell {
  color: yellow;
  font-weight: bold;
}

/*表格边框样式*/
.dialogModel1 >>> .el-table td,
.dialogModel1 >>> .el-table th.is-leaf,
.dialogModel1 >>> .el-table--border,
.dialogModel1 >>> .el-table--group .dialogAppend >>> .el-table td,
.dialogAppend >>> .el-table th.is-leaf,
.dialogAppend >>> .el-table--border,
.dialogAppend >>> .el-table--group {
  border-color: $tableBorderClr;
}

.dialogModel1 >>> .el-table,
.dialogAppend >>> .el-table {
  border-right: 1px solid $tableBorderClr;
}

.dialogModel1 >>> .el-table--border::after,
.dialogModel1 >>> .el-table--group::after,
.dialogModel1 >>> .el-table::before,
.dialogAppend >>> .el-table--border::after,
.dialogAppend >>> .el-table--group::after,
.dialogAppend >>> .el-table::before {
  background-color: $tableBorderClr;
}

.dialogModel1 >>> .el-table td,
.el-table th.is-leaf,
.dialogAppend >>> .el-table td,
.el-table th.is-leaf {
  border-color: $tableBorderClr;
}

.dialogModel1 >>> .el-table td .el-input-number,
.dialogAppend >>> .el-table td .el-input-number {
  width: 100%;
}

.dialogModel1 >>> .el-table td .el-input-number .el-input__inner,
.dialogAppend >>> .el-table td .el-input-number .el-input__inner {
  text-align: center;
}

.dialogModel1 >>> .el-table__fixed-right::before,
.dialogModel1 >>> .el-table__fixed::before,
.dialogAppend >>> .el-table__fixed-right::before,
.dialogAppend >>> .el-table__fixed::before {
  background-color: $tableBorderClr;
}

.dialogModel1 >>> .el-table td .el-input__inner,
.dialogAppend >>> .el-table td .el-input__inner {
  background: none;
  border-color: $tableBorderClr;
  color: #fff;
}

.dialogModel1 >>> .el-table td .table-el-input-number .el-input-number,
.dialogAppend >>> .el-table td .table-el-input-number .el-input-number {
  width: 100%;
}

/*tabs样式*/
.dialogModel1 >>> .el-tabs {
  width: 100%;
  height: 100%;
}

.dialogModel1 >>> .el-dialog__body .el-tabs__content {
  height: 90% !important;
  overflow-y: auto;
}

.dialogModel1 >>> .el-dialog__body .el-tabs__content .el-tab-pane {
  /*padding-bottom: 20px;*/
  height: 100%;
}

/*--------------------------------------------------------------------------------*/

.dialogModel1 >>> .el-tabs--top > .el-tabs__header .el-tabs__item,
.dialogAppend >>> .el-tabs--top > .el-tabs__header .el-tabs__item {
  color: #fff;
}

.dialogModel1 >>> .el-tabs--top > .el-tabs__header .el-tabs__item.is-active,
.dialogModel1 >>> .el-tabs--top > .el-tabs__header .el-tabs__item:hover,
.dialogAppend >>> .el-tabs--top > .el-tabs__header .el-tabs__item.is-active,
.dialogAppend >>> .el-tabs--top > .el-tabs__header .el-tabs__item:hover {
  color: #cbb68c;
  font-weight: bold;
}

.dialogModel1 >>> .el-tabs--top > .el-tabs__header .el-tabs__active-bar,
.dialogAppend >>> .el-tabs--top > .el-tabs__header .el-tabs__active-bar {
  background-color: #407c39;
}

.dialogModel1 >>> .el-tabs--top > .el-tabs__header .el-tabs__nav-wrap::after,
.dialogAppend >>> .el-tabs--top > .el-tabs__header .el-tabs__nav-wrap::after {
  background-color: #4e5a50;
  height: 1px;
}

/*-------------------------------------------------------------------------------------*/

.dialogModel1 >>> .el-tabs--card > .el-tabs__header .el-tabs__nav,
.dialogModel1 >>> .el-tabs--card > .el-tabs__header .el-tabs__item,
.dialogModel1 >>> .el-tabs--card > .el-tabs__header .el-tabs__item.is-active,
.dialogModel1 >>> .el-tabs--card > .el-tabs__header,
.dialogAppend >>> .el-tabs--card > .el-tabs__header .el-tabs__nav,
.dialogAppend >>> .el-tabs--card > .el-tabs__header .el-tabs__item,
.dialogAppend >>> .el-tabs--card > .el-tabs__header .el-tabs__item.is-active,
.dialogAppend >>> .el-tabs--card > .el-tabs__header {
  border-color: $tableBorderClr;
  color: #fff;
}

.dialogModel1 >>> .el-tabs--card > .el-tabs__header .el-tabs__item.is-active,
.dialogModel1 >>> .el-tabs--card > .el-tabs__header .el-tabs__item:hover,
.dialogAppend >>> .el-tabs--card > .el-tabs__header .el-tabs__item.is-active,
.dialogAppend >>> .el-tabs--card > .el-tabs__header .el-tabs__item:hover {
  background-color: $tableBorderClr;
  color: #fff;
  font-weight: normal;
}

.dialogModel1 >>> .el-tabs--card > .el-tabs__header .el-tabs__item.is-disabled,
.dialogAppend >>> .el-tabs--card > .el-tabs__header .el-tabs__item.is-disabled {
  background-color: #394039e3;
}
</style>

<style lang="scss">
/*仅限于装备申请*/
.dialogModel1 .zgContent {
  height: 100% !important;
}
</style>
