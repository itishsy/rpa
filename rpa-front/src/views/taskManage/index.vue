<template>
  <div class="content spl-container">
    <!-- 导航 -->
    <breadcrumb :data="pathData">
      <template slot="breadcrumb-btn">
        <el-button type="primary" class="search-btn mr-20" size="small" @click="goRulePage">调度规则配置</el-button>
      </template>
    </breadcrumb>
    <div class="search-row">
      <el-row>
        <el-col :span="8" class="display-flex">
          <span class="label">客户名称：</span>
          <el-select v-model="search.clientId" class="flex1" style="max-width: 300px" placeholder="请选择" clearable filterable
                     @change="getDeclareAccountList">
            <el-option v-for="it in options.custList" :key="it.id" :label="it.name" :value="it.id"></el-option>
          </el-select>
        </el-col>
        <el-col :span="8" class="display-flex">
          <span class="label ml-20">参保城市：</span>
          <div class="flex1" style="max-width: 300px">
            <addrSelector v-model="search.addrName" :addrArr="options.addrList" width="100%"
                          @changeAddrSelect="getAddrId">
            </addrSelector>
          </div>
        </el-col>
        <el-col :span="8" class="display-flex">
          <span class="label ml-20">申报账户：</span>
          <el-select v-model="search.declareAccount" class="flex1" style="max-width: 300px" placeholder="请选择" clearable filterable>
            <el-option v-for="(it, index) in options.declareAccountList" :key="index" v-if="it.accountNumber" :label="it.name"
                       :value="it.accountNumber"></el-option>
          </el-select>
        </el-col>
      </el-row>
      <el-row class="mt-10">
        <el-col :span="8" class="display-flex">
          <span class="label">任务状态：</span>
          <div class="flex1" style="padding-top: 6px;">
            <el-checkbox-group v-model="search.statusList">
              <el-checkbox v-for="item in options.statusList" :key="item.code" :label="item.code"
                           style="margin-right: 20px;">{{ item.name }}
              </el-checkbox>
            </el-checkbox-group>
          </div>
        </el-col>
        <el-col :span="12" class="display-flex">
          <span class="label ml-20">创建时间：</span>
          <div class="flex1 display-flex">
            <div class="flex1" style="max-width: 212px">
              <el-date-picker v-model="search.startTime" value-format="yyyy-MM-dd" class="w-p100"
                              type="date" placeholder="开始时间"
                              clearable></el-date-picker>
            </div>
            <span class="lh-com" style="padding: 0 5px;">至</span>
            <div class="flex1" style="max-width: 212px">
              <el-date-picker v-model="search.endTime" value-format="yyyy-MM-dd" class="w-p100"
                              placeholder="结束时间"
                              clearable></el-date-picker>
            </div>
          </div>
        </el-col>
        <el-col :span="4" class="text-right">
          <el-button size="small" type="primary" class="ml-10" @click="revoke">撤销任务</el-button>
          <el-button size="small" type="primary" class="w-60 ml-10" @click="getTableData">查询</el-button>
        </el-col>
      </el-row>
    </div>
    <div class="pl-20 pr-20">
      <!-- 表格 -->
      <dTable @fetch="getTableData" ref="table" :tableHeight="tableHeight" :paging="true" :showSelection="true"
              :showIndex="false" rowKey="id" :rowHeight="50" @selection-change="selectChange">
        <u-table-column prop="sort" label="优先级" min-width="80" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <span v-if='scope.row.status===2'>{{ scope.row.sort }}</span>
          </template>
        </u-table-column>
        <u-table-column prop="customerName" label="客户名称" min-width="150" :show-overflow-tooltip="true">
          
        </u-table-column>
        <u-table-column prop="addrName" label="参保城市" min-width="80" :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="orgName" label="申报账户" min-width="200"
                        :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <span>{{ scope.row.companyName }}_{{ scope.row.declareAccount }}</span>
          </template>
        </u-table-column>
        <u-table-column prop="businessTypeName" label="类型" min-width="100" :show-overflow-tooltip="true">
          <template #header>
            <span class="mr-10">类型</span>
            <i class="el-icon-setting f-cursor" @click='handelShowHeader'></i>
            <el-select v-if="showHeader" v-model="search.businessType" placeholder="请选择" @change="changeHeaderSelect" clearable>
              <el-option label="社保" value="1"></el-option>
              <el-option label="公积金" value="2"></el-option>
            </el-select>
          </template>
        </u-table-column>
        <u-table-column prop="queueItemName" label="执行事项" min-width="120" :show-overflow-tooltip="true">
          <template #header>
            <span class="mr-10">执行事项</span>
            <i class="el-icon-setting f-cursor" @click='handelShowHeader'></i>
            <el-select v-if="showHeader" v-model="search.queueItem" placeholder="请选择" @change="changeHeaderSelect" clearable>
              <el-option v-for="item in options.queueItemList" :label="item.label" :value="item.value" :key="item.label"></el-option>
            </el-select>
          </template>
        </u-table-column>
        <u-table-column prop="serviceItemName" label="执行项目" min-width="160" :show-overflow-tooltip="true">
        </u-table-column>
        <u-table-column prop="declareSystemName" label="执行系统" min-width="120" :show-overflow-tooltip="true">
          <template #header>
            <span class="mr-10">执行系统</span>
            <i class="el-icon-setting f-cursor" @click='handelShowHeader'></i>
            <el-select v-if="showHeader" v-model="search.declareSystem" placeholder="请选择" @change="changeHeaderSelect" clearable>
              <el-option v-for="item in options.declareSystemList" :label="item.dictName" :value="item.dictCode" :key="item.dictCode"></el-option>
            </el-select>
          </template>
        </u-table-column>
        <u-table-column prop="loginType" label="登录方式" min-width="160" :show-overflow-tooltip="true">
          <template #header>
            <span class="mr-10">登录方式</span>
            <i class="el-icon-setting f-cursor" @click='handelShowHeader'></i>
            <el-input v-if="showHeader" v-model="search.loginType" placeholder="请输入内容" @input="changeInput"></el-input>
          </template>
        </u-table-column>
        <u-table-column prop="registerNumber" label="任务状态" min-width="80" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <span>{{handleStatus(scope.row.status)}}</span>
          </template>
        </u-table-column>
        <u-table-column prop="registerList" label="执行设备" min-width="150" :show-overflow-tooltip="true">
          <template #header>
            <span class="mr-10">执行设备</span>
            <i class="el-icon-setting f-cursor" @click='handelShowHeader'></i>
            <el-select v-if="showHeader" v-model="search.machineCode" placeholder="请选择" @change="changeHeaderSelect" clearable>
              <el-option v-for="item in options.machineCodeList" :label="item.machineCode" :value="item.machineCode" :key="item.machineCode"></el-option>
            </el-select>
          </template>
          <template slot-scope="scope">
            <el-tooltip class="item" effect="dark" :disabled="!scope.row.machineCode" :content="'设备厂商：'+scope.row.machineFactory" placement="top">
              <span>{{scope.row.machineCode?scope.row.machineCode:'待分配'}}</span>
            </el-tooltip>
          </template>
        </u-table-column>
        <u-table-column prop="fixMachineCode" label="指定设备" min-width="150" :show-overflow-tooltip="true">
          <template #header>
            <span class="mr-10">指定设备</span>
            <i class="el-icon-setting f-cursor" @click='handelShowHeader'></i>
            <el-select v-if="showHeader" v-model="search.fixMachineCode" placeholder="请选择" @change="changeHeaderSelect" clearable>
              <el-option v-for="item in options.machineCodeList" :label="item.machineCode" :value="item.machineCode" :key="item.machineCode"></el-option>
            </el-select>
          </template>
        </u-table-column>
        <u-table-column prop="comment" label="调度备注" min-width="160" :show-overflow-tooltip="true">
          <template #header>
            <span class="mr-10">调度备注</span>
            <i class="el-icon-setting f-cursor" @click='handelShowHeader'></i>
            <el-input v-if="showHeader" v-model="search.comment" placeholder="请输入内容" @input="changeInput" clearable></el-input>
          </template>
        </u-table-column>
        <u-table-column prop="phoneNumber" label="手机号码" min-width="160" :show-overflow-tooltip="true">
          <template #header>
            <span class="mr-10">手机号码</span>
            <i class="el-icon-setting f-cursor" @click='handelShowHeader'></i>
            <el-input v-if="showHeader" v-model="search.phoneNumber" placeholder="请输入内容" @input="changeInput"></el-input>
          </template>
        </u-table-column>
        <u-table-column prop="createTime" label="任务生成时间" min-width="160" :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="accfundRegisterNumber" label="预计时间" min-width="150" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <p>{{scope.row.preStartTime}}</p>
            <p>{{scope.row.preEndTime}}</p>
          </template>
        </u-table-column>
        <u-table-column prop="accfundRegisterNumber" label="实际时间" min-width="150" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <p>{{scope.row.praStartTime}}</p>
            <p>{{scope.row.praEndTime}}</p>
          </template>
        </u-table-column>
        <u-table-column fixed="right" label="操作" width="80">
          <template slot-scope="scope">
            <el-button v-if="scope.row.status===2" type="text" size="small" @click="handleToTop(scope.row.id)">优先执行</el-button>
            <el-button v-else type="text" size="small" @click="viewLog(scope.row)">执行日志</el-button>
          </template>
        </u-table-column>
      </dTable>
    </div>
  </div>
