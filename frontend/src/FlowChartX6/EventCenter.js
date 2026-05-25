/* eslint-disable guard-for-in */
/* eslint-disable no-param-reassign */

/**
 * 数据处理流程画布事件中转中心文件
 */

function Event() {
  this.clientList = {};
}

Event.prototype = {
  on(key, fn) {
    if (!this.clientList[key]) {
      this.clientList[key] = [];
    }
    this.clientList[key].push(fn);
  },
  emit(...args) {
    const key = Array.prototype.shift.call(args);
    const fns = this.clientList[key];
    if (fns && fns.length !== 0) {
      fns.forEach(fn => {
        fn.apply(this, args);
      });
    }
    return false;
  },
  remove(key, fn) {
    const fns = this.clientList[key];
    if (fns) {
      if (!fn) {
        fns.length = 0;
      } else {
        for (let l = fns.length - 1; l >= 0; l -= 1) {
          const func = fns[l];
          if (func === fn) {
            fns.splice(l, 1);
          }
        }
      }
    }
    return false;
  },
};

export const EventCenter = new Event();

export default function extendsEvent(obj) {
  const eventInstance = new Event();
  for (const key in eventInstance) {
    obj[key] = eventInstance[key];
  }
}
