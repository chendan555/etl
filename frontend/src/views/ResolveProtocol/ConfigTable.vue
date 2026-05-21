<template>
  <base-dialog v-bind="$attrs" v-on="$listeners" @save="saveTableConfig">
    <el-form
      ref="form"
      :model="tableRelationForm"
      label-width="80px"
      size="mini"
      :rules="autoFormRules"
    >
      <el-form-item label="名称" prop="name">
        <el-input placeholder="请输入名称（示例：制表符解析）" v-model="tableRelationForm.name"></el-input>
      </el-form-item>
      <el-form-item label="主表名称" prop="tableName">
        <el-input placeholder="请输入名称（示例：TEST_DATA_20220601）" v-model="tableRelationForm.tableName"></el-input>
      </el-form-item>
      <!-- <el-form-item label="设备类型" prop="deviceType">
        <el-select
          v-model="tableRelationForm.deviceTypeId"
          placeholder="请选择设备类型"
          clearable
          filterable
        >
          <el-option
            v-for="deviceType in deviceTypeList"
            :key="deviceType.value"
            :label="deviceType.label"
            :value="deviceType.value"
          ></el-option>
        </el-select>
      </el-form-item>-->
      <el-form-item label="文件类型" prop="fileType">
        <el-select v-model="tableRelationForm.fileType" placeholder="请选择文件类型" clearable filterable>
          <el-option
            v-for="fileType in fileTypeList"
            :key="fileType.value"
            :label="fileType.label"
            :value="fileType.value"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item
        label="分隔符"
        prop="separator"
        v-show="tableRelationForm.fileType === 'txt' || tableRelationForm.fileType === 'dat'"
      >
        <el-autocomplete
          class="inline-input"
          v-model="crtSeparator"
          :fetch-suggestions="querySearch"
          placeholder="请输入分隔符，请注意符号全角/半角区别"
        ></el-autocomplete>
      </el-form-item>
      <el-form-item label="ID类型" prop="tableIdType">
        <el-select
          v-model="tableRelationForm.tableIdType"
          placeholder="请选择数据表ID类型"
          clearable
          filterable
        >
          <el-option
            v-for="tableIdType in tableIdTypeList"
            :key="tableIdType.value"
            :label="tableIdType.label"
            :value="tableIdType.value"
          ></el-option>
        </el-select>
      </el-form-item>
      <!-- <el-form-item label="UUID字段" prop="idColumnName" v-if="tableRelationForm.tableIdType === '2'">
        <el-input placeholder="请输入字段名（示例：ID）" v-model="tableRelationForm.idColumnName"></el-input>
      </el-form-item>-->
      <el-form-item label="备注" prop="remark">
        <el-input
          type="textarea"
          placeholder="请输入备注（示例：解析协议配置备注）"
          v-model="tableRelationForm.remark"
        ></el-input>
      </el-form-item>
    </el-form>
    <!-- <div class="pzgz">
      <el-tooltip class="item" placement="bottom">
        <div slot="content">字段注释不能为空</div>
        <span>
          <i class="el-icon-question"></i>配置规则
        </span>
      </el-tooltip>
    </div>-->
    <div class="labeldiv">
      <p>列</p>
      <p>字段</p>
    </div>
    <el-tree
      :data="tableIdList"
      default-expand-all
      :expand-on-click-node="false"
      v-if="isShowTableIdList"
    >
      <div class="custom-tree-node" slot-scope="{ node, data }">
        <div class="columndiv">
          <div class="relation">
            <el-input-number v-model="data.columnIndex" :min="1" label="选择列" size="mini"></el-input-number>
            <span class="divide">---</span>
            <el-input size="mini" v-model="data.columnComment" placeholder="输入ID字段注释"></el-input>
            <el-input size="mini" v-model="data.columnName" placeholder="输入Id字段名"></el-input>
          </div>
        </div>
      </div>
    </el-tree>
    <el-tree :data="protocolList" default-expand-all :expand-on-click-node="false">
      <div class="custom-tree-node" slot-scope="{ node, data }">
        <div class="columndiv">
          <div class="relation">
            <el-input-number v-model="data.columnIndex" :min="1" label="选择列" size="mini"></el-input-number>
            <span class="divide">---</span>
            <el-input
              size="mini"
              v-model="data.columnComment"
              @blur="setColumnName(data.columnComment, data.columnIndex)"
              placeholder="输入字段注释"
            ></el-input>
            <el-input size="mini" v-model="data.columnName" placeholder="输入字段名"></el-input>
          </div>
          <div class="treeHandle">
            <el-button
              type="text"
              size="mini"
              icon="el-icon-circle-plus-outline"
              @click="() => append(data)"
            ></el-button>
            <el-button
              type="text"
              size="mini"
              icon="el-icon-delete"
              @click="() => remove(node, data)"
            ></el-button>
          </div>
        </div>
        <div class="matchListdiv" v-if="data.matchList.length">
          <!-- 字符映射值不建子表，暂时删除，包含子表名称、字段注释等内容 -->
          <!-- <div class="childTableName">
            <el-input size="mini" v-model="data.childTableName" placeholder="输入子表名称"></el-input>
          </div>-->
          <div class="columnItem" v-for="(columnItem, index) in data.matchList" :key="index">
            <div class="column">
              <el-input size="mini" v-model="columnItem.matchCode" placeholder="输入匹配字符"></el-input>
              <span class="divide">---</span>
              <el-input size="mini" v-model="columnItem.matchValue" placeholder="输入字典值映射"></el-input>
              <!-- <el-input size="mini" v-model="columnItem.columnComment" placeholder="输入字段注释"></el-input> -->
            </div>
            <div class="handle">
              <el-button
                type="text"
                size="mini"
                icon="el-icon-delete"
                @click="deleteItem(data, index)"
              ></el-button>
            </div>
          </div>
        </div>
      </div>
    </el-tree>
    <div class="handlediv">
      <el-button icon="el-icon-circle-plus-outline" size="mini" @click="addColumn">添加</el-button>
      <el-button icon="el-icon-setting" size="mini" @click="loadConfigFile">导入配置文件</el-button>
      <input
        type="file"
        name="chooseFile"
        id="chooseFile"
        ref="chooseFile"
        accept=".xlsx, .xls"
        @change="handleChange"
        hidden
      >
    </div>
  </base-dialog>
