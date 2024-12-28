<template>
  <div class="content spl-container">
    <!-- 导航 -->
    <breadcrumb :data="pathData">
    </breadcrumb>
    <!-- Tabs -->
    <div style="padding: 20px 20px 0 20px">
      <palTab :tabs="tabs" v-model="tabActive" :type="2" @change="changeTab">
      </palTab>
    </div>

    <!--社保-->
    <div v-show="tabActive == 0">
      <div class="search-row">
        <el-row>
          <el-col :span="7" class="display-flex align-center">
            <span class="label required">申报单位：</span>
            <el-select v-model="socialSearch.orgId" class="w-300" placeholder="请选择申报单位" clearable filterable
              @change="onSelectChange('orgId', $event)" disabled>
              <el-option v-for="it in customMadeList" :key="it.id" :label="it.orgName" :value="it.id"></el-option>
            </el-select>
          </el-col>
          <el-col :span="7" class="display-flex align-center">
            <span class="label required">参保年月：</span>
            <div style="display:flex;max-width:300px">
              <el-date-picker v-model="socialSearch.dataMonth" value-format="yyyy-MM" style="width:100%;" type="month"
                placeholder="请选择参保年月" clearable @change="onSelectChange('dataMonth', $event)"></el-date-picker>
            </div>
          </el-col>
          <el-col :span="6" class="display-flex align-center">
            <span class="label">关键字：</span>
            <!-- <el-input v-model="socialSearch.searchText" @keyup.enter.native="clickQuery"  @blur="removeSpaces()" placeholder="请输入姓名/证件号码关键字" style="width: 250px;"></el-input> -->
            <el-input v-model.trim="socialSearch.searchText" @keyup.enter.native="clickQuery" placeholder="请输入姓名/证件号码关键字"
              style="width: 250px;"></el-input>
          </el-col>
          <el-col :span="4" class="text-right">
            <el-button size="small" type="primary" class=" w-60 search-btn" @click="clickQuery">查询</el-button>
          </el-col>
        </el-row>
        <el-row class="mt-15">
          <el-col :span="7" class="display-flex align-center">
            <span class="label">申报系统：</span>
            <el-select v-model="socialSearch.tplTypeCode" class="w-300" placeholder="请选择申报系统" clearable filterable
              @change="onSelectChange('tplTypeCode', $event)">
              <el-option v-for="it in systemArr" :key="it.id" :label="it.dictName" :value="it.dictCode"></el-option>
            </el-select>
          </el-col>
        </el-row>
      </div>

      <div class="">
        <div
          style="width: 100%;background-color: #e2e2e2;padding: 6px 0;display: flex;justify-content: space-between;align-items: center;">
          <span class="ml-30">在户人数：{{ totalMember }}</span>
          <el-button size="small" plain type="primary" class="mr-10" @click="routeToDetail(1)">费用明细</el-button>
        </div>
        <!-- 表格 -->
        <dTable @fetch="getSocialTable" ref="socailTable" :tableHeight="tableHeight" :paging="true" :showSelection="true"
          :showIndex="true" rowKey="id" :rowHeight="45">
          <u-table-column prop="empName" label="姓名" min-width="200" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="idCard" label="证件号码" min-width="160" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="accountNumber" label="单位社保号" min-width="100"
            :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="empAccount" label="个人社保号" min-width="160" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="addrName" label="参保城市" min-width="160" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="dataMonth" label="参保年月" min-width="160" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="base" label="缴纳基数" min-width="160" :show-overflow-tooltip="true"></u-table-column>

          <!-- <u-table-column fixed="right" label="操作" width="100">
            <template slot-scope="scope">
              <el-button type="text" size="small" @click="viewRequest('1', scope.row)">查看</el-button>
            </template>
          </u-table-column> -->
          <template slot="pagination-btns">
            <div class="display-flex">
              <el-button size="small" icon="icon ic-export-blue" class="btn--border-blue s-btn"
                @click="exportTableData">导出</el-button>
              <el-button size="small" icon="icon ic-export-blue" class="btn--border-blue s-btn"
                @click="exportSourceTableData">导出原始表</el-button>
            </div>
          </template>
        </dTable>
      </div>

    </div>
    <!-- 公积金 -->
    <div v-show="tabActive == 1">
      <div class="search-row">
        <el-row>
          <el-col :span="7" class="display-flex align-center">
            <span class="label required">申报单位：</span>
            <el-select v-model="accfoundSearch.orgId" class="w-300" placeholder="请选择申报单位" clearable filterable disabled
              @change="onSelectChange('orgId', $event)">
              <el-option v-for="it in accfoundMadeList" :key="it.id" :label="it.orgName" :value="it.id"></el-option>
            </el-select>
          </el-col>
          <el-col :span="7" class="display-flex align-center">
            <span class="label required">参保年月：</span>
            <div style="display:flex;max-width:300px">
              <el-date-picker v-model="accfoundSearch.dataMonth" value-format="yyyy-MM" style="width:100%;" type="month"
                placeholder="选择参保年月" clearable @change="onSelectChange('dataMonth', $event)"></el-date-picker>
            </div>
          </el-col>
          <el-col :span="6" class="display-flex align-center">
            <span class="label">关键字：</span>
            <el-input v-model.trim="accfoundSearch.searchText" @keyup.enter.native="clickQuery"
              placeholder="请输入姓名/证件号码关键字" style="width: 250px;"></el-input>
          </el-col>
          <el-col :span="4" class="text-right">
            <el-button size="small" type="primary" class=" w-60 search-btn" @click="clickQuery">查询</el-button>
          </el-col>
        </el-row>
      </div>

      <div class="">
        <div
          style="width: 100%;background-color: #e2e2e2;padding: 6px 0;display: flex;justify-content: space-between;align-items: center;">
          <span class="ml-30">在户人数：{{ totalMember }}</span>
          <el-button size="small" plain type="primary" class="mr-10" @click="routeToDetail(2)">费用明细</el-button>
        </div>
        <!-- 表格 -->
        <dTable @fetch="getAccfoundTable" ref="accfoundTable" :tableHeight="tableHeight" :paging="true"
          :showSelection="true" :showIndex="true" rowKey="id" :rowHeight="45">
          <u-table-column prop="empName" label="姓名" min-width="200" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="idCard" label="证件号码" min-width="160" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="accountNumber" label="单位公积金号" min-width="100"
            :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="empAccount" label="个人公积金号" min-width="160" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="addrName" label="参保城市" min-width="160" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="dataMonth" label="参保年月" min-width="160" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="base" label="缴纳基数" min-width="160" :show-overflow-tooltip="true"></u-table-column>

          <!-- <u-table-column fixed="right" label="操作" width="100">
            <template slot-scope="scope">
              <el-button type="text" size="small" @click="viewRequest('1', scope.row)">查看</el-button>
            </template>
          </u-table-column> -->
          <template slot="pagination-btns">
            <div class="display-flex">
              <el-button size="small" icon="icon ic-export-blue" class="btn--border-blue s-btn"
                @click="exportTableData">导出</el-button>
              <el-button size="small" icon="icon ic-export-blue" class="btn--border-blue s-btn"
                @click="exportSourceTableData">导出原始表</el-button>
            </div>
          </template>
        </dTable>
      </div>
    </div>
  </div>
