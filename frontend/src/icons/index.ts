/**
 *
 * svg图标组件文件
 * 定义svg信息
 * 包括样式、功能等
 *
 */

//定义全局svg组件
import Vue from 'vue';
import svgIcon from '@/components/SvgIcon/index.vue';
Vue.component('svg-icon', svgIcon);

// 获取本地svg图片库
const req = require.context('./svg', false, /\.svg$/);
// 加载svg图片函数
const requireAll = (requireContext: any) => requireContext.keys().map(requireContext);
// 加载svg图片
requireAll(req);
