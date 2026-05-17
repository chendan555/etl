<template>
  <div class="navPage">
    <SideFrame :headTitle="headTitle">
      <el-button
        class="handleBtn"
        size="mini"
        type="text"
        icon="el-icon-circle-plus-outline"
        @click="showDialog"
        title="添加根级目录"
        slot="titleButton"
      ></el-button>
      <div class="sideContent" slot="sideContent">
        <div class="searchdiv">
          <el-input placeholder="请输入内容" v-model="searchVal" size="mini">
            <!-- <el-button slot="append" icon="el-icon-search"></el-button> -->
          </el-input>
        </div>
        <div class="navdiv">
          <el-tree
            ref="tree"
            :data="treeData"
            :props="treeProps"
            node-key="idTaskGroup"
            @node-click="showProj"
            :filter-node-method="filterNode"
            :expand-on-click-node="false"
            default-expand-all
          >
            <span class="custom-tree-node" slot-scope="{ node, data }">
              <span :title="node.label">
                <i class="el-icon-s-home"></i>
                {{ node.label.length > 25 ? node.label.slice(0, 15) + '...' : node.label }}
              </span>
              <span>
                <!-- <el-button
                  v-show="data.isRoot"
                  type="text"
                  size="mini"
                  class="addNode"
                  @click="showDialog(node, data, 'add')"
                >
                  <i class="el-icon-circle-plus-outline"></i>
                </el-button>-->
                <el-button
                  v-show="!data.isRoot"
                  type="text"
                  size="mini"
                  class="editNode"
                  @click="editGroupDialog(node, data)"
                >
                  <i class="el-icon-edit-outline"></i>
                </el-button>
                <el-button
                  v-show="!data.isRoot"
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
      width="50%"
      @closed="closeDialog"
      append-to-body
      modal-append-to-body
      :show-close="false"
    >
      <div class="content">
        <el-form ref="form" :model="formList" label-width="80px" size="small" :rules="rules">
          <el-form-item label="分组名称" prop="taskGroupName">
            <el-input v-model="formList.taskGroupName"></el-input>
          </el-form-item>
          <el-form-item label="描述">
            <el-input type="textarea" v-model="formList.taskDesc"></el-input>
          </el-form-item>
          <!-- <el-form-item label="标签">
            <AddTag ref="cleardata" @resTagName="resTagName" :tagList="tagList" :labelList="formList.projLabel"></AddTag>
          </el-form-item>-->
        </el-form>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="handleSave">确 定</el-button>
        <el-button type="info" @click="closeDialog">取 消</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import SideFrame from '@/components/SideFrame/index.vue';
