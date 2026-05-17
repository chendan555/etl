<template>
  <div class="topOne">
    <div class="headTitle">
      <p>{{data.title}}</p>
    </div>
    <div class="content">
      <div class="leftdiv">
        <div class="itemdiv">
          <p class="name">
            <i class="circle"></i>
            <span>总数据</span>
          </p>
          <p class="cont">{{ datas.processAll}}</p>
        </div>
        <div class="itemdiv">
          <p class="name">
            <i class="circle" style="background: #13D7D0;"></i>
            <span>待处理数据</span>
          </p>
          <p class="cont" style="color: #13D7D0;">{{datas.pending}}</p>
        </div>
      </div>
      <div class="rightdiv">
        <div id="shuiboqiu">
          <charts :options="sbqOptions" v-if="data.title == '数据处理概况'"></charts>
          <charts :options="sbqOptionsTwo" v-else></charts>
        </div>
        <p class="title">已处理数据</p>
      </div>
    </div>
  </div>
</template>
<script>
// import axios from '@/api/base/http';
import charts from '@/components/Echarts/index';
import { sbqOptions } from './config.js';
// import { type } from 'os';
export default {
  name: 'topOne',
  props: ['data'],
  data() {
    return {
      dataNum: {
        totalDataNum: 0,
        dealDataNum: 0
      },
      echart: null,
      sbqOptions: sbqOptions,
      sbqOptionsTwo: {},
      datas: {}
    };
  },
  components: { charts },
  methods: {
    // 初始化数据
    // getData() {
    //   axios
    //     .get('/api/getTopOneData')
    //     .then(res => {
    //       this.dataNum = res.data.data;
    //     })
    //     .then(() => {
    //       this.initShuiboqiu();
    //     });
    // },
    getAllNums() {
      if (this.data.title == '数据处理概况') {
        this.data.api.allNums({ type: 1 }).then(({ data }) => {
          if (data.code == 200) {
            this.datas = data.data;
            let per = ((data.data.processAll - data.data.pending) / data.data.processAll).toFixed(2);
            isNaN(per) ? (this.sbqOptions.series[0].data = [0, 0]) : (this.sbqOptions.series[0].data = [per, per]);
          }
        });
      } else {
        this.data.api.allNums({ type: 2 }).then(({ data }) => {
          if (data.code == 200) {
            this.datas = data.data;
            let per = ((data.data.processAll - data.data.pending) / data.data.processAll).toFixed(2);
            this.sbqOptionsTwo = this.$utils.deepClone(this.sbqOptions);
            isNaN(per) ? (this.sbqOptionsTwo.series[0].data = [0, 0]) : (this.sbqOptionsTwo.series[0].data = [per, per]);
          }
        });
      }
    }
  },
  mounted() {
    this.getAllNums();
  }
};
</script>
<style lang="scss" scoped>
@import '@/styles/theme.scss';
.topOne {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  // padding: 8px;
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
  .content {
    flex: 1;
    display: flex;
    padding: 8px;
    .rightdiv {
      height: 100%;
    }
    > div {
      width: 60%;
      position: relative;
      .itemdiv {
        width: 100%;
        padding-left: 10px;
        .name {
          width: 100%;
          height: 30px;
          line-height: 30px;
          font-size: 14px;
          .circle {
            display: inline-block;
            width: 8px;
            height: 8px;
            border-radius: 4px;
            background: #46aa6e;
            margin: 0 5px;
          }
        }
        .cont {
          height: 50px;
          font-size: 24px;
          padding-left: 18px;
          color: #46aa6e;
          font-weight: bold;
        }
      }
      #shuiboqiu {
        width: 80%;
        height: 80%;
      }
      .title {
        position: absolute;
        width: 100%;
        height: 20px;
        line-height: 20px;
        left: 120px;
        bottom: 170px;
      }
    }
  }
}
</style>
