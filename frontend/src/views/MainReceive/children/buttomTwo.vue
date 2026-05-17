<template>
  <div class="dataDeal">
    <div class="headTitle">
      <p>引接累计数据量</p>
    </div>
    <div class="echartsdiv" slot="content">
      <charts :options="buttomTwoOption"></charts>
    </div>
  </div>
</template>
<script>
import charts from '@/components/Echarts/index';
import { setBtmRightOption } from './config.js';
export default {
  name: 'topOne',
  props: ['data'],
  components: { charts },
  data() {
    return {
      echart: null,
      // buttomTwoOption: buttomTwoOption,
      statData: [
        {
          taskName: '任务1',
          dataTable: 1154,
          file: 2277,
          emulate: 124
        },
        {
          taskName: '任务2',
          dataTable: 2275,
          file: 1143,
          emulate: 286
        },
        {
          taskName: '任务3',
          dataTable: 3523,
          file: 3464,
          emulate: 234
        },
        {
          taskName: '任务4',
          dataTable: 6454,
          file: 1277,
          emulate: 124
        },
        {
          taskName: '任务5',
          dataTable: 3675,
          file: 7543,
          emulate: 2386
        },
        {
          taskName: '任务6',
          dataTable: 2323,
          file: 6464,
          emulate: 3734
        }
      ]
    };
  },
  computed: {
    buttomTwoOption() {
      return setBtmRightOption(this.statData);
    }
  },
  methods: {
    getAllNums() {
      this.$API.mainPageStat.getReceiveTotal().then(({ data }) => {
        console.log(data);
        this.statData = data;
      });
    }
  },
  mounted() {
    this.getAllNums();
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
  .echartsdiv {
    width: 100%;
    height: 100%;
  }
}
</style>
