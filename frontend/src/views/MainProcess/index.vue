<template>
  <div class="mainPage">
    <!-- <div class="leftPart sidePart">
            <NavPage></NavPage>
    </div>-->
    <div class="centerPart">
      <!-- <div class="topdiv partdiv">
        <div class="statItem" v-for="item in statData" :key="item.label">
          <div class="bgdiv"></div>
          <p class="value">{{item.value}}</p>
          <p class="name">{{item.name}}</p>
        </div>
      </div> -->
      <div class="middlediv partdiv">
        <div class="itemdiv">
          <TopOne :data="cldata"></TopOne>
        </div>
        <div class="itemdiv">
          <TopTwo :data="yjdata"></TopTwo>
        </div>
      </div>
      <div class="bottomdiv partdiv">
        <div class="itemdiv">
          <DealProcess :data="clFlow"></DealProcess>
        </div>
        <div class="itemdiv">
          <DataDeal :data="yjFlow"></DataDeal>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
// import axios from '@/api/base/http'
import NavPage from './children/NavPage';
import InforPage from './children/InforPage';
import DealProcess from './children/DealProcess';
import TopOne from './children/TopOne';
import TopTwo from './children/TopTwo';
import DataDeal from './children/DataDeal';
import AddData from './children/AddData';
import DataReview from './children/DataReview';
import DataReduction from './children/DataReduction';
// import { dataRules } from '@/api/';
export default {
  name: 'MainProcess',
  /* meta: {
    pageIndex: 12,
    title: '首页',
    pageType: 'mainNav',
    belongTo: 'process'
  }, */
  components: { NavPage, InforPage, DealProcess,TopOne, TopTwo, DataDeal, AddData, DataReview, DataReduction },
  data() {
    return {
      cldata: {
        title: '处理流程统计',
        api: this.$API.MainPage
      },
      yjdata: {
        title: '挖掘流程统计',
        api: this.$API.MainPage
      },
      clFlow: {
        title: '任务维度流程统计',
        api: this.$API.MainPage
      },
      yjFlow: {
        title: '流程执行情况统计',
        api: this.$API.MainPage
      },
      // dataYesterday: {
      //   dataType: 0,
      //   title: '昨日处理数据'
      // },
      // dataToday: {
      //   dataType: 1,
      //   title: '引接流程执行结果'
      // },
      isShow: true,
      // statData: [
      //   {
      //     name: '数据表总数',
      //     value: ''
      //   },
      //   {
      //     name: '数据引接流程数量',
      //     value: ''
      //   },
      //   {
      //     name: '数据处理流程数量',
      //     value: ''
      //   },
      //   {
      //     name: '方案数量',
      //     value: ''
      //   }
      // ]
    };
  },
  methods: {
    // 获取昨日处理数据和今日待处理数据
    getDealData() {
      // let p1 = axios.get('/api/getYstdayData').then(res=>{
      //     this.dataYesterday.dealNumber = res.data.data.dealNumber - 0;
      //     this.dataYesterday.undealNumber = res.data.data.undealNumber - 0;
      //     this.dataYesterday.finishPer = res.data.data.finishPer - 0;
      // });
      // let p2 = axios.get('/api/getTodayData').then(res=>{
      //     this.dataToday.dealNumber = res.data.data.dealNumber - 0;
      //     this.dataToday.finishPer = res.data.data.finishPer - 0;
      // });
      // Promise.all([p1, p2]).then(()=>{
      //     this.isShow = true;
      // })
      // dataRules.apiGetAllNums().then(res => {
      //   if (res.data.code == 200) {
      //     Object.assign(this.dataYesterday, res.data.data);
      //     Object.assign(this.dataToday, res.data.data);
      //     this.isShow = true;
      //   }
      // });
    },
    //获取第一个图的数据
    /* getStatData() {
      this.$API.count().then(({ data }) => {
        if (data.code == 200) {
          this.statData[0].value = data.data.bataBaseCount;
          this.statData[1].value = data.data.transOtherCount;
          this.statData[2].value = data.data.transCount;
          this.statData[3].value = data.data.transPlanCount;
        }
      });
    } */
  },
  mounted() {
    this.getDealData();
    // this.getStatData();
  },
  /* created() {
    sessionStorage.setItem('rootPath', 'process');
  } */
};
</script>
<style lang="scss" scoped>
@import '@/styles/theme.scss';
.mainPage {
  width: 100%;
  height: 100%;
  display: flex;
  .sidePart {
    height: 100%;
  }
  .leftPart {
    border-right: 1px solid #808353;
    width: 280px;
  }
  .rightPart {
    border-left: 1px solid #808353;
    width: 280px;
  }
  .centerPart {
    flex: 1;
    padding: 10px;
    .partdiv {
      height: calc(100% / 2);
      padding: 5px 0;
    }
    .topdiv {
      display: flex;
      align-items: center;
      .statItem {
        flex: 1;
        height: 80%;
        margin: 0 20px;
        display: flex;
        flex-direction: column;
        justify-content: space-around;
        align-items: center;
        position: relative;
        .bgdiv {
          background: var(--mainPageItemBgClr, #132f20);
          position: absolute;
          top: 15px;
          left: 0;
          width: 100%;
          height: 84%;
          z-index: 1;
          border: 1px solid #0dcde0;
          box-shadow: 0px 0px 13px #0dcde0;
        }
        .value {
          font-size: 40px;
          color: #dceb14;
          position: relative;
          z-index: 10;
        }
        .name {
          font-size: 14px;
          position: relative;
          z-index: 10;
        }
        &:first-child {
          perspective: 900px;
          .bgdiv {
            height: 100%;
            transform: rotateY(30deg);
            left: 30px;
            top: 0;
          }
        }
        &:last-child {
          perspective: 900px;
          .bgdiv {
            height: 100%;
            transform: rotateY(-30deg);
            right: 30px;
            top: 0;
            left: auto;
          }
        }
      }
    }
    // .middlediv {
    //   height: calc(100% / 2.28);
    //   margin-bottom: 10px;
    // }
    // .bottomdiv {
    //   height: calc((100%) / 2);
    // }
    > div {
      width: 100%;
      display: flex;
      justify-content: space-between;
      .itemdiv {
        flex: 1;
        background: $mainPageItemBgClr;
        margin-right: 8px;
      }
      .itemdiv:last-child {
        margin: 0;
      }
    }
  }
}
</style>
 