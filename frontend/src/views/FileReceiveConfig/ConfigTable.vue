<template>
  <base-dialog v-bind="$attrs" v-on="$listeners" @save="saveTableConfig">
    <el-form
      ref="form"
      :model="tableRelationForm"
      label-width="80px"
      size="mini"
      :rules="formRules"
    >
      <el-form-item label="名称" prop="name">
        <el-input v-model="tableRelationForm.name"></el-input>
      </el-form-item>
      <el-form-item label="数据源" prop="database">
        <el-select
          v-model="tableRelationForm.database"
          placeholder="请选择数据源"
          value-key="value"
          clearable
          filterable
        >
          <el-option
            v-for="database in databaseList"
            :label="database.name"
            :value="database"
            :key="database.value"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="模式" prop="mode">
        <el-select
          v-model="tableRelationForm.mode"
          placeholder="请选择模式"
          value-key="text"
          clearable
          filterable
        >
          <el-option v-for="mode in modeList" :label="mode.text" :value="mode" :key="mode.text"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="备注" prop="remarks">
        <el-input type="textarea" v-model="tableRelationForm.remarks"></el-input>
      </el-form-item>
    </el-form>

    <el-tree
      :data="tableRelation"
      node-key="tableName"
      default-expand-all
      :expand-on-click-node="false"
    >
      <div class="custom-tree-node" slot-scope="{ node, data }">
        <div>
          <el-select
            v-model="data.table"
            size="mini"
            placeholder="请选择表..."
            @change="setTableId(data, $event, node)"
            value-key="tableName"
            clearable
            filterable
          >
            <el-option
              v-for="item in tableList"
              :key="item.tableName"
              :label="item.tableName"
              :value="item"
            ></el-option>
          </el-select>
          <!-- <el-input v-model="data.table.name" disabled size="mini" v-else></el-input> -->
          <el-select
            v-model="data.mainKey"
            size="mini"
            placeholder="请选择主键..."
            v-if="node.level>1"
            clearable
            filterable
          >
            <el-option
              v-for="item in mainTableKeysList"
              :key="item.name"
              :label="item.comments"
              :value="item.name"
            ></el-option>
          </el-select>
          <el-select
            v-model="data.outLinkKey"
            size="mini"
            placeholder="请选择外键..."
            v-if="node.level>1"
            clearable
            filterable
          >
            <el-option
              v-for="item in data.table.keys"
              :key="item.name"
              :label="item.comments"
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
          <el-button
            type="text"
            size="mini"
            icon="el-icon-delete"
            @click="() => remove(node, data)"
          ></el-button>
        </div>
      </div>
    </el-tree>
  </base-dialog>
