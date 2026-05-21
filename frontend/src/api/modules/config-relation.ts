/* 
  定义主子表关系模块接口类ConfigRelation
  基于baseApi进行扩展
*/

import baseApi from '../base/baseApi';
export default class ConfigRelation extends baseApi {
  /* 
    获取主子表配置列表
     - params：参数
  */
  getTableRelationList(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/yj/config/table/page', params)
        .then(resolve)
        .catch(reject);
    });
  }
  /* 
    删除主子表配置数据
     - params：参数
  */
  deleteTableRelation(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/yj/config/table/delete', params)
        .then(resolve)
        .catch(reject);
    });
  }
  /* 
    保存、更新主子表配置数据
     - params：参数
  */
  saveTableRelation(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/yj/config/table/save', params)
        .then(resolve)
        .catch(reject);
    });
  }
  /* 
    获取文件配置列表
     - params：参数
  */
  getFileList(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/yj/config/file/page', params)
        .then(resolve)
        .catch(reject);
    });
  }
  /* 
    保存、更新文件配置数据
     - params：参数
  */
  saveFileRelation(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/yj/config/file/save', params)
        .then(resolve)
        .catch(reject);
    });
  }
  /* 
    删除文件配置数据
     - params：参数
  */
  deleteFileRelation(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/yj/config/file/delete', params)
        .then(resolve)
        .catch(reject);
    });
  }
  /* 
    获取仿真数据列表
     - params：参数
  */
  getEmulatedataList(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/yj/config/simulation/page', params)
        .then(resolve)
        .catch(reject);
    });
  }
  /* 
    保存、更新仿真配置数据
     - params：参数
  */
  saveEmulateRelation(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/yj/config/simulation/save', params)
        .then(resolve)
        .catch(reject);
    });
  }
  /* 
    删除仿真配置数据
     - params：参数
  */
  deleteEmulateRelation(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/yj/config/simulation/delete', params)
        .then(resolve)
        .catch(reject);
    });
  }
  /* 
    获取表单数据列表
     - params：参数
  */
  getFormwordListList(data: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .get('/api/getFormwordListList', { data })
        .then(resolve)
        .catch(reject);
    });
  }
  /* 
    上传文件
     - params：参数
  */
  uploadFile(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/task/plan/upload/file/col', params)
        .then(resolve)
        .catch(reject);
    });
  }
  /* 
    合并Txt
     - params：参数
  */
  mergeTxt(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/yj/protocol/file/merge', params)
        .then(resolve)
        .catch(reject);
    });
  }
}
