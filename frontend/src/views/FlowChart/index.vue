<template>
  <el-container class="flowChartWrap">
    <el-aside width="224px" class="left">
      <ComponentsTree
        @dragHandle="dragHandle"
        @showPlanData="showPlanData"
        :data="nodeData"
        :procType="procType"
        :bindTableNames="bindTableNames"
      />
    </el-aside>
    <el-main class="main-container" style="padding:0">
      <el-container>
        <el-container>
          <el-main class="main">
            <div id="mainMenu" ref="mainMenu">
              <div class="tool-left">
                <router-link
                  :to="backPage"
                  style="color: #fff;
                  font-size: 24px;
                  line-height: 40px;
                  margin-left: 10px;
                  cursor: pointer;"
                >
                  <i class="el-icon-back" title="返回"></i>
                </router-link>
                <p class="transName">{{transName}}</p>
                <el-button
                  size="mini"
                  icon="el-icon-s-grid"
                  style="margin-left: 10px; background: transparent; color: #fff; border: 1px solid rgba(255,255,255,0.3);"
                  @click="showBindTableDialog"
                >绑定表</el-button>
              </div>
              <div class="tool-right">
                <div class="funcIcon">
                  <div
                    class="item"
                    v-for="(item, index) in funcIcon"
                    :key="'funcIcon' + index"
                    @click="clickIcon(item.function)"
                    :title="item.name"
                  >
                    <img :src="item.imgUrl" alt>
                  </div>
                  <!-- <div class="item">
                    <el-dropdown>
                      <span class="el-dropdown-link">
                        <img src="~@/assets/indexPage/func_zx.png" alt>
                      </span>
                      <el-dropdown-menu slot="dropdown">
                        <el-dropdown-item>
                          <p @click="execModel">执行</p>
                        </el-dropdown-item>
                        <el-dropdown-item>
                          <p @click="execModelOptions">参数执行</p>
                        </el-dropdown-item>
                      </el-dropdown-menu>
                    </el-dropdown>
                  </div>-->
                </div>
              </div>
            </div>
            <div class="mainContainer" @drop="dropHandle" @dragover="dragoverHandle">
              <div
                ref="mainContainer"
                id="mainContainer"
                @click="clickBgHandle"
                @keyup.ctrl.86="pastNode"
              ></div>
            </div>
            <!-- <div class="bottom-container" :style="{ height: showBottom + 'px' }"> -->
            <div
              class="bottom-container"
              :class="showBottom?'showBottom':'hideBottom'"
              v-if="procType !== 'sjyj'"
            >
              <p class="setHeight" @click="setHeight" v-if="showBottom">
                <i class="el-icon-caret-bottom"></i>
              </p>
              <p class="setHeight" @click="setHeight" v-else>
                <i class="el-icon-caret-top"></i>
              </p>
              <Terminal
                :runLog="runLog"
                :stepTableData="stepTableData"
                :batchProgress="batchProgress"
                @cancel-batch="cancelBatch"
              />
            </div>
          </el-main>
          <el-aside class="right" :class="allWidth?'showAttrSide':'hideAttrSide'">
            <Attr
              :currentNodeMsg="currentNodeMsg"
              :projectDesc="projectDesc"
              :toolBarShow="toolBarShow"
              :isShowNode="isShowNode"
              :bindTableNames="bindTableNames"
            />
            <div class="chgWidth">
              <p @click="allWidth = !allWidth">
                <i class="el-icon-caret-right" v-if="allWidth"></i>
                <i class="el-icon-caret-left" v-else></i>
              </p>
            </div>
          </el-aside>
        </el-container>
      </el-container>
    </el-main>

    <base-dialog-form
      v-if="dialog.visible"
      :isShowTab="isShowTab"
      :tabActiveInit="tabActiveInit"
      :tabList="tabList"
      @tabChange="tabChange"
      :formItemConfig="formItemConfig"
      :formItemRuleConfig="formItemRuleConfig"
      :isShowOperation="isShowOperation"
      :ruleForm="ruleForm"
      :dialog="dialog"
      :tableColumnConfig="tableColumnConfig"
      :tableColumnConfigList="tableColumnConfigList"
      :tableData="tableData"
      :tabListData="tabListData"
      :isHasMonaco="isHasMonaco"
      :monacoType="monacoType"
      :isHasPreview="isHasPreview"
      :isHasPreviewOut="isHasPreviewOut"
      :isHasPreviewExcel="isHasPreviewExcel"
      :isFilePreview="isFilePreview"
      :formPreview="formPreview"
      :tabTableVisible="tabTableVisible"
      :isHasTable="isHasTable"
      :isHasSqlConditionTree="isHasSqlConditionTree"
      :isHasDoubleTable="isHasDoubleTable"
      :doubleTableConfig="doubleTableConfig"
      :isHasCreateTableBtn="isHasCreateTableBtn"
      :isHasExec="isHasExec"
      :tabVisible="tabVisible"
      :otherConfig="otherConfig"
      @formSubmit="formSubmit"
      @deleteRow="deleteRow"
      @moveRowPosition="moveRowPosition"
      :handleExtensionVisible="handleExtensionVisible"
      @startExec="startExec"
      @close="resetOptions"
      ref="baseDialog"
    >
      <div slot="slot-form-table-operation" v-if="!isShowBtn && !formPreview">
        <el-button
          size="mini"
          v-if="isHasGetFieldBtn && !isShowRun && isShowNewBtn"
          @click="getField"
        >获取字段</el-button>
        <el-button
          size="mini"
          v-if="isHasGetFieldBtn && !isShowRun && !isShowNewBtn"
          @click="getExcelField"
        >获取Excel字段</el-button>
        <el-button size="mini" v-if="isShowRun" @click="runCode">执行</el-button>
        <el-button size="mini" v-if="isShowNewBtn" @click="addRow">新增行</el-button>
      </div>
      <div slot="secondTable" style="max-height:300px;overflow:auto">
        <base-form
          ref="baseForm"
          v-if="formItemConfigDown&&formItemConfigDown.length"
          :formItemConfig="formItemConfigDown"
          :formItemRuleConfig="formItemRuleConfig"
          :ruleForm="ruleForm"
          :tableData="tableData"
          :formPreview="formPreview"
        />
      </div>
    </base-dialog-form>
    <ProgressEle :isShow="prog.isShow" :percentNum="prog.percent"></ProgressEle>

    <el-dialog title="生成方案" :visible.sync="createModelDialog.visible" width="50%">
      <div class="content">
        <el-form ref="form" :model="createModelDialog.formList" label-width="80px" size="small">
          <el-form-item label="流程名称">
            <el-input v-model="createModelDialog.formList.procName" disabled></el-input>
          </el-form-item>
          <el-form-item label="方案名称">
            <el-input v-model="createModelDialog.formList.planName"></el-input>
          </el-form-item>
          <el-form-item label="方案分组">
            <el-cascader
              v-model="createModelDialog.formList.planGroup"
              :options="createModelDialog.planGroupOptions"
              :props="{ checkStrictly: true, label: 'name', value: 'id' }"
              clearable
            ></el-cascader>
          </el-form-item>
          <el-form-item label="描述">
            <el-input type="textarea" v-model="createModelDialog.formList.proDesc"></el-input>
          </el-form-item>
        </el-form>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="cancelCreateDialog" size="small">取 消</el-button>
        <el-button type="primary" @click="saveCreateDialog" size="small">确 定</el-button>
      </span>
    </el-dialog>

    <!-- 组件配置弹框 -->
    <el-dialog
      :close-on-click-modal="false"
      :title="previewDialog.ruleForm.stepName"
      :visible.sync="previewDialog.visible"
      id="previewDialog"
      top="5vh"
      width="80%"
    >
      <ComponentsPreview
        ref="ComponentsPreview"
        v-if="previewDialog.visible"
        :stepId="previewDialog.stepId"
        :ruleForm="previewDialog.ruleForm"
      />
      <span slot="footer" class="dialog-footer">
        <el-button @click="cancelForm" size="small">取 消</el-button>
        <el-button
          v-if="previewCompList.includes(currentNodeMsg.pluginId)"
          @click="previewTableExcelData"
          size="small"
        >预 览</el-button>
        <!-- <el-button
          v-if="previewDialog.ruleForm.stepName.includes('表输入')"
          @click="previewTable()"
          size="small"
        >预 览</el-button>
        <el-button
          v-if="previewDialog.ruleForm.stepName.includes('表输出')"
          @click="previewTableout"
          size="small"
        >预 览</el-button>-->
        <el-button type="primary" @click="submitForm(false)" size="small">确 定</el-button>
      </span>
    </el-dialog>
    <!-- id="previewDataDialog"
      top="5vh"
    width="80%"-->
    <!-- 数据预览弹框 -->
    <el-dialog
      :close-on-click-modal="false"
      :title="previewTableData.title"
      :visible.sync="previewTableData.visible"
      width="80%"
      id="previewDataDialog"
    >
      <BaseTable :tableData="previewData" :tableColumnConfig="previewDataTableColumnConfig"></BaseTable>
      <BasePagination :data="page" @current="currentPage" @sizes="pageSizes"></BasePagination>

    </el-dialog>
    <!-- 组件配置弹框 -->
    <el-dialog
      :close-on-click-modal="false"
      title="配置"
      :visible.sync="autoDialogVisible"
      top="5vh"
      width="30%"
    >
      <el-form ref="form" :model="newRuleForm" label-width="80px" size="small">
        <el-form-item label="组件名称">
          <el-input v-model="newRuleForm.text"></el-input>
        </el-form-item>
        <el-form-item label="组件编码">
          <el-input v-model="newRuleForm.pluginId"></el-input>
        </el-form-item>
        <el-form-item label="备注">
          <el-input type="textarea" v-model="newRuleForm.remark"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button @click="saveAuto(null)">保存</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>

    <!-- 绑定表编辑弹框 -->
    <el-dialog
      title="绑定目标表"
      :visible.sync="bindTableDialog.visible"
      width="500px"
      append-to-body
    >
      <el-form label-width="80px" size="small">
        <el-form-item label="选择表">
          <el-select
            v-model="bindTableDialog.selectedTables"
            multiple
            filterable
            placeholder="请选择绑定的表（可多选）"
            style="width: 100%"
          >
            <el-option
              v-for="item in bindTableDialog.tableOptions"
              :key="item"
              :label="item"
              :value="item"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="bindTableDialog.visible = false" size="small">取 消</el-button>
        <el-button type="primary" @click="saveBindTables" size="small">确 定</el-button>
      </span>
    </el-dialog>
  </el-container>
