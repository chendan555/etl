/**
 * Create by Leo
 * 系统共用接口API：API
 * 基于baseApi进行扩展
 */
/**
 * 注：
 * transNew开头的加上/api/前缀
 */

/**
 * 引接baseApi
 */
import axios from './base/http';
/**
 * 引接系统存储api
 */
import store from '@/store';
/**
 * 引接画布api
 */
import FlowChart from '@/FlowChart/index';

// 数据中心相关接口
import DataCenterService from './modules/data-center';
// 数据审核相关接口
import DataReview from './modules/data-review';
// 数据整编相关接口
import DataReduction from './modules/data-reduction';
// 数据查询相关接口
import DataQuery from './modules/data-query';
// 分类管理相关接口
import ClassifyService from './modules/classify-manage';
// 预处理概览相关接口
import MainPage from './modules/data-mainPage';
// 登录登出相关接口
import LoginOut from './modules/loginout';
// 评价规则相关接口
import DataRules from './modules/data-rules';
// 方案管理相关接口
import PlanManage from './modules/plan-manage';
// 引入处理流程管理相关接口
import ProjectManage from './modules/project-manage';
// 引接数据表引接配置接口模块
import ConfigRelation from './modules/config-relation';
// 用户管理的接口
import userManage from './modules/user-manage';
// 解析协议管理的接口
import ResolveProtocol from './modules/resolve-protocol';
// 任务管理的接口
import TaskManagerService from './modules/task-manager';
// 引入数据引接接口模块
import DataReceive from './modules/data-receive';
// 引接主子表关系配置接口模块
import TableRelation from './modules/table-relation';
// 引接表单配置接口模块
import ResolveFormModel from './modules/resolve-form-model';
// 引接首页统计接口模块
import MainPageStat from './modules/main-page';
// 引接工具集接口模块
import Toolset from './modules/toolset';
import SampleCascade from './modules/sample-cascade';

const projectManage = new ProjectManage();
const configRelation = new ConfigRelation();
const resolveProtocol = new ResolveProtocol();
const dataReceive = new DataReceive();
const tableRelation = new TableRelation();
const resolveFormModel = new ResolveFormModel();
const mainPageStat = new MainPageStat();
const toolset = new Toolset();
const sampleCascade = new SampleCascade();

/**
 * 获取步骤名称
 * params：参数
 */
function getStepsName(params: any) {
  params = { ...getFlowChart(), ...params };
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/transNew/previousSteps.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}
/**
 * 获取参数名称
 * params：参数
 */
function getParamsName(params: any) {
  params = { ...getFlowChart(), ...params, path: store.state.path };
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/transNew/prevSteps.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 注：
 * transNew开头的加上/api/前缀
 */
/**
 * 获取从哪个步奏获取文件名
 * params：参数
 */
function getStepsFileds(params: any) {
  params = { ...getFlowChart(), ...params, nodeId: store.state.nodeId };
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/transNew/prevStepsFileds', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 获取当前所有节点数据及构造必须参数
 */
function getFlowChart() {
  let data = FlowChart.getData();
  data.path = sessionStorage.getItem('path');
  // data.transformationId = sessionStorage.getItem('transformationId');
  data.transformationId = store.state.transformationId; //transformationId获取方式由sessionStorage更换为store，避免多窗口同时运行导致的transformationId值丢失
  data.type = 'transformation';
  // 验证每个节点是否绑定nodeId，如果没有则取id值或者data中nodeId值赋值，避免节点名称重复验证接口报错    --Leo 20210519
  if (data.nodes && data.nodes.length) {
    data.nodes.forEach((ele: any) => {
      if (!ele.nodeId) {
        ele.nodeId = ele.id || ele.data.nodeId || '';
      }
    });
  }

  return data;
}

/**
 * 转换为FormData
 * params：参数
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

/**
 * 获取所有控件目录
 * params：参数
 */
function getMenuData(params: any) {
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/transNew/steps.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}
/**
 * 获取左侧tree节点
 * params：参数
 */
function getTree(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .get('/v1/center/database/categories/tree', params)
      .then(resolve)
      .catch(reject);
  });
}
/**
 * 获取数据库类型列表
 * params：参数
 */
function getCategoriesList() {
  return new Promise((resolve, reject) => {
    axios
      .get('/v1/center/database/dbTypes')
      .then(resolve)
      .catch(reject);
  });
}
/**
 * 获取数据源
 * params：参数
 */
