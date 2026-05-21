<template>
  <div class="planList">
    <!-- 
      start
      页面头部区域 
      包含列表操作按钮区和列表筛选区
    -->
    <div class="headerDiv">
      <!-- 
        start
        包含列表操作按钮区
      -->
      <div class="btndiv">
        <el-button icon="el-icon-connection" type="primary" size="mini" @click="newProcess">新建方案</el-button>
        <el-button icon="el-icon-menu" type="primary" size="mini" @click="chgView">切换视图</el-button>
        <!-- <el-button icon="el-icon-notebook-2" size="mini" @click="multipleSetInput">批量输入</el-button> -->
        <!-- <el-button icon="el-icon-notebook-2" size="mini" @click="multiplePerform">批量执行</el-button> -->
      </div>
      <!-- end -->
      <!-- 
        start
        列表筛选区
      -->
      <div class="filterdiv">
        <el-form ref="form" :model="filterForm" label-width="80px" size="mini" :inline="true">
          <el-form-item label="关键字">
            <el-input v-model="filterForm.searchVal" placeholder="请输入检索关键字"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchPlanList">查询</el-button>
            <el-button type="info" @click="cancelSearchPlanList">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      <!-- end -->
    </div>
    <!-- end -->
    <!-- 
      start
      方案列表展示区域
      分为卡片、列表两种展示类型
    -->
    <div class="listDiv">
      <!-- 
        start
        卡片类型列表展示区域
      -->
      <div class="cardType" v-if="viewType">
        <div class="itemdiv" v-for="(item, index) in procListData" :key="index">
          <div class="headTitle">
            <p class="title">{{item.name}}</p>
            <div class="btn">
              <el-tooltip
                class="item"
                effect="dark"
                content="编辑信息"
                placement="top"
                v-show="item.status!=='执行'"
              >
                <el-button type="text" icon="el-icon-edit-outline" @click="editProc(item, index,1)"></el-button>
              </el-tooltip>
              <el-tooltip
                class="item"
                effect="dark"
                :content="`${item.status==='执行'?'停止':'执行'}`"
                placement="top"
              >
                <el-button
                  size="mini"
                  type="text"
                  icon="el-icon-help"
                  @click="runStop(item, index,'')"
                ></el-button>
              </el-tooltip>
              <el-tooltip
                class="item"
                effect="dark"
                content="添加到案例库"
                placement="top"
                v-if="item.statusInfo==='已执行'"
              >
                <el-button
                  size="mini"
                  type="text"
                  icon="el-icon-guide"
                  @click="addToCaseBank(item, index)"
                ></el-button>
              </el-tooltip>
              <el-tooltip
                class="item"
                effect="dark"
                content="查看结果"
                placement="top"
                v-if="item.statusInfo==='已执行'"
              >
                <el-button
                  size="mini"
                  type="text"
                  icon="el-icon-view"
                  @click="viewResult(item, index)"
                ></el-button>
              </el-tooltip>
              <el-tooltip
                class="item"
                effect="dark"
                content="删除"
                placement="top"
                v-show="item.status!=='执行'"
              >
                <el-button type="text" icon="el-icon-delete" @click="removeProc(item)"></el-button>
              </el-tooltip>
            </div>
          </div>
          <div class="contdiv">
            <div class="item" v-for="(it, idx) in procInforLabel" :key="idx" v-show="idx">
              <div class="name">{{it.label}}：</div>
              <div class="cont">
                <p>{{item[it.prop]}}</p>
              </div>
            </div>
            <!-- <div class="item">
              <div class="name">执行进度：</div>
              <div class="cont">
                <el-progress
                  :percentage="item.progress"
                  :stroke-width="12"
                  style="margin-top: 3px;"
                ></el-progress>
              </div>
            </div>-->
          </div>
        </div>
      </div>
      <!-- end -->
      <!-- 
        start
        列表类型列表展示区域
      -->
      <div class="tableType" v-else>
        <el-table
          ref="planList"
          :data="procListData"
          @selection-change="handleSelectionChange"
          style="width: 100%;height: 100%"
        >
          <!-- <el-table-column type="selection"></el-table-column> -->
          <el-table-column
            v-for="(item, index) in procInforLabel"
            :key="index"
            :prop="item.prop"
            :label="item.label"
            :width="item.width"
          ></el-table-column>
          <el-table-column label="操作" width="230">
            <template slot-scope="scope">
              <div class="btn">
                <el-tooltip
                  class="item"
                  effect="dark"
                  content="编辑信息"
                  placement="top"
                  v-show="scope.row.statusInfo!=='执行中'"
                >
                  <!-- icon="el-icon-edit-outline" -->
                  <el-button size="mini" type="text" @click="editProc(scope.row, scope.$index,1)">修改</el-button>
                </el-tooltip>
                <!-- <el-tooltip
                  class="item"
                  effect="dark"
                  content="编辑方案"
                  placement="top"
                  v-show="scope.row.statusInfo!=='执行中'"
                >
                  <el-button
                    size="mini"
                    type="text"
                    @click="editProcCont(scope.row, scope.$index)"
                  >绘制</el-button>
                </el-tooltip>-->
                <el-tooltip
                  class="item"
                  effect="dark"
                  :content="scope.row.statusInfo==='执行中'?'停止':'执行'"
                  placement="top"
                >
                  <!-- icon="el-icon-help" -->
                  <el-button
                    size="mini"
                    type="text"
                    @click="runStop(scope.row, scope.$index,'')"
                  >{{scope.row.statusInfo==='执行中'?'停止':'执行'}}</el-button>
                </el-tooltip>
                <el-tooltip
                  class="item"
                  effect="dark"
                  content="添加到案例库"
                  placement="top"
                  v-if="scope.row.statusInfo==='已执行'"
                >
                  <el-button
                    size="mini"
                    type="text"
                    @click="addToCaseBank(scope.row, scope.$index)"
                  >添加</el-button>
                </el-tooltip>
                <el-tooltip
                  v-if="scope.row.statusInfo==='已执行'"
                  class="item"
                  effect="dark"
                  content="查看结果"
                  placement="top"
                >
                  <el-button
                    size="mini"
                    type="text"
                    @click="viewResult(scope.row, scope.$index)"
                  >查看结果</el-button>
                </el-tooltip>
                <el-tooltip
                  class="item"
                  effect="dark"
                  content="删除"
                  placement="top"
                  v-show="scope.row.statusInfo!=='执行中'"
                >
                  <!-- icon="el-icon-delete" -->
                  <el-button size="mini" type="text" @click="removeProc(scope.row)">删除</el-button>
                </el-tooltip>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <!-- end -->
    </div>
    <!-- end -->
    <!-- 
      start
      分页组件区域
    -->
    <div class="pageSize">
      <BasePagination :data="page" @current="currentPage" @sizes="pageSizes"></BasePagination>
    </div>
    <!-- end -->
    <!-- 
      start
      方案新增、修改、执行功能操作区域
      以弹框形式展示
    -->
    <el-dialog
      :title="FAtitle"
      :show-close="false"
      :visible.sync="procDialog.visible"
      width="70%"
      class="procDialog"
    >
      <div class="content">
        <!-- 
          start
          方案基本信息配置区域
        -->
        <el-form
          ref="procDialog"
          :inline="true"
          :model="formList"
          label-width="80px"
          size="small"
          :rules="rules"
        >
          <el-form-item label="方案名称" prop="name">
            <el-input v-model="formList.name" :disabled="showCase"></el-input>
          </el-form-item>
          <!-- <el-form-item label="数据范围" prop="dataRange">
            <el-input v-model="formList.dataRange" :disabled="showCase"></el-input>
          </el-form-item>-->
          <el-form-item label="方案分组" prop="groupId">
            <el-cascader
              v-model="formList.groupId"
              :options="treeData"
              :props="{label: 'name', value: 'id'}"
              filterable
              clearable
              :disabled="showCase"
            ></el-cascader>
          </el-form-item>
          <el-form-item label="批次号" prop="batchNumber" v-if="showCase==true">
            <el-select v-model="formList.batchNumber">
              <el-option
                :label="item.NAME"
                :value="item.VALUE"
                v-for="(item, index) in batchNumberList"
                :key="index"
              ></el-option>
            </el-select>
          </el-form-item>
          <!-- <el-form-item label="描述">
            <el-input type="textarea" v-model="formList.description"></el-input>
          </el-form-item>-->
          <!-- <el-form-item>
            <el-button size="mini" type="success" @click="procListDialog.visible = true">添加流程</el-button>
          </el-form-item>-->
        </el-form>
        <!-- end -->
        <!-- 
          start
          数据引接配置区域
        -->
        <div class="btndiv">
          <el-dropdown @command="selectTable" v-if="showCase==false">
            <el-button size="mini" class="btn" type="primary" style="margin-bottom:10px">
              添加引接
              <i class="el-icon-arrow-down el-icon--right"></i>
            </el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item
                class="withDrop"
                v-for="item in options"
                :key="item.type"
                :label="item.label"
                :value="item.type"
                :command="item.type"
              >{{item.label}}</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
          <div class="chsdProcList">
            <jyBigTale :dataList="yjdata" :showCase="showCase"></jyBigTale>
          </div>
        </div>
        <!-- end -->
        <!-- 
          start
          数据处理流程配置区域
        -->
        <div>
          <el-button
            size="mini"
            type="primary"
            @click="showProcListDialog()"
            v-if="showCase==false"
            class="btn"
            style="margin-bottom:10px"
          >添加流程</el-button>
          <div class="chsdProcList">
            <ChsdProcList
              :dataList="chsdProcList"
              :showCase="showCase"
              @removeChsdProc="removeChsdProc"
            ></ChsdProcList>
          </div>
        </div>
        <!-- end -->
      </div>
      <!-- 
        start
        方案配置弹框操作按钮区域
      -->
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="saveprocDialog" v-if="showCase==false">确 定</el-button>
        <el-button type="primary" @click="runStopDialog" v-if="showCase==true">执行</el-button>
        <el-button type="info" @click="cancelProcDialog">取 消</el-button>
      </span>
      <!-- end -->
      <!-- 
        start
        弹框边角样式
      -->
      <!-- end -->
    </el-dialog>
    <!-- end -->
    <!-- 
      start
      方案配置-配置流程-流程列表
      弹框展示
    -->
    <el-dialog title="流程列表" :visible.sync="procListDialog.visible" width="1300px" class="procList">
      <div class="content">
        <!-- 
          start
          流程列表筛选区域
        -->
        <div class="filterdiv">
          <el-form
            ref="form"
            :model="procListDialog.filterForm"
            label-width="80px"
            size="mini"
            :inline="true"
          >
            <el-form-item label="创建时间">
              <el-col :span="11">
                <el-date-picker
                  v-model="procListDialog.filterForm.createTime"
                  value-format="yyyy-MM-dd HH:mm:ss"
                  type="datetimerange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                ></el-date-picker>
              </el-col>
            </el-form-item>
            <el-form-item label="关键字">
              <el-input v-model="procListDialog.filterForm.searchVal" placeholder="请输入检索关键字"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="getTtlProcList">查询</el-button>
              <el-button type="info" @click="cancelSearch">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
        <!-- end -->
        <!-- 
          start
          流程列表区域
        -->
        <div class="listdiv">
          <el-table
            ref="procListData"
            :data="procListDialog.procListData"
            @selection-change="chsdProc"
            style="width: 100%"
          >
            <el-table-column type="selection"></el-table-column>
            <el-table-column type="index"></el-table-column>
            <el-table-column
              v-for="(item, index) in procListDialog.procInforLabel"
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
                <p v-else>{{scope.row[item.prop]}}</p>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <!-- end -->
        <!-- 
          start
          流程列表分页区域
        -->
        <div class="pageSize">
          <BasePagination
            :data="procListDialog.page"
            @current="currentProcPage"
            @sizes="pageProcSizes"
          ></BasePagination>
        </div>
        <!-- end -->
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="saveProcList">确 定</el-button>
        <el-button type="info" @click="cancelProcList">取 消</el-button>
      </span>
    </el-dialog>
    <!-- end -->
    <el-dialog title="批量输入" :visible.sync="inputDialog.visible" width="40%">
      <div class="content multipleInput">
        <div class="chsdListdiv">
          <h3>已选方案</h3>
          <div class="contdiv">
            <div class="item" v-for="(item, index) in chsdItemTtlList" :key="index">
              <p>{{item.name}}</p>
              <p class="close" @click="deleteChsdItem(index)">
                <i class="el-icon-close"></i>
              </p>
            </div>
          </div>
        </div>
        <div class="inputdiv">
          <el-form ref="form" :model="inputDialog.data" label-width="80px" size="small">
            <el-form-item label="输入" prop="insert">
              <el-cascader
                v-model="inputDialog.data.inputType"
                :options="inputOptions"
                :props="{label: 'categoriesName', value: 'id'}"
                filterable
                clearable
              ></el-cascader>
            </el-form-item>
          </el-form>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="closeIptDialog">确 定</el-button>
        <el-button type="info" @click="initIptDialog">取 消</el-button>
      </span>
    </el-dialog>
    <el-dialog
      title="批量执行"
      :visible.sync="performDialog.visible"
      width="70%"
      class="multiplePerform"
    >
      <div class="content multiplePerform">
        <div class="chsdListdiv">
          <h3>已选方案</h3>
          <div class="contdiv">
            <ChsdProcList :dataList="chsdItemTtlList"></ChsdProcList>
          </div>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="closePerformDialog">执 行</el-button>
        <el-button type="info" @click="initPerformDialog">取 消</el-button>
      </span>
    </el-dialog>

    <!-- 数据预览弹框 -->
    <view-deal-result v-if="showViewDealResult" :planId="planId" @closeDialog="closeViewDealResult"></view-deal-result>
    <!-- 添加引接的弹窗 -->
    <yjTable v-if="showYj" @close="showYj=false" :yjType="yjType" @yjData="yjData"></yjTable>
    <!-- 选择案例库分类 -->
    <choose-case-bank
      v-if="ccbDialogConfig.visible"
      :ccbDialogConfig="ccbDialogConfig"
      @closeDialog="closeCCBDialog"
    ></choose-case-bank>
  </div>
