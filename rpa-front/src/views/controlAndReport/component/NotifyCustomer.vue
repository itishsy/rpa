<template>
  <div>
    <el-drawer title="通知客户" :visible.sync="show" :wrapperClosable="false"
               custom-class="spl-filter-drawer detail-drawer-cur" @close="doClose">
      <div v-if="currentRow">
        <div style="padding: 20px 30px 0px 30px;">
          <el-row type="flex" style="flex-wrap:wrap;">
            <el-col :span="8">
              <div class="item-row"><span class="lab">预警项目：</span><span class="sel">{{ currentRow.warnType }}</span></div>
            </el-col>
            <el-col :span="8">
              <div class="item-row"><span class="lab">参保城市：</span><span class="sel">{{ currentRow.addrName }}</span></div>
            </el-col>
            <el-col :span="8">
              <div class="item-row"><span class="lab">业务：</span><span class="sel">{{handleFlowName(currentRow.serviceName, currentRow.serviceItemName, currentRow.flowName)}}</span></div>
            </el-col>
            <el-col :span="8">
              <div class="item-row"><span class="lab">客户名称：</span><span class="sel">{{ currentRow.clientName }}</span></div>
            </el-col>
            <el-col :span="16">
              <div class="item-row"><span class="lab">申报账户：</span><span class="sel">{{ currentRow.accountNumber }}</span></div>
            </el-col>
          </el-row>
        </div>
        <div style="background-color: #fff6f6;padding: 10px 30px;color: #FF743E;">
          <div class="mt-5">
            <span>1、仅当预警项目为【网站升级或异常】，才能通知客户。如需通知，请先更新预警项目。</span>
            <el-button v-show="currentRow.warnCode!='10017002'" size="mini" class="btn--border-blue ml-20"
                       @click="handleUpdate">确定更新
            </el-button>
          </div>
          <div class="mt-5">
            <span>2、若网站升级停服，需通知地区服务的所有客户，请点此链接发布公告。</span>
            <el-button size="mini" class="btn--border-blue ml-20" :disabled="currentRow.warnCode!='10017002'"
                       @click="handleNotice(1, 0)">发布公告
            </el-button>
          </div>
          <div class="mt-5">
            <span>3、若仅通知当前客户，且需修正消息内容，请点此前往编辑。</span>
            <el-button size="mini" class="btn--border-blue ml-20" :disabled="currentRow.warnCode!='10017002'"
                       @click="handleNotice(2, 0)">编辑内容
            </el-button>
          </div>
          <div class="mt-5">
            <span>4、若仅通知当前客户，且下方消息内容无需修正，可点此立即发送。</span>
            <el-button size="mini" class="btn--border-blue ml-20" :disabled="currentRow.warnCode!='10017002'"
                       @click="handleSendEmail">立即发送
            </el-button>
          </div>
        </div>
        <div style="padding: 20px 30px;">
          <p class="fw-blod font-16 pb-15">消息内容</p>
          <div class="messageContent">
            <div class="mb-20">{{ currentRow.messageTopic }}</div>
            <div v-html="currentRow.messageContent" class="mb-20"></div>
          </div>
        </div>
      </div>
      <div class="drawer-footer-buts">
        <!--编辑： 待提交，待申报，申报失败数据显示-->
        <el-button size="small" class="w-80" type="primary" @click="doClose">关闭</el-button>
      </div>

    </el-drawer>
  </div>
