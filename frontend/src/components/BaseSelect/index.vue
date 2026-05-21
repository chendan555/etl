<template>
  <div class="common-select">
    <!-- 下拉树 -->
    <div class="tree-select" v-if="selectConfig.type === 'tree'">
      <el-popover
        id="popover"
        ref="treePopover"
        v-model="orgListVisible"
        placement="bottom-start"
        width="300"
        height="300"
        popper-class="tree-popover"
        trigger="click"
      >
        <trees
          :config="treeConfig"
          style="width:100%;height:278px;box-sizing: border-box;"
          v-model="currentValue"
          v-if="treeConfig.lazy || (optionsData && optionsData.length)"
          @change="changeValue"
          :props="{label: 'name'}"
          :treeData="optionsData"
        />
      </el-popover>
      <el-input
        v-model="currentLabel"
        v-popover:treePopover
        clearable
        :readonly="true"
        :title="currentLabel"
        class="base-select"
        maxlength="100"
        :placeholder="selectConfig.placeholder"
      />
    </div>
    <!-- 原生下拉 -->
    <div class="base-select" v-else>
      <el-select
        v-model="currentValue"
        :placeholder="selectConfig.placeholder"
        :filterable="selectConfig.filterable"
        :collapse-tags="selectConfig.collapseTags"
        :multiple="selectConfig.multiple"
        :disabled="selectConfig.disabled"
        :clearable="selectConfig.clearable"
        :readonly="selectConfig.readonly"
        :loading="selectConfig.loading"
        :loadingText="selectConfig.loadingText"
        :noMatchText="selectConfig.noMatchText"
        :noDataText="selectConfig.noDataText"
        :popperAppendToBody="selectConfig.popperAppendToBody"
        :multipleLimit="selectConfig.multipleLimit"
        :valueKey="selectConfig.valueKey"
        :autoComplete="selectConfig.autoComplete"
        :remoteMethod="remoteMethod"
        :filterMethod="filterMethod"
        @change="changeBaseSelect"
        @visible-change="visibleChange"
        @remove-tag="removeTag"
        @clear="clear"
      >
        <el-option
          v-for="(item, index) in optionsData"
          :key="`select-option-${index}`"
          :label="item[selectConfig.labelKey]"
          :value="item[selectConfig.valueKey]"
        ></el-option>
      </el-select>
    </div>
  </div>
</template>
<script>
/**
 *
 * Create by Leo
 * 基础组件
 * 基础下拉选择组件
 * 左右两栏式布局
 * 坐侧区域可控制显隐
 * 并减少一定的代码量
 * 统一设置弹框的配置项、确认按钮、取消按钮、关闭按钮等信息
 *
 */
/**
 * 下拉树数据源最好不要使用懒加载，如果要使用懒加载应该是在没有中文数据回显的情况下使用，不然在没有默认展示全部的情况
 * 下回显会出问题（如果设置了复选框，勾选状态将不能准确的显示，还会影响初始值）；
 */
