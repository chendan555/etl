<template>
  <div class="element-item view-table-box">
    <!-- <table
      class="view-table"
      cellspacing="0"
      cellpadding="0"
      border="0"
      :style="eleConfig | getStyle"
    >
      <tr v-for="(row, indexRow) in childEle" :key="indexRow">
        <template v-for="(td, indexCol) in row">
          <td
            :class="[{dragging: td.dragging}, {choosed: td.choosed}, {'data-complete': td.userAttrConfig.key}]"
            :key="indexCol"
            v-if="!td.merged"
            @dragleave="dragLeave($event, indexRow, indexCol)"
            @dragenter="dragEnter($event, indexRow, indexCol)"
            @drop.stop="drop(indexRow, indexCol,$event)"
            @click.stop.exact="chooseChildEle(td)"
            @click.ctrl.stop.exact="chooseChildEle(td,true)"
            @contextmenu.prevent.stop="tdRightHandler(td, $event, indexRow, indexCol)"
            @dblclick="editTable(td)"
            :style="gettd2Style(td)"
            :width="td.userInputConfig.w + 'px'"
            :rowspan="td.row"
            :colspan="td.col"
          >
            <div v-html="td.userAttrConfig.content" :id="td.i"></div>
          </td>
        </template>
      </tr>
    </table>-->
  </div>
</template>

