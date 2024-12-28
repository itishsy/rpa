<template>
  <div class="content spl-container">
    <!-- 导航 -->
    <breadcrumb :data="pathData"></breadcrumb>
    <div style="padding: 0px 20px 0 20px;">
      <div>
        <div class="search-row clearfix">
          <el-row>
            <el-col :span="7" class="display-flex">
              <span class="label">客户：</span>
              <el-select
                v-model="formData.custId"
                class="w-280"
                clearable
                filterable
                placeholder="请选择客户"
              >
                <el-option
                  v-for="item in customList"
                  :key="item.id"
                  :label="item.customerName"
                  :value="item.id"
                ></el-option>
              </el-select>
            </el-col>
            <el-col :span="6" class="display-flex" style="height: 30px;">
              <span class="label" style="white-space:nowrap;margin-left:30px;">地区：</span>
              <addrSelector
                v-model="formData.addrName"
                @changeAddrSelect="changeAddrSelect"
                :addrArr="addrArr"
                width="100%"
                class="search-row-item"
              ></addrSelector>
            </el-col>
            <el-col :span="6" class="display-flex" style="height: 30px;">
              <span class="label" style="white-space:nowrap;margin-left: 30px;">日期：</span>
              <el-date-picker
                v-model="formData.createStartTime"
                value-format="yyyy-MM-dd"
                type="date"
                class="search-row-item"
                placeholder="选择时间"
                :picker-options="getPickerOption()"
                clearable
              ></el-date-picker>
            </el-col>
            <el-col :span="5" class="text-right">
              <el-button type="primary" class="search-btn w-60" @click="querySearch">查询</el-button>
              <el-tooltip class="item" effect="dark" content="只生成当前日期的数据" placement="top-end">
                <el-button type="primary" style="margin-left: 20px;" @click="resetReport">重新生成</el-button>
              </el-tooltip>
              <!-- <el-button class="ml-15" size="small" type="primary" @click="configMsgTemplate">短信模板配置</el-button> -->
            </el-col>
          </el-row>
        </div>
        <div>
          <dTable
            ref="tableList"
            @fetch="getTableList"
            :tableHeight="tableHeight"
            :paging="true"
            :showIndex="true"
            rowKey="id"
            :showSummary="true"
            :getSummaries="getSummaries"
            :span-method="arraySpanMethod"
          >
            <u-table-column prop="addrName" label="城市" min-width="80" :show-overflow-tooltip="true"></u-table-column>
            <u-table-column
              prop="businessType"
              label="业务类型"
              min-width="80"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <p>{{scope.row.businessType == 1 ? '社保':'公积金'}}</p>
              </template>
            </u-table-column>
            <u-table-column
              prop="clientName"
              label="客户"
              min-width="150"
              :show-overflow-tooltip="true"
            ></u-table-column>
            <u-table-column
              prop="fileName"
              label="申报期"
              :show-overflow-tooltip="true"
              min-width="80"
            >
              <template slot-scope="scope">
                <p>{{scope.row.declareStartTime}} ~ {{scope.row.declareEndTime}}</p>
              </template>
            </u-table-column>
            <u-table-column
              prop="todayPendDeclare"
              label="当日待申报人数"
              min-width="130"
              :show-overflow-tooltip="true"
            ></u-table-column>
            <u-table-column
              prop="pendDeclareThree"
              label="待申报超3日"
              min-width="100"
              :show-overflow-tooltip="true"
            ></u-table-column>
            <u-table-column
              prop="pendDeclareFive"
              label="待申报超5日"
              min-width="100"
              :show-overflow-tooltip="true"
            ></u-table-column>
            <u-table-column
              prop="todayDeclareProgress"
              label="当日申报中人数"
              min-width="130"
              :show-overflow-tooltip="true"
            ></u-table-column>
            <u-table-column
              prop="progressDeclareOne"
              label="申报中超1日"
              min-width="100"
              :show-overflow-tooltip="true"
            ></u-table-column>
            <u-table-column
              prop="todayCompleteDeclare"
              label="当日完成申报数据"
              min-width="140"
              :show-overflow-tooltip="true"
            ></u-table-column>
            <u-table-column
              prop="monthCompleteDeclare"
              label="本月完成申报数据"
              min-width="140"
              :show-overflow-tooltip="true"
            ></u-table-column>
            <u-table-column
              prop="monthFailDeclare"
              label="本月申报失败条数"
              min-width="140"
              :show-overflow-tooltip="true"
            ></u-table-column>
            <template slot="pagination-btns">
              <div class="display-flex">
                <el-button
                  size="small"
                  icon="icon ic-export-blue"
                  class="btn--border-blue s-btn"
                  @click="exportDailyReport"
                >导出</el-button>
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

