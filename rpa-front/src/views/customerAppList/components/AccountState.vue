<template>
  <div class="spl-container">
    <div v-show="showIndex===0">
      <breadcrumb :data="pathData" :cust-back="true" @back="toBack" :cust-link="true" @toPath="toBack">
      </breadcrumb>

      <div style="padding: 10px 20px 20px 20px;">
        <div v-if="detailData">
          <div class="top">
            <img src="@/assets/images/robot-head.png" alt="" width="70px" height="70px" class="mt-15"/>
            <div class="ml-10" style="max-width: 400px">
              <p class="row">{{ detailData.customerName }}</p>
              <p class="row">
              <span style="display: inline-block;padding-top: 3px;">
                {{ detailData.machineCode }}
                <span v-if="detailData.machineFactory">（{{ detailData.machineFactory }}）</span>
              </span>
                <span v-if="detailData.status" class="tag ml-10" :class="{'tag-red': accountStateData.machineStatusTag.red.indexOf(detailData.status)>-1 ,
                 'tag-blue': accountStateData.machineStatusTag.blue.indexOf(detailData.status)>-1}">{{
                    detailData.status
                  }}</span>
              </p>
              <p class="row">共{{ detailData.accountNumber }}个账户</p>
            </div>
            <div class="top-r">
              <div class="line"></div>
              <div class="pl-70">
                <p class="row"><span class="lab">增员申报期：</span>{{ handleDeclarePolicy(1) }}</p>
                <p class="row"><span class="lab">减员申报期：</span>{{ handleDeclarePolicy(2) }}</p>
                <p class="row"><span class="lab">补缴申报期：</span>{{ handleDeclarePolicy(5) }}</p>
              </div>
              <div class="pl-70">
                <p class="row"><span class="lab">调基申报期：</span>{{ handleDeclarePolicy(3) }}</p>
                <div class="row">
                  <span class="lab">缴费计划：</span>
                  <div class="sel"
                       v-if="declarePolicyData && declarePolicyData.policyAddrCostSettingList && declarePolicyData.policyAddrCostSettingList.length>0">
                    <p style="margin-bottom: 3px;" v-for="(item, index) in declarePolicyData.policyAddrCostSettingList"
                       :key="index" v-html="handleCostSet(item)"></p>
                  </div>
                  <div v-else>-</div>
                </div>
                <p class="row"></p>
              </div>
            </div>
          </div>

          <div class="middle">
            <span class="name">{{ detailData.appName }}</span>
            <span class="info">正在执行：{{ detailData.runningCount }}</span>
            <span class="info">已启用：{{ detailData.startCount }}</span>
            <span class="info">已停用：{{ detailData.stopCount }}</span>
            <span class="info">应用主流程数：{{ detailData.flowCount }}</span>

            <div class="middle-r">
              <el-button class="refresh-btn mr-5" icon="el-icon-refresh-right ic-refresh" title="刷新页面"
                         @click="doFresh"></el-button>
              <el-input placeholder="请输入申报单位/申报账户关键字搜索" v-model.trim="searchText" clearable
                        @keyup.enter.native="getTableData" @clear="getTableData"
                        style="display: inline-block;width: 300px;">
                <i @click="getTableData" slot="suffix" class="el-icon-search f-cursor font-16 fw-blod text-gray mr-5"
                   style="color: #dbdbdb;vertical-align: middle"></i></el-input>
            </div>
          </div>
        </div>

        <dTable :data="tableData" v-loading="tableLoading" :showIndex="false" :showSelection="false" :paging="false"
                :cancelMinHeight="true">
          <u-table-column prop="createTime" label="执行状态" width="95" :show-overflow-tooltip="true">
            <template slot-scope="scope">
           <span v-if="scope.row.taskStatus" class="tag" :class="{'tag-red': scope.row.taskStatus==='已停用',
                 'tag-blue': scope.row.taskStatus==='已启用'}">{{ scope.row.taskStatus }}</span>
            </template>
          </u-table-column>
          <u-table-column prop="orgName" label="申报单位" min-width="150" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="accountNumber" :label="accountStateData.businessType===1?'单位社保号':'单位公积金号'"
                          min-width="150" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="declaredCount" label="本月已完成" width="100">
            <template slot-scope="scope">
              <div style="position: relative">
                <span> {{ scope.row.declaredCount===0?'-':scope.row.declaredCount }}</span>
                <el-tooltip effect="dark" content="今日已完成" placement="top">
                  <span class="todayDeclaredCount text-green" >+{{scope.row.todayDeclaredCount}}</span>
                </el-tooltip>
              </div>
            </template>
          </u-table-column>
          <u-table-column prop="noDeclaredCount" label="本月未完成" min-width="90"
                          :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <span v-if="scope.row.noDeclaredCount===0 && scope.row.auditCount===0">-</span>
              <el-tooltip v-else effect="dark" content="本月未完成/网站审核中" placement="top">
                <span class="text-red">{{ scope.row.noDeclaredCount + '/' +  scope.row.auditCount}}</span>
              </el-tooltip>
            </template>
          </u-table-column>
          <u-table-column prop="execPlant" label="执行计划" min-width="220" :show-overflow-tooltip="true">
            <template slot-scope="scope">
            <span v-if="scope.row.haveCustom==='是'" class="ic-cust" title="自定义"><i class="el-icon-star-on font-12"
                                                                                       style="color: #ffffff"></i></span>
              <!--            <span v-else class="ic-cust ic-cust-no"></span>-->
              <span class="text-hover-blue" @click="handleExecutionPlan(scope.row)">{{ scope.row.execPlant }}</span>
            </template>
          </u-table-column>
          <u-table-column prop="taskStartCount" label="主流程数在服/启用" min-width="100" :show-overflow-tooltip="true">
            <template slot="header" slot-scope="scope">
              <p>主流程数</p>
              <p>在服/启用</p>
            </template>
            <template slot-scope="scope">
              {{ scope.row.flowCount }} / {{scope.row.taskStartCount}}
            </template>
          </u-table-column>
          <u-table-column prop="execCount" label="今日执行次数" min-width="110"
                          :show-overflow-tooltip="true"></u-table-column>
          <u-table-column v-if="accountStateData.businessType===2" prop="usbPort" label="USB端口" min-width="80"
                          :show-overflow-tooltip="true">
            <template slot-scope="scope">
              {{ scope.row.usbPort == null || scope.row.usbPort === '' ? '-' : scope.row.usbPort }}
            </template>
          </u-table-column>
          <u-table-column prop="comment" label="原因/备注" min-width="200"
                          :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <span v-if="scope.row.comment">{{scope.row.editName}}-{{scope.row.comment}}</span>
            </template>
          </u-table-column>
          <u-table-column label="操作" align="left" fixed="right" width="200">
            <template slot-scope="scope">
              <div class="jkh-flex">
