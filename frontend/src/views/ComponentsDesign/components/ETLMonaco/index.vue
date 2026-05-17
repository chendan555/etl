<template>
  <div class="etl-monaco" :id="`monaco${monacoId}`"></div>
</template>
<script>
/**
 * @description 编辑框功能面板基础组件
 * @description 用于算子组件编辑功能实现
 */
import * as monaco from 'monaco-editor/esm/vs/editor/editor.api.js';
import 'monaco-editor/esm/vs/basic-languages/mysql/mysql.contribution.js';
export default {
  name: 'ETLMonaco',
  props: {
    // 编辑框元素id
    monacoId: {
      type: String
    },
    // 编辑框中默认值
    value: {
      type: String
    }
  },
  data() {
    return {
      code: '',
      monacoInstance: null
    };
  },
  mounted() {
    // 初始化编辑框
    this.monacoInstance = monaco.editor.create(document.getElementById(`monaco${this.monacoId}`), {
      value: this.value,
      language: 'mysql',
      theme: 'vs-dark',
      automaticLayout: true
    });
    // let sqlString = `SELECT FROM`;
    // this.setValue(sqlString);
    // 绑定编辑框输入及修改事件
    this.monacoInstance.onDidChangeModelContent(e => {
      this.$emit('input', this.monacoInstance.getValue());
      this.$emit('change');
    });
  },
  methods: {
    /**
     * @description 设置编辑框值
     * @param str 编辑框值
     */
    setValue(str) {
      this.monacoInstance.setValue(str);
    }
  }
};
</script>
<style lang="scss" scoped>
.etl-monaco {
  height: 300px;
}
</style>
