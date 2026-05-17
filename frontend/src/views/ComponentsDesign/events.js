/* 
  * 定义元素处理事件信息 
    - key —— 约束事件适用元素类型
    - label —— 定义下拉中显示内容
    - value —— 定义事件名称，事件方法在各元素组件内定义
  * Author —— 刘瑞麟
  * modified —— Leo 20220711
*/

export default [
  {
    key: 'table',
    label: '增加行',
    value: 'add'
  },
  {
    key: 'select',
    label: '更新选项',
    value: 'updateOptions'
  },
  {
    key: 'selectTree',
    label: '更新选项树',
    value: 'updateOptionsTree'
  },
  {
    key: 'code',
    label: '内容填充',
    value: 'setValue'
  },
  {
    key: 'createTableBtn',
    label: '创建表',
    value: 'createTable'
  },
/*   {
    key: 'input',
    label: '输入验证',
    value: 'validateValue'
  }, */
/*   {
    key: 'table',
    label: '加载数据库字段',
    value: 'laodTableColumns'
  } */
];
