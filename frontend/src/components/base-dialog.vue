<template>
  <el-dialog custom-class="base-dialog" v-on="$listeners" v-bind="_attrs" :visible="_props.visible">
    <div v-loading="_props.loading" class="dialog-body" :class="layout">
      <el-form
        v-if="!$slots['dialog-body']"
        :model="_props.bindModel"
        ref="form"
        :label-width="_attrs['label-width']"
      >
        <el-form-item
          :rules="[
            { required: !!item.validate && !!item.validate.required, message: !!item.validate ? item.validate.message || item.label + '为必填项！' : '' },
            { validator: item.validate && item.validate.validator ? item.validate.validator : null}
          ]"
          :prop="item.bindKey"
          v-for="(item, index) in _props.formConfig"
          :label="item.label || ''"
          :key="index"
          :style="computedStyle(item)"
        >
          <!-- 输入框 -->
          <el-input
            size="small"
            v-model.trim="_props.bindModel[item.bindKey]"
            v-if="item.type === 'input'"
            :placeholder="item.placeholder || '请输入'"
          ></el-input>
          <!-- 密码框 -->
          <el-input
            size="small"
            type="password"
            v-model.trim="_props.bindModel[item.bindKey]"
            v-if="item.type === 'input-password'"
            :placeholder="item.placeholder || '请输入密码！'"
          ></el-input>

          <!-- 文本域 -->
          <el-input
            size="small"
            type="textarea"
            v-model.trim="_props.bindModel[item.bindKey]"
            v-if="item.type === 'textarea'"
            :placeholder="item.placeholder || '请输入'"
          ></el-input>
          <!-- 单选框 -->
          <el-radio-group v-model="_props.bindModel[item.bindKey]" v-if="item.type === 'radio'">
            <el-radio
              v-for="(rd, index) in item.options.data"
              :label="rd[item.options.value]"
              :key="index"
            >{{ rd[item.options.label] }}</el-radio>
          </el-radio-group>
          <!-- 多选框 -->
          <el-checkbox-group
            v-model="_props.bindModel[item.bindKey]"
            v-if="item.type === 'checkbox'"
          >
            <el-checkbox
              v-for="(ck, index) in item.options.data"
              :label="ck[item.options.value]"
              :key="index"
            >{{ ck[item.options.label] }}</el-checkbox>
          </el-checkbox-group>
          <!-- 下拉选择 -->
          <el-select
            filterable
            size="small"
            v-model="_props.bindModel[item.bindKey]"
            v-if="item.type === 'select'"
            :placeholder="item.placeholder || '请选择'"
          >
            <el-option
              v-for="(op, index) in item.options.data"
              :key="index"
              :label="op[item.options.label]"
              :value="op[item.options.value]"
            ></el-option>
          </el-select>
          <!-- 标签 -->
          <el-select
            filterable
            multiple
            allow-create
            default-first-option
            size="small"
            v-model="_props.bindModel[item.bindKey]"
            v-if="item.type === 'tag'"
            :placeholder="item.placeholder || '请选择或者输入标签'"
          >
            <el-option
              v-for="(op, index) in item.options.data"
              :key="index"
              :label="op[item.options.label]"
              :value="op[item.options.value]"
            ></el-option>
          </el-select>
          <!-- 日期选择器 -->
          <el-date-picker
            size="mini"
            :picker-options="limitMaxDate"
            value-format="yyyy-MM-dd"
            type="date"
            :placeholder="item.placeholder || '请选择日期'"
            v-if="item.type === 'date'"
            v-model="_props.bindModel[item.bindKey]"
          ></el-date-picker>
          <!-- 时间选择器 -->
          <el-time-picker
            size="mini"
            :placeholder="item.placeholder || '请选择时间'"
            v-if="item.type === 'time'"
            v-model="_props.bindModel[item.bindKey]"
          ></el-time-picker>
          <!-- 时间日期选择器-->
          <el-date-picker
            size="mini"
            :picker-options="limitMaxDate"
            value-format="yyyy-MM-dd hh:mm:ss"
            type="datetimerange"
            :placeholder="item.placeholder || '请选择日期时间段'"
            v-if="item.type === 'time&date'"
            v-model="_props.bindModel[item.bindKey]"
          ></el-date-picker>

          <div v-if="item.type === 'trees'">
            <common-select
              v-model="_props.bindModel[item.bindKey]"
              :optionsData="_props.treeData"
              :config="_props.treeConfig"
              filterable
              @change="handleOrgSelected"
            ></common-select>
          </div>
        </el-form-item>
      </el-form>
      <slot name="dialog-body" v-else></slot>
    </div>
    <div slot="footer">
      <el-button size="mini" @click="handleCustom">{{ _props.btnConfig.custom }}</el-button>
      <el-button
        type="primary"
        size="mini"
        @click="confirm"
        :disabled="_props.btnConfig.confirm.disabled"
      >{{ _props.btnConfig.confirm.label }}</el-button>
      <el-button
        type="info"
        size="mini"
        @click="$emit('update:visible', false)"
      >{{ _props.btnConfig.cancel }}</el-button>
    </div>
  </el-dialog>
