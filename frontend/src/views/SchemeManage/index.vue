<template>
  <div class="scheme-manage">
    <!-- 顶部操作栏 -->
    <div class="scheme-header">
      <el-button type="primary" size="small" @click="openDialog()">新增</el-button>
      <div style="margin-left: auto; display: flex; align-items: center;">
        <el-input
          v-model="searchName"
          placeholder="搜索方案名称"
          size="small"
          style="width: 200px"
          clearable
          @clear="loadSchemes"
          @keyup.enter.native="loadSchemes"
        />
        <el-button type="primary" size="small" style="margin-left: 10px" @click="loadSchemes">搜索</el-button>
      </div>
    </div>

    <!-- 方案列表 -->
    <el-table :data="schemeList" border stripe style="width: 100%; margin-top: 10px">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="schemeName" label="方案名称" />
      <el-table-column prop="schemeDesc" label="方案描述" />
      <el-table-column prop="cjName" label="数据采集配置" />
      <el-table-column prop="transName" label="处理流程" />
      <el-table-column prop="status" label="状态" width="80">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'info'" size="small">
            {{ scope.row.status === 1 ? '已完成' : '未完成' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="280" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="openDialog(scope.row)">编辑</el-button>
          <el-button type="text" size="small" @click="viewDetail(scope.row)">查看详情</el-button>
          <el-button type="text" size="small" @click="startProcess(scope.row)">引接</el-button>
          <el-button v-if="scope.row.transId" type="text" size="small" @click="goFlowChart(scope.row)">设计流程</el-button>
          <el-button type="text" size="small" style="color: #F56C6C" @click="deleteScheme(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-if="total > 0"
      layout="total, sizes, prev, pager, next"
      :total="total"
      :page-size="pageSize"
      :current-page="pageIndex"
      :page-sizes="[10, 20, 50]"
      style="margin-top: 10px; text-align: right"
      @current-change="onPageChange"
      @size-change="onSizeChange"
    />

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="650px"
      :close-on-click-modal="false"
      @closed="resetDialog"
    >
      <el-form ref="schemeForm" :model="form" :rules="rules" label-width="120px" size="small">
        <el-form-item label="方案名称" prop="schemeName">
          <el-input v-model="form.schemeName" placeholder="请输入方案名称" />
        </el-form-item>
        <el-form-item label="方案描述" prop="schemeDesc">
          <el-input v-model="form.schemeDesc" type="textarea" :rows="2" placeholder="请输入方案描述" />
        </el-form-item>
        <el-form-item label="数据采集配置" prop="cjListId" v-if="!form.id">
          <el-select v-model="form.cjListId" placeholder="请选择数据采集配置" style="width: 100%">
            <el-option
              v-for="item in cjConfigList"
              :key="item.id"
              :label="item.ybName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="处理流程" prop="transId">
          <el-select v-model="form.transId" placeholder="请选择处理流程" style="width: 100%" clearable filterable>
            <el-option
              v-for="item in transformationList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="仿真方案" v-if="!form.id">
          <el-select
            v-model="cascade.planId"
            placeholder="请选择仿真方案"
            style="width: 100%"
            @change="onPlanChange"
            clearable
          >
            <el-option
              v-for="item in samplePlans"
              :key="item.id"
              :label="item.planName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="大样本" v-if="!form.id">
          <el-select
            v-model="cascade.dataId"
            placeholder="请选择大样本"
            style="width: 100%"
            :disabled="!cascade.planId"
            @change="onDataChange"
            clearable
          >
            <el-option
              v-for="item in sampleDataList"
              :key="item.id"
              :label="item.dataName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="样本子集" v-if="!form.id">
          <el-select
            v-model="cascade.subDataIds"
            placeholder="请选择样本子集（可多选）"
            style="width: 100%"
            multiple
            collapse-tags
            :disabled="!cascade.dataId"
            @change="onSubDataChange"
          >
            <el-option
              v-for="item in sampleSubDataList"
              :key="item.id"
              :label="item.subName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
      </el-form>

      <div slot="footer">
        <el-button size="small" @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" size="small" @click="saveScheme">保存</el-button>
      </div>
    </el-dialog>

    <!-- 查看详情弹窗 -->
    <el-dialog title="仿真方案详情" :visible.sync="detailVisible" width="700px">
      <template v-if="detailData">
        <table class="detail-table" style="width: 100%; border-collapse: collapse;">
          <tr>
            <td class="detail-label">方案名称</td>
            <td class="detail-value">{{ detailData.schemeName }}</td>
            <td class="detail-label">数据采集配置</td>
            <td class="detail-value">{{ detailData.cjName }}</td>
          </tr>
          <tr>
            <td class="detail-label">处理流程</td>
            <td class="detail-value" colspan="3">{{ detailData.transName || '-' }}</td>
          </tr>
          <tr>
            <td class="detail-label">状态</td>
            <td class="detail-value">
              <el-tag :type="detailData.status === 1 ? 'success' : 'info'" size="small">
                {{ detailData.status === 1 ? '已完成' : '未完成' }}
              </el-tag>
            </td>
            <td class="detail-label">创建时间</td>
            <td class="detail-value">{{ detailData.createTime }}</td>
          </tr>
          <tr>
            <td class="detail-label">方案描述</td>
            <td class="detail-value" colspan="3">{{ detailData.schemeDesc }}</td>
          </tr>
        </table>

        <el-table :data="pagedDetailList" border size="small" style="margin-top: 15px" v-if="detailData.detailList && detailData.detailList.length > 0">
          <el-table-column type="index" label="序号" width="60">
            <template slot-scope="scope">
              {{ (detailPage - 1) * detailPageSize + scope.$index + 1 }}
            </template>
          </el-table-column>
          <el-table-column prop="ybName" label="样本名" />
          <el-table-column prop="ybId" label="样本id" width="80" />
          <el-table-column prop="ybList" label="样本集" />
          <el-table-column label="状态" width="80">
            <template slot-scope="scope">
              <el-tag :type="scope.row.status === 1 ? 'success' : 'info'" size="small">
                {{ scope.row.status === 1 ? '完成' : '未完成' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination
          v-if="detailData.detailList && detailData.detailList.length > detailPageSize"
          layout="total, prev, pager, next"
          :total="detailData.detailList.length"
          :page-size="detailPageSize"
          :current-page="detailPage"
          style="margin-top: 10px; text-align: right"
          @current-change="onDetailPageChange"
        />
      </template>
    </el-dialog>

    <!-- 引接处理进度弹窗 -->
    <el-dialog title="引接处理进度" :visible.sync="progressVisible" width="500px" :close-on-click-modal="false" :close-on-press-escape="false" :show-close="progressFinished">
      <div style="text-align: center; padding: 20px 0;">
        <el-progress
          :percentage="progressPercent"
          :status="progressFinished ? 'success' : undefined"
          :stroke-width="20"
          text-inside
        />
        <p style="margin-top: 15px; color: #606266;">
          {{ progressFinished ? '处理完成' : '处理中...' }} {{ progressCompleted }} / {{ progressTotal }}
        </p>
      </div>
      <div slot="footer" v-if="progressFinished">
        <el-button type="primary" size="small" @click="progressVisible = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import API from '@/api/index';

export default {
  name: 'SchemeManage',
  meta: {
    pageIndex: 70,
    title: '仿真方案管理',
    pageType: 'mainNav',
    belongTo: 'process'
  },
  data() {
    return {
      // 列表
      schemeList: [],
      total: 0,
      pageIndex: 1,
      pageSize: 20,
      searchName: '',

      // 弹窗
      dialogVisible: false,
      dialogTitle: '新增仿真方案',
      form: {
        id: null,
        schemeName: '',
        schemeDesc: '',
        cjListId: null,
        transId: null,
        status: 0
      },
      rules: {
        schemeName: [{ required: true, message: '请输入方案名称', trigger: 'blur' }]
      },

      // 下拉数据
      cjConfigList: [],
      transformationList: [],

      // 三级联动
      samplePlans: [],
      sampleDataList: [],
      sampleSubDataList: [],
      sampleDetailMap: {},
      cascade: {
        planId: null,
        dataId: null,
        subDataIds: []
      },

      // 详情弹窗
      detailVisible: false,
      detailData: null,
      detailPage: 1,
      detailPageSize: 10,

      // 引接进度
      progressVisible: false,
      progressFinished: false,
      progressTotal: 0,
      progressCompleted: 0,
      progressTimer: null
    };
  },
  computed: {
    progressPercent() {
      if (this.progressTotal === 0) return 0;
      return Math.round((this.progressCompleted / this.progressTotal) * 100);
    },
    pagedDetailList() {
      if (!this.detailData || !this.detailData.detailList) return [];
      const start = (this.detailPage - 1) * this.detailPageSize;
      return this.detailData.detailList.slice(start, start + this.detailPageSize);
    }
  },
  mounted() {
    this.loadSchemes();
    this.loadCjConfigs();
    this.loadTransformations();
    this.loadSamplePlans();
  },
  beforeDestroy() {
    if (this.progressTimer) {
      clearInterval(this.progressTimer);
    }
  },
  methods: {
    // ========== 列表 ==========
    loadSchemes() {
      API.sampleCascade.getSchemePage({
        pageIndex: this.pageIndex,
        pageSize: this.pageSize,
        schemeName: this.searchName
      }).then(res => {
        if (res.data && res.data.success) {
          const d = res.data.data;
          this.schemeList = d.records || [];
          this.total = d.total || 0;
        }
      });
    },
    onPageChange(page) {
      this.pageIndex = page;
      this.loadSchemes();
    },
    onSizeChange(size) {
      this.pageSize = size;
      this.pageIndex = 1;
      this.loadSchemes();
    },

    // ========== 下拉数据 ==========
    loadCjConfigs() {
      API.sampleCascade.getCjConfigs().then(res => {
        if (res.data && res.data.success) {
          this.cjConfigList = res.data.data || [];
        }
      });
    },
    loadTransformations() {
      API.sampleCascade.getTransformations().then(res => {
        if (res.data && res.data.success) {
          this.transformationList = res.data.data || [];
        }
      });
    },
    loadSamplePlans() {
      API.sampleCascade.getSamplePlans().then(res => {
        if (res.data && res.data.success) {
          this.samplePlans = res.data.data || [];
        }
      });
    },

    // ========== 三级联动 ==========
    onPlanChange(planId) {
      this.cascade.dataId = null;
      this.cascade.subDataIds = [];
      this.sampleDataList = [];
      this.sampleSubDataList = [];
      if (!planId) return;
      API.sampleCascade.getSampleDataByPlanId(planId).then(res => {
        if (res.data && res.data.success) {
          this.sampleDataList = res.data.data || [];
        }
      });
    },
    onDataChange(dataId) {
      this.cascade.subDataIds = [];
      this.sampleSubDataList = [];
      this.sampleDetailMap = {};
      if (!dataId) return;
      API.sampleCascade.getSampleSubDataByDataId(dataId).then(res => {
        if (res.data && res.data.success) {
          this.sampleSubDataList = res.data.data || [];
        }
      });
    },
    onSubDataChange(subDataIds) {
      this.sampleDetailMap = {};
      if (!subDataIds || subDataIds.length === 0) return;
      // 获取每个选中三级下的四级数据
      subDataIds.forEach(id => {
        API.sampleCascade.getSampleDetailBySubDataId(id).then(res => {
          if (res.data && res.data.success) {
            const ids = (res.data.data || []).map(item => item.id);
            this.sampleDetailMap[id] = ids;
          }
        });
      });
    },

    // ========== 弹窗 ==========
    openDialog(row) {
      if (row) {
        this.dialogTitle = '编辑仿真方案';
        API.sampleCascade.getSchemeById(row.id).then(res => {
          if (res.data && res.data.success) {
            const scheme = res.data.data;
            this.form = {
              id: scheme.id,
              schemeName: scheme.schemeName,
              schemeDesc: scheme.schemeDesc,
              cjListId: scheme.cjListId,
              transId: scheme.transId,
              status: scheme.status
            };
            // 回显级联数据
            if (scheme.detailList && scheme.detailList.length > 0) {
              const firstDetail = scheme.detailList[0];
              this.cascade.dataId = firstDetail.ybId;
              this.cascade.subDataIds = firstDetail.ybList ? firstDetail.ybList.split(',').map(Number) : [];
            }
            this.dialogVisible = true;
          }
        });
      } else {
        this.dialogTitle = '新增仿真方案';
        this.dialogVisible = true;
      }
    },
    resetDialog() {
      this.form = {
        id: null,
        schemeName: '',
        schemeDesc: '',
        cjListId: null,
        transId: null,
        status: 0
      };
      this.cascade = { planId: null, dataId: null, subDataIds: [] };
      this.sampleDataList = [];
      this.sampleSubDataList = [];
      this.sampleDetailMap = {};
      this.$nextTick(() => {
        if (this.$refs.schemeForm) {
          this.$refs.schemeForm.clearValidate();
        }
      });
    },
    saveScheme() {
      this.$refs.schemeForm.validate(valid => {
        if (!valid) return;
        const params = {
          id: this.form.id,
          schemeName: this.form.schemeName,
          schemeDesc: this.form.schemeDesc,
          cjListId: this.form.cjListId,
          transId: this.form.transId,
          status: this.form.status,
          detailList: []
        };

        if (!this.cascade.dataId) {
          // 没选二级，直接保存
          this.doSave(params);
          return;
        }

        // 选了二级，确定要处理的三级列表
        const subIds = this.cascade.subDataIds.length > 0
          ? this.cascade.subDataIds
          : this.sampleSubDataList.map(item => item.id);

        if (subIds.length === 0) {
          this.doSave(params);
          return;
        }

        // 获取每个三级下的四级数据
        const promises = subIds.map(subId => {
          const subItem = this.sampleSubDataList.find(s => s.id === subId);
          return API.sampleCascade.getSampleDetailBySubDataId(subId).then(res => {
            if (res.data && res.data.success) {
              return (res.data.data || []).map(item => item.id);
            }
            return [];
          }).then(detailIds => ({
            ybId: subId,
            ybName: subItem ? subItem.subName : '',
            ybList: detailIds.join(',')
          }));
        });

        Promise.all(promises).then(details => {
          params.detailList = details;
          this.doSave(params);
        });
      });
    },
    doSave(params) {
      API.sampleCascade.saveScheme(params).then(res => {
        if (res.data && res.data.success) {
          this.$message.success('保存成功');
          this.dialogVisible = false;
          this.loadSchemes();
        } else {
          this.$message.error(res.data.msg || '保存失败');
        }
      });
    },

    // ========== 详情 ==========
    viewDetail(row) {
      API.sampleCascade.getSchemeById(row.id).then(res => {
        if (res.data && res.data.success) {
          this.detailData = res.data.data;
          this.detailPage = 1;
          this.detailVisible = true;
        }
      });
    },
    onDetailPageChange(page) {
      this.detailPage = page;
    },

    // ========== 删除 ==========
    goFlowChart(row) {
      sessionStorage.setItem('backPage', '/SchemeManage');
      // 加载方案详情，收集 YB_LIST 拆分为批量参数
      API.sampleCascade.getSchemeById(row.id).then(res => {
        if (res.data && res.data.success) {
          const scheme = res.data.data;
          const schemeIds = [];
          if (scheme.detailList) {
            scheme.detailList.forEach(d => {
              if (d.ybList) {
                d.ybList.split(',').forEach(id => {
                  if (id) schemeIds.push(id.trim());
                });
              }
            });
          }
          if (schemeIds.length > 0) {
            sessionStorage.setItem('batchParams', JSON.stringify(
              schemeIds.map(id => ({ SCHEME_ID: id }))
            ));
          } else {
            sessionStorage.removeItem('batchParams');
          }
        }
        this.$router.push({
          path: '/flowChart',
          query: {
            path: 'transformation/' + row.transId,
            transName: row.transName || '',
            transformationId: row.transId
          }
        });
      });
    },
    deleteScheme(row) {
      this.$confirm('确定删除该仿真方案？', '提示', { type: 'warning' }).then(() => {
        API.sampleCascade.deleteScheme(row.id).then(res => {
          if (res.data && res.data.success) {
            this.$message.success('删除成功');
            this.loadSchemes();
          }
        });
      }).catch(() => {});
    },

    // ========== 引接处理 ==========
    startProcess(row) {
      if (row.status === 1) {
        this.$message.info('该方案已完成');
        return;
      }
      this.$confirm('确定开始引接处理？', '提示', { type: 'info' }).then(() => {
        this.progressVisible = true;
        this.progressFinished = false;
        this.progressTotal = 0;
        this.progressCompleted = 0;

        API.sampleCascade.processScheme(row.id).then(res => {
          if (res.data && res.data.success) {
            this.pollProgress(row.id);
          } else {
            this.$message.error(res.data.msg || '启动失败');
            this.progressVisible = false;
          }
        }).catch(err => {
          console.error('processScheme error:', err);
          this.$message.error('启动失败');
          this.progressVisible = false;
        });
      }).catch(() => {});
    },
    pollProgress(schemeId) {
      if (this.progressTimer) clearInterval(this.progressTimer);
      this.progressTimer = setInterval(() => {
        API.sampleCascade.getProcessProgress(schemeId).then(res => {
          if (res.data && res.data.success) {
            const d = res.data.data;
            console.log('progress:', d);
            this.progressTotal = d.total;
            this.progressCompleted = d.completed;
            if (d.finished) {
              clearInterval(this.progressTimer);
              this.progressTimer = null;
              this.progressFinished = true;
              this.loadSchemes();
            }
          }
        }).catch(err => {
          console.error('pollProgress error:', err);
        });
      }, 1000);
    }
  }
};
</script>

<style scoped>
.scheme-manage {
  padding: 20px;
}
.scheme-header {
  display: flex;
  align-items: center;
}
.detail-table td {
  border: 1px solid #e6e6e6;
  padding: 8px 12px;
}
.detail-table tr:hover td {
  background: inherit;
  color: inherit;
}
.detail-label {
  background: #f5f7fa;
  width: 100px;
  color: #909399;
  font-size: 13px;
}
.detail-value {
  color: #606266;
  font-size: 13px;
}
.scheme-manage >>> .el-table__body tr:hover > td {
  background-color: #f5f7fa !important;
  color: #606266 !important;
}
.scheme-manage >>> .el-table__body tr.current-row > td {
  background-color: #ecf5ff !important;
  color: #606266 !important;
}
.scheme-manage >>> .el-table__body tr > td {
  color: #606266 !important;
}
</style>
