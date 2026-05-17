// 迎接查询的表格配置
export const cljgTableConfig = {
  tableColumnConfig: [
    {
      label: '输出数据名称',
      prop: 'stepName',
      align: 'center'
    },
    {
      label: '处理时间',
      prop: 'opDateString',
      align: 'center'
    },
    {
      label: '处理流程名称',
      prop: 'jobName',
      align: 'center'
    },
    {
      label: '执行人',
      prop: 'opUserName',
      align: 'center'
    },
    // {
    //   label: '执行批次',
    //   prop: 'runBatch',
    //   align: 'center'
    // },
    // {
    //   label: '数据来源',
    //   prop: 'useType',
    //   align: 'center'
    // }
  ]
};
