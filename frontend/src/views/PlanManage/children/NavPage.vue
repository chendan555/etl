<template>
  <div class="navPage">
    <SideFrame :headTitle="headTitle">
      <p class="addRoot" title="添加分组" slot="titleButton" @click="addRoot">
        <i class="el-icon-circle-plus-outline"></i>
      </p>
      <div class="sideContent" slot="sideContent">
        <div class="searchdiv">
          <el-input placeholder="请输入内容" v-model="searchVal" size="mini">
          </el-input>
        </div>
        <div class="navdiv">
          <div class="flat-list">
            <div
              v-for="item in filteredNavList"
              :key="item.id"
              class="flat-item"
              @click="showProj(item)"
            >
              <span class="item-label" :title="item.name">
                <i class="el-icon-s-home"></i>
                {{ item.name && item.name.length > 8 ? item.name.slice(0, 7) + '...' : item.name }}
              </span>
              <span class="item-actions">
                <el-button type="text" size="mini" class="editNode" @click.stop="handleEdit(item)">
                  <i class="el-icon-edit-outline"></i>
                </el-button>
                <el-button type="text" size="mini" class="removeNode" @click.stop="removeNode(item)">
                  <i class="el-icon-delete"></i>
                </el-button>
              </span>
            </div>
          </div>
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
      ruleForm: { name: '', descs: '', id: '', parentId: -1 }
    };
  },
  computed: {
    filteredNavList() {
      if (!this.searchVal) return this.navListData;
      return this.navListData.filter(item => item.name && item.name.indexOf(this.searchVal) !== -1);
    }
  },
  mounted() {
    this.getNavTree();
  },
  methods: {
    showProj(data) {
      this.$eventBus.$emit('filterPlanList', data.id || '', data.name);
    },
    createEmptyForm() {
      return {
        name: '',
        descs: '',
        id: '',
        parentId: -1
      };
    },
    getNavTree() {
      this.$API.getPlanNavTree().then(res => {
        // 兼容不同响应结构：axios拦截器可能已解包或未解包
        const result = res.data || res;
        const list = result.data || result;
        console.log('[getNavTree] result:', result, 'list:', list);
        if (Array.isArray(list)) {
          this.navListData = [...list];
          this.$emit('navListTree', this.navListData);
        }
      });
    },
    removeNode(item) {
      this.$confirm('确认删除当前分组吗？', '系统提示')
        .then(() => {
          dataRules.apiDelFormatTree({ ids: item.id }).then(res => {
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
    },
    addRoot() {
      this.projDialog.visible = true;
      this.projDialog.title = '新建方案分组';
      this.ruleForm = this.createEmptyForm();
    },
    handleEdit(item) {
      this.projDialog.visible = true;
      this.projDialog.title = '修改方案分组';
      this.ruleForm = Object.assign(this.createEmptyForm(), item);
    },
    closeProjDialog() {
      this.projDialog.visible = false;
      this.ruleForm = this.createEmptyForm();
      this.$refs['form'].clearValidate();
    },
    formValidate() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          dataRules.apiSaveFormatTree(this.ruleForm).then(res => {
            this.$message({
              type: res.data.success ? 'success' : 'warning',
              message: res.data.success ? '保存成功！' : res.data.message
            });
            this.getNavTree();
            this.closeProjDialog();
          });
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
      .flat-list {
        .flat-item {
          display: flex;
          align-items: center;
          justify-content: space-between;
          padding: 6px 12px;
          cursor: pointer;
          &:hover {
            background: var(--currentRow, #186097);
          }
          .item-label {
            flex: 1;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            // 原为 #333，暗色背景下看不清，跟随主题文字色
            color: $normalWordsClr;
            i {
              margin-right: 4px;
            }
          }
          .item-actions {
            flex-shrink: 0;
            opacity: 0;
            transition: opacity 0.2s;
          }
          &:hover .item-actions {
            opacity: 1;
          }
        }
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