</template>
<script>
import dTable from '@/components/common/table'
import palTab from '@/components/common/pal-tab'
export default {
  name: '',
  components: { dTable, palTab },
  props: [''],
  data() {
    return {
      pathData: [],
      tabs: ['社保', '公积金'],
      tabActive: 0,
      socialSearch: {
        orgId: null,     //申报单位Id
        businessType: 1,      //社保
        dataMonth: null,     //参保年月
        searchText: '',    //关键字
        tplTypeCode: null    //申报系统
      },
      accfoundSearch: {
        orgId: null,     //申报单位Id
        businessType: 2,      //社保
        dataMonth: null,     //参保年月
        searchText: ''    //关键字
      },
      systemArr: [],     //社保申报系统
      customMadeList: [],     //社保申报单位下拉
      accfoundMadeList: [],      //公积金申报单位下拉      
      totalMember: '-',      //在户人数
      disabledIndex: -1        //当前不可用
    };
  },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 350 + 'px'
    },
  },
  watch: {},
  created() {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = this.$global.deepcopyArray(
        tabs[this.$route.meta.parentPath]
      )
    }
    this.pathData.push({ name: '在户名单' })

    this.socialSearch.orgId = parseInt(this.$route.query.orgId)
    this.accfoundSearch.orgId = parseInt(this.$route.query.orgId)
    this.socialSearch.dataMonth = this.$route.query.dataMonth
    this.accfoundSearch.dataMonth = this.$route.query.dataMonth
    
    // this.getDeclarationSystem()
    // this.getOrgList(1)      //通过OrgId请求社保单位下拉，并校验该单位是否在社保下
    // this.getOrgList(2)      //通过orgId请求公积金单位下拉，并校验该单位是否在公积金下
    Promise.all([this.getOrgList(1), this.getOrgList(2)]).then(() => {  
      this.clickQuery()
      this.getHeaderStatistics()
    });
  },
  beforeMount() { },
  mounted() {

    this.getSocialSytemByUnit(this.$route.query.orgId)
  },
  methods: {
    changeTab(index) {
      console.log('this.disabledIndex------',this.disabledIndex,index)
      if (this.disabledIndex == 0 && this.disabledIndex == index) {
        //社保不可用
        this.$confirm('该单位不存在社保账号！', '提示', {
          confirmButtonText: '确定',
          showClose: false,
          showCancelButton: false,
          closeOnClickModal:false,
          closeOnPressEscape:false,
          customClass: 'pal-confirm',
          type: 'warning',
        }).then(() => {
          this.tabActive = 1
        })
        return
      }
      if (this.disabledIndex == 1 && this.disabledIndex == index) {
        //公积金不可用
        this.$confirm('该单位不存在公积金账号！', '提示', {
          confirmButtonText: '确定',
          showClose: false,
          showCancelButton: false,
          closeOnClickModal:false,
          closeOnPressEscape:false,
          customClass: 'pal-confirm',
          type: 'warning',
        }).then(() => {
          this.tabActive = 0
        })

        return
      }
      this.tabActive = index
      this.clickQuery()
      this.getHeaderStatistics()
    },
    clickQuery() {
      if (this.tabActive == 0) {
        if (!this.socialSearch.orgId) {
          this.$message.warning('请先选择申报单位')
          return
        }
        if (!this.socialSearch.dataMonth) {
          this.$message.warning('请先选择参保年月')
          return
        }
        // if (!this.socialSearch.tplTypeCode) {
        //   this.$message.warning('请先选择申报系统')
        //   return
        // }
        this.getSocialTable()
      } else {
        if (!this.accfoundSearch.orgId) {
          this.$message.warning('请先选择申报单位')
          return
        }
        if (!this.accfoundSearch.dataMonth) {
          this.$message.warning('请先选择参保年月')
          return
        }
        this.getAccfoundTable()
      }
    },
    //获取申报系统
    async getDeclarationSystem() {
      const { data: { code, data: result } } = await this.$http({
        url: `policy/sys/dict/getDictByKey?dataKey=10007`,
        method: 'get'
      })
      console.log('getDeclarationSystem-----------------', code, result)
      if (code == 200) {
        this.systemArr = result
      }
    },
    //获取申报单位 type 1：社保 2：公积金
    getOrgList(type) {
      return this.$http({
        url: 'policy/customMade/offerSettings/getOrgList',
        method: 'post',
        params: {
          addrId: '',
          custFilter: 1,
          businessType: type,
          custId:''
        },
      })
        .then((res) => {
          res.data.data = res.data.data[0] == null ? res.data.data.slice(1) : res.data.data
          if (type == 1) {
            this.customMadeList = res.data.data
            if (!this.changeBusinessType(1)) {
              //当前传入orgId不存在社保申报单位下拉中 
              this.socialSearch.orgId = ''
              //自动切换至公积金
              this.tabActive = 1
              this.$set(this,'disabledIndex',0) //当前社保tab不可用
              console.log('当前不可用', this.disabledIndex)
            } 
          } else {
            this.accfoundMadeList = res.data.data
            if (!this.changeBusinessType(2)) {
              //当前传入orgId不存在公积金申报单位下拉中 
              this.accfoundSearch.orgId = ''
              this.disabledIndex = 1
              //自动切换至社保
              this.tabActive = 0
              this.$set(this,'disabledIndex',1) //当前公积金tab不可用
              console.log('当前不可用', this.disabledIndex)
            } 
          }
        })
        .catch((item) => { })

    },
    changeBusinessType(type) {
      let tempList = type == 1 ? this.customMadeList : this.accfoundMadeList
      var check = false
      let searchData = type == 1 ? this.socialSearch : this.accfoundSearch
      tempList.forEach((item) => {
        if (item.id == searchData.orgId) {
          check = true
        }
      })
      return check
    },
    //获取社保表格
    getSocialTable() {
      let that = this
      let search = this.socialSearch
      var params = [
        { property: 'orgId', value: search.orgId },
        { property: 'dataMonth', value: search.dataMonth },
        { property: 'businessType', value: search.businessType },
        { property: 'searchText', value: search.searchText },
        { property: 'tplTypeCode', value: search.tplTypeCode },
      ]

      this.$refs.socailTable.fetch({
        query: params,
        method: 'post',
        url: '/policy/cust/insured/detailPage'
      })
    },
    //获取公积金表格
    getAccfoundTable() {
      let that = this
      let search = this.accfoundSearch
      var params = [
        { property: 'orgId', value: search.orgId },
        { property: 'dataMonth', value: search.dataMonth },
        { property: 'businessType', value: search.businessType },
        { property: 'searchText', value: search.searchText },
      ]

      this.$refs.accfoundTable.fetch({
        query: params,
        method: 'post',
        url: '/policy/cust/insured/detailPage',
        callback: function (res) {
          console.log('res----------', res)
          if (res.records == 0) return
        },
      })
    },
    //导出数据
    exportTableData() {
      let params = null
      if (this.tabActive == 0) {
        params = this.$refs.socailTable.getParamsQuery()
      } else {
        params = this.$refs.accfoundTable.getParamsQuery()
      }
      if (!params) {
        this.$message.warning('请先查询数据')
        return
      }

      var params2 = this.$global.deepcopyArray(params)
      // params2.push({ property: 'businessType', value: parseInt(this.tabActive + 1) })
      this.$downloadFile(
        '/policy/cust/insured/exportDetail',
        'post',
        {
          query: params2,
        },
        this.$global.EXCEL
      )
    },
    //导出原始数据
    exportSourceTableData() {
      let params = null
      if (this.tabActive == 0) {
        params = this.$refs.socailTable.getParamsQuery()
      } else {
        params = this.$refs.accfoundTable.getParamsQuery()
      }
      if (!params) {
        this.$message.warning('请先查询数据')
        return
      }

      var params2 = this.$global.deepcopyArray(params)
      // params2.push({ property: 'businessType', value: parseInt(this.tabActive + 1) })
      this.$downloadFile(
        '/policy/cust/insured/exportFile',
        'post',
        {
          query: params2,
        },
        this.$global.EXCEL
      )
    },
    //获取列头统计
    async getHeaderStatistics() {
      let search = this.tabActive == 0 ? this.socialSearch : this.accfoundSearch
      let params = this.tabActive == 0 ? [
        { property: 'orgId', value: search.orgId },
        { property: 'dataMonth', value: search.dataMonth },
        { property: 'businessType', value: search.businessType },
        { property: 'tplTypeCode', value: search.tplTypeCode },
      ] : [
        { property: 'orgId', value: search.orgId },
        { property: 'dataMonth', value: search.dataMonth },
        { property: 'businessType', value: search.businessType },
      ]
      if (!params) {
        this.$message.warning('请先查询数据')
        return
      }

      const { data: { code, data: result } } = await this.$http({
        url: `policy/cust/insured/detailStatistics`,
        method: 'post',
        data: { query: params }
      })
      if (code == 200) {
        console.log('获取列头统计', result)
        this.totalMember = result
      }
    },
    //跳转至费用明细
    routeToDetail(type) {
      this.$router.push({
        path: '/system/houseHoldsDetail',
        query: {
          businessType: type,
          orgId: this.$route.query.orgId,               //申报单位id
          dataMonth: this.$route.query.dataMonth,           //参保年月
        }
      })
    },
    //查询选择回调
    onSelectChange(key, event) {
      console.log(key, event);
      let search = this.tabActive == 0 ? this.socialSearch : this.accfoundSearch
      console.log('seach---------', search)
      for (const searchkey in search) {
        if (searchkey === key) {
          search[key] = event
        }
      }
      this.getHeaderStatistics()
      if (key == 'orgId') {
        this.getSocialSytemByUnit(event)
      }
      this.clickQuery()
    },
    //在户人数管理-获取社保申报系统信息
    async getSocialSytemByUnit(orgId) {
      const { data: { code, data: result } } = await this.$http({
        url: `policy/cust/insured/getSocialSystem/${orgId}`,
        method: 'post'
      })
      if (code == 200) {
        console.log('获取社保申报系统信息', result)
        this.systemArr = result
      }
    },
    removeSpaces() {
      let search = this.tabActive == 0 ? this.socialSearch : this.accfoundSearch
      search.searchText = search.searchText.replace(/\s+/g, "")
    }
  },
}
</script>
<style lang='less' scoped>
.align-center {
  align-items: center;
}
</style>