export default {
  components: { dTable, addrSelector },
  data() {
    return {
      formData: {
        custName: '',
        custId: '',
        createStartTime: this.$moment().format('YYYY-MM-DD'),
        resetReportTime: this.$moment().format('YYYY-MM-DD'),
        addrId: '',
        addrName: '',
      },
      accountList: [],
      pathData: [],
      options: [],
      customList: [],
      loading: null,
      addrArr: [],
      formList: [],
      declareSumData: {} // 汇总数据
    }
  },
  computed: {
    getPickerOption() {
      return function (time, key) {
        var obj = {}
        var self = this
        return {
          disabledDate(time) {
            let curDate = new Date().getTime()
            return time.getTime() > curDate
          },
        }
      }
    },
    tableHeight: function () {
      return this.$global.bodyHeight - 270 + 'px'
    },
  },
  created() {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = tabs[this.$route.path]
    }
    this.getlistCustomer() //获取客户
    this.getAddrArr()
  },
  mounted() {
    this.querySearch()
  },
  methods: {
    querySearch() {
      let that = this
      this.showLoading()
      // 查询汇总
      var params = [
        { property: 'clientId', value: this.formData.custId },
        { property: 'addrId', value: this.formData.addrId },
        { property: 'reportDate', value: this.formData.createStartTime?this.formData.createStartTime:'' },
      ]
      this.$http({
        url: '/agent/declareChangeCount/queryDeclareSum',
        method: 'post',
        data: {
          query: params
        }
      }).then(({ data }) => {
        that.declareSumData = data.data
        that.closeLoading()
        that.getTableList()
      }).catch((item) => {
        that.closeLoading()
      })
    },
    getTableList() {
      var params = [
        { property: 'clientId', value: this.formData.custId },
        { property: 'addrId', value: this.formData.addrId },
        { property: 'reportDate', value: this.formData.createStartTime?this.formData.createStartTime:'' },
      ]
      this.$refs.tableList.fetch({
        query: params,
        method: 'post',
        url: '/agent/declareChangeCount/queryDeclareCount',
      })
    },
    //改变地区
    changeAddrSelect(item) {
      if(item.id!=this.formData.addrId){
        this.formData.addrId = item.id
      }
    },
    //重新生成日报表
    resetReport() {
      let that = this
      this.showLoading()
      this.$http({
        url: '/agent/declareChangeCount/generateReport',
        method: 'post'
      }).then(({ data }) => {
        that.closeLoading()
        that.$message.success('操作成功')
        if (that.formData.createStartTime == null || that.formData.createStartTime === '' || that.formData.createStartTime === that.formData.resetReportTime){
          that.querySearch()
        }
      }).catch((item) => {
        that.closeLoading()
      })
    },
    //导出日报表
    exportDailyReport() {
      var params = this.$refs.tableList.getParamsQuery()
      this.$downloadFile(
        '/agent/declareChangeCount/downloadDeclareCount',
        'post',
        {
          query: params
        },
        this.$global.EXCEL
      )
    },
    showLoading(target) {
      this.loading = this.$loading({
        target: target ? target : document.body,
        lock: true,
        text: '生成中',
        spinner: 'el-icon-loading',
        background: 'rgba(255, 255, 255, 0.7)',
      })
    },
    closeLoading() {
      if (this.loading && this.loading.close) {
        this.loading.close()
      }
    },
    //获取客户
    getlistCustomer() {
      this.$http({
        url: '/robot/app/client/listCustomer',
        method: 'post',
      })
        .then((data) => {
          this.customList = data.data.data
        })
        .catch((err) => {})
    },
    //获取地区
    getAddrArr() {
      this.$http({
        url: '/policy/declareAddr/addrList',
        method: 'post',
      })
        .then(({ data }) => {
          this.addrArr = data.data
        })
        .catch((data) => {})
    },
    //合计
    getSummaries() {
      var sums = [['合计', '0', '0', '0', '0', '0', '0', '0', '0']]
      var data = this.declareSumData
      if(data){
        sums = [['合计', data.todayPendDeclareCount, data.pendDeclareThreeCount,
          data.pendDeclareFiveCount, data.todayDeclareProgressCount,
          data.progressDeclareOneCount, data.todayCompleteDeclareCount,
          data.monthCompleteDeclareCount, data.monthFailDeclareCount]]
      }
      return sums
    },
		//合并合计行
    arraySpanMethod() {
      this.$nextTick(() => {
        if (this.$refs.tableList.$el) {
          let current = this.$refs.tableList.$el
            .querySelector('.el-table__footer-wrapper')
            .querySelector('.el-table__footer')
          let cell = current.rows[0].cells
          cell[0].style.textAlign = 'center'
          cell[0].colSpan = '5'
          let current2 = this.$refs.tableList.$el
            .querySelector('.el-table__fixed-footer-wrapper')
            .querySelector('.el-table__footer')
          let cell2 = current2.rows[0].cells
          cell2[0].style.textAlign = 'center'
          cell2[0].colSpan = '4'
        }
      })
    },
  },
}
</script>
  <style lang="less" scoped>
/deep/.pal-table .el-table__fixed {
  height: 100% !important;
}
/deep/.el-table__body-wrapper {
  z-index: 2;
}
/deep/.el-table__fixed-footer-wrapper, /deep/.el-table__footer-wrapper {
  bottom: -3px;
  td{
    font-weight: bold!important;
    background-color: #EEF0F7;
  }
}
</style>
