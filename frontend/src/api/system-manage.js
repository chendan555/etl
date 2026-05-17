import { ApiService, BaseApiService } from '../utils/apiService';

//数据库源配置
export const ipConfigApi = new ApiService('/api/sys/database');
export const ipConfigDefaultApi = new BaseApiService('/api/sys/database/setDataSource');

// 数据字典
export const dataDictApi = new ApiService('/api/sys/dict');
//表单管理
export const formManageApi = new ApiService('/api/from');
export const formSaveConfigApi = new BaseApiService('/api/from/saveConfig');
//表单管理记录
export const formManageRecordApi = new ApiService('/api/dmcd');
//系统用户
export const formUserApi = new ApiService('/api/sys/user');
//组织机构
export const departmentApi = new ApiService('/api/sys/ywjczzjgb');

export const depFormConfigApi = new ApiService('/api/sys/pgxmzgbdconfig');

export const dataInfoListApi = new ApiService('/api/from/dataInfoList');
//问题提交
export const saveQuestionApi = new ApiService('/api/sys/question');
//小程序模板管理
export const smallProgramApi = new ApiService('/api/from/formsmallversion');
//系统日志
export const systemLogApi = new ApiService('/api/sys/log');
//获取所属模块
export const getModuleApi = new ApiService('/api/dynamic/module');
//科目模块
export const getSubjectApi = new ApiService('/api/subject');
// 图片库
export const getPictureApi = new ApiService('/api/icon');
// 图片库新增
export const savePictureApi = new ApiService('/api/sys/attachment');

export const getBaseApi = new ApiService('/api');
