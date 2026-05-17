import Vue from 'vue';
import ElementUI from 'element-ui';
import Echarts from 'echarts';
import 'element-ui/lib/theme-chalk/index.css';
import App from './App.vue';
import router from './router';
import store from './store';
import utils from './utils';
import axios from 'axios';
import API, { dataCenterService } from './api/index';

// import 'kityMinder/lib/kityMinder.css';
// import kityMinder from 'kityMinder';
// Vue.use(kityMinder);

import { draggable, dropable, edit } from './directive/index.js';
Vue.use(draggable);
Vue.use(dropable);
Vue.use(edit);

// 重置样式文件
import '@/styles/common.scss';
import '@/styles/global.scss';
// 覆盖element-ui的样式
import '@/styles/element-ui.scss';

// Mock.js 会替换全局 XMLHttpRequest，与 dom-to-image 等库冲突，已禁用
// require('./mock/index');

import './icons';
import 'echarts-liquidfill';

Vue.prototype.$dataCenterService = dataCenterService;

declare module 'vue/types/vue' {
  interface Vue {
    $utils: any;
    $eventBus: any;
    $echarts: any;
    $API: any;
    $axios: any;
    [key: string]: any;
  }
  interface VueConstructor {
    $meta: any;
  }
}

Vue.prototype.$API = API;
Vue.prototype.$axios = axios;
Vue.prototype.$echarts = Echarts;
Vue.use(ElementUI);

Vue.prototype.$utils = utils;
utils.autoLogin();
/* let userInfo = {
  username: 'chendan',
  relname: '陈丹',
  id: '1234'
}
if (!localStorage.getItem('userDetail')) localStorage.setItem('userDetail', JSON.stringify(userInfo)); */

// eventBus
Vue.prototype.$eventBus = new Vue();

Vue.config.productionTip = false;
new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app');