<script>
import { createRandomId, copyObj } from '@/utils/utils';
import { info as tdInfo, userInputConfig, userAttrConfig, config, dataSource } from './tdConfig';
export default {
  name: 'tableEle',
  props: {
    dragEle: null, //拖拽进入项
    eleConfig: Object, //表格配置
    tableList: Array,
    canvasBus: null // 父类事件bus，面板
  },
  data() {
    return {
      childConfig: {
        ...this.eleConfig,
        child: this.tableList
      },
      childEle: [], //子数据
      tdMenuShow: false, //单元格右键菜单显示隐藏
      tdMenuX: 0,
      tdMenuY: 0,
      tdRightChoosed: {
        x: 0,
        y: 0,
        td: {}
      }
    };
  },

  created() {
    this.setEleChild();
    for (let i = 0; i < this.childConfig.col; i++) {
      this.childConfig.head.push({
        name: ''
      });
    }
  },

  mounted() {
    // 覆盖页面可编辑元素的默认页面保存事件 —Leo
    let _this = this;
    let divDom = document.getElementsByTagName('div');
    for (let item of divDom) {
      if (item.getAttribute('contenteditable')) {
        item.addEventListener(
          'keydown',
          function(e) {
            if (e.keyCode == 83 && e.ctrlKey) {
              e.preventDefault();
              _this.$bus.$emit('keyDownCtrlS');
            }
          },
          false
        );
      }
    }
    this.checkWidth();
    console.log(this.childEle);
  },

  methods: {
    // 阻止事件默认行为，不阻止不能触发完成
    dragEnter(e, x, y) {
      e.preventDefault();
      if (!this.dragEle) {
        return;
      }
      this.childEle[x][y].dragging = true;
    },
    dragLeave(e, x, y) {
      this.childEle[x][y].dragging = false;
    },
    // 拖拽完成
    drop(x, y, e) {
      let tdBox;
      if (e.target.nodeName.toLocaleLowerCase() !== 'div') {
        tdBox = e.target.children[0].getBoundingClientRect();
      } else {
        tdBox = e.target.getBoundingClientRect();
      }
      if (!this.dragEle) {
        return;
      }
      // let tdBox = this.$el.querySelector('.view-table').children[x].children[y].getBoundingClientRect()
      // let tdBox = e.target.getBoundingClientRect()
      if (this.dragEle.userInputConfig.w > tdBox.width) {
        this.dragEle.userInputConfig.h =
          ((tdBox.width * 1) / this.dragEle.userInputConfig.w) * this.dragEle.userInputConfig.h < 10
            ? 10
            : ((tdBox.width * 1) / this.dragEle.userInputConfig.w) * this.dragEle.userInputConfig.h;
        this.dragEle.userInputConfig.w = tdBox.width * 1;
      }
      if (this.dragEle.userInputConfig.x) {
        delete this.dragEle.userInputConfig.x;
      }
      if (this.dragEle.userInputConfig.y) {
        delete this.dragEle.userInputConfig.y;
      }
      this.childEle[x][y].child = {
        ...this.dragEle.info,
        userAttrConfig: this.dragEle.userAttrConfig,
        userInputConfig: this.dragEle.userInputConfig,
        config: this.dragEle.config,
        i: createRandomId(),
        choosed: false
      };
      if (this.dragEle.dataSource) {
        this.childEle[x][y].child['dataSource'] = this.dragEle.dataSource;
      }
      this.childEle[x][y].dragging = false;
      this.chooseChildEle(this.childEle[x][y].child);
      this.$emit('dragInTableEvt');
    },
    gettd2Style(td) {
      let obj = this.getStyle(td);
      obj.width = '1px';
      return obj;
    },
    // td下的 edit 文字对齐
    getTdStyle(td) {
      // let obj = this.getStyle(td);
      // obj.width = '100%'
      return {
        width: '100%',
        height: td.userInputConfig.h * 1 - td.userInputConfig.paddingTop * 1 - td.userInputConfig.paddingBottom * 1 + 'px'
      };
    },
    // td下的 edit 文字对齐
    getTdClass(td) {
      td.verticalAlign = td.userInputConfig.verticalAlign;
      td.textAlign = td.userInputConfig.textAlign;
      if (td.verticalAlign == 'inherit') {
        td.verticalAlign = this.childConfig.userInputConfig.verticalAlign;
      }
      if (td.textAlign == 'inherit') {
        td.textAlign = this.childConfig.userInputConfig.textAlign;
      }

      return [td.verticalAlign, td.textAlign];
      // console.log(this.$filter.getSyle);
    },
    //表格右击
    tdRightHandler(td, e, x, y) {
      if (!td.choosed) {
        this.chooseChildEle(td);
      }
      this.tdMenuShow = true;
      this.tdMenuX = e.clientX;
      //  - e.path[2].getBoundingClientRect().left
      this.tdMenuY = e.clientY;
      //  - e.path[2].getBoundingClientRect().top
      this.tdRightChoosed.x = x;
      this.tdRightChoosed.y = y;
      this.tdRightChoosed.td = td;
    },
    //隐藏单元格右击菜单
    hideTdMenu() {
      this.tdMenuShow = false;
    },
    // 隐藏右击
    hideRightMenu() {
      this.tdMenuShow = false;
    },
    //根据单元格ID获取坐标
    getTdById(id) {
      for (let i = 0; i < this.childEle.length; i++) {
        for (let j = 0; j < this.childEle[i].length; j++) {
          if (this.childEle[i][j].i == id) {
            return { x: i, y: j };
          }
        }
      }
    },
    //已经合并过的单元格内插入行
    mergeInsetRow(lastRow, nextRow) {
      let mergedTd = null,
        addRow = [];
      for (let i = 0; i < this.eleConfig.col; i++) {
        addRow.push(this.getTdConfig());
        if (
          (this.childEle[lastRow][i].row > 1 && this.childEle[nextRow][i].merged == this.childEle[lastRow][i].i) ||
          (this.childEle[lastRow][i].merged == this.childEle[nextRow][i].merged && this.childEle[nextRow][i].merged)
        ) {
          addRow[i].merged = this.childEle[nextRow][i].merged;
          if (mergedTd != addRow[i].merged) {
            mergedTd = addRow[i].merged;
            let position = this.getTdById(addRow[i].merged);
            this.childEle[position.x][position.y].row++;
          }
        }
      }
      return addRow;
    },
    //已经合并过的单元格内插入列
    mergeInsetCol(y) {
      let mergedTd = '';
      this.childEle.forEach(ary => {
        let config = this.getTdConfig();
        if (y != 0 && y < ary.length - 1 && ((ary[y - 1].col > 1 && ary[y - 1].i == ary[y].merged) || (ary[y - 1].merged == ary[y].merged && ary[y].merged))) {
          config.merged = ary[y].merged;
          let position = this.getTdById(config.merged);
          ary.splice(y, 0, config);
          if (mergedTd !== config.merged) {
            mergedTd = config.merged;
            this.childEle[position.x][position.y].col++;
          }
        } else {
          ary.splice(y, 0, this.getTdConfig());
        }
      });
    },
    //插入内容
    insetTable(type) {
      let addRow = [];
      if (type == 'up' || type == 'down') {
        let x = this.tdRightChoosed.x;
        for (let index = 0; index < this.eleConfig.col; index++) {
          addRow.push(this.getTdConfig());
        }
        if (type == 'up' && x != 0) {
          addRow = this.mergeInsetRow(x - 1, x);
        }
        if (type == 'down' && x + this.tdRightChoosed.td.row < this.childEle.length - 1) {
          addRow = this.mergeInsetRow(x + this.tdRightChoosed.td.row - 1, x + this.tdRightChoosed.td.row);
        }
      }

      switch (type) {
        case 'up':
          this.childEle.splice(this.tdRightChoosed.x, 0, addRow);
          this.childConfig.row++;
          break;
        case 'down':
          // this.childEle.splice(this.tdRightChoosed.x + this.tdRightChoosed.td.row,0,addRow)
          this.childEle.splice(this.childEle.length, 0, addRow);
          this.childConfig.row++;
          break;
        case 'left':
          this.mergeInsetCol(this.tdRightChoosed.y);
          this.childConfig.head.splice(this.tdRightChoosed.y, 0, { name: '' });
          this.childConfig.col++;
          break;
        case 'right':
          this.mergeInsetCol(this.tdRightChoosed.y + this.tdRightChoosed.td.col);
          this.childConfig.head.splice(this.tdRightChoosed.y + this.tdRightChoosed.td.col, 0, { name: '' });
          this.childConfig.col++;
          break;
        default:
          break;
      }
      this.tdMenuShow = false;
    },
    // 删除行，列
    deleteTable(type) {
      this.$emit('chooseEleClearEvt');
      let mergedTd = null;
      switch (type) {
        case 'row':
          // let row = null;
          // this.childEle[this.tdRightChoosed.x].forEach((obj,index) => {
          //   if (obj.row > 1) {
          //     obj.row--
          //     this.childEle[this.tdRightChoosed.x+1].splice(index,1,obj)
          //     row = obj.row
          //   }else if(obj.merged && mergedTd !== obj.merged) {
          //     let position = this.getTdById(obj.merged)
          //     mergedTd = obj.merged
          //     this.childEle[position.x][position.y].row--
          //     row = this.childEle[position.x][position.y].row
          //   }
          //   if (row == 1) {
          //     console.log(this.childEle[this.tdRightChoosed.x+1][index].merged);
          //     this.childEle[this.tdRightChoosed.x+1][index].merged = false
          //   }
          // });
          // this.childEle.splice(this.tdRightChoosed.x,1);
          // this.childConfig.row--
          if (this.childEle.length > 2) {
            this.childEle.splice(this.childEle.length - 1, 1);
            this.childConfig.row--;
          } else {
            this.$message('列表表格最少输入一行');
          }

          break;
        case 'col':
          let y = this.tdRightChoosed.y;
          this.childEle.forEach((ary, index) => {
            if (ary[y].col > 1) {
              let obj = ary[y];
              obj.col--;
              ary.splice(y, 2, obj);
            }
            if (ary[y].merged && mergedTd !== ary[y].merged) {
              let position = this.getTdById(ary[y].merged);
              mergedTd = ary[y].merged;
              if (position) {
                this.childEle[position.x][position.y].col--;
              }
              ary.splice(y, 1);
            } else {
              ary.splice(y, 1);
            }
            if (ary.length == 0) {
              this.childEle.splice(index, 1);
            }
          });
          this.childConfig.col--;
          break;
        default:
          break;
      }
      this.tdMenuShow = false;
    },
    // 选择行，列
    selectTable(type) {
      this.$emit('chooseEleClearEvt');
      switch (type) {
        case 'row':
          this.childEle[this.tdRightChoosed.x].forEach(element => {
            if (!element.choosed) {
              this.chooseChildEle(element, true);
            }
          });
          break;
        case 'col':
          this.childEle.forEach(ary => {
            if (ary[this.tdRightChoosed.y] && !ary[this.tdRightChoosed.y].choosed) {
              this.chooseChildEle(ary[this.tdRightChoosed.y], true);
            }
          });
          break;
        default:
          break;
      }
      this.tdMenuShow = false;
    },
    //判断选择的单元格是否可以合并
    mergeAbled(choosedTable) {
      if (choosedTable.length == 0 || (choosedTable.length == 1 && choosedTable[0].length == 1)) {
        return false;
      }
      for (let i = 0; i < choosedTable.length; i++) {
        for (let j = 0; j < choosedTable[i].length; j++) {
          if (j > 0) {
            if (choosedTable[i][j].y - choosedTable[i][j - 1].y != 1) {
              return false;
            }
          } else if (i > 0) {
            if (choosedTable[i - 1][j].x + 1 != choosedTable[i][j].x) {
              return false;
            }
            if (j == 0 && choosedTable[i - 1][j].y != choosedTable[i][j].y) {
              return false;
            }
            if (j == choosedTable.length - 1 && choosedTable[i - 1][j].y != choosedTable[i][j].y) {
              return false;
            }
            if (choosedTable[i].length != choosedTable[0].length) {
              return false;
            }
          }
        }
      }
      return true;
    },
    // 合并单元格
    mergeTable() {
      this.hideTdMenu();
      let choosedTable = [],
        startX = null,
        startY = null,
        chooseLength = 0;
      // 选中的格子重新放入二维数组
      for (let i = 0; i < this.childEle.length; i++) {
        let ary = [];
        for (let j = 0; j < this.childEle[i].length; j++) {
          if (this.childEle[i][j].choosed) {
            chooseLength++;
            if (startX === null) {
              (startX = i), (startY = j);
            }
            ary.push({
              x: i,
              y: j,
              id: this.childEle[i][j].i
            });
          }
          if (this.childEle[i][j].merged) {
            let pos = this.getTdById(this.childEle[i][j].merged);
            if (this.childEle[pos.x][pos.y].choosed) {
              ary.push({
                x: i,
                y: j,
                id: this.childEle[i][j].i
              });
            }
          }
        }
        if (ary.length > 0) {
          choosedTable.push(ary);
        }
      }
      if (!this.mergeAbled(choosedTable) || chooseLength == 1) {
        this.$message({
          message: '选中的单元格不能进行合并',
          type: 'warning'
        });
        return;
      }
      let rowNum = choosedTable.length;
      let colNum = choosedTable[0].length;
      // 单元格合并 TODO：合并行,处理表格都有内容的情况
      this.childEle[startX][startY].col = colNum;
      this.childEle[startX][startY].row = rowNum;
      for (let i = 0; i < rowNum; i++) {
        if (i == 0) {
          for (let j = 0; j < colNum - 1; j++) {
            this.childEle[startX + i][startY + 1 + j].merged = this.childEle[startX][startY].i;
            this.childEle[startX + i][startY + 1 + j].choosed = false;
          }
          // this.childEle[startX+i].splice(startY+1, colNum-1)
        } else {
          for (let j = 0; j < colNum; j++) {
            this.childEle[startX + i][startY + j].merged = this.childEle[startX][startY].i;
            this.childEle[startX + i][startY + j].choosed = false;
          }
          // this.mergeTable=null
          // this.childEle=null
          // this.childEle[startX+i].splice(startY, colNum)
        }
      }
    },
    // 双击编辑
    editTable(td) {
      if (!td.child || td.child.length == 0) {
        td.editAbled = true;
        this.chooseChildEle(td);
      }
    },
    //设置行，列的时候，计算表格内容
    setEleChild() {
      let childEle = copyObj(this.childConfig.child);
      let nowRow = childEle.length;
      let nowCol = (childEle[0] && childEle[0].length) || 0;
      let newRow = this.eleConfig.row - nowRow;
      let newCol = this.eleConfig.col - nowCol;

      if (newRow > 0) {
        for (let index = 0; index < newRow; index++) {
          let array = [];
          for (let z = 0; z < nowCol; z++) {
            array.push(this.getTdConfig());
          }
          childEle.push(array);
        }
      } else {
        childEle.splice(this.eleConfig.row, newRow * -1);
      }

      if (newCol > 0) {
        for (let i = 0; i < this.eleConfig.row; i++) {
          for (let z = 0; z < newCol; z++) {
            childEle[i].push(this.getTdConfig());
          }
        }
      } else {
        for (let i = 0; i < this.eleConfig.row; i++) {
          childEle[i].splice(this.eleConfig.col, newCol * -1);
        }
      }
      this.childEle = childEle;
    },
    // 选择子组件
    chooseChildEle(eleConfig, multiple = false) {
      // this.canvasBus.$emit("hideRightMenu")
      // eleConfig.choosed = true
      this.$emit('chooseChildEle', eleConfig, multiple);
    },
    // 处理用户输入
    editEleConfig(config) {
      for (let index = 0; index < this.childEle.length; index++) {
        for (let z = 0; z < this.childEle[index].length; z++) {
          if (this.childEle[index][z].choosed) {
            this.childEle[index][z].userInputConfig = Object.assign({}, this.childEle[index][z].userInputConfig, config.userInputConfig);
          }
          if (this.childEle[index][z].child && this.childEle[index][z].child.choosed) {
            this.childEle[index][z].child.userInputConfig = Object.assign({}, this.childEle[index][z].child.userInputConfig, config.userInputConfig);
          }
        }
      }
    },
    // 删除选中的元素
    deleteEle() {
      for (let index = 0; index < this.childEle.length; index++) {
        for (let z = 0; z < this.childEle[index].length; z++) {
          if (this.childEle[index][z].choosed) {
            this.childEle[index][z].child = null;
            this.childEle[index][z].userAttrConfig.content = '';
          } else if (this.childEle[index][z].child) {
            if (this.childEle[index][z].child.choosed) {
              this.childEle[index][z].child = null;
            }
          }
        }
      }
      this.$emit('chooseEleClearEvt');
    },
    // 表格内容超出
    checkWidth() {
      if (this.$el.offsetWidth > this.eleConfig.userInputConfig.w) {
        this.childConfig.userInputConfig.w = this.$el.offsetWidth;
      }
      if (this.$el.offsetHeight > this.eleConfig.userInputConfig.h) {
        this.childConfig.userInputConfig.h = this.$el.offsetHeight;
      }
    },
    //单元格
    getTdConfig() {
      return copyObj({
        dragging: false,
        choosed: false,
        child: null,
        i: createRandomId(),
        ...tdInfo,
        userInputConfig: userInputConfig,
        userAttrConfig: userAttrConfig,
        dataSource: dataSource,
        config: config,
        merged: false
      });
    },
    getStyle(config) {
      const eleConfig = config.userInputConfig;
      const eleAttr = config.userAttrConfig;
      let obj = {
        left: eleConfig.x == 'inherit' ? 'inherit' : eleConfig.x + 'px',
        top: eleConfig.y == 'inherit' ? 'inherit' : eleConfig.y + 'px',
        right: '0px',
        margin: eleConfig.positionAlign == 'center' ? '0 auto' : '',
        width: eleConfig.w == 'inherit' ? 'inherit' : eleConfig.w + 'px',
        'min-width': eleConfig.w == 'inherit' ? 'inherit' : eleConfig.w + 'px',
        'max-width': eleConfig.w == 'inherit' ? 'inherit' : eleConfig.w + 'px',
        height: eleConfig.h == 'inherit' ? 'inherit' : eleConfig.h + 'px',
        'background-color': eleConfig.background,
        textAlign: eleConfig.textAlign,
        verticalAlign: eleConfig.verticalAlign,
        fontWeight: (eleConfig.fontWeight && 'bold') || 'normal',
        fontStyle: (eleConfig.fontStyle && 'italic') || 'normal',
        textDecoration: eleConfig.textDecoration ? 'underline' : 'none',
        textTransform: eleConfig.textTransform,
        fontSize: eleConfig.fontSize + 'px',
        color: eleConfig.color,
        fontFamily: eleConfig.fontFamily,
        borderRadius: eleConfig.borderRadius + 'px',
        opacity: eleConfig.opacity / 100,
        paddingTop: eleConfig.paddingTop + 'px',
        paddingBottom: eleConfig.paddingBottom + 'px',
        paddingLeft: eleConfig.paddingLeft + 'px',
        paddingRight: eleConfig.paddingRight + 'px',
        lineHeight: (eleConfig.lineHeight && eleConfig.lineHeight + 'px') || '',
        cursor: eleAttr.disabled ? 'not-allowed' : ''
        // 'overflow':eleConfig.overflow || ''
        // 'borderWidth':eleConfig.borderWidth !== undefined && (eleConfig.borderWidth+'px') || 'none',
      };
      if (config.type === 'tableList') {
        Object.assign(obj, {
          borderCollapse: 'collapse'
        });
      }
      if (eleConfig.borderWidth !== undefined) {
        Object.assign(obj, {
          borderColor: eleConfig.borderColor,
          borderStyle: eleConfig.borderStyle,
          borderTopWidth: (eleConfig.borderTop && ((eleConfig.borderWidth !== undefined && eleConfig.borderWidth + 'px') || 'none')) || '0',
          borderRightWidth: (eleConfig.borderRight && ((eleConfig.borderWidth !== undefined && eleConfig.borderWidth + 'px') || 'none')) || '0',
          borderBottomWidth: (eleConfig.borderBottom && ((eleConfig.borderWidth !== undefined && eleConfig.borderWidth + 'px') || 'none')) || '0',
          borderLeftWidth: (eleConfig.borderLeft && ((eleConfig.borderWidth !== undefined && eleConfig.borderWidth + 'px') || 'none')) || '0'
        });
      }

      return obj;
    }
  },
  watch: {
    childEle: {
      handler: function(val) {
        this.checkWidth();
      },
      deep: true
    }
    // "eleConfig.row"() {
    //   this.setEleChild();
    // },
    // "eleConfig.col"() {
    //   this.setEleChild();
    // }
  },
  filters: {
    getStyle(config) {
      const eleConfig = config.userInputConfig;
      const eleAttr = config.userAttrConfig;
      let obj = {
        left: eleConfig.x == 'inherit' ? 'inherit' : eleConfig.x + 'px',
        top: eleConfig.y == 'inherit' ? 'inherit' : eleConfig.y + 'px',
        right: '0px',
        margin: eleConfig.positionAlign == 'center' ? '0 auto' : '',
        width: eleConfig.w == 'inherit' ? 'inherit' : eleConfig.w + 'px',
        'min-width': eleConfig.w == 'inherit' ? 'inherit' : eleConfig.w + 'px',
        'max-width': eleConfig.w == 'inherit' ? 'inherit' : eleConfig.w + 'px',
        height: eleConfig.h == 'inherit' ? 'inherit' : eleConfig.h + 'px',
        'background-color': eleConfig.background,
        textAlign: eleConfig.textAlign,
        verticalAlign: eleConfig.verticalAlign,
        fontWeight: (eleConfig.fontWeight && 'bold') || 'normal',
        fontStyle: (eleConfig.fontStyle && 'italic') || 'normal',
        textDecoration: eleConfig.textDecoration ? 'underline' : 'none',
        textTransform: eleConfig.textTransform,
        fontSize: eleConfig.fontSize + 'px',
        color: eleConfig.color,
        fontFamily: eleConfig.fontFamily,
        borderRadius: eleConfig.borderRadius + 'px',
        opacity: eleConfig.opacity / 100,
        paddingTop: eleConfig.paddingTop + 'px',
        paddingBottom: eleConfig.paddingBottom + 'px',
        paddingLeft: eleConfig.paddingLeft + 'px',
        paddingRight: eleConfig.paddingRight + 'px',
        lineHeight: (eleConfig.lineHeight && eleConfig.lineHeight + 'px') || '',
        cursor: eleAttr.disabled ? 'not-allowed' : ''
        // 'overflow':eleConfig.overflow || ''
        // 'borderWidth':eleConfig.borderWidth !== undefined && (eleConfig.borderWidth+'px') || 'none',
      };
      if (config.type === 'tableList') {
        Object.assign(obj, {
          borderCollapse: 'collapse'
        });
      }
      if (eleConfig.borderWidth !== undefined) {
        Object.assign(obj, {
          borderColor: eleConfig.borderColor,
          borderStyle: eleConfig.borderStyle,
          borderTopWidth: (eleConfig.borderTop && ((eleConfig.borderWidth !== undefined && eleConfig.borderWidth + 'px') || 'none')) || '0',
          borderRightWidth: (eleConfig.borderRight && ((eleConfig.borderWidth !== undefined && eleConfig.borderWidth + 'px') || 'none')) || '0',
          borderBottomWidth: (eleConfig.borderBottom && ((eleConfig.borderWidth !== undefined && eleConfig.borderWidth + 'px') || 'none')) || '0',
          borderLeftWidth: (eleConfig.borderLeft && ((eleConfig.borderWidth !== undefined && eleConfig.borderWidth + 'px') || 'none')) || '0'
        });
      }

      return obj;
    }
  },
  activated() {}
};
</script>
<style>
td .td-edit {
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 1.2;
  word-wrap: break-word;
  /* display: -webkit-box!important;
    line-height: 1.6;
    -webkit-line-clamp: 2;
    flex-direction: column;
    -webkit-box-orient: vertical; */
}
</style>

