<template>
  <div class="wrap">
    <!-- <div class="guidePage" v-show="isShowGuide">
      <div class="btndiv">
        <button @click="hideGuide">我已了解</button>
        <el-checkbox v-model="notShowGuideAgain">下次不出现引导页面</el-checkbox>
      </div>
    </div>-->
    <!-- <div class="partLeft">
        <div class="timediv">
          <i class="el-icon-time"></i>
          <p>{{ time }}</p>
        </div>
        <div class="navdiv">
          <div
            class="navItem"
            v-for="(item, index) in getLeftMenus"
            :class="{ 'chsd': item.name === getCurrentRoute }"
            :key="'nav' + index"
            @click.stop="chsNav(item)"
          >
            <svg-icon :icon-class="`nav_${item.name}`"/>
            <p>{{ item.meta.title }}</p>
          </div>
        </div>
    </div>-->
    <!-- <div class="partRight">
        <div class="timediv"></div>
        <div class="navdiv">
          <div class="navList">
            <div
              class="navItem"
              v-for="(item, index) in getRightMenus"
              :class="{ 'chsd': item.name === getCurrentRoute }"
              :key="'nav' + index"
              @click="chsNav(item)"
            >
              <svg-icon :icon-class="`nav_${item.name}`"/>
              <p>{{ item.meta.title }}</p>
            </div>
          </div>
          <div class="userInfodiv">
            <div class="imgdiv">
              <img src="~@/assets/header/avtar.png" alt>
            </div>
            <div class="infodiv">
              <p class="name">{{ user.username }}</p>
              <p class="unit">{{ user.name }}</p>
            </div>
          </div>
          <div class="sysNavdiv">
            <div
              class="sysNav border"
              @click.stop="dropMeunVisible = true"
              id="inner"
              style="position:relative; overflow:visible"
            >
              <p class="iconSpan">
                <i class="el-icon-document"></i>
              </p>
              <transition name="el-zoom-in-top">
                <ul class="drop-menu" v-show="dropMeunVisible">
                  <li
                    @click="jump2Target(item)"
                    v-for="(item, index) in getViceNavTree"
                    :key="index"
                  >
                    <span class="icon">
                      <i class="el-icon-guide"></i>
                    </span>
                    <span>{{ item.meta.title }}</span>
                  </li>
                </ul>
              </transition>
            </div>
            <div class="signOut border" @click.stop="logout">
              <i class="el-icon-switch-button"></i>
            </div>
          </div>
        </div>
    </div>-->
    <!-- <div class="headerdiv">
      <div class="sidediv">
        <div class="timediv">
          <p>{{ time }}</p>
        </div>
      </div>
      <div class="logodiv">
        <img src="~@/assets/logo.png" alt>
        <p>{{setSystemName}}</p>
      </div>
      <div class="sidediv infodiv">
        <div class="otherNav">
          <el-tooltip effect="dark" content="工具集" placement="top">
            <p class="item" @click="showToolset">
              <i class="el-icon-coordinate"></i>
            </p>
          </el-tooltip>
        </div>
        <div class="userInfo backToHome" @click="backToHome">
          <span class="home">
            <i class="el-icon-s-home"></i>
          </span>
          <p>首页</p>
        </div>
        <div class="userInfo">
          <i class="el-icon-user"></i>
          <p>{{username}}</p>
        </div>
      </div>
    </div>-->
    <div class="contentdiv">
      <div class="navdiv">
        <!-- <div class="sconedDiv">{{setSystemName}}</div> -->
        <div class="theme-toggle" @click="toggleTheme">
          <i :class="crtSkinType === 1 ? 'el-icon-moon' : 'el-icon-sunny'"></i>
          <span>{{ crtSkinType === 1 ? '暗色主题' : '亮色主题' }}</span>
        </div>
        <div class="centerdiv">
          <p
            v-for="nav in getCrtNavList"
            :key="nav.name"
            @click="chsNav(nav)"
            :class="{ 'crtNav': nav.name === getCurrentRoute }"
          >
            <svg-icon :icon-class="`nav_${nav.name}`"/>
            <span>{{ nav.meta.title }}</span>
          </p>
        </div>
      </div>

      <div class="rightContent">
        <div class="address">
          <i class="circle"></i>
          <span class="crtNavName">{{crtNavName}}</span>
        </div>
        <div class="rightContentDiv" @click="globalEvent">
          <router-view></router-view>
        </div>
      </div>
    </div>

    <!-- 工具集 -->
    <!-- <toolset v-if="isShowToolset" @closeDialog="closeDialog"></toolset> -->

    <el-dialog title="关于" :visible.sync="aboutWindow" width="30%">
      <div class="about-window">
        <img class="logo" :src="sysLogo" alt>

        <!-- <div class="intro">数据引接处理分系统</div> -->
        <div class="intro">版本号：V0.1.0</div>
      </div>
    </el-dialog>
    <el-dialog title="一键换肤" :visible.sync="changeSkin" width="30%">
      <div class="changeSkin">
        <p>选择配色</p>
        <el-radio-group v-model="crtSkinType" @change="initTheme">
          <el-radio :label="1">黑夜版</el-radio>
          <el-radio :label="2">白天版</el-radio>
        </el-radio-group>
      </div>
    </el-dialog>
  </div>
