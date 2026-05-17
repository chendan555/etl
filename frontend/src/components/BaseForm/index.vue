<template>
  <div>
    <el-form
      class="base-form"
      ref="baseForm"
      :model="ruleForm"
      :rules="formItemRuleConfig"
      label-width="200px"
      size="small"
    >
      <el-row>
        <el-col
          v-for="(formItem, index) in crtFormItemConfig"
          :span="formItem.span"
          :key="formItem.valueKey + index+Math.random()"
        >
          <el-form-item
            v-if="formItem.showCondition ? ruleForm[formItem.showCondition.key] === formItem.showCondition.value : true"
            :label="formItem.label"
            :label-width="formItem.labelWidth"
            :style="formItem.style"
            :prop="formItem.valueKey"
          >
            <mixin-components
              key="index"
              v-if="!formPreview"
              :inputLength="formItem.inputLength"
              :width="formItem.width"
              :ruleForm="ruleForm"
              :valueKey="formItem.valueKey"
              :disabledKey="formItem.disabledKey"
              :nameKey="formItem.nameKey"
              :options="formItem.options"
              :treeProps="formItem.treeProps"
              :loadNode="formItem.loadNode"
              :lazy="formItem.lazy"
              :otherOptions="formItem.otherOptions"
              :optionsParams="formItem.optionsParams"
              :refresh="formItem.refresh"
              :optionsConfig="formItem.optionsConfig"
              :optionsList="formItem.optionsList"
              :treeData="formItem.treeData"
              :tableData="tableData"
              :treeConfig="formItem.treeConfig"
              :type="formItem.type"
              :btnName="formItem.btnName"
              :placeholder="formItem.placeholder"
              :inputType="formItem.inputType ? formItem.inputType : 'text'"
              :watch="formItem.watch"
              :disabled="formItem.disabled"
              :valueFormat="formItem.valueFormat"
              :autoUpload="formItem.autoUpload"
              :fileType="formItem.fileType"
              v-on="$listeners"
            />
            <span class="form-detail-label" v-else>{{ getFormItemValue(formItem) }}</span>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>
<script>
/**
 * Create by Leo
 * 基础组件
 * 基础配置表单组件
 * 用于页面弹框显示进行树形结构数据选择
 */
//  引入表单元素MixinComponents组件
import MixinComponents from '@/components/MixinComponents';
export default {
  name: 'baseForm',
  components: {
    MixinComponents
  },
  props: {
    /**
     * 表单配置项
     */
    formItemConfig: {
      type: Array,
      default: () => {
        return [];
      }
    },
    /**
     * 表单配置项对象
     */
    ruleForm: {
      type: Object,
      default: () => {
        return {};
      }
    },
    /**
     * 表单配置项验证配置
     */
    formItemRuleConfig: {
      type: Object,
      default: () => {
        return {};
      }
    },
    /**
     * 树形选择元素数据
     */
    treeData: {
      type: Array,
      default: () => {
        return [];
      }
    },
    /**
     * 通用表单预览
     */
    formPreview: {
      type: Boolean,
      default: false
    },
    /**
     * 表格数据
     */
    tableData: {
      type: Array,
      default: () => {
        return [];
      }
    }
  },
  data() {
    return {
      /**
       * 表单配置项对象键名
       */
      ruleFormValueKey: '',
      /**
       * 表单配置项对象名称关键字
       */
      ruleFormNameKey: '',
      /**
       * 当前表单配置
       */
      crtFormItemConfig: this.formItemConfig
    };
  },
  watch: {
    /**
     * 监听表单配置项
     */
    formItemConfig: {
      handler: function(val) {
        this.crtFormItemConfig = val;
      },
      deep: true
    }
  },
  beforeCreate() {
    this.crtFormItemConfig = [];
  },
  // mounted() {
  //   this.crtFormItemConfig.forEach(ele=>{
  //     if(typeof ele.options === 'string') {
  //       this.queryOptions(ele)
  //     }
  //   })
  // },
  methods: {
    /**
     * 根据接口配置项自动请求接口获取数据
     * @param ele 表单配置项元素
     */
    queryOptions(ele) {
      if (!ele.optionsParams.hasOwnProperty('nodeId')) {
        ele.optionsParams.nodeId = this.$store.state.nodeId;
      }
      API[ele.options](ele.optionsParams).then(data => {
        ele.optionsList = data.data.data ? data.data.data : data.data;
      });
    },
    /**
     * 表单验证结果
     */
    validate(isSubmit = true) {
      let vaild;
      this.$refs.baseForm.validate(valid => {
        if (isSubmit) {
          this.$emit('formValidate', valid);
        }
        vaild = valid;
      });
      return vaild;
    },
    /**
     * 获取表单配置项的值
     * @param formItem 表单配置项元素
     */
    getFormItemValue(formItem) {
      return formItem.nameKey ? this.ruleForm[formItem.nameKey] : this.ruleForm[formItem.valueKey];
    },
    /**
     * 获取字典值
     * @param key 字段名
     * @param val 字段值
     */
    getPropInDict(key, val) {
      let dictType = this.$common.common.getStorageOption(key, val);
      if (dictType) {
        return dictType.aname;
      }
    },
    /**
     * 重置表单
     */
    resetForm() {
      this.$refs.baseForm && this.$refs.baseForm.resetFields();
    }
  },
  destroyed() {
    this.crtFormItemConfig = [];
    // console.log(124)
  }
};
</script>

<style lang="scss" scoped>
.form-detail-label {
  color: #fff;
}
</style>
