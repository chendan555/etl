/**
 *
 * mock样例数据文件
 * 定义各类样例数信息
 *
 */

import utils from '@/utils/index';
const Mock = require('mockjs');
let tableRelationList = <any>[];
let mainTableList = <any>[];
/**
 * 创建数据
 */
(() => {
  for (let i = 0; i < 25; i++) {
    let demoData = {
      id: utils.createRandomId(),
      name: Mock.Random.ctitle(3, 5),
      createDate: utils.setTimeType(),
      createBy: '管理员',
      remarks: Mock.Random.ctitle(5, 7),
      connection: {},
      schema: {},
      mainTable: ''
    };
    tableRelationList.unshift(demoData);
    /**
     * 创建数据
     */
    let mainTable = {
      id: utils.createRandomId(),
      modelId: 'GX_GH_BASE',
      databaseId: '1',
      keyNums: '7',
      remarks: null,
      tableName: 'S_POINT_MAN',
      relationName: '数据'
    };
    mainTableList.push(mainTable);
  }
})();

/**
 * 新增表关系
 */
Mock.mock('/api/addTableRelation', 'post', ({ body }: any) => {
  let data = JSON.parse(body);
  let table = utils.deepCopy(data.tableRelation[0].table);
  data.id = utils.createRandomId();
  table.relationName = data.name;
  table.relation = data;
  let obj = <any>null;
  obj = mainTableList.find((ele: any) => ele.tableName === table.tableName && ele.modelId === table.modelId && ele.databaseId === table.databaseId);
  if (obj) {
    return {
      success: false,
      msg: '新增失败'
    };
  } else {
    mainTableList.unshift(table);
    tableRelationList.unshift(data);
    return {
      success: true,
      msg: '新增成功'
    };
  }
});

/**
 * 更新表关系
 */
Mock.mock('/api/updateTableRelation', 'post', ({ body }: any) => {
  let data = JSON.parse(body);
  tableRelationList.forEach((ele: any, index: number) => {
    if (ele.id === data.id) {
      tableRelationList.splice(index, 1, data);
    }
  });
  return {
    success: true,
    msg: '修改成功'
  };
});

/**
 * 删除表关系
 */
Mock.mock('/api/deleteTableRelation', 'delete', ({ body }: any) => {
  let data = JSON.parse(body);
  tableRelationList.forEach((ele: any, index: number) => {
    if (ele.id === data.id) {
      tableRelationList.splice(index, 1);
    }
  });
  return {
    success: true,
    msg: '删除成功'
  };
});

/**
 * 系获取表关系列表
 */
Mock.mock('/api/getTableRelationList', 'get', ({ body }: any) => {
  let { pageSize, pageNum } = JSON.parse(body);
  let list = tableRelationList.slice((pageNum - 1) * pageSize, pageNum * pageSize);
  return {
    success: true,
    data: {
      list: list,
      total: tableRelationList.length
    }
  };
});

/**
 * 获取表关系列表
 */
Mock.mock('/api/getMainTableList', 'get', ({ body }: any) => {
  let { pageSize, pageNum } = JSON.parse(body);
  let list = mainTableList.slice((pageNum - 1) * pageSize, pageNum * pageSize);
  return {
    success: true,
    data: {
      list: list,
      total: mainTableList.length
    }
  };
});

/**
 * 删除表关系
 */
Mock.mock('/api/deleteMainTableList', 'delete', ({ body }: any) => {
  let data = JSON.parse(body);
  mainTableList.forEach((ele: any, index: number) => {
    if (ele.id === data.id) {
      mainTableList.splice(index, 1);
    }
  });
  return {
    success: true,
    msg: '删除成功'
  };
});
