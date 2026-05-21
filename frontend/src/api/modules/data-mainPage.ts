/**
 * 预处理概览的api
 * 基于基础请求类http
 */
import request from '../base/http';

/* 
  数据处理概况的数据待处理数据
    - params：参数
*/
const allNums = (params: any) => {
  return request({ url: '/api/transNew/log/allNums', method: 'GET', params });
};
/* 
  获取处理执行结果
    - params：参数
*/
const apiGetRunResult = (params: any) => {
  return request({ url: '/api/transNew/log/runResult', method: 'GET', params });
};
/* 
  获取处理执行结果
    - params：参数
*/
const apiGetRunResult_New = (params: any) => {
  return request({ url: '/api/transNew/get/transformationByTask', method: 'GET', params });
};
export default {
  allNums, // 表格数据查询
  apiGetRunResult,
  apiGetRunResult_New
};
