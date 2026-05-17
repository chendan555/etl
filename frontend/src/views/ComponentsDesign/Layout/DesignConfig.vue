<template>
  <div class="design-config">
    <div class="design-main-config">
      <el-form :model="attrConfig" label-width="80px">
        <el-form-item label="名称">
          <el-input @blur="setEleAttr('name')" v-model="attrConfig.name"></el-input>
        </el-form-item>
        <el-form-item v-if="attrConfig.hasOwnProperty('isChooseValueKey')" label="是否选择字段">
          <el-switch @change="changeSetValueKeyWay" v-model="attrConfig.isChooseValueKey"></el-switch>
        </el-form-item>
        <el-form-item
          label="字段名"
          v-if="!attrConfig.hasOwnProperty('eleList')&&!attrConfig.isChooseValueKey"
        >
          <el-input @blur="setEleAttr('valueKey')" v-model="attrConfig.valueKey"></el-input>
        </el-form-item>
        <el-form-item
          label="字段名"
          v-if="!attrConfig.hasOwnProperty('eleList')&&attrConfig.isChooseValueKey"
        >
          <el-select
            clearable
            filterable
            v-model="attrConfig.valueKey"
            @change="setEleAttr('valueKey')"
          >
            <el-option
              v-for="item in formKeys"
              :key="item.key"
              :label="item.label"
              :value="item.key"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="标题宽度">
          <el-input @blur="setEleAttr('labelWidth')" v-model="attrConfig.labelWidth"></el-input>
        </el-form-item>
        <el-form-item label="输入提示" v-if="attrConfig.hasOwnProperty('placeholder')">
          <el-input @blur="setEleAttr('placeholder')" v-model="attrConfig.placeholder"></el-input>
        </el-form-item>
        <el-form-item label="分栏比例">
          <el-select
            @change="setEleAttr('span')"
            v-model="attrConfig.span"
            clearable
            placeholder="请选择"
          >
            <el-option
              v-for="item in spanOptions"
              :key="item.value"
              :label="item.value"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <!-- 选项类接口配置 -->
        <el-form-item v-if="attrConfig.hasOwnProperty('isHttpGetOptions')" label="后台获取">
          <el-switch @change="setEleAttr('isHttpGetOptions')" v-model="attrConfig.isHttpGetOptions"></el-switch>
        </el-form-item>
        <el-form-item v-if="attrConfig.isHttpGetOptions" label="接口地址">
          <el-select
            @change="changeHttpUrl"
            v-model="attrConfig.httpUrl"
            filterable
            clearable
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
        <el-form-item v-if="attrConfig.isHttpGetOptions" label="选项文本">
          <el-input @blur="setEleAttr('optionsConfig')" v-model="attrConfig.optionsConfig.name"></el-input>
        </el-form-item>
        <el-form-item v-if="attrConfig.isHttpGetOptions" label="选项值">
          <el-input @blur="setEleAttr('optionsConfig')" v-model="attrConfig.optionsConfig.value"></el-input>
        </el-form-item>
        <!-- 树形接口配置 -->
        <el-form-item v-if="attrConfig.hasOwnProperty('isHttpGetTreeData')" label="后台获取">
          <el-switch
            @change="setEleAttr('isHttpGetTreeData')"
            v-model="attrConfig.isHttpGetTreeData"
          ></el-switch>
        </el-form-item>
        <el-form-item v-if="attrConfig.isHttpGetTreeData" label="接口地址">
          <el-select
            @change="changeHttpTreeDataUrl"
            v-model="attrConfig.httpUrl"
            filterable
            clearable
            placeholder="请选择"
          >
            <el-option
              v-for="item in reqTreeDataType"
              :key="item.url"
              :label="item.name"
              :value="item.url"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item v-if="attrConfig.isHttpGetTreeData" label="选项值">
          <el-input @blur="setEleAttr('optionsConfig')" v-model="attrConfig.optionsConfig.label"></el-input>
        </el-form-item>
        <el-form-item v-if="attrConfig.isHttpGetOptions||attrConfig.isHttpGetTreeData" label="接口参数">
          <ETLMonaco
            ref="ETLMonacoParams"
            :monacoId="$utils.createRandomId()"
            v-model="attrConfig.params"
            @change="setEleAttr('params')"
          />
        </el-form-item>
        <!-- 树形懒加载 -->
        <el-form-item v-if="attrConfig.isHttpGetTreeData" label="是否懒加载">
          <el-switch @change="setEleAttr('isLazyLoad')" v-model="attrConfig.isLazyLoad"></el-switch>
        </el-form-item>
        <!-- 树形懒加载 -->
        <el-form-item v-if="attrConfig.hasOwnProperty('multiple')" label="是否多选">
          <el-switch @change="setEleAttr('multiple')" v-model="attrConfig.multiple"></el-switch>
        </el-form-item>
      </el-form>
      <div class="dynamic-options">
        <!-- 表格 -->
        <div class="dynamic-options-box" v-if="attrConfig.hasOwnProperty('tableColumnConfig')">
          <div class="title">列配置</div>
          <el-button size="mini" @click="startTableColumnConfig">开始配置</el-button>
        </div>
        <!-- 复选、单选 -->
        <ETLOptions
          v-if="attrConfig.hasOwnProperty('options')&&!attrConfig.isHttpGetOptions"
          title="选项配置"
          :options="attrConfig.options"
          attrName="options"
          @setEleAttr="setEleAttr"
          keys="name,value"
          values="新标签,random"
        />
        <!-- tab -->
        <ETLOptions
          v-if="attrConfig.hasOwnProperty('tabList')"
          title="标签配置"
          :options="attrConfig.tabList"
          attrName="tabList"
          @setEleAttr="setEleAttr"
          keys="label,name,eleList"
          values="新标签,random,[]"
        />
        <!-- 显示隐藏 -->
        <ETLOptions
          v-if="attrConfig.hasOwnProperty('showCondition')"
          title="显隐配置"
          :options="attrConfig.showCondition"
          attrName="showCondition"
          @setEleAttr="setEleAttr"
          keys="key,value"
          values="null,null"
          editType="select,input"
          :selectOptions1="eleListValueKeyAndName"
        />
        <!-- 事件配置 -->
        <ETLOptions
          v-if="attrConfig.hasOwnProperty('events')"
          title="事件配置"
          :options="attrConfig.events"
          attrName="events"
          @setEleAttr="setEleAttr"
          @editNode="editNode"
          :showEditNode="true"
          keys="ele,event"
          values="null,null"
          editType="select,select"
          :selectOptions1="eleListId_Name_key"
          :selectOptions2="eleEvents"
          selectOptions2-filterCondition="key"
          :select1ChangeClearSelect2="true"
        />
        <!-- 树形 -->
        <ETLTreeOptions
          title="树配置"
          v-if="attrConfig.hasOwnProperty('treeData')&&!attrConfig.isHttpGetTreeData"
          @setEleAttr="setEleAttr"
          attrName="treeData"
          :treeData="attrConfig.treeData"
        />
      </div>
    </div>

    <el-dialog
      title="事件参数配置"
      :visible.sync="eventParamsConfigVisible"
      :close-on-click-modal="false"
      width="50%"
    >
      <el-form :model="eventParamsConfig" label-width="150px">
        <el-form-item label="从控件值中获取">
          <el-select
            @change="eventParamsHttpUrlChange"
            v-model="eventParamsConfig.value"
            clearable
            placeholder="可不选，默认表格添加行"
          >
            <el-option
              v-for="item in eleListId_Name_key"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="填充表格列">
          <el-select v-model="eventParamsConfig.column" clearable placeholder="可不选，默认表格添加行">
            <el-option
              v-for="item in tableColumnOptions"
              :key="item.prop"
              :label="item.label"
              :value="item.prop"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="参数处理">
          <el-select
            @change="eventParamsHttpUrlChange"
            v-model="eventParamsConfig.httpUrl"
            clearable
            placeholder="请选择"
          >
            <el-option
              v-for="item in reqEventParamsType"
              :key="item.url"
              :label="item.name"
              :value="item.url"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="eventParamsConfigSet" size="small">确 定</el-button>
      </span>
    </el-dialog>

    <el-dialog
      title="表格列配置"
      :visible.sync="tableColumnConfigVisible"
      :close-on-click-modal="false"
      width="50%"
    >
      <ETLTableColumnConfig
        ref="ETLTableColumnConfig"
        v-if="tableColumnConfigVisible"
        :tableColumnConfig="attrConfig.tableColumnConfig"
        :formKeys="formKeys"
      />
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" size="small" @click="updateTableColumnConfig">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import ETLOptions from '../components/ETLOptions';
import ETLTreeOptions from '../components/ETLTreeOptions';
import ETLMonaco from '../components/ETLMonaco';
import ETLTableColumnConfig from '../components/ETLTableColumnConfig';
import { reqOptionsType, reqTreeDataType, reqEventParamsType } from '../req_config.js';
import events from '../events';
export default {
  name: 'DesignConfig',
  components: { ETLOptions, ETLTreeOptions, ETLMonaco, ETLTableColumnConfig },
  props: {
    canvasDesign: {
      type: Object,
      default: {}
    },
    formKeys: {
      type: Array
    }
  },
  data() {
    return {
      attrConfig: {},
      defaultProps: {
        children: 'children',
        label: 'label'
      },
      curTreeNode: null,
      spanOptions: [],
      alignOptions: [{ label: '左对齐', value: 'left' }, { label: '居中对齐', value: 'center' }, { label: '右对齐', value: 'right' }],
      editTypeOptions: [{ label: '输入框', value: 'input' }, { label: '下拉框', value: 'select' }, { label: '无', value: 'none' }],
      eleEvents: events,
      eventParamsConfig: {
        value: '',
        httpUrl: '',
        resDeal: '',
        column: ''
      },
      eventOptionsIndex: 0,
      eventParamsConfigVisible: false,
      tableColumnConfigVisible: false,
      // 表格字段选项
      tableColumnOptions: []
    };
  },
  computed: {
    eleListValueKeyAndName() {
      return this.canvasDesign.getEleListValueKeyAndName();
    },
    eleListId_Name_key() {
      return this.canvasDesign.getEleListId_Name_key();
    }
  },
  created() {
    this.reqOptionsType = reqOptionsType;
    this.reqTreeDataType = reqTreeDataType;
    this.reqEventParamsType = reqEventParamsType;
    this.$eventBus.$on('attrEdit', this.attrEdit);
    this.$eventBus.$on('sendTableColumn', this.sendTableColumn);
    const spanOptions = [];
    for (let i = 1; i <= 24; i++) {
      spanOptions.push({ value: i });
    }
    this.spanOptions = spanOptions;
  },
  beforeDestroy() {
    this.$eventBus.$off('attrEdit');
    this.$eventBus.$off('sendTableColumn');
  },
  methods: {
    attrEdit(config) {
      this.attrConfig = this.$utils.deepClone(config);
      console.log(this.attrConfig);
      this.$refs.ETLMonacoParams && this.$refs.ETLMonacoParams.setValue(this.attrConfig.params);
    },
    setEleAttr(attr) {
      this.$emit('setEleAttr', this.attrConfig.id, attr, this.attrConfig[attr]);
    },
    eventParamsConfigSet() {
      this.attrConfig.events[this.eventOptionsIndex].eventParamsConfig = this.eventParamsConfig;
      this.eventParamsConfigVisible = false;
      this.setEleAttr('events');
    },
    editNode(index) {
      this.eventOptionsIndex = index;
      if (this.attrConfig.events[index].hasOwnProperty('eventParamsConfig')) {
        this.eventParamsConfig = this.attrConfig.events[index].eventParamsConfig;
      } else {
        this.eventParamsConfig = {
          value: '',
          httpUrl: '',
          resDeal: '',
          column: ''
        };
      }
      this.$eventBus.$emit('getTableColumn', this.attrConfig.events);
      this.eventParamsConfigVisible = true;
    },
    sendTableColumn(tableColumn) {
      this.tableColumnOptions = tableColumn;
    },
    changeSetValueKeyWay() {
      if (this.attrConfig.isChooseValueKey) {
        this.attrConfig.valueKey = '';
      } else {
        this.attrConfig.valueKey = '';
      }
      this.setEleAttr('valueKey');
      this.setEleAttr('isChooseValueKey');
    },
    changeHttpUrl() {
      reqOptionsType.some(item => {
        if (item.url === this.attrConfig.httpUrl) {
          this.attrConfig.optionsConfig.name = item.optionsConfig.name;
          this.attrConfig.optionsConfig.value = item.optionsConfig.value;
          if (item.optionsConfig.params) {
            this.attrConfig.params = item.optionsConfig.params;
            this.$refs.ETLMonacoParams.setValue(item.optionsConfig.params);
          }
          return true;
        }
      });
      this.setEleAttr('httpUrl');
      this.setEleAttr('params');
      this.setEleAttr('optionsConfig');
    },
    changeHttpTreeDataUrl() {
      reqTreeDataType.some(item => {
        if (item.url === this.attrConfig.httpUrl) {
          this.attrConfig.optionsConfig.label = item.treeConfig.label;
          this.$refs.ETLMonacoParams.setValue(item.treeConfig.params);
          this.attrConfig.params = item.treeConfig.params;
          this.attrConfig.isLazyLoad = item.treeConfig.isLazyLoad;
          return true;
        }
      });
      this.setEleAttr('httpUrl');
      this.setEleAttr('params');
      this.setEleAttr('optionsConfig');
      this.setEleAttr('isLazyLoad');
    },
    eventParamsHttpUrlChange(url) {
      this.reqEventParamsType.forEach(req => {
        if (url === req.url) {
          this.eventParamsConfig.resDeal = req.resDeal;
        }
      });
      this.setEleAttr('events');
    },
    startTableColumnConfig() {
      this.tableColumnConfigVisible = true;
    },
    updateTableColumnConfig() {
      this.attrConfig.tableColumnConfig = this.$refs.ETLTableColumnConfig.getTableColumnConfig();
      this.setEleAttr('tableColumnConfig');
      this.tableColumnConfigVisible = false;
    }
  }
};
</script>
<style lang="scss" scoped>
.design-config {
  border-left: 1px solid #808353;
  width: 300px;
  height: 100%;
  .design-main-config {
    padding: 5px;
    box-sizing: border-box;
    height: 100%;
    overflow-y: scroll;
    .dynamic-options {
      &-box {
        border-top: 1px solid #808353;
        padding: 10px 0;
        .title {
          width: 80px;
          font-size: 14px;
          text-align: center;
          margin-bottom: 10px;
        }
        .el-button {
          margin-bottom: 10px;
        }
      }
    }
  }
  .el-form-item {
    margin-bottom: 5px;
  }
  .node-operation {
    padding: 10px 10px 0 10px;
    border-top: 1px solid #808353;
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
    .el-button {
      margin-bottom: 10px;
      margin-left: 0;
    }
  }
  /deep/ .el-select {
    width: 100%;
  }
  /deep/ .el-date-editor.el-input {
    width: 100%;
  }
}
</style>

