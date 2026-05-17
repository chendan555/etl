/**
 *
 * mock样例数据文件
 * 定义各类样例数信息
 *
 */

import utils from '../utils/index';
import './data/table-relation';
import './data/resolve-protocol';
import './data/config-file';
import './data/data-receive';
// import './data/task';
import './config';

const Mock = require('mockjs');

/**
 * 定义状态码
 */
const code = 200;

/**
 * @description 定义数据表接口
 */
Mock.mock('/api/getTableList', 'get', () => {
  const list: any = [];
  for (let i = 0; i < 50; i++) {
    const keysNum = Math.floor(Math.random() * 8) + 5;
    // 设置表字段信息
    const keys: any = [];
    for (let u = 0; u < keysNum; u++) {
      keys.push(Mock.Random.title(1));
    }
    // 设置表名称、id
    const table = {
      name: Mock.Random.title(1),
      id: utils.createRandomId(),
      keys
    };
    list.push(table);
  }
  return {
    success: true,
    data: {
      list
    }
  };
});

/**
 * 定义流程标签接口
 */
Mock.mock('/api/project/tagList', 'get', () => {
  const data = [
    {
      label: '数据库',
      value: '数据库'
    },
    // {
    //   label: '音频',
    //   value: '音频'
    // },
    // {
    //   label: '视频',
    //   value: '视频'
    // },
    // {
    //   label: '电子表格',
    //   value: '电子表格'
    // },
    // {
    //   label: '二进制',
    //   value: '二进制'
    // },
    // {
    //   label: '图片',
    //   value: '图片'
    // },
    // {
    //   label: '扫描文件',
    //   value: '扫描文件'
    // }
  ];

  return { code, data };
});
