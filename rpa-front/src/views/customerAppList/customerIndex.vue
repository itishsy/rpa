<template>
  <!-- 客户应用列表  -->
  <div class="" :class="{'spl-container': !isComponent}">
    <div v-show="isShowIndex == 0">
      <breadcrumb :data="pathData" v-if="!isComponent">
        <!-- <el-button  type="primary" slot="breadcrumb-btn" size="mini" @click="goAdd">新增</el-button> -->
        <div slot="breadcrumb-btn" style="display:flex;">
          <el-button v-if="$global.hasPermission('customerDeviceManagement')" size="small" @click="goDevice()">客户设备维护</el-button>
          <el-button size="small" type="primary" @click="addApplition()">新增客户应用</el-button>
        </div>
      </breadcrumb>
      <div class="pl-20 pr-20">
        <div class="search-row">
          <el-row>
            <el-col :span="8">
              <span class="lab">客户：</span>
              <el-select placeholder="请选择" filterable v-model="formData.clientId" style="width:260px;" clearable
                         @clear="clearClientId" @change="getMachineByClientId">
                <el-option v-for="(item, index) in listCustomerOption" :key="index" :label="item.customerName"
                           :value="item.id"></el-option>
              </el-select>
            </el-col>
            <el-col :span="8">
              <span class="lab">所在设备：</span>
              <el-select placeholder="请选择" filterable v-model="formData.machineCode" style="width:260px;" clearable>
                <el-option v-for="(item, index) in machineCodeList" :key="index" :label="item.machineCode"
                           :value="item.machineCode"></el-option>
              </el-select>
            </el-col>
            <el-col :span="8">
              <el-input v-model.trim="formData.keyWord" placeholder="请输入地区/应用/申报账户等关键字搜索" style="width: 300px"
                        @change="changeSearch" clearable></el-input>
            </el-col>
            <!-- <el-col :span="6">
              <div class="search-row display-flex" style="align-items: center; padding-top: 28px">
                <el-checkbox-group v-model="formData.statusList" @change="changeBussinssType">
                  <el-checkbox label="0">未登录</el-checkbox>
                  <el-checkbox label="1">启用</el-checkbox>
                  <el-checkbox label="2">停用</el-checkbox>
                </el-checkbox-group>
              </div>
            </el-col>-->
          </el-row>
          <el-row class="mt-15">
            <el-col :span="8">
              <span class="lab">账户状态：</span>
              <el-select placeholder="请选择" filterable multiple collapse-tags v-model="formData.status" style="width:260px;" clearable>
                <el-option label="已启用" value="已启用"></el-option>
                <el-option label="正在执行" value="正在执行"></el-option>
                <el-option label="已停用" value="已停用"></el-option>
              </el-select>
            </el-col>
            <el-col :span="16" class="text-right">
              <el-button size="small" type="primary" class="ml-20 w-60" @click="getTableData">查询</el-button>
            </el-col>
          </el-row>
        </div>
        <div>
          <dTable :data="tableData" v-loading="isloading" ref="table" style="margin-top: 0" :tableHeight="tableHeight"
            :showIndex="true" :showSelection="true" :paging="false" :showBottom="true" :cancelMinHeight="isComponent">
            <u-table-column prop="customerName" label="客户" min-width="150" :show-overflow-tooltip="true"></u-table-column>
            <u-table-column prop="appName" label="应用名称" min-width="150" :show-overflow-tooltip="true"></u-table-column>
            <u-table-column prop="machineCode" label="所在设备" min-width="180">
              <template slot-scope="scope">
                <el-tooltip class="item" effect="dark" :disabled="!scope.row.machineFactory" :content="'设备厂商：'+scope.row.machineFactory" placement="top">
                  <div style=" white-space: nowrap;overflow: hidden;text-overflow: ellipsis;">
                    <span v-if="scope.row.machineStatus" class="tag" :class="{'tag-red': machineStatusTag.red.indexOf(scope.row.machineStatus)>-1 ,
                 'tag-blue': machineStatusTag.blue.indexOf(scope.row.machineStatus)>-1}">{{scope.row.machineStatus}}</span>
                    {{scope.row.machineCode}}
                  </div>
                </el-tooltip>
              </template>
            </u-table-column>
            <u-table-column prop="accountNumber" label="申报账户" min-width="250">
              <template slot-scope="scope">
                <div class="display-flex">
                  <!--<el-tooltip class="item" effect="dark" :disabled="scope.row.taskStatus!=='已停用'" :content="scope.row.taskComment" placement="top">-->
                  <span v-if="scope.row.taskStatus" class="tag" :class="{'tag-red': scope.row.taskStatus==='已停用',
                 'tag-blue': scope.row.taskStatus==='已启用'}">{{scope.row.taskStatus}}</span>
                  <!--</el-tooltip>-->
                  <el-tooltip effect="dark" :content="scope.row.accountNumber" placement="top" class="flex1">
                    <div style=" white-space: nowrap;overflow: hidden;text-overflow: ellipsis;">
                      {{scope.row.accountNumber}}
                    </div>
                  </el-tooltip>
                </div>
              </template>
            </u-table-column>
            <u-table-column prop="fixMachine" label="指定此设备执行" width="120">
              <template slot-scope="scope"><span v-if="scope.row.fixMachine==1">是</span></template>
            </u-table-column>
            <u-table-column prop="fixRemark" label="指定执行原因" min-width="150" :show-overflow-tooltip="true"></u-table-column>
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
            <u-table-column prop="noDeclaredCount" label="本月未完成" min-width="100"
                            :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <span v-if="scope.row.noDeclaredCount===0 && scope.row.auditCount===0">-</span>
                <el-tooltip v-else effect="dark" content="本月未完成/网站审核中" placement="top">
                  <span class="text-red">{{ scope.row.noDeclaredCount + '/' + scope.row.auditCount}}</span>
                </el-tooltip>
              </template>
            </u-table-column>
            <u-table-column prop="taskComment" label="原因备注" min-width="200" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <div class="taskComment">
                  <img title="操作记录" src="@/assets/images/icons/ic-eye-view.png" class="ic-view" alt="" width="20px" height="14px" @click="getAccountStatusHistory(scope.row)">
                  <p class="pl-20" style="height: 100%; white-space: nowrap;overflow: hidden;text-overflow: ellipsis;">{{ scope.row.taskComment }}</p></div>
              </template>
            </u-table-column>
            <u-table-column prop="createTime" label="执行计划" width="240" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <span v-if="scope.row.haveCustom==='是'" class="ic-cust" title="自定义"><i class="el-icon-star-on font-12" style="color: #ffffff"></i></span>
