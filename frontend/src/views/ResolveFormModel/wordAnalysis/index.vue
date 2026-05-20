<template>
  <div class="wordAnalysis" id="wordAnalysis">
    <!-- 接收word复制的内容 -->
    <div class="accieveWord" v-if="finishAnalysis">
      <div class="btn">
        <el-button type="primary" size="mini" @click="analysisTableDom">文档结构化解析</el-button>
      </div>
      <div id="wordContainer" contenteditable="true"></div>
    </div>
    <div class="mainPart" v-else>
      <div class="tablePart" id="tablePart">
        <LayoutItem
          v-for="(item, index) in layout"
          :tabindex="index"
          :dataId="item.i"
          :key="index"
          :config="item"
          :style="{height:'auto'}"
          @click.native.stop.exact="chooseChildEle(item)"
        >
          <div class="htmlCont" v-if="item.type === 'html'">
            <p v-html="item.userAttrConfig.content"></p>
          </div>
          <TableExhibit
            v-else
            ref="eleItem"
            :class="[ item.choosed ? 'choosed' : '', {'data-complete': item.userAttrConfig.key }]"
            class="view-components-box"
            @chooseEleClearEvt="chooseEleClear"
            @chooseChildEle="chooseChildEle"
            :enadleEdit="false"
            :eleConfig="{...item, child: tableList}"
            :tableList="tableList"
            @chooseTableEle="chooseTableEle"
          ></TableExhibit>
        </LayoutItem>
      </div>
      <div class="attributePart">
        <DataSource
          v-bind="$attrs"
          ref="dataSource"
          :eleEdit="eleEdit"
          :eleList="layout"
          :currTableConfig="currTableConfig"
          :layout="[]"
          :boxConfig="boxConfig"
          @updateNode="updateNode"
          @setTableName="setTableName"
        >
          <div class="save" style="margin-top: 10px;display: flex;">
            <el-button style="flex: 1;" type="success" size="mini" @click="saveSourceConfig">完成配置</el-button>
          </div>
        </DataSource>
      </div>
    </div>
  </div>
</template>
<script lang="ts">
import Vue from 'vue';
import utils from '@/utils/index.ts';
import {
  userInputConfig as tableUserInputConfig,
  userAttrConfig as tableUserAttrConfig,
  info as tableInfo
  // config as tableConfig
} from './tableExhibit/index.js';
import {
  info as tdInfo,
  userInputConfig as tdUserInputConfig,
  userAttrConfig as tdUserAttrConfig,
  // config as tdConfig,
  dataSource as tdDataSource
} from './tableExhibit/tdConfig/index.js';
import {
  userInputConfig as htmlUserInputConfig,
  userAttrConfig as htmlUserAttrConfig,
  info as htmlInfo
  // config as htmlConfig
} from './tableExhibit/html/index.js';
import LayoutItem from './tableExhibit/layoutItem.vue';
import TableExhibit from './tableExhibit/show.vue';
import DataSource from './dataSource.vue';
// import Pinyin from '@/utils/ChinesePY.js';
// import http from '@/utils/request.js';

const vue: any = {};
let autoBind = function(e: any) {
  if (e.altKey && e.code == 'Digit1') {
    vue.autoConfigColumnName(false);
  } else if (e.altKey && e.code == 'Digit2') {
    vue.autoConfigColumnName(true);
  } else if (e.altKey && e.code == 'Digit3') {
    vue.autoConfigtableHeaderName();
  } else if (e.altKey && e.code == 'Backquote') {
    vue.getFormModel();
  }
};

