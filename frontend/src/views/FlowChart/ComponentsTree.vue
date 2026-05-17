<template>
  <div class="tabdiv">
    <!-- <div class="adddiv">
      <img src="~@/assets/indexPage/tab_add.png" alt="">
    </div>-->
    <el-tabs type="border-card">
      <el-tab-pane>
        <span slot="label">
          <img src="~@/assets/indexPage/tab_cz.png" alt>
          <!-- <i class="el-icon-s-operation"></i> -->
          操作
        </span>
        <div class="tab_cz_div">
          <div class="search">
            <el-input placeholder="搜索" size="small" v-model="filterText">
              <i slot="prefix" class="el-input__icon el-icon-search"></i>
            </el-input>
          </div>
          <div class="navdiv">
            <el-tree
              class="components-tree"
              :data="data"
              node-key="id"
              :default-expanded-keys="['source','preHandle','sign','learn']"
              icon-class="el-icon-arrow-right"
              :default-expand-all="false"
              :filter-node-method="filterNode"
              :highlight-current="true"
              ref="filterTree"
              :props="defaultProps"
            >
              <!-- :render-content="renderContentFunction" 修改为使用如下树组件模板结构，以控制组件图标存储在前端进行展示，并使节点代码结构化，便于阅读 20210118 -->
              <span class="custom-tree-node" slot-scope="{ node, data }">
                <span
                  :draggable="!(data.children && data.children.length)"
                  @dragstart="dragHandle($event, data)"
                  class="dragNode"
                >
                  <SvgIcon :iconClass="data.icon | getSvgName" style="marginTop: 3px;"></SvgIcon>
                  <!-- <img class="icon" :src="data.icon" alt /> -->
                  <span style="marginLeft: 5px;">{{ node.label }}</span>
                </span>
              </span>
            </el-tree>
          </div>
        </div>
      </el-tab-pane>
      <!-- <el-tab-pane v-if="procType !== 'sjyj'">
        <span slot="label">
          <img src="~@/assets/indexPage/tab_fa.png" alt>
          方案
        </span>
        <div
          class="plan__List"
          v-for="(item,index) in planList"
          :key="index"
          @click="showPlanData(item)"
        >
          <i class="el-icon-document"></i>
          {{item.name}}
        </div>
      </el-tab-pane>-->
    </el-tabs>
  </div>
