/**
 * Create By WangSheng on 2021/02/22
 * 数据查询api
 * 基于基础请求类http
 */
import request from '../base/http';

/**
 * 查询列表
 *  - params：参数
 */
const list = (params: any) => {
  return request({ url: '/api/trans/plan/page/data/query.do', method: 'GET', params });
};

/**
 * 查询详情
 *  - params：参数
 */
const get = (params: any) => {
  return request({ url: '/api/trans/plan/get/out/detail.do', method: 'GET', params });
};

/* 
 * 数据类别 
*/
const sjlx = () => {
  return request({ url: '/api/trans/plan/getDataType.do', method: 'GET' });
};
/* 
 * 数据状态
*/
const sjzt = () => {
  return request({ url: '/api/trans/plan/getDataStatus.do', method: 'GET' });
};
/* 
 * 数据分类
*/
const sjfl = () => {
  return request({ url: '/api/trans/plan/getFileType.do', method: 'GET' });
};
export default {
  list, // 表格数据查询
  get, // 查询详情
  sjlx, // 数据类别
  sjzt, // 数据状态
  sjfl // 数据分类
};
