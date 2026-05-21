<template>
  <base-dialog v-bind="$attrs" v-on="$listeners" @save="uploadFile">
    <!-- <base-form ref="form" v-bind="{...formConfig}" @fileData="saveFile"></base-form> -->
    <el-form
      ref="form"
      :model="formConfig.ruleForm"
      label-width="120px"
      size="mini"
      :rules="formConfig.formItemRuleConfig"
    >
      <el-form-item label="名称" prop="name">
        <el-input type="input" placeholder="请输入名称（示例：xxx文件）" v-model="formConfig.ruleForm.name"></el-input>
      </el-form-item>
      <el-form-item label="解析协议" prop="analysisProtocolId">
        <el-select
          v-model="formConfig.ruleForm.analysisProtocolId"
          placeholder="请选择解析协议"
          clearable
          filterable
        >
          <el-option
            v-for="protocol in protocolList"
            :label="protocol.name"
            :value="protocol.id"
            :key="protocol.id"
          ></el-option>
        </el-select>
      </el-form-item>
    </el-form>
    <ChsDatabase
      ref="chsDatabase"
      :config="{isShowTable: false, labelWidth: '120px', databaseLabel: '目标数据源', schemaLabel: '目标模式', databaseRuleForm}"
      @emitData="getDatabase"
    ></ChsDatabase>
    <el-form
      ref="form2"
      :model="formConfig.ruleForm"
      label-width="120px"
      size="mini"
      :rules="formConfig.formItemRuleConfig"
    >
      <!-- <el-form-item label="所属任务" prop="taskId">
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
      <el-form-item label="型号" prop="ddType">
        <el-input type="text" disabled v-model="formConfig.ruleForm.ddType"></el-input>
      </el-form-item>
      <el-form-item label="添加任务字段" prop="taskDataFlag">
        <el-radio-group v-model="formConfig.ruleForm.taskDataFlag">
          <el-radio :label="1">是</el-radio>
          <el-radio :label="0">否</el-radio>
        </el-radio-group>
      </el-form-item> -->
      <el-form-item label="剔除行数" prop="jumpRow">
        <div class="addRowNum">
          <div class="addRow">
            <el-input placeholder="请输入内容" v-model="rowNum" type="number" @change="addRowNum">
              <el-button slot="append" icon="el-icon-circle-plus-outline" @click="addRowNum"></el-button>
            </el-input>
          </div>
          <div class="rowNum">
            <span v-for="(num, index) in formConfig.ruleForm.jumpRow" :key="index">
              {{num}}
              <span v-show="index < formConfig.ruleForm.jumpRow.length - 1">,</span>
            </span>
          </div>
        </div>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input
          type="textarea"
          placeholder="请输入备注（示例：文件解析配置备注）"
          v-model="formConfig.ruleForm.remark"
        ></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="getLocalFiles">本地文件</el-button>
        <el-button type="primary" @click="getServerList">服务器文件</el-button>
      </el-form-item>
    </el-form>
    <div class="fileList">
      <p v-for="(file, index) in localFileList" :key="index">{{file.url}}</p>
      <p v-for="(file, index) in serverFileList" :key="index">
        <span v-if="file.fileName">{{`${file.directory}${file.fileName}`}}</span>
        <span v-else>{{file.filePath}}</span>
      </p>
    </div>
    <base-dialog
      :dialogConfig="serverConfig"
      @closeDialog="closeServerConfig"
      @save="closeServerConfig"
    >
      <SelectServerFile ref="serverFile" @fileList="getFileList"></SelectServerFile>
    </base-dialog>
  </base-dialog>
