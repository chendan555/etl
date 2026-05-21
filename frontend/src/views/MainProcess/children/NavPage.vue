<template>
  <div class="navPage">
    <div class="headTitle">
      <div class="name">
        <img src="~@/assets/mainPage/taskManage.png" alt>
        <p>任务管理</p>
      </div>
      <div class="handle">
        <img src="~@/assets/mainPage/exchangeList.png" alt @click="dialog.visible = true">
      </div>
    </div>
    <div class="content">
      <el-tree :data="navListData" node-key="id" default-expand-all :expand-on-click-node="false">
        <span class="custom-tree-node" slot-scope="{ node, data }">
          <span
            :title="node.label"
          >{{ node.label.length > 12 ? node.label.slice(0, 12) + '...' : node.label }}</span>
          <span v-if="node.disabled">
            <el-tag size="mini" type="success" v-if="node.disabled === '1'">成功</el-tag>
            <el-tag size="mini" v-else>待执行</el-tag>
          </span>
        </span>
      </el-tree>
    </div>
    <div class="footer">
      <p
        v-for="(item, index) in navType"
        :key="index"
        :title="item.name"
        @click="chgNavType(item, index)"
        :class="[crtNavType === index ? 'chsd' : '']"
      >
        <svg-icon :icon-class="item.img"></svg-icon>
      </p>
    </div>
    <el-dialog title="任务列表" :visible.sync="dialog.visible" width="80%">
      <div class="content"></div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialog.visible = false">取 消</el-button>
        <el-button type="primary" @click="dialog.visible = false">确 定</el-button>
      </span>
    </el-dialog>
    <!-- <div class="task-manage-content">
      <LineTree ref="tree" :lineTreeConfig="taskTreeConfig" @showInfor="handleSelected"></LineTree>
    </div>-->
  </div>
</template>
<script>
import LineTree from '@/components/LineTree/index.vue';
import { dataRules } from '@/api/';
import axios from '@/api/base/http';
export default {
  name: 'navPage',
  components: {
    LineTree
  },
  data() {
    return {
      navType: [
        {
          name: '任务',
          img: 'nav_task'
        },
        {
          name: '设备',
          img: 'nav_equip'
        },
        {
          name: '单位',
          img: 'nav_unit'
        },
        {
          name: '地点',
          img: 'nav_position'
        }
      ],
      crtNavType: 0,
      navListData: [],
      dialog: {
        visible: false
      },
      taskTreeConfig: {
        //任务树形菜单配置项
        navData: [],
        defaultProps: {
          label: 'name',
          children: 'childs'
        },
        defaultKeys: [],
        loading: false
      }
    };
  },
  created() {
    this.initTaskList();
  },
  methods: {
    // 切换任务列表类型类型
    chgNavType(item, index) {
      this.crtNavType = index;
      this.initTaskList();
    },
    // 获取任务列表
    initTaskList() {
      axios.get('/api/getTaskListData').then(res => {
        console.log(res);
        this.navListData = res.data.data;
      });
    },
    getDefaultTask() {
      this.taskTreeConfig.loading = true;
      dataRules
        .getTaskList({ pageSize: 100, pageIndex: 1, noAuthStatus: 1 })
        .then(res => {
          this.taskTreeConfig.loading = false;
          if (res.success && Array.isArray(res.data)) {
            const firstTask = res.data[0];
            const { currentTask } = this.$store.state;
            const hasCurrentTask = currentTask.id ? res.data.some(x => x.id == currentTask.id) : false;
            hasCurrentTask ? this.getTaskDetail(currentTask) : this.getTaskDetail(firstTask);
          } else {
            this.$interaction.showError('获取任务失败!');
          }
        })
        .catch(() => {
          this.$interaction.showError('获取任务失败!');
          this.taskTreeConfig.loading = false;
        });
    },

    async getTaskDetail(task) {
      try {
        this.$central.$emit('handleTaskList', task);
        if (!task) {
          return;
        }
        let resp1 = await taskService.getTaskNodes(task.id);
        let resp2 = await taskService.getTaskNodeDetails(task.id);

        if (resp1.success && resp2.success) {
          let treeData = resp1.data.taskTree;
          let taskDetails = resp2.data;
          this.buildTreeData(treeData[0], taskDetails);
          Object.assign(treeData[0].data, taskDetails.taskData);
          this.taskTreeConfig.navData = treeData;
          // this.notifyCurrentTask(treeData[0].data);
        }
      } catch (error) {}
    },

    buildTreeData(origin, details) {
      if (Array.isArray(origin.childs)) {
        origin.childs.forEach(child => {
          let result = details.flowData.find(item => item.id == child.id);
          if (result) {
            child.data = Object.assign(child.data, result);
          }
          this.buildTreeData(child, details);
        });
      }
    }
  }
};
</script>
<style lang="scss" scoped>
@import '@/styles/theme.scss';
.navPage {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  .headTitle {
    width: 100%;
    height: 40px;
    display: flex;
    justify-content: space-between;
    padding: 0 5px;
    background: $sideBarTtlBgClr;
    > div {
      display: flex;
      align-items: center;
    }
    .name p {
      font-weight: bold;
      margin-left: 5px;
    }
    .handle {
      cursor: pointer;
    }
  }
  .content {
    flex: 1;
    padding: 5px;
    .el-tree {
      background: none;
      color: $normalWordsClr;
      font-size: 14px;
      ::v-deep .el-tree-node__children {
        .el-tree-node__content {
          padding-left: 10px !important;
        }
        .el-tree-node__children .el-tree-node__content {
          padding-left: 20px !important;
        }
      }
      ::v-deep .custom-tree-node {
        width: 100%;
        display: flex;
        justify-content: space-between;
        > span:first-child {
          line-height: 28px;
          // font-size: 12px;
        }
      }
      ::v-deep .el-tree-node:hover {
        > .el-tree-node__content {
          background: none;
        }
      }
      ::v-deep .el-tree-node.is-current {
        .el-tree-node__content {
          background: $crtTreeNode !important;
        }
      }
    }
  }
  .footer {
    width: 100%;
    height: 40px;
    background: #2b352b;
    display: flex;
    justify-content: space-between;
    border-top: 1px solid #808353;
    > p {
      flex: 1;
      border-right: 1px solid #808353;
      display: flex;
      justify-content: center;
      align-items: center;
      cursor: pointer;
      font-size: 20px;
    }
    > p.chsd {
      color: #e6e26b;
    }
    > p:last-child {
      border: none;
    }
  }
}
</style>
