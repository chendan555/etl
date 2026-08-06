/**
 *
 * 数据处理流程画布模型文件
 * 定义画布中使用各类方法
 *
 */

let FlowChartJson = {
  nodes: [],
  endpoints: [],
  edges: [],
  head: ''
};

export default {
  /**
   * 获取数据
   */
  getData() {
    return FlowChartJson;
  },
  /**
   * 设置数据
   * @param data
   */
  setData(data) {
    FlowChartJson = data;
    return FlowChartJson;
  },
  /**
   * 获取节点信息
   */
  getEndpoints() {
    return FlowChartJson.endpoints;
  },
  /**
   * 添加节点
   * @param nodeData
   */
  addNode(nodeData) {
    // 兼容两种数据格式
    let props = nodeData.data && nodeData.data.properties ? nodeData.data.properties : nodeData.properties;
    if (props && !props.ruleForm) {
      props.ruleForm = {
        stepName: nodeData.data ? nodeData.data.text : nodeData.text
      };
    }
    FlowChartJson.nodes.push(nodeData);
    // console.log(FlowChartJson);
  },
  /**
   * 根据节点id移除节点
   * @param nodeId
   */
  removeNodeByNodeId(nodeId) {
    FlowChartJson.nodes = FlowChartJson.nodes.filter(node => node.id !== nodeId);
  },
  /**
   * 根据节点id获取节点信息
   * @param nodeId
   */
  getNodeDataByNodeId(nodeId) {
    return FlowChartJson.nodes.find(n => n.id === nodeId);
  },

  /**
   * 通过id获取来源源节点
   * @param {*} nodeId
   */
  getSourceNodeIdsByNodeId(nodeId) {
    let targets = this.getNodeDataByNodeId(nodeId).points.targets;
    let edges = this.getEdgesByPointIds(targets);
    let sources = edges.map(edge => {
      return edge.split('&&')[0];
    });
    /**
     * 遍历节点树
     */
    const data = [];
    FlowChartJson.nodes.forEach(node => {
      sources.forEach(source => {
        node.points.sources.forEach(itemSource => {
          if (itemSource === source) {
            data.push(node.id);
          }
        });
      });
    });
    return data;
  },
  /**
   * 添加边缘
   * @param {*} edge 
   */
  addEdge(edge) {
    FlowChartJson.edges.push(edge);
  },
  /**
   * 移除边缘
   * @param {*} edge 
   */
  removeEdge(edge) {
    FlowChartJson.edges = FlowChartJson.edges.filter(item => item !== edge);
  },
  /**
   * 添加边缘
   * @param {*} ids 
   */
  getEdgesByPointIds(ids) {
    const data = [];
    FlowChartJson.edges.forEach(edge => {
      ids.forEach(id => {
        if (edge.indexOf(id) > -1) {
          data.push(edge);
        }
      });
    });
    return data;
  },
  /**
   * 添加边缘
   * @param  ids 
   */
  removeEdgesByPointIds(ids) {
    ids.forEach(id => {
      FlowChartJson.edges = FlowChartJson.edges.filter(edge => edge.indexOf(id) === -1);
    });
  },
  /**
   * 添加
   * @param  point 
   */
  addEndpoint(point) {
    FlowChartJson.endpoints.push(point);
  },
  /**
   * 获取
   * @param  ids 
   */
  getEndpointsByPointIds(ids) {
    return [
      ...FlowChartJson.endpoints.filter(point => {
        if (ids.indexOf(point.id) > -1) {
          return true;
        }
        return false;
      })
    ];
  },
  /**
   * 移除
   * @param ids 
   */
  removeEndpointsByPointIds(ids) {
    ids.forEach(id => {
      FlowChartJson.endpoints = FlowChartJson.endpoints.filter(point => point.id !== id);
    });
  },
  /**
   * 修改节点位置
   * @param nodeId
   * @param newPosition
   */
  changeNodePos(nodeId, newPosition) {
    const node = FlowChartJson.nodes.find(n => n.id === nodeId);
    node.position = newPosition;
  },
  /**
   * 修改节点值
   * @param nodeId
   * @param value
   */
  changeNodeValue(nodeId, value) {
    const node = FlowChartJson.nodes.find(n => n.id === nodeId);
    node.data.value = value;
  },
  /**
   * 获取头部信息
   */
  getHead() {
    return FlowChartJson.head;
  }
};
