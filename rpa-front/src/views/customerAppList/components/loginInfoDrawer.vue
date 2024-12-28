<template>
  <div>
    <!--登录信息-->
    <el-drawer title="账户信息" :visible.sync="show" :wrapperClosable="false"
               custom-class="spl-filter-drawer detail-drawer"
               :show-close="true" size="100%">
      <div style="padding: 15px 20px 20px 20px;">
        <div class="type-title mt-0 clearfix"><span class="text">登录信息</span></div>
        <div class="pl-10 pt-20">
          <el-row>
            <el-col :span="12" class="row-item">
              <p class="lab">客户名称：</p>
              <p class="sel">{{customerName}}</p>
            </el-col>
            <el-col :span="12" class="row-item">
              <p class="lab">应用名称：</p>
              <p class="sel">{{appName}}</p>
            </el-col>
          </el-row>
          <div v-for="(item,index) in infoList" :key="index">
            <div class="row-title">{{item.groupName}}</div>
            <el-row>
              <el-col :span="12" class="row-item" v-for="(it,ind) in item.args" :key="ind">
                <p class="lab">{{it.fieldName}}：</p>
                <p class="sel" v-if="it.fieldType==='password'">
                  <span v-if="it.argsValue">****** <span class="text-blue f-cursor ml-5" @click="doCopy(it.argsValue)">复制</span></span>
                </p>
                <p class="sel" v-else-if="it.fieldType==='photoUpload' || it.fieldType==='fileUpload'">
                  <span class="text-blue f-cursor" @click="doDownload(it.argsValue)">{{it.fileName}}</span>
                </p>
                <p class="sel" v-else>{{it.argsValue}}</p>
              </el-col>
            </el-row>
          </div>
        </div>

        <div class="type-title clearfix" style="margin-top: 10px;"><span class="text">账户情况</span>
          <span class="a-aim text-blue fr font-14 mt-15 mr-10 f-cursor" @click="viewAll">查看全部账户 <i class="el-icon-d-arrow-right"></i></span></div>
        <div class="pl-10 pt-10">
          <el-row>
            <el-col :span="12">
              <div>
                <p class="row"><span class="lab">增员申报期：</span>{{ handleDeclarePolicy(1) }}</p>
                <p class="row"><span class="lab">减员申报期：</span>{{ handleDeclarePolicy(2) }}</p>
                <p class="row"><span class="lab">补缴申报期：</span>{{ handleDeclarePolicy(5) }}</p>
              </div>
            </el-col>
            <el-col :span="12">
              <div>
                <p class="row"><span class="lab">调基申报期：</span>{{ handleDeclarePolicy(3) }}</p>
                <div class="row">
                  <span class="lab">缴费计划：</span>
                  <div class="sel"
                       v-if="declarePolicyData && declarePolicyData.policyAddrCostSettingList && declarePolicyData.policyAddrCostSettingList.length>0">
                    <p style="margin-bottom: 3px;" v-for="(item, index) in declarePolicyData.policyAddrCostSettingList"
                       :key="index" v-html="handleCostSet(item)"></p>
                  </div>
                  <div v-else>-</div>
                </div>
                <p class="row"></p>
              </div>
            </el-col>
          </el-row>
        </div>
      </div>
    </el-drawer>
  </div>
</template>
<script>
import { parseDeclarePolicy, parsePaymentPlan } from '@/utils/socialAccfundProduct'
export default {
  name: 'loginInfoDrawer',
  props: [],
  components: {},
  computed: {},
  watch: {},
  data () {
    return {
      show: false,
      customerName: '',
      appName: '',
      infoList: [],
      row: null,
      declarePolicyData: null
    }
  },
  created () {
  },
  mounted () {
  },
  methods: {
    init (obj) {
      this.customerName = obj.customerName
      this.appName = obj.appName
      this.row = obj
      this.infoList = []
      this.declarePolicyQuery()
      this.$http({
        url: `/robot/app/client/getRobotTaskConfigInfo/${obj.taskCode}`,
        method: 'post'
      }).then((data) => {
        if (data.data.code == 200) {
          this.infoList = data.data.data
          this.show = true
        }
      }).catch(() => {
      })
    },
    //   复制
    doCopy (str) {
      var cinput = document.createElement('textarea') // 动态创建textarea元素
      cinput.value = this.$global.decrypt(str) // 把需要复制的内容赋值到textarea的vlaue上
      cinput.setAttribute('readOnly', 'true')
      document.body.appendChild(cinput) // 把创建的textarea元素添加进body元素内
      cinput.select() // 选择对象
      document.execCommand('Copy') // 执行浏览器复制命令
      cinput.className = 'oInput'
      cinput.style.display = 'none'
      cinput.style.opacity = '0'
      this.$message.success('复制成功')
    },
    doDownload (id) {
      this.$downloadFile(`/policy/sys/file/download/${id}`, 'get')
    },
    viewAll () {
      this.show = false
      this.$emit('handleAccountState', this.row)
    },
    declarePolicyQuery () {
      this.$http({
        url: `/policy/declareAddr/declarePolicyQuery`,
        method: 'post',
        data: {
          addrId: Number(this.row.addrId),
          businessType: Number(this.row.businessType)
        }
      }).then((data) => {
        if (data.data.code == 200) {
          this.declarePolicyData = data.data.data
        }
      }).catch(() => {
      })
    },
    handleCostSet (item) {
      return parsePaymentPlan(item)
    },
    handleDeclarePolicy (declareType) {
      if (!this.declarePolicyData) {
        return '-'
      }
      let list = this.declarePolicyData.policyAddrDeadlineSettingList ? this.declarePolicyData.policyAddrDeadlineSettingList : []
      return parseDeclarePolicy(list, declareType)
    }
  }
}
</script>
<style lang="less" scoped>
/deep/ .detail-drawer {
  width: 800px !important;
}
.row-title {
  font-weight: bold;
  margin-bottom: 15px;
  margin-top: 10px;
}

.row-item {
  display: flex;
  margin-bottom: 15px;

  .lab {
    width: 120px;
    text-align: right;
  }

  .sel {
    flex: 1;
    margin-left: 5px;
    word-break: break-all;
  }
}
.row{
  margin-top: 10px;
}
</style>
