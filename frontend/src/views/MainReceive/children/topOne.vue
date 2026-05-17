<template>
  <div class="dataDeal">
    <div class="headTitle">
      <p>引接配置项数量</p>
    </div>
    <div class="echartsdiv" slot="content">
      <charts :options="topOption"></charts>
    </div>
  </div>
</template>
<script>
import charts from '@/components/Echarts/index';
import { setTopLeftOption } from './config.js';
export default {
  name: 'topOne',
  props: ['data'],
  components: { charts },
  data() {
    return {
      echart: null,
      // topOption: topOption,
      statData: [
        {
          taskName: '任务1',
          dataTable: 54,
          file: 77,
          emulate: 24
        },
        {
          taskName: '任务2',
          dataTable: 75,
          file: 43,
          emulate: 86
        },
        {
          taskName: '任务3',
          dataTable: 23,
          file: 64,
          emulate: 34
        },
        {
          taskName: '任务4',
          dataTable: 54,
          file: 77,
          emulate: 24
        },
        {
          taskName: '任务5',
          dataTable: 75,
          file: 43,
          emulate: 86
        },
        {
          taskName: '任务6',
          dataTable: 23,
          file: 64,
          emulate: 34
        }
      ]
    };
  },
  computed: {
    topOption() {
      return setTopLeftOption(this.statData);
    }
  },
  methods: {
    getAllNums() {
      this.$API.mainPageStat.getReceiveItemStat({ reqType: 1 }).then(({ data }) => {
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
