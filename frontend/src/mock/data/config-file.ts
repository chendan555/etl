/**
 *
 * mock样例数据文件
 * 定义各类样例数信息
 *
 */

import utils from '@/utils/index';
const Mock = require('mockjs');
const random = Mock.Random;

/**
 * 文件列表
 */
const fileList: any = [];
/**
 * 仿真列表
 */
const emulatedata: any = [];
/**
 * 表单列表
 */
const formwordList: any = [];
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
 * 创建数据
 */
(() => {
  const taskName = random.ctitle(3, 5);
  for (let i = 0; i < 40; i++) {
    /**
     * 创建文件数据
     */
    const fileType = Math.floor(Math.random() * 3);
    const fileName = random.ctitle(3, 5);
    const file = {
      id: utils.createRandomId(),
      name: fileName,
      filePath: `192.168.1.1:8181/fileData/${fileName}.${fileTypeList[fileType].value}`,
      taskName,
      nodeName: random.ctitle(3, 5),
      protocol: '',
      protocolName: '',
      remarks: random.ctitle(5, 10)
    };
    fileList.push(file);
    /**
     * 创建表单数据
     */
    const formword = {
      id: utils.createRandomId(),
      name: fileName,
      filePath: `192.168.1.1:8181/fileData/${fileName}.docx`,
      taskName,
      nodeName: random.ctitle(3, 5),
      template: '',
      remarks: random.ctitle(5, 10)
    };
    formwordList.push(formword);
    /**
     * 创建仿真数据
     */
    const ipNum = Math.floor(Math.random() * 999) + 1;
    let emulate = {
      id: utils.createRandomId(),
      name: random.ctitle(3, 5),
      IP: `192.168.1.${ipNum}:8080`,
      databaseId: '',
      databaseName: '',
      modelId: '',
      modelName: '',
      tableId: '',
      tableName: '',
      remarks: random.ctitle(5, 10)
    };
    emulatedata.push(emulate);
  }
})();
/**
 * 创建文件数据接口
 */
Mock.mock('/api/getFileList', 'get', ({ body }: any) => {
  const { pageSize, pageNum } = JSON.parse(body);
  return {
    success: true,
    data: {
      list: fileList.slice(pageSize * (pageNum - 1), pageSize * pageNum),
      total: fileList.length
    }
  };
});
/**
 * 创建仿真数据接口
 */
Mock.mock('/api/getEmulatedataList', 'get', ({ body }: any) => {
  const { pageSize, pageNum } = JSON.parse(body);
  return {
    success: true,
    data: {
      list: emulatedata.slice(pageSize * (pageNum - 1), pageSize * pageNum),
      total: emulatedata.length
    }
  };
});
/**
 * 创建表单数据接口
 */
Mock.mock('/api/getFormwordListList', 'get', ({ body }: any) => {
  const { pageSize, pageNum } = JSON.parse(body);
  return {
    success: true,
    data: {
      list: formwordList.slice(pageSize * (pageNum - 1), pageSize * pageNum),
      total: formwordList.length
    }
  };
});
