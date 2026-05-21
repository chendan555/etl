/* 
  定义分类接口请求类函数ClassifyService
  基于BaseApi基础请求类进行扩展
*/

import BaseApi from '../base/baseApi';

export default class ClassifyService extends BaseApi {
  /* 
    分类树
     - params：参数
  */
  getClassifyFind(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .get('/api/equip/classify/tree', { params })
        .then(resolve)
        .catch(reject);
    });
  }

  /* 
    分类详情
     - params：参数
  */
  getClassifyDetails(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .get('/api/equip/classify/info', { params })
        .then(resolve)
        .catch(reject);
    });
  }

  /* 
    分类删除
     - params：参数
  */
  getClassifyDel(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .delete('/api/equip/classify/delete', { params })
        .then(resolve)
        .catch(reject);
    });
  }

  /* 
    分类保存
     - params：参数
  */
  getClassifySave(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .put('/api/equip/classify/update', params)
        .then(resolve)
        .catch(reject);
    });
  }

  /* 
    分类id
     - params：参数
  */
  getClassifyId(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .get('/api/equip/classify/selectMaxId', { params })
        .then(resolve)
        .catch(reject);
    });
  }
}
