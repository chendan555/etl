import Vue from "vue";

/**
 * @description 拖拽保存技术数据和鼠标起始位置
 * @description 使用VUE自定义事件方式全局挂载
 * @param el 事件元素
 * @param binding 事件元素属性
 * @param vnode
 */
export const draggable = Vue.directive("draggable", {
  bind(el, binding, vnode) {
    // 设置事件元素拖拽属性为真
    el.setAttribute("draggable", true);
    // 设置事件元素拖拽起始事件
    el.ondragstart = (event) => {
      let data = binding.value;
      if (binding.modifiers.updatePos) {
        data.updatePos = true;
      }
      if (binding.modifiers.stop) {
        event.stopPropagation();
      }
      if (data !== undefined && data !== null) {
        event.dataTransfer.setData("data", JSON.stringify(data));
      }
    }
  }
})

/**
 * @description 放置传入方法调用
 * @description 使用VUE自定义事件方式全局挂载
 * @param el 事件元素
 * @param binding 事件元素属性
 * @param vnode
 */
export const dropable = Vue.directive("dropable", {
  bind(el, binding, vnode) {
    // 设置事件元素拖拽终止事件
    el.ondragover = (event) => {
      event.preventDefault()
    }
    // 设置事件元素释放事件
    el.ondrop = (event) => {
      if (binding.modifiers.stop) {
        event.stopPropagation();
      }
      try {
        let data = JSON.parse(event.dataTransfer.getData("data"));
        if (data !== undefined || data !== null) {
          binding.value(data, event);
        }
      } catch (e) {

      }

    }
  }
})

/**
 * @description 节点元素编辑事件
 * @description 使用VUE自定义事件方式全局挂载
 * @param el 事件元素
 * @param binding 事件元素属性
 * @param vnode
 */
export const edit = Vue.directive("edit", {
  bind(el, binding, vnode) {
    let value = binding.value.value;
    let event = binding.value.event;
    let disabled = binding.value.disabled;
    let stop = binding.value.hasOwnProperty("stop") ? binding.value.stop : false;
    el.innerText = value;
    if (!disabled) {
      // 设置元素可编辑属性
      el.setAttribute('contenteditable', !disabled);
      el.addEventListener('input', (e) => {
        event(el.innerText, el.innerHTML);
      });
      // 阻止画布监听事件
      el.addEventListener('keyup', (e) => {
        if (stop) {
          e.stopPropagation();
        }
      });
    }
  }
})
