<template>
  <div class="processList">
    <div class="headerDiv">
      <div class="btndiv">
        <el-button icon="el-icon-connection" type="primary" size="mini" @click="newProcess">新建流程</el-button>
        <el-dropdown class="chgView" @command="handleCommand">
          <el-button size="mini" type="primary">
            布局
            <i class="el-icon-arrow-down"></i>
          </el-button>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item
              v-for="type in viewTypeList"
              :key="type.value"
              :command="type.value"
            >{{type.label}}</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
        <div class="leftdiv">
          <el-button size="mini" type="primary" icon="el-icon-attract" @click="mergeTXT">合并TXT文件</el-button>
        </div>
      </div>
      <div class="filterdiv">
        <el-form ref="form" :model="filterForm" size="mini" :inline="true">
          <el-form-item label="创建时间">
            <el-date-picker
              style="width:335px"
              v-model="filterForm.createTime"
              value-format="yyyy-MM-dd HH:mm:ss"
              type="datetimerange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
            ></el-date-picker>
          </el-form-item>
          <el-form-item label="关键字">
            <el-input v-model="filterForm.searchVal" placeholder="请输入关键字"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="getListdate">查询</el-button>
            <el-button type="info" @click="cancelSearch">重置</el-button>
          </el-form-item>
          <!-- <el-form-item label="运行状态">
                        <el-select v-model="filterForm.status" placeholder="请选择运行状态">
                            <el-option :label="item.label" :value="item.value" v-for="(item, index) in runStatusOptions" :key="index"></el-option>
                        </el-select>
          </el-form-item>-->
        </el-form>
      </div>
    </div>
    <div class="listDiv">
      <div class="imageType" v-if="viewType === 3">
        <div class="tips" v-if="!procListData.length">
          <i class="el-icon-warning-outline"></i>
          <p>暂无数据</p>
        </div>
        <div
          class="itemdiv"
          v-for="(item, index) in procListData"
          :key="index"
          @dblclick="editProcCont(item, index)"
          @click="showProcDetail(item)"
        >
          <div class="imgdiv">
            <img :src="item.imgUrl" alt>
          </div>
          <div class="infodiv">
            <div class="item allWidth">
              <div class="name">
                <p>
                  <span v-for="(i, ix) in '流程名称'" :key="ix">{{i}}</span>
                </p>：
              </div>
              <div class="cont">
                <p>{{item.name}}</p>
              </div>
            </div>
            <div class="item" v-for="(it, idx) in procInforLabel" :key="idx" v-show="idx">
              <div class="name">
                <p>
                  <span v-for="(i, ix) in it.label" :key="ix">{{i}}</span>
                </p>：
              </div>
              <div class="cont">
                <div v-if="it.prop === 'status'">{{item.status | rtnStatus}}</div>
                <p v-else>{{item[it.prop]}}</p>
              </div>
            </div>
          </div>
          <div class="btndiv btn">
            <operation-btn
              :projectItem="item"
              @removeProc="removeProc"
              @copyItem="copyItem"
              @setItemModel="setItemModel"
              @setProjectToModel="setProjectToModel"
              @editProc="editProc"
            ></operation-btn>
          </div>
        </div>
      </div>
      <div class="cardType" v-if="viewType === 2" ref="cardType">
        <div class="tips" v-if="!procListData.length">
          <i class="el-icon-warning-outline"></i>
          <p>暂无数据</p>
        </div>
        <div
          class="itemdiv"
          v-for="(item, index) in procListData"
          :key="index"
          @dblclick="editProcCont(item, index)"
          @click="showProcDetail(item)"
        >
          <div class="headTitle">
            <p class="title">{{item.name}}</p>
            <div class="btn">
              <operation-btn
                :projectItem="item"
                @removeProc="removeProc"
                @copyItem="copyItem"
                @setItemModel="setItemModel"
                @setProjectToModel="setProjectToModel"
                @editProc="editProc"
              ></operation-btn>
            </div>
          </div>
          <div class="contdiv">
            <div class="infodiv">
              <div class="item" v-for="(it, idx) in procInforLabel" :key="idx" v-show="idx">
                <div class="name">
                  <p>
                    <span v-for="(i, ix) in it.label" :key="ix">{{i}}</span>
                  </p>：
                </div>
                <div class="cont">
                  <div v-if="it.prop === 'status'">
                    {{item.status | rtnStatus}}
                    <!-- <span>
                    <i class="success el-icon-success" v-if="item.status === 1"></i>
                    <i class="error el-icon-error" v-else-if="item.status === 2"></i>
                    <i class="info el-icon-info" v-else></i>
                    </span>-->
                  </div>
                  <p v-else>{{item[it.prop]}}</p>
                </div>
              </div>
            </div>
            <div class="imgdiv">
              <img :src="item.imgUrl" alt>
            </div>
          </div>
        </div>
      </div>
      <div class="tableType" v-if="viewType === 1">
        <el-table
          :data="procListData"
          @row-dblclick="editProcCont"
          @row-click="showProcDetail"
          height="100%"
        >
          <el-table-column
            v-for="(item, index) in procInforLabel"
            :key="index"
            :prop="item.prop"
            :label="item.label"
          >
            <template slot-scope="scope">
              <p
                v-if="item.prop === 'describe'"
                class="describe"
                :title="scope.row[item.prop]"
              >{{scope.row[item.prop]}}</p>
              <p
                v-else-if="item.prop === 'status'"
                class="status"
                :title="scope.row[item.prop] | rtnStatus"
              >{{scope.row[item.prop] | rtnStatus}}</p>
              <p v-else>{{scope.row[item.prop]}}</p>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template slot-scope="scope">
              <div class="btn">
                <operation-btn
                  :projectItem="scope.row"
                  @removeProc="removeProc"
                  @copyItem="copyItem"
                  @setItemModel="setItemModel"
                  @setProjectToModel="setProjectToModel"
                  @editProc="editProc"
                ></operation-btn>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
    <div class="pageSize">
      <BasePagination :data="page" @current="currentPage" @sizes="pageSizes"></BasePagination>
    </div>
    <el-dialog
      :title="procDialog.isNew?'新建流程':'修改流程'"
      :visible.sync="procDialog.visible"
      class="newProcessDialog"
      width="70%"
      @closed="initProcDialog"
      append-to-body
      modal-append-to-body
      :show-close="false"
    >
      <div class="content">
        <el-form ref="form" :model="formList" label-width="100px" size="small" :rules="rules">
          <el-form-item label="流程名称" prop="name">
            <el-input placeholder="请输入流程名称（示例：样例数据处理流程）" v-model="formList.name"></el-input>
          </el-form-item>
          <!-- <el-form-item label="数据来源" prop="serverSourceId">
            <el-select v-model="formList.serverSourceId">
              <el-option
                :label="item.sourceName"
                :value="item.id"
                v-for="(item, index) in serverSourceList"
                :key="index"
              ></el-option>
            </el-select>
          </el-form-item>-->
          <el-form-item label="选择分组" prop="taskGroupId">
            <el-select v-model="formList.taskGroupId" placeholder="请选择分组" @change="handleChange">
              <el-option
                v-for="item in flatGroupList"
                :key="item.idTaskGroup"
                :label="item.taskGroupName"
                :value="item.idTaskGroup"
              ></el-option>
            </el-select>
          </el-form-item>
          <!-- <el-form-item label="试验任务" prop="taskId">
            <el-select v-model="formList.taskId" @change="setTaskName">
              <el-option
                v-for="(item, index) in taskLists"
                :label="item.taskName"
                :value="item.id"
                :key="index"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="所属节点" prop="taskNodePath">
            <el-cascader
              v-model="formList.taskNodePath"
              :options="nodeList"
              :props="{
            label: 'name',
            children: 'children',
            value: 'id',
            checkStrictly: true
          }"
              placeholder="请选择所属节点"
              clearable
              filterable
            ></el-cascader>
          </el-form-item>-->
          <el-form-item label="描述">
            <el-input placeholder="请输入描述（示例：流程描述）" type="textarea" v-model="formList.describe"></el-input>
          </el-form-item>
          <!-- <el-form-item label="定时策略">
            <el-checkbox v-model="isMakeStratagy">是否定制</el-checkbox>
            <Cron v-if="isMakeStratagy" v-model="formList.cron"></Cron>
          </el-form-item>-->
        </el-form>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="closeprocDialog">确 定</el-button>
        <el-button type="info" @click="initProcDialog">取 消</el-button>
      </span>
    </el-dialog>
    <copy-flow
      v-if="showCopy"
      ref="add"
      @close=" showCopy=false"
      :itemCopy="itemCopy"
      :dataList="treeData"
    ></copy-flow>
    <!-- @refresh="getList" -->
    <base-dialog :dialogConfig="dialogConfig" @closeDialog="closeDialog" @save="saveDialog">
      <div class="setProjToModel">
        <el-form ref="form" :model="projToModel" size="mini">
          <el-form-item label="模板分组" prop="taskGroupId">
            <el-select v-model="projToModel.taskGroupId">
              <el-option
                v-for="menu in modelMenuList"
                :key="menu.idTaskGroup"
                :label="menu.taskGroupName"
                :value="menu.idTaskGroup"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
    </base-dialog>
    <base-dialog
      :dialogConfig="baseDialogConfig"
      @closeDialog="closeDialogTxt"
      @save="saveDialogTxt"
    >
      <merge-txt
        ref="mergeTxt"
        v-if="baseDialogConfig.visible"
        :ruleForm="baseDialogConfig.data.ruleForm"
        @closeDialog="closeDialogTxt"
      ></merge-txt>
    </base-dialog>
  </div>
