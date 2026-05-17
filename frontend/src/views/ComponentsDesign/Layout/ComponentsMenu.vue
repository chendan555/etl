<template>
  <div class="comp">
    <div class="comp-list">
      <div
        class="comp-list-item"
        v-for="(item) in components"
        v-draggable="item"
        :key="item.key"
      >{{item.name}}</div>
      <div
        class="comp-list-item"
        v-for="(item,index) in customComponents"
        v-draggable="item"
        :key="index"
      >{{item.name}}</div>
    </div>
  </div>
</template>
<script>
import { components, customComponents } from '../components';
export default {
  name: 'ComponentsMenu',
  data() {
    return {
      components: components,
      customComponents: customComponents
    };
  }
};
</script>
<style lang="scss" scoped>
.comp {
  .comp-list {
    height: 100%;
    overflow-y: scroll;
    width: 240px;
    border-right: 1px solid #808353;
    .comp-list-item {
      height: 30px;
      line-height: 30px;
      text-align: center;
    }
  }
}
</style>

