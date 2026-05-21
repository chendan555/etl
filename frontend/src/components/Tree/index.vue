<template>
  <div class="common-tree">
    <!-- 搜索框 -->
    <div class="tree-search" v-if="treeConfig.filterable">
      <el-input
        v-model="filterText"
        prefix-icon="el-icon-search"
        placeholder="输入关键词,回车搜索"
        @keyup.enter.native="keydownFilter"
      ></el-input>
      <!-- @change="filterChange" -->
    </div>
    <!-- 树 -->
    <div class="common-tree-el" :style="!treeConfig.filterable ? 'top:2px;height:99%' : ''">
      <el-tree
        :node-key="treeConfig.nodeKey?treeConfig.nodeKey:treeConfig.valueBy"
        v-if="treeDataList&&treeDataList.length"
        :data="treeDataList"
        :props="treeConfig.props"
        ref="treeRef"
        :load="loadNode"
        :accordion="treeConfig.accordion"
        :indent="treeConfig.indent"
        :lazy="treeConfig.lazy"
        :draggable="treeConfig.draggable"
        :autoExpandParent="treeConfig.autoExpandParent"
        :check-strictly="treeConfig.checkStrictly"
        :show-checkbox="treeConfig.showCheckbox"
        :default-expand-all="expandAll"
        :expand-on-click-node="treeConfig.expandOnClickNode"
        :default-expanded-keys="treeConfig.defaultExpandedKeys ? treeConfig.defaultExpandedKeys : []"
        :filter-node-method="filterNode"
        @node-click="nodeClick"
        @check="handleCheckChange"
        @check-change="checkChange"
        @current-change="currentChnage"
        @node-collapse="nodeCollapse"
      >
        <div class="custom-tree-node" slot-scope="{ node, data }">
          <span style="color: #fff;">{{ node.label }}</span>
          <!--添加-->
          <span @click.stop.prevent="treeAdd(node,data);" v-if="treeConfig.showAdd">
            <i class="el-icon-circle-plus-outline" style="color: #409EFF;margin-left: 15px"></i>
          </span>
          <!--修改-->
          <span
            @click.stop.prevent="treeEdit(node,data)"
            v-if="treeConfig.showEdit"
            style="margin: 0 5px;"
          >
            <i class="el-icon-edit" style="color: rosybrown;"></i>
          </span>
          <!--删除-->
          <span @click.stop.prevent="treeRemove(node,data)" v-if="treeConfig.showDelete">
            <i class="el-icon-delete" style="color: coral"></i>
          </span>
        </div>
      </el-tree>
    </div>
  </div>
</template>
<script>
/**
 *
 * Create by Leo
 * 基础组件
 * 基础表格布局组件
 * 左右两栏式布局
 * 坐侧区域可控制显隐
 * 并减少一定的代码量
 * 统一设置弹框的配置项、确认按钮、取消按钮、关闭按钮等信息
 *
 */
