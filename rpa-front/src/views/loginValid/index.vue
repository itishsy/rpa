<template>
  <div class="content spl-container">
    <!-- 导航 -->
    <breadcrumb :data="pathData">
      <template slot="breadcrumb-btn">
        <el-popover
          placement="left"
          title=""
          width="400"
          trigger="hover"
          content="">
          <div>
            <p>当存在有需要执行的任务时，系统会产需要手机认证登录的记录</p>
            <p class='mt-5'>登录后，机器人会下发任务，立马执行任务</p>
            <p class='mt-5'>你需依次登录列表中的申报账户</p>
          </div>
          <span slot="reference" class="inlineBlock mt-5 f-cursor">说明 <i class='el-icon-warning-outline text-orange font-18' style='vertical-align: text-bottom'></i></span>
        </el-popover>
      </template>
    </breadcrumb>
    <div class="search-row">
      <el-row>
        <el-col :span="7" class="display-flex">
          <span class="label ml-20">参保城市：</span>
          <div class="flex1" style="max-width: 300px">
            <addrSelector v-model="search.addrName" :addrArr="options.addrList" width="100%"
                          @changeAddrSelect="getAddrId">
            </addrSelector>
          </div>
        </el-col>
        <el-col :span="7" class="display-flex">
          <span class="label ml-20">申报账户：</span>
          <el-select v-model="search.declareAccount" class="flex1" style="max-width: 300px" placeholder="请选择" clearable filterable>
            <el-option v-for="(it, index) in options.declareAccountList" :key="index" v-if="it.accountNumber" :label="it.name"
                       :value="it.accountNumber"></el-option>
          </el-select>
        </el-col>
        <el-col :span="8" class="pt-5 pl-50">
          <el-checkbox-group v-model="search.processStatusList">
            <el-checkbox v-for="item in options.processStatusList" :key="item.code" :label="item.code"
                         style="margin-right: 20px;">{{ item.name }}
            </el-checkbox>
          </el-checkbox-group>
        </el-col>
        <el-col :span="2" class="text-right pr-20">
          <el-button size="small" type="primary" class="w-60 ml-10" @click="getTableData">查询</el-button>
        </el-col>
      </el-row>
    </div>
    <div class="pl-20 pr-20">
      <!-- 表格 -->
      <dTable @fetch="getTableData" ref="table" :tableHeight="tableHeight" :paging="true" :showSelection="true"
              :showIndex="true" rowKey="id">
        <u-table-column prop="addrName" label="参保城市" min-width="80" :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="orgName" label="申报账户" min-width="200"
                        :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <span>{{ scope.row.companyName }}_{{ scope.row.accountNumber }}</span>
          </template>
        </u-table-column>
        <u-table-column prop="businessTypeName" label="类型" min-width="80" :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="declareSystemName" label="申报系统" min-width="100" :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="queueItemName" label="执行事项" min-width="120" :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="loginType" label="登录方式" min-width="100" :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="phoneNumber" label="通知号码" min-width="100" :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="createTime" label="记录生成时间" min-width="120" :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="loginTime" label="登录时间" min-width="120" :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="processStatus" label="处理状态" min-width="80" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <span>{{scope.row.processStatus===0?'待登录':'已登录'}}</span>
          </template>
        </u-table-column>
        <u-table-column prop="loginStatus" label="登录状态" min-width="120" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <span v-if="scope.row.loginStatus===0">未维持</span>
            <span v-else-if="scope.row.loginStatus===1">维持中</span>
            <span v-else-if="scope.row.loginStatus===2">已失效</span>
          </template>
        </u-table-column>
        <u-table-column fixed="right" label="操作" width="80">
          <template slot-scope="scope">
            <el-button v-if="scope.row.processStatus===0" type="text" size="small" @click="handleLogin(scope.row)">登录</el-button>
          </template>
        </u-table-column>
      </dTable>
    </div>

    <!--  登录弹窗  -->
    <loginDialog ref="loginDialog"></loginDialog>
  </div>
</template>
<script>
import dTable from '@/components/common/table'
import addrSelector from '../../components/common/addrSelector'
import loginDialog from '@/views/loginValid/components/loginDialog.vue'

export default {
  name: '',
  components: { dTable, addrSelector, loginDialog },
  props: [''],
  data () {
    return {
      pathData: [],
      search: {
        addrName: '',
        addrId: '',
        declareAccount: '',
        processStatusList: ['0']
      },
      options: {
        addrList: [],
        declareAccountList: [],
        processStatusList: [
          { code: '0', name: '待登录' },
          { code: '1', name: '已登录' }
        ]
      }
    }
  },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 270 + 'px'
    }
  },
  watch: {},
  created () {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = tabs[this.$route.path]
    }
    this.getAddrList()
    this.getDeclareAccountList()
  },
  beforeMount () {
  },
  mounted () {
    this.getTableData()
  },
  methods: {
    // 获取参保城市
    async getAddrList () {
      const { data: { code, data: result } } = await this.$http({
        url: `policy/declareAccount/availableAddress`,
        method: 'post'
      })
      if (code === 200) {
        this.options.addrList = result
      }
    },
    // 获取申报账户
    getDeclareAccountList () {
      this.search.declareAccount = ''
      this.$http({
        url: '/policy/declareAccount/seeMainBody',
        method: 'post',
        params: {
          custLimit: 1, // 传1是查当前用户的数据，不是1就查全部
          addressId: this.search.addrId
        }
      }).then((data) => {
        this.options.declareAccountList = data.data.data
      }).catch(() => { })
    },
    // 选择参保城市
    getAddrId (item) {
      if (this.search.addrId == item.id) {
        return false
      }
      this.search.addrId = item.id
      this.getDeclareAccountList()
    },
    getTableData () {
      let search = this.search
      var params = [
        { property: 'addrName', value: search.addrName },
        { property: 'declareAccount', value: search.declareAccount },
        { property: 'processStatusList', value: search.processStatusList }
      ]

      this.$refs.table.fetch({
        query: params,
        method: 'post',
        url: '/robot/loginAuth/pageList'
      })
    },
    handleLogin (row) {
      this.$refs.loginDialog.init(row)
    }
  }
}
</script>
<style lang='less' scoped>
/deep/.addr-main{
  width: 100%;
}
</style>
