/**
 *
 * mock样例数据文件
 * 定义各类样例数信息
 *
 */

import utils from '@/utils/index';
const Mock = require('mockjs');
const random = Mock.Random;
let resolveProtocolList = <any>[];
/** 
 * 文件类型列表
 */
const fileTypeList = [
  {
    label: '1',
    value: 'txt'
  },
  {
    label: '2',
    value: 'csv'
  },
  {
    label: '3',
    value: 'xml'
  }
];
/** 
 * 装备类型列表
 */
const equipTypeList = [
  {
    label: '遥测车',
    value: '1'
  },
  {
    label: '雷达',
    value: '2'
  },
  {
    label: '干扰器',
    value: '3'
  }
];
/** 
 * 创建数据
 */
(() => {
  for (let i = 0; i < 30; i++) {
    const typeIndex = Math.floor(Math.random() * 3);
    const rp = {
      name: random.ctitle(2, 4),
      id: utils.createRandomId(),
      equipTypeName: random.ctitle(2, 4),
      fileType: (typeIndex + 1).toString(),
      fileTypeName: fileTypeList[typeIndex].value,
      createBy: 'admin',
      createDate: utils.setTimeType(),
      remarks: random.ctitle(4, 8)
    };
    resolveProtocolList.push(rp);
  }
})();
/** 
 * 获取解析协议接口
 */
Mock.mock('/api/getResolveProtocolList', 'get', ({ body }: any) => {
  const { pageSize, pageNum } = JSON.parse(body);
  return {
    success: true,
    data: {
      list: resolveProtocolList.slice(pageSize * (pageNum - 1), pageSize * pageNum),
      total: resolveProtocolList.length
    }
  };
});
/** 
 * 获取解析协议接口
 */
Mock.mock('/api/getAllProtocolList', 'get', () => {
  return {
    success: true,
    data: resolveProtocolList
  };
});
