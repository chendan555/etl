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
            <!-- 目标表输出 - 字段映射 -->
            <div v-if="currentNodeMsg.pluginId === 'BoundTableOutput'" class="bound-table-mapping">
              <div class="mapping-title">字段映射</div>
              <div class="mapping-table-select">
                <span class="item">目标表</span>
                <el-select
                  v-model="boundTable"
                  size="mini"
                  placeholder="选择目标表"
                  @change="onBoundTableChange"
                  style="width: 160px; margin-left: 8px;"
                >
                  <el-option
                    v-for="t in boundTableOptions"
                    :key="t"
                    :label="t"
                    :value="t"
                  ></el-option>
                </el-select>
              </div>
              <div class="mapping-header">
                <span class="mapping-col">目标字段</span>
                <span class="mapping-col">流字段</span>
              </div>
              <div class="mapping-body">
                <div
                  class="mapping-row"
                  v-for="(col, idx) in targetColumns"
                  :key="col.name"
                >
                  <span class="mapping-col target-col" :title="col.comment || col.name">
                    {{ col.name }}
                    <em v-if="col.comment" class="col-comment">{{ col.comment }}</em>
                  </span>
                  <el-select
                    v-model="fieldMappings[idx]"
                    size="mini"
                    placeholder="选择流字段"
                    clearable
                    filterable
                    class="mapping-col"
                    style="width: 140px;"
                  >
                    <el-option
                      v-for="f in upstreamFields"
                      :key="f.name"
                      :label="f.name"
                      :value="f.name"
                    ></el-option>
                  </el-select>
                </div>
              </div>
              <div class="mapping-actions">
                <el-button size="mini" type="primary" @click="saveMapping">保存映射</el-button>
              </div>
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
import FlowChart from '@/FlowChart/index';
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
    },
    bindTableNames: {
      type: String,
      default: ''
    }
  },
  watch: {
    currentNodeMsg: {
      deep: true,
      handler(val) {
        if (val && val.pluginId === 'BoundTableOutput') {
          this.initBoundTableMapping();
        }
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
      preViewTableColumnConfig: [],
      // 目标表输出映射相关
      boundTable: '',
      boundTableOptions: [],
      targetColumns: [],
      upstreamFields: [],
      fieldMappings: []
    };
  },
  methods: {
    // 初始化目标表映射
    initBoundTableMapping() {
      // 加载绑定表选项
      const tables = this.bindTableNames ? this.bindTableNames.split(',').filter(Boolean) : [];
      this.boundTableOptions = tables;

      // 读取已保存的配置
      const nodeData = FlowChart.getNodeDataByNodeId(this.$store.state.nodeId);
      if (nodeData && nodeData.properties && nodeData.properties.ruleForm) {
        this.boundTable = nodeData.properties.ruleForm.boundTable || '';
      }
      // 读取已保存的映射
      if (nodeData && nodeData.properties && nodeData.properties.tableData) {
        this.savedMappings = nodeData.properties.tableData;
      } else {
        this.savedMappings = [];
      }

      if (this.boundTable) {
        this.loadTargetColumns();
      } else {
        this.targetColumns = [];
        this.fieldMappings = [];
      }
      this.loadUpstreamFields();
    },
    onBoundTableChange() {
      this.loadTargetColumns();
    },
    loadTargetColumns() {
      if (!this.boundTable) {
        this.targetColumns = [];
        this.fieldMappings = [];
        return;
      }
      API.getBindTableColumns({ tableName: this.boundTable }).then(res => {
        const data = res.data.data || res.data;
        if (Array.isArray(data)) {
          this.targetColumns = data;
          // 恢复已保存的映射
          this.fieldMappings = data.map(col => {
            const saved = this.savedMappings.find(m => m.tableChartsName === col.name);
            return saved ? saved.name : '';
          });
        }
      });
    },
    loadUpstreamFields() {
      API.getStepsFileds({}).then(res => {
        const data = res.data.data || res.data;
        if (Array.isArray(data)) {
          this.upstreamFields = data;
        }
      }).catch(() => {});
    },
    saveMapping() {
      // 构建 tableData 格式
      const tableData = this.targetColumns.map((col, idx) => ({
        tableChartsName: col.name,
        name: this.fieldMappings[idx] || ''
      })).filter(m => m.name); // 只保存有映射的

      // 更新节点数据
      const nodeData = FlowChart.getNodeDataByNodeId(this.$store.state.nodeId);
      if (nodeData) {
        if (!nodeData.properties) nodeData.properties = {};
        nodeData.properties.tableData = tableData;
        // 同时更新 ruleForm 中的 boundTable
        if (!nodeData.properties.ruleForm) nodeData.properties.ruleForm = {};
        nodeData.properties.ruleForm.boundTable = this.boundTable;
        FlowChart.setData(FlowChart.getData());
        this.$message.success('映射已保存');
      }
    },
    // Excel下载
    downloadExcel() {
      window.location = `/api/transNew/downloadExcel.do?type=transformation&path=${this.$route.query.path}&nodeId=${this.currentNodeMsg.nodeId}`;
    },
    preview() {
      const loading = this.$utils.setLoading('查询中...');
      API.execModel({}).then(data => {
        if (data.data.success) {
          this.getLog({ transformationId: this.$store.state.transformationId });
        }
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
  color: $normalWordsClr;
}
#mainNodeInfo {
  .title {
    height: 40px;
    font-size: 16px;
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
      font-size: 14px;
    }
    .value {
      font-size: 14px;
      color: $normalWordsClr;
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
      font-size: 14px;
      width: 56px;
      display: inline-block;
    }
    .value {
      font-size: 14px;
      color: $normalWordsClr;
      margin-left: 10px;
    }
  }
  .bound-table-mapping {
    padding: 10px;
    border-top: 1px solid rgba(128,128,128,0.3);
    .mapping-title {
      font-size: 15px;
      font-weight: bold;
      margin-bottom: 10px;
      color: #409eff;
    }
    .mapping-table-select {
      display: flex;
      align-items: center;
      margin-bottom: 10px;
      .item {
        font-size: 14px;
        width: 56px;
        flex-shrink: 0;
      }
    }
    .mapping-header {
      display: flex;
      padding: 6px 0;
      border-bottom: 1px solid rgba(128,128,128,0.3);
      font-size: 14px;
      color: $normalWordsClr;
      .mapping-col {
        flex: 1;
        text-align: center;
      }
    }
    .mapping-body {
      max-height: 300px;
      overflow-y: auto;
    }
    .mapping-row {
      display: flex;
      align-items: center;
      padding: 4px 0;
      border-bottom: 1px solid rgba(128,128,128,0.15);
      .target-col {
        font-size: 14px;
        flex: 1;
        text-align: center;
        color: #67c23a;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        .col-comment {
          display: block;
          font-size: 12px;
          color: $normalWordsClr;
          font-style: normal;
          opacity: 0.7;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
      }
    }
    .mapping-actions {
      margin-top: 10px;
      text-align: center;
    }
  }
  .messageInfo {
    padding: 0px;
    margin: 10px;
    color: $normalWordsClr;
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
