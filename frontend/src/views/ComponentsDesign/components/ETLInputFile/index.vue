<template>
  <div>
    <el-button @click="getLocalFile">获取本地文件</el-button>
    <!-- <p class="inputFile" @click="getLocalFile">{{fileName}}</p> -->
    <input
      type="file"
      name="chooseFile"
      id="chooseFile"
      ref="chooseFile"
      accept=".xlsx, .xls, .txt, .xml, .csv, .dat"
      @change="handleChange"
      hidden
    >
  </div>
</template>
<script>
/**
 * @description 获取本地文件基础组件
 * @description 用于算子组件获取本地文件功能实现
 */
export default {
  name: 'ETLInputFile',
  props: {
    /**
     * @description 算子组件信息
     */
    itemConfig: {
      type: Object
    }
  },
  data() {
    return {
      fileName: ''
    };
  },
  methods: {
    /**
     * @description 获取本地文件点击事件
     */
    getLocalFile() {
      this.$refs.chooseFile.click();
    },
    /**
     * @description 选择文件后执行事件
     * @param file 文件数据
     */
    handleChange(file) {
      let fileContent = file.srcElement.files[0];
      this.fileName = fileContent.name;
      let emitData = {
        item: this.itemConfig,
        data: fileContent
      };
      // 文件数据提交到算子组件
      this.$emit('change', emitData);
    }
  }
};
</script>
<style lang="scss" scoped>
@import "./src/styles/theme.scss";
.inputFile{
  width: 100%;
  height: 40px;
  line-height: 40px;
  background: var(--inputBgClr, #1d2822);
  padding: 0 15px;
  color: #fff;
}
</style>


