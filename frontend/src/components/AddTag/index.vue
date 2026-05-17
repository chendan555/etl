<template>
  <div class="addTag">
    <div class="add">
      <el-input
        class="input-new-tag"
        v-model="newTagName"
        ref="saveTagInput"
        @keyup.enter.native="addTag"
      ></el-input>
      <el-button class="button-new-tag" @click="addTag">添加标签</el-button>
    </div>
    <div class="select">
      <el-select
        v-model="chsTagName"
        filterable
        placeholder="请选择"
        @visible-change="chsTag"
        multiple
      >
        <el-option
          v-for="item in tagList"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        ></el-option>
      </el-select>
    </div>
    <div class="showTag">
      <el-tag
        v-for="(tag, index) in resTagName"
        :key="index"
        @close="handleClose(index)"
        closable
      >{{tag}}</el-tag>
    </div>
  </div>
</template>
<script>
/**
 * Create by Leo
 * 基础组件
 * 基础标签配置组件
 * 集成标签的添加、显示、编辑、删除等功能
 * 主要用于数据添加标签或者配置添加标签
 */

// import { clear } from '../../utils';
export default {
  name: 'addTag',
  props: ['tagList', 'labelList'],
  data() {
    return {
      chsTagName: [],
      newTagName: ''
    };
  },
  computed: {
    /**
     * 合并输入添加标签和下来选择标签
     */
    resTagName() {
      return this.labelList.concat(this.chsTagName);
    }
  },
  methods: {
    /**
     * 选择标签
     */
    chsTag() {
      let moreList = [];
      // 选择标签后判断是否重复
      if (this.labelList.length) {
        this.labelList.forEach(ele => {
          this.chsTagName.forEach((el, idx) => {
            if (ele === el) {
              // 重复的添加到moreList中
              moreList.push(el);
              // 在已选列表中删除当前项
              this.chsTagName.splice(idx, 1);
            }
          });
        });
      }
      // 当moreList有值时展示提示弹窗
      if (moreList.length) {
        let str = '';
        moreList.forEach(ele => {
          str += `“${ele}”`;
        });
        this.$message.warning(`标签${str}添加失败，该标签已存在，请重新选择！`);
      }
      this.$emit('resTagName', this.resTagName);
    },
    /**
     * 新增标签
     */
    addTag() {
      // 判断当前输入框是否有值
      if (this.newTagName) {
        let bool = true;
        if (this.resTagName.length) {
          // console.log(this.resTagName, 222);
          // 判断当前标签是否存在
          this.resTagName.forEach(ele => {
            if (ele === this.newTagName) {
              this.$message.warning(`标签“${this.newTagName}”添加失败，该标签已存在，请重新输入！`);
              this.newTagName = '';
              bool = false;
              this.$refs.saveTagInput.focus();
            }
          });
        }
        // 判断没有重复时添加到标签列表
        if (bool) {
          this.labelList.push(this.newTagName);
          this.newTagName = '';
        }
      } else {
        this.$message.warning('标签不能为空值，请输入。');
      }
      this.$emit('resTagName', this.resTagName);
    },
    /**
     * 删除已选标签
     * index：标签序号
     */
    handleClose(index) {
      let idx = index - this.labelList.length;
      // 判断当前序号idx有效性
      if (idx > -1) {
        // idx有效则从TagName中删除
        this.chsTagName.splice(idx, 1);
      } else {
        // idx无效则从labelList中删除
        this.labelList.splice(index, 1);
      }
      this.$emit('resTagName', this.resTagName);
    },
    /**
     * 清空标签
     */
    clearData() {
      this.newTagName = '';
      this.chsTagName = [];
    }
  }
};
</script>
<style lang="scss" scoped>
.addTag {
  width: 100%;
  .add {
    width: 100%;
    display: flex;
    margin-bottom: 5px;
    align-items: center;
    .el-input {
      width: 200px;
      margin-right: 10px;
    }
    .el-button {
      height: 32px;
    }
  }
  .select {
    margin-bottom: 5px;
    .el-select {
      width: 200px;
    }
  }
  .showTag .el-tag {
    margin-right: 5px;
    margin-bottom: 5px;
  }
}
</style>
