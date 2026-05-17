/**
 *
 * 移动、缩放功能模块文件
 * 定义画布移动和缩放功能函数
 *
 */
import instance from './instance';

const panzoom = require('panzoom');

/**
 * 初始化panzoom插件
 */
function init() {
  const mainContainer = instance.getContainer();
  const mainContainerWrap = mainContainer.parentNode;
  const pan = panzoom(mainContainer, {
    smoothScroll: false,
    bounds: true,
    // autocenter: true,
    zoomDoubleClickSpeed: 1,
    minZoom: 0.5,
    maxZoom: 2
  });
  instance.mainContainerWrap = mainContainerWrap;
  instance.pan = pan;
  /**
   * 缩放时设置jsPlumb的缩放比率
   */
  pan.on('zoom', e => {
    const { scale } = e.getTransform();
    instance.setZoom(scale);
  });

  /**
   * 平移时设置鼠标样式
   */
  mainContainerWrap.style.cursor = 'grab';
  /**
   * 鼠标按下时鼠标样式
   */
  mainContainerWrap.addEventListener('mousedown', function wrapMousedown() {
    this.style.cursor = 'grabbing';
    mainContainerWrap.addEventListener('mouseout', function wrapMouseout() {
      this.style.cursor = 'grab';
    });
  });
  /**
   * 鼠标弹起时鼠标样式
   */
  mainContainerWrap.addEventListener('mouseup', function wrapMouseup() {
    this.style.cursor = 'grab';
  });
}
const panZoom = {
  init
};

export default panZoom;
