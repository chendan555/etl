<template>
  <div class="selectServerFile">
    <div class="filterdiv">
      <el-form size="mini" label-width="120px" :model="ruleForm" :inline="true">
        <el-form-item label="服务器来源">
          <el-select v-model="ruleForm.sourceId" placeholder="服务器来源" @change="getFileServerList">
            <el-option
              v-for="item in serverSourceList"
              :key="item.id"
              :label="item.sourceName"
              :value="item.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="服务器地址">
          <el-select v-model="ruleForm.fileServer" placeholder="服务器地址" @change="getFileDataList">
            <el-option
              v-for="item in fileServerList"
              value-key="id"
              :key="item.id"
              :label="item.name"
              :value="item"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-form>
    </div>
    <div class="tablediv">
      <base-table
        ref="baseTable"
        :tableColumnConfig="tableConfig.tableColumnConfig"
        :tableData="tableConfig.tableData"
        :highlightCurrentRow="true"
        :isShowCheckBox="false"
        @current-change="currentChange"
      ></base-table>
    </div>
  </div>
</template>
<script lang="ts">
import Vue from 'vue';
import BaseTable from '@/components/BaseTable/index.vue';
import { dataCenterService } from '@/api/index.ts';
export default Vue.extend({
  name: 'selectServerFile',
  components: { BaseTable },
  data() {
    return {
      ruleForm: {
        sourceId: '',
        fileServer: <any>{}
      },
      serverSourceList: this.$store.state.serverSourceList,
      fileServerList: [],
      tableConfig: {
        tableColumnConfig: [
          {
            label: '文件名称',
            prop: 'fileName'
          },
          {
            label: '存储路径',
            prop: 'directory'
          }
        ],
        tableData: []
      }
    };
  },
  created() {
    this.getServerSourceList();
  },
  methods: {
    // 获取数据服务器来源类型
    getServerSourceList() {
      this.$API.getServerSourceList().then(({ data }: any) => {
        this.serverSourceList = data.data;
      });
    },
    // 获取文件服务器地址列表
    getFileServerList(sourceId: string) {
      const params = {
        sourceId: sourceId
      };
      dataCenterService.getFileServerList(params).then(({ data }: any) => {
        this.fileServerList = data.data;
        if (data.data[0]) {
          this.getFileDataList(data.data[0]);
          this.ruleForm.fileServer = data.data[0];
        }
      });
    },
    // 获取文件数据列表
    getFileDataList(fileServer: any) {
      const params = { id: fileServer.id };
      dataCenterService.getFileDataList(params).then(({ data }: any) => {
        this.tableConfig.tableData = data.data;
      });
    },
    currentChange(val: any) {
      let fileList = [val];
      fileList.forEach((file: any) => {
        file.serverSourceId = this.ruleForm.sourceId;
        file.fileSourceId = this.ruleForm.fileServer.id;
        file.fileSourceName = this.ruleForm.fileServer.name;
      });
      this.$emit('fileList', fileList);
    },
    // 获取选择的文件
    selectionChange(fileList: any) {
      fileList.forEach((file: any) => {
        file.serverSourceId = this.ruleForm.sourceId;
        file.fileSourceId = this.ruleForm.fileServer.id;
        file.fileSourceName = this.ruleForm.fileServer.name;
      });
      this.$emit('fileList', fileList);
    },
    // 文件服务器文件下载到系统本地服务器，并返回本地服务器地址做回显
    downloadFileToServer(file: any) {
      console.log(file);
      const params = JSON.stringify([
        {
          type: 3,
          fileSourceId: file.fileSourceId,
          fileName: file.fileName,
          directory: file.directory
        }
      ]);
      return this.$API.downloadFileToServer(params);
    }
  }
});
</script>
<style lang="scss" scoped>
.selectServerFile {
  width: 100%;
  height: 100%;
  .filterdiv {
    width: 100%;
    height: 40px;
  }
  .tablediv {
    width: 100%;
    height: calc(100% - 40px);
  }
}
</style>
