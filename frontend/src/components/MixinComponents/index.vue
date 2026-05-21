<template>
  <div class="mixin-components">
    <!-- 文本展示 -->
    <el-input
      v-if="type === 'text'"
      :type="inputType"
      :style="{ width: width }"
      v-model="ruleForm[valueKey]"
      :disabled="disabled"
    ></el-input>
    <!-- 按钮 -->
    <el-button v-if="type === 'button'" @click="liveFunction(valueKey)">点击</el-button>
    <!-- 输入框 -->
    <el-input
      v-if="type === 'input'"
      :type="inputType"
      :style="{ width: width }"
      @change="changeValue"
      v-model="ruleForm[valueKey]"
      :disabled="disabledKey ? !ruleForm[disabledKey] : disabled"
      :placeholder="placeholder || '请输入'"
    ></el-input>
    <!-- 文本域 -->
    <el-input
      v-if="type === 'textarea'"
      :style="{ width: width }"
      type="textarea"
      v-model="ruleForm[valueKey]"
      :maxlength="500 / 2"
      :disabled="disabled"
    ></el-input>
    <!-- 单个复选框 -->
    <el-checkbox
      v-if="type === 'checkbox'"
      v-model="ruleForm[valueKey]"
      @change="handleCheckboxSelect"
      :disabled="disabled"
    ></el-checkbox>
    <!-- 日期-->
    <el-date-picker
      v-else-if="type === 'date' || type === 'datetime'"
      :style="{ width: width }"
      :type="type"
      v-model="ruleForm[valueKey]"
      size="small"
      :value-format="valueFormat"
      :format="valueFormat"
      :disabled="disabled"
    ></el-date-picker>
    <!-- 年度-->
    <el-date-picker
      v-else-if="type === 'year'"
      :style="{ width: width }"
      type="year"
      v-model="ruleForm[valueKey]"
      size="small"
      :disabled="disabled"
    ></el-date-picker>
    <!-- 下拉框-->
    <el-select
      :style="{ width: width }"
      v-else-if="type === 'select' || type === 'selectMultiple'"
      :multiple="type === 'selectMultiple'"
      v-model="ruleForm[valueKey]"
      :disabled="disabled"
      filterable
      allow-create
      clearable
      placeholder="请选择"
      @change="changeValue"
      @visible-change="visibleChange"
    >
      <el-option
        v-for="(item) in selectOptions"
        :key="item[optionsConfig.value]"
        :label="item[optionsConfig.name]"
        :value="item[optionsConfig.value]"
      ></el-option>
    </el-select>
    <!-- 单选下拉框-->
    <el-select
      :style="{ width: width }"
      v-else-if="type === 'singleSelect'"
      v-model="ruleForm[valueKey]"
      :disabled="disabled"
      filterable
      clearable
      placeholder="请选择"
      @change="changeValue"
      @visible-change="visibleChange"
    >
      <el-option
        v-for="(item) in selectOptions"
        :key="item[optionsConfig.value]"
        :label="item[optionsConfig.name]"
        :value="item[optionsConfig.value]"
      ></el-option>
    </el-select>
    <!-- 单选框-->
    <el-radio-group
      :style="{ width: width }"
      v-else-if="type === 'radio'"
      :disabled="disabled"
      @change="changeValue"
      v-model="ruleForm[valueKey]"
    >
      <el-radio
        v-for="item in selectOptions"
        :key="item[optionsConfig.value]"
        :label="item[optionsConfig.value]"
      >{{ item[optionsConfig.name] }}</el-radio>
    </el-radio-group>
    <!-- 复选框 -->
    <el-checkbox-group v-else-if="type === 'checkBoxBtn'" v-model="ruleForm[valueKey]">
      <el-checkbox-button
        v-for="(item, index) in selectOptions"
        :key="index"
        :label="item[optionsConfig.value]"
      >{{ item[optionsConfig.name] }}</el-checkbox-button>
    </el-checkbox-group>
    <!-- 开关-->
    <el-switch
      v-else-if="type === 'switch'"
      @change="changeValue"
      :disabled="disabled"
      v-model="ruleForm[valueKey]"
    ></el-switch>
    <!-- 弹框树 -->
    <div v-else-if="databasePickVisible">
      <el-input
        :id="treeConfig.inputId"
        :style="{ width: width }"
        :placeholder="placeholder"
        v-model="ruleForm[nameKey]"
        disabled
      >
        <el-button slot="append" @click="openDialog('baseTreeDialog')" size="small">选择</el-button>
      </el-input>
      <base-dialog-tree
        ref="baseTreeDialog"
        :treeData="treeData"
        :treeConfig="treeConfig"
        :value="ruleForm[valueKey]"
        @change="getTreeSelected"
      />
    </div>
    <!-- 上传 -->
    <div v-else-if="type === 'upload'">
      <base-upload-file :disabled="disabled" btn-name="上传附件" @uploadFile="uploadFile"/>
    </div>
    <!-- 选择文件 -->
    <div v-else-if="type === 'file'">
      <el-input placeholder="请选择文件" v-model="ruleForm[valueKey]" clearable>
        <el-button slot="append" icon="el-icon-plus" @click="chooseFile"></el-button>
      </el-input>
      <input id="file" type="file" ref="fileRef" @change="handleSelectedFiles" :accept="fileType">
      <!-- :value="ruleForm[valueKey]" -->
    </div>
    <!-- 选择文件 -->
    <div v-else-if="type === 'folder'">
      <el-input placeholder="请选择文件" v-model="ruleForm[valueKey]" clearable>
        <el-button slot="append" icon="el-icon-plus" @click="chooseFolder"></el-button>
      </el-input>
      <input
        id="folder"
        type="file"
        ref="folderRef"
        webkitdirectory
        directory
        :value="ruleForm[valueKey]"
        @change="handleSelectedFolders"
      >
    </div>
    <!-- 自动生成时间 -->
    <el-input
      v-else-if="type === 'autoTime'"
      :type="inputType"
      :style="{ width: width }"
      @change="changeValue"
      v-model="ruleForm[valueKey]"
      disabled="disabled"
    ></el-input>
    <!-- 带验证的输入框 -->
    <el-input
      v-else-if="databaseAppendVisible"
      :placeholder="placeholder"
      :type="inputType"
      :style="{ width: width }"
      v-model="ruleForm[valueKey]"
      :disabled="disabledKey ? !ruleForm[disabledKey] : disabled"
    >
      <el-button slot="append" icon="el-icon-s-check" @click="checkValue">{{ btnName }}</el-button>
    </el-input>
    <!-- 可新增的标签 -->
    <div v-else-if="type == 'newTags'">
      <el-tag
        :key="tag"
        v-for="tag in ruleForm[valueKey]"
        closable
        :disable-transitions="false"
        @close="handleClose(tag)"
      >{{ tag }}</el-tag>
      <el-input
        class="input-new-tag"
        v-if="inputVisible"
        v-model="inputValue"
        ref="saveTagInput"
        size="small"
        @keyup.enter.native="handleInputConfirm"
        @blur="handleInputConfirm"
      ></el-input>
      <el-button v-else class="button-new-tag" size="small" @click="showInput">+ 添加</el-button>
    </div>
    <!-- 计数器 -->
    <el-input-number
      v-else-if="type === 'inputnumber'"
      v-model="ruleForm[valueKey]"
      :min="inputLength.min"
      :max="inputLength.max"
    ></el-input-number>
    <!-- 数值范围 -->
    <div class="range" v-else-if="type == 'numberrange'">
      <el-input :type="inputType" :style="{ width: width }" v-model="ruleForm[valueKey][0]"></el-input>
      <span>~</span>
      <el-input :type="inputType" :style="{ width: width }" v-model="ruleForm[valueKey][1]"></el-input>
    </div>
    <!-- 树形数据下拉框 -->
    <div class="cascaderdiv" v-else-if="type == 'cascader'">
      <el-cascader
        v-model="ruleForm[valueKey]"
        :options="selectOptions"
        :props="lazyLoadProps"
        filterable
        clearable
        @change="changeValue"
      ></el-cascader>
    </div>
    <div class="treeSelect" v-else-if="type == 'treeSelect'">
      <TreeSelect
        :value="ruleForm[valueKey]"
        :treeProps="treeProps"
        :loadNode="loadNode"
        :options="options"
        :lazy="lazy"
        :width="width"
        :selectOptions="selectOptions"
        :disabled="disabled"
        @getSelectValue="getSelectValue"
      ></TreeSelect>
    </div>
  </div>
