<template>
  <div class="content spl-container">
    <!-- 导航 -->
    <breadcrumb :data="pathData"></breadcrumb>
    <div style="padding: 20px 20px 0 20px;">
      <div>
        <div class="search-row clearfix" v-if="this.bussinssType == '1'">
          <el-row>
            <el-col :span="8" class="display-flex">
              <span class="label" style="white-space:nowrap">参保城市：</span>
              <addrSelector
                v-model="socialSearch.addressName"
                :addrArr="addrArr"
                width="100%"
                class="search-row-item"
                @changeAddrSelect="changeAddrSelect($event,'social')"
              ></addrSelector>
            </el-col>
            <el-col :span="8" class="display-flex" style="align-items: center;">
              <span class="label" style="white-space:nowrap;text-align:right;width:84px;">证件号码：</span>
              <el-input v-model.trim="socialSearch.idCard" placeholder="请输入内容" style="width:212px"></el-input>
            </el-col>
            <el-col :span="8" class="display-flex" style="justify-content: flex-end;">
              <span class="label" style="white-space:nowrap">费款所属期：</span>
              <div class="date-editor-div search-row-item" style="display:flex;align-items:center;">
                <!-- <el-date-picker
                  v-model="socialSearch.moneyTime"
                  type="monthrange"
                  value-format="yyyy-MM"
                  range-separator="至"
                  style="width:100%;"
                  unlink-panels
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  @change="changeMoneyTime($event,'social')"
                ></el-date-picker>-->
                <el-date-picker
                  v-model="socialSearch.moneyStartTime"
                  type="month"
                  value-format="yyyy-MM"
                  placeholder="开始月份"
                  style="width:100%"
                  :picker-options="getPickerOption2('start','socialSearch')"
                ></el-date-picker>
                <span style="margin:0 5px;">至</span>
                <el-date-picker
                  v-model="socialSearch.moneyEndTime"
                  type="month"
                  value-format="yyyy-MM"
                  :picker-options="getPickerOption2('end','socialSearch')"
                  placeholder="结束月份"
                  style="width:100%"
                ></el-date-picker>
              </div>
            </el-col>
          </el-row>
          <el-row class="mt-20">
            <el-col :span="8" class="display-flex">
              <span class="label" style="white-space:nowrap">组织名称：</span>
              <el-select
                v-model="socialSearch.orgName"
                placeholder="请选择"
                size="mini"
                filterable
                clearable
                @change="changeOrgName($event,'social')"
              >
                <el-option
                  v-for="item in getOrgName(socialSearch.addressName,this.bussinssType)"
                  :key="item.id"
                  :label="item.orgName"
                  :value="item.orgName"
                ></el-option>
              </el-select>
            </el-col>

            <el-col :span="8" class="display-flex">
              <span class="label" style="white-space:nowrap">单位社保号：</span>
              <el-select
                v-model="socialSearch.accountNumber"
                placeholder="请选择"
                size="mini"
                filterable
                clearable
              >
                <el-option
                  v-for="item in getAccounts(socialSearch.orgName,this.bussinssType,socialSearch.addressName,'socialSearch')"
                  :key="item.id"
                  :label="item.accountNumber"
                  :value="item.accountNumber"
                ></el-option>
              </el-select>
            </el-col>
            <el-col :span="8" class="text-right">
              <el-button type="primary" class="search-btn w-60" @click="getSocialTable">查询</el-button>
            </el-col>
          </el-row>
        </div>
        <!-- 公积金 -->
        <div class="search-row clearfix" v-if="this.bussinssType == '2'">
          <el-row>
            <el-col :span="8" class="display-flex">
              <span class="label" style="white-space:nowrap">参保城市：</span>
              <addrSelector
                v-model="accfundSearch.addressName"
                :addrArr="addrArr"
                width="100%"
                class="search-row-item"
                @changeAddrSelect="changeAddrSelect($event,'accfund')"
              ></addrSelector>
            </el-col>
            <el-col :span="8" class="display-flex" style="align-items: center;">
              <span class="label" style="white-space:nowrap;text-align:right;width:98px;">证件号码：</span>
              <el-input v-model.trim="accfundSearch.idCard" placeholder="请输入内容" style="width:212px"></el-input>
            </el-col>
            <el-col :span="8" class="display-flex" style="justify-content: flex-end;">
              <span class="label" style="white-space:nowrap">费款所属期：</span>
              <div class="date-editor-div search-row-item" style="display:flex;align-items:center;">
                <!-- <el-date-picker
                  v-model="accfundSearch.moneyTime"
                  type="monthrange"
                  value-format="yyyy-MM"
                  range-separator="至"
                  style="width:100%;"
                  unlink-panels
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  @change="changeMoneyTime($event,'accfund')"
                ></el-date-picker>-->
                <el-date-picker
                  v-model="accfundSearch.moneyStartTime"
                  type="month"
                  value-format="yyyy-MM"
                  placeholder="开始月份"
                  style="width:100%"
                  :picker-options="getPickerOption2('start','accfundSearch')"
                ></el-date-picker>
                <span style="margin:0 5px;">至</span>
                <el-date-picker
                  v-model="accfundSearch.moneyEndTime"
                  type="month"
                  value-format="yyyy-MM"
                  :picker-options="getPickerOption2('end','accfundSearch')"
                  placeholder="结束月份"
                  style="width:100%"
                ></el-date-picker>
              </div>
            </el-col>
          </el-row>
          <el-row class="mt-20">
            <el-col :span="8" class="display-flex">
              <span class="label" style="white-space:nowrap">组织名称：</span>
              <el-select
                v-model="accfundSearch.orgName"
                placeholder="请选择"
                size="mini"
                filterable
                clearable
                @change="changeOrgName($event,'accfund')"
              >
                <el-option
                  v-for="item in getOrgName(accfundSearch.addressName,this.bussinssType)"
                  :key="item.id"
                  :label="item.orgName"
                  :value="item.orgName"
                ></el-option>
              </el-select>
            </el-col>
            <el-col :span="8" class="display-flex">
              <span class="label" style="white-space:nowrap">单位公积金号：</span>
              <el-select
                v-model="accfundSearch.accountNumber"
                placeholder="请选择"
                size="mini"
                filterable
                clearable
              >
                <el-option
                  v-for="item in getAccounts(accfundSearch.orgName,this.bussinssType,accfundSearch.addressName,'accfundSearch')"
                  :key="item.id"
                  :label="item.accountNumber"
                  :value="item.accountNumber"
                ></el-option>
              </el-select>
            </el-col>
            <el-col :span="8" class="text-right">
              <el-button type="primary" class="search-btn w-60" @click="getAccfundTable">查询</el-button>
            </el-col>
          </el-row>
        </div>
        <div v-if="this.bussinssType == '1'">
          <!-- 表格 -->
          <dTable
            ref="socialTable"
            :tableHeight="tableHeight"
            :paging="true"
            :showIndex="true"
            rowKey="itemId"
            :showSummary="true"
            :getSummaries="getSummaries"
            row-id="id"
            row-key="id"
          >
            <!-- <u-table-column
              prop="employeeName"
              label="姓名"
              min-width="100"
              :show-overflow-tooltip="true"
              fixed="left"
              v-if="getKey('employeeName','socialKeyList')"
            ></u-table-column>
            <u-table-column
              prop="idCard"
              label="证件号码"
              min-width="200"
              :show-overflow-tooltip="true"
              fixed="left"
              v-if="getKey('idCard','socialKeyList')"
            >
              <template slot-scope="scope">
                <span>{{plusXing(scope.row.idCard,7,4)}}</span>
              </template>
            </u-table-column>
            <u-table-column
              prop="periodOfExpense"
              label="费款所属期"
              min-width="150"
              :show-overflow-tooltip="true"
              fixed="left"
              v-if="getKey('periodOfExpense','socialKeyList')"
            ></u-table-column> -->
            <!-- <u-table-column
              prop="totalAmount"
              label="缴纳合计金额"
              min-width="150"
              :show-overflow-tooltip="true"
              v-if="getKey('totalAmount','socialKeyList')"
            ></u-table-column>
            <u-table-column
              prop="compTotalAmount"
              label="单位合计金额"
              min-width="150"
              :show-overflow-tooltip="true"
              v-if="getKey('compTotalAmount','socialKeyList')"
            ></u-table-column>
            <u-table-column
              prop="empTotalAmount"
              label="个人合计金额"
              min-width="150"
              :show-overflow-tooltip="true"
              v-if="getKey('empTotalAmount','socialKeyList')"
            ></u-table-column> -->
            <u-table-column
              :prop="item.entityFieldName"
              :label="item.fieldName"
              :min-width="['证件号码'].indexOf(item.fieldName) > -1 ? 170 : 120"
              :show-overflow-tooltip="true"
              v-for="(item,index) in tableKeyList"
              :key="item.fileName"
              :fixed="column.indexOf(item.fieldName) > -1 ? 'left':false"
              v-if="getKey(item.entityFieldName,'socialKeyList')"
            ></u-table-column>
            <!-- getKey(item.entityFieldName,'socialKeyList') -->
          </dTable>
        </div>
        <div v-if="this.bussinssType == '2'">
          <!-- 表格 -->
          <dTable
            ref="accfundTable"
            :tableHeight="tableHeight"
            :paging="true"
            :showIndex="true"
            row-id="id"
            rowKey="itemId"
            :showSummary="true"
            :getSummaries="getSummaries"
          >
            <!-- <u-table-column
              prop="employeeName"
              label="姓名"
              min-width="100"
              :show-overflow-tooltip="true"
              fixed="left"
              v-if="getKey('employeeName','accfundKeyList')"
            ></u-table-column>
            <u-table-column
              prop="idCard"
              label="证件号码"
              min-width="200"
              :show-overflow-tooltip="true"
              fixed="left"
              v-if="getKey('idCard','accfundKeyList')"
            >
              <template slot-scope="scope">
                <span>{{plusXing(scope.row.idCard,7,4)}}</span>
              </template>
            </u-table-column>
            <u-table-column
              prop="periodOfExpense"
              label="费款所属期"
              min-width="150"
              :show-overflow-tooltip="true"
              fixed="left"
              v-if="getKey('periodOfExpense','accfundKeyList')"
            ></u-table-column> -->
            <u-table-column
              :prop="item.entityFieldName"
              :label="item.fieldName"
              :min-width="['证件号码'].indexOf(item.fieldName) > -1 ? 170 : 100"
              :show-overflow-tooltip="true"
              v-for="(item,index) in tableKeyList"
              :key="item.fileName"
              :fixed="column.indexOf(item.fieldName) > -1 ? 'left':false"
              v-if="getKey(item.entityFieldName,'accfundKeyList')"
            ></u-table-column>
          </dTable>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import dTable from '../../components/common/table'