</template>
<script>
/**
 *
 * Create by Leo
 * 基础组件
 * 基础弹框组件
 * 左右两栏式布局
 * 坐侧区域可控制显隐
 * 并减少一定的代码量
 * 统一设置弹框的配置项、确认按钮、取消按钮、关闭按钮等信息
 *
 */
import commonSelect from '@/components/BaseSelect/index.vue';
export default {
  inheritAttrs: false,
  name: 'base-dialog',
  components: {
    commonSelect
  },
  props: {
    /**
     * 配置项
     */
    visible: {
      type: Boolean,
      default: false
    },
    /**
     * 配置项
     */
    loading: {
      type: Boolean,
      default: false
    },
    //表单配置信息
    /**
     * 配置项
     */
    formConfig: {
      type: Array,
      default: () => []
    },
    //表单绑值对象,内部填值变化，直接反映到外部对象，弹窗内部只做填值，不做业务操作
    /**
     * 配置项
     */
    bindModel: {
      type: Object,
      default: () => ({})
    },
    //按钮文字信息配置
    /**
     * 配置项
     */
    btnConfig: {
      type: Object,
      default: () => ({
        confirm: {
          label: '确定',
          disabled: false
        },
        cancel: '取消',
        custom: '自定义'
      })
    },
    //可选值为half和diy，默认full,half为半式布局，diy为自定义布局，full为填充布局
    /**
     * 配置项
     */
    layout: {
      type: String,
      default: 'full'
    },
    // 树形组件值
    /**
     * 配置项
     */
    treeData: {
      type: Array,
      default: () => {
        return [];
      }
    },
    // 树形组件配置
    /**
     * 配置项
     */
    treeConfig: {
      type: Object,
      default: () => {
        return {};
      }
    }
  },
  computed: {
    /**
     * 配置项
     */
    _attrs() {
      const defaultAttrs = {
        'close-on-click-modal': false,
        'append-to-body': true,
        'close-on-press-escape': false,
        'label-width': '80px'
      };

      return { ...defaultAttrs, ...this.$attrs };
    }
  },
  data() {
    return {
      /**
       * 配置项
       */
      limitMaxDate: {
        disabledDate: t => {
          return new Date(t) > new Date();
        }
      }
    };
  },
  methods: {
    /**
     * 样式控制
     */
    computedStyle(item) {
      if (this._props.layout === 'diy') {
        return {
          width: `${item.width} !important`
        };
      }
      return '';
    },
    /**
     * 确认
     */
    confirm() {
      if (this.$slots['dialog-body']) {
        this.$emit('confirm');
      } else {
        this.$refs.form.validate(result => {
          if (result) {
            this.$emit('confirm');
          }
        });
      }
    },
    /**
     * 控制
     */
    handleCustom() {
      if (this.$slots['dialog-body']) {
        this.$emit('custom');
      } else {
        this.$refs.form.validate(result => {
          if (result) {
            this.$emit('custom');
          }
        });
      }
    },
    /**
     * 配置项
     */
    handleOrgSelected() {
      this.$emit('selectedChange', this.valueKey);
    }
  },
  mounted() {
    console.log(this.treeData);
  }
};
</script>
<style lang="scss" scoped>
@import '@/styles/theme.scss';
.base-dialog {
  .dialog-body {
    /deep/ .el-form {
      .el-form-item {
        display: inline-flex;
        align-items: center;
        width: 100%;
        > .el-form-item__label {
          color: $normalWordsClr;
        }
        > .el-form-item__content {
          flex-grow: 1;
          width: 2px;
          margin-left: 10px !important;
        }
        .el-range-input {
          background-color: $inputBgClr;
          color: #ffffff;
        }
        .el-range-separator {
          color: #ffffff;
        }
        .el-select {
          width: 100%;
          .el-select__input {
            color: #ffffff;
          }
        }
      }
    }
  }
}

.half {
  /deep/ .el-form {
    .el-form-item {
      width: 48% !important;
      &:nth-child(even) {
        margin-left: 4%;
      }
    }
  }
}
/deep/ .el-dialog__header {
  border-bottom: 1px solid #394e39;
  .el-icon-close {
    position: relative;
    top: -4px;
    color: #ffffff;
  }
}
/deep/ .el-dialog__body {
  // background-color: #1f2b25 !important ;
}
/deep/ .el-popper .popper__arrow::after {
  top: 0;
}
</style>
