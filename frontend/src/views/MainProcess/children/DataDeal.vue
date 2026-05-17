<template>
  <div class="dataDeal">
    <div class="headTitle">
      <p>{{data.title}}</p>
    </div>
    <!-- <FrameDemo :title="title"> -->
    <!-- <div class="handle" slot="handle">
        <router-link to="/">方案模型></router-link>
    </div>-->
    <div class="echartsdiv" slot="content">
      <charts :options="dataDealOptions"></charts>
      <!-- <charts :options="dataDealOptionsTwo" v-else></charts> -->
    </div>
    <!-- </FrameDemo> -->
  </div>
</template>
<script>
import charts from '@/components/Echarts/index';
import FrameDemo from './FrameDemo';
import { dataDealOptions } from './config.js';
// import { dataRules } from '@/api/';
// import { type } from 'os';
export default {
  name: 'dataDeal',
  components: { charts, FrameDemo },
  props: ['data'],
  data() {
    return {
      title: {
        imgUrl: 'dataDeal',
        name: '数据处理流程'
      },
      dataDealOptions: dataDealOptions,
      dataDealOptionsTwo: {},
      datas: [],
      dataTwos: [],
      isShow: false
    };
  },
  methods: {
    // 整理数据
    dataReduretion(dataZl, dataHt) {
      dataZl.push([dataHt.thisDay.runSuccess, dataHt.oldDay.runSuccess, dataHt.all.runSuccess]);
      dataZl.push([dataHt.thisDay.runError, dataHt.oldDay.runError, dataHt.all.runError]);
    },
    // 获取数据
    initData() {
      this.data.api.apiGetRunResult({ type: null }).then(res => {
        if (res.data.code == 200) {
          this.dataReduretion(this.datas, res.data.data);
          this.dataDealOptions.series.forEach((ele, index) => {
            ele.data = this.datas[index];
          });
        }
      });
      /* if (this.data.type == 1) {
      } else {
        this.data.api.apiGetRunResult({ type: 2 }).then(res => {
          if (res.data.code == 200) {
            this.dataReduretion(this.dataTwos, res.data.data);
            this.dataDealOptionsTwo = this.$utils.deepClone(this.dataDealOptions);
            this.dataDealOptionsTwo.series.forEach((ele, index) => {
              ele.data = this.dataTwos[index];
            });
          }
        });
      } */
    }
  },
  created() {
    this.initData();
    // console.log('aaaaaaa', this.data);
  }
};
</script>
<style lang="scss" scoped>
@import '@/styles/theme.scss';
.dataDeal {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  color: $normalWordsClr;
  .headTitle {
    width: 100%;
    height: 40px;
    min-height: 40px;
    font-weight: bold;
    display: flex;
    align-items: center;
    justify-content: space-between;
    background: $currentRow;
    padding-left: 8px;
  }
  .handle a {
    color: #51fad4;
  }
  .echartsdiv {
    width: 100%;
    height: 100%;
  }
}
</style>
