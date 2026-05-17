<template>
  <el-tabs type="border-card">
    <el-tab-pane label="步骤度量">
      <base-table height="243px" :tableColumnConfig="stepTableConfig" :tableData="stepTableData"></base-table>
    </el-tab-pane>
    <el-tab-pane label="日志">
      <textarea v-model="runLog" class="log-area"></textarea>
    </el-tab-pane>
  </el-tabs>
</template>
<script>
import BaseTable from "@/components/BaseTable/index.vue";
export default {
  components: { BaseTable },
  props: {
    runLog: {
      type: String,
      default: ""
    },
    stepTableData: {
      type: Array,
      default: () => {
        return [];
      }
    }
  },
  data() {
    return {
      stepTableConfig: [
        {
          label: "步骤名称",
          prop: "0",
          showOverflowTooltip: true
        },
        // {
        //   label: "复制记录行",
        //   prop: "1",
        //   showOverflowTooltip: true
        // },
        {
          label: "读",
          prop: "2",
          showOverflowTooltip: true
        },
        {
          label: "写",
          prop: "3",
          showOverflowTooltip: true
        },
        {
          label: "输入",
          prop: "4",
          showOverflowTooltip: true
        },
        {
          label: "输出",
          prop: "5",
          showOverflowTooltip: true
        },
        {
          label: "更新",
          prop: "6",
          showOverflowTooltip: true
        },
        {
          label: "拒绝",
          prop: "7",
          showOverflowTooltip: true
        },
        {
          label: "错误",
          prop: "8",
          showOverflowTooltip: true
        },
        {
          label: "状态",
          prop: "9",
          showOverflowTooltip: true
        },
        {
          label: "时间",
          prop: "10",
          showOverflowTooltip: true
        },
        {
          label: "速度（条/秒）",
          prop: "11",
          width: '130',
          showOverflowTooltip: true
        },
        // {
        //   label: "Pri/in/out",
        //   prop: "12",
        //   showOverflowTooltip: true
        // }
      ]
    };
  }
};
</script>

