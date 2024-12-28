<template>
  <div class="spl-container">
    <!-- 监控面板 -->
    <breadcrumb :data="pathData"></breadcrumb>
    <div v-show="showIndex == 0" style="padding: 0px 20px 0 20px">
      <div class="search-row">
        <el-row type="flex">
          <el-col :span="8" class="display-flex">
            <span class="label">处理状态：</span>
            <div style="display: flex; align-items: center; padding-right: 0px">
              <el-radio-group size="medium" v-model="searchData.handleStatus">
                <el-radio-button v-for="(item, index) in statusArr" :key="index" :label="item.id" class="radio-button">
                  <div class="tabs-sty-all">
                    {{ item.name }}
                  </div>
                </el-radio-button>
              </el-radio-group>
            </div>
          </el-col>
          <el-col :span="8" class="display-flex">
            <span class="label">上报时间：</span>
            <div class="search-row-item display-flex">
              <el-date-picker v-model="searchData.reportStartTime" type="date" value-format="yyyy-MM-dd"
                              placeholder="开始时间"
                              style="flex: 1"></el-date-picker>
              <span class="lh-com" style="margin: 0 5px">至</span>
              <el-date-picker v-model="searchData.reportEndTime" type="date" value-format="yyyy-MM-dd" placeholder="结束时间"
                              style="flex: 1"></el-date-picker>
            </div>

          </el-col>
          <el-col :span="8">
            <el-input v-model="searchData.keyword" placeholder="请输入地区/业务流程/申报账户关键字"
                      class="search-row-item" clearable></el-input>
          </el-col>
        </el-row>
        <el-row class="mt-10" type="flex">
          <el-col :span="8" class="display-flex">
            <span class="label">预警项目：</span>
            <el-select v-model="searchData.warnTypeCode" class="search-row-item" clearable filterable placeholder="请选择">
              <el-option v-for="item in warnTypeList" :key="item.id" :label="item.dictName" :value="item.dictCode">
              </el-option>
            </el-select>
          </el-col>
          <el-col :span="8" class="display-flex">
            <span class="label">客户名称：</span>
            <el-select v-model="searchData.clientId" class="search-row-item" clearable filterable placeholder="请选择">
              <el-option v-for="item in listCustomerOption" :key="item.id" :label="item.customerName"
                         :value="item.id"></el-option>
            </el-select>
          </el-col>
          <el-col :span="8" class="text-right">
            <el-button class="w-60" size="small" type="primary" @click="getTableData(true)">查询</el-button>
          </el-col>

        </el-row>
      </div>
      <!-- 表格 -->
      <dTable @fetch="getTableData" ref="dTable" :tableHeight="tableHeight" :paging="true" :showSelection="true"
              :showIndex="true" rowKey="id">
        <u-table-column prop="warnType" label="预警项目" min-width="130" :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="errorResult" label="异常原因" min-width="160"
                        :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="warnCount" label="预警次数" min-width="80"
                        :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="accountNumber" label="申报账户" min-width="180"
                        :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="flowName" label="业务流程" min-width="130"
                        :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <span>{{handleFlowName(scope.row.serviceName, scope.row.serviceItemName, scope.row.flowName)}}</span>
          </template>
        </u-table-column>
        <u-table-column prop="addrName" label="参保城市" min-width="80" :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="machineCode" label="设备编号" min-width="140"
                        :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="clientName" label="客户名称" min-width="140"
                        :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="reportTime" label="上报时间" min-width="100"
                        :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="handleStatusName" label="处理状态" min-width="80"
                        :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="handleTypeName" label="原因归类" min-width="150"
                        :show-overflow-tooltip="true"></u-table-column>
        <u-table-column label="操作" align="left" fixed="right" width="160">
          <template slot-scope="scope">
            <span class="text-blue a-aim f-cursor" @click="checkDetail(scope.row)">查看异常</span>
            <!--【RPA执行异常-10017001、网站升级或异常-10017002】项目才出现此按钮-->
            <span class="text-blue a-aim f-cursor ml-20" v-if="scope.row.warnCode == '10017001' || scope.row.warnCode == '10017002'" @click="handleNotifyCustomer(scope.row)">通知客户</span>
          </template>
        </u-table-column>
        <template slot="pagination-btns">
          <div class="display-flex">
            <el-button size="small" icon="icon ic-file-download" class="btn--border-blue s-btn" @click="downloadError">
              下载异常
            </el-button>
            <el-button v-show="showFinishBtn" class="ml-15 btn--border-blue s-btn" size="small" icon="el-icon-s-claim text-blue"
                       @click="doBusiness">办结
            </el-button>
          </div>
        </template>
      </dTable>
    </div>
    <!-- 办结drawer -->
    <el-drawer title="办结" :visible.sync="finishDrawer.show" :wrapperClosable="false" custom-class="spl-filter-drawer"
               :before-close="cancelFinishDrawer" :close-on-press-escape="false">
      <div class="formDrawer-content">
        <p class="mb-5">已选择{{ selectRows.length }}条数据</p>
        <el-form :model="finishDrawer" ref="finishDrawer" label-width="0">
          <el-form-item label="" prop="handleType"
                        :rules="[{ required: true, message: '请选择原因归类', trigger: ['change'] }]" style="margin-bottom: 0px;">
            <p class="item-lab required-pre">原因归类</p>
            <el-select placeholder="请选择" v-model="finishDrawer.handleType" clearable filterable class="w-p100">
              <el-option v-for="item in handleTypeList" :key="item.dictCode" :label="item.dictName"
                         :value="item.dictCode"></el-option>
            </el-select>
            <p class="text-606 text-right" style="line-height: normal">{{ handleTypeComment() }}</p>
          </el-form-item>
          <el-form-item label="" prop="handleLink" style="margin-bottom: 10px;">
            <p class="item-lab  clearfix">补充说明</p>
            <el-input v-model.trim="finishDrawer.handleRemark" type="textarea" :autosize="{ minRows: 4, maxRows: 46 }"
                      placeholder="请输入具体原因，及处置方式" clearable></el-input>
          </el-form-item>
          <el-form-item label="" prop="handleLink">
            <p class="item-lab clearfix">关联链接</p>
            <el-input v-model.trim="finishDrawer.handleLink" placeholder="请输入对应的jira号" clearable></el-input>
          </el-form-item>

        </el-form>
      </div>

      <div class="drawer-footer-buts">
        <el-button class="btn--border-blue s-btn" @click="cancelFinishDrawer">取消</el-button>
        <el-button type="primary" class="s-btn ml-12" @click="confirmFinishDrawer">确定办结</el-button>
      </div>
    </el-drawer>
    <!-- 查看异常 -->
    <el-drawer title="查看异常" :visible.sync="detailDrawer.show" :wrapperClosable="false"
               :custom-class="'spl-filter-drawer '+(currentRow.detailMoVO?'detail-drawer-max':'detail-drawer-min')">
      <div class="pt-20 pl-20 pr-20" v-if="currentRow">
        <div v-if="currentRow.detailMoVO" class="display-flex mb-20">
          <div v-if="currentRow.detailMoVO.files.length>0" class="flex1 mr-20" style="overflow: auto">
            <img v-for="(item, index) in currentRow.detailMoVO.files" :key="index" :src="item.fileFullPath" alt="" width="100%">
          </div>
          <div :style="{'width': currentRow.detailMoVO.files.length>0?'30%':'100%','min-width': '300px','word-break': 'break-all'}">
            <div class="display-flex">
              <div class="flex1">
                <span class="text-blue mr-20 mb-5 inlineBlock">{{currentRow.detailMoVO.stepName}}</span>
                <span class="mb-5" style="display: inline-block;width: 150px">{{currentRow.detailMoVO.startTime}}</span>
              </div>
              <div class="ml-20">
                <el-button class="btn--border-blue" style="vertical-align: middle;margin-top: -5px;" size="mini" @click="handleRunError">定位执行记录</el-button>
              </div>
            </div>
            <div class="text-red mt-5" style="">原因: {{currentRow.detailMoVO.error}}</div>
          </div>
        </div>
        <p class="fw-blod pb-15">预警内容</p>
        <el-row type="flex" style="flex-wrap:wrap;padding-left: 15px;">
          <el-col :span="8">
            <div class="item-row"><span class="lab">预警项目：</span><span class="sel">{{ currentRow.warnType }}</span></div>
          </el-col>
          <el-col :span="8">
            <div class="item-row"><span class="lab">上报时间：</span><span class="sel">{{ currentRow.reportTime }}</span></div>
          </el-col>
          <el-col :span="8">
            <div class="item-row"><span class="lab">客户名称：</span><span class="sel">{{ currentRow.clientName }}</span></div>
          </el-col>
          <el-col :span="8">
            <div class="item-row"><span class="lab">设备编号：</span><span class="sel">{{ currentRow.machineCode }}</span></div>
          </el-col>
          <el-col :span="8">
            <div class="item-row"><span class="lab">参保城市：</span><span class="sel">{{ currentRow.addrName }}</span></div>
          </el-col>
          <el-col :span="8">
            <div class="item-row"><span class="lab">业务：</span><span class="sel">{{handleFlowName(currentRow.serviceName, currentRow.serviceItemName, currentRow.flowName)}}</span></div>
          </el-col>
          <el-col :span="24">
            <div class="item-row"><span class="lab">申报账户：</span><span class="sel">{{ currentRow.accountNumber }}</span></div>
          </el-col>
        </el-row>

        <p class="fw-blod pb-15 pt-10">处理状态</p>
        <el-row type="flex" style="flex-wrap:wrap;padding-left: 15px;">
          <el-col :span="8">
            <div class="item-row"><span class="lab">处理状态：</span><span class="sel">{{ currentRow.handleStatusName }}</span></div>
          </el-col>
          <el-col :span="16">
            <div class="item-row"><span class="lab">办结时间：</span><span class="sel">{{ currentRow.handleDate }}</span></div>
          </el-col>
          <el-col :span="8">
            <div class="item-row"><span class="lab">原因归类：</span><span class="sel">{{ currentRow.handleTypeName }}</span></div>
          </el-col>
          <el-col :span="16">
            <div class="item-row"><span class="lab">补充说明：</span><span class="sel">{{ currentRow.handleRemark }}</span></div>
          </el-col>
          <el-col :span="24">
            <div class="item-row"><span class="lab">关联链接：</span><span class="sel">{{ currentRow.handleLink }}</span></div>
          </el-col>
        </el-row>
      </div>
      <div class="drawer-footer-buts">
        <!--编辑： 待提交，待申报，申报失败数据显示-->
        <el-button size="small" class="w-80" type="primary" @click="detailDrawer.show = false">关闭</el-button>
      </div>
    </el-drawer>

    <!--通知客户-->
    <NotifyCustomer ref="notifyCustomer" @refreshTable="getTableData"></NotifyCustomer>
    <!--  执行记录  -->
    <ExecuteQuery :rowData="executeQueryData" v-if="showIndex == 1" @back="toIndex" />
  </div>
