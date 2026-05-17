<template>
  <div class="dataReduction">
    <FrameDemo :title="title">
      <div class="handle" slot="handle">
        <p
          class="item"
          :class="[activeName === index ? 'chsd' : '']"
          v-for="(item, index) in tabList"
          :key="index"
          @click="handleClick(index)"
        >
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
import { dataReductionOptions } from './config.js';
import { dataRules } from '@/api/';
export default {
  name: 'dataReduction',
  components: { charts, FrameDemo },
  data() {
    return {
      title: {
        imgUrl: 'taskInfor',
        name: '数据整编'
      },
      options: dataReductionOptions,
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
      // let data = [
      //     36, 25, 41
      // ];
      // this.options.series[0].data.forEach((ele, index)=>{
      //     ele.value = data[index];
      // });
      dataRules.apiGetCategroy().then(res => {
        if (res.data.success && res.data.data) {
          this.allData = this.buildData(res.data.data.all);
          this.todayData = this.buildData(res.data.data.thisDay);
          this.oldData = this.buildData(res.data.data.oldDay);
          this.options.series[0].data.forEach((ele, index) => {
            ele.value = this.allData[index];
          });
        }
      });
    },
    buildData(data) {
      const result = [];
      for (let key in data) {
        // if (key == 'catewait') {
        //   // 审核失败
        //   result.push(data[key]);
        // } else if (key == 'auditEnd') {
        //   // 待整编
        //   result.push(data[key]);
        // } else {
        //   }
        result.push(data[key]); // 审核完成
      }
      return result;
    },
    // 切换数据
    handleClick(index) {
      this.activeName = index;
      this.$nextTick(() => {
        switch (index) {
          case 2:
            this.options.series[0].data.forEach((ele, index) => {
              ele.value = this.todayData[index];
            });
            break;
          case 1:
            this.options.series[0].data.forEach((ele, index) => {
              ele.value = this.oldData[index];
            });
            break;
          default:
            this.options.series[0].data.forEach((ele, index) => {
              ele.value = this.allData[index];
            });
            break;
        }
      });
    }
  },
  created() {
    this.initData();
  }
};
</script>
<style lang="scss" scoped>
.dataReduction {
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