</template>
<script>
/**
 * Create by Leo
 * 包含方案列表展示、列表筛选、方案新增、方案修改、方案执行、查看执行结果等功能
 * 列表包括卡片和列表两种展示形式
 */
import BasePagination from '@/components/BasePagination';
import BaseTable from '@/components/BaseTable/index.vue';
import ViewDealResult from './ViewDealResult.vue';
import yjTable from './yjTable';
import ChsdProcList from './ChsdProcList';
import jyBigTale from './jyBigTale';
import ChooseCaseBank from './chooseCaseBank';
import API from '@/api/index';
import { procInforLabel, options } from './config.js';
import http from '@/api/base/http';
export default {
  name: 'planList',
  components: {
    BasePagination,
    BaseTable,
    ChsdProcList,
    yjTable,
    jyBigTale,
    ChooseCaseBank,
    ViewDealResult
  },
  // 方案管理的tree(方案分组)
  props: ['treeData'],
  data() {
    return {
      /**
       * 当前方案id值
       */
      planId: '',
      /**
       * 是否展示方案执行结果查看弹框
       */
      showViewDealResult: false,
      /**
       * 批次号列表
       */
      batchNumberList: [],
      /**
       * 案例库配置弹框
       */
      ccbDialogConfig: {
        title: '选择案例库分类',
        visible: false,
        dataInfo: {
          planId: ''
        }
      },
      /**
       * 方案标题
       */
      FAtitle: '',
      /**
       * 引接数据
       */
      allYjdata: [],
      yjdata: [], //引接选择的数据
      yjType: '', //引接的类型选择
      /**
       * 是否展示引接
       */
      showYj: false,
      /**
       * 引接值
       */
      yjValue: '', //引接
      /**
       * 引接的类型
       */
      options: options,
      /**
       * 是否展示进度
       */
      isShowProgress: false,
      /**
       * 执行结果
       */
      runResult: true,
      /**
       * 筛选表单
       */
      filterForm: {
        searchVal: ''
      },
      /**
       * 分页配置信息
       */
      page: {
        pageSize: 20,
        pageNum: 1,
        sizes: [10, 20, 30, 40, 50, 100],
        total: 10
      },
      /**
       * 展示类型
       */
      viewType: 0,
      /**
       * 方案列表
       */
      procListData: [],
      /**
       * 配置项信息
       */
      procInforLabel,
      /**
       * 是否展示操作按钮
       */
      isShowOperation: true,
      /**
       * 方案配置弹框信息
       */
      procDialog: {
        isNew: true,
        visible: false,
        data: {
          name: '',
          dataRange: '',
          groupId: '',
          batchNumber: ''
        }
      },
      /**
       * 配置项验证
       */
      rules: {
        name: [
          {
            required: true,
            message: '请输入流程名称',
            trigger: 'blur'
          }
        ],
        dataRange: [
          {
            required: true,
            message: '请选择输入',
            trigger: 'blur'
          }
        ],
        groupId: [
          {
            required: true,
            message: '请选择方案分组',
            trigger: 'blur'
          }
        ]
      },
      /**
       * 数据源列表
       */
      dataSourceList: [],
      /**
       * 编辑序号
       */
      editProcIndex: null,
      /**
       * 处理方式列表
       */
      dealTypeOptions: [{ label: '补全', value: 1 }, { label: '去重', value: 2 }],
      /**
       * 输入选项
       */
      inputOptions: [],
      /**
       * 选择的标题列表
       */
      chsdTtlList: [],
      /**
       * 选择的项目列表
       */
      chsdItemList: [],
      /**
       * 选择的项目标题列表
       */
      chsdItemTtlList: [],
      /**
       * 输入弹框
       */
      inputDialog: {
        visible: false,
        data: {
          inputType: []
        }
      },
      /**
       * 执行弹框配置
       */
      performDialog: {
        visible: false
      },
      /**
       * 流程配置弹框
       */
      procListDialog: {
        visible: false,
        procListData: [],
        procInforLabel: [
          {
            label: '流程名称',
            prop: 'name',
            value: ''
          },
          {
            label: '项目分组名称',
            prop: 'belongToTaskGroup',
            value: ''
          },
          // {
          //   label: '创建人',
          //   prop: 'createUser',
          //   value: ''
          // },
          {
            label: '创建时间',
            prop: 'createDate',
            value: ''
          },
          {
            label: '描述',
            prop: 'describe',
            value: ''
          }
        ],
        filterForm: {
          // groupId: '',
          // name: '',
          createTime: [],
          // status: null,
          searchVal: ''
        },
        page: {
          pageSize: 20,
          pageNum: 1,
          sizes: [10, 20, 30, 40, 50, 100],
          total: 10
        }
      },
      /**
       * 选择的流程列表
       */
      chsdProcList: [],
      /**
       * 选择的流程数据
       */
      chsdProcData: [],
      /**
       * 方案列表参数
       */
      planListParams: {
        groupId: '',
        // groupName: '',
        searchVal: ''
      },
      /**
       * 当前方案信息
       */
      crtPlanInfo: {},
      /**
       * socket信息
       */
      socketUrlInfo: {},
      /**
       * 弹窗内输入的框的编辑和不可编辑
       */
      showCase: false
    };
  },
  computed: {
    formList() {
      return this.procDialog.data;
    }
  },
  created() {
    this.getSocketUrl();
  },
  mounted() {
    this.$eventBus.$on('filterPlanList', (id, name) => {
      this.planListParams.groupId = id || '';
      this.formList.groupId = id || '';
      // this.planListParams.groupName = name;
      this.getProcessData();
    });
    if (sessionStorage.getItem('viewType')) {
      this.viewType = parseInt(sessionStorage.getItem('viewType'));
    }
    // 暂时注释掉了
    // this.getTtlProcList();
    this.setInputOptions();
    this.getProcessData();
  },
  methods: {
    /**
     * 获取批次号列表
     */
    getBatchNumList() {
      this.$API.getBatchNumList().then(({ data }) => {
        this.batchNumberList = data.data;
      });
    },
    /**
     * 关闭弹框
     */
    closeCCBDialog() {
      this.ccbDialogConfig.visible = false;
      // console.log(this.ccbDialogConfig.visible);
    },
    /**
     * 选择引接的table
     * @param dataType
     */
    selectTable(dataType) {
      this.yjType = dataType;
      this.showYj = true;
    },
    /**
     * 获取引接的数据
     * @param data
     */
    yjData(data) {
      data.forEach(ele => {
        if (ele.filePath) {
          let filePahtArr = ele.filePath.split('\\');
          ele.mainBody = filePahtArr[filePahtArr.length - 1];
        } else {
          ele.mainBody = ele.tableName;
        }
        ele.analysisProtocolName = ele.tableConfigName || ele.analysisProtocolName;
      });
      if (this.yjdata.length) {
        let datas = this.yjdata.concat(data);
        this.yjdata = this.newSetArry(datas);
      } else {
        this.yjdata = data;
      }
    },
    /**
     * 查询
     */
    searchPlanList() {
      this.planListParams.searchVal = this.filterForm.searchVal;
      this.page.pageNum = 1;
      this.getProcessData();
    },
    /**
     * 重置
     */
    cancelSearchPlanList() {
      this.planListParams.searchVal = '';
      this.filterForm.searchVal = '';
      this.getProcessData();
    },
    /**
     * 取消
     */
    cancelProcDialog() {
      this.procDialog.visible = false;
      this.procDialog.data = {
        name: '',
        dataRange: '',
        groupId: ''
      };
      // 设置方案分组信息
      this.formList.groupId = this.planListParams.groupId;
      // this.chsdProcList = [];
      this.$refs['procDialog'].clearValidate();
    },
    /**
     * 添加流程
     */
    showProcListDialog() {
      this.getTtlProcList();
      this.procListDialog.visible = true;
      this.$nextTick(() => {
        this.$refs.procListData.clearSelection();
        if (!this.chsdProcData.length) {
          this.chsdProcData = this.chsdProcList;
        }
        this.chsdProcData.forEach(row => {
          this.$refs.procListData.toggleRowSelection(row, true);
        });
      });
    },
    /**
     * 删除选择的流程
     * @param index
     */
    removeChsdProc(index) {
      this.chsdProcData.splice(index, 1);
    },
    /**
     * 确定保存
     */
    saveprocDialog() {
      this.$refs['procDialog'].validate(valid => {
        if (valid) {
          // let { name, dataRange, groupId, id } = this.procDialog.data;
          // 判断是否有选择流程
          if (this.chsdProcList.length !== 0 || this.yjdata.length !== 0) {
            this.chsdProcList.forEach(item => {
              item['groupName'] = item.belongToTaskGroup;
              item['type'] = 5;
              item['remark'] = item.describe;
              item['souceId'] = item.transformationId;
              item['kettleName'] = item.url;
            });
            this.yjdata.forEach(item => {
              if (item.hasOwnProperty('analysisProtocolName')) {
                item['agreementName'] = item.analysisProtocolName;
              } else {
                item['agreementName'] = '';
              }
              item['souceId'] = item['souceId'] || item.id;
            });
            let params = {};
            if (this.procDialog.data.id == undefined) {
              params = {
                id: '',
                name: this.procDialog.data.name,
                groupId: Array.isArray(this.procDialog.data.groupId) ? this.procDialog.data.groupId[0] : this.procDialog.data.groupId,
                clList: this.chsdProcList,
                yjList: this.yjdata
              };
            } else {
              params = {
                id: this.procDialog.data.id,
                name: this.procDialog.data.name,
                groupId: Array.isArray(this.procDialog.data.groupId) ? this.procDialog.data.groupId[0] : this.procDialog.data.groupId,
                clList: this.chsdProcList,
                yjList: this.yjdata
              };
            }
            // let pathArr = this.chsdProcList.map(item => {
            //   return item.url;
            // });
            // console.log(this.chsdProcList);
            // let idArr = this.chsdProcList.map(item => {
            //   return item.transformationId;
            // });
            // if (Array.isArray(groupId)) {
            //   groupId = '' + groupId[groupId.length - 1];
            // }
            // let params = {
            //   planName: name,
            //   dataRange,
            //   groupId,
            //   transId: this.chsdProcList[0].transformationId,
            //   path: pathArr.join(',')
            // };
            // if (id) params.id = id;
            // const formData = new FormData();
            // // 参数转formData
            // const formParams = Object.keys(params).map(key => {
            //   formData.append(key, params[key]);
            // });
            this.$API.newPlan(params).then(({ data }) => {
              const { code } = data;
              if (code === 200) {
                this.$message.success('保存成功');
                this.cancelProcDialog();
                this.getProcessData();
              }
            });
          } else {
            this.$message.success('请添加流程');
          }
        } else {
          return false;
        }
      });
    },
    /**
     *  回显数据信息
     * @param options
     * @param yjType
     */
    datashow(options, yjType) {
      let actions = [];
      Object.keys(options).some(key => {
        if (parseInt(key) + 1 == yjType) {
          actions.push(options[key].label);
          return true;
        }
      });
      return actions.join('');
    },
    /**
     * 编辑
     * @param scope
     * @param index
     * @param a
     */
    editProc(scope, index, a) {
      this.FAtitle = '修改方案';
      this.procDialog.isNew = false;
      this.procDialog.data = { ...scope };
      // let dataProcDialog = {};
      a == 1 ? (this.showCase = false) : (this.showCase = true);
      this.$API.getTransByPlanId({ transPlanId: scope.id }).then(res => {
        if (res.data.success) {
          let data = res.data.data;
          if (data !== null) {
            for (let pr in this.procDialog.data) {
              this.procDialog.data[pr] = data[pr];
            }
            // this.chsdProcList = [];
            if (data.clList.length !== 0) {
              data.clList.forEach(item => {
                item['belongToTaskGroup'] = item.groupName;
                item['url'] = item.kettleName;
                item['transformationId'] = item.souceId;
                item['describe'] = item.remark;
              });
            }
            if (data.yjList.length !== 0) {
              data.yjList.forEach(item => {
                item['analysisProtocolName'] = item.agreementName;
                item['valueType'] = this.datashow(this.options, item.type);
              });
            }
            this.yjdata = data.yjList;
            this.chsdProcList = data.clList;
            // data.children.forEach(item => {
            //   let proc = this.procListDialog.procListData.find(ele => ele.transformationId.toString() === item.transId.toString());
            //   proc && this.chsdProcList.push(proc);
            // });
          }
        }
      });
      /*去除 if (!this.dataSourceList.length) {
        this.initDataSource();
      } */
      this.procDialog.visible = true;
    },
    /**
     * 保存流程列表
     */
    saveProcList() {
      this.addChsdProcList();
      this.procListDialog.visible = false;
    },
    /**
     * 选择流程
     * @param val
     */
    chsdProc(val) {
      this.chsdProcData = val;
    },
    /**
     * 数组对象去重
     * @param array
     */
    newSetArry(array) {
      let map = new Map();
      for (let index in array) {
        map.set(array[index].id, array[index]);
      }
      let result = [];
      map.forEach(value => {
        result.push(value);
      });
      return result;
    },
    /**
     * 添加数据流程
     */
    addChsdProcList() {
      if (this.chsdProcList.length) {
        this.chsdProcList = this.chsdProcData.concat(this.chsdProcList);
        this.chsdProcList = this.newSetArry(this.chsdProcList);
      } else {
        this.chsdProcList = this.chsdProcData;
      }
      // return;
      // let arr = this.chsdProcList.map(item => {
      //   return JSON.stringify(item);
      // });
      // this.chsdProcList = [...new Set(arr)].map(item => {
      //   return JSON.parse(item);
      // });
    },
    /**
     * 获取全部流程
     * @param paramsData
     */
    getTotal(paramsData) {
      this.$API.getTotalProcList(paramsData).then(({ data }) => {
        const { code } = data;
        if (code === 200) {
          const { data: listObj } = data;
          const { records, total } = listObj;
          this.procListDialog.procListData = [...records];
          this.procListDialog.page.total = total;
        }
      });
    },
    /**
     * 获取全部流程
     */
    getTtlProcList() {
      let params = {
        startDate: this.procListDialog.filterForm.createTime[0] ? this.procListDialog.filterForm.createTime[0] : '',
        endDate: this.procListDialog.filterForm.createTime[1] ? this.procListDialog.filterForm.createTime[1] : '',
        name: this.procListDialog.filterForm.searchVal ? this.procListDialog.filterForm.searchVal : '',
        pageSize: this.procListDialog.page.pageSize,
        pageIndex: this.procListDialog.page.pageNum
      };
      this.getTotal(params);
    },
    /**
     * 修改分页
     * @param e
     */
    currentProcPage(e) {
      this.procListDialog.page.pageNum = e;
      this.addChsdProcList();
      this.getTtlProcList();
    },
    /**
     * 修改分页
     * @param e
     */
    pageProcSizes(e) {
      this.procListDialog.page.pageSize = e;
      this.getTtlProcList();
    },
    /**
     * 隐藏流程列表
     */
    cancelProcList() {
      this.procListDialog.visible = false;
    },
    /**
     * 删除所选流程项目
     * @param index
     */
    deleteChsdItem(index) {
      let item = this.chsdItemList[index];
      this.$refs.planList.toggleRowSelection(item, false);
      this.chsdItemTtlList.splice(index, 1);
    },
    /**
     * 控制列表勾选项
     * @param val
     */
    handleSelectionChange(val) {
      this.chsdItemList = val;
    },
    /**
     * 多选输入
     */
    multipleSetInput() {
      this.setChsdTtlList();
      if (this.chsdItemTtlList.length) {
        this.setInputOptions();
        this.inputDialog.visible = true;
      } else {
        this.$message.warning('请先选择需要配置的方案！');
      }
    },
    /**
     * 多选执行
     */
    multiplePerform() {
      this.setChsdTtlList();
      if (this.chsdItemTtlList.length) {
        this.performDialog.visible = true;
      } else {
        this.$message.warning('请先选择需要配置的方案！');
      }
    },
    /**
     * 设置选择列表
     */
    setChsdTtlList() {
      this.chsdTtlList = this.chsdTtlList.concat(this.chsdItemList);
      let arr = this.chsdTtlList.map(item => {
        return JSON.stringify(item);
      });
      this.chsdItemTtlList = [...new Set(arr)].map(item => {
        return JSON.parse(item);
      });
    },
    /**
     * 编辑流程内容
     */
    editProcCont(scope, index) {
      sessionStorage.setItem('backPage', '/PlanManage');
      this.$router.push({
        path: '/flowChart'
        // query: {
        //     path: scope.row.directoryName,
        //     transName: scope.row.name,
        //     taskGroupName: scope.row.belongToTaskGroup,
        //     transformationId: scope.row.transformationId
        // }
      });
      // this.$eventBus.$emit('setNavIndex', 0);
    },
    /**
     * 执行长连接
     */
    getSocketUrl() {
      const self = this;
      http.get('/api/trans/plan/websocketUrl').then(res => {
        if (res.data.code == 200) {
          this.socketUrlInfo = res.data.data;
          let ws = new WebSocket(res.data.data.url);
          ws.onopen = function() {
            ws.send('打开SOCKET通道');
          };
          ws.onmessage = function(evt) {
            evt && self.handleMessage(evt);
          };
          ws.onclose = function() {
            console.log('关闭执行状态数据连接。');
          };
        }
      });
    },
    // 执行
    runStop(scope, index, a) {
      this.getBatchNumList();
      this.editProc(scope, index, a);
      this.FAtitle = '执行方案';
    },
    // 执行进度的随随机数
    runStopDialog() {
      if (!this.formList.batchNumber) {
        this.$utils.setMessage(false, '请选择批次号');
        return;
      }
      let params = {
        id: this.procDialog.data.id,
        requestId: this.socketUrlInfo.requestId,
        batchNumber: this.formList.batchNumber
      };
      this.$API.runPlan(params).then(({ data }) => {
        let index = 0;
        new Promise(resolve => {
          if (this.yjdata.length !== 0) {
            this.runProc(index, resolve, this.yjdata);
          } else {
            this.runProc(index, resolve, this.chsdProcList);
          }
        })
          .then(() => {
            return new Promise(resolve => {
              if (this.chsdProcList.length !== 0 && this.yjdata.length !== 0) {
                this.runProc(index, resolve, this.chsdProcList);
              } else {
                resolve();
              }
            });
          })
          .catch(reason => {
            console.log(reason);
          });
      });
    },
    /**
     * 执行流程
     * @param index
     * @param resolve
     * @param aloneData
     */
    runProc(index, resolve, aloneData) {
      let procItem = aloneData[index];
      procItem['progress'] = 0;
      let addTime = setInterval(() => {
        let add = Math.floor(Math.random() * 20);
        procItem.progress += add;
        this.$set(aloneData, index, procItem);
        if (procItem.progress >= 100) {
          procItem.progress = 100;
          clearInterval(addTime);
          if (++index < aloneData.length) {
            this.runProc(index, resolve, aloneData);
          } else {
            resolve();
          }
        }
      }, 200);
    },
    /**
     * 消息展示
     * @param wsInfo
     */
    handleMessage(wsInfo) {
      const data = JSON.parse(wsInfo.data);
      if (data.status != 'open') {
        new Promise(resolve => {
          let getNum = setInterval(() => {
            if (this.yjdata.length !== 0 && this.chsdProcList.length !== 0) {
              if (
                this.chsdProcList[this.chsdProcList.length - 1] &&
                this.chsdProcList[this.chsdProcList.length - 1].progress &&
                this.chsdProcList[this.chsdProcList.length - 1].progress === 100 &&
                this.yjdata[this.yjdata.length - 1] &&
                this.yjdata[this.yjdata.length - 1].progress &&
                this.yjdata[this.yjdata.length - 1].progress === 100
              ) {
                clearInterval(getNum);
                resolve(data);
              }
            } else if (this.chsdProcList.length !== 0) {
              if (
                this.chsdProcList[this.chsdProcList.length - 1] &&
                this.chsdProcList[this.chsdProcList.length - 1].progress &&
                this.chsdProcList[this.chsdProcList.length - 1].progress === 100
              ) {
                clearInterval(getNum);
                resolve(data);
              }
            } else {
              if (this.yjdata[this.yjdata.length - 1] && this.yjdata[this.yjdata.length - 1].progress && this.yjdata[this.yjdata.length - 1].progress === 100) {
                clearInterval(getNum);
                resolve(data);
              }
            }
          }, 200);
        }).then(data => {
          if (data.status == 'success') {
            this.$message.success(data.msg || '执行成功');
          } else {
            this.$message.warning(data.msg || '执行失败');
            this.chsdProcList[this.chsdProcList.length - 1].progress = 0;
          }
          this.getProcessData();
          this.cancelProcDialog();
          this.crtPlanInfo = {};
        });
      }
    },
    /**
     * 查看数据
     * @param scope
     */
    viewResult(scope) {
      this.planId = scope.id;
      this.showViewDealResult = true;
    },
    /**
     * 关闭方案执行结果查看弹框
     * 并将planId置空
     */
    closeViewDealResult() {
      this.showViewDealResult = false;
      this.planId = '';
    },
    /**
     * 添加至案例库
     * @param scope
     */
    addToCaseBank(scope) {
      this.ccbDialogConfig.dataInfo.planId = scope.id;
      this.ccbDialogConfig.visible = true;
    },
    /**
     * 关闭流程弹框
     * @param formName
     */
    closeprocDialog(formName) {
      this.$refs['form'].validate(valid => {});
      if (this.procDialog.isNew) {
        let nowTime = new Date();
        let date = nowTime.toLocaleDateString();
        let newDate = date.split('/').join('-');
        let time = nowTime.toTimeString();
        let patt = /\d{2}:\d{2}:\d{2}/g;
        let newTime = patt.exec(time).toString();
        this.procDialog.data.createTime = newDate + ' ' + newTime;
        this.procDialog.data.createBy = '张海迪';
        this.procListData.push(this.procDialog.data);
      } else {
        Object.assign(this.procListData[this.editProcIndex], this.procDialog.data);
      }
      this.initProcDialog();
    },
    /**
     * 初始化流程弹框
     */
    initProcDialog() {
      this.procDialog.visible = false;
      this.procDialog.data = {
        name: '',
        description: '',
        dataSource: '',
        createBy: '',
        createTime: '',
        status: 0
      };
    },
    /**
     * 新建方案
     */
    newProcess() {
      this.FAtitle = '新建方案';
      // this.formList.groupId = '';
      this.showCase = false;
      // if (!this.dataSourceList.length) {
      //   this.initDataSource();
      // }
      // this.initDataSource();
      this.chsdProcList = [];
      this.yjdata = [];
      this.procDialog.visible = true;
    },
    /* initDataSource() {
      // this.$API.getProcDataList().then(res => {
      //   this.dataSourceList = res.data.data;
      // });
    }, */
    /**
     * 修改展示方式
     */
    chgView() {
      this.viewType = this.viewType ? 0 : 1;
      sessionStorage.setItem('viewType', this.viewType);
    },
    /**
     * 移除流程列表
     * @param row
     */
    removeProc(row) {
      this.$confirm('此操作将删除该方案, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        center: true
      })
        .then(() => {
          API.getCreatePlanRemove({
            id: row.id
          }).then(res => {
            this.$message({
              type: 'success',
              message: '删除成功!'
            });
            this.getProcessData();
          });
        })
        .catch(() => {});
    },
    /**
     * 分页配置
     * @param e
     */
    currentPage(e) {
      this.page.pageNum = e;
      this.getProcessData();
      this.setChsdTtlList();
    },
    /**
     * 分页配置
     * @param e
     */
    pageSizes(e) {
      this.page.pageSize = e;
      this.getProcessData();
    },
    /**
     * 获取表格的数据
     */
    getProcessData() {
      let { pageNum: start, pageSize: limit } = this.page;
      this.$API
        .getCreatePlanList({
          pageIndex: start,
          pageSize: limit,
          flag: 0,
          groupId: this.formList.groupId,
          name: this.filterForm.searchVal
        })
        // ...this.procListDialog.filterForm,
        // ...this.formList
        .then(res => {
          // res.data.data.records.forEach(element => {
          //   element.status = element.status == '1' ? '执行' : '未执行';
          // });
          this.procListData = res.data.data.records.map(item => {
            let statusInfo = '';
            let progress = 0;
            switch (item.status) {
              case '1':
                statusInfo = '未执行';
                progress = 0;
                break;
              case '2':
                statusInfo = '已执行';
                progress = 100;
                break;
            }
            return {
              ...item,
              statusInfo,
              progress
            };
          });
          this.page.total = res.data.data.total;
        })
        .catch((this.procListData = []));
    },
    /**
     * 重置筛选
     */
    cancelSearch() {
      this.procListDialog.filterForm.createTime = [];
      this.procListDialog.filterForm.searchVal = '';
      this.getTtlProcList();
    },
    /**
     * 设置输入选项
     */
    setInputOptions() {
      this.$dataCenterService.getTree().then(res => {
        if (res.data.success && Array.isArray(res.data.data)) {
          this.inputOptions = this.cleanData(res.data.data);
        }
      });
    },
    /**
     * 清除数据
     * @param arr
     */
    cleanData(arr) {
      arr.forEach((ele, index) => {
        if (ele.children && ele.children.length === 0) {
          delete ele.children;
        } else {
          ele.children = this.cleanData(ele.children);
        }
      });
      return arr;
    },
    /**
     * 关闭弹框
     */
    closeIptDialog() {
      this.inputDialog.visible = false;
    },
    /**
     * 关闭弹框
     */
    initIptDialog() {
      this.inputDialog.visible = false;
    },
    /**
     * 关闭弹框
     */
    closePerformDialog() {
      this.performDialog.visible = false;
    },
    /**
     * 关闭弹框
     */
    initPerformDialog() {
      this.performDialog.visible = false;
    }
  },
  filters: {
    rtnStatus(val) {
      return val ? '已执行' : '未执行';
    }
  }
};
</script>
<style lang="scss" scoped>
@import './src/styles/theme.scss';
.withDrop {
  width: 100px;
  text-align: center;
}
.planList {
  width: 100%;
  height: 100%;
  // margin-top: 5px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  .headerDiv {
    width: 100%;
    // background: $centerTtlBgClr;
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    .btndiv {
      width: 240px;
      height: 40px;
      display: flex;
      align-items: center;
    }
    .filterdiv {
      display: flex;
      align-items: center;
      .el-form-item--mini.el-form-item,
      .el-form-item--small.el-form-item {
        margin-bottom: 0;
      }
      // ::v-deep .el-form-item__label {
      //   color: #fff;
      // }
      ::v-deep .el-input__inner {
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
    overflow: hidden;
    overflow-y: auto;
    margin: 5px 0;
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
      .btn {
        display: flex;
        justify-content: space-around;
        width: 100%;
      }
      .btn .el-button {
        font-size: 14px;
        &:hover {
          color: #fff;
        }
      }
      // .btn .el-button:first-child {
      //   color: #fefa8e;
      // }
      .btn .el-button:last-child {
        color: #e96a54;
      }
    }
    .cardType {
      display: flex;
      flex-wrap: wrap;
      .itemdiv {
        width: calc((100% - 80px) / 4);
        height: 220px;
        display: flex;
        flex-direction: column;
        margin: 10px;
        font-size: 12px;
        color: $normalWordsClr;
        .headTitle {
          width: 100%;
          height: 32px;
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 5px 10px;
          background: $cardTtlBgClr;
          .btn {
            height: 22px;
            width: auto;
            display: flex;
            align-items: center;
            margin-bottom: 10px;
          }
        }
        .contdiv {
          flex: 1;
          background: $cardContBgClr;
          overflow: hidden;
          overflow-y: auto;
          padding: 5px;
          .item {
            width: 100%;
            line-height: 20px;
            margin: 8px 0;
            display: flex;
            font-size: 12px;
            div.name {
              width: 84px;
              text-align: right;
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
    }
  }
  .pageSize {
    width: 100%;
    height: 40px;
  }
  ::v-deep .el-form {
    // .el-form-item__label {
    //   color: $normalWordsClr;
    // }
    .el-select {
      width: 100%;
    }
  }
  .multipleInput {
    display: flex;
  }
  .content {
    form {
      width: 100%;
    }
    .chsdListdiv {
      width: 200px;
      color: #fff;
      h3 {
        line-height: 30px;
      }
      .contdiv {
        // background: #284838;
        padding: 5px;
        .item {
          width: 100%;
          display: flex;
          justify-content: space-between;
          line-height: 24px;
          .close {
            display: none;
          }
          &:hover {
            background: #456b4b;
            .close {
              display: block;
              cursor: pointer;
            }
          }
        }
      }
    }
    .inputdiv {
      flex: 1;
    }
  }
  .multiplePerform .chsdListdiv {
    width: 100%;
  }
  ::v-deep .procList .el-dialog,
  ::v-deep .procDialog .el-dialog,
  ::v-deep .multiplePerform .el-dialog {
    height: 750px;
    margin-top: 100px !important;
    .el-dialog__body {
      height: calc(100% - 115px);
      overflow: hidden;
      overflow-y: auto;
      .el-input__inner .el-range-input {
        background: transparent;
        color: $normalWordsClr;
      }
    }
  }
  .procDialog {
    .btndiv {
      margin-bottom: 10px;
    }
  }
  // .procList{
  //   ::v-deep .el-table__header thead.has-gutter .el-checkbox{
  //     display: none;
  //   }
  // }
}
#previewResultData {
  ::v-deep .el-dialog {
    height: calc(100% - 10vh);
    .el-dialog__body {
      height: calc(100% - 45px);
      overflow: hidden;
      overflow-y: auto;
      .selectdiv {
        height: 40px;
        display: flex;
        align-items: center;
        color: #fff;
      }
      .tablediv {
        height: calc(100% - 40px);
      }
    }
  }
}
</style>
