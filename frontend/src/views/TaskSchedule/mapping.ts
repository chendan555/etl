export default {
  /**
   * @description 调度任务状态
   */
  jobStatus: {
    '0': '正常',
    '1': '暂停'
  },
  /**
   * @description 计划策略
   */
  misfirePolicy: {
    '0': '默认',
    '1': '立即触发执行',
    '2': '触发一次执行',
    '3': '不触发立即执行'
  },
  /**
   * @description 是否并发执行
   */
  concurrent: {
    '0': '允许',
    '1': '禁止'
  }
};
