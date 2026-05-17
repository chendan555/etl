<template>
  <div id="mainContainer"></div>
</template>
<script lang="ts">
import Vue from 'vue';
import API from '@/api/index';
import FlowChart from '@/FlowChart/index';
import editor from '@/FlowChart/editor.js';

export default Vue.extend({
  props: {
    path: {
      type: String
    }
  },
  data() {
    return {};
  },
  mounted() {
    // 清空之前保留的流程节点
    editor.clearAll();
    this.init();
  },
  methods: {
    init() {
      FlowChart.setContainer('mainContainer');

      API.getFlowChartData({
        type: 'transformation',
        path: this.path
      }).then((data: any) => {
        FlowChart.loadData(data.data.content);
      });
    },

    zoomOut() {
      FlowChart.zoomOut();
    },
    zoomIn() {
      FlowChart.zoomIn();
    },
    undo() {
      FlowChart.undo();
    }
  }
});
</script>

<style lang="scss" scoped>
#mainContainer {
  height: 600px;
}
</style>
