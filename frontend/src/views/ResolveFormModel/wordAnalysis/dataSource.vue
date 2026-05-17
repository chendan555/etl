<template>
  <div class="dataSource">
    <!-- <div class="dataSource-head">
      <span class="title">配置数据源</span>
    </div>-->
    <div class="dataSource-body">
      <el-form label-width="100px">
        <el-form-item label="解析模板类型">
          <el-input type="input" size="mini" v-model="formName" @change="setTableName"></el-input>
        </el-form-item>
        <el-form-item label="数据表名称">
          <el-input type="input" size="mini" v-model="tableChnName" @change="setTableName"></el-input>
          <!-- <el-select
            filterable
            allow-create
            size="mini"
            v-model="tableChnName"
            @change="setTableName"
          >
            <el-option
              v-for="(item,index) in tableName"
              :key="index"
              :label="item.tableName"
              :value="item.tableName"
            ></el-option>
          </el-select>-->
        </el-form-item>
        <el-form-item label="字段注释">
          <el-input
            type="input"
            size="mini"
            v-model="eleEdit.dataSource.columnConfig.columnComment"
            @change="setColumnName"
          ></el-input>
          <!-- <el-select
            filterable
            allow-create
            size="mini"
            v-model="eleEdit.dataSource.columnConfig.columnComment"
            @change="setColumnName"
          >
            <el-option
              v-for="(item,index) in tableColumn"
              :key="index"
              :label="item.columnComment || item.columnName"
              :value="item.columnName"
            ></el-option>
          </el-select>-->
        </el-form-item>
        <el-form-item label="字段名称">
          <el-input
            disabled
            type="input"
            size="mini"
            v-model="eleEdit.dataSource.columnConfig.columnName"
            @blur="changekey"
          ></el-input>
        </el-form-item>
        <el-form-item label="数据行(列)数">
          <el-input
            :disabled="!tableChnName"
            clearable
            type="input"
            v-model.number="eleEdit.dataSource.tableLength"
            size="mini"
            @blur="emitDefaultLength"
          />
        </el-form-item>
        <el-form-item label="解析方向">
          <el-radio-group
            clearable
            :disabled="!tableChnName"
            v-model="eleEdit.dataSource.columnConfig.dealDirection"
          >
            <el-radio :label="0">纵向</el-radio>
            <el-radio :label="1">横向</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
    </div>
    <div class="handlediv">
      <div style="display: flex;margin-top: 23px;">
        <el-button
          style="flex: 1;"
          type="primary"
          size="mini"
          @click="autoConfigColumnName(true)"
        >配置单列名</el-button>
        <el-button
          style="flex: 1;"
          type="primary"
          size="mini"
          @click="autoConfigtableHeaderName"
        >配置表头</el-button>
        <el-button
          style="flex: 1;"
          type="primary"
          size="mini"
          @click="autoConfigColumnName(false)"
        >配置列名</el-button>
      </div>
      <div class="save" style="margin-top: 10px;display: flex;">
        <el-button style="flex: 1;" type="info" size="mini" @click="deleteCrtColumns(false)">清除当前列表名</el-button>
        <!-- <el-button style="flex: 1;" type="info" size="mini" @click="deleteColumns">一键清除所有列表名</el-button> -->
      </div>
      <slot></slot>
    </div>
    <!-- <div class="introdiv">
      <p class="title">数据源配置说明</p>
      <ul>
        <li>配置单列名：用于配置单列表名，快捷键为ALT+Q；</li>
        <li>配置列名：用于配置多列表名，快捷键为ALT+~；</li>
        <li>配置表头：用于配置表格中多行列表表头，快捷键为ALT+1；</li>
        <li>快捷键ALT+2：用于配置表格中多行列表的双行表头；</li>
        <li>快捷键ALT+X：可以实现左侧单元格和上侧单元格组合列表名；</li>
        <li>不同单元格内容组合列名或表头名称建议使用“-”或“_”符号连接。</li>
      </ul>
    </div>-->
  </div>
</template>

