<template>
  <!-- zgContent666 -->
  <div class="zgContent zg-content-input zg-content-card row" id="list">
    <!--<el-row type="flex" class="row-bg" justify="space-between">

      <el-col :span="5" v-if="left">

      </el-col>

      <el-col :span="5" v-if="left1">
        <topTitle v-if="leftTitle" :title-btn="leftTitleBtn" :title="leftTitle" @on-click="clickBtn"></topTitle>
        <div class="slot-form-left">
          <slot name="slot-form-left"></slot>
        </div>
        <topTitle v-if="leftTitle1" :title-btn="leftTitleBtn" :title="leftTitle1" @on-click="clickBtn"></topTitle>
        <div class="slot-form-left">
          <slot name="slot-form-left1"></slot>
        </div>
      </el-col>

      <el-col :span="left ? 18 : 24">
      </el-col>
    </el-row>-->

    <div v-if="left" class="zg-content-left" :style="{width:positionLeft}">
      <!--<topTitle v-if="leftTitle" :title-btn="leftTitleBtn" :title="leftTitle" @on-click="clickBtn"></topTitle>-->
      <div class="slot-form-left">
        <slot name="slot-form-left"></slot>
      </div>
    </div>
    <p
      v-if="drag"
      class="toggle"
      @click.stop="toggle"
      :style="{'left':'calc('+positionLeft+' - 15px)'}"
      title="放大缩小左边菜单"
      @mousedown="down"
    >| |</p>
    <div
      class="zg-content-right"
      :style="{'width':drag? 'calc(100% - '+positionLeft+')': left ? 'calc(84% - 1px)' : '100%'}"
    >
      <div :class="[bgColor ? 'content-bg' : '' ,'slot-form-right']">
        <!--<topTitle v-if="rightTitle" :title-btn="rightTitleBtn" :title="rightTitle" @on-click="clickBtn"></topTitle>-->
        <div class="content-form" style="display: flex;flex-direction: column;height:100%">
          <div class="slot-form-dialog">
            <slot name="slot-form-dialog"></slot>
          </div>
          <!--右侧顶部-->
          <div class="slot-form-head" v-if="$slots['slot-form-head']">
            <slot name="slot-form-head"></slot>
          </div>
          <!--筛选部分-->
          <div
            v-if="$slots['slot-form-screen']"
            :class="[isDown ? 'content-screen-down' :'','slot-form-screen','content-screen',screenBorderColor ? 'screen-border-color':'','']"
          >
            <!--筛选输入框-->
            <slot name="slot-form-screen"></slot>
          </div>
          <div v-if="$slots['slot-form-screen2']" :class="[isDown ? 'content-screen-down' :'']">
            <!--筛选输入框-->
            <slot name="slot-form-screen2"></slot>
          </div>

          <!--自定义内容区域-->
          <div class="slot-form" v-if="$slots['slot-form']">
            <slot name="slot-form"></slot>
          </div>
          <!--编辑页面内容部分只有input-->
          <div class="slot-form-input" v-if="$slots['slot-form-input']">
            <slot name="slot-form-input"></slot>
          </div>
          <!--按钮组与表格组合-->
          <div class="slot-form-group-list" v-if="$slots['slot-form-group-list']">
            <slot name="slot-form-group-list"></slot>
          </div>
          <!--中间按钮组部分-->
          <div class="slot-form-group" v-if="$slots['slot-form-group']">
            <slot name="slot-form-group"></slot>
          </div>
          <!--列表数据部分-->
          <div
            class="slot-form-list"
            v-if="$slots['slot-form-list']"
            style="height:100%;flex:1;overflow:auto"
          >
            <slot name="slot-form-list"></slot>
          </div>
          <!--分页部分-->
          <div class="slot-form-page" v-if="$slots['slot-form-page']">
            <slot name="slot-form-page"></slot>
          </div>
          <!--图表-->
          <div class="slot-echarts" v-if="$slots['slot-echarts']">
            <slot name="slot-echarts"></slot>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
/**
 *
 * Create by Leo
 * 基础组件
 * 基础界面组件
 * 左右两栏式布局
 * 坐侧区域可控制显隐
 * 并减少一定的代码量
 * 统一设置弹框的配置项、确认按钮、取消按钮、关闭按钮等信息
 *
 */
