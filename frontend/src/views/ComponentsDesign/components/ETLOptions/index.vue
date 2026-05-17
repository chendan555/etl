<template>
  <!-- 不传入title默认为表单控件中展示，控制样式效果 -->
  <div class="dynamic-options-container" :class="{'inner-form':!title}">
    <div class="title" v-if="title">{{title}}</div>
    <el-button size="mini" @click="addNode">增加元素</el-button>
    <el-row :gutter="10">
      <el-col class="dynamic-options-col" :span="24" v-for="(option,index) in options" :key="index">
        <el-col :span="20" class="dynamic-options-input-container">
          <!-- 左 -->
          <div class="dynamic-options-input">
            <el-input
              v-if="editTypeArr[0] === 'input'"
              size="mini"
              v-model="option[keysArr[0]]"
              @blur="setEleAttr"
            ></el-input>
            <el-select
              size="mini"
              v-else
              @change="setEleAttr(option)"
              v-model="option[keysArr[0]]"
              placeholder="请选择"
            >
              <el-option
                v-for="item in selectOptions1"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              ></el-option>
            </el-select>
          </div>
          <!-- 右 -->
          <div class="dynamic-options-input">
            <el-input
              v-if="editTypeArr[1] === 'input'"
              size="mini"
              v-model="option[keysArr[1]]"
              @blur="setEleAttr"
            ></el-input>
            <el-select
              size="mini"
              v-else
              @change="setEleAttr(null)"
              v-model="option[keysArr[1]]"
              placeholder="请选择"
            >
              <el-option
                v-for="(item, index) in getFilterSelectOptions2(option)"
                :key="index"
                :label="item.label"
                :value="item.value"
              ></el-option>
            </el-select>
          </div>
        </el-col>
        <el-col :span="4">
          <div v-if="showEditNode" class="dynamic-options-col-icon" @click="editNode(index)">
            <i class="el-icon-edit"></i>
          </div>
          <div class="dynamic-options-col-icon" @click="removeNode(index)">
            <i class="el-icon-circle-close"></i>
          </div>
        </el-col>
      </el-col>
    </el-row>
  </div>
</template>
<script>
export default {
  name: 'ETLOptions',
  props: {
    options: Array,
    title: String,
    keys: String,
    values: String,
    attrName: String,
    editType: {
      type: String,
      default: 'input,input'
    },
    selectOptions1: Array,
    selectOptions2: Array,
    // selectOptions2对应selectOptions1对应字段，由此过滤selectOptions2
    selectOptions2FilterCondition: String,
    // 第一选择框改变后是否清除第二选择框内容
    select1ChangeClearSelect2: Boolean,
    showEditNode: Boolean
  },
  computed: {
    keysArr() {
      return this.keys.split(',');
    },
    editTypeArr() {
      return this.editType.split(',');
    }
  },
  methods: {
    // 通过传入过滤条件获取第二下拉选项
    getFilterSelectOptions2(option) {
      let value1 = option[this.keysArr[0]];
      console.log(this.selectOptions2);
      return this.selectOptions2;
      // if (value1) {
      //   let key = '';
      //   this.selectOptions1.forEach(option => {
      //     if (option.value === value1) {
      //       key = option.key;
      //     }
      //   });
      //   return this.selectOptions2.filter(option => option.key === key);
      // } else {
      //   return this.selectOptions2;
      // }
    },
    addNode() {
      const keys = this.keys.split(',');
      const values = this.values.split(',');
      const option = {};
      for (let i = 0; i < keys.length; i++) {
        if (values[i] === '[]') {
          values[i] = [];
        } else if (values[i] === 'random') {
          values[i] = Math.random().toString();
        } else if (values[i] === 'null') {
          values[i] = null;
        }
        option[keys[i]] = values[i];
      }
      this.options.push(option);

      this.setEleAttr();
    },
    removeNode(index) {
      this.options.splice(index, 1);
      this.setEleAttr();
    },
    editNode(index) {
      this.$emit('editNode', index);
    },
    setEleAttr(option) {
      console.log(option);
      // 第一下拉更改时清空第二内容
      if (option && this.select1ChangeClearSelect2) {
        option[this.keysArr[1]] = '';
      }
      this.$emit('setEleAttr', this.attrName);
    }
  }
};
</script>
<style lang="scss" scoped>
.dynamic-options-container {
  border-top: 1px solid #808353;
  padding: 10px 0;
  .title {
    width: 80px;
    font-size: 14px;
    text-align: center;
    margin-bottom: 10px;
  }
  .el-button {
    margin-bottom: 10px;
  }
  .dynamic-options-col {
    margin-top: 5px;
    display: flex;
    align-items: center;
    &-icon {
      color: #c0c4cc;
      line-height: 28px;
      cursor: pointer;
      text-align: center;
      display: inline-block;
    }
  }
  .dynamic-options-input-container {
    display: flex;
    justify-content: space-between;
    .dynamic-options-input {
      width: 45%;
    }
  }
}
.inner-form {
  border: none;
  padding: 0;
}
</style>
