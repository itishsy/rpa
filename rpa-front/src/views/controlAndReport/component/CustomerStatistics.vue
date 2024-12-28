<template>
  <div>
    <div class="search-row clearfix">
      <el-row>
        <el-col :span="8" class="display-flex">
          <span class="label required-pre">业务类型：</span>
          <div class="pt-5">
            <el-radio-group v-model="searchData.businessType">
              <el-radio :label="1">社保</el-radio>
              <el-radio :label="2">公积金</el-radio>
            </el-radio-group>
          </div>
        </el-col>
        <el-col :span="8" class="display-flex">
          <span class="label">参保城市：</span>
          <div class="search-row-item">
            <addrSelector
              v-model="searchData.addrName"
              :addrArr="allAddr"
              width="100%"
              placeholder="请输入"
              @changeAddrSelect="changeAddrSelect"></addrSelector>
          </div>
        </el-col>
        <el-col :span="8" class="display-flex">
          <span class="label">客户名称：</span>
          <el-select v-model="searchData.clientId" class="search-row-item" clearable filterable placeholder="请选择">
            <el-option v-for="item in listCustomerOption" :key="item.id" :label="item.customerName"
                       :value="item.id"></el-option>
          </el-select>
        </el-col>

      </el-row>
      <el-row class="mt-15">
        <el-col :span="8" class="display-flex">
          <span class="label">申报时间：</span>
          <div class="flex1" style="display: flex; align-items: center; padding-right: 0px">
            <el-date-picker v-model="searchData.startDate" type="month" value-format="yyyy-MM"
                            placeholder="开始时间" style="width: 120px"></el-date-picker>
            <span style="margin: 0 5px">至</span>
            <el-date-picker v-model="searchData.endDate" type="month" value-format="yyyy-MM"
                            placeholder="结束时间" style="width: 120px"></el-date-picker>
          </div>
        </el-col>
        <el-col :span="8" class="display-flex">
          <span class="label"></span>
          <el-input v-model="searchData.searchText" class="search-row-item" placeholder="请输入申报信息关键字"
                    clearable></el-input>
        </el-col>
        <el-col :span="8" class="text-right">
          <el-button class="w-60" size="small" type="primary" @click="getTableData">查询</el-button>
        </el-col>
      </el-row>
    </div>
    <dTable @fetch="getTableData" ref="dTable" :tableHeight="tableHeight"
            :paging="true" :showSelection="false" :showIndex="true" rowKey="id">
      <u-table-column prop="submitMonth" label="申报时间" min-width="80"
                      :show-overflow-tooltip="true"></u-table-column>
      <u-table-column prop="customerName" label="客户" min-width="160"
                      :show-overflow-tooltip="true"></u-table-column>
      <u-table-column prop="machineName" label="设备编号" min-width="120" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <span>{{ scope.row.machineName || '-' }}</span>
        </template>
      </u-table-column>
      <u-table-column prop="addrName" label="参保城市" min-width="80">
      </u-table-column>
      <u-table-column prop="companyAccount" label="申报账户" min-width="180"
                      :show-overflow-tooltip="true"></u-table-column>
      <u-table-column prop="changeTypeName" label="申报类型" min-width="80"></u-table-column>
      <u-table-column prop="validateFailedNumber" label="校验失败人数" min-width="110">
        <template slot-scope="scope">
                        <span class="view-text" :class="{ 'text-red': scope.row.validateFailedNumber }">{{
                            scope.row.validateFailedNumber || '-'
                          }}</span>
        </template>
      </u-table-column>
      <u-table-column prop="toBeDeclaredNumber" :label="declareTimeStr" min-width="180">
        <template slot-scope="scope">
                        <span class="view-text" :class="{ 'text-red': scope.row.toBeDeclaredNumber }">{{
                            scope.row.toBeDeclaredNumber || '-'
                          }}</span>
        </template>
      </u-table-column>
      <u-table-column prop="declarationInProgressNumber" :label="declareTime2Str" min-width="180">
        <template slot-scope="scope">
                        <span class="view-text">{{
                            scope.row.declarationInProgressNumber || '-'
                          }}</span>
        </template>
      </u-table-column>
      <u-table-column prop="failedNumber" label="申报失败人数" min-width="110">
        <template slot-scope="scope">
                        <span class="view-text">{{
                            scope.row.failedNumber || '-'
                          }}</span>
        </template>
      </u-table-column>
      <u-table-column prop="successNumber" label="申报成功人数" min-width="110">
        <template slot-scope="scope">
                        <span class="view-text">{{
                            scope.row.successNumber || '-'
                          }}</span>
        </template>
      </u-table-column>

      <template slot="pagination-btns">
        <div class="display-flex">
          <el-button size="small" icon="icon ic-export-blue" class="btn--border-blue s-btn"
                     v-if="$global.hasPermission('curInsuranceExport')" @click="exportTableData">导出
          </el-button>
        </div>
      </template>
    </dTable>
  </div>
