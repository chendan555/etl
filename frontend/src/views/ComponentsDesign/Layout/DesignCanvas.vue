<template>
  <div class="design-canvas" v-dropable.stop="eleDrop">
    <el-form>
      <el-row :gutter="20">
        <div v-for="item in canvasDesign.eleList" :key="item.config.id">
          <el-col
            v-if="isShow(item)"
            :span="item.config.span"
            :id="item.config.id"
            v-draggable.updatePos.stop="item"
            :class="{'choosed-item':item.config.choosed}"
          >
            <el-form-item
              class="design-ele"
              :label="item.config.isShowFormLabel?item.config.name:''"
              :label-width="item.config.isShowFormLabel?item.config.labelWidth+'px':'0px'"
              @mousedown.native.stop="chooseEle(item)"
              :style="{width: item.config.width+'px'}"
            >
              <!-- 组件 -->
              <ETLTabs
                v-if="item.key === 'tabs'"
                :ruleForm="ruleForm"
                :tabList="item.config.tabList"
                :eleList="item.config.eleList"
                :isPreview="isPreview"
                :tabId="item.config.id"
              ></ETLTabs>
              <el-input
                v-else-if="item.key === 'input'"
                v-model="item.config.value"
                :placeholder="item.config.placeholder"
              ></el-input>
              <el-input
                v-else-if="item.key === 'textarea'"
                type="textarea"
                v-model="item.config.value"
              ></el-input>
              <ETLSelect
                :ref="item.config.id"
                v-else-if="item.key === 'select'"
                v-model="item.config.value"
                :config="item.config"
                @change="eventNotify(item)"
              />
              <el-radio-group v-else-if="item.key === 'radio'" v-model="item.config.value">
                <el-radio
                  v-for="(option,index) in item.config.options"
                  :key="index"
                  :label="option[item.config.optionsConfig.value]"
                >{{option[item.config.optionsConfig.name]}}</el-radio>
              </el-radio-group>
              <el-checkbox-group v-else-if="item.key === 'checkbox'" v-model="item.config.value">
                <el-checkbox
                  v-for="(option,index) in item.config.options"
                  :key="index"
                  :label="option[item.config.optionsConfig.value]"
                >{{option[item.config.optionsConfig.name]}}</el-checkbox>
              </el-checkbox-group>
              <ETLMonaco
                v-else-if="item.key === 'code'"
                :ref="item.config.id"
                @change="eventNotify(item)"
                :monacoId="item.config.id"
                v-model="item.config.value"
              />
              <ETLMath
                v-else-if="item.key === 'formula'"
                :ref="item.config.id"
                @change="eventNotify(item)"
                :monacoId="item.config.id"
                v-model="item.config.value"
              />
              <el-button
                v-else-if="item.key === 'button'"
                @click="eventNotify(item)"
              >{{item.config.name}}</el-button>
              <el-switch
                v-else-if="item.key === 'switch'"
                active-value="Y"
                inactive-value="N"
                v-model="item.config.value"
              ></el-switch>
              <el-date-picker
                v-else-if="item.key === 'date'"
                v-model="item.config.value"
                type="datetime"
                format="yyyy-MM-dd HH:mm:ss"
                value-format="yyyy-MM-dd HH:mm:ss"
                placeholder="选择日期"
              ></el-date-picker>
              <ETLTree v-else-if="item.key === 'tree'" :treeData="item.config.treeData"/>
              <ETLSelectTree
                v-else-if="item.key === 'selectTree'"
                :ref="item.config.id"
                @change="eventNotify(item)"
                :config="item.config"
              />
              <ETLTable
                :ref="item.config.id"
                v-else-if="item.key === 'table'"
                :tableColumnConfig="item.config.tableColumnConfig"
                :config="item.config"
                height="300"
                @resetTableValue="resetTableValue"
                :tableData="item.config.value"
                :isShowOperation="item.config.isShowOperation"
              />
              <SqlConditionTree
                v-else-if="item.key === 'sqlConditionTree'"
                :sqlConditionTree="item.config.value"
              />
              <ETLInputFile
                v-else-if="item.key === 'inputFile'"
                :itemConfig="item"
                @change="uploadFile"
              />
            </el-form-item>
          </el-col>
        </div>
      </el-row>
    </el-form>
  </div>
