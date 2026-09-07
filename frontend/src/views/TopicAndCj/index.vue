<template>
  <div class="topic-cj-page">
    <!-- 左栏：数据采集配置 -->
    <div class="left-panel">
      <div class="panel-header">
        <span class="panel-title">数据采集配置</span>
        <div style="margin-left: auto; display: flex; align-items: center;">
          <el-input
            v-model="cj.searchName"
            placeholder="搜索采集名称"
            size="small"
            style="width: 150px"
            clearable
            @clear="loadCj"
            @keyup.enter.native="loadCj"
          />
          <el-button type="primary" size="small" style="margin-left: 5px" @click="loadCj">搜索</el-button>
          <el-button type="primary" size="small" style="margin-left: 5px" @click="openCjDialog()">新增</el-button>
        </div>
      </div>
      <el-table :data="cj.tableData" border stripe size="small" style="width: 100%">
        <el-table-column prop="id" label="ID" width="50" />
        <el-table-column prop="ybName" label="采集名称" min-width="1" show-overflow-tooltip />
        <el-table-column label="关联主题名称" min-width="2" show-overflow-tooltip>
          <template slot-scope="scope">
            {{ getTopicNames(scope.row.ybList) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="openCjDialog(scope.row)">编辑</el-button>
            <el-button type="text" size="small" style="color: #F56C6C" @click="deleteCj(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-if="cj.total > 0"
        layout="total, prev, pager, next"
        :total="cj.total"
        :page-size="cj.pageSize"
        :current-page="cj.pageIndex"
        small
        style="margin-top: 5px; text-align: right"
        @current-change="onCjPageChange"
      />
    </div>

    <!-- 右栏：主题配置 -->
    <div class="right-panel">
      <div class="panel-header">
        <span class="panel-title">主题配置</span>
        <div style="margin-left: auto; display: flex; align-items: center;">
          <el-input
            v-model="topic.searchName"
            placeholder="搜索主题名称"
            size="small"
            style="width: 150px"
            clearable
            @clear="loadTopics"
            @keyup.enter.native="loadTopics"
          />
          <el-button type="primary" size="small" style="margin-left: 5px" @click="loadTopics">搜索</el-button>
          <el-button type="primary" size="small" style="margin-left: 5px" @click="openImportDialog">从数据库获取</el-button>
          <el-button type="primary" size="small" style="margin-left: 5px" @click="openTopicDialog()">新增</el-button>
        </div>
      </div>
      <el-table :data="topic.tableData" border stripe size="small" style="width: 100%">
        <el-table-column prop="id" label="ID" width="50" />
        <el-table-column prop="topicName" label="主题名称" show-overflow-tooltip />
        <el-table-column prop="topicCode" label="主题别名" show-overflow-tooltip />
        <el-table-column prop="tableName" label="表名" show-overflow-tooltip />
        <el-table-column label="操作" width="120" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="openTopicDialog(scope.row)">编辑</el-button>
            <el-button type="text" size="small" style="color: #F56C6C" @click="deleteTopic(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-if="topic.total > 0"
        layout="total, prev, pager, next"
        :total="topic.total"
        :page-size="topic.pageSize"
        :current-page="topic.pageIndex"
        small
        style="margin-top: 5px; text-align: right"
        @current-change="onTopicPageChange"
      />
    </div>

    <!-- 主题配置弹窗 -->
    <el-dialog :title="topic.dialogTitle" :visible.sync="topic.dialogVisible" width="650px" :close-on-click-modal="false" @closed="resetTopicForm">
      <el-form ref="topicForm" :model="topic.form" :rules="topic.rules" label-width="100px" size="small">
        <el-form-item label="主题名称" prop="topicName">
          <el-input v-model="topic.form.topicName" placeholder="请输入主题名称" />
        </el-form-item>
        <el-form-item label="主题别名" prop="topicCode">
          <el-input v-model="topic.form.topicCode" placeholder="请输入主题别名" />
        </el-form-item>
        <el-form-item label="数据库表名" prop="tableName">
          <el-input v-model="topic.form.tableName" placeholder="请输入数据库表名" />
        </el-form-item>
        <el-form-item label="字段集合">
          <el-button type="primary" size="mini" @click="addField">添加字段</el-button>
          <el-table :data="topic.form.fieldList" border size="mini" style="margin-top: 10px" v-if="topic.form.fieldList.length > 0">
            <el-table-column type="index" label="序号" width="50" />
            <el-table-column label="字段名称">
              <template slot-scope="scope">
                <el-input v-model="scope.row.fieldName" size="mini" placeholder="字段名称" />
              </template>
            </el-table-column>
            <el-table-column label="字段注释">
              <template slot-scope="scope">
                <el-input v-model="scope.row.fieldAlias" size="mini" placeholder="字段注释" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="60">
              <template slot-scope="scope">
                <el-button type="text" size="mini" style="color: #F56C6C" @click="removeField(scope.$index)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button size="small" @click="topic.dialogVisible = false">取消</el-button>
        <el-button type="primary" size="small" @click="saveTopic">保存</el-button>
      </div>
    </el-dialog>

    <!-- 从MySQL模型库导入主题弹窗 -->
    <el-dialog title="从数据库获取主题" :visible.sync="importDialog.visible" width="780px" :close-on-click-modal="false">
      <el-form inline size="small" label-width="90px">
        <el-form-item label="数据源">
          <el-select v-model="importDialog.dbId" placeholder="请选择MySQL数据源" style="width: 200px" filterable>
            <el-option v-for="item in importDialog.mysqlDatabases" :key="item.value" :label="item.name" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="模型库名">
          <el-input v-model="importDialog.modelDb" placeholder="如 modelmanager" style="width: 160px" />
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="importDialog.addPrefix" @change="repreviewIfNeeded">添加前缀 TOPIC_MSG_</el-checkbox>
          <el-checkbox v-model="importDialog.stripPrefix" @change="repreviewIfNeeded">去掉原前缀</el-checkbox>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="small" :loading="importDialog.previewing" @click="reconnectAndPreview">连接并预览</el-button>
        </el-form-item>
      </el-form>
      <div style="display: flex; align-items: center; margin-bottom: 8px">
        <el-input
          v-model="importDialog.keyword"
          placeholder="搜索主题名称/备注"
          size="small"
          style="width: 220px"
          clearable
          @clear="searchMysqlTopics"
          @keyup.enter.native="searchMysqlTopics"
        />
        <el-button type="primary" size="small" style="margin-left: 5px" @click="searchMysqlTopics">搜索</el-button>
        <span v-if="importDialog.selectedNames.length" style="margin-left: auto; color: #909399; font-size: 12px">
          已选 {{ importDialog.selectedNames.length }} 个（可跨页累计）
        </span>
      </div>
      <el-table
        ref="previewTable"
        :data="importDialog.previewList"
        border
        size="mini"
        max-height="320"
        row-key="rawName"
        style="width: 100%"
        @selection-change="onPreviewSelectionChange"
      >
        <el-table-column type="selection" width="45" reserve-selection />
        <el-table-column prop="topicName" label="主题名称" show-overflow-tooltip />
        <el-table-column prop="topicCode" label="主题别名" show-overflow-tooltip />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column prop="fieldCount" label="字段数" width="70" />
      </el-table>
      <el-pagination
        v-if="importDialog.total > 0"
        layout="total, prev, pager, next"
        :total="importDialog.total"
        :page-size="importDialog.pageSize"
        :current-page="importDialog.pageIndex"
        small
        style="margin-top: 5px; text-align: right"
        @current-change="onPreviewPageChange"
      />
      <div style="margin-top: 8px; color: #909399; font-size: 12px">
        主题名称取 MySQL 主题的备注（汉字，为空时用别名）；主题别名为技术名，可勾选"去掉原前缀"（去除 TOPIC_XX_）和"添加前缀 TOPIC_MSG_"。按主题名称去重：已存在的主题只更新别名与字段。
      </div>
      <div slot="footer">
        <el-button size="small" @click="clearSelected">清空选择</el-button>
        <el-button size="small" @click="importDialog.visible = false">取消</el-button>
        <el-button
          type="primary"
          size="small"
          :loading="importDialog.importing"
          :disabled="importDialog.selectedNames.length === 0"
          @click="doImport"
        >导入所选（{{ importDialog.selectedNames.length }}）</el-button>
      </div>
    </el-dialog>

    <!-- 采集配置弹窗 -->
    <el-dialog :title="cj.dialogTitle" :visible.sync="cj.dialogVisible" width="500px" :close-on-click-modal="false" @closed="resetCjForm">
      <el-form ref="cjForm" :model="cj.form" :rules="cj.rules" label-width="100px" size="small">
        <el-form-item label="采集名称" prop="ybName">
          <el-input v-model="cj.form.ybName" placeholder="请输入采集名称" />
        </el-form-item>
        <el-form-item label="关联主题" prop="ybList">
          <el-select v-model="cj.selectedTopicIds" multiple filterable placeholder="请选择主题" style="width: 100%">
            <el-option
              v-for="item in cj.topicList"
              :key="item.id"
              :label="item.topicName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button size="small" @click="cj.dialogVisible = false">取消</el-button>
        <el-button type="primary" size="small" @click="saveCj">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import API from '@/api/index';

export default {
  name: 'TopicAndCj',
  meta: {
    pageIndex: 73,
    title: '主题与采集配置',
    pageType: 'mainNav',
    belongTo: 'process'
  },
  data() {
    return {
      topic: {
        tableData: [],
        total: 0,
        pageIndex: 1,
        pageSize: 20,
        searchName: '',
        dialogVisible: false,
        dialogTitle: '新增主题',
        form: { id: null, topicName: '', topicCode: '', tableName: '', fieldList: [] },
        rules: {
          topicName: [{ required: true, message: '请输入主题名称', trigger: 'blur' }]
        }
      },
      cj: {
        tableData: [],
        total: 0,
        pageIndex: 1,
        pageSize: 20,
        searchName: '',
        dialogVisible: false,
        dialogTitle: '新增采集配置',
        form: { id: null, ybName: '', ybList: '' },
        rules: {
          ybName: [{ required: true, message: '请输入采集名称', trigger: 'blur' }]
        },
        topicList: [],
        selectedTopicIds: []
      },
      importDialog: {
        visible: false,
        dbId: null,
        modelDb: 'modelmanager',
        addPrefix: true,
        stripPrefix: true,
        keyword: '',
        pageIndex: 1,
        pageSize: 20,
        total: 0,
        mysqlDatabases: [],
        previewing: false,
        importing: false,
        previewList: [],
        selectedNames: []
      }
    };
  },
  mounted() {
    this.loadTopics();
    this.loadCj();
    this.loadAllTopics();
  },
  methods: {
    // ========== 主题配置 ==========
    loadTopics() {
      API.sampleCascade.getTopicPage({
        pageIndex: this.topic.pageIndex,
        pageSize: this.topic.pageSize,
        topicName: this.topic.searchName
      }).then(res => {
        if (res.data && res.data.success) {
          const d = res.data.data;
          this.topic.tableData = d.records || [];
          this.topic.total = d.total || 0;
        }
      });
    },
    onTopicPageChange(page) {
      this.topic.pageIndex = page;
      this.loadTopics();
    },
    openTopicDialog(row) {
      if (row) {
        this.topic.dialogTitle = '编辑主题';
        this.topic.form = { id: row.id, topicName: row.topicName, topicCode: row.topicCode, tableName: row.tableName, fieldList: [] };
        API.sampleCascade.getTopicDetail(row.id).then(res => {
          if (res.data && res.data.success) {
            this.topic.form.fieldList = res.data.data.fieldList || [];
          }
        });
      } else {
        this.topic.dialogTitle = '新增主题';
      }
      this.topic.dialogVisible = true;
    },
    resetTopicForm() {
      this.topic.form = { id: null, topicName: '', topicCode: '', tableName: '', fieldList: [] };
      this.$nextTick(() => {
        if (this.$refs.topicForm) this.$refs.topicForm.clearValidate();
      });
    },
    addField() {
      this.topic.form.fieldList.push({ fieldName: '', fieldAlias: '' });
    },
    removeField(index) {
      this.topic.form.fieldList.splice(index, 1);
    },
    saveTopic() {
      this.$refs.topicForm.validate(valid => {
        if (!valid) return;
        API.sampleCascade.saveTopic(this.topic.form).then(res => {
          if (res.data && res.data.success) {
            this.$message.success('保存成功');
            this.topic.dialogVisible = false;
            this.loadTopics();
            this.loadAllTopics();
          } else {
            this.$message.error(res.data.msg || '保存失败');
          }
        });
      });
    },
    deleteTopic(row) {
      this.$confirm('确定删除该主题？', '提示', { type: 'warning' }).then(() => {
        API.sampleCascade.deleteTopic(row.id).then(res => {
          if (res.data && res.data.success) {
            this.$message.success('删除成功');
            this.loadTopics();
            this.loadAllTopics();
          }
        });
      }).catch(() => {});
    },

    // ========== 从MySQL模型库导入主题 ==========
    openImportDialog() {
      this.importDialog.visible = true;
      this.importDialog.keyword = '';
      this.importDialog.pageIndex = 1;
      this.importDialog.total = 0;
      this.importDialog.previewList = [];
      this.importDialog.selectedNames = [];
      if (this.$refs.previewTable) this.$refs.previewTable.clearSelection();
      if (!this.importDialog.mysqlDatabases.length) {
        this.loadMysqlDatabases();
      }
    },
    loadMysqlDatabases() {
      API.getDatabases().then(res => {
        if (res.data && res.data.success) {
          const list = res.data.data || [];
          this.importDialog.mysqlDatabases = list.filter(d => (d.dbType || '').toUpperCase() === 'MYSQL');
          if (!this.importDialog.mysqlDatabases.length) {
            this.$message.warning('数据源管理中没有启用的MySQL数据源，请先到数据源管理新增');
          } else if (!this.importDialog.dbId) {
            this.importDialog.dbId = this.importDialog.mysqlDatabases[0].value;
          }
        }
      });
    },
    // 连接并预览：重置回第一页并清空跨页勾选（换连接了）
    reconnectAndPreview() {
      this.importDialog.pageIndex = 1;
      if (this.$refs.previewTable) this.$refs.previewTable.clearSelection();
      this.previewMysqlTopics();
    },
    searchMysqlTopics() {
      this.importDialog.pageIndex = 1;
      this.previewMysqlTopics();
    },
    previewMysqlTopics() {
      if (!this.importDialog.dbId) {
        this.$message.warning('请选择MySQL数据源');
        return;
      }
      if (!this.importDialog.modelDb || !this.importDialog.modelDb.trim()) {
        this.$message.warning('请填写模型库名');
        return;
      }
      this.importDialog.previewing = true;
      API.sampleCascade.previewMysqlTopics({
        dbId: this.importDialog.dbId,
        modelDb: this.importDialog.modelDb.trim(),
        addPrefix: this.importDialog.addPrefix,
        stripPrefix: this.importDialog.stripPrefix,
        keyword: this.importDialog.keyword,
        pageIndex: this.importDialog.pageIndex,
        pageSize: this.importDialog.pageSize
      }).then(res => {
        if (res.data && res.data.success) {
          const d = res.data.data || {};
          this.importDialog.previewList = d.records || [];
          this.importDialog.total = d.total || 0;
        } else {
          this.$message.error(res.data.msg || '获取MySQL主题失败');
        }
      }).catch(() => {
        this.$message.error('连接失败，请确认后端服务已更新重启且数据源可达');
      }).finally(() => {
        this.importDialog.previewing = false;
      });
    },
    // 勾选前缀选项后，如果已经预览过就自动重新预览（保留当前页与勾选）
    reprviewIfNeeded() {
      if (this.importDialog.previewList.length || this.importDialog.total) {
        this.previewMysqlTopics();
      }
    },
    onPreviewPageChange(page) {
      this.importDialog.pageIndex = page;
      this.previewMysqlTopics();
    },
    onPreviewSelectionChange(selection) {
      this.importDialog.selectedNames = selection.map(item => item.rawName);
    },
    clearSelected() {
      if (this.$refs.previewTable) this.$refs.previewTable.clearSelection();
      this.importDialog.selectedNames = [];
    },
    doImport() {
      this.importDialog.importing = true;
      API.sampleCascade.importMysqlTopics({
        dbId: this.importDialog.dbId,
        modelDb: this.importDialog.modelDb.trim(),
        addPrefix: this.importDialog.addPrefix,
        stripPrefix: this.importDialog.stripPrefix,
        topicNames: this.importDialog.selectedNames
      }).then(res => {
        if (res.data && res.data.success) {
          const d = res.data.data || {};
          const failCount = (d.failed || []).length;
          this.$message.success(`导入完成：新增 ${d.imported || 0} 个，更新 ${d.updated || 0} 个` +
            (failCount ? `，失败 ${failCount} 个` : ''));
          (d.failed || []).forEach(f => {
            this.$message.error(`主题 ${f.topicName} 导入失败：${f.error}`);
          });
          this.importDialog.visible = false;
          this.loadTopics();
          this.loadAllTopics();
        } else {
          this.$message.error(res.data.msg || '导入失败');
        }
      }).catch(() => {
        this.$message.error('连接失败，请确认后端服务已更新重启且数据源可达');
      }).finally(() => {
        this.importDialog.importing = false;
      });
    },

    // ========== 采集配置 ==========
    loadCj() {
      API.sampleCascade.getCjPage({
        pageIndex: this.cj.pageIndex,
        pageSize: this.cj.pageSize,
        ybName: this.cj.searchName
      }).then(res => {
        if (res.data && res.data.success) {
          const d = res.data.data;
          this.cj.tableData = d.records || [];
          this.cj.total = d.total || 0;
        }
      });
    },
    loadAllTopics() {
      API.sampleCascade.getTopics().then(res => {
        if (res.data && res.data.success) {
          this.cj.topicList = res.data.data || [];
        }
      });
    },
    onCjPageChange(page) {
      this.cj.pageIndex = page;
      this.loadCj();
    },
    getTopicNames(ybList) {
      if (!ybList || !this.cj.topicList.length) return ybList;
      return ybList.split(',').map(id => {
        const topic = this.cj.topicList.find(t => t.id === Number(id.trim()));
        return topic ? topic.topicName : id;
      }).join('、');
    },
    openCjDialog(row) {
      if (row) {
        this.cj.dialogTitle = '编辑采集配置';
        this.cj.form = { id: row.id, ybName: row.ybName, ybList: row.ybList };
        this.cj.selectedTopicIds = row.ybList ? row.ybList.split(',').map(id => Number(id.trim())) : [];
      } else {
        this.cj.dialogTitle = '新增采集配置';
        this.cj.selectedTopicIds = [];
      }
      this.cj.dialogVisible = true;
    },
    resetCjForm() {
      this.cj.form = { id: null, ybName: '', ybList: '' };
      this.cj.selectedTopicIds = [];
      this.$nextTick(() => {
        if (this.$refs.cjForm) this.$refs.cjForm.clearValidate();
      });
    },
    saveCj() {
      this.$refs.cjForm.validate(valid => {
        if (!valid) return;
        this.cj.form.ybList = this.cj.selectedTopicIds.join(',');
        API.sampleCascade.saveCj(this.cj.form).then(res => {
          if (res.data && res.data.success) {
            this.$message.success('保存成功');
            this.cj.dialogVisible = false;
            this.loadCj();
          } else {
            this.$message.error(res.data.msg || '保存失败');
          }
        });
      });
    },
    deleteCj(row) {
      this.$confirm('确定删除该采集配置？', '提示', { type: 'warning' }).then(() => {
        API.sampleCascade.deleteCj(row.id).then(res => {
          if (res.data && res.data.success) {
            this.$message.success('删除成功');
            this.loadCj();
          }
        });
      }).catch(() => {});
    }
  }
};
</script>

<style scoped>
.topic-cj-page {
  display: flex;
  padding: 20px;
  gap: 20px;
  height: 100%;
}
.left-panel {
  flex: 3;
  min-width: 0;
  display: flex;
  flex-direction: column;
}
.right-panel {
  flex: 2;
  min-width: 0;
  display: flex;
  flex-direction: column;
}
.panel-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}
.panel-title {
  font-size: 15px;
  font-weight: bold;
  color: var(--normalWordsClr, #303133);
  white-space: nowrap;
}
.topic-name-cell {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
<style>
.topic-cj-page .el-table { flex: 1; }
.topic-cj-page .el-table__body td { padding: 0 !important; height: 44px !important; line-height: 44px !important; }
.topic-cj-page .el-table__header th { padding: 0 !important; height: 44px !important; line-height: 44px !important; }
</style>
