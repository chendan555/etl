/**
 * Create by Leo
 * 系统解析协议模块API：ResolveProtocol
 * 基于baseApi进行扩展
 */

import baseApi from '../base/baseApi';
export default class ResolveProtocol extends baseApi {
  /**
   * 获取解析协议列表
   * params：参数
   */
  getResolveProtocolList(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/yj/protocol/page', params)
        .then(resolve)
        .catch(reject);
    });
  }
  /**
   * 保存、修改协议
   * params：参数
   */
  saveProtocol(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/yj/protocol/save', params)
        .then(resolve)
        .catch(reject);
    });
  }
  /**
   * 删除协议
   * params：参数
   */
  deleteProtocol(data: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .delete('/api/yj/protocol/delete', { data })
        .then(resolve)
        .catch(reject);
    });
  }
}