</template>
<script lang="ts">
import Vue, { CreateElement } from 'vue';
import API from '@/api/index';
import FlowChart from '@/FlowChart/index';
import editor from '@/FlowChart/editor.js';
import BaseDialogForm from '@/components/BaseDialogForm/index.vue';
import ProgressEle from '@/components/Progress/index.vue';
import BaseTable from '@/components/BaseTable/index.vue';
import exec, { RenameNodeCommand, PasteNodeCommand } from '@/FlowChart/Command.js';
import ComponentsTree from './ComponentsTree.vue';
import Terminal from './Terminal.vue';
import Attr from './Attr.vue';
import { funcIcon, execModelConfig } from './config.js';
import axios from '@/api/base/http';
import BaseForm from '@/components/BaseForm/index.vue';
import BasePagination from '@/components/BasePagination/index.vue';
import ComponentsPreview from '@/views/ComponentsPreview/index.vue';
import domToImage from 'dom-to-image';
let eleList: any = {};
//加载所有ETL组件
function initEleList() {
  const requireAll = (requireContext: any) => requireContext.keys().map(requireContext);
  const req = require.context('@/FlowChart/TaskTypeConfig', true, /\index.js$/);
  const eleMap = requireAll(req);
  eleMap.forEach((item: any) => {
    eleList[item.default.type] = item.default;
  });
}