function getDatabases() {
  return new Promise((resolve, reject) => {
    axios
      .post('/api/common/getDatabases.do')
      .then(resolve)
      .catch(reject);
  });
}
/**
 * 数据模式表查询
 * params：参数
 */
function getSchema(params: any) {
  let defaultParams = {
    pageSize: 10000,
    pageIndex: 1,
    connection: ''
  };
  if (!params.connection) {
    return new Promise((resolve, reject) => {
      resolve({ data: { data: [] } });
    });
  }
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/database/getSchema.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}
/**
 * 表结构查询
 * params：参数
 */
function getColumnsByTable(params: any) {
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/database/getColumnsByTable.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}
/**
 * 数据表列表接口
 * params：参数
 */
function getTableDatas(params: any) {
  if (!params.connection || !params.schema) {
    return new Promise((resolve, reject) => {
      resolve({ data: { data: [] } });
    });
  }
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/database/yj/getDataTables.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}
/**
 * 编辑数据源
 * params：参数
 */
function saveDatabase(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .post('/v1/center/database/save', params, { headers: { 'Content-Type': 'application/json;charset=UTF-8' } })
      .then(resolve)
      .catch(reject);
  });
}
/**
 * 删除数据源
 * params：参数
 */
function delDatabase(params: any) {
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/v1/center/database/del', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}
/**
 * 获取数据库模式
 * params：参数
 */
function getSchemaData(params: any) {
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/database/getSchema.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 获取数据表
 * params：参数
 */
function getBaseTableData(params: any) {
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/database/getDataTables.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 获取数据表字段
 * params：参数
 */
function apiTableColumns(params: any) {
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/database/getColumnsByTable.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 获取任务组
 * params：参数
 */
function getAllTaskGroupBeforeCreate() {
  return new Promise((resolve, reject) => {
    axios
      .post('/api/task/group/getAllTaskGroupBeforeCreate.do')
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 创建转换
 * params：参数
 */
function createTrans(params: any) {
  // let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/repository/new/createTrans.do', params)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 修改转换
 * params：参数
 */
function modifyTrans(params: any) {
  // let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/repository/new/updateTrans.do', params)
      .then(resolve)
      .catch(reject);
  });
}
/**
 * 新增数据引接流程接口(需要调整)
 * params：参数
 */
function addReceiveProc(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .post('/api/repository/new/createYJTrans.do', params)
      .then(resolve)
      .catch(reject);
  });
}
/**
 * 编辑数据引接流程接口(需要调整)
 * params：参数
 */
function updateReceiveProc(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .post('/api/repository/new/updateYJTrans.do', params)
      .then(resolve)
      .catch(reject);
  });
}
/**
 * 引接流程列表接口
 * params：参数
 */
function getReceiceProcList(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .get('/api/task/find/yjTrans.do', { params })
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 创建转换Json格式
 * params：参数
 */
function createTransByJson(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .post('/api/repository/createTrans.do', params)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 删除数据引接流程接口(现有)
 * params：参数
 */
function deleteTask(params: any) {
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/task/delete.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 获取调度
 * params：参数
 */
function getAllTransScheduler(params: any) {
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/transScheduler/getAllTransScheduler.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 保存调度
 * params：参数
 */
function fiexdExecuteTrans(params: any) {
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/transTask/fiexdExecuteTrans.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 删除调度
 * params：参数
 */
function deleteScheduler(params: any) {
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/transScheduler/deleteScheduler.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 修改调度状态
 * params：参数
 */
function stopTransScheduler(params: any) {
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/transScheduler/stopTransScheduler.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 修改调度
 * params：参数
 */
function updateTransScheduler(params: any) {
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/transScheduler/updateTransScheduler.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 获取数据表
 * params：参数
 */
function getExplorer(params: any) {
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/database/explorer.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 文件数据列表
 * params：参数
 */
function getFilesName(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .get(`/api/transNew/files.do`, { params })
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 文件数据预览
 * params：参数
 */
function filePreview(params: any) {
  let data = getFlowChart();
  let dataParams: any = {};
  for (let pr in data) {
    if (typeof data[pr] === 'string') {
      dataParams[pr] = data[pr];
    } else {
      dataParams[pr] = JSON.stringify(data[pr]);
    }
  }
  let param = {
    ...params,
    ...dataParams
  };
  return new Promise((resolve, reject) => {
    axios
      .post(`/api/transNew/previewFile.do`, param)
      .then(resolve)
      .catch(reject);
  });
}
/**
 * 通过id获取浏览器
 * params：参数
 */
function getExplorerById(params: any) {
  let newConfig = transferToFormData(params);
  let formData = newConfig.formData;
  let tableOut: any = document.getElementById('tableOut');
  let nodeId: any;
  if (tableOut) {
    nodeId = tableOut.value;
  }

  let text: any = sessionStorage.getItem('text');
  formData.delete('nodeId');

  formData.append('nodeId', nodeId);
  formData.append('text', text);
  formData.append('databaseInfo', text);

  return new Promise((resolve, reject) => {
    axios
      .post('/api/database/explorer.do', formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 获取字段
 * params：参数
 */
function getTableFields(params: any) {
  params = { ...getFlowChart(), ...params };
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/transNew/tableFields.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 获取预览数据
 * params：参数
 */
function getPreviewData(params: any) {
  params = { ...getFlowChart(), ...params };
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/transNew/previewData.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 获取表输出预览数据
 * params：参数
 */
function getPreviewDataOut(params: any) {
  params = { ...getFlowChart(), ...params };
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/transNew/previewDataOut.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}
/**
 * 任意节点数据查看
 * params：参数
 */
function previewDataView(params: any) {
  params = { ...params };
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/transNew/getDataView.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 获取excel数据
 * params：参数
 */
function getExcelDataOut(params: any) {
  params = { ...getFlowChart(), ...params };
  // if (!params.filename) {
  //   return;
  // }
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/excelInputController/excelPreview.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 保存节点
 * params：参数
 */
function SaveJSON(params: any) {
  params = { ...getFlowChart(), ...params };
  let newConfig = transferToFormData(params);
  newConfig.config = <any>{
    ...newConfig.config,
    loadText: '保存中...'
  };
  return new Promise((resolve, reject) => {
    axios
      .post('/api/transNew/saveJSON.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}
/**
 * 获取字段
 * params：参数
 */
function SaveJSONAuto(params: any) {
  params = { ...getFlowChart(), ...params };
  params.nodes.forEach((item: any, itemIndex: any) => {
    let dom = JSON.parse(item.data.dom);
    let ruleForm = item.properties.ruleForm;
    let domString = JSON.stringify(dom);
    for (let props in ruleForm) {
      let curString = domString.split(props);
      let index = curString[0].split('valueKey').length - 2;
      if (dom[index]) {
        dom[index].config.value = ruleForm[props];
      }
    }
    item.data.dom = JSON.stringify(dom);
  });
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/plugin/manage/saveCustomPlugin', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 流程回显
 * params：参数
 */
function getFlowChartData(params: any) {
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/transNew/openNew.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}
/**
 * 更新流程分组
 * params：参数
 */
function updateProjFolder(params: any) {
  // let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/task/group/save.do', params)
      .then(resolve)
      .catch(reject);
  });
}
/**
 * 方案回显
 * params：参数
 */
function getPlanData(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .get(`/api/trans/plan/${params.id}/detail.do`)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 流程执行
 * params：参数
 * execParams：执行参数（变量替换用，如 {batchId: 1}）
 */
function execModel(params: any, execParams?: any) {
  params = { ...getFlowChart(), ...params };
  if (execParams) {
    params.params = JSON.stringify(execParams);
  }
  let newConfig = transferToFormData(params);
  newConfig.config = <any>{
    ...newConfig.config,
    loadText: '执行中...'
  };
  return new Promise((resolve, reject) => {
    axios
      .post('/api/transNew/run.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 批量执行 - 一次传入多组参数，后端循环执行
 * transformationId：流程ID
 * params：参数列表 [{key: value, ...}, ...]
 */
function execBatchRun(transformationId: number, params: any[]) {
  let formData = new FormData();
  formData.append('transformationId', String(transformationId));
  formData.append('params', JSON.stringify(params));
  let config = {
    headers: { 'Content-Type': 'multipart/form-data' },
    loadText: '批量执行中...'
  };
  return new Promise((resolve, reject) => {
    axios
      .post('/api/transNew/batchRun', formData, config)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 取消批量执行任务
 * @param taskId 任务ID
 */
function cancelBatch(taskId: string) {
  let formData = new FormData();
  formData.append('taskId', taskId);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/transNew/cancelBatch', formData)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 流程执行
 * params：参数
 */
function execRunResult(params: any) {
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/transNew/result.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 获取字段
 * params：参数
 */
function getInputOutputFields(params: any) {
  params = { ...getFlowChart(), ...params };
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/transNew/iputOutputFields.do', newConfig.formData, newConfig.config)
      .then(res => {
        if (res.data && res.data.length) {
          res.data.forEach((item: any) => {
            item.tableChartsName = item.name;
          });
        }
        resolve(res);
      })
      .catch(reject);
  });
}

/**
 * 获取字段名称
 * params：参数
 */
function getIputOutputFieldsName(params: any) {
  params = { ...getFlowChart(), ...params };
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/transNew/iputOutputFieldsName.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 获取字段和行信息
 * params：参数
 */
function getFieldAndRow(params: any) {
  params = { ...getFlowChart(), ...params };
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/transNew/getFieldAndRow.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 获取NULL替换字段
 * params：参数
 */
function getReplaceValue(params: any) {
  params = { ...getFlowChart(), ...params };
  // 字段为空或补全方式为空值补全时，直接返回空串，不调用接口
  if (params.fulfillType === 0 || !params.columnStr) {
    return new Promise(resolve => {
      resolve('');
    });
  }
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/transNew/contendValue.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 获取js代码字段
 * params：参数
 */
function getJavaScriptCodeFields(params: any) {
  params = { ...getFlowChart(), ...params };
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/script/variables.do', newConfig.formData, newConfig.config)
      .then(res => {
        if (res.data && res.data.length) {
          res.data.forEach((item: any) => {
            item.tableChartsName = item.name;
          });
        }
        resolve(res);
      })
      .catch(reject);
  });
}

/**
 * 获取字段类型
 * params：参数
 */
function getColumnType() {
  return new Promise((resolve, reject) => {
    axios
      .post('/api/transNew/fieldType.do')
      .then(resolve)
      .catch(reject);
  });
}

/**
 * Excel下载
 * params：参数
 */
function downloadExcel(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .get('/api/transNew/downloadExcel.do', { params })
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 获取格式
 * params：参数
 */
function getColumnFormats() {
  return new Promise((resolve, reject) => {
    axios
      .post('/api/ExcelOutput/columnFormats.do')
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 获取聚合类型
 * params：参数
 */
function getAggre() {
  return new Promise((resolve, reject) => {
    axios
      .post('/api/denormaliser/aggre.do')
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 获取数据类型带id
 * params：参数
 */
function getValueMeta() {
  return new Promise((resolve, reject) => {
    axios
      .post('/api/system/valueMeta.do')
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 获取数据类型带id
 * params：参数
 */
function getfuncType() {
  return new Promise((resolve, reject) => {
    axios
      .post('/api/system/func.do')
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 获取表连接方法类型
 * params：参数
 */
function getMergeJoinType() {
  return new Promise((resolve, reject) => {
    axios
      .post('/api/mergejoin/types.do')
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 获取计算详情
 * params：参数
 */
function getCalcDesc() {
  return new Promise((resolve, reject) => {
    axios
      .post('/api/transNew/calcDesc.do')
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 获取项目列表
 * params：参数
 */
function getTrans(params: any) {
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/task/trans/get.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 获取任务组列表
 * params：参数
 */
function getTaskGroupList(params: any) {
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/task/trans/get.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 获取目标分支数据
 * params：参数
 */
function getNextSteps(params: any) {
  params = { ...getFlowChart(), ...params };
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/transNew/nextSteps.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 获取目标来源步骤
 * params：参数
 */
function getPreviousSteps(params: any) {
  params = { ...getFlowChart(), ...params };
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/transNew/previousSteps.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 拖入控件获取名称
 * params：参数
 */
function addStepName(params: any) {
  params = { ...getFlowChart(), ...params };
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/transNew/addStepName.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 修改控件获取名称
 * params：参数
 */
function updateStepName(params: any) {
  params = { ...getFlowChart(), ...params };
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/transNew/updateStepName.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 获取execl工作表
 * params：参数
 */
function getExcelSheet(params: any) {
  params = { ...getFlowChart(), ...params };
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/excelInputController/getExcelSheet.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 获取execl工作表字段
 * params：参数
 */
function getExcelFields(params: any) {
  params = { ...getFlowChart(), ...params };
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/excelInputController/getFields.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 文件上传
 * params：参数
 */
function getFileUpload(uploadFile: any, params: any) {
  return new Promise((resolve, reject) => {
    axios
      .post('/api/excelInputController/upload.do', uploadFile, { headers: { 'Content-Type': 'multipart/form-data' } })
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 数据库列表
 * params：参数
 */
function getDatabaseManageList(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .get('/v1/center/database/page', { params })
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 新增数据库
 * params：参数
 */
function getDatabaseCreate(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .post('/v1/center/database/create', params)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 更新数据库
 * params：参数
 */
function getDatabaseUpdate(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .post('/v1/center/database/update', params)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 删除数据库
 * params：参数
 */
function getDatabaseDelete(params: any) {
  let newConfig = transferToFormData(params);
  //
  // return new Promise((resolve, reject) => {
  //   axios.post('/repository/createTrans.do', newConfig.formData, newConfig.config).then(resolve).catch(reject);
  // });
  //
  return new Promise((resolve, reject) => {
    axios
      .post('/v1/center/database/del', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 数据库源类型管理列表
 * params：参数
 */
function getDatabaseTypeList(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .get('/v1/center/databasetype/list', { params })
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 数据库连接类型管理列表
 * params：参数
 */
function getDatabaseConnTypeList(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .get('/v1/center/databasecontype/list', { params })
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 获取本地日期格式
 * params：参数
 */
function getLocalDateTimeFormat() {
  return new Promise((resolve, reject) => {
    axios
      .post('/api/system/datetimeformat.do')
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 获取编码方式
 * params：参数
 */
function getAvailableCharsets() {
  return new Promise((resolve, reject) => {
    axios
      .post('/api/system/availableCharsets.do')
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 获取压缩格式
 * params：参数
 */
function getCompressionProviderNames() {
  return new Promise((resolve, reject) => {
    axios
      .post('/api/system/compressionProviderNames.do')
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 创建表
 * params：参数
 */
function getCreateTable(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .post('/api/trans/plugin/create/table.do', params)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 验证表名
 * params：参数
 */
function getCheckTableName(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .get('/api/trans/plugin/check/table.do', { params })
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 方案生成
 * params：参数
 */
function getCreatePlan(params: any) {
  params = { ...getFlowChart(), ...params };
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/trans/plan/create.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 方案列表
 * params：参数
 */
function getCreatePlanList(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .get('/api/trans/plan/page.do', { params })
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 方案可见控制
 * params：参数
 */
function getCreatePlanEdit(params: any) {
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/trans/plan/edit.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 方案删除
 * params：参数
 */
function getCreatePlanRemove(params: any) {
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/trans/plan/deletePlanAndDetail.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 方案执行结果列表
 * params：参数
 */
function getPlanResultList(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .get('/api/trans/plan/findTransList.do', { params })
      .then(resolve)
      .catch(reject);
  });
}
/**
 * 方案执行结果预览
 * params：参数
 */
function getPlanResultData(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .get('/api/trans/plan/yulan.do', { params })
      .then(resolve)
      .catch(reject);
  });
}

/**
 * JAVA脚本组件获取关联代码树
 * params：参数
 */
function getConnectCodeTree() {
  return new Promise((resolve, reject) => {
    axios
      .get('/api/userDefinedJavacClass/tree.do')
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 获取组件列表
 * params：参数
 */
function getComponentsList(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .get('/api/plugin/manage/list', { params })
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 获取组件列表树
 * params：参数
 */
function getComponentsTree() {
  return new Promise((resolve, reject) => {
    axios
      .get('/api/plugin/manage/categories/tree')
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 获取任务列表树
 * params：参数
 */
function getTaskListData() {
  return new Promise((resolve, reject) => {
    axios
      .get('/api/getTaskListData')
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 获取项目列表树
 * params：参数
 */
function getProjListData(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .get('/api/task/group/all/list.do', { params })
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 获取组件列表
 * params：参数
 */
function getProjTransList(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .get('/api/task/find/trans.do', { params })
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 获取全部流程列表
 * params：参数
 */
function getTotalProcList(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .get('/api/task/find/trans/no/group.do', { params })
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 获取项目信息
 * params：参数
 */
function getProjDetail(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .get('/api/task/group/detail.do', { params })
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 获取项数据源列表信息
 * params：参数
 */
function getProjDataSource(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .get('/api/task/group/datasource/info.do', { params })
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 新建项目根目录
 * params：参数
 */
function createProjFolder(params: any) {
  // let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/task/group/create.do', params)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 修改项目根目录
 * params：参数
 */
function modifyProjFolder(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .post('/api/task/group/modify.do', params)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 删除项目根目录
 * params：参数
 */
function removeProjFolder(data: any) {
  return new Promise((resolve, reject) => {
    axios
      .post('/api/task/group/dels.do', data)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 获取报表列表
 * params：参数
 */
function getReportLists(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .get('/api/report/template/list.do', { params })
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 新增报表模板
 * params：参数
 */
function addReportTemplate(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .post('/api/report/template/add.do', params)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 删除报表模板
 * params：参数
 */
function delReportTemplate(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .post('/api/report/template/delete.do', params)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 启动方案
 * params：参数
 */
function runPlan(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .get('/api/trans/plan/start.do', { params })
      .then(resolve)
      .catch(reject);
  });
}
/**
 * 停止方案
 * params：参数
 */
function stopPlan(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .get('/api/trans/plan/stop.do', { params })
      .then(resolve)
      .catch(reject);
  });
}

/**
  * 获取质量评价的评价模板列表 
  * params: {
    pageIndex, pageSize, dimension, assessName
  }
  */
function getAssessTemplateList(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .get('/api/quality/assess/template/page.do', { params })
      .then(resolve)
      .catch(reject);
  });
}
/* 
  获取质量评价的评价模板全部列表 
  params: {
    pageIndex, pageSize, dimension, assessName
  }
*/
function getAllAssessTemplateList(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .get('/api/quality/assess/template/list.do', { params })
      .then(resolve)
      .catch(reject);
  });
}
/* 
  质量评价的评价模板新建保存
  params: {
    dimensionList, assessName
  }
*/
function saveNewTemplate(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .post('/api/quality/assess/template/save.do', params)
      .then(resolve)
      .catch(reject);
  });
}
/* 
  质量评价的评价模板删除
  params: {
    id
  }
*/
function removeTemplate(params: any) {
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/quality/assess/template/delete.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}
/* 
  质量评价获取全部方案列表
  params: {
    id
  }
*/
function getAllPlanList(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .get('/api/trans/plan/run/list.do', { params })
      .then(resolve)
      .catch(reject);
  });
}
/* 
  质量评价新增评价
  params: {
    id
  }
*/
function saveNewAssess(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .post('/api/quality/assess/save.do', params)
      .then(resolve)
      .catch(reject);
  });
}
/* 
  质量评价获取评价列表
  params: {
    id
  }
*/
function getAssessList(params: any) {
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/transNew/assess/page.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}
/* 
  质量评价获取评价列表
  params: {
    id
  }
*/
function getAssessInfor(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .get('/api/quality/assess/info.do', { params })
      .then(resolve)
      .catch(reject);
  });
}
/* 
  质量评价删除评价
  params: {
    id
  }
*/
function deleteAssessItem(params: any) {
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/transNew/assess/delete.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}
/* 
  质量评价获取规则列表
  params: {
    id
  }
*/
function getAssessRuleList(params: any) {
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/transNew/rule/page.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}
/* 
  质量评价执行
  params: {
    id
  }
*/
function runAssess(params: any) {
  let newConfig = transferToFormData(params);
  (newConfig.config as any).loadText = '执行中...';
  return new Promise((resolve, reject) => {
    axios
      .post('/api/transNew/assess/run.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}
/* 
质量评价补充采集引接
params: {
  id
}
*/
function getReplenishList(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .get('/api/transNew/errorForm/page.do', { params })
      .then(resolve)
      .catch(reject);
  });
}
/* 
质量评价补充采集表单
params: {
  id
}
*/
function getReplenishForm(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .get('/tybd/formOutData', { params })
      .then(resolve)
      .catch(reject);
  });
}
/* 
方案管理左侧树形菜单
*/
function getPlanNavTree(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .get('/api/trans/plan/categroy/tree.do', { params })
      .then(resolve)
      .catch(reject);
  });
}
/* 
方案管理新增方案
*/
function newPlan(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .post('/api/trans/plan/savePlanAndDetail.do', params)
      .then(resolve)
      .catch(reject);
  });
}
/**
 * 首页的第一个图的数据查询接口
 * params：参数
 */
function count(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .post('/api/transNew/get/count.do', params)
      .then(resolve)
      .catch(reject);
  });
}
/**
 * 表数据查询(需要实现)
 * params：参数
 */
function getTableDataed(params: any) {
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/database/yj/getTableDatas.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}
/**
 * 使用方案id获取方案详情
 * params：参数
 */
function getTransByPlanId(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .post('/api/trans/plan/getTransPlanById.do', params)
      .then(resolve)
      .catch(reject);
  });
}
/**
 * 获取项目流程数据列表
 * params：参数
 */
function getProcDataList() {
  return new Promise((resolve, reject) => {
    axios
      .get('/api/getProcDataList')
      .then(resolve)
      .catch(reject);
  });
}
/**
 * 获取项目流程数据列表
 * params：参数
 */
function navTree() {
  return new Promise((resolve, reject) => {
    axios
      .get('/v1/auth/org/page')
      .then(resolve)
      .catch(reject);
  });
}
/**
 * 获取处理结果，引接结果列表
 * params：参数
 */
function DealResult(params: any) {
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/transNew/log/page.do', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}
/**
 * 删除处理记录
 * params：参数
 */
function removeDealRecord(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .get('/api/transNew/log/del.do', { params })
      .then(resolve)
      .catch(reject);
  });
}
/**
 * 获取处理结果，引接结果列表下载
 * params：参数
 */
function download(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .get('/api/file/download.do', { params })
      .then(resolve)
      .catch(reject);
  });
}
/**
 * 获取所有解析列表
 * params：参数
 */
function getAllProtocolList() {
  let params = {
    pageSize: 100000,
    pageIndex: 1,
    searchData: { configName: '' }
  };
  return new Promise((resolve, reject) => {
    axios
      .post('/api/yj/protocol/page.do', params)
      .then(resolve)
      .catch(reject);
  });
}
/**
 * 获取服务器列表
 * params：参数
 */
function getArtificial(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .get('/api/data/source/artificial/list.do', { params })
      .then(resolve)
      .catch(reject);
  });
}
/**
 * 通过任务节点id获取仿真数据
 * params：参数
 */
function getFzDataByNodeId(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .post('/api/es/find.do', params)
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 获取任务列表
 */
function getTaskLists() {
  return new Promise((resolve, reject) => {
    axios
      .get('/api/task/plan/node/tree.do')
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 获取数据来源分类列表
 */
// function getServerSourceList() {
//   return new Promise((resolve, reject) => {
//     axios
//       .get('/api/data/source/serverSource/list.do')
//       .then(resolve)
//       .catch(reject);
//   });
// }

/* 
  获取数据库列表 
  businessType 使用类型
  sourceId 来源类型
*/
function getDatabasesByType(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .get('/api/common/getDatabasesByType.do', { params })
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
function saveFileServer(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .post('/api/data/source/file/save/or/edit.do', params)
      .then(resolve)
      .catch(reject);
  });
}
/**
 * 引接MySQL数据库数据
 * params：参数
 */
function receiveMysql(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .post('/api/yj/exec/exec/transfer/script.do', params)
      .then(resolve)
      .catch(reject);
  });
}
/**
 * 引接Oracle数据库数据
 * params：参数
 */
function receiveOracle(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .post('/api/yj/exec/exec/transfer/script.do', params)
      .then(resolve)
      .catch(reject);
  });
}
/*
 * 获取现有批次列表
 */
function getBatchList() {
  return new Promise((resolve, reject) => {
    axios
      .get('/api/data/source/serverSource/list.do')
      .then(resolve)
      .catch(reject);
  });
}
/**
 * 获取批次号列表
 * params：参数
 */
function getBatchNumList() {
  return new Promise((resolve, reject) => {
    axios
      .get('/api/yj/batch/num/list.do')
      .then(resolve)
      .catch(reject);
  });
}
/**
 * 下载文件到本地服务器
 * params：参数
 */
function downloadFileToServer(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .post('/api/task/plan/download/file.do', params, { headers: { 'Content-Type': 'application/json;charset=UTF-8' } })
      .then(resolve)
      .catch(reject);
  });
}
/**
 * 获取xh筛选项历史查询列表
 * params：参数
 */
function getTypeInputHis(params?: any) {
  params = {
    count: 10,
    type: 'DDXH',
    ...params
  };
  delete params.nodeId; // 删除默认参数nodeId   --Leo 20221014
  return new Promise((resolve, reject) => {
    axios
      .get('/api/yj/queryRecord/get/limit.do', { params })
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 获取绑定表列表（ETL数据库中的所有表名）
 */
function getBindTables() {
  return new Promise((resolve, reject) => {
    axios
      .get('/api/transNew/bindTables')
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 获取绑定表的字段列表
 * params：{ tableName: string }
 */
function getBindTableColumns(params: any) {
  return new Promise((resolve, reject) => {
    axios
      .get('/api/transNew/bindTableColumns', { params })
      .then(resolve)
      .catch(reject);
  });
}

/**
 * 更新流程绑定的表名
 * params：{ transformationId: number, bindTableNames: string }
 */
function updateBindTables(params: any) {
  let newConfig = transferToFormData(params);
  return new Promise((resolve, reject) => {
    axios
      .post('/api/transNew/updateBindTables', newConfig.formData, newConfig.config)
      .then(resolve)
      .catch(reject);
  });
}

export default {
  getTypeInputHis,
  downloadFileToServer,
  getBatchNumList,
  getBatchList,
  receiveMysql,
  receiveOracle,
  saveFileServer,
  getCategoriesList,
  getDatabasesByType,
  // getServerSourceList,
  updateProjFolder,
  getTaskLists,
  getArtificial,
  getFzDataByNodeId,
  getAllProtocolList,
  download,
  DealResult,
  removeDealRecord,
  navTree,
  getProcDataList,
  getTransByPlanId,
  getTableDataed,
  count,
  newPlan,
  getPlanNavTree,
  getAssessInfor,
  getReplenishList,
  getReplenishForm,
  deleteAssessItem,
  runAssess,
  getAssessRuleList,
  getAssessList,
  saveNewAssess,
  getAllPlanList,
  getAssessTemplateList,
  getAllAssessTemplateList,
  saveNewTemplate,
  removeTemplate,
  runPlan,
  stopPlan,
  modifyTrans,
  addReceiveProc,
  updateReceiveProc,
  getReceiceProcList,
  getProjDataSource,
  modifyProjFolder,
  removeProjFolder,
  createProjFolder,
  getProjDetail,
  getProjTransList,
  getTotalProcList,
  getProjListData,
  getTaskListData,
  getConnectCodeTree,
  getComponentsTree,
  getComponentsList,
  getFlowChartData,
  getPlanData,
  getMenuData,
  getDatabases,
  getSchema,
  getSchemaData,
  getBaseTableData,
  apiTableColumns,
  getAllTaskGroupBeforeCreate,
  createTrans,
  createTransByJson,
  deleteTask,
  getAllTransScheduler,
  fiexdExecuteTrans,
  deleteScheduler,
  stopTransScheduler,
  updateTransScheduler,
  getExplorer,
  getParamsName,
  getExplorerById,
  getFilesName,
  filePreview,
  getTableFields,
  getPreviewData,
  getPreviewDataOut,
  previewDataView,
  getExcelDataOut,
  SaveJSON,
  SaveJSONAuto,
  execModel,
  execBatchRun,
  cancelBatch,
  execRunResult,
  getInputOutputFields,
  getIputOutputFieldsName,
  getFieldAndRow,
  getReplaceValue,
  getJavaScriptCodeFields,
  getColumnType,
  downloadExcel,
  getColumnFormats,
  getAggre,
  getValueMeta,
  getNextSteps,
  getfuncType,
  addStepName,
  updateStepName,
  getPreviousSteps,
  getMergeJoinType,
  getCalcDesc,
  getTrans,
  getTaskGroupList,
  getExcelSheet,
  getExcelFields,
  getFileUpload,
  getDatabaseManageList,
  getDatabaseCreate,
  getDatabaseUpdate,
  getDatabaseDelete,
  getDatabaseTypeList,
  getDatabaseConnTypeList,
  getLocalDateTimeFormat,
  getAvailableCharsets,
  getCompressionProviderNames,
  getCreateTable,
  getCheckTableName,
  getCreatePlan,
  getCreatePlanList,
  getPlanResultList,
  getPlanResultData,
  getCreatePlanEdit,
  getCreatePlanRemove,
  getReportLists,
  addReportTemplate,
  delReportTemplate,
  getStepsName,
  getStepsFileds,
  getColumnsByTable,
  saveDatabase,
  delDatabase,
  getTableDatas,
  getTree,
  DataReview, // 数据审核接口
  DataReduction, // 数据整编接口
  DataQuery, // 数据查询
  MainPage, //预处理概览
  userManage, //用户管理
  projectManage,
  configRelation,
  resolveProtocol,
  dataReceive,
  tableRelation,
  resolveFormModel,
  mainPageStat,
  toolset,
  getBindTables,
  getBindTableColumns,
  updateBindTables,
  sampleCascade
};

export var dataCenterService = new DataCenterService();
export var classifyService = new ClassifyService();
export const loginOut = new LoginOut();
export var dataRules = new DataRules();
export var planManage = new PlanManage();
export const taskManagerService = new TaskManagerService();