import palTab from '../../components/common/pal-tab'
import addrSelector from '../../components/common/addrSelector'
export default {
  components: {
    dTable,
    addrSelector,
    palTab,
  },
  data() {
    return {
      pathData: [{ name: '参保管理' }, { name: '实缴明细' }],
      addrArr: [],
      socialSearch: {
        idCard: '',
        addrId: '',
        addressName: '',
        businessType: '1',
        moneyStartTime: this.$moment()
          .month(this.$moment().month() - 1)
          .startOf('month')
          .format('YYYY-MM'),
        moneyEndTime: this.$moment().endOf('month').format('YYYY-MM'),
        moneyTime: [
          this.$moment()
            .month(this.$moment().month() - 1)
            .startOf('month')
            .format('YYYY-MM'),
          this.$moment().endOf('month').format('YYYY-MM'),
        ],
        orgName: '',
        accountNumber: '',
      },
      accfundSearch: {
        idCard: '',
        addrId: '',
        addressName: '',
        businessType: '1',
        moneyStartTime: this.$moment()
          .month(this.$moment().month() - 1)
          .startOf('month')
          .format('YYYY-MM'),
        moneyEndTime: this.$moment().endOf('month').format('YYYY-MM'),
        moneyTime: [
          this.$moment()
            .month(this.$moment().month() - 1)
            .startOf('month')
            .format('YYYY-MM'),
          this.$moment().endOf('month').format('YYYY-MM'),
        ],
        orgName: '',
        accountNumber: '',
      },
      statusList: [],
      label: '单位社保号',
      tabs: ['社保', '公积金'],
      bussinssType: '1',
      tabActive: 0,
      listData: [],
      socialAccountsArr: [],
      accfundAccountsArr: [],
      socialKeyList: [],
      accfundKeyList: [],
      options:[{id: 1, fieldName: "姓名", fieldCode: "10001001", entityFieldName: "employeeName", sort: 1},
        {id: 2, fieldName: "证件号码", fieldCode: "10001002", entityFieldName: "idCard", sort: 2},
        {id: 3, fieldName: "费用所属期", fieldCode: "10001003", entityFieldName: "periodOfExpense", sort: 3},
        {id: 4, fieldName: "单位合计金额", fieldCode: "10001004", entityFieldName: "compTotalAmount", sort: 4},
        {id: 5, fieldName: "个人合计金额", fieldCode: "10001005", entityFieldName: "empTotalAmount", sort: 5},
        {id: 6, fieldName: "缴纳合计金额", fieldCode: "10001006", entityFieldName: "totalAmount", sort: 6}],
      column:['姓名','证件号码','费用所属期'],
      loading:null,
      tableKeyList:[{id: 1, fieldName: "姓名", fieldCode: "10001001", entityFieldName: "employeeName", sort: 1},
        {id: 2, fieldName: "证件号码", fieldCode: "10001002", entityFieldName: "idCard", sort: 2},
        {id: 3, fieldName: "费用所属期", fieldCode: "10001003", entityFieldName: "periodOfExpense", sort: 3},
        {id: 4, fieldName: "单位合计金额", fieldCode: "10001004", entityFieldName: "compTotalAmount", sort: 4},
        {id: 5, fieldName: "个人合计金额", fieldCode: "10001005", entityFieldName: "empTotalAmount", sort: 5},
        {id: 6, fieldName: "缴纳合计金额", fieldCode: "10001006", entityFieldName: "totalAmount", sort: 6}]
    }
  },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 350 + 'px'
    },
    plusXing() {
      return function (str, frontLen, endLen) {
        if(str == null || str.length <= 0){
          return ''
        }
        var len = str.length - frontLen - endLen
        if(len <= 0){
          return str
        }
        var xing = ''
        for (var i = 0; i < len; i++) {
          xing += '*'
        }
        return (
          str.substring(0, frontLen) + xing + str.substring(str.length - endLen)
        )
      }
    },
    getOrgName() {
      return function (city, type) {
        var arr = []
        this.listData.forEach((item) => {
          item.accounts.forEach((item1) => {
            if (item.addressName == city) {
              if (item1.bussinssType == type) {
                arr.push({
                  addressName: item.addressName,
                  ...item1,
                  orgName: item.orgName,
                })
              }
            } else {
              if (item1.bussinssType == type && !city) {
                arr.push({
                  addressName: item.addressName,
                  ...item1,
                  orgName: item.orgName,
                })
              }
            }
          })
        })
        var arr2 = []
        var obj = {}
        arr.forEach((item, index) => {
          if (obj[item.orgName] != undefined) {
            if (arr2[obj[item.orgName]][item.addressName]) {
              arr2[obj[item.orgName]][item.addressName].push(item)
            } else {
              arr2[obj[item.orgName]][item.addressName] = []
              arr2[obj[item.orgName]][item.addressName].push(item)
            }
            arr2[arr2.length - 1].allAccount.push(item)
          } else {
            obj[item.orgName] = arr2.length
            var arr3 = []
            arr3.push(item)
            arr2.push({
              orgName: item.orgName,
            })
            arr2[arr2.length - 1][item.addressName] = arr3
            arr2[arr2.length - 1].allAccount = []
            arr2[arr2.length - 1].allAccount.push(item)
          }
        })
        return arr2
      }
    },
    getAccounts() {
      return function (orgName, type, city, key) {
        var arr = []
        if (!orgName) {
          this.getOrgName(this[key].addressName, type).forEach((item) => {
            arr.push(...item.allAccount)
          })
          return arr
        } else {
          this.getOrgName(this[key].addressName, type).forEach((item) => {
            if (item.orgName == orgName && city == item.addressName) {
              arr.push(...item[item.addressName])
            } else if (item.orgName == orgName) {
              arr.push(...item.allAccount)
            }
          })
        }
        return arr
      }
    },
    getKey() {
      return function (key,origin) {
        if (this[origin].length <= 0) {
          return true
        }
        return this[origin].indexOf(key) > -1
      }
    },
    getPickerOption2() {
      return function (time, key) {
        var obj = {}
        var self = this
        if (time == 'start') {
          return {
            disabledDate(time) {
              if (!self[key].moneyEndTime) {
                return false
              }
              let curDate = new Date(self[key].moneyEndTime + '-01').getTime()
              let three = (365 / 2) * 24 * 3600 * 1000
              let threeMonths = curDate - three
              return time.getTime() > curDate
            },
          }
        } else {
          return {
            disabledDate(time) {
              if (!self[key].moneyStartTime) {
                return false
              }
              let curDate = new Date(self[key].moneyStartTime + '-01').getTime()
              let three = (365 / 2) * 24 * 3600 * 1000
              let threeMonths = curDate + three
              return time.getTime() <= curDate - 86400000
            },
          }
        }
      }
    },
  },
  created() {
    this.getAddrArr()
    this.getList()

  },
  mounted() {
    if (this.$route.query.type) {
      this.bussinssType = this.$route.query.type
      if (this.bussinssType == '1') {
        this.tabActive = 0
        // this.getSocialTable()
        this.pathData[1] = { name: '员工社保费用明细' }
        this.$set(this.pathData, '1', { name: '员工社保费用明细' })
      } else {
        this.tabActive = 1
        // this.getAccfundTable()
        this.pathData[1] = { name: '员工公积金费用明细' }
        this.$set(this.pathData, '1', { name: '员工公积金费用明细' })
      }
    }
    this.getLocationCode().then((data)=>{
      this.requestInfo(data)
    })
  },
  methods: {
    changeOrgName(val, key) {
      if (key == 'social') {
        this.socialSearch.accountNumber = ''
      } else {
        this.accfundSearch.accountNumber = ''
      }
    },
    changeAddrSelect(val, key) {
      if (key == 'social') {
        this.socialSearch.accountNumber = ''
        this.socialSearch.orgName = ''
      } else {
        this.accfundSearch.accountNumber = ''
        this.accfundSearch.orgName = ''
      }
      clearTimeout(this.timer)
      this.timer = setTimeout(()=>{
        if(val.id == ''){
          this.socialSearch.addrId = ''
          this.accfundSearch.addrId = ''
          return
        }
        if (key == 'social') {
          this.socialSearch.addrId = val.id
        } else {
          this.accfundSearch.addrId = val.id
        }
      },100)
    },
    changeMoneyTime(val, key) {
      if (key == 'social') {
        if (val == null) {
          this.socialSearch.moneyStartTime = ''
          this.socialSearch.moneyEndTime = ''
          return
        }
        this.socialSearch.moneyStartTime = val[0]
        this.socialSearch.moneyEndTime = val[1]
      } else {
        if (val == null) {
          this.accfundSearch.moneyStartTime = ''
          this.accfundSearch.moneyEndTime = ''
          return
        }
        this.accfundSearch.moneyStartTime = val[0]
        this.accfundSearch.moneyEndTime = val[1]
      }
    },
    // 获取参保地
    getAddrArr() {
      this.$http({
        url: 'policy/declareAddr/addrList',
        method: 'post',
      })
        .then(({ data }) => {
          this.addrArr = data.data
        })
        .catch((data) => {})
    },
    // 改变社保-参保地
    getAddrId(item) {
      if (this.formData.addrId == item.id) {
        return false
      }
      this.formData.addrId = item.id
      this.getCompanyList(1)
    },
    getTableList() {},
    goDetail() {
      this.$router.push('/paidIn/index?type=' + this.formData.bussinssType)
    },
    changeBussinssType(val) {
      if (val == 1) {
        this.label = '单位社保号'
      } else {
        this.label = '单位公积金号'
      }
    },
    //S=社保列表
    async getSocialTable() {
      await this.requestInfo()
      var params = [
        { property: 'addressName', value: this.socialSearch.addressName },
        { property: 'orgName', value: this.socialSearch.orgName },
        { property: 'idCard', value: this.socialSearch.idCard },
        { property: 'accountNumber', value: this.socialSearch.accountNumber },
        { property: 'moneyStartTime', value: this.socialSearch.moneyStartTime },
        { property: 'moneyEndTime', value: this.socialSearch.moneyEndTime },
      ]
      this.$refs.socialTable.fetch({
        query: params,
        method: 'post',
        url: '/policy/paidIn/querySocialPaidIn',
        callback: (res) => {
          this.socialKeyList = []
          if (res.rows.length > 0) {
            this.socialKeyList = res.rows[0].entityNames ? res.rows[0].entityNames : []
          }
        },
      })
    },
    //S=公积金列表
    async getAccfundTable() {
      await this.requestInfo()
      var params = [
        { property: 'addressName', value: this.accfundSearch.addressName },
        { property: 'orgName', value: this.accfundSearch.orgName },
        { property: 'accountNumber', value: this.accfundSearch.accountNumber },
        { property: 'idCard', value: this.accfundSearch.idCard },
        {
          property: 'moneyStartTime',
          value: this.accfundSearch.moneyStartTime,
        },
        { property: 'moneyEndTime', value: this.accfundSearch.moneyEndTime },
      ]
      this.$refs.accfundTable.fetch({
        query: params,
        method: 'post',
        url: '/agent/paidIn/queryFundPaidIn',
        callback:(res)=>{
          this.accfundKeyList = []
          if (res.rows.length > 0) {
            this.accfundKeyList = res.rows[0].entityNames ? res.rows[0].entityNames : []
          }
        }
      })
    },
    // 改变tab
    changeTab(index) {
      this.bussinssType = index + 1
      if (this.bussinssType == '1') {
        this.getSocialTable()
      } else {
        this.getAccfundTable()
      }
    },
    // 获取组织名称鹤单位社保/公积金号列表数据
    getList() {
      let that = this
      this.$http({
        url: '/policy/declareAccount/list',
        method: 'post',
      })
        .then(({ data }) => {
          // let res = data.data
          // let socialAccountsArr = [],
          //   accfundAccountsArr = []
          // res.map((item) => {
          //   socialAccountsArr = []
          //   accfundAccountsArr = []
          //   item.socialAccounts.map((item1) => {
          //     socialAccountsArr.push({
          //       id: item1.id,
          //       accountNumber: item1.accountNumber,
          //     })
          //   })
          //   item.accfundAccounts.map((item2) => {
          //     accfundAccountsArr.push({
          //       id: item2.id,
          //       accountNumber: item2.accountNumber,
          //     })
          //   })
          //   item.socialAccountsStr = socialAccountsArr.join('；')
          //   item.accfundAccountsStr = accfundAccountsArr.join('；')
          //   this.socialAccountsArr.push(...socialAccountsArr)
          //   this.accfundAccountsArr.push(...accfundAccountsArr)
          // })
          that.listData = data.data
        })
        .catch((data) => {})
    },
    getSummaries(param) {
      var sums = [['', '合计']]
      const { columns, data } = param
      if (!data || data.length <= 0) {
        return sums
      }
      var reg = /Amount$/
      columns.forEach((column, index) => {
        if (index === 1) {
          sums[0][index] = '合计'
          return
        }
        if (reg.test(column.property) && data[0][column.property + 'Total']) {
          sums[0][index] = data[0][column.property + 'Total']
            ? data[0][column.property + 'Total']
            : ''
        } else {
          sums[0][index] = ''
        }
      })
      return sums
    },
    getLocationCode(){
      return new Promise((resolve,reject)=>{
        this.$http({
          url: '/policy/paidIn/listPaidFieldDict',
          method: 'post',
        }).then((res) => {
          if(res.data.code == 200){
            this.options = res.data.data
            this.$nextTick(()=>{
              this.$refs.socialTable.doLayout()
            })
            resolve(res)
          }else{
            this.$message.error('获取本地实缴字典出错')
            reject(res)
          }
          // console.log(this.valueTypeOption2Item)
        }).catch(err=>{
          this.$message.error('获取本地实缴字典出错')
        })
      })
    },
    requestInfo() {
      this.showLoading()
      // console.log(this.$options.data())
      this.tableKeyList = [{id: 1, fieldName: "姓名", fieldCode: "10001001", entityFieldName: "employeeName", sort: 1},
        {id: 2, fieldName: "证件号码", fieldCode: "10001002", entityFieldName: "idCard", sort: 2},
        {id: 3, fieldName: "费用所属期", fieldCode: "10001003", entityFieldName: "periodOfExpense", sort: 3},
        {id: 4, fieldName: "单位合计金额", fieldCode: "10001004", entityFieldName: "compTotalAmount", sort: 4},
        {id: 5, fieldName: "个人合计金额", fieldCode: "10001005", entityFieldName: "empTotalAmount", sort: 5},
        {id: 6, fieldName: "缴纳合计金额", fieldCode: "10001006", entityFieldName: "totalAmount", sort: 6}]
      return this.$http({
        url: '/policy/paidIn/queryPaidInField',
        method: 'post',
        params:{
          address:this.bussinssType == '1' ? this.socialSearch.addrId : this.accfundSearch.addrId,
          businessType:this.bussinssType
        }
      })
        .then(async ({ data }) => {
          console.log(data.data)
          this.closeLoading()
          if(data && data.code == 200){
              if(data.data.length > 0){
                this.tableKeyList.push(...this.filterKey(this.options,data.data))
              }else{

              }
          }else{

          }
        })
        .catch(() => {
          this.closeLoading()
        })
    },
    showLoading(target) {
      this.loading = this.$loading({
        target: target ? target : document.body,
        lock: true,
        text: '加载中',
        spinner: 'el-icon-loading',
        background: 'rgba(255, 255, 255, 0.7)',
      })
    },
    closeLoading() {
      if (this.loading && this.loading.close) {
        this.loading.close()
      }
    },
    filterKey(data1,data2){
      var arr = []
      var arr2 = ['姓名','证件号码','费用所属期','单位合计金额','个人合计金额','缴纳合计金额']
      data1.forEach(item=>{
        data2.forEach(item2=>{
          if(item.fieldCode == item2.localPaidInField && arr2.indexOf(item.fieldName) <= -1){
            arr.push(item)
          }
        })
      })
      return arr
    }
  },
}
</script>
<style lang="less" scoped>
/deep/.el-date-editor .el-range-separator {
  line-height: 23px;
}
/deep/.pal-table .el-table__fixed {
  height: 100% !important;
}
/deep/.el-table__body-wrapper {
  z-index: 2;
}
/deep/.el-table__fixed-footer-wrapper {
  bottom: -5px;
}
</style>
