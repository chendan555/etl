/**
 * Create by Leo
 * 批量数据引接模块API：DataReceive
 * 基于baseApi进行扩展
 */

import baseApi from '../base/baseApi';
export default class DataReceive extends baseApi {
  /** 
   * 获取数据表引接列表
   * params：参数
   */
  getTableRelationList(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/yj/exec/table/page.do', params)
        .then(resolve)
        .catch(reject);
    });
  }
  /** 
   * 获取文件引接列表
   * params：参数
   */
  getFileReceiveList(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/yj/exec/file/page.do', params)
        .then(resolve)
        .catch(reject);
    });
  }
  /** 
   * 获取批次号
   */
  getBatchNumber() {
    return new Promise((resolve, reject) => {
      this.$http
        .get('/api/getBatchNumber')
        .then(resolve)
        .catch(reject);
    });
  }
  /** 
   * 引接数据
   * params：参数
   */
  receiveData(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/receiveData', params)
        .then(resolve)
        .catch(reject);
    });
  }
  /** 
   * 数据库引接的数据
   * params：参数
   */
  receiveExec(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/yj/exec/table/exec.do', params)
        .then(resolve)
        .catch(reject);
    });
  }
  /** 
   * 引接仿真数据
   * params：参数
   */
  receiveEmulate(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/es/imitate/call.do', params)
        .then(resolve)
        .catch(reject);
    });
  }
  /** 
   * 获取仿真数据引接列表
   * params：参数
   */
  getEmulateData(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/yj/record/simulation/page.do', params)
        .then(resolve)
        .catch(reject);
    });
  }
  /** 
   * 引接文件数据
   * params：参数
   */
  receiveFileData(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/file/analysis/context.do', params)
        .then(resolve)
        .catch(reject);
    });
  }
  /** 
   * 引接记录查询,数据表分页列表
   * params：参数
   */
  yjRecordData(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/yj/record/history/page.do', params)
        .then(resolve)
        .catch(reject);
    });
  }
  /** 
   * 引接记录删除
   * params：参数
   */
  removeRecordData(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .get('/api/yj/record/history/del.do', { params })
        .then(resolve)
        .catch(reject);
    });
  }
  /** 
   * 引接记录查询,查看
   * params：参数
   */
  seeYjData(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/yj/record/check/page.do', params)
        .then(resolve)
        .catch(reject);
    });
  }
  /** 
   * 文件引接记录查询,查看
   * params：参数
   */
  getFileReceiveData(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .get('/api/file/analysis/whereabouts.do', { params })
        .then(resolve)
        .catch(reject);
    });
  }
  /** 
   * 生成JSON复盘数据配置保存
   * params：参数
   */
  saveJsonConfig(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/yj/jsonData/saveJsonConfig.do', params)
        .then(resolve)
        .catch(reject);
    });
  }
  /** 
   * 获取JSON复盘数据配置
   * params：参数
   */
  getJsonConfig(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .get('/api/yj/jsonData/queryJsonConfigById.do', { params })
        .then(resolve)
        .catch(reject);
    });
  }
  /** 
   * 获取新建的批次号
   * params：参数
   */
  getCreatedBatchNum() {
    return new Promise((resolve, reject) => {
      this.$http
        .get('/api/yj/batch/num/generate.do')
        .then(resolve)
        .catch(reject);
    });
  }
}
