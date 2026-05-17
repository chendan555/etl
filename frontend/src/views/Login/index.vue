<template>
  <div class="login__wrapper">
    <!-- <div class="guidePage" v-show="isShowGuide">
      <div class="btndiv">
        <button @click="hideGuide">我已了解</button>
        <el-checkbox v-model="notShowGuideAgain">下次不出现引导页面</el-checkbox>
      </div>
    </div>-->
    <div class="login-form__body">
      <div class="login-form__header">数据引接处理分系统</div>
      <el-form ref="loginForm" :model="formData" :rules="formRules">
        <el-form-item label prop="userName">
          <img src="~@/assets/user.png" alt="user">
          <el-input v-model="formData.userName" placeholder="请输入用户名" @keyup.native.enter="login"></el-input>
        </el-form-item>
        <el-form-item label prop="passWord">
          <img src="~@/assets/lock.png" alt="lock">
          <el-input
            type="passWord"
            v-model="formData.passWord"
            placeholder="请输入密码"
            @keyup.native.enter="login"
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-button :loading="loading" class="login-button" @click="login">登 录</el-button>
        </el-form-item>
        <!-- <div class="forget-passWord-button">忘记密码？</div> -->
      </el-form>
    </div>
  </div>
</template>

<script lang="tsx">
import Vue from 'vue';
import { Component } from 'vue-property-decorator';
import { loginOut } from '@/api';
@Component({
  name: 'Login'
})
export default class Login extends Vue {
  formData: any = {
    userName: '',
    passWord: ''
    // systemKey: 'ETL' // 系统标识
  };

  formRules: object = {
    userName: [{ required: true, message: '请输入用户名！', trigger: 'blur' }],
    passWord: [{ required: true, message: '请输入密码！', trigger: 'blur' }]
  };

  loading: boolean = false;
  isShowGuide: boolean = false;
  notShowGuideAgain: boolean = false;
  errorCount: any = 0;
  mounted() {
    console.log(this.$route);
  }
  login() {
    (this.$refs.loginForm as any).validate((valid: boolean) => {
      if (valid) {
        this.loading = true;
        loginOut
          .login(this.formData)
          .then((res: any) => {
            this.loading = false;
            if (res.data.success && res.data.data) {
              const { token, account } = res.data.data;
              sessionStorage.setItem('token', token);
              sessionStorage.setItem('user', JSON.stringify(account));
              this.$store.commit('menuRouteLoaded', false); // 要求重新加载导航菜单
              this.$message.success('登录成功!');
              /*               if (Array.isArray(resource) && resource.length) {
                const menuOptions = resource.filter((x: any) => x.authType != 'buttons'); // 把返回的用户权限中按钮类型的过滤掉
                const allTree = this.buildToTree(menuOptions);
                const result = allTree.find((x: any) => x.title == this.formData.systemKey);
                console.log(result);
                if (result) {
                  //导航菜单为空
                  result.children = [];
                  sessionStorage.setItem('roleAuth', JSON.stringify(result.children));
                  // this.$router.push('/');
                  let pathArr = this.$route.path.split('/');
                  let rootPath = sessionStorage.getItem('rootPath');
                  if (rootPath === 'receive') {
                    this.$router.push(`/MainReceive`);
                  } else {
                    this.$router.push('/MainProcess');
                  }
                } else {
                  this.$message.warning('该用户不具有此系统相应权限, 请联系管理员进行配置！');
                }
              } else {
                this.$message.warning('该用户未进行角色权限配置, 请联系管理员进行配置！');
              }
 */
            } else {
              this.$message.error(res.data.message);
            }
          })
          .catch((err: any) => {
            this.loading = false;
            console.error(err);
          });
      }
    });
  }

  // 将数组转为树形结构
  buildToTree(list: any) {
    let result = [];
    let map: any = {};
    list.forEach((x: any) => {
      delete x.children;
      map[x.id] = x;
    });
    for (const item of list) {
      if (item.parentId == '-1') {
        result.push(item);
        continue;
      }

      if (item.parentId in map) {
        const parent = map[item.parentId];
        parent.children = parent.children || [];
        parent.children.push(item);
      }
    }
    return result;
  }

  beforeCreate() {
    // localStorage.setItem('isShowGuide', '1');
    localStorage.setItem('crtSkinType', '1');
  }
  // 是否展示导航页
  // if ((this.isShowGuide = localStorage.getItem('isShowGuide') === '1')) {
  //   this.isShowGuide = false;
  // }
  // }
}
</script>

<style lang="scss">
.login__wrapper {
  width: 100%;
  height: 100%;
  background: url('~@/assets/background.png') no-repeat;
  background-size: cover;
  position: fixed;
  top: 0;
  left: 0;
  .guidePage {
    width: 100%;
    height: 100%;
    position: fixed;
    top: 0;
    left: 0;
    background: url('~@/assets/images/guidePage.png') no-repeat center;
    background-size: 100% 100%;
    z-index: 1000;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: flex-end;
    .btndiv {
      width: 200px;
      height: 100px;
      display: flex;
      flex-direction: column;
      align-items: center;
      margin-bottom: 70px;
      button {
        width: 200px;
        height: 60px;
        background: #25432b;
        border: none;
        border-radius: 5px;
        color: #fff;
        font-size: 20px;
        margin-bottom: 10px;
        outline: none;
        cursor: pointer;
      }
    }
  }
  .login-form__body {
    width: 740px;
    height: 570px;
    box-sizing: border-box;
    padding-top: 60px;
    position: absolute;
    top: 0;
    right: 0;
    bottom: 0;
    left: 0;
    margin: auto;
    // background: url('~@/assets/login-form-bg.png') no-repeat;
    background-size: 100% 100%;
  }
  .login-form__header {
    height: 70px;
    line-height: 70px;
    margin-bottom: 20px;
    color: #fff;
    text-align: center;
    font-size: 40px;
    // background: url('~@/assets/header-text.png') no-repeat center;
  }
  .el-form {
    padding: 0 108px;
    // .el-button.el-button--default {
    //   border-color: #1d6641 !important;
    //   background-color: #1d6641 !important;
    // }
    .el-form-item {
      position: relative;
      margin-bottom: 30px;
      img {
        position: absolute;
        top: 0;
        bottom: 0;
        left: 12px;
        margin: auto;
        z-index: 2;
      }
    }
    .el-input__inner {
      height: 60px;
      line-height: 60px;
      padding-left: 70px;
      border-radius: 6px;
      // background: #313f30;
      font-size: 16px;
    }
    .login-button {
      width: 100%;
      height: 50px;
      background: #a89a50;
      border: none;
      outline: none;
      border-radius: 6px;
      margin-top: 24px;
      font-size: 18px;
    }
    .forget-passWord-button {
      position: absolute;
      bottom: 35px;
      right: 110px;
      color: #8ec196;
      font-size: 15px;
      padding-left: 4px;
      cursor: pointer;
      &:after {
        content: '';
        display: block;
        width: 100%;
        height: 1px;
        background: #8ec196;
        position: absolute;
        left: 0;
        bottom: -4px;
      }
    }
  }
}
</style>

