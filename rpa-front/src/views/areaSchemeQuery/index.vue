<template>
  <div class="spl-container">
    <breadcrumb :data="pathData"></breadcrumb>
    <div class="pl-20 pr-20 box-big">
      <div style="padding: 20px">
        <div class="display-flex">
          <span class="lh-com required-pre mr-10">参保城市</span>
          <addrSelector
            v-model="formData.addrName"
            :addrArr="allAddr"
            style="line-height: 0px"
            @changeAddrSelect="changeAddrSelect"
          ></addrSelector>
          <el-button
            size="small"
            type="primary"
            class="ml-20 w-60"
            @click="handleSearch()"
          >查询</el-button
          >
        </div>
      </div>
      <div class="file-box">
        <palTab
          :tabs="tabs"
          v-model="tabActive"
          :type="2"
          @change="changeTab"
        ></palTab>
        <div v-if="!noAddr" v-loading="loading">
          <div class="cust-type-title" style="margin-top: 15px;"><span class="text">{{ formData.addrName}}{{ formData.bussinssType == 1 ? '社保' : '公积金' }}缴纳标准</span></div>
            <div style="padding: 0 20px;">
              <div class="table-title mt-15">
                <p class="table-title-l">
                  增员申报期：<span v-if="handleDeclarePolicy(1)!=='-'">{{ handleDeclarePolicy(1) }}</span>
                  <span v-else>该地{{formData.bussinssType == 1 ? '社保' : '公积金' }}系统未公示申报期</span>
                </p>
                <p v-show="getAllowAgainInsure(1)!==''">
                  是否允许同月增减：{{getAllowAgainInsure(1) == 1 ? '是' : '否' }}
                </p>
              </div>
              <div class="table-title marTop-10">
                <p class="table-title-l">
                  减员申报期：<span v-if="handleDeclarePolicy(2)!=='-'">{{ handleDeclarePolicy(2) }}</span>
                  <span v-else>该地{{formData.bussinssType == 1 ? '社保' : '公积金' }}系统未公示申报期</span>
                </p>
                <p v-show="getAllowAgainInsure(2)!==''">
                  是否允许同月增减：{{getAllowAgainInsure(2) == 1 ? '是' : '否' }}
                </p>
              </div>
              <div class="table-title marTop-10">
                <p class="table-title-l">
                  调基申报期：<span v-if="handleDeclarePolicy(3)!=='-'">{{ handleDeclarePolicy(3) }}</span>
                  <span v-else>该地{{formData.bussinssType == 1 ? '社保' : '公积金' }}系统未公示申报期</span>
                </p>
                <p v-show="getAllowAgainInsure(3)!==''">
                  是否允许同月增减：{{getAllowAgainInsure(3) == 1 ? '是' : '否' }}
                </p>
              </div>
              <div class="table-title marTop-10">
                <p class="table-title-l">
                  补缴申报期：<span v-if="handleDeclarePolicy(5)!=='-'">{{ handleDeclarePolicy(5) }}</span>
                  <span v-else>该地{{formData.bussinssType == 1 ? '社保' : '公积金' }}系统未公示申报期</span>
                </p>
                <p v-show="getAllowAgainInsure(5)!==''">
                  是否允许同月增减：{{getAllowAgainInsure(5) == 1 ? '是' : '否' }}
                </p>
              </div>
              <div
                v-show="productDetails.length > 0"
                v-for="(item, index) in productDetails"
                :key="index"
                class="rules-tables"
              >
                <p class="font-16 fw-blod text-blue mt-15" v-if="productDetails.length>1"><i class="el-icon-s-promotion font-18"></i> {{item.productName}}</p>
                <div class="table-title marTop-10">
                  <p class="table-title-l">最低金额计算：{{item.minBaseRuleName}}</p>
                  <p>最高金额计算：{{item.maxBaseRuleName}}</p>
                </div>
                <dTable
                  class="table-sin"
                  border
                  :paging="false"
                  header-cell-class-name="table_header"
                  :data="item.insuranceDetails"
                  ref="table"
                  :cancelMinHeight="true"
                  :showIndex="false"
                  :showSelection="false"
                  row-key="id"
                  row-id="id"
                  :showSummary="tabActive=='0'?true:false"
                  :getSummaries="getSummaries"
                  :key="refreshTableKey"
                >
                  <u-table-column
                    prop="itemTypeName"
                    label="险种"
                    min-width="80"
                    :show-overflow-tooltip="true"
                  >
                  </u-table-column>
                  <u-table-column v-if="formData.bussinssType == 2" prop="compBaseMin" label="最低基数" min-width="90" :show-overflow-tooltip="true"></u-table-column>
                  <u-table-column v-if="formData.bussinssType == 2" prop="compBaseMax" label="最高基数" min-width="90" :show-overflow-tooltip="true"></u-table-column>
                  <u-table-column
                    class="center-tit"
                    label="单位部分"
                    class-name="db-header"
                    :show-overflow-tooltip="true"
                  >
                    <u-table-column v-if="formData.bussinssType == 1" prop="compBaseMin" label="单位最低基数" min-width="110" :show-overflow-tooltip="true"></u-table-column>
                    <u-table-column v-if="formData.bussinssType == 1" prop="compBaseMax" label="单位最高基数" min-width="110" :show-overflow-tooltip="true"></u-table-column>
                    <u-table-column
                      v-if="formData.bussinssType == 1"
                      prop="compRatioStr"
                      label="比例"
                      min-width="80"
                      :show-overflow-tooltip="true"></u-table-column>
                    <!-- 公积金字段 -->
                    <u-table-column
                      v-if="formData.bussinssType == 2"
                      prop="compRatioMinStr"
                      label="最低比例"
                      min-width="80"
                      :show-overflow-tooltip="true"
                    >
                    </u-table-column>
                    <u-table-column
                      v-if="formData.bussinssType == 2"
                      prop="compRatioMaxStr"
                      label="最高比例"
                      min-width="80"
                      :show-overflow-tooltip="true"
                    ></u-table-column>
                    <!-- 公积金字段 以上 -->
                    <u-table-column
                      prop="compMinPayAmount"
                      label="最低缴费金额"
                      min-width="110"
                      :show-overflow-tooltip="true"
                    >
                    </u-table-column>
                    <u-table-column
                      prop="compMaxPayAmount"
                      label="最高缴费金额"
                      min-width="110"
                      :show-overflow-tooltip="true"
                    >
                    </u-table-column>
                  </u-table-column>
                  <u-table-column label="个人部分" :show-overflow-tooltip="true" class-name="db-header">
                    <u-table-column v-if="formData.bussinssType == 1" prop="empBaseMin" label="个人最低基数" min-width="110" :show-overflow-tooltip="true"></u-table-column>
                    <u-table-column v-if="formData.bussinssType == 1" prop="empBaseMax" label="个人最高基数" min-width="110" :show-overflow-tooltip="true"></u-table-column>
                    <u-table-column
                      v-if="formData.bussinssType == 1"
                      prop="empRatioStr"
                      label="比例"
                      min-width="80"
                      :show-overflow-tooltip="true"
                    >
                    </u-table-column>
                    <!-- 公积金字段 -->
                    <u-table-column
                      v-if="formData.bussinssType == 2"
                      prop="empRatioMinStr"
                      label="最低比例"
                      min-width="80"
                      :show-overflow-tooltip="true"
                    >
                    </u-table-column>
                    <u-table-column
                      v-if="formData.bussinssType == 2"
                      prop="empRatioMaxStr"
                      label="最高比例"
                      min-width="80"
                      :show-overflow-tooltip="true"
                    >
                    </u-table-column>
                    <!-- 公积金字段 -->
                    <u-table-column
                      prop="empMinPayAmount"
                      label="最低缴费金额"
                      min-width="110"
                      :show-overflow-tooltip="true"
                    >
                    </u-table-column>
                    <u-table-column
                      prop="empMaxPayAmount"
                      label="最高缴费金额"
                      min-width="110"
                      :show-overflow-tooltip="true"
                    >
                    </u-table-column>
                  </u-table-column>
                  <u-table-column label="合计" :show-overflow-tooltip="true" class-name="db-header">
                    <u-table-column
                      prop="totalMinPayAmount"
                      label="最低缴费金额"
                      min-width="110"
                      :show-overflow-tooltip="true"
                    >
                    </u-table-column>
                    <u-table-column
                      prop="totalMaxPayAmount"
                      label="最高缴费金额"
                      min-width="110"
                      :show-overflow-tooltip="true"
                    >
                    </u-table-column>
                  </u-table-column>
                </dTable>
              </div>
              <div class="title-remork" v-show="tabActive=='0'&&productDetails.length != 0">
                <div class="remark-left">备注：</div>
                <div class="remark-right">
                  <p>
                    工伤保险缴费比例，根据用人单位主体的行业类别及工伤发生率情况，浮动核定用人单位主体的缴费比例
                  </p>
                  <p>
                    失业保险缴费比例，根据用人单位失业率情况，浮动核定用人单位主体的缴费比例
                  </p>
                </div>
              </div>
              <el-empty
                v-show="productDetails.length == 0"
                description="该地社保系统未提供标准缴纳方案"
              ></el-empty>
            </div>

          <div class="cust-type-title"><span class="text">{{ formData.addrName}}{{ formData.bussinssType == 1 ? '社保' : '公积金' }}补缴规则</span></div>
          <div style="padding: 0 20px 30px 20px;">
            <div v-show="paymentRulesData.ruleDetails.length > 0">
              <dTable
                border
                header-cell-class-name="table_header"
                :data="paymentRulesData.ruleDetails"
                ref="table1"
                style="margin-top: 20px"
                :cancelMinHeight="true"
                :showIndex="false"
                :showSelection="false"
                :paging="false"
                row-key="id"
                row-id="id"
              >
                <u-table-column
                  prop="insuranceType"
                  label="可补缴险种"
                  min-width="80"
                  :show-overflow-tooltip="true"
                >
                </u-table-column>
                <u-table-column
                  prop="mustSupplement"
                  label="是否必须补缴"
                  min-width="110"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <span v-show="scope.row.mustSupplement == 0">否</span>
                    <span v-show="scope.row.mustSupplement == 1">是</span>
                  </template>
                </u-table-column>
                <u-table-column
                  prop="acrossMonth"
                  label="是否允许跨月补缴"
                  min-width="110"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <span v-show="scope.row.acrossMonth == 0">否</span>
                    <span v-show="scope.row.acrossMonth == 1">是</span>
                  </template>
                </u-table-column>
                <u-table-column
                  prop="allowSuppType"
                  label="补缴方式"
                  min-width="120"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                  <span
                  >{{ scope.row.allowSuppType }}
                    <span v-if="scope.row.earliestAllowYearMonth"
                    >：{{ scope.row.earliestAllowYearMonth }}</span
                    >
                    <span v-if="scope.row.allowMonths"
                    >：{{ scope.row.allowMonths }}个月</span
                    >
                  </span>
                  </template>
                </u-table-column>
                <u-table-column
                  prop="suppRatioRule"
                  label="补缴比例"
                  min-width="110"
                  :show-overflow-tooltip="true"
                >
                </u-table-column>
                <u-table-column
                  prop="suppBaseRule"
                  label="补缴基数"
                  min-width="110"
                  :show-overflow-tooltip="true"
                >
                </u-table-column>
              </dTable>
            </div>
            <el-empty
              v-show="paymentRulesData.ruleDetails.length == 0"
              description="该地社保系统未公示补缴规则"
            ></el-empty>
          </div>

        </div>
        <el-empty
          v-else
          description="请先选择参保城市"
        ></el-empty>
      </div>
    </div>
    <!-- 引入地区模板 -->
  </div>
