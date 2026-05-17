<template>
  <base-dialog v-bind="$attrs" @closeDialog="closeDialog" @save="saveTableConfig">
    <el-form
      ref="form"
      :model="tableRelationForm"
      label-width="100px"
      size="mini"
      :rules="formRules"
    >
      <el-form-item label="数据源" prop="dataSourceId">
        <el-select
          v-model="tableRelationForm.dataSourceId"
          placeholder="请选择数据源"
          @change="setDatabase"
          clearable
          filterable
        >
          <el-option
            v-for="database in databaseList"
            :label="database.name"
            :value="database.databaseId"
            :key="database.databaseId"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item
        label="模式"
        v-show="tableRelationForm.dataSourceId !== '1324302969336971265'"
        prop="schema"
      >
        <el-select
          v-model="tableRelationForm.schema"
          placeholder="请选择模式"
          @change="setSchema"
          clearable
          filterable
        >
          <el-option
            v-for="mode in modeList"
            :label="mode.text"
            :value="mode.text"
            :key="mode.text"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="主表" prop="tableName">
        <el-select
          v-model="tableRelationForm.tableName"
          placeholder="请选择数据表"
          clearable
          filterable
        >
          <el-option
            v-for="table in tableList"
            :label="`${table.remarks}/${table.tableName}`"
            :value="table.tableName"
            :key="table.tableName"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="主子表关系" prop="tableConfigId">
        <el-select
          v-model="tableRelationForm.tableConfigId"
          @change="setTableConfigName"
          placeholder="请选择主子表关系"
          clearable
          filterable
        >
          <el-option
            v-for="item in tableRelationList"
            :label="item.lable"
            :value="item.value"
            :key="item.value"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="目标数据源" prop="targetDataSourceId">
        <el-select
          v-model="tableRelationForm.targetDataSourceId"
          placeholder="请选择数据源"
          @change="setTargetDatabase"
          clearable
          filterable
        >
          <el-option
            v-for="database in databaseList"
            :label="database.name"
            :value="database.databaseId"
            :key="database.databaseId"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="目标模式" prop="targetSchema">
        <el-select
          v-model="tableRelationForm.targetSchema"
          placeholder="请选择模式"
          clearable
          filterable
        >
          <el-option
            v-for="mode in targetModeList"
            :label="mode.text"
            :value="mode.text"
            :key="mode.text"
          ></el-option>
        </el-select>
      </el-form-item>
      <!-- <el-form-item label="所属任务" prop="taskId">
        <el-select
          v-model="tableRelationForm.taskId"
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
          v-model="tableRelationForm.taskNodePath"
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
        <el-input type="text" disabled v-model="tableRelationForm.ddType"></el-input>
      </el-form-item>-->
      <el-form-item label="备注" prop="remarks">
        <el-input
          type="textarea"
          placeholder="请输入备注（示例：描述表关系配置备注）"
          v-model="tableRelationForm.remarks"
        ></el-input>
      </el-form-item>
    </el-form>
  </base-dialog>
</template>
<script>
/**
 * Create by Leo
 * 数据引接配置模块-数据表关系配置模块-数据表引接配置弹框组件
 * 完成源数据库、模式、数据表和模板数据库、模式等相关信息配置
 */