export default {
  props: {
    /*
    是否显示左边
    */
    left: {
      type: Boolean,
      default: false
    },
    /*
    是否启用左侧拖动，默认false不启用
    */
    drag: {
      type: Boolean,
      default: false
    },
    /*
    是否显示左边
    */
    left1: {
      type: Boolean,
      default: false
    },
    /*
    左边title
    */
    leftTitle: {
      type: String,
      default: ''
    },
    /*
    左边title
    */
    leftTitle1: {
      type: String,
      default: ''
    },
    /*
    右边title
    */
    rightTitle: {
      type: String,
      default: ''
    },
    /*
    是否显示查询筛选头部
    */
    screenHead: {
      type: Boolean,
      default: false
    },
    /*
    边框颜色
    */
    screenBorderColor: {
      type: Boolean,
      default: false
    },
    /*
    是否显示背景颜色
    */
    bgColor: {
      type: Boolean,
      default: false
    },
    /**
     * 配置项
     */
    rightTitleBtn: {
      type: Boolean,
      default: false
    },
    /**
     * 配置项
     */
    leftTitleBtn: {
      type: Boolean,
      default: false
    },
    /**
     * 配置项
     */
    positionLeft: {
      type: String,
      default: '16%'
    }
  },
  name: 'zgContent',
  data() {
    return {
      /**
       * 配置项
       */
      isDown: false,
      /**
       * 配置项
       */
      time: 0,
      /**
       * 配置项
       */
      leftWidth: true
    };
  },
  mounted() {},
  methods: {
    /*
    标题按钮添加触发事件
    */
    clickBtn() {
      this.$emit('on-click-btn');
    },
    /**
     * 收缩控制
     */
    toggle() {
      // 点击收缩
      if (this.time == 0) {
        if (this.leftWidth) {
          this.positionLeft = '0%';
        } else {
          this.positionLeft = '16%';
        }
        this.leftWidth = !this.leftWidth;
      }
    },
    // 拖动
    /**
     * 配置项
     * @param event
     */
    down(event) {
      let startTime = new Date().getTime();
      let that = this;
      document.onmousemove = function(event) {
        that.positionLeft = event.clientX + 'px';
      };
      document.onmouseup = function(event) {
        let endTime = new Date().getTime();
        if (endTime - startTime > 150) {
          that.time = 1;
        } else {
          that.time = 0;
        }
        document.onmousemove = null;
        document.onmouseup = null;
      };
    }
  }
};
</script>

<style lang="scss" scoped>
@import '@/styles/theme.scss';
.QuJuitemJianJu {
  margin: 0 20px 0 38px;
}
.el-table .warning-row {
  background: rgba(0, 94, 164, 0.4) !important;
}

.el-table .success-row {
  background: rgba(1, 2, 29, 0.1) !important;
}

#list >>> .el-table,
#list >>> .el-table__expanded-cell {
  background: none;
}

#list >>> .el-table {
  background: none;
}

#list >>> .el-table::before {
  background: none;
}

#list >>> .el-table th {
  /*background-color: rgb(0, 121, 211);*/
  background-color: $tableOddBgClr;
}

#list >>> .el-table th,
#list >>> .el-table tr th {
  height: 42px;
  color: white;
}

#list >>> .el-table th,
#list >>> .el-table tr th div {
  color: white;
}

#list >>> .el-table .cell {
  color: #fff;
  font-size: 12px;
  /*text-align: center;*/
}

#list >>> .el-table thead .cell {
  color: #fff;
  font-size: 14px;
  font-weight: bold;
  /*text-align: center;*/
}

#list >>> .el-table__footer .cell {
  color: yellow;
}

#list >>> .el-table--enable-row-transition .el-table__body td div {
  color: white;
  font-size: 14px;
}

#list >>> .el-table td,
#list >>> .el-table th {
  padding: 0;
  height: 40px;
}

#list >>> .el-table tr {
  background: #1d291f;
}

#list >>> .el-table__fixed {
  background-color: #1d291c;
}

#list >>> .el-table__body tr.el-table__row--striped td {
  background-color: rgba(35, 49, 41, 0.5);
}

#list >>> .el-table__body tr.el-table__row--striped:hover > td {
  background-color: #384a39;
}

#list >>> .el-table td,
#list >>> .el-table th.is-leaf {
  /* border-color: #435b44; */
  border-color: rgba(67, 91, 68, 0.9);

  cursor: pointer;
}

#list >>> .el-table--border::after,
#list >>> .el-table--group::after,
#list >>> .el-table::before,
#list >>> .el-table__fixed-right::before,
#list >>> .el-table__fixed::before {
  background-color: #435b44;
}

#list >>> .el-table__body tr.current-row > td {
  background-color: rgba(67, 91, 68, 0.68);
}

#list >>> .el-table__body tr.current-row.el-table__row--striped > td,
#list >>> .el-table__body tr.hover-row > td {
  background-color: rgba(67, 91, 68, 0.68);
}

