import baseApi from '../base/baseApi';

export default class SampleCascade extends baseApi {
  // ========== 主题配置 CRUD ==========
  getTopics() {
    return new Promise((resolve, reject) => {
      this.$http.get('/api/scheme/topics').then(resolve).catch(reject);
    });
  }

  getTopicPage(params: any) {
    return new Promise((resolve, reject) => {
      this.$http.get('/api/scheme/topic/page', { params }).then(resolve).catch(reject);
    });
  }

  saveTopic(params: any) {
    return new Promise((resolve, reject) => {
      this.$http.post('/api/scheme/topic/save', params).then(resolve).catch(reject);
    });
  }

  deleteTopic(id: number) {
    return new Promise((resolve, reject) => {
      this.$http.post('/api/scheme/topic/delete', null, { params: { id } }).then(resolve).catch(reject);
    });
  }

  getTopicDetail(id: number) {
    return new Promise((resolve, reject) => {
      this.$http.get('/api/scheme/topic/detail', { params: { id } }).then(resolve).catch(reject);
    });
  }

  // ========== 数据采集配置 CRUD ==========
  getCjConfigs() {
    return new Promise((resolve, reject) => {
      this.$http.get('/api/scheme/cjConfigs').then(resolve).catch(reject);
    });
  }

  getCjPage(params: any) {
    return new Promise((resolve, reject) => {
      this.$http.get('/api/scheme/cj/page', { params }).then(resolve).catch(reject);
    });
  }

  saveCj(params: any) {
    return new Promise((resolve, reject) => {
      this.$http.post('/api/scheme/cj/save', params).then(resolve).catch(reject);
    });
  }

  deleteCj(id: number) {
    return new Promise((resolve, reject) => {
      this.$http.post('/api/scheme/cj/delete', null, { params: { id } }).then(resolve).catch(reject);
    });
  }

  // ========== 仿真方案 CRUD ==========
  getSchemePage(params: any) {
    return new Promise((resolve, reject) => {
      this.$http.get('/api/scheme/page', { params }).then(resolve).catch(reject);
    });
  }

  getSchemeById(id: number) {
    return new Promise((resolve, reject) => {
      this.$http.get('/api/scheme/detail', { params: { id } }).then(resolve).catch(reject);
    });
  }

  saveScheme(params: any) {
    return new Promise((resolve, reject) => {
      this.$http.post('/api/scheme/save', params).then(resolve).catch(reject);
    });
  }

  deleteScheme(id: number) {
    return new Promise((resolve, reject) => {
      this.$http.post('/api/scheme/delete', null, { params: { id } }).then(resolve).catch(reject);
    });
  }

  // ========== 样本三级联动 ==========
  getSamplePlans() {
    return new Promise((resolve, reject) => {
      this.$http.get('/api/scheme/sample/plans').then(resolve).catch(reject);
    });
  }

  getSampleDataByPlanId(planId: number) {
    return new Promise((resolve, reject) => {
      this.$http.get('/api/scheme/sample/data', { params: { planId } }).then(resolve).catch(reject);
    });
  }

  getSampleSubDataByDataId(dataId: number) {
    return new Promise((resolve, reject) => {
      this.$http.get('/api/scheme/sample/subData', { params: { dataId } }).then(resolve).catch(reject);
    });
  }

  getSampleDetailBySubDataId(subDataId: number) {
    return new Promise((resolve, reject) => {
      this.$http.get('/api/scheme/sample/detail', { params: { subDataId } }).then(resolve).catch(reject);
    });
  }

  // ========== 方案详细数据 ==========
  getSchemeDetailData(schemeId: number, ybList?: string) {
    return new Promise((resolve, reject) => {
      this.$http.get('/api/scheme/detailData', { params: { schemeId, ybList } }).then(resolve).catch(reject);
    });
  }

  getSchemeDetails(schemeId: number) {
    return new Promise((resolve, reject) => {
      this.$http.get('/api/scheme/schemeDetails', { params: { schemeId } }).then(resolve).catch(reject);
    });
  }

  getEquipTree(schemeId: string) {
    return new Promise((resolve, reject) => {
      this.$http.get('/api/scheme/equipTree', { params: { schemeId } }).then(resolve).catch(reject);
    });
  }

  // ========== 引接处理 ==========

  getTransformations() {
    return new Promise((resolve, reject) => {
      this.$http.get('/api/scheme/transformations').then(resolve).catch(reject);
    });
  }
  processScheme(schemeId: number) {
    return new Promise((resolve, reject) => {
      this.$http.post('/api/scheme/process', null, { params: { schemeId } }).then(resolve).catch(reject);
    });
  }

  getProcessProgress(schemeId: number) {
    return new Promise((resolve, reject) => {
      this.$http.get('/api/scheme/progress', { params: { schemeId } }).then(resolve).catch(reject);
    });
  }
}
