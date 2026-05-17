<template>
  <base-dialog :dialogConfig="dialogConfig" @save="uploadFiles" @closeDialog="closeDialog">
    <el-form ref="form" :model="formConfig.ruleForm" label-width="120px" size="mini">
      <el-form-item label="所属任务" prop="taskId">
        <el-select
          v-model="formConfig.ruleForm.taskId"
          placeholder="请选择所属任务"
          clearable
          filterable
          @change="setTaskName"
        >
          <el-option
            v-for="task in taskList"
            :label="task.taskName"
            :value="task.id"
            :key="task.id"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="所属节点" prop="taskNodePath">
        <el-cascader
          ref="taskNode"
          v-model="formConfig.ruleForm.taskNodePath"
          :options="nodeList"
          :props="{
            label: 'name',
            children: 'children',
            value: 'id',
            checkStrictly: true
          }"
          @change="showTaskNodePath"
          placeholder="请选择所属节点"
          show-all-levels
          clearable
          filterable
        ></el-cascader>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input type="textarea" v-model="formConfig.ruleForm.remark"></el-input>
      </el-form-item>
    </el-form>
    <el-button icon="el-icon-upload" size="mini" @click="getUploadFile">选择文件</el-button>
    <div class="filediv">
      <p>{{this.file && this.file.name}}</p>
    </div>
    <input
      type="file"
      name="chooseFile"
      id="chooseFile"
      ref="chooseFile"
      @change="chooseFile"
      hidden
    >
  </base-dialog>
</template>
<script>
import { taskManagerService, dataCenterService } from '@/api/index.ts';
import BaseDialog from '@/components/BaseDialog/index.vue';
// import { type } from 'os';
export default {
  name: 'uploadFile',
  components: { BaseDialog },
  props: {
    dataParams: {
      type: Object,
      default() {
        return {};
      }
    }
  },
  data() {
    return {
      dialogConfig: {
        visible: true,
        title: '文件上传'
      },
      formConfig: {
        ruleForm: {
          id: '',
          workingPath: '',
          taskId: '',
          taskName: '',
          taskNodeId: '',
          taskNodeName: '',
          taskNodePath: '',
          taskNodeNamePath: '',
          remark: ''
        }
      },
      taskList: [],
      nodeList: [],
      file: null
    };
  },
  mounted() {
    this.formConfig.ruleForm.id = this.dataParams.id;
  },
  methods: {
    // 获取任务节点信息
    setTaskName(value) {
      taskManagerService
        .getTaskFlowById(value)
        .then(({ data }) => {
          if (data.code == 200 && data.data) {
            let importJson = JSON.parse(data.data.mapInfo);
            this.arrangeData([importJson.root]);
            this.nodeList = [importJson.root];
          } else {
          }
        })
        .catch(() => {})
        .finally(() => {
          const task = this.taskList.find(ele => ele.id === value);
          task && (this.formConfig.ruleForm.taskName = task.taskName);
        });
    },
    showTaskNodePath(data) {
      this.formConfig.ruleForm.taskNodeId = data[data.length - 1];
      let taskNodeList = this.$refs.taskNode.getCheckedNodes();
      this.formConfig.ruleForm.taskNodeName = taskNodeList[0].label;
      this.formConfig.ruleForm.taskNodeNamePath = taskNodeList[0].pathLabels.join('-');
      console.log(this.formConfig.ruleForm);
    },
    arrangeData(data) {
      data.forEach(ele => {
        ele.name = ele.data.text;
        ele.id = ele.data.id;
        if (ele.children && ele.children.length) {
          this.arrangeData(ele.children);
        }
      });
    },
    // 选择上传文件
    getUploadFile() {
      this.$refs.chooseFile.click();
    },
    // 上传文件
    chooseFile(e) {
      this.file = e.target.files[0];
      console.log(this.file);
    },
    uploadFiles() {
      if (this.file) {
        let formData = new FormData();
        formData.append('file', this.file);
        formData.append('ftpUpload', JSON.stringify(this.formConfig.ruleForm));
        dataCenterService.uploadFtpFiles(formData).then(({ data }) => {
          if (data.success) {
            this.$utils.setMessage(true, '上传成功');
            this.$emit('closeDialog', true);
          } else {
            this.$utils.setMessage(false, '上传失败');
          }
        });
      }
    },
    closeDialog() {
      this.$emit('closeDialog');
    }
  }
};
</script>
<style lang="scss" scoped>
.el-select {
  width: 100%;
}
.filediv {
  line-height: 40px;
  color: #fff;
}
</style>