<!--                <span v-else class="ic-cust ic-cust-no"></span>-->
                {{scope.row.execPlant}}
              </template>
            </u-table-column>
            <u-table-column prop="execCount" label="今日执行次数" min-width="110"
                            :show-overflow-tooltip="true"></u-table-column>
            <u-table-column label="操作" align="left" fixed="right" width="300">
              <template slot-scope="scope">
                <div class="jkh-flex">
                  <!-- <el-button
                    v-show="scope.row.status !== 0"
                    type="text"
                    size="small"
                    @click="stopUse(scope.row)"
                  >{{ scope.row.status == 1 ? '停用' : '启用' }}</el-button>
                  <div class="opt-btn-split" v-show="scope.row.status !== 0"></div>-->
<!--                  <el-button type="text" size="small" class="text-blue"-->
<!--                    @click="openApplication(scope.row)">应用概况</el-button>-->

                  <el-button v-if="scope.row.taskCode && scope.row.taskCode!==''" type="text" size="small" class="text-blue"
                             @click="handleLoginInfo(scope.row)">账户信息</el-button>
<!--                  <el-button type="text" size="small" class="text-blue"
                             @click="handleLoginInfo(scope.row)">登录信息</el-button>
                  <el-button type="text" size="small" class="text-blue"
                    @click="handleAccountState(scope.row)">账户情况</el-button>-->
                  <el-button v-if="scope.row.taskCode && scope.row.taskCode!==''" type="text" size="small" class="text-blue"
                    @click="handleExecutionPlan(scope.row)">执行控制</el-button>
                  <el-button type="text" size="small" class="text-blue"
                             @click="openEditDevice(scope.row)">设备变更</el-button>
                  <el-button type="text" size="small" class="text-blue"
                             @click="addApplition(scope.row)">编辑</el-button>
                  <el-button v-if="scope.row.taskCode && scope.row.taskCode!=='' && scope.row.taskStatus==='已停用'" type="text" size="small" class="text-blue ml-10"
                             @click="handleTaskFlowStatus(scope.row, 1)">启用
                  </el-button>
                  <span v-if="scope.row.taskCode && scope.row.taskCode!==''" class="text-gray font-12 f-cursor ml-10" @click="handleTaskFlowStatus(scope.row, 0)">停用</span>