import API from '@/api/index';
export default {
  name: 'common-tree',
  props: {
    /**
     * 配置项
     */
    config: {
      type: Object,
      default: () => {}
    },
    /**
     * 配置项
     */
    treeData: {
      type: Array,
      default: () => []
    },
    /**
     * 配置项
     */
    type: {},
    /**
     * 配置项
     */
    value: ''
  },
  data() {
    return {
      /**
       * 配置项
       */
      treeDataList: [],
      /**
       * 配置项
       */
      expandAll: false,
      /**
       * 配置项
       */
      filterText: '',
      /**
       * 配置项
       */
      contantProps: {
        type: '', //设置type
        propsApi: {}, //自定义树形结构请求接口
        filterable: false, //是否可过滤
        showAdd: false, //显示新增按钮
        showDelete: false, //显示删除按钮
        showEdit: false, //显示编辑按钮
        showCheckbox: false, //显示复选框
        emptyText: '', //数据为空时展示的内容
        highlightCurrent: false, //
        defaultExpandAll: false, //默认展开全部
        expandOnClickNode: true,
        checkStrictly: false,
        checkedKeys: [], //默认选中节点的keys
        defaultExpandedKeys: [], //默认展开树
        accordion: false,
        isCludeChildren: true, //父节点全选，设置是否需要返回子节点数据
        indent: 16, //缩进
        lazy: false, //是否进行懒加载
        draggable: false, //是否可拖动
        autoExpandParent: true,
        leafKey: 'leaf',
        flag: false, //根据后台接口的需要，是否需要获取当前目前，true为只需要下级节点
        inputPlaceHolder: '请输入关键词', //过滤输入框的placeholder
        filterTextKey: 'name', //设置过滤框的字段，如果是懒加载时，将此字段传至后台进行过滤,
        loadParentId: 'pid', //懒加载，加载子节点数据时，需要传递父节点参数的参数名字;同时如果父节点全选，不需要返回子节点数据时有用
        valueBy: 'id', //设置值取自节点数据的哪一个字段,
        valueType: 'string', //设置值得类型
        searchTrigger: ['change', 'enter', 'blur'], //设置过滤的触发方式，默认为值改变、失去焦点、回车
        props: {
          label: 'name',
          children: 'children',
          disabled: false
        } //el-tree的字段映射配置
      }
    };
  },
  watch: {
    /**
     * 配置项
     */
    value: {
      immediate: true,
      deep: true,
      handler: function(v) {
        this.defaultCheckHandle(v ? v : '');
      }
    },
    /**
     * 配置项
     */
    treeData: {
      handler: function(data) {
        this.treeDataList = data;
      }
    },
    /**
     * 配置项
     */
    filterText(val) {
      this.$refs.treeRef.filter(val);
    }
  },
  computed: {
    /**
     * 配置项
     */
    treeConfig() {
      /**
       * 可以配置部分业务树数据，具体在config目录下面配置
       */
      // let importData = null;
      // try {
      //   importData = require(`./config/${this.config.type}`).default;
      // } catch (e) {
      //   importData = {};
      // }
      // let temp = this.$merge({}, this.contantProps, importData);
      return Object.assign({}, this.contantProps, this.config);
    }
  },
  created() {
    /**
     * 配置了treeData的优先级比设置了propsApi的优先级高，根据treeData或者propsApi来设置树的数据
     */
    if (!this.treeData.length && Object.keys(this.treeConfig.propsApi).length) {
      let params = this.treeConfig.propsApi.onceParams ? this.treeConfig.propsApi.onceParams : this.treeConfig.propsApi.params || {};
      // 判断是否有一次性参数，主要为了传递数据初始请求需要带过去的参数
      params = this.treeConfig.propsApi.onceParams ? Object.assign({}, params, this.treeConfig.propsApi.onceParams) : params;
      this.getTreeList(params, false, true);
    }
    if (this.treeData && this.treeData.length) {
      this.treeDataList = JSON.parse(JSON.stringify(this.treeData));
    }
    this.expandAll = this.treeConfig.defaultExpandAll;
  },
  methods: {
    //获取中文描述
    /**
     * 配置项
     * @param v
     * @param data
     */
    getDesc(v, data) {
      let tempArray = Array.isArray(v) ? v : typeof v === 'string' ? v.split(',') : [];
      if (!v || !tempArray.length) return;
      let temp = [];
      let _this = this;
      tempArray.forEach(value => {
        getDeepDesc(value, data);
      });
      /**
       * 递归设置节点
       */
      function getDeepDesc(val, list) {
        list.some(item => {
          if (item[_this.treeConfig.valueBy] === val) {
            temp.push(item[_this.treeConfig.props.label]);
            return true;
          } else {
            if (item[_this.treeConfig.props.children] && item[_this.treeConfig.props.children].length) {
              getDeepDesc(val, item[_this.treeConfig.props.children]);
            } else {
              return false;
            }
          }
        });
      }
      return temp.join(',');
    },
    /**
     * 默认选中的方法
     * @param keys 默认选中节点的key字，如果需要默认选中，则必须给节点设置key值
     * @param flag
     */
    defaultCheckHandle(keys, flag) {
      // let _this = this;
      if (Array.isArray(keys) && this.treeConfig.showCheckbox) {
        this.$nextTick(() => {
          this.$refs.treeRef && this.$refs.treeRef.setCheckedKeys(keys);
        });
      } else if (typeof keys === 'string') {
        if (this.treeConfig.showCheckbox) {
          let temp = keys.split(',');
          this.$nextTick(() => {
            this.$refs.treeRef && this.$refs.treeRef.setCheckedKeys(temp);
          });
        } else {
          this.$nextTick(() => {
            this.$refs.treeRef && this.$refs.treeRef.setCurrentKey(keys);
          });
        }
      }
    },
    //handleCheckChange事件回调
    /**
     * 配置项
     * @param checkedNodes
     * @param checkedKeys
     * @param halfCheckedNodes
     * @param halfCheckedKeys
     */
    handleCheckChange(checkedNodes, checkedKeys, halfCheckedNodes, halfCheckedKeys) {
      this.$emit('check', checkedNodes, checkedKeys, halfCheckedNodes, halfCheckedKeys);
    },
    //获取选中的节点并返回值
    /**
     * 配置项
     */
    getNodes() {
      let temp = this.$refs.treeRef ? this.$refs.treeRef.getCheckedNodes() : [];
      if (!this.treeConfig.isCludeChildren) {
        temp = temp.reduce((prev, item) => {
          let flag = temp.some(child => {
            return child[this.treeConfig.valueBy] === item[this.treeConfig.loadParentId];
          });
          !flag && prev.push(item);
          return prev;
        }, []);
      }
      let data = temp.reduce(
        (prev, item) => {
          prev[0].push(item[this.treeConfig.valueBy]);
          prev[1].push(item[this.treeConfig.props.label]);
          return prev;
        },
        [[], []]
      );
      this.$emit('input', this.treeConfig.valueType === 'array' ? data[0] : data[0].join(','));
      this.$emit('change', this.treeConfig.valueType === 'array' ? data[0] : data[0].join(','), data[1].join(','));
    },
    //checkChange事件回调
    /**
     * 配置项
     * @param data
     * @param selected
     * @param childSelected
     */
    checkChange(data, selected, childSelected) {
      if (selected) {
        this.$refs.treeRef.setCheckedNodes([data]); // 暂时只支持单选
      }

      this.$emit('checkChange', data, selected, childSelected);
      this.getNodes();
    },
    //点击节点触发的事件
    /**
     * 配置项
     * @param currentData
     * @param node
     */
    currentChnage(currentData, node) {
      this.$emit('currentChnage', currentData, node);
      !this.treeConfig.showCheckbox && this.$emit('input', currentData[this.treeConfig.valueBy]);
      !this.treeConfig.showCheckbox && this.$emit('change', currentData[this.treeConfig.valueBy], currentData[this.treeConfig.props.label]);
    },
    //点击节点触发的事件
    /**
     * 配置项
     * @param node
     */
    nodeClick(node) {
      this.$emit('nodeClick', node);
    },
    //收起节点
    /**
     * 配置项
     * @param data
     * @param node
     */
    nodeCollapse(data, node) {
      this.childNodes(node.childNodes);
    },
    //收起所有节点
    /**
     * 配置项
     * @param data
     */
    childNodes(data) {
      data.forEach(item => {
        if (item.childNodes.length) {
          this.childNodes(item.childNodes);
        }
        item.expanded = false;
      });
    },
    //添加按钮事件，添加成功之后并不需要重新请求数据，调用第三个回调形参即可
    /**
     * 配置项
     * @param node
     * @param data
     */
    treeAdd(node, data) {
      this.$emit('onAdd', node, data, data => {
        this.$refs.treeRef.append(data, node);
      });
    },
    //删除，删除成功之后并不需要重新请求数据，调用第三个回调形参即可
    /**
     * 配置项
     * @param node
     * @param data
     */
    treeRemove(node, data) {
      this.$emit('onRemove', node, data, data => {
        this.$refs.treeRef.remove(node);
      });
    },
    //编辑，编辑成功之后并不需要重新请求数据，调用第三个回调形参即可
    /**
     * 配置项
     * @param node
     * @param data
     */
    treeEdit(node, data) {
      this.$emit('onEdit', node, data, data => {
        this.$refs.treeRef.updateKeyChildren(node.key, data);
      });
    },
    //加载子数据的方法，lazy为true时有用
    /**
     * 配置项
     * @param node
     * @param resolve
     */
    loadNode(node, resolve) {
      let param = new Object();
      if (node.level === 0) {
        return resolve(this.treeDataList);
      }
      //判断是否需要其他的参数参与查询
      if (this.treeConfig.propsApi.params) {
        param = Object.assign({}, this.treeConfig.propsApi.params, {
          [this.treeConfig.loadParentId]: node.data[this.treeConfig.valueBy],
          [this.treeConfig.filterTextKey]: this.filterText
        });
      } else {
        param = {
          [this.treeConfig.loadParentId]: node.data[this.treeConfig.valueBy],
          [this.treeConfig.filterTextKey]: this.filterText
        };
      }
      API[this.treeConfig.propsApi.api](param).then(res => {
        if (res.data && res.data instanceof Array) {
          let data = res.data;
          if (this.treeConfig.showCheckbox && node.checked) {
            this.$nextTick(() => {
              this.$refs.treeRef && this.$refs.treeRef.setChecked(node.key, true, true);
            });
          }
          resolve(data);
        } else {
          resolve([]);
        }
      });
    },
    /**
     * lazy为true时的请求方法
     * @param data data为请求的除了name之外的参数
     * @param type type为设置是否需要默认展开全部的设置参数，与lazy和filterable配置搭配使用，在搜索框的事件调用处传递
     */
    getTreeList(data, type, flag) {
      let params = new Object();
      //如果是懒加载的话，是需要将input的值传过去进行查询,字段名默认为name
      if (this.treeConfig.lazy && this.treeConfig.filterable) {
        params = Object.assign({}, { [this.treeConfig.filterTextKey]: this.filterText }, data);
      } else {
        params = data;
      }
      API[this.treeConfig.propsApi.api](params).then(res => {
        if (res) {
          let data = res.data;
          this.treeDataList = this.treeConfig.needCludeArray ? [data] : data;
          if (this.treeConfig.lazy && this.treeConfig.filterable && type) {
            this.expandAll = true;
          }
          this.$nextTick(() => {
            this.defaultCheckHandle(this.value, flag ? true : false);
          });
        }
      });
      // this.$api[this.treeConfig.propsApi.api]
      //   [this.treeConfig.propsApi.fn](params)
      //   .then(res => {
      //     if (res) {
      //       let data = res.data.data;
      //       this.treeDataList = this.treeConfig.needCludeArray ? [data] : data;
      //       this.treeConfig.lazy && this.treeConfig.filterable && type
      //         ? (this.expandAll = true)
      //         : null;
      //       this.$nextTick(() => {
      //         this.defaultCheckHandle(this.value, flag ? true : false);
      //       });
      //     }
      //   })
      //   .catch(e => {});
    },
    //设置了filterable时有用
    /**
     * 配置项
     * @param value
     * @param data
     */
    filterNode(value, data) {
      if (!value) return true;
      let label = data[this.treeConfig.props.label].toUpperCase(); //通过树节点label值进行模糊查询 20210115改
      return label.indexOf(value.toUpperCase()) !== -1;
      // return data[this.treeConfig.filterTextKey].indexOf(value) !== -1;
    },
    //搜索框值改变时搜索，结合searchTrigger使用
    /**
     * 配置项
     * @param e
     */
    filterChange(e) {
      if (this.treeConfig.searchTrigger.indexOf('change') === -1) return;
      if (this.treeConfig.lazy) {
        this.treeDataList = [];
        this.expandAll = true;
        this.getTreeList(this.treeConfig.propsApi.params ? this.treeConfig.propsApi.params : {}, this.filterText ? true : false);
      } else {
        this.$refs['treeRef'].filter(this.filterText);
      }
    },
    //回车事件搜索，结合searchTrigger使用
    /**
     * 配置项
     * @param e
     */
    keydownFilter(e) {
      if (this.treeConfig.searchTrigger.indexOf('enter') === -1) return;
      this.$refs['treeRef'].filter(this.filterText);

      // 取消懒加载判断（输入值进行懒加载筛选可能导致大量后端请求，导致进程卡死） -20210617
      /* //如果是懒加载的话，是需要将input的值传过去进行查询,字段名默认为name
      if (this.treeConfig.lazy) {
        this.treeDataList = [];
        this.expandAll = true;
        this.getTreeList(
          this.treeConfig.propsApi.params
            ? this.treeConfig.propsApi.params
            : {},
          this.filterText ? true : false
        );
      } else {
        this.$refs["treeRef"].filter(this.filterText);
      } */
    },
    //失去焦点事件搜索，结合searchTrigger使用
    /**
     * 配置项
     * @param e
     */
    blurFilter(e) {
      if (this.treeConfig.searchTrigger.indexOf('blur') === -1) return;
      if (this.treeConfig.lazy) {
        this.treeDataList = [];
        this.expandAll = true;
        this.getTreeList(this.treeConfig.propsApi.params ? this.treeConfig.propsApi.params : {}, this.filterText ? true : false);
      } else {
        this.$refs['treeRef'].filter(this.filterText);
      }
    }
  }
};
</script>
<style scoped lang="scss">
.common-tree {
  padding: 0px 10px;
  position: relative;
  height: 100%;
  & ::v-deep .el-checkbox__input.is-checked .el-checkbox__inner,
  & ::v-deep .el-checkbox__input.is-indeterminate .el-checkbox__inner {
    background: var(--tree-checked);
    border-color: var(--tree-checked);
  }
  & ::v-deep .el-tree {
    background: none;
  }
  & ::v-deep .el-tree-node__content:hover {
    background-color: var(--tree-hover-bg);
  }
  & ::v-deep .is-current > .el-tree-node__content {
    background-color: var(--tree-hover-bg);
  }
  & ::v-deep .el-tree-node:focus > .el-tree-node__content {
    background: var(--tree-hover-bg);
  }
  & ::v-deep .tree-search {
    padding: 5px 5px;
    position: absolute;
    left: 0;
    top: 0;
    width: 97%;
    border: 1px solid #0e4a25;
  }

  & ::v-deep .el-input__inner {
    height: 30px;
    line-height: 30px;
  }
  & ::v-deep .el-input__icon {
    line-height: 30px;
  }
  .tree-search ::v-deep .el-input__inner {
    background: none;
    border-color: var(--tree-bg);
    color: #fff;
    width: 100% !important;
  }
  .tree-search >>> .el-input__icon {
    line-height: 30px !important;
  }
}

.common-tree-el {
  position: absolute;
  left: 0;
  top: 50px;
  min-width: 100%;
  height: calc(100% - 60px);
  overflow-y: auto;
}
</style>
