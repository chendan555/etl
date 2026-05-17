/* 
  Created By Leo 
  创建接口基础类BaseApi
  $http：BaseApi中接口请求二次封装
  transferToFormData：接口参数转formData方法
*/

import $http from './http';
export default class BaseApi {
  protected $http = $http;
  protected transferToFormData = function(params: any) {
    let formData = new FormData();
    // 将参数逐个封装进formData中
    for (let key in params) {
      formData.append(key, typeof params[key] === 'string' ? params[key] : JSON.stringify(params[key]));
    }
    // 配置头部信息
    let config = {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    };
    // 返还包含formData格式参数和头部配置config
    return {
      formData,
      config
    };
  };
  constructor() {}
}