<!--                  <el-button type="text" size="small" class="text-blue"
                    @click="handleExecutionQuery(scope.row)">查记录</el-button>-->
                </div>
              </template>
            </u-table-column>
            <template slot="pagination-btns">
              <el-button size="small" class="btn--border-blue s-btn" @click="exportCustomer" v-if="!isComponent">导出客户应用情况</el-button>
              <el-button size="small" class="btn--border-blue s-btn" @click="handleUpdateStatusBatch(1)" v-if="!isComponent">批量启用</el-button>
              <el-button size="small" class="s-btn w-80" @click="handleUpdateStatusBatch(0)" v-if="!isComponent">批量停用</el-button>
            </template>
          </dTable>
        </div>
      </div>
    </div>
<!--    新增客户应用-->
    <AddApplicationFormModel :appList="appList" :visible.sync="addVisible" :addAppData="addAppData" :listCustomerOption="listCustomerOption"
      @refresh-list="refreshList()" />

<!--    应用概况-->
<!--    <ApplicationsOfDrawer :visible.sync="showApplication" :applicationSurveyFrom="JSON.stringify(applicationSurveyFrom)"
      @to-query="toQuery()" @handleNull-newFrom="handleNullNewFrom" />-->

<!--    执行情况-->
    <ExecutionPlanAll ref="ExecutionPlanAll" @to-index="toIndex" @refreshTable="getTableData"/>
<!--    <ExecutionPlan v-if="isShowIndex == 1" :infoData="executionPlanData" @to-index="toIndex" @refreshTable="getTableData"/>-->

<!--    执行查询（查记录）-->
    <ExecuteQuery :rowData="executeQueryData" v-if="isShowIndex == 2"
      @back="toIndex" />

    <!--    账户情况-->
    <AccountState v-if="isShowIndex == 3" :accountStateData="accountStateData" @to-index="toIndex" @refreshTable="getTableData" />

<!--    设备变更-->
    <el-dialog :visible.sync="editDeviceShow" width="500px" :close-on-click-modal="false" title="设备变更">
      <el-form style="margin-top: 10px" ref="editMachineCode" label-width="100px" :model="editDeviceData" :rules="rules">
        <el-form-item prop="customerName" label="客户：">{{ editDeviceData.customerName }}</el-form-item>
        <el-form-item prop="appName" label="应用名称：">{{ editDeviceData.appName }}</el-form-item>
        <el-form-item prop="taskCode" label="申报账户：">
          <el-select v-model="editDeviceData.taskCode" placeholder="请选择" filterable clearable style="width:260px;">
            <el-option v-for="item in clientAppTaskList" :key="item.taskCode"
              :label="item.companyName + '（' + item.accountNumber + '）'" :value="item.taskCode"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop="machineCode" label="设备编号：">
          <el-select v-model="editDeviceData.machineCode" placeholder="请选择" filterable clearable style="width:260px;">
            <el-option v-for="item in machineCodeList2" :key="item.machineCode"
              :label="item.machineName + '（' + item.machineCode + '）'" :value="item.machineCode"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="cancelDevice()">取消</el-button>
        <el-button type="primary" @click="confirmDevice()">确定</el-button>
      </span>
    </el-dialog>

    <!--登录信息（登录信息+账户情况）-->
    <loginInfoDrawer ref="loginInfoDrawer" @handleAccountState="handleAccountState"></loginInfoDrawer>

    <!--操作记录-->
    <operateRecordsDrawer ref="operateRecordsDrawer"></operateRecordsDrawer>

    <!--    停用\启用账号-->
    <updateStatusDialog ref="updateStatusDialog" @refreshTable="getTableData"></updateStatusDialog>

    <!--    批量停用\启用账号-->
    <updateStatusBatch ref="updateStatusBatch" @refreshTable="getTableData"></updateStatusBatch>

  </div>