<!--                <el-button type="text" size="small" class="text-blue"
                           @click="handleAllRun(scope.row)">全执行
                </el-button>-->
                <el-button type="text" size="small" class="text-blue"
                           @click="handleSingleRun(scope.row)">单独执行
                </el-button>
                <el-button type="text" size="small" class="text-blue"
                           @click="handleExecutionQuery(scope.row)">查记录
                </el-button>
                <el-button v-if="scope.row.status===0" type="text" size="small" class="text-blue ml-10"
                           @click="handleTaskFlowStatus(scope.row, 1)">启用
                </el-button>
                <span class="text-gray font-12 f-cursor ml-10" @click="handleTaskFlowStatus(scope.row, 0)">停用</span>
              </div>
            </template>
          </u-table-column>
        </dTable>

      </div>

      <!--单独执行-->
      <el-drawer title="单独执行" :visible.sync="singleRun.show" :wrapperClosable="false"
                 custom-class="spl-filter-drawer singleRun-drawer"
                 :show-close="true">
        <singleRun ref="singleRun" @handleExecutionQuery="handleExecutionQuery"></singleRun>
      </el-drawer>

      <!--    停用\启用账号-->
      <updateStatusDialog ref="updateStatusDialog" @refreshTable="refreshTable"></updateStatusDialog>
    </div>

    <!--    执行查询（查记录）-->
    <ExecuteQuery :rowData="executeQueryData" v-if="showIndex===1" @back="toIndex" />

    <ExecutionPlan v-if="showIndex == 2" :infoData="executionPlanData" @to-index="toIndex" @refreshTable="getTableData"/>

  </div>
