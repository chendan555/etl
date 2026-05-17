<template>
  <base-dialog v-bind="$attrs" v-on="$listeners" @save="saveTableConfig">
    <el-form
      ref="form"
      :model="tableRelationForm"
      label-width="80px"
      size="mini"
      :rules="formRules"
    >
      <el-form-item label="名称" prop="configName">
        <el-input v-model="tableRelationForm.configName" placeholder="请输入名称（示例：***主子表映射）"></el-input>
      </el-form-item>
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
      <el-form-item label="模式" prop="schema">
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
      <el-form-item label="备注" prop="remark">
        <el-input
          type="textarea"
          placeholder="请输入备注（示例：描述主子表关系配置备注）"
          v-model="tableRelationForm.remark"
        ></el-input>
      </el-form-item>
    </el-form>

    <el-tree :data="tableRelation" node-key="id" default-expand-all :expand-on-click-node="false">
      <div class="custom-tree-node" slot-scope="{ node, data }">
        <div class="selectDiv">
          <el-select
            v-model="data.table"
            size="mini"
            placeholder="请选择表..."
            @change="setTableId(data, $event, node)"
            value-key="tableName"
            allow-create
            clearable
            filterable
          >
            <template v-for="item in tableList">
              <el-option
                v-if="item && item.tableName"
                :key="item.tableName"
                :label="`${item.remarks?item.remarks+'/':''}${item.tableName}`"
                :value="item"
              ></el-option>
            </template>
          </el-select>
          <!-- <el-input v-model="data.table.name" disabled size="mini" v-else></el-input> -->
          <el-select
            v-model="data.outLinkKey"
            size="mini"
            placeholder="请选择子表外键..."
            v-if="node.level>1"
            allow-create
            clearable
            filterable
          >
            <el-option
              v-for="item in data.table.keys"
              :key="item.name"
              :label="`${item.comments?item.comments+'/':''}${item.name}`"
              :value="item.name"
            ></el-option>
          </el-select>
          <el-select
            v-model="data.mainKey"
            size="mini"
            placeholder="请选择主表主键..."
            v-if="node.level>1"
            allow-create
            clearable
            filterable
          >
            <el-option
              v-for="item in node.parent.data.table.keys"
              :key="item.name"
              :label="`${item.comments}/${item.name}`"
              :value="item.name"
            ></el-option>
          </el-select>
        </div>

        <div class="treeHandle">
          <el-button
            type="text"
            size="mini"
            icon="el-icon-circle-plus-outline"
            @click="() => append(data)"
          ></el-button>
          <!-- <el-button type="text" size="mini" icon="el-icon-edit" @click="() => editNode(data)"></el-button>  // 编辑字段按钮，暂时隐藏  Leo 20221011 -->
          <el-button
            type="text"
            size="mini"
            icon="el-icon-delete"
            v-if="node.level>1"
            @click="() => remove(node, data)"
          ></el-button>
        </div>
      </div>
    </el-tree>
  </base-dialog>
</template>
<script>
/**
 * Create by Leo
 * 数据引接配置模块-数据表关系配置模块-数据表关系配置弹框组件
 * 完成主子表关系名称、数据表主子表关系等内容配置、新增或修改
 */
