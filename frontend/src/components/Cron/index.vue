<template lang="html">
  <div class="cron" :val="value_">
    <el-tabs v-model="activeName">
      <el-tab-pane label="秒" name="s">
        <second-and-minute v-model="sVal" lable="秒" />
      </el-tab-pane>
      <el-tab-pane label="分" name="m">
        <second-and-minute v-model="mVal" lable="分" />
      </el-tab-pane>
      <el-tab-pane label="时" name="h">
        <hour v-model="hVal" lable="时" />
      </el-tab-pane>
      <el-tab-pane label="日" name="d">
        <day v-model="dVal" lable="日" />
      </el-tab-pane>
      <el-tab-pane label="月" name="month">
        <month v-model="monthVal" lable="月" />
      </el-tab-pane>
      <el-tab-pane label="周" name="week">
        <week v-model="weekVal" lable="周" />
      </el-tab-pane>
      <el-tab-pane label="年" name="year">
        <year v-model="yearVal" lable="年" />
      </el-tab-pane>
    </el-tabs>
    <!-- table -->
    <el-table :data="tableData" size="mini" border style="width: 100%;">
      <el-table-column prop="sVal" label="秒" width="70" />
      <el-table-column prop="mVal" label="分" width="70" />
      <el-table-column prop="hVal" label="时" width="70" />
      <el-table-column prop="dVal" label="日" width="70" />
      <el-table-column prop="monthVal" label="月" width="70" />
      <el-table-column prop="weekVal" label="周" width="70" />
      <el-table-column prop="yearVal" label="年" />
    </el-table>
  </div>
</template>

<script>
/**
 *
 * Create by Leo
 * 基础组件
 * 基础时间组件
 * 左右两栏式布局
 * 坐侧区域可控制显隐
 * 并减少一定的代码量
 * 统一设置弹框的配置项、确认按钮、取消按钮、关闭按钮等信息
 *
 */

import SecondAndMinute from './component/secondAndMinute';
import hour from './component/hour';
import day from './component/day';
import month from './component/month';
import week from './component/week';
import year from './component/year';
export default {
  components: {
    SecondAndMinute,
    hour,
    day,
    month,
    week,
    year
  },
  props: {
    /**
     * 配置项
     */
    value: {
      type: String,
      default: function() {
        return '';
      }
    }
  },
  data() {
    return {
      /**
       * 配置项
       */
      activeName: 's',
      /**
       * 配置项
       */
      sVal: '',
      /**
       * 配置项
       */
      mVal: '',
      /**
       * 配置项
       */
      hVal: '',
      /**
       * 配置项
       */
      dVal: '',
      /**
       * 配置项
       */
      monthVal: '',
      /**
       * 配置项
       */
      weekVal: '',
      /**
       * 配置项
       */
      yearVal: ''
    };
  },
  computed: {
    /**
     * 配置项
     */
    tableData() {
      return [
        {
          sVal: this.sVal,
          mVal: this.mVal,
          hVal: this.hVal,
          dVal: this.dVal,
          monthVal: this.monthVal,
          weekVal: this.weekVal,
          yearVal: this.yearVal
        }
      ];
    },
    /**
     * 配置项
     */
    value_() {
      if (!this.dVal && !this.weekVal) {
        return '';
      }
      /**
       * 配置项
       */
      if (this.dVal === '?' && this.weekVal === '?') {
        this.$message.error('日期与星期不可以同时为“不指定”');
      }
      /**
       * 配置项
       */
      if (this.dVal !== '?' && this.weekVal !== '?') {
        this.$message.error('日期与星期必须有一个为“不指定”');
      }
      /**
       * 配置项
       */
      const v = `${this.sVal} ${this.mVal} ${this.hVal} ${this.dVal} ${this.monthVal} ${this.weekVal} ${this.yearVal}`;
      if (v !== this.value) {
        this.$emit('input', v);
      }
      return v;
    }
  },
  watch: {
    /**
     * 配置项
     */
    value(a, b) {
      this.updateVal();
    }
  },
  created() {
    this.updateVal();
  },
  methods: {
    
    /**
     * 配置项
     */
    updateVal() {
      if (!this.value) {
        return;
      }
      const arrays = this.value.split(' ');
      this.sVal = arrays[0];
      this.mVal = arrays[1];
      this.hVal = arrays[2];
      this.dVal = arrays[3];
      this.monthVal = arrays[4];
      this.weekVal = arrays[5];
      this.yearVal = arrays[6];
    }
  }
};
</script>

<style lang="scss">
@import '@/styles/theme.scss';
.cron {
  text-align: left;
  padding: 10px;
  /* background: #fff;
  border: 1px solid #dcdfe6; */
  box-shadow: 0 2px 4px 0 rgba(0, 0, 0, 0.12), 0 0 6px 0 rgba(0, 0, 0, 0.04);
  color: #fff;
}
.corn span {
  color: #fff;
}
// .el-radio__label {
//   color: $normalWordsClr;
// }
.el-input-number__decrease,
.el-input-number__increase {
  background: $defaultBtnBgClr;
  color: #fff;
  border: none;
}
.section > div{
  min-height: 32px;
  line-height: 32px;
}
</style>
