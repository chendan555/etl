<template>
  <!-- width: innerConfig.whiteSpace == 'normal'?innerConfig.w+'px':'auto',height: innerConfig.whiteSpace == 'normal'?innerConfig.h+'px':'auto', -->

  <div
    class="layoutItem"
    :class="[{'fixed': moving},{'choosed': config.choosed}]"
    :style="{
        width: innerConfig.w+'px',
        height: innerConfig.h+'px',
        top: (!moving ? innerConfig.y : y) +'px',
        left: (!moving ? innerConfig.x : x) +'px'
      }"
  >
    <slot></slot>
    <div
      class="drag-point"
      v-for="(i, index) in 8"
      :class="'drag-point' + i"
      :key="index + '0'"
      @mousedown.self.stop.exact="start($event, i)"
    ></div>
    <!-- v-throttle="{
      'time': 20, 
      'events': [
    {name: 'mousedown', handle: ($event) => start($event, i)}]}"-->
    <!-- @mousedown.self.stop.exact="mousedownHandler($event)"   优化表格全选方式，修改为只能点击外侧border选中 -->

    <!-- v-if="config.type == 'table'" -->
    <div
      class="border"
      v-for="(i, index) in 4"
      :class="'border' + i"
      draggable="true"
      :key="index + '1'"
      @dragstart="dragStart"
      @dragend="dragEnd"
      @mousedown.self.stop.exact="mousedownHandler($event)"
    ></div>
  </div>
</template>

