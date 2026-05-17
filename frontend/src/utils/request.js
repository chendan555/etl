/* eslint-disable linebreak-style */
/* eslint-disable prefer-promise-reject-errors */
/* eslint-disable new-cap */
/* eslint-disable no-param-reassign */
/* eslint-disable arrow-parens */
/* eslint-disable spaced-comment */
/* eslint-disable import/newline-after-import */
/* eslint-disable linebreak-style */
/**
 * Create by leo
 * 系统接口方法文件
 * 定义各类在功能模块均可使用的接口方法函数
 * 减少代码量
 *
 */
import axios from 'axios';
// import { Message } from 'element-ui';
const SERVER = axios.create({
  baseURL: '',
  timeout: 10000,
});

const pendingList = []; //存储ajax列表

const cancelToken = axios.CancelToken; //请求拦截函数
//拦截函数执行主体
const removePending = (config, fn) => {
  //拼装完整请求 做请求重复数据校验
  let completeAjax = config.url + config.method; //判断请求的是否重复 应该包含 请求路径 请求方式 请求参数

  //读取请求参数根据请求方式来界定
  if (['post', 'POST'].includes(config.method)) {
    completeAjax += JSON.stringify(config.data);
  } else {
    completeAjax += JSON.stringify(config.params);
  }
  if (pendingList.includes(completeAjax)) {
    //已经有这个请求，表示该请求重复
    if (fn) {
      //取消函数存在，执行
      fn();
      console.info(`已拦截重复请求！路径${config.url}`);
    } else {
      //不存在取消函数，删除队列请求项
      pendingList.splice(pendingList.indexOf(completeAjax));
    }
  } else {
    //没有这个请求，表示是新的请求，加入请求队列
    pendingList.push(completeAjax);
  }
};
//拦截器
SERVER.interceptors.request.use(
  config => {
    // 执行请求
    config.cancelToken = new cancelToken(c => {
      removePending(config, c);
    });
    // Message.error('开始调用！');
    return config;
  },
  error => Promise.reject(error),
);
SERVER.interceptors.response.use(
  response => {
    //响应 返回成功 移除该请求
    removePending(response.config);
    // console.log(response);
    if (response.code === 200) {
      return response.data;
    }
    // Message.error('调用失败！');
    return Promise.reject('');
  },
  error => Promise.reject(error),
);

export default SERVER;