#list >>> .el-table__body tr:hover > td {
  /*background: rgba(67, 91, 68, 0.68);*/
  /* background-color: #384a39; */
  background-color: #384a39;
}

#list >>> .el-table__fixed-footer-wrapper tbody td {
  background: none;
}

#list >>> .el-table__fixed-right-patch {
  background: none;
}

#list >>> .el-loading-mask {
  background: none;
}

#list >>> .el-table__footer-wrapper tbody td {
  background: none;
}

.el-table--border {
  border-right: 1px solid #435b44;
  border-bottom: 1px solid #435b44;
}
#list >>> .el-table--border th,
#list >>> .el-table--border th,
#list >>> .el-table--border {
  border-color: #435b44;
}

#list >>> .el-table__fixed-right::before,
#list >>> .el-table__fixed::before {
  background: none;
}

.table-list-radio >>> .el-table .el-radio__input .el-radio__inner {
  border-radius: 2px;
}

.tableList >>> .el-input-number .el-input__inner {
  padding: 0;
  width: 100%;
}

.tableList >>> .el-input-number__decrease,
.tableList >>> .el-input-number__increase {
  background: none;
  border: none;
  color: #fff;
}

.tableList >>> .el-input-number {
  width: 100%;
}

.tableList >>> .el-switch__label {
  color: #fff;
}

.tableList >>> .el-switch__label.is-active {
  color: #409eff;
}

.tableList >>> .el-input__inner {
  width: 100%;
}

.zg-content-input >>> .el-input__inner {
  background: #233129 !important;
  border-color: #435b44;
  color: #fff;
}

.zg-content-input >>> .el-form-item__label {
  color: #fff;
}

.zg-content-input >>> .el-input__inner {
  /* width: 243px; */
  font-size: 13px;
  height: 30px;
  /*height: 35px;*/
  /*line-height: 35px;*/
}

.el-select .el-input.is-disabled .el-input__inner {
  height: 30px !important;
}

.zg-content-input >>> .el-input__inner::placeholder {
  font-size: 12px !important;
}

.zg-content-input >>> .el-textarea__inner {
  /* border-color: #1E9FFF; */
  width: 240px;
  font-size: 13px;
  border-color: #435b44;
}

.zg-content-input >>> .el-input-group .el-input__inner {
  /* width: 200px; */
  /* font-size: 13px; */
}

.zg-content-input >>> .selected-el-form-item .el-input-group .el-input__inner {
  width: 205px;
}

.zg-content-input >>> .el-form--inline .el-form-item .el-form-item {
  margin-bottom: 0px;
}

.zg-content-input >>> .el-input-group__append .el-button {
  background-color: #435b44;
  border-color: #435b44;
  border-radius: 0;
  color: #fff;
  padding: 13px 8px;
  font-size: 10px !important;
}

.zg-content-input >>> .el-input-group__append {
  background: none;
  overflow: hidden;
  border-color: #435b44;
}

.zg-content-input >>> .el-radio {
  color: #fff;
}

.zg-content-input >>> .el-checkbox {
  color: #fff;
}

.zg-content-input >>> .el-textarea .el-textarea__inner {
  background: none;
  color: #fff;
  border-color: #435b44;
}

.zg-content-input >>> .el-select .el-input.is-focus .el-input__inner {
  border-color: #233129;
}

.zg-content-input >>> .el-input.is-disabled .el-input__inner {
  border-color: #435b44;
}

.el-dialog__body >>> .el-dialog__body {
  padding: 0 !important;
}

.zg-content-input >>> .el-date-editor .el-range-separator {
  line-height: 22px;
}

.zg-content-input >>> .el-date-editor .el-range__icon {
  line-height: 22px;
}

.zg-content-input >>> .el-form--inline .couromTextarea .el-form-item__content {
  width: 75%;
  word-break: break-word;
}

.zg-content-input >>> .el-form--inline .form-item-files .el-form-item__content {
  width: 90%;
}

.zg-content-input >>> .couromTextarea .el-textarea__inner {
  width: 100%;
  border-color: #435b44;
  max-height: 100px;
}

.zg-content-input >>> .el-date-editor.el-input {
  width: 100%;
}

.zg-content-input >>> .el-form-item-number-d .el-input-number {
  width: 100%;
}

.zg-content-input >>> .el-form-item-number-d .el-input-number__decrease,
.zg-content-input >>> .el-form-item-number-d .el-input-number__increase {
  display: none;
}

.zg-content-input >>> .el-form-item-number-d .el-input-number .el-input__inner {
  padding: 0 15px;
  text-align: left;
}

.zg-content-input >>> .el-range-editor .el-range-input {
  background: none;
  color: #fff;
}

