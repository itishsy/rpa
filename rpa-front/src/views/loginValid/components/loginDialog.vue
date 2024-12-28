<template>
  <div>
    <!--登录验证-->
    <el-dialog title="登录验证" :visible.sync="show" class="cust-dialog" width="600px" :close-on-click-modal="false">
      <div style="padding: 0 10px 20px 10px;" v-if="row">
        <div data-v-754fd37a="" style="color: rgb(255, 116, 62); background: rgb(255, 246, 246); padding: 10px;">
          说明：当前单位申报账户存在待执行的任务且需要手机验证登录，您可提前验证，机器人再执行任务
        </div>
        <div class="pl-10 pt-10">
          <p class="row">参保城市：<span class="sel">{{row.addrName}}</span></p>
          <p class="row">申报账户：<span class="sel">{{row.companyName}}_{{row.accountNumber}}</span></p>
          <p class="row">申报系统：<span class="sel">{{row.declareSystemName}}</span></p>
          <p class="row">执行事项：<span class="sel">{{row.queueItemName}}</span></p>
          <p class="row">登录方式：<span class="sel">{{row.loginType}}</span></p>
          <p class="row">通知号码：<span class="sel">{{row.phoneNumber}}</span></p>
        </div>
        <div class="text-center mt-30">
          <div class="inlineBlock f-cursor" @click="doStartLogin">
            <i class="el-icon-video-play text-blue mb-5" style="font-size: 60px"></i>
            <p class="font-16">启动登录</p>
          </div>
        </div>
      </div>

    </el-dialog>
  </div>
</template>

<script>
import { MessageBox } from 'element-ui'
export default {
  components: {},
  props: [],
  data () {
    return {
      show: false,
      row: null,
      alertMsg: null
    }
  },
  computed: {},
  created () {
  },
  mounted () {
  },
  methods: {
    init (row) {
      this.row = row
      this.show = true
    },
    doStartLogin () {
      let that = this
      this.$http({
        url: '/robot/loginAuth/startLogin',
        method: 'post',
        params: {
          id: this.row.id
        },
        headers: {
          customSuccess: true
        }
      }).then((res) => {
        let data = res.data
        if (data.code == 200) {
          that.show = false
          let msg = this.row.loginType === '扫码登录' ? '将在15秒内打开验证页面，进行扫码登录' : '将在15秒内打开验证页面，进行短信验证码填入'
          var html = '<div>\n' +
            '        <div class="display-flex" style="align-items: center">\n' +
            '          <i class="el-icon-time text-blue" style="font-size: 60px"></i>\n' +
            '          <div class="flex1 ml-15">\n' +
            '            <div class="font-16 fw-blod">请稍等，正在启动登录程序</div>\n' +
            '            <div class="text-red mt-5">' + msg + '</div>\n' +
            '          </div>\n' +
            '        </div>\n' +
            '      </div>'
          MessageBox.confirm(html, '', {
            dangerouslyUseHTMLString: true,
            showClose: false,
            showConfirmButton: false,
            showCancelButton: false
          })
          setTimeout(function () {
            that.checkLoginAuthInfo()
          }, 3000)
        } else {
          this.$message.error(data.message ? data.message : '操作失败，请稍后再试')
        }
      }).catch(() => {})
    },
    checkLoginAuthInfo () {
      let that = this
      let row = this.row
      this.$http({
        url: '/robot/h5/checkLoginAuthInfo',
        method: 'get',
        params: {
          accountNumber: row.accountNumber,
          declareSystem: row.declareSystem,
          orgName: row.companyName,
          phoneNumber: row.phoneNumber
        }
      }).then((res) => {
        console.log(res)
        let data = res.data
        if (data.code === 200) {
          // status: 0未登录  1已登录  2已失效   101已发送短信
          if (data.data) {
            let status = data.data.status
            if (status === 0) {
              //   继续查询状态
              setTimeout(function () {
                that.checkLoginAuthInfo()
              }, 3000)
            } else if (status === 1) {
              //   1已登录
              MessageBox.close()
              that.$message.warning('已登录')
              setTimeout(function () {
                that.$emit('finish')
              }, 2000)
            } else if (status === 2) {
              //   2已失效
              MessageBox.close()
              that.$message.warning('已失效')
            } else if (status === 101) {
              //   101已发送短信
              MessageBox.close()
              window.open(data.data.data)
              that.$emit('finish')
            }
          } else {
            MessageBox.close()
            that.$message.warning('操作超时')
            setTimeout(function () {
              that.$emit('finish')
            }, 2000)
          }
        } else {
          this.$message.error(data.message ? data.message : '操作失败，请稍后再试')
        }
      }).catch(() => {})
    }
  }
}
</script>

<style lang="less" scoped>
.row{
  margin-top: 10px;
  display: flex;
  .sel{
    flex: 1;
  }
}
</style>
