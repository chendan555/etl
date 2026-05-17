<template>
  <base-dialog v-bind="$attrs" v-on="$listeners" @save="saveTableConfig">
    <base-table v-bind="{...tableConfig}" @selectionChange="selectionChange"></base-table>
  </base-dialog>
</template>
<script lang="ts">
import Vue from 'vue';
import BaseDialog from '@/components/BaseDialog/index.vue';
import BaseTable from '@/components/BaseTable/index.vue';
// import axios from '@/api/base/http';
// import config from './config';
export default Vue.extend({
  name: 'dialogReceiveData',
  components: { BaseDialog, BaseTable },
  props: {
    tableConfig: {
      type: Object,
      default: {}
    }
  },
  data() {
    return {
      tableConfig: {
        height: '600px',
        ...this.tableConfig
      }
    };
  },
  mounted() {},
  methods: {
    saveTableConfig() {},
    selectionChange(selections: any) {
      console.log(selections);
    }
  }
});
</script>
<style lang="scss" scoped>
</style>