</template>
<script>
import dTable from '../../components/common/table'
// import ApplicationsOfDrawer from './components/applicationsOfDrawer.vue'
import ExecutionPlanAll from './components/ExecutionPlanAll.vue'
import ExecuteQuery from './components/executeQuery.vue'
import AddApplicationFormModel from './components/addApplicationFormModel'
import AccountState from './components/AccountState'
import loginInfoDrawer from './components/loginInfoDrawer'
import operateRecordsDrawer from './components/operateRecordsDrawer'
import updateStatusDialog from '@/views/customerAppList/components/updateStatusDialog.vue'
import updateStatusBatch from '@/views/customerAppList/components/updateStatusBatch.vue'

export default {
  components: {
    updateStatusDialog,
    loginInfoDrawer,
    dTable,
    ExecutionPlanAll,
    ExecuteQuery,
    AddApplicationFormModel,
    AccountState,
    operateRecordsDrawer,
    updateStatusBatch
  },
  props: ['configData'],
  data () {
    return {
      listCustomerOption: [],
      appList: [],
      /* searchForm: {
        execStartTime: this.$moment(new Date()).format('YYYY-MM-DD'),
        execEndTime: this.$moment(new Date()).format('YYYY-MM-DD')
      }, */
      objectVal: {},
      addVisible: false,
      addAppData: {},
      pageInfo: {
        pageSize: 20,
        pageNo: 1
      },
      tableData: [],
      tableDataBig: [],
      tableDataop: [{ name: '111' }],
      execution: false,
      client: false,
      isloading: false,
      isShowIndex: 0,
      showApplication: false,
      applicationSurveyFrom: {},
      formData: {
        statusList: ['0', '1'],
        keyWord: '',
        machineCode: '',
        clientId: null,
        status: []
      },
      pathData: [],
      appCode: '',
      clientId: '',
      machineCode: '',
      machineCodeList: [],
      machineCodeList2: [],
      clientAppTaskList: [],
      editDeviceData: {
        // 设备变更
        id: '',
        machineCode: '',
        appName: '',
        customerName: '',
        taskCode: ''
      },
      editDeviceShow: false,
      rules: {
        machineCode: [
          { required: true, message: '请选择设备编号', trigger: 'change' }
        ]
      },
      isComponent: false,
      machineStatusTag: {
        red: ['已关闭', '程序异常', '客户端异常', '升级失败', '离线'],
        blue: ['初始化', '准备就绪', '正在升级']
      },
      accountStateData: null,
      executeQueryData: null
    }
  },
  computed: {
    tableHeight: function () {
      if (this.isComponent) {
        return 0
      }
      return this.$global.bodyHeight - 310 + 'px'
    }
  },
  watch: {
    isShowIndex: function (val) {
      this.$emit('changePage', val === 0)
    },
    'configData.customerOptions': {
      handler (val) {
        if (this.isComponent) {
          this.listCustomerOption = val
        }
      },
      deep: true,
      immediate: true
    },
    'configData.maCodeOptions': {
      handler (val) {
        if (this.isComponent) {
          this.machineCodeList = val
        }
      },
      deep: true,
      immediate: true
    }
  },
  created () {
    let that = this
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = tabs[this.$route.path]
      console.log('this.tabs', this.pathData)
    }
    if (this.configData) {
      this.isComponent = true
    } else {
      that.getlistCustomer()
      this.getMachineByClientId()
      let status = this.$route.query.status
      if (status) {
        status = status.split(',')
        this.formData.status = status
      }
    }
    this.$nextTick(() => {
      that.appAll() // 应用名称接口
      that.getTableData()
      this.getDictByKey()
    })

    if (this.$route.query.goExecutionQuery === 'true') {
      // 需要跳转执行查询（从数据监控-今日执行-查看-执行查询进来）
      let obj = {}
      if (this.$route.query.clientId) {
        obj.clientId = this.$route.query.clientId
      }
      if (this.$route.query.appCode) {
        obj.appCode = this.$route.query.appCode
      }
      if (this.$route.query.taskCode) {
        obj.taskCode = this.$route.query.taskCode
      }
      if (this.$route.query.taskCode) {
        obj.taskCode = this.$route.query.taskCode
      }
      if (this.$route.query.filterFlowCodes) {
        obj.filterFlowCodes = this.$route.query.filterFlowCodes
        obj.locating = true
      }
      this.handleExecutionQuery(obj)
    } else if (this.$route.query.appCode && this.$route.query.taskCode && this.$route.query.machineCode && this.$route.query.clientId && this.$route.query.startTime && this.$route.query.endTime) {
      // 社保、公积金详情（当月参保情况）
      this.handleExecutionQuery(
        {
          appCode: this.$route.query.appCode,
          taskCode: this.$route.query.taskCode,
          machineCode: this.$route.query.machineCode,
          clientId: this.$route.query.clientId,
          startTime: this.$route.query.startTime,
          endTime: this.$route.query.endTime,
          nofilterTime: false,
          doquery: true, // 标记是从操作记录跳转进来的
          locateRecord: true // 需要定位到跳转进来的操作记录，并显示右侧明细
        }
      )
    } else if (this.$route.query.appCode && this.$route.query.taskCode && this.$route.query.machineCode && this.$route.query.clientId) {
      // yongfeng 从rpa异常进行跳转
      this.handleExecutionQuery(
        {
          appCode: this.$route.query.appCode,
          taskCode: this.$route.query.taskCode,
          machineCode: this.$route.query.machineCode,
          clientId: this.$route.query.clientId,
          nofilterTime: true,
          locating: true // 需要定位到最新的失败记录
        }
      )
      // 同时要按列表的正序 来找出最新的一条失败记录进行定位显示
    }
  },

  mounted () { },
  methods: {
    getTableData () {
      this.isloading = true
      let that = this
      /* var params1 = [
        { property: 'keyWord', value: this.formData.keyWord },
        { property: 'status', value: this.formData.status },
        { property: 'machineCode', value: this.formData.machineCode },
        { property: 'clientId', value: this.formData.clientId }
      ] */

      var params1 = {
        keyWord: this.formData.keyWord,
        status: this.formData.status,
        machineCode: this.formData.machineCode,
        clientId: this.formData.clientId
      }

      if (that.isComponent) {
        params1['appCode'] = that.configData.appCode
        // params1.push({ property: 'appCode', value: that.configData.appCode })
      }
      this.$http({
        url: '/robot/app/client/list',
        method: 'post',
        data: params1
      }).then(({ data }) => {
        if (data.code == 200) {
          this.isloading = false
          this.tableData = data.data.rows
          if (that.isComponent) {
          // 需要返回没有查询条件时，数据的总条数
            if (!that.formData.keyWord && !that.formData.machineCode && !that.formData.clientId) {
              that.$emit('tableCallback', { totalCount: res.records || 0 })
            }
          }
        }
      }).catch((data) => {
        this.isloading = false
      })

      /* this.$refs.table.fetch({
        query: params1,
        method: 'post',
        url: '/robot/app/client/list',
        callback: function (res) {
          if (that.isComponent) {
            // 需要返回没有查询条件时，数据的总条数
            if (!that.formData.keyWord && !that.formData.machineCode && !that.formData.clientId) {
              that.$emit('tableCallback', { totalCount: res.records || 0 })
            }
          }
        }
      }) */
    },
    getlistCustomer () {
      this.$http({
        url: '/robot/app/client/listCustomer',
        method: 'post'
      })
        .then((data) => {
          this.listCustomerOption = data.data.data
        })
        .catch((err) => { })
    },
    appAll () {
      this.$http({
        url: '/robot/app/all',
        method: 'post'
      })
        .then((data) => {
          this.appList = data.data.data
          // console.log('data', data)
        })
        .catch((err) => { })
    },
    /* handleNullNewFrom () {
      this.applicationSurveyFrom = {}
    }, */
    /* toQuery () {
      this.handleExecutionPlan(this.objectVal)
    }, */
    refreshList () {
      let params = {
        status: this.formData.statusList,
        appName: this.formData.keyWord
      }
      this.getTableData(params)
    },
    addApplition (row) {
      if (row) {
        this.addAppData = row
      } else {
        this.addAppData = {}
      }
      this.addVisible = true
    },

    stopUse (val) {
      if (val.status == 2) {
        this.disableOrElse(val)
        return
      }
      this.$confirm('是否确认停用该客户此应用？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning'
      })
        .then(() => {
          this.disableOrElse(val)
          // this.table.tableData.splice(index, 1)
        })
        .catch(() => { })
    },
    // 停用启用应用
    disableOrElse (val) {
      this.showLoading()
      this.$http({
        url: '/robot/app/client/disableOrElse',
        method: 'post',
        params: {
          id: val.id,
          status: val.status == 1 ? 2 : 1
        }
      })
        .then((data) => {
          this.isloading = false
          let params = {
            status: this.formData.statusList,
            appName: this.formData.keyWord
          }
          this.getTableData(params)
          this.closeLoading()
        })
        .catch((err) => {
          this.isloading = false
          this.closeLoading()
          this.$message.error('接口出错，请稍后再试')
        })
    },

    // 登录信息
    handleLoginInfo (row) {
      let obj = row
      this.$refs.loginInfoDrawer.init(obj)
    },

    handleExecutionQuery (row) {
      // 传参【客户、应用、申报账户】，跳转执行查询页
      this.executeQueryData = {
        clientId: row.clientId,
        appCode: row.appCode,
        taskCode: row.taskCode
      }
      if (row.doquery) {
        // RPA-3299 yongfeng:从参保之类的右侧详情 - 操作记录 - 执行查询跳转过来的
        this.executeQueryData.execStartTime = row.startTime
        this.executeQueryData.execEndTime = row.endTime
      }
      // 从参保之类的右侧详情 - 操作记录 - 执行查询，需要定位到跳转进来的操作记录，并显示右侧明细
      if (row.locateRecord) {
        this.executeQueryData.locateRecord = row.locateRecord
      }
      // 从rpa异常进行跳转，需要定位到最新的失败记录
      if (row.locating) {
        this.executeQueryData.locating = row.locating
      }
      // 需要跳转执行查询（从数据监控-今日执行-查看-执行查询进来），需要该类型的第一条异常
      if (row.filterFlowCodes) {
        this.executeQueryData.filterFlowCodes = row.filterFlowCodes
      }
      this.isShowIndex = 2
      /* this.rowObject = row
      this.isloading = true
      if (row.nofilterTime) {
        this.searchForm = {
          execStartTime: undefined,
          execEndTime: undefined
        }
        this.executionList(row) // 查询table
        this.clientList(row) // 查询右侧列表
      } else if (row.doquery) {
        // RPA-3299 yongfeng:从参保之类的右侧详情 - 操作记录 - 执行查询跳转过来的
        this.searchForm = {
          execStartTime: row.startTime,
          execEndTime: row.endTime
        }
        this.executionList(row) // 查询table
        this.clientList(row) // 查询右侧列表
      } else {
        this.executionList(row) // 查询table
        this.clientList(row) // 查询右侧列表
        this.searchForm = {
          execStartTime: this.$moment(new Date()).format('YYYY-MM-DD'),
          execEndTime: this.$moment(new Date()).format('YYYY-MM-DD')
        }
      } */

      // console.log('searchForm', this.searchForm)
    },

    // 账户情况
    handleAccountState (row) {
      this.accountStateData = {
        appCode: row.appCode,
        clientId: row.clientId,
        machineCode: row.machineCode,
        accNumber: row.accNumber,
        businessType: row.businessType,
        addrId: row.addrId,
        machineStatusTag: this.machineStatusTag
      }
      this.isShowIndex = 3
    },

    // 执行计划
    handleExecutionPlan (row) {
      this.$refs.ExecutionPlanAll.init(row)
    },

    clientList () {
      // this.$http({
      //   url: '/robot/app/client/list',
      //   method: 'post',
      //   // params: {
      //   //   appCode: val.appCode,
      //   // },
      // })
      //   .then((res) => {
      //     console.log('res.data', res.data)
      //     this.client = true
      //     this.isJump()
      //   })
      //   .catch((err) => {
      //     this.isloading = false
      //     this.$message.error('接口出错，请稍后再试')
      //   })
    },
    // isJump() {
    //   if (this.execution == true && this.client == true) {
    //     this.isloading = false
    //     this.isShowIndex = 2
    //   }
    // },

    toIndex (index) {
      this.isShowIndex = index != undefined ? index : 0
    },
    /* openApplication (val) {
      Object.assign(this.objectVal, val)
      this.$http({
        url: '/robot/app/client/getAppSituation',
        method: 'post',
        params: {
          appCode: val.appCode,
          clientId: val.clientId
          // appCode: '679f161f0f8c4670b40a3fb1a1eca721',
        }
      })
        .then((data) => {
          if (data.data.code == '200') {
            this.showApplication = true
            Object.assign(this.applicationSurveyFrom, data.data.data)
          }
        })
        .catch((err) => {
          this.isloading = false
          this.$message.error('接口出错，请稍后再试')
        })
    }, */
    // 导出客户应用情况
    exportCustomer () {
      // var params = this.$refs.table.getParamsQuery()
      let that = this
      var params1 = {
        keyWord: this.formData.keyWord,
        status: this.formData.status,
        machineCode: this.formData.machineCode,
        clientId: this.formData.clientId
      }

      if (that.isComponent) {
        params1['appCode'] = that.configData.appCode
      }
      this.$downloadFile(
        '/robot/app/client/downloadClientApp',
        'post',
        params1,
        this.$global.EXCEL
      )
    },
    // 多选框
    changeBussinssType () {
      let params = {
        status: this.formData.statusList,
        appName: this.formData.keyWord
      }
      this.getTableData(params)
    },
    // 获取字典值
    getDictByKey () {
      // this.$http({
      //   url: 'policy/sys/dict/getDictByKey',
      //   method: 'get',
      //   params: {
      //     dataKey: '10003',
      //   },
      // })
      //   .then((res) => {
      //     this.options = res.data.data
      //   })
      //   .catch((err) => {
      //     this.$message.error('字典接口出错，请稍后再试')
      //   })
    },

    changeSearch () {
      let params = {
        status: this.formData.statusList,
        appName: this.formData.keyWord
      }
      this.getTableData(params)
    },
    goDevice () {
      this.$router.push('/deviceManagement/index')
    },
    getMachineByClientId () {
      let clientId = this.formData.clientId ? this.formData.clientId : ''
      this.$http({
        url: '/robot/app/client/getMachineByClientId?clientId=' + clientId,
        method: 'get'
      }).then((data) => {
        if (data.data.code == '200') {
          this.formData.machineCode = ''
          this.machineCodeList = data.data.data
        }
      }).catch(() => {
        this.isloading = false
      })
    },
    clearClientId () {
      this.formData.clientId = null
    },
    openEditDevice (row) {
      this.getMachineCodeList(row)
      this.getClientAppTask(row)
    },
    cancelDevice () {
      this.editDeviceData = this.$options.data().editDeviceData
      this.editDeviceShow = false
    },
    confirmDevice () {
      this.$refs.editMachineCode.validate((valid) => {
        if (valid) {
          this.showLoading()
          this.$http({
            url: '/robot/app/client/changeRobotClientApp',
            method: 'post',
            data: {
              id: this.editDeviceData.id,
              machineCode: this.editDeviceData.machineCode,
              taskCode: this.editDeviceData.taskCode
            }
          })
            .then((data) => {
              this.closeLoading()
              if (data.data.code == 200) {
                this.editDeviceShow = false
                this.editDeviceData = this.$options.data().editDeviceData
                this.$message.success(data.data.message || '保存成功')
                this.getTableData()
              }
            })
            .catch((err) => {
              this.closeLoading()
            })
        }
      })
    },
    getMachineCodeList (row) {
      this.showLoading()
      this.$http({
        url: '/robot/version/client/getByClientId/' + row.clientId,
        method: 'post',
        data: {}
      })
        .then((data) => {
          this.closeLoading()
          this.machineCodeList2 = data.data.data
          this.editDeviceData.id = row.id
          this.editDeviceData.appName = row.appName
          this.editDeviceData.customerName = row.customerName
          this.editDeviceData.machineCode = row.machineCode
          this.editDeviceShow = true
        })
        .catch((err) => { })
    },
    getClientAppTask (row) {
      this.showLoading()
      this.$http({
        url: '/robot/app/client/getClientAppTask',
        method: 'post',
        data: {
          id: row.id
        }
      }).then((data) => {
        this.closeLoading()
        if (data.data.code == 200) {
          this.clientAppTaskList = data.data.data
        }
      }).catch((err) => {
        this.closeLoading()
      })
    },
    getAccountStatusHistory (row) {
      this.$refs.operateRecordsDrawer.init(row)
    },

    //   停用、启用
    handleTaskFlowStatus (row, updateStatus) {
      this.$refs.updateStatusDialog.init(row, updateStatus)
    },

    showLoading (target) {
      this.loading = this.$loading({
        target: target || document.body,
        lock: true,
        text: '加载中',
        spinner: 'el-icon-loading',
        background: 'rgba(255, 255, 255, 0.7)'
      })
    },
    closeLoading () {
      if (this.loading && this.loading.close) {
        this.loading.close()
      }
    },
    // 批量停用\启用账号
    handleUpdateStatusBatch (updateStatus) {
      let selections = this.$refs.table.selections
      if (selections.length === 0) {
        this.$message.warning('请先选择需要操作的数据')
        return
      }
      this.$refs.updateStatusBatch.init(selections, updateStatus)
    }
  }
}
</script>
<style lang="less" scoped>
/deep/.el-dialog__header {
  padding: 10px 20px;
}

