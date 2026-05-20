<template>
  <div class="cj-config">
    <div class="cj-header">
      <el-button type="primary" size="small" @click="openDialog()">新增</el-button>
      <div style="margin-left: auto; display: flex; align-items: center;">
        <el-input
          v-model="searchName"
          placeholder="搜索采集名称"
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
      <el-table-column prop="ybName" label="采集名称" min-width="1" />
      <el-table-column label="关联主题名称" min-width="2">
        <template slot-scope="scope">
          <el-tooltip :content="getTopicNames(scope.row.ybList)" placement="top" :disabled="!getTopicNames(scope.row.ybList)">
            <span class="topic-name-cell">{{ getTopicNames(scope.row.ybList) }}</span>
          </el-tooltip>
        </template>
      </el-table-column>
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

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px" :close-on-click-modal="false" @closed="resetForm">
      <el-form ref="cjForm" :model="form" :rules="rules" label-width="100px" size="small">
        <el-form-item label="采集名称" prop="ybName">
          <el-input v-model="form.ybName" placeholder="请输入采集名称" />
        </el-form-item>
        <el-form-item label="关联主题" prop="ybList">
          <el-select v-model="selectedTopicIds" multiple filterable placeholder="请选择主题" style="width: 100%">
            <el-option
              v-for="item in topicList"
              :key="item.id"
              :label="item.topicName"
              :value="item.id"
            />
          </el-select>
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
  name: 'CjConfig',
  data() {
    return {
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
    };
  },
  mounted() {
    this.loadData();
    this.loadTopics();
  },
  methods: {
    loadData() {
      API.sampleCascade.getCjPage({
        pageIndex: this.pageIndex,
        pageSize: this.pageSize,
        ybName: this.searchName
      }).then(res => {
        if (res.data && res.data.success) {
          const d = res.data.data;
          this.tableData = d.records || [];
          this.total = d.total || 0;
        }
      });
    },
    loadTopics() {
      API.sampleCascade.getTopics().then(res => {
        if (res.data && res.data.success) {
          this.topicList = res.data.data || [];
        }
      });
    },
    getTopicNames(ybList) {
      if (!ybList || !this.topicList.length) return ybList;
      return ybList.split(',').map(id => {
        const topic = this.topicList.find(t => t.id === Number(id.trim()));
        return topic ? topic.topicName : id;
      }).join('、');
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
        this.dialogTitle = '编辑采集配置';
        this.form = { id: row.id, ybName: row.ybName, ybList: row.ybList };
        this.selectedTopicIds = row.ybList ? row.ybList.split(',').map(id => Number(id.trim())) : [];
      } else {
        this.dialogTitle = '新增采集配置';
        this.selectedTopicIds = [];
      }
      this.dialogVisible = true;
    },
    resetForm() {
      this.form = { id: null, ybName: '', ybList: '' };
      this.selectedTopicIds = [];
      this.$nextTick(() => {
        if (this.$refs.cjForm) this.$refs.cjForm.clearValidate();
      });
    },
    handleSave() {
      this.$refs.cjForm.validate(valid => {
        if (!valid) return;
        this.form.ybList = this.selectedTopicIds.join(',');
        API.sampleCascade.saveCj(this.form).then(res => {
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
      this.$confirm('确定删除该采集配置？', '提示', { type: 'warning' }).then(() => {
        API.sampleCascade.deleteCj(row.id).then(res => {
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
.cj-config {
  padding: 20px;
}
.cj-header {
  display: flex;
  align-items: center;
}
.topic-name-cell {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
