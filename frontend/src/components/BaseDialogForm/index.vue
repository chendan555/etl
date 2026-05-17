<template>
  <div>
    <dialog-model :dialog="dialog" @on-open="modelOpen" @onClose="modelCancel">
      <div slot="slot-form">
        <el-tabs
          v-if="isShowTab"
          v-model="tabActive"
          :before-leave="tabBeforeLeave"
          @tab-click="tabChange"
        >
          <el-tab-pane
            v-for="(item, index) in tabList"
            :key="index"
            :label="item.label"
            :name="item.name"
          ></el-tab-pane>
        </el-tabs>
        <base-form
          ref="baseForm"
          v-if="crtFormItemConfig.length"
          :formItemConfig="crtFormItemConfig"
          :formItemRuleConfig="formItemRuleConfig"
          :ruleForm="crtRuleForm"
          :tableData="tableDataCopy"
          :formPreview="formPreview"
          @formValidate="formValidate"
        />
      </div>
      <div slot="slot-monaco" v-if="isHasMonaco">
        <div id="monaco"></div>
        <div class="checkCode">
          <!-- <el-button type="primary" size="mini" v-if="monacoType === 'java'" @click="checkCodeClass">测试类</el-button> -->
          <el-button type="primary" size="mini" @click="deleteAllCode">删除代码</el-button>
        </div>
      </div>
      <div slot="slot-table-operation" style="margin:10px 0;">
        <el-tabs v-model="temActiveName" v-if="tabVisible">
          <el-tab-pane label="字段" name="first">字段</el-tab-pane>
          <el-tab-pane label="其它输出字段" name="second">其它输出字段</el-tab-pane>
        </el-tabs>
        <slot
          v-if="temActiveName!='second'&&(isHasTable && (crtRuleForm.hasOwnProperty('isHasTable')?crtRuleForm.isHasTable:true)) || (tableColumnConfigList&&tableColumnConfigList.length)"
          name="slot-form-table-operation"
        ></slot>
      </div>
      <div slot="slot-table">
        <base-table
          height="300px"
          v-if="temActiveName!='second'&&isHasTable && (crtRuleForm.hasOwnProperty('isHasTable')?crtRuleForm.isHasTable:true)"
          :resizable="resizable"
          :isShowOperation="isShowOperation"
          :isShowCheckBox="isShowCheckBox"
          :tableColumnConfig="crtTableColumnConfig"
          :tableData="tableDataCopy"
          @selectionChange="selectionChange"
        >
          <div slot="operation" slot-scope="scope">
            <span @click="deleteRow(scope)">删除</span>
            <span
              style="margin-left:4px"
              @click="$emit('moveRowPosition', scope.row.$index, true)"
              v-if="_props.handleExtensionVisible"
            >上移</span>
            <span
              style="margin-left:4px"
              @click="$emit('moveRowPosition', scope.row.$index, false)"
              v-if="_props.handleExtensionVisible"
            >下移</span>
          </div>
        </base-table>
        <!-- 多表格情况 -->
        <div v-if="temActiveName!='second'&&tableColumnConfigList.length" class="mul-table">
          <div v-for="(config,index) in tableColumnConfigList" :key="index" class="mul-table-inner">
            <div class="mul-table-inner-title">{{config.title}}</div>
            <div class="btndiv">
              <el-button size="mini" @click="getSglField(config)">获取字段</el-button>
              <el-button size="mini" @click="addSglRow(config)">新增行</el-button>
              <el-button size="mini" @click="deleteAll(config)">删除全部</el-button>
            </div>
            <base-table
              height="300px"
              :resizable="resizable"
              :isShowOperation="isShowOperation"
              :isShowCheckBox="isShowCheckBox"
              :tableColumnConfig="config.tableColumnConfig"
              :tableData="crtRuleForm[config.key]"
              @selectionChange="selectionChange"
            >
              <div slot="operation" slot-scope="scope">
                <span @click="deleteRow(scope,crtRuleForm[config.key])">删除</span>
                <span
                  style="margin-left:4px"
                  @click="$emit('moveRowPosition', scope.row.$index, true, crtRuleForm, config.key)"
                  v-if="_props.handleExtensionVisible"
                >上移</span>
                <span
                  style="margin-left:4px"
                  @click="$emit('moveRowPosition', scope.row.$index, false, crtRuleForm, config.key)"
                  v-if="_props.handleExtensionVisible"
                >下移</span>
              </div>
            </base-table>
          </div>
        </div>
        <SqlConditionTree
          v-else-if="temActiveName!='second'&&isHasSqlConditionTree"
          ref="sqlConditionTree"
          :sqlConditionTree="crtRuleForm.sqlConditionTree"
        />
        <DoubleTable
          v-else-if="temActiveName!='second'&&isHasDoubleTable"
          :leftTableColumnConfig="doubleTableConfig.leftTableColumnConfig"
          :leftTableData="crtRuleForm.leftTableData"
          :rightTableColumnConfig="doubleTableConfig.rightTableColumnConfig"
          :rightTableData="crtRuleForm.rightTableData"
          :ruleForm="crtRuleForm"
        />
        <slot name="secondTable" v-else-if="tabTableVisible&&temActiveName=='second'"></slot>
      </div>
      <div slot="slot-footer" style="display: flex;justify-content: flex-end">
        <el-button v-if="isHasCreateTableBtn" size="mini" @click="createTable" type="primary">创建表</el-button>
        <el-button v-if="!formPreview && !isHasExec" size="mini" @click="confirm" type="primary">确定</el-button>
        <el-button size="mini" v-if="isHasPreview" @click="previewTable" type="primary">预览</el-button>
        <el-button size="mini" v-if="isHasPreviewOut" @click="previewTableOut" type="primary">预览</el-button>
        <el-button
          size="mini"
          v-if="isHasPreviewExcel&&ruleForm.accept_filenames=='N'"
          @click="previewTableExcel"
          type="primary"
        >预览</el-button>
        <el-button size="mini" v-if="isFilePreview" @click="filePreview" type="primary">预览</el-button>
        <!-- 表输出预览，取消该功能，功能暂未实现20210114 -->
        <el-button size="mini" v-if="isHasPreviewTrans" @click="previewTrans" type="primary">预览转换</el-button>
        <el-button size="mini" v-if="isHasExec" @click="startExec" type="primary">启 动</el-button>
        <el-button size="mini" @click="close">关 闭</el-button>
      </div>
    </dialog-model>
    <!-- 表输入预览 -->
    <base-dialog-table
      :tableColumnConfig="preViewTableColumnConfig"
      :tableData="preViewTableData"
      :dialog="preViewDialog"
    ></base-dialog-table>
    <!--  -->
    <dialog-model :dialog="flowChartDialog">
      <div slot="slot-form">
        <flow-chart-page :path="transPath"/>
      </div>
    </dialog-model>
  </div>
