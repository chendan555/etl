<template>
  <div class="mergeTxtDialog">
    <base-form v-bind="{...formConfig}"></base-form>
    <div class="txtFileItem" v-for="item in mergeConfigList" :key="item.fileName">
      <p class="close">
        <i class="el-icon-close"></i>
      </p>
      <div class="infoItem">
        <p class="label">文件名称</p>
        <p class="value">{{item.filePath}}</p>
      </div>
      <div class="infoItem">
        <p class="label">合并列序号</p>
        <p class="value">
          <el-select v-model="item.mergeColumnIndex" multiple placeholder="请选择" size="small">
            <el-option v-for="item in item.columns" :key="item" :label="item" :value="item"></el-option>
          </el-select>
        </p>
      </div>
    </div>
    <el-button type="primary" @click="getLocalFiles" size="small">添加本地文件</el-button>
  </div>
</template>
<script lang="ts">
import Vue from 'vue';
import BaseForm from '@/components/BaseForm/index.vue';
// import { configRelation } from '@/api/index.ts';
import config from './config';
export default Vue.extend({
  name: 'mergeTxtDialog',
  components: { BaseForm },
  props: {
    ruleForm: {
      type: Object,
      default: () => {
        return {};
      }
    }
  },
  data() {
    return {
      formConfig: {
        ruleForm: this.ruleForm,
        formItemConfig: config.mergeTxtFormConfig.formItemConfig
      },
      mergeConfigList: <any>[]
    };
  },
  methods: {
    // 展示文件选择器
    getLocalFiles() {
      const input = document.createElement('input');
      input.type = 'file';
      input.accept = '.txt';
      const self = this;
      const cleanup = () => {
        if (input.parentNode) document.body.removeChild(input);
      };
      input.addEventListener('change', function(e: any) {
        self.getFilesPath(e);
        cleanup();
      });
      window.addEventListener('focus', function onFocus() {
        setTimeout(() => {
          if (input.files && input.files.length === 0) {
            cleanup();
          }
          window.removeEventListener('focus', onFocus);
        }, 500);
      });
      document.body.appendChild(input);
      input.click();
    },
    // 上传文件
    getFilesPath(e: any) {
      const loading = this.$loading({
        text: '上传中...',
        target: '.gx-taskList-body',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      });
      let formData = new FormData();
      formData.append('file', e.target.files[0]);
      formData.append('separator', this.formConfig.ruleForm.deviceSign);
      this.$API.configRelation
        .uploadFile(formData)
        .then((res: any) => {
          loading.close();
          this.$message({
            type: res.data.success ? 'success' : 'warning',
            message: res.data.success ? '文件上传成功！' : (res.data && res.data.result) || '文件上传失败!'
          });
          console.log(res.data.data);
          this.mergeConfigList.push({
            mergeColumnIndex: [],
            columns: res.data.data.colNum,
            filePath: res.data.data.filePath
          });
        })
        .catch((err: any) => {
          this.$message.warning((err && err.data && err.data.result) || '上传失败!');
          loading.close();
        });
    },
    submit() {
      this.formConfig.ruleForm.mergeConfigList = this.mergeConfigList;
      const list: any = [];
      this.mergeConfigList.forEach((merge: any) => {
        list.push({
          filePath: merge.filePath,
          colIndex: merge.mergeColumnIndex,
          separator: this.formConfig.ruleForm.deviceSign,
          fillStr: this.formConfig.ruleForm.fillSign
        });
      });
      // const loading = this.$loading({
      //   text: '合并中...',
      //   spinner: 'el-icon-loading',
      //   background: 'rgba(0, 0, 0, 0.7)'
      // });
      this.$API.configRelation.mergeTxt(list).then(({ data }: any) => {
        console.log(data);
        const blob = new Blob([data]);
        const blobUrl = window.URL.createObjectURL(blob);
        console.log(blobUrl);
        let a = document.createElement('a');
        a.download = `${this.formConfig.ruleForm.resultName}.txt`;
        a.href = blobUrl;
        a.style.display = 'none';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        this.$emit('closeDialog');
      });
    }
  }
});
</script>
<style lang="scss" scoped>
.mergeTxtDialog {
  width: 100%;
  height: 100%;
  .txtFileItem {
    width: 100%;
    margin-bottom: 20px;
    position: relative;
    &:hover {
      background: rgba(73, 99, 138, 0.356);
      .close {
        display: block;
      }
    }
    .close {
      position: absolute;
      right: 0;
      top: 0;
      width: 20px;
      height: 20px;
      line-height: 20px;
      color: #fff;
      text-align: center;
      cursor: pointer;
      display: none;
    }
    .infoItem {
      width: 100%;
      height: 36px;
      display: flex;
      align-items: center;
      color: #fff;
      .label {
        width: 100px;
        padding-right: 10px;
        text-align: right;
      }
      .value {
        flex: 1;
        ::v-deep .el-select{
          width: 100%;
        }
      }
    }
  }
}
</style>
