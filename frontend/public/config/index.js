// 解析跳转地址路径参数
function analysisUrlParams(url) {
  const paramsArr = url.split('?');
  let paramsString = '';
  if (paramsArr.length > 1) {
    paramsString = paramsArr[1];
  } else {
    return null;
  }
  const paramsObjArr = paramsString.split('&');
  const params = {};
  paramsObjArr.forEach(param => {
    const keyValue = param.split('=');
    params[keyValue[0]] = keyValue[1];
  });
  return params;
}

if (window && window.location) {
  const userInfo = analysisUrlParams(window.location.hash);
  if (userInfo) sessionStorage.setItem('user', JSON.stringify(userInfo));
}


window.onload = function() {
  window.addEventListener('message', e => {
    if (e.data.hasOwnProperty('relname')) {
      localStorage.setItem('userDetail', JSON.stringify(e.data));
    }
  });
};

// 评估平台系统主页地址
window.homeUrl = 'http://www.baidu.com';
window.formUrl = 'http://localhost:8091';

/* const token =
  'Bearer_eyJraWQiOiIxMDAiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJ1cm46Y29tOm5ldHdvcmtudDpvYXV0aDI6djEiLCJhdWQiOiJ1cm46Y29tLm5ldHdvcmtudCIsImV4cCI6MTY0Mjk5MTY4OCwianRpIjoiUEZQamVwNTRBUmNQUzF5QVh4V2VJUSIsImlhdCI6MTY0Mjk5MTA4OCwibmJmIjoxNjQyOTkwOTY4LCJ2ZXJzaW9uIjoiMS4wIiwiY2xpZW50X0lkIjpudWxsLCJzeXNVc2VyIjoie1wiZGVwYXJ0bWVudElkXCI6XCI5YzY4ZTM0Ni0xYzE1LTQ0ZjAtYTllNi1hZjdlNDFlNGQ4ZjlcIixcImRlcGFydG1lbnROYW1lXCI6XCLkv53pmpzokKVcIixcImlkXCI6XCI1NVwiLFwibmFtZVwiOlwi566h55CG5ZGYXCIsXCJyb2xlSWRzXCI6XCJBRE1JTlwiLFwidXNlcm5hbWVcIjpcImFkbWluXCJ9In0.kedJ95px5_0880qIx_-9tNh1m9LAuJ30vqq-o65paxN0vtnNBmtIvFNd0bFwOGJau9Cw_gE4RTIy_eJakjRAaOfFKcphfP-rWwgAccMJQRWApg3Yw6NcqtRUIpcCgrCpgSKq_ZI2WTiq7KxGfYyhC5geCo_6HALSJforIAf1CcDS74c52g_zBfSFGMezTxJ1AZEqIx5QWIMrc6T0P0UXxirpdMSLv_ZXUEELf6dw7rso4YNBa7mfD6xWNzNgxEK6jolJjV4vtVmxrL8dnJT_G8OIvRiW_xzlqWqgTm5bhcfILpRcQ9IvIGxg242_iW9d9GsMUldWCOMvmOSLzBOHew';
sessionStorage.setItem('token', token); */