<script>
import utils from '@/utils/index';
// import Pinyin from '@/utils/ChinesePY.js';
// import { formSaveConfigApi, ipConfigApi } from '@/api/system-manage';
export default {
  name: 'dataSource',
  props: {
    eleEdit: Object,
    boxConfig: Object,
    layout: Array,
    currTableConfig: Object,
    eleList: {
      type: Array,
      default() {
        return [];
      }
    },
    itemFormName: String,
    itemTableName: String
  },
  data() {
    return {
      showTableName: false,
      isCreateTableOprions: [
        //表名
        { tableComment: '是', tableName: '0' },
        { tableComment: '否', tableName: '1' }
      ],
      columnName: [], //列表名
      dataType: '', //数据类型
      source: [{ key: 0, value: '现有表' }, { key: 1, value: '新建表' }],
      tableName: [],
      tableChnName: this.itemTableName || '', // 自定义表名称
      formName: this.itemFormName || '',
      tableColumn: [], // 数据库中字段名及注释
      defaultTableLength: 0,
      tableSchemaActive: '',
      tableSchema: []
    };
  },
  computed: {
    columnNameList() {
      let result = [];
      this.eleList.forEach(comp => {
        if (comp.hasOwnProperty('dataSource') && comp.dataSource && comp.dataSource.columnConfig.columnName) {
          result.push(this.pushColunName(comp.dataSource.columnConfig.columnName));
        }
        if (comp.hasOwnProperty('child') && comp.child.length) {
          comp.child.forEach(tr => {
            if (utils.checkDataType(tr) === 'Array') {
              tr.forEach(td => {
                if (td.hasOwnProperty('dataSource') && td.dataSource.columnConfig.columnName) {
                  result.push(this.pushColunName(td.dataSource.columnConfig.columnName));
                }
                if (td.child && td.child.length) {
                  td.child.forEach(el => {
                    if (el.hasOwnProperty('dataSource') && el.dataSource.columnConfig.columnName) {
                      result.push(this.pushColunName(el.dataSource.columnConfig.columnName));
                    }
                  });
                }
              });
            }
          });
        }
      });
      return result;
    }
  },
  mounted() {
    console.log(this.itemFormName);
    console.log(this.itemTableName);
    this.$eventBus.$on('autoConfigColumnName', data => {
      this.autoConfigColumnName(data);
    });
    this.$eventBus.$on('autoConfigSingleColumnName', data => {
      this.autoConfigSingleColumnName(data);
    });
    this.$eventBus.$on('autoConfigtableHeaderName', () => {
      this.autoConfigtableHeaderName();
    });
    this.$eventBus.$on('configSpecialHeaderName', () => {
      this.configSpecialHeaderName();
    });
    this.$eventBus.$on('setDsTableName', name => {
      this.tableChnName = name;
    });
  },
  destroyed() {
    this.$eventBus.$off('autoConfigColumnName');
    this.$eventBus.$off('autoConfigSingleColumnName');
    this.$eventBus.$off('autoConfigtableHeaderName');
    this.$eventBus.$off('configSpecialHeaderName');
  },
  created() {
    /* ipConfigApi
      .all()
      .then(res => {
        this.tableSchema = res.data;
      })
      .then(() => {
        if (this.boxConfig.userInputConfig.tableSchema) {
          this.tableSchemaActive = this.boxConfig.userInputConfig.tableSchema;
        }
      })
      .then(() => {
        this.setTableName();
      }); */
    // this.getTableList();
  },
  methods: {
    setTableSchema() {
      this.boxConfig.userInputConfig.tableSchema = this.tableSchemaActive;
      // this.getTableList();
    },
    getTableList() {
      /* this.$axios
        .get('/databaseTable/getTableName', {
          tableSchema: this.boxConfig.userInputConfig.tableSchema
        })
        .then(res => {
          console.log(res.data);
          this.tableName = res.data;
        }); */
    },
    getRowCol() {
      let { indexRow, indexCol } = this.eleEdit;
      if (indexRow === undefined) {
        let crtComp = {};
        try {
          this.eleList.forEach(ele => {
            if (ele.i === this.eleEdit.i) {
              crtComp = ele;
              throw new Error();
            }
          });
        } catch (err) {
          indexRow = crtComp.child[this.eleEdit.parentTable.x][this.eleEdit.parentTable.y].indexRow;
          indexCol = crtComp.child[this.eleEdit.parentTable.x][this.eleEdit.parentTable.y].indexCol;
        }
      }
      return { indexRow, indexCol };
    },
    // 获取横向加上方一单元格内容
    autoConfigSingleColumnName(single) {
      if (!this.tableChnName) {
        this.$message.warning('请先设置表名！');
        return;
      }
      let { indexRow, indexCol } = this.getRowCol();
      let indexColTemp = indexCol;
      // let indexRowTemp = indexRow;
      let columnName = '';
      let dealIds = [];
      if (indexColTemp-- && !single) {
        do {
          let id = this.currTableConfig.child[indexRow][indexColTemp].i;
          if (this.currTableConfig.child[indexRow][indexColTemp].merged) {
            id = this.currTableConfig.child[indexRow][indexColTemp].merged;
          }
          if (dealIds.includes(id)) {
            continue;
          }
          let innerText = document.getElementById(id).innerText.trim();
          if (innerText) {
            columnName = `${this.filterSChar(innerText)}_${columnName}`;
            dealIds.push(id);
          }
          if (!innerText && indexCol - indexColTemp > 1) {
            indexColTemp = -1;
          }
        } while (--indexColTemp > -1);
      }
      /* if (indexRowTemp--) {
        do {
          if (
            this.currTableConfig.child[indexRowTemp][indexCol].dataSource &&
            this.currTableConfig.child[indexRowTemp][indexCol].dataSource
              .tableLength == 0
          ) {
            let innerText = document
              .getElementById(
                this.currTableConfig.child[indexRowTemp][indexCol].i
              )
              .innerText.trim();
            if (innerText) {
              columnName = `${columnName}-${this.filterSChar(innerText)}`;
              break;
            }
            if (!innerText && indexRow - indexRowTemp > 1) {
              indexRowTemp = -1;
            }
          }
        } while (--indexRowTemp > -1 && !single);
      } */
      // 上一个单元格内容
      let upId = this.currTableConfig.child[indexRow - 1][indexCol].i;
      let upInnerText = document.getElementById(upId).innerText.trim();
      columnName = `${columnName}_${upInnerText}`;
      if (columnName.endsWith('_')) {
        columnName = columnName.substr(0, columnName.length - 1);
      }
      this.setColumnName(columnName);
      this.changekey();
      indexRow = undefined;
    },
    autoConfigColumnName(single) {
      if (!this.tableChnName) {
        this.$message.warning('请先设置表名！');
        return;
      }
      let { indexRow, indexCol } = this.getRowCol();
      let indexColTemp = indexCol;
      // let indexRowTemp = indexRow;
      let columnName = '';
      let dealIds = [];
      if (indexColTemp-- && !single) {
        do {
          let id = this.currTableConfig.child[indexRow][indexColTemp].i;
          if (this.currTableConfig.child[indexRow][indexColTemp].merged) {
            id = this.currTableConfig.child[indexRow][indexColTemp].merged;
          }
          if (dealIds.includes(id)) {
            continue;
          }
          let innerText = document.getElementById(id).innerText.trim();
          if (innerText) {
            columnName = `${this.filterSChar(innerText)}_${columnName}`;
            dealIds.push(id);
          }
        } while (--indexColTemp > -1);
      }
      if (columnName.endsWith('_')) {
        columnName = columnName.substr(0, columnName.length - 1);
      }
      this.setColumnName(columnName);
      this.changekey();
      indexRow = undefined;
    },
    autoConfigtableHeaderName() {
      this.configSpecialHeaderName();
      let { indexRow, indexCol } = this.eleEdit;
      let id = this.currTableConfig.child[indexRow][indexCol].i;
      let innerText = document.getElementById(id).innerText.trim();
      this.setColumnName(innerText);
      this.eleEdit.dataSource.tableLength = this.defaultTableLength || 1;
      this.eleEdit.dataSource.columnConfig.dealDirection = 0;
      this.changekey();
    },
    configSpecialHeaderName() {
      let { indexRow, indexCol } = this.eleEdit;
      if (indexRow > 0) {
        let id = this.currTableConfig.child[indexRow][indexCol].i;
        let innerText = document.getElementById(id).innerText.trim();
        let upTd = this.currTableConfig.child[indexRow - 1][indexCol];
        let upId = upTd.merged || upTd.i;
        let upText = document.getElementById(upId).innerText.trim();
        this.setColumnName(`${upText}-${innerText}`);
        this.eleEdit.dataSource.tableLength = this.defaultTableLength || 1;
        this.eleEdit.dataSource.columnConfig.dealDirection = 0;
        this.changekey();
      }
    },
    // 根据获取的名称设置当前列表名，并判断是否重复
    async setColumnName(columnName) {
      let columnObj = this.tableColumn.find(column => column.columnName === columnName);
      let resColumnName = '';
      let columnComment = '';
      if (columnObj) {
        resColumnName = columnObj.columnName;
        columnComment = columnObj.columnComment;
      } else {
        // resColumnName = Pinyin.getWordsCode(columnName);
        columnComment = columnName;
        await this.checkColumnName(resColumnName);
      }
      this.$nextTick(() => {
        this.eleEdit.dataSource.columnConfig.columnName = resColumnName;
        this.eleEdit.dataSource.columnConfig.columnComment = columnComment || resColumnName;
        this.changekey();
      });
    },
    checkColumnName(resColumnName) {
      if (this.columnNameList.length) {
        if (this.columnNameList.includes(resColumnName)) {
          this.$message.warning(`列表名汉字首字母${resColumnName}重复，请调整！`);
        }
      }
    },
    filterSChar(s) {
      let rs = '';
      let pattern = new RegExp('[，；,;、：～\r\n.]');
      for (let i = 0; i < s.length; i++) {
        rs = rs + s.substr(i, 1).replace(pattern, '');
      }
      return rs;
    },
    /**
     * 设置数据表表名称
     */
    setTableName() {
      let params = {
        formName: this.formName,
        tableName: this.tableChnName
      };
      this.$emit('setTableName', params);
      this.$eventBus.$emit('setTableName', this.tableChnName);
      /* this.$axios
        .get('/databaseTable/getTableColumns', {
          tableName: this.tableChnName,
          tableSchema: this.boxConfig.userInputConfig.tableSchema
        })
        .then(res => {
          this.tableColumn = res.data;
          this.eleEdit.dataSource.columnConfig.columnName = '';
        }); */
    },
    // setColumnName() {
    //   this.eleEdit.dataSource.columnConfig.columnName = Pinyin.getWordsCode(
    //     this.eleEdit.dataSource.columnConfig.columnComment
    //   );
    // },
    changekey() {
      this.eleEdit.userAttrConfig.key = `${this.tableChnName}/${this.eleEdit.dataSource.columnConfig.columnComment || ''}/${
        this.eleEdit.dataSource.columnConfig.columnName
      }`;
      // if (this.boxConfig.userInputConfig.tableSchema && this.tableChnName && this.eleEdit.dataSource && this.eleEdit.dataSource.columnConfig.columnComment) {
      //   // this.eleEdit.userAttrConfig.key =
      //   //   this.tableChnName +
      //   //   "/" +
      //   //   this.eleEdit.dataSource.columnConfig.columnComment;
      // } else {
      //   this.eleEdit.userAttrConfig && (this.eleEdit.userAttrConfig.key = '');
      // }
    },
    emitDefaultLength() {
      this.defaultTableLength = this.eleEdit.dataSource.tableLength;
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
          console.log(this.layout);
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
    deleteColumnName(compList) {
      compList.forEach(comp => {
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
    deleteCrtColumns(ele) {
      ele = ele || this.eleEdit;
      ele.dataSource.columnConfig.columnName = '';
      ele.dataSource.columnConfig.columnComment = '';
      ele.dataSource.columnConfig.dealDirection = '';
      ele.dataSource.tableLength = 0;
      delete ele.userAttrConfig.key;
    },
    pushColunName(data) {
      let patt = /.*[\u4e00-\u9fa5]+.*$/g;
      /*  if (patt.test(data)) {
        return Pinyin.getWordsCode(data);
      } else {
        return data;
      } */
    }
  }
};
</script>

<style lang="scss" scoped>
.dataSource {
  padding: 5px;
  &-head {
    text-align: center;
    .title {
      color: #606266;
      font-size: 14px;
    }
  }
  &-body {
    margin-top: 10px;
    /deep/ textarea {
      height: 200px;
    }
  }
  .introdiv {
    width: 100%;
    margin-top: 50px;
    .title {
      font-size: 14px;
      line-height: 28px;
    }
    ul li {
      line-height: 20px;
    }
  }
}
</style>
