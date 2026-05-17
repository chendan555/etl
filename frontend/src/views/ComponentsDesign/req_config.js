/* 
  * 定义元素业务类型reqOptionsType，元素处理事件的前置处理事件方法reqEventParamsType，表格选项过滤处理reqOptionsDealType
  * Author —— 刘瑞麟
  * modified —— Leo 20220711
*/

export const reqOptionsType = [
  {
    name: '字段类型',
    url: '/api/transNew/fieldType.do',
    optionsConfig: {
      name: 'name',
      value: 'name'
    }
  },
  {
    name: '数据库',
    url: '/api/common/getDatabases.do',
    optionsConfig: {
      name: 'name',
      value: 'name'
    }
  },
  {
    name: '数据库模式',
    url: '/api/database/getSchema.do',
    optionsConfig: {
      name: 'text',
      value: 'text',
      params: `{ "connection":"" }`
    }
  },
  {
    name: '数据表',
    url: '/api/database/getDataTables.do',
    optionsConfig: {
      name: 'text',
      value: 'text',
      params: `{ "connection":"","schema":"" }`
    }
  },
  {
    name: '获取表字段',
    url: '/api/database/getColumnsByTable.do',
    optionsConfig: {
      name: 'name',
      value: 'name',
      params: `{ "connection":"","schema":"","table":"" }`
    }
  },
  {
    name: '表格类型',
    url: '/api/excelInputController/getSpreadSheetType.do',
    optionsConfig: {
      name: 'description',
      value: 'description'
    }
  },
  {
    name: '编码',
    url: '/api/system/availableCharsets.do',
    optionsConfig: {
      name: 'name',
      value: 'name'
    }
  },
  {
    name: '格式化类型',
    url: '/api/excelInputController/getFieldFormats.do',
    optionsConfig: {
      name: 'name',
      value: 'name',
      params: `{ "valueType":"" }`
    }
  },
  {
    name: '去除空格类型',
    url: '/api/excelInputController/getTrimType.do',
    optionsConfig: {
      name: 'desc',
      value: 'code'
    }
  },
  {
    name: '获取上一个步骤名',
    url: '/api/transNew/prevSteps.do',
    optionsConfig: {
      name: 'name',
      value: 'name'
    }
  },
  {
    name: '获取上一个步骤字段',
    url: '/api/transNew/prevStepsFileds',
    optionsConfig: {
      name: 'name',
      value: 'name'
    }
  },
  {
    name: '获取下一个步骤名',
    url: '/api/transNew/nextSteps.do',
    optionsConfig: {
      name: 'name',
      value: 'name'
    }
  },
  {
    name: '获取指定步骤字段',
    url: '/api/transNew/getStepFieldsByName.do',
    optionsConfig: {
      name: 'field',
      value: 'field'
    }
  },
  {
    name: '获取文件或目录',
    url: '/api/system/getFilterFileType.do',
    optionsConfig: {
      name: 'text',
      value: 'value'
    }
  },
  {
    name: '获取数据类型格式',
    url: '/api/system/valueFormat.do',
    optionsConfig: {
      name: 'name',
      value: 'name',
      params: `{ "valueType" : "" }`
    }
  },
  {
    name: '获取聚合方式',
    url: '/api/system/getAggregationType.do',
    optionsConfig: {
      name: 'text',
      value: 'value'
    }
  },
  {
    name: '获取比较符',
    url: '/api/system/getSQLfun.do',
    optionsConfig: {
      name: 'text',
      value: 'value'
    }
  },
  {
    name: '获取大小写方式',
    url: '/api/system/getLowerUpper.do',
    optionsConfig: {
      name: 'text',
      value: 'value'
    }
  },
  {
    name: '获取填充方式',
    url: '/api/system/getPadding.do',
    optionsConfig: {
      name: 'text',
      value: 'value'
    }
  },
  {
    name: '获取删除特殊字符方式',
    url: '/api/system/getRemoveSpecialCharacters.do',
    optionsConfig: {
      name: 'text',
      value: 'value'
    }
  },
  {
    name: '获取XML转译方式',
    url: '/api/system/getmaskXML.do',
    optionsConfig: {
      name: 'text',
      value: 'value'
    }
  },
  {
    name: '获取字符串中数字处理方式',
    url: '/api/system/getDigits.do',
    optionsConfig: {
      name: 'text',
      value: 'value'
    }
  },
  {
    name: '获取字体颜色',
    url: '/api/system/getFontColor.do',
    optionsConfig: {
      name: 'text',
      value: 'value'
    }
  },
  {
    name: '获取字体对齐方式',
    url: '/api/system/getFontAlignmentr.do',
    optionsConfig: {
      name: 'text',
      value: 'value'
    }
  },
  {
    name: '获取字体方向',
    url: '/api/system/getFontOrientation.do',
    optionsConfig: {
      name: 'text',
      value: 'value'
    }
  },
  {
    name: '获取字体下划线',
    url: '/api/system/getFontUnderline.do',
    optionsConfig: {
      name: 'text',
      value: 'value'
    }
  },
  {
    name: '获取字体名称',
    url: '/api/system/getFontName.do',
    optionsConfig: {
      name: 'text',
      value: 'value'
    }
  },
  {
    name: '获取时区',
    url: '/api/system/getTimeZone.do',
    optionsConfig: {
      name: 'text',
      value: 'value'
    }
  },
  {
    name: '获取时间格式化类型',
    url: '/api/system/datetimeformat.do',
    optionsConfig: {
      name: 'name',
      value: 'name'
    }
  },
  {
    name: '获取变量类型',
    url: '/api/system/variableType.do',
    optionsConfig: {
      name: 'name',
      value: 'code'
    }
  },
  {
    name: '获取本地日期',
    url: '/api/system/getLocalDateType.do',
    optionsConfig: {
      name: 'code',
      value: 'value'
    }
  }
];

export const reqTreeDataType = [
  {
    name: '文件目录',
    url: '/api/transNew/files.do',
    treeConfig: {
      label: 'path',
      params: `{ "path" : "/" }`,
      isLazyLoad: true
    }
  }
];

// 事件参数请求配置
export const reqEventParamsType = [
  {
    name: '生成sql',
    url: '/api/database/getColumnsByTable.do',
    resDeal: 'getSqlString'
  },
  {
    name: '构造表格数据',
    url: null,
    resDeal: 'getTableAddRow'
  },
  {
    name: '加载数据库字段',
    url: 'api/database/getColumnsByTable.do',
    resDeal: 'laodTableColumns'
  },
  {
    name: '获取工作表名称',
    url: '/api/excelInputController/getExcelSheet.do',
    resDeal: 'getExcelSheet'
  },
  {
    name: '获取文本文件字段',
    url: '/api/textFileInputController/getFields.do',
    resDeal: 'getFileFields'
  },
  {
    name: '获取Excel字段',
    url: '/api/excelInputController/getFields.do',
    resDeal: 'getExcelFields'
  },
  {
    name: '获取XML文件字段',
    url: '/api/xmlFileInputController/getFields.do',
    resDeal: 'getXMLFields'
  },
  {
    name: '创建达梦数据库表',
    url: '/api/common/createTableByDM.do',
    resDeal: ''
  },
  {
    name: '上传文件',
    url: '/api/excelInputController/upload.do',
    resDeal: 'getTableAddRow'
  }
];

// 表格选项过滤处理
export const reqOptionsDealType = [
  {
    name: '通过字段类型过滤获取格式',
    resOptionsDeal: 'getFieldFormats'
  },
  {
    name: '通过节点获取字段',
    resOptionsDeal: 'getStepFields'
  }
];