.zg-content-input >>> .el-date-editor .el-range-separator {
  color: #fff;
}

.text-ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  -o-text-overflow: ellipsis;
  white-space: nowrap;
}

.zg-content-input >>> .form-line a {
  padding: 5px 15px;
  background-color: #435b44;
  font-size: 12px;
  color: #fff;
}

.zg-content-input >>> .form-line {
  width: 100%;
  height: auto;
  background-color: #435b44;
  padding: 5px 10px;
  font-size: 12px;
  border-bottom: 1px solid #435b44;
}

.zg-content-input >>> .el-collapse {
  border-color: #435b44;
}

.zg-content-input >>> .el-carousel__mask,
.zg-content-input >>> .el-cascader-menu,
.zg-content-input >>> .el-cascader-menu__item.is-disabled:hover,
.zg-content-input >>> .el-collapse-item__header,
.zg-content-input >>> .el-collapse-item__wrap {
  background: none;
  border-color: #435b44;
}

.zg-content-input >>> .el-collapse-item__header {
  color: #fff;
}

.zg-content-card >>> .el-card {
  background: none;
  color: #fff;
  border-color: #1e9fff;
  max-height: 251px;
}

.zg-content-card >>> .el-card__body {
  padding: 10px;
  padding-top: 0;
}

.zg-content-card >>> .box-card {
  width: 31.5%;
  margin: 10px 10px;
  font-size: 14px;
}

.zgContent {
  height: 100%;
  font-size: 14px;
}

.zg-content-left {
  width: 20%;
  height: 100%;
  box-sizing: border-box;
  border-right: 1px solid #435b44;
  overflow: hidden;
}

.zg-content-right {
  height: 100%;
  width: 80%;
  box-sizing: border-box;
}

.slot-form-left {
  height: 100%;
  padding: 15px;
  padding-right: 0;
}

.content-form {
  width: 100%;
  margin: auto;
}

.content-screen,
.qcInfoList > #list > .zg-content-right .slot-form-input {
  width: 99.9%;
  height: auto;
  border-color: #4d6c4f;
  margin: 10px 0;
}

.qcInfoList > #list > .zg-content-right .slot-form-input {
  width: 99.5%;
  height: auto;
  margin: 10px 0;
  padding: 10px 0 0 9px;
}

.content-screen >>> .el-form-item__content,
.content-screen >>> .el-form-item__label {
  line-height: 30px;
}

.content-screen-head {
  width: 100%;
  height: 30px;
  line-height: 30px;
  display: flex;
  justify-content: space-between;
  background-color: #435b44;
  cursor: pointer;
  color: #fff;
}

.screen-border-color {
  /*border: 1px solid #1E9FFF;*/
  border: 1px solid #435b44;
  /*margin-bottom: 10px;*/
  /*margin: 15px 0;*/
}

.content-screen-head span {
  display: inline-block;
  padding-left: 10px;
}

.content-screen-head span:last-child {
  width: 30px;
  text-align: center;
  border-left: 1px solid #fff;
  padding-left: 0;
}

.screen-border-color {
  /*border: 1px solid #1E9FFF;*/
  border: 1px solid #435b44;
  /*margin-bottom: 10px;*/
}

.content-screen-down {
  height: 30px;
  overflow: hidden;
}

.slot-form-group {
  margin: 5px 0 5px;
}

.slot-form-page {
  width: 100%;
  height: 40px;
  display: flex;
  align-items: center;
}
.slot-form-page > div {
  width: 100%;
}
.slot-form-page .pagination {
  align-self: flex-end;
  float: right;
}

.content-bg {
  background-color: rgba(2, 106, 188, 0.3) !important;
}

.slot-form-right {
  height: 100%;
}

.hoverOpen {
  background-repeat: no-repeat;
  background-position: center;
  background-size: cover;
  transition: all ease-in 0.1s;
}

.hoverOpen.topOpen,
.hoverOpen.bottomOpen {
  width: 39px;
  height: 19px;
}
.hoverOpen.leftOpen,
.hoverOpen.rightOpen {
  width: 19px;
  height: 39px;
}
</style>
<style lang="scss">
.slot-form-list {
  height: 100%;
  flex: 1 1 0%;
  overflow: auto;

  > div {
    height: 100%;

    > div > div:nth-child(3) {
      // height: 100%;
    }
  }
}

.toggle {
  width: 30px;
  height: 100px;
  line-height: 100px;
  position: absolute;
  top: calc(50% - 150px);
  font-size: 20px;
  color: #435b44;
  text-align: center;
  cursor: pointer;
  background: rgba(26, 39, 26, 0);
}
</style>

