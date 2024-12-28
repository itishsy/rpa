<template>
  <div class="spl-container">
    <breadcrumb :data="pathData"></breadcrumb>
    <div class="pl-20 pr-20">
      <el-row>
        <el-col :span="24">
          <div class="search-row">
            <el-row>
              <el-col :span="9">
                <span class="lab">参保城市：</span>
                <addrSelector v-model="search.addrName" :addrArr="options.addrArr" @changeAddrSelect="getAddrId" width="300px"></addrSelector>
              </el-col>
              <el-col :span="8" class="display-flex">
                <span class="lab pt-5">业务类型：</span>
                <el-checkbox-group v-model="search.businessType" size="medium" class="checkbox-group">
                  <el-checkbox-button :label="1">社保</el-checkbox-button>
                  <el-checkbox-button :label="2">公积金</el-checkbox-button>
                </el-checkbox-group>
              </el-col>
              <el-col :span="7">
                <el-input @keyup.enter.native="search" v-model.trim="search.keyword" clearable placeholder="输入关键字" style="width: 300px" ></el-input>
              </el-col>

<!--              <el-col :span="7" class="display-flex">
                <span class="lab pt-5">是否上线：</span>
                <el-checkbox-group v-model="search.isLive" size="medium" class="checkbox-group">
                  <el-checkbox-button :label="1">是</el-checkbox-button>
                  <el-checkbox-button :label="0">否</el-checkbox-button>
                </el-checkbox-group>
              </el-col>-->
            </el-row>
            <el-row class="mt-15">
              <el-col :span="16" class="display-flex">
                <span class="lab pt-5">申报类型：</span>
                <el-checkbox-group v-model="search.declareType" size="medium" class="checkbox-group">
                  <el-checkbox-button :label="1">增员</el-checkbox-button>
                  <el-checkbox-button :label="2">减员</el-checkbox-button>
                  <el-checkbox-button :label="3">调基</el-checkbox-button>
                  <el-checkbox-button :label="5">补缴</el-checkbox-button>
                </el-checkbox-group>
              </el-col>
              <el-col :span="8" class="text-right">
                <el-button type="primary" class="w-60" @click="doSearch">查询</el-button>
                <el-button class="btn--border-blue s-btn" style="margin-left: 20px;" @click="goAddOrEdit()">新增</el-button>
              </el-col>
            </el-row>
          </div>
        </el-col>
      </el-row>
      <div>
        <dTable @fetch="getTableData" ref="table" style="margin-top: 0;" :tableHeight="tableHeight" :showIndex='true'
                :showSelection='false' row-key="id" row-id="id">
          <u-table-column prop="addrName" label="参保城市" width="100" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="businessType" label="业务类型" width="100" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <p>{{scope.row.businessType=='1'?'社保':'公积金'}}</p>
            </template>
          </u-table-column>
          <u-table-column prop="declareWebsiteName" label="申报网站" width="120" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="declareType" label="申报类型" width="100" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <p v-if="scope.row.declareType=='1'">增员</p>
              <p v-else-if="scope.row.declareType=='2'">减员</p>
              <p v-else-if="scope.row.declareType=='3'">调基</p>
              <p v-else-if="scope.row.declareType=='5'">补缴</p>
            </template>
          </u-table-column>
          <u-table-column prop="functionPointName" label="当前节点" min-width="100" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="warnMessage" label="提示信息" min-width="200" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <p v-if="(scope.row.matchRule!==''&&scope.row.matchRule!=null) || (scope.row.resultMsg!==''&&scope.row.resultMsg!=null)">
                <span v-if="scope.row.matchRule===1">完全等于</span>
                <span v-else-if="scope.row.matchRule===2">包含</span>
                <span v-else-if="scope.row.matchRule===3">以此信息开头</span>
                <span v-else-if="scope.row.matchRule===4">以此信息结尾</span>
                ：{{ scope.row.resultMsg }}
              </p>
            </template>
          </u-table-column>
          <u-table-column prop="declareStatus" label="申报回盘" width="100" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <p v-if="scope.row.declareStatus===2">申报中</p>
              <p v-else-if="scope.row.declareStatus===4">申报成功</p>
              <p v-else-if="scope.row.declareStatus===5">申报失败</p>
            </template>
          </u-table-column>
          <u-table-column prop="nextDeclareName" label="下一节点" min-width="120" :show-overflow-tooltip="true"></u-table-column>

