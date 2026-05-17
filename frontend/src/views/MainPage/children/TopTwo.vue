<template>
  <div class="topTwo">
    <div class="headTitle">
      <p>{{data.title}}</p>
      <!-- <el-button type="primary" v-if="data.dataType" size="mini" @click="goHandle">去处理</el-button> -->
    </div>
    <div class="content">
      <div class="persentDiv">
        <div class="item">
          <p
            class="num"
            :style="{color: data.dataType?'#DDD119':'#13D7D0'}"
          >{{data.dataType?data.thisDayWaitHandle:data.oldDayHandle}}</p>
          <p class="name">{{data.dataType?'待':'已'}}处理数据</p>
        </div>
        <div class="item">
          <p class="num" style="fontSize: 24px;">{{getPrecent(data)}}%</p>
          <p class="name">完成百分比</p>
        </div>
      </div>
      <div class="progressdiv">
        <p>总进度</p>
        <div class="progress">
          <el-progress
            :show-text="true"
            :stroke-width="18"
            :percentage="getPrecent(data)"
            color="#46AA6E"
          ></el-progress>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  name: 'topTwo',
  props: ['data'],
  filters: {
    // 设置分数结构，避免精度问题
    setNumber(val) {
      let n1 = (val - 0) * 10000;
      let n2 = Math.round(n1);
      return n2 / 100;
    }
  },
  methods: {
    getPrecent(data) {
      if (data.dataType) {
        if (!data.thisDayHandle) return 0;
        const num = data.thisDayHandle / data.thisDayWaitHandle;
        let n1 = (num - 0) * 10000;
        let n2 = Math.round(n1);
        return n2 / 100;
      } else {
        if (!data.oldDayHandle) return 0;
        const num = data.oldDayHandle / data.allWaitHandle;
        let n1 = (num - 0) * 10000;
        let n2 = Math.round(n1);
        return n2 / 100;
      }
    },
    goHandle() {
      this.$router.push('/DataReviewReduce');
    }
  }
};
</script>
<style lang="scss" scoped>
@import '@/styles/theme.scss';
.topTwo {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  //   padding: 8px;
  .headTitle {
    width: 100%;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    background: $currentRow;
    padding-left: 8px;
    font-weight: bold;
    .el-button {
      border-radius: 5px;
    }
  }
  .content {
    padding: 8px;
    flex: 1;
    .persentDiv {
      width: 100%;
      height: 60%;
      display: flex;
      .item {
        width: 50%;
        p {
          text-align: center;
        }
        .num {
          font-size: 30px;
          font-weight: bold;
          line-height: 60px;
        }
        .name {
          font-size: 14px;
        }
      }
    }
    .progressdiv {
      width: 100%;
      margin-top: 20px;
      display: flex;
      > p {
        width: 60px;
        font-size: 14px;
      }
      .progress {
        flex: 1;
        /deep/ .el-progress {
          display: flex;
          .el-progress-bar {
            padding-right: 60px;
          }
          .el-progress__text {
            color: #fff;
            width: 55px;
            margin-left: 0;
          }
        }
      }
    }
  }
}
</style>
