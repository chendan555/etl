<template>
  <div class="frameDemo">
    <div class="headerTitle">
      <p class="name">
        <img :src="require('@/assets/mainPage/'+title.imgUrl+'.png')" alt>
        <span>{{title.name}}</span>
      </p>
      <div class="handle">
        <slot name="handle"></slot>
      </div>
    </div>
    <div class="content">
      <slot name="content"></slot>
    </div>
  </div>
</template>
<script>
export default {
  name: 'frameDemo',
  props: ['title']
};
</script>
<style lang="scss" scoped>
@import '@/styles/theme.scss';
.frameDemo {
  width: 100%;
  height: 100%;
  .headerTitle {
    width: 100%;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    background: $currentRow;
    padding: 5px;
    p {
      display: flex;
      img {
        margin-right: 5px;
      }
    }
    .handle {
      height: 100%;
      line-height: 30px;
    }
  }
  .content {
    width: 100%;
    height: calc(100% - 40px);
    background: $mainPageItemBgClr;
    padding: 5px;
  }
}
</style>
