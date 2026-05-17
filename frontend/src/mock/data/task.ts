/**
 *
 * mock样例数据文件
 * 定义各类样例数信息
 *
 */

// import utils from '@/utils/index';
const Mock = require('mockjs');
// const random = Mock.Random;
/**
 * 创建数据
 */
const taskList = [
  {
    taskName: '评估任务1',
    id: '0001'
  },
  {
    taskName: '评估任务2',
    id: '0002'
  },
  {
    taskName: '评估任务3',
    id: '0003'
  }
];
/**
 * 任务接口
 */
Mock.mock('/management-task/taskManagement/task/selectTaskNode', 'post', () => {
  return {
    code: 0,
    msg: '请求成功',
    data: {
      total: taskList.length,
      list: taskList,
      pageNum: 1,
      pageSize: 10,
      pages: 1
    }
  };
});
/**
 * 任务接口
 */
Mock.mock('/management-task/taskManagement/task/selecTaskDataById', 'get', ({ body }: any) => {
  return {
    code: 0,
    msg: '请求成功',
    data: {
      nodes: [
        {
          id: '',
          nodeId: '',
          nodeName: '',
          processId: '',
          nodeType: ''
        }
      ]
    }
  };
});
