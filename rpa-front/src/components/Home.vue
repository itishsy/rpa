<template>
  <el-container class="home-container" id="homeContainer">
    <!-- 头部和右侧主体 -->
    <el-container>
      <!-- 头部 -->
      <el-header style="z-index:990;height:70px">
        <div class="home-header">
          <div class="menu-content">
<!--            <div class="logo" style="width:20%;">
            </div>-->
            <scrollView class="scroll-view">
              <!-- <el-menu background-color="#FFFFFF" mode="horizontal" text-color="#303133" text-size="24px"
                router active-text-color="#3E82FF" :default-active="activePath"> -->
              <el-menu background-color="#FFFFFF" mode="horizontal" text-color="#303133" text-size="24px" ref="menu"
                active-text-color="#3E82FF" :default-active="activePath">
                <!-- 一级菜单 -->
                <el-submenu :index="item.id + ''" v-for="(item, index) in menulist" :key="index"
                  v-if="item.childMenu != null && item.childMenu.length > 0">
                  <!-- 一级菜单的模板区域 -->
                  <template slot="title">
                    <!-- 图标 -->
                    <i :class="item.frontCls + ' ic-menu-icon'"></i>
                    <!-- 文本 -->
                    <span slot="title">{{ item.name }}</span>
                    <!-- 圆点 -->
                    <span class="dot" v-if="item.name === '监控与报表' && monitorDotCount > 0">{{ monitorDotCount }}</span>
                    <span class="dot" v-if="item.name === '参保管理' && (businessDotCount > 0 || loginValidCount > 0)">{{ businessDotCount+loginValidCount}}</span>
                  </template>

                  <div v-for="subItem in item.childMenu" :key="subItem.id">
                    <!-- 二级菜单区域 -->
                    <el-menu-item :index="'/' + subItem.url"
                      v-if="subItem.childMenu == null || subItem.childMenu.length == 0">
                      <template slot="title">
                        <!-- 图标 -->
                        <i :class="subItem.iconClass + ' ic-menu-icon'"></i>
                        <!-- 文本 -->
                        <!-- <span>{{ subItem.name }}</span> -->
                        <a slot="title" :href="'/#/' + subItem.url" class="a-menu">{{ subItem.name }}
                          <!-- 圆点 -->
                          <span class="dot" v-if="subItem.name === '运维监控' && monitorDotCount > 0">{{ monitorDotCount
                          }}</span>
                          <!-- 圆点 -->
                          <span class="dot" v-if="subItem.name === '企业缴费' && businessDotCount > 0">{{ businessDotCount
                          }}</span>
                          <span class="dot" v-if="subItem.name === '登录验证' && loginValidCount > 0">{{ loginValidCount
                            }}</span>
                        </a>

                      </template>
                    </el-menu-item>

                    <el-submenu :index="subItem.id + ''" v-else>
                      <!-- 二级菜单的模板区域 -->
                      <template slot="title">
                        <!-- 图标 -->
                        <i :class="subItem.frontCls + ' ic-menu-icon'"></i>
                        <!-- 文本 -->
                        <span slot="title">{{ item.name }}</span>
                        <!-- <a slot="title" :href="'/#/' + subItem.url">{{subItem.name}}</a> -->
                      </template>

                      <!-- 三级菜单的模板区域 -->
                      <el-menu-item :index="'/' + subItem3.url" v-for="subItem3 in subItem.childMenu" :key="subItem3.id">
                        <template slot="title">
                          <!-- 图标 -->
                          <i :class="subItem3.iconClass + ' ic-menu-icon'"></i>
                          <!-- 文本 -->
                          <!-- <span>{{ subItem3.name }}</span> -->
                          <a slot="title" :href="'/#/' + subItem.url" class="a-menu">{{ subItem.name }}</a>
                        </template>
                      </el-menu-item>
                    </el-submenu>
                  </div>
                </el-submenu>

                <!-- 一级菜单的模板区域 -->
                <el-menu-item v-else :index="'/' + item.url">
                  <!-- 图标 -->
                  <i :class="item.iconClass + ' ic-menu-icon'"></i>
                  <!-- 文本 -->
                  <!-- <span>{{ item.name }}</span> -->
                  <a slot="title" :href="'/#/' + item.url" class="a-menu">{{ item.name }}</a>
                </el-menu-item>

              </el-menu>
            </scrollView>
          </div>
          <div class="account-info">
            <div style="position: relative;cursor: pointer;display: inline-block;">
              <div :class="['el-icon-bell',$store.state.msgNumber > 0 ? 'animate':'']" style="margin-right: 30px;position: relative;font-size: 24px;cursor: pointer;" @click="goMessage">
              </div>
              <div class="msg-number" v-if="$store.state.msgNumber > 0" @click="goMessage">{{$store.state.msgNumber > 99 ? '99+' : $store.state.msgNumber}}</div>
            </div>
            <div class="companyName">{{ $store.state.companyInfo.custName ? 'hi，' + $store.state.companyInfo.custName : ''
            }}
            </div>
            <el-popover placement="bottom-end" class="f-cursor" style="display: inline-block;margin-right: 20px;" title=""
              width="220" popper-class="account-info-popper" trigger="click" v-model="popoverVisible">
              <div slot="reference">
                <div class="userImg">
                  <img src="../assets/images/ic-account-type7.png" alt="">
                </div>
                <span class="userName">{{ $store.state.companyInfo.name }}<i
                    class="el-icon-arrow-down el-icon--right"></i></span>
              </div>
              <div class="popper-content">
                <div class="top">
                  <div class="userImg">
                    <img src="../assets/images/ic-account-type7.png" alt="">
                  </div>
                  <span class="userName">{{ $store.state.companyInfo.name }}</span>
                </div>
                <ul>
                  <!-- <li @click="accountSettings"> -->
                  <!--<img class="li-icon" src="../assets/images/icons/ic-account-type8.png" alt="">-->
                  <!--<img class="li-icon li-icon-hover" src="../assets/images/icons/ic-set-white.png" alt="">-->
                  <!-- <span>设置账户</span> -->
                  <!-- </li> -->
                  <!-- <li @click="goMyRobot" v-if="$global.hasPermission('viewMain')">
                    <span>机器人申报城市</span>
                  </li> -->
                  <li @click="dialogVisible = true">
                    <!--<img class="li-icon" src="../assets/images/icons/ic-account-logout.png" alt="">-->
                    <!--<img class="li-icon li-icon-hover" src="../assets/images/icons/ic-loginout-white.png" alt="">-->
                    <span>退出系统</span>
                  </li>
                </ul>
              </div>
            </el-popover>
