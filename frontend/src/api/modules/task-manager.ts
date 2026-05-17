/**
 * Create by Leo
 * 系统任务管理模块API：TaskManagerService
 * 基于baseApi进行扩展
 */
import BaseApi from '../base/baseApi';
/**
 * 转换为FormData
 * params：接口参数
 */
function transferToFormData(params: any) {
  let formData = new FormData();
  for (let key in params) {
    formData.append(key, typeof params[key] === 'string' ? params[key] : JSON.stringify(params[key]));
  }
  let config = {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  };

  return {
    formData,
    config
  };
}
// 任务管理相关接口
export default class TaskManagerService extends BaseApi {
  /**
   * 导入任务
   * params：参数
   */
  apiImportTask(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/task/plan/import/task/node.do', params, { timeout: 5 * 60 * 1000, headers: { 'Content-Type': 'multipart/form-data' } })
        .then(resolve)
        .catch(reject);
    });
  }

  /**
   * 获取任务列表
   * params：参数
   */
  getTaskLists() {
    return new Promise((resolve, reject) => {
      this.$http
        .get('/api/task/plan/task/node/list.do')
        .then(resolve)
        .catch(reject);
    });
  }

  /* getTaskLists() {
    const params = {
      userId: JSON.parse(sessionStorage.getItem('user') || '').id,
      processName: '数据引接处理',
      pageNum: 1,
      pageSize: 100000
    };
    return new Promise((resolve, reject) => {
      this.$http
        .post('/management-task/taskManagement/task/selectTaskNode', params)
        .then(resolve)
        .catch(reject);
    });
  } */

  // 通过ID获取任务画布数据
  /* getTaskFlowById(data: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .get(`/management-task/taskManagement/task/selecTaskDataById`, { data })
        .then(resolve)
        .catch(reject);
    });
  } */
  /**
   * 根据任务id获取画布信息
   * taskId：参数任务id
   */
  getTaskFlowById(taskId: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .get(`/api/task/plan/map/info/${taskId}.do`)
        .then(resolve)
        .catch(reject);
    });
  }

  /**
   * 保存画布数据
   * params：参数
   */
  saveTaskFlow(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post(`/api/task/plan/save/both.do`, params)
        .then(resolve)
        .catch(reject);
    });
  }
  /**
   * 删除画布数据
   * params：参数
   */
  deleteTaskFlow(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .delete(`/api/task/plan/delete/both.do`, { params })
        .then(resolve)
        .catch(reject);
    });
  }
  /**
   * 获取数据源
   * params：参数
   */
  apiGetDatabases() {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/common/getDatabases.do')
        .then(resolve)
        .catch(reject);
    });
  }

  /**
   * 获取数据模式
   * params：参数
   */
  apiGetDataSchema(params: any) {
    let newConfig = transferToFormData(params);
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/database/getSchema.do', newConfig.formData, newConfig.config)
        .then(resolve)
        .catch(reject);
    });
  }

  /**
   * 获取数据表
   * params：参数
   */
  apiGetDataTable(params: any) {
    let newConfig = transferToFormData(params);
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/database/getDataTables.do', newConfig.formData, newConfig.config)
        .then(resolve)
        .catch(reject);
    });
  }

  /**
   * 获取仿真数据
   * params：参数
   */
  apiGetArtificialData() {
    return new Promise((resolve, reject) => {
      this.$http
        .get('/api/data/source/artificial/list.do')
        .then(resolve)
        .catch(reject);
    });
  }

  /**
   * 上传文件
   * params：参数
   */
  apiUploadFile(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/task/plan/upload/file.do', params, { timeout: 5 * 60 * 1000, headers: { 'Content-Type': 'multipart/form-data' } })
        .then(resolve)
        .catch(reject);
    });
  }

  /**
   * 保存任务节点配置
   * params：参数
   */
  apiSaveTaskNode(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/task/plan/node/data/sourceAndFile/save.do', params)
        .then(resolve)
        .catch(reject);
    });
  }

  /**
   * 任务节点回现配置数据
   * params：参数
   */
  getTaskNodeDetails(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .get(`/api/task/plan/node/data/source/find.do`, { params })
        .then(resolve)
        .catch(reject);
    });
  }

  /**
   * 获取表单数据列表
   * params：参数
   */
  getFormTableList() {
    const params = {
      current: 1,
      size: 99999999,
      projectId: '',
      token: '16b14ebd1a9644728fd8fd99d325ab76'
    };
    return new Promise((resolve, reject) => {
      this.$http
        .get(`/api/from/list`, { params })
        .then(resolve)
        .catch(reject);
    });
  }
}
