<template>
  <div class="projConfig">
    <SideFrame :headTitle="headTitle">
      <div class="sideContent" slot="sideContent">
        <div class="multipleInput">
          <el-form ref="form" :model="formList" label-width="80px" size="small" :rules="rules">
            <el-form-item label="方案名称" prop="name">
              <el-input v-model="formList.name"></el-input>
            </el-form-item>
            <el-form-item label="输入" prop="insert">
              <el-cascader
                v-model="formList.inputType"
                :options="inputOptions"
                :props="{label: 'categoriesName', value: 'id'}"
                filterable
                clearable
              ></el-cascader>
            </el-form-item>
            <el-form-item label="处理类型" prop="dealType">
              <el-select v-model="formList.dealType">
                <el-option
                  :label="item.label"
                  :value="item.value"
                  v-for="(item, index) in dealTypeOptions"
                  :key="index"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="描述">
              <el-input type="textarea" v-model="formList.description"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button>提交</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </SideFrame>
  </div>
</template>
<script>
import SideFrame from '@/components/SideFrame/index.vue';
import BaseTable from '@/components/BaseTable/index.vue';
// import axios from '@/api/base/http';
export default {
  name: 'projConfig',
  components: { SideFrame, BaseTable },
  data() {
    return {
      headTitle: {
        imgUrl: 'taskInfor',
        title: '格式化方案信息'
      },
      formList: {
        name: '',
        inputType: [],
        dealType: '',
        description: ''
      },
      rules: {
        name: [
          {
            required: true,
            message: '请输入流程名称',
            trigger: 'blur'
          }
        ],
        insert: [
          {
            required: true,
            message: '请选择输入',
            trigger: 'blur'
          }
        ],
        dealType: [
          {
            required: true,
            message: '请选择处理类型',
            trigger: 'blur'
          }
        ]
      },
      inputOptions: [],
      dealTypeOptions: [
        {
          label: '补全',
          value: 1
        },
        {
          label: '去重',
          value: 2
        }
      ]
    };
  },
  mounted() {
    this.setInputOptions();
  },
  methods: {
    setInputOptions() {
      this.$dataCenterService.getTree().then(res => {
        if (res.data.success && Array.isArray(res.data.data)) {
          this.inputOptions = this.cleanData(res.data.data);
        }
      });
    },
    cleanData(arr) {
      arr.forEach((ele, index) => {
        if (ele.children && ele.children.length === 0) {
          delete ele.children;
        } else {
          ele.children = this.cleanData(ele.children);
        }
      });
      return arr;
    }
  }
};
</script>
<style lang="scss" scoped>
.projConfig {
  width: 100%;
  height: 100%;
  .sideContent {
    width: 100%;
    height: 100%;
    /deep/ .el-form-item__label {
      color: #fff;
    }
    /deep/ .el-input__inner {
      //   background: #1d2822;
      .el-range-input {
        background: transparent;
        color: #fff;
      }
      .el-range-separator {
        color: #ccc;
      }
    }
  }
}
</style>
