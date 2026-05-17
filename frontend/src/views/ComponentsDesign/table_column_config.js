export const table_column_config = {
  label: '数据字段',
  valueKey: [],
  prop: 'name',
  showOverflowTooltip: true,
  align: 'left',
  headerAlign: 'left',
  isEdit: true,
  editType: 'input',
  isHttpGetOptions: false,
  httpUrl: "",
  params: "{}",
  defaultValue: "",
  optionsConfig: {
    name: "name",
    value: "value",
    isRelyOnRowData: false, // 是否依赖表格行数据进行请求 1.不在打开弹框时预请求 2.在点击带下拉框的单元格时依赖行数据请求
    multiple: false, // 是否能多选
  },
  options: [{
    name: '测试1',
    value: '1'
  }, {
    name: '测试2',
    value: '2'
  }],
}