</template>
<script>
/**
 * Create by Leo
 * 文件解析协议配置组件
 * 弹框展示在主界面
 * 分为上下两个部分
 * 上部为基础配置项
 * 下部为文件对应字段及字段注释配置项
 */
import Vue from 'vue';
import BaseDialog from '@/components/BaseDialog/index.vue';
import Pinyin from '@/utils/ChinesePY.js';
import config from './config';
export default Vue.extend({
  name: 'configTable',
  components: { BaseDialog },
  props: {
    /**
     * 当前解析协议数据信息
     */
    relationInfo: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      /**
       * 配置项
       */
      tableRelation: [],
      /**
       * 配置项
       */
      tableRelationForm: {
        id: '',
        name: '',
        tableIdType: '',
        idColumnName: 'ID',
        fileType: '',
        separator: '',
        tableName: '',
        remark: ''
      },
      /**
       * 表单验证
       */
      formRules: {
        name: [{ required: true, message: '请输入协议名称', trigger: 'blur' }],
        tableName: [{ required: true, message: '请输入主表名称', trigger: 'blur' }],
        fileType: [{ required: true, message: '请输入文件类型', trigger: 'blur' }],
        // separator: [{ required: true, message: '请输入分隔符', trigger: 'blur' }],
        tableIdType: [{ required: true, message: '请选择ID类型', trigger: 'blur' }]
      },
      /**
       * 字段及注释配置列表
       */
      protocolList: [
        {
          columnName: '',
          columnComment: '',
          columnIndex: 1,
          // childTableName: '',
          matchList: []
        }
      ],
      /**
       * 字段id配置
       */
      tableIdList: [
        {
          columnName: 'id',
          columnComment: '主键',
          columnIndex: 1
        }
      ],
      /**
       * 当前行数
       */
      crtColumnNum: 1,
      /**
       * 历史行数
       */
      historyColumnNum: 1,
      /**
       * 设备类型列表
       */
      deviceTypeList: config.deviceTypeList,
      /**
       * 文件类型列表
       */
      fileTypeList: config.fileTypeList,
      /**
       * id类型列表
       */
      tableIdTypeList: config.tableIdTypeList,
      /**
       * 分隔符列表
       */
      separatorList: config.separatorList,
      /**
       * 当前分隔符
       */
      crtSeparator: ''
    };
  },
  watch: {
    crtSeparator: {
      handler(val) {
        let separator = this.separatorList.find(ele => ele.value === val);
        if (separator) {
          this.tableRelationForm.separator = separator.label;
        } else {
          this.tableRelationForm.separator = val;
        }
        this.tableRelationForm.separatorName = val;
      }
    }
  },
  computed: {
    autoFormRules() {
      if (this.tableRelationForm.fileType === 'txt' || this.tableRelationForm.fileType === 'dat') {
        return {
          ...this.formRules,
          separator: [{ required: true, message: '请输入分隔符', trigger: 'blur' }]
        };
      } else {
        return this.formRules;
      }
    },
    /**
     * 是否展示字段id配置栏
     */
    isShowTableIdList() {
      if (this.tableRelationForm.tableIdType === '3') {
        this.crtColumnNum++;
      } else {
        this.crtColumnNum = this.protocolList.length;
      }
      return this.tableRelationForm.tableIdType === '3';
    }
  },
  mounted() {
    // console.log(this.relationInfo);
    if (this.relationInfo && this.relationInfo.hasOwnProperty('id')) {
      this.tableRelationForm = this.relationInfo;
      this.crtSeparator = this.relationInfo.separatorName;
      this.protocolList = JSON.parse(this.relationInfo.jsonForm);
    } else {
      this.tableRelationForm = {
        name: '',
        tableIdType: '',
        idColumnName: 'ID',
        fileType: '',
        separator: '',
        // createBy: '',
        remark: ''
      };
    }
  },
  methods: {
    /**
     * @description 根据字段注释自动生成字段名，拼音首字母组成
     * @param comment
     * @param index
     */
    setColumnName(comment, index) {
      // 判断是否有值
      if (comment) {
        let columnName = Pinyin.getWordsCode(comment);
        let colName = columnName;
        let indexNum = 0;
        while (this.protocolList.findIndex(ele => ele.columnName === colName) > -1) {
          indexNum++;
          colName = columnName + indexNum;
        }
        this.$set(this.protocolList[index - 1], 'columnName', colName);
      } else {
        this.$set(this.protocolList[index - 1], 'columnName', '');
      }
    },
    /**
     * @description 选择配置文件
     */
    loadConfigFile() {
      this.$refs.chooseFile.click();
    },
    /**
     * @description 上传解析配置文件
     * @param file
     * @param fileList
     */
    handleChange(file, fileList) {
      let fileContent = file.srcElement.files[0];
      // const fileName = fileContent.name;
      // const fileType = fileName.substring(fileName.lastIndexOf('.') + 1);
      if (fileContent) {
        this.importFile(fileContent);
      }
    },
    /**
     * 读取Excel文件内容
     * @param obj excel文件内容
     */
    importFile(obj) {
      const reader = new FileReader();
      const _this = this;
      reader.readAsArrayBuffer(obj);
      reader.onload = function() {
        let buffer = reader.result;
        let bytes = new Uint8Array(buffer);
        const length = bytes.byteLength;
        let binary = '';
        for (let i = 0; i < length; i++) {
          binary += String.fromCharCode(bytes[i]);
        }
        const XLSX = require('xlsx');
        const wb = XLSX.read(binary, {
          type: 'binary'
        });
        const outdata = XLSX.utils.sheet_to_json(wb.Sheets[wb.SheetNames[0]]);
        let fileData = [...outdata];
        if (fileData.length) {
          _this.protocolList = [];
          fileData.forEach(item => {
            _this.protocolList.push({
              ...item,
              matchList: []
            });
          });
        }
      };
    },
    /**
     * 分割符筛选
     * @param obj
     */
    querySearch(queryString, cb) {
      cb(this.separatorList);
    },
    /**
     * 文件解析协议配置项保存
     */
    saveTableConfig() {
      this.$refs.form.validate(valid => {
        if (valid) {
          /**
           * start
           * 在文件类型为txt或者dat时，判断是否配置分隔符
           * 未配置分割符则返回终止保存操作，并提示用户配置
           * 其他文件类型则继续执行
           */
          if (['txt', 'dat'].includes(this.tableRelationForm.fileType) && !this.tableRelationForm.separator) {
            this.$utils.setMessage(false, '请完成分隔符配置');
            return false;
          }
          /* end */

          /**
           * start
           * 判断是否完成文件解析列序号、字段及字段注释配置
           * 未配置分割符则返回终止保存操作，并提示用户配置
           */
          let bool = true;
          this.protocolList.forEach(ele => {
            if (!ele.columnIndex || !ele.columnName || !ele.columnComment) {
              bool = false;
            }
          });
          if (!bool) {
            this.$utils.setMessage(false, '请完成文件解析列序号、字段及字段注释配置');
            return false;
          }
          /* end */

          // let deviceType = this.deviceTypeList.find(ele => ele.value === this.tableRelationForm.deviceTypeId);
          let fileType = this.fileTypeList.find(ele => ele.value === this.tableRelationForm.fileType);
          let params = {
            ...this.tableRelationForm,
            // deviceType: deviceType.label,
            fileTypeName: fileType.label,
            jsonForm: JSON.stringify(this.protocolList),
            tableId: this.isShowTableIdList ? this.tableIdList[0] : null
          };
          this.$API.resolveProtocol.saveProtocol(params).then(({ data }) => {
            this.$message({
              message: data.result,
              type: data.success ? 'success' : 'warning'
            });
            if (data.success) {
              this.$emit('closeDialog', true);
            }
          });
        } else {
          return false;
        }
      });
    },
    /**
     * 添加行
     */
    addColumn() {
      this.crtColumnNum++;
      this.protocolList.push({
        columnName: '',
        columnComment: '',
        columnIndex: this.crtColumnNum,
        matchList: []
      });
    },
    /**
     * 添加行
     * @param data
     */
    append(data) {
      data.matchList.push({
        matchCode: '',
        matchValue: ''
        // columnComment: ''
      });
    },
    /**
     * 移除
     * @param node
     * @param data
     */
    remove(node, data) {
      const parent = node.parent;
      const children = parent.data.children || parent.data;
      const index = children.findIndex(d => d.columnIndex === data.columnIndex);
      children.splice(index, 1);
    },
    /**
     * 删除
     * @param data
     * @param index
     */
    deleteItem(data, index) {
      data.matchList.splice(index, 1);
    }
  }
});
</script>
<style lang="scss" scoped>
::v-deep .el-form {
  .el-select,
  .el-autocomplete {
    width: 100%;
  }
}
::v-deep .el-tree-node__content {
  height: auto;
}
.labeldiv {
  width: 100%;
  height: 40px;
  line-height: 40px;
  display: flex;
  color: #fff;
  > p {
    margin-right: 130px;
    margin-left: 24px;
  }
}
::v-deep .el-tree {
  background: transparent;
  .custom-tree-node {
    width: 100%;
    .columndiv {
      width: 100%;
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin: 5px 0;
      .relation {
        color: #fff;
        .divide {
          margin: 0 10px;
        }
        > .el-input {
          width: 200px;
          margin-right: 10px;
        }
      }
      .treeHandle {
        padding: 0 10px;
        ::v-deep .el-button {
          color: #fff;
        }
      }
    }
    .matchListdiv {
      padding-left: 24px;
      color: #fff;
      .childTableName {
        width: 100%;
        margin-bottom: 5px;
        .el-input {
          width: 443px;
        }
      }
      .columnItem {
        width: 100%;
        margin-bottom: 5px;
        display: flex;
        align-items: center;
        justify-content: space-between;
        .column {
          .divide {
            margin: 0 10px;
          }
          .el-input {
            width: 200px;
            margin-right: 5px;
          }
        }
        .handle {
          width: 40px;
          display: flex;
          justify-content: flex-end;
          padding: 0 10px;
          .el-button {
            color: #fff;
          }
        }
      }
    }
  }
}
.handlediv {
  padding-left: 24px;
  margin-top: 10px;
  display: flex;
  ::v-deep .el-button {
    margin-right: 10px;
  }
}
.pzgz {
  display: flex;
  align-items: flex-end;
  justify-content: flex-end;
  color: #fff;
}
</style>
