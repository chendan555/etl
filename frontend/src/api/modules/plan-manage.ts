/**
 * Create by Leo
 * 系统处理方案管理功能模块API：PlanManage
 * 基于baseApi进行扩展
 */

import baseApi from '../base/baseApi';
export default class PlanManage extends baseApi {
  /**
   * 新增规则
   * params：参数
   */
  getTransByPlanId(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .get('/api/trans/plan/getTransByPlan', { params })
        .then(resolve)
        .catch(reject);
    });
  }
  /**
   * 添加至案例库
   * params：参数
   */
  addToCaseBank(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .put('/api/trans/plan/update/type', { ...params })
        .then(resolve)
        .catch(reject);
    });
  }
  /**
   * 从案例库中删除
   * params：参数
   */
  deleteFromCaseBank(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/trans/plan/quit/type', params)
        .then(resolve)
        .catch(reject);
    });
  }
}
