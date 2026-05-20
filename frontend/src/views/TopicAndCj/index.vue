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
  color: #303133;
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
