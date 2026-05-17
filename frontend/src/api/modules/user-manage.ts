/**
 * Create by Leo
 * 系统用户管理模块API
 * 基于baseApi进行扩展
 */

import request from '../base/http';

/**
 * 获取菜单树
 */
const navTree = () => request({ url: '/v1/auth/org/page', method: 'GET' });

/**
 * 获取列表
 */
const list = (params: any) => request({ url: '/v1/auth/user/page', method: 'GET', params });

/**
 * 更新数据
 */
const addorupdata = (data: any) => request({ url: '/v1/auth/user/save', method: 'POST', data });

// 关联角色
const getRoles = (params: any) => request({ url: '/v1/auth/roles/page', method: 'GET', params });
// 关联角色
const deleted = (data: any) => request({ url: '/v1/auth/users/del', method: 'POST', data });

export default {
  // 获取菜单数据
  navTree, 
  // 获取列表
  list, 
  // 更新数据
  addorupdata, 
  // 关联角色
  getRoles, 
  // 删除角色
  deleted,
};
