<template>
  <div class="dataDeal">
    <div class="headTitle">
      <p>引接配置执行情况</p>
    </div>
    <div class="echartsdiv" slot="content">
      <charts :options="buttomOneOption"></charts>
    </div>
  </div>
</template>
<script>
import charts from '@/components/Echarts/index';
import { setBtmLeftOption } from './config.js';
export default {
  name: 'topOne',
  props: ['data'],
  components: { charts },
  data() {
    return {
      echart: null,
      // buttomOneOption: buttomOneOption,
      statData: [
        {
          taskName: '任务1',
          finished: 154,
          undo: 77
        },
        {
          taskName: '任务2',
          finished: 175,
          undo: 43
        },
        {
          taskName: '任务3',
          finished: 123,
          undo: 64
        },
        {
          taskName: '任务4',
          finished: 154,
          undo: 77
        },
        {
          taskName: '任务5',
          finished: 175,
          undo: 43
        },
        {
          taskName: '任务6',
          finished: 123,
          undo: 64
        }
      ]
    };
  },
  computed: {
    buttomOneOption() {
      return setBtmLeftOption(this.statData);
    }
  },
  methods: {
    getAllNums() {
      this.$API.mainPageStat.getReceiveItemStatus().then(({ data }) => {
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