</template>
<script>
import ETLInputFile from '@/views/ComponentsDesign/components/ETLInputFile/index';
import ETLTabs from '@/views/ComponentsDesign/components/ETLTabs/index';
import ETLMonaco from '@/views/ComponentsDesign/components/ETLMonaco/index';
import ETLMath from '@/views/ComponentsDesign/components/ETLMath/index';
import ETLSelectTree from '@/views/ComponentsDesign/components/ETLSelectTree/index';
import ETLTree from '@/views/ComponentsDesign/components/ETLTree/index';
import ETLTable from '@/views/ComponentsDesign/components/ETLTable/index';
import ETLSelect from '@/views/ComponentsDesign/components/ETLSelect/index';
import resDeal from '@/views/ComponentsDesign/res_deal';
import etlHttpMixins from '@/views/ComponentsDesign/mixins/etlHttpMixins.js';
import SqlConditionTree from '@/components/SqlConditionTree';
import FlowChart from '@/FlowChart/index';
// import axios from '@/api/base/http';
export default {
  name: 'DesignCanvas',
  components: { ETLInputFile, ETLTabs, ETLMonaco, ETLMath, ETLSelectTree, ETLTree, ETLTable, ETLSelect, SqlConditionTree },
  mixins: [etlHttpMixins],
  props: {
    canvasDesign: {
      type: Object,
      default: {}
    },
    isPreview: {
      type: Boolean
    },
    // tabs内嵌画布的情况下需要传true
    canvasRuleForm: {
      type: Object
    }
  },
  data() {
    return {};
  },
  computed: {
    ruleForm() {
      if (this.canvasRuleForm) return this.canvasRuleForm;
      return this.canvasDesign.getRuleForm();
    }
  },
  mounted() {
    console.log('canvasDesign', this.canvasDesign);
  },
  methods: {
    eleDrop(data, event) {
      if (data.updatePos) {
        delete data.updatePos;
        this.$eventBus.$emit('updateElePos', data, event);
        return;
      }
      this.$emit('addEle', this.generateNode(data));
    },
    generateNode(data) {
      const dataTemp = this.$utils.deepClone(data);
      dataTemp.config.id = this.$utils.createRandomId();
      return dataTemp;
    },
    chooseEle(item) {
      // 编辑时选择其他元素会导致数据丢失，增加异步调用避免
      this.$nextTick(() => {
        this.$emit('chooseEle', this.$utils.deepClone(item.config));
      });
    },
    isShow(item) {
      if (!this.isPreview) {
        return true;
      }
      const { showCondition } = item.config;
      if (showCondition.length > 0) {
        let conditionNum = showCondition.length;
        showCondition.forEach(condition => {
          if (this.ruleForm[condition.key] == condition.value) {
            conditionNum--;
          }
        });
        return conditionNum === 0;
      } else {
        return true;
      }
    },
    submit() {
      return this.ruleForm;
    },
    uploadFile(emitData) {
      let { item, data } = emitData;
      this.eventNotify(item, data);
    },
    /**
     * 判断选择的文件类型和算子能处理的文件类型匹配情况
     * 匹配返回true
     * 不匹配返回false
     */
    checkFileType(fileData) {
      let currentNodeMsg = FlowChart.getNodeDataByNodeId(this.$store.state.nodeId);
      const nameArr = fileData.name.split('.');
      // Excel输入算子支持'xls', 'xlsx'两种格式文件
      if (['xls', 'xlsx'].includes(nameArr[nameArr.length - 1]) && currentNodeMsg.data.pluginId === 'ExcelInput') {
        return true;
        // 文本文件输入算子支持'csv', 'txt', 'dat'三种格式文件
      } else if (['csv', 'txt', 'dat'].includes(nameArr[nameArr.length - 1]) && currentNodeMsg.data.pluginId === 'TextFileInput') {
        return true;
        // XML文件输入算子支持'text/xml'一种种格式文件
      } else if (fileData.type === 'text/xml' && currentNodeMsg.data.pluginId === 'XmlFileInput') {
        return true;
      }
      return false;
    },
    // 事件通知
    eventNotify(ele, fileData) {
      ele.config.events.forEach(event => {
        // 是否绑定了事件
        if (event.event) {
          if (this.$refs && this.$refs[event.ele]) {
            const targetEle = this.$refs[event.ele][0];
            // 事件配置传递参数
            if (event.hasOwnProperty('eventParamsConfig')) {
              // 请求响应处理
              if (event.eventParamsConfig.httpUrl) {
                // 判断是否为文件上传
                if (fileData) {
                  // 判断选择的文件类型和算子能处理的文件类型是否匹配，不匹配则提示重新选择，并停止后续程序执行
                  if (!this.checkFileType(fileData)) {
                    this.$utils.setMessage(false, '请选择对应的文件类型');
                    return;
                  }
                  let formData = new FormData();
                  formData.append('uploadFile', fileData);
                  this.$API.getFileUpload(formData).then(res => {
                    this.$message({
                      type: res.data.success ? 'success' : 'error',
                      message: res.data.data.msg
                    });
                    let filePath = res.data.data.path;
                    targetEle[event.event](resDeal[event.eventParamsConfig.resDeal](filePath, targetEle, event));
                  });
                } else {
                  console.log('[eventNotify] httpUrl:', event.eventParamsConfig.httpUrl, 'ruleForm:', this.ruleForm, 'event:', event.event, 'targetEle:', event.ele);
                  this.http(event.eventParamsConfig.httpUrl, this.ruleForm).then(res => {
                    if (res && res.data && res.data.success) {
                      this.$utils.setMessage(res.data.success, res.data.result);
                    }
                    if (event.event === 'updateOptions') {
                      // updateOptions事件：通过eventBus触发，让ComponentsPreview用目标组件自身的httpUrl刷新下拉数据
                      // 避免直接调用导致eventParamsConfig.httpUrl的响应覆盖正确数据
                      this.$eventBus.$emit('updateOptions', targetEle.config, {});
                    } else {
                      // 其他事件（如setValue）：直接调用目标方法，传入处理后的响应数据
                      targetEle[event.event](resDeal[event.eventParamsConfig.resDeal](res, targetEle, event));
                    }
                  });
                }
              } else {
                let value = event.eventParamsConfig.value ? this.$refs[event.eventParamsConfig.value][0].getValue() : null;
                targetEle[event.event](resDeal[event.eventParamsConfig.resDeal](value, targetEle, event));
              }
            } else {
              targetEle[event.event]();
            }
          } else {
            this.$message.warning('请检查你的事件配置关系！');
          }
        }
      });
      console.log(this.canvasDesign);
    },
    // 查找ref
    // todo 此处还需兼容向下查询 暂时不考虑
    findRefs(vue, ref) {
      if (vue && vue.$refs && vue.$refs[ref]) {
        return vue.$refs;
      } else {
        return this.findRefs(vue.$parent, ref);
      }
    },
    resetTableValue(config) {
      config.value = [];
    }
  }
};
</script>
<style lang="scss" scoped>
@import '@/styles/theme.scss';
.design-canvas {
  flex: 1;
  height: 100%;
  overflow: scroll;
  position: relative;
  .el-row {
    margin: 0 !important;
  }
  .el-col {
    margin-bottom: 5px;
    position: relative;
  }
  .choosed-item:before {
    position: absolute;
    content: '';
    width: 5px;
    height: 100%;
    background: rgb(14, 245, 45);
    left: 0;
    top: 0;
    z-index: 1;
  }
  .choosed-item .design-ele {
    background: rgba(255, 255, 255, 0.05);
  }
  .design-ele {
    margin-bottom: 0;
    /deep/ .el-form-item__label {
      line-height: 20px;
      display: flex;
      flex-direction: column;
      justify-content: center;
      height: 40px;
    }
    /deep/ .el-select {
      width: 100%;
    }
    /deep/ .el-date-editor.el-input {
      width: 100%;
    }
    /deep/ .el-radio__input.is-checked .el-radio__inner {
      border-color: #719681;
      background-color: var(--inputBgClr, #1d2822);
    }
    /deep/ .el-radio__inner {
      background-color: var(--inputBgClr, #1d2822);
      border-color: #719681;
    }
    /deep/ .el-radio__label {
      color: $normalWordsClr;
    }
    /deep/ .el-radio {
      line-height: inherit;
    }
    /deep/ .el-checkbox-group {
      font-size: inherit;
    }
    /deep/ .el-tabs__header {
      margin: 0;
    }
  }
}
</style>

