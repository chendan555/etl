<template>
  <base-dialog v-bind="$attrs" v-on="$listeners" @save="saveCronConfig">
    <div class="crondiv">
      <Cron ref="cronComp" :value="cronValue"></Cron>
    </div>
  </base-dialog>
</template>
<script >
// import Vue from 'vue';
import BaseDialog from '@/components/BaseDialog/index.vue';
import Cron from '@/components/Cron/index.vue';
// import { param } from '@/utils';
export default {
  name: 'dialogCron',
  components: { BaseDialog, Cron },
  props: {
    cronExpression: {
      type: String,
      default() {
        return '* * * * * ? *';
      }
    }
  },
  data() {
    return {
      cronValue: this.cronExpression
    };
  },
  methods: {
    saveCronConfig() {
      this.cronValue = this.$refs.cronComp.value_;
      this.$emit('closeDialog', this.cronValue);
    }
  }
};
</script>
<style lang="scss" scoped>
.crondiv {
  width: 100%;
  height: calc(100% - 40px);
}
.pagediv {
  width: 100%;
  height: 40px;
}
</style>