</template>
<script lang="ts">
import Vue from 'vue';
import BaseDialog from '@/components/BaseDialog/index.vue';
import BaseForm from '@/components/BaseForm/index.vue';
import ChsDatabase from '@/components/ChsDatabase/index.vue';
import SelectServerFile from './SelectServerFile.vue';
import { taskManagerService } from '@/api/index.ts';
import API from '@/api/index';
import config from './config';
export default Vue.extend({
  name: 'editFile',
  components: { BaseDialog, BaseForm, ChsDatabase, SelectServerFile },
  props: {
    fileInfo: {
      type: Object,
      default: {}
    }
  },
  data() {
    return {
      rowNum: '',
      formConfig: {
        ...config.formConfig,
        ruleForm: this.$utils.deepCopy(config.formConfig.ruleForm)
      },
      protocolList: <any>[],
      uploadFileInfo: <any>{},
      taskList: <any>[],
      nodeList: <any>[],
      databaseRuleForm: <any>{},
      localFileList: <any>[],
      serverFileList: <any>[],
      filesList: <any>[],
      serverConfig: {
        visible: false,
        title: '服务器文件',
        width: '80%'
        // fullscreen: true
      }
    };
  },
  mounted() {
    if (this.fileInfo && this.fileInfo.hasOwnProperty('id')) {
      // this.formConfig.ruleForm = { ...this.formConfig.ruleForm, ...this.fileInfo };
      Object.keys(this.fileInfo).forEach((key: string) => {
        if (key === 'taskId') {
          this.formConfig.ruleForm[key] = Number(this.fileInfo[key]);
        } else if (key === 'jumpRow') {
          this.formConfig.ruleForm[key] = this.fileInfo[key] ? this.fileInfo[key].split(',') : [];
        } else {
          this.formConfig.ruleForm[key] = this.fileInfo[key];
        }
      });
      if (this.fileInfo.pathType === 'local') {
        this.localFileList.push({
          type: 'local',
          url: this.fileInfo.filePath
        });
      } else {
        this.serverFileList.push({
          ...this.fileInfo,
          type: 'server'
        });
      }
      this.databaseRuleForm.databaseId = parseInt(this.formConfig.ruleForm.targetDataSourceId);
      this.databaseRuleForm.connection = this.formConfig.ruleForm.targetDataSourceName;
      this.databaseRuleForm.schema = this.formConfig.ruleForm.targetSchema;
      console.log(this.formConfig.ruleForm);
    } else {
      this.formConfig.ruleForm = this.$utils.deepCopy(config.formConfig).ruleForm;
    }
    this.getAllProtocolList();
    if (this.formConfig.ruleForm.taskId) {
      this.setTaskName(this.formConfig.ruleForm.taskId);
    }
    console.log(config.formConfig.ruleForm);
  },
  methods: {
    // 添加剔除行数
    addRowNum() {
      if (this.rowNum) {
        if (Number(this.rowNum) > 0) {
          let arrayItem = this.formConfig.ruleForm.jumpRow.find((item: string) => item === this.rowNum);
          if (!arrayItem) {
            this.formConfig.ruleForm.jumpRow.push(this.rowNum);
            this.rowNum = '';
          }
        } else {
          this.$message.warning('请输出大于0的数字！');
        }
      }
    },
    // 展示文件选择器
    getLocalFiles() {
      const input = document.createElement('input');
      input.type = 'file';
      input.accept = '.txt, .xml, .csv, .dat';
      input.multiple = true;
      const self = this;
      const cleanup = () => {
        if (input.parentNode) document.body.removeChild(input);
      };
      input.addEventListener('change', function(e: any) {
        const files = e.target.files;
        for (let i = 0; i < files.length; i++) {
          self.localFileList.push({
            type: 'local',
            url: files[i].name
          });
          self.filesList.push(files[i]);
        }
        cleanup();
      });
      // 用户取消选择时清理
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
    // 展示服务器文件列表
    getServerList() {
      this.serverConfig.visible = true;
    },
    getFilesPath(e: any) {
      for (let i = 0; i < e.target.files.length; i++) {
        this.localFileList.push({
          type: 'local',
          url: e.target.files[i].name
        });
        this.filesList.push(e.target.files[i]);
      }
    },
    // 获取所选文件地址
    getFileList(list: any) {
      this.serverFileList = list;
    },
    closeServerConfig() {
      this.serverConfig.visible = false;
      const list = <any>[];
      this.serverFileList.forEach((file: any) => {
        (this.$refs.serverFile as any).downloadFileToServer(file).then(({ data }: any) => {
          list.push({
            ...file,
            url: data.data[0].filePath,
            fileName: data.data[0].fileFullName,
            type: 'server'
          });
          this.formConfig.ruleForm.filePath = data.data[0].filePath;
        });
      });
      this.serverFileList = list;
    },
    setTaskName(value: string) {
      taskManagerService
        .getTaskFlowById(value)
        .then(({ data }: any) => {
          if (data.code == 200 && data.data) {
            let importJson = JSON.parse(data.data.mapInfo);
            this.arrangeData([importJson.root]);
            this.nodeList = [importJson.root];
          } else {
          }
        })
        .catch(() => {})
        .finally(() => {
          const task = this.taskList.find((ele: any) => ele.id === value);
          task && (this.formConfig.ruleForm.taskName = task.taskName);
          // this.formConfig.ruleForm.ddType = task.typeName;
        });
    },
    arrangeData(data: any) {
      data.forEach((ele: any) => {
        ele.name = ele.data.text;
        ele.id = ele.data.id;
        if (ele.children && ele.children.length) {
          this.arrangeData(ele.children);
        }
      });
    },
    // 获取并设置数据源和模式信息
    getDatabase(database: any) {
      this.formConfig.ruleForm.targetDataSourceId = database.databaseId;
      this.formConfig.ruleForm.targetDataSourceName = database.connection;
      this.formConfig.ruleForm.targetSchema = database.schema;
    },
    showTaskNodePath(data: any) {
      this.formConfig.ruleForm.taskNodeId = data[data.length - 1];
      let taskNodeList = (this.$refs.taskNode as any).getCheckedNodes();
      this.formConfig.ruleForm.taskNodeName = taskNodeList[0].label;
    },
    /**
     * 保存文件到文件列表
     */
    saveFile(e: any) {
      this.uploadFileInfo = e;
    },
    /**
     * 保存文件解析配置信息
     */
    saveData() {
      // 表单验证
      (this.$refs['form'] as any).validate((valid: any) => {
        if (valid) {
          // 获取创建人
          let createName = JSON.parse(sessionStorage.getItem('user') || '').name;
          let params = {
            ...this.formConfig.ruleForm,
            jumpRow: this.formConfig.ruleForm.jumpRow.toString(),
            createName,
            createTime: this.$utils.setTimeType(),
            taskNodeId: this.formConfig.ruleForm.taskNodePath[this.formConfig.ruleForm.taskNodePath.length - 1]
          };
          // id无值时为新增，删除该接口参数，不予传递
          if (!params.id) {
            delete params.id;
          }
          API.configRelation.saveFileRelation(params).then(({ data }: any) => {
            this.$message({
              message: data.result,
              type: data.success ? 'success' : 'warning'
            });
            this.$emit('closeDialog', true);
          });
        } else {
          return false;
        }
      });
    },
    /**
     * 解析文件上传
     * 先完成文件上传，再进行解析配置项保存
     */
    uploadFile() {
      // 判断文件解析配置项是否完成填报，否则不进行文件上传
      (this.$refs['form'] as any).validate((valid: any) => {
        if (valid) {
          // 判断是否有需要上传的文件
          // 有文件则先上传文件，无文件则直接保存配置
          if (this.filesList.length) {
            const loading = this.$loading({
              text: '上传中...',
              target: '.gx-taskList-body',
              spinner: 'el-icon-loading',
              background: 'rgba(0, 0, 0, 0.7)'
            });
            let formData = new FormData();
            for (let i = 0; i < this.filesList.length; i++) {
              formData.append('file', this.filesList[i]);
            }
            taskManagerService
              .apiUploadFile(formData)
              .then((res: any) => {
                loading.close();
                this.$message({
                  type: res.data.success ? 'success' : 'warning',
                  message: res.data.success ? '文件上传成功！' : (res.data && res.data.result) || '文件上传失败!'
                });
                console.log(res.data.data);
                // this.fileUrl = [];
                // this.formUrl = [];
                res.data.data.forEach((file: any) => {
                  let fileItem = this.localFileList.find((item: any) => item.url === file.fileFullName);
                  fileItem.url = file.filePath;
                });
                this.formConfig.ruleForm.filePath = res.data.data[0].filePath;
                // this.formConfig.ruleForm.filePath = res.data.data.filePath;
                this.saveData();
              })
              .catch(err => {
                loading.close();
              });
          } else {
            this.saveData();
          }
        } else {
          return false;
        }
      });
    },
    /* uploadFile() {
      if (Object.keys(this.uploadFileInfo).length) {
        const loading = this.$loading({
          text: '上传中...',
          target: '.gx-taskList-body',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        });
        let formData = new FormData();
        for (let i = 0; i < this.uploadFileInfo.target.files.length; i++) {
          formData.append('file', this.uploadFileInfo.target.files[i]);
        }
        taskManagerService.apiUploadFile(formData).then((res: any) => {
          loading.close();
          this.$message({
            type: res.data.success ? 'success' : 'warning',
            message: res.data.success ? '文件上传成功！' : (res.data && res.data.result) || '文件上传失败!'
          });
          if (this.$utils.checkDataType(res.data.data) === 'Array') {
            this.formConfig.ruleForm.filePath = res.data.data[0].filePath;
          } else {
            this.formConfig.ruleForm.filePath = res.data.data.filePath;
          }
          this.save();
        });
      } else {
        this.save();
      }
    }, */
    getAllProtocolList() {
      (this as any).$API.getAllProtocolList().then(({ data }: any) => {
        this.protocolList = data.data.records;
      });
    }
  }
});
</script>
<style lang="scss" scoped>
::v-deep .el-select {
  width: 100%;
}
.fileList {
  color: #fff;
  line-height: 20px;
}
.addRowNum {
  display: flex;
  align-items: center;
  .addRow {
    width: 180px;
    ::v-deep .el-input-group__append {
      border: 1px solid #1c5dbf;
      color: #fff;
    }
  }
  .rowNum {
    width: calc(100% - 190px);
    margin-left: 10px;
    color: #fff;
  }
}
</style>
