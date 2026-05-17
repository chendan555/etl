/**
 * Create by Leo
 * 系统首页模块API：MainPage
 * 基于baseApi进行扩展
 */

import baseApi from '../base/baseApi';
export default class MainPage extends baseApi {
  /**
   * 获取引接数量统计
   * params：参数
   */
  getReceiveItemStat(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .get('/api/yj/index/config/merge/num.do', { params })
        .then(resolve)
        .catch(reject);
    });
  }
  /**
   * 获取引接状态统计
   */
  getReceiveItemStatus() {
    return new Promise((resolve, reject) => {
      this.$http
        .get('/api/yj/index/config/exec/situation.do')
        .then(resolve)
        .catch(reject);
    });
  }
  /**
   * 获取引接总数统计
   */
  getReceiveTotal() {
    return new Promise((resolve, reject) => {
      this.$http
        .get('/api/yj/index/cumulative/volume.do')
        .then(resolve)
        .catch(reject);
    });
  }
}
