/**
 * Create by Leo
 * 系统表单能模块API：ResolveFormModel
 * 基于baseApi进行扩展
 */

import baseApi from '../base/baseApi';
export default class ResolveFormModel extends baseApi {
  /* 
    获取表单解析模板列表 
    params: {
      pageIndex: 当前页,
      pageSize: 每页大小,
      searchData: 检索参数
    }
  */
  getResolveFormModelList(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/yj/form/page', params)
        .then(resolve)
        .catch(reject);
    });
  }
  /* 
    保存表单解析模板 
    params: {
        name: 解析模板名称,
        remarks: 备注,
        formName: 解析表单类型,
        tableName: 入库数据表名称,
        allConfig: 表单DOM结构,
        formModel: 解析模板规则
    }
  */
  saveResolveFormModel(params: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .post('/api/yj/form/save', params)
        .then(resolve)
        .catch(reject);
    });
  }
  /* 
    删除表单解析模板 
    data: {
        ids: 解析模板ID
    }
  */
  deleteResolveFormModel(data: any) {
    return new Promise((resolve, reject) => {
      this.$http
        .delete('/api/yj/form/delete', { data })
        .then(resolve)
        .catch(reject);
    });
  }
}
