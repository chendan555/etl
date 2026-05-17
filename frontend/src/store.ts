import Vue from 'vue';
import Vuex from 'vuex';

Vue.use(Vuex);

let rootPath = '';
let pageName = window.location.hash.slice(2);
let receiveRouter = ['DataCenterN', 'DataReceive', 'MainReceive', 'ReceiveConfig', 'ReceiveRecords', 'Task'];
let processRouter = ['ComponentsManage', 'DealResult', 'MainProcess', 'PlanManage', 'ProjectManage', 'caseBank'];
if (pageName && receiveRouter.includes(pageName)) {
  // sessionStorage.setItem('rootPath', 'receive');
  rootPath = 'receive';
}
if (pageName && processRouter.includes(pageName)) {
  // sessionStorage.setItem('rootPath', 'process');
  rootPath = 'process';
}

export default new Vuex.Store({
  state: {
    rootPath,
    modelMenu: [],
    serverSourceList: [
      {
        id: 1,
        sourceName: '试验数据中心'
      },
      {
        id: 2,
        sourceName: '大数据中心'
      },
      {
        id: 3,
        sourceName: '全数字仿真平台'
      },
      {
        id: 4,
        sourceName: '外场试验数据'
      }
    ],
    nodeData: {},
    database: {},
    schema: {},
    nodeId: '',
    transformationId: '',
    taskGroupName: '',
    path: '',
    dimensionList: [
      { label: '完整性', prop: 'wzx', value: '1' },
      { label: '一致性', prop: 'yzx', value: '2' },
      { label: '唯一性', prop: 'wyx', value: '3' },
      { label: '有效性', prop: 'yxx', value: '4' },
      { label: '准确性', prop: 'zqx', value: '5' },
      { label: '及时性', prop: 'jsx', value: '6' }
    ],
    navTree: [], // 导航菜单树
    menuRouteLoaded: false, // 菜单和路由是否已经加载
    databaseType: [],
    cancelToken: <any>[]
  },
  mutations: {
    SET_CANCELTOKEN: (state, { c, config }) => {
      state.cancelToken.push({
        name: config.url,
        ct: c
      });
    },
    DELETE_CANCELTOKEN: (state, config) => {
      let ctIndex = null;
      state.cancelToken.find((ele: any, index: number) => {
        if (ele.name === config.url) {
          ctIndex = index;
        }
      });
      if (ctIndex !== null) {
        state.cancelToken.splice(ctIndex, 1);
      }
    },
    SET_DATABASETYPE: (state, dbType) => {
      state.databaseType = dbType;
    },
    SET_MODEL_MENU: (state, modelMenu) => {
      state.modelMenu = modelMenu;
    },
    SET_SCHEMA: (state, schema) => {
      state.schema = schema;
    },
    SET_DATABASE: (state, database) => {
      state.database = database;
    },
    SET_NODEDATA: (state, nodeData) => {
      state.nodeData = nodeData;
    },
    SET_NODEID: (state, nodeId) => {
      state.nodeId = nodeId;
    },
    SET_TRANSID: (state, id) => {
      state.transformationId = id;
    },
    SET_TASKNAME: (state, name) => {
      state.taskGroupName = name;
    },
    SET_PATH: (state, name) => {
      state.path = name;
    },
    setNavTree(state, navTree) {
      // 设置导航菜单树
      state.navTree = navTree;
    },
    setRootPath(state, rootPath) {
      // 设置导航菜单树
      state.rootPath = rootPath;
    },
    menuRouteLoaded(state, menuRouteLoaded) {
      // 改变菜单和路由的加载状态
      state.menuRouteLoaded = menuRouteLoaded;
    }
  },
  actions: {
    deleteCancelToken: ({ commit }, c) => {
      commit('DELETE_CANCELTOKEN', c);
    },
    setCancelToken: ({ commit }, c) => {
      commit('SET_CANCELTOKEN', c);
    },
    setDatabaseType: ({ commit }, dbType) => {
      commit('SET_DATABASETYPE', dbType);
    },
    setNodeData: ({ commit }, nodeData) => {
      commit('SET_NODEDATA', nodeData);
    },
    setNodeId: ({ commit }, nodeId) => {
      commit('SET_NODEID', nodeId);
    },
    setTransId: ({ commit }, id) => {
      commit('SET_TRANSID', id);
    },
    setTaskName: ({ commit }, name) => {
      commit('SET_TASKNAME', name);
    },
    setPath: ({ commit }, name) => {
      commit('SET_PATH', name);
    },
    setDatabase: ({ commit }, database) => {
      commit('SET_DATABASE', database);
    },
    setSchema: ({ commit }, schema) => {
      commit('SET_SCHEMA', schema);
    },
    setModelMenu: ({ commit }, modelMenu) => {
      commit('SET_MODEL_MENU', modelMenu);
    }
  }
});
