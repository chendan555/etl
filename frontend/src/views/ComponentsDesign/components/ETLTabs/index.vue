<template>
  <el-tabs v-model="activeName" @tab-click="handleClick">
    <el-tab-pane v-for="(item,index) in tabList" :key="index" :label="item.label" :name="item.name"></el-tab-pane>
    <div class="tab-content">
      <span v-if="curTabCanvas.eleList.length === 0" class="tab-tip">请拖入组件</span>
      <DesignCanvas
        :canvasDesign="curTabCanvas"
        :isPreview="isPreview"
        :canvasRuleForm="ruleForm"
        @addEle="addEle"
        @chooseEle="chooseEle"
      />
    </div>
  </el-tabs>
</template>
<script>
export default {
  name: 'ETLTabs',
  props: {
    tabList: {
      default: [],
      type: Array
    },
    eleList: {
      default: [],
      type: Array
    },
    tabId: {
      default: '',
      type: String
    },
    isPreview: {
      type: Boolean,
      default: false
    },
    ruleForm: {
      type: Object
    }
  },
  data() {
    return {
      activeName: ''
    };
  },
  computed: {
    curTabCanvas() {
      let obj = { eleList: [] };
      this.eleList.forEach(ele => {
        if (ele.config.tagName === this.activeName) {
          obj.eleList.push(ele);
        }
      });
      return obj;
    }
  },
  created() {
    this.initSelect();
  },
  beforeCreate() {
    this.$options.components.DesignCanvas = () => import('../../Layout/DesignCanvas.vue');
  },
  methods: {
    handleClick(tab) {},
    initSelect() {
      this.activeName = this.tabList[0].name;
    },
    addEle(data) {
      if (data.key === 'tabs') {
        this.$message.warning('不可嵌套添加选项卡组件！');
        return;
      }
      this.$eventBus.$emit('addEle', data, this.tabId, this.activeName);
    },
    chooseEle(config) {
      this.$eventBus.$emit('chooseEle', config, this.tabId, this.activeName);
    }
  }
};
</script>
<style lang="scss" scoped>
.tab-content {
  min-height: 100px;
  border: 2px solid #394e39;
  position: relative;
  .tab-tip {
    position: absolute;
    left: 10px;
  }
  .design-canvas {
    min-height: 100px;
    overflow: initial;
  }
}
</style>
