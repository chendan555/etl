/**
 *
 * mock样例数据文件
 * 定义各类样例数信息
 *
 */

import utils from '@/utils/index';
const Mock = require('mockjs');
const random = Mock.Random;
let batchNumber = 1;
let receiveRecords: any = [];
/**
 * 设置超时时间
 */
Mock.setup({
  timeout: '2000'
});
/**
 * 引接数据接口
 */
Mock.mock('/api/receiveData', 'post', ({ body }: any) => {
  let params = JSON.parse(body);
  params.receiveBy = 'admin';
  params.receiveDate = utils.setTimeType();
  /**
   * 判断批次号
   */
  if (params.batchNumber < batchNumber) {
    let crtBatch = receiveRecords.find((ele: any) => ele.batchNumber === params.batchNumber);
    if (crtBatch) {
      crtBatch.receiveDate = params.receiveDate;
      Object.keys(params.records).forEach((key: string) => {
        if (!crtBatch.records.hasOwnProperty(key)) {
          crtBatch.records[key] = <any>[];
        }
        crtBatch.records[key] = crtBatch.records[key].concat(params.records[key]);
      });
    } else {
      receiveRecords.push(params);
    }
  } else {
    receiveRecords.push(params);
    batchNumber++;
  }
  return {
    success: true,
    msg: '引接成功'
  };
});
/**
 * 获取批次号接口
 */
Mock.mock('/api/getBatchNumber', 'get', () => {
  return {
    success: true,
    data: {
      data: batchNumber
    }
  };
});
/**
 * 获取引接记录
 */
Mock.mock('/api/getReceiveRecords', 'get', () => {
  return {
    success: true,
    data: receiveRecords
  };
});
