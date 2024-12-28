<template>
  <div class="content spl-container">
    <!-- 导航 -->
    <breadcrumb :data="pathData">
      <!--<el-button
        type="plain"
        slot="breadcrumb-btn"
        style="color:#3E82FF;border-color:#3E82FF;"
        size="mini"
        @click="goDetail"
      >费用明细</el-button>-->
    </breadcrumb>
    <div style="padding: 20px 20px 0 20px;">
      <palTab :tabs="tabs" v-model="tabActive" :type="2" @change="changeTab"></palTab>
      <!--社保-->
      <div v-show="tabActive == '0'">
        <div class="search-row clearfix">
          <el-row>
            <!-- <el-col :span="8" class="display-flex" style="align-items: center;">
              <span class="label">业务类型：</span>
              <el-radio-group v-model="formData.showType">
                <el-radio label="1">台账</el-radio>
                <el-radio label="2">明细</el-radio>
              </el-radio-group>
            </el-col>-->
            <!-- <el-col :span="8" class="display-flex">
              <span class="label" style="white-space:nowrap">参保城市：</span>
              <addrSelector
                v-model="formData.addressName"
                :addrArr="addrArr"
                @changeAddrSelect="getAddrId"
                width="100%"
                class="search-row-item"
              ></addrSelector>
            </el-col>-->
            <el-col :span="7" class="display-flex">
              <!-- <span class="label required-pre" style="white-space:nowrap">申报账户：</span> -->
              <span class="label required-pre" style="white-space:nowrap">申报单位：</span>
              <el-select v-model="formData.orgId" class="w-300" placeholder="请选择申报单位" clearable filterable disabled>
                <el-option v-for="it in customMadeList" :key="it.id" :label="it.orgName" :value="it.id"></el-option>
              </el-select>
              <!-- <el-select
                ref = "accountNumberSocial"
                v-model="formData.selectItem"
                placeholder="请选择"
                size="mini"
                filterable
                clearable
                class="w-250"
                value-key="itemId"
                @change="changeSelect($event,'social')"
              >
                <el-option
                  v-for="(item,index) in listData"
                  :key="index"
                  :label="item.name"
                  :value="item"
                ></el-option>
              </el-select> -->
            </el-col>
            <el-col :span="9" class="display-flex">
              <span class="label required-pre" style="white-space:nowrap">费款所属期：</span>
              <div class="date-editor-div search-row-item" style="display:flex;align-items:center;">
                <el-date-picker v-model="formData.moneyStartTime" type="month" value-format="yyyy-MM" placeholder="开始月份"
                  style="width:150px" :picker-options="getPickerOption2('start', 'formData')"></el-date-picker>
                <span style="margin:0 5px;">至</span>
                <el-date-picker v-model="formData.moneyEndTime" type="month" value-format="yyyy-MM"
                  :picker-options="getPickerOption2('end', 'formData')" placeholder="结束月份"
                  style="width:150px"></el-date-picker>
              </div>
            </el-col>
            <el-col :span="5" class="display-flex">
              <span class="label" style="white-space:nowrap">关键字：</span>
              <el-input v-show="formData.showType == '2'" style="width: 285px;" v-model.trim="formData.keyword" clearable
                placeholder="请输入姓名/证件号关键字" @keyup.enter.native="querySocial"></el-input>
            </el-col>
            <!--<el-col :span="8" class="display-flex" v-show="formData.businessType == '2'">
              <span class="label" style="white-space:nowrap">{{label}}：</span>
              <el-select
                v-model="formData.accountNumber"
                placeholder="请选择"
                size="mini"
                filterable
                clearable
              >
                <el-option
                  v-for="item in getAccounts(formData.orgName,formData.businessType,formData.addressName)"
                  :key="item.id"
                  :label="item.accountNumber"
                  :value="item.accountNumber"
                ></el-option>
              </el-select>
            </el-col>-->
            <el-col :span="3" class="text-right">
              <el-button type="primary" class="search-btn w-60" @click="querySocial">查询</el-button>
            </el-col>
          </el-row>
        </div>
        <!-- 台账 -->
        <div v-show="formData.showTypeTemp === '1'">
          <dTable ref="tableSocial" @fetch="getTableSocial" :tableHeight="tableHeight" :paging="true" :showIndex="true"
            rowKey="itemId">
            <u-table-column prop="addressName" label="参保城市" min-width="100"
              :show-overflow-tooltip="true"></u-table-column>
            <u-table-column prop="businessType" label="业务类型" min-width="100" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <span>{{ getBusinessType(scope.row.businessType) }}</span>
              </template>
            </u-table-column>
            <u-table-column prop="declareAccount" label="申报账户" min-width="150"
              :show-overflow-tooltip="true"></u-table-column>
            <u-table-column prop="periodOfExpense" label="费款所属期" min-width="100"
              :show-overflow-tooltip="true"></u-table-column>
            <u-table-column prop="totalAmount" label="费用金额" min-width="100" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <span>{{ formatNumber(scope.row.totalAmount) }}</span>
              </template>
            </u-table-column>
            <!--<u-table-column
              prop="periodTime"
              label="缴款时间"
              min-width="100"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <span>{{scope.row.periodTime ? $moment(scope.row.periodTime).format('YYYY-MM-DD') : ''}}</span>
              </template>
            </u-table-column>-->
            <u-table-column prop="fileName" label="网站数据源文件" :show-overflow-tooltip="true" min-width="150">
              <template slot-scope="scope">
                <span @click="downLoadFile(scope.row)" class="downLoadFile">{{ scope.row.fileName }}</span>
              </template>
            </u-table-column>
          </dTable>
        </div>
        <!-- 明细 -->
        <div v-show="formData.showTypeTemp === '2'">
          <dTable ref="tableSocialDetail" @fetch="getTableSocialDetail" :tableHeight="tableHeight" :paging="true"
            :showIndex="true" rowKey="itemId" :showSummary="false" :getSummaries="getSummaries" row-id="id" row-key="id">
            <u-table-column :prop="item.entityFieldName" :label="item.fieldName"
              :min-width="['证件号码'].indexOf(item.fieldName) > -1 ? 170 : 120" :show-overflow-tooltip="true"
              v-for="(item) in formData.tableKeyList" :key="item.fileName"
              :fixed="column.indexOf(item.fieldName) > -1 ? 'left' : false">
              <template scope="scope">
                <span>{{ scope.row[item.fieldName] }}</span>
              </template>
            </u-table-column>
            <template slot="pagination-btns">
              <div class="display-flex">
                <el-button size="small" icon="icon ic-export-blue" @click="exportSocialTableData">导出</el-button>
                <el-button size="small" icon="icon ic-export-blue" @click="downloadSocial">下载源文件</el-button>
              </div>
            </template>
          </dTable>
        </div>
      </div>

      <!--公积金-->
      <div v-show="tabActive == '1'">
        <div class="search-row clearfix">
          <el-row>
            <!-- <el-col :span="8" class="display-flex" style="align-items: center;">
              <span class="label">业务类型：</span>
              <el-radio-group v-model="formData2.showType">
                <el-radio label="1">台账</el-radio>
                <el-radio label="2">明细</el-radio>
              </el-radio-group>
            </el-col>-->
            <!-- <el-col :span="8" class="display-flex">
              <span class="label" style="white-space:nowrap">参保城市：</span>
              <addrSelector
                v-model="formData2.addressName"
                :addrArr="addrArr"
                @changeAddrSelect="getAddrId"
                width="100%"
                class="search-row-item"
              ></addrSelector>
            </el-col>-->
            <el-col :span="7" class="display-flex">
              <!-- <span class="label required-pre" style="white-space:nowrap">申报账户：</span>
              <el-select ref="accountNumberAccfund" v-model="formData2.selectItem" placeholder="请选择" size="mini"
                filterable clearable class="w-250" @change="changeSelect($event, 'accfund')" value-key="itemId">
                <el-option v-for="(item, index) in listData" :key="index" :label="item.name" :value="item"></el-option>
              </el-select> -->
              <span class="label required-pre" style="white-space:nowrap">申报单位：</span>
              <el-select v-model="formData2.orgId" class="w-300" placeholder="请选择申报单位" clearable filterable disabled>
                <el-option v-for="it in customMadeList" :key="it.id" :label="it.orgName" :value="it.id"></el-option>
              </el-select>
            </el-col>
            <el-col :span="9" class="display-flex">
              <span class="label required-pre" style="white-space:nowrap">费款所属期：</span>
              <div class="date-editor-div search-row-item" style="display:flex;align-items:center;">
                <el-date-picker v-model="formData2.moneyStartTime" type="month" value-format="yyyy-MM" placeholder="开始月份"
                  style="width: 150px" :picker-options="getPickerOption2('start', 'formData2')"></el-date-picker>
                <span style="margin:0 5px;">至</span>
                <el-date-picker v-model="formData2.moneyEndTime" type="month" value-format="yyyy-MM"
                  :picker-options="getPickerOption2('end', 'formData2')" placeholder="结束月份"
                  style="width: 150px"></el-date-picker>
              </div>
            </el-col>
            <el-col :span="5" class="display-flex">
              <span class="label" style="white-space:nowrap">关键字：</span>
              <el-input v-show="formData2.showType == '2'" style="width: 285px;" v-model.trim="formData2.keyword" clearable
                placeholder="请输入姓名/证件号关键字" @keyup.enter.native="queryAccfund"></el-input>
            </el-col>
            <el-col :span="3" class="text-right">
              <el-button type="primary" class="search-btn w-60" @click="queryAccfund">查询</el-button>
            </el-col>
          </el-row>
        </div>
        <!-- 台账 -->
        <div v-show="formData2.showTypeTemp === '1'">
          <dTable ref="tableAccfund" @fetch="getTableSocial" :tableHeight="tableHeight" :paging="true" :showIndex="true"
            rowKey="itemId">
            <u-table-column prop="addressName" label="参保城市" min-width="100"
              :show-overflow-tooltip="true"></u-table-column>
            <u-table-column prop="businessType" label="业务类型" min-width="100" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <span>{{ getBusinessType(scope.row.businessType) }}</span>
              </template>
            </u-table-column>
            <u-table-column prop="declareAccount" label="申报账户" min-width="150"
              :show-overflow-tooltip="true"></u-table-column>
            <u-table-column prop="periodOfExpense" label="费款所属期" min-width="100"
              :show-overflow-tooltip="true"></u-table-column>
            <u-table-column prop="totalAmount" label="费用金额" min-width="100" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <span>{{ formatNumber(scope.row.totalAmount) }}</span>
              </template>
            </u-table-column>
            <!--<u-table-column
              prop="periodTime"
              label="缴款时间"
              min-width="100"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <span>{{scope.row.periodTime ? $moment(scope.row.periodTime).format('YYYY-MM-DD') : ''}}</span>
              </template>
            </u-table-column>-->
            <u-table-column prop="fileName" label="网站数据源文件" :show-overflow-tooltip="true" min-width="150">
              <template slot-scope="scope">
                <span @click="downLoadFile(scope.row)" class="downLoadFile">{{ scope.row.fileName }}</span>
              </template>
            </u-table-column>
          </dTable>
        </div>
        <!-- 明细 -->
        <div v-show="formData2.showTypeTemp === '2'">
          <dTable ref="tableAccfundDetail" @fetch="getTableAccfundDetail" :tableHeight="tableHeight" :paging="true"
            :showIndex="true" rowKey="itemId" :showSummary="false" :getSummaries="getSummaries" row-id="id" row-key="id">
            <u-table-column :prop="item.entityFieldName" :label="item.fieldName"
              :min-width="['证件号码'].indexOf(item.fieldName) > -1 ? 170 : 100" :show-overflow-tooltip="true"
              v-for="(item) in formData2.tableKeyList" :key="item.fileName"
              :fixed="column.indexOf(item.fieldName) > -1 ? 'left' : false">
              <template scope="scope">
                <span>{{ scope.row[item.fieldName] }}</span>
              </template>
            </u-table-column>
            <template slot="pagination-btns">
              <div class="display-flex">
                <el-button size="small" icon="icon ic-export-blue" @click="exportAccfundTableData">导出</el-button>
                <el-button size="small" icon="icon ic-export-blue" @click="downloadAccfund">下载源文件</el-button>
              </div>
            </template>
          </dTable>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import dTable from '../../components/common/table'