</template>
<script>
import dTable from '../../components/common/table'
import palTab from '../../components/common/pal-tab'
import addrSelector from '../../components/common/addrSelector'
import { accMul, accAdd2, parseDeclarePolicy } from '../../utils/socialAccfundProduct'
export default {
  components: { addrSelector, palTab, dTable },
  data () {
    return {
      paymentRulesData: {
        ruleDetails: []
      },
      productDetails: [],
      policyAddrDeadlineSettingList: [],
      loading: false,
      pageInfo: {
        pageSize: 20,
        pageNo: 1
      },
      fileIndex: undefined,
      fileData: [],
      tabs: ['社保', '公积金'],
      tabActive: 0,
      formData: {
        bussinssType: 1, // 1-社保或2-公积金
        addrId: '110100', // 城市id
        quoteAddrId: '', // 源地区
        quoteAddrName: '',
        addrName: '北京', // 城市名字
        changeType: '', // 申报类型
        changeTypeName: '', // 申报类型名字
        columnSettings: [], // 解析 excel 的字段集合
        fyRuleType: '', // 费用年月显示规则
        status: '1' // 有效无效
      },
      allAddr: [],
      pathData: [],
      noAddr: false,
      refreshTableKey: 0
    }
  },
  computed: {
  },
  created () {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = this.$global.deepcopyArray(
        tabs[this.$route.meta.parentPath]
      )
    }
    this.getAddr(1)
    this.$nextTick(() => {
      this.queryDate()
      this.findCityBaseProductInfo() // 标准方案
      this.findInsuredSupplementRules() // 补缴规则
    })
  },
  mounted () {},
  watch: {},
  methods: {
    queryDate () {
      this.findCityInsuranceDeadline()
    },
    handleSearch () {
      console.log(this.formData.addrId)
      if (this.formData.addrId) {
        this.noAddr = false
        this.queryDate() // 申报日期
        this.findCityBaseProductInfo() // 标准方案
        this.findInsuredSupplementRules() // 补缴规则
      } else {
        this.noAddr = true
        this.$message.warning('请先选择参保城市')
      }
    },
    onHover (item, index) {
      this.fileIndex = index
    },
    onOut (item, index) {
      this.fileIndex = -1
    },
    changeTab (index) {
      this.formData.bussinssType = index + 1
      this.handleSearch()
    },
    //  查询申报期
    findCityInsuranceDeadline (val) {
      let params = {
        businessType: this.formData.bussinssType,
        addrName: this.formData.addrName
      }
      this.$http({
        url: 'policy/policyInfo/findCityInsuranceDeadline',
        method: 'POST',
        data: params
      }).then(({ data }) => {
        this.policyAddrDeadlineSettingList = data.data
      })
    },
    handleDeclarePolicy (declareType) {
      let list = this.policyAddrDeadlineSettingList ? this.policyAddrDeadlineSettingList : []
      return parseDeclarePolicy(list, declareType)
    },
    getAllowAgainInsure (declareType) {
      let list = this.policyAddrDeadlineSettingList ? this.policyAddrDeadlineSettingList : []
      for (let i = 0; i < list.length; i++) {
        if (list[i].declareType === declareType) {
          return list[i].allowAgainInsure
        }
      }
      return ''
    },
    //  补缴规则
    findInsuredSupplementRules () {
      let params = {
        businessType: this.formData.bussinssType,
        addrName: this.formData.addrName
      }
      this.$http({
        url: '/policy/policyInfo/findInsuredSupplementRules',
        method: 'POST',
        data: params
      })
        .then(({ data }) => {
          if (data.data) {
            this.paymentRulesData = data.data
          } else {
            this.paymentRulesData = {
              ruleDetails: []
            }
          }
        })
        .catch(() => {})
    },
    // 标准方案
    findCityBaseProductInfo () {
      this.loading = true
      let bussinssType = this.formData.bussinssType
      let params = {
        businessType: bussinssType,
        addrName: this.formData.addrName,
        insureDate: this.$moment(new Date()).format('YYYY-MM')
      }
      this.$http({
        url: '/policy/policyInfo/findCityBaseProductInfo',
        method: 'POST',
        data: params
      })
        .then(({ data }) => {
          this.loading = false
          if (data.data) {
            let productDetails = data.data.productDetails
            productDetails.forEach((val) => {
              val.insuranceDetails.forEach((val) => {
                // 合计最低/高 =  单位最低/高  + 个人最低/高
                if (bussinssType == '1') {
                //  社保
                  val.compRatioStr = accMul(val.compRatio, 100).toFixed(2) + '%'
                  val.empRatioStr = accMul(val.empRatio, 100).toFixed(2) + '%'
                } else {
                  //  公积金
                  val.compRatioMaxStr = accMul(val.compRatioMax, 100).toFixed(2) + '%'
                  val.compRatioMinStr = accMul(val.compRatioMin, 100).toFixed(2) + '%'
                  val.empRatioMaxStr = accMul(val.empRatioMax, 100).toFixed(2) + '%'
                  val.empRatioMinStr = accMul(val.empRatioMin, 100).toFixed(2) + '%'
                }
              })
            })
            this.productDetails = productDetails
          } else {
            this.productDetails = []
          }
          this.refreshTableKey = new Date().getTime()
        })
        .catch(() => {})
    },

    getSummaries (param) {
      const { columns, data } = param
      const sums = []
      columns.forEach((column, index) => {
        if (index === 0) {
          sums[index] = '合计'
          return
        }
        if (index === 1) {
          sums[index] = ''
          return
        }
        if (column.property.indexOf('PayAmount') > -1) {
          const values = data.map((item) => Number(item[column.property]))
          if (!values.every((value) => isNaN(value))) {
            sums[index] = values
              .reduce((prev, curr) => {
                const value = Number(curr)
                if (!isNaN(value)) {
                  return accAdd2(prev, curr)
                } else {
                  return prev
                }
              }, 0)
          } else {
            sums[index] = 'N/A'
          }
        } else {
          sums[index] = ''
        }
      })
      return [sums]
    },
    // 改变城市
    changeAddrSelect (item) {
      console.log(item)
      if (item.id != this.formData.addrId) {
        this.formData.addrName = item.name
        this.formData.addrId = item.id
        if (item.id != '') {
          this.handleSearch()
        }
      }
    },
    // 获取参保地
    getAddr (type) {
      let that = this
      this.$http({
        url: 'policy/declareAddr/addrList',
        method: 'post'
      }).then(({ data }) => {
        this.allAddr = data.data
      })
    }
  }
}
</script>
<style lang="less" scoped>
.box-big {
  /deep/ .pal-table thead th {
    height: 40px;
    -webkit-box-sizing: border-box;
    box-sizing: border-box;
    border: 1px solid white;
    border-bottom: 1px solid white !important;
    // text-align: center;
  }
  /deep/ .pal-table tbody td {
    // border-bottom: none !important;
    // text-align: center;
  }
  /deep/ .lineFooter {
    font-weight: 600;
  }
}
.p-padd {
  padding: 15px;
}
.box-padd {
  padding: 15px;
}
.file-flex {
  display: flex;
  justify-content: start;
  flex-wrap: wrap;
  margin-left: 4%;
  .all-box {
    width: 12%;
    padding: 21px;
    padding-bottom: 0;
  }
  .isHover-dow {
    background: gainsboro;
  }
  .box-single {
    display: flex;
    flex-flow: column;
    align-items: center;
    margin-bottom: 40px;
    position: relative;
    padding: 10px 0px;
    .img-file {
      width: 86px;
      height: 67px;
    }
    .down-button {
      position: absolute;
      top: 56%;
      left: 0%;
    }
    .down-p {
      margin-top: 15px;
    }
  }
}
.ml-20 {
  margin-left: 20px;
}
.w-dow {
  width: 80px;
}

.empty-box {
  width: 100%;
  height: 100%;
  padding: 60px 0px;
  display: flex;
  justify-content: space-around;
  align-items: center;
  align-content: center;
}
.emptyState {
  width: 250px;
  height: 250px;
}
.empty-p {
  margin-left: -27px;
  color: darkgray;
}
.table-title {
  display: flex;
  .table-title-l {
    min-width: 400px;
  }
}
.marTop-10 {
  margin-top: 10px;
  //   position: relative;
  //   top: 9px;
}
.rules-tables {
  .table-sin {
    margin-top: 20px;
  }
}
.title-remork {
  display: flex;
  margin-top: 28px;
  .remark-right {
    flex: 1;
  }
}
  /deep/.db-header{
    text-align: center!important;
  }
  /deep/.has-gutter{
    font-weight: bold;
  }
/deep/.pal-table tbody td{
  padding-top: 12px;
  padding-bottom: 12px;
}
</style>
