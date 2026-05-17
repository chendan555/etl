<template>
  <div class="dataDeal">
    <div class="headTitle">
      <p>{{data.title}}</p>
    </div>
      <div class="echartsdiv" slot="content">
        <charts :options="dataDealOptions"></charts>
      </div>
  </div>
</template>
<script>
import charts from '@/components/Echarts/index';
import { dataDealOptions_new } from './config.js';
// import { dataRules } from '@/api/';
// import { type } from 'os';
export default {
  name: 'dataDeal',
  components: { charts },
  props:["data"],
  data() {
    return {
      title: {
        imgUrl: 'dataDeal',
        name: '数据处理流程'
      },
      dataDealOptions: dataDealOptions_new,
      datas: [],
      dataTwos: [],
      isShow: false
    };
  },
  methods: {
    // 整理数据
    dataReduretion(dataZl,dataHt){
     dataZl.push([dataHt.thisDay.runSuccess, dataHt.oldDay.runSuccess, dataHt.all.runSuccess]);
     dataZl.push([dataHt.thisDay.runError, dataHt.oldDay.runError, dataHt.all.runError]);
    },
    // 获取数据
    initData() {
       this.data.api.apiGetRunResult_New().then(res => {
        if (res.data.code == 200) {
          let arr = res.data.data
          let yAxis=[]
          let ycl=[]
          let wjfx=[]
          arr.forEach((item)=>{
            yAxis.push(item.taskName)
            ycl.push(item.dealCount)
            wjfx.push(item.analystCount)
          })
          this.dataDealOptions.yAxis.data=yAxis
          this.dataDealOptions.series[0].data=ycl
          this.dataDealOptions.series[1].data=wjfx
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
