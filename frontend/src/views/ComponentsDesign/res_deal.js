/* 
  * 定义元素处理事件的前置处理事件方法，事件来源于req_config.js中的reqEventParamsType的resDeal项
  * Author —— 刘瑞麟
  * modified —— Leo 20220711
*/

const resDeal = {
  getSqlString: function(res) {
    const columns = Array.isArray(res.data) ? res.data : (res.data && Array.isArray(res.data.data) ? res.data.data : []);
    let fields = columns
      .map(item => {
        return `"${item.name}"`; // 加引号，避免关键字导致sql执行报错
      })
      .join();
    return `SELECT ${fields} FROM "${res.config.data.get('schema')}"."${res.config.data.get('table')}"`;
  },
  // 加载所有数据库字段并添加到表格中 —— Leo 20220711
  laodTableColumns(value, targetEle, event) {
    let rowArr = [];
    value.data.forEach(item=>{
      let addRow = {};
      targetEle.tableColumnConfig.forEach(config => {
        if (config.prop === event.eventParamsConfig.column) {
          addRow[config.prop] = item.name;
        } else {
          addRow[config.prop] = config.defaultValue;
        }
      });
      rowArr.push(addRow);
    })
    return rowArr;
  },
  getTableAddRow(value, targetEle, event) {
    let addRow = {};
    targetEle.tableColumnConfig.forEach(config => {
      if (config.prop === event.eventParamsConfig.column) {
        addRow[config.prop] = value;
      } else {
        addRow[config.prop] = config.defaultValue;
      }
    });
    return addRow;
  },
  getExcelSheet(res, targetEle, event) {
    let addRowArr = [];
    // 解包后端 Result 包装
    let dataList = Array.isArray(res.data) ? res.data : (res.data && Array.isArray(res.data.data) ? res.data.data : []);
    dataList.forEach(data => {
      let addRow = {};
      targetEle.tableColumnConfig.forEach(config => {
        if (config.prop === event.eventParamsConfig.column) {
          addRow[config.prop] = data.name;
        } else {
          addRow[config.prop] = config.defaultValue;
        }
      });
      addRowArr.push(addRow);
    });
    return addRowArr;
  },
  getExcelFields(res, targetEle, event) {
    let addRowArr = [];
    // 解包后端 Result 包装：{code:200, data:[...], msg:"success"}
    let dataList = Array.isArray(res.data) ? res.data : (res.data && Array.isArray(res.data.data) ? res.data.data : []);
    dataList.forEach(data => {
      let addRow = {};
      targetEle.tableColumnConfig.forEach(config => {
        if (config.prop.indexOf('name') > -1) {
          addRow[config.prop] = data.name;
        } else {
          addRow[config.prop] = config.defaultValue;
        }
      });
      addRowArr.push(addRow);
    });
    return addRowArr;
  },
  getFileFields(res, targetEle, event) {
    let addRowArr = [];
    let dataList = Array.isArray(res.data) ? res.data : (res.data && Array.isArray(res.data.data) ? res.data.data : []);
    dataList.forEach(data => {
      let addRow = {};
      targetEle.tableColumnConfig.forEach(config => {
        let colNameArr = config.prop.split('.');
        let colName = '';
        if (colNameArr.length === 3 && data.hasOwnProperty(colNameArr[2])) {
          colName = colNameArr[2];
        }
        addRow[config.prop] = data[colName];
      });
      addRowArr.push(addRow);
    });
    return addRowArr;
  },
  getXMLFields(res, targetEle, event) {
    let addRowArr = [];
    let dataList = Array.isArray(res.data) ? res.data : (res.data && Array.isArray(res.data.data) ? res.data.data : []);
    dataList.forEach(data => {
      let addRow = {};
      targetEle.tableColumnConfig.forEach(config => {
        let colNameArr = config.prop.split('.');
        let colName = '';
        if (colNameArr.length === 3 && data.hasOwnProperty(colNameArr[2])) {
          colName = colNameArr[2];
        }
        addRow[config.prop] = data[colName];
      });
      addRowArr.push(addRow);
    });
    return addRowArr;
  }
};

export default resDeal;
