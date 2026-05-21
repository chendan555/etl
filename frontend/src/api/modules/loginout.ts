/**
 * Create by Leo
 * 系统登录登出功能模块API：UserService
 * 基于baseApi进行扩展
 */

import BaseApi from '../base/baseApi';

// 登录登出相关接口
export default class UserService extends BaseApi {
  /**
   * 登录
   * params：参数
   */
  login(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/sys/login', params)
        .then(resolve)
        .catch(reject);
    });
  }
  /**
   * 登出
   * params：参数
   */
  logout(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/v1/auth/user/logout', params)
        .then(resolve)
        .catch(reject);
    });
  }
}
