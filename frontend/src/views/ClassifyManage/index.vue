<template>
  <div class="org-manager">
    <ConfigContent>
      <!-- 左侧边栏 -->
      <template slot="config-aside">
        <el-button class="add-root-button" size="mini" @click="addRoot">添加根节点</el-button>
        <tree
          ref="tree"
          :config="treeConfig"
          :treeData="treeData"
          @onAdd="onTreeAdd"
          @onRemove="onTreeRemove"
          @onEdit="onTreeEdit"
          @nodeClick="handleTreeNodeClick"
        />
      </template>
      <template slot="config-main">
        <!-- 列表栏 -->
        <base-form
          ref="baseForm"
          :ruleForm="ruleForm"
          :formItemConfig="formItemConfig"
          :formItemRuleConfig="formItemRuleConfig"
          :formPreview="formPreview"
        />
        <div slot="config-footer" class="org-footer">
          <el-button size="mini" @click="handleSave">保存</el-button>
          <el-button size="mini" @click="handleCancle">取消</el-button>
        </div>
      </template>
    </ConfigContent>
  </div>
</template>

<script>
/**
 * create by Leo
 * 主界面
 */
import { classifyService } from '@/api/index';
export default {
  components: {
    ConfigContent: () => import('./components/ConfigContent.vue'),
    Tree: () => import('@/components/Tree'),
    BaseForm: () => import('@/components/BaseForm')
  },
  data() {
    return {
      /**
       * 配置项
       */
      treeData: [],
      /**
       * 配置项
       */
      treeConfig: {
        showAdd: true,
        showEdit: true,
        showDelete: true,
        filterable: true,
        defaultExpandAll: false,
        expandOnClickNode: false,
        props: {
          children: 'children',
          label: 'name'
          // isLeaf: function(data, node) {
          //   return !!!parseInt(data.leaf) ? true : false;
          // }
        }
      },
      /**
       * 配置项
       */
      ruleForm: this.createEmptyForm(),
      /**
       * 配置项
       */
      formPreview: true,
      /**
       * 配置项
       */
      formItemConfig: [
        {
          label: '分类名称：',
          type: 'input',
          valueKey: 'name',
          span: 8
        },
        {
          label: '层次码：',
          type: 'input',
          valueKey: 'id',
          span: 8,
          disabled: true
        },
        {
          label: '标识码：',
          type: 'input',
          valueKey: 'flagcode',
          span: 8
        }
      ],
      /**
       * 配置项
       */
      formItemRuleConfig: {},
      /**
       * 配置项
       */
      currentClickNode: null
    };
  },
  methods: {
    /**
     * 创建新表单
     */
    createEmptyForm() {
      return {
        name: '',
        id: '',
        flagcode: ''
      };
    },
    /**
     * @description 获取数据
     */
    getClassifyData(checkedId = '') {
      classifyService.getClassifyFind().then(res => {
        this.treeData = res.data.data;
        this.$nextTick(() => {
          checkedId = checkedId ? checkedId : res.data.data[0].id;
          this.$refs.tree && this.$refs.tree.defaultCheckHandle(checkedId); // 默认选中根节点
          this.getClassifyDetails(checkedId);
        });
      });
    },
    /**
     * @description 获取组织标签详情
     * @id 标签ID
     */
    async getClassifyDetails(id) {
      let resp = await classifyService.getClassifyDetails({ id });
      if (resp.data.success) {
        this.ruleForm = Object.assign({}, this.ruleForm, resp.data.data);
      } else {
        this.$message.warning('获取标签详情失败！');
      }
    },
    /**
     * 树形结构点击事件
     * @param data
     */
    handleTreeNodeClick(data) {
      this.getClassifyDetails(data.id);
    },
    /**
     * 树形结构点击事件
     * @param node
     */
    onTreeAdd(node) {
      this.formPreview = false;
      this.ruleForm = this.createEmptyForm();
      classifyService.getClassifyId({ parentCode: node.data.id }).then(res => {
        if (res.data.success) {
          this.ruleForm.id = res.data.result;
        }
      });
      this.buildFormConfig(node);
      this.currentClickNode = node.data.id;
    },
    /**
     * 树形结构添加事件
     */
    addRoot() {
      this.formPreview = false;
      this.ruleForm = this.createEmptyForm();
      classifyService.getClassifyId({ parentCode: null }).then(res => {
        if (res.data.success) {
          this.ruleForm.id = res.data.result;
        }
      });
    },
    /**
     * 树形结构移除事件
     * @param node
     */
    onTreeRemove(node) {
      // if (Array.isArray(node.childNodes) && node.childNodes.length) {
      //   this.$message.warning('请先删除子节点！');
      // } else {
      this.$confirm('确认删除当前标签吗？', '系统提示', {
        confirmButtonClass: 'confirm-button-class',
        cancelButtonClass: 'cancel-button-class'
      })
        .then(() => {
          classifyService.getClassifyDel({ ids: node.data.id }).then(res => {
            this.$message({
              type: res.data.success ? 'success' : 'warning',
              message: res.data.success ? '删除成功！' : res.data.message
            });
            if (res.data.success) {
              this.getClassifyData();
            }
          });
        })
        .catch(() => {});
      // }
    },
    /**
     * 树形结构编辑事件
     * @param node
     */
    onTreeEdit(node) {
      this.formPreview = false;
      this.currentClickNode = node.data.id;
      this.getClassifyDetails(node.data.id);
    },
    /**
     * 构建表单配置
     * @param node
     */
    buildFormConfig(node) {
      this.ruleForm.pid = node.data.id;
    },
    /**
     * 保存
     */
    handleSave() {
      classifyService.getClassifySave(this.ruleForm).then(res => {
        this.$message({
          type: res.data.success ? 'success' : 'warning',
          message: res.data.success ? '保存成功！' : res.data.message
        });
        if (res.data.success) {
          let id = this.ruleForm.id ? this.ruleForm.id : this.ruleForm.pid;
          this.getClassifyData(id);
          this.resetFormConfig();
        }
      });
    },
    /**
     * 重置表单配置
     */
    resetFormConfig() {
      this.formPreview = true;
    },
    /**
     * 取消事件
     */
    handleCancle() {
      this.resetFormConfig();
      this.getClassifyDetails(this.currentClickNode);
    }
  },
  mounted() {
    this.getClassifyData();
  }
};
</script>

<style lang="scss" scoped>
.org-manager {
  width: 100%;
  height: 100%;
  .add-root-button {
    margin-left: 5px;
    margin-top: 5px;
  }
  .common-tree {
    height: calc(100% - 35px);
  }
  ::v-deep .el-tree {
    font-size: 14px;
  }
  ::v-deep .el-form {
    .el-form-item__label {
      color: #fff;
    }
  }
  .org-footer {
    margin-top: 40px;
    text-align: center;
  }
}
</style>

