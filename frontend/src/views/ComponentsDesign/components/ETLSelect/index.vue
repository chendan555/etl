<template>
  <el-select
    filterable
    :multiple="config.multiple"
    @change="valueChange"
    v-model="modelValue"
    placeholder="请选择"
    clearable
    @mousedown.native.stop
  >
    <el-option
      v-for="(option, idx) in optionList"
      :key="idx"
      :label="getOptionLabel(option)"
      :value="getOptionValue(option)"
    ></el-option>
  </el-select>
</template>
<script>
export default {
  name: 'ETLSelect',
  props: {
    value: {
      type: String
    },
    config: {
      type: Object
    }
  },
  data() {
    return {
      modelValue: ''
    };
  },
  computed: {
    optionList() {
      const opts = this.config.options;
      return Array.isArray(opts) ? opts : [];
    }
  },
  mounted() {
    this.modelValue = this.value || '';
  },
  watch: {
    value(newVal) {
      if (newVal !== this.modelValue) {
        this.modelValue = newVal || '';
      }
    }
  },
  methods: {
    getOptionLabel(option) {
      if (typeof option === 'string') return option;
      const oc = this.config.optionsConfig || {};
      return option[oc.name] || option.label || option.name || option.text || '';
    },
    getOptionValue(option) {
      if (typeof option === 'string') return option;
      const oc = this.config.optionsConfig || {};
      return String(option[oc.value] || option.value || option.key || option.name || '');
    },
    valueChange(val) {
      this.$emit('input', val);
      this.$emit('change', val);
    },
    updateOptions() {
      this.$eventBus.$emit('updateOptions', this.config);
    }
  }
};
</script>
<style lang="scss" scoped>
.etl-monaco {
  height: 300px;
}
</style>
