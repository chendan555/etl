

declare module '*.vue' {
  import Vue from 'vue'
  export default Vue
}

declare module 'echarts' {
  const echarts: any;
  export default echarts
}

declare module '*.js'