</template>
<script>
import dTable from '@/components/common/table'
import addrSelector from 'components/common/addrSelector.vue'

export default {
  name: '',
  components: { addrSelector, dTable },
  props: [''],
  data () {
    return {
      searchData: {
        businessType: 1,
        addrId: '',
        addrName: '',
        clientId: null, // 客户id
        startDate: this.$moment().endOf('month').format('YYYY-MM'),
        endDate: this.$moment().endOf('month').format('YYYY-MM'), // 上报时间
        searchText: ''
      },
      allAddr: [],
      listCustomerOption: [],
      declareTime: '', // 待申报时间
      declareTime2: '' // 申报中时间
    }
  },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 380 + 'px'
    },
    declareTimeStr () {
      return `待申报（>${this.declareTime}小时）人数`
    },
    declareTime2Str () {
      return `申报中（>${this.declareTime2}小时）人数`
    }

  },
  watch: {},
  created () {
    this.getAddrArr()
    this.getlistCustomer()
    this.getTableHeader()
    this.$nextTick(() => {
      this.getTableData()
    })
  },
  beforeMount () {
  },
  mounted () {
  },
  methods: {
    getTableData () {
      let searchData = this.searchData
      if (searchData.startDate && searchData.endDate && new Date(searchData.startDate) > new Date(searchData.endDate)) {
        this.$message.warning('申报开始时间不能大于结束时间')
        return
      }
      var params = [
        { property: 'businessType', value: searchData.businessType },
        { property: 'addrId', value: searchData.addrId },
        { property: 'customerId', value: searchData.clientId || null },
        { property: 'monthStart', value: searchData.startDate },
        { property: 'monthEnd', value: searchData.endDate },
        { property: 'searchText', value: searchData.searchText }
      ]
      this.$refs.dTable.fetch({
        query: params,
        method: 'post',
        url: '/agent/declareChange/statistics'
      })
    },
    // 获取参保城市
    getAddrArr () {
      this.$http({
        url: '/policy/declareAddr/addrList',
        method: 'post'
      }).then(({ data }) => {
        if (data.code == 200) {
          this.allAddr = data.data
        }
      }).catch((data) => {
      })
    },
    // 改变参保地
    changeAddrSelect (item) {
      if (this.searchData.addrId == item.id) {
        return
      }
      this.searchData.addrId = item.id
    },
    getlistCustomer () {
      this.$http({
        url: '/robot/app/client/listCustomer',
        method: 'post'
      })
        .then((data) => {
          this.listCustomerOption = data.data.data
        })
        .catch((err) => {
        })
    },
    // 获取动态表头
    getTableHeader () {
      this.$http({
        url: 'policy/sys/dict/getDictByKey?dataKey=10015',
        method: 'get',
        data: {}
      })
        .then((data) => {
          const headerData = data.data.data
          console.log('getTableHeader------>', headerData)
          this.declareTime = headerData.find(item => item.dictCode === '10015001').dictName
          this.declareTime2 = headerData.find(item => item.dictCode === '10015002').dictName
        })
        .catch((err) => {
        })
    },
    // 导出表格
    exportTableData () {
      let params = this.$refs.dTable.getParamsQuery()
      if (!params) {
        this.$message.warning('请先查询数据')
        return
      }

      var params2 = this.$global.deepcopyArray(params)
      this.$downloadFile(
        '/agent/declareChange/exportStatistics',
        'post',
        {
          query: params2
        },
        this.$global.EXCEL
      )
    }

  }
}
</script>
<style lang="less" scoped>
.label {
  width: 80px;
  text-align: right;
}

.search-row-item {
  width: 80%;
  min-width: 150px;
  max-width: 300px;

  /deep/ .addr-main {
    width: 100%;
  }
}
</style>