import AddTag from '@/components/AddTag';
export default {
  name: 'navPage',
  components: { SideFrame, AddTag },
  data() {
    return {
      restagName: [], // 合并输入添加标签和下来选择标签
      treeProps: {
        label: 'taskGroupName',
        children: 'children'
      },
      headTitle: {
        imgUrl: 'taskManage',
        title: '流程分组管理'
      },
      treeData: [
        /* {
          idTaskGroup: 'TEMP',
          isRoot: true,
          taskGroupName: '模板',
          taskDesc: '',
          children: [],
          type: '1',
          disabled: true // 配置处理流程选择分组时，模板分组不可选
        },
        {
          idTaskGroup: 'ETL',
          isRoot: true,
          taskGroupName: '预处理',
          taskDesc: '',
          children: [],
          type: '2'
        },
        {
          idTaskGroup: 'SJFX',
          isRoot: true,
          taskGroupName: '挖掘分析',
          taskDesc: '',
          children: [],
          type: '3'
        } */
      ],
      projDialog: {
        title: '新增分组',
        visible: false,
        data: {
          taskGroupName: '',
          taskDesc: ''
        }
      },
      rules: {
        taskGroupName: [
          {
            required: true,
            message: '请输入项目名称',
            trigger: 'blur'
          }
        ]
      },
      tagList: [],
      searchVal: '',
      targetNode: {},
      modifyProjId: '',
      curTreeNodeData: null // 当前选择的树节点
    };
  },
  computed: {
    formList() {
      return this.projDialog.data;
    }
  },
  watch: {
    // 菜单筛选值
    searchVal(val) {
      this.$refs.tree.filter(val);
    }
  },
  created() {
    this.initTreeData();
  },
  methods: {
    // 基本信息展示组件展示项目基本信息
    showProj(data, node) {
      this.$eventBus.$emit('targetNode', data.idTaskGroup);
      this.$eventBus.$emit('showProjInfor', data);
      this.$store.dispatch('setTaskName', data.taskGroupName);
      // 设置节点选中
      if (!node) {
        const { taskGroupCode } = data;
        this.$refs.tree.setCurrentKey(taskGroupCode);
      }
    },
    initTreeData() {
      this.$API.getProjListData().then(res => {
        if (res.data.code == 200 && Array.isArray(res.data.data)) {
          this.treeData = res.data.data;
          // res.data.data.forEach(item => {
          //   if (item.type == 1) {
          //     this.treeData[0].children = item.children;
          //     this.$store.dispatch('setModelMenu', item.children);
          //   } else if (item.type == 2) {
          //     this.treeData[1].children = item.children;
          //   } else if (item.type == 3) {
          //     this.treeData[2].children = item.children;
          //   }
          // });
          this.$refs.tree.setCurrentKey(res.data.data[0].idTaskGroup);
          this.$emit('navListTree', this.treeData);
        }
      });
    },
    // 项目菜单筛选
    filterNode(value, data) {
      if (!value) return true;
      return data[this.treeProps.label ? this.treeProps.label : 'label'].indexOf(value) !== -1;
    },
    // 删除项目菜单节点
    removeNode(node, data) {
      this.$confirm('确认是否删除该项目？', '提示', {
        distinguishCancelAndClose: true,
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      })
        .then(() => {
          this.$API.removeProjFolder([data.idTaskGroup]).then(r => {
            const res = r.data;
            if (res.success) {
              this.$message.success('删除成功！');
              this.initTreeData();
            } else {
              this.$message.error('删除失败！');
            }
          });
        })
        .catch(action => {});
    },
    /**
     * @description 展示新增项目弹框
     */
    showDialog() {
      Object.assign(this.projDialog.data, {
        taskGroupName: '',
        taskDesc: ''
      });
      this.projDialog.title = '新增分组';
      this.projDialog.visible = true;
    },
    /**
     * @description 编辑分组信息
     * @param node
     * @param data
     * @param type
     */
    editGroupDialog(node, data) {
      Object.assign(this.projDialog.data, data);
      this.projDialog.title = '修改分组';
      this.projDialog.visible = true;
    },
    // 关闭弹框并新建或修改项目信息
    handleSave() {
      this.$refs.form.validate(valid => {
        if (valid) {
          if (this.projDialog.title == '新增分组') {
            this.updateProkFolder(this.formList);
          } else {
            this.updateProkFolder(this.formList);
          }
        } else {
          this.$message.warning('请完成输入必填项目。');
        }
      });
    },
    // 查找当前节点
    findNode(dom, nodeId) {
      for (let item of dom) {
        if (item.$treeNodeId === nodeId) {
          this.targetNode = item;
          this.$eventBus.$emit('targetNode', this.targetNode);
          return;
        }
        if (item.children && item.children.length) {
          this.findNode(item.children, nodeId);
        }
      }
    },
    // 关闭弹框内容
    closeDialog() {
      this.projDialog.visible = false;
      this.projDialog.data = {
        taskGroupName: '',
        taskDesc: ''
      };
    },

    // 更新流程分组
    updateProkFolder(params) {
      this.$API.updateProjFolder(params).then(res => {
        if (res.data.code === 200) {
          this.$message.success('操作成功！');
          this.initTreeData();
          this.closeDialog();
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
  .handleBtn {
    width: 18px;
    height: 28px;
    font-size: 16px;
    padding: 0;
    color: #fff;
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
      /deep/ .el-button--mini,
      /deep/ .el-button--small {
        font-size: 14px;
      }
    }
  }
  /deep/ .el-dialog__header {
    border-bottom: 1px solid #394e39;
  }
  /deep/ .el-form {
    .el-form-item__label {
      color: $normalWordsClr;
    }
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

.el-tag + .el-tag {
  margin-left: 10px;
}
.button-new-tag {
  margin-left: 10px;
  height: 32px;
  line-height: 30px;
  padding-top: 0;
  padding-bottom: 0;
}
.input-new-tag {
  width: 90px;
  margin-left: 10px;
  vertical-align: bottom;
}
</style>