</template>
<script>
import dTable from '@/components/common/table'
import addrSelector from '../../components/common/addrSelector'

export default {
  name: '',
  components: { dTable, addrSelector },
  props: [''],
  data () {
    return {
      pathData: [],
      search: {
        clientId: '', // 客户id
        addrName: '',
        addrId: '',
        declareAccount: '',
        statusList: [],
        startTime: this.$moment(new Date()).format('YYYY-MM-DD'),
        endTime: this.$moment(new Date()).format('YYYY-MM-DD'),
        businessType:"",
        queueItem:"",
        declareSystem:"",
        machineCode:"",
        fixMachineCode:"",
        comment:"", //string
        phoneNumber:"", //string
        loginType:""
      },
      options: {
        custList: [],
        addrList: [],
        declareAccountList: [],
        statusList: [
          { code: 2, name: '待执行' },
          { code: 1, name: '执行中' },
          { code: 4, name: '执行成功' },
          { code: 3, name: '执行中断' }
        ],
        machineCodeList:[],
        queueItemList:[
          {label:'增减员',value:1},
          {label:"缴费",value:6},
          {label:'在册名单',value:7},
          {label:'费用明细',value:8},
          {label:'政策通知',value:9},
          {label:'基数申报',value:10},
          {label:'登录',value:11},
          {label:'查审核结果',value:12},
        ],
        declareSystemList:[]
      },
      showHeader:false,
      timer:null,
      tableSelectList:[]
    }
  },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 310 + 'px'
    }
  },
  watch: {},
  created () {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = tabs[this.$route.path]
    }
    this.getCustomerList()
    this.getAddrList()
    this.getDeclareAccountList()
    this.getMachineByClientId()
    this.getDictByKey("10007")
    this.getDictByKey("10008")
  },
  mounted () {
    this.getTableData()
  },
  methods: {
    getCustomerList () {
      let that = this
      this.$http({
        url: '/policy/sys/customer/listAllCustomer?filter=false&status=',
        method: 'post'
      }).then(({ data }) => {
        that.options.custList = data.data
      }).catch((data) => {
      })
    },
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
          custId: this.search.clientId,
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
      let startTime = search.startTime
      let endTime = search.endTime
      if (search.startTime && search.endTime && (new Date(startTime) > new Date(endTime))) {
        this.$message.warning('创建开始时间不能大于结束时间')
        return
      }
      var params = [
        { property: 'clientId', value: search.clientId },
        { property: 'addrId', value: search.addrId },
        { property: 'declareAccount', value: search.declareAccount },
        { property: 'statusList', value: search.statusList },
        { property: 'startTime', value: startTime },
        { property: 'endTime', value: endTime },
        { property: 'businessType', value: search.businessType  },
        { property: 'queueItem', value: search.queueItem  },
        { property: 'declareSystem', value: search.declareSystem  },
        { property: 'machineCode', value: search.machineCode  },
        { property: 'fixMachineCode', value: search.fixMachineCode  },
        { property: 'comment', value: search.comment  },
        { property: 'phoneNumber', value: search.phoneNumber  },
        { property: 'loginType', value: search.loginType  },
      ]
      this.$refs.table.fetch({
        query: params,
        method: 'post',
        url: '/robot/taskQueue/page'
      })
    },
    handleStatus (code) {
      return this.options.statusList.find(item => item.code === code).name
    },
    // 优先执行
    handleToTop (id) {
      let that = this
      that.$confirm('是否确定优先执行该任务？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning'
      }).then(() => {
        that.$global.showLoading()
        that.$http({
          url: '/robot/taskQueue/toTop?id=' + id,
          method: 'post'
        }).then((data) => {
          that.$global.closeLoading()
          that.$message.success('操作成功')
          that.getTableData()
        }).catch(() => { })
      }).catch(() => {
        that.$global.closeLoading()
      })
    },
    // 执行日志
    viewLog (row) {
      let routeData = this.$router.resolve({
        path: '/customerAppList/customerIndex', // 需要打开的路由名称
        query: {
          clientId: row.clientId,
          appCode: row.appCode,
          taskCode: row.taskCode,
          filterFlowCodes: null,
          goExecutionQuery: true
        }
      })
      window.open(routeData.href, '_blank')
    },
    goRulePage () {
      this.$router.push('/customerRules')
    }, 
    NumberRenderHeader(createElement, { column, $index }) {
      let self = this;
      // if (!item.isHeadSearch) {
      //   return item.label;
      // }
      var timer = null
      return createElement("div", [
        createElement("div", {
          style:"display:flex;justify-content: space-between;align-items: center;",
          domProps: {
            innerHTML: column.label + '<i class="el-icon-search" style="font-size:16px;cursor:pointer;"></i>'
          }
        }),
        createElement("div",{style:"background:#fff;"},
          [createElement("el-input", {
            props:{
              value:self.a
            },
            on:{
              input:(value)=>{
                self.a = value
                clearTimeout(timer)
                timer = setTimeout(()=>{
                  self.getTableData2()
                },2000)
              }
            },
            attrs:{placeholder:'请输入'}
          })]
        )
      ]);
    },
    // 获取列表查询
    getDictByKey (dataKey) {
      this.$http({
        url: '/policy/sys/dict/getDictByKey?dataKey='+dataKey,
        method: 'get',
      }).then(res => {
        this.$global.closeLoading()
        this.options.declareSystemList = this.options.declareSystemList.concat(res.data.data)
      }).catch(err => {
        this.$global.closeLoading()
      })
    },
    getMachineByClientId () {
      this.$http({
        url: '/robot/app/client/getMachineByClientId?clientId=',
        method: 'get'
      }).then((data) => {
        if (data.data.code == '200') {
          this.options.machineCodeList = data.data.data
        }
      }).catch(() => {
        this.isloading = false
      })
    },
    changeHeaderSelect(){
      this.getTableData()
      this.$refs.table.doLayout()
    },
    handelShowHeader(){
      this.showHeader = !this.showHeader
      if(!this.showHeader){
        this.search.businessType=""
        this.search.queueItem=""
        this.search.declareSystem=""
        this.search.machineCode=""
        this.search.fixMachineCode=""
        this.search.comment=""
        this.search.phoneNumber=""
        this.search.loginType = ""
        this.getTableData()
      }
      this.$nextTick(()=>{
        this.$refs.table.doLayout()
      })
    },
    changeInput(){
      clearTimeout(this.timer)
      this.timer = setTimeout(()=>{
        this.getTableData()
      },1000)
    },
    selectChange(val){
      this.tableSelectList = val 
    },
    revoke(){
      if(this.tableSelectList.length <=0){
        this.$message.warning('请选择数据')
        return
      }
      var arr = this.tableSelectList.filter(item=>{return item.status == 2})
      if(arr.length <=0){
        this.$message.warning("请至少选择一条待执行任务撤销")
        return
      }
      var ids = arr.map(item=>{
        return item.id
      }).join(",")
      this.$global.showLoading()
      this.$http({
        url: '/robot/taskQueue/revoke?ids='+ids,
        method: 'post',
      }).then((res) => {
        this.$global.closeLoading()
        if (res.data.code == '200') {
          this.$message.success("操作成功")
          this.getTableData()
        }else{
          this.$message.error(res.data.message ? res.data.message : '操作失败，请稍后再试')
        }
      }).catch(() => {
        this.$global.closeLoading()
      })
    }
  }
}
</script>
<style lang='less' scoped>

</style>
