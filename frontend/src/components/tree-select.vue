<template>
  <div class="tree-select-box" :id="innerId">
    <el-input
      size="mini"
      @focus="treeVisible = true"
      :style="{ width: _props.width }"
      v-model="input"
      readonly
      :placeholder="_props.placeholder"
    ></el-input>
    <i :class="dropIconClass" class="dropdonw-icon el-icon-caret-bottom"></i>
    <transition name="el-zoom-in-top">
      <div class="tree-box" v-show="treeVisible">
        <div class="tree-box-body">
          <!-- 树形控件 -->
          <el-tree
            @node-click="nodeClick"
            :highlight-current="true"
            default-expand-all
            :expand-on-click-node="false"
            style="width: 100%; height: 100%;"
            :data="_props.treeData"
            :props="_props.treeProps"
            node-key="id"
            ref="el-tree"
          ></el-tree>
        </div>

        <div v-if="_props.handleVisible" class="tree-box-footer">
          <el-button type="primary" @click="confirm" size="mini">确 认</el-button>
          <el-button type="info" @click="treeVisible = false" size="mini">取 消</el-button>
        </div>
      </div>
    </transition>
  </div>
</template>
<script>
/**
 *
 * Create by Leo
 * 基础组件
 * 基础下拉树形选择组件
 * 左右两栏式布局
 * 坐侧区域可控制显隐
 * 并减少一定的代码量
 * 统一设置弹框的配置项、确认按钮、取消按钮、关闭按钮等信息
 *
 */
//树状下拉选择器
export default {
  inheritAttrs: false,
  name: 'tree-select',
  props: {
    /**
     * 配置项
     */
    placeholder: {
      type: String,
      default: '请选择'
    },
    /**
     * 配置项
     */
    width: {
      type: String,
      default: '200px'
    },
    //树状数据
    /**
     * 配置项
     */
    treeData: {
      type: Array,
      default: () => []
    },
    //指定展示字段 默认为name
    /**
     * 配置项
     */
    treeProps: {
      type: Object,
      default: () => {
        return {
          label: 'name'
        };
      }
    },
    //是否显示操作按钮
    /**
     * 配置项
     */
    handleVisible: {
      type: Boolean,
      default: true
    },
    //指定读写字段 默认为id,需确认后端需要的值
    /**
     * 配置项
     */
    valueKey: {
      type: String,
      default: 'id'
    },

    //v-mdoel对应数据
    /**
     * 配置项
     */
    modelKey: String
  },
  model: {
    prop: 'modelKey',
    event: 'modelChange'
  },
  watch: {
    /**
     * 配置项
     */
    '_props.modelKey': {
      immediate: true,
      handler: function(val) {
        if (val) {
          this.selKey = val;
          if (this._props.treeData.length) {
            //绑定值发生变化 更新绑定后内容
            this.findKeyName();
          }
        }
      }
    },
    /**
     * 配置项
     */
    '_props.treeData': {
      //异步获取时 会存在树状数据延后
      immediate: true,
      handler: function(val) {
        if (val.length) {
          if (this._props.selKey) {
            this.findKeyName();
          }
        }
      }
    }
  },
  computed: {
    /**
     * 配置项
     */
    dropIconClass() {
      return { dropdown: this.treeVisible };
    }
  },
  mounted() {
    let _this = this;
    //生成随机id,做外部点击事件句柄
    this.innerId = this.radomId();
    this.$nextTick(() => {
      let innerDom = document.getElementById(this.innerId);
      let outsideClick = function(e) {
        if (innerDom) {
          if (!innerDom.contains(e.target)) {
            _this.treeVisible = false;
          }
        }
      };
      /**
       * 设置点击事件
       */
      document.body.addEventListener('click', outsideClick);
      this.$on('hook:beforeDestory', function() {
        document.body.removeEventListener('click', outsideClick);
      });
    });
  },
  data() {
    return {
      input: '',
      treeVisible: false,
      innerId: '',
      selKey: null
    };
  },
  methods: {
    radomId() {
      let str = '';
      let chartsList = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'];
      for (let i = 0; i < 10; i++) {
        str += [...chartsList].sort(() => Math.random() - 0.5)[0];
      }
      return str;
    },
    nodeClick(node) {
      //保存对应值
      this.selKey = node[this._props.valueKey];

      if (!this._props.handleVisible) {
        this.confirm();
      }
    },
    confirm() {
      if (!this.selKey) {
        this.$message.error('请选择有效内容！');
      } else {
        this.$emit('modelChange', this.selKey);
        this.treeVisible = false;
      }
    },
    findKeyName() {
      let selNode = this.flatBy(this._props.treeData).find(i => i[this._props.valueKey] === this.selKey);
      if (selNode) {
        this.input = selNode[this._props.treeProps.label];
        this.$nextTick(() => {
          this.$refs['el-tree'].setCurrentKey(selNode.id);
        });
      }
    },
    //降维方便查找数据
    flatBy(arr, key = 'children', list = []) {
      if (Object.prototype.toString.call(key) !== '[object String]') {
        throw new Error('The key must be string!');
      }
      for (let item of arr) {
        list.push(item);
        if (Object.prototype.toString.call(item) === '[object Object]' && item[key] && item[key].length) {
          this.flatBy(item[key], key, list);
        }
      }

      return list;
    }
  }
};
</script>
<style lang="scss" scoped>
@import '@/styles/theme.scss';
.tree-select-box {
  display: inline-block;
  position: relative;
  .dropdonw-icon {
    position: absolute;
    right: 2px;
    top: 7px;
    transform: rotate(0);
    transition: transform 0.3s;
  }
  .dropdown {
    transform: rotate(180deg);
  }
  .tree-box {
    position: absolute;
    top: 32px;
    width: 400px;
    height: 300px;
    box-shadow: 0 0 5px gray;
    z-index: 100;
    background-color: $contBg;
    padding: 10px;
    display: flex;
    flex-direction: column;

    ::v-deep .el-tree--highlight-current {
      .el-tree-node.is-current {
        > .el-tree-node__content {
          background-color: #bddaf9;
        }
      }
    }

    .tree-box-body {
      flex-grow: 1;
      height: 1px;
      padding-bottom: 10px;
    }
    .tree-box-footer {
      text-align: right;
      padding-top: 10px;
      border-top: 1px solid #dedede;
    }
  }
}
</style>
