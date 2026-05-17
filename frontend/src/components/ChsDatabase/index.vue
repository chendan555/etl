<template>
  <el-form
    ref="form"
    :model="ruleForm"
    :label-width="props.labelWidth"
    size="mini"
    :rules="formRules"
  >
    <el-form-item :label="props.databaseLabel" prop="databaseId">
      <el-select
        v-model="ruleForm.databaseId"
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
    <el-form-item :label="props.schemaLabel" v-if="props.isShowSchema" prop="schema">
      <el-select
        v-model="ruleForm.schema"
        placeholder="请选择模式"
        @change="setSchema"
        allow-create
        clearable
        filterable
      >
        <el-option
          v-for="mode in schemaList"
          :label="mode.text"
          :value="mode.text"
          :key="mode.text"
        ></el-option>
      </el-select>
    </el-form-item>
    <el-form-item :label="props.tableLabel" v-if="props.isShowTable" prop="table">
      <el-select
        v-model="ruleForm.table"
        placeholder="请选择数据表"
        allow-create
        clearable
        filterable
        @change="setTable"
      >
        <el-option
          v-for="mode in tableList"
          :label="mode.tableName"
          :value="mode.tableName"
          :key="mode.tableName"
        ></el-option>
      </el-select>
    </el-form-item>
    <el-form-item :label="props.columnLabel" v-if="props.isShowColumn" prop="column">
      <el-select
        v-model="ruleForm.column"
        placeholder="请选择数据表字段"
        allow-create
        clearable
        filterable
        @change="setColumn"
      >
        <el-option
          v-for="mode in columnList"
          :label="mode.name"
          :value="mode.name"
          :key="mode.name"
        ></el-option>
      </el-select>
    </el-form-item>
  </el-form>
</template>
<script lang="ts">
/**
 *
 * Create by Leo
 * 基础组件
 * 基础数据源、模式、数据表配置组件
 * 左右两栏式布局
 * 坐侧区域可控制显隐
 * 并减少一定的代码量
 * 统一设置弹框的配置项、确认按钮、取消按钮、关闭按钮等信息
 *
 */

import Vue from 'vue';
export default Vue.extend({
  name: 'chsDatabase',
  props: {
    /**
     * 配置项
     */
    config: {
      type: Object,
      default: () => {
        return {};
      }
    }
  },
  data() {
    return {
      /**
       * 配置项
       */
      formRules: {
        databaseId: [{ required: true, message: '请输入数据源', trigger: 'blur' }],
        schema: [{ required: true, message: '请输入模式', trigger: 'blur' }],
        table: [{ required: true, message: '请输入数据表', trigger: 'blur' }],
        column: [{ required: true, message: '请输入表字段', trigger: 'blur' }]
      },
      /**
       * 配置项
       */
      ruleForm: {
        databaseId: '',
        connection: '',
        schema: '',
        table: '',
        column: ''
      },
      /**
       * 配置项
       */
      databaseList: <any>[],
      /**
       * 配置项
       */
      schemaList: <any>[],
      /**
       * 配置项
       */
      tableList: <any>[],
      /**
       * 配置项
       */
      columnList: <any>[],
      /**
       * 配置项
       */
      defaultProps: {
        isShowSchema: true,
        isShowTable: true,
        isShowColumn: false,
        databaseLabel: '数据源',
        schemaLabel: '模式',
        tableLabel: '数据表',
        columnLabel: '表字段',
        labelWidth: '100px'
      }
    };
  },
  computed: {
    /**
     * 配置项
     */
    props(): any {
      this.ruleForm = {
        ...this.ruleForm,
        ...this.config.databaseRuleForm
      };
      return {
        ...this.defaultProps,
        ...this.config
      };
    }
  },
  mounted() {
    this.getDatabaseList();
  },
  methods: {
    /**
     * 获取数据源列表
     */
    getDatabaseList() {
      this.$API.getDatabases().then(({ data }: any) => {
        this.databaseList = data;
      });
    },
    /**
     * 获取模式列表
     */
    getSchemaList() {
      let params = {
        connection: this.ruleForm.connection
      };
      this.$API.getSchema(params).then(({ data }: any) => {
        this.schemaList = data;
      });
    },
    /**
     * 获取数据表列表
     */
    getTableList() {
      let params = {
        connection: this.ruleForm.connection,
        schema: this.ruleForm.schema,
        pageSize: 100000
      };
      this.$API.getTableDatas(params).then(({ data }: any) => {
        this.tableList = data.data.records;
      });
    },
    /**
     * 获取数据表字段列表
     */
    getColumnList() {
      let params = {
        connection: this.ruleForm.connection,
        schema: this.ruleForm.schema,
        table: this.ruleForm.table
      };
      this.$API.getColumnsByTable(params).then(({ data }: any) => {
        this.columnList = data;
      });
    },
    /**
     * 设置数据源
     */
    setDatabase(id: string) {
      let database = this.databaseList.find((database: any) => database.databaseId === id);
      if (database) {
        this.ruleForm.connection = database.name;
        this.ruleForm.schema = '';
        this.ruleForm.table = '';
        this.getSchemaList();
      }
    },
    /**
     * 设置模式
     */
    setSchema() {
      this.ruleForm.table = '';
      if (this.props.isShowTable) {
        this.getTableList();
      } else {
        this.$emit('emitData', this.ruleForm);
      }
    },
    /**
     * 设置数据表
     */
    setTable() {
      if (this.props.isShowColumn) {
        this.getColumnList();
      } else {
        this.$emit('emitData', this.ruleForm);
      }
    },
    /**
     * 设置数据表字段
     */
    setColumn() {
      this.$emit('emitData', this.ruleForm);
    }
  }
});
</script>
<style lang="scss" scoped>
/deep/ .el-select {
  width: 100%;
}
</style>