<!--            <div class="info">
              <div class="number" @click="goMessageReminder">{{ infoNumber > 99 ? '99+' : infoNumber }}</div>
            </div>-->
          </div>
        </div>
      </el-header>
      <!-- 右侧主体 -->

      <el-main ref="elMain" :class="{'big-main': !isClientRole}" v-show="isClientRole!==''">
        <div :style="{ minHeight: mianContentHeight + 'px' }" class="boxContent">
          <div class="main-container" id="mainContainer" ref="mainContainer">
            <router-view :key="routerViewKey"></router-view>
          </div>
        </div>
      </el-main>

    </el-container>
    <!-- 退出登录弹窗 -->
    <el-dialog title="确认提示" :visible.sync="dialogVisible" width="600px" :close-on-click-modal='false' :show-close='false'>
      <div class="title">
        <!--<img class="mr-20" src="../assets/images/icons/ic-account-type11.png" alt="">-->
        <p class="login-out">退出系统确认</p>
      </div>
      <p class="warning mt-10">确定要退出登录吗？</p>
      <div class="dialog-footer">
        <el-button size="small" @click="dialogVisible = false">取消</el-button>
        <el-button size="small" style="margin-left:50px;" type="primary" @click="logout">确认</el-button>
      </div>
    </el-dialog>

  </el-container>
</template>

<script>
import scrollView from '@/components/common/scrollView'
import { mapGetters } from 'vuex'

