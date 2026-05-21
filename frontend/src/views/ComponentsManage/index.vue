<template>
  <div class="componentsManage">
    <zg-content>
      <div slot="slot-form-group" class="btndiv">
        <div class="leftdiv">
          <el-button
            size="mini"
            icon="el-icon-new"
            type="primary"
            @click="
              appendCatalogueVisible = true;
              $set(catalogueData, 'catalogueId', null);
              $set(catalogueData, 'isEditing', null);
            "
            v-if="showCompHandle"
          >
            <span>新增目录</span>
          </el-button>
          <el-button
            size="mini"
            type="primary"
            @click="
              appendComponentVisible = true;
              $set(componentData, 'catalogueId', null);
              $set(componentData, 'isEditing', null);
            "
            v-if="showCompHandle"
          >
            <span>新增组件</span>
          </el-button>
          <el-button
            size="mini"
            type="success"
            @click="downloadExample"
            v-if="showCompHandle"
          >
            <span>示例算子文件</span>
          </el-button>
        </div>
        <div class="rightdiv">
          <el-select
            v-model="queryCount"
            placeholder="查询模式"
            size="mini"
            style="margin-right:10px;width:130px"
            @change="search"
          >
            <el-option label="不统计数量" value="false"></el-option>
            <el-option label="统计数量" value="true"></el-option>
          </el-select>
          <!-- <el-select
            v-model="componentStatus"
            placeholder="请选择组件状态"
            @change="search"
            size="mini"
            style="margin-right:10px"
          >
            <el-option
              v-for="item in componentStatusList"
              :key="item.value"
              :value="item.value"
              :label="item.label"
            ></el-option>
          </el-select>-->
          <el-input
            size="mini"
            placeholder="请输入名称"
            v-model="queryRuleForm.name"
            class="input-with-select"
            @keyup.enter="search"
            style="margin-right:10px"
          ></el-input>
          <el-button
            slot="append"
            type="primary"
            icon="el-icon-search"
            size="mini"
            @click="search"
          >查询</el-button>
          <el-button size="mini" type="info" @click="resetForm" class="resetForm">重置</el-button>
        </div>
      </div>
      <div slot="slot-form-list" class="tablearea">
        <el-table row-key="id" :data="tableData" style="width:100%" height="100%">
          <el-table-column type="index" width="80" label="序号" align="center"></el-table-column>
          <el-table-column prop="name" label="名称"></el-table-column>
          <el-table-column label="目录种类">
            <template #default="scope">{{ scope.row.typeMark ? '组件' : '目录' }}</template>
          </el-table-column>
          <el-table-column align="center" label="编码">
            <template
              #default="scope"
            >{{ scope.row.typeMark ? scope.row.id : scope.row.code }}</template>
          </el-table-column>
          <el-table-column prop="name" align="center" label="算子类型"></el-table-column>
          <el-table-column prop="remark" align="center" label="描述"></el-table-column>
          <el-table-column prop="idIndex" align="center" label="顺序"></el-table-column>
          <el-table-column v-if="queryCount === 'true'" align="center" label="数量">
            <template #default="scope">{{ scope.row.typeMark ? '' : (scope.row.count || 0) }}</template>
          </el-table-column>
          <el-table-column align="center" label="状态">
            <template #default="scope">
              <span>{{ scope.row.status === 0 ? '不可见' : '可见' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80">
            <template #default="scope">
              <span class="handler-area">
                <span @click="editRow(scope.row)" title="修改">
                  <svg-icon icon-class="xg"/>
                </span>
                <span>
                  <el-dropdown trigger="click" placement="bottom-start">
                    <span title="更多选项" class="gdxx">
                      <svg-icon icon-class="more"/>
                    </span>
                    <el-dropdown-menu slot="dropdown">
                      <template v-if="showCompHandle">
                        <el-dropdown-item v-if="!scope.row.typeMark">
                          <span @click="buildChildCatalogueSe(scope.row)">新建子目录</span>
                        </el-dropdown-item>
                        <el-dropdown-item v-if="!scope.row.typeMark">
                          <span @click="buildChildComponent(scope.row)">新建组件</span>
                        </el-dropdown-item>
                      </template>
                      <el-dropdown-item>
                        <span
                          @click="setRowVisible(scope.row)"
                        >{{scope.row.status === 0 ? '可见' : '不可见'}}</span>
                      </el-dropdown-item>
                      <el-dropdown-item v-if="showCompHandle">
                        <span @click="deleteItem(scope.row)">删除</span>
                      </el-dropdown-item>
                      <el-dropdown-item v-if="showCompHandle">
                        <span v-if="scope.row.parentName" @click="designItem(scope.row)">设计</span>
                      </el-dropdown-item>
                      <el-dropdown-item v-if="showCompHandle">
                        <span v-if="scope.row.typeMark" @click="triggerReUpload(scope.row)">重新上传</span>
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </el-dropdown>
                </span>
              </span>
              <!-- <span class="design-status" v-if="scope.row.dom">
                <i class="el-icon-circle-check"></i>
              </span> -->
            </template>
          </el-table-column>
        </el-table>
      </div>
      <!-- <div slot="slot-form-page">
        <BasePagination :data="page" @current="currentPage" @sizes="pageSizes"></BasePagination>
      </div>-->
    </zg-content>
    <!-- 组件 -->
    <el-dialog
      :close-on-click-modal="false"
      :visible="appendCatalogueVisible"
      :title="formatTitle"
      :before-close="closeDialog"
      :show-close="false"
    >
      <div class="dialog-body">
        <el-form label-width="80px">
          <el-row>
            <el-col :span="12">
              <el-form-item label="所属目录">
                <el-select v-model="catalogueData.catalogueName" placeholder="请选择" size="mini">
                  <el-option
                    v-for="item in directory"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id"
                  ></el-option>
                </el-select>
                <!-- <span style="position:relative" id="inner">
                <i class="el-icon-caret-bottom dropdown-icon"></i>
                <el-input
                  @focus="!catalogueData.catalogueId && (catalogueSelVisible = true)"
                  size="mini"
                  readonly
                  v-model="catalogueData.catalogueName"
                  style="width:322px"
                ></el-input>
                <transition name="el-zoom-in-top" >
                  <div v-show="catalogueSelVisible" class="catalogue-list">
                    <div>
                      <el-tree
                        ref="tree"
                        :expand-on-click-node="false"
                        @node-click="selectCatalogue"
                        highlight-current
                        default-expand-all
                        :props="{ label: 'name', children: 'childs' }"
                        node-key="id"
                        :data="catalogueList"
                      ></el-tree>
                    </div>
                    <div>
                      <el-button size="mini" @click="confirmCatalogueSel">确 定</el-button>
                      <el-button @click="catalogueSelVisible = false" size="mini" type="info">取 消</el-button>
                    </div>
                  </div>
                </transition>
                </span>-->
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="目录名称">
                <el-input
                  size="mini"
                  placeholder="请输入目录名称（示例：数据分析目录）"
                  v-model.trim="catalogueData.name"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="目录编码">
                <el-input
                  size="mini"
                  placeholder="请输入目录编码（示例：007）"
                  v-model.trim="catalogueData.code"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="目录顺序">
                <el-input
                  size="mini"
                  placeholder="请输入目录顺序（示例：1）"
                  v-model.trim="catalogueData.sequence"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="备注">
                <el-input
                  size="mini"
                  type="textarea"
                  placeholder="请输入备注（示例：该目录用于****）"
                  v-model.trim="catalogueData.mark"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>

          <!-- <el-form-item label="图标">
            <input style="width:100%" @change="fileSel" size="mini" type="file" />
          </el-form-item>-->

          <!-- <el-form-item label="目录用途" class="use">
            <el-select size="mini" v-model.trim="catalogueData.useType" placeholder>
              <el-option
                v-for="item in zjoptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              ></el-option>
            </el-select>
          </el-form-item>-->
        </el-form>
      </div>
      <template #footer>
        <div>
          <el-button size="mini" @click="appendCatalogue">确 定</el-button>
          <el-button
            @click="
              appendCatalogueVisible = false;
              clearCatalogueHistory();
            "
            size="mini"
            type="info"
          >取 消</el-button>
        </div>
      </template>
    </el-dialog>
    <!-- 目录 -->
    <el-dialog
      :close-on-click-modal="false"
      :visible="appendComponentVisible"
      :title="formatTitleC"
      :before-close="closeDialog"
      :show-close="false"
    >
      <div class="dialog-body">
        <el-form label-width="100px">
          <el-row>
            <el-col :span="12">
              <el-form-item label="所属目录">
                <el-select v-model="componentData.catalogueName" placeholder="请选择" size="mini">
                  <el-option
                    v-for="item in directory"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="组件名称">
                <el-input size="mini" v-model.trim="componentData.name"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="组件顺序">
                <el-input size="mini" v-model.trim="componentData.sequence"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="24" v-if="!componentData.isEditing">
              <el-form-item label="上传算子">
                <el-upload
                  ref="operatorUpload"
                  action=""
                  :auto-upload="false"
                  :limit="1"
                  accept=".java"
                  :on-change="handleFileChange"
                  :on-remove="handleFileRemove"
                  :file-list="uploadFileList"
                  size="mini"
                >
                  <el-button size="mini" type="primary" :disabled="!!componentData.uploadFile">选择 .java 文件</el-button>
                </el-upload>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="备注">
                <el-input size="mini" type="textarea" v-model.trim="componentData.mark"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <!-- <el-form-item label="图标">
            <input style="width:100%" @change="fileSel" size="mini" type="file" />
          </el-form-item>-->
        </el-form>
      </div>
      <template #footer>
        <div>
          <el-button size="mini" @click="appendComponent">确 定</el-button>
          <el-button
            @click="
              appendComponentVisible = false;
              clearCatalogueHistory();
            "
            size="mini"
            type="info"
          >取 消</el-button>
        </div>
      </template>
    </el-dialog>
    <!-- 查看详情 -->
    <el-dialog
      :close-on-click-modal="false"
      :visible="detailsVisible"
      :title="detailsTitle + '详情'"
      :before-close="
        () => {
          this.detailsVisible = false;
        }
      "
      custom-class="detail-dialog"
      top="16vh"
    >
      <el-form v-if="Object.keys(detailsData).length > 0" class="detailDialog" label-width="120px">
        <el-row>
          <el-col :span="12">
            <el-form-item :label="detailsTitle + '名称'">
              <el-input size="mini" disabled v-model="detailsData.text"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="detailsTitle + '状态'">
              <el-input size="mini" disabled v-model="detailsData.status"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="detailsTitle + '编码'">
              <el-input size="mini" disabled v-model="detailsData.idIndex"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="detailsTitle + '所属目录'" v-if="showDetails">
              <el-input size="mini" disabled v-model="detailsData.parentName"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="detailsTitle + '创建时间'" v-if="showDetails">
              <el-input size="mini" disabled v-model="detailsData.createDate"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item :label="detailsTitle + '源数据对象'" v-if="showDetails">
              <el-input size="mini" disabled v-model="detailsData.metaClass"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item :label="detailsTitle + '描述'">
              <el-input size="mini" type="textarea" disabled v-model="detailsData.remark"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-dialog>
    <!-- 隐藏的文件选择器 -->
    <input
      type="file"
      ref="reUploadInput"
      accept=".java"
      style="display: none"
      @change="handleReUploadFile"
    />
  </div>
</template>
<script>
/**
 * Create by Leo
 * 数据处理流程搭建所使用的算子组件列表展示模块
 * 根据运行环境判断当前属于开发环境还是生产环境，判断算子组件开发功能是否展示
 */
import BaseTable from '@/components/BaseTable/index.vue';
import BaseDialogForm from '@/components/BaseDialogForm/index.vue';
import BasePagination from '@/components/BasePagination';
import BaseSearch from '@/components/BaseSearch';
import zgContent from '@/components/zgContent';
import API from '@/api/index';
// import exec from '@/FlowChart/Command.js';
import { queryConfig, editDialogConfig, tableConfig, zjoptions } from './config.js';

import axios from '@/api/base/http';

export default {
  name: 'ComponentsManage',
  meta: {
    pageIndex: 100,
    title: '算子组件管理',
    pageType: 'mainNav',
    belongTo: 'process'
  },
  data() {
    return {
      /**
       * 是否展示详情
       */
      showDetails: true,
      /**
       * 组件配置选项
       */
      zjoptions: zjoptions,
      /**
       * 任务分组标签
       */
      taskGroupTag: '',
      /**
       * 是否展示列表操作按钮
       */
      isShowOperation: tableConfig.isShowOperation,
      /**
       * 是否展示列表勾选项
       */
      isShowCheckBox: tableConfig.isShowCheckBox,
      /**
       * 列表表头配置项
       */
      tableColumnConfig: tableConfig.tableColumnConfig,
      /**
       * 列表数据
       */
      tableData: [],
      /**
       * 算子组件配置项验证
       */
      formItemRuleConfig: editDialogConfig.formItemRuleConfig,
      /**
       * 算子组件配置项
       */
      formItemConfig: editDialogConfig.formItemConfig,
      /**
       * 是否有表格
       */
      isHasTable: editDialogConfig.isHasTable,
      /**
       * 算子组件配置项表单对象
       */
      ruleForm: editDialogConfig.ruleForm,
      /**
       * 是否有进度表
       */
      isHasTableScheduler: editDialogConfig.isHasTableScheduler,
      /**
       * 是否有预览
       */
      isHasPreviewTrans: editDialogConfig.isHasPreviewTrans,
      /**
       * 弹框配置项
       */
      dialog: editDialogConfig.dialog,
      // 查询配置
      queryRuleForm: this.$utils.deepCopy(queryConfig.queryRuleForm),
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
       * 列表筛选项
       */
      searchVal: '',
      /**
       * 算子组件状态筛选项列表
       */
      componentStatusList: [{ label: '全部', value: null }, { label: '可见', value: 1 }, { label: '不可见', value: 0 }],
      /**
       * 算子组件状态筛选项
       */
      componentStatus: null,
      /**
       * 查询模式：是否统计数量
       */
      queryCount: 'false',
      /**
       * 配置项
       */
      catalogueSelVisible: false,
      /**
       * 配置项
       */
      hasSelectCatalogue: null,
      /**
       * 添加目录数据按钮显隐
       */
      appendCatalogueVisible: false,
      /**
       * 目录数据配置项
       */
      catalogueData: {
        isEditing: null,
        catalogueId: null,
        catalogueName: null,
        name: null,
        code: null,
        icon: null,
        sequence: null,
        mark: null
      },
      /**
       * 是否展示添加组件
       */
      appendComponentVisible: false,
      /**
       * 组件信息配置项
       */
      componentData: {
        isEditing: null,
        catalogueId: null,
        catalogueName: null,
        metaClass: null,
        name: null,
        code: null,
        icon: null,
        sequence: null,
        mark: null,
        uploadFile: null
      },
      uploadFileList: [],
      reUploadRow: null,
      /**
       * 目录列表
       */
      catalogueList: [],
      /**
       * 指向
       */
      directory: [],
      /**
       * 是否展示详情
       */
      detailsVisible: false,
      /**
       * 详情标题
       */
      detailsTitle: '',
      /**
       * 详情数据
       */
      detailsData: {}
    };
  },
  mounted() {
    this.getData();
    this.getcatalogueList();
  },
  components: {
    BaseTable,
    BaseDialogForm,
    BasePagination,
    zgContent,
    BaseSearch
  },
  computed: {
    /**
     * 判断系统运行环境，返回是否展示组件新建操作按钮
     */
    showCompHandle() {
      return process.env.NODE_ENV === 'development';
    },
    /**
     * 目录格式标题
     */
    formatTitle() {
      if (this.catalogueData.isEditing) return '编辑目录';
      else if (this.catalogueData.catalogueId) return '新增子目录';
      return '新增目录';
    },
    /**
     * 组件格式标题
     */
    formatTitleC() {
      if (this.componentData.isEditing) return '编辑组件';
      return '新增组件';
    }
  },
  methods: {
    /**
     * 分页设置
     * @param e
     */
    currentPage(e) {
      this.page.pageNum = e;
      this.getData();
    },
    /**
     * 分页设置
     * @param e
     */
    pageSizes(e) {
      this.page.pageSize = e;
      this.getData();
    },
    /**
     * 获取算子组件列表数据
     */
    getData() {
      const loading = this.$utils.setLoading('查询中...');
      // let { pageNum, pageSize } = this.page;
      let params = {
        status: this.componentStatus,
        name: this.queryRuleForm.name,
        type: 1,
        count: this.queryCount
      };
      API.getComponentsList(params)
        .then(r => {
          let res = r.data;
          if (res.success) {
            this.createMark(res.data);
            for (let item of res.data) {
              item.children = [...item.childs, ...item.children];

              delete item.childs;
            }

            this.tableData = res.data;
          }
        })
        .finally(() => {
          loading.close();
        });
    },
    /**
     * 创建标注
     * @param arr
     */
    createMark(arr) {
      for (let item of arr) {
        if (item.children && item.children.length) {
          for (let c of item.children) {
            c.name = c.text;
            c.typeMark = 'components';
          }
        }
        this.createMark(item.childs);
      }
    },
    /**
     * 查询
     */
    search() {
      this.getData();
    },
    /**
     * 重置查询
     */
    resetForm() {
      this.componentStatus = null;
      this.queryRuleForm.name = '';
      this.queryCount = 'false';
      this.getData();
    },
    /**
     * 编辑算子组件
     * @param scope
     */
    edit(scope) {
      this.clearRuleForm();
      this.ruleForm = JSON.parse(JSON.stringify(scope.row.row));
      this.dialog.title = '修改数据库';
      this.dialog.visible = true;
    },
    // deleteItem(scope) {
    //   let params = {
    //     idDatabase: scope.row.row.idDatabase
    //   };
    //   this.$confirm('是否删除？', '确认信息', {
    //     distinguishCancelAndClose: true,
    //     confirmButtonText: '确定',
    //     cancelButtonText: '取消'
    //   })
    //     .then(() => {
    //       API.getDatabaseDelete(params).then(res => {
    //         this.$message({
    //           type: res.data.success ? 'success' : 'error',
    //           message: res.data.message
    //         });
    //         if (res.data.success) this.getData();
    //       });
    //     })
    //     .catch(action => {});
    // },
    /**
     * 提交表单
     * @param valid
     * @param usClose
     */
    formSubmit(valid, isClose) {
      if (valid) {
      }
    },
    /**
     * 清除表单
     */
    clearRuleForm() {
      for (let item in this.ruleForm) {
        this.ruleForm[item] = '';
      }
    },
    /**
     * 关闭弹框
     */
    closeDialog() {
      this.appendCatalogueVisible = false;
      this.appendComponentVisible = false;
      this.clearCatalogueHistory();
    },
    /**
     * 文件数据
     * @param ev
     */
    fileSel(ev) {
      let fileList = ev.target.files;
      let iconForm = new FormData();
      for (let file of fileList) {
        let fileFormat = file.name.split('.')[file.name.split('.').length - 1];
        if (!['jpg', 'png', 'gif', 'jpeg'].includes(fileFormat)) {
          this.$message.error('请选择图标文件！');
          ev.target.value = null;
          return;
        }
      }

      iconForm.append('file', fileList[0]);
      this.catalogueData.icon = iconForm;
    },
    /**
     * 获取算子组件目录列表数据
     */
    getcatalogueList() {
      API.getComponentsTree().then(r => {
        let res = r.data;
        if (res.success) {
          this.catalogueList = [
            {
              name: '全部',
              id: -1,
              children: res.data,
              childs: res.data
            }
          ];
          this.directory = res.data;
        }
      });
    },
    // selectCatalogue(data) {
    //   this.hasSelectCatalogue = data;
    // },

    // confirmCatalogueSel() {
    //   if (!this.hasSelectCatalogue) {
    //     this.$message.error('请选择目录！');
    //     return;
    //   }
    //   this.$set(this.catalogueData, 'catalogueName', this.hasSelectCatalogue.name);
    //   this.catalogueSelVisible = false;
    // },
    /**
     * 添加目录
     */
    appendCatalogue() {
      let validateList = [this.catalogueData.catalogueName, this.catalogueData.name, this.catalogueData.code, this.catalogueData.sequence];
      if (!validateList.every(Boolean)) {
        this.$message.info('请填写完整信息！');
        return;
      }
      /**
       * 判断是否可编辑
       */
      if (this.catalogueData.isEditing) {
        axios({
          method: 'POST',
          url: '/api/plugin/manage/edit',
          data: {
            id: this.catalogueData.isEditing.id,
            parent: this.hasSelectCatalogue.id,
            text: this.catalogueData.name,
            code: this.catalogueData.code,
            idIndex: this.catalogueData.sequence,
            remark: this.catalogueData.mark,
            status: this.catalogueData.isEditing.status
          }
        }).then(r => {
          const res = r.data;
          if (res.success) {
            this.$message.success('编辑目录成功！');

            this.getData();
            this.closeDialog();
          }
        });
        return;
      }
      axios({
        method: 'POST',
        url: '/api/plugin/manage/add',
        data: {
          parent: '-1',
          metaClass: this.catalogueData.metaClass,
          text: this.catalogueData.name,
          code: this.catalogueData.code,
          idIndex: this.catalogueData.sequence,
          remark: this.catalogueData.mark
        }
      }).then(r => {
        const res = r.data;
        if (res.success) {
          this.$message.success('添加目录成功！');
          this.getData();
          this.closeDialog();
        }
      });
    },
    /**
     * 回显
     * @param options
     * @param mlId
     */
    datashow(options, mlId) {
      let actions = [];
      Object.keys(options).some(key => {
        if (options[key].id == mlId) {
          actions.push(options[key].name);
          return true;
        }
      });
      return actions.join('');
    },
    /**
     * 下载示例算子文件
     */
    downloadExample() {
      const content = `package com.example;

import com.gx.etl.operator.core.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 示例算子：两个字段值相加
 *
 * 使用说明：
 * 1. 将此文件上传到"算子组件管理" -> "新增组件" -> "上传算子"
 * 2. 系统会自动编译并注册此算子
 * 3. 在流程设计页面可以拖拽使用此算子
 */
public class AddFieldsOperator extends AbstractOperator {

    /**
     * getId() - 返回算子的唯一标识符
     * 这个ID用于在流程中识别算子，必须全局唯一
     * 前端流程图中每个节点的pluginId就是这个值
     */
    @Override
    public String getId() {
        return "AddFields";
    }

    /**
     * getName() - 返回算子的显示名称
     * 这个名称会显示在前端算子列表和流程图节点上
     */
    @Override
    public String getName() {
        return "字段相加";
    }

    /**
     * getCategory() - 返回算子所属分类
     * 可选值：
     *   "input"     - 输入类算子（如读取Excel、CSV、数据库）
     *   "output"    - 输出类算子（如写入Excel、CSV、数据库）
     *   "transform" - 转换类算子（如过滤、计算、排序）
     *   "script"    - 脚本类算子（如JavaScript代码执行）
     * 这个分类决定了算子在前端侧边栏中的分组位置
     */
    @Override
    public String getCategory() {
        return "transform";
    }

    /**
     * getSchema() - 返回算子的参数配置Schema
     * 前端会根据这个Schema动态生成配置表单
     *
     * 支持的参数类型（PropertyType）：
     *   STRING        - 文本输入框
     *   NUMBER        - 数字输入框
     *   BOOLEAN       - 复选框
     *   SELECT        - 下拉选择框（需要配合options使用）
     *   TEXTAREA      - 多行文本框
     *   CODE          - 代码编辑器
     *   TABLE_COLUMN_MAP - 表格列映射
     *
     * property()方法参数说明：
     *   参数1: key - 参数的key，用于在execute中获取值
     *   参数2: label - 参数的显示名称
     *   参数3: type - 参数类型
     *   参数4: required - 是否必填（可选，默认false）
     */
    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("field1", "字段1", OperatorSchema.PropertyType.STRING, true)
                .property("field2", "字段2", OperatorSchema.PropertyType.STRING, true)
                .property("resultField", "结果字段", OperatorSchema.PropertyType.STRING, true);
    }

    /**
     * execute() - 算子的核心执行逻辑
     *
     * 参数说明：
     *   ctx   - 执行上下文，包含：
     *           - ctx.getString("key")  获取String类型参数值
     *           - ctx.getInt("key")     获取Integer类型参数值
     *           - ctx.getLong("key")    获取Long类型参数值
     *           - ctx.getBoolean("key") 获取Boolean类型参数值
     *           - ctx.get("key")        获取Object类型参数值
     *           - ctx.getConfig()       获取所有参数的Map
     *           - ctx.getDataSource(id) 获取数据源连接
     *
     *   input - 输入数据集，包含：
     *           - input.getColumnNames() 获取所有列名
     *           - input.iterator()       遍历所有行
     *           - input.collect()        将所有行收集到List
     *           - input.filter(predicate) 过滤行
     *           - input.count()          获取总行数
     *           - input.isEmpty()        是否为空
     *
     * 返回值：
     *   DataSet - 输出数据集，传递给下一个算子
     *   DataSet.of(columns, rows) - 创建新的数据集
     *   DataSet.empty() - 创建空数据集
     *   直接返回input表示透传（不做任何处理）
     *
     * DataRow操作：
     *   row.get("fieldName")    获取字段值
     *   row.put("fieldName", value) 设置字段值
     *   row.copy()              复制一行（深拷贝）
     *   row.hasField("name")    判断字段是否存在
     *   row.remove("name")      删除字段
     *   row.getFields()         获取所有字段的Map
     */
    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        // 从配置中获取参数
        String field1 = ctx.getString("field1");
        String field2 = ctx.getString("field2");
        String resultField = ctx.getString("resultField");

        // 参数校验
        if (field1 == null || field2 == null || resultField == null) {
            return input; // 参数不完整，直接透传
        }

        // 获取输入数据的列名，并添加结果列
        List<String> columns = input.getColumnNames();
        if (!columns.contains(resultField)) {
            columns.add(resultField);
        }

        // 遍历每一行，执行相加逻辑
        List<DataRow> rows = new ArrayList<>();
        for (DataRow row : input) {
            DataRow newRow = row.copy(); // 复制原行
            Object val1 = row.get(field1);
            Object val2 = row.get(field2);

            // 尝试数值相加
            double sum = 0;
            try {
                if (val1 != null) sum += Double.parseDouble(val1.toString());
                if (val2 != null) sum += Double.parseDouble(val2.toString());
            } catch (NumberFormatException e) {
                // 如果不是数字，执行字符串拼接
                newRow.put(resultField, String.valueOf(val1) + String.valueOf(val2));
                rows.add(newRow);
                continue;
            }

            // 整数结果去掉小数点
            if (sum == Math.floor(sum) && !Double.isInfinite(sum)) {
                newRow.put(resultField, (long) sum);
            } else {
                newRow.put(resultField, sum);
            }
            rows.add(newRow);
        }

        // 返回新的数据集
        return DataSet.of(columns, rows);
    }

    /**
     * requiresMaterializedInput() - 是否需要物化输入数据
     *
     * 默认返回false，表示流式处理（逐行处理，内存友好）
     * 返回true表示需要先将所有数据加载到内存（适用于排序、去重、关联等需要全局数据的操作）
     *
     * 一般情况下不需要重写此方法，除非你的算子需要：
     * - 排序（需要所有数据才能排序）
     * - 去重（需要所有数据才能判断重复）
     * - 关联/Join（需要两份数据都在内存中）
     * - 全局统计（如求平均值、最大值、最小值）
     */
    // @Override
    // public boolean requiresMaterializedInput() {
    //     return true;
    // }
}`;
      const blob = new Blob([content], { type: 'text/plain' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = 'AddFieldsOperator.java';
      a.click();
      URL.revokeObjectURL(url);
    },
    /**
     * 触发重新上传
     */
    triggerReUpload(row) {
      this.reUploadRow = row;
      this.$refs.reUploadInput.click();
    },
    /**
     * 处理重新上传的文件
     */
    handleReUploadFile(event) {
      const file = event.target.files[0];
      if (!file) return;
      if (!file.name.endsWith('.java')) {
        this.$message.error('请选择 .java 文件');
        return;
      }
      const loading = this.$utils.setLoading('编译中...');
      let formData = new FormData();
      formData.append('file', file);
      formData.append('id', this.reUploadRow.id);
      axios({
        method: 'POST',
        url: '/api/plugin/manage/reUploadOperator',
        headers: { 'Content-Type': 'multipart/form-data' },
        data: formData
      }).then(r => {
        const res = r.data;
        if (res.success) {
          this.$message.success('重新上传成功！');
          this.getData();
        } else {
          this.$message.error(res.msg || '上传失败');
        }
      }).catch(() => {}).finally(() => {
        loading.close();
        this.$refs.reUploadInput.value = '';
        this.reUploadRow = null;
      });
    },
    /**
     * 文件选择变化
     */
    handleFileChange(file) {
      this.componentData.uploadFile = file.raw;
    },
    /**
     * 文件移除
     */
    handleFileRemove() {
      this.componentData.uploadFile = null;
    },
    /**
     * 上传算子文件
     */
    uploadOperatorFile() {
      let formData = new FormData();
      formData.append('file', this.componentData.uploadFile);
      if (this.componentData.catalogueName) {
        formData.append('categoryId', this.componentData.catalogueName);
      }
      if (this.componentData.name) {
        formData.append('name', this.componentData.name);
      }
      if (this.componentData.mark) {
        formData.append('description', this.componentData.mark);
      }
      if (this.componentData.sequence) {
        formData.append('sortOrder', this.componentData.sequence);
      }
      const loading = this.$utils.setLoading('编译中...');
      axios({
        method: 'POST',
        url: '/api/plugin/manage/uploadOperator',
        headers: { 'Content-Type': 'multipart/form-data' },
        data: formData
      }).then(r => {
        const res = r.data;
        if (res.success) {
          this.$message.success('上传并注册算子成功！');
          this.getData();
          this.closeDialog();
        } else {
          this.$message.error(res.msg || '上传失败');
        }
      }).catch(() => {}).finally(() => {
        loading.close();
      });
    },
    /**
     * 添加算子组件
     */
    appendComponent() {
      // 如果有上传文件，走上传流程
      // 如果有上传文件，走上传流程
      if (this.componentData.uploadFile && !this.componentData.isEditing) {
        this.uploadOperatorFile();
        return;
      }

      let validateList = [this.componentData.catalogueName, this.componentData.name, this.componentData.sequence];
      let parentName = this.datashow(this.directory, this.componentData.catalogueName);
      if (!validateList.every(Boolean)) {
        this.$message.info('请填写完整信息！');
        return;
      }
      /**
       * 判断是否可编辑
       */
      if (this.componentData.isEditing) {
        let params = {
          id: this.componentData.isEditing.id,
          categoriesId: this.componentData.catalogueName,
          parentName: parentName,
          metaClass: this.componentData.metaClass,
          text: this.componentData.name,
          pluginId: this.componentData.code,
          idIndex: this.componentData.sequence,
          remark: this.componentData.mark
        };
        if (params.parentName == '') {
          params.parentName = this.componentData.isEditing.parentName;
          params.categoriesId = this.componentData.isEditing.categoriesId;
        }
        axios({
          method: 'POST',
          url: '/api/plugin/manage/type/edit',
          data: params
        }).then(r => {
          const res = r.data;
          if (res.success) {
            this.$message.success('编辑组件成功！');

            this.getData();
            this.closeDialog();
          }
        });
      } else {
        axios({
          method: 'POST',
          url: '/api/plugin/manage/init',
          data: {
            categoriesId: this.componentData.catalogueName,
            parentName: parentName,
            metaClass: this.componentData.metaClass,
            text: this.componentData.name,
            pluginId: this.componentData.code,
            idIndex: this.componentData.sequence,
            remark: this.componentData.mark
          }
        }).then(r => {
          const res = r.data;
          if (res.success) {
            this.$message.success('添加组件成功！');
            this.getData();
            this.closeDialog();
          }
        });
      }
    },
    /**
     * 设置行可见
     * @param row
     */
    setRowVisible(row) {
      const loading = this.$utils.setLoading('修改中...');
      let { id, parentId: parent, text, code, idIndex, remark, metaClass } = row;
      // 判断行级别
      if (row.parentId) {
        axios({
          method: 'POST',
          url: '/api/plugin/manage/edit',
          data: {
            id,
            parent,
            text,
            code,
            idIndex,
            remark,
            metaClass,
            status: row.status === 0 ? 1 : 0
          }
        })
          .then(r => {
            const res = r.data;
            this.$message({
              type: res.success ? 'success' : 'error',
              message: res.success ? '修改成功' : '修改失败'
            });
            res.success && this.getData();
          })
          .finally(res => {
            loading.close();
          });
      } else {
        axios({
          method: 'POST',
          url: '/api/plugin/manage/type/edit',
          data: {
            id,
            parent,
            text,
            code,
            idIndex,
            remark,
            metaClass,
            status: row.status === 0 ? 1 : 0
          }
        })
          .then(r => {
            const res = r.data;
            this.$message({
              type: res.success ? 'success' : 'error',
              message: res.success ? '修改成功' : '修改失败'
            });
            res.success && this.getData();
          })
          .finally(res => {
            loading.close();
          });
      }
    },
    /**
     * 清除目录历史
     */
    clearCatalogueHistory() {
      this.hasSelectCatalogue = null;
      this.catalogueSelVisible = false;
      this.uploadFileList = [];
      for (let i in this.catalogueData) {
        this.$set(this.catalogueData, i, null);
      }
      for (let i in this.componentData) {
        this.$set(this.componentData, i, null);
      }
    },
    /**
     * 创建目录
     * @param row
     */
    buildChildCatalogueSe(row) {
      this.$set(this.catalogueData, 'catalogueId', row.id);
      this.findNodeMsg(this.catalogueList, row.id);
      this.appendCatalogueVisible = true;
    },
    /**
     * 创建子算子组件
     * @param row
     */
    buildChildComponent(row) {
      this.$set(this.componentData, 'catalogueId', row.id);
      this.findNodeMsg(this.catalogueList, row.id, 'component');
      this.appendComponentVisible = true;
    },
    /**
     * 编辑行
     * @param row
     */
    editRow(row) {
      if (!row.typeMark) {
        this.$set(this.catalogueData, 'isEditing', row);
        this.findNodeMsg(this.catalogueList, row.parentId);
        this.$set(this.catalogueData, 'catalogueName', this.datashow(this.directory, row.id));
        this.$set(this.catalogueData, 'name', row.name);
        this.$set(this.catalogueData, 'code', row.code);
        this.$set(this.catalogueData, 'mark', row.remark);
        this.$set(this.catalogueData, 'sequence', row.idIndex);
        this.$set(this.catalogueData, 'catalogueId', null);
        this.appendCatalogueVisible = true;
        // this.$nextTick(() => {
        //   if (this.hasSelectCatalogue) {
        //     this.$refs['tree'].setCurrentKey(this.hasSelectCatalogue.id);
        //   }
        // });
      } else {
        this.$set(this.componentData, 'isEditing', row);
        this.findNodeMsg(this.catalogueList, row.categoriesId, 'component');
        this.$set(this.componentData, 'name', row.name);
        this.$set(this.componentData, 'code', row.pluginId);
        this.$set(this.componentData, 'mark', row.remark);
        this.$set(this.componentData, 'catalogueName', row.parentName);
        this.$set(this.componentData, 'sequence', row.idIndex);
        this.$set(this.componentData, 'metaClass', row.metaClass);
        this.appendComponentVisible = true;

        // this.$nextTick(() => {
        //   if (this.hasSelectCatalogue) {
        //     this.$refs['ctree'].setCurrentKey(this.hasSelectCatalogue.id);
        //   }
        // });
      }
    },
    /**
     * 查找节点信息
     * @param arr
     * @param nodeId
     * @param typeMark
     */
    findNodeMsg(arr, nodeId, typeMark) {
      for (let item of arr) {
        if (item.id == nodeId) {
          this.hasSelectCatalogue = item;
          if (!typeMark) {
            this.$set(this.catalogueData, 'catalogueName', item.name);
          } else {
            this.$set(this.componentData, 'catalogueName', item.name);
          }
          break;
        } else {
          if (item.children && item.children.length) {
            this.findNodeMsg(item.children, nodeId);
          } else if (item.childs && item.childs.length) {
            this.findNodeMsg(item.childs, nodeId);
          }
        }
      }
    },
    /**
     * 删除项
     * @param row
     */
    deleteItem(row) {
      let url;
      if (!row.typeMark) {
        url = '/api/plugin/manage/remove';
      } else {
        url = '/api/plugin/manage/type/remove';
      }
      let data = new FormData();
      data.append('id', row.id);
      /**
       * 确认是否删除
       */
      this.$confirm('此操作将永久删除数据, 是否继续?', '删除确认', {
        confirmButtonText: '确 定',
        cancelButtonText: '取 消',
        type: 'warning'
      })
        .then(() => {
          axios({
            method: 'POST',
            url,
            headers: {
              'Content-Type': 'form-data'
            },
            data
          }).then(r => {
            const res = r.data;
            if (res.success) {
              this.$message.success('删除成功！');
              this.getData();
            }
          });
        })
        .catch(() => {});
    },
    /**
     * 查看详情
     * @param row
     */
    viewDetails(row) {
      row.hasOwnProperty('children') ? (this.showDetails = false) : (this.showDetails = true);
      let url = `/api/plugin/manage/type/${row.id}/detail`;
      if (!row.typeMark) url = `/api/plugin/manage/${row.id}/detail`;
      axios({
        method: 'GET',
        url
      }).then(r => {
        const res = r.data;
        if (res.success) {
          this.detailsTitle = row.typeMark ? '组件' : '目录';
          this.detailsData = res.data;
          this.detailsData.status == 0 ? (this.detailsData.status = '不可见') : (this.detailsData.status = '可见');
          if (!row.typeMark) {
            // this.detailsData. = row.code;
          }
          this.detailsVisible = true;
        }
      });
    },
    /**
     * 跳转组件设计页面
     * @param row
     */
    designItem(row) {
      this.$router.push({ path: 'ComponentsDesign', query: { id: row.id } });
    }
  }
};
</script>
<style lang="scss" scoped>
@import '@/styles/theme.scss';
.el-select {
  display: block;
}
::v-deep.use .el-select .el-input .el-select__caret {
  margin-top: 80%;
}

.componentsManage {
  width: 100%;
  height: 100%;
  background: $contBg;
  border-radius: 6px;
  padding: 5px;
}
.btndiv {
  width: 100%;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.btndiv .leftdiv {
  width: 50%;
  display: flex;
}
.btndiv .leftdiv button {
  display: flex;
  align-items: center;
}
// .btndiv .leftdiv button:nth-child(2) {
//   background: #4198ca;
// }
// .btndiv .leftdiv button:last-child {
//   background: #1ba9a7;
// }
.btndiv .leftdiv button img {
  width: 12px;
}

.rightdiv {
  display: flex;
  align-items: center;
}
.rightdiv .resetForm {
  margin-left: 10px;
}
.rightdiv >>> .el-select {
  margin-right: 10px;
}
.rightdiv >>> .el-select .el-input__icon {
  line-height: 25px;
}
.rightdiv >>> .el-input-group__append,
.el-input-group__prepend {
  border-radius: 0;
}

.tablearea {
  padding-bottom: 20px;
  ::v-deep .el-table__body-wrapper {
    height: calc(100% - 42px) !important;
    overflow: auto;
  }
}
.el-table tbody tr.el-table__row:hover .handler-area .gdxx {
  color: #fff;
}
.tablearea .el-table .handler-area > span {
  cursor: pointer;
}
.tablearea .el-table .handler-area > span:not(:first-child) {
  margin-left: 12px;
}
.design-status {
  margin-left: 15px;
}
.tablearea >>> .el-table .el-table__expand-icon > .el-icon {
  margin-left: -8px;
  margin-top: -8px;
  color: #ffffff;
}
.componentsManage >>> .el-table__body-wrapper {
  overflow-x: hidden;
  overflow-y: auto;
}
.componentsManage >>> .el-dialog .catalogue-list {
  position: absolute;
  top: 28px;
  left: 0;
  height: 360px;
  width: 100%;
  z-index: 10;
  // background-color: #ffffff;
  padding: 10px;
  background-color: #1f2b25;
  border: 1px solid #435b44;
  border-top: none;
  box-shadow: 0 0 5px gray;
}

.componentsManage >>> .el-tree {
  background-color: #1f2b25;
  color: #ffffff;
}
.componentsManage >>> .el-dialog .el-dialog__title {
  color: #fefcb9;
  font-weight: bold;
}
::v-deep .detailDialog .el-form-item__label {
  color: #fff;
  width: 130px;
}

.componentsManage >>> .el-dialog .dropdown-icon {
  font-size: 18px;
  position: absolute;
  right: 8px;
  top: 4px;
  color: #fefcb9;
  z-index: 10;
}
.componentsManage .el-dialog .detail-content {
  color: #ffffff;
}
.componentsManage >>> .el-dialog .el-tree .is-current > .el-tree-node__content {
  background-color: #fefcb9;
  color: #333333;
}
.componentsManage >>> .el-dialog .el-tree .el-tree-node__content:hover {
  color: #333333;
}
.componentsManage >>> .el-dialog .el-dialog__header {
  padding-bottom: 20px;
  border-bottom: 1px solid #435b44;
}
.detail-dialog .el-form {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}
.detail-dialog .el-form > div {
  width: 100%;
}
.componentsManage >>> .el-dropdown {
  color: #ffffff;
}

// .gdxx {
//   color: $normalWordsClr;
// }
</style>
