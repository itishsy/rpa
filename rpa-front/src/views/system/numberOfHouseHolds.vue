<template>
    <div class="content spl-container">
        <!-- 导航 -->
        <breadcrumb :data="pathData">
        </breadcrumb>
        <div style="padding: 20px 30px 0px 30px">
            <el-row type="flex" align="center">
                <el-col :span="6" class="display-flex align-center">
                    <span class="label">客户名称：</span>
                    <el-select v-model="searchForms.custId" class="w-300" placeholder="请选择客户名称" clearable filterable
                        @change="onCustIdChange">
                        <el-option v-for="it in customerArr" :key="it.id" :label="it.name" :value="it.id"></el-option>
                    </el-select>
                </el-col>
                <el-col :span="6" class="display-flex align-center">
                    <span class="label">申报单位：</span>
                    <el-select v-model="searchForms.baseId" class="w-300" placeholder="请选择参保单位" clearable filterable @change="onChangeBaseId">
                        <el-option v-for="it in unitList" :key="it.id" :label="it.orgName" :value="it.id"></el-option>
                    </el-select>
                </el-col>
                <el-col :span="6" class="display-flex align-center">
                    <span class="label">参保城市：</span>
                    <addrSelector v-model="searchForms.addrName" :addrArr="cityList" width="100%" class="search-row-item"
                        @changeAddrSelect="getAddrId">
                    </addrSelector>
                </el-col>
                <el-col :span="6" class="display-flex align-center">
                    <span class="label required">参保时间：</span>
                    <div style="display:flex;max-width:300px">
                        <el-date-picker v-model="searchForms.dataMonth" value-format="yyyy-MM" style="width:100%;"
                            type="month" placeholder="选择参保时间" clearable @change="onCustIdChange"></el-date-picker>
                    </div>
                </el-col>
                <el-col :span="2" class="text-right">
                    <el-button size="small" type="primary" class="search-btn" @click="clickQuery">查询</el-button>
                    <el-button size="small" class="search-btn" @click="reset">重置</el-button>
                </el-col>
            </el-row>

            <div class="mt-20">
                <div style="width: 100%;background-color: #e2e2e2;padding: 10px 0;">
                    <span class="pl-10">共{{ total }}条数据</span>
                    <span class="ml-30">在户人数：{{ totalMember }}</span>
                </div>
                <!-- 表格 -->
                <dTable @fetch="getTableData" ref="table" :tableHeight="tableHeight" :paging="true" :showSelection="true"
                    :showIndex="true" rowKey="id" :rowHeight="45" @selection-change="handleSelectionChange">
                    <u-table-column prop="customerName" label="客户名称" min-width="200"
                        :show-overflow-tooltip="true"></u-table-column>
                    <u-table-column prop="orgName" label="申报单位" min-width="250"
                        :show-overflow-tooltip="true"></u-table-column>
                    <u-table-column prop="addrName" label="参保城市" min-width="100"
                        :show-overflow-tooltip="true"></u-table-column>
                    <u-table-column prop="dataMonth" label="参保时间" min-width="100"
                        :show-overflow-tooltip="true"></u-table-column>
                    <u-table-column prop="registerNumber" label="在户人数" min-width="160" :show-overflow-tooltip="true"
                        sortable>
                      <template slot-scope="scope">
                        <span v-text="scope.row.registerNumber==null || scope.row.registerNumber==0?'-':scope.row.registerNumber"></span>
                      </template>
                    </u-table-column>
                    <u-table-column prop="registerList" label="社保人数" min-width="160" :show-overflow-tooltip="true">
                        <template slot-scope="scope">
                            <el-tooltip class="item" effect="dark" :content="valiFullText(scope.row.registerList)"
                                placement="top">
                                <span>
                                    {{ valiText(scope.row.registerList) }}
                                </span>
                            </el-tooltip>
                        </template>
                    </u-table-column>
                    <u-table-column prop="accfundRegisterNumber" label="公积金人数" min-width="160"
                        :show-overflow-tooltip="true">
                      <template slot-scope="scope">
                        <span v-text="scope.row.accfundRegisterNumber==null || scope.row.accfundRegisterNumber==0?'-':scope.row.accfundRegisterNumber"></span>
                      </template>
                    </u-table-column>

                    <u-table-column fixed="right" label="操作" width="100">
                        <template slot-scope="scope">
                            <el-button type="text" size="small" @click="viewRequest(scope.row)">查看</el-button>
                        </template>
                    </u-table-column>
                    <template slot="pagination-btns">
                        <div class="display-flex">
                            <el-button size="small" icon="icon ic-export-blue" class="btn--border-blue s-btn"
                                @click="exportTableData">导出</el-button>
                        </div>
                    </template>
                </dTable>
            </div>
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
      searchForms: {
        addrName: '',
        addrId: null,
        baseId: null,
        custId: null, // 客户id
        dataMonth: this.$moment().format('YYYY-MM') // 参保时间
      },
      customerArr: [],
      total: '-', // 总记录数
      totalMember: '-', // 在户人数总数
      unitList: [], // 参保单位列表
      cityList: [] // 参保城市列表
    }
  },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 300 + 'px'
    }
  },
  watch: {},
  created () {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = tabs[this.$route.path]
    }
    this.getCustomerArr()
    this.getUnitList()
    this.getCityList()
  },
  beforeMount () { },
  mounted () {
    this.clickQuery()
    this.getHeaderStatistics()
  },
  methods: {
    valiText (list) {
      if (list.length == 0) return '-'
      let text = ''
      for (const item of list) {
        text += item.registerNumber + ';'
      }
      return text.substring(0, text.length - 1)
    },
    valiFullText (list) {
      let text = ''
      for (const item of list) {
        text += item.tplTypeName + ':' + item.registerNumber + ';'
      }
      return text.substring(0, text.length - 1)
    },
    getCustomerArr () {
      let that = this
      this.$http({
        url: '/policy/sys/customer/listAllCustomer?filter=true&status=',
        method: 'post'
      }).then(({ data }) => {
        if (data.code == '200') {
          that.customerArr = data.data
        }
      }).catch((data) => { })
    },
    getTableData () {
      let that = this
      let search = this.searchForms
      if (!search.dataMonth) {
        this.$message.warning('请先选择参保时间')
        return
      }
      var params = [
        { property: 'custId', value: search.custId },
        { property: 'dataMonth', value: search.dataMonth },
        { property: 'addrId', value: search.addrId },
        { property: 'baseId', value: search.baseId }

      ]

      this.$refs.table.fetch({
        query: params,
        method: 'post',
        url: '/policy/cust/insured/page',
        callback: function (res) {
          console.log('res----------', res)
          if (res.records == 0) return
          // that.total = res.records
          // that.totalMember = res.rows.reduce((accumulator, currentValue) => {
          //     return accumulator + currentValue.registerNumber
          // }, 0);
        }
      })
    },
    // 表格勾选回调
    handleSelectionChange (val) {
    },
    clickQuery () {
      this.getTableData()
    },
    reset () {
      this.searchForms = {
        custId: null, // 客户id
        dataMonth: this.$moment().format('YYYY-MM') // 参保时间
      }
      this.clickQuery()
      this.getHeaderStatistics() // 重新获取表头
    },
    exportTableData () {
      let params = this.$refs.table.getParamsQuery()
      if (!params) {
        this.$message.warning('请先查询数据')
        return
      }

      var params2 = this.$global.deepcopyArray(params)
      this.$downloadFile(
        '/policy/cust/insured/export',
        'post',
        {
          query: params2
        },
        this.$global.EXCEL
      )
    },
    // 查看
    viewRequest (row) {
      // console.log('viewRequest-------------',row)
      this.$router.push({
        path: '/system/houseHoldsList',
        query: {
          orgId: row.orgId, // 申报单位id
          dataMonth: row.dataMonth // 参保年月
        }
      })
    },
    // 获取列头统计
    async getHeaderStatistics () {
      const { data: { code, data: result } } = await this.$http({
        url: `policy/cust/insured/statistics`,
        method: 'post',
        data: {
          query: [
            { property: 'custId', value: this.searchForms.custId },
            { property: 'dataMonth', value: this.searchForms.dataMonth },
            { property: 'addrId', value: this.searchForms.addrId },
            { property: 'baseId', value: this.searchForms.baseId }
          ]
        }
      })
      if (code == 200) {
        this.total = result.records
        this.totalMember = result.registerNumber
      }
    },
    // 点选回调
    onCustIdChange (e) {
      this.getUnitList()
      this.getHeaderStatistics()
      this.clickQuery()
    },
    // 获取申报单位
    async getUnitList () {
      const { data: { code, data: result } } = await this.$http({
        url: `policy/customMade/offerSettings/getOrgList?addrId=&custFilter=0&businessType=&custId=${this.searchForms.custId || ''}`,
        method: 'post'
      })
      if (code == 200) {
        // console.log('申报单位',result)
        this.unitList = result.filter(item => item)
      }
    },
    // 获取参保城市
    async getCityList () {
      const { data: { code, data: result } } = await this.$http({
        url: `policy/declareAccount/availableAddress`,
        method: 'post'
      })
      if (code == 200) {
        // console.log('参保城市',result)
        this.cityList = result
      }
    },
    // 选择参保城市
    getAddrId (item) {
      if (this.searchForms.addrId == item.id) {
        return false
      }
      this.searchForms.addrId = item.id
      this.getHeaderStatistics()
      this.clickQuery()
    },
    // 选择申报单位
    onChangeBaseId (e) {
      this.getHeaderStatistics()
      this.clickQuery()
    }
  }
}
</script>
<style lang='less' scoped>
.align-center {
    align-items: center;
}
</style>
