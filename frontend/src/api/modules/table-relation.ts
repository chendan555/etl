/**
 * Create by Leo
 * 系统主子表关系模块API：TableRelation
 * 基于baseApi进行扩展
 */

import baseApi from '../base/baseApi';
export default class TableRelation extends baseApi {
  /**
   * 获取主子表关系列表
   * params：参数
   */
  getTableRelationList(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/yj/tableConfig/queryListTableConfig.do', params)
        .then(resolve)
        .catch(reject);
    });
  }
  /**
   * 保存、修改主子表关系
   * params：参数
   */
  saveTableRelation(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/yj/tableConfig/saveTableConfigAndDetail.do', params)
        .then(resolve)
        .catch(reject);
    });
  }
  /**
   * 删除主子表关系
   * params：参数
   */
  deleteTableRelation(data: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .delete(`/api/yj/tableConfig/deleteTableConfigAndDetail.do?id=${data}`)
        .then(resolve)
        .catch(reject);
    });
  }
  /**
   * 获取全部主子表关系列表
   * params：参数
   */
  getAllTableRelationList(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/yj/tableConfig/getTableConfigList.do', params)
        .then(resolve)
        .catch(reject);
    });
  }
  /**
   * 获取主子表关系详情
   * params：参数
   */
  getTableRelationInfor(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .get('/api/yj/tableConfig/queryTableConfigById.do', { params })
        .then(resolve)
        .catch(reject);
    });
  }
}
