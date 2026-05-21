<template>
  <div class="navPage">
    <SideFrame :headTitle="headTitle">
      <!-- <el-button
        size="mini"
        type="text"
        icon="el-icon-circle-plus-outline"
        @click="addRoot"
        title="添加根级目录"
        slot="titleButton"
      ></el-button>-->
      <p class="addRoot" title="添加根级目录" slot="titleButton" @click="addRoot">
        <i class="el-icon-circle-plus-outline"></i>
      </p>
      <div class="sideContent" slot="sideContent">
        <div class="searchdiv">
          <el-input placeholder="请输入内容" v-model="searchVal" size="mini">
            <!-- <el-button slot="append" icon="el-icon-search"></el-button> -->
          </el-input>
        </div>
        <div class="navdiv">
          <el-tree
            ref="tree"
            :data="navTree"
            node-key="id"
            :props="{
              label: 'name'
            }"
            @node-click="showProj"
            :filter-node-method="filterNode"
          >
            <!-- :expand-on-click-node="false" -->
            <!-- default-expand-all -->
            <span class="custom-tree-node" slot-scope="{ node, data }">
              <span :title="node.label">
                <i class="el-icon-s-home"></i>
                {{ node.label.length>8?node.label.slice(0, 7)+'...' : node.label }}
              </span>
              <span>
                <!-- <el-button type="text" size="mini" class="addNode" @click="handleAdd(node)">
                  <i class="el-icon-circle-plus-outline"></i>
                </el-button>-->
                <el-button type="text" size="mini" class="editNode" @click="handleEdit(node)">
                  <i class="el-icon-edit-outline"></i>
                </el-button>
                <el-button
                  type="text"
                  size="mini"
                  class="removeNode"
                  @click="() => removeNode(node, data)"
                >
                  <i class="el-icon-delete"></i>
                </el-button>
              </span>
            </span>
          </el-tree>
        </div>
      </div>
    </SideFrame>
    <el-dialog
      :title="projDialog.title"
      :visible.sync="projDialog.visible"
      @closed="closeProjDialog"
      width="50%"
      :show-close="false"
    >
      <div class="content">
        <el-form ref="form" :model="ruleForm" label-width="120px" size="small" :rules="rules">
          <el-form-item label="方案分组名称" prop="name">
            <el-input v-model="ruleForm.name"></el-input>
          </el-form-item>
          <el-form-item label="描述">
            <el-input type="textarea" v-model="ruleForm.descs"></el-input>
          </el-form-item>
          <!-- <el-form-item label="标签">
            <AddTag :tagList="tagList" :labelList="projDialog.data.proLabel"></AddTag>
          </el-form-item>-->
        </el-form>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="formValidate">确 定</el-button>
        <el-button type="info" @click="closeProjDialog">取 消</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import SideFrame from '@/components/SideFrame/index.vue';
