<template>
  <el-dialog :title="title" :visible.sync="visible" @closed="closedCallback">
    <div class="content">
      <el-form ref="form" :model="form" label-width="80px" :rules="rules">
        <el-row>
          <el-col :span="12">
            <el-form-item label="流程名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入流程名称"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="流程分组" prop="secretLevel">
              <el-cascader
                v-model="form.secretLevel"
                :props="{value: 'idTaskGroup', label: 'taskGroupName'}"
                :options="dataList"
              ></el-cascader>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>
    <div slot="footer">
      <el-button type="primary" @click="formValidate">确 定</el-button>
      <el-button type="info" @click="closedCallback">取 消</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { copyRules } from './config.js';
// import API from '@/api/index';
export default {
  name: 'copyFlow',
  data() {
    return {
      rules: copyRules,
      title: '模板复制', //标题
      visible: true, // 弹窗的打开和关闭
      form: {}
    };
  },
  computed: {},
  props: ['itemCopy', 'dataList'],
  methods: {
    // 隐藏dialog回调
    closedCallback() {
      this.visible = false;
      this.reset();
      this.$refs['form'].clearValidate();
      this.$emit('close');
    },
    // 重置表单
    reset() {
      this.form = {
        name: '',
        secretLevel: []
      };
    },
    // 确定
    formValidate(valid) {
      let params = {
        ...this.itemCopy,
        name: this.form.name,
        isModel: 0,
        directoryName: `/${this.form.name}`,
        taskGroupId: this.form.secretLevel.toString()
      };
      this.$refs['form'].validate(valid => {
        if (valid) {
          this.$API.projectManage.addCopyTrans(params).then(({ data }) => {
            this.$message({
              message: data.result,
              type: data.success ? 'success' : 'warning'
            });
            this.closedCallback();
          });
        }
      });
    }
  }
};
</script>

