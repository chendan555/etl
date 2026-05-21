<template>
  <div class="navPart">
    <SideFrame>
      <div class="searchdiv" slot="sideMenu">
        <el-input placeholder="输入关键字进行过滤" v-model="filterText" size="small"></el-input>
      </div>
      <div class="navTreediv" slot="sideContent">
        <div class="navDataItem">
          <div class="navTitle">
            <div class="siderBox">
              <div class="siderDiv" style="background:#32FFC7"></div>
              <p>数据表数据</p>
            </div>
            <p class="btnDiv">
              <!-- <span>
                <el-tooltip class="item" effect="dark" content="数据表、模式展示规则配置" placement="top">
                  <i class="el-icon-setting"></i>
                </el-tooltip>
              </span>-->
              <span @click="configDatabase">
                <el-tooltip class="item" effect="dark" content="新增" placement="top">
                  <i class="el-icon-circle-plus-outline"></i>
                </el-tooltip>
              </span>
            </p>
          </div>
          <div class="navCont">
            <el-tree
              class="filter-tree"
              :data="dataTableTree.data"
              :props="dataTableTree.defaultProps"
              default-expand-all
              :filter-node-method="filterNode"
              @node-click="showDatabaseInfor"
              ref="dataTableTree"
            >
              <span class="custom-tree-node" slot-scope="{ node, data }">
                <span :title="node.level === 2?node.label:''">{{ node.label }}</span>
                <span class="handle">
                  <span v-if="node.level === 2">
                    <el-button
                      type="text"
                      size="mini"
                      @click.stop="() => updateDatabase(node, data)"
                      icon="el-icon-edit"
                    ></el-button>
                  </span>
                  <span v-if="node.level === 2">
                    <el-button
                      type="text"
                      size="mini"
                      @click.stop="() => removeDatabase(node, data)"
                      icon="el-icon-delete"
                    ></el-button>
                  </span>
                </span>
              </span>
            </el-tree>
          </div>
        </div>
        <div class="navDataItem">
          <div class="navTitle">
            <div class="siderBox">
              <div class="siderDiv" style="background:#FFD220"></div>
              <p>文件数据</p>
            </div>
            <p @click="configFile">
              <i class="el-icon-circle-plus-outline"></i>
            </p>
          </div>
          <div class="navCont">
            <el-tree
              class="filter-tree"
              :data="fileDataTree.data"
              :props="fileDataTree.defaultProps"
              default-expand-all
              :filter-node-method="filterNode"
              @node-click="showFileDatalist"
              ref="fileDataTree"
            >
              <span class="custom-tree-node" slot-scope="{ node, data }">
                <span :title="node.label">{{ node.label }}</span>
                <span class="handle">
                  <span style="color: ">
                    <el-button
                      type="text"
                      size="mini"
                      @click.stop="() => updateFileServer(node, data)"
                      icon="el-icon-edit"
                    ></el-button>
                  </span>
                  <span>
                    <el-button
                      type="text"
                      size="mini"
                      @click.stop="() => removeFileOrEmulateServer(node, data, 'file')"
                      icon="el-icon-delete"
                    ></el-button>
                  </span>
                </span>
              </span>
            </el-tree>
          </div>
        </div>
        <!-- <div class="navDataItem">
          <div class="navTitle">
            <div class="siderBox">
              <div class="siderDiv" style="background:#1EB6FE"></div>
              <p>仿真数据</p>
            </div>
            <p @click="configEmulate">
              <i class="el-icon-circle-plus-outline"></i>
            </p>
          </div>
          <div class="navCont">
            <el-tree
              class="filter-tree"
              :data="emulateDataTree.data"
              :props="emulateDataTree.defaultProps"
              default-expand-all
              :filter-node-method="filterNode"
              ref="emulateDataTree"
              @node-click="showEmulateDataInfor"
            >
              <span class="custom-tree-node" slot-scope="{ node, data }">
                <span :title="node.label">{{ node.label }}</span>
                <span class="handle">
                  <span style="color: ">
                    <el-button
                      type="text"
                      size="mini"
                      @click.stop="() => updateEmulateServer(node, data)"
                      icon="el-icon-edit"
                    ></el-button>
                  </span>
                  <span>
                    <el-button
                      type="text"
                      size="mini"
                      @click.stop="() => removeFileOrEmulateServer(node, data, 'emulate')"
                      icon="el-icon-delete"
                    ></el-button>
                  </span>
                </span>
              </span>
            </el-tree>
          </div>
        </div>-->
        <!-- <div class="navDataItem">
          <div class="navTitle">
            <div class="siderBox">
              <div class="siderDiv" style="background:#FE921E"></div>
              <p>表单数据</p>
            </div>
            <p>
              <i class="el-icon-circle-plus-outline"></i>
            </p>
          </div>
          <div class="navCont">
            <el-tree
              class="filter-tree"
              :data="formDataTree.data"
              :props="formDataTree.defaultProps"
              default-expand-all
              :filter-node-method="filterNode"
              ref="formDataTree"
            ></el-tree>
          </div>
        </div>-->
      </div>
    </SideFrame>
    <config-database
      v-if="databaseDialog.visible"
      :dialogConfig="databaseDialog"
      :databaseServerInfo="databaseServerInfo"
      :dataCenterType="selectedCategoryId"
      @closeDialog="closeConfigDatabase"
    ></config-database>
    <config-file
      v-if="fileDialog.visible"
      :dialogConfig="fileDialog"
      :fileServerInfo="fileServerInfo"
      :dataCenterType="sourceId"
      @closeDialog="closeConfigFile"
    ></config-file>
    <!-- <config-emulate
      v-if="emulateDialog.visible"
      :dialogConfig="emulateDialog"
      :emulateServerInfo="emulateServerInfo"
      :dataCenterType="sourceId"
      @closeDialog="closeConfigEmulate"
    ></config-emulate>-->
  </div>
