import configView from './config';
import show from './show';
const info = {
  category: 10000,
  name: '表格列表',
  icon: 'table',
  type: 'tableList123123', //组件的唯一类型
  row: 4, //默认行数
  col: 6, //默认列数
  child: [],
  size:'large',
  head: [],
  baseList: [],
  textAlign: '',
  verticalAlign: '',
  th: '',
}
const userInputConfig = {
  x: 0,
  y: 0,
  w: 600,
  h: 160,
  zIndex:'',
  background: '',
  textAlign: 'center',
  verticalAlign: 'middle',
  fontWeight: false,
  fontStyle: false,
  textDecoration: false,
  textTransform: 'inherit',
  fontSize: '16',
  color:  '',
  opacity: '100',
  lineHeight: 'inherit',
}
const userAttrConfig = {
  key: ''
}
const config = [
  // {prop:'key',type:'text',value:'',label:'表单字段名'},
]

export {info, configView, show, userInputConfig, userAttrConfig, config }
