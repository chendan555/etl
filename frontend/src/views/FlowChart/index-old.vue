<template>
  <el-container class="flowChartWrap">
    <el-aside width="224px" class="left">
      <ComponentsTree @dragHandle="dragHandle" @showPlanData="showPlanData" :data="nodeData"/>
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
                  <div class="item">
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
                  </div>
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
            <div class="bottom-container" :class="showBottom?'showBottom':'hideBottom'">
              <p class="setHeight" @click="setHeight()" v-if="showBottom">
                <i class="el-icon-caret-bottom"></i>
              </p>
              <p class="setHeight" @click="setHeight()" v-else>
                <i class="el-icon-caret-top"></i>
              </p>
              <Terminal :runLog="runLog" :stepTableData="stepTableData"/>
            </div>
          </el-main>
          <el-aside class="right" :class="allWidth?'showAttrSide':'hideAttrSide'">
            <Attr
              :currentNodeMsg="currentNodeMsg"
              :projectDesc="projectDesc"
              :toolBarShow="toolBarShow"
              :isShowNode="isShowNode"
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
  components: {
    ProgressEle,
    BaseDialogForm,
    BaseTable,
    ComponentsTree,
    Terminal,
    Attr,
    BaseForm
  },

  data() {
    return {
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
      // 多tab弹框配置
      isShowTab: false,
      tabList: [],
      tabActiveInit: '',
      isShowBtn: false,
      isStartMove: false,
      showBottom: true,
      handleExtensionVisible: false,
      transformationId: '', //当前组件transformationId
      crtNodeData: {
        nodeId: ''
      }
    };
  },
  watch: {
    filterText(val: string) {
      (this.$refs.filterTree as any).filter(val);
    },
    eleList(val: object) {
      console.log(val);
    }
  },
  mounted() {
    initEleList();
    // 清空之前保留的流程节点
    editor.clearAll();
    this.$route.query.type ? this.showPlanData(this.$route.query.item) : this.init();
    this.$eventBus.$on('clearTable', this.clearTable);
    this.$eventBus.$on('refreshConfig', this.refreshConfig);
    this.$eventBus.$on('copyNodeData', (data: any) => {
      this.copyNodeData = data;
    });

    this.$eventBus.$on('showExcelBtn', (data: any) => {
      if (this.ruleForm.hasOwnProperty('ExcelInput') && (this.ruleForm as any).ExcelInput) {
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
    pastNode() {
      let dom: any = document.getElementById('mainContainer');
      let width: string = window.getComputedStyle(dom, null).width || '';
      let height: string = window.getComputedStyle(dom, null).height || '';
      exec(
        PasteNodeCommand,
        {
          pageX: (this.copyNodeData as any).position.left + 268,
          pageY: (this.copyNodeData as any).position.top + 180
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
      axios.get('/api/trans/plan/categroy/tree.do').then((res: any) => {
        console.log(res.data.data);
        this.createModelDialog.planGroupOptions = res.data.data;
      });
    },
    clearTable() {
      this.tableData = [];
    },
    setHeight(hei: any) {
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
        case 'execModel':
          this.execModel();
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
      this.transformationId = this.$route.query.transformationId as string;
      this.transName = this.$route.query.transName as string;
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
      });
      // debugger
      this.$store.dispatch('setPath', this.$route.query.path);
      API.getFlowChartData({
        type: 'transformation',
        path: this.$route.query.path
      }).then((data: any) => {
        // -start 处理后端返回的数据中tabList为字符串，数据在画布中加载报错的问题  --20210514
        data.data.content.nodes.forEach((elem: any) => {
          if (elem.properties.tabList === '') {
            elem.properties.tabList = [];
          }
        });
        // -end

        FlowChart.loadData(data.data.content);
      });
      API.getMenuData({ type: 1, useType: 1 }).then((data: any) => {
        this.nodeData = data.data.content;
      });
    },
    showPlanData(item: any) {
      // console.log('item');
      sessionStorage.setItem('path', '/' + item.name);
      this.getData(item.name);
      // sessionStorage.setItem('transformationId', item.id as any);
      this.$store.dispatch('setTransId', item.id);
      this.transformationId = item.id as string;
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
        this.projectDesc = res.data.root[0];
        if (res.data.dataBase.length) {
          sessionStorage.setItem('crtDatabase', res.data.dataBase[0].name);
        }
        // this.$nextTick(() => {
        //   initEleList();
        // });
      });
    },
    // 初始化节点配置
    generateNode(nodeData: any) {
      let type = '';
      if (nodeData.hasOwnProperty('data')) {
        type = nodeData.data.pluginId;
      } else {
        type = nodeData.pluginId;
      }
      let {
        tabVisible,
        activeName,
        dialog,
        isShowTab,
        tabList,
        tabActiveInit,
        formPreview,
        formItemRuleConfig,
        formItemConfig,
        formItemConfigDown,
        ruleForm,
        tableColumnConfig,
        tableColumnConfigList,
        tableData,
        isHasMonaco,
        monacoType,
        otherConfig,
        isShowRun,
        isHasPreview,
        isHasPreviewOut,
        isHasPreviewExcel,
        isFilePreview,
        isHasTable,
        isShowBtn,
        isHasSqlConditionTree,
        isHasDoubleTable,
        isHasCreateTableBtn,
        isHasGetFieldBtn,
        isShowNewBtn,
        isShowNewBtnInputType,
        doubleTableConfig,
        handleExtensionVisible,
        tabTableVisible
      } = eleList[type];
      if (type === 'TableInput') {
        //当组件为表输入时，设置当前流程选择的数据库地址第一个为默认值，该地址由流程双击时获取流程基本信息接口获取，并存入sessionStorage中
        ruleForm.connection = sessionStorage.getItem('crtDatabase') ? sessionStorage.getItem('crtDatabase') : '';
      }

      this.dialog = dialog;
      this.formItemRuleConfig = formItemRuleConfig;
      this.formItemConfig = formItemConfig;
      this.formItemConfigDown = formItemConfigDown;
      this.tableColumnConfig = tableColumnConfig;
      this.tableColumnConfigList = tableColumnConfigList;
      this.tabVisible = tabVisible;
      this.tableData = tableData;
      this.isHasPreview = isHasPreview;
      this.isHasPreviewOut = isHasPreviewOut;
      this.isHasPreviewExcel = isHasPreviewExcel;
      this.isFilePreview = isFilePreview;
      this.tabTableVisible = tabTableVisible;
      this.activeName = activeName;
      this.isHasMonaco = isHasMonaco;
      this.monacoType = monacoType;
      this.otherConfig = otherConfig;
      this.isShowRun = isShowRun;
      this.isHasTable = isHasTable;
      this.isHasSqlConditionTree = isHasSqlConditionTree;
      this.isHasDoubleTable = isHasDoubleTable;
      this.isHasCreateTableBtn = isHasCreateTableBtn;
      this.doubleTableConfig = doubleTableConfig;
      this.isHasGetFieldBtn = isHasGetFieldBtn === false ? false : true;
      this.isShowNewBtn = isShowNewBtn === false ? isShowNewBtn : true;
      this.isShowNewBtnInputType = isShowNewBtnInputType;
      this.isShowTab = false;
      this.formPreview = formPreview;
      this.isShowBtn = isShowBtn;

      this.handleExtensionVisible = handleExtensionVisible;

      if (isShowTab) {
        // 多tab弹框配置
        this.isShowTab = isShowTab;
        this.tabList = tabList;
        this.tabActiveInit = tabActiveInit;
        this.tabChange(this.tabActiveInit);
      } else {
        this.isShowTab = false;
        this.tabList = [];
      }

      let currentNodeMsg = FlowChart.getNodeDataByNodeId(nodeData.nodeId);
      this.$store.dispatch('setNodeId', nodeData.nodeId);
      if (currentNodeMsg && currentNodeMsg.properties.ruleForm) {
        this.ruleForm = currentNodeMsg.properties.ruleForm;
        this.tabList = currentNodeMsg.properties.tabList;
        // 初始化重新绑定formItemConfig和tableColumnConfig
        if (tabList && tabList.length) {
          let _this = this;
          tabList.forEach((tab: any, index: number) => {
            if (this.tabList[index]) {
              const { formItemConfig = [], tableColumnConfig = [], tableData = [] } = tab;
              (this.tabList as any)[index] = {
                ...(_this.tabList[index] as any),
                formItemConfig,
                tableColumnConfig,
                tableData
              };
            } else {
              (this.tabList as any).push(tab);
            }
            // (this.tabList[index] as any).formItemConfig = formItemConfig;
            // (this.tabList[index] as any).tableColumnConfig = tableColumnConfig;
          });
        }
        this.tableData = currentNodeMsg.properties.tableData;
        this.generateTabList(this.ruleForm.stepName);
      } else {
        let text = nodeData.hasOwnProperty('data') ? nodeData.data.text : nodeData.text;
        this.ruleForm = this.$utils.deepClone(ruleForm);
        this.ruleForm.stepName = text;
        this.tableData = tableData ? this.$utils.deepClone(tableData) : [];
        this.generateTabList(text);
      }

      // 初始化后出发配置方法
      if (eleList[type].hasOwnProperty('created')) {
        eleList[type].created.call(this, this.ruleForm);
      }

      this.dialog.visible = true;
    },
    /**
     * @description 初始化tabList内容
     * @param text 节点名称
     */
    generateTabList(text: string) {
      if (this.tabList && this.tabList.length) {
        this.tabList.forEach((tab: any) => {
          (tab.ruleForm as any).stepName = text;
        });
      }
    },
    refreshConfig(key: string, config: any) {
      (this as any)[key] = config;
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
          (this.ruleForm as any)[config.key].push(this.initRowData(config.tableColumnConfig));
        });
      } else {
        // 单个表格
        (this.tableData as any).push(this.initRowData(this.tableColumnConfig));
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
          (this.ruleForm as any)[config.key] = [];
        });
      }
      // 此处将tableData设置为[]组件将出错，无法保存该项表格的数据，因为改变了tableData的引用地址，断开了与组件配置项的联系。  --20210517
      // this.tableData = [];
      if (this.ruleForm.remove) {
        this.ruleForm.remove = [];
      }
      console.log(this.ruleForm);

      let interfaceName =
        this.otherConfig && this.otherConfig.hasOwnProperty('getFieldsInterface') ? (this.otherConfig as any).getFieldsInterface : 'getInputOutputFields';
      switch (interfaceName) {
        case 'getExcelFields':
          (params as any).index = (this.ruleForm as any).sheetName;
          (params as any).fileName = this.ruleForm.filefield;
          (params as any).spreadsheet_type = this.ruleForm.spreadsheet_type;
        default:
          break;
      }
      (API as any)
        [interfaceName](params)
        .then((data: any) => {
          // let tableData: any = [];
          if (data.data || data.data.content) {
            data.data.forEach((dataItem: any) => {
              // 多个表格
              if (this.tableColumnConfigList && this.tableColumnConfigList.length) {
                this.tableColumnConfigList.forEach((config: any) => {
                  (this.ruleForm as any)[config.key].push(this.initRowData(config.tableColumnConfig, dataItem));
                });
              } else {
                // 单个表格

                (this.tableData as any).push(this.initRowData(this.tableColumnConfig, dataItem));
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
      this.ruleForm.sql = (this.$refs.baseDialog as any).monacoInstance.getValue();
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
      API.SaveJSON({
        noFresh: callback ? true : false,
        transformationId: this.transformationId
      }).then((data: any) => {
        if (callback) {
          callback();
        } else {
          this.$message({
            type: data.data.success ? 'success' : 'error',
            message: data.data.message ? data.data.message : '保存失败！'
          });
        }
      });
    },
    formSubmit(vaild: any, isClose: boolean) {
      if (vaild) {
        let nowFlowChart = FlowChart.getData();
        nowFlowChart.nodes.forEach((item: any) => {
          if (item.id === this.$store.state.nodeId) {
            item.properties.ruleForm = this.$utils.deepClone(this.ruleForm);
            item.properties.tableData = this.$utils.deepClone(this.tableData);
            item.properties.tabList = this.tabList;
            item.data.text = this.ruleForm.stepName;
            if (this.tabList && this.tabList.length) {
              this.tabList.forEach((ele: any) => {
                if (ele.hasOwnProperty('alias') && ele.alias) {
                  item.properties.ruleForm[ele.alias] = this.$utils.deepClone(ele.tableData);
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
              message: data.data.message
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
      // console.log(data);
      if (data) FlowChart.addNode({ pageX: ev.pageX, pageY: ev.pageY }, JSON.parse(data));
    },
    clickBgHandle() {
      this.isShowNode = false;
      (document.getElementById('mainContainer') as any).setAttribute('tabindex', '0');
      (document.getElementById('mainContainer') as any).focus();
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
        this.runLog = data.data.log;
        this.stepTableData = data.data.stepMeasure;
        data.data.stepStatus.forEach((dataItem: any) => {
          let nowFlowChart = FlowChart.getData();
          nowFlowChart.nodes.forEach((item: any) => {
            if (dataItem.stepName === item.data.text) {
              editor.execModel(item.id, this.getNodeState(dataItem.stepStatus));
            }
          });
        });
      });
    },
    getNodeState(status: number) {
      if (status === 0) {
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
        API.execModel({ noFresh: true })
          .then((data: any) => {
            if (data.data.success) {
              this.getLog({ executionId: data.data.message });
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
              message: data.data.message ? '执行完成' : '未执行完成'
            });
          });
      });

      // this.prog.isShow = false;
    },
    execModelOptions() {
      this.dialog = execModelConfig.dialog;
      // this.formItemRuleConfig = execModelConfig.formItemRuleConfig;
      this.formItemConfig = execModelConfig.formItemConfig;
      this.formItemConfigDown = execModelConfig.formItemConfigDown;
      this.tableColumnConfig = execModelConfig.tableColumnConfig;
      this.tableData = execModelConfig.tableData;
      this.isHasTable = execModelConfig.isHasTable;
      this.isHasExec = execModelConfig.isHasExec;
      // this.formPreview = execModelConfig.formPreview;
      this.dialog.visible = true;
    },
    startExec() {},
    createModel() {
      this.createModelDialog.formList.procName = this.transName;
      this.getPlanGroupList();
      this.createModelDialog.visible = true;
    }
  }
});
</script>

<style lang="scss" scoped>

@import './src/styles/theme.scss';
// .bottom-container .el-tabs--border-card > .el-tabs__header .el-tabs__item.is-active{
//   border: none;
// }
.flowChartWrap {
  height: 100%;
  .left {
    border-right: 1px solid #435b44;
    height: 100%;
    .el-tree {
      height: calc(100% - 40px);
      overflow-y: auto;
    }
  }
  .right {
    border-left: 1px solid #d4e8f5;
    position: relative;
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
      background: $inputBgClr;
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
      bottom: 0;
      left: 0;
      width: 100%;
     
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
    height: 300px;
  }
  to {
    height: 42px;
  }
}
@keyframes showBottom {
  from {
    height: 42px;
  }
  to {
    height: 300px;
  }
}
.hideBottom {
  animation: hideBottom 0.8s;
  height: 42px !important;
}
.showBottom {
  animation: showBottom 0.8s;
  height: 300px !important;
  overflow: hidden;
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
/deep/.dialogModel1 .el-tabs__content {
  display: none;
}
/deep/.dialogModel1 .el-input__inner,
.dialogModel1 .el-textarea__inner {
  border: 1px solid #435b44 !important;
}
</style>
