/**
 * Create By WangSheng on 2020/02/20
 * 数据审核api
 * 基于request进行扩展
 */
// import request from '../base/http';
import request from '@/utils/request.js';

/**
 * 获取侧边栏数据
 */
const navTree = () => {
  return request({ url: '/api/audit/rule/types.do', method: 'POST' });
};

/**
 * 获取列表
 * params：参数
 */
const list = (params: any) => {
  return request({ url: '/api/audit/rule/page.do', method: 'POST', params });
};

/**
 * 查询详情
 * params：参数
 */
const get = (params: any) => {
  return request({ url: '/api/audit/rule/detail.do', method: 'POST', params });
};

/**
 * 新增规则
 * params：参数
 */
const add = (data: any) => {
  return request({ url: '/api/audit/rule/create.do', method: 'POST', data });
};

/**
 * 更新数据
 * params：参数
 */
const update = (data: any) => {
  return request({ url: '/api/audit/rule/edit.do', method: 'POST', data });
};

/**
 * 审核
 * params：参数
 */
const review = (params: any) => {
  return request({ url: '/api/audit/rule/audit.do', method: 'POST', params });
};

export default {
  // 侧边栏数据
  navTree,
  // 获取列表
  list,
  // 获取详情
  get,
  // 新建
  add,
  // 更新
  update,
  // 审核
  review
};