import AddTag from '@/components/AddTag';
// import axios from '@/api/base/http';
import { dataRules } from '@/api/';
export default {
  name: 'navPage',
  components: { SideFrame, AddTag },
  data() {
    return {
      rules: {
        name: [
          {
            required: true,
            message: '请输入方案分类名称',
            trigger: 'blur'
          }
        ]
      },
      headTitle: {
        imgUrl: 'taskManage',
        title: '方案管理'
      },
      navListData: [],
      projDialog: {
        title: '新建方案分组',
        visible: false
      },
      tagList: [],
      searchVal: '',
      newTagName: '',
      chsTagName: '',
      targetNode: {},
      showNavList: [
        {
          label: '数据预处理方案',
          children: []
        }
      ],
      ruleForm: this.createEmptyForm()
    };
  },
  computed: {
    formList() {
      return this.projDialog.data;
    },
    // 添加根级数据
    navTree() {
      if (this.navListData.length === 0) return [];
      // const treeData = [{
      //   id: '',
      //   name: '全部',
      //   children: this.navListData
      // }];
      return this.navListData;
    }
  },
  watch: {
    searchVal(val) {
      this.$refs.tree.filter(val);
    }
  },
  mounted() {
    this.getNavTree();
  },
  methods: {
    showProj(data, node) {
      this.$eventBus.$emit('filterPlanList', data.id || '', data.name);
    },
    createEmptyForm() {
      return {
        name: '',
        descs: '',
        id: '',
        parentId: []
      };
    },
    /**
     * 数据菜单树列表
     */
    getNavTree() {
      this.$API.getPlanNavTree().then(({ data }) => {
        const { code } = data;
        if (code === 200) {
          const { data: list } = data;
          this.navListData = [...list];
          this.$emit('navListTree', this.navListData);
          // console.log(this.navListData);
        }
      });
    },
    // 查询树状图列表
    filterNode(value, data) {
      if (!value) return true;
      return data.name.indexOf(value) !== -1;
    },
    removeNode(node, data) {
      if (Array.isArray(node.childNodes) && node.childNodes.length) {
        this.$message.warning('请先删除子节点！');
      } else {
        this.$confirm('确认删除当前节点吗？', '系统提示')
          .then(() => {
            dataRules.apiDelFormatTree({ ids: node.data.id }).then(res => {
              this.$message({
                type: res.data.success ? 'success' : 'warning',
                message: res.data.success ? '删除成功！' : res.data.message
              });
              if (res.data.success) {
                this.getNavTree();
              }
            });
          })
          .catch(() => {});
      }
    },
    addRoot() {
      this.projDialog.visible = true;
      this.projDialog.title = '新建方案分组';
      this.ruleForm = this.createEmptyForm();
      this.ruleForm.parentId = 0;
    },
    handleAdd(node) {
      this.projDialog.visible = true;
      this.projDialog.title = '新建方案分组';
      this.ruleForm = this.createEmptyForm();
      this.ruleForm.parentId = node.data.id;
    },
    handleEdit(node) {
      this.projDialog.visible = true;
      this.projDialog.title = '修改方案分组';
      this.ruleForm = Object.assign(this.createEmptyForm(), node.data);
      this.ruleForm.parentId = node.parent.data.id; // 父级ID
    },

    closeProjDialog() {
      this.projDialog.visible = false;
      this.ruleForm = this.createEmptyForm();
      this.$refs['form'].clearValidate();
    },

    formValidate(valid) {
      this.$refs['form'].validate(valid => {
        if (valid) {
          console.log(this.ruleForm);
          dataRules.apiSaveFormatTree(this.ruleForm).then(res => {
            this.$message({
              type: res.data.success ? 'success' : 'warning',
              message: res.data.success ? '保存成功！' : res.data.message
            });
            this.getNavTree();
            this.closeProjDialog();
          });
        } else {
          console.log('error submit!!');
          return false;
        }
      });
    }
  }
};
</script>
<style lang="scss" scoped>
@import '@/styles/theme.scss';
.navPage {
  width: 100%;
  height: 100%;
  .addRoot {
    color: $whiteWordClr;
  }
  .sideContent {
    width: 100%;
    height: 100%;
    // padding: 5px;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    overflow-y: auto;
    .searchdiv {
      width: 100%;
      height: 30px;
    }
    .navdiv {
      flex: 1;
      overflow: hidden;
      overflow-y: auto;
      ::v-deep .el-button--mini,
      ::v-deep .el-button--small {
        font-size: 14px;
      }
    }
  }
  ::v-deep .el-form {
    // .el-form-item__label {
    //   color: $normalWordsClr;
    // }
    .add {
      width: 100%;
      display: flex;
      margin-bottom: 10px;
      .el-input {
        width: 200px;
        margin-right: 10px;
      }
    }
    .select {
      width: 100%;
      margin-bottom: 10px;
      .el-select {
        width: 100%;
      }
    }
    .showTag {
      .el-tag {
        margin-right: 5px;
      }
    }
  }
}
</style>