</template>
<script>
/**
 * Create by Leo
 * 基础组件
 * 基础弹框表单组件
 * 用于页面弹框显示输入表单的控制
 */

// 引入DialogModel组件
import DialogModel from '@/components/DialogModel';
// 引入BaseForm组件
import BaseForm from '@/components/BaseForm';
// 引入BaseTable组件
import BaseTable from '@/components/BaseTable';
// 引入DoubleTable组件
import DoubleTable from '@/components/DoubleTable';
// 引入BaseDialogTable组件
import BaseDialogTable from '@/components/BaseDialogTable';
// 引入SqlConditionTree组件
import SqlConditionTree from '@/components/SqlConditionTree';
// 引入monaco编辑器插件
import * as monaco from 'monaco-editor/esm/vs/editor/editor.api.js';
import 'monaco-editor/esm/vs/basic-languages/mysql/mysql.contribution.js';
// import FlowChart from '@/FlowChart/index';
// 引入流程画布
import FlowChartPage from '@/views/FlowChart/flowChart';
// 引入接口API
import API from '@/api/index';
// import { clear } from '../../utils/index';
export default {
  name: 'addBtn',
  components: {
    DialogModel,
    BaseForm,
    BaseTable,
    BaseDialogTable,
    SqlConditionTree,
    DoubleTable,
    FlowChartPage
  },
  props: {
    /**
     * 其他配置信息
     */
    otherConfig: {
      type: Object,
      default: () => {
        return {};
      }
    },
    /**
     * tab显隐控制
     */
    tabVisible: {
      type: Boolean,
      default: false
    },
    /**
     * tab当前框名称
     */
    activeName: {
      type: String,
      default: 'first'
    },
    /**
     * 弹框信息
     */
    dialog: {
      type: Object
    },
    /**
     * 是否有可执行代码
     */
    isHasExec: {
      type: Boolean,
      default: false
    },
    /**
     * 表单信息
     */
    formItemConfig: {
      type: Array,
      default: []
    },
    /**
     * 表单数据对象
     */
    ruleForm: {
      type: Object,
      default: {}
    },
    /**
     * 表单验证信息
     */
    formItemRuleConfig: {
      type: Object,
      default: () => {
        return {};
      }
    },
    /**
     * 可控大小
     */
    resizable: {
      type: Boolean,
      default: true
    },
    /**
     * 是否展示操作按钮
     */
    isShowOperation: {
      type: Boolean,
      default: false
    },
    /**
     * 是否展示tab框
     */
    isShowTab: {
      type: Boolean,
      default: false
    },
    /**
     * 是否展示勾选框
     */
    isShowCheckBox: {
      type: Boolean,
      default: false
    },
    /**
     * 表格显隐控制
     */
    tabTableVisible: {
      type: Boolean,
      default: false
    },
    /**
     * 高度
     */
    height: {
      type: String
    },
    /**
     * 表格列表信息配置
     */
    tableColumnConfig: {
      type: Array,
      default: () => {
        return [];
      }
    },
    /**
     * 表格列表信息配置
     */
    tableColumnConfigList: {
      type: Array,
      default: () => {
        return [];
      }
    },
    /**
     * tab列表数据
     */
    tabListData: {
      type: Array,
      default: () => {
        return [];
      }
    },
    /**
     * 表格数据
     */
    tableData: {
      type: Array,
      default: () => {
        return [];
      }
    },
    /**
     * 通用表单预览
     */
    formPreview: {
      type: Boolean,
      default: false
    },
    /**
     * 是否展示monaco组件
     */
    isHasMonaco: {
      type: Boolean,
      default: false
    },
    /**
     * monaco组件类型
     */
    monacoType: {
      type: String
    },
    /**
     * 是否含有表格
     */
    isHasTable: {
      type: Boolean,
      default: true
    },
    /**
     * 是否含有创建表按钮
     */
    isHasCreateTableBtn: {
      type: Boolean,
      default: false
    },
    /**
     * 是否含有数据预览按钮
     */
    isHasPreview: {
      type: Boolean,
      default: false
    },
    /**
     * 是否含有预览按钮
     */
    isHasPreviewOut: {
      type: Boolean,
      default: false
    },
    /**
     * 是否含有excel预览按钮
     */
    isHasPreviewExcel: {
      type: Boolean,
      default: false
    },
    /**
     * 是否含有文件预览按钮
     */
    isFilePreview: {
      type: Boolean,
      default: false
    },
    /**
     * 是否含有预览转换
     */
    isHasPreviewTrans: {
      type: Boolean,
      default: false
    },
    // 预览流程图参数
    transPath: {
      type: String
    },
    /**
     * 是否双表格
     */
    isHasDoubleTable: {
      type: Boolean,
      default: false
    },
    /**
     * 双表格配置
     */
    doubleTableConfig: {
      type: Object
    },
    /**
     * 是否含有条件树
     */
    isHasSqlConditionTree: {
      type: Boolean,
      default: false
    },
    /**
     * 条件树配置
     */
    sqlConditionTree: {
      type: Array,
      default: () => {
        return [];
      }
    },
    // 选项切换列表
    tabList: {
      type: Array,
      default: () => {
        return [];
      }
    },
    /**
     * 是否含有表格
     */
    tabActiveInit: {
      type: String
    },
    /**
     * 是否含有表格
     */
    handleExtensionVisible: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      /**
       * 当前获取窗口名称
       */
      temActiveName: this.$props.activeName,
      /**
       * 数据预览弹框配置
       */
      preViewDialog: {
        title: '数据0000预览',
        visible: false,
        append: true
      },
      /**
       * 流程画布预览弹框配置
       */
      flowChartDialog: {
        title: '流程预览',
        visible: false,
        append: true
      },
      /**
       * 预览表格数据
       */
      preViewTableData: [],
      /**
       * 预览表格配置
       */
      preViewTableColumnConfig: [],
      /**
       * tab当前获取框
       */
      tabActive: '',
      /**
       * 表格数据
       */
      tableDataCopy: this.tableData,
      /**
       * tab列表数据
       */
      tabListDataCopy: this.tabListData,
      /**
       * 当前配置表单信息
       */
      crtFormItemConfig: this.formItemConfig,
      /**
       * 当前表格列表配置信息
       */
      crtTableColumnConfig: this.tableColumnConfig,
      /**
       * 表单配置对象
       */
      crtRuleForm: this.ruleForm
    };
  },
  watch: {
    /**
     * 监听ruleForm.dataTable
     */
    'ruleForm.dataTable': {
      handler: function(v) {
        if (this.isHasMonaco && this.monacoInstance) {
          const loading = this.$utils.setLoading('查询中...');
          API.getTableFields({
            databaseName: this.crtRuleForm.connection,
            schema: this.crtRuleForm.dataBase,
            table: this.crtRuleForm.dataTable,
            path: this.$route.query.path
          })
            .then(res => {
              let data = res.data;
              if (data.length) {
                let fields = data
                  .map(item => {
                    return item.name;
                  })
                  .join();
                let sqlString = `SELECT ${fields} FROM ${this.crtRuleForm.dataTable}`;
                this.crtRuleForm.sql = sqlString;
                this.monacoInstance.setValue(sqlString);
              } else {
                this.$message.warning('未查询到数据表中的数据，请重新选择。');
              }
            })
            .finally(() => {
              loading.close();
            });
        }
      }
    },
    /**
     * 监听tab切换
     */
    tabActiveInit: {
      handler: function(value) {
        this.tabActive = value;
        this.$emit('tabChange', this.tabActive);
      },
      immediate: true
    },
    /**
     * 监听表单配置项变化
     */
    formItemConfig: {
      deep: true,
      handler(val) {
        if (this.tabList.length) {
          this.crtFormItemConfig = val;
        }
      }
    },
    /**
     * 监听表格配置项变化
     */
    tableColumnConfig: {
      deep: true,
      handler(val) {
        this.crtTableColumnConfig = val;
      }
    },
    /**
     * 表格数据
     */
    tableData: {
      deep: true,
      handler(val) {
        this.tableDataCopy = val;
      }
    },
    /**
     * 表单对象
     */
    ruleForm: {
      deep: true,
      handler(val) {
        this.crtRuleForm = val;
      }
    }
  },
  mounted() {
    // if (this.tabActiveInit) {
    //   this.tabActive = this.tabActiveInit;
    // }

    this.$nextTick(() => {
      if (this.isHasMonaco) {
        this.monacoInstance = monaco.editor.create(document.getElementById('monaco'), {
          value: this.crtRuleForm.sql ? this.crtRuleForm.sql : '',
          language: this.monacoType || 'mysql',
          theme: 'vs-dark',
          automaticLayout: true
        });
      }
    });
    // 代码编辑器内容更新
    this.$eventBus.$on('setSql', data => {
      let crtSql = this.monacoInstance.getValue();
      let newSql = crtSql + data;
      this.$set(this.crtRuleForm, 'sql', newSql);
      this.monacoInstance.setValue(newSql);
    });
  },
  destroyed() {
    this.crtFormItemConfig = [];
    this.crtTableColumnConfig = [];
    this.tableDataCopy = [];
    if (this.isHasMonaco) {
      this.monacoInstance.dispose();
    }
  },
  methods: {
    /**
     * 删除代码编辑器中所有代码
     */
    deleteAllCode() {
      this.monacoInstance.setValue('');
      this.crtRuleForm.sql = '';
    },
    // 代码为Java时可执行测试类方式测试代码是否可执行
    // checkCodeClass() {
    //   let sql = this.monacoInstance.getValue();
    // },
    /**
     * 全部删除
     */
    deleteAll(config) {
      this.crtRuleForm[config.key] = [];
    },
    /**
     * 单表格获取字段
     */
    getSglField(config) {
      const loading = this.$utils.setLoading('获取中...');
      let params = {
        nodeId: this.$store.state.nodeId,
        before: true
      };
      // console.log(this.currentNodeMsg);
      // 获取字段前清除所有已有数据
      this.crtRuleForm[config.key] = [];

      this.tableDataCopy = [];
      if (this.crtRuleForm.remove) {
        this.crtRuleForm.remove = [];
      }

      API.getStepsFileds(params)
        .then(data => {
          let tableData = [];
          if (data.data || data.data.content) {
            data.data.forEach(dataItem => {
              this.crtRuleForm[config.key].push(this.initRowData(config.tableColumnConfig, dataItem));
            });
          }
          this.tableDataCopy = tableData;
        })
        .finally(() => {
          loading.close();
        });
    },
    /**
     * 单表格新增行
     */
    addSglRow(config) {
      this.crtRuleForm[config.key].push(this.initRowData(config.tableColumnConfig));
    },
    /**
     * 表格行初始化
     */
    initRowData(tableColumnConfig, data) {
      let addRowData = {};
      tableColumnConfig.forEach(item => {
        // 每个控件中获取字段的对应key
        if (
          [
            'in_stream_name',
            'name',
            'field_name',
            'keyLookup',
            'updateLookup',
            'tableChartsName',
            'keyStream1',
            'keyStream2',
            'field',
            'rename',
            'name1'
          ].includes(item.prop) &&
          data
        ) {
          addRowData[item.prop] = data.name;
        } else {
          addRowData[item.prop] = item.hasOwnProperty('default') ? item.default : '';
        }
      });
      return addRowData;
    },
    /**
     * 开始执行
     */
    startExec() {
      this.$emit('startExec');
    },
    /**
     * tab切换验证
     */
    tabBeforeLeave(activeName, oldActiveName) {
      if (this.crtFormItemConfig.length) {
        return this.$refs.baseForm.validate(false);
      }
    },
    /**
     * tab切换
     */
    tabChange(tab) {
      this.crtFormItemConfig = [];
      this.crtTableColumnConfig = [];
      // this.tableDataCopy = [];
      this.$emit('tabChange', this.tabActive);
    },
    /**
     * 删除行
     */
    deleteRow(scope, arr) {
      this.$emit('deleteRow', scope.row.$index, arr);
    },
    /**
     * 展示弹框
     */
    add() {
      this.dialog.visible = true;
    },
    /**
     * 点击确认按钮
     */
    confirm() {
      if (this.isHasMonaco) {
        this.crtRuleForm.sql = this.monacoInstance.getValue();
      }
      this.$refs.baseForm && this.$refs.baseForm.validate();
      this.$emit('confirm');
    },
    /**
     * 预览表格数据
     */
    previewTable() {
      const loading = this.$utils.setLoading('查询中...');
      this.$emit('formSubmit', true, false);
      API.getPreviewData({
        nodeId: this.$store.state.nodeId,
        rowLimit: 1000
      })
        .then(res => {
          let data = res.data;
          if (data.columnsList && data.columnsList.length) {
            this.preViewDialog.visible = true;

            this.preViewTableColumnConfig = data.columnsList.map(item => {
              return { label: item.comments ? item.comments : item.columnName, prop: item.columnName };
            });

            this.preViewTableData = data.firstRecords;
          } else {
            this.$message.warning('该数据表暂无数据，请重新选择。');
          }
        })
        .finally(() => {
          loading.close();
        });
    },
    /**
     * 文件预览
     */
    filePreview() {
      let files = this.tableDataCopy.map(ele => {
        // 获取文件名组件
        const { fileMask, includeSubFolders } = ele;
        // 获取子目录名称组件
        const { folderName, folderRequire } = ele;
        return {
          fileMask,
          includeSubFolders,
          folderName,
          folderRequire,
          name: ele.fileName
        };
      });
      const loading = this.$utils.setLoading('查询中...');
      this.$emit('formSubmit', true, false);
      API.filePreview({
        nodeId: this.$store.state.nodeId,
        files
      })
        .then(res => {
          let data = res.data;
          if (data.fields && data.fields.length) {
            this.preViewDialog.visible = true;
            // 返回特定数据结果
            this.preViewTableColumnConfig = data.fields.map(item => {
              return { label: item, prop: item };
            });

            this.preViewTableData = data.previewDatas;
          } else {
            this.$message.warning('该文件暂无数据，请重新选择。');
          }
        })
        .finally(() => {
          loading.close();
        });
    },
    /**
     * 预览excel表格
     */
    previewTableExcel() {
      const loading = this.$utils.setLoading('查询中...');
      this.$emit('formSubmit', true, false);
      API.getExcelDataOut({
        nodeId: this.$store.state.nodeId,
        rowLimit: 1000,
        path: this.ruleForm.fileName,
        index: this.ruleForm.sheetName,
        spreadsheet_type: this.crtRuleForm.spreadsheet_type
      })
        .then(res => {
          let data = res.data;
          if (data.columnsList && data.columnsList.length) {
            this.preViewDialog.visible = true;

            this.preViewTableColumnConfig = data.columnsList.map(item => {
              return { label: item.comments ? item.comments : item.columnName, prop: item.columnName };
            });

            this.preViewTableData = data.firstRecords;
          } else {
            this.$message.warning('该数据表暂无数据，请重新选择。');
          }
        })
        .finally(() => {
          loading.close();
        });
    },
    /**
     * 数据预览
     */
    previewTableOut() {
      const loading = this.$utils.setLoading('查询中...');
      this.$emit('formSubmit', true, false);
      API.getPreviewDataOut({
        nodeId: this.$store.state.nodeId,
        rowLimit: 1000
      })
        .then(res => {
          let data = res.data;
          if (data.columnsList && data.columnsList.length) {
            this.preViewDialog.visible = true;

            this.preViewTableColumnConfig = data.columnsList.map(item => {
              return { label: item.comments ? item.comments : item.columnName ? item.columnName : item, prop: item.columnName || item };
            });

            this.preViewTableData = data.firstRecords;
          } else {
            this.$message.warning('该数据表暂无数据，请重新选择。');
          }
        })
        .finally(() => {
          loading.close();
        });
    },
    /**
     * 创建表格
     */
    createTable() {
      if (!this.crtRuleForm.table) {
        this.$message.error('请输入新建表名！');
        return;
      }
      let path = typeof this.$route.query.path === 'string' ? this.$route.query.path : '';
      API.getCreateTable({
        title: this.dialog.title,
        tranName: path,
        tableName: this.crtRuleForm.table,
        columns: this.tableDataCopy.map(item => {
          return {
            [item.name]: item.value_type
          };
        }),
        stepname: this.crtRuleForm.stepName,
        databaseName: this.crtRuleForm.connection
      }).then(res => {
        this.$message({
          type: res.data.success ? 'success' : 'error',
          message: res.data.result
        });
      });
    },
    /**
     * 预览流程
     */
    previewTrans() {
      this.flowChartDialog.visible = true;
    },
    /**
     * 关闭弹框
     */
    close() {
      this.dialog.visible = false;
      this.$refs.baseForm && this.$refs.baseForm.resetForm();
      this.$emit('close');
    },
    /**
     * 表格操作
     */
    addTableData(data) {
      this.tableDataCopy.push(data);
    },
    /**
     * 关闭弹框
     */
    modelCancel() {
      this.$emit('close');
    },
    /**
     * 表单校验后会触发的方法
     */
    formValidate(valid) {
      this.$emit('formSubmit', valid, true);
    },
    /**
     * 列表勾选切换
     */
    selectionChange(val) {
      this.$emit('selectionChange', val);
    }
  }
};
</script>

<style lang="scss" scoped>
#monaco {
  height: 300px;
}
.checkCode {
  margin: 5px 0;
}
.mul-table {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  &-inner {
    width: calc(50% - 5px);
    &-title {
      color: #fff;
      font-size: 15px;
      padding: 10px 0;
    }
  }
}
.btndiv {
  margin-bottom: 5px;
}
</style>