export default {
  components: { scrollView },
  inject: ['reload'],
  provide () {
    return {
      getInsuranceList: () => this.insuranceList
    }
  },
  data () {
    return {
      infoNumber: 0,
      dialogVisible: false,
      menulist: [],
      companyInfo: {},
      activePath: '',
      routerViewKey: new Date().getTime(),
      mianContentHeight: window.innerHeight - 170,
      popoverVisible: false,
      insuranceList: [],
      timer: null,
      isClientRole: '',
      notify: {},
      loginValidCount: 0
    }
  },
  computed: {
    ...mapGetters(['monitorDotCount', 'businessDotCount'])
  },
  watch: {
    $route: {
      handler: function (val, oldVal) {
        // 当前位置返回的时候用到
        this.$store.commit('updateActivePath', val.path)
        var meta = val.meta
        if (meta && meta.parentPath && meta.parentPath != null && meta.parentPath !== '') {
          this.activePath = meta.parentPath
        } else {
          this.activePath = val.path
        }
        console.log(val, this.activePath)
      },
      // 深度观察监听
      deep: true
    }
  },
  created () {
    let that = this
    // 在生命周期钩子中获取所有数据
    this.getMenuList()
    this.getCurrentInfo()
    this.timer = setInterval(() => {
      this.getCurrentInfo()
      if (this.notify.close) {
        this.notify.close()
      }
      sessionStorage.setItem('messageReminder', '')
    }, 600000)
    that.getLoginValidCount()
    setInterval(function () {
      that.getLoginValidCount()
    }, 10000)

    var meta = this.$route.meta
    var activePath = this.$store.state.activePath
    if (meta && meta.parentPath && meta.parentPath != null && meta.parentPath !== '') {
      activePath = meta.parentPath
    }
    if (activePath) {
      this.activePath = activePath
    }
    this.getDictByKey('1003')
    this.getPage()
  },
  mounted () {
    const that = this
    var elementResizeDetectorMaker = require('element-resize-detector')// 导入
    const erd = elementResizeDetectorMaker()
    erd.listenTo(document.getElementById('homeContainer'), (element) => {
      that.mianContentHeight = window.innerHeight - 170
    })

    this.bus.$on('update', (route) => {
      console.log('收到路由', route)
      this.$store.commit('updateActivePath', route)
      this.activePath = route
      this.$refs.menu.activeIndex = route
    })
  },
  beforeDestroy () {
    this.bus.$off('update')
  },
  methods: {
    logout () {
      this.$http({
        url: '/api/user/logout',
        method: 'get'
      }).then(({ data }) => {
        this.$store.dispatch('clearLoginInfo')
        this.$router.replace('/login')
        sessionStorage.setItem('messageReminder', '')
        clearInterval(this.timer)
        if (this.notify.close) {
          this.notify.close()
        }
      })
    },
    // 获取所有的菜单数据
    async getMenuList () {
      const { data: res } = await this.$http.post('/sys/user/menu/listMenuByUser/1')
      if (res.data) {
        this.menulist = res.data.menus
        this.$store.commit('menuList', this.menulist)
        this.$store.commit('tabs', res.data.tabs)
        var buttons = res.data.buttons
        if (!buttons) {
          buttons = []
        }
        this.$store.commit('buttons', res.data.buttons ? res.data.buttons : [])
      } else {
      }
    },
    // 获取当前登录用户的信息
    getCurrentInfo () {
      this.$http({
        url: '/api/user/current',
        method: 'post',
        data: {}
      }).then(({ data }) => {
        this.$store.commit('updateCompanyInfo', data.data)
        this.$store.commit('updateUserData', data.data)
        let roles = data.data.roles ? data.data.roles : []
        let isClientRole = true
        for (let i = 0; i < roles.length; i++) {
          if (roles[i] !== 'client') {
            isClientRole = false
            break
          }
        }
        this.isClientRole = isClientRole
        if (data.data.comment && data.data.comment.indexOf('小杨哥') === -1) {
          this.getListAddrMsg()
        }
      })
    },
    // 获取待登录验证条数
    getLoginValidCount () {
      let that = this
      if (!this.$store.state.token) {
        return
      }
      this.$http({
        url: '/robot/loginAuth/curUserNoLoginCount',
        method: 'post',
        data: {}
      }).then(({ data }) => {
        that.loginValidCount = data.data
      })
    },
    saveNavState (activePath) {
      this.routerViewKey = this.$global.UUID()
      this.$router.replace({
        path: activePath,
        query: {
          randomStr: this.$global.UUID() // 保证每次点击路由的query项都是不一样的，确保会重新刷新view
        }
      })
    },
    getFirstUrl (menu) {
      var url = menu.url
      if (menu.subResources && menu.subResources.length > 0) {
        url = this.getFirstUrl(menu.subResources[0])
      }
      return url
    },
    goMyRobot () {
      this.popoverVisible = false
      this.$router.push('/myRobot/index')
    },
    // 获取险种字典，用与参保险种，按返回数据排序
    async getDictByKey () {
      return this.$http({
        url: 'policy/sys/dict/getDictByKey',
        method: 'get',
        params: {
          dataKey: '10003'
        }
      })
        .then((res) => {
          this.insuranceList = res.data.data
        })
        .catch((err) => {
          if (err.response.status == 401) return
          this.$message.error('字典接口出错，请稍后再试')
        })
    },
    // 获取开发消息管理-地区消息列表
    getListAddrMsg () {
      var roles = []
      var that = this
      if (Object.prototype.toString.call(this.$store.state.userData.roles) == '[object Array]') {
        roles = this.$store.state.userData.roles.filter(item => {
          return item != 'client'
        })
      }
      if (roles.length <= 0) {
        clearInterval(this.timer)
        return
      }
      this.$http({
        url: 'agent/dev/msg/listAddrMsg',
        method: 'get'
      })
        .then((res) => {
          if (res.data.code == 200) {
            if (res.data.data && res.data.data.length > 0) {
              var arr = res.data.data.filter(item => item.coreNum != 0 || item.rpaNum != 0).map((item, index) => {
                if (index >= 1) {
                  return ''
                }
                return this.$createElement('div',
                  {
                    domProps: {
                      innerHTML: `<p>${index + 1}、${item.addrName}-${item.businessType}-${item.customerName}，core未申报：<span style="color:#FD6154;">${item.coreNum}</span>，RPA未申报：<span style="color:#FD6154;">${item.rpaNum}</span></p>`
                    }
                  })
              })
              if (arr.length >= 3) {
                arr.push(this.$createElement(
                  'p',
                  {
                    style: 'color:#3e82ff;cursor: pointer;text-align: center;',
                    on: {
                      click: this.goMessageReminder // 路由加载之后，调用关闭消息弹窗的方法
                    }
                  },
                  `查看全部${res.data.data ? '(' + res.data.data.length + ')' : ''}`
                ))
              }
              if (!sessionStorage.getItem('messageReminder')) {
                this.notify = this.$notify({
                  title: ``,
                  message: this.$createElement('div', {}, [...arr]),
                  position: 'bottom-right',
                  duration: 0,
                  dangerouslyUseHTMLString: true,
                  customClass: 'custNotifyClass',
                  onClose: () => {
                    sessionStorage.setItem('messageReminder', '1')
                  }
                })
              }
              // this.infoNumber = res.data.data ? res.data.data.length : 0
            }
          }
        })
        .catch((err) => {
          console.log(err)
          clearInterval(this.timer)
          this.$message.error('获取消息列表出错，请稍后再试')
        })
    },
    goMessageReminder () {
      if (this.notify.close) {
        this.notify.close()
      }
      sessionStorage.setItem('messageReminder', '1')
      this.$router.push('/controlAndReport/dataControl')
    },
    goMessage () {
      this.$router.push('/messageManage')
    },
    // 0：未读，1：已读
    getPage () {
      return this.$http({
        url: `/policy/message/list/page`,
        method: 'post',
        data: {
          'page': 1,
          'start': 0,
          'size': 9999,
          'query': [
            {
              'property': 'messageType',
              'value': ''
            },
            {
              'property': 'searchText',
              'value': ''
            },
            {
              'property': 'status',
              'value': 0
            }
          ],
          'sidx': '',
          'sort': ''
        }
      }).then((res) => {
        // this.infoNumber = res.data.data.records
        this.$store.commit('updateMsgNumber', res.data.data.records)
      })
        .catch((err) => {
        })
    }
  }
}
</script>
<style lang="less" scoped>
.home-container {
  position: relative;
  display: block;
  background-color: @bodyBaseBgc;
  overflow: auto;
  min-height: 100%;
  /*max-width: 100%;*/
  width: 100%;
  padding-bottom: 20px;
  box-sizing: border-box;

  /deep/.el-container {
    /*max-width: calc(100% - 231px);*/
    width: 100%;
  }

  /deep/ .el-dialog__header {
    border-bottom: 1px solid #BFBFBF;
  }

  .title {
    display: flex;
    align-items: center;

    img {
      margin-left: 36px;
    }
  }

  .login-out {
    font-size: 18px;
    color: #606266;
  }

  .warning {
    margin-left: 86px;
    margin-bottom: 50px;
  }

  .dialog-footer {
    text-align: center;
  }

  .phone-dialog {
    /deep/ .el-dialog__body {
      padding: 28px 0 0 55px;

      /deep/ .el-form-item,
      .is-required {
        margin-top: 30px;
        margin-left: 7px;

        /deep/ .el-button--default {
          width: 103px;
          height: 36px;
          margin-left: 17px;
          color: #409EFF;
          text-align: center;
          line-height: 36px;
          padding: 0;
          border-color: #409EFF;
        }
      }

      /deep/ .el-form-item:last-child {
        margin: 69px 0 19px 85px;

        /deep/ .el-button--primary {
          width: 320px;
          height: 36px;
          margin: 0 auto;
        }
      }

      .footer-info {
        color: #909399;
      }

      .footer-info:last-child {
        padding: 0px 0 19px 0;
      }
    }
  }

  .el-header {
    position: fixed;
    width: 100%;
    color: #303133;
    font-size: 20px;
    background-color: #ffffff;
    text-align: right;
    overflow: hidden;
    box-shadow: 0px 0px 10px 0px rgba(135, 175, 228, 0.1);

    .home-header {
      width: 100%;
      display: flex;

      .menu-content {
        overflow: hidden;
        display: flex;
        flex: 1;

        .logo {
          /*min-width: 340px;*/
          padding-left: 25px;
          height: 70px;
          display: flex;
          justify-content: center;
          align-items: center;

          .logo-img {
            width: 27px;
            height: 29px;
            margin-right: 11px;
          }

          .cust-select {
            min-width: 125px;

            /deep/ .el-input {
              .el-input__inner {
                font-size: 16px;
                border: none !important;
              }
            }
          }

          .enterprise-name {
            min-width: 125px;
            height: 20px;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
            font-size: 16px;
            color: #303133;
            text-align: left;
          }
        }

        .scroll-view {
          flex: 1;
          margin-left: 25px;
        }

        .toggle-button {
          font-size: 10px;
          line-height: 24px;
          color: #fff;
          text-align: center;
          letter-spacing: 0.2em;
          cursor: pointer;
        }

        .el-menu {
          border-right: none;
          height: 70px;
          background: none !important;
        }

        .el-submenu {
          border-right: none;
          height: 70px;
          background: none !important;
        }

        /deep/ .el-menu-item {
          background: none !important;
          font-size: 16px;
          padding: 5px 10px 0 10px;
          height: 100%;
        }

        /deep/ .el-submenu .el-menu,
        .el-submenu .el-menu-item {
          background: none !important;
          font-size: 16px;
        }

        /deep/.el-menu--horizontal>.el-menu-item,
        .el-menu--horizontal>.el-submenu {
          display: inline-block !important;
          float: none !important;
        }

        /deep/.el-menu--horizontal>.el-menu-item:hover {
          color: #3E82FF !important;
        }

        /deep/.el-menu--horizontal>.el-submenu>.el-submenu__title:hover {
          color: #3E82FF !important;
        }

        /deep/.el-menu--horizontal>.el-menu-item.is-active,
        /deep/.el-menu--horizontal>.el-submenu.is-active .el-submenu__title {
          border-bottom: 3px solid #409EFF;
        }

        /deep/ .el-submenu .el-submenu__title {
          font-size: 16px;
          height: 100%;
          padding: 5px 10px 0 10px;
        }

        /deep/ .el-submenu__icon-arrow,
        .el-icon-arrow-down {
          color: #606266;
        }

        .el-submenu.is-opened>.el-submenu__title .el-submenu__icon-arrow,
        .el-submenu__icon-arrow {
          color: #606266;
        }

        /deep/ .el-submenu__title {
          background: none !important;
        }

        .el-submenu.is-opened {
          background: none !important;
        }
      }

      /deep/.account-info {
        padding-right: 30px;
        padding-top: 15px;

        .userImg {
          display: inline-block;
          width: 40px;
          height: 40px;
          line-height: 40px;
          background: #E9EEF7;
          vertical-align: middle;
          border-radius: 50%;
          text-align: center;

          img {
            width: 16px;
            height: 18px;
          }
        }

        .userName {
          font-size: 14px;
          color: #606266;
          margin-left: 10px;
        }

        .container {
          background-color: #fff;
          padding: 20px;
        }

        .el-dropdown {
          margin: 0 30px 0 10px;
          cursor: pointer;
        }

        .info {
          display: inline-block;
          position: relative;
          cursor: pointer;
          vertical-align: middle;
          margin-top: 7px;

          .number {
            position: absolute;
            top: -10px;
            right: -6px;
            width: 14px;
            height: 14px;
            background: #FD6154;
            border-radius: 20px;
            font-size: 10px;
            text-align: center;
            line-height: 14px;
            color: #ffffff;
            width: 20px;
          }
        }

        /*.line {*/
        /*display: inline-block;*/
        /*vertical-align: middle;*/
        /*width: 0px;*/
        /*height: 100%;*/
        /*margin: 0 20px 0 0;*/
        /*}*/
      }
    }
  }

  .el-main {
    background-color: @bodyBaseBgc;
    height: calc(100% - 20px);
    /*padding: 70px 210px 0 210px;*/
    width: 80%;
    max-width: 1920px;
    min-width: 1200px;
    margin: 0 auto;
    position: relative;
    /*left: 50%;*/
    /*transform: translateX(-50%);*/
    overflow: hidden;
    padding: 70px 0 0px 0;
    background-color: #fff;
    /*border-bottom: 20px solid @bodyBaseBgc;*/
    border-radius: 0px 0px 6px 6px;
  }
  .big-main{
    width: 95%;
    max-width: none;
    /deep/.spl-breadcrumb{
      width: 95%;
      max-width: none;
    }
  }

  .el-main::-webkit-scrollbar {
    width: 8px;
  }

  .main-container {
    box-shadow: none !important;

  }

  .boxContent {
    background-color: #fff;
    position: relative;
  }
}

