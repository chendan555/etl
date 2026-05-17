/**
 * 
 * 数据处理流程画布配置项文件
 * 定义画布中接连接连的线型等内容
 * 
 */

export default {
  DragOptions: { cursor: 'pointer', zIndex: 2000 },
  ConnectionOverlays: [
    [
      'Arrow',
      {
        location: 1,
        visible: true,
        width: 8,
        length: 8,
        id: 'ARROW'
      }
    ]
  ],
  maxConnections: 10,
  ConnectionsDetachable: false
};