</template>
<script lang="js">
import Vue from "vue";
import SvgIcon from "@/components/SvgIcon/index";
import API from "@/api/index";
export default Vue.extend({
  components: {SvgIcon},
  props: {
    data: {
      type: Array,
      default: () => {
        return [];
      }
    },
    defaultProps: {
      type: Object,
      default: () => {
        return {
          children: "children",
          label: "text"
        };
      }
    },
    procType: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      filterText: "",
      planList:[]
    };
  },
  filters: {
    getSvgName(icon) {
      if(icon) {
        let startStr = 'ui/images/';
        let endStr = '.svg?';
        let startIndex = icon.indexOf(startStr);
        let endIndex = icon.indexOf(endStr);
        return icon.slice(startIndex+startStr.length, endIndex);
      }else {
        return 'PTHJB'
      }
    }
  },
  mounted() {
    this.getData();
  },
  watch: {
    filterText(val) {
      this.$refs.filterTree.filter(val);
    }
  },
  methods: {
    showPlanData(id){
      this.$emit('showPlanData', id, )
    },
    getData(){
      API.getCreatePlanList({
        pageIndex: 1,
        pageSize: 1000,
        flag: 1
      }).then(res => {
        this.planList = res.data.data.records;
      });
    },
    dragHandle(ev,data) {
      this.$emit("dragHandle", ev, data);
    },
    renderContentFunction(
      h,
      { node, data }
    ) {
      const className = node.expanded
        ? "el-icon-folder-opened"
        : "el-icon-folder";
      const classNameChild = !data.children && data.icon ? data.icon : "";
      return h(
        "div",
        {
          class: { leafNode: !data.children },
          style: {
            height: "38px",
            lineHeight: "38px",
            fontSize: "12px",
            color: "#1b1c23"
          }
        },
        [
          h("div", [
            h(
              "span",
              {
                attrs: {
                  draggable: !data.children,
                  id: data.id
                },
                on: {
                  dragstart: (e) => {
                    if (!data.children) this.dragHandle(e, data);
                  }
                },
                class: "node",
                style: {
                  display: "flex",
                  alignItems: "center",
                  marginTop: "4px",
                  height: "30px",
                  lineHeight: "30px",
                  width: "140px",
                  borderRadius: "4px",
                  position: "relative",
                  outline: "none",
                  border: !data.children ? "1px solid transparent" : "none"
                }
              },
              [
                h("img", {
                  attrs: {
                    src: data.icon
                  },
                  class: {
                    [className]: !!data.children,
                    [classNameChild]: !data.children
                  },
                  style: {
                    color: "#00cdea",
                    marginLeft: data.children ? "10px" : "15px"
                  }
                }),
                h(
                  "span",
                  {
                    style: {
                      fontSize: "13px",
                      marginLeft: "10px"
                    }
                  },
                  data.text
                )
              ]
            )
          ])
        ]
      );
    },
    filterNode(value, data) {
      if (!value) return true;
      return data.text.toLocaleLowerCase().indexOf(value.toLocaleLowerCase()) !== -1;
    }
  }
});
</script>
<style lang="scss">
@import '@/styles/theme.scss';
.tabdiv {
  width: 100%;
  height: 100%;
  position: relative;
}
.tabdiv .adddiv {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 10;
  cursor: pointer;
}
.tabdiv /deep/ .el-tabs--border-card > .el-tabs__header .el-tabs__item {
  color: $normalWordsClr !important;
}
.tabdiv /deep/ .el-tabs__nav {
  padding-top: 4px;
}
.search {
  background: #314a40;
  // & /deep/ .el-input__inner {
  //   color: #000;
  // }
}
.components-tree {
  background: transparent;
  color: $normalWordsClr !important;
  .el-tree-node__content > div {
    color: $normalWordsClr;
  }
  .dragNode {
    font-size: 16px;
    display: flex;
    align-items: center;
  }
}
// /deep/ .el-tabs--border-card, /deep/ .el-tabs--border-card > .el-tabs__header .el-tabs__item.is-active{
//   background: $conterBg;
// }
</style>
<style lang="scss">
@import '@/styles/theme.scss';
.components-tree .el-tree-node__content > div {
  color: #fff !important;
}
.components-tree .el-tree-node__content:hover {
  background: transparent !important;
}
.components-tree /deep/ .el-tree-node__expand-icon {
  display: inline-block !important;
}
.tabdiv .el-tabs {
  height: 100%;
  border: none;
}
.tabdiv .el-tree--highlight-current .el-tree-node.is-current > .el-tree-node__content {
  background: transparent !important;
}
.tabdiv .el-tabs .el-tabs__header {
  background: $centerTtlBgClr;
  height: 40px;
  border-bottom: none;
}
.tabdiv .el-tabs .el-tabs__content {
  height: calc(100% - 40px);
  padding: 0;
  overflow: hidden;
  overflow-y: auto;
  background: transparent;
  color: $normalWordsClr;
}
.tabdiv .el-tabs .el-tabs__content .el-tab-pane,
.tabdiv .el-tabs .el-tabs__content .tab_cz_div {
  height: 100%;
  background: var(--flowBgClr, #082b5f);
  /deep/ .el-tree {
    background: transparent;
  }
}
.tabdiv .el-tabs .el-tabs__content .search {
  margin-bottom: 4px;
}
.tabdiv .el-tabs .el-tabs__content .navdiv {
  height: calc(100% - 36px);
  overflow: hidden;
  overflow-y: auto;
}
.tabdiv .el-tabs .el-tabs__item {
  height: 36px;
  line-height: 36px;
  margin-top: 6px;
  border: none;
}
.tabdiv .el-tabs .el-tabs__item span {
  display: flex;
  align-items: center;
}
.tabdiv .el-tabs--border-card {
  background: transparent;
}
.tabdiv .el-tree-node:focus > .el-tree-node__content {
  background: transparent;
}
.tabdiv .el-tabs--border-card > .el-tabs__header .el-tabs__item.is-active {
  background: transparent;
  border: none;
  color: $normalWordsClr;
  border-top-left-radius: 5px;
  border-top-right-radius: 5px;
  height: 34px;
  padding-left: 10px;
}
.tabdiv .el-tabs--border-card > .el-tabs__header .el-tabs__item:not(.is-disabled):hover {
  color: $normalWordsClr;
}
.plan__List {
  line-height: 32px;
  width: 150px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  cursor: pointer;
}
</style>


