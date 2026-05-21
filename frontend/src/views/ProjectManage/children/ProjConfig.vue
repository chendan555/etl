<template>
  <div class="projConfig">
    <SideFrame :headTitle="headTitle">
      <div class="sideContent" slot="sideContent">
        <div class="infordiv">
          <div class="item" v-for="(item, index) in crtLabelList" :key="index">
            <p class="name">{{item.label}}：</p>
            <p class="cont">{{item.value | chgType}}</p>
          </div>
        </div>
        <!-- <el-tabs v-model="activeName" type="card" @tab-click="handleClick">
                    <el-tab-pane label="基本信息" name="first">
                        <div class="infordiv">
                            <div class="item" v-for="(item, index) in crtLabelList" :key="index">
                                <p class="name">{{item.label}}：</p>
                                <p class="cont">{{item.value | chgType}}</p>
                            </div>
                        </div>
                    </el-tab-pane>
                    <el-tab-pane label="数据源" name="second">
                        <base-table
                            :isShowOperation="dataSource.isShowOperation"
                            :tableColumnConfig="dataSource.tableColumnConfig"
                            :tableData="dataSource.tableData"
                            operationWidth="56"
                            >
                            <div slot="operation" slot-scope="scope">
                                <div class="table-operation">
                                    <span title="删除" slot="reference" @click="removeItem(scope)">
                                        <i class="el-icon-delete"></i>
                                    </span>
                                </div>
                            </div>
                        </base-table>
                    </el-tab-pane>
                    <el-tab-pane label="数据集" name="third">
                        <base-table
                            :isShowOperation="dataCollection.isShowOperation"
                            :tableColumnConfig="dataCollection.tableColumnConfig"
                            :tableData="dataCollection.tableData"
                            operationWidth="56"
                            >
                            <div slot="operation" slot-scope="scope">
                                <div class="table-operation">
                                    <span title="删除" slot="reference" @click="removeItem(scope)">
                                        <i class="el-icon-delete"></i>
                                    </span>
                                </div>
                            </div>
                        </base-table>
                    </el-tab-pane>
        </el-tabs>-->
      </div>
    </SideFrame>
  </div>
</template>
<script>
import SideFrame from '@/components/SideFrame/index.vue';
import BaseTable from '@/components/BaseTable/index.vue';
import { projInforLabel, groupInforLabel, dataSource, dataCollection } from './config.js';
export default {
  name: 'projConfig',
  components: { SideFrame, BaseTable },
  data() {
    return {
      headTitle: {
        imgUrl: 'taskInfor',
        title: '项目配置'
      },
      projInforLabel: this.$utils.deepCopy(projInforLabel),
      groupInforLabel: this.$utils.deepCopy(groupInforLabel),
      activeName: 'first',
      dataSource,
      dataCollection,
      visible: false,
      crtLabelList: projInforLabel
    };
  },
  mounted() {
    this.$eventBus.$on('showProjInfor', data => {
      if (data) {
        this.getProjInfor(data);
        console.log(data);
        // this.getProjDataSource(data.idTaskGroup);
      }
    });
    this.$eventBus.$on('procDetailInfor', val => {
      this.crtLabelList = val;
    });
  },
  methods: {
    handleClick(tab, event) {},
    removeItem(scope) {
      this.$confirm('此操作将删除该数据源, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        center: true
      })
        .then(() => {
          this.dataSource.tableData.splice(scope.row.$index, 1);
          this.$message({
            type: 'success',
            message: '删除成功!'
          });
        })
        .catch(() => {});
    },
    /**
     * 获取项目目录信息
     */
    getProjInfor(data) {
      // 判断是否为分组根目录
      if (data.isRoot) {
        // 根据根目录下分组信息及统计信息统计根目录的流程总数
        let countTrans = 0;
        if (data.children && data.children.length) {
          data.children.forEach(trans => {
            countTrans += trans.countTrans;
          });
        }
        data.countTrans = countTrans;
        this.groupInforLabel.forEach(ele => {
          ele.value = data[ele.prop];
        });
        this.crtLabelList = this.groupInforLabel;
      } else {
        // 展示流程分组信息及流程数据统计信息
        this.projInforLabel.forEach(ele => {
          ele.value = data[ele.prop];
        });
        this.crtLabelList = this.projInforLabel;
      }
    },
    // 获取项目目录信息
    getProjDataSource(id) {
      let params = {
        taskGroupId: id
      };
      this.$API.getProjDataSource(params).then(res => {
        this.dataSource.tableData = res.data.data;
      });
    }
  },
  filters: {
    chgType(val) {
      if (Array.isArray(val)) {
        let res = '';
        val.forEach((ele, index) => {
          res += ele.name;
          if (index !== val.length - 1) {
            res += ',';
          }
        });
        return res;
      } else {
        return val;
      }
    }
  }
};
</script>
<style lang="scss" scoped>
@import '@/styles/theme.scss';
.projConfig {
  width: 100%;
  height: 100%;
  .sideContent {
    width: 100%;
    // height: 100%;
    ::v-deep .el-tabs__header {
      border: none;
      .el-tabs__nav {
        width: 100%;
        display: flex;
        border: none;
        .el-tabs__item {
          flex: 1;
          border: none;
          text-align: center;
          font-size: 12px;
        }
      }
    }
    ::v-deep .el-tabs__content {
      ::v-deep .cell,
      ::v-deep .cell span {
        font-size: 12px !important;
      }
    }
    .infordiv {
      width: 100%;
      height: 100%;
      .item {
        width: 100%;
        color: $formLabelClr;
        line-height: 20px;
        margin: 10px 0;
        display: flex;
        font-size: 12px;
        p.name {
          width: 84px;
          text-align: right;
        }
        p.cont {
          flex: 1;
        }
      }
    }
  }
}
</style>
