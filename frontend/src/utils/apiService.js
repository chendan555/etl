/**
 * Create by leo
 * 系统接口方法文件
 * 定义各类在功能模块均可使用的接口方法函数
 * 减少代码量
 *
 */

import request from './request';
/**
 * 定义类
 */
export class ApiService {
  constructor(url) {
    this.baseUrl = url;
  }

  // 数据入库回显
  /**
   * 定义方法
   */
  getRukuData(query) {
    return request({
      url: this.baseUrl + '/find/ruku/data',
      method: 'get',
      params: query
    });
  }

  // 动态表格配置数据库
  /**
   * 定义方法
   */
  createTable(data) {
    return request({
      url: this.baseUrl + '/create/table',
      method: 'post',
      data: data
    });
  }
  /**
   * 定义方法
   */
  getTableData(query) {
    return request({
      url: this.baseUrl + '/databaseTable/getTableColumns',
      method: 'get',
      params: query
    });
  }

  // 获取图片库左侧菜单列表
  /**
   * 定义方法
   */
  getleftTree() {
    return request({
      url: this.baseUrl + '/leftTree',
      method: 'get'
    });
  }

  // 获取图片库左侧菜单列表
  /**
   * 定义方法
   */
  getleftTreeByLazy() {
    return request({
      url: this.baseUrl + '/leftTreeByLazy',
      method: 'get'
    });
  }
  // 获取图片数据列表
  /**
   * 定义方法
   */
  getIcon(query) {
    return request({
      url: this.baseUrl + '/query',
      method: 'get',
      params: query
    });
  }
  // 移动图片
  /**
   * 定义方法
   */
  moveIcon(data) {
    return request({
      url: this.baseUrl + '/move',
      method: 'post',
      data: data
    });
  }
  // 修改图片
  /**
   * 定义方法
   */
  changeIconName(data) {
    return request({
      url: this.baseUrl + '/changeIconName',
      method: 'post',
      data: data
    });
  }
  // 新增图片
  /**
   * 定义方法
   */
  addIcon(data) {
    return request({
      url: this.baseUrl + '/save/batch',
      method: 'post',
      data: data
    });
  }

  //获取列表
  /**
   * 定义方法
   */
  list(query) {
    return request({
      url: this.baseUrl + '/list',
      method: 'get',
      params: query
    });
  }

  //获取列表所有数据
  /**
   * 定义方法
   */
  all(query) {
    return request({
      url: this.baseUrl + '/all',
      method: 'get',
      params: query
    });
  }

  //对树形结构的数据进行获取
  /**
   * 定义方法
   */
  tree(query) {
    return request({
      url: this.baseUrl + '/tree',
      method: 'get',
      params: query
    });
  }
  /**
   * 定义方法
   */
  select(query) {
    return request({
      url: this.baseUrl + '/select',
      method: 'get',
      params: query
    });
  }

  //新增
  /**
   * 定义方法
   */
  create(data) {
    return request({
      url: this.baseUrl + '/save',
      method: 'post',
      data: data
    });
  }
  //批量新增
  /**
   * 定义方法
   */
  createM(data) {
    return request({
      url: this.baseUrl + '/save/batch', //修改接口地址  -Leo
      method: 'post',
      data: data
    });
  }

  //新增
  /**
   * 定义方法
   */
  save(data) {
    return request({
      url: this.baseUrl + '/save', //修改接口地址  -Leo
      method: 'post',
      data: data,

      headers: { 'Content-Type': 'multipart/form-data' }
    });
  }

  //修改
  /**
   * 定义方法
   */
  update(data) {
    return request({
      url: this.baseUrl + '/update',
      method: 'put',
      data: data
    });
  }

  //科目管理 模块管理 修改公用save接口
  /**
   * 定义方法
   */
  newUpdate(data) {
    return request({
      url: this.baseUrl + '/save', //修改接口地址  -Leo
      method: 'post',
      data: data,

      headers: { 'Content-Type': 'application/json;charset=UTF-8' }
    });
  }

  //坑
  /**
   * 定义方法
   */
  updateFormData(data) {
    return request({
      url: this.baseUrl + '/update',
      method: 'post',
      data: data
    });
  }

  //删除
  /**
   * 定义方法
   */
  delete(data) {
    return request({
      url: this.baseUrl + '/delete',
      method: 'delete',
      data: data
    });
  }

  //GET方式删除
  /**
   * 定义方法
   */
  deleteByGet(params) {
    return request({
      url: this.baseUrl + '/delete',
      method: 'get',
      params
    });
  }
  //获取详情
  /**
   * 定义方法
   */
  detail(data) {
    return request({
      url: this.baseUrl + '/info/' + data.id,
      method: 'get'
    });
  }