</template>
<script>
export default {
  components: {},
  props: [],
  data () {
    return {
      show: false,
      currentRow: null,
      isRefreshTable: false
    }
  },
  computed: {},
  watch: {},
  created () {
  },
  mounted () {
  },
  methods: {
    init (row) {
      this.isRefreshTable = false
      this.currentRow = row
      this.show = true
    },
    doClose () {
      this.show = false
      if (this.isRefreshTable) {
        this.$emit('refreshTable')
      }
    },
    // 业务流程-显示
    handleFlowName (name1, name2, name3) {
      let arr = []
      if (name1) {
        arr.push(name1)
      }
      if (name2) {
        arr.push(name2)
      }
      if (name3) {
        arr.push(name3)
      }
      return arr.join('-')
    },
    // 确定更新
    handleUpdate () {
      this.$confirm('请确定预警项目是否更新为【网站升级或异常】', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning'
      }).then(() => {
        this.$global.showLoading('请稍候...')
        this.$http({
          url: '/robot/operation/updateWarnType/10017002',
          method: 'post',
          data: this.currentRow
        }).then((res) => {
          this.$global.closeLoading()
          if (res.data.code == 200) {
            this.isRefreshTable = true
            this.currentRow.warnCode = '10017002'
            this.currentRow.warnType = '网站升级或异常'
            this.$message.success('操作成功')
          }
        }).catch(() => {
          this.$global.closeLoading()
        })
      }).catch(() => {
      })
    },
    handleSendEmail () {
      this.$confirm('是否确定立即通知该客户？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning'
      }).then(() => {
        this.handleNotice(2, 1)
      }).catch(() => {
      })
    },
    // 发布公告 || 编辑内容 || 立即发送
    handleNotice (type, status) {
      /* name = "type", value = "类型，1：发布公告，2：编辑内容", required = true
      "status", value = "状态，0：编辑，1：立即发送", required = true */
      let that = this
      this.$global.showLoading()
      this.$http({
        url: `/policy/message/rule/saveSystemNotice/${type}/${status}`,
        method: 'post',
        data: this.currentRow
      }).then((res) => {
        this.$global.closeLoading()
        if (res.data.code == 200) {
          if (status === 1) {
            this.$message.success('已发送该客户')
            setTimeout(function () {
              that.doClose()
            })
          } else {
            window.open(window.location.origin + '/#/controlAndReport/addMessage?type=2&id=' + res.data.data)
          }
        }
      }).catch(() => {
        this.$global.closeLoading()
      })
    }
  }
}
</script>
<style lang="less" scoped>
</style>
<style lang="less" scoped>
.lab {
  display: inline-block;
  width: 70px;
  text-align: right;
}

.input-item {
  display: inline-block;
  width: 90%;
  max-width: 200px;

  /deep/ .addr-main {
    width: 100%;
  }
}

.opt-col {
  display: flex;
  padding-left: 70px;

  .count-box {
    flex: 1;
    max-width: 200px;
    box-sizing: border-box;
    border: 1px solid #dbdbdb;
    border-radius: 6px;
    padding: 6px 10px;
    display: flex;
  }

  .count {
    font-weight: bold;
    color: @orangeColor;
    flex: 1;
    text-align: right;
  }

}

.flag {
  display: inline-block;
  width: 22px;
  height: 22px;
  line-height: 22px;
  text-align: center;
  color: #606266;
  margin-left: 5px;
  border-radius: 30px;
  font-size: 12px;
  background-color: #e4e4e4;
}

.flag-orange {
  background-color: @orangeColor;
  color: #ffffff;
}

.tag {
  display: inline-block;
  margin-right: 5px;
  text-align: center;
  font-size: 12px;
  color: #ffffff;
  background-color: #008000;
  min-width: 70px;
  height: 24px;
  line-height: 24px;
  border-radius: 30px;
  margin-right: 5px;
}

.tag-red {
  background-color: #fde2e2;
  color: #CC0000;
}

.tag-blue {
  background-color: #d9ecff;
  color: @blueColor;
}

/deep/ .detail-drawer-cur {
  width: 60%!important;
  min-width: 800px!important;
  max-width: 1000px!important;
}

.row-title {
  font-weight: bold;
  margin-bottom: 15px;
  margin-top: 10px;
}

.item-row{
  display: flex;
  margin-bottom: 20px;
  .lab{
    width: 70px;
    text-align: right;
  }
  .sel{
    flex: 1;
    padding-right: 10px;
    word-break: break-all;
  }
}
/deep/.messageContent{
  word-break: break-all;
  padding-left: 15px;
  img{
    width: 100%!important;
  }
}
</style>
