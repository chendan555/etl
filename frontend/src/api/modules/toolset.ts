/**
 * Create by Leo
 * 系统工具集模块模块API：Toolset
 * 基于baseApi进行扩展
 */

import baseApi from '../base/baseApi';
export default class Toolset extends baseApi {
  /**
   * 保存工具集配置
   * params：参数
   */
  saveToolset(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/dll/exec/model/save.do', params, { headers: { 'Content-Type': 'multipart/form-data' } })
        .then(resolve)
        .catch(reject);
    });
  }
  /**
   * 获取工具集列表
   * params：参数
   */
  getToolsetList(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post(`/api/dll/exec/list/page.do`, params)
        .then(resolve)
        .catch(reject);
    });
  }
  /**
   * 文件上传
   * params：参数
   */
  uploadDataFile(formData: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/dll/exec/data/file/upload.do', formData, { headers: { 'Content-Type': 'multipart/form-data' } })
        .then(resolve)
        .catch(reject);
    });
  }
  /**
   * 删除工具集
   * params：参数
   */
  deleteToolset(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .get(`/api/dll/exec/model/del.do`, { params })
        .then(resolve)
        .catch(reject);
    });
  }
  /**
   * 执行工具集
   * params：参数
   */
  runToolset(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post(`/api/dll/exec/model/exec.do`, params, { responseType: 'blob' })
        .then(resolve)
        .catch(reject);
    });
  }
}