</template>
<script lang="ts">
import Vue from 'vue';
import config from './config';
import ConfigDatabase from './ConfigDatabase.vue';
import ConfigFile from './ConfigFileData.vue';
import ConfigEmulate from './ConfigEmulateData.vue';
import SideFrame from '@/components/SideFrame/index.vue';
import { dataCenterService } from '@/api/index.ts';
export default Vue.extend({
  name: 'navPart',
  components: { ConfigDatabase, ConfigFile, ConfigEmulate, SideFrame },
  props: {
    // 数据中心类型
    sourceId: {
      type: Number,
      default: ''
    }
  },
  data() {
    return {
      headTitle: {
        imgUrl: 'taskManage',
        title: '数据源管理'
      },
      // 当前选中的分类ID
      selectedCategoryId: 1,
      // 树形菜单筛选字段
      filterText: '',
      // 数据表树形菜单数据和配置
      dataTableTree: {
        data: config.defaultNavTreeData,
        defaultProps: {
          children: 'children',
          label: 'name'
        }
      },
      // 文件数据树形菜单数据和配置
      fileDataTree: {
        data: [],
        defaultProps: {
          children: 'children',
          label: 'name'
        }
      },
      // 仿真数据树形菜单数据和配置
      /* emulateDataTree: {
        data: [],
        defaultProps: {
          children: 'children',
          label: 'name'
        }
      }, */
      // 表单数据树形菜单数据和配置
      /* formDataTree: {
        data: [],
        defaultProps: {
          children: 'children',
          label: 'label'
        }
      }, */
      databaseDialog: {
        title: '编辑数据源',
        visible: false
      },
      databaseServerInfo: {},
      fileDialog: {
        title: '编辑文件服务器',
        visible: false
      },
      fileServerInfo: {},
      emulateDialog: {
        title: '编辑仿真数据',
        visible: false
      },
      emulateServerInfo: {}
    };
  },
  watch: {
    sourceId(val) {
      this.getAllDatabasesByType();
      this.getFileServerList();
      // this.getEmulateServerList();
    },
    // 树形菜单筛选输入监听
    filterText(val) {
      (this.$refs.dataTableTree as any).filter(val);
      (this.$refs.fileDataTree as any).filter(val);
      // (this.$refs.emulateDataTree as any).filter(val);
      // (this.$refs.formDataTree as any).filter(val);
    }
  },
  created() {
    this.selectedCategoryId = Number(this.sourceId) || 1;
    this.getAllDatabasesByType();
    this.getFileServerList();
    // this.getEmulateServerList();
  },
  methods: {
    // 配置文件服务器
    configFile() {
      this.fileDialog.visible = true;
    },
    // 关闭文件服务器配置弹框
    closeConfigFile(bool: boolean) {
      this.fileDialog.visible = false;
      this.fileServerInfo = {};
      bool && this.getFileServerList();
    },
    // 获取文件服务器地址列表
    getFileServerList() {
      const params = {
        sourceId: this.sourceId
      };
      dataCenterService.getFileServerList(params).then(({ data }: any) => {
        this.fileDataTree.data = data.data;
      });
    },
    // 展示文件服务器信息
    updateFileServer(node: any, data: any) {
      this.fileServerInfo = data;
      this.fileDialog.visible = true;
    },
    // 查看文件服务器下文件列表
    showFileDatalist(data: any, node: any) {
      console.log(node);
      console.log(data);
      const params = {
        id: data.id
      };
      this.$emit('changeShowType', 'dataTable'); // 右侧内容区域显示切换
      this.$nextTick(() => {
        this.$eventBus.$emit('showFileDatalist', params);
      });
      /*  */
    },
    // 配置仿真数据
    configEmulate() {
      this.emulateDialog.visible = true;
    },
    // 关闭文件服务器配置弹框
    // closeConfigEmulate(bool: boolean) {
    //   this.emulateDialog.visible = false;
    //   this.emulateServerInfo = {};
    //   bool && this.getEmulateServerList();
    // },
    // 获取文件服务器地址列表
    /* getEmulateServerList() {
      let params = {
        sourceId: this.sourceId
      };
      dataCenterService.getArtificial(params).then(({ data }: any) => {
        this.emulateDataTree.data = data.data;
      });
    }, */
    // 展示仿真数据信息
    /* updateEmulateServer(node: any, data: any) {
      this.emulateServerInfo = data;
      this.emulateDialog.visible = true;
    }, */
    // 查看文件服务器下文件列表
    /* showEmulateInfor(data: any, node: any) {
      console.log(node);
      console.log(data);
    }, */
    // 删除文件服务器或仿真数据地址
    removeFileOrEmulateServer(node: any, data: any, type: string) {
      console.log(node);
      this.$confirm('此操作将删除该数据源, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        center: true
      })
        .then(() => {
          dataCenterService.deleteArtificial({ id: data.id }).then((res: any) => {
            this.$message({
              type: res.data.success ? 'success' : 'warning',
              message: res.data.success ? '删除成功！' : res.data.message
            });
            if (res.data.success) {
              if (type === 'file') {
                this.getFileServerList();
              } else {
                // this.getEmulateServerList();
              }
            }
          });
        })
        .catch(() => {});
    },
    // 树形菜单执行筛选
    filterNode(value: string, data: any) {
      if (!value) return true;
      return data.name.indexOf(value) !== -1;
    },
    /*  
    获取并显示数据源列表
     - userType: 使用分类
    */
    getDatabasesByType(useType: string, update?: boolean) {
      let useTypeObj = this.dataTableTree.data[Number(useType) - 1];
      // 判断当前分类下是否已存在数据，有数据则不再请求
      if (useTypeObj.children.length && !update) {
        return;
      }
      const params = {
        sourceId: this.sourceId,
        businessType: useType
      };
      this.$API.getDatabasesByType(params).then(({ data }: any) => {
        useTypeObj.children = data.data.map((item: any) => {
          item.children = [];
          return item;
        });
      });
    },
    /**
     * @description 获取数据库和文件服务器地址
     */
    getAllDatabasesByType() {
      // 1：源数据库服务器
      // 2：引接数据库服务器
      // 3：处理数据库服务器
      ['1', '2', '3'].forEach((ele: string) => {
        const params = {
          sourceId: this.sourceId,
          businessType: ele
        };
        this.$API.getDatabasesByType(params).then(({ data }: any) => {
          this.dataTableTree.data[Number(ele) - 1].children = data.data.map((item: any) => {
            item.children = [];
            return item;
          });
        });
      });
    },
    /* getDatabaseList() {
      let params = {
        dataCenterType: this.sourceId
      };
      this.$API.getDatabases(params).then(({ data }: any) => {
        this.dataTableTree.data.forEach((type: any) => {
          type.children = [];
          data.forEach((dt: any) => {
            dt.useType = '1'; // 暂无使用类型（useType）字段，后期修改
            dt.children = [];
            if (dt.useType && dt.useType === type.id) {
              type.children.push(dt);
            }
          });
        });
      });
    }, */
    // 获取并显示节点信息接口分发
    showDatabaseInfor(data: any, node: any) {
      if (node.level.toString() === '2' && !data.children.length) {
        this.getSchemaList(data);
      } else if (node.level.toString() === '3') {
        let params = {
          connection: node.parent.data.name,
          schema: data.name
        };
        this.$eventBus.$emit('showDataTableList', params);
      } else if (node.level.toString() === '1') {
        this.selectedCategoryId = Number(data.id);
        this.getDatabasesByType(data.id);
      } else {
        return;
      }
      this.$emit('changeShowType', 'dataTable'); // 右侧内容区域显示切换
    },
    // 获取并显示模式列表
    getSchemaList(nodeData: any) {
      let params = {
        connection: nodeData.name
      };
      this.$API.getSchema(params).then(({ data }: any) => {
        const list = data.data || [];
        list.forEach((schema: any) => {
          schema.name = schema.text;
          nodeData.children.push(schema);
        });
      });
    },
    /* getEmulateDataList() {
      let params = {
        dataCenterType: this.sourceId
      };
      this.$API.getArtificial(params).then(({ data }: any) => {
        this.emulateDataTree.data = data.data;
      });
    }, */
    /* showEmulateDataInfor(data: any, node: any) {
      this.$emit('changeShowType', 'emulate'); // 右侧内容区域显示切换
      this.$nextTick(() => {
        this.$eventBus.$emit('showEmulateInfor', data);
      });
    }, */
    configDatabase() {
      this.databaseDialog.visible = true;
    },
    closeConfigDatabase(useType: string, bool?: boolean) {
      this.databaseDialog.visible = false;
      this.databaseServerInfo = {};
      if (bool) {
        this.getAllDatabasesByType();
      }
    },
    removeDatabase(node: any, data: any) {
      this.$confirm('此操作将删除该数据源, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        center: true
      })
        .then(() => {
          this.$API.delDatabase({ idDatabase: node.data.id }).then((res: any) => {
            this.$message({
              type: res.data.success ? 'success' : 'warning',
              message: res.data.success ? '删除成功！' : res.data.message
            });
            if (res.data.success) {
              this.getDatabasesByType(node.data.categoriesId, true);
            }
          });
        })
        .catch(() => {});
    },
    updateDatabase(node: any, data: any) {
      this.databaseServerInfo = { ...node.data };
      this.configDatabase();
    }
  }
});
</script>
<style lang="scss" scoped>
@import '@/styles/theme.scss';
.navPart {
  width: 100%;
  height: 100%;
  .searchdiv {
    ::v-deep.el-input {
      width: calc(100% - 10px);
      margin: 5px;
      &__inner {
        background-color: $navBg;
        color: $whiteWordClr;
        border: 1px solid $currentRow;
      }
    }
  }
  .searchTitle {
    width: 100%;
    height: 40px;
    line-height: 40px;
    display: flex;
    justify-content: space-between;
    padding: 0 5px;
    background: $sideBarTtlBgClr;
  }
  .navTreediv {
    width: 100%;
    height: 100%;
    overflow: hidden;
    overflow-y: auto;
    .navDataItem {
      width: 100%;
      height: auto;
      margin-bottom: 10px;
      .navTitle {
        width: 100%;
        height: 24px;
        line-height: 24px;
        display: flex;
        justify-content: space-between;
        padding: 0 5px;
        // background: $conterBg;
        border-bottom: 1px solid $currentRow;
        font-size: 16px;
        .siderBox {
          display: flex;
          .siderDiv {
            width: 5px;
            height: 15px;
            margin: 5px 5px;
          }
        }
        p.btnDiv {
          span {
            margin-left: 5px;
            cursor: pointer;
          }
        }
      }
      .navCont {
        width: 100%;
        ::v-deep .el-tree {
          background: transparent;
          color: #666666;
          .custom-tree-node {
            display: flex;
            justify-content: space-between;
            align-items: center;
            width: 100%;
            font-size: 14px;
            span {
              &.handle {
                display: flex;
                span {
                  margin-left: 4px;
                  &:last-child {
                    .el-button {
                      color: #c54a2e;
                    }
                  }
                }
              }
              &:first-child {
                width: 100%;
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
              }
            }
          }
          .el-tree-node__children {
            width: 100%;
            .el-tree-node__content {
              width: 100%;
              // padding-left: 8px !important;
              .custom-tree-node {
                width: 100%;
              }
            }
          }
        }
      }
    }
  }
}
</style>