/deep/.el-dialog__body {
  padding: 10px 10px;
}

/deep/.el-drawer__body {
  padding-bottom: 0;
}

/deep/.el-dialog__header {
  border-bottom: none !important;
}

.text-blue {
  &:hover {
    text-decoration: underline;
  }
}

/deep/ .el-radio__input.is-checked .el-radio__inner::after {
  content: '';
  width: 3px;
  height: 7px;
  border: 1px solid white;
  border-top: transparent;
  border-left: transparent;
  text-align: center;
  display: block;
  position: absolute;
  top: 20%;
  left: 50%;
  vertical-align: middle;
  transform: rotate(45deg) translateX(-50%);
  border-radius: 0px;
  background: none;
}

/deep/ .el-radio__inner {
  border-radius: 2px;
  width: 14px;
  height: 14px;
}

/deep/.drawerForm {
  .item-label {
    margin: 5px 0;
  }

  .el-form-item {
    width: 100%;
    margin-right: 0;
    margin-bottom: 0;

    .el-form-item__content,
    .addr-main,
    .el-select {
      width: 100%;
    }
  }

  .el-form-item__error {
    position: relative;
  }

  .opt-icon {
    margin-left: 15px;
    margin-top: 8px;
    cursor: pointer;
    font-size: 28px;
  }

  .ic-addr {
    margin-top: 13px !important;
  }
}