import addrSelector from '../../components/common/addrSelector'
import palTab from '../../components/common/pal-tab'
export default {
  components: {
    dTable,
    addrSelector,
    palTab,
  },
  data() {
    return {
      customMadeList: [],
      pathData: [],
      tabs: ['社保', '公积金'],
      tabActive: 0,
      addrArr: [],
      formData: {
        file: [],
        rows: [],
        showType: '2',
        showTypeTemp: '2',
        addrId: '',
        addressName: '',
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
        // orgName: '',
        accountNumber: '',
        selectItem: null,
        keyword: '',
        tableKeyList: [
          {
            id: 1,
            fieldName: '姓名',
            fieldCode: '10001001',
            entityFieldName: 'employeeName',
            sort: 1,
          },
          {
            id: 2,
            fieldName: '证件号码',
            fieldCode: '10001002',
            entityFieldName: 'idCard',
            sort: 2,
          },
          {
            id: 3,
            fieldName: '费款所属期',
            fieldCode: '10001003',
            entityFieldName: 'periodOfExpense',
            sort: 3,
          },
          {
            id: 4,
            fieldName: '单位合计金额',
            fieldCode: '10001004',
            entityFieldName: 'compTotalAmount',
            sort: 4,
          },
          {
            id: 5,
            fieldName: '个人合计金额',
            fieldCode: '10001005',
            entityFieldName: 'empTotalAmount',
            sort: 5,
          },
          {
            id: 6,
            fieldName: '缴纳合计金额',
            fieldCode: '10001006',
            entityFieldName: 'totalAmount',
            sort: 6,
          },
        ],
      },
      formData2: {
        file: [],
        rows: [],
        showType: '2',
        showTypeTemp: '2',
        addrId: '',
        addressName: '',
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
        // orgName: '',
        accountNumber: '',
        selectItem: null,
        keyword: '',
        tableKeyList: [
          {
            id: 1,
            fieldName: '姓名',
            fieldCode: '10001001',
            entityFieldName: 'employeeName',
            sort: 1,
          },
          {
            id: 2,
            fieldName: '证件号码',
            fieldCode: '10001002',
            entityFieldName: 'idCard',
            sort: 2,
          },
          {
            id: 3,
            fieldName: '费款所属期',
            fieldCode: '10001003',
            entityFieldName: 'periodOfExpense',
            sort: 3,
          },
          {
            id: 4,
            fieldName: '单位合计金额',
            fieldCode: '10001004',
            entityFieldName: 'compTotalAmount',
            sort: 4,
          },
          {
            id: 5,
            fieldName: '个人合计金额',
            fieldCode: '10001005',
            entityFieldName: 'empTotalAmount',
            sort: 5,
          },
          {
            id: 6,
            fieldName: '缴纳合计金额',
            fieldCode: '10001006',
            entityFieldName: 'totalAmount',
            sort: 6,
          },
        ],
      },
      statusList: [],
      listData: [],
      socialAccountsArr: [],
      accfundAccountsArr: [],

      socialKeyList: [],
      accfundKeyList: [],
      column: ['姓名', '证件号码', '费款所属期'],
      loading: null,
      options: [
        {
          id: 1,
          fieldName: '姓名',
          fieldCode: '10001001',
          entityFieldName: 'employeeName',
          sort: 1,
        },
        {
          id: 2,
          fieldName: '证件号码',
          fieldCode: '10001002',
          entityFieldName: 'idCard',
          sort: 2,
        },
        {
          id: 3,
          fieldName: '费款所属期',
          fieldCode: '10001003',
          entityFieldName: 'periodOfExpense',
          sort: 3,
        },
        {
          id: 4,
          fieldName: '单位合计金额',
          fieldCode: '10001004',
          entityFieldName: 'compTotalAmount',
          sort: 4,
        },
        {
          id: 5,
          fieldName: '个人合计金额',
          fieldCode: '10001005',
          entityFieldName: 'empTotalAmount',
          sort: 5,
        },
        {
          id: 6,
          fieldName: '缴纳合计金额',
          fieldCode: '10001006',
          entityFieldName: 'totalAmount',
          sort: 6,
        },
      ],
    }
  },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 330 + 'px'
    },
    getBusinessType() {
      return function (val) {
        if (val) {
          if (val == '1') {
            return '社保'
          } else if (val == '2') {
            return '公积金'
          }
        } else {
          return ''
        }
      }
    },
    //千分号
    formatNumber(num) {
      return function (num) {
        if (!num) {
          return '0.00'
        }
        num = Number(num).toFixed(2)
        return ('' + num).replace(/(\d{1,3})(?=(\d{3})+(?:$|\.))/g, '$1,')
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
    getAccountsSocial() {
      return function () {
        var arr = []
        this.getOrgName(this.formData.addressName, '1').forEach((item) => {
          arr.push(...item.allAccount)
        })
        return arr
      }
    },
    getAccountsAccfund() {
      return function () {
        var arr = []
        this.getOrgName(this.formData2.addressName, '2').forEach((item) => {
          arr.push(...item.allAccount)
        })
        return arr
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
    getKey() {
      return function (key, origin) {
        if (this[origin].length <= 0) {
          return true
        }
        return this[origin].indexOf(key) > -1
      }
    },
  },
  created() {
    this.getAddrArr()
    this.getList()
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = this.$global.deepcopyArray(
        tabs[this.$route.meta.parentPath]
      )
    }
    this.pathData.push({ name: '在户名单',path:`/system/houseHoldsList?orgId=${this.$route.query.orgId}&dataMonth=${this.$route.query.dataMonth}` }, { name: '费用明细' })

    this.tabActive = parseInt(this.$route.query.businessType) - 1
    // if (this.tabActive == 0) {
    //   this.formData.orgId = parseInt(this.$route.query.orgId)
    //   this.formData.moneyEndTime = this.$route.query.dataMonth
    // } else {
    //   this.formData2.orgId = parseInt(this.$route.query.orgId)
    //   this.formData2.moneyEndTime = this.$route.query.dataMonth
    // }

    this.formData.orgId = parseInt(this.$route.query.orgId)
    this.formData.moneyEndTime = this.$route.query.dataMonth
    this.formData.moneyStartTime = this.$moment(this.$route.query.dataMonth, 'YYYY-MM').clone().subtract(1, 'month').format('YYYY-MM')
    this.formData2.orgId = parseInt(this.$route.query.orgId)
    this.formData2.moneyEndTime = this.$route.query.dataMonth
    this.formData2.moneyStartTime = this.$moment(this.$route.query.dataMonth, 'YYYY-MM').clone().subtract(1, 'month').format('YYYY-MM')

    this.getOrgList()


    // this.getLocationCode().then((data) => {
    // this.requestInfo('formData')
    // this.requestInfo('formData2')
    // })
  },
  mounted() {
    // this.getTableList()


  },
  methods: {
    //获取申报单位
    getOrgList() {
      this.$http({
        url: 'policy/customMade/offerSettings/getOrgList',
        method: 'post',
        params: {
          addrId: '',
          custFilter: 1,
          businessType: this.$route.query.businessType,
          custId:''
        },
      })
        .then((res) => {
          res.data.data = res.data.data[0] == null ? res.data.data.slice(1) : res.data.data
          this.customMadeList = res.data.data
          if (this.$route.query.businessType == 1) {
            this.querySocial()
          } else {
            this.queryAccfund()
          }
        })
        .catch((item) => { })
    },
    changeSelect(val, type) {
      console.log('changeSelect--------------------------', val, type)
      if (type == 'social') {
        this.formData.accountNumber = val ? val.accountNumber : ''
        if (!val) {
          this.formData.selectItem = null
        }
      } else if (type == 'accfund') {
        this.formData2.accountNumber = val ? val.accountNumber : ''
        if (!val) {
          this.formData2.selectItem = null
        }
      }
    },
    getSteamFileInfo(response) {
      let name = response.headers['content-disposition']
      name = name.split(';')
      name = name.find((e) => ~e.indexOf('filename'))
      if (name) {
        name = decodeURIComponent(name.split('=')[1])
      }
      return name || ''
    },
    exportSocialTableData() {
      if (this.formData.rows.length <= 0) {
        this.$message.warning('无数据可导出，请先查询数据')
        return
      }
      let params = this.$refs.tableSocialDetail.getParamsQuery()
      this.$downloadFile(
        '/agent/paidIn/downloadSocialPaidIn',
        'post',
        {
          query: params,
        },
        this.$global.EXCEL
      )
    },
    exportAccfundTableData() {
      if (this.formData2.rows.length <= 0) {
        this.$message.warning('无数据可导出，请先查询数据')
        return
      }
      let params = this.$refs.tableAccfundDetail.getParamsQuery()
      this.$downloadFile(
        '/agent/paidIn/downloadFundPaidIn',
        'post',
        {
          query: params,
        },
        this.$global.EXCEL
      )
    },
    downloadSocial() {
      if (this.formData.file.length <= 0) {
        this.$message.warning('无源文件可供下载')
        return
      }
      var id = this.formData.file.map(item => {
        return item.id
      }).join(',')
      if (this.formData.file.length > 1) {
        this.$confirm(`请确定是否下载${this.formData.file.length}份源文件`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          showClose: false,
          customClass: 'pal-confirm',
          type: 'warning',
        }).then((item) => {
          this.downloadFileZIP(id)
        })
      } else {
        this.downloadFileZIP(id)
      }
    },
    downloadAccfund() {
      if (this.formData2.file.length <= 0) {
        this.$message.warning('无源文件可供下载')
        return
      }
      var id = this.formData2.file.map(item => {
        return item.id
      }).join(',')
      if (this.formData2.file.length > 1) {
        this.$confirm(`请确定是否下载${this.formData2.file.length}份源文件`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          showClose: false,
          customClass: 'pal-confirm',
          type: 'warning',
        }).then((item) => {
          this.downloadFileZIP(id)
        })
      } else {
        this.downloadFileZIP(id)
      }
    },
    downloadFileZIP(id) {
      this.$http({
        method: 'get',
        url: `/policy/sys/file/batchDownload/${id}`,
        responseType: 'blob',
      }).then((res) => {
        if (res.status === 200) {
          const blob = new Blob([res.data], { type: 'application/zip' })
          const a = document.createElement('a')
          const URL = window.URL || window.webkitURL
          const herf = URL.createObjectURL(blob)
          a.href = herf
          a.download = this.getSteamFileInfo(res) || '费用台账源文件'
          document.body.appendChild(a)
          a.click()
          document.body.removeChild(a)
          window.URL.revokeObjectURL(herf)
        }
      })
    },
    /* changeOrgName() {
      this.formData.accountNumber = ''
    },*/
    downLoadFile(row) {
      this.$downloadFile(
        'policy/sys/file/download/' + row.fileId,
        'get',
        {},
        this.$global.EXCEL
      )
    },
    /* changeMoneyTime(val) {
      if (val == null) {
        this.formData.moneyStartTime = ''
        this.formData.moneyEndTime = ''
        return
      }
      this.formData.moneyStartTime = val[0]
      this.formData.moneyEndTime = val[1]
    },*/
    // 获取参保地
    getAddrArr() {
      this.$http({
        url: 'policy/declareAddr/addrList',
        method: 'post',
      })
        .then(({ data }) => {
          this.addrArr = data.data
        })
        .catch((data) => { })
    },
    // 改变社保-参保地
    getAddrId(item) {
      if (this.tabActive == 0) {
        if (this.formData.addrId == item.id) {
          return false
        }
        this.formData.addrId = item.id
        // this.formData.orgName = ''
        this.formData.accountNumber = ''
        // this.getCompanyList(1)
      } else {
        if (this.formData2.addrId == item.id) {
          return false
        }
        this.formData2.addrId = item.id
        // this.formData2.orgName = ''
        this.formData2.accountNumber = ''
        // this.getCompanyList(1)
      }
      this.getList()
    },
    querySocial() {
      this.formData.showTypeTemp = this.formData.showType
      // if (!this.formData.accountNumber) {
      //   this.$message.warning('请选择申报账户')
      //   return
      // }
      if (!this.formData.moneyStartTime || !this.formData.moneyEndTime) {
        this.$message.warning('请选择费款所属期')
        return
      }
      if (this.formData.showType == '1') {
        this.getTableSocial()
      } else {
        // if (
        //   this.isEmpty(this.formData.accountNumber) ||
        //   (this.isEmpty(this.formData.moneyStartTime) &&
        //     this.isEmpty(this.formData.moneyEndTime))
        // ) {
        //   this.$message.warning('请选择申报账户和费款所属期')
        //   return
        // }
        this.getTableSocialDetail()
      }
    },
    getTableSocial() {
      var params = [
        // { property: 'addressName', value: this.formData.addressName },
        // { property: 'orgName', value: this.formData.orgName },
        { property: 'accountNumber', value: this.formData.accountNumber },
        { property: 'businessType', value: '1' },
        { property: 'moneyStartTime', value: this.formData.moneyStartTime },
        { property: 'moneyEndTime', value: this.formData.moneyEndTime },
      ]
      this.$refs.tableSocial.fetch({
        query: params,
        method: 'post',
        // url: '/agent/paidIn/queryPaidInStiatution',
        url: '/agent/paidIn/listSocialPaidIns',
      })
    },
    async getTableSocialDetail() {
      // await this.requestInfo('formData')
      var orgName = '';
      if (this.formData.accountNumber) {
        orgName = this.$refs.accountNumberSocial.selectedLabel
        orgName = orgName.slice(0, orgName.indexOf('_' + this.formData.accountNumber))
      }
      var params = [
        // { property: 'addressName', value: this.formData.addressName },
        { property: 'keyword', value: this.formData.keyword.trim() },
        // { property: 'accountNumber', value: this.formData.accountNumber },
        { property: 'orgId', value: this.formData.orgId },
        { property: 'moneyStartTime', value: this.formData.moneyStartTime },
        { property: 'moneyEndTime', value: this.formData.moneyEndTime },
      ]
      this.$refs.tableSocialDetail.fetch({
        query: params,
        method: 'post',
        url: '/agent/paidIn/listSocialPaidIns',
        callback: (res) => {
          this.socialKeyList = []
          if (res.rows.length > 0) {
            // this.socialKeyList = res.rows[0].entityNames ? res.rows[0].entityNames : []
            // {id: 2, fieldName: "证件号码", fieldCode: "10001002", entityFieldName: "idCard", sort: 2},
            this.formData.file = res.rows[0] ? res.rows[0].files : []
            this.formData.rows = res.rows
            this.formData.tableKeyList = res.rows[0]
              ? res.rows[0].fields.map((item) => {
                return {
                  fieldName: item
                }
              })
              : this.$options.data().formData.tableKeyList
            console.log('this.formData.tableKeyList--------------', this.formData.tableKeyList)
            this.$nextTick(() => {
              this.$refs.tableSocialDetail.doLayout()
            })
          }
        },
      })
    },
    queryAccfund() {
      this.formData2.showTypeTemp = this.formData2.showType
      console.log(this.formData2.accountNumber)
      // if (!this.formData2.accountNumber) {
      //   this.$message.warning('请选择申报账户')
      //   return
      // }
      if (!this.formData2.moneyStartTime || !this.formData2.moneyEndTime) {
        this.$message.warning('请选择费款所属期')
        return
      }
      if (this.formData2.showType == '1') {
        this.getTableAccfund()
      } else {
        // if (
        //   this.isEmpty(this.formData2.accountNumber) ||
        //   (this.isEmpty(this.formData2.moneyStartTime) &&
        //     this.isEmpty(this.formData2.moneyEndTime))
        // ) {
        //   this.$message.warning('请选择申报账户和费款所属期')
        //   return
        // }
        this.getTableAccfundDetail()
      }
    },
    getTableAccfund() {
      var params = [
        // { property: 'addressName', value: this.formData2.addressName },
        // { property: 'orgName', value: this.formData.orgName },
        { property: 'accountNumber', value: this.formData2.accountNumber },
        { property: 'businessType', value: '2' },
        { property: 'moneyStartTime', value: this.formData2.moneyStartTime },
        { property: 'moneyEndTime', value: this.formData2.moneyEndTime },
      ]
      this.$refs.tableAccfund.fetch({
        query: params,
        method: 'post',
        url: '/agent/paidIn/queryPaidInStiatution',
      })
    },
    async getTableAccfundDetail() {
      // await this.requestInfo('formData2')
      var orgName = '';
      if (this.formData2.accountNumber) {
        orgName = this.$refs.accountNumberAccfund.selectedLabel
        orgName = orgName.slice(0, orgName.indexOf('_' + this.formData2.accountNumber))
      }
      var params = [
        // { property: 'addressName', value: this.formData2.addressName },
        { property: 'keyword', value: this.formData2.keyword.trim() },
        // { property: 'accountNumber', value: this.formData2.accountNumber },      //通过组织去查 不需要账户了 yf
        // { property: 'orgName', value: orgName },
        { property: 'orgId', value: this.formData2.orgId },
        { property: 'moneyStartTime', value: this.formData2.moneyStartTime },
        { property: 'moneyEndTime', value: this.formData2.moneyEndTime },
      ]
      this.$refs.tableAccfundDetail.fetch({
        query: params,
        method: 'post',
        url: '/agent/paidIn/listFundPainds',
        callback: (res) => {
          this.accfundKeyList = []
          if (res.rows.length > 0) {
            // this.accfundKeyList = res.rows[0].entityNames ? res.rows[0].entityNames : []
            this.formData2.file = res.rows[0] ? res.rows[0].files : []
            this.formData2.rows = res.rows
            this.formData2.tableKeyList = res.rows[0]
              ? res.rows[0].fields.map((item) => {
                return {
                  fieldName: item
                }
              })
              : this.$options.data().formData2.tableKeyList
            this.$nextTick(() => {
              this.$refs.tableAccfundDetail.doLayout()
            })
          }
        },
      })
    },

    getLocationCode() {
      return new Promise((resolve, reject) => {
        this.$http({
          url: '/policy/paidIn/listPaidFieldDict',
          method: 'post',
        })
          .then((res) => {
            if (res.data.code == 200) {
              this.options = res.data.data
              this.$nextTick(() => {
                this.$refs.tableSocialDetail.doLayout()
                this.$refs.tableAccfundDetail.doLayout()
              })
              resolve(res)
            } else {
              this.$message.error('获取本地实缴字典出错')
              reject(res)
            }
            // console.log(this.valueTypeOption2Item)
          })
          .catch((err) => {
            this.$message.error('获取本地实缴字典出错')
          })
      })
    },
    requestInfo(form) {
      this.showLoading()
      this[form].tableKeyList = [
        {
          id: 1,
          fieldName: '姓名',
          fieldCode: '10001001',
          entityFieldName: 'employeeName',
          sort: 1,
        },
        {
          id: 2,
          fieldName: '证件号码',
          fieldCode: '10001002',
          entityFieldName: 'idCard',
          sort: 2,
        },
        {
          id: 3,
          fieldName: '费款所属期',
          fieldCode: '10001003',
          entityFieldName: 'periodOfExpense',
          sort: 3,
        },
        {
          id: 4,
          fieldName: '单位合计金额',
          fieldCode: '10001004',
          entityFieldName: 'compTotalAmount',
          sort: 4,
        },
        {
          id: 5,
          fieldName: '个人合计金额',
          fieldCode: '10001005',
          entityFieldName: 'empTotalAmount',
          sort: 5,
        },
        {
          id: 6,
          fieldName: '缴纳合计金额',
          fieldCode: '10001006',
          entityFieldName: 'totalAmount',
          sort: 6,
        },
      ]
      return this.$http({
        url: '/policy/paidIn/queryPaidInField',
        method: 'post',
        params: {
          address: this[form].addrId,
          businessType: form == 'formData' ? '1' : '2',
        },
      })
        .then(async ({ data }) => {
          console.log(data.data)
          this.closeLoading()
          if (data && data.code == 200) {
            if (data.data.length > 0) {
              this[form].tableKeyList.push(
                ...this.filterKey(this.options, data.data)
              )
            } else {
            }
          } else {
          }
        })
        .catch(() => {
          this.closeLoading()
        })
    },
    // 明细合计
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
    filterKey(data1, data2) {
      var arr = []
      var arr2 = [
        '姓名',
        '证件号码',
        '费款所属期',
        '单位合计金额',
        '个人合计金额',
        '缴纳合计金额',
      ]
      data1.forEach((item) => {
        data2.forEach((item2) => {
          if (
            item.fieldCode == item2.localPaidInField &&
            arr2.indexOf(item.fieldName) <= -1
          ) {
            arr.push(item)
          }
        })
      })
      return arr
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
    /*goDetail() {
      this.$router.push('/paidIn/detail?type=' + this.formData.businessType)
    },*/
    /*  changeBussinssType(val) {
      this.formData.accountNumber = ''
    },*/
    // 获取申报账户
    getList() {
      let that = this
      this.$http({
        url: '/policy/declareAccount/seeMainBody',
        method: 'post',
        params: {
          bussinessType: this.tabActive + 1,
          addressId:
            this.tabActive + 1 == '1'
              ? this.formData.addrId
              : this.formData2.addrId,
        },
      })
        .then(({ data }) => {
          // let res = data.data
          // let socialAccountsArr = [], accfundAccountsArr = []
          // res.map(item => {
          //   socialAccountsArr = []
          //   accfundAccountsArr = []
          //   item.socialAccounts.map(item1 => {
          //     socialAccountsArr.push({id:item1.id,accountNumber:item1.accountNumber})
          //   })
          //   item.accfundAccounts.map(item2 => {
          //     accfundAccountsArr.push({id:item2.id,accountNumber:item2.accountNumber})
          //   })
          //   item.socialAccountsStr = socialAccountsArr.join('；')
          //   item.accfundAccountsStr = accfundAccountsArr.join('；')
          //   this.socialAccountsArr.push(...socialAccountsArr)
          //   this.accfundAccountsArr.push(...accfundAccountsArr)
          // })
          that.listData = data.data
        })
        .catch((data) => { })
    },
    changeTab(index) {
      // if(index == 0){
      //   this.formData.accountNumber = ''
      // }else{
      //   this.formData2.accountNumber = ''
      // }
      this.getList()

      if (index == 0) {
        this.querySocial()
      } else {
        this.queryAccfund()
      }
    },
    isEmpty(value) {
      return value === null || value === undefined || value === ''
    },
  },
}
</script>
<style lang="less" scoped>
/deep/.el-date-editor .el-range-separator {
  line-height: 23px;
}

.downLoadFile {
  color: #3e82ff;
  cursor: pointer;
}

.downLoadFile:hover {
  text-decoration: underline;
}

.search-row {
  padding-top: 15px;
  padding-bottom: 15px;
}
</style>