</template>
<script>
import dTable from '../../../components/common/table'
import singleRun from './singleRun'
import ExecuteQuery from '@/views/customerAppList/components/executeQuery.vue'
import ExecutionPlan from '../executionPlan'
import updateStatusDialog from './updateStatusDialog'
import { parseDeclarePolicy, parsePaymentPlan } from '@/utils/socialAccfundProduct'

export default {
  name: 'AccountState',
  components: { ExecuteQuery, dTable, singleRun, ExecutionPlan, updateStatusDialog },
  props: ['accountStateData'],
  data () {
    return {
      pathData: [],
      searchText: '',
      detailData: null,
      declarePolicyData: null,
      tableData: [],
      tableLoading: false,
      showIndex: 0, // 0-主页面  1-执行查询页面 2-执行计划
      isSingRunShowType: false,
      executeQueryData: null,
      executionPlanData: null,
      singleRun: {
        show: false
      }
    }
  },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 195 + 'px'
    }
  },
  watch: {},
  created () {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = this.$global.deepcopyArray(
        tabs[this.$route.meta.parentPath]
      )
    }
    this.pathData.push({ name: '账户情况' })
    // this.searchText = this.accountStateData.accNumber
    this.init()
  },
  mounted () {
  },
  methods: {
    init () {
      this.getRobotClientInfo()
      this.declarePolicyQuery()
      this.getTableData()
    },
    doFresh () {
      this.searchText = ''
      this.init()
    },
    getRobotClientInfo () {
      let obj = this.accountStateData
      this.$http({
        url: `/robot/app/client/getRobotClientInfo/${obj.clientId}/${obj.machineCode}/${obj.appCode}`,
        method: 'post'
      }).then((data) => {
        if (data.data.code == 200) {
          this.detailData = data.data.data
        }
      }).catch(() => {
      })
    },
    declarePolicyQuery () {
      let obj = this.accountStateData
      this.$http({
        url: `/policy/declareAddr/declarePolicyQuery`,
        method: 'post',
        data: {
          addrId: Number(obj.addrId),
          businessType: Number(obj.businessType)
        }
      }).then((data) => {
        if (data.data.code == 200) {
          this.declarePolicyData = data.data.data
        }
      }).catch(() => {
      })
    },
    getTableData () {
      this.tableLoading = true
      let obj = this.accountStateData
      this.$http({
        url: `/robot/app/client/getRobotTasks/${obj.clientId}/${obj.machineCode}/${obj.appCode}?searchText=${this.searchText}`,
        method: 'post'
      }).then((data) => {
        this.tableLoading = false
        if (data.data.code == 200) {
          this.tableData = data.data.data
        }
      }).catch(() => {
        this.tableLoading = false
      })
    },
    handleDeclarePolicy (declareType) {
      if (!this.declarePolicyData) {
        return '-'
      }
      let list = this.declarePolicyData.policyAddrDeadlineSettingList ? this.declarePolicyData.policyAddrDeadlineSettingList : []
      return parseDeclarePolicy(list, declareType)
    },
    handleCostSet (item) {
      return parsePaymentPlan(item)
    },
    toBack () {
      this.$emit('to-index')
    },

    // 单独执行
    handleSingleRun (row) {
      let obj = {
        customerName: this.detailData.customerName,
        appName: this.detailData.appName,
        businessType: this.accountStateData.businessType,
        accountNumber: row.accountNumber,
        orgName: row.orgName,
        taskCode: row.taskCode,
        appCode: row.appCode,
        taskStatus: row.taskStatus,
        taskComment: row.comment
      }
      this.singleRun.show = true
      this.$nextTick(() => {
        this.$refs.singleRun.init(obj)
      })
    },

    //   全执行
    handleAllRun (row) {
      let that = this
      let text = '是否确定执行此流程？'
      if (row.taskStatus === '已停用') {
        text = '当前账户停用，原因：' + (row.comment ? row.comment : '') + '，是否继续？'
      }
      this.$confirm(text, '提示', {
        confirmButtonText: '继续',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning'
      }).then(() => {
        that.doAllRun(row, true)
      }).catch(() => {
      })
    },
    doAllRun (row, checkSchedule) {
      let that = this
      that.$http({
        url: `/robot/app/client/runTask/${row.appCode}/${row.taskCode}/${row.machineCode}?flowCodes=&serviceItemName=&checkSchedule=${checkSchedule}`,
        method: 'post',
        headers: {
          customSuccess: true
        }
      }).then((data) => {
        if (data.data.code === 200) {
          that.$message.success('操作成功')
          that.getTableData()
        } else if (data.data.code === 500 && data.data.data === '该任务不在执行计划期内') {
          that.$confirm('该任务不在执行计划期内，确定执行？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            showClose: false,
            customClass: 'pal-confirm',
            type: 'warning'
          }).then(() => {
            that.doAllRun(row, false)
          }).catch(() => {
          })
        } else {
          that.$message.error('操作失败')
        }
      }).catch(() => {
      })
    },

    //   停用、启用
    handleTaskFlowStatus (row, updateStatus) {
      this.$refs.updateStatusDialog.init(row, updateStatus)
    },

    toIndex () {
      this.showIndex = 0
      if (this.isSingRunShowType) {
        this.singleRun.show = true
      }
    },

    handleExecutionQuery (row, type) {
      // 传参【客户，应用，账户】，跳转执行查询页
      this.executeQueryData = {
        clientId: this.accountStateData.clientId,
        appCode: row.appCode,
        taskCode: row.taskCode
      }
      this.singleRun.show = false
      this.showIndex = 1
      this.isSingRunShowType = type === 'singleRun'
    },

    // 执行计划
    handleExecutionPlan (row) {
      this.executionPlanData = {
        appCode: row.appCode,
        clientId: this.accountStateData.clientId,
        machineCode: row.machineCode,
        accNumber: row.accountNumber,
        businessType: this.accountStateData.businessType,
        isSearchAccNumber: true
      }
      this.showIndex = 2
    },

    refreshTable () {
      this.getTableData()
      this.$emit('refreshTable')
    }
  }
}
</script>
<style lang="less" scoped>
.top {
  display: flex;

  .top-r {
    flex: 1;
    display: flex;
    position: relative;
    margin-left: 50px;
  }

  .line {
    position: absolute;
    border-left: 1px solid #dbdbdb;
    height: 50%;
    left: 0;
    top: 20%;
  }

  .row {
    margin: 10px 0;
    display: flex;

    .lab {
      display: inline-block;
      width: 100px;
      text-align: right;
    }

    .sel {
      flex: 1;
    }
  }
}