  //  下发
  /**
   * 定义方法
   */
  xiafa(data) {
    return request({
      url: this.baseUrl + '/xiafa',
      method: 'post',
      data: data
    });
  }

  //  上级报批
  /**
   * 定义方法
   */
  reportApprove(data) {
    return request({
      url: this.baseUrl + '/baopi',
      method: 'post',
      data: data
    });
  }

  //  审批
  /**
   * 定义方法
   */
  approve(data) {
    return request({
      url: this.baseUrl + '/shenpi',
      method: 'post',
      data: data
    });
  }

  // 批复录入
  /**
   * 定义方法
   */
  approveWrite(data) {
    return request({
      url: this.baseUrl + '/pifuluru',
      method: 'post',
      data: data
    });
  }

  //  保存并提交
  /**
   * 定义方法
   */
  saveAndCommit(data) {
    return request({
      url: this.baseUrl + '/saveAndCommit',
      method: 'post',
      data: data
    });
  }
  //问题提交的保存及更新
  /**
   * 定义方法
   */
  saveOrUpdate(data) {
    return request({
      url: this.baseUrl + '/saveOrUpdatePC',
      method: 'post',
      data: data
    });
  }
  //  更新并提交
  /**
   * 定义方法
   */
  updateAndCommit(data) {
    return request({
      url: this.baseUrl + '/ updateAndCommit',
      method: 'post',
      data: data
    });
  }

  //  提交
  /**
   * 定义方法
   */
  commit(data) {
    return request({
      url: this.baseUrl + '/commit',
      method: 'post',
      data: data
    });
  }

  //撤销
  /**
   * 定义方法
   */
  revert(id) {
    return request({
      url: this.baseUrl + '/chehui/' + id,
      method: 'get'
    });
  }
}

//不默认增加后缀的api服务
/**
 * 定义方法
 */
export class BaseApiService {
  constructor(url) {
    this.baseUrl = url;
  }
  /**
   * 定义方法
   */
  get(query) {
    return request({
      url: this.baseUrl,
      method: 'get',
      params: query
    });
  }

  //新增
  /**
   * 定义方法
   */
  post(data) {
    return request({
      url: this.baseUrl,
      method: 'post',
      data: data
    });
  }

  //修改
  /**
   * 定义方法
   */
  put(data) {
    return request({
      url: this.baseUrl,
      method: 'put',
      data: data
    });
  }

  //删除
  /**
   * 定义方法
   */
  delete(data) {
    return request({
      url: this.baseUrl,
      method: 'delete',
      data: data
    });
  }

  //撤销
  /**
   * 定义方法
   */
  revert(id) {
    return request({
      url: this.baseUrl + '/' + id,
      method: 'post'
    });
  }

  //get方式的提交请求，路径需要拼接id
  /**
   * 定义方法
   */
  getById(id) {
    return request({
      url: this.baseUrl + '/' + id,
      method: 'get'
    });
  }

  //post方式的提交请求，路径需要拼接id
  /**
   * 定义方法
   */
  postById(id) {
    return request({
      url: this.baseUrl + '/' + id,
      method: 'post'
    });
  }
}
/**
 * 定义方法
 */
export class MyApiService {
  constructor(url) {
    this.baseUrl = url;
  }
  /**
   * 定义方法
   */
  get(query, url) {
    return request({
      url: this.baseUrl + url,
      method: 'get',
      params: query
    });
  }

  //新增
  /**
   * 定义方法
   */
  post(data, url) {
    return request({
      url: this.baseUrl + url,
      method: 'post',
      data: data
    });
  }

  //修改
  /**
   * 定义方法
   */
  put(data, url) {
    return request({
      url: this.baseUrl + url,
      method: 'put',
      data: data
    });
  }

  //删除
  /**
   * 定义方法
   */
  delete(data, url) {
    return request({
      url: this.baseUrl + url,
      method: 'delete',
      data: data
    });
  }

  //撤销
  /**
   * 定义方法
   */
  revert(id, url) {
    return request({
      url: this.baseUrl + url + '/' + id,
      method: 'post'
    });
  }

  //get方式的提交请求，路径需要拼接id
  /**
   * 定义方法
   */
  getById(id, url) {
    return request({
      url: this.baseUrl + url + '/' + id,
      method: 'get'
    });
  }

  //post方式的提交请求，路径需要拼接id
  /**
   * 定义方法
   */
  postById(id, url) {
    return request({
      url: this.baseUrl + url + '/' + id,
      method: 'post'
    });
  }
}
