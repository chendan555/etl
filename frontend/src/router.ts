import Vue from 'vue';
import Router from 'vue-router';
import store from '@/store';

Vue.use(Router);

let staticRoutes = [
  {
    path: '/',
    hidden: true,
    redirect: '/DataCenterN'
  },
  // {
  //   path: '/receive/MainReceive',
  //   name: 'MainReceive',
  //   meta: {
  //     title: '首页'
  //   },
  //   component: () => import('@/views/MainReceive/index.vue'),
  //   hidden: true
  // },
  // {
  //   path: '/process/MainReceive',
  //   name: 'MainReceive',
  //   meta: {
  //     title: '首页'
  //   },
  //   hidden: true,
  //   component: () => import('@/views/MainReceive/index.vue')
  // }
];

let receiveRouter = <Array<string>>[];
let processRouter = <Array<string>>[];
// 加载菜单模块
function setRoute() {
  const req = require.context('@/views', true, /\index.vue$/);
  const eleMap = req.keys().map(req);
  let navPageList = <any>[];
  eleMap.forEach((page: any) => {
    // extendOptions为vue+ts格式组件信息所在属性
    if (page.default.hasOwnProperty('meta') || (page.default.hasOwnProperty('extendOptions') && page.default.extendOptions.hasOwnProperty('meta'))) {
      let name = page.default.hasOwnProperty('extendOptions') ? page.default.extendOptions.name : page.default.name;
      let meta = page.default.hasOwnProperty('extendOptions') ? page.default.extendOptions.meta : page.default.meta;
      navPageList.push({
        path: `/${name}`,
        name: `${name}`,
        meta: meta,
        component: () => import(`@/views/${name}/index.vue`)
      });
      // 获取引接和处理子系统各自的模块
      if (meta && meta.belongTo === 'receive') {
        receiveRouter.push(name);
      }
      if (meta && meta.belongTo === 'process') {
        processRouter.push(name);
      }
    }
  });
  navPageList.sort((a: any, b: any) => Number(a.meta.pageIndex || 0) - Number(b.meta.pageIndex || 0));
  staticRoutes = staticRoutes.concat(navPageList);
  // 保存菜单树
  store.commit('setNavTree', staticRoutes);
  return staticRoutes;
}

const router = new Router({
  routes: setRoute()
});

router.beforeEach((to, from, next) => {
  if (to.name && receiveRouter.includes(to.name)) {
    store.commit('setRootPath', 'receive');
  }
  if (to.name && processRouter.includes(to.name)) {
    store.commit('setRootPath', 'process');
  }
  setTimeout(() => {
    next();
  });
});

/* router.beforeEach((to, from, next) => {
  const token = sessionStorage.getItem('token');
  // console.log(pathArr);
  if (to.path.includes('login')) {
    // 如果是访问登录界面，如果用户会话信息存在，代表已登录过，跳转到主页
    let pathArr = to.path.split('/');
    sessionStorage.setItem('rootPath', pathArr[1]);
    if (token) {
      if (pathArr[1] === 'receive') {
        next({ path: `/MainReceive` });
      } else {
        next({ path: '/MainProcess' });
      }
    } else {
      next();
    }
  } else if (!token) {
    // 如果访问非登录界面，且户会话信息不存在，代表未登录，则跳转到登录界面
    let target: any = staticRoutes.find(rt => rt.path === to.path);
    if (target) {
      next({ path: `/${target.meta.belongTo}/login` });
    } else {
      alert('请输入正确地址！');
    }
  } else {
    if (!store.state.menuRouteLoaded) {
      addDynamicMenuAndRoutes();
      next({
        ...to,
        replace: true
      });
    } else {
      next();
    }
  }
}); */

// 界面中按钮控制
// router.afterEach(to => {
//   const { buttonList } = to.meta;
//   if (buttonList && buttonList.length) {
//     buttonList.forEach((item: any) => {
//       if (item.value) {
//         // value代表按钮Id
//         setTimeout(() => {
//           let curDom = document.getElementById(`${item.value}`);
//           curDom && (curDom.style.display = "inline-block");
//         }, 1000);
//       }
//     });
//   }
// });

/**
 * 加载动态菜单和路由
 */
function addDynamicMenuAndRoutes() {
  // const roleAuthList = JSON.parse(sessionStorage.getItem("roleAuth") as any);
  // if (!roleAuthList) return;
  // // 添加动态路由
  // let dynamicRoutes = addDynamicRoutes(roleAuthList);
  // // const routes = dynamicRoutes.concat(staticRoutes);
  // 非动态数据
  const routes = staticRoutes;
  // console.log("动态匹配路由：", dynamicRoutes);
  (router as any).options.routes = routes;
  (router as any).matcher = (new Router() as any).matcher; // addRoutes方法仅仅是帮注入新的路由，并不会剔除其它路由（导致提示name重复),避免此问题在使用之前，将新的路由传递给matcher
  router.addRoutes((router as any).options.routes);
  // 保存加载状态
  store.commit('menuRouteLoaded', true);
  // 保存菜单树
  store.commit('setNavTree', routes);
}

/**
 * 添加动态(菜单)路由
 * @param {*} authList 菜单列表
 * @param {*} routes 递归创建的动态(菜单)路由
 */
// function addDynamicRoutes(authList: any, routes: any = []) {
//   if (authList.length) {
//     routes = authList.map((item: any) => {
//       return {
//         path: item.url,
//         name: item.title,
//         meta: {
//           title: item.name,
//           buttonList: Array.isArray(item.buttonList) ? item.buttonList.filter((item: any) => item.checked) : [] // 过滤出该用户当前路由具有权限的按钮
//         },
//         hidden: item.hidden == "true" ? true : false, // 控制页面不在导航栏展示
//         redirect: item.redirect ? item.redirect : null,
//         // 动态加载vue组件,这里要求vue组件须按照路径存储,如组件路径是"@/views/config/LabelManager.vue",则值应为"/config/LabelManager",否则组件加载不到
//         component: () => import(`@/views${item.component}.vue`),
//         children: item.children && item.children.length ? addDynamicRoutes(item.children) : []
//       };
//     });
//   }
//   return routes;
// }

export default router;
