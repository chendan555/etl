const path = require('path');
const webpackConfig = require('./webpack.config');

function resolve(dir) {
  return path.join(__dirname, dir);
}

const apiUrl = '127.0.0.1';

module.exports = {
  publicPath: './',
  lintOnSave: false,
  devServer: {
    proxy: {
      '/v1/center': {
        target: `http://${apiUrl}:9056`,
        changeOrigin: true
      },
      '/v1/auth': {
        target: `http://${apiUrl}:9056`,
        changeOrigin: true
      },
      '/api': {
        target: `http://${apiUrl}:9056`,
        changeOrigin: true
      },
      '/page': {
        target: `http://${apiUrl}:9056`,
        changeOrigin: true
      }
    },
    port: 8689,
    inline: false,
    overlay: {
      warning: false,
      errors: false
    }
  },
  // css在所有环境下，都不单独打包为文件。这样是为了保证最小引入（只引入js）
  css: {
    extract: false
  },
  configureWebpack: webpackConfig,
  chainWebpack: config => {
    config.resolve.alias.set('@', resolve('src'));
    // 一个规则里的基础loader，svg是个基础loader
    const svgRule = config.module.rule('svg');
    // 清除已有的所有loader。不然接下来的loader会附加再该规则现有的loader之后
    svgRule.uses.clear();

    // 添加要替换的loader
    svgRule
      .use('svg-sprite-loader')
      .loader('svg-sprite-loader')
      .options({
        symbolId: 'icon-[name]',
        includle: ['./src/icons/svg'] // svg文件位置
      });
  }
};
