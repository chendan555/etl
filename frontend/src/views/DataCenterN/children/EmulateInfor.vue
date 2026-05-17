<template>
  <div class="emulateInfor">
    <BaseForm v-bind="{...formConfig}"></BaseForm>
  </div>
</template>
<script lang="ts">
import Vue from 'vue';
import config from './config';
import BaseForm from '@/components/BaseForm/index.vue';
export default Vue.extend({
  name: 'emulateInfor',
  components: { BaseForm },
  data() {
    return {
      formConfig: {
        ...config.emulateInforFormConfig,
        ruleForm: this.$utils.deepCopy(config.emulateInforFormConfig.ruleForm)
      }
    };
  },
  mounted() {
    this.$eventBus.$on('showEmulateInfor', (infor: any) => {
      this.formConfig.ruleForm = infor;
    });
  }
});
</script>
<style lang="scss" scoped>
.emulateInfor {
  width: 100%;
  height: 100%;
  padding: 10px;
}
</style>
