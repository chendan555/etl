export default {
  validateServerName: (rule: any, value: string, callback: any) => {
    if (!value) {
      callback(new Error('请输入服务器名称或IP地址！'));
    } else {
      const valueString = value.toLowerCase();
      // 允许 localhost 和 127.0.0.1
      if (value === 'localhost' || value === '127.0.0.1') {
        callback();
        return;
      }
      let ipArr = valueString.split('.');
      if (ipArr.length === 4) {
        let bool = true;
        ipArr.forEach(ele => {
          if (ele.length != Number(ele).toString().length || ele.length > 3 || !ele.length) {
            bool = false;
          }
        });
        if (bool) {
          callback();
          return;
        }
      }
      callback(new Error('请输入正确的服务器名称或IP地址！'));
    }
  },
  validatePort: (rule: any, value: string, callback: any) => {
    value = value.toString();  // 编辑时数据为数值型，转为字符串后进行判断  --Leo-20221008
    if (!value) {
      callback(new Error('请输入端口号！'));
    } else {
      if (value.length === Number(value).toString().length && Number(value) > 0 && Number(value) < 65535) {
        callback();
      } else {
        callback('请输入正确的端口号！');
      }
    }
  }
};
