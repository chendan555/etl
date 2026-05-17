<template>
  <el-container id="mainNodeInfo">
    <el-main>
      <div class="rightPart">
        <div v-show="toolBarShow==='component'">
          <div v-show="!isShowNode">
            <div class="title">项目属性</div>
            <div class="model-attr">
              <p>
                <span class="item">项目名称</span>
                <span class="value">{{projectDesc.name}}</span>
              </p>
              <p>
                <span class="item">创建日期</span>
                <span class="value">{{projectDesc.createDate}}</span>
              </p>
            </div>
          </div>
          <div v-show="isShowNode">
            <div class="title"><img src="~@/assets/indexPage/bz_logo.png" alt=""> 步骤信息</div>
            <div class="node-attr">
              <p>
                <span class="item">步骤名称</span>
                <span class="value">{{currentNodeMsg.text}}</span>
              </p>
              <p v-if="currentNodeMsg.pluginId === 'TableOutput'">
                <span class="item">数据预览</span>
                <span class="value">
                  <el-button @click="preview" size="small" icon="el-icon-view">查看</el-button>
                </span>
              </p>
              <p v-if="currentNodeMsg.pluginId === 'ExcelOutput'">
                <span class="item">Excel下载</span>
                <span class="value">
                  <el-button @click="downloadExcel" size="small" icon="el-icon-download">下载</el-button>
                </span>
              </p>
              <p>
                <span class="item">模块描述</span>
                <span class="value">{{currentNodeMsg.qtip}}</span>
              </p>
            </div>
          </div>
        </div>
      </div>
    </el-main>
    <base-dialog-table
      :tableColumnConfig="preViewTableColumnConfig"
      :tableData="previewData"
      :dialog="preViewDialog"
    ></base-dialog-table>
  </el-container>
</template>
<script>
import BaseDialogTable from "@/components/BaseDialogTable";
import API from "@/api/index";
export default {
  components: {
    BaseDialogTable
  },
  props: {
    currentNodeMsg: {
      type: Object,
      default: () => {
        return {};
      }
    },
    projectDesc: {
      type: Object,
      default: () => {
        return {};
      }
    },
    toolBarShow: {
      type: String
    },
    isShowNode: {
      type: Boolean,
      default: false
    }
  },
  watch: {
    currentNodeMsg: {
      deep: true,
      handler(val) {
        // console.log(val)
      }
    }
  },
  data() {
    return {
      previewData: [],
      preViewDialog: {
        title: "数据预览",
        visible: false
      },
      preViewTableColumnConfig: []
    };
  },
  methods: {
    // Excel下载
    downloadExcel() {
      // let params = {
      //   type: 'transformation',
      //   path: this.$route.query.path,
      //   nodeId: this.currentNodeMsg.nodeId
      // }
      window.location = `/api/transNew/downloadExcel.do?type=transformation&path=${this.$route.query.path}&nodeId=${this.currentNodeMsg.nodeId}`;
    },
    preview() {
      const loading = this.$utils.setLoading('查询中...');
      API.execModel({}).then(data => {
        if (data.data.success) {
          this.getLog({ transformationId: this.$store.state.transformationId });
        }
        // this.$message({
        //   type: data.data.success ? "success" : "error",
        //   message: data.data.message ? "执行成功" : "执行失败"
        // });
        if(!data.data.success) {
          this.$message({
            type: 'error',
            message: '当前节点数据无法预览'
          })
        }
      }).finally(()=>{
        loading.close();
      });
    },
    getLog(params) {
      const loading = this.$utils.setLoading('查询中...');
      API.execRunResult(params).then(data => {
        let rootData = data.data.previewData;

        // if (!rootData.hasOwnProperty(this.projectDesc.name)) {
        //   this.$message({
        //     type: "error",
        //     message: "当前节点数据无法预览"
        //   });
        //   return;
        // }
        // this.previewData = rootData.firstRecords[this.projectDesc.name];
        // this.preViewTableColumnConfig = rootData.columns.map(item => {
        //   return { label: item.header, prop: item.header };
        // });

        this.previewData = rootData[this.currentNodeMsg.text].firstRecords;
        this.preViewTableColumnConfig = rootData[this.currentNodeMsg.text].columns.map(item => {
          return { label: item.header, prop: item.header };
        });

        this.preViewDialog.visible = true;
      }).finally(()=>{
        loading.close();
      });
    }
  }
};
</script>
<style lang="scss" scoped>
@import '@/styles/theme.scss';
.rightPart{
  height: 100%;
  background: $conterBg;
  color: #fff;
}
#mainNodeInfo {
  .title {
    // border-bottom: 1px solid #e5e5e5;
    height: 40px;
    font-size: 14px;
    color: #fff;
    line-height: 40px;
    text-align: left;
    background: $centerTtlBgClr;
    display: flex;
    align-items: center;
    padding: 0 5px;
    img{
      margin-right: 5px;
    }
  }
  .model-attr {
    padding: 10px;
    p{
      margin: 15px 0;
    }
    .item {
      font-size: 12px;
    }
    .value {
      font-size: 12px;
      color: #999;
      margin-left: 10px;
    }
    .el-input {
      margin-top: 5px;
    }
    textarea {
      margin-top: 5px;
      font-family: inherit;
    }
  }
  .node-attr {
    padding: 10px;
    p{
      margin: 15px 0;
    }
    .item {
      font-size: 12px;
      width: 56px;
      display: inline-block;
    }
    .value {
      font-size: 12px;
      color: #999;
      margin-left: 10px;
    }
  }
  .messageInfo {
    padding: 0px;
    margin: 10px;
    color: #333;
    font-size: 14px;
    .el-card__body {
      padding: 10px;
    }
    p {
      padding: 0;
      margin: 0;
      font-size: 12px;
    }
  }
}
</style>