export default Vue.extend({
  name: 'FlowChart',
  meta: {
    title: '流程设计'
  },
  components: {
    ProgressEle,
    BaseDialogForm,
    BaseTable,
    ComponentsTree,
    Terminal,
    Attr,
    BaseForm,
    ComponentsPreview,
    BasePagination
  },

  data() {
    return {
      newRuleForm: {},
      autoDialogVisible: false,
      previewCompList: ['TableInput', 'TableOutput', 'ExcelOutput0', 'DataView'],
      previewTableData: {
        visible: false,
        title: '数据预览'
      },
      previewDataTableColumnConfig: [],
      previewData: [],
      newForm: {},
      copyNodeData: {},
      activeName: 'first',
      tabTableVisible: false,
      tabVisible: false,
      createModelDialog: {
        visible: false,
        formList: {
          procName: '',
          planName: '',
          planGroup: [],
          descripe: ''
        },
        planGroupOptions: []
      },
      backPage: sessionStorage.getItem('backPage'),
      prog: {
        isShow: false,
        percent: 0
      },
      allWidth: false,
      transName: '',
      funcIcon,
      isShowNode: true,
      currentNodeMsg: {
        id: '',
        text: '',
        pluginId: '',
        icon: '',
        dragIcon: '',
        cls: '',
        qtip: '',
        leaf: true
      },
      isUndoDisable: true,
      isExecDisable: false,
      nodeData: [],
      filterText: '',
      toolBarShow: 'component',
      projectDesc: {
        name: '',
        createDate: ''
      },
      // 节点配置是否含有编辑器框
      isHasMonaco: false,
      monacoType: 'mysql',
      otherConfig: {},
      isHasPreview: false,
      isFilePreview: false,
      isHasPreviewOut: false,
      isHasPreviewExcel: false,
      isHasTable: false,
      isHasSqlConditionTree: false,
      isHasGetFieldBtn: true,
      isShowNewBtn: true,
      isShowNewBtnInputType: true,
      isShowOperation: true,
      isHasDoubleTable: false,
      isHasCreateTableBtn: false,
      doubleTableConfig: null,
      isShowRun: false,
      // 弹框配置
      dialog: {
        title: '',
        visible: false,
        height: '93%'
      },
      formItemRuleConfig: null,
      formItemConfig: null,
      formItemConfigDown: [],
      tableColumnConfig: [],
      tableColumnConfigList: [],
      tabListData: [],
      tableData: [],
      isHasExec: false,
      // 表单
      ruleForm: {
        sql: '',
        scriptName: '',
        stepName: '',
        fileName: '',
        spreadsheet_type: '',
        filefield: '',
        sheetfield: '',
        sheetrownumfield: '',
        rownumfield: '',
        shortFileFieldName: '',
        extensionFieldName: '',
        pathFieldName: '',
        sizeFieldName: '',
        hiddenFieldName: '',
        lastModificationTimeFieldName: '',
        uriNameFieldName: '',
        rootUriNameFieldName: '',
        remove: []
      },
      formPreview: false,
      dialogNodeId: '',
      runLog: '',
      stepTableData: [],
      // 批量执行进度
      batchProgress: {
        taskId: '',
        current: 0,
        total: 0,
        successCount: 0,
        failCount: 0,
        elapsedTime: 0,
        estimatedRemaining: 0,
        totalRows: 0,
        status: 'idle', // idle, running, completed, cancelled, error
        batchDetails: []
      },
      // WebSocket 连接
      websocket: null,
      // 多tab弹框配置
      isShowTab: false,
      tabList: [],
      tabActiveInit: '',
      isShowBtn: false,
      isStartMove: false,
      showBottom: false,
      handleExtensionVisible: false,
      transformationId: '', //当前组件transformationId
      bindTableNames: '', //绑定的目标表名
      bindTableDialog: {
        visible: false,
        tableOptions: [],
        selectedTables: []
      },
      crtNodeData: {
        nodeId: ''
      },
      // 画布预览弹框
      previewDialog: {
        visible: false,
        title: '',
        stepId: '',
        ruleForm: {
          stepName: ''
        }
      },
      procType: '',
      page: {
        pageSize: 20,
        pageNum: 1,
        sizes: [10, 20, 30, 40, 50, 100],
        total: 0
      } //页码的配置
    };
  },
  watch: {
    filterText(val: string) {
      this.$refs.filterTree.filter(val);
    },
    eleList(val: object) {
    }
  },
  mounted() {
    initEleList();
    // 清空之前保留的流程节点
    editor.clearAll();
    if (this.$route.query.procType) {
      this.procType = this.$route.query.procType;
    } else {
      this.procType = '';
    }
    this.$route.query.type ? this.showPlanData(this.$route.query.item) : this.init();
    this.$eventBus.$on('clearTable', this.clearTable);
    this.$eventBus.$on('refreshConfig', this.refreshConfig);
    this.$eventBus.$on('copyNodeData', (data: any) => {
      this.copyNodeData = data;
    });

    this.$eventBus.$on('showExcelBtn', (data: any) => {
      if (this.ruleForm.hasOwnProperty('ExcelInput') && this.ruleForm.ExcelInput) {
        this.isHasGetFieldBtn = data === 'N';
      }
    });
  },
  // beforeRouteLeave() {

  // },
  beforeRouteLeave(to, from, next) {
    this.$confirm('您还有数据未保存, 是否保存?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
      .then(() => {
        this.save();
      })
      .catch(() => {
        this.$message({
          type: 'info',
          message: '已选择不保存。'
        });
      })
      .finally(() => {
        // 取消浏览器窗口关闭监听
        window.onbeforeunload = null;
        // 流程还原为可编辑状态
        this.$API.projectManage.setTransEditable(this.$route.query.transformationId).then(({ data }: any) => {
        });
        next();
      });
  },
  destroyed() {
    this.$eventBus.$off('clearTable');
    this.tableColumnConfig = [];
    this.tableColumnConfigList = [];
    this.ruleForm = {
      sql: '',
      scriptName: '',
      stepName: '',
      fileName: '',
      spreadsheet_type: '',
      filefield: '',
      sheetfield: '',
      sheetrownumfield: '',
      rownumfield: '',
      shortFileFieldName: '',
      extensionFieldName: '',
      pathFieldName: '',
      sizeFieldName: '',
      hiddenFieldName: '',
      lastModificationTimeFieldName: '',
      uriNameFieldName: '',
      rootUriNameFieldName: '',
      remove: []
    };
  },
  methods: {
    currentPage(e: any) {
      this.page.pageNum = e;
      this.previewTableout();
    },
    pageSizes(e: any) {
      this.page.pageSize = e;
      this.previewTableout();
    },
    saveAuto(callback?: any) {
      const loading = this.$loading({
        lock: true,
        text: '保存中...',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      });
      new Promise((resolve, reject) => {
        const imgDom = document.getElementById('mainContainer');
        imgDom &&
          domToImage
            .toPng(imgDom)
            .then(imgUrl => {
              resolve(imgUrl);
            })
            .catch(() => {
              resolve('');
            });
      })
        .then(imgUrl => {
          API.SaveJSONAuto({
            noFresh: callback ? true : false,
            transformationId: this.transformationId,
            imgUrl,
            ...this.newRuleForm
          }).then((data: any) => {
            if (callback) {
              callback();
            } else {
              this.$message({
                type: data.data.success ? 'success' : 'error',
                message: data.data.success ? '保存成功！' : (data.data.message || data.data.msg || '保存失败！')
              });
            }
          });
        })
        .finally(() => {
          loading.close();
          this.autoDialogVisible = false;
        });
    },
    openAutoDialog() {
      this.autoDialogVisible = true;
    },
    submitForm(view?: boolean) {
      let currentNodeMsg = FlowChart.getNodeDataByNodeId(this.$store.state.nodeId);
      // 兼容两种数据格式
      let props = currentNodeMsg.data && currentNodeMsg.data.properties ? currentNodeMsg.data.properties : currentNodeMsg.properties;
      props.ruleForm = this.$utils.deepClone(this.$refs.ComponentsPreview.submit());
      let stepName = props.ruleForm.stepName;
      FlowChart.setData(FlowChart.getData());

      return API.updateStepName({
        nodeId: this.$store.state.nodeId,
        stepName: stepName
      }).then((data: any) => {
        if (data.data.success) {
          exec(RenameNodeCommand, this.$store.state.nodeId, null, stepName);
          if (!view) {
            this.previewDialog.visible = false;
          }
        } else {
          this.$message({
            type: 'warning',
            message: data.data.message
          });
        }
      });
    },
    cancelForm() {
      this.previewDialog.visible = false;
    },
    previewTableExcelData() {
      if (this.currentNodeMsg.pluginId === 'DataView') {
        this.previewDataView();
      } else {
        this.submitForm(true).then(() => {
          switch (this.currentNodeMsg.pluginId) {
            case 'TableInput':
              this.previewTable();
              break;
            case 'TableOutput':
              this.previewTableout();
              break;
            default:
              this.previewExcelOutput();
          }
        });
      }
    },
    // 数据查看组件查看任意节点数据
    previewDataView() {
      API.previewDataView({
        key: `${this.$route.query.transName}_数据查看`
      }).then((res: any) => {
        const result = res.data.data || res.data;
        if (result.columns && result.columns.length) {
          result.columns.forEach((column: any) => {
            column.label = column.lable;
            column.prop = column.lable;
            column.width = 'auto';
          });
          this.previewDataTableColumnConfig = result.columns;
          this.previewData = result.firstRecords;
          this.previewTableData.visible = true;
        } else {
          this.$message.warning('该数据表暂无数据，请重新选择。');
        }
      });
    },
    previewTable() {
      const loading = this.$utils.setLoading('查询中...');
      this.$emit('formSubmit', true, false);
      API.getPreviewData({
        nodeId: this.$store.state.nodeId,
        rowLimit: 1000, //限制请求数据量，作废，数据预览做分页展示 --Leo 20220903
        pageSize: this.page.pageSize,
        pageIndex: this.page.pageNum
      })
        .then((res: any) => {
          let data = res.data;
          if (data.columnsList && data.columnsList.length) {
            data.columnsList.forEach((column: any) => {
              column.label = column.comments || column.columnName;
              column.prop = column.columnName;
              column.width = 'auto';
            });
            this.previewDataTableColumnConfig = data.columnsList;
            this.previewData = data.firstRecords;
            this.page.total = data.total;
            this.previewTableData.visible = true;
          } else {
            this.$message.warning('该数据表暂无数据，请重新选择。');
          }
        })
        .finally(() => {
          loading.close();
        });
    },
    previewTableout() {
      const loading = this.$utils.setLoading('查询中...');
      this.$emit('formSubmit', true, false);
      API.getPreviewDataOut({
        nodeId: this.$store.state.nodeId,
        // rowLimit: 1000,  //限制请求数据量，作废，数据预览做分页展示 --Leo 20220903
        pageSize: this.page.pageSize,
        pageIndex: this.page.pageNum
      })
        .then((res: any) => {
          let data = res.data;
          if (data.columnsList && data.columnsList.length) {
            data.columns = [];
            data.columnsList.forEach((column: any) => {
              // data.columns.push({
              //   label: column,
              //   prop: column,
              //   width: 'auto'
              // });
              column.label = column.comments || column.columnName;
              column.prop = column.columnName;
              column.width = 'auto';
            });
            this.previewDataTableColumnConfig = data.columnsList;
            this.previewData = data.firstRecords;
            this.page.total = data.total;
            this.previewTableData.visible = true;
          } else {
            this.$message.warning('该数据表暂无数据，请重新选择。');
          }
        })
        .finally(() => {
          loading.close();
        });
    },
    previewExcelOutput() {
      const loading = this.$utils.setLoading('查询中...');
      this.$emit('formSubmit', true, false);
      API
        .getExcelDataOut({
          nodeId: this.$store.state.nodeId,
          rowLimit: 1000,
          path: this.previewDialog.ruleForm.fileName,
          fileName: this.previewDialog.ruleForm.fileName,
          // index: this.ruleForm.sheetName,
          spreadsheet_type: 'Excel 2007 XLSX (Apache POI)'
        })
        .then((res: any) => {
          let data = res.data;
          if (data.columnsList && data.columnsList.length) {
          } else {
            this.$message.warning('该数据表暂无数据，请重新选择。');
          }
        })
        .finally(() => {
          loading.close();
        });
    },
    pastNode() {
      let dom: any = document.getElementById('mainContainer');
      let width: string = window.getComputedStyle(dom, null).width || '';
      let height: string = window.getComputedStyle(dom, null).height || '';
      exec(
        PasteNodeCommand,
        {
          pageX: this.copyNodeData.position.left + 268,
          pageY: this.copyNodeData.position.top + 180
        },
        this.copyNodeData
      );
    },
    cancelCreateDialog() {
      this.createModelDialog.visible = false;
    },
    saveCreateDialog() {
      API.getCreatePlan({
        path: this.$route.query.path,
        transName: this.$route.query.transName,
        taskGroupName: this.$route.query.taskGroupName,
        idTransformation: this.$route.query.transformationId,
        ...this.createModelDialog.formList
      }).then((data: any) => {
        this.$message({
          type: data.data.success ? 'success' : 'error',
          message: data.data.result
        });
      });
      this.cancelCreateDialog();
    },
    getPlanGroupList() {
      axios.get('/api/trans/plan/categroy/tree').then((res: any) => {
        this.createModelDialog.planGroupOptions = res.data.data;
      });
    },
    clearTable() {
      this.tableData = [];
    },
    setHeight() {
      this.showBottom = !this.showBottom;
    },
    clickIcon(f: String) {
      switch (f) {
        case 'zoomIn':
          this.zoomIn();
          return;
        case 'zoomOut':
          this.zoomOut();
          return;
        case 'save':
          this.save();
          return;
        case 'openAutoDialog':
          this.openAutoDialog();
          return;
        case 'execModel':
          this.execModel();
          return;
        case 'execModelOptions':
          this.execModelOptions();
          return;
        case 'createModel':
          this.createModel();
          return;
      }
    },
    init() {
      let path = typeof this.$route.query.path === 'string' ? this.$route.query.path : '';
      sessionStorage.setItem('path', path);
      this.getData(path.split('/')[1]);
      // sessionStorage.setItem('transformationId', this.$route.query.transformationId as any);
      this.$store.dispatch('setTransId', this.$route.query.transformationId);
      this.transformationId = this.$route.query.transformationId;
      this.transName = this.$route.query.transName;
      FlowChart.setContainer('mainContainer');
      FlowChart.on('commandListEmpty', () => {
        this.isUndoDisable = true;
      });
      FlowChart.on('showNodeData', (nodeData: any) => {
        new Promise((resovle: any) => {
          this.crtNodeData = nodeData;
          this.$store.dispatch('setNodeId', nodeData.nodeId);
          resovle();
        }).then(() => {
          this.generateNode(nodeData);
        });
      });
      FlowChart.on('addCommand', () => {
        this.isUndoDisable = false;
      });
      FlowChart.on('selectNode', (nodeData: any) => {
        this.crtNodeData = nodeData;
        this.dialogNodeId = nodeData.nodeId;
        this.$store.dispatch('setNodeId', nodeData.nodeId);
        this.$store.dispatch('setNodeData', nodeData);
        this.isShowNode = true;
        if (nodeData.hasOwnProperty('data')) {
          this.currentNodeMsg = nodeData.data;
        } else {
          this.currentNodeMsg = nodeData;
        }
        // 选中目标表输出节点时自动展开右侧属性面板
        if (this.currentNodeMsg.pluginId === 'BoundTableOutput') {
          this.allWidth = true;
        }
      });
      // debugger
      this.$store.dispatch('setPath', this.$route.query.path);
      API.getFlowChartData({
        type: 'transformation',
        path: this.$route.query.path
      }).then((data: any) => {
        let content = data.data && data.data.data && data.data.data.content ? data.data.data.content : (data.data && data.data.content ? data.data.content : null);
        if (content) {
          // 加载绑定表名
          if (content.bindTableNames) {
            this.bindTableNames = content.bindTableNames;
          }
          // -start 处理后端返回的数据中tabList为字符串，数据在画布中加载报错的问题  --20210514
          if (content.nodes && Array.isArray(content.nodes)) {
            content.nodes.forEach((elem: any) => {
              if (elem.properties && elem.properties.tabList === '') {
                elem.properties.tabList = [];
              }
            });
          }
          // -end

          FlowChart.loadData(content);
        }
      });
      API.getMenuData({ type: 1 }).then((data: any) => {
        let content = data.data && data.data.data && data.data.data.content ? data.data.data.content : (data.data && data.data.content ? data.data.content : null);
        if (content && Array.isArray(content)) {
          if (this.procType) {
            // 数据引接流程配置菜单
            let sjyjList = ['开始', '输入', '输出', '转换', '流程', '引接'];
            this.nodeData = content.filter((item: any) => sjyjList.includes(item.text));
          } else {
            // 数据处理流程配置菜单
            this.nodeData = content.filter((item: any) => item.text !== '引接');
          }
          console.log('nodeData:', this.nodeData);
        }
      });
    },
    showPlanData(item: any) {
      sessionStorage.setItem('path', '/' + item.name);
      this.getData(item.name);
      // sessionStorage.setItem('transformationId', item.id as any);
      this.$store.dispatch('setTransId', item.id);
      this.transformationId = item.id;
      this.transName = item.name;
      FlowChart.setContainer('mainContainer');
      // FlowChart.clearContainer("mainContainer");
      FlowChart.on('commandListEmpty', () => {
        this.isUndoDisable = true;
      });
      FlowChart.on('showNodeData', (nodeData: any) => {
        this.$store.dispatch('setNodeId', nodeData.nodeId);

        this.generateNode(nodeData);
      });
      FlowChart.on('addCommand', () => {
        this.isUndoDisable = false;
      });
      FlowChart.on('selectNode', (nodeData: any) => {
        this.dialogNodeId = nodeData.nodeId;
        this.isShowNode = true;
        if (nodeData.hasOwnProperty('data')) {
          this.currentNodeMsg = nodeData.data;
        } else {
          this.currentNodeMsg = nodeData;
        }
        // 选中目标表输出节点时自动展开右侧属性面板
        if (this.currentNodeMsg.pluginId === 'BoundTableOutput') {
          this.allWidth = true;
        }
      });
      API.getPlanData({
        id: item.id
      }).then((data: any) => {
        FlowChart.loadData(data.data.data);
      });
    },
    getData(name: String) {
      API.getTrans({
        start: 0,
        limit: 15,
        name: name,
        date: ''
      }).then((res: any) => {
        if (res.data && res.data.root && res.data.root.length) {
          this.projectDesc = res.data.root[0];
        }
        if (res.data && res.data.dataBase && res.data.dataBase.length) {
          sessionStorage.setItem('crtDatabase', res.data.dataBase[0].name);
        }
      });
    },
    // 初始化节点配置
    generateNode(nodeData: any) {
      if (nodeData.hasOwnProperty('data')) {
        this.previewDialog.stepId = nodeData.data.id;
        this.previewDialog.title = nodeData.data.text;
      } else {
        this.previewDialog.stepId = nodeData.id;
        this.previewDialog.title = nodeData.text;
      }
      this.previewDialog.visible = true;
      this.$nextTick(() => {
        let currentNodeMsg = FlowChart.getNodeDataByNodeId(nodeData.nodeId);
        this.$store.dispatch('setNodeId', nodeData.nodeId);
        let ruleForm = null;
        if (currentNodeMsg) {
          if (currentNodeMsg.data && currentNodeMsg.data.properties && currentNodeMsg.data.properties.ruleForm) {
            ruleForm = currentNodeMsg.data.properties.ruleForm;
          } else if (currentNodeMsg.properties && currentNodeMsg.properties.ruleForm) {
            ruleForm = currentNodeMsg.properties.ruleForm;
          }
        }
        if (ruleForm) {
          this.previewDialog.ruleForm = ruleForm;
        } else {
          this.previewDialog.ruleForm = { stepName: this.previewDialog.title };
        }
      });
    },
    /**
     * @description 初始化tabList内容
     * @param text 节点名称
     */
    generateTabList(text: string) {
      if (this.tabList && this.tabList.length) {
        this.tabList.forEach((tab: any) => {
          tab.ruleForm.stepName = text;
        });
      }
    },
    refreshConfig(key: string, config: any) {
      this[key] = config;
    },
    resetOptions() {
      this.isHasExec = false;
    },
    tabChange(tab: string) {
      if (this.tabList && this.tabList.length) {
        this.tabList.forEach((item: any) => {
          if (item.name === tab) {
            this.formItemConfig = item.formItemConfig;
            this.formItemConfigDown = item.formItemConfigDown;
            this.formItemRuleConfig = item.formItemRuleConfig;
            this.ruleForm = item.ruleForm;
            this.tableColumnConfig = item.tableColumnConfig;
            this.tabListData = item.tableData;
            this.tableData = item.tableData;
            this.isHasTable = item.isHasTable;
            this.formPreview = item.formPreview;
            this.isShowBtn = item.isShowBtn;
            this.isHasGetFieldBtn = item.isHasGetFieldBtn;
          }
        });
      }
    },
    getExcelField() {
      this.tableData = [];
      let _this = this;
      console.log(124);
      let params = {
        nodeId: this.$store.state.nodeId,
        fileName: this.ruleForm.filefield,
        before: true,
        index: 0,
        spreadsheet_type: _this.ruleForm.spreadsheet_type
      };
      API.getExcelFields(params).then((data: any) => {
        let tableData: any = [];
        if (data.data || data.data.content) {
          data.data.forEach((dataItem: any) => {
            let addRowData: any = {};
            if (this.tableColumnConfig) {
              this.tableColumnConfig.forEach((item: any) => {
                // 每个控件中获取字段的对应key
                // if (
                //   [
                //     "in_stream_name",
                //     "name",
                //     "field_name",
                //     "tableChartsName"
                //   ].includes(item.prop)
                // ) {
                // addRowData[item.prop] = dataItem.name;
                addRowData[item.prop] = dataItem[item.prop];
                // } else {
                //   addRowData[item.prop] = item.hasOwnProperty("default")
                //     ? item.default
                //     : "";
                // }
              });
            }
            tableData.push(addRowData);
          });
        }
        // this.tableColumnConfig = this.tableColumnConfig;
        this.tableData = tableData;
      });
    },
    addRow() {
      if (this.tableColumnConfigList && this.tableColumnConfigList.length) {
        this.tableColumnConfigList.forEach((config: any) => {
          this.ruleForm[config.key].push(this.initRowData(config.tableColumnConfig));
        });
      } else {
        // 单个表格
        this.tableData.push(this.initRowData(this.tableColumnConfig));
      }
    },
    getField() {
      const loading = this.$utils.setLoading('获取中...');

      let params = {
        nodeId: this.crtNodeData.nodeId,
        before: true
      };
      // console.log(this.currentNodeMsg);
      // 获取字段前清除所有已有数据
      if (this.tableColumnConfigList && this.tableColumnConfigList.length) {
        this.tableColumnConfigList.forEach((config: any) => {
          this.ruleForm[config.key] = [];
        });
      }
      // 此处将tableData设置为[]组件将出错，无法保存该项表格的数据，因为改变了tableData的引用地址，断开了与组件配置项的联系。  --20210517
      // this.tableData = [];
      if (this.ruleForm.remove) {
        this.ruleForm.remove = [];
      }

      let interfaceName =
        this.otherConfig && this.otherConfig.hasOwnProperty('getFieldsInterface') ? this.otherConfig.getFieldsInterface : 'getInputOutputFields';
      switch (interfaceName) {
        case 'getExcelFields':
          params.index = this.ruleForm.sheetName;
          params.fileName = this.ruleForm.filefield;
          params.spreadsheet_type = this.ruleForm.spreadsheet_type;
          break;
        case 'getTextFileFields': {
          // 文件选择保存在"文件"标签页表格数据里（alias: file），ruleForm.fileName 为空
          // 文件选择保存在"文件"标签页表格数据里（alias: file），ruleForm.fileName 为空；
          // treeSelect 的值是数组，需取第一个元素，否则 transferToFormData 会 JSON.stringify 成 ["..."]
          const fileRow = (this.ruleForm.file || [])[0] || {};
          const rawPath = fileRow.fileName;
          const filePath = (Array.isArray(rawPath) ? rawPath[0] : rawPath) || this.ruleForm['inputFiles.fileName'] || this.ruleForm.filefield;
          if (filePath) params.fileName = filePath;
          params['content.separator'] = this.ruleForm['content.separator'] || this.ruleForm.separator;
          params['content.header'] = this.ruleForm['content.header'] || (this.ruleForm.header ? 'Y' : 'N');
          params['content.nrHeaderLines'] = this.ruleForm['content.nrHeaderLines'] || this.ruleForm.nrHeaderLines;
          params['content.encoding'] = this.ruleForm['content.encoding'] || this.ruleForm.encoding;
          break;
        }
        default:
          break;
      }
      API
        [interfaceName](params)
        .then((data: any) => {
          // let tableData: any = [];
          if (data.data || data.data.content) {
            data.data.forEach((dataItem: any) => {
              // 多个表格
              if (this.tableColumnConfigList && this.tableColumnConfigList.length) {
                this.tableColumnConfigList.forEach((config: any) => {
                  this.ruleForm[config.key].push(this.initRowData(config.tableColumnConfig, dataItem));
                });
              } else {
                // 单个表格

                this.tableData.push(this.initRowData(this.tableColumnConfig, dataItem));
              }
            });
          }
          // this.tableColumnConfig = this.tableColumnConfig;
          // this.tableData = tableData;
        })
        .finally(() => {
          loading.close();
        });
    },
    initRowData(tableColumnConfig: any, data?: any, index?: number) {
      let addRowData: any = {};
      tableColumnConfig.forEach((item: any) => {
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
    runCode() {
      this.ruleForm.sql = this.$refs.baseDialog.monacoInstance.getValue();
      this.ruleForm.scriptName = 'Script 1';
      this.tableData = [];
      let params = {
        nodeId: this.$store.state.nodeId,
        before: false
        // scriptName: 'Script 1',
        // stepName: 'JavaScript代码'
      };
      API.getJavaScriptCodeFields(params).then((data: any) => {
        let tableData: any = [];
        if (data.data.data || data.data.content) {
          data.data.data.forEach((dataItem: any) => {
            let addRowData: any = {};
            if (this.tableColumnConfig) {
              this.tableColumnConfig.forEach((item: any) => {
                // 每个控件中获取字段的对应key
                // if (
                //   [
                //     "in_stream_name",
                //     "name",
                //     "field_name",
                //     "tableChartsName"
                //   ].includes(item.prop)
                // ) {
                addRowData[item.prop] = dataItem[item.prop];
                // } else {
                //   addRowData[item.prop] = item.hasOwnProperty("default")
                //     ? item.default
                //     : "";
                // }
              });
            }
            tableData.push(addRowData);
          });
        }
        // this.tableColumnConfig = this.tableColumnConfig;
        this.tableData = tableData;
        this.$eventBus.$emit('setTableData', tableData);
      });
    },
    save(callback?: any) {
      const loading = this.$loading({
        lock: true,
        text: '保存中...',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      });
      new Promise((resolve) => {
        const imgDom = document.getElementById('mainContainer');
        if (imgDom) {
          domToImage
            .toPng(imgDom)
            .then(imgUrl => {
              resolve(imgUrl);
            })
            .catch(() => {
              resolve('');
            });
        } else {
          resolve('');
        }
      })
        .then(imgUrl => {
          API.SaveJSON({
            noFresh: callback ? true : false,
            transformationId: this.transformationId,
            imgUrl
          }).then((data: any) => {
            if (callback) {
              callback();
            } else {
              this.$message({
                type: data.data.success ? 'success' : 'error',
                message: data.data.success ? '保存成功！' : (data.data.message || data.data.msg || '保存失败！')
              });
            }
          });
        })
        .finally(() => {
          loading.close();
        });
    },
    formSubmit(vaild: any, isClose: boolean) {
      if (vaild) {
        let nowFlowChart = FlowChart.getData();
        nowFlowChart.nodes.forEach((item: any) => {
          if (item.id === this.$store.state.nodeId) {
            // 兼容两种数据格式
            let props = item.data && item.data.properties ? item.data.properties : item.properties;
            props.ruleForm = this.$utils.deepClone(this.ruleForm);
            props.tableData = this.$utils.deepClone(this.tableData);
            props.tabList = this.tabList;
            if (item.data) item.data.text = this.ruleForm.stepName;
            if (this.tabList && this.tabList.length) {
              this.tabList.forEach((ele: any) => {
                if (ele.hasOwnProperty('alias') && ele.alias) {
                  props.ruleForm[ele.alias] = this.$utils.deepClone(ele.tableData);
                }
                // ruleForm最后合并
                Object.assign(item.properties.ruleForm, this.$utils.deepClone(ele.ruleForm));
              });
            }
          }
        });
        FlowChart.setData(nowFlowChart);
        API.updateStepName({
          nodeId: this.$store.state.nodeId,
          stepName: this.ruleForm.stepName
        }).then((data: any) => {
          if (data.data.success) {
            exec(RenameNodeCommand, this.$store.state.nodeId, null, this.ruleForm.stepName);

            if (isClose) {
              this.dialog.visible = false;
            }
          } else {
            this.$message({
              type: 'warning',
              message: data.data.message || data.data.msg || '更新失败'
            });
          }
        });
      }
    },
    deleteRow(index: number, arr?: []) {
      if (this.tableColumnConfigList && arr) {
        arr.splice(index, 1);
      } else if (this.isShowTab) {
        this.tabListData.splice(index, 1);
      } else {
        const [{ name }] = this.tableData.splice(index, 1);
        if (this.ruleForm.remove && this.ruleForm.remove.indexOf(name) === -1) {
          this.ruleForm.remove.push(name);
        }
      }
    },
    moveRowPosition(index: number, isUp: boolean, obj?: { [attr: string]: any }, key?: string) {
      let temporary = null;
      if (!obj || !key) {
        return;
      }
      let tableData = obj[key] ? obj[key] : JSON.parse(JSON.stringify(this.tableData));

      if (isUp) {
        //上移逻辑
        if (!index) {
          this.$message.info('该行已无法上移！');
          return;
        }

        temporary = JSON.parse(JSON.stringify(tableData[index - 1]));
        tableData[index - 1] = JSON.parse(JSON.stringify(tableData[index]));
        tableData[index] = temporary;
      } else {
        if (index === tableData.length - 1) {
          this.$message.info('该行已无法下移！');
          return;
        }
        //下移逻辑
        temporary = JSON.parse(JSON.stringify(tableData[index + 1]));
        tableData[index + 1] = JSON.parse(JSON.stringify(tableData[index]));
        tableData[index] = temporary;
      }
      if (obj[key]) {
        obj[key] = [];
        this.$nextTick(() => {
          obj[key] = tableData;
        });
      } else {
        this.tableData = tableData;
      }
    },
    filterNode(value: string, data: any) {
      if (!value) return true;
      return data.text.indexOf(value) !== -1;
    },
    dragoverHandle(ev: Event) {
      ev.preventDefault();
    },
    dragHandle(ev: any, data: any) {
      ev.dataTransfer.setData('target', JSON.stringify(data));
    },
    dropHandle(ev: any) {
      let data = ev.dataTransfer.getData('target');
      if (data) FlowChart.addNode({ pageX: ev.pageX, pageY: ev.pageY }, JSON.parse(data));
    },
    clickBgHandle() {
      this.isShowNode = false;
      document.getElementById('mainContainer').setAttribute('tabindex', '0');
      document.getElementById('mainContainer').focus();
    },
    zoomOut() {
      FlowChart.zoomOut();
    },
    zoomIn() {
      FlowChart.zoomIn();
    },
    undo() {
      FlowChart.undo();
    },
    getLog(params: any) {
      API.execRunResult(params).then((data: any) => {
        const result = data.data.data || data.data;
        this.runLog = result.log || '';
        this.stepTableData = result.stepMeasure || [];
        if (!this.showBottom) {
          this.setHeight();
        }
        if (result.stepStatus && Array.isArray(result.stepStatus)) {
          result.stepStatus.forEach((dataItem: any) => {
            let nowFlowChart = FlowChart.getData();
            nowFlowChart.nodes.forEach((item: any) => {
              if (dataItem.stepName === item.data.text) {
                editor.execModel(item.id, this.getNodeState(dataItem.stepStatus));
              }
            });
          });
        }
      });
    },
    getNodeState(status: any) {
      if (status === 0 || status === 'success') {
        return 'success';
      } else {
        return 'failed';
      }
    },
    // 启动
    execModel() {
      this.save(() => {
        editor.blingConnectors('add');
        // const loading = this.$loading({
        //   lock: true,
        //   text: '执行中...',
        //   spinner: 'el-icon-loading',
        //   background: 'rgba(0, 0, 0, 0.7)'
        // });
        this.prog.isShow = true;

        // 清空日志
        this.runLog = '';
        this.stepTableData = [];

        API.execModel({ noFresh: true })
          .then((data: any) => {
            if (data.data.success) {
              this.getLog({ transformationId: this.transformationId });
            }
            editor.blingConnectors('remove');
            // loading.close();
            this.prog.percent = 100;
            return data;
          })
          .then((data: any) => {
            this.prog.isShow = false;
            this.$message({
              type: data.data.success ? 'success' : 'error',
              message: data.data.success ? '执行完成' : (data.data.message || data.data.msg || '执行失败')
            });
          });
      });

      // this.prog.isShow = false;
    },
    execModelOptions() {
      // 从流程数据中提取 ${xxx} 变量，生成动态列
      const flowData = FlowChart.getData();
      const vars: string[] = [];
      const varsSet = new Set();
      const jsonStr = JSON.stringify(flowData);
      const regex = /\$\{([^}]+)}/g;
      let match;
      while ((match = regex.exec(jsonStr)) !== null) {
        if (!varsSet.has(match[1])) {
          varsSet.add(match[1]);
          vars.push(match[1]);
        }
      }
      if (vars.length === 0) {
        this.$message.warning('当前流程没有参数变量（${xxx}），请先在算子配置中使用参数');
        return;
      }
      // 动态生成表格列，每列对应一个变量
      const columns: any = vars.map((v: string) => ({
        label: v,
        prop: v,
        showOverflowTooltip: true,
        align: 'left',
        headerAlign: 'left',
        isEdit: true,
        editConfig: { type: 'input' }
      }));
      this.dialog = execModelConfig.dialog;
      this.dialog.title = '参数执行（可添加多行批量执行）';
      this.formItemConfig = execModelConfig.formItemConfig;
      this.tableColumnConfig = columns;
      this.tableData = [];
      // 检查是否有从仿真方案管理预填的批量参数
      const preseed = sessionStorage.getItem('batchParams');
      if (preseed) {
        try {
          const rows = JSON.parse(preseed);
          // 只填充当前流程中存在的变量
          rows.forEach((row: any) => {
            const filtered: any = {};
            vars.forEach((v: string) => {
              if (row.hasOwnProperty(v)) filtered[v] = row[v];
            });
            if (Object.keys(filtered).length > 0) this.tableData.push(filtered);
          });
        } catch (e) { /* ignore */ }
        sessionStorage.removeItem('batchParams');
      }
      this.isHasTable = true;
      this.isHasExec = true;
      this.dialog.visible = true;
    },
    startExec() {
      const rows = this.tableData;
      if (!rows || rows.length === 0) {
        this.$message.warning('请添加至少一行参数');
        return;
      }
      this.dialog.visible = false;
      this.save(() => {
        // 重置批量进度
        this.batchProgress = {
          taskId: '',
          current: 0,
          total: rows.length,
          successCount: 0,
          failCount: 0,
          elapsedTime: 0,
          estimatedRemaining: 0,
          totalRows: 0,
          status: 'running',
          batchDetails: []
        };

        // 清空日志
        this.runLog = '';
        this.stepTableData = [];

        API.execBatchRun(Number(this.transformationId), rows)
          .then((response: any) => {
            console.log('[batchRun] 返回数据:', response);
            // axios 响应拦截器返回的是完整的 response 对象
            // 数据结构: response.data.data.taskId
            const res = response.data?.data || response.data;
            console.log('[batchRun] res:', res);
            if (res && res.taskId) {
              // 建立 WebSocket 连接
              this.connectWebSocket(res.taskId);
              this.$message.info('批量任务已启动，正在接收进度...');
            } else {
              console.error('[batchRun] 未获取到taskId, res:', res);
              this.$message.error('未获取到任务ID');
            }
          })
          .catch((err: any) => {
            this.$message.error('批量执行请求失败：' + (err.message || '未知错误'));
            this.batchProgress.status = 'error';
          });
      });
    },
    connectWebSocket(taskId: string) {
      // 关闭现有连接
      if (this.websocket) {
        this.websocket.close();
      }

      // 建立新连接
      // 使用相对路径，让 devServer 代理处理 WebSocket
      const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
      const wsUrl = `${protocol}//${window.location.host}/ws/batchProgress/${taskId}`;
      this.websocket = new WebSocket(wsUrl);

      this.websocket.onopen = () => {
        console.log('[WebSocket] 连接建立:', taskId);
      };

      this.websocket.onmessage = (event) => {
        try {
          const message = JSON.parse(event.data);
          this.updateBatchProgress(message);
        } catch (e) {
          console.error('[WebSocket] 解析消息失败:', e);
        }
      };

      this.websocket.onclose = () => {
        console.log('[WebSocket] 连接关闭:', taskId);
      };

      this.websocket.onerror = (error) => {
        console.error('[WebSocket] 连接错误:', error);
        this.$message.error('WebSocket 连接失败，请刷新页面重试');
      };
    },
    updateBatchProgress(message: any) {
      this.batchProgress = {
        taskId: message.taskId,
        current: message.current,
        total: message.total,
        successCount: message.successCount,
        failCount: message.failCount,
        elapsedTime: message.elapsedTime,
        estimatedRemaining: message.estimatedRemaining,
        totalRows: message.totalRows,
        status: message.status,
        batchDetails: message.batchDetails || []
      };

      // 执行完成或取消时关闭 WebSocket
      if (message.status === 'completed' || message.status === 'cancelled' || message.status === 'error') {
        if (this.websocket) {
          this.websocket.close();
          this.websocket = null;
        }

        // 显示消息
        if (message.status === 'completed') {
          this.$message.success(`批量执行完成：成功 ${message.successCount}，失败 ${message.failCount}，共 ${message.total} 批`);
        } else if (message.status === 'cancelled') {
          this.$message.warning('批量执行已取消');
        }

        // 刷新日志
        this.getLog({ transformationId: this.transformationId });
      }
    },
    cancelBatch(taskId: string) {
      if (!taskId) return;

      API.cancelBatch(taskId)
        .then(() => {
          this.$message.info('取消请求已发送');
        })
        .catch((err: any) => {
          this.$message.error('取消请求失败：' + (err.message || '未知错误'));
        });
    },
    createModel() {
      this.createModelDialog.formList.procName = this.transName;
      this.getPlanGroupList();
      this.createModelDialog.visible = true;
    },
    showBindTableDialog() {
      this.bindTableDialog.selectedTables = this.bindTableNames ? this.bindTableNames.split(',').filter(Boolean) : [];
      API.getBindTables().then(res => {
        const data = res.data.data || res.data;
        if (Array.isArray(data)) {
          this.bindTableDialog.tableOptions = data;
        }
      });
      this.bindTableDialog.visible = true;
    },
    saveBindTables() {
      const bindTableNames = this.bindTableDialog.selectedTables.join(',');
      API.updateBindTables({
        transformationId: Number(this.transformationId),
        bindTableNames: bindTableNames
      }).then(res => {
        if (res.data.success || res.data.code === 200) {
          this.bindTableNames = bindTableNames;
          this.bindTableDialog.visible = false;
          this.$message.success('绑定表更新成功');
        }
      });
    }
  }
});
</script>

<style lang="scss" scoped>
@import './src/styles/theme.scss';
// .main-container{
//  width: 80%;
//  height: 20%;
// }
.bottom-container ::v-deep .el-tabs--border-card > .el-tabs__header .el-tabs__item.is-active {
  border: none;
  color: #409eff;
  background-color: $contBg;
}
::v-deep .el-tabs--border-card > .el-tabs__header .el-tabs__item {
  color: #606266;
}
::v-deep .el-tabs--border-card > .el-tabs__header .el-tabs__item:hover {
  color: #409eff;
}
.flowChartWrap {
  height: 100%;
  .left {
    border-right: 1px solid $currentRow;
    height: 100%;
    .el-tree {
      height: calc(100% - 40px);
      overflow-y: auto;
    }
  }
  .right {
    border-left: 1px solid $currentRow;
    position: relative;
    overflow: hidden;
    .chgWidth {
      position: absolute;
      top: calc(50% - 30px);
      left: -15px;
      // border: 1px solid #ccc;
      width: 14px;
      height: 60px;
      color: #fff;
      line-height: 60px;
      display: flex;
      justify-content: center;
      align-items: center;
      cursor: pointer;
      background: $tableOddBgClr;
      p {
        height: 100%;
        display: flex;
        justify-content: center;
        align-items: center;
      }
    }
  }
  .main {
    position: relative;
    padding: 0;
    overflow: hidden;
    #mainMenu {
      height: 40px;
      // border-bottom: 1px solid #435b44;
      background: $centerTtlBgClr;
      .tool-left {
        float: left;
        display: flex;
        color: #fff;
        align-items: center;
        .el-button {
          background: transparent;
          &:first-child {
            margin-left: 10px;
          }
          border: none;
          margin-top: 8px;
        }
        .transName {
          margin-left: 10px;
          font-size: 14px;
        }
      }
      .tool-right {
        float: right;
        height: 100%;
        .funcIcon {
          display: flex;
          align-items: center;
          height: 100%;
          .item {
            margin: 0 15px;
            cursor: pointer;
            font-size: 18px;
          }
          .dealItem {
            margin: 0 15px;
            cursor: pointer;
            display: flex;
            align-items: center;
            font-size: 14px;
            color: #fff;
            img {
              margin-right: 5px;
            }
          }
        }
        .el-button {
          position: relative;
          border: none;
          margin: 3px 0 0 0;
          color: #222;
          &:last-child {
            margin-right: 10px;
          }
          background: transparent;
        }
      }
    }
    .mainContainer {
      height: calc(100% - 42px);
      position: relative;
      overflow: hidden;
      outline: none !important;
      background: $flowBgClr;
      #mainContainer {
        outline: none !important;
        height: 100%;
        width: 100%;
        position: relative;
      }
    }
    .bottom-container {
      height: 300px;
      position: absolute;
      bottom: -258px;
      left: 0;
      width: 100%;
      .setHeight {
        position: absolute;
        top: 0;
        right: 10px;
        width: 40px;
        height: 40px;
        line-height: 40px;
        text-align: center;
        color: #fff;
        z-index: 10;
        cursor: pointer;
      }
    }
  }
  .el-aside {
    overflow: initial;
    // background: #f8f8f8;
    .search {
      height: 40px;
      background-color: #f3f3f3;
      padding: 8px 12px;
      box-sizing: border-box;
      border-bottom: 1px solid #e5e5e5;
      .el-input {
        height: 24px;
        .el-input__inner {
          height: 24px;
        }
      }
      .el-input--small .el-input__icon {
        line-height: 24px;
      }
    }
    .el-tree {
      background: transparent;
    }
  }
  .el-container {
    height: 100%;
  }
  .tabsNav {
    padding: 0;
    .el-tabs--card > .el-tabs__header .el-tabs__nav {
      border-top: 3px solid #01c1de;
      border-radius: 0;
    }
    .el-tabs__item.is-active {
      color: #333 !important;
    }
    .el-tabs__item {
      font-size: 12px;
    }
    .el-tabs__item:focus.is-active.is-focus:not(:active) {
      box-shadow: none !important;
    }
  }
}

@keyframes hideBottom {
  from {
    bottom: 0;
  }
  to {
    bottom: -258px;
  }
}
@keyframes showBottom {
  from {
    bottom: -258px;
  }
  to {
    bottom: 0;
  }
}
.hideBottom {
  animation: hideBottom 0.8s;
  bottom: -258px;
  overflow: hidden;
}
.showBottom {
  animation: showBottom 0.8s;
  bottom: 0 !important;
}
@keyframes hideAttrSide {
  from {
    width: 300px;
  }
  to {
    width: 2px;
  }
}
@keyframes showAttrSide {
  from {
    width: 2px;
  }
  to {
    width: 300px;
  }
}
.hideAttrSide {
  animation: hideAttrSide 0.8s;
  width: 2px !important;
}
.showAttrSide {
  animation: showAttrSide 0.8s;
  width: 300px !important;
}
::v-deep.dialogModel1 .el-tabs__content {
  display: none;
}
::v-deep.dialogModel1 .el-input__inner,
.dialogModel1 .el-textarea__inner {
  border: 1px solid #435b44 !important;
}
#previewDialog {
  ::v-deep .el-dialog {
    height: calc(100% - 11vh);
    .el-dialog__body {
      height: calc(100% - 107px);
      overflow: hidden;
      overflow-y: auto;
    }
  }
}
#previewDataDialog {
  ::v-deep .el-dialog {
    margin-top: 10vh !important;
    height: calc(100% - 20vh);
    .el-dialog__body {
      height: calc(100% - 45px);
      overflow: hidden;
      overflow-y: auto;
    }
    .el-dialog__header {
      .el-dialog__title {
        color: #fff;
      }
      .el-dialog__headerbtn .el-dialog__close {
        color: #fff;
      }
    }
  }
}
/* 算子弹窗标题栏样式 - 深蓝渐变 */
#previewDialog {
  ::v-deep .el-dialog {
    /* 跟随主题弹窗内容区配色，保持与全局弹窗一致 */
    background-color: var(--dialogContBgClr, #0b1a2e);
    .el-dialog__header {
      padding: 0 15px !important;
      margin: 0;
      border-bottom: none !important;
      background: linear-gradient(90deg, #0d47a1, #1565c0);
      height: 48px;
      line-height: 48px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      .el-dialog__title {
        color: #fff;
        font-size: 15px;
        font-weight: bold;
      }
      .el-dialog__headerbtn {
        position: static;
        .el-dialog__close {
          color: #fff;
          font-size: 18px;
        }
      }
    }
  }
}
/* 参数执行/批量处理弹窗标题栏样式 */
::v-deep .dialogModel1 .el-dialog {
  background-color: var(--dialogContBgClr, #0b1a2e);
}
::v-deep .dialogModel1 .el-dialog__header {
  padding: 0 15px !important;
  margin: 0;
  border-bottom: none !important;
  background: linear-gradient(90deg, #0d47a1, #1565c0);
  height: 48px;
  line-height: 48px;
  .title {
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 100%;
    span:first-child {
      color: #fff;
      font-size: 15px;
      font-weight: bold;
    }
    span:last-child {
      color: #fff;
      i {
        color: #fff;
        font-size: 18px;
        cursor: pointer;
        padding: 4px;
      }
      i.el-icon-close:hover {
        opacity: 0.7;
      }
    }
  }
}
/* 暗色弹窗内表格文字与悬浮行配色（原为白底设计的 #333/#ecf5ff） */
::v-deep .dialogModel1 .el-table__body tr.hover-row > td,
::v-deep .dialogModel1 .el-table__body tr.el-table__row--striped.hover-row > td {
  color: var(--tableWordClr, #c8ddf0) !important;
  background-color: var(--currentRow, #186097) !important;
}
::v-deep .dialogModel1 .el-table__body tr:hover > td {
  color: var(--tableWordClr, #c8ddf0) !important;
  background-color: var(--currentRow, #186097) !important;
}
::v-deep .dialogModel1 .el-table .cell {
  color: var(--tableWordClr, #c8ddf0);
}
::v-deep .dialogModel1 .el-table th .cell {
  color: var(--tableHeaderClr, #a0c4e8);
}
::v-deep .dialogModel1 .el-table td,
::v-deep .dialogModel1 .el-table th {
  border-color: var(--tableBorderClr, #1a3a6a);
}
::v-deep .dialogModel1 .el-table td .el-input__inner {
  color: var(--tableWordClr, #c8ddf0);
}
</style>
