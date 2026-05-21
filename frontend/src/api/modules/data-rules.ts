/**
 * Create by Leo
 * 数据规则模块API：DataRules
 * 基于baseApi进行扩展
 */

import BaseApi from '../base/baseApi';

// 转换为FormData
function transferToFormData(params: any) {
  let formData = new FormData();
  for (let key in params) {
    formData.append(key, typeof params[key] === 'string' ? params[key] : JSON.stringify(params[key]));
  }
  let config = {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  };

  return {
    formData,
    config
  };
}

/**
 * 数据规则相关接口
 * params：参数
 */
export default class DataRules extends BaseApi {
  /**
   * 新增规则
   * params：参数
   */
  apiSaveNewRules(params: any) {
    let newConfig = transferToFormData(params);
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/transNew/rule/save', newConfig.formData, newConfig.config)
        .then(resolve)
        .catch(reject);
    });
  }
  /**
   * 删除规则
   * params：参数
   */
  apiDelRules(params: any) {
    let newConfig = transferToFormData(params);
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/transNew/rule/delete', newConfig.formData, newConfig.config)
        .then(resolve)
        .catch(reject);
    });
  }
  /**
   * 获取单条规则详情
   * params：参数
   */
  apiGetRuleInfo(params: any) {
    let newConfig = transferToFormData(params);
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/transNew/rule/find', newConfig.formData, newConfig.config)
        .then(resolve)
        .catch(reject);
    });
  }
  /**
   * 获取规则集合
   * params：参数
   */
  apiGetRulelists(params: any) {
    let newConfig = transferToFormData(params);
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/transNew/rule/findTypeList', newConfig.formData, newConfig.config)
        .then(resolve)
        .catch(reject);
    });
  }
  /**
   * 保存评价计划
   * params：参数
   */
  apiGetPlanInfo(params: any) {
    let newConfig = transferToFormData(params);
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/transNew/assess/find', newConfig.formData, newConfig.config)
        .then(resolve)
        .catch(reject);
    });
  }
  /**
   * 保存评价计划
   * params：参数
   */
  apiSaveRuleplan(params: any) {
    let newConfig = transferToFormData(params);
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/transNew/assess/save', newConfig.formData, newConfig.config)
        .then(resolve)
        .catch(reject);
    });
  }
  /**
   * 保存评价计划
   * params：参数
   */
  apiGetRuleplan(params: any) {
    let newConfig = transferToFormData(params);
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/transNew/assess/page', newConfig.formData, newConfig.config)
        .then(resolve)
        .catch(reject);
    });
  }
  /**
   * 数据审核列表
   * params：参数
   */
  apiGetAuditLists(params: any) {
    let newConfig = transferToFormData(params);
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/transNew/audit/page', newConfig.formData, newConfig.config)
        .then(resolve)
        .catch(reject);
    });
  }
  /**
   * 单个数据审核详情
   * params：参数
   */
  apiGetAuditInfo(params: any) {
    let newConfig = transferToFormData(params);
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/transNew/audit/find', newConfig.formData, newConfig.config)
        .then(resolve)
        .catch(reject);
    });
  }
  /**
   * 执行审核
   * params：参数
   */
  apiSaveAudit(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/transNew/audit/auditById', params)
        .then(resolve)
        .catch(reject);
    });
  }
  /**
   * 整编目录树查询
   */
  apiGetCategoryCacheData() {
    return new Promise((resolve, reject) => {
      this.$http
        .get('/api/common/CategoryCache/tree')
        .then(resolve)
        .catch(reject);
    });
  }
  /**
   * 新增整编目录树节点
   * params：参数
   */
  apiAddCategory(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/common/CategoryCache/save', params)
        .then(resolve)
        .catch(reject);
    });
  }
  /**
   * 删除整编目录树节点
   * params：参数
   */
  apiDelCategory(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .get('/api/common/CategoryCache/deleteBatchIds', { params })
        .then(resolve)
        .catch(reject);
    });
  }
  /**
   * 更新整编目录树节点
   * params：参数
   */
  apiUpdateCatalog(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/common/CategoryCache/update', params)
        .then(resolve)
        .catch(reject);
    });
  }
  /**
   * 数据整编更新
   * params：参数
   */
  apiUpdateCategory(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/transNew/update/reorganization', params)
        .then(resolve)
        .catch(reject);
    });
  }
  /**
   * params：参数
   * 获取任务列表
   */
  getTaskList(params?: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .get('/v1/task/job/page', { params })
        .then(res => {
          resolve(res.data);
        })
        .catch(reject);
    });
  }
  /**
   * 首页统计-质量审核
   */
  apiGetAuditResult() {
    return new Promise((resolve, reject) => {
      this.$http
        .get('/api/transNew/census/auditResult')
        .then(resolve)
        .catch(reject);
    });
  }
  /**
   * 首页统计-数据处理流程
   */
  apiGetRunResult() {
    return new Promise((resolve, reject) => {
      this.$http
        .get('/api/transNew/census/runResult')
        .then(resolve)
        .catch(reject);
    });
  }
  /**
   * 首页统计-数据整编
   * params：参数
   */
  apiGetCategroy(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .get('/api/transNew/census/categroy', params)
        .then(resolve)
        .catch(reject);
    });
  }
  /**
   * 首页统计-数据处理概览
   */
  apiGetAllNums() {
    return new Promise((resolve, reject) => {
      this.$http
        .get('/api/transNew/census/allNums')
        .then(resolve)
        .catch(reject);
    });
  }
  /**
   * 首页统计-数据质量评价
   */
  apiGetQuality() {
    return new Promise((resolve, reject) => {
      this.$http
        .get('/api/transNew/census/checkNum')
        .then(resolve)
        .catch(reject);
    });
  }
  /**
   * 首页统计-补充数据
   * params：参数
   */
  apiGetErrorForm(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .get('/api/transNew/errorForm/page', { params })
        .then(resolve)
        .catch(reject);
    });
  }
  /**
   * 数据清洗与格式化-删除TREE节点
   * params：参数
   */
  apiDelFormatTree(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .get('/api/trans/plan/categroy/tree/delete', { params })
        .then(resolve)
        .catch(reject);
    });
  }
  /**
   * 数据清洗与格式化-创建TREE节点
   * params：参数
   */
  apiSaveFormatTree(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .get('/api/trans/plan/categroy/tree/save', { params })
        .then(resolve)
        .catch(reject);
    });
  }
  /**
   * 根据任务Id获取任务树
   * @param taskId 任务Id
   */
  getTaskNodes(taskId: string) {
    return new Promise((resolve, reject) => {
      this.$http
        .get('v1/task/node/find', {
          params: {
            taskId
          }
        })
        .then(res => {
          resolve(res.data);
        })
        .catch(reject);
    });
  }
  /**
   * 根据任务Id获取任务节点列表详情
   * @param taskId 任务Id
   */
  getTaskNodeDetails(taskId: string) {
    return new Promise((resolve, reject) => {
      this.$http
        .get('/v1/task/job/findOne', {
          params: {
            taskId
          }
        })
        .then(res => {
          resolve(res.data);
        })
        .catch(reject);
    });
  }
}
