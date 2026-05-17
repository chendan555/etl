/**
 * Create by leo
 * 系统公共方法文件
 * 定义各类在功能模块均可使用的方法函数
 * 减少代码量
 *
 */

import { Loading, Message, MessageBox } from 'element-ui';
import { components } from '@/views/ComponentsDesign/components.js';
import { loginOut } from '@/api';
/**
 * 工具函数统一出口
 */
const utils = {
  /**
   * @description 接口参数转换
   * @param params
   */
  transferToFormData(params: any) {
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
  },
  /**
   * 文件下载
   * @param href
   * @param name
   */
  downloadFile(href: string, name?: string) {
    let a = document.createElement('a');
    a.href = href;
    a.download = name || '';
    a.target = '_blank';
    a.style.display = 'none';
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
  },
  /**
   * 确认弹框
   * @param msg
   * @param title
   * @param confirmButtonText
   * @param cancelButtonText
   * @param type
   */
  confirm(
    msg: string = '此操作将永久删除数据, 是否继续?',
    title: string = '提示',
    confirmButtonText: string = '确定',
    cancelButtonText: string = '取消',
    type: any = 'warning'
  ) {
    return new Promise((resolve, reject) => {
      MessageBox.confirm(msg, title, {
        showCancelButton: true,
        confirmButtonText: confirmButtonText,
        cancelButtonText: cancelButtonText,
        confirmButtonClass: 'confirm-btn',
        type: type
      })
        .then(resolve)
        .catch(reject);
    });
  },
  /**
   * 消息弹框
   * @param type
   * @param msg
   */
  setMessage(type: boolean, msg: string) {
    Message({
      type: type ? 'success' : 'warning',
      message: msg
    });
  },
  /**
   * 获取时间
   * @param d
   * @param divide
   */
  setTimeType(d?: any, divide?: string) {
    d = d || new Date();
    let date = d.toLocaleDateString();
    if (divide) {
      date.replace(/\//g, divide);
    }
    let time = d.toTimeString().slice(0, 8);
    return `${date} ${time}`;
  },
  /**
   * 测试字段命名是否符合规则
   * @param str
   */
  checkFieldFormat(str: string) {
    let patt1 = /[A-z]|_/;
    let patt2 = /[A-z]|[0-9]|_/;
    if (patt1.test(str[0])) {
      let bool = true;
      for (let i = 1; i < str.length; i++) {
        if (!patt2.test(str[i])) {
          bool = false;
        }
      }
      if (bool) {
        return true;
      }
    }
    return false;
  },
  /**
   * 判断数据类型
   * @param data
   */
  checkDataType(data: any) {
    let typeStr = Object.prototype.toString.call(data);
    return typeStr.slice(8, -1);
  },
  /**
   * 公有加载方法
   * @param text
   */
  setLoading(text: string) {
    return Loading.service({
      lock: true,
      text: text || '加载中...',
      spinner: 'el-icon-loading',
      background: 'rgba(0, 0, 0, 0.7)'
    });
  },
  /**
   * 深拷贝
   * @param obj
   */
  deepCopy(obj: Object): any {
    return JSON.parse(JSON.stringify(obj));
  },
  /**
   * 深拷贝
   * 递归方式
   * @param obj
   */
  deepClone(obj: any) {
    if (typeof obj !== 'object' || obj === null) {
      return obj;
    }
    let result: any = typeof obj.splice === 'function' ? [] : {};
    let key;
    if (obj && typeof obj === 'object') {
      for (key in obj) {
        if (obj[key] && typeof obj[key] === 'object') {
          result[key] = this.deepClone(obj[key]);
        } else {
          result[key] = obj[key];
        }
      }
      return result;
    }
    return obj;
  },

  /**
   * 校验IP地址
   * @param str
   */
  isIP(str: string) {
    const reg = /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/;
    return reg.test(str);
  },
  /**
   * 校验IP地址
   * @param str
   */
  getIpUrl(str: string) {
    const reg = /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/;
    return str.match(reg);
  },
  /**
   * 创建随机id
   */
  createRandomId(): any {
    return (
      (Math.random() * 10000000).toString(16).substr(0, 4) +
      '-' +
      new Date().getTime() +
      '-' +
      Math.random()
        .toString()
        .substr(2, 5)
    );
  },
  /**
   * 更新组件配置项
   * @param eleList
   */
  updateFormConfig: function(eleList: any) {
    let newEleList = utils.deepClone(eleList);
    let componentObj: any = {};
    components.forEach((comp: any) => {
      componentObj[comp.key] = comp.config;
    });
    newEleList.forEach((ele: any) => {
      if (ele.key === 'tabs') {
        ele.config.eleList = utils.updateFormConfig(ele.config.eleList);
      }
      for (let key in componentObj[ele.key]) {
        if (!(key in ele.config)) {
          ele.config[key] = componentObj[ele.key][key];
        }
        // tableColumnConfig
        if (key === 'tableColumnConfig') {
          let tableColumnConfig = componentObj[ele.key].tableColumnConfig[0];
          ele.config[key].forEach((columnConfig: any) => {
            for (let key in tableColumnConfig) {
              if (!(key in columnConfig)) {
                columnConfig[key] = tableColumnConfig[key];
              }
              if (key === 'optionsConfig') {
                for (let configkey in tableColumnConfig.optionsConfig) {
                  if (!(configkey in columnConfig.optionsConfig)) {
                    columnConfig.optionsConfig[configkey] = tableColumnConfig.optionsConfig[configkey];
                  }
                }
              }
            }
          });
        }
      }
    });
    return newEleList;
  },
  // 自动登录
  /**
   * 自动登录
   */
  autoLogin() {
    const userInfo = sessionStorage.getItem('user');
    if (userInfo) {
      return;
    }
    // 无需登录认证，直接设置默认用户
    const defaultUser = {
      username: 'admin',
      relname: '管理员',
      id: '1'
    };
    sessionStorage.setItem('user', JSON.stringify(defaultUser));
  }
};
/**
 * 清除格式
 */
export const clear = function(json: any) {
  let reset: any = {
    Number: 0,
    Undefined: undefined,
    Null: null,
    Object: {},
    Array: [],
    String: ''
  };
  for (let i in json) {
    for (let j in reset) {
      if (Object.prototype.toString.call(i).includes(j)) {
        json[i] = reset[j];
        break;
      }
    }
  }
};

/**
 *  数组对象去重
 */
export const newSetArry = function(array: any) {
  let map = new Map();
  for (let index in array) {
    map.set(array[index].transformationId, array[index]);
  }
  let result: any = [];
  map.forEach(value => {
    result.push(value);
  });
  return result;
};
export default utils;