</template>

<script>
// import Toolset from './views/Toolset/index.vue';
import colorConfig from './colorConfig/index.js';
import routerObj from './router';
import { loginOut } from '@/api';
export default {
  name: 'app',
  // components: { Toolset },
  computed: {
    address() {
      return `${this.setSystemName} — `;
    },
    username() {
      const userDetail = this.userDetail;
      return userDetail ? (userDetail.relname ? userDetail.relname : userDetail.name) : '';
    },
    activeModule() {
      return this.$route.path;
    },
    sysLogo() {
      let crtType = 3;
      return require(`@/assets/header/logo${crtType || 1}.png`);
    },
    colorStyle() {
      let arr = [];
      for (let pr in this.colorConfig[this.crtArmyType - 1][this.crtSkinType - 1]) {
        arr.push(pr);
      }
      return arr;
    },
    getNavTree() {
      return this.$store.state.navTree.filter(x => x.meta.pageType === 'mainNav');
    },
    getViceNavTree() {
      const viceNavList = this.$store.state.navTree.filter(x => x.meta.pageType === 'viceNav');
      return viceNavList.concat(this.moreList);
    },
    // getCrtNavList() {
    //   // 判断当前所属子系统
    //   let rootPath = '';
    //   return this.$store.state.navTree.filter(x => x.meta && x.meta.belongTo === rootPath);
    // },
    getLeftMenus() {
      return this.getNavTree.slice(0, 4);
    },
    getRightMenus() {
      const len = this.getNavTree.length;
      if (len > 4) {
        return this.getNavTree.slice(4, len);
      }
      return [];
    },
    getCurrentRoute() {
      const routeName = this.$route.name;
      if (routeName == 'DataReview') {
        return 'DataReviewReduce';
      }
      return this.$route.name;
    }
  },
  data() {
    return {
      // isShowGuide: false,
      // notShowGuideAgain: false,
      getCrtNavList: [],
      setSystemName: '数据接入处理',
      sconedTitle: '数据处理工具',
      time: '',
      moreList: [
        {
          name: '一键换肤',
          meta: {
            title: '一键换肤'
          },
          dialog: 'changeSkin'
        },
        {
          name: '关于',
          meta: {
            title: '关于'
          },
          dialog: 'aboutWindow'
        }
      ],
      crtNavIdx: 1,
      crtNavName: this.$route.meta.title,
      user: JSON.parse(sessionStorage.getItem('user')) || {},
      userDetail: null,
      aboutWindow: false,
      changeSkin: false,
      crtSkinType: 1,
      crtArmyType: 4, //军种，默认为1火箭军
      dropMeunVisible: false,
      colorConfig,
      // isShowToolset: false
    };
  },
  created() {
    this.userDetail = JSON.parse(sessionStorage.getItem('user'));
  },
  mounted() {
    this.setGetCrtNavList();
    // 获取登录用户真是姓名
    setTimeout(() => {
      let userDetail = JSON.parse(localStorage.getItem('userDetail'));
      userDetail && (this.userDetail = userDetail);
    }, 2000);

    // console.log(this.getCrtNavList);
    // 设置当前菜单
    if (sessionStorage.getItem('navIndex')) {
      this.crtNavIdx = sessionStorage.getItem('navIndex');
    }
    // 监听菜单切换
    // this.$eventBus.$on('setNavIndex', res => {
    //   this.crtNavIdx = res;
    //   sessionStorage.setItem('navIndex', this.crtNavIdx);
    // });
    // 页面时间定时任务
    setInterval(() => {
      this.setTime();
    }, 1000);
    this.setInnerClick();
    this.crtSkinTyped();
    this.$eventBus.$on('updateUserinfo', data => {
      this.user.username = data.username;
      this.user.name = data.name;
    });
    // this.autoLogin();
    setTimeout(() => {
      this.crtNavName = this.$route.meta.title;
    });
  },
  watch: {
    '$store.state.rootPath': {
      deep: true,
      handler(val) {
        this.setGetCrtNavList(val);
      }
    }
  },
  methods: {
    backToHome() {
      const href = window.homeUrl;
      this.$utils.downloadFile(href);
    },
    /* showToolset() {
      this.isShowToolset = true;
    },
    closeDialog() {
      this.isShowToolset = false;
    }, */
    setGetCrtNavList(rootPath) {
      rootPath = rootPath || this.$store.state.rootPath;
      // 子系统模块列表（过滤掉隐藏的菜单）
      this.getCrtNavList = this.$store.state.navTree.filter(x => x.meta && x.meta.belongTo && !x.meta.hidden);
      // 设置系统名称
      /* if (rootPath) {
        this.setSystemName = '数据接入处理';
      } else {
        const bool = window.location.hash === '#/' || window.location.hash.includes('Receive');
        this.setSystemName = '数据接入处理';
      } */
    },
    /* autoLogin() {
      const userInfo = sessionStorage.getItem('user');
      const token = sessionStorage.getItem('token');
      // console.log(userInfo);
      if (userInfo || token) {
        return;
      }
      const params = {
        userName: 'admin',
        passWord: '123456',
        systemKey: 'ETL'
      };
      loginOut
        .login(params)
        .then(res => {
          if (res.data.success && res.data.data) {
            const { token, account, resource } = res.data.data;
            sessionStorage.setItem('token', token);
            sessionStorage.setItem('user', JSON.stringify(account));
          } else {
            this.$message.error(res.data.message);
          }
        })
        .catch(err => {
          console.error(err);
        });
    }, */
    globalEvent() {
      this.$eventBus.$emit('hideTree');
    },
    initTheme(crtType) {
      let crtSkinType = crtType || this.crtSkinType; //配色版本，默认为0黑夜版
      let style = document.getElementsByTagName('body')[0].style;
      let themeConfig = this.colorConfig[this.crtArmyType - 1][crtSkinType - 1];
      for (let key in themeConfig) {
        style.setProperty(`--${key}`, themeConfig[key]);
      }
      this.changeSkin = false;
    },
    // 隐藏导航页
    // hideGuide() {
    //   if (this.notShowGuideAgain) {
    //     localStorage.setItem('isShowGuide', '1');
    //   }
    //   this.isShowGuide = false;
    // },
    // 设置当前时间
    setTime() {
      let d = new Date();
      let date = d.toLocaleDateString();
      let time = d.toTimeString().slice(0, 8);
      date = date.replace('/', '年');
      date = date.replace('/', '月');
      time = time.replace(':', '时');
      time = time.replace(':', '分');
      this.time = date + '日 ' + time + '秒';
    },
    // 主菜单切换页面
    chsNav(route) {
      // this.crtNavIdx = index;
      // sessionStorage.setItem('navIndex', this.crtNavIdx);
      // if (link) {
      //   this.$router.push('/' + link);
      // }
      this.crtNavName = route.meta.title;
      this.$router.push(route.path);
    },
    setInnerClick() {
      const dom = document.querySelector('#inner');
      const _this = this;
      if (dom) {
        let ev = e => {
          if (!dom.contains(e.target)) {
            _this.dropMeunVisible = false;
          }
        };
        document.body.addEventListener('click', ev);
        this.$on('hook:beforDestory', () => {
          document.body.removeEventListener('click', ev);
        });
      }
    },
    // 跳转下拉列表菜单页面
    jump2Target(item) {
      this.crtNavIdx = null;
      if (item.hasOwnProperty('path')) {
        this.$router.push({
          path: item.path
        });
      } else if (item.hasOwnProperty('dialog')) {
        this[item.dialog] = true;
        // this.$router.push({
        //   path: item.url
        // });
      }
    },
    // 跳转主页面
    turn2MainPage() {
      this.crtNavIdx = null;
      sessionStorage.setItem('navIndex', this.crtNavIdx);
      this.$router.push({
        path: '/'
      });
    },
    // 登出
    logout() {
      let token = sessionStorage.getItem('token');
      if (token)
        loginOut.logout({ token }).then(res => {
          if (res.data.success) {
            localStorage.removeItem('user');
            localStorage.removeItem('loglevel:webpack-dev-server');
            sessionStorage.clear();
            this.$router.push('/login');
            this.$message.success('退出登录成功!');
          } else {
            this.$message.error(res.data.message || '退出登录失败!');
          }
        });
      else this.$router.push('/login');
    },
    // 渲染的皮肤
    crtSkinTyped() {
      // 从localStorage读取保存的主题
      let savedTheme = localStorage.getItem('themeType');
      if (savedTheme) {
        this.crtSkinType = parseInt(savedTheme);
      }
      this.initTheme(this.crtSkinType);
    },
    // 切换主题
    toggleTheme() {
      this.crtSkinType = this.crtSkinType === 1 ? 2 : 1;
      localStorage.setItem('themeType', this.crtSkinType);
      this.initTheme(this.crtSkinType);
    }
  }
};
</script>
<style lang="scss">
@font-face {
  font-family: 'Digital Font';
  src: url('./styles/fonts/Digital-7Mono.TTF') format('truetype');
  font-weight: bold;
  font-style: normal;
}
.v-modal {
  z-index: 2000 !important;
}
@import '@/styles/theme.scss';
.drop-menu {
  position: absolute;
  top: 42px;
  right: 0;
  z-index: 10;
  background-color: $dropMenuBgClr;
  border: 1px solid $defaultBtnBgClr(67, 91, 68);
  > li {
    width: 140px;
    height: 40px;
    line-height: 40px;
    text-indent: 20px;
    font-size: 16px;
    &:hover {
      border-bottom: 4px solid #fefcb9;
      color: #fefcb9;
    }
  }
}
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
html {
  height: 100%;
  body {
    margin: 0;
    padding: 0;
    height: 100%;
    overflow: hidden;
    // color: #fff;
  }
  .wrap {
    height: 100%;
    background: $mainBg;
    font-size: 16px;
    // .guidePage {
    //   width: 100%;
    //   height: 100%;
    //   position: fixed;
    //   top: 0;
    //   left: 0;
    //   background: url('~@/assets/images/guidePage.png') no-repeat center;
    //   background-size: 100% 100%;
    //   z-index: 1000;
    //   display: flex;
    //   flex-direction: column;
    //   align-items: center;
    //   justify-content: flex-end;
    //   .btndiv {
    //     width: 200px;
    //     height: 100px;
    //     display: flex;
    //     flex-direction: column;
    //     align-items: center;
    //     margin-bottom: 70px;
    //     button {
    //       width: 200px;
    //       height: 60px;
    //       background: #25432b;
    //       border: none;
    //       border-radius: 5px;
    //       color: #fff;
    //       font-size: 20px;
    //       margin-bottom: 10px;
    //       outline: none;
    //       cursor: pointer;
    //     }
    //   }
    // }
    // .headerdiv {
    //   width: 100%;
    //   height: 96px;
    //   background: $headerBg;
    //   display: flex;
    //   justify-content: center;
    //   color: #fff;
    //   padding: 0 10px;
    //   border-bottom: 1px solid $currentRow;
    //   > div {
    //     // flex: 1;
    //     // height: 100%;
    //     // .timediv {
    //     //   width: 100%;
    //     //   line-height: 20px;
    //     //   height: 20px;
    //     //   display: flex;
    //     //   align-items: center;
    //     //   // color: $timeClr;
    //     //   color: #6ad20b;
    //     //   font-size: 18px;
    //     //   p {
    //     //     color: #6ad20b;
    //     //     font-family: 'Digital Font';
    //     //     margin-left: 5px;
    //     //   }
    //     // }
    //     .navdiv {
    //       width: 30%;
    //       height: 76px;
    //       display: flex;
    //       justify-content: flex-start;
    //       align-items: center;
    //       .navItem {
    //         width: 140px;
    //         max-width: 140px;
    //         height: 45px;
    //         // background: url('~@/assets/header/botton-bg-left.png') no-repeat center;
    //         // background-size: 100% 100%;
    //         display: flex;
    //         justify-content: center;
    //         align-items: center;
    //         color: $mainNavClr;
    //         font-size: 14px;
    //         margin: 0 5px;
    //         border: 1px solid #244573;
    //         border-top-left-radius: 10px;
    //         border-bottom-right-radius: 10px;
    //         cursor: pointer;
    //         img {
    //           width: 20px;
    //         }
    //         p {
    //           margin-left: 5px;
    //         }
    //       }
    //       .navItem.chsd,
    //       .navItem:hover {
    //         // background: url('~@/assets/header/botton-hover-left.png') no-repeat center;
    //         background-size: 100% 100%;
    //         color: #fff;
    //         background: $crtNavBgClr;
    //         border-bottom: 3px solid #ffff;
    //         border-top-left-radius: 10px;
    //         border-bottom-right-radius: 10px;
    //       }
    //     }
    //   }
    //   div.partLeft {
    //     .navdiv {
    //       justify-content: space-around;
    //     }
    //   }
    //   div.logodiv {
    //     display: flex;
    //     justify-content: center;
    //     align-items: flex-end;
    //     width: 500px;
    //     max-width: 500px;
    //     padding: 0 20px;
    //     img {
    //       width: 100%;
    //       margin: auto;
    //     }
    //   }
    //   div.partRight {
    //     .navdiv {
    //       justify-content: flex-end;
    //     }
    //     .navList {
    //       display: flex;
    //       align-items: center;
    //       width: auto;
    //       flex: 1;
    //     }
    //     .userInfodiv {
    //       width: 120px;
    //       max-width: 140px;
    //       height: 100%;
    //       margin-left: 25px;
    //       display: flex;
    //       align-items: center;
    //       .imgdiv {
    //         width: 60px;
    //         height: 100%;
    //         display: flex;
    //         align-items: center;
    //         img {
    //           width: 50px;
    //           // height: 60px;
    //         }
    //       }
    //       .infodiv {
    //         display: flex;
    //         flex-direction: column;
    //         justify-content: center;
    //         color: $timeClr;
    //         p {
    //           line-height: 30px;
    //           font-size: 14px;
    //         }
    //       }
    //     }
    //     .sysNavdiv {
    //       width: 120px;
    //       max-width: 120px;
    //       display: flex;
    //       justify-content: space-around;
    //       align-items: center;
    //       div.border {
    //         width: 40px;
    //         max-width: 40px;
    //         height: 40px;
    //         border: 1px solid #fff;
    //         border-radius: 5px;
    //         display: flex;
    //         justify-content: center;
    //         align-items: center;
    //         cursor: pointer;
    //         overflow: hidden;
    //       }
    //       div.sysNav .iconSpan {
    //         width: 100%;
    //         height: 100%;
    //         display: flex;
    //         justify-content: center;
    //         align-items: center;
    //         background: rgba(0, 0, 0, 0.4);
    //         color: #b0b06e;
    //         border-color: #b0b06e;
    //         border-radius: 5px;
    //       }
    //       div.signOut {
    //         background: #a76d5e;
    //         color: #fff;
    //       }
    //     }
    //   }
    // }
    .headerdiv {
      width: 100%;
      height: 100px;
      display: flex;
      justify-content: space-between;
      background: url('~@/assets/header/title-tu.png') no-repeat center;
      background-size: 100% 100%;
      background-position-y: 40px;
      .sidediv {
        width: calc((100% - 1000px) / 2);
        padding: 10px;
        .timediv {
          display: flex;
          height: 20px;
          align-items: center;
          margin-top: 30px;
          p {
            margin-left: 5px;
            // font-family: 'Digital Font';
            font-size: 18px;
          }
        }
        &.infodiv {
          display: flex;
          justify-content: flex-end;
          margin-top: 30px;
          .otherNav {
            width: 30px;
            height: 30px;
            margin: -5px 10px;
            .item {
              width: 100%;
              height: 100%;
              border: 1px solid #ccc;
              border-radius: 5px;
              text-align: center;
              line-height: 28px;
              cursor: pointer;
            }
          }
          .userInfo {
            display: flex;
            height: 20px;
            align-items: center;
            margin-left: 10px;
            p {
              margin-left: 5px;
            }
            span.home {
              display: inline-block;
              width: 24px;
              height: 24px;
              background-color: rgba(255, 255, 255, 0.2);
              border-radius: 12px;
              text-align: center;
              line-height: 24px;
            }
            &.backToHome {
              cursor: pointer;
            }
          }
        }
      }
      .logodiv {
        display: flex;
        justify-content: center;
        align-items: center;
        width: 1000px;
        text-align: center;
        line-height: 80px;
        font-size: 32px;
        color: #34e1fd;
        font-weight: bold;
        img {
          margin-right: 5px;
          width: 40px;
          height: 40px;
        }
      }
    }
    .contentdiv {
      width: 100%;
      height: 100%;
      overflow: hidden;
      overflow-y: auto;
      display: flex;
      padding: 10px;
      > .navdiv {
        width: 240px;
        height: 100%;
        background: url('~@/assets/mainPage/navBg.png') no-repeat center;
        background-size: 100% 100%;
        // border: 1px solid $currentRow;
        margin-right: 10px;
        position: relative;
        .theme-toggle {
          display: flex;
          align-items: center;
          justify-content: center;
          padding: 12px 15px;
          cursor: pointer;
          border-bottom: 1px solid rgba(255, 255, 255, 0.1);
          transition: all 0.3s;
          &:hover {
            background: rgba(255, 255, 255, 0.1);
          }
          i {
            font-size: 18px;
            color: $mainNavClr;
            margin-right: 8px;
          }
          span {
            font-size: 14px;
            color: $mainNavClr;
          }
        }
        .sconedDiv {
          width: 100%;
          height: 40px;
          // margin: 10px auto;
          // background-color: #0d213d;
          line-height: 40px;
          text-align: center;
          background: url('~@/assets/header/navActive.png') no-repeat center;
          background-size: 100% 100%;
        }
        .upImag {
          display: flex;
          justify-content: space-between;
        }
        .centerdiv {
          height: 100%;
          // background: #11243d;
          box-shadow: 0px 0px 5px #fff;
          > p {
            cursor: pointer;
            // padding: 10px 10px 10px 20%;
            padding: 10px 10px;
            line-height: 30px;
            color: #fff;
            font-size: 16px;
            > span {
              text-align: center;
              margin-left: 5px;
            }
            &:hover,
            &.crtNav {
              // background-color: $crtNavBgClr;
              color: #adccfa;
            }
          }
        }
      }
      .rightContent {
        flex: 1;
        height: 100%;
        .address {
          width: 100%;
          height: 32px;
          display: flex;
          align-items: center;
          margin-bottom: 10px;
          padding-left: 5px;
          .circle {
            width: 4px;
            height: 18px;
            display: inline-block;
            background: $tableHeaderClr;
            border-radius: 4px;
            line-height: 40px;
            margin-right: 5px;
            // margin-bottom: 3px;
          }
          span {
            color: $tableHeaderClr;
          }
        }
        .rightContentDiv {
          position: relative;
          // border: 1px solid $currentRow;
          height: calc(100% - 42px);
        }
      }
    }
  }
}

.el-table ::-webkit-scrollbar-thumb {
  background: #fff;
}
.about-window {
  .logo {
    width: 100%;
    margin-bottom: 30px;
  }
  .intro {
    text-align: right;
    color: $normalWordsClr;
  }
}
.changeSkin {
  > p {
    line-height: 32px;
    color: $normalWordsClr;
  }
}
</style>