import Vue from 'vue';
import BaseDialog from '@/components/BaseDialog/index.vue';
import { taskManagerService } from '@/api/index.ts';
import config from './config';
import API from '@/api/index';
export default Vue.extend({
  name: 'configTable',
  components: { BaseDialog },
  props: {
    /**
     * 当前数据表引接配置信息
     * 用于配置信息回显和配置信息修改
     */
    relationInfo: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      /**
       * 数据表引接配置项
       */
      tableRelationForm: this.$utils.deepCopy(config.tableRelationRuleForm),
      /**
       * 数据表引接配置项表单验证
       */
      formRules: config.formItemRuleConfig,
      /**
       * 任务列表
       */
      taskList: [],
      /**
       * 任务节点列表
       */
      nodeList: [],
      /**
       * 任务信息
       */
      taskInfo: {},
      /**
       * 源数据库列表
       */
      databaseList: [],
      /**
       * 源模式列表
       */
      modeList: [],
      /**
       * 目标模式列表
       */
      targetModeList: [],
      /**
       * 源数据表列表
       */
      tableList: [],
      /**
       * 主子表关系列表
       */
      tableRelationList: [],
      /**
       * 主表列表
       */
      mainTableKeysList: [],
      /**
       * 数据表字段列表
       */
      tableKeysList: [],
      /**
       * 数据表配置
       */
      tableConfig: {
        connection: '',
        schema: '',
        tableName: '',
        table: {},
        mainKey: '',
        outLinkKey: ''
      }
    };
  },
  created() {
    this.getDatabaseList();
    this.getAllTableRelationList();
    // console.log(this.relationInfo);
  },
  methods: {
    /**
     * 初始化化数据
     */
    async initInfoData() {
      // 数据回显
      if (this.relationInfo) {
        let relationInfo = this.$utils.deepCopy(this.relationInfo);
        if (this.$utils.checkDataType(relationInfo.dataSourceId) === 'String') {
          relationInfo.dataSourceId = Number(relationInfo.dataSourceId);
        }
        if (this.$utils.checkDataType(relationInfo.targetDataSourceId) === 'String') {
          relationInfo.targetDataSourceId = Number(relationInfo.targetDataSourceId);
        }
        if (relationInfo.taskId) {
          await this.setTaskName(relationInfo.taskId);
        }
        for (let key in this.tableRelationForm) {
          if (key === 'taskId') {
            this.tableRelationForm[key] = Number(relationInfo[key]);
          } else {
            this.tableRelationForm[key] = relationInfo[key];
          }
        }
        if (relationInfo.jsonForm) {
          this.tableRelation = JSON.parse(relationInfo.jsonForm).tableRelation;
        }
        /**
         * 判断当前信息有源数据库字段数据时请求源模式列表
         */
        if (relationInfo.dataSourceName) {
          let database = {
            name: relationInfo.dataSourceName
          };
          this.getModeList(database);
          /**
           * 判断当前信息有源模式字段数据时请求源数据表列表
           */
          if (relationInfo.schema) {
            let params = {
              connection: relationInfo.dataSourceName,
              schema: relationInfo.schema
            };
            this.getTableList(params);
          }
        }
        /**
         * 判断当前信息有目标数据库字段数据时请求目标模式列表
         */
        if (relationInfo.targetDataSourceName) {
          let database = {
            name: relationInfo.targetDataSourceName
          };
          this.getTargetModeList(database);
        }
      }
    },
    /**
     * 获取全部主子表关系列表
     */
    getAllTableRelationList() {
      API.tableRelation.getAllTableRelationList().then(({ data }) => {
        this.tableRelationList = data.data;
        // console.log(this.tableRelationList);
      });
    },
    /**
     * 设置数据库
     */
    setDatabase(data) {
      let database = this.databaseList.find(ele => ele.databaseId === data);
      if (database) {
        this.tableRelationForm.databaseName = database.name;
        this.getModeList(database);
      }
    },
    /**
     * 设置目标数据库列表
     */
    setTargetDatabase(data) {
      let database = this.databaseList.find(ele => ele.databaseId === data);
      this.tableRelationForm.targetDataSourceName = database.name;
      this.getTargetModeList(database);
    },
    /**
     * 设置源模式
     */
    setSchema(data) {
      let schema = this.modeList.find(ele => ele.text === data);
      this.tableRelationForm.schemaId = schema.id;
      this.tableRelationForm.schema = schema.text;
      let params = {
        connection: this.tableRelationForm.databaseName,
        schema: this.tableRelationForm.schema
      };
      this.getTableList(params);
    },
    // setTableList(data) {},
    /**
     * 设置数据表配置名称
     * @param id
     */
    setTableConfigName(id) {
      if (id) {
        let tableConfig = this.tableRelationList.find(item => item.value === id);
        if (tableConfig) {
          this.tableRelationForm.tableConfigName = tableConfig.lable;
        }
      }
    },
    /**
     * 设置任务名称
     * @param value
     */
    setTaskName(value) {
      taskManagerService
        .getTaskFlowById(value)
        .then(({ data }) => {
          // console.log(data);
          if (data.code == 200 && data.data) {
            let importJson = JSON.parse(data.data.mapInfo);
            this.arrangeData([importJson.root]);
            this.nodeList = [importJson.root];
            // console.log(importJson);
          }
        })
        .catch(() => {})
        .finally(() => {
          const task = this.taskList.find(ele => ele.id === value);
          task && (this.tableRelationForm.taskName = task.taskName);
          this.tableRelationForm.ddType = task.typeName;
        });
    },
    /**
     * 整理任务数据
     * @param data
     */
    arrangeData(data) {
      data.forEach(ele => {
        ele.name = ele.data.text;
        ele.id = ele.data.id;
        if (ele.children && ele.children.length) {
          this.arrangeData(ele.children);
        }
      });
    },
    /**
     * 展示任务节点路径
     * @param data
     */
    showTaskNodePath(data) {
      this.tableRelationForm.taskNodeId = data[data.length - 1];
      let taskNodeList = this.$refs.taskNode.getCheckedNodes();
      this.tableRelationForm.taskNodeName = taskNodeList[0].label;
    },
    /**
     * 获取数据源列表
     */
    getDatabaseList() {
      this.$API
        .getDatabases()
        .then(({ data }) => {
          this.databaseList = data;
        })
        .finally(() => {
          this.initInfoData();
        });
    },
    /**
     * 获取所选数据源下模式列表
     * @param database
     */
    getModeList(database) {
      this.$API
        .getSchema({ connection: database.name })
        .then(({ data }) => {
          this.modeList = data;
        })
        .catch();
    },
    /**
     * 获取所选数据源下模式列表
     * @param database
     */
    getTargetModeList(database) {
      this.$API
        .getSchema({ connection: database.name })
        .then(({ data }) => {
          this.targetModeList = data;
        })
        .catch();
    },
    /**
     * 选择所选数据源和模式下数据表列表
     * @param param
     */
    getTableList(param) {
      if (param.connection && param.schema) {
        let data = {
          pageIndex: 1,
          pageSize: 100000,
          ...param
        };
        this.$API
          .getTableDatas(data)
          .then(({ data }) => {
            if (data.code === 200) {
              this.tableList = data.data.records;
              // console.log(this.tableList);
              if (this.tableRelation && this.tableRelation.length) {
                this.getTableKeysList(this.tableRelation[0]);
              } else {
                this.initMainTable();
              }
            }
          })
          .catch((this.tableList = []));
      }
    },
    /**
     * 获取主数据表的字段列表
     * @param table
     */
    getMainTableKeysList(table) {
      let list = [];
      let params = {
        connection: this.tableRelationForm.databaseName,
        schema: this.tableRelationForm.schema,
        table: table.tableName
      };
      this.$API.getColumnsByTable(params).then(({ data }) => {
        this.mainTableKeysList = data;
      });
    },
    /**
     * 获取子数据表的字段列表
     * @param table
     */
    getTableKeysList(table) {
      let list = [];
      let params = {
        connection: this.tableRelationForm.databaseName,
        schema: this.tableRelationForm.schema,
        table: table.tableName
      };
      this.$API.getColumnsByTable(params).then(({ data }) => {
        table.table.keys = data;
      });
    },
    /**
     * 初始化表关系树形结构
     */
    initMainTable() {
      this.tableRelation = [];
      let tableInfo = this.$utils.deepCopy(this.tableConfig);
      tableInfo.table = {};
      tableInfo.tableName = '';
      this.tableRelation.push(tableInfo);
    },
    /**
     * 保存表关系信息
     */
    saveTableConfig() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          // 判断是否为同一数据源
          // 源和目标都一致时不能引接
          const reg = /\(.+\)/;
          let dataSource1 = this.tableRelationForm.databaseName.match(reg)[0];
          let dataSource2 = this.tableRelationForm.targetDataSourceName.match(reg)[0];
          if (dataSource1 === dataSource2 && this.tableRelationForm.schema === this.tableRelationForm.targetSchema) {
            this.$message.error('相同数据源和模式的数据表不能执行引接操作，请修改相关配置！');
            return;
          }

          let createName = JSON.parse(sessionStorage.getItem('user')).name;
          let params = {
            createName,
            createTime: this.$utils.setTimeType(),
            ...this.tableRelationForm
            // taskNodeId: this.tableRelationForm.taskNodePath[this.tableRelationForm.taskNodePath.length - 1]
          };
          if (!params.id) {
            delete params.id;
          }
          API.configRelation.saveTableRelation(params).then(({ data }) => {
            this.$message({
              message: data.result,
              type: data.success ? 'success' : 'warning'
            });
            this.closeDialog(data.success);
          });

          // if (this.relationInfo) {
          //   this.$axios.post('/api/updateTableRelation', params).then(data => {
          //     this.$message.success(data.data.msg);
          //     this.$emit('closeDialog', true);
          //   });
          // } else {
          //   this.$axios.post('/api/addTableRelation', params).then(data => {
          //     this.$message.success(data.data.msg);
          //     this.$emit('closeDialog', true);
          //   });
          // }
        } else {
          return false;
        }
      });
    },
    /**
     * 关闭弹框
     */
    closeDialog(bool) {
      this.tableRelationForm = this.$utils.deepCopy(config.tableRelationRuleForm);
      this.$emit('closeDialog', bool);
      // console.log(this.tableRelationForm);
    }
  }
});
</script>
<style lang="scss" scoped>
/deep/ .el-form {
  .el-select {
    width: 100%;
  }
}
/deep/ .el-tree-node__content {
  height: auto;
}
/deep/ .el-tree {
  background: transparent;
  .custom-tree-node {
    width: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin: 5px 0;
    /deep/ .el-select {
      margin-right: 5px;
    }
    .treeHandle {
      padding: 0 10px;
      /deep/ .el-button {
        color: #fff;
      }
    }
  }
}
</style>