import trees from '@/components/Tree';
export default {
  name: 'common-select',
  components: {
    trees
  },
  props: {
    /**
     * 绑定值
     */
    value: null,
    /**
     * 配置项
     */
    config: {
      type: Object,
      required: true
      // default: () => {}
    },
    /**
     * 下拉数据源，如果是非树类型则为必填
     */
    optionsData: {
      type: Array
      // default: () => []
    },
    /**
     * 远程搜索回调
     */
    remoteMethod: {
      type: Function,
      default: null
    },
    /**
     * 过滤方法回调
     */
    filterMethod: {
      type: Function,
      default: null
    }
  },
  mounted() {
    this.setcrtLabel(this.optionsData);
  },
  data() {
    return {
      /**
       *
       * 双向绑定的值
       *
       */
      currentValue: '',
      /**
       *
       * 展示值
       *
       */
      currentLabel: '',
      /**
       *
       * 下拉树下拉框显示控制变量
       *
       */
      orgListVisible: false,
      constantConfig: {
        /**
         *
         * 下拉类型。select:一般的下拉；tree:下拉树
         *
         */
        type: 'select',
        /**
         *
         * 下拉类型为tree时设置是否是懒加载
         *
         */
        lazy: false,
        /**
         *
         * 原生api
         */
        multiple: false, //
        /**
         *
         * 原生api
         */
        disabled: false, //
        /**
         *
         * 原生api，取值字段的key值
         */
        valueKey: 'value', //
        /**
         *
         * 描述字段的key值
         */
        labelKey: '', //
        /**
         *
         * 原生api
         */
        clearable: false, //原生api
        /**
         *
         * 原生api
         */
        collapseTags: false, //原生api
        /**
         *
         * 原生api
         */
        multipleLimit: 0, //原生api
        /**
         *
         * 原生api
         */
        name: null, //原生api,
        /**
         *
         * 设置父子关系依据字段
         */
        loadParentId: 'pAid', //
        /**
         *
         * 原生api
         */
        autoComplete: 'off', //原生api
        /**
         *
         * 原生api
         */
        placeholder: '请选择', //原生api
        /**
         *
         * 原生api
         */
        filterable: false, //原生api
        /**
         *
         * 原生api
         */
        remote: false, //原生api
        /**
         *
         * 原生api
         */
        isCludeChildren: false,
        /**
         *
         * 原生api
         */
        readonly: false, //原生api
        /**
         *
         * 原生api
         */
        loading: false, //原生api
        /**
         *
         * 原生api
         */
        loadingText: '加载中', //原生api
        /**
         *
         * 原生api
         */
        noMatchText: '无匹配数据', //原生api
        /**
         *
         * 原生api
         */
        noDataText: '无数据', //原生api
        /**
         *
         * 原生api
         */
        valueType: 'string',
        /**
         *
         * 原生api
         */
        popperAppendToBody: true, //原生api
        /**
         *
         * 原生api
         */
        props: {
          label: 'name',
          children: 'children'
        }, //下拉树可能需要的东西,
        /**
         *
         * 原生api
         */
        propsApi: {}
      }
    };
  },
  computed: {
    /**
     * 下拉基本配置
     */
    selectConfig() {
      return Object.assign({}, this.constantConfig, JSON.parse(JSON.stringify(this.config)));
    },
    /**
     * 下拉树配置
     */
    treeConfig() {
      return {
        propsApi: this.selectConfig.propsApi,
        lazy: this.selectConfig.lazy,
        filterable: this.selectConfig.filterable,
        showCheckbox: this.selectConfig.multiple,
        valueType: this.selectConfig.valueType,
        valueBy: this.selectConfig.valueKey,
        props: this.selectConfig.props,
        expandOnClickNode: this.selectConfig.multiple,
        checkStrictly: this.selectConfig.checkStrictly,
        isCludeChildren: this.selectConfig.isCludeChildren,
        loadParentId: this.selectConfig.loadParentId
      };
    }
  },
  watch: {
    /**
     *
     * 监听value
     */
    value: {
      immediate: true,
      handler(value) {
        if (this.selectConfig.type !== 'tree') return;
        this.currentValue = value;
      }
    }
  },
  methods: {
    /**
     *
     * 设置默认值
     * @param arr
     */
    setcrtLabel(arr) {
      arr.forEach(ele => {
        if (ele.id === this.currentValue) {
          this.currentLabel = ele.categoriesName;
        } else {
          if (ele.children.length) {
            this.setcrtLabel(ele.children);
          }
        }
      });
    },
    /**
     * 下拉树change事件回调
     * @param v value值
     * @param desc label值
     */
    changeValue(v, desc) {
      if (!this.selectConfig.multiple) {
        this.orgListVisible = false;
      }
      this.currentLabel = desc;
      this.$emit('input', this.currentValue);
      this.$emit('change', this.currentValue, this.currentLabel);
      this.orgListVisible = false; // 关闭弹窗
    },
    /**
     * 原生visibleChange事件回调
     * @param data
     */
    visibleChange(data) {
      this.$emit('visibleChange', data);
    },
    /**
     * 原生select 移除标签事件回调
     * @param tag
     */
    removeTag(tag) {
      this.$emit('removeTag', tag);
    },
    /**
     * 原生select change事件回调
     * @param data
     */
    changeBaseSelect(data) {
      this.$emit('input', data);
    },
    /**
     * 清除事件回调
     */
    clear() {
      this.$emit('clear');
    }
  }
};
</script>
<style lang="scss" scoped>
.common-select {
  width: 100%;
  display: inline-block;
  vertical-align: middle;
}
.tree-select {
  position: relative;
  & ::v-deep span {
    padding: 0 !important;
  }
}
.base-select {
  & ::v-deep .el-input__inner,
  & ::v-deep .el-input__icon {
    // height: 32px;
    // line-height: 32px;
  }
}
</style>
