<template>
  <div class="dataReview">
    <FrameDemo :title="title">
      <div class="handle" slot="handle">
        <p class="item" :class="[activeName === index ? 'chsd' : '']" v-for="(item, index) in tabList" :key="index" @click="handleClick(index)">
          <span>{{ item.name }}</span>
        </p>
      </div>
      <div class="echartsdiv" slot="content">
        <charts :options="options"></charts>
      </div>
    </FrameDemo>
  </div>
</template>
<script>
import charts from '@/components/Echarts/index';
import FrameDemo from './FrameDemo';
import { dataReviewOptions } from './config.js';
import { dataRules } from '@/api/';
export default {
  name: 'dataReview',
  components: { charts, FrameDemo },
  data() {
    return {
      title: {
        imgUrl: 'dataReview',
        name: '质量审核'
      },
      options: dataReviewOptions,
      tabList: [
        {
          name: '全部'
        },
        {
          name: '昨日'
        },
        {
          name: '今日'
        }
      ],
      activeName: 0,
      allData: [], // 全部
      todayData: [], // 今天
      oldData: [] // 昨天
    };
  },
  methods: {
    // 初始化数据
    initData() {
      dataRules.apiGetAuditResult().then(res => {
        if (res.data.success && res.data.data) {
            this.allData = this.buildData(res.data.data.all);
            this.todayData = this.buildData(res.data.data.thisDay);
            this.oldData = this.buildData(res.data.data.oldDay);
            this.options.series.forEach((ele, index) => {
                ele.data = this.allData;
            });
        }
      });
    },
    buildData(data) {
      const result = [];
      for (let key in data) {
        if (key == 'auditEnd') {
          result.push({ value: data[key], name: '已审核' });
        } else if (key == 'auditError') {
          result.push({ value: data[key], name: '已驳回' });
        } else {
          result.push({ value: data[key], name: '待审核' });
        }
      }
      return result;
    },
    // 切换数据
    handleClick(index) {
      this.activeName = index;
      this.$nextTick(() => {
        switch (index) {
            case 2:
                this.options.series.forEach((ele, index) => {
                    ele.data = this.todayData;
                });
                break;
            case 1:
                this.options.series.forEach((ele, index) => {
                    ele.data = this.oldData;
                });
                break;
            default:
                this.options.series.forEach((ele, index) => {
                    ele.data = this.allData;
                });
                break;
        }
      });
    }
  },
  mounted() {
    this.initData();
  }
};
</script>
<style lang="scss" scoped>
.dataReview {
  width: 100%;
  height: 100%;
  .handle {
    display: flex;
    .item {
      width: 60px;
      height: 100%;
      font-size: 14px;
      display: flex;
      justify-content: flex-end;
      span {
        cursor: pointer;
        height: 100%;
      }
    }
    .chsd span {
      border-bottom: 1px solid #4ae16d;
      color: #4ae16d;
    }
  }
  .echartsdiv {
    width: 100%;
    height: 100%;
  }
}
</style>
