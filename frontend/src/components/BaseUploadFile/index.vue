<template>
  <div class="files">
    <div class="files-btn">
      <div class="files-input" :style="{'opacity':disabled?0.5:1}">
        {{btnName}}
        <input
          :style="{'cursor':disabled?'not-allowed':'pointer'}"
          :disabled="disabled"
          type="file"
          @change="uploadFile"
        >
      </div>
      <div class="files-name">{{this.selectedFile.name}}</div>
    </div>
  </div>
</template>
<script>
/**
 *
 * Create by Leo
 * 基础组件
 * 基础文件上传组件
 * 左右两栏式布局
 * 坐侧区域可控制显隐
 * 并减少一定的代码量
 * 统一设置弹框的配置项、确认按钮、取消按钮、关闭按钮等信息
 *
 */
export default {
  props: {
    /**
     * 配置项
     */
    btnName: {
      type: String,
      default: ''
    },
    /**
     * 配置项
     */
    disabled: {
      type: Boolean
    }
  },
  data() {
    return {
      selectedFile: { name: '' }
    };
  },
  methods: {
    /**
     * 上传文件
     * @param e
     */
    uploadFile(e) {
      let file = e.target.files[0];
      this.selectedFile = file;
      if (file) {
        this.$emit('uploadFile', file);
      }
    }
  }
};
</script>
<style lang="scss">
.files {
  font-size: 12px;
  .files-btn {
    display: flex;

    .files-input {
      background-color: #4095d1;
      border-color: #4095d1;
      position: relative;
      padding: 10px 20px;
      overflow: hidden;
      line-height: 1 !important;
      color: #fff;
      cursor: pointer;

      input {
        position: absolute;
        left: 0;
        top: 0;
        opacity: 0;
        cursor: pointer;
      }
    }

    .files-name {
      margin-left: 10px;
      color: #fff;
    }

    .files-tips {
      line-height: 30px;
      margin-left: 14px;
      color: #fff;
    }
  }

  .files-progress {
    margin: 10px 0;
  }

  .files-tag {
    margin: 10px 0;
    /*.el-tag{*/
    /*background: none;*/
    /*border: none;*/
    /*}*/
  }
}
</style>
