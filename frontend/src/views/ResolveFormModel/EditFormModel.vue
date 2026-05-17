<template>
  <base-dialog v-bind="$attrs" v-on="$listeners">
    <base-form v-bind="{...formConfig}"></base-form>
    <div class="configdiv">
      <word-analysis @sendFormModel="sendFormModel" v-bind="{...formModelItem}"></word-analysis>
    </div>
    <div class="footer" slot="footer">
      <el-button
        @click="saveFormModel"
        icon="el-icon-check"
        v-if="this.formConfig.ruleForm.formName"
      >保存</el-button>
    </div>
  </base-dialog>
</template>
<script lang="ts">
import Vue from 'vue';
import BaseDialog from '@/components/BaseDialog/index.vue';
import BaseForm from '@/components/BaseForm/index.vue';
import WordAnalysis from './wordAnalysis/index.vue';
import API from '@/api';
import config from './config';
export default Vue.extend({
  name: 'editFormModel',
  components: { BaseDialog, BaseForm, WordAnalysis },
  props: {
    formModelInfo: {
      type: Object,
      default: {}
    }
  },
  data() {
    return {
      formConfig: {
        formItemConfig: config.formConfig.formItemConfig,
        ruleForm: this.$utils.deepCopy(config.formConfig.ruleForm)
      },
      formModelItem: <any>null
    };
  },
  watch: {
    formModelInfo: {
      deep: true,
      handler(val) {
        this.formConfig.ruleForm = val;
        Object.keys(val).forEach((key: string) => {
          this.formConfig.ruleForm[key] = val[key];
        });
        this.formModelItem = { allConfig: JSON.parse(val.allConfig), itemFormName: val.formName, itemTableName: val.tableName };
      }
    }
  },
  mounted() {
    if (Object.keys(this.formModelInfo).length) {
      Object.keys(this.formModelInfo).forEach(key => {
        this.formConfig.ruleForm[key] = this.formModelInfo[key];
      });
      this.formModelItem = {
        allConfig: JSON.parse(this.formModelInfo.allConfig),
        itemFormName: this.formModelInfo.formName,
        itemTableName: this.formModelInfo.tableName
      };
    }
  },
  methods: {
    sendFormModel(formModel: any) {
      this.formConfig.ruleForm.formName = formModel.formName;
      this.formConfig.ruleForm.tableName = formModel.tableName;
      this.formConfig.ruleForm.formModel = formModel.formModel;
      this.formConfig.ruleForm.allConfig = JSON.stringify(formModel.allConfig);
    },
    saveFormModel() {
      let params = {
        ...this.formConfig.ruleForm,
        createTime: this.$utils.setTimeType()
      };
      console.log(params);
      API.resolveFormModel.saveResolveFormModel(params).then(({ data }: any) => {
        this.$utils.setMessage(data.success, data.result);
        this.closeDialog(true);
      });
    },
    closeDialog(bool?: boolean) {
      this.$emit('closeDialog', bool);
      this.formConfig.ruleForm = this.$utils.deepCopy(config.formConfig.ruleForm);
    }
  }
});
</script>
<style lang="scss" scoped>
.configdiv {
  width: 100%;
  height: calc(100% - 68px);
}
</style>