</template>
<script>
import dTable from '@/components/common/table'
import NotifyCustomer from './component/NotifyCustomer'
import ExecuteQuery from '@/views/customerAppList/components/executeQuery.vue'

export default {
  name: '',
  components: { ExecuteQuery, dTable, NotifyCustomer },
  props: [''],
  data () {
    return {
      pathData: [],
      searchData: {
        handleStatus: '0', // 处理状态
        reportStartTime: '', // 上报开始时间
        reportEndTime: '', // 上报结束时间,
        keyword: '', // 关键字
        warnTypeCode: '', // 预警项目
        clientId: null // 客户id
      },
      listCustomerOption: [], // 客户列表
      warnTypeList: [], // 预警项目list
      statusArr: [
        { id: '0', name: '未处理' },
        { id: '1', name: '已办结' },
        { id: '', name: '全部' }
      ],
      finishDrawer: { // 办结
        show: false,
        handleType: '', // 原因归类
        handleRemark: '', // 补充说明
        handleRLink: '' // 关联链接
      },
      handleTypeList: [], // 原因归类列表
      detailDrawer: {
        show: false
      },
      currentRow: {}, // 当前点击的row
      selectRows: [], // 当前选择的集合
      showFinishBtn: false, // 是否显示办结按钮(状态选择【未处理】，查询出数据时，可见此按钮)
      executeQueryData: null,
      showIndex: 0
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
    let curDate = this.$moment().format('YYYY-MM-DD')
    this.searchData.reportStartTime = curDate
    this.searchData.reportEndTime = curDate
    this.getWarnTypeList()
    this.getListCustomer()
    this.getHandleTypeList()
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
      if (searchData.reportStartTime && searchData.reportEndTime && new Date(searchData.reportStartTime) > new Date(searchData.reportEndTime)) {
        this.$message.warning('上报开始时间不能大于结束时间')
        return
      }
      let params = [
        { property: 'handleStatus', value: searchData.handleStatus },
        { property: 'reportStartTime', value: searchData.reportStartTime },
        { property: 'reportEndTime', value: searchData.reportEndTime },
        { property: 'keyword', value: searchData.keyword },
        { property: 'warnTypeCode', value: searchData.warnTypeCode },
        { property: 'clientId', value: searchData.clientId === '' ? null : searchData.clientId }
      ]
      this.$refs.dTable.fetch({
        query: params,
        method: 'post',
        url: '/robot/operation/operationPage',
        callback: (res) => {
          this.showFinishBtn = searchData.handleStatus === '0' && res.records > 0
        }
      })
    },
    getListCustomer () {
      this.$http({
        url: '/robot//operation/operationListCustomer',
        method: 'post'
      }).then((data) => {
        this.listCustomerOption = data.data.data
      }).catch(() => {
      })
    },
    // 获取预警项目列表
    getWarnTypeList () {
      this.$http({
        url: '/robot/data/dict/10017',
        method: 'post',
        data: {}
      }).then((data) => {
        this.warnTypeList = data.data.data
      })
        .catch(() => {
        })
    },
    // 获取原因归类
    getHandleTypeList () {
      this.$http({
        url: '/robot/data/dict/10016',
        method: 'post',
        data: {}
      }).then((data) => {
        this.handleTypeList = data.data.data
      }).catch(() => {
      })
    },
    // 下载异常
    downloadError () {
      let params = this.$refs.dTable.getParamsQuery()
      this.$downloadFile(
        '/robot/operation/export',
        'post',
        {
          query: params
        },
        this.$global.EXCEL
      )
    },
    // 业务流程-显示
    handleFlowName (name1, name2, name3) {
      let arr = []
      if (name1) {
        arr.push(name1)
      }
      if (name2) {
        arr.push(name2)
      }
      if (name3) {
        arr.push(name3)
      }
      return arr.join('-')
    },
    // 办结-原因备注
    handleTypeComment () {
      let handleType = this.finishDrawer.handleType
      if (!handleType) {
        return ''
      }
      for (let i = 0; i < this.handleTypeList.length; i++) {
        if (this.handleTypeList[i].dictCode === handleType) {
          return this.handleTypeList[i].comment
        }
      }
      return ''
    },
    // 办结
    doBusiness () {
      // 判断当前选择的数据项 必须先选择一项数据
      this.selectRows = this.$refs.dTable.selections
      if (!this.selectRows || this.selectRows.length === 0) {
        this.$message.warning('请先选择需要办结的数据！')
        return
      }
      this.finishDrawer.show = true
    },
    // drawer取消
    cancelFinishDrawer () {
      this.finishDrawer = this.$options.data().finishDrawer
      this.$refs.finishDrawer.resetFields()
    },
    // drawer确认 - 提交办结业务
    confirmFinishDrawer () {
      this.$refs.finishDrawer.validate((valid) => {
        if (valid) {
          this.$global.showLoading('请稍候...')
          this.$http({
            url: '/robot/operation/finish',
            method: 'post',
            data: {
              detailVOList: this.selectRows,
              handleType: this.finishDrawer.handleType,
              handleRemark: this.finishDrawer.handleRemark,
              handleLink: this.finishDrawer.handleLink
            }
          }).then((data) => {
            this.cancelFinishDrawer()
            this.$global.closeLoading()
            this.$message.success('操作成功')
            this.getTableData()
          }).catch(() => {
            this.$global.closeLoading()
          })
        }
      })
    },
    // 查看异常
    checkDetail (row) {
      this.currentRow = row
      this.detailDrawer.show = true
    },
    //   通知客户
    handleNotifyCustomer (row) {
      this.$refs.notifyCustomer.init(row)
    },
    toIndex (index) {
      this.showIndex = index !== undefined ? index : 0
    },
    handleRunError () {
      let row = this.currentRow
      this.executeQueryData = {
        appCode: row.appCode,
        taskCode: row.taskCode,
        clientId: row.clientId,
        execStartTime: this.$moment(row.detailMoVO.startTime).format('YYYY-MM-DD'),
        execEndTime: this.$moment(row.detailMoVO.endTime).format('YYYY-MM-DD'),
        locating: true, // 需要定位到最新的失败记录
        filterExecutionCode: row.detailMoVO.executionCode // 需要定位到最新的失败记录
      }
      this.detailDrawer.show = false
      this.showIndex = 1
    }
  }
}
</script>
<style lang="less" scoped>
.search-row-item {
  width: 80%;
  max-width: 350px;
}
.formDrawer-content {
  padding: 15px 30px 30px 30px;
  position: relative;
  min-height: 100%;
}

/deep/ .detail-drawer-max {
  width: 95% !important;
  min-width: 1000px !important;
}
/deep/ .detail-drawer-min {
  width: 80% !important;
  max-width: 1000px !important;
}

/deep/ .el-icon-s-claim {
  font-size: 18px !important;
  vertical-align: middle;
  margin-top: -3px;
}
.item-row{
  display: flex;
  margin-bottom: 20px;
  .lab{
    width: 70px;
    text-align: right;
  }
  .sel{
    flex: 1;
    padding-right: 10px;
    word-break: break-all;
  }
}
</style>