export default Vue.extend({
  name: 'wordAnalysis',
  components: { LayoutItem, TableExhibit, DataSource },
  props: ['allConfig'],
  mounted() {
    let wordAnalysis = (document as any).getElementById('wordAnalysis');
    wordAnalysis.addEventListener('click', this.focusContainer, true);

    document.addEventListener('keydown', autoBind);
    vue.autoConfigColumnName = this.autoConfigColumnName;
    vue.autoConfigtableHeaderName = this.autoConfigtableHeaderName;
    vue.getFormModel = this.getFormModel;
    this.$nextTick(() => {
      console.log(this.allConfig);
      if (this.allConfig && this.allConfig.eleList) {
        this.layout = this.allConfig.eleList;
        let eleConfig = this.layout.find((item: any) => item.type.includes('tableList'));
        this.tableList = eleConfig.child;
        this.finishAnalysis = false;
      }
    });
  },
  data() {
    return {
      tableList: <any>[],
      finishAnalysis: true,
      layout: <any>[], //编辑框中的控件
      expandKeyIndex: 1,
      choosedLayout: <any>[], //选中的控件
      eleEdit: <any>{
        isCreateTable: 0, // 是否是新建表
        userInputConfig: {},
        userAttrConfig: {},
        dataSource: {
          columnConfig: {
            columnName: '',
            tableHead: 0,
            tableLength: 0,
            dealDirection: 0
          },
          tableName: '',
          tableHead: 0,
          tableLength: 0,
          startRow: 0,
          startCol: 0
        }
      }, //属性设置面板上面显示的控件id
      boxConfig: {
        //画布属性值
        choosed: false,
        type: 'canvasBox',
        id: '',
        userInputConfig: {
          //画布属性值
          w: 0,
          h: 0,
          paddingX: 0,
          paddingY: 0,
          name: '新建表单',
          canvasType: 'responsive',
          isAcross: true,
          tableSchema: '',
          prevTableName: ''
        }
      },
      currTableConfig: <any>{}, //保存当前操作的table Config
      defaultTableLength: 1,
      tableChnName: '',
      formName: '',
      tableColumn: [], // 数据库中字段名及注释
      tableFormModel: <any>{
        arrays: <any>{},
        parameters: <any>{}
      },
      creatDataSource: <any>{}
    };
  },
  watch: {
    eleEdit: {
      deep: true,
      handler(val) {
        let target = this.layout.find((element: any) => element.i === val.tableId);
        if (target) {
          this.updateNode(target, val);
        }
      }
    }
  },
  methods: {
    updateNode({ child }: any, val: any) {
      child[val.indexRow][val.indexCol] = val;
    },
    //获取配置文件
    getAllConfig() {
      let config = {
        ...this.boxConfig,
        eleList: <any>[]
      };
      let showConfig = {
        w: this.boxConfig.userInputConfig.w,
        h: this.boxConfig.userInputConfig.h,
        name: this.boxConfig.userInputConfig.name,
        eleList: <any>[]
      };

      let formModel: any = [];
      //增加需要取子组件数据时，需要使用
      this.layout.forEach((element: any, index: number) => {
        let obj = utils.deepCopy(element);
        let showObj = {};
        config.eleList.push(obj);
        showConfig.eleList.push(this.getConfigApp(obj));

        if (element.userAttrConfig.key) {
          let obj = <any>{};
          obj[element.expandKey] = element.userAttrConfig.key;
          obj.keyId = element.i;
          formModel.push(obj);
        }
        if (element.icon === 'table' || element.type === 'table' || element.type === 'tableList') {
          if (element.child) {
            element.child.forEach((row: any, indexRow: number) => {
              row.forEach((td: any, indexCol: number) => {
                if (td.child && td.child.length > 0) {
                  td.child.forEach((item: any, index: number) => {
                    if (item.userAttrConfig && item.userAttrConfig.key) {
                      let obj = <any>{};
                      obj[td.expandKey + index] = item.userAttrConfig.key;
                      obj.keyId = item.i;
                      if (item.dataSource.tableLength) {
                        obj.tableLength = item.dataSource.tableLength;
                        obj.dealDirection = item.dataSource.columnConfig.dealDirection ? item.dataSource.columnConfig.dealDirection : 0;
                      }
                      formModel.push(obj);
                    } else {
                      let obj = <any>{};
                      obj[td.expandKey] = td.userAttrConfig.key;
                      obj.keyId = td.i;
                      if (td.dataSource.tableLength) {
                        obj.tableLength = td.dataSource.tableLength;
                        obj.dealDirection = td.dataSource.columnConfig.dealDirection ? td.dataSource.columnConfig.dealDirection : 0;
                      }
                      formModel.push(obj);
                    }
                  });
                } else {
                  if (td.userAttrConfig.key) {
                    let obj = <any>{};
                    let keyInfo = td.userAttrConfig.key.split('/');
                    obj.columnComment = keyInfo[1];
                    obj.columnName = keyInfo[2];
                    obj.indexRow = indexRow;
                    obj.indexCol = indexCol;
                    // obj[`key_${indexRow}_${indexCol}`] = td.userAttrConfig.key;
                    // obj.keyId = td.i;
                    if (td.dataSource.tableLength) {
                      obj.tableLength = td.dataSource.tableLength;
                      obj.dealDirection = td.dataSource.columnConfig.dealDirection ? td.dataSource.columnConfig.dealDirection : 0;
                      obj.columnType = 2;
                    } else {
                      obj.columnType = 1;
                    }
                    formModel.push(obj);
                  }
                }
              });
            });
          }
        } else {
        }
      });
      // 表格单元格多组件的情况下formModel配置去重
      /* let newFormModel = <any>[];
      formModel.forEach((model: any) => {
        let isPush = true;
        newFormModel.forEach((newModel: any) => {
          if (!model.tableLength && !newModel.tableLength && model.keyId === newModel.keyId) {
            isPush = false;
          }
        });
        if (isPush) {
          newFormModel.push(model);
        }
      }); */

      return {
        allConfig: config,
        appConfig: showConfig,
        formModel,
        creatDataSource: this.creatDataSource
      };
    },
    //精简config，choosed属性改为false
    getConfigApp(eleItemObj: any) {
      // 修改选中
      eleItemObj.choosed = false;
      // 新建数据源的配置
      this.setCreatDataSource(eleItemObj.dataSource);

      let eleItem = utils.deepCopy(eleItemObj);
      let obj = <any>{};
      obj.type = eleItem.type;
      obj.expandKey = eleItem.expandKey;
      obj.i = eleItem.i;
      obj.userAttrConfig = utils.deepCopy(eleItem.userAttrConfig);
      obj.userInputConfig = utils.deepCopy(eleItem.userInputConfig);
      for (const key in obj.userInputConfig) {
        if (key === 'opacity') {
          if (obj.userInputConfig[key] == '100') {
            delete obj.userInputConfig[key];
          }
        } else if (
          obj.userInputConfig['borderLeft'] &&
          obj.userInputConfig['borderColor'] == '#000' &&
          obj.userInputConfig['borderWidth'] == 1 &&
          obj.userInputConfig['borderRight'] &&
          obj.userInputConfig['borderStyle'] == 'solid' &&
          obj.userInputConfig['borderTop'] &&
          obj.userInputConfig['borderBottom']
        ) {
        } else if (
          key === 'dataSource' ||
          (!obj.userInputConfig[key] && obj.userInputConfig[key] !== 0) ||
          obj.userInputConfig[key] == 'inherit' ||
          obj.userInputConfig[key] == 'auto'
        ) {
          delete obj.userInputConfig[key];
        }
      }

      if (eleItem.type == 'tdConfig') {
        delete obj.userAttrConfig.inherit;
        if (obj.child) {
          obj.userAttrConfig.editAble = false;
        }
        if (JSON.stringify(obj.userInputConfig) == '{}') {
          delete obj.userInputConfig;
        }
      } else if (eleItem.type == 'qrCodeShow') {
        obj.i = eleItem.i;
      } else if (eleItem.type == 'tableList') {
        obj.userAttrConfig.key = utils.createRandomId();
      }

      if (eleItem.type == 'table' || eleItem.type == 'tableList') {
        obj.tdWidth = eleItem.tdWidth;
        obj.inheritIndex = eleItem.inheritIndex;
        obj.inheritKey = eleItem.inheritKey;
        for (let i = 0; i < eleItem.child.length; i++) {
          for (let j = 0; j < eleItem.child[i].length; j++) {
            // 修改选中
            eleItemObj.child[i][j].choosed = false;
            // 新建数据源的配置
            this.setCreatDataSource(eleItemObj.dataSource);
            if (eleItemObj.child[i][j].child) {
              eleItemObj.child[i][j].child.choosed = false;
              this.setCreatDataSource(eleItemObj.dataSource);
            }

            const element = eleItem.child[i][j];
            if (element.merged) {
              eleItem.child[i].splice(j--, 1);
              continue;
            } else if (eleItem.type == 'tableList' && eleItem.inherit[j] && i > eleItem.inheritIndex[j]) {
              let obj = <any>{
                userAttrConfig: {}
              };
              if (eleItem.inherit[j].expandKey) {
                obj.userAttrConfig = {
                  key: eleItem.inherit[j].expandKey
                };
                obj.expandKey = eleItem.inherit[j].expandKey;
              }
              if (eleItem.inherit[j].child) {
                obj.child = {
                  type: eleItem.inherit[j].child.type,
                  userAttrConfig: eleItem.inherit[j].child.userAttrConfig
                };
              } else {
                obj.userAttrConfig.content = eleItem.child[i][j].userAttrConfig.content;
              }
              eleItem.child[i][j] = utils.deepCopy(obj);
            } else if (eleItem.type == 'tableList' && eleItem.inheritKey[j] && i > eleItem.inheritKeyIndex[j]) {
              eleItem.child[i][j].userInputConfig = this.getStyle(eleItem.child[i][j]);
              eleItem.child[i][j].child.forEach((obj: any) => {
                obj.userInputConfig = this.getStyle(obj);
              });
              //  getStyle = this.getConfigApp(eleItem.child[i][j]);
              eleItem.child[i][j].userAttrConfig.key = eleItem.inheritKey[j];
            } else {
              eleItem.child[i][j].userInputConfig = this.getStyle(eleItem.child[i][j]);
              eleItem.child[i][j].child.forEach((obj: any) => {
                obj.userInputConfig = this.getStyle(obj);
              });
              // eleItem.child[i][j] = this.getConfigApp(eleItem.child[i][j]);
            }
            if (!eleItem.child[i][j].userAttrConfig.content) {
              eleItem.child[i][j].userAttrConfig.editAble = true;
            }
          }
        }
        obj.child = eleItem.child;
      } else if (eleItem.type == 'tdConfig') {
        if (eleItem.col != 1) {
          obj.col = eleItem.col;
        }
        if (eleItem.row != 1) {
          obj.row = eleItem.row;
        }
        if (eleItem.child) {
          obj.child = this.getConfigApp(eleItem.child);
        }
      }

      if (obj.userAttrConfig.dataSource) {
        delete obj.userAttrConfig.dataSource;
      } else if (obj.userAttrConfig.name !== undefined) {
        delete obj.userAttrConfig.name;
      }

      if (obj.userInputConfig) {
        obj.userInputConfig = this.getStyle(obj);
      }

      if (obj.type == 'tdConfig') {
        delete obj.type;
      }

      return obj;
    },
    // 新建数据源的配置
    setCreatDataSource(dataSource: any) {
      let dataName = this.boxConfig.userInputConfig.tableSchema;
      if (dataName && dataSource && dataSource.isNew == 1 && dataSource.tableName && dataSource.columnConfig.columnName) {
        this.creatDataSource[dataName] = this.creatDataSource[dataName] || {};
        this.creatDataSource[dataName][dataSource.tableName] = this.creatDataSource[dataName][dataSource.tableName] || { column: [] };
        let index = this.creatDataSource[dataName][dataSource.tableName].column.findIndex(
          (obj: any) => obj.columnName == this.creatDataSource[dataName][dataSource.tableName][dataSource.columnConfig.columnName]
        );
        if (index < 0) {
          this.creatDataSource[dataName][dataSource.tableName].column.push(dataSource.columnConfig);
        }
        // this.creatDataSource[dataName][dataSource.tableName][dataSource.columnConfig.columnName] = dataSource.columnConfig
      }
    },
    getStyle(elConfig: any) {
      var obj = <any>{};
      elConfig.userInputConfig.fontFamily !== undefined && (obj.fontFamily = elConfig.userInputConfig.fontFamily);
      elConfig.userInputConfig.x !== undefined && (obj.left = elConfig.userInputConfig.x + 'px');
      elConfig.userInputConfig.y !== undefined && (obj.top = elConfig.userInputConfig.y + 'px');
      elConfig.userInputConfig.w !== undefined && (obj.width = elConfig.userInputConfig.w + 'px');
      elConfig.userInputConfig.h !== undefined && (obj.height = elConfig.userInputConfig.h + 'px');
      elConfig.userInputConfig.verticalAlign !== undefined && (obj.verticalAlign = elConfig.userInputConfig.verticalAlign);
      elConfig.userInputConfig.textAlign !== undefined && (obj.textAlign = elConfig.userInputConfig.textAlign);

      elConfig.userInputConfig.lineHeight !== undefined &&
        (obj.lineHeight = elConfig.userInputConfig.lineHeight ? elConfig.userInputConfig.lineHeight + 'px' : 'normal');

      elConfig.userInputConfig.background !== undefined && (obj.backgroundColor = elConfig.userInputConfig.background);
      elConfig.userInputConfig.fontWeight !== undefined && (obj.fontWeight = (elConfig.userInputConfig.fontWeight && 'bold') || 'normal');
      elConfig.userInputConfig.fontStyle !== undefined && (obj.fontStyle = (elConfig.userInputConfig.fontStyle && 'italic') || 'normal');
      elConfig.userInputConfig.textDecoration !== undefined && (obj.textDecoration = elConfig.userInputConfig.textDecoration ? 'underline' : 'none');
      elConfig.userInputConfig.textTransform !== undefined && (obj.textTransform = elConfig.userInputConfig.textTransform);
      elConfig.userInputConfig.fontSize !== undefined && (obj.fontSize = elConfig.userInputConfig.fontSize + 'px');
      elConfig.userInputConfig.color !== undefined && (obj.color = elConfig.userInputConfig.color);
      elConfig.userInputConfig.borderRadius !== undefined && (obj.borderRadius = elConfig.userInputConfig.borderRadius + 'px');
      elConfig.userInputConfig.opacity !== undefined && (obj.opacity = elConfig.userInputConfig.opacity + '%');
      elConfig.userInputConfig.paddingTop !== undefined && (obj.paddingTop = elConfig.userInputConfig.paddingTop + 'px');
      elConfig.userInputConfig.paddingBottom !== undefined && (obj.paddingBottom = elConfig.userInputConfig.paddingBottom + 'px');
      elConfig.userInputConfig.paddingLeft !== undefined && (obj.paddingLeft = elConfig.userInputConfig.paddingLeft + 'px');
      elConfig.userInputConfig.paddingRight !== undefined && (obj.paddingRight = elConfig.userInputConfig.paddingRight + 'px');
      elConfig.userInputConfig.borderColor !== undefined && (obj.borderColor = elConfig.userInputConfig.borderColor);
      elConfig.userInputConfig.borderStyle !== undefined && (obj.borderStyle = elConfig.userInputConfig.borderStyle);

      if (elConfig.userInputConfig.borderDefault) {
        obj.borderDefault = true;
      } else if (elConfig.userInputConfig.borderWidth !== undefined) {
        obj.borderColor = elConfig.userInputConfig.borderColor;
        obj.borderStyle = elConfig.userInputConfig.borderStyle;
        obj.borderTopWidth =
          (elConfig.userInputConfig.borderTop &&
            ((elConfig.userInputConfig.borderWidth !== undefined && elConfig.userInputConfig.borderWidth + 'px') || 'none')) ||
          '0';
        obj.borderRightWidth =
          (elConfig.userInputConfig.borderRight &&
            ((elConfig.userInputConfig.borderWidth !== undefined && elConfig.userInputConfig.borderWidth + 'px') || 'none')) ||
          '0';
        obj.borderBottomWidth =
          (elConfig.userInputConfig.borderBottom &&
            ((elConfig.userInputConfig.borderWidth !== undefined && elConfig.userInputConfig.borderWidth + 'px') || 'none')) ||
          '0';
        obj.borderLeftWidth =
          (elConfig.userInputConfig.borderLeft &&
            ((elConfig.userInputConfig.borderWidth !== undefined && elConfig.userInputConfig.borderWidth + 'px') || 'none')) ||
          '0';
      }
      return obj;
    },
    /**
     * 设置表名称
     */
    setTableName(data: any) {
      this.tableChnName = data.tableName;
      this.formName = data.formName;
    },
    // 设置默认数据行列数
    emitDefaultLength(data: number) {
      this.defaultTableLength = data;
    },
    autoConfigColumnName(single: boolean) {
      // if (!this.tableChnName && !this.eleEdit.dataSource.tableName) {
      //   this.$message.warning("请先设置表名！");
      //   return;
      // }
      let { indexRow, indexCol } = this.eleEdit;
      if (indexRow === undefined) {
        let crtComp: any = {};
        try {
          this.eleEdit.eleList.forEach((ele: any) => {
            if (ele.i === this.eleEdit.parentTable.i) {
              crtComp = ele;
              throw new Error();
            }
          });
        } catch (err) {
          indexRow = crtComp.child[this.eleEdit.parentTable.x][this.eleEdit.parentTable.y].indexRow;
          indexCol = crtComp.child[this.eleEdit.parentTable.x][this.eleEdit.parentTable.y].indexCol;
        }
      }
      let indexColTemp = indexCol;
      let indexRowTemp = indexRow;
      let columnName = '';
      let dealIds: any = [];
      if (indexColTemp--) {
        do {
          let id = this.currTableConfig.child[indexRow][indexColTemp].i;
          if (this.currTableConfig.child[indexRow][indexColTemp].merged) {
            id = this.currTableConfig.child[indexRow][indexColTemp].merged;
          }
          if (dealIds.includes(id)) {
            continue;
          }
          let innerText = document.getElementById(id) ? (document.getElementById(id) as any).innerText.trim() : '';
          if (innerText) {
            columnName = `${this.filterSChar(innerText)}_${columnName}`;
            dealIds.push(id);
          }
        } while (--indexColTemp > -1 && !single);
      }
      if (indexRowTemp--) {
        do {
          if (this.currTableConfig.child[indexRowTemp][indexCol].dataSource && this.currTableConfig.child[indexRowTemp][indexCol].dataSource.tableLength == 0) {
            let innerText = document.getElementById(this.currTableConfig.child[indexRowTemp][indexCol].i)
              ? (document.getElementById(this.currTableConfig.child[indexRowTemp][indexCol].i) as any).innerText.trim()
              : '';
            if (innerText) {
              columnName = `f_${columnName}${this.filterSChar(innerText)}`;
              break;
            }
          }
        } while (--indexRowTemp > -1 && !single);
      }
      if (columnName.endsWith('_')) {
        columnName = columnName.substr(0, columnName.length - 1);
      }
      if (columnName) {
        let resColumnName = `${columnName}_${indexRow}_${indexCol}`;
        this.eleEdit.dataSource.columnConfig.columnName = resColumnName;
        this.eleEdit.dataSource.columnConfig.columnComment = resColumnName;
        // (this.$refs.dataSource as any).initDataSource(this.eleEdit.dataSource);
        indexRow = undefined;
        this.getFormModel();
      } else {
        this.$message.warning('当前列名单元格无内容，请先确认或输入！');
      }
    },
    autoConfigtableHeaderName() {
      let { indexRow, indexCol } = this.eleEdit;
      let id = this.currTableConfig.child[indexRow][indexCol].i;
      let innerText = document.getElementById(id) ? (document.getElementById(id) as any).innerText.trim() : '';
      if (innerText) {
        this.eleEdit.dataSource.columnConfig.columnName = innerText;
        this.eleEdit.dataSource.columnConfig.columnComment = innerText;
        this.eleEdit.dataSource.columnConfig.tableLength = this.defaultTableLength || 1;
        this.eleEdit.dataSource.columnConfig.dealDirection = 0;
        // (this.$refs.dataSource as any).changekey();
        // (this.$refs.dataSource as any).initDataSource(this.eleEdit.dataSource);
        this.getFormModel();
      } else {
        this.$message.warning('当前单元格无内容，请先确认或输入！');
      }
    },
    deleteColumns() {
      this.$confirm('确认是否清除当前已配置的所有列表名？', '提示', {
        showCancelButton: true,
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        confirmButtonClass: 'confirmButtonClass',
        type: 'warning'
      })
        .then(() => {
          // this.isRedraw = false;
          this.tableColumn = [];
          this.deleteColumnName(this.layout);
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          });
        });
    },
    // 清除节点上绑定的数据源列表名
    deleteColumnName(compList: any) {
      compList.forEach((comp: any) => {
        if (comp.hasOwnProperty('dataSource')) {
          this.deleteCrtColumns(comp);
        }
        if (comp.hasOwnProperty('child') && comp.child.length) {
          this.deleteColumnName(comp.child);
        }
        if (comp instanceof Array) {
          this.deleteColumnName(comp);
        }
      });
    },
    deleteCrtColumns(ele: any) {
      ele = ele || this.eleEdit;
      ele.dataSource.columnConfig.columnName = '';
      ele.dataSource.columnConfig.columnComment = '';
      ele.dataSource.columnConfig.dealDirection = '';
      ele.dataSource.tableLength = 0;
      delete ele.userAttrConfig.key;
    },
    getFormModel() {
      let ds = this.eleEdit.dataSource;
      // let key: string = Pinyin.getWordsCode(ds.columnConfig.columnComment);
      if (ds.columnConfig.columnComment && ds.columnConfig.columnName) {
        if (ds.columnConfig.tableLength) {
          let crtTableChild = <any>[];
          this.layout.forEach((layoutItem: any) => {
            if (layoutItem.i === this.eleEdit.tableId) {
              crtTableChild = layoutItem.child;
            }
          });
          let textResult = <any>[];
          let startIndex = this.eleEdit.indexRow + this.eleEdit.row - 1;
          crtTableChild.forEach((tr: any, trIndex: number) => {
            if (trIndex <= startIndex || trIndex > startIndex + ds.columnConfig.tableLength) {
              return;
            }
            let tdDomId = tr[this.eleEdit.indexCol].i;
            textResult.push(this.getDomText(tdDomId));
          });
          this.$set(this.tableFormModel.arrays, ds.columnConfig.columnComment, textResult);
        } else {
          let crtDomText = this.getDomText(this.eleEdit.i);
          this.$set(this.tableFormModel.parameters, ds.columnConfig.columnComment, crtDomText);
        }
      } else {
        this.$message.warning('请先配置列名和注释！');
      }
    },
    addKeyValue(newKeyValue: any) {
      this.$set(this.tableFormModel.parameters, newKeyValue.key, newKeyValue.value);
    },
    getDomText(id: string) {
      return (document.getElementById(id) as any).innerText;
    },
    saveSourceConfig() {
      // const loading = this.$loading({
      //   lock: true,
      //   text: "加载中...",
      //   spinner: "el-icon-loading",
      //   background: "rgba(0, 0, 0, 0.7)"
      // });
      const allConfig = JSON.parse(JSON.stringify(this.getAllConfig()));
      // allConfig.allConfig.expandKeyIndex = this.expandKeyIndex;
      // allConfig.id = this.formVersionId;
      // allConfig.tableChnName = this.tableChnName;
      let arr = [];
      let isRepeat = false; // 当配置数据源选择新建表单时，列表名不能有重复值
      let repeatValue = ''; // 重复字段名
      const { formModel } = allConfig;
      let result = {
        formName: this.formName,
        formModel,
        allConfig: allConfig.allConfig,
        tableName: this.tableChnName
      };
      this.$emit('sendFormModel', result);
      /* for (let i = 0, len = formModel.length; i < len; i++) {
        for (var key in formModel[i]) {
          if (key.indexOf('key') === -1 || key === 'keyId' || !formModel[i][key]) continue;

          if (
            arr.indexOf(formModel[i][key]) == -1 ||
            formModel[i].dealDirection == 1 // 横向表头可以重复
          ) {
            arr.push(formModel[i][key]);
          } else {
            repeatValue = formModel[i][key];
            isRepeat = true;
            break;
          }
        }
      } */
      // if (isRepeat) {
      //   this.$message.warning(
      //     `不能配置相同的字段名-${repeatValue}, 请重新配置！`
      //   );
      //   loading.close();
      //   return;
      // }
      // console.log(allConfig);
      // formSaveConfigApi.post(allConfig).then(
      //   res => {
      //     loading.close();
      //     this.$message.success("配置数据源成功！");
      //     this.$emit("saveSuccess");
      //   },
      //   err => {
      //     loading.close();
      //   }
      // );
      // loading.close();
    },
    // 当接收word复制内容的容器为空时，点击页面任何位置都将聚焦到该容器内。容器内有内容时清楚聚焦事件
    focusContainer() {
      if (!(document as any).getElementById('wordContainer').innerHTML) {
        (document as any).getElementById('wordContainer').focus();
      } else {
        (document as any).getElementById('wordAnalysis').removeEventListener('click', this.focusContainer, true);
      }
    },
    // 解析表单节点
    analysisTableDom() {
      let html = (document as any).getElementById('wordContainer').children;
      let addEle = [];
      // 遍历解析节点,p为文字节点,div为表单节点
      for (let i = 0; i < html.length; i++) {
        if (html[i].getElementsByTagName('table').length > 0 || html[i].tagName == 'TABLE') {
          let tableArr: any = [];
          let tableHtml = [];
          if (html[i].querySelectorAll('table').length > 0) {
            tableHtml = html[i].querySelectorAll('table');
          } else {
            tableHtml[0] = html[i];
          }
          tableHtml.forEach((tableHtml: any) => {
            let tableStyle: any = getComputedStyle(tableHtml);
            let table: any = [];
            tableHtml.querySelectorAll('tr').forEach((trHtml: any) => {
              if (trHtml.getAttribute('height') !== '0') {
                let tr: any = [];
                trHtml.querySelectorAll('td').forEach((tdHtml: any) => {
                  // 样式
                  let tdStyle: any = getComputedStyle(tdHtml);
                  if (tdStyle.border.includes('none')) {
                    //判断word单元格是否为换行无边框单元格，是则不予显示 -20210623
                    return;
                  }
                  let tdWidth = tdStyle.width.indexOf('%') !== -1 ? 0 : parseFloat(tdStyle.width);
                  let tdHeight = tdStyle.height.indexOf('%') !== -1 ? 0 : parseFloat(tdStyle.height);
                  // 解析
                  let tdChild: any = [];
                  let tdTextAlign = 'left';

                  if (tdHtml.querySelectorAll('p').length) {
                    //调整表格内p标签样式和属性，以符合设置单元格对齐设置条件 -Leo
                    tdHtml.querySelectorAll('p').forEach((p: any) => {
                      p.removeAttribute('style');
                      p.removeAttribute('align');
                    });
                  }
                  let colspan = tdHtml.getAttribute('colspan') ? parseInt(tdHtml.getAttribute('colspan')) : 1;
                  let rowspan = tdHtml.getAttribute('rowspan') ? parseInt(tdHtml.getAttribute('rowspan')) : 1;
                  let id = utils.createRandomId();
                  tr.push({
                    colspan: colspan,
                    rowspan: rowspan,
                    value: tdHtml.innerHTML,
                    i: id,
                    w: tdWidth,
                    child: tdChild,
                    h: tdHeight + 10,
                    textAlign: tdTextAlign
                  });
                  if (colspan > 1) {
                    for (let i = 1; i < colspan; i++) {
                      tr.push({
                        colspan: 1,
                        rowspan: 1,
                        value: '',
                        child: [],
                        i: utils.createRandomId(),
                        w: 0,
                        h: 0,
                        merged: id
                      });
                    }
                  }
                });
                if (tr.length) {
                  table.push(tr);
                }
              }
            });
            tableArr.push({
              table: table,
              w: parseFloat(tableStyle.width) - 10,
              h: parseFloat(tableStyle.height)
            });
          });
          tableArr = tableArr.map((item: any) => {
            return this.formatWordToTable(item);
          });

          tableArr.forEach((item: any, index: number, arr: any) => {
            const tableId = utils.createRandomId();
            let eleConfig: any = {
              ...tableInfo,
              userInputConfig: { ...tableUserInputConfig },
              userAttrConfig: { ...tableUserAttrConfig },
              config: [],
              dataSource: null,
              i: tableId,
              choosed: false,
              child: <any>[]
            };
            eleConfig.expandKey = 'key' + this.expandKeyIndex;
            this.expandKeyIndex++;
            // 定位居中
            if (html[i].getAttribute('align') == 'center') {
              eleConfig.userInputConfig.positionAlign = 'center';
              eleConfig.userInputConfig.x = 0;
            } else {
              eleConfig.userInputConfig.x = 50;
            }
            eleConfig.row = item.row;
            eleConfig.col = item.col;
            eleConfig.userInputConfig.w = item.w;
            eleConfig.userInputConfig.h = item.h;
            eleConfig.userInputConfig.y =
              i > 0 && this.layout[this.layout.length - 1]
                ? (this.layout[this.layout.length - 1] as any).userInputConfig.y + (this.layout[this.layout.length - 1] as any).userInputConfig.h
                : 0;
            eleConfig.child = item.table.map((row: any, rowIndex: number) => {
              return row.map((td: any, tdIndex: number) => {
                let config = utils.deepCopy({
                  dragging: false,
                  choosed: false,
                  child: td.child,
                  i: td.i,
                  ...tdInfo,
                  userInputConfig: { ...tdUserInputConfig },
                  userAttrConfig: { ...tdUserAttrConfig },
                  dataSource: { ...tdDataSource },
                  // config: [...tdConfig],
                  merged: td.merged,
                  indexRow: rowIndex,
                  indexCol: tdIndex,
                  tableId: tableId
                });
                config.userAttrConfig.content = td.value;
                config.userInputConfig.textAlign = td.textAlign || '';
                config.userInputConfig.w = td.w;
                config.userInputConfig.h = td.h;
                td.h && (config.userInputConfig.h = td.h);
                config.row = td.rowspan || 1;
                config.col = td.colspan || 1;
                return config;
              });
            });
            this.tableList = eleConfig.child;
            this.layout.push(eleConfig);
            addEle.push(eleConfig);
          });
        } else {
          if (html[i].innerHTML.search(/\w/g) > -1 || html[i].innerText) {
            //判断当非表格元素无内容时，舍弃该元素 -20210623
            let tdStyle: any = getComputedStyle(html[i]);
            let tdWidth = tdStyle.width.indexOf('%') !== -1 ? 0 : parseFloat(tdStyle.width);
            let tdHeight = tdStyle.height.indexOf('%') !== -1 ? 0 : parseFloat(tdStyle.height);
            let eleConfig = {
              ...htmlInfo,
              userInputConfig: { ...htmlUserInputConfig },
              userAttrConfig: { ...htmlUserAttrConfig },
              // config: [...htmlConfig],
              dataSource: null,
              i: utils.createRandomId(),
              choosed: false
            };
            eleConfig.userAttrConfig.content = html[i].outerHTML;
            eleConfig.userInputConfig.w = tdWidth.toString();
            // 元素高度+上下padding+10 = 元素最终h
            eleConfig.userInputConfig.h = (tdHeight + 20).toString();
            eleConfig.userInputConfig.positionAlign = 'center';
            eleConfig.userInputConfig.textAlign = 'center';
            eleConfig.userInputConfig.x = 0;
            eleConfig.userInputConfig.y =
              i > 0 ? this.layout[this.layout.length - 1].userInputConfig.y + this.layout[this.layout.length - 1].userInputConfig.h : 0;
            this.layout.push(eleConfig);
            console.log(this.layout);
            addEle.push(eleConfig);
          }
        }
      }
      // console.log(addEle);
      if (addEle.length) {
        this.finishAnalysis = false;
        (document as any).getElementById('wordContainer').innerHTML = '';
      }
    },
    formatWordToTable(data: any) {
      let row = data.table.length;
      let col = 0;
      let copyTable = data.table;
      // let copyTable = [...tableList];
      data.table.forEach((tr: any, index: number) => {
        tr.forEach((td: any, tdIndex: number) => {
          // if (index === 0) {
          //   col += td.colspan || 1;
          // }
          if (td.rowspan > 1) {
            for (let i = 1; i < td.rowspan; i++) {
              copyTable[index + i].splice(tdIndex, 0, {
                colspan: 1,
                rowspan: 1,
                w: 0,
                h: 0,
                value: '',
                i: utils.createRandomId(),
                merged: td.i
              });
              for (let j = 1; j < td.colspan; j++) {
                copyTable[index + i].splice(tdIndex + j, 0, {
                  colspan: 1,
                  rowspan: 1,
                  w: 0,
                  h: 0,
                  value: '',
                  i: utils.createRandomId(),
                  merged: td.i
                });
              }
            }
          }
        });
        col = tr.length > col ? tr.length : col;
      });
      let wordContainer: any = document.getElementById('wordContainer');
      let allWidth: string = window.getComputedStyle(wordContainer, null).width || '';
      let sWidth = (parseInt(allWidth) - 20) / (col + 1);
      copyTable.forEach((tr: any) => {
        tr.forEach((td: any) => {
          if (!td.merged) {
            td.w = td.colspan * sWidth;
          }
        });
      });
      data.table = copyTable;
      data.row = row;
      data.col = col;
      return data;
    },
    /**
     * 选择子元素
     */
    chooseChildEle(ele: any, multiple: boolean) {
      // 旧数据域不存在解析方向字段，需要增加
      if (ele.dataSource && ele.dataSource.columnConfig.dealDirection === undefined) {
        this.$set(ele.dataSource.columnConfig, 'dealDirection', '');
      }
      this.layout.forEach((elem: any) => {
        if (ele.tableId === elem.i) {
          this.currTableConfig = elem;
        }
      });
      if (multiple) {
        // 多选，按Ctrl选=>现在改为可圈选，所以设置为已选中的再次点击，不取消选中效果
        if (!ele.choosed) {
          ele.choosed = true;
          this.choosedLayout.push(ele);
        }
      } else {
        // 选中一个取消·其他选中，不按Ctrl选
        this.choosedLayout.forEach((element: any) => {
          element.choosed = false;
        });
        ele.choosed = true;
        this.choosedLayout = [ele];
      }

      if (this.choosedLayout.length == 0) {
        this.eleEdit = {
          ...this.eleEdit,
          dataSource: {
            columnConfig: {
              columnName: '',
              tableHead: 0,
              tableLength: 0,
              dealDirection: 0
            },
            tableName: '',
            tableHead: 0,
            tableLength: 0,
            startRow: 0,
            startCol: 0
          }
        };
      } else if (this.choosedLayout.length == 1) {
        this.eleEdit = ele;
        /* this.eleEdit = {
          ...this.eleEdit,
          dataSource: {
            columnConfig: {
              columnName: '',
              tableHead: 0,
              tableLength: 0,
              dealDirection: 0
            },
            tableName: '',
            tableHead: 0,
            tableLength: 0,
            startRow: 0,
            startCol: 0
          },
          ...ele
        }; */
      } else {
        //提取公共样式，全局通用编辑
        let config: any = {
          userInputConfig: utils.deepCopy(this.choosedLayout[0].userInputConfig),
          userAttrConfig: utils.deepCopy(this.choosedLayout[0].userAttrConfig),
          config: utils.deepCopy(this.choosedLayout[0].config) || []
        };
        config.type = 'commonEdit';
        for (const key in config.userInputConfig) {
          if (config.userInputConfig.hasOwnProperty(key)) {
            config.userInputConfig[key] = '';
            for (let i = 1; i < this.choosedLayout.length; i++) {
              if (!this.choosedLayout[i].userInputConfig.hasOwnProperty(key)) {
                delete config.userInputConfig[key];
                break;
              }
            }
          }
        }
        //提取公共属性
        for (const key in config.userAttrConfig) {
          if (config.userAttrConfig.hasOwnProperty(key)) {
            config.userAttrConfig[key] = '';
            for (let i = 1; i < this.choosedLayout.length; i++) {
              if (!this.choosedLayout[i].userAttrConfig.hasOwnProperty(key)) {
                let index = config.config.findIndex((item: any) => {
                  return item.prop == key;
                });
                if (index != -1) {
                  config.config.splice(index, 1);
                }
                delete config.userAttrConfig[key];
                break;
              }
            }
          }
        }
        this.eleEdit = {
          ...this.eleEdit,
          dataSource: {
            columnConfig: {
              columnName: '',
              tableHead: 0,
              tableLength: 0,
              dealDirection: 0
            },
            tableName: '',
            tableHead: 0,
            tableLength: 0,
            startRow: 0,
            startCol: 0
          },
          ...config
        };
      }
      // (this.$refs.dataSource as any).initDataSource(this.eleEdit.dataSource);
    },
    chooseTableEle(config: any) {
      //选择的表格配置
      this.currTableConfig = config;
    },
    // 获取节点数据中数据源绑定的key值
    getColumnNameFromKey(ele: any) {
      if (ele.userAttrConfig.key) {
        let nameArr = ele.userAttrConfig.key.split('/');
        return nameArr[nameArr.length - 1];
      }
      return '';
    },
    //清空选择内容
    chooseEleClear() {
      this.choosedLayout.forEach((element: any) => {
        element.choosed = false;
      });
      this.choosedLayout = [];
    },
    filterSChar(s: any) {
      let rs = '';
      let pattern = new RegExp('[，；,;、：～\r\n.]');
      for (let i = 0; i < s.length; i++) {
        rs = rs + s.substr(i, 1).replace(pattern, '');
      }
      return rs;
    }
  }
});
</script>
<style lang="scss" scoped>
@import '@/styles/theme.scss';
.wordAnalysis {
  width: 100%;
  height: 100%;
  overflow: hidden;
  .accieveWord {
    width: 100%;
    height: 100%;
    #wordContainer {
      width: calc(100% - 300px);
      height: calc(100% - 40px);
      background: $contBg;
      color: $normalWordsClr;
      overflow: hidden;
      overflow-y: auto;
      overflow-x: auto;
      padding: 5px;
      margin: 0 auto;
    }
    .btn {
      width: 100%;
      height: 40px;
      display: flex;
      // justify-content: flex-end;
      align-items: center;
    }
  }
  .mainPart {
    width: 100%;
    height: 100%;
    display: flex;
    .tablePart {
      flex: 1;
      overflow: hidden;
      overflow-y: auto;
      position: relative;
      background: $contBg;
      padding: 5px;
      color: $normalWordsClr;
    }
    .attributePart {
      width: 300px;
      height: 100%;
      // border-left: 1px solid #fff;
    }
  }
}
</style>
