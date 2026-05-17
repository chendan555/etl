<template>
  <div class="design-tool">
    <div class="comp-msg">
      <div class="title">{{compText}}</div>
      <div class="btndiv">
        <el-button size="mini" @click="removeCurNode">删除配置项</el-button>
        <el-button size="mini" @click="updateFormConfig">组件配置同步</el-button>
        <el-button size="mini" @click="save">整体保存</el-button>
        <el-button size="mini" @click="previewForm">预览</el-button>
        <!-- <el-button size="mini" @click="submit">提交</el-button> -->
      </div>
    </div>
    <div class="design-container">
      <ComponentsMenu v-if="!isPreview"/>
      <DesignCanvas
        v-if="showCanvas"
        ref="DesignCanvas"
        :canvasDesign="canvasDesign"
        :isPreview="!!isPreview"
        @addEle="addEle"
        @chooseEle="chooseEle"
        @updateElePos="updateElePos"
      />
      <DesignConfig
        ref="DesignConfig"
        :canvasDesign="canvasDesign"
        :formKeys="formKeys"
        @setEleAttr="setEleAttr"
        @removeCurNode="removeCurNode"
        v-if="!isPreview"
      />
    </div>
  </div>
</template>
<script>
import CanvasDesign from './canvas_design';
import ComponentsMenu from './Layout/ComponentsMenu';
import DesignCanvas from './Layout/DesignCanvas';
import DesignConfig from './Layout/DesignConfig';
import axios from '@/api/base/http';
export default {
  name: 'ComponentsDesign',
  meta: {
    title: '组件设计'
  },
  components: { DesignCanvas, ComponentsMenu, DesignConfig },
  data() {
    return {
      canvasDesign: null,
      attrConfig: {},
      tabsTagName: '',
      tabsId: '',
      isPreview: this.$route.query.isPreview,
      designId: this.$route.query.id,
      // 根据后端对象获取key，进行直接绑定
      formKeys: [],
      showCanvas: false, // 判断是否展示画布，避免数据还未更新就执行画布内部逻辑
      compText: ''
    };
  },
  created() {
    this.$eventBus.$on('addEle', this.addEle);
    this.$eventBus.$on('chooseEle', this.chooseEle);
    this.$eventBus.$on('updateElePos', this.updateElePos);
    /*     this.$eventBus.$on('save', this.save);
    this.$eventBus.$on('previewForm', this.previewForm);
    this.$eventBus.$on('submit', this.submit);
    this.$eventBus.$on('updateFormConfig', this.updateFormConfig); */
    this.$eventBus.$on('getTableColumn', this.getTableColumn);
    this.getData();
  },
  beforeDestroy() {
    this.$eventBus.$off('addEle');
    this.$eventBus.$off('chooseEle');
    this.$eventBus.$off('updateElePos');
    /*     this.$eventBus.$off('save');
    this.$eventBus.$off('submit');
    this.$eventBus.$off('updateFormConfig'); */
    this.$eventBus.$off('getTableColumn');
  },
  methods: {
    getData() {
      this.canvasDesign = new CanvasDesign();
      if (!this.designId) {
        return;
      }
      axios({
        method: 'GET',
        url: `/api/plugin/manage/type/${this.designId}/detail`
      }).then(res => {
        this.compText = res.data.data.text;
        if (res.data.data.dom) {
          this.canvasDesign.eleList = JSON.parse(res.data.data.dom);
        }
        this.showCanvas = true;
        this.getFormKeys(res.data.data.pluginId);
      });
    },
    getFormKeys(pluginID) {
      axios({
        method: 'GET',
        url: `/api/plugin/manage/getDeclaredFields?pluginID=${pluginID}`
      }).then(res => {
        this.formKeys = res.data.content;
      });
    },
    noChildrenToNull(item) {
      if (item.children.length === 0) {
        delete item.children;
      } else {
        item.children.map(child => {
          return this.noChildrenToNull(child);
        });
      }
    },
    /**
     * @param data 节点信息
     * @param tabsId 选项卡id
     * @param tagName tab名
     */
    addEle(data, tabsId, tagName) {
      if (tabsId) {
        //  绑定tab父子关系
        data.config.tabsId = tabsId;
        data.config.tagName = tagName;
        this.canvasDesign.addTabNodeById(tabsId, data);
        return;
      }
      this.canvasDesign.addNode(data);
    },
    /**
     * @param data 节点信息
     * @param event 事件信息
     * @description 利用当前节点信息与事件放置元素进行对比更新排序位置
     */
    updateElePos(data, event) {
      const toElement = event.toElement;
      if (data.config.tabsId) {
        const formChildren = document.getElementById(data.config.tabsId).querySelector('.el-row').children;
        const curEleIndex = this.canvasDesign.findTabNodeIndexById(data.config.id);
        Array.from(formChildren).forEach((child, index) => {
          if (child.contains(toElement)) {
            let moveIndex = this.canvasDesign.findTabNodeIndexById(child.children[0].getAttribute('id'));
            // 根据鼠标位置讨论元素放置位置
            if (Math.abs(event.offsetY) > child.children[0].offsetHeight / 2) {
              moveIndex++;
            }
            this.canvasDesign.updateTabElePos(data.config.tabsId, moveIndex, curEleIndex);
          }
        });
      } else {
        const formChildren = this.$refs.DesignCanvas.$el.children[0].children[0].children;
        const curEleIndex = this.canvasDesign.findNodeIndexById(data.config.id);
        Array.from(formChildren).forEach((child, index) => {
          if (child.contains(toElement)) {
            let moveIndex = index;
            // 根据鼠标位置讨论元素放置位置
            if (Math.abs(event.offsetY) > child.children[0].offsetHeight / 2) {
              moveIndex++;
            }
            this.canvasDesign.updatePos(moveIndex, curEleIndex);
          }
        });
      }
    },
    /**
     * @param data 节点信息
     * @param tabsId 选项卡id
     * @param tagName tab名
     */
    chooseEle(config, tabsId, tagName) {
      // 异步包装，避免配置修改失效
      setTimeout(() => {
        // 获取待编辑组件配置
        this.attrConfig = this.$utils.deepClone(config);
        console.log(this.attrConfig, 333);
        // 选中当前组件
        this.canvasDesign.batchUpdate('choosed', false);
        this.tabsId = tabsId;
        this.tabsTagName = tagName;
        if (!tabsId) {
          const curNode = this.canvasDesign.findNodeById(config.id);
          curNode.config.choosed = true;
        } else {
          const curNode = this.canvasDesign.findTabNodeById(config.id);
          curNode.config.choosed = true;
        }
        this.$eventBus.$emit('attrEdit', this.attrConfig);
      });
    },
    setEleAttr(id, attr, attrVal) {
      if (this.tabsId) {
        const curNode = this.canvasDesign.findTabNodeById(id);
        curNode.config[attr] = attrVal;
      } else {
        const curNode = this.canvasDesign.findNodeById(id);
        curNode.config[attr] = attrVal;
      }
    },
    removeCurNode(id) {
      id = typeof id === 'string' ? id : this.$refs.DesignConfig.attrConfig.id;
      if (this.tabsId) {
        const curTabsNode = this.canvasDesign.findNodeById(this.tabsId);
        const tabNodeEleIndex = curTabsNode.config.eleList.findIndex(ele => ele.config.id === id);
        curTabsNode.config.eleList.splice(tabNodeEleIndex, 1);
        return;
      }
      this.canvasDesign.removeNodeById(id);
    },
    previewForm() {
      window.open(`#/ComponentsPreview?id=${this.designId}&isPreview=true`);
    },
    save() {
      axios({
        method: 'POST',
        url: '/api/plugin/manage/type/edit',
        data: {
          id: this.designId,
          dom: JSON.stringify(this.canvasDesign.eleList)
        }
      }).then(r => {
        const res = r.data;
        if (res.success) {
          this.$message.success('保存成功!');
        }
      });
    },
    submit() {
      console.log(this.$refs.DesignCanvas.submit());
    },
    updateFormConfig() {
      this.$utils.updateFormConfig(this.canvasDesign.eleList);
    },
    getTableColumn(events) {
      let tableColumn = [];
      let eles = events.map(event => this.canvasDesign.findNodeByIdDeep(event.ele));
      eles.forEach(ele => {
        if (ele.key === 'table') {
          ele.config.tableColumnConfig.forEach(config => {
            tableColumn.push({ label: config.label, prop: config.prop });
          });
        }
      });
      this.$eventBus.$emit('sendTableColumn', tableColumn);
    }
  }
};
</script>
<style lang="scss" scoped>
.design-tool {
  width: 100%;
  height: 100%;
  .comp-msg {
    height: 39px;
    line-height: 39px;
    padding: 0 5px;
    border-bottom: 1px solid #808353;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  .design-container {
    width: 100%;
    height: calc(100% - 40px);
    display: flex;
  }
}
</style>