</template>
<script>
/**
 * Create by Leo
 * 数据处理流程列表页面
 * 主要实现数据处理流程列表展示、列表筛选、新建处理流程、编辑流程、删除流程、设置模板等列表项操作、合并txt文件等功能
 * 流程列表支持卡片、大图、列表三种布局方式
 */
import BasePagination from '@/components/BasePagination';
import BaseTable from '@/components/BaseTable/index.vue';
import BaseDialog from '@/components/BaseDialog/index.vue';
import copyFlow from './copyFlow';
import Cron from '@/components/Cron';
import MergeTxt from './MergeTxt.vue';
import OperationBtn from './OperationBtn.vue';
import { taskManagerService } from '@/api/index.ts';
import { procInforLabel, detailInforLabel, mergeTxtFormConfig } from './config.js';
// import config from './config.ts';
export default {
  name: 'processList',
  components: { MergeTxt, BasePagination, BaseTable, Cron, copyFlow, BaseDialog, OperationBtn },
  props: ['treeData'],
  data() {
    return {
      /**
       * 流程分组信息
       */
      treeDataNew: [],
      /**
       * 合并TXT功能弹框配置项
       */
      baseDialogConfig: {
        visible: false,
        title: '合并TXT',
        data: {
          ruleForm: this.$utils.deepCopy(mergeTxtFormConfig.ruleForm)
        }
      },
      /**
       * 列表展示方式列表
       */
      viewTypeList: [
        {
          label: '列表',
          value: 1
        },
        {
          label: '缩略图',
          value: 2
        }
        // {
        //   label: '大图',
        //   value: 3
        // }
      ],
      /**
       * 流程设置为模板弹框配置
       */
      dialogConfig: {
        visible: false,
        title: '选择模板分组'
      },
      /**
       * 流程设置为模板配置项
       */
      projToModel: {
        type: '1',
        taskGroupId: '',
        isModel: 1,
        transformationId: ''
      },
      /**
       * 数据源列表
       */
      serverSourceList: [],
      /**
       * 数据列表
       */
      dataList: {},
      /**
       * 复制弹窗的关闭和打开
       */
      showCopy: false, //复制弹窗的关闭和打开
      /**
       * 是否设置定时策略
       */
      isMakeStratagy: false,
      /**
       * 任务列表
       */
      taskLists: [],
      /**
       * 筛选项表单配置
       */
      filterForm: {
        groupId: '',
        name: '',
        createTime: [],
        status: null,
        searchVal: '',
        type: '' // 所属流程分组类型
      },
      /**
       * 运行状态列表
       */
      runStatusOptions: [
        {
          label: '运行成功',
          valeue: 1
        },
        {
          label: '运行失败',
          valeue: 2
        },
        {
          label: '待运行',
          valeue: 0
        }
      ],
      /**
       * 分页配置
       */
      page: {
        pageSize: 20,
        pageNum: 1,
        sizes: [10, 20, 30, 40, 50, 100],
        total: 10
      },
      /**
       * 当前列表展示形式
       */
      viewType: 1,
      /**
       * 流程列表数据
       */
      procListData: [],
      /**
       * 流程配置信息配置
       */
      procInforLabel,
      /**
       * 是否展示列表操作按钮
       */
      isShowOperation: true,
      /**
       * 流程配置项
       */
      procDialog: {
        isNew: true,
        visible: false,
        data: {
          name: '',
          describe: '',
          taskGroupId: '',
          serverSourceId: '',
          taskId: '',
          taskNodePath: '',
          createBy: '',
          createDate: '',
          status: 0,
          cron: ''
        }
      },
      /**
       * 流程配置项表单验证
       */
      rules: {
        name: [
          {
            required: true,
            message: '请输入流程名称',
            trigger: 'blur'
          }
        ],
        taskGroupId: [
          {
            required: true,
            message: '请选择分组',
            trigger: 'blur'
          }
        ],
        serverSourceId: [
          {
            required: true,
            message: '请选择数据来源',
            trigger: 'blur'
          }
        ]
      },
      /**
       * 数据源列表
       */
      dataSourceList: [],
      /**
       * 任务组列表
       */
      taskGroupList: [],
      /**
       * 当前编辑流程序号
       */
      editProcIndex: null,
      /**
       * 目标节点
       */
      targetNode: {},
      itemCopy: {}, //复制传递的参数
      /**
       * 信息配置详情
       */
      detailInforLabel,
      /**
       * 任务节点列表
       */
      nodeList: []
      // type: 1,//请求接口区分流程管理和数据挖据分析
    };
  },

  computed: {
    /**
     * 计算获取表单列表
     */
    formList() {
      if (typeof this.procDialog.data.taskNodePath === 'string') {
        this.procDialog.data.taskNodePath = this.procDialog.data.taskNodePath ? this.procDialog.data.taskNodePath.split(',') : [];
      }
      this.procDialog.data.taskId = this.procDialog.data.taskId ? Number(this.procDialog.data.taskId) : this.procDialog.data.taskId;
      return this.procDialog.data;
    },
    /**
     * 计算获取用户登录名称
     */
    username() {
      let userinfoJSON = sessionStorage.getItem('user');
      let userinfo = userinfoJSON ? JSON.parse(userinfoJSON) : null;
      return userinfo ? userinfo.username : '';
    },
    /**
     * 计算获取模型菜单列表
     */
    modelMenuList() {
      return this.$store.state.modelMenu;
    },
    /**
     * 将树形数据扁平化为列表
     */
    flatGroupList() {
      const result = [];
      const flatten = (list) => {
        if (!list || !Array.isArray(list)) return;
        list.forEach(item => {
          result.push(item);
          if (item.children && item.children.length) {
            flatten(item.children);
          }
        });
      };
      flatten(this.treeData);
      return result;
    }
  },
  mounted() {
    this.$eventBus.$on('showProjInfor', data => {
      this.filterForm.groupId = data.idTaskGroup;
      this.filterForm.type = data.type;
      this.getProcessData();
    });
    this.$eventBus.$on('targetNode', data => {
      this.targetNode = data;
      this.procDialog.data.taskGroupId = data;
    });
    if (parseInt(sessionStorage.getItem('viewType'))) {
      this.viewType = parseInt(sessionStorage.getItem('viewType'));
    }
    this.initWidth();
    let _this = this;
    window.onresize = function() {
      _this.initWidth();
    };
    this.getGroup();
    this.getProcessData();
  },
  methods: {
    /**
     * 列表布局切换
     */
    handleCommand(command) {
      this.viewType = command;
    },
    /**
     * 请求任务列表
     * 设置任务名称
     */
    setTaskName(value) {
      this.taskLists.forEach((item, index) => {
        if (item.id == this.formList.taskId) {
          this.$set(this.formList, 'taskName', item.taskName);
        }
      });
      taskManagerService
        .getTaskFlowById(value)
        .then(({ data }) => {
          if (data.code == 200 && data.data) {
            let importJson = JSON.parse(data.data.mapInfo);
            this.arrangeData([importJson.root]);
            this.nodeList = [importJson.root];
            console.log(this.nodeList);
          } else {
          }
        })
        .catch(() => {});
    },
    /**
     * 整理数据结构
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
     * 关闭多TXT文件合并弹框
     */
    closeDialogTxt() {
      this.baseDialogConfig.visible = false;
      this.baseDialogConfig.data.ruleForm = this.$utils.deepCopy(mergeTxtFormConfig.ruleForm);
    },
    /**
     * 展示多TXT文件合并弹框
     */
    mergeTXT() {
      this.baseDialogConfig.visible = true;
    },
    /**
     * 获取数据源列表
     */
    // getServerSource() {
    //   this.$API.getServerSourceList().then(({ data }) => {
    //     this.serverSourceList = data.data;
    //   });
    // },
    /**
     * 获取流程信息，弹框选择模板分组
     */
    setProjectToModel(proj) {
      // this.projToModel.isModel = proj.isModel;
      this.projToModel.transformationId = proj.transformationId;
      this.dialogConfig.visible = true;
    },
    /**
     * 关闭弹框
     */
    closeDialog() {
      this.dialogConfig.visible = false;
    },
    /**
     * 执行多TXT文件合并
     */
    saveDialogTxt() {
      this.$refs.mergeTxt.submit();
    },
    /**
     * 设置流程到选定模板分组
     */
    saveDialog() {
      this.$API.projectManage.updateTransToModel(this.projToModel).then(({ data }) => {
        this.$message({
          message: data.result,
          type: data.success ? 'success' : 'warning'
        });
        this.getProcessData();
        this.closeDialog();
      });
    },
    /**
     * 获取流程分组
     */
    getGroup() {
      this.$API.projectManage.getGroupList().then(res => {
        this.dataList = res.data;
      });
    },
    /**
     * 模板复制
     */
    copyItem(item) {
      this.showCopy = true;
      this.itemCopy = item;
    },
    /**
     * 设置/取消模板
     */
    setItemModel(item) {
      let params = {
        transformationId: item.transformationId,
        isModel: 0
      };
      this.$confirm(`当前流程${item.isModel ? '取消' : '设置为'}模板?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          this.$API.projectManage.updateTransToModel(params).then(({ data }) => {
            this.$message({
              message: data.result,
              type: data.success ? 'success' : 'warning'
            });
            this.getProcessData();
          });
        })
        .catch(() => {});
    },
    /**
     * 查询
     */
    getListdate() {
      this.page.pageNum = 1;
      this.getProcessData();
    },
    /**
     * 展示流程详情
     */
    showProcDetail(item) {
      this.detailInforLabel.forEach(ele => {
        ele.value = item[ele.prop];
      });
      this.$eventBus.$emit('procDetailInfor', this.detailInforLabel);
    },
    /**
     * 初始化宽度
     */
    initWidth() {
      //动态计算卡片布局宽度
      if (this.$refs.cardType) {
        let allWidth = parseInt(window.getComputedStyle(this.$refs.cardType, null).width);
        let num = parseInt(allWidth / 310);
        let style = document.getElementsByTagName('body')[0].style;
        style.setProperty(`--itemMaxWidth`, `calc((100% - 80px) / ${num})`);
      }
    },
    /**
     *
     */
    handleChange(value) {
      // console.log(value);
    },
    /**
     * 跳转流程编辑页面
     */
    editProcCont(scope, index) {
      sessionStorage.setItem('backPage', '/ProjectManage');
      // 设置流程为编辑状态
      this.$API.projectManage.setTransEditing(scope.transformationId).then(({ data }) => {
        // console.log(data);
        if (data.success) {
          this.$router.push({
            path: '/flowChart',
            query: {
              path: scope.directoryName,
              transName: scope.name,
              taskGroupName: scope.belongToTaskGroup,
              transformationId: scope.transformationId
            }
          });
          const _this = this;
          window.onbeforeunload = function() {
            // 流程还原为可编辑状态
            _this.$API.projectManage.setTransEditable(scope.transformationId).then(({ data }) => {
              // console.log(data);
              // console.log('123');
            });
            // console.log('124');
            return '退出？';
          };
        }
      });
      // sessionStorage.setItem('type', this.type);

      // this.$eventBus.$emit('setNavIndex', 0);
    },
    /**
     * 编辑流程基本信息
     */
    editProc(scope, index) {
      this.procDialog.isNew = false;
      // this.editProcIndex = index;
      let taskGroupObj = null;
      if (scope.isModel) {
        taskGroupObj = this.treeData[0];
      } else {
        taskGroupObj = this.treeData.find(task => task.type === scope.type.toString());
      }
      // console.log(taskGroupObj);
      taskGroupObj && (scope.taskGroupId = scope.belongToTaskGroupId ? Number(scope.belongToTaskGroupId) : taskGroupObj.idTaskGroup);
      Object.assign(this.procDialog.data, scope);
      // console.log(this.procDialog.data);
      // 判断流程任务配置项是否有数据，有则获取任务关联的节点列表
      if (this.procDialog.data.taskId) {
        this.setTaskName(this.procDialog.data.taskId);
      }
      // let belongToTaskGroup = this.procDialog.data.belongToTaskGroupId ? this.procDialog.data.belongToTaskGroupId.split(',') : [];
      /* this.procDialog.data.dataBaseId = this.procDialog.data.dataBaseId ? this.procDialog.data.dataBaseId.split(',') : [];
      // // this.procDialog.data.dataBaseId = this.procDialog.data.dataBaseId.map(ele => {
        return parseInt(ele);
      }); */
      // this.procDialog.data.taskGroupId = belongToTaskGroup.map(ele => {
      //   return parseInt(ele);
      // });
      this.newProcess();
    },
    /**
     * 关闭新建或修改流程信息弹框
     */
    closeprocDialog() {
      this.$refs.form.validate(valid => {
        if (valid) {
          // 判断当前流程配置为新建还是修改，并进行保存分发
          if (this.procDialog.isNew) {
            let nowTime = new Date();
            let date = nowTime.toLocaleDateString();
            let newDate = date.split('/').join('-');
            let time = nowTime.toTimeString();
            let patt = /\d{2}:\d{2}:\d{2}/g;
            let newTime = patt.exec(time).toString();
            this.procDialog.data.createDate = newDate + ' ' + newTime;
            this.procDialog.data.createBy = this.username;
            this.submitNewProc();
          } else {
            this.modifyProc();
          }
          // this.initProcDialog();
        }
      });
    },
    /**
     * 新建流程
     */
    submitNewProc() {
      // this.procDialog.data.dataBaseId = this.procDialog.data.dataBaseId.join(',');
      let params = {
        // isMakeStratagy: this.isMakeStratagy,
        dir: '/',
        ...this.procDialog.data,
        // type: this.type,
        taskGroupId: Array.isArray(this.procDialog.data.taskGroupId) ? this.procDialog.data.taskGroupId.join(',') : this.procDialog.data.taskGroupId,
        taskNodePath: Array.isArray(this.procDialog.data.taskNodePath) && this.procDialog.data.taskNodePath.length ? this.procDialog.data.taskNodePath.join(',') : ''
      };
      this.$API.createTrans(params).then(res => {
        if (res.data.code === 200) {
          this.$message({
            type: res.data.success ? 'success' : 'error',
            message: res.data.success ? '创建成功' : res.data.result
          });
          this.initProcDialog();
          this.getProcessData();
        }
      });
    },
    /**
     * 修改流程
     */
    modifyProc() {
      // this.procDialog.data.dataBaseId = this.procDialog.data.dataBaseId.join(',');
      let params = {
        // isMakeStratagy: this.isMakeStratagy,
        dir: '/',
        ...this.procDialog.data,
        taskGroupId: Array.isArray(this.procDialog.data.taskGroupId) ? this.procDialog.data.taskGroupId.join(',') : this.procDialog.data.taskGroupId,
        taskNodePath: Array.isArray(this.procDialog.data.taskNodePath) ? this.procDialog.data.taskNodePath.join(',') : (this.procDialog.data.taskNodePath || '')
      };
      this.$API.modifyTrans(params).then(res => {
        if (res.data.code === 200) {
          this.$message({
            type: res.data.success ? 'success' : 'error',
            message: res.data.success ? '修改成功' : res.data.content
          });
          this.initProcDialog();
          this.getProcessData();
        }
      });
    },
    /**
     * 初始化流程弹框信息内容
     */
    initProcDialog() {
      this.procDialog.isNew = true;
      this.procDialog.visible = false;
      this.isMakeStratagy = false;
      let procDialogData = {
        name: '',
        describe: '',
        taskGroupId: '',
        serverSourceId: '',
        taskId: '',
        taskNodePath: '',
        createBy: '',
        createDate: '',
        status: 0,
        cron: ''
      };
      // 设置初始化数据
      this.procDialog.data = Object.assign(this.procDialog.data, procDialogData);
      if (typeof this.procDialog.data.taskGroupId == 'string') {
        this.procDialog.data.taskGroupId = this.procDialog.data.taskGroupId.split(',');
      }
      // 清除任务名称数据，避免缓存数据污染其他流程数据  --Leo 2022/10/21
      this.formList.taskName = '';

      this.getProcessData();
      this.$refs['form'].clearValidate();
    },
    /**
     * 打开新建流程弹框
     */
    newProcess() {
      // this.getServerSource();
      if (!this.dataSourceList.length) {
        let p1 = this.initDataSource();
        // let p2 = this.initTaskGroupList();
        Promise.all([p1]).then(() => {
          this.procDialog.visible = true;
        });
      } else {
        this.procDialog.visible = true;
      }
    },
    /**
     * 获取项目列表
     */
    initTaskGroupList() {
      return new Promise((resolve, reject) => {
        this.$API
          .getProjListData
          // { type: this.type }
          ()
          .then(res => {
            this.taskGroupList = res.data.data;
            this.taskGroupList.forEach(ele => {
              ele.children = null;
            });
            resolve();
          });
      });
    },
    /**
     * 获取数据源列表
     */
    initDataSource() {
      return new Promise((resolve, reject) => {
        this.$API.getDatabases().then(res => {
          this.dataSourceList = res.data;
          resolve();
        });
      });
    },
    /**
     * 切换视图
     */
    chgView(num) {
      this.viewType = num;
      if (num === 2) {
        this.initWidth();
      }
      sessionStorage.setItem('viewType', this.viewType);
    },
    /**
     * 删除流程
     */
    removeProc(scope) {
      let params = {
        path: scope.directoryName,
        flag: 'transformation',
        idTransformation: scope.transformationId
      };
      // 弹框确认是否删除
      this.$confirm('此操作将删除该流程, 是否继续?', '提示', {
        distinguishCancelAndClose: true,
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      })
        .then(() => {
          this.$API.deleteTask(params).then(res => {
            this.$message({
              type: res.data.success ? 'success' : 'error',
              message: res.data.message
            });
            if (res.data.success) this.getProcessData();
          });
        })
        .catch(action => {});
    },
    /**
     * 配置分页信息并请求数据
     */
    currentPage(e) {
      this.page.pageNum = e;
      this.getProcessData();
    },
    /**
     * 配置分页信息并请求数据
     */
    pageSizes(e) {
      this.page.pageSize = e;
      this.getProcessData();
    },
    /**
     * 获取流程列表数据
     */
    getProcessData() {
      let params = {
        groupId: this.filterForm.groupId,
        startDate: this.filterForm.createTime[0] ? this.filterForm.createTime[0] : '',
        endDate: this.filterForm.createTime[1] ? this.filterForm.createTime[1] : '',
        name: this.filterForm.searchVal ? this.filterForm.searchVal : '',
        pageSize: this.page.pageSize,
        pageIndex: this.page.pageNum,
        type: this.filterForm.type
      };
      this.$API.getProjTransList(params).then(({ data }) => {
        const { code } = data;
        if (code === 200) {
          const { data: listObj } = data;
          const { records, total } = listObj;
          this.procListData = [...records];
          this.page.total = total;
        }
      });
    },
    /**
     * 重置筛选
     */
    cancelSearch() {
      this.filterForm.createTime = [];
      this.filterForm.searchVal = '';
      this.getProcessData();
    },
    /**
     * 展示行信息
     */
    showRowInfor(row, column, event) {
      console.log(row);
      console.log(column);
      console.log(event);
    }
  },
  filters: {
    rtnStatus(val) {
      if (val === 1) {
        return '编辑中';
      } else if (val === 2) {
        return '可编辑';
      } else {
        return '';
      }
    }
  }
};
</script>
<style lang="scss" scoped>
@import './src/styles/theme.scss';
$itemMaxWidth: var(--itemMaxWidth, calc((100% - 80px) / 3));
.processList {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  .headerDiv {
    min-height: 40px;
    // margin-top: 10px;
    // background: $centerTtlBgClr;
    display: flex;
    justify-content: space-between;
    align-items: center;
    .btndiv {
      // width: 240px;
      min-height: 40px;
      display: flex;
      align-items: center;
      .chgView {
        margin-left: 10px;
      }
    }
    .filterdiv {
      display: flex;
      justify-content: space-around;
      .el-form-item--mini.el-form-item,
      .el-form-item--small.el-form-item {
        margin-bottom: 0;
      }
      /deep/ .el-form-item__label {
        color: $formLabelClr;
      }
      /deep/ .el-input__inner {
        background: $inputBgClr;
        .el-range-input {
          background: transparent;
          color: #fff;
        }
        .el-range-separator {
          color: #ccc;
        }
      }
    }
  }
  .listDiv {
    flex: 1;
    height: 100%;
    overflow: hidden;
    overflow-y: auto;
    margin: 0 0 5px 0;
    > div {
      width: 100%;
      .success {
        color: #2b9f57;
      }
      .error {
        color: #ff7059;
      }
      .info {
        color: #a2a47a;
      }
      .btn .el-button {
        font-size: 14px;
      }
      .btn .el-button:first-child {
        color: #fefa8e;
      }
      .btn .el-button:last-child {
        color: #e96a54;
      }
    }
    .imageType {
      display: flex;
      justify-content: space-between;
      flex-wrap: wrap;
      .itemdiv {
        width: calc(50% - 5px);
        font-size: 12px;
        background: $cardContBgClr;
        margin-bottom: 10px;
        .imgdiv {
          width: 100%;
          height: 300px;
          img {
            width: 100%;
            height: 100%;
          }
        }
        .infodiv {
          width: 100%;
          display: flex;
          flex-wrap: wrap;
          padding: 8px;
          .item {
            width: 50%;
            line-height: 20px;
            margin: 8px 0;
            display: flex;
            font-size: 12px;
            div.name {
              width: 84px;
              text-align: right;
              display: flex;
              > p {
                flex: 1;
                display: flex;
                justify-content: space-between;
              }
            }
            div.cont {
              flex: 1;
            }
          }
          .allWidth {
            width: 100%;
          }
        }
        .btndiv {
          width: 100%;
          height: 32px;
          display: flex;
          justify-content: flex-end;
          align-items: center;
          padding: 5px 10px;
          background: $cardTtlBgClr;
        }
      }
    }
    .cardType {
      display: flex;
      flex-wrap: wrap;
      justify-content: space-between;
      > div.tips {
        width: 100%;
        height: 100px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 16px;
        color: #999;
        p {
          margin-left: 5px;
        }
      }
      .itemdiv {
        min-width: 310px;
        flex: 1;
        max-width: $itemMaxWidth;
        height: 170px;
        display: flex;
        flex-direction: column;
        margin-bottom: 10px;
        font-size: 12px;
        color: $normalWordsClr;
        .headTitle {
          width: 100%;
          height: 32px;
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 5px 10px;
          background: #2c5686;
        }
        .contdiv {
          flex: 1;
          background: #557fb1;
          overflow: hidden;
          overflow-y: auto;
          padding: 8px;
          display: flex;
          .imgdiv {
            width: 100px;
            height: 100%;
            img {
              width: 100%;
              height: 100%;
            }
          }
          .infodiv {
            flex: 1;
          }
          .item {
            width: 100%;
            line-height: 20px;
            margin: 8px 0;
            display: flex;
            font-size: 12px;
            div.name {
              width: 84px;
              text-align: right;
              display: flex;
              > p {
                flex: 1;
                display: flex;
                justify-content: space-between;
              }
            }
            div.cont {
              flex: 1;
            }
          }
        }
      }
    }
    .tableType {
      height: 100%;
      .describe {
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
  }
  .pageSize {
    width: 100%;
    height: 40px;
  }
  // /deep/ .el-dialog__header {
  //   border-bottom: 1px solid #394e39;
  // }
  /deep/ .el-form {
    // .el-form-item__label {
    //   color: $normalWordsClr;
    // }
    .el-select {
      width: 100%;
    }
  }
}
.newProcessDialog /deep/ .el-dialog {
  height: 80%;
  margin-top: 5% !important;
  .el-dialog__body {
    height: calc(100% - 115px);
    overflow: hidden;
    overflow-y: auto;
    .el-select {
      width: 100%;
    }
  }
}
.leftdiv {
  margin-left: 10px;
}
</style>
