<template>
  <div class="progress" v-if="isShowProg">
    <div class="center">
      <p>
        <i class="el-icon-loading"></i>
      </p>
      <p>执行中...</p>
      <div class="progdiv">
        <el-progress :percentage="percent"></el-progress>
      </div>
    </div>
  </div>
</template>
<script>
/**
 *
 * Create by Leo
 * 基础组件
 * 基础进度组件
 * 左右两栏式布局
 * 坐侧区域可控制显隐
 * 并减少一定的代码量
 * 统一设置弹框的配置项、确认按钮、取消按钮、关闭按钮等信息
 *
 */
export default {
  name: 'progressEle',
  data() {
    return {
      /**
       * 配置项
       */
      isShowProg: false,
      /**
       * 配置项
       */
      percent: 0
    };
  },
  props: {
    /**
     * 配置项
     */
    isShow: Boolean,
    /**
     * 配置项
     */
    percentNum: Number
  },
  watch: {
    /**
     * 监听显隐控制
     */
    isShow: {
      deep: true,
      handler(newVal, oldVal) {
        this.isShowProg = newVal;
        if (newVal) {
          this.init();
        }
      }
    },
    /**
     * 监听百分比数值
     */
    percentNum: {
      deep: true,
      handler(newVal, oldVal) {
        if (newVal == 100) {
          this.percent = newVal;
        }
      }
    }
  },
  methods: {
    /**
     * 初始化
     */
    init() {
      let time = setInterval(() => {
        this.percent += 5;
        if (this.percent >= 100) {
          clearInterval(time);
          this.$nextTick(() => {
            this.percent = 0;
          });
        }
      }, 1000);
    }
  },
  mounted() {
    // this.init();
  }
};
</script>
<style scoped>
.progress {
  width: 100%;
  height: 100%;
  position: fixed;
  top: 0;
  left: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}
.center {
  width: 400px;
  height: 100px;
  display: flex;
  justify-content: space-around;
  align-items: center;
  color: #fff;
  flex-direction: column;
}
.progdiv {
  width: 100%;
}
</style>
