<template>
  <base-dialog v-bind="$attrs" v-on="$listeners" @save="saveConfig">
    <div class="dialogContDiv">
      <div class="groupDiv">
        <el-tree
          ref="tree"
          :data="navListData"
          node-key="id"
          :props="{
              label: 'name'
            }"
          @node-click="showProj"
        >
          <span class="custom-tree-node" slot-scope="{ node, data }">
            <span :title="node.label">
              <i class="el-icon-s-home"></i>
              {{ node.label.length>8?node.label.slice(0, 7)+'...' : node.label }}
            </span>
          </span>
        </el-tree>
      </div>
      <div class="listDiv">
        <div class="headerDiv">
          <el-form ref="form" :model="filterForm" label-width="80px" size="mini" :inline="true">
            <el-form-item label="关键字">
              <el-input v-model="filterForm.searchVal" placeholder="请输入检索关键字"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="searchPlanList">查询</el-button>
              <el-button type="info" @click="cancelSearchPlanList">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
        <div class="tableDiv">
          <el-table
            ref="planList"
            :data="tableConfig.planListData"
            @selection-change="handleSelectionChange"
            style="width: 100%;height: 100%"
          >
            <el-table-column type="selection" width="55"></el-table-column>
            <el-table-column
              v-for="(item, index) in tableConfig.tableColumnConfig"
              :key="index"
              :prop="item.prop"
              :label="item.label"
              :width="item.width"
            ></el-table-column>
          </el-table>
        </div>
        <div class="pageDiv">
          <BasePagination :data="page" @current="currentPage" @sizes="pageSizes"></BasePagination>
        </div>
      </div>
      <div class="choosedDiv">
        <div class="titleDiv">
          <p>
            <span>
              <i class="el-icon-tickets"></i> 已选列表
            </span>
          </p>
        </div>
        <div class="chsdListDiv">
          <p class="item" v-for="(item, index) in getChoosedList" :key="item.id">
            <span>{{index + 1}}</span>
            <span>{{item.name}}</span>
            <span class="delete" @click="deleteItem(item)">
              <i class="el-icon-close"></i>
            </span>
          </p>
        </div>
      </div>
    </div>
  </base-dialog>
</template>
<script >
import BaseDialog from '@/components/BaseDialog/index.vue';
import BasePagination from '@/components/BasePagination/index.vue';
import config from '../config';
export default {
  name: 'dialogCron',
  components: { BaseDialog, BasePagination },
  data() {
    return {
      navListData: [],
      choosedListObj: {},
      /**
       * 方案分组id
       */
      groupId: '',
      /**
       * 筛选表单
       */
      filterForm: {
        searchVal: ''
      },
      /**
       * 列表配置
       */
      tableConfig: {
        planListData: [],
        tableColumnConfig: config.tableColumnConfig
      },
      /**
       * 分页配置信息
       */
      page: {
        pageSize: 20,
        pageNum: 1,
        sizes: [10, 20, 30, 40, 50, 100],
        total: 10
      },
      tableStatus: true
    };
  },
  mounted() {
    this.getNavTree();
    // this.getPlanData();
  },
  computed: {
    getChoosedList() {
      let list = [];
      for (let key in this.choosedListObj) {
        list = list.concat(this.choosedListObj[key]);
      }
      return list;
    }
  },
  methods: {
    saveConfig() {
      this.$emit('closeDialog', this.getChoosedList);
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
          this.groupId = this.navListData[0].id;
          this.getPlanData();
        }
      });
    },
    /**
     * 展示列表
     */
    showProj(data, node) {
      this.groupId = data.id;
      this.getPlanData();
    },
    /**
     * 获取表格的数据
     */
    getPlanData() {
      this.tableStatus = false;
      let { pageNum: start, pageSize: limit } = this.page;
      this.$API
        .getCreatePlanList({
          pageIndex: start,
          pageSize: limit,
          flag: 0,
          groupId: this.groupId,
          name: this.filterForm.searchVal
        })
        .then(res => {
          this.tableConfig.planListData = res.data.data.records.map(item => {
            let statusInfo = '';
            let progress = 0;
            switch (item.status) {
              case '1':
                statusInfo = '未执行';
                progress = 0;
                break;
              case '2':
                statusInfo = '已执行';
                progress = 100;
                break;
            }
            return {
              ...item,
              statusInfo,
              progress
            };
          });
          this.page.total = res.data.data.total;
        })
        .catch(() => {
          this.tableConfig.planListData = [];
        })
        .finally(() => {
          this.$nextTick(() => {
            this.tableStatus = true;
            if (this.choosedListObj.hasOwnProperty(this.groupId)) {
              this.choosedListObj[this.groupId].forEach(ele => {
                let planItem = this.tableConfig.planListData.find(item => item.id === ele.id);
                this.$refs.planList.toggleRowSelection(planItem, true);
              });
            }
          });
        });
    },
    /**
     * 查询
     */
    searchPlanList() {
      this.planListParams.searchVal = this.filterForm.searchVal;
      this.page.pageNum = 1;
      this.getPlanData();
    },
    /**
     * 重置
     */
    cancelSearchPlanList() {
      this.planListParams.searchVal = '';
      this.filterForm.searchVal = '';
      this.getPlanData();
    },
    /**
     * 控制列表勾选项
     * @param list
     */
    handleSelectionChange(list) {
      if (this.tableStatus) {
        this.$set(this.choosedListObj, this.groupId, list);
      }
    },
    /**
     * 分页配置
     * @param e
     */
    currentPage(e) {
      this.page.pageNum = e;
      this.getPlanData();
    },
    /**
     * 分页配置
     * @param e
     */
    pageSizes(e) {
      this.page.pageSize = e;
      this.getPlanData();
    },
    /**
     * 删除已选方案
     * @param item
     */
    deleteItem(item) {
      let index = this.choosedListObj[item.groupId].findIndex(ele => ele.id === item.id);
      let deleteItem = this.choosedListObj[item.groupId].splice(index, 1);
      this.$refs.planList.toggleRowSelection(deleteItem[0]);
    }
  }
};
</script>
<style lang="scss" scoped>
.dialogContDiv {
  width: 100%;
  height: 100%;
  display: flex;
  .groupDiv {
    width: 150px;
    height: 100%;
  }
  .listDiv {
    flex: 1;
    height: 100%;
    margin: 0 10px;
    .headerDiv,
    .pageDiv {
      width: 100%;
      height: 40px;
      display: flex;
      justify-content: flex-end;
      align-items: center;
    }
    .tableDiv {
      width: 100%;
      height: calc(100% - 80px);
    }
  }
  .choosedDiv {
    width: 400px;
    background: rgba(0, 0, 0, 0.5);
    height: 100%;
    color: #fff;
    .titleDiv {
      width: 100%;
      height: 40px;
      line-height: 40px;
      > p {
        text-indent: 4px;
      }
    }
    .chsdListDiv {
      width: 100%;
      height: calc(100% - 40px);
      p.item {
        height: 32px;
        line-height: 32px;
        padding: 0 5px;
        display: flex;
        justify-content: space-between;
        .delete:hover {
          display: inline-block;
          color: #ff0000;
          cursor: pointer;
        }
      }
    }
  }
}
.pageDiv {
  width: 100%;
  height: 40px;
}
</style>
