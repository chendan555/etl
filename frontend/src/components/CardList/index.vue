<template>
  <!-- 卡片展示列表 -->
  <div class="card-list-wrapper">
    <div
      class="card-list-content"
      v-for="(item, index) in listData"
      :key="index"
      @click="handleClick(item, 'handleSelf')"
    >
      <div class="card-header">
        <div class="name">{{item.databaseName}}</div>
        <div class="btn">
          <el-tooltip content="编辑" class="edit">
            <span style="color:#E26853;font-size:14px;" @click="handleClick(item, 'handleEdit')">
              <svg-icon icon-class="edit"/>
            </span>
          </el-tooltip>
          <el-tooltip content="删除" class="del">
            <span style="color:#E26853;font-size:14px;" @click="handleClick(item, 'handleDel')">
              <svg-icon icon-class="del"/>
            </span>
          </el-tooltip>
          <!-- <el-tooltip content="分配" class="allot">
            <span style="color:#E26853;font-size:14px;" @click="handleClick(item, 'handleAllot')">
              <svg-icon icon-class="del"/>
            </span>
          </el-tooltip>-->
        </div>
      </div>
      <div class="card-body">
        <div class="item">创建人：{{ item.createName }}</div>
        <div class="item">类型：{{ item.idDatabaseTypeName }}</div>
        <div class="item">关联项目数：{{ item.countNum }}</div>
        <div class="item">创建时间：{{ item.createDate }}</div>
        <div class="item">更新时间：{{ item.updateDate }}</div>
      </div>
    </div>
  </div>
</template>

<script>
/**
 *
 * Create by Leo
 * 基础组件
 * 基础卡片布局组件
 * 左右两栏式布局
 * 坐侧区域可控制显隐
 * 并减少一定的代码量
 * 统一设置弹框的配置项、确认按钮、取消按钮、关闭按钮等信息
 *
 */
export default {
  props: {
    /**
     * 配置项
     */
    listData: {
      type: Array,
      // default: () => {}
    }
  },
  data() {
    return {};
  },
  methods: {
    /**
     * 点击事件
     * @param item
     * @param type
     */
    handleClick(item, type) {
      this.$emit(type, item);
    }
  }
};
</script>

<style lang="scss" scoped>
@import '@/styles/theme.scss';
.card-list-wrapper {
  width: 100%;
  display: grid;
  justify-items: center;
  grid-template-columns: 1fr 1fr 1fr 1fr;
  grid-gap: 20px;
  .card-list-content {
    width: 100%;
    cursor: pointer;
    .card-header {
      position: relative;
      background: $cardTtlBgClr;
      .name {
        padding: 10px;
      }
      .btn {
        position: absolute;
        right: 0;
        top: 10px;
        > span {
          margin-right: 10px;
          cursor: pointer;
        }
      }
    }
    .card-body {
      padding: 20px;
      background: $cardContBgClr;
      .item {
        line-height: 2;
        font-size: 12px;
      }
    }
  }
}
</style>

