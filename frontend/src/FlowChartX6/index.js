import extendsEvent, { EventCenter } from './EventCenter';
import editor from './editor';
import model from './model';
import exec, { undo, AddNodeCommand } from './Command';
import contextMenu from './contextMenu';
import { setFlowChartRef } from './editor';

const FlowChartX6 = {
  setContainer(id) {
    const el = document.getElementById(id);
    if (el) el.innerHTML = '';
    this.init();
  },

  clearContainer(id) {
    const el = document.getElementById(id);
    if (el) el.innerHTML = '';
  },

  addNode(position, elId) {
    exec(AddNodeCommand, position, elId);
  },

  undo() {
    undo();
  },

  init() {
    setFlowChartRef(FlowChartX6);
    editor.init();
    contextMenu.init();
  },

  zoomIn() {
    const graph = editor.getGraph();
    if (graph) {
      const zoom = graph.zoom();
      graph.zoom(Math.max(zoom * 0.8, 0.2));
    }
  },

  zoomOut() {
    const graph = editor.getGraph();
    if (graph) {
      const zoom = graph.zoom();
      graph.zoom(Math.min(zoom * 1.2, 3));
    }
  },

  loadData(data) {
    model.setData(data);
    const graph = editor.getGraph();
    if (graph) {
      graph.clearCells();
    }
    editor.render();
  },

  getData() {
    const graph = editor.getGraph();
    if (graph) {
      return model.fromX6Data(graph);
    }
    return model.getData();
  },

  setData(data) {
    return model.setData(data);
  },

  getNodeDataByNodeId(id) {
    return model.getNodeDataByNodeId(id);
  },

  execModel() {
    return editor.execModel();
  },
};

extendsEvent(FlowChartX6);

FlowChartX6.on('contextmenu', (menuInfo) => {
  contextMenu.show(menuInfo);
});

document.addEventListener('click', (ev) => {
  EventCenter.emit('document.click', ev);
});

export default FlowChartX6;