import Vue from 'vue';
import BaseDialog from '@/components/BaseDialog/index.vue';
import { taskManagerService } from '@/api/index.ts';
import API from '@/api/index';
export default Vue.extend({
  name: 'configTable',
  components: { BaseDialog },
  props: {
    /**
     * 父组件数据表主子表关系信息
     */
    relationInfo: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      /**
       * 数据表主子表关系信息
       */
      tableRelation: [],
      /**
       * 主子表关系配置表单信息
       */
      tableRelationForm: {
        id: '',
        configName: '',
        taskId: '',
        taskName: '',
        nodeId: '',
        nodeName: '',
        dataSourceId: '',
        databaseName: '',
        schemaId: '',
        schema: '',
        remark: ''
      },
      /**
       * 主子表关系配置表单验证
       */
      formRules: {
        configName: [{ required: true, message: '请输入表关系名称', trigger: 'blur' }],
        dataSourceId: [{ required: true, message: '请输入数据源', trigger: 'blur' }],
        schema: [{ required: true, message: '请输入模式', trigger: 'blur' }]
      },
      /**
       * 任务列表
       */
      taskList: [],
      /**
       * 任务信息
       */
      taskInfo: {},
      /**
       * 节点列表
       */
      nodeList: [],
      /**
       * 数据源列表
       */
      databaseList: [],
      /**
       * 模块列表
       */
      modeList: [],
      /**
       * 数据表列表
       */
      tableList: [],
      /**
       * 主表字段列表
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
      },
      /**
       * 当前数据节点
       */
      crtTreeNode: []
    };
  },
  mounted() {
    this.getDatabaseList();
    this.$eventBus.$on('passNewKeys', data => {
      this.crtTreeNode.table.keys = data;
    });
    this.initMainTable();
  },
  methods: {
    /**
     * 初始化数据
     * @param relationInfo
     */
    initInfoData(relationInfo) {
      // 数据回显
      if (this.$utils.checkDataType(relationInfo.dataSourceId) === 'String') {
        relationInfo.dataSourceId = Number(relationInfo.dataSourceId);
      }
      // 表单配置项回显
      for (let key in this.tableRelationForm) {
        this.tableRelationForm[key] = relationInfo[key];
      }
      // 主子表关系结构信息回显
      if (relationInfo.jsonForm) {
        this.tableRelation = JSON.parse(relationInfo.jsonForm);
      }
      // console.log(this.tableRelationForm);
      if (this.tableRelationForm.dataSourceId) {
        let database = this.databaseList.find(ele => ele.databaseId === this.tableRelationForm.dataSourceId);
        this.tableRelationForm.databaseName = database.name;
        let paramsModel = {
          name: database.name
        };
        this.getModeList(paramsModel);
        let params = {
          connection: database.name,
          schema: this.tableRelationForm.schema
        };
        this.getTableList(params);
      }
    },
    /**
     * 获取主子表关系信息
     * @param node
     */
    getTableRelationInfor(node) {
      // console.log(node);
      let params = {
        id: node.id
      };
      API.tableRelation.getTableRelationInfor(params).then(({ data }) => {
        this.initInfoData(data.data);
      });
    },
    /**
     * 设置数据源
     * @param data
     */
    setDatabase(data) {
      let database = this.databaseList.find(ele => ele.databaseId === data);
      if (database) {
        this.tableRelationForm.databaseName = database.name;
        this.getModeList(database);
      }
    },
    /**
     * 设置模式
     * @param data
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
      // 切换数据源模式时清除原有主子表关系配置
      this.$nextTick(() => {
        this.initMainTable();
      });
    },
    /**
     * 设置任务名称
     * @param value
     */
    setTaskName(value) {
      let task = this.taskList.find(ele => ele.id === value);
      this.tableRelationForm.taskName = task.name;
      this.nodeList = task.subList;
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
          if (this.relationInfo) {
            this.getTableRelationInfor(this.relationInfo);
          }
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
              // if (this.tableRelation && this.tableRelation.length) {
              //   this.getTableKeysList(this.tableRelation[0]);
              // } else {
              //   this.initMainTable();
              // }
              // 判断是否为再次编辑状态，判断主表名称是否存在
              if (this.tableRelation) {
                // console.log(this.tableRelation);
                let tableNameList = this.getAllTableName(this.tableRelation);
                tableNameList.forEach(tableName => {
                  let tableItem = this.tableList.find(item => item.tableName === tableName.tableName);
                  if (!tableItem) {
                    this.tableList.unshift(tableName);
                  }
                });
              }
            }
          })
          .catch((this.tableList = []));
      }
    },
    /**
     * 获取所有数据表名称
     * @param list
     * @param tableList
     */
    getAllTableName(list, tableList) {
      tableList = tableList || [];
      list.forEach(item => {
        tableList.push(item.table);
        if (item.children && item.children.length) {
          tableList = this.getAllTableName(item.children, tableList);
        }
      });
      return tableList;
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
      let params = {
        connection: this.tableRelationForm.databaseName,
        schema: this.tableRelationForm.schema,
        table: table.tableName
      };
      this.$API.getColumnsByTable(params).then(({ data }) => {
        this.$set(table.table, 'keys', data);
      });
    },
    /**
     * 初始化表关系树形结构
     */
    initMainTable() {
      this.tableRelation = [];
      let tableInfo = this.$utils.deepCopy(this.tableConfig);
      tableInfo.id = this.$utils.createRandomId();
      tableInfo.table = {};
      tableInfo.tableName = '';
      this.tableRelation.push(tableInfo);
    },
    /**
     * 新增子节点
     * @param data
     */
    append(data) {
      let tableInfo = this.$utils.deepCopy(this.tableConfig);
      tableInfo.id = this.$utils.createRandomId();
      if (!data.children) {
        this.$set(data, 'children', []);
      }
      data.children.push(tableInfo);
    },
    /**
     * 移除子节点
     * @param node
     * @param data
     */
    remove(node, data) {
      const parent = node.parent;
      const children = parent.data.children || parent.data;
      const index = children.findIndex(d => d.id === data.id);
      children.splice(index, 1);
    },
    /**
     * 显示字段配置弹窗
     * @param data
     */
    editNode(data) {
      this.crtTreeNode = data;
      // console.log(data);
      this.$emit('showConfigKey');
      this.$nextTick(() => {
        this.$eventBus.$emit('showKeys', data.table.keys);
      });
    },
    /**
     * 保存表关系信息
     */
    saveTableConfig() {
      this.$refs['form'].validate(valid => {
        // 判断是否配置数据表
        if (!this.tableRelation[0].tableName) {
          this.$utils.setMessage(false, '请设置数据表');
          return false;
        }
        if (valid) {
          // let createName = JSON.parse(sessionStorage.getItem('user')).name;
          let params = {
            // createName,
            // createDate: this.$utils.setTimeType(),
            ...this.tableRelationForm,
            mainTableName: this.tableRelation.length ? this.tableRelation[0].tableName : '',
            jsonForm: JSON.stringify(this.tableRelation)
          };
          if (!params.id) {
            delete params.id;
          }
          API.tableRelation.saveTableRelation(params).then(({ data }) => {
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
     * 选择数据表
     * @param data
     * @param evt
     * @param node
     */
    setTableId(data, evt, node) {
      if (typeof evt === 'string') {
        data.tableName = evt;
        data.table = {
          tableName: evt,
          keys: []
        };
        this.tableList.unshift(data.table);
      } else {
        data.tableName = evt.tableName;
      }
      // console.log(data);
      this.getTableKeysList(data);
      if (node.level === 1) {
        this.tableRelationForm.mainTable = data.tableName;
        // this.getMainTableKeysList(evt);
      } else {
        // this.getTableKeysList(data);
      }
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
    > div.selectDiv {
      flex: 1;
      display: flex;
    }
    /deep/ .el-select {
      margin-right: 5px;
      flex: 1;
    }
    .treeHandle {
      padding: 0 10px;
      /deep/ .el-button {
        // color: #fff;
      }
    }
  }
}
</style>
