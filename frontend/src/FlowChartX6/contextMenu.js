import { Message } from 'element-ui';
import { EventCenter } from './EventCenter';
import exec, { PasteNodeCommand, RemoveNodeCommand, RemoveConnectorCommand } from './Command';
import editor from './editor';

let ul = null;
let copiedNodeData = '';
let contextMenuPos = {};

class MenuItem {
  constructor(opt) {
    this.text = opt.text;
    this.icon = opt.icon;
    this.role = opt.role;
    this.clickHandle = opt.clickHandle;
    this.init();
  }

  init() {
    const li = document.createElement('li');
    li.className = 'fy_contextMenu_item';
    li.dataset.role = this.role;
    li.innerHTML = `<i class="${this.icon}"></i><span>${this.text}</span>`;
    this.el = li;
    this.el.onclick = (e) => {
      e.stopPropagation();
      this.clickHandle(e);
    };
  }

  getEl() { return this.el; }
}

function hide() {
  ul.style.display = 'none';
}

function generateMenuDivideLine() {
  const divide = document.createElement('div');
  divide.style.cssText = 'height:1px;background:#efefef;';
  ul.appendChild(divide);
}

function generateMenuItem(opt) {
  const item = new MenuItem(opt);
  ul.appendChild(item.getEl());
}

function deleteHandle(comp) {
  if (comp) {
    if (typeof comp === 'object') {
      exec(RemoveConnectorCommand, comp);
    } else if (typeof comp === 'string') {
      exec(RemoveNodeCommand, comp);
    }
  }
}

function generateNodeMenu(currentComponent, currentComponentData) {
  generateMenuItem({
    text: '复制',
    icon: 'el-icon-copy-document',
    role: 'copy',
    clickHandle: () => {
      copiedNodeData = currentComponentData;
      Message.success({ message: '复制成功！' });
    },
  });
  generateMenuItem({
    text: '删除',
    icon: 'el-icon-delete',
    role: 'delete',
    clickHandle: () => deleteHandle(currentComponent),
  });
  generateMenuDivideLine();
  generateMenuItem({
    text: '查看数据',
    icon: 'el-icon-view',
    role: 'view',
    clickHandle: () => editor.emitShowNodeData(currentComponentData),
  });
}

function generateConnectorMenu(currentComponent) {
  generateMenuItem({
    text: '删除',
    icon: 'el-icon-delete',
    role: 'delete',
    clickHandle: () => deleteHandle(currentComponent),
  });
}

function generateBgMenu() {
  generateMenuItem({
    text: '粘贴节点',
    icon: 'el-icon-download',
    role: 'paste',
    clickHandle: () => {
      if (copiedNodeData) {
        exec(PasteNodeCommand, { pageX: contextMenuPos.left, pageY: contextMenuPos.top }, copiedNodeData);
      } else {
        Message({ type: 'warning', message: '请复制节点后再粘贴。' });
      }
    },
  });
}

function show(menuInfo) {
  const { type, position, component, nodeData } = menuInfo;
  ul.innerHTML = '';
  ul.style.top = `${position.top}px`;
  ul.style.left = `${position.left}px`;
  ul.style.display = 'block';
  contextMenuPos = position;

  if (type === 'node') generateNodeMenu(component, nodeData);
  if (type === 'edge') generateConnectorMenu(component);
  if (type === 'blank') generateBgMenu();
}

function init() {
  ul = document.createElement('ul');
  ul.className = 'fy_contextMenu';
  document.body.appendChild(ul);
  ul.onmouseup = hide;

  hide();

  EventCenter.on('document.click', () => {
    hide();
  });
}

export default { init, show };