<script>
// import { copyObj } from "@/utils";
var observer;
export default {
  name: "layoutItem",
  components: {},
  props: {
    config: {
      required: true,
      type: Object
    },
    choosedLayout: {
      type: Array
    }
  },
  data() {
    return {
      moving: false,
      startX: null,
      startY: null,
      lastX: null,
      lastY: null,
      moveType: null,
      x: null,
      y: null,
      draging: false,
      dragStartEle: "",
      oldEleEdit: "",
      showBorder: false
    };
  },
  methods: {
    // 拖拽大小
    start(e, i) {
      this.oldEleEdit = JSON.parse(JSON.stringify(this.choosedLayout));
      this.$bus.$emit("changeMovingEvt", true, e, i);
    },
    move(e) {
      if (!this.moving) {
        return;
      }
      let moveX = 0,
        moveY = 0;
      let allHeight = 1;
      let flagNum = 0;
      if (this.moveType == 1) {
        //拖拽左上
        moveX = -(e.pageX - this.lastX);
        moveY = -(e.pageY - this.lastY);
      } else if (this.moveType == 2) {
        //拖拽上中
        moveY = -(e.pageY - this.lastY);
      } else if (this.moveType == 3) {
        //拖拽右上
        moveX = e.pageX - this.lastX;
        moveY = -(e.pageY - this.lastY);
      } else if (this.moveType == 4) {
        //拖拽右中
        moveX = e.pageX - this.lastX;
      } else if (this.moveType == 5) {
        //拖拽右下
        moveX = e.pageX - this.lastX;
        moveY = e.pageY - this.lastY;
      } else if (this.moveType == 6) {
        //拖拽下中
        moveY = e.pageY - this.lastY;
      } else if (this.moveType == 7) {
        //拖拽左下
        moveX = -(e.pageX - this.lastX);
        moveY = e.pageY - this.lastY;
      } else if (this.moveType == 8) {
        //拖拽左中
        moveX = -(e.pageX - this.lastX);
      }
      if (this.moveType == 1 || this.moveType == 8 || this.moveType == 7) {
        this.x -= moveX;
      }

      //由于表格有最小高度，需要特殊处理
      if (this.config.type == "table" || this.config.type == "tableList") {
        this.config.child.map((tr, trIndex) => {
          //处理某一行为30，其他行大于30的情况
          let flag = false;
          tr.map((td, tdIndex) => {
            td.userInputConfig.w += moveX / tr.length;
            if (
              td.userInputConfig.h +
                moveY / (this.config.child.length - flagNum) <=
              30
            ) {
              td.userInputConfig.h = 30;
              if (!flag) {
                flagNum++;
                flag = true;
              }
            } else {
              td.userInputConfig.h +=
                moveY / (this.config.child.length - flagNum);
            }
          });
        });
        this.config.child.map(tr => {
          allHeight += tr[0].userInputConfig.h + 1;
          //处理某一行为30，其他行大于30的情况
        });
        //表格单元格最小高度为30，计算表格的最小高度
        // let flagHeight = this.config.child.length * 30;
        // if (allHeight > flagHeight) {
        if (this.moveType == 1 || this.moveType == 2 || this.moveType == 3) {
          if (this.config.child.length >= flagNum) {
            this.y -= moveY;
          }
        }
        this.innerConfig.h = allHeight;
      } else {
        if (this.moveType == 1 || this.moveType == 2 || this.moveType == 3) {
          this.y -= moveY;
        }
        this.innerConfig.h = this.innerConfig.h * 1;
        this.innerConfig.h += moveY;
        this.innerConfig.w = this.innerConfig.w * 1;
        this.innerConfig.w += moveX;
      }
      this.$nextTick(() => {
        if (
          this.$el.querySelector(".element-item").getBoundingClientRect()
            .height > this.innerConfig.h
        ) {
          if (this.moveType === 4) {
            this.innerConfig.h -= moveY;
          }
        }
      });
      let isTable =
        this.config.type === "table" || this.config.type === "tableList";
      if ((isTable && this.config.child.length >= flagNum) || !isTable) {
        this.lastY = e.pageY;
      }
      this.lastX = e.pageX;
    },
    end(e) {
      this.moving = false;
      this.$bus.$emit("changeMovingEvt", false, e);
      if (this.oldEleEdit) {
        this.$bus.$emit(
          "setHistory",
          this.choosedLayout,
          "update",
          null,
          null,
          false,
          this.oldEleEdit
        );
        this.oldEleEdit = "";
      }
    },
    //拖拽位置
    dragStart(e) {
      this.draging = true;
      this.dragStartEle = JSON.parse(JSON.stringify(this.choosedLayout));

      // this.$bus.$emit("setHistory", this.choosedLayout, "move");
      // 画板中开始拖拽时清空定位属性
      this.$bus.$emit("clearPositionAlign");
      this.changeDraging(true, e);
      // this.$bus.$emit("changeDragingEvt", true, e);
      // this.$bus.$emit("changeDragingEvtStart");
    },
    dragOver(e) {
      if (!this.draging) {
        return;
      }

      this.innerConfig.x = this.innerConfig.x * 1;
      this.innerConfig.y = this.innerConfig.y * 1;
      this.innerConfig.x += e.pageX - this.lastX;
      this.innerConfig.y += e.pageY - this.lastY;

      this.lastX = e.pageX;
      this.lastY = e.pageY;
    },
    dragEnd(e) {
      // let canvasWidth = parseInt(
      //   getComputedStyle(document.getElementsByClassName("layout-box")[0]).width
      // );
      this.draging = false;
      this.$bus.$emit("changeDragingEvt", false);
      this.$bus.$emit(
        "setHistory",
        this.choosedLayout,
        "move",
        null,
        null,
        false,
        this.dragStartEle
      );
    },
    //重置大小
    resize() {},
    mousedownHandler(e) {
      //设置表格移动边框适应表格高度 -Leo
      let parentDom = e.target.parentNode;
      let tableHei = getComputedStyle(
        parentDom.getElementsByClassName("element-item")[0]
      ).height;
      parentDom.style.height = tableHei;
      // let tableWid = getComputedStyle(parentDom.getElementsByClassName('element-item')[0]).width;
      // parentDom.style.width = tableWid;
      // 初始化当前定位
      if (!this.config.choosed) {
        this.$emit("dragMove");
      }
    },
    // 拖拽和大小状态改变
    changeMoving(status, e, i) {
      this.$bus.$emit("clearPositionAlign");
      let canvasDom = document.querySelector(".canvas-box");
      if (this.config.choosed) {
        this.moving = status;
        if (status) {
          // let pos = e.path[1].getBoundingClientRect()
          let pos = this.$el.getBoundingClientRect();
          this.moveType = i;
          this.startX = e.pageX;
          this.startY = e.pageY;
          // this.startX = pos.left;
          // this.startY = pos.top;
          this.lastX = e.pageX;
          this.lastY = e.pageY;
          this.y = pos.top - 156 + canvasDom.scrollTop;
          this.x = pos.left - 270 + canvasDom.scrollLeft;
        } else {
          let moveX = this.lastX - this.startX;
          let moveY = this.lastY - this.startY;
          if (this.moveType == 1 || this.moveType == 8 || this.moveType == 7) {
            this.innerConfig.x = this.innerConfig.x * 1;
            this.innerConfig.x += moveX;
          }
          if (this.moveType == 1 || this.moveType == 2 || this.moveType == 3) {
            this.innerConfig.y = this.innerConfig.y * 1;
            this.innerConfig.y += moveY;
          }
          this.startX = null;
          this.startY = null;
          this.lastX = null;
          this.lastY = null;
          this.moveType = null;
          this.x = null;
          this.y = null;
        }
      }
    },
    changeDraging(status, e) {
      if (this.config.choosed) {
        this.draging = status;
        if (status) {
          this.lastX = e.pageX;
          this.lastY = e.pageY;
        } else {
          this.lastX = null;
          this.lastY = null;
        }
      }
    }
  },

  computed: {
    innerConfig() {
      // 页面完成加载设置表单外框高度 -Leo
      if (
        this.config.type == "tableList" &&
        this.config.userInputConfig.h == 180
      ) {
        this.config.userInputConfig.h = 185;
      }
      return this.config.userInputConfig;
    }
  },
  activated() {}
};
</script>