<!--          <u-table-column prop="functionPoints" label="功能点" min-width="100" :show-overflow-tooltip="true"></u-table-column>-->
<!--          <u-table-column prop="handleAction" label="处置动作" min-width="150" :show-overflow-tooltip="true"></u-table-column>-->
<!--          <u-table-column prop="scenarioDescription" label="场景说明" min-width="150" :show-overflow-tooltip="true"></u-table-column>-->
<!--          <u-table-column prop="isLive" label="是否上线" min-width="100" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <p v-if="scope.row.isLive===0">否</p>
              <p v-else>是</p>
            </template>
          </u-table-column>-->
<!--          <u-table-column prop="operator" label="操作人" min-width="100" :show-overflow-tooltip="true"></u-table-column>-->
          <u-table-column prop="operatorTime" label="操作时间" width="160" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <p>{{ $moment(scope.row.operatorTime).format('YYYY-MM-DD HH:mm:ss') }}</p>
            </template>
          </u-table-column>
          <u-table-column label="操作" align="center" width="100" fixed="right">
            <template slot-scope="scope">
              <div>
                <el-button type="text" size="small" @click="goAddOrEdit(scope.row.id)">编辑</el-button>
              </div>
            </template>
          </u-table-column>

          <template slot="pagination-btns">
            <div class="display-flex">
<!--              v-if="$global.hasPermission('curInsuranceExport')" -->
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

import dTable from '../../../components/common/table'
import addrSelector from '../../../components/common/addrSelector'
export default {
  components: { dTable, addrSelector },
  data () {
    return {
      pathData: [],
      search: {
        keyword: '',
        addrName: '',
        addrId: '',
        isLive: [], // 是否上线 0 否 1是
        businessType: [], // 业务类型 1社保 2公积金
        declareType: [] // 1：增员，2：减员，3：调基，5：补缴
      },
      options: {
        addrArr: []
      }
    }
  },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 320 + 'px'
    }
  },
  watch: {

  },
  created () {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = this.$global.deepcopyArray(
        tabs[this.$route.meta.parentPath]
      )
    }
    this.pathData.push({ name: '业务场景提示' })
    this.getAddrArr() // 获取参保地
  },
  mounted () {
  },
  methods: {
    // 获取参保地
    getAddrArr () {
      let that = this
      this.$http({
        url: '/policy/declareAccount/availableAddress',
        method: 'post'
      }).then(({ data }) => {
        that.options.addrArr = data.data
      }).catch((data) => {
      })
    },
    doSearch () {
      this.$nextTick(() => {
        this.getTableData()
      })
    },
    getTableData () {
      var params = [
        { property: 'keyword', value: this.search.keyword },
        { property: 'addrId', value: this.search.addrId },
        { property: 'isLive', value: this.search.isLive },
        { property: 'businessType', value: this.search.businessType },
        { property: 'declareType', value: this.search.declareType }
      ]
      this.$refs.table.fetch({
        query: params,
        method: 'post',
        url: '/policy/businessWarn/queryBusinessWarn'
      })
    },

    // 改变社保-参保地
    getAddrId (item) {
      if (this.search.addrId == item.id) {
        return
      }
      this.search.addrId = item.id
    },
    // 导出表格
    exportTableData () {
      var params = this.$refs.table.getParamsQuery()
      this.$downloadFile(
        '/policy/businessWarn/downloadBusinessWarn',
        'post',
        {
          query: params
        },
        this.$global.EXCEL
      )
    },
    goAddOrEdit (id) {
      let query = ''
      if (id) {
        query = '?id=' + id
      }
      this.$router.push('/businessSceneTip/add' + query)
    }
  }
}
</script>
<style lang="less" scoped>
/deep/.checkbox-group{
  .el-checkbox-button .el-checkbox-button__inner{
    width: 100px;
  }
}
</style>