.middle {
  padding: 5px 10px;
  background-color: #EEF0F7;
  display: flex;
  align-items: center;
  margin-top: 10px;
  margin-bottom: 15px;

  .name {
    font-weight: bold;
    font-size: 16px;
    margin-right: 20px;
  }

  .info {
    margin: 0 20px;
  }

  .middle-r {
    text-align: right;
    flex: 1;
    display: flex;
    justify-content: flex-end;
  }

  .refresh-btn {
    padding: 6px 8px;
  }

  /deep/ .ic-refresh {
    font-weight: bold;
    font-size: 18px;
    color: @blueColor;
  }
}

.tag {
  display: inline-block;
  margin-right: 5px;
  text-align: center;
  font-size: 12px;
  color: #ffffff;
  background-color: #008000;
  min-width: 70px;
  height: 24px;
  line-height: 24px;
  border-radius: 30px;
}

.tag-red {
  background-color: #fde2e2;
  color: #CC0000;
}

.tag-blue {
  background-color: #d9ecff;
  color: @blueColor;
}

.ic-cust {
  display: inline-block;
  width: 16px;
  height: 16px;
  line-height: 14px;
  border-radius: 50%;
  text-align: center;
  background: @blueColor;
  vertical-align: middle;
  margin-top: -3px;
}

.ic-cust-no {
  background: none;
}
.todayDeclaredCount{
  position: absolute;
  right: 0;
  top: -5px;
  z-index: 2;
}
.text-hover-blue:hover{
  color: @blueColor;
  text-decoration: underline;
  cursor: pointer;
}
/deep/ .singleRun-drawer {
  width: 800px !important;
}
</style>