</template>
<script>
/**
 *
 * Create by Leo
 * 基础组件
 * 基础表单元素组件
 * 左右两栏式布局
 * 坐侧区域可控制显隐
 * 并减少一定的代码量
 * 统一设置弹框的配置项、确认按钮、取消按钮、关闭按钮等信息
 *
 */

import BaseDialogTree from '@/components/BaseDialogTree';
import BaseUploadFile from '@/components/BaseUploadFile';
import TreeSelect from './treeSelect';
import model from '@/FlowChart/model.js';
import API from '@/api/index';
// import { Message } from 'element-ui';
export default {
  name: 'mixinComponents',
  components: { BaseDialogTree, BaseUploadFile, TreeSelect },
  props: {
    // 树形结构配置项
    /**
     * 配置项
     */
    treeProps: {
      type: Object,
      default: () => {
        return {};
      }
    },
    // 树形结构懒加载方法
    /**
     * 配置项
     */
    loadNode: {
      type: Function
    },
    // 树形结构懒加载
    /**
     * 配置项
     */
    lazy: {
      type: Boolean,
      default: false
    },
    // 级联控件懒加载方法
    /**
     * 配置项
     */
    lazyLoadProps: {
      type: Object,
      default: () => {
        return {};
      }
    },
    // 示例
    // lazyLoadProps: {
    //   lazy: true,
    //   checkStrictly: true,
    //   lazyLoad(node, resolve) {
    //     let params = {
    //       path: node.hasOwnProperty('data') ? node.data.value : '/'
    //     };
    //     API.getFilesName(params).then(res => {
    //       res.data.forEach(ele => {
    //         if (ele.fileType === 'FILE') {
    //           ele.leaf = true;
    //         }
    //       });
    //       resolve(res.data);
    //     });
    //   }
    // }
    // 表单对象
    /**
     * 配置项
     */
    ruleForm: {
      type: Object | Array,
      default: () => {
        return {};
      }
    },
    // 表单key值
    /**
     * 配置项
     */
    valueKey: {
      type: String,
      default: ''
    },
    // 当输入框是否禁用取决于前一项checkbox的选中情况时，需要传入此值
    /**
     * 配置项
     */
    disabledKey: {
      type: String,
      default: ''
    },
    // 用于树形等回显名称key值
    /**
     * 配置项
     */
    nameKey: {
      type: String,
      default: ''
    },
    // 树形组件配置
    /**
     * 配置项
     */
    treeConfig: {
      type: Object,
      default: () => {
        return {};
      }
    },
    // 树形组件值
    /**
     * 配置项
     */
    treeData: {
      type: Array,
      default: () => {
        return [];
      }
    },
    // 控件类型
    /**
     * 配置项
     */
    type: {
      type: String,
      default: 'input'
    },
    /**
     * 配置项
     */
    btnName: {
      type: String,
      default: ''
    },
    /**
     * 配置项
     */
    placeholder: {
      type: String,
      default: ''
    },
    // 输入框类型
    /**
     * 配置项
     */
    inputType: {
      type: String,
      default: 'text'
    },
    // 是否禁用
    /**
     * 配置项
     */
    disabled: {
      type: Boolean,
      default: false
    },
    // 计数器范围
    /**
     * 配置项
     */
    inputLength: {
      type: Object,
      default: () => {
        return {
          min: 1,
          max: 100
        };
      }
    },
    // 控件宽度
    /**
     * 配置项
     */
    width: {
      type: String,
      default: ''
    },
    // 下拉选项，可传静态数组及字符串，字符串则传入请求方法名
    /**
     * 配置项
     */
    options: {
      type: Array | String
    },
    /**
     * 配置项
     */
    optionsList: {
      type: Array
    },
    // 动态下拉无值是补充使用
    /**
     * 配置项
     */
    otherOptions: {
      type: Object
    },
    // options传入字符串，optionsParams为默认参数
    /**
     * 配置项
     */
    optionsParams: {
      type: Object,
      default: () => {
        return {};
      }
    },
    // 下拉key-value配置
    /**
     * 配置项
     */
    optionsConfig: {
      type: Object,
      default: () => {
        return {
          name: 'name',
          value: 'value'
        };
      }
    },
    // 日期格式
    /**
     * 配置项
     */
    valueFormat: {
      type: String,
      default: 'yyyy-MM-dd HH:mm:ss'
    },
    // 传入监听方法用于组件改变时
    /**
     * 配置项
     */
    watch: {
      type: Function
    },
    /**
     * 配置项
     */
    tableData: {
      type: Array,
      default: () => {
        return [];
      }
    },
    /**
     * 配置项
     */
    index: {
      type: Number,
      default: 0
    },
    /**
     * 配置项
     */
    autoUpload: {
      type: Boolean,
      default: true
    },
    /**
     * 配置项
     */
    fileType: {
      type: String,
      default: ''
    },
    /**
     * 配置项
     */
    refresh: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      resizable: true,
      isShowOperation: false,
      checkboxList: [],
      isShowCheckBox: false,
      height: '300px',
      selectOptions: [],
      inputVisible: false,
      inputValue: ''
    };
  },
  mounted() {
    if (typeof this.options === 'string') {
      // 由于参数需要nodeId此处进行统一设置
      if (this.type === 'select' && this.optionsList) {
        this.selectOptions = this.optionsList;
      } else {
        this.queryOptions();
      }
    } else {
      this.selectOptions = this.options;
    }
    if (this.type == 'autoTime') {
      this.autoNewTime();
    }
  },
  watch: {
    // form表单下拉选项配置项刷新
    /**
     * 配置项
     */
    refresh: {
      deep: true,
      handler(bool) {
        if (bool) {
          this.queryOptions();
        }
      }
    },
    // 修改参数动态加载
    /**
     * 配置项
     */
    optionsParams: {
      handler(val) {
        console.log(val);
        this.queryOptions();
      },
      deep: true
    },
    // 额外选项更新动态合并
    /**
     * 配置项
     */
    otherOptions: {
      handler(val) {
        this.unionObj(this.selectOptions, val.data);
      },
      deep: true
    },
    'ruleForm.connection': {
      immediate: true,
      handler: function(val) {
        if (val) {
          sessionStorage.setItem('text', val);
        }
      }
    }
  },
  computed: {
    /**
     * 计算已有表选择的显示隐藏
     */
    databasePickVisible() {
      //表输出 控制在不同条件下 已有表选择的显示隐藏
      let conditions = this.ruleForm.isNewTable ? [this.type === 'inputButtonTree', this.ruleForm.isNewTable === 1] : [this.type === 'inputButtonTree'];
      return conditions.every(Boolean);
    },
    /**
     * 计算新增表的显示隐藏
     */
    databaseAppendVisible() {
      //表输出 控制在不同条件下 新增表的显示隐藏
      let conditions = this.ruleForm.isNewTable ? [this.type === 'inputBtn', this.ruleForm.isNewTable === 2] : [this.type === 'inputBtn'];
      return conditions.every(Boolean);
    }
  },
  methods: {
    /**
     * 选择文件
     */
    chooseFile() {
      this.$refs.fileRef.click();
    },
    /**
     * 获取下拉选择的值
     */
    getSelectValue(data) {
      if (this.options === 'getConnectCodeTree' && !this.lazy) {
        this.$eventBus.$emit('setSql', data);
        return;
      }
      this.ruleForm[this.valueKey] = data;
    },
    /**
     * 配置项
     */
    liveFunction(valueKey) {
      console.log(valueKey);
    },
    /**
     * 关闭控制
     */
    handleClose(tag) {
      this.ruleForm[this.valueKey].splice(this.ruleForm[this.valueKey].indexOf(tag), 1);
    },
    /**
     * 展示输入框
     */
    showInput() {
      this.inputVisible = true;
      this.$nextTick(_ => {
        this.$refs.saveTagInput.$refs.input.focus();
      });
    },
    /**
     * 输入确认
     */
    handleInputConfirm() {
      let inputValue = this.inputValue;
      if (inputValue) {
        this.ruleForm[this.valueKey].push(inputValue);
      }
      this.inputVisible = false;
      this.inputValue = '';
    },
    /**
     * 检查值
     */
    checkValue() {
      if (this.ruleForm[this.valueKey] && this.watch) {
        this.watch.call(this, this.ruleForm);
      }
    },
    /**
     * 自动时间
     */
    autoNewTime() {
      let d = new Date();
      let date = d.toLocaleDateString();
      let time = d.toTimeString().slice(0, 8);
      date = date.replace(/\//g, '-');
      this.ruleForm[this.valueKey] = date + ' ' + time;
    },
    /**
     * 获取结果
     * @param data
     */
    findResultArray(data) {
      let result = [];
      Object.keys(data).forEach(key => {
        let type = this.$utils.checkDataType(data[key]);
        if (type === 'Array') {
          result = data[key];
        } else if (type === 'Object') {
          result = this.findResultArray(data[key]);
        }
      });
      return result;
    },
    /**
     * 获取下拉数据
     */
    queryOptions() {
      // debugger;
      if (!this.optionsParams.hasOwnProperty('nodeId')) {
        this.optionsParams.nodeId = this.$store.state.nodeId;
      }
      API[this.options](this.optionsParams)
        .then(data => {
          // 类型为input时，将返回值赋值到ruleForm对应字段中进行回显  ————Leo 20210427
          if (this.valueKey === 'replaceValue') {
            if (data) {
              this.selectOptions = [{ name: data.data.content, value: data.data.content }];
            } else {
              this.selectOptions = [];
            }
          } else {
            if (this.$utils.checkDataType(data.data) === 'Array') {
              this.selectOptions = data.data;
            } else {
              this.selectOptions = this.findResultArray(data.data);
            }
            // this.selectOptions = data.data.data ? data.data.data : data.data;

            // if (!this.selectOptions || this.$utils.checkDataType(this.selectOptions) !== 'Array') {
            // }
          }
          /* if (this.valueKey === 'replaceValue') {
          this.selectOptions = [];
          this.$nextTick(() => {
            // this.selectOptions = data.data[0].map(option => {
            //   return { name: option };
            // });
            if (data) {
              this.ruleForm.replaceValue = data.data.content;
            }
          });
        } else {
          this.selectOptions = data.data.data ? data.data.data : data.data;
        } */

          // 当接口名称为getConnectCodeTree时，代码需要整理为树形结构
          if (this.options === 'getConnectCodeTree' && !this.lazy) {
            this.initConnectCodeData();
          }

          if (this.otherOptions) {
            if (this.selectOptions.length === 0) {
              this.selectOptions = this.otherOptions.data;
            }
            // 对于计算器的
            if (this.tableData.length > 0) {
              let newArr = this.tableData
                .map(item => {
                  if (item.field_name) {
                    return { name: item.field_name };
                  } else {
                    return null;
                  }
                })
                .filter(item => item != null);
              this.unionObj(this.selectOptions, newArr);
            }
          }
        })
        .catch(err => {
          this.selectOptions = [];
        });
    },
    /**
     * 初始化数据
     */
    initConnectCodeData() {
      const dataList = {};
      const typeList = [];
      /**
       * 遍历选项
       */
      this.selectOptions.forEach(ele => {
        ele.leaf = true;
        if (typeList.includes(ele.category)) {
          dataList[ele.category].children.push(ele);
        } else {
          typeList.push(ele.category);
          dataList[ele.category] = {
            name: ele.category,
            children: [ele]
          };
        }
      });
      const data = [];
      for (let pr in dataList) {
        data.push(dataList[pr]);
      }
      this.selectOptions = data;
    },
    /**
     * @param obj 合并后对象
     * @param otherObj 被合并项
     */
    unionObj(obj, otherObj) {
      otherObj.forEach(valItem => {
        let count = 0;
        obj.forEach(item => {
          if (item.name !== valItem.name && valItem.name) {
            count++;
          }
        });
        if (count === obj.length) {
          obj.push(valItem);
        }
      });
    },
    /**
     * 修改值
     */
    changeValue(data) {
      let specialData = null;
      if (this.watch) {
        if (this.selectOptions && this.selectOptions.length && data) {
          this.selectOptions.forEach((item, index) => {
            if (item[this.optionsConfig.value] == data && this.optionsConfig.list) {
              specialData = item[this.optionsConfig.list];
            }
          });
        }
        this.watch.call(this, this.ruleForm, this.selectOptions, this.tableData, this.index, specialData);
      }

      // 修改步骤名称时验证流程中名称是否重复
      if (this.valueKey === 'stepName' && data) {
        let nodeData = model.getData();
        let nodeList = nodeData.nodes;
        let bool = -1;
        nodeList.forEach(ele => {
          if (ele.properties.ruleForm.stepName === data) {
            bool++;
          }
        });
        if (bool) {
          this.$message.warning('步骤名称存在，请更换！');
        }
      }

      if (this.valueKey === 'accept_filenames' && data) {
        this.$eventBus.$emit('showExcelBtn', data);
      }
    },
    // 多表格连接特殊处理
    visibleChange(state) {
      if (state) {
        // this.queryOptions();
        // if (this.valueKey === 'keys') {
        //   this.optionsParams.stepName = this.ruleForm['stepnames'];
        //   this.queryOptions();
        // }
        if (this.valueKey === 'replaceValue') {
          this.optionsParams.key = this.tableData[this.index].name;
          // this.queryOptions('replace_by_string');
        }
      }
    },
    /**
     * 打开弹框
     */
    openDialog(dialogRef) {
      if (!this.ruleForm.connection && this.ruleForm.stepName !== 'Excel输出') {
        this.$message.error('请先选择数据源！');
        return;
      }
      // 打开弹窗初始化默认值
      this.changeValue();
      this.$refs[dialogRef].show({ title: this.treeConfig.title });
    },
    /**
     * 获取树形选择结果
     */
    getTreeSelected(id, name) {
      this.$set(this.ruleForm, this.valueKey, id);
      this.$set(this.ruleForm, this.nameKey, name);
      // this.ruleForm[this.valueKey] = id;
      // this.ruleForm[this.nameKey] = name;
      // Excel输出组件赋值
      if (this.nameKey === 'filePath') {
        this.ruleForm[this.nameKey] = id;
        this.ruleForm[this.valueKey] = name;
      }
      this.changeValue();
    },
    /**
     * 获取表格勾选结果
     */
    getTableSelected(id, name) {
      this.ruleForm[this.valueKey] = id;
      // this.ruleForm[this.nameKey] = name;
    },
    /**
     * 上传文件
     */
    uploadFile(uploadFile) {
      if (this.autoUpload) {
        const loading = this.$utils.setLoading('上传中...');
        let formData = new FormData();
        formData.append('uploadFile', uploadFile);
        API.getFileUpload(formData)
          .then(res => {
            if (this.watch) {
              this.watch.call(this, this.ruleForm, res.data.data);
            }
            this.$message({
              type: res.data.success ? 'success' : 'error',
              message: res.data.data.msg
            });
          })
          .finally(() => {
            loading.close();
          });
      } else {
        this.watch.call(this, this.ruleForm, uploadFile);
      }
    },
    /**
     * 控制勾选框
     * @param val
     */
    handleCheckboxSelect(val) {
      if (this.watch) {
        this.watch.call(this, this.ruleForm, val);
      }
    },
    /**
     * 控制选择文件
     * @param e
     */
    handleSelectedFiles(e) {
      let file = e.target.files[0];
      this.$emit('fileData', e);
      this.ruleForm[this.valueKey] = file.name;
    },
    /**
     * 选择文件夹
     */
    chooseFolder() {
      this.$refs.folderRef.click();
    },
    /**
     * 控制选择的文件夹
     * @param e
     */
    handleSelectedFolders(e) {
      let file = e.target.files[0];
      console.log(file);
      this.ruleForm[this.valueKey] = file.name;
    }
  }
};
</script>
<style scoped lang="scss">
@import '@/styles/theme.scss';
.mixin-components ::v-deep .el-input__inner {
  background: $inputBgClr !important;
  border: 1px solid $tableBorderClr;
  border-radius: 4px;
  // color: #fff;
  height: 30px;
  line-height: 30px;
}
.mixin-components ::v-deep .el-input-group__append .el-button.el-button--default {
  background-color: transparent !important;
  border-color: transparent !important;
}