.search-row {
  .lab{
    display: inline-block;
    width: 80px;
    text-align: right;
  }
}
.tag{
  display: inline-block;
  margin-right: 5px;
  text-align: center;
  font-size: 12px;
  color: #ffffff;
  background-color: #008000;
  width: 70px;
  height: 24px;
  line-height: 24px;
  border-radius: 30px;
}
.tag-red{
  background-color: #fde2e2;
  color: #CC0000;
}
.tag-blue{
  background-color: #d9ecff;
  color: @blueColor;
}
.ic-cust{
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
.ic-cust-no{
  background: none;
}
.taskComment{
  position: relative;
  width: 100%;
  height: 100%;
  min-height: 30px;
  display: flex;
  align-items: center;
  .ic-view{
    font-size: 18px;
    color: @blueColor;
    position: absolute;
    left: 0px;
    top: 8px;
    z-index: 2;
    cursor: pointer;
  }
}
</style>
<style scoped>
body .el-table th.gutter {
  display: table-cell !important;
}

.error {
  margin-top: 10px;
  /* position: absolute; */
  left: 0;
  /* top: 120%; */
  color: #f56c6c;
  font-size: 12px;
}

.jkh-flex {
  /* display: flex;
  justify-content: center; */
}

.button-topst {
  position: absolute;
  top: -60px;
  left: 0%;
}
.button-add {
  position: absolute;
  right: 0;
}

.dialog-footer {
  display: flex;
  justify-content: center;
}
.todayDeclaredCount{
  position: absolute;
  right: 0;
  top: -5px;
  z-index: 2;
}
</style>
