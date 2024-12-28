<template>
  <div class="content">
    <div class="login">
      <p class="title" ref="title">seebot社保机器人<span class="text ml-10">欢迎您！</span></p>
      <div class="login-content">
        <el-form ref="loginForm" :model="loginForm" label-width="30px" >
          <el-form-item prop="username" :rules="[{ required: true, message: '请输入用户名', trigger: 'blur'}]">
            <el-input type="text" class="user-input" placeholder="请输入用户名" v-model="loginForm.username" @keyup.enter.native="handleSubmit"></el-input>
          </el-form-item>

          <el-form-item prop="password" :rules="[{ required: true, message: '请输入登录密码', trigger: 'blur' }]">
            <el-input type="password" class="user-input" placeholder="请输入登录密码" v-model="loginForm.password" @keyup.enter.native="handleSubmit"></el-input>
          </el-form-item>

          <div class="submit">
            <el-button size="small" type="primary" @click="handleSubmit">登录</el-button>
          </div>

        </el-form>
      </div>

    </div>
  </div>
</template>

<script>
export default {
  data () {
    return {
      loginForm: {
        username: '',
        password: '',
        stamp: '',
        code: ''
      }
    }
  },
  watch: {

  },
  mounted () {

  },
  methods: {
    /* 登录 */
    handleSubmit () {
      this.$refs.loginForm.validate((valid) => {
        if (valid) {
          // let pwd = this.loginForm.code + this.loginForm.password;
          var cryptoJS = require("crypto-js");
          var key = cryptoJS.enc.Utf8.parse(this.$global.AES_KEY)
          var encryptedData = cryptoJS.AES.encrypt(this.loginForm.password, key,{
            mode: cryptoJS.mode.ECB,
            padding: cryptoJS.pad.Pkcs7
          })
          this.$http({
            url: '/oauth/token',
            method: 'post',
            params: {
              'client_id': 'rpa',
              'client_secret': '123',
              'grant_type': 'password',
              'username': this.loginForm.username,
              'password': encryptedData.toString()
            }
          }).then(({ data }) => {
            console.log(data)
            let token = data['access_token']
            if (token=='' || token == null) {
              this.$message.error('用户名或者密码错误')
              return
            }

            this.$store.commit('isLogin', true)
            this.$store.commit('updateUsername', this.loginForm.username)
            this.$store.commit('updateCompanyInfo', data)
            this.$store.commit('updateToken', token)
            this.getMenuList().then( menulist =>{
              if (menulist == null || menulist.length == 0) {
                this.$router.replace('/index')
              } else {
                var url = '/' + this.getFirstUrl(menulist[0])
                this.$router.replace(url)
              }
            })

          })
        }
      })
    },
    // 获取所有的菜单数据
    async getMenuList () {
      const { data: res } = await this.$http.post('/sys/user/menu/listMenuByUser/1')
      return new Promise((resovle,reject)=>{
        if (res.data) {
          this.$store.commit('menuList',res.data.menus)
          this.$store.commit('tabs', res.data.tabs)
          this.$store.commit('buttons', res.data.buttons?res.data.buttons:[])
          resovle(res.data.menus)
        } else {

        }
      })
      // this.reload()
    },
    getFirstUrl(menu) {
      var url = menu.url
      if (menu.subResources && menu.subResources.length > 0) {
          url = this.getFirstUrl(menu.subResources[0])
      }
      return url;
    },
    /* 忘记密码 */
    handleGoToMima () {
      this.$router.push('/retrievePsd')
    }
  }
}
</script>

<style scoped lang="less">
.content {
  position: relative;
  width: 100%;
  height: 100%;
  min-height: 550px;
  overflow: hidden;
  background: url('../assets/images/login/login-bg.png') center no-repeat;
  background-size:100% 100%;
  .login {
    width: 600px;
    height: 520px;
    position: absolute;
    top: 50%;
    left: 50%;
    margin-top: -260px;
    margin-left: -260px;
    .title {
      font-weight: bold;
      font-size: 26px;
      color: #FFFFFF;
      margin-bottom: 8px;
    }
    .text {
      font-weight: normal;
    }
    .login-content {
      background-color: #fff;
      height: 380px;
      border-radius: 5px;
      padding-top: 25px;
    }
    /deep/.el-form {
      display: flex;
      flex-flow: column;
      align-items: center;
      justify-content: center;
    }
    /deep/.el-form-item .is-required{
      display: flex;
      justify-content: center;
    }

    /deep/.el-form-item{
      margin-bottom: 10px;
    }
    /deep/.el-form-item:first-child, /deep/.el-form-item:nth-of-type(2){
      margin-bottom: 20px;
    }
    /deep/.el-form-item__content{
        display: flex;
        align-items: center;
        margin:  0 auto;
        margin-left: 0 !important;
        width: 420px;
        position: relative;
        .icon-img{
          display: inline-block;
          width: 26px;
          height: 26px;
          position: absolute;
          top: 7px;
          left: 12px;
          z-index: 999;
        }
      }
    /deep/.user-input {
      margin-top: 25px;
      .el-input__inner{
        border: none;
        border-bottom: 1px solid #D8D8D8;
        box-shadow: none;
        padding-left: 0;
        background: #FFFFFF !important;
      }
    }
      /deep/.el-input__inner{
        width: 100%;
        height: 40px !important;
        background: #fff;
        border-radius: 2px;
        border: none;
        box-shadow: none;
        }
    /deep/.select {
      margin: 0 auto;
      width: 320px;
      display: flex;
      justify-content: space-between;
      .forget-paw{
        margin-left: 175px;
        color: #909399;
      }
    }
    .submit {
      width: 420px;
      margin: 0 auto;
      margin-top: 60px;
      .el-button {
        width: 100%;
        height: 50px;
        font-size: 18px;
        border-radius: 6px;
        &.is-disabled {
          background: #D8D8D8;
          border-color: #D8D8D8;
          color: #FFFFFF;
        }
      }
    }
  }
  .copyright{
    position: fixed;
    color: #fff;
    font-size: 16px;
    bottom: 35px;
    left: 50%;
    transform: translateX(-50%);
  }
}
</style>
