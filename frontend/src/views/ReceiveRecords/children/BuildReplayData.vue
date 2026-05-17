<template>
  <base-dialog v-bind="$attrs" v-on="$listeners" @save="saveTableConfig">
    <ChsDatabase
      ref="chsDatabase"
      :config="{isShowColumn: true, labelWidth: '145px', databaseRuleForm}"
      @emitData="getDatabase"
    ></ChsDatabase>
    <el-form ref="keyValueRef" size="mini" label-width="145px" :rules="rules" :model="ruleForm">
      <el-form-item label="instanceId字段值" prop="instanceId">
        <el-select v-model="ruleForm.instanceId" placeholder="请选择数据表字段数据" clearable filterable>
          <el-option
            v-for="columnData in columnDataList"
            :label="columnData"
            :value="columnData"
            :key="columnData"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="modelCode字段值" prop="modelCode">
        <el-input v-model="ruleForm.modelCode"></el-input>
      </el-form-item>
    </el-form>
  </base-dialog>
</template>
<script lang="ts">
import Vue from 'vue';
import BaseDialog from '@/components/BaseDialog/index.vue';
import ChsDatabase from '@/components/ChsDatabase/index.vue';
import { dataCenterService } from '@/api';
export default Vue.extend({
  name: 'buildReplayData',
  components: { BaseDialog, ChsDatabase },
  props: {
    crtFileRecord: {
      type: Object,
      default: {}
    },
    crtTableData: {
      type: Object,
      default: {}
    }
  },
  data() {
    return {
      ruleForm: <any>{
        instanceId: '',
        modelCode: ''
      },
      tableData: this.crtTableData || <any>{},
      databaseRuleForm: <any>{},
      rules: {
        instanceId: [{ required: true, message: '请选择数据值', trigger: 'blur' }],
        modelCode: [{ required: true, message: '请输入复盘数据表名称', trigger: 'blur' }]
      },
      columnDataList: <any>[]
    };
  },
  mounted() {
    this.getJsonConfig();
  },
  watch: {
    crtFileRecord: {
      deep: true,
      handler(val) {
        this.getJsonConfig();
      }
    }
  },
  methods: {
    getJsonConfig() {
      if (this.crtFileRecord && this.crtFileRecord.id) {
        let params = {
          historyId: this.crtFileRecord.id
        };
        this.$API.dataReceive.getJsonConfig(params).then(({ data }: any) => {
          if (data.data) {
            this.ruleForm.id = data.data.id;
            this.ruleForm.instanceId = data.data.instanceId;
            this.ruleForm.modelCode = data.data.modelCode;
            this.databaseRuleForm.databaseId = parseInt(data.data.dataSourceId);
            this.databaseRuleForm.schema = data.data.schema;
            this.databaseRuleForm.table = data.data.tableName;
            this.databaseRuleForm.column = data.data.columnName;
          }
        });
      }
    },
    getDatabase(ruleForm: any) {
      if (ruleForm.column) {
        this.databaseRuleForm = ruleForm;
        let { connection, schema } = ruleForm;
        let params = {
          connection,
          schema,
          tableName: ruleForm.table,
          columnName: ruleForm.column
        };
        dataCenterService.getColumnDataList(params).then(({ data }: any) => {
          this.columnDataList = data.data;
        });
      }
    },
    saveTableConfig() {
      (this.$refs.keyValueRef as any).validate((valid: boolean) => {
        if (valid) {
          let params = {
            historyId: this.crtFileRecord.id,
            dataSourceId: this.databaseRuleForm.databaseId,
            schema: this.databaseRuleForm.schema,
            tableName: this.databaseRuleForm.table,
            columnName: this.databaseRuleForm.column,
            tableData: this.tableData,
            ...this.ruleForm
          };
          this.$API.dataReceive.saveJsonConfig(params).then(({ data }: any) => {
            this.$utils.setMessage(data.success, data.result);
            this.ruleForm = {
              instanceId: '',
              modelCode: ''
            };
            this.databaseRuleForm = <any>{};
            this.$emit('closeDialog');
          });
        }
      });
    }
  }
});
</script>
<style lang="scss" scoped>
/deep/ .el-select {
  width: 100%;
}
</style>
