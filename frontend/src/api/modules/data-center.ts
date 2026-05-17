/* 
  数据源管理模块接口类DataCenterService
  基于baseApi进行扩展
*/

import BaseApi from '../base/baseApi';

// 转换为FormData
function transferToFormData(params: any) {
  const formData = new FormData();
  for (const key in params) {
    formData.append(key, typeof params[key] === 'string' ? params[key] : JSON.stringify(params[key]));
  }
  const config = {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  };

  return {
    formData,
    config
  };
}

// 数据中心相关接口
export default class DataCenterService extends BaseApi {
  getColumnDataList(params: any) {
    const newConfig = this.transferToFormData(params);
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/database/yj/getValusByColumn.do', newConfig.formData, newConfig.config)
        .then(resolve)
        .catch(reject);
    });
  }
  /* 
    通过数据库新建数据源
    - params：参数
  */
  saveDataSource(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/v1/center/database/save', params)
        .then(resolve)
        .catch(reject);
    });
  }

  /* 
    测试数据库连接
     - params：参数
  */
  testConnect(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/v1/center/database/connect/dm/testConnect', params)
        .then(resolve)
        .catch(reject);
    });
  }

  /* 
    获取数据源列表
     - params：参数
  */
  getDataSourceLists(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .get('/v1/center/database/list/page', { params })
        .then(resolve)
        .catch(reject);
    });
  }

  /* 
    删除数据源
     - params：参数
  */
  delDataSource(params: any) {
    const newConfig = transferToFormData(params);
    return new Promise((resolve, reject) => {
      this.$http
        .post('/v1/center/database/del', newConfig.formData, newConfig.config)
        .then(resolve)
        .catch(reject);
    });
  }

  /* 
    获取分配项目详情
     - params：参数
  */
  getAllotDetails(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/v1/center/database/task/group/list', params)
        .then(resolve)
        .catch(reject);
    });
  }

  /* // 获取分配项目详情
  getDatabaseDetail(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .get('/v1/center/database/connect/dm/getTableInfo', { params })
        .then(resolve)
        .catch(reject);
    });
  } */

  /* 
    获取关联项目详情
     - params：参数
  */
  getLinkDatabase(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .get('/v1/center/database/back/show/assign', { params })
        .then(resolve)
        .catch(reject);
    });
  }

  /* 
    获取数据表列表
     - params：参数
  */
  getDataSheetList(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .get('/v1/center/database/connect/dm/getTables', { params })
        .then(resolve)
        .catch(reject);
    });
  }

  /* 
    获取表视图列表
     - params：参数
  */
  getSheetViewList(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .get('/v1/center/database/connect/dm/getViews', { params })
        .then(resolve)
        .catch(reject);
    });
  }

  /* 
    获取表字段详情列表
     - params：参数
  */
  getSheetDetail(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .get('/v1/center/database/connect/dm/getColumns', { params })
        .then(resolve)
        .catch(reject);
    });
  }

  /* 
    获取表数据详情列表
     - params：参数
  */
  getSheetColumn(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .get('/v1/center/database/connect/dm/getTableData', { params })
        .then(resolve)
        .catch(reject);
    });
  }

  /* 
    获取分配项目详情
     - params：参数
  */
  delAllotItem(params: any) {
    const newConfig = transferToFormData(params);
    return new Promise((resolve, reject) => {
      this.$http
        .post('/v1/center/database/task/group/del', newConfig.formData, newConfig.config)
        .then(resolve)
        .catch(reject);
    });
  }

  /* 
    获取左侧tree节点
     - params：参数
  */
  getTree(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .get('/v1/center/database/categories/tree', params)
        .then(resolve)
        .catch(reject);
    });
  }

  /* 
    添加根级目录节点
     - params：参数
  */
  saveRootNode(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/v1/center/database/categories/save', params)
        .then(resolve)
        .catch(reject);
    });
  }

  /* 
    删除tree节点
     - params：参数
  */
  delTreeNode(params: any) {
    const newConfig = transferToFormData(params);
    return new Promise((resolve, reject) => {
      this.$http
        .post('/v1/center/database/categories/del', newConfig.formData, newConfig.config)
        .then(resolve)
        .catch(reject);
    });
  }

  /* 
    获取分配数据源
     - params：参数
  */
  getAllotSource(params: any) {
    const newConfig = transferToFormData(params);
    return new Promise((resolve, reject) => {
      this.$http
        .post('/v1/center/database/assign/tree', newConfig.formData, newConfig.config)
        .then(resolve)
        .catch(reject);
    });
  }

  /* 
    分配数据源
     - params：参数
  */
  saveAllotSource(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/v1/center/database/assign/save', params)
        .then(resolve)
        .catch(reject);
    });
  }

  /* 
    数据源集合查询
     - params：参数
  */
  getDatabases(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/common/getDatabases.do', { params })
        .then(resolve)
        .catch(reject);
    });
  }

  /* 
    数据模式表查询
     - params：参数
  */
  getSchema(params: any) {
    const newConfig = this.transferToFormData(params);
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/database/getSchema.do', newConfig.formData, newConfig.config)
        .then(resolve)
        .catch(reject);
    });
  }

  /* 
    表结构查询
     - params：参数
  */
  getColumnsByTable(params: any) {
    const newConfig = this.transferToFormData(params);
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/database/getColumnsByTable.do', newConfig.formData, newConfig.config)
        .then(resolve)
        .catch(reject);
    });
  }

  /* 
    编辑数据源
     - params：参数
  */
  saveDatabase(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/v1/center/database/save.do', params, { headers: { 'Content-Type': 'application/json;charset=UTF-8' } })
        .then(resolve)
        .catch(reject);
    });
  }

  /* 
    删除数据源
     - params：参数
  */
  delDatabase(params: any) {
    const newConfig = transferToFormData(params);
    return new Promise((resolve, reject) => {
      this.$http
        .post('/v1/center/database/del.do', newConfig.formData, newConfig.config)
        .then(resolve)
        .catch(reject);
    });
  }

  /* 
    获取仿真的树状图的结构
     - params：参数
  */
  getArtificial(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .get('/api/data/source/artificial/list.do', { params })
        .then(resolve)
        .catch(reject);
    });
  }

  /* 
    新增或者修改仿真的树状图的结构
     - params：参数
  */
  saveArtificial(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/data/source/artificial/save/or/edit.do', params)
        .then(resolve)
        .catch(reject);
    });
  }

  /* 
    删除文件服务器或仿真数据地址
     - id：服务器id
  */
  deleteArtificial(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .delete(`/api/data/source/delete/${params.id}.do`)
        .then(resolve)
        .catch(reject);
    });
  }

  /* 
  保存文件服务器
  - sourceId     --引接服务来源ID
  - name --名称
  - sourceUrl --地址
  */
  saveFileServer(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/data/source/file/save/or/edit.do', params)
        .then(resolve)
        .catch(reject);
    });
  }
  /* 
    获取文件服务器地址列表
     - params：参数
  */
  getFileServerList(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .get('/api/data/source/file/list.do', { params })
        .then(resolve)
        .catch(reject);
    });
  }
  /* 
  保存仿真数据
  - sourceId     --引接服务来源ID
  - name --名称
  - sourceUrl --地址
  */
  saveEmulateServer(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/data/source/artificial/save/or/edit.do', params)
        .then(resolve)
        .catch(reject);
    });
  }
  /* 
    获取文件服务器文件列表 
  */
  getFileDataList(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .get('/api/data/source/fileMessage/list/all.do', { params })
        .then(resolve)
        .catch(reject);
    });
  }
  /* 
    文件数据下载查看
     - params：参数
  */
  downloadFile(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .get('/api/data/source/fileMessage/download.do', { params })
        .then(resolve)
        .catch(reject);
    });
  }
  /* 
    ftp文件上传
     - params：参数
  */
  uploadFtpFiles(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/data/source/fileMessage/ftp/upload.do', params)
        .then(resolve)
        .catch(reject);
    });
  }
  /* 
    上传记录查询
     - params：参数
  */
  getUploadRecords(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/data/source/fileMessage/ftp/record/list.do', params)
        .then(resolve)
        .catch(reject);
    });
  }
}
