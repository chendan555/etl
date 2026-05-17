<template>
  <base-dialog v-bind="$attrs" v-on="$listeners" @save="saveTableConfig">
    <word-analysis v-on="$listeners"></word-analysis>
    <div slot="footer"></div>
  </base-dialog>
</template>
<script lang="ts">
import Vue from 'vue';
import BaseDialog from '@/components/BaseDialog/index.vue';
import WordAnalysis from './wordAnalysis/index.vue';

export default Vue.extend({
  name: 'setFormModel',
  components: { BaseDialog, WordAnalysis },
  data() {
    return {};
  },
  methods: {
    saveTableConfig() {},
    closeDialog() {
      this.$emit('closeDialog');
    }
  }
});
</script>
