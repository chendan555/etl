<template>
  <BaseDialog v-bind="$attrs" v-on="$listeners" @save="saveFileData">
    <BaseForm v-bind="{...formConfig}" ref="baseForm"></BaseForm>
  </BaseDialog>
</template>
<script lang="ts">
import Vue from 'vue';
import BaseDialog from '@/components/BaseDialog/index.vue';
import BaseForm from '@/components/BaseForm/index.vue';
import { dataCenterService } from '@/api/index.ts';
import config from './config';
export default Vue.extend({
  name: 'ConfigFile',
  components: { BaseDialog, BaseForm },
  props: {
    fileServerInfo: {
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
        ...config.fileDataFormConfig,
        ruleForm: Object.keys(this.fileServerInfo).length ? this.$utils.deepCopy(this.fileServerInfo) : this.$utils.deepCopy(config.fileDataFormConfig.ruleForm)
      }
    };
  },
  mounted() {
    console.log(this.formConfig);
  },
  methods: {
    saveFileData() {
      if (!(this.$refs.baseForm as any).validate()) {
        return;
      }
      let params = {
        ...this.formConfig.ruleForm,
        sourceId: this.dataCenterType
      };
      dataCenterService
        .saveFileServer(params)
        .then(({ data }: any) => {
          this.$utils.setMessage(data.success, data.result);
          this.$emit('closeDialog', true);
        })
        .catch();
    }
  }
});
</script>