<style lang='scss' scoped>
.layoutItem {
  // background: #fff;
  &.fixed {
    // position: fixed;
  }
  &.choosed {
    box-sizing: content-box;
    // border: 1.6px dashed rgb(14, 245, 45);
    .drag-point {
      display: block;
    }
    .border {
      &:before {
        display: block;
      }
    }
  }
  // position: absolute;
  width: 80px;
  height: 30px;
  // &:before {
  //   // z-index: -1;
  //   content: '';
  //   position: absolute;
  //   left: -6px;
  //   top: -6px;
  //   bottom: -6px;
  //   right: -6px;
  //   background-color: rgba(153, 153, 153, 0.3);
  // }
  .drag-point {
    display: none;
    padding: 0;
    margin: 0;
    width: 6px;
    height: 6px;
    position: absolute;
    border: 1px solid #000;
    // border-radius: 50%;
    cursor: w-resize;
    box-sizing: border-box;
  }
  .drag-point1 {
    left: -4px;
    top: -4px;
    z-index: 2;
    cursor: se-resize;
  }
  .drag-point2 {
    left: 50%;
    margin-left: -3px;
    top: -4px;
    z-index: 2;

    cursor: s-resize;
  }
  .drag-point3 {
    right: -4px;
    top: -4px;
    z-index: 2;

    cursor: sw-resize;
  }
  .drag-point4 {
    right: -4px;
    top: 50%;
    z-index: 2;

    margin-top: -3px;
  }
  .drag-point5 {
    right: -4px;
    bottom: -4px;
    z-index: 2;

    cursor: se-resize;
  }
  .drag-point6 {
    left: 50%;
    margin-left: -3px;
    bottom: -4px;
    z-index: 2;

    cursor: s-resize;
  }
  .drag-point7 {
    left: -4px;
    bottom: -4px;
    z-index: 2;

    cursor: sw-resize;
  }
  .drag-point8 {
    left: -4px;
    top: 50%;
    z-index: 2;

    margin-top: -3px;
  }
  .border {
    position: absolute;
    width: 6px;
    height: 6px;
    cursor: move;
    &:before {
      background: rgb(14, 245, 45);
      content: "";
      position: absolute;
      display: none;
    }
  }
  .border1 {
    left: -6px;
    top: 0;
    height: 100%;
    &:before {
      top: 0;
      bottom: 0;
      width: 1px;
      right: 0px;
    }
  }
  .border3 {
    right: -6px;
    top: 0;
    height: 100%;
    &:before {
      top: 0;
      bottom: 0;
      width: 1px;
      left: 1px;
    }
  }
  .border2 {
    left: 0;
    top: -6px;
    width: 100%;
    &:before {
      left: 0;
      right: 0;
      height: 1px;
      bottom: 0px;
    }
  }
  .border4 {
    left: 0;
    bottom: -6px;
    width: 100%;
    &:before {
      left: 0;
      right: 0;
      height: 1px;
      top: 0px;
    }
  }
}
</style>