.account-info-popper {
  .popper-content {
    margin: -7px;
  }

  .top {
    display: flex;
    vertical-align: middle;
    border-bottom: 1px solid #dbdbdb;
    padding: 5px 10px 10px 10px;
    line-height: 40px;

    .userName {
      margin-left: 10px;
      flex: 1;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }

  .userImg {
    display: inline-block;
    width: 40px;
    height: 40px;
    line-height: 46px;
    background: #E9EEF7;
    vertical-align: middle;
    border-radius: 50%;
    text-align: center;

    img {
      display: inline-block;
      width: 16px;
      height: 18px;
    }
  }

  ul {
    margin: 0 -5px;
    padding: 10px 0 0 0;
  }

  li {
    height: 46px;
    line-height: 46px;
    padding-left: 20px;
    cursor: pointer;

    .li-icon-hover {
      display: none;
    }
  }

  li:hover {
    background: @blueColor;
    color: #ffffff;

    .li-icon {
      display: none;
    }

    .li-icon-hover {
      display: inline-block;
    }
  }

  .li-icon {
    display: inline-block;
    margin-right: 24px;
    width: 18px;
    height: 18px;
    vertical-align: middle;
    margin-top: -3px;
  }
}

.companyName {
  font-size: 14px;
  margin-right: 10px;
  display: inline-block;
}

/deep/ .el-menu-item {
  margin: 0 10px;
}

/deep/.el-submenu {
  margin: 0 10px;
}

.a-menu {
  -webkit-user-select: none;
  text-decoration: none;
  // display: inline-block;
  width: 100%;
  display: flex;
  align-items: center;
}

a:link {
  color: inherit;
}

a:visited {
  color: inherit;
}

a:hover {
  color: inherit;
}

a:active {
  color: inherit;
}

.dot {
  background-color: #FD6154;
  border-radius: 50%;
  width: 14px;
  height: 14px;
  line-height: 14px;
  color: #ffffff;
  font-size: 10px;
  display: inline-block;
  text-align: center;
  margin-left: 10px;
  padding: 2px;
}
</style>
<style>
.info .number {
  padding: 3px;
}
.custNotifyClass{
  padding: 12px 10px 5px 10px;
  border: 1px solid #3e82ff;
  width: auto;
  max-width: 380px;
}
.custNotifyClass .el-notification__group{
  margin: 0;
}
.custNotifyClass .el-notification__closeBtn {
  top: 6px;
  right: 8px;
}

.custNotifyClass2{
  border: 1px solid #3e82ff;
  width: auto;
  max-width: 380px;
}
.custNotifyClass2 .el-notification__closeBtn {
  top: 6px;
  right: 8px;
}
.robotCommandExeList{
  text-align: start;
  word-break: break-all;
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
  width: 310px;
}
.msg-number{
  position: absolute;
  top:50%;
  font-size: 11px;
  color: #fff;
  background: #FD6154;
  text-align: left;
  border-radius: 10px;
  padding: 1px 6px;
  left: 10px;
}
.animate{
  transform-origin: top;
  animation:linlin 1.2s infinite cubic-bezier(0.03, 1, 1, 0.03);
  animation-direction:alternate;
}
@keyframes linlin {
  0% {
    transform: rotate(0deg);
  }
  70% {
    transform: rotate(0deg);
  }
  75%{
    transform: rotate(30deg);
  }
  80%{
    transform: rotate(-30deg);
  }
  85%{
    transform: rotate(30deg);
  }
  90%{
    transform: rotate(-30deg);
  }
  95%{
    transform: rotate(30deg);
  }
  100%{
    transform: rotate(-30deg);
  }
}
</style>
