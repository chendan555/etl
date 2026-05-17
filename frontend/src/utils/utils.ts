/**
 * Create By WangSheng on 2020/05/13
 * 常用方法封装
 */

/**
 * 解码base64编码的字符串
 */
export const atob = (enCodeString: string): string => {
  return window.atob ? window.atob(enCodeString) : enCodeString;
};

/**
 * 转换为FormData
 * params：参数
 */
export const transferToFormData = (params: any) => {
  let formData = new FormData();
  for (let key in params) {
    formData.append(key, typeof params[key] === 'string' ? params[key] : JSON.stringify(params[key]));
  }
  let config = {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  };

  return {
    formData,
    config
  };
};

/**
 * 编码base64字符串
 */
export const btoa = (deCodeString: string): string => {
  return window.btoa ? window.btoa(deCodeString) : deCodeString;
};

/**
 * @description 复制对象
 * @param obj 深拷贝对象
 */
export const copyObj = function(obj: any) {
  return obj ? JSON.parse(JSON.stringify(obj)) : JSON.parse(JSON.stringify({}));
};

// 生成随机id
export const createRandomId = function() {
  return (
    (Math.random() * 10000000).toString(16).substr(0, 4) +
    '-' +
    new Date().getTime() +
    '-' +
    Math.random()
      .toString()
      .substr(2, 5)
  );
};
/**
 * 导出
 */
export const exports = (res: any, callback: any) => {
  let fileName = '';
  let blob = new Blob([res.data]);
  let url = window.URL.createObjectURL(blob);
  let link = document.createElement('a');
  link.style.display = 'none';
  link.href = url;

  let disposition = res.headers['content-disposition'];

  if (disposition) {
    fileName = res.headers['content-disposition'].split('=');
  }
  link.setAttribute('download', decodeURI(fileName ? fileName[fileName.length - 1] : 'xxxx'));

  document.body.appendChild(link);

  link.click();

  callback();
};

/**
 * 下载图片
 */
export const downloadImg = (imgSrc: string, suffix: string = 'png'): any => {
  return new Promise((resolve, reject) => {
    const img = new Image() as any;
    img.onload = function() {
      const { width: imgWidth, height: imgHeight } = img;
      // 图片加载成功
      const canvas = document.createElement('canvas');
      canvas.width = imgWidth;
      canvas.height = imgHeight;
      // 绘制图片
      const ctx = canvas.getContext('2d') as CanvasRenderingContext2D;
      ctx.drawImage(img, 0, 0, imgWidth, imgHeight);
      ctx.save();
      // 转base64
      const dataURL = canvas.toDataURL(`image/${suffix}`);
      // 获取文件名称
      const imgName = (+new Date()).toString();
      if (navigator.msSaveBlob) {
        const blobObj = new Blob([dataURL]);
        navigator.msSaveBlob(blobObj, imgName);
      } else {
        const a = document.createElement('a');
        a.href = dataURL;
        a.download = imgName;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
      }
      resolve(dataURL);
    };
    // 图片加载失败
    img.onerror = reject;
    // 判断图片是否是base64
    if (imgSrc.substr(0, 4) !== 'data') {
      img.crossOrigin = 'Anonymous';
    }
    img.src = imgSrc;
  });
};

/**
 * 防抖
 */
const debounceFunc = function() {
  let debounce: any = null;
  return function(fn: any, time: number) {
    if (debounce) {
      clearTimeout(debounce);
    }
    debounce = setTimeout(() => {
      fn();
    }, time);
  };
};
export const debounce = debounceFunc();

/**
 * 节流
 */
function throttleFunc() {
  // 时间池
  let eventPool: any = [];
  // 上次时间点
  let lastTime = 0;
  // 执行序号
  let eventIndex = -1;
  // 顺序执行循环
  let interval: any = null;
  // 返回执行函数
  return function(fn: any, delay: number) {
    let crtTime = new Date().getTime();
    if (lastTime && delay + lastTime > crtTime) return;
    eventPool.push(crtTime);
    lastTime = crtTime;
    if (interval) return;
    fn();
    interval = setInterval(() => {
      if (eventPool.length && eventPool[++eventIndex]) {
        fn();
      } else {
        clearInterval(interval);
        interval = null;
      }
    }, delay);
  };
}
export const throttle = throttleFunc();
