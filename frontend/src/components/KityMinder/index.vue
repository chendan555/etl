<template>
  <div class="kityMinderdiv">
    <div class="navCol" v-if="showHandle">
      <div class="nodediv">
        <div class="nodeItem" v-for="svg in kmNodeList" :key="svg.shape">
          <div class="svgs" @dragstart="ev => handleDrag(ev, svg)" draggable="true">
            <svg-icon :icon-class="svg.name"></svg-icon>
            <p>{{svg.content}}</p>
          </div>
        </div>
      </div>
      <div class="handlediv">
        <div class="handleItem" v-for="handle in handleList" :key="handle.handle">
          <el-tooltip
            :content="handle.content"
            v-if="handle.children && handle.children.length"
            placement="top"
          >
            <el-dropdown @command="handleCommand">
              <el-button type="text">
                <svg-icon :icon-class="handle.svgIcon"/>
              </el-button>
              <el-dropdown-menu slot="dropdown" class="spreadTypeDrop">
                <el-dropdown-item
                  :command="item.value"
                  v-for="(item, index) in handle.children"
                  :key="item.value"
                >
                  <div class="imgItem" :style="{'background-position': `-${32 * index}px center`}"></div>
                  <p>{{item.label}}</p>
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </el-tooltip>
          <el-tooltip :content="handle.content" placement="top" v-else>
            <el-button @click="handleClick(handle.handle)" type="text">
              <svg-icon :icon-class="handle.svgIcon"/>
            </el-button>
          </el-tooltip>
        </div>
      </div>
    </div>
    <div class="kmContainer" id="kmContainer" @drop="addNode($event)" @dragover="dragoverHandle">
      <kityMinder
        ref="kityMinder"
        :importJson="importJson"
        @minder="minderHandle"
        :options="minderOptions"
      ></kityMinder>
    </div>
  </div>
</template>
<script lang="ts">
/**
 *
 * Create by Leo
 * 基础组件
 * 基础流程画布组件
 * 左右两栏式布局
 * 坐侧区域可控制显隐
 * 并减少一定的代码量
 * 统一设置弹框的配置项、确认按钮、取消按钮、关闭按钮等信息
 *
 */