</template>
<script>
import Vue from 'vue';
import BaseDialog from '@/components/BaseDialog/index.vue';
export default Vue.extend({
  name: 'configTable',
  components: { BaseDialog },
  props: {
    relationInfo: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      tableRelation: [],
      tableRelationForm: {
        id: '',
        name: '',
        database: '',
        mode: '',
        createDate: '',
        createBy: '',
        remarks: ''
      },
      formRules: {
        name: [{ required: true, message: '请输入表关系名称', trigger: 'blur' }],
        database: [{ required: true, message: '请输入数据源', trigger: 'blur' }],
        mode: [{ required: true, message: '请输入模式', trigger: 'blur' }]
      },
      databaseList: [],
      modeList: [],
      tableList: [],
      mainTableKeysList: [],
      tableKeysList: [],
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
  watch: {
    'tableRelationForm.database': {
      deep: true,
      handler(val) {
        if (val) {
          this.getModeList(val);
        }
      }
    },
    'tableRelationForm.mode': {
      deep: true,
      handler(val) {
        if (val) {
          let params = {
            connection: this.tableRelationForm.database.name,
            schema: this.tableRelationForm.mode.text
          };
          this.tableConfig.connection = this.tableRelationForm.database.name;
          this.tableConfig.schema = this.tableRelationForm.mode.text;
          this.getTableList(params);
        }
      }
    }
  },
  mounted() {
    this.getDatabaseList();
    // 数据回显
    if (this.relationInfo) {
      let relationInfo = this.$utils.deepCopy(this.relationInfo);
      for (let key in this.tableRelationForm) {
        this.tableRelationForm[key] = relationInfo[key];
      }
      this.tableRelation = relationInfo.tableRelation;
    }
  },
  methods: {
    // 获取数据源列表
    getDatabaseList() {
      this.$API.getDatabases().then(({ data }) => {
        this.databaseList = data.data || data;
      });
    },
    // 获取所选数据源下模式列表
    getModeList(database) {
      this.$API
        .getSchema({ connection: database.name })
        .then(({ data }) => {
          this.modeList = data.data || data;
        })
        .catch();
    },
    // 选择所选数据源和模式下数据表列表
    getTableList(param) {
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
            if (!this.relationInfo) {
              this.initMainTable();
            } else {
              this.getMainTableKeysList(this.tableRelation[0]);
            }
          }
        })
        .catch((this.tableList = []));
    },
    // 获取主数据表的字段列表
    getMainTableKeysList(table) {
      // let list = [];
      let params = {
        connection: this.tableRelationForm.database.name,
        schema: this.tableRelationForm.mode.text,
        table: table.tableName
      };
      this.$API.getColumnsByTable(params).then(({ data }) => {
        this.mainTableKeysList = data.data || data;
      });
    },
    // 获取子数据表的字段列表
    getTableKeysList(table) {
      // let list = [];
      let params = {
        connection: this.tableRelationForm.database.name,
        schema: this.tableRelationForm.mode.text,
        table: table.tableName
      };
      this.$API.getColumnsByTable(params).then(({ data }) => {
        table.table.keys = data.data || data;
      });
    },
    // 初始化表关系树形结构
    initMainTable() {
      this.tableRelation = [];
      let tableInfo = this.$utils.deepCopy(this.tableConfig);
      tableInfo.table = {};
      tableInfo.tableName = '';
      this.tableRelation.push(tableInfo);
    },
    // 新增子节点
    append(data) {
      let tableInfo = this.$utils.deepCopy(this.tableConfig);
      if (!data.children) {
        this.$set(data, 'children', []);
      }
      data.children.push(tableInfo);
    },
    // 移除子节点
    remove(node, data) {
      const parent = node.parent;
      const children = parent.data.children || parent.data;
      const index = children.findIndex(d => d.id === data.id);
      children.splice(index, 1);
    },
    // 保存表关系信息
    saveTableConfig() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          let createBy = JSON.parse(sessionStorage.getItem('user')).name;
          let params = {
            ...this.tableRelationForm,
            createBy,
            createDate: this.$utils.setTimeType(),
            tableName: this.tableRelation[0].tableName,
            tableRelation: this.tableRelation
          };
          console.log(params);
          if (this.relationInfo) {
            this.$axios.post('/api/updateTableRelation', params).then(data => {
              this.$message.success(data.data.msg);
              this.$emit('updateTable');
              this.$emit('closeDialog');
            });
          } else {
            this.$axios.post('/api/addTableRelation', params).then(data => {
              this.$message.success(data.data.msg);
              this.$emit('updateTable');
              this.$emit('closeDialog');
            });
          }
        } else {
          return false;
        }
      });
    },
    // 选择数据表
    setTableId(data, evt, node) {
      data.tableName = evt.tableName;
      if (node.level === 1) {
        this.getMainTableKeysList(evt);
      } else {
        this.getTableKeysList(data);
      }
    }
  }
});
</script>
<style lang="scss" scoped>
::v-deep .el-form {
  .el-select {
    width: 100%;
  }
}
::v-deep .el-tree-node__content {
  height: auto;
}
::v-deep .el-tree {
  background: transparent;
  .custom-tree-node {
    width: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin: 5px 0;
    ::v-deep .el-select {
      margin-right: 5px;
    }
    .treeHandle {
      padding: 0 10px;
      ::v-deep .el-button {
        color: #fff;
      }
    }
  }
}
</style>
