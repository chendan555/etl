/**
 * Create By WangSheng on 2021/02/22
 * 数据整编api
 * 基于http进行扩展
 */
import request from '../base/http';

/**
 * 获取菜单树
 */
const navTree = () => {
  return request({ url: '/api/trans/plan/categroy/cache/tree.do', method: 'GET' });
};

/**
 * 获取列表
 * params：参数
 */
const list = (params: any) => {
  return request({ url: '/api/trans/plan/pageReorganizationByCondition', method: 'POST', params });
};

/**
 * 更新数据
 * params：参数
 */
const updata = (params: any) => {
  return request({ url: '/api/trans/plan/update/reorganization.do', method: 'POST', params });
};

export default {
  navTree, // 获取菜单数据
  list, // 获取列表
  updata // 更新数据
};
