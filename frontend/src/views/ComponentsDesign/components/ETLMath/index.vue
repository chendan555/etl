<template>
  <div class="etlMath">
    <!-- <el-form ref="form" :model="ruleForm" label-width="150px">
      <el-form-item label="结果字段" prop="newField">
        <el-input v-model="ruleForm.newField"></el-input>
      </el-form-item>
      <el-form-item label="结果精度" prop="precision">
        <el-input-number v-model="ruleForm.precision" :min="0"></el-input-number>
      </el-form-item>
    </el-form>-->
    <h3>计算公式</h3>
    <div class="mathdiv">
      <div class="contdiv">
        <div class="editordiv">
          <!-- 公式内容编辑区域 -->
          <el-input type="textarea" v-model="ruleForm.formula"></el-input>
        </div>
      </div>
      <!-- 公式符号列表 -->
      <div class="signdiv">
        <el-tooltip
          v-for="sign in signList"
          :key="sign.label"
          effect="dark"
          :content="sign.tooltip"
          placement="top"
        >
          <el-button type="primary" size="small" @click="handleSign(sign.value)">{{sign.label}}</el-button>
        </el-tooltip>
        <el-tooltip effect="dark" content="平方" placement="top">
          <el-button type="primary" size="small" @click="handleSign('^2')">
            X
            <sup>2</sup>
          </el-button>
        </el-tooltip>
        <el-tooltip effect="dark" content="n次方" placement="top">
          <el-button type="primary" size="small" @click="handleSign('^n')">
            X
            <sup>n</sup>
          </el-button>
        </el-tooltip>
        <el-tooltip effect="dark" content="平方根" placement="top">
          <el-button type="primary" size="small" @click="handleSign('^(1/2)')">√</el-button>
        </el-tooltip>
        <el-tooltip effect="dark" content="n方根" placement="top">
          <el-button type="primary" size="small" @click="handleSign('^(1/n)')">
            <sup>n</sup>√
          </el-button>
        </el-tooltip>
        <el-tooltip effect="dark" content="添加变量" placement="top">
          <el-button type="primary" size="small" @click="addVariable">?</el-button>
        </el-tooltip>
        <el-button type="primary" size="small" @click="finishConfig">完成</el-button>
      </div>
    </div>
    <!-- 配置变量映射字段 -->
    <!-- <div class="variablediv">
      <base-form v-bind="{...varFormConfig}"></base-form>
    </div>-->
  </div>
</template>
<script>
/**
 * @description 计算功能面板基础组件
 * @description 用于算子组件计算功能实现
 */
import config from './config';
import BaseForm from '@/components/BaseForm/index.vue';
// import { type } from 'os';
export default {
  name: 'ETLMath',
  components: { BaseForm },
  props: {
    value: {
      type: String
    }
  },
  data() {
    return {
      // 字段列表
      columnList: [],
      // 计算表单信息
      ruleForm: {
        newField: '',
        precision: '',
        formula: ''
      },
      // 符号列表，本地配置
      signList: config.signList,
      // variableIndex: 1,
      // variableList: [],
      // varFormConfig: {
      //   ruleForm: {},
      //   formItemConfig: []
      // },
      // formItem: {
      //   label: '',
      //   type: 'select',
      //   labelWidth: '150px',
      //   valueKey: '',
      //   span: 24,
      //   options: 'getStepsFileds',
      //   optionsConfig: {
      //     name: 'name',
      //     value: 'name'
      //   },
      //   optionsParams: {
      //     before: true
      //   }
      // }
    };
  },
  mounted() {
    // 加载当前公式信息
    this.ruleForm.formula = this.value;
  },
  methods: {
    /**
     * @description 获取字段信息
     */
    getStepsFileds() {
      const params = {
        before: true
      };
      this.$API.getStepsFileds(params).then(({ data }) => {
        this.columnList = data.data;
      });
    },
    /**
     * @description 设置运算符号
     * @param sign 符号
     */
    handleSign(sign) {
      this.ruleForm.formula += sign;
    },
    /**
     * @description 添加变量
     */
    addVariable() {
      this.ruleForm.formula += '?';
      /* //  配置变量
      let variable = `?${this.variableIndex++}`;
      this.variableList.push({
        varName: variable,
        columnName: ''
      });
      this.$set(this.varFormConfig.ruleForm, variable, '');
      let formItem = this.$utils.deepCopy(this.formItem);
      formItem.label = variable;
      formItem.valueKey = variable;
      this.varFormConfig.formItemConfig.push(formItem); */
    },
    /**
     * @description 完成公式配置
     */
    finishConfig() {
      this.$emit('input', this.ruleForm.formula);
      this.$message.success('公式配置完成!');
    }
  }
};
</script>
<style lang="scss" scoped>
.etlMath {
  width: 100%;
  height: 100%;
  color: #fff;
  h3 {
    color: #fff;
  }
  .mathdiv {
    width: 100%;
    height: 300px;
    display: flex;
    // margin-top: 10px;
    .contdiv {
      width: calc(100% - 290px);
      height: 100%;
      //   .btndiv {
      //     width: 100%;
      //     height: 40px;
      //   }
      .editordiv {
        width: 100%;
        height: 100%;
        background: #333;
        .el-textarea {
          height: 100%;
          /deep/ .el-textarea__inner {
            height: 100%;
          }
        }
      }
    }
    .signdiv {
      width: 290px;
      height: 100%;
      padding: 0 10px;
      display: flex;
      flex-wrap: wrap;
      .el-button {
        margin: 5px;
        width: 80px;
        height: 40px;
      }
    }
  }
  .el-form {
    height: 90px;
    &-item {
      margin-bottom: 5px;
    }
  }
  .variablediv {
    width: 100%;
    margin-top: 5px;
  }
}
</style>
