<template>
  <div class="design-container">
    <DesignCanvas
      v-if="showCanvas"
      ref="DesignCanvas"
      :canvasDesign="canvasDesign"
      :isPreview="!!isPreview"
      @chooseEle="chooseEle"
    />
  </div>
</template>
<script>
import CanvasDesign from '../ComponentsDesign/canvas_design';
import DesignCanvas from '../ComponentsDesign/Layout/DesignCanvas';
import FlowChart from '@/FlowChart/index';
import axios from '@/api/base/http';
import API from '@/api/index';
import etlHttpMixins from '@/views/ComponentsDesign/mixins/etlHttpMixins.js';
export default {
  name: 'ComponentsPreview',
  meta: {
    title: '组件预览'
  },
  components: { DesignCanvas },
  mixins: [etlHttpMixins],
  props: {
    stepId: String,
    ruleForm: Object
  },
  data() {
    return {
      canvasDesign: null,
      attrConfig: {},
      tabsTagName: '',
      tabsId: '',
      isPreview: this.$route.query.isPreview,
      designId: this.$route.query.id,
      showCanvas: false // 判断是否展示画布，避免数据还未更新就执行画布内部逻辑
    };
  },
  created() {
    this.initOptions();
    this.$eventBus.$on('chooseEle', this.chooseEle);
    this.$eventBus.$on('submit', this.submit);
    this.$eventBus.$on('updateOptions', this.updateOptions);
  },
  beforeDestroy() {
    this.$eventBus.$off('chooseEle');
    this.$eventBus.$off('submit');
    this.$eventBus.$off('updateOptions');
  },
  methods: {
    // 从各种响应格式中提取数组数据
    extractArray(resData) {
      if (Array.isArray(resData)) return resData;
      if (resData && typeof resData === 'object') {
        if (Array.isArray(resData.data)) return resData.data;
        if (resData.data && Array.isArray(resData.data.records)) return resData.data.records;
        if (resData.data && Array.isArray(resData.data.data)) return resData.data.data;
      }
      return [];
    },
    initOptions() {
      if (this.stepId) {
        this.isPreview = true;
        this.designId = this.stepId;
      }
      this.getData();
    },
    getData() {
      this.canvasDesign = new CanvasDesign();
      if (!this.designId) {
        return;
      }
      axios({
        method: 'GET',
        url: `/api/plugin/manage/type/${this.designId}/detail`
      }).then(res => {
        if (res.data.data.dom) {
          this.canvasDesign.eleList = JSON.parse(res.data.data.dom);
          if (this.ruleForm) {
            this.canvasDesign.updateValueByRuleForm(this.ruleForm);
          }
        }
        this.showCanvas = true;
        this.$nextTick(() => {
          this.getOptionsByHttp();
          this.resolveStringOptions();
        });
      });
    },
    /**
     * @param data 节点信息
     * @param tabsId 选项卡id
     * @param tagName tab名
     */
    chooseEle(config, tabsId, tagName) {
      // 异步包装，避免配置修改失效
      setTimeout(() => {
        // 获取待编辑组件配置
        this.attrConfig = this.$utils.deepClone(config);
        // 选中当前组件
        this.canvasDesign.batchUpdate('choosed', false);
        this.tabsId = tabsId;
        this.tabsTagName = tagName;
        if (!tabsId) {
          const curNode = this.canvasDesign.findNodeById(config.id);
          curNode.config.choosed = true;
        } else {
          const curNode = this.canvasDesign.findTabNodeById(config.id);
          curNode.config.choosed = true;
        }
        this.$eventBus.$emit('attrEdit', this.attrConfig);
      });
    },
    save() {
      axios({
        method: 'POST',
        url: '/api/plugin/manage/type/edit',
        data: {
          id: this.designId,
          dom: JSON.stringify(this.canvasDesign.eleList)
        }
      }).then(r => {
        const res = r.data;
        if (res.success) {
          this.$message.success('保存成功!');
        }
      });
    },
    submit() {
      return this.$refs.DesignCanvas.submit();
    },
    // 收集请求对象
    /**
     * @description 收集需要请求数据（仅收集无参数依赖的接口）
     */
    getReqUrlObj() {
      const reqUrl = {};
      this.canvasDesign.eleList.forEach((ele, index) => {
        try {
          if (ele.config.httpUrl)
            reqUrl[ele.config.httpUrl] = {
              res: null,
              params: this.safeParseParams(ele.config.params)
            };
          if (ele.key === 'table' && ele.config.tableColumnConfig) {
            ele.config.tableColumnConfig.forEach(config => {
              if (config.httpUrl && config.optionsConfig && !config.optionsConfig.isRelyOnRowData)
                reqUrl[config.httpUrl] = {
                  res: null,
                  params: this.safeParseParams(config.params)
                };
            });
          }
          if (ele.key === 'tabs' && ele.config.eleList) {
            ele.config.eleList.forEach((tabEle, tabIndex) => {
              try {
                if (tabEle.config.httpUrl)
                  reqUrl[tabEle.config.httpUrl] = {
                    res: null,
                    params: this.safeParseParams(tabEle.config.params)
                  };
                if (tabEle.key === 'table' && tabEle.config.tableColumnConfig) {
                  tabEle.config.tableColumnConfig.forEach(config => {
                    if (config.httpUrl && config.optionsConfig && !config.optionsConfig.isRelyOnRowData)
                      reqUrl[config.httpUrl] = {
                        res: null,
                        params: this.safeParseParams(config.params)
                      };
                  });
                }
              } catch (e) {
                console.warn('[getReqUrlObj] tabs element error:', e);
              }
            });
          }
        } catch (e) {
          console.warn('[getReqUrlObj] element error at index ' + index + ':', e);
        }
      });
      console.log('[getReqUrlObj] collected URLs:', Object.keys(reqUrl));
      return reqUrl;
    },
    safeParseParams(params) {
      if (!params) return {};
      if (typeof params === 'object') return params;
      try {
        return JSON.parse(params);
      } catch (e) {
        console.warn('[safeParseParams] invalid params:', params);
        return {};
      }
    },
    setResAttr(config, reqUrl) {
      if (reqUrl[config.httpUrl]) {
        if (config.isHttpGetOptions) {
          if (config.optionsConfig.isRelyOnRowData) {
            config.options = [];
          } else {
            const resData = this.extractArray(reqUrl[config.httpUrl].res);
            // 字段映射
            if (config.optionsConfig && resData.length > 0) {
              ['name', 'value'].forEach(key => {
                const targetKey = config.optionsConfig[key];
                if (targetKey && !(targetKey in resData[0])) {
                  const fallbacks = ['text', 'name', 'tableName', 'label'];
                  const fallback = fallbacks.find(f => f in resData[0]);
                  if (fallback) {
                    resData.forEach(item => { item[targetKey] = item[fallback]; });
                  }
                }
              });
            }
            config.options = resData;
          }
        }
        if (config.isHttpGetTreeData) {
          config.treeData = reqUrl[config.httpUrl].res;
        }
      }
    },
    /**
     * @description 动态通过接口获取数据
     */
    getOptionsByHttp() {
      console.log('[getOptionsByHttp] called, eleList count:', this.canvasDesign.eleList.length);
      const reqUrl = this.getReqUrlObj();
      const reqArr = [];
      console.log('[getOptionsByHttp] URLs to fetch:', Object.keys(reqUrl).length);
      // 从 canvasDesign 元素中获取表单值，补充 submit() 可能缺失的值
      let ruleFormParams = {
        ...this.submit()
      }
      if(ruleFormParams.isCreateTable === 'Y' && ruleFormParams.newTable) {
        ruleFormParams.table = ruleFormParams.newTable;
      }
      if (this.canvasDesign && this.canvasDesign.eleList) {
        this.canvasDesign.eleList.forEach(ele => {
          if (ele.config && ele.config.valueKey) {
            const valueKey = Array.isArray(ele.config.valueKey) ? ele.config.valueKey[ele.config.valueKey.length - 1] : ele.config.valueKey;
            if (valueKey && !ruleFormParams[valueKey] && ele.config.value !== undefined && ele.config.value !== '') {
              ruleFormParams[valueKey] = ele.config.value;
            }
          }
          if (ele.key === 'tabs' && ele.config && ele.config.eleList) {
            ele.config.eleList.forEach(tabEle => {
              if (tabEle.config && tabEle.config.valueKey) {
                const valueKey = Array.isArray(tabEle.config.valueKey) ? tabEle.config.valueKey[tabEle.config.valueKey.length - 1] : tabEle.config.valueKey;
                if (valueKey && !ruleFormParams[valueKey] && tabEle.config.value !== undefined && tabEle.config.value !== '') {
                  ruleFormParams[valueKey] = tabEle.config.value;
                }
              }
            });
          }
        });
      }
      for (let key in reqUrl) {
        reqArr.push(this.http(key, { ...this.getFlowChart(), ...reqUrl[key].params, ...ruleFormParams, nodeId: this.$store.state.nodeId }));
      }
      Promise.all(reqArr).then(resArr => {
        resArr.forEach(res => {
          reqUrl[res.config.url].res = res.data;
        });
        this.canvasDesign.eleList.forEach((ele, index) => {
          this.setResAttr(ele.config, reqUrl);
          if (ele.key === 'table') {
            ele.config.tableColumnConfig.forEach(config => {
              this.setResAttr(config, reqUrl);
            });
          }
          if (ele.key === 'tabs') {
            ele.config.eleList.forEach((tabEle, tabIndex) => {
              this.setResAttr(tabEle.config, reqUrl);
              if (tabEle.key === 'table') {
                tabEle.config.tableColumnConfig.forEach(config => {
                  this.setResAttr(config, reqUrl);
                });
              }
            });
          }
        });
      });
    },
    /**
     * 解析 options 为字符串引用的下拉框（如 "getStepsFileds"）
     * 老系统 BaseTable/MixinComponents 有这个逻辑，新系统 ETLSelect 没有，这里补上
     */
    resolveStringOptions() {
      const nodeId = this.$store.state.nodeId;
      const flowChart = this.getFlowChart();

      const resolveEle = (config) => {
        if (config && typeof config.options === 'string' && config.options.length > 0 && !config.httpUrl) {
          const apiName = config.options;
          if (API[apiName]) {
            config.options = [];
            const params = { nodeId, ...flowChart };
            if (config.optionsParams) {
              Object.assign(params, config.optionsParams);
            }
            API[apiName](params).then(res => {
              const data = res.data;
              let arr = [];
              if (Array.isArray(data)) {
                arr = data;
              } else if (data && Array.isArray(data.data)) {
                arr = data.data;
              } else if (data && Array.isArray(data.content)) {
                arr = data.content;
              }
              // 统一字段映射：确保options有name/value字段，兼容label/key等格式
              if (arr.length > 0) {
                arr.forEach(item => {
                  if (!item.name && item.label) item.name = item.label;
                  if (!item.value && item.key) item.value = item.key;
                });
              }
              config.options = arr;
            }).catch(e => {
              console.warn('[resolveStringOptions] API call failed:', apiName, e);
            });
          }
        }
      };

      this.canvasDesign.eleList.forEach(ele => {
        resolveEle(ele.config);
        if (ele.key === 'table' && ele.config.tableColumnConfig) {
          ele.config.tableColumnConfig.forEach(col => resolveEle(col));
        }
        if (ele.key === 'tabs' && ele.config.eleList) {
          ele.config.eleList.forEach(tabEle => {
            resolveEle(tabEle.config);
            if (tabEle.key === 'table' && tabEle.config.tableColumnConfig) {
              tabEle.config.tableColumnConfig.forEach(col => resolveEle(col));
            }
          });
        }
      });
    },
    updateOptions(config, params = {}, cb) {
      // 做新建表获取表字段参数兼容
      let ruleFormParams = {
        ...this.submit()
      }
      if(ruleFormParams.isCreateTable === 'Y' && ruleFormParams.newTable) {
        ruleFormParams.table = ruleFormParams.newTable;
      }
      // 补充 ruleFormParams 中可能缺失的值（从 canvasDesign 元素中获取）
      if (this.canvasDesign && this.canvasDesign.eleList) {
        this.canvasDesign.eleList.forEach(ele => {
          if (ele.config && ele.config.valueKey) {
            const valueKey = Array.isArray(ele.config.valueKey) ? ele.config.valueKey[ele.config.valueKey.length - 1] : ele.config.valueKey;
            if (valueKey && !ruleFormParams[valueKey] && ele.config.value !== undefined && ele.config.value !== '') {
              ruleFormParams[valueKey] = ele.config.value;
            }
          }
          // 处理 tabs 内的元素
          if (ele.key === 'tabs' && ele.config && ele.config.eleList) {
            ele.config.eleList.forEach(tabEle => {
              if (tabEle.config && tabEle.config.valueKey) {
                const valueKey = Array.isArray(tabEle.config.valueKey) ? tabEle.config.valueKey[tabEle.config.valueKey.length - 1] : tabEle.config.valueKey;
                if (valueKey && !ruleFormParams[valueKey] && tabEle.config.value !== undefined && tabEle.config.value !== '') {
                  ruleFormParams[valueKey] = tabEle.config.value;
                }
              }
            });
          }
        });
      }
      this.http(config.httpUrl, { ...this.getFlowChart(), ...params, ...ruleFormParams, nodeId: this.$store.state.nodeId }).then(res => {
        const resData = this.extractArray(res.data);
        // 字段映射：如果optionsConfig指定的字段在数据中不存在，尝试从常见字段名映射
        if (config.optionsConfig && resData.length > 0) {
          ['name', 'value'].forEach(key => {
            const targetKey = config.optionsConfig[key];
            if (targetKey && !(targetKey in resData[0])) {
              const fallbacks = ['text', 'name', 'tableName', 'label'];
              const fallback = fallbacks.find(f => f in resData[0]);
              if (fallback) {
                resData.forEach(item => { item[targetKey] = item[fallback]; });
              }
            }
          });
        }
        if (config.hasOwnProperty('options')) {
          config.options = resData;
        }
        if (config.hasOwnProperty('treeData')) {
          config.treeData = resData;
        }
        if (cb) {
          cb(resData);
        }
      });
    },
    getFlowChart() {
      let data = FlowChart.getData();
      data.path = sessionStorage.getItem('path');
      data.transformationId = this.$store.state.transformationId; //transformationId获取方式由sessionStorage更换为store，避免多窗口同时运行导致的transformationId值丢失
      data.type = 'transformation';
      // 验证每个节点是否绑定nodeId，如果没有则取id值或者data中nodeId值赋值，避免节点名称重复验证接口报错    --Leo 20210519
      if (data.nodes && data.nodes.length) {
        data.nodes.forEach(ele => {
          if (!ele.nodeId) {
            ele.nodeId = ele.id || ele.data.nodeId || '';
          }
        });
      }

      return data;
    }
  }
};
</script>
<style lang="scss" scoped>
.design-container {
  width: 100%;
  height: 100%;
  display: flex;
}
</style>

