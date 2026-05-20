<template>
  <!-- <el-dialog
    :title="projDialog.title"
    :visible.sync="projDialog.visible"
    @close="closeProjDialog"
    width="50%"
  >-->
  <BaseDialog v-bind="$attrs" v-on="$listeners">
    <div class="content">
      <el-form ref="form" :model="ruleForm" label-width="120px" size="small" :rules="rules">
        <el-row>
          <el-col :span="24">
            <el-form-item label="数据库名称" prop="name">
              <el-input v-model="ruleForm.name" placeholder="请输入数据库名称（示例：效能评估库）"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="服务器名称/IP" prop="host">
              <el-input
                v-model="ruleForm.host"
                placeholder="请输入（示例：***.***.***.*** | ***为0~255范围）"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="端口" prop="port">
              <el-input v-model="ruleForm.port" placeholder="请输入端口（示例：8080）"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="ruleForm.username" placeholder="请输入用户名"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="密码" prop="password">
              <el-input v-model="ruleForm.password" placeholder="请输入密码"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="使用分类" prop="categoriesId">
              <el-select v-model="ruleForm.categoriesId" placeholder="请选择使用分类" @change="setDatabaseType">
                <el-option
                  v-for="item in useTypeList"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="类型分类" prop="dbType">
              <el-select
                v-model="ruleForm.dbType"
                placeholder="请选择数据库类型"
                @change="onDbTypeChange"
              >
                <el-option
                  v-for="item in dbTypeList"
                  :key="item.id"
                  :label="item.categoriesName"
                  :value="item.dbCode"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-button size="mini" type="primary" @click="handleCustom">连接测试</el-button>
          </el-col>
        </el-row>
      </el-form>
      <!-- v-if="linkStatus" -->
      <el-form ref="form2" :model="ruleForm" label-width="120px" size="small" :rules="rules">
        <el-row>
          <el-col :span="12" v-if="dbCode === 'ORACLE'">
            <el-form-item label="服务名" prop="sid">
              <el-input v-model="ruleForm.sid" placeholder="请输入服务名"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="选择模式" prop="schemaList">
              <el-select v-model="ruleForm.schemaList" placeholder="请选择模式" multiple :disabled="!linkStatus">
                <el-option
                  v-for="item in schemaList"
                  :key="item"
                  :label="item"
                  :value="item"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="数据表筛选规则" prop="isInclude">
              <p>
                <el-radio-group v-model="ruleForm.isInclude">
                  <el-radio :label="'1'">包含</el-radio>
                  <el-radio :label="'0'">不包含</el-radio>
                </el-radio-group>
              </p>
              <p>
                <span>以</span>
                <el-input class="ruleCont" v-model="ruleCont.rulePrefix" placeholder="以**开头"></el-input>
                <span>开头，且以</span>
                <el-input class="ruleCont" v-model="ruleCont.ruleSuffix" placeholder="以**结尾"></el-input>
                <span>结尾，其中包含</span>
                <el-input class="ruleCont" v-model="ruleCont.ruleMiddle" placeholder="包含**"></el-input>
              </p>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>
    <span slot="footer" class="dialog-footer">
      <el-button size="mini" type="primary" @click="formValidate" :disabled="!linkStatus">确 定</el-button>
      <el-button size="mini" type="info" @click="closeProjDialog">取 消</el-button>
    </span>
  </BaseDialog>