.mixin-components ::v-deep .el-input__icon {
  line-height: 30px;
}

.mixin-components ::v-deep .el-date-editor.el-input {
  width: 100%;
}

.mixin-components ::v-deep .el-select {
  width: 100%;
}

.mixin-components ::v-deep .el-textarea__inner {
  background: $inputBgClr;
  border: 1px solid $inputBgClr;
  border-radius: 4px;
}
.mixin-components ::v-deep .el-tag + .el-tag {
  margin-left: 10px;
}
.mixin-components ::v-deep .button-new-tag {
  margin-left: 10px;
  height: 32px;
  line-height: 30px;
  padding-top: 0;
  padding-bottom: 0;
}
.mixin-components ::v-deep .input-new-tag {
  width: 90px;
  margin-left: 10px;
  vertical-align: bottom;
}
.range {
  display: flex;
  justify-content: space-between;
}
.range span {
  width: 80px;
  text-align: center;
}
.mixin-components ::v-deep .el-input-number__decrease,
.mixin-components ::v-deep .el-input-number__increase {
  color: #fff;
  border-color: $inputBgClr;
  background-color: $inputBgClr;
}
#file {
  visibility: hidden;
  position: absolute;
  left: 0;
}
#folder {
  visibility: hidden;
  position: absolute;
  left: 0;
}
</style>

