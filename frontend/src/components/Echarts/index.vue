<template>
  <div class="charts_w" :style="{'width':width,'height':height}">
    <div class="charts" :id="id" :style="{'width':width,'height':height}"></div>
  </div>
</template>

<script>
/**
 *
 * Create by Leo
 * 基础组件
 * 基础echart图表组件
 * 左右两栏式布局
 * 坐侧区域可控制显隐
 * 并减少一定的代码量
 * 统一设置弹框的配置项、确认按钮、取消按钮、关闭按钮等信息
 *
 */

export default {
  name: 'echarts',
  components: {},
  data() {
    return {
      /**
       * id
       */
      id: `echart${(Math.random() * 2e15).toFixed()}`,
      /**
       * 配置项
       */
      myChart: ''
    };
  },
  props: {
    /**
     * 配置项
     */
    width: {
      type: String,
      default: '100%'
    },
    /**
     * 配置项
     */
    height: {
      type: String,
      default: '100%'
    },
    // 仅支持绑定用户点击可触发的事件
    /**
     * 配置项
     */
    events: {
      type: Array | String,
      default() {
        return [];
      }
    },
    /**
     * 配置项
     */
    options: {
      type: Object
    },
    /**
     * 配置项
     */
    loading: {
      type: Boolean,
      default: false
    }
  },
  watch: {
    /**
     * 配置项
     */
    options: {
      handler(newVal) {
        console.log(newVal);
        this.myChart.clear();
        this.myChart.setOption(this.options);
      },
      deep: true
    }
    //   loading: {
    //     handler: function(newVal, oldVal) {
    //       if (newVal) {
    //         this.$nextTick(() => {
    //           this.showLoading();
    //         });
    //       } else {
    //         this.$nextTick(() => {
    //           this.hideLoading();
    //         });
    //       }
    //     },
    //     immediate: true
    //   }
  },
  mounted() {
    this.myChart = this.$echarts.init(document.querySelector(`#${this.id}`));
    this.$nextTick(() => {
      this.myChart.setOption(this.options);
      this.initEvents(this.events);
    });
  },
  methods: {
    /**
     * 初始化事件
     */
    initEvents(events) {
      if (events instanceof Array) {
        events.forEach(event => {
          this.myChart.on(event, params => {
            this.$emit(event, params);
          });
        });
      } else {
        this.myChart.on(events, params => {
          this.$emit(events, params);
        });
      }
    },
    /**
     * 刷新图表
     */
    refreshChart() {
      this.myChart.dispose();
      this.myChart = this.$echarts.init(document.querySelector(`#${this.id}`));
      this.myChart.setOption(this.options);
    },
    /**
     * 展示加载动画
     */
    showLoading() {
      this.myChart.showLoading({
        text: 'loading~',
        color: '#c23531',
        textColor: '#000',
        maskColor: 'rgba(255, 255, 255, 0.8)',
        zlevel: 0
      });
    },
    /**
     * 隐藏加载动画
     */
    hideLoading() {
      this.myChart.hideLoading();
    }
  },
  beforeDestroy() {
    this.myChart.clear();
  }
};
</script>