</template>
<script lang="ts">
import Vue from 'vue';
import config from './config';
import { dataCenterService } from '@/api/index.ts';
import BaseDialog from '@/components/BaseDialog/index.vue';
export default Vue.extend({
  name: 'configDatabase',
  components: { BaseDialog },
  props: {
    databaseServerInfo: {
      type: Object,
      default: () => {
        return {};
      }
    },
    // 数据中心类型
    dataCenterType: {
      type: Number,
      default: ''
    }
  },
  data() {
    return {
      ruleForm: Object.keys(this.databaseServerInfo).length
        ? this.$utils.deepCopy(this.databaseServerInfo)
        : this.$utils.deepCopy(config.databaseFormConfig.ruleForm),
      rules: config.databaseFormConfig.rules,
      linkStatus: false,
      useTypeList: [
        {
          label: '源数据库',
          value: '1'
        }
      ],
      dbTypeList: <any>[],
      schemaList: <any>[],
      ruleCont: {
        rulePrefix: '',
        ruleMiddle: '',
        ruleSuffix: ''
      }
    };
  },
  created() {
    this.getDbTypeList();
    console.log(this.ruleForm);
    this.initRuleConfig();
  },
  mounted() {
    this.getSchemaList();
  },
  computed: {
    dbCode() {
      // ruleForm.dbType 现在直接存 dbCode（如 "DM"），兼容旧数据（如 "1"）
      const val = this.ruleForm.dbType;
      if (!val) return '';
      const db = (this as any).dbTypeList.find((ele: any) => ele.dbCode === val || ele.id === val);
      return db ? db.dbCode : val;
    }
  },
  methods: {
    /**
     * @description 初始化筛选规则内容
     */
    initRuleConfig() {
      if (this.ruleForm.schemaFilter) {
        this.ruleForm.schemaList = this.ruleForm.schemaFilter.split(',');
      }
      const keys = Object.keys(this.ruleCont);
      keys.forEach((key: string) => {
        (this as any).ruleCont[key] = this.ruleForm[key];
      });
    },
    /**
     * @description 获模式列表
     */
    getSchemaList() {
      const { name, host, port, username, password } = this.ruleForm;
      const dbCode = this.dbCode;
      if (host && port && username && password && dbCode) {
        const params = { name, host, port, username, password, dbCode };
        this.$axios.post('/api/database/getSchemaAll', params).then(({ data }: any) => {
          if (data.success) {
            this.schemaList = data.data || [];
          }
        });
      }
    },
    setDatabaseType(data: string) {
      if (data !== '1') {
        this.ruleForm.dbType = this.dbTypeList[0].dbCode;
      }
    },
    // 数据库类型改变时自动填充默认值
    onDbTypeChange(data: string) {
      const dbType = this.dbTypeList.find((item: any) => item.dbCode === data);
      if (!dbType) return;
      const defaults: any = {
        'DM': { port: '5236', username: 'SYSDBA', password: 'SYSDBA' },
        'MYSQL': { port: '3306', username: 'root', password: 'root' },
        'ORACLE': { port: '1521', username: 'system', password: 'oracle' },
        'POSTGRESQL': { port: '5432', username: 'postgres', password: 'postgres' },
        'KINGBASE': { port: '54321', username: 'system', password: '123456' }
      };
      const config = defaults[dbType.dbCode];
      if (config) {
        this.ruleForm.host = '127.0.0.1';
        this.ruleForm.port = config.port;
        this.ruleForm.username = config.username;
        this.ruleForm.password = config.password;
      }
    },
    // 获取数据库分类
    getDbTypeList() {
      this.$API.getCategoriesList().then(({ data }: any) => {
        // console.log(data);
        this.dbTypeList = data.data;
        // 兼容旧数据：dbType 可能存的是 id（"1"）而不是 dbCode（"DM"），自动转换
        if (this.ruleForm.dbType && this.dbTypeList.length > 0) {
          const match = this.dbTypeList.find((item: any) => item.id === this.ruleForm.dbType);
          if (match) {
            this.ruleForm.dbType = match.dbCode;
          }
        }
        // 判断当前是否为修改状态，新增状态则默认数据库类型为达梦并填充默认值
        if (!Object.keys(this.databaseServerInfo).length && this.dbTypeList.length > 0) {
          this.ruleForm.dbType = this.dbTypeList[0].dbCode;
          this.onDbTypeChange(this.dbTypeList[0].dbCode);
        }
        this.$store.dispatch('setDatabaseType', this.dbTypeList);
      });
    },
    // 对后端请求过来的数据进行处理
    mapTree(options: any) {
      let arr: any = [];
      options.map((element: any) => {
        let newOptions: any = {};
        newOptions.value = element.id;
        newOptions.label = element.categoriesName;
        if (!element.children.length) {
          newOptions.children = null;
        } else {
          newOptions.children = this.mapTree(element.children);
        }
        arr.push(newOptions);
      });
      return arr;
    },
    closeProjDialog(bool: boolean) {
      this.$emit('closeDialog', this.ruleForm.categoriesId, bool);
    },
    // 测试
    handleCustom() {
      (this.$refs['form'] as any).validate((valid: boolean) => {
        if (valid) {
          const loading = this.$utils.setLoading('连接中...');
          let params = {
            ...this.ruleForm,
            dbCode: this.dbCode
          };
          dataCenterService
            .testConnect(params)
            .then((res: any) => {
              if (res.data.success) {
                this.$message.success('连接成功！');
                this.linkStatus = true;
                this.getSchemaList();
              } else {
                this.$message.error(res.data.msg || '连接失败');
              }
            })
            .catch((err: any) => {
              this.$message.error('连接失败: ' + (err.message || '未知错误'));
            })
            .finally(() => {
              loading.close();
            });
        } else {
          return false;
        }
      });
    },
    // 分类数据的整理
    classData() {
      let newClass = this.ruleForm.databaseType;
      if (this.ruleForm.databaseType.length > 1) {
        newClass = this.ruleForm.databaseType[this.ruleForm.databaseType.length - 1];
      } else {
        newClass = this.ruleForm.databaseType[0];
      }
      return newClass;
    },
    // 保存数据源信息
    formValidate() {
      (this.$refs['form'] as any).validate((valid: boolean) => {
        if (valid) {
          let params = {
            ...this.ruleForm,
            ...this.ruleCont,
            schemaFilter: this.ruleForm.schemaList ? this.ruleForm.schemaList.join(',') : '',
            dbCode: this.dbCode
          };
          this.$API
            .saveDatabase(params)
            .then((res: any) => {
              this.$message({
                type: res.data.success ? 'success' : 'warning',
                message: res.data.success ? '保存成功！' : res.data.message
              });
              this.closeProjDialog(true);
            })
            .catch();
        } else {
          return false;
        }
      });
    }
  }
});
</script>
<style lang="scss" scoped>
/deep/ .el-select {
  width: 100%;
}
.ruleCont {
  width: 20%;
}
</style>
