<template>
  <div class="topic-config">
    <div class="topic-header">
      <el-button type="primary" size="small" @click="openDialog()">新增</el-button>
      <div style="margin-left: auto; display: flex; align-items: center;">
        <el-input
          v-model="searchName"
          placeholder="搜索主题名称"
          size="small"
          style="width: 200px"
          clearable
          @clear="loadData"
          @keyup.enter.native="loadData"
        />
        <el-button type="primary" size="small" style="margin-left: 10px" @click="loadData">搜索</el-button>
      </div>
    </div>

    <el-table :data="tableData" border stripe style="width: 100%; margin-top: 10px">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="topicName" label="主题名称" />
      <el-table-column prop="topicCode" label="主题别名" />
      <el-table-column prop="tableName" label="数据库表名" />
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="150" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="openDialog(scope.row)">编辑</el-button>
          <el-button type="text" size="small" style="color: #F56C6C" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

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

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="650px" :close-on-click-modal="false" @closed="resetForm">
      <el-form ref="topicForm" :model="form" :rules="rules" label-width="100px" size="small">
        <el-form-item label="主题名称" prop="topicName">
          <el-input v-model="form.topicName" placeholder="请输入主题名称" />
        </el-form-item>
        <el-form-item label="主题别名" prop="topicCode">
          <el-input v-model="form.topicCode" placeholder="请输入主题别名" />
        </el-form-item>
        <el-form-item label="数据库表名" prop="tableName">
          <el-input v-model="form.tableName" placeholder="请输入数据库表名" />
        </el-form-item>
        <el-form-item label="字段集合">
          <el-button type="primary" size="mini" @click="addField">添加字段</el-button>
          <el-table :data="form.fieldList" border size="mini" style="margin-top: 10px" v-if="form.fieldList.length > 0">
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
        <el-button size="small" @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" size="small" @click="handleSave">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import API from '@/api/index';

export default {
  name: 'TopicConfig',
  data() {
    return {
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
    };
  },
  mounted() {
    this.loadData();
  },
  methods: {
    loadData() {
      API.sampleCascade.getTopicPage({
        pageIndex: this.pageIndex,
        pageSize: this.pageSize,
        topicName: this.searchName
      }).then(res => {
        if (res.data && res.data.success) {
          const d = res.data.data;
          this.tableData = d.records || [];
          this.total = d.total || 0;
        }
      });
    },
    onPageChange(page) {
      this.pageIndex = page;
      this.loadData();
    },
    onSizeChange(size) {
      this.pageSize = size;
      this.pageIndex = 1;
      this.loadData();
    },
    openDialog(row) {
      if (row) {
        this.dialogTitle = '编辑主题';
        this.form = { id: row.id, topicName: row.topicName, topicCode: row.topicCode, tableName: row.tableName, fieldList: [] };
        API.sampleCascade.getTopicDetail(row.id).then(res => {
          if (res.data && res.data.success) {
            this.form.fieldList = res.data.data.fieldList || [];
          }
        });
      } else {
        this.dialogTitle = '新增主题';
      }
      this.dialogVisible = true;
    },
    resetForm() {
      this.form = { id: null, topicName: '', topicCode: '', tableName: '', fieldList: [] };
      this.$nextTick(() => {
        if (this.$refs.topicForm) this.$refs.topicForm.clearValidate();
      });
    },
    addField() {
      this.form.fieldList.push({ fieldName: '', fieldAlias: '' });
    },
    removeField(index) {
      this.form.fieldList.splice(index, 1);
    },
    handleSave() {
      this.$refs.topicForm.validate(valid => {
        if (!valid) return;
        API.sampleCascade.saveTopic(this.form).then(res => {
          if (res.data && res.data.success) {
            this.$message.success('保存成功');
            this.dialogVisible = false;
            this.loadData();
          } else {
            this.$message.error(res.data.msg || '保存失败');
          }
        });
      });
    },
    handleDelete(row) {
      this.$confirm('确定删除该主题？', '提示', { type: 'warning' }).then(() => {
        API.sampleCascade.deleteTopic(row.id).then(res => {
          if (res.data && res.data.success) {
            this.$message.success('删除成功');
            this.loadData();
          }
        });
      }).catch(() => {});
    }
  }
};
</script>

<style scoped>
.topic-config {
  padding: 20px;
}
.topic-header {
  display: flex;
  align-items: center;
}
</style>