import Vue from 'vue';
import config from './config';
import utils from '../../utils/index';
export default Vue.extend({
  name: 'kityMinderdiv',
  props: {
    /**
     * 配置项
     */
    nodeData: {
      type: Object,
      default: () => utils.deepCopy(config.importJson)
    },
    /**
     * 配置项
     */
    showHandle: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      /**
     * 配置项
     */
      kmNodeList: config.nodeList,
      /**
     * 配置项
     */
      handleList: config.handleList,
      /**
     * 配置项
     */
      importJson: this.nodeData,
      /**
     * 配置项
     */
      minderOptions: config.minderOptions,
      /**
     * 配置项
     */
      minder: <any>{},
      /**
     * 配置项
     */
      crtNode: <any>{},
      /**
     * 配置项
     */
      spreadNodes: 1
    };
  },
  methods: {
    /**
     * 获取节点
     */
    getNodes() {
      return this.minder.exportJson();
    },
    // 外部调用初始化数据
    /**
     * 配置项
     */
    initData(data: any) {
      if (data.hasOwnProperty('root')) {
        this.minder.importJson(data);
      } else {
        this.minder.importJson(utils.deepCopy(config.importJson));
      }
    },
    // kityMinder初始化
    /**
     * 配置项
     */
    minderHandle(minder: any, editor: any) {
      let _this = this;
      this.minder = minder;
      // 选中节点改变时触发，可获取当前切换节点
      minder.on('selectionchange', () => {
        this.selectHandle(minder);
      });
      this.rightMenuHandle(editor, minder);
    },
    // 获取当前选中的节点
    /**
     * 配置项
     * @param minder
     */
    selectHandle(minder: any) {
      let node = minder.getSelectedNode();
      this.crtNode = node;
      this.$emit('handleMinder', 'crtNode', this.crtNode);
    },
    /**
     * 画布命令控制
     * @param value
     */
    handleCommand(value: string) {
      this.minder.execCommand('Template', value);
    },
    /**
     * 点击事件控制
     * @param handle
     */
    handleClick(handle: string) {
      switch (handle) {
        // 复制
        case 'copy':
          this.minder.execCommand('Copy');
          this.$message.success('节点复制成功!');
          break;
        // 粘贴
        case 'paste':
          this.minder.execCommand('Paste');
          break;
        // 概要
        case 'createOutLine':
          this.createOutLine();
          break;
        // 展开收缩
        case 'spreadNode':
          this.minder.execCommand('ExpandToLevel', this.spreadNodes);
          this.$nextTick(() => {
            if (this.spreadNodes === 1) {
              this.spreadNodes = Infinity;
            } else {
              this.spreadNodes = 1;
            }
          });
          break;
        // 删除
        case 'removeNode':
          this.removeNode();
          break;
        case 'save':
          let allNodes = this.minder.exportJson();
          this.$emit('handleMinder', 'save', allNodes);
      }
    },
    // 设置概要节点
    /**
     * 配置项
     */
    createOutLine() {
      let nodeList = this.minder.getSelectedNodes();
      if (nodeList.length) {
        let parentList = nodeList.map((item: any) => {
          item.data.outId = item.data.id;
          return item.parent.data.id;
        });
        let singleParent = new Set(parentList);
        let parentNodeList: any = [];
        singleParent.forEach((node: any) => {
          parentNodeList.push(node);
        });
        if (parentNodeList.length === 1) {
          let allData = this.minder.exportJson();
          let parentNode = this.findNodeById(parentNodeList[0], [allData.root]);
          let containerNode = nodeList.map((item: any) => {
            return item.data.id;
          });
          parentNode.children.push({
            data: { ...config.outline, containerNode }
          });
          this.minder.importJson(allData);
        } else {
          this.$message.warning('请选择具有同一父节点的节点!');
        }
      } else {
        this.$message.warning('请选择节点!');
      }
    },
    // 通过id在指定列表中查找节点
    /**
     * 配置项
     */
    findNodeById(id: string, list: any) {
      let result = <any>null;
      // debugger;
      list.forEach((item: any) => {
        if (item.data.id === id) {
          result = item;
        }
        if (item.children && item.children.length) {
          result = result || this.findNodeById(id, item.children);
        }
      });
      return result;
    },
    // 调用kityminder提供的方法添加节点
    /**
     * 配置项
     */
    addNode(ev: any) {
      if (this.crtNode) {
        let node = JSON.parse(ev.dataTransfer.getData('node'));
        const { content, shape, nodeType } = node;
        let nodeInfo = {
          shape,
          nodeType,
          text: content,
          background: config.backgroundList[shape],
          padding: shape === 'circle' ? 6 : 26
        };
        this.minder.execCommand('AppendChildNode', {
          ...config.nodeStyle,
          ...nodeInfo
        });
      }
    },
    /* 
      右键菜单事件 
      @description 修改已有按钮的名称，添加按钮事件前回调和后回调
      @grammar buttonHook('main',label,beforeHook,afterHook);
      @param {String} label 需求修改右键菜单按钮名
      @param {String} beforeHook 按钮事件执行前回调，return false 阻止默认事件
      @param {String} afterHook 按钮事件执行后回调
    */
    rightMenuHandle(editor: any, minder: any) {
      // 添加同级节点
      editor.hotbox.buttonHook(
        'main',
        '同级',
        () => {
          if (this.crtNode.parent) {
            this.setNodeSelectedById(this.crtNode.parent.data.id);
            minder.execCommand('AppendChildNode', config.nodeStyle);
          } else {
            this.$message.warning('根节点不能添加同级节点！');
          }
          return false;
        },
        () => {}
      );
      // 添加下级节点
      editor.hotbox.buttonHook(
        'main',
        '下级',
        () => {
          minder.execCommand('AppendChildNode', config.nodeStyle);
          return false;
        },
        () => {}
      );
      // 删除当前节点，并向父组件返还当前删除节点信息
      editor.hotbox.buttonHook(
        'main',
        '删除',
        () => {
          this.removeNode();
          return false;
        },
        () => {}
      );
      // editor.hotbox.removeButton("main", "上级");
      // editor.hotbox.removeButton("main", "重做");
    },
    /**
     * 移除节点
     */
    removeNode() {
      this.$emit('handleMinder', 'delete', this.crtNode);
      this.minder.execCommand('RemoveNode');
    },
    // 通过ID设置画布节点选中
    /**
     * 配置项
     * @param id
     */
    setNodeSelectedById(id: string) {
      let currentSelectedNode = this.minder.getSelectedNodes().map((i: any) => {
        return i;
      });
      currentSelectedNode.forEach((item: any) => {
        this.minder.toggleSelect(item); // 清除画布中所有选中节点
      });
      this.minder.selectById([id]); // 通过ID设置节点选中
    },
    // 拖动添加节点
    /**
     * 配置项
     * @param ev
     * @param data
     */
    handleDrag(ev: DragEvent, data: any) {
      if (this.crtNode) {
        const { shape, nodeType, content } = data;
        let json = JSON.stringify({ shape, nodeType, content });
        ev.dataTransfer && ev.dataTransfer.setData('node', json);
      } else {
        this.$message.warning('请先选择要添加的父节点！');
      }
    },
    /**
     * 拖动事件
     * @param ev
     */
    dragoverHandle(ev: DragEvent) {
      ev.preventDefault();
    }
  }
});
</script>
<style lang="scss" scoped>
.kityMinderdiv {
  width: 100%;
  height: 100%;
  display: flex;
  .navCol {
    width: 60px;
    height: 100%;
    background: rgba(255, 255, 255, 0.1);
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    .nodediv {
      .nodeItem {
        text-align: center;
        margin-bottom: 10px;
        cursor: pointer;
        &:hover {
          color: #ffff00;
        }
        .svgs {
          font-size: 30px;
          p {
            font-size: 12px;
            user-select: none;
          }
        }
      }
    }
    .handlediv {
      .handleItem {
        text-align: center;
        margin-bottom: 10px;
        /deep/ .el-button {
          font-size: 20px;
        }
      }
    }
  }
}
</style>
<style lang="scss">
#kmContainer {
  flex: 1;
  height: 100%;
  .receiver.input {
    color: #333;
  }
}
</style>
