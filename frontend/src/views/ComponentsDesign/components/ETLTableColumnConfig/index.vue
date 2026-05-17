<template>
  <div class="table-config">
    <div class="table-config-operation">
      <el-button class size="mini" @click="addColumn">增加列</el-button>
    </div>
    <div class="table-config-container">
      <div
        class="table-column-config"
        v-for="(columnConfig,index) in editTableColumnConfig"
        :key="index"
      >
        <el-form :model="columnConfig" label-width="80px">
          <el-form-item label="列名">
            <el-input v-model="columnConfig.label"></el-input>
          </el-form-item>
          <el-form-item label="默认值">
            <el-input v-model="columnConfig.defaultValue"></el-input>
          </el-form-item>
          <el-form-item label="字段名">
            <el-select
              clearable
              filterable
              v-model="columnConfig.valueKey"
              @change="columnConfigValueKeyChange(columnConfig)"
            >
              <el-option
                v-for="item in formKeys"
                :key="item.key"
                :label="item.label"
                :value="item.key"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="表头对齐">
            <el-select v-model="columnConfig.headerAlign" placeholder="请选择">
              <el-option
                v-for="item in alignOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="列对齐">
            <el-select v-model="columnConfig.align" placeholder="请选择">
              <el-option
                v-for="item in alignOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="编辑类型">
            <el-select v-model="columnConfig.editType" placeholder="请选择">
              <el-option
                v-for="item in editTypeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item v-if="showConfig(columnConfig)" label="是否通过接口请求">
            <el-switch v-model="columnConfig.isHttpGetOptions"></el-switch>
          </el-form-item>
          <el-form-item
            label="下拉选项"
            v-if="showConfig(columnConfig) && !columnConfig.isHttpGetOptions"
          >
            <ETLOptions :options="columnConfig.options" keys="name,value" values="新标签,random"/>
          </el-form-item>
          <el-form-item
            v-if="showConfig(columnConfig) && columnConfig.isHttpGetOptions"
            label="接口地址"
          >
            <el-select
              @change="changeHttpUrl(index)"
              v-model="columnConfig.httpUrl"
              filterable
              placeholder="请选择"
            >
              <el-option
                v-for="item in reqOptionsType"
                :key="item.url"
                :label="item.name"
                :value="item.url"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item
            v-if="showConfig(columnConfig) && columnConfig.isHttpGetOptions"
            label="是否可以多选"
          >
            <el-switch v-model="columnConfig.optionsConfig.multiple"></el-switch>
          </el-form-item>
          <el-form-item
            v-if="showConfig(columnConfig) && columnConfig.isHttpGetOptions"
            label="请求时依赖行数据"
          >
            <el-switch v-model="columnConfig.optionsConfig.isRelyOnRowData"></el-switch>
          </el-form-item>
          <el-form-item
            v-if="showConfig(columnConfig) && columnConfig.isHttpGetOptions"
            label="选项文本"
          >
            <el-input v-model="columnConfig.optionsConfig.name"></el-input>
          </el-form-item>
          <el-form-item
            v-if="showConfig(columnConfig) && columnConfig.isHttpGetOptions"
            label="选项值"
          >
            <el-input v-model="columnConfig.optionsConfig.value"></el-input>
          </el-form-item>
          <el-form-item
            v-if="showConfig(columnConfig) && columnConfig.isHttpGetOptions"
            label="接口参数"
          >
            <ETLMonaco :monacoId="$utils.createRandomId()" v-model="columnConfig.params"/>
          </el-form-item>
        </el-form>
        <el-button size="mini" @click="removeColumn(index, $event)">删除列</el-button>
      </div>
    </div>
  </div>
</template>
<script>
import { table_column_config } from '../../table_column_config';
import ETLOptions from '../ETLOptions';
import ETLMonaco from '../ETLMonaco';
import { reqOptionsType, reqOptionsDealType } from '../../req_config.js';
export default {
  name: 'ETLTableColumnConfig',
  components: { ETLOptions, ETLMonaco },
  props: {
    tableColumnConfig: {
      type: Array
    },
    formKeys: {
      type: Array
    }
  },
  data() {
    return {
      current: 0,
      editTableColumnConfig: this.$utils.deepClone(this.tableColumnConfig),
      alignOptions: [{ label: '左对齐', value: 'left' }, { label: '居中对齐', value: 'center' }, { label: '右对齐', value: 'right' }],
      editTypeOptions: [
        { label: '输入框', value: 'input' },
        { label: '下拉框', value: 'select' },
        { label: '级联选择器', value: 'cascader' },
        { label: '无', value: 'none' }
      ]
    };
  },
  created() {
    this.reqOptionsType = reqOptionsType;
    this.reqOptionsDealType = reqOptionsDealType;
  },
  methods: {
    showConfig(columnConfig) {
      return columnConfig.editType === 'select' || columnConfig.editType === 'cascader';
    },
    columnConfigValueKeyChange(config) {
      if (config.valueKey) {
        if (Array.isArray(config.valueKey) && config.valueKey.length > 0) {
          config.prop = config.valueKey[config.valueKey.length - 1];
        } else {
          config.prop = config.valueKey;
        }
      }
    },
    addColumn() {
      this.editTableColumnConfig.push(this.$utils.deepClone(table_column_config));
      console.log(this.editTableColumnConfig);
    },
    removeColumn(index, event) {
      this.current = index;
      this.editTableColumnConfig.splice(this.current, 1);
    },
    getTableColumnConfig() {
      return this.editTableColumnConfig;
    },
    changeHttpUrl(index) {
      reqOptionsType.some(item => {
        if (item.url === this.editTableColumnConfig[index].httpUrl) {
          this.editTableColumnConfig[index].optionsConfig.name = item.optionsConfig.name;
          this.editTableColumnConfig[index].optionsConfig.value = item.optionsConfig.value;
          return true;
        }
      });
    }
  }
};
</script>
<style lang="scss" scoped>
.table-config {
  margin-top: 10px;
  .table-config-operation {
    height: 40px;
  }
  .table-config-container {
    margin-top: 10px;
    overflow-y: scroll;
    height: calc(100vh - 38vh);
    .table-column-config {
      padding: 10px;
      background: #143c65;
      margin-bottom: 10px;
    }
  }
}
</style>

