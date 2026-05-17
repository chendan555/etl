/**
 * Create by Leo
 * 系统处理流程设计功能模块API：ProjectManage
 * 基于baseApi进行扩展
 */

import baseApi from '../base/baseApi';
export default class ProjectManage extends baseApi {
  /**
   * 设置不取消模板
   * data：参数
   */
  updateTransToModel(data: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .put('/api/repository/new/updateTransToModel.do', data)
        .then(resolve)
        .catch(reject);
    });
  }
  /**
   * 复制模板
   * params：参数
   */
  addCopyTrans(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/repository/new/addCopyTrans.do', params)
        .then(resolve)
        .catch(reject);
    });
  }
  /**
   * 树状的结构
   */
  getGroupList() {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/task/group/getGroupList.do')
        .then(resolve)
        .catch(reject);
    });
  }
  /**
   * 流程编辑中
   * id：流程id
   */
  setTransEditing(id: string) {
    return new Promise((resolve, reject) => {
      this.$http
        .put(`/api/task/compiling/${id}.do`)
        .then(resolve)
        .catch(reject);
    });
  }
  /**
   * 流程可编辑
   * id：流程id
   */
  setTransEditable(id: string) {
    return new Promise((resolve, reject) => {
      this.$http
        .put(`/api/task/may/compile/${id}.do`)
        .then(resolve)
        .catch(reject);
    });
  }
}
