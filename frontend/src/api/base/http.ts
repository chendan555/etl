/* 
  axios接口请求类二次封装，封装内容包括：
  1.设置超时时间；
  2.使用统一的消息提示框和加载动画
  3.设置请求拦截器和响应拦截器
  请求拦截器是在请求头部统一设置token值做服务器校验
  响应拦截器为返回的错误信息设置统一的处理方式
  4.设置拦截函数
  5.定义异常返回数据的处理函数errorTips
*/

import axios from 'axios';
import { Message, Loading } from 'element-ui';
import router from '../../router';
import store from '@/store';
// 创建axios实例
const service = axios.create({
  baseURL: '',
  timeout: 30000, // 请求超时时间
  withCredentials: true
});

const cancelToken = axios.CancelToken; //请求拦截函数

/* 
  设置并配置数据加载动画关闭方法，关闭时间同接口请求超时时间
*/
let loading = <any>{};
setTimeout(() => {
  loading.close && loading.close();
}, 30000);

/* 
  异常返回数据的处理函数
   - data：异常数据
*/
const errorTips = (data: any) => {
  const { msg = null, message, result, content } = data;
  const tipMsg = msg || message || result || content || '请求失败';
  // 每次调用前清除所有提示实例
  (Message as any).closeAll();
  Message.error(tipMsg);
  if (data.code === -2) {
    // sessionStorage.removeItem('token');
    // router.push({ path: '/login' });
  }
};

// request拦截器
service.interceptors.request.use(
  config => {
    // debugger
    loading = Loading.service({
      lock: true,
      text: (config as any).loadText || '加载中...',
      spinner: 'el-icon-loading',
      background: 'rgba(0, 0, 0, 0.7)'
    });

    // 获取平台传递下来的用户信息  ——20220328 -Leo
    const userInfoStorage = localStorage.getItem('userDetail');
    /* 
      判断在localStorage中是否有userDetail，
      如果有使用该值作为token（系统成功连接综合评估管理平台系统状态），
      如果没有从sessionStorage中获取token值（系统未连接综合评估管理平台系统状态）
    */
    if (userInfoStorage) {
      const userInfo = JSON.parse(userInfoStorage);
      const { id, username, relname } = userInfo;
      const relnamecode = encodeURIComponent(relname);
      const userParams = { id, username, relname: relnamecode || username };
      config.headers.token = JSON.stringify(userParams) || '';
    } else {
      config.headers.token = sessionStorage.getItem('token') || '';
    }
    // 设置请求拦截函数
    config.cancelToken = new cancelToken(c => {
      // removePending(config, c);
      store.dispatch('setCancelToken', { c, config });
    });
    return config;
  },
  error => {
    // Do something with request error
    Promise.reject(error);
  }
);

// respone拦截器
service.interceptors.response.use(
  response => {
    store.dispatch('deleteCancelToken', response.config);
    const { status, data = {} } = response;
    // 代表请求成功的code
    const successCode = [0, 200];

    if (status === 200) {
      const { code } = data;
      // 后端未定义code当请求成功处理
      if (typeof code === 'undefined' || successCode.includes(code)) {
        return Promise.resolve(response).finally(() => {
          if (loading && loading.close) {
            loading.close();
          }
        });
      }
    }
    // debugger
    errorTips(data);
    return Promise.reject(response).finally(() => {
      if (loading && loading.close) {
        loading.close();
      }
    });
  },
  error => {
    // 处理异常
    const { data } = error;
    // 处理后端返回的异常提示
    errorTips(data || {});
    return Promise.reject(error).finally(() => {
      if (loading && loading.close) {
        loading.close();
      }
    });
  }
);

export default service;