<style lang='scss' scoped>
.td-edit-table {
  display: table;
  width: 100%;
  height: 100%;
  text-align: inherit;
  vertical-align: inherit;
  overflow: hidden;
}
.td-edit-box {
  display: block;
  width: 100%;
  height: 100%;
  text-align: inherit;
  vertical-align: inherit;
  overflow: hidden;
}
.td-edit {
  width: 100%;
  height: 100%;
  display: table-cell;
  text-align: inherit;
  vertical-align: inherit;
  font-weight: inherit;
  font-style: inherit;
  text-decoration: inherit;
  text-transform: inherit;
  opacity: inherit;
  // padding: inherit;
  overflow: hidden;
}
.td-edit.top {
  align-items: flex-start;
}
.td-edit.middle {
  align-items: center;
}
.td-edit.bottom {
  align-items: flex-end;
}
.td-edit.left {
  justify-content: flex-start;
}
.td-edit.center {
  justify-content: center;
}
.td-edit.right {
  justify-content: flex-end;
}
.add-row {
  width: 16px;
  height: 16px;
  line-height: 16px;
  font-size: 16px;
  text-align: center;
  border: 1px solid #e7e7e7;
  border-radius: 4px;
  color: #000;
  position: absolute;
  bottom: -20px;
  right: 4px;
  box-sizing: border-box;
}
.view-table {
  width: 100%;
  height: 100%;
  z-index: 10;
  border-collapse: collapse;
  background-color: #fff;
  border-top: 1px solid #999;
  border-left: 1px solid #999;
  // table-layout: fixed;
  tr {
    vertical-align: inherit;
    text-align: inherit;
  }
  th {
    border-bottom: 1px solid #999;
    border-right: 1px solid #999;
    height: 30px;
    background-color: #fff;
    font-size: 20px;
    padding-left: 10px;
    text-align-last: left;
  }
  td {
    border-bottom: 1px solid #999;
    border-right: 1px solid #999;
    word-break: break-all;
    border-collapse: collapse;
    box-sizing: border-box;
    .td-edit-content {
      /*width: 100%;*/
      height: auto;
      vertical-align: inherit;
      text-align: inherit;
      display: inline-block;
      // display: inherit;
      background: none;
      border: 0;
      padding: 0;
      margin: 0;
      font: inherit;
    }
    &.choosed {
      position: relative;
      box-shadow: inset 0px 0px 1px 1px rgb(14, 245, 45);
    }
  }
  .view-components-box {
    display: inline-block;
    z-index: 10;
    &.choosed {
      box-shadow: 0px 0px 1px 1px rgb(14, 245, 45);
    }
  }
  .dragging {
    box-shadow: inset 0px 0px 6px 1px pink;
    // border-color: rgb(238, 7, 7);
  }
}
.view-table-box {
  transform: none;
  position: relative;
  // &::before{
  //   z-index: -1;
  //   content: '';
  //   position: absolute;
  //   left: -6px;
  //   top: -6px;
  //   bottom: -6px;
  //   right: -6px;
  //   background-color: rgba($color: #999, $alpha: 0.6);
  // }
  .size-box {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    z-index: -1;
  }
}
</style>
