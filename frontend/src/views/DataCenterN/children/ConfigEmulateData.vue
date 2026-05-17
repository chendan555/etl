<template>
  <BaseDialog v-bind="$attrs" v-on="$listeners" @save="saveEmulateData">
    <BaseForm v-bind="{...formConfig}"></BaseForm>
  </BaseDialog>
</template>
<script lang="ts">
import Vue from 'vue';
import BaseDialog from '@/components/BaseDialog/index.vue';
import BaseForm from '@/components/BaseForm/index.vue';
import { dataCenterService } from '@/api/index.ts';
import config from './config';
export default Vue.extend({
  name: 'ConfigEmulate',
  components: { BaseDialog, BaseForm },
  props: {
    emulateServerInfo: {
      type: Object,
      default: {}
    },
    dataCenterType: {
      type: Number,
      default: ''
    }
  },
  data() {
    return {
      formConfig: {
        ...config.emulateDataFormConfig,
        ruleForm: Object.keys(this.emulateServerInfo).length ? this.$utils.deepCopy(this.emulateServerInfo) : this.$utils.deepCopy(config.fileDataFormConfig.ruleForm)
      }
    };
  },
  methods: {
    saveEmulateData() {
      let params = {
        id: this.formConfig.ruleForm.id,
        name: this.formConfig.ruleForm.name,
        sourceUrl: this.formConfig.ruleForm.sourceUrl,
        sourceId: this.dataCenterType
      };
      dataCenterService
        .saveEmulateServer(params)
        .then(({ data }: any) => {
          this.$utils.setMessage(data.success, data.result);
          this.$emit('closeDialog', true);
        })
        .catch();
    }
  }
});
</script>
