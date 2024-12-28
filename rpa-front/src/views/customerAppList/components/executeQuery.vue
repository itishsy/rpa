<template>
  <div class="spl-container">
    <breadcrumb :data="pathData" :cust-back="true" @back="toBack" :cust-link="true" @toPath="toBack">
    </breadcrumb>
    <div class="pl-20 pr-20">
      <div class="search-row" style="padding-left: 0; padding-right: 0">
        <el-row>
          <el-col :span="8">
            <span class="nowarp pl-20">客户名称：</span>
             <el-select placeholder="请选择" filterable v-model="searchForm.clientId" style="width:260px;" clearable @change="getAppList();getRobotTaskList()">
              <el-option v-for="(item, index) in listCustomerOption" :key="index" :label="item.customerName"
                :value="item.id"></el-option>
            </el-select>
          </el-col>
          <el-col :span="8">
            <div class="display-flex">
              <span class="label">执行时间：</span>
              <div class="flex1">
                <div class="flex1" style="display: flex; align-items: center; padding-right: 0px">
                  <el-date-picker v-model="searchForm.execStartTime" type="date" value-format="yyyy-MM-dd"
                                  placeholder="开始时间" style="width: 135px"></el-date-picker>
                  <span style="margin: 0 5px">—</span>
                  <el-date-picker v-model="searchForm.execEndTime" type="date" value-format="yyyy-MM-dd"
                                  placeholder="结束时间" style="width: 135px"></el-date-picker>
                </div>
              </div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="display-flex">
              <span class="label">具体时段：</span>
              <div class="flex1">
                <div class="flex1" style="display: flex; align-items: center; padding-right: 0px">
                  <el-time-picker v-model="searchForm.execTimeStart" placeholder="开始时间" format="HH:mm" value-format="HH:mm" style="width: 135px">
                  </el-time-picker>
                  <span style="margin: 0 5px">—</span>
                  <el-time-picker v-model="searchForm.execTimeEnd" placeholder="结束时间" format="HH:mm" value-format="HH:mm" style="width: 135px">
                  </el-time-picker>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
        <el-row class="mt-15">
          <el-col :span="8">
            <span class="nowarp pl-20">应用名称：</span>
            <el-select placeholder="请选择" filterable v-model="searchForm.appCode" style="width:260px;" clearable
                       @change="getRobotTaskList">
              <el-option v-for="(item, index) in appList" :key="index" :label="item.appName"
                         :value="item.appCode"></el-option>
            </el-select>
            <span class="ml-10">{{handleOnlineStatus(searchForm.appCode)}}</span>
          </el-col>
          <el-col :span="8">
            <span class="nowarp">申报账户：</span>
            <el-select placeholder="请选择" filterable v-model="searchForm.taskCode" style="width:296px;" clearable>
              <el-option v-for="(item, index) in taskList" :key="index" :label="item.orgName + '-' +item.accountNumber"
                         :value="item.taskCode"></el-option>
            </el-select>
            <span class="ml-10">{{handleTaskStatus(searchForm.taskCode)}}</span>
          </el-col>
          <el-col :span="8" class="text-right">
            <el-button type="primary" @click="getTableData" class="ml-20 w-80">查询</el-button>
            <el-button type="primary" @click="exportTableData" class="ml-20 w-80">导出</el-button>
          </el-col>
        </el-row>
      </div>

      <div>
        <el-row>
          <el-col :span="14">
            <div>
              <dTable v-loading="isloadingQuey" element-loading-text="拼命加载中" element-loading-spinner="el-icon-loading"
                :data="tableData2" ref="table" :tableHeight="tableHeight" :showIndex="false" :showSelection="false"
                rowKey="component" :paging="false" @row-click="clickDetailsFun"
                :customTableRowClassName="tableRowClassName">
                <u-table-column prop="startTime" label="执行时间" width="160" :show-overflow-tooltip="true">
                  <template slot-scope="scope">
                    {{ $moment(scope.row.startTime).format('YYYY-MM-DD HH:mm:ss') }}
                  </template>
                </u-table-column>
                <u-table-column prop="accountNumber" label="申报账户" min-width="120" :show-overflow-tooltip="true">
                  <template slot="header" slot-scope="scope">
                    <span class="mr-10">申报账户</span>
                    <i class="el-icon-setting f-cursor" @click='showHeader = !showHeader'></i>
                    <el-input v-if="showHeader" v-model="searchAccountNumber" size="mini" placeholder="输入申报账户搜索"
                              @input="handleTableData" clearable />
                  </template>
                </u-table-column>
                <u-table-column prop="flowName" label="流程名称" min-width="120" :show-overflow-tooltip="true">
                  <template slot="header" slot-scope="scope">
                    <span class="mr-10">流程名称</span>
                    <i class="el-icon-setting f-cursor" @click='showHeader = !showHeader'></i>
                    <el-input v-if="showHeader" v-model="searchFlowName" size="mini" placeholder="输入流程搜索"
                      @input="handleTableData" clearable />
                  </template>
                  <template slot-scope="scope">
                    <el-tooltip effect="dark" :content="scope.row.executePlan" placement="top">
                      <span class="flowName">{{ scope.row.flowName }}</span>
                    </el-tooltip>
                  </template>
                </u-table-column>
                <u-table-column prop="machineCode" label="执行设备" min-width="120" :show-overflow-tooltip="true">
                  <template slot-scope="scope">
                      <span class="flowName">{{ scope.row.machineCode }}</span>
                  </template>
                </u-table-column>
                <u-table-column prop="status" label="状态" min-width="70" :show-overflow-tooltip="true"
                  filter-placement="bottom">
                   <template slot="header" slot-scope="scope">
                    <span class="mr-10">状态</span>
                    <i class="el-icon-setting f-cursor" @click='showHeader = !showHeader'></i>
                     <el-select v-if="showHeader" placeholder="请选择" filterable v-model="searchStatus" clearable @change="handleTableData">
                       <el-option label="成功" :value="1"></el-option>
                       <el-option label="失败" :value="0"></el-option>
                     </el-select>
                  </template>
                  <template slot-scope="scope">
                    <span v-show="scope.row.status == 1">成功</span>
                    <span v-show="scope.row.status == 0" class="text-red">失败</span>
                  </template>
                </u-table-column>
                <u-table-column label="附件" width="100" :show-overflow-tooltip="true">
                  <template slot-scope="scope">
                    <el-button v-if="scope.row.reportFile && scope.row.reportFile.length > 0" type="text" size="medium"
                      class="text-blue text-blue2" @click="viewReportFile(scope.row)">申报文件
                    </el-button>
                    <!-- <span>{{ scope.row }}</span> -->
                    <!-- <el-button
                      type="text"
                      size="medium"
                      class="text-blue text-blue2"
                      >回盘文件
                    </el-button> -->
                  </template>
                </u-table-column>
                <u-table-column label="执行明细" align="left" width="80">
                  <template slot="header" slot-scope="scope">
                    <span>执行明细</span>
                  </template>
                  <template slot-scope="scope">
                    <el-button @click="implementationDetails(scope.row)" type="text" size="medium"
                      class="text-blue text-blue2">查看</el-button>
                  </template>
                </u-table-column>
              </dTable>
            </div>
            <div class="text-right mt-5">记录数： {{tableData2.length}}</div>
          </el-col>
          <el-col :span="10">
            <div class="detail-box">
              <p class="title">查看执行明细</p>
              <div v-loading="isLoadingDetails">
                <div class="view-content" :style="{ height: viewHeight }"
                     element-loading-text="拼命加载中" element-loading-spinner="el-icon-loading">
                  <div class="step-item" v-for="(item, index) in implementationDetailsArr" :key="item.id">
                    <div style="display: flex; justify-content: space-between">
                      <p class="step-title">
                        <span class="step-name">{{ item.stepName }}</span>
                        {{
                          $moment(item.startTime).format('YYYY-MM-DD HH:mm:ss')
                        }}
                      </p>
                      <!-- <p style="cursor: pointer; color: #3e82ff" v-if="
                        item.robotExecutionFileInfos &&
                        item.robotExecutionFileInfos.length > 0
                      " @click="screenshots(item.files)">
                        查看截屏
                      </p> -->
                      <p style="cursor: pointer; color: #3e82ff" v-if="item.files &&
                        item.files.length > 0
                        " @click="screenshots(item.files)">
                        查看截屏
                      </p>
                    </div>
                    <p class="reason" v-if="item.error">原因：{{ item.error }}</p>
                    <p class="step-dis" v-if="item.flowType == 2" @click="seeDeatil(item.detailVOS)">
                      <span style="cursor: pointer; color: #3e82ff">执行步骤</span>
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>
    <el-dialog title="执行步骤" :visible.sync="visible2" width="40%" :close-on-click-modal="false" class="cust-dialog">
      <div class="visi-box">
        <div class="step-item" v-for="(item, index) in setpList" :key="item.id">
          <div style="display: flex; justify-content: space-between">
            <p class="step-title">
              <span class="step-name">{{ item.stepName }}</span>
              {{ $moment(item.startTime).format('YYYY-MM-DD HH:mm:ss') }}
            </p>
          </div>
        </div>
      </div>

      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="confirmDialog">确 定</el-button>
      </span>
    </el-dialog>
    <el-image-viewer v-if="showViewer" :on-close="closeViewer" :url-list="srcList" />
  </div>
</template>
<script>
import dTable from '../../../components/common/table'
import ElImageViewer from 'element-ui/packages/image/src/image-viewer'
export default {
  components: { dTable, ElImageViewer },
  data () {
    return {
      pathData: [],
      searchForm: {
        clientId: '',
        execStartTime: '',
        execEndTime: '',
        execTimeStart: '',
        execTimeEnd: '',
        appCode: '',
        taskCode: '', // 申报账户
        flowCodes: []
      },
      isloadingQuey: false,
      tableData: [],
      listCustomerOption: [],
      appList: [],
      taskList: [],

      getRowIndex: 0,
      stepbarList: [
        { status: 1, name: 11 },
        { status: 2, name: 22 }
      ],
      visible2: false,
      isLoadingDetails: false,
      // timeArrList: [new Date(), new Date()],
      implementationDetailsArr: [],
      showViewer: false,
      screenshotsUrl: '',
      srcList: [],
      setpList: [],
      form: { // 替换原来的文字为可选下拉
        clientId: null,
        appCode: null
      },
      showHeader: false,
      searchAccountNumber: '', // 表头筛选 申报账户
      searchFlowName: '', // 表头筛选 流程名称
      searchStatus: '', // 表头筛选 状态
      tableData2: [] // 表头筛选 流程名称
    }
  },
  props: {
    rowData: {
      type: Object,
      default: () => { }
    }
  },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 255 + 'px'
    },
    viewHeight: function () {
      return this.$global.bodyHeight - 310 + 'px'
    }
  },
  watch: {
    'tableData': {
      handler (val) {
        this.tableData2 = val
      },
      deep: true,
      immediate: true
    }
  },
  created () {
    console.log('created')
    let tabs = this.$store.state.tabs
    if (tabs) {
      if (this.$route.meta.parentPath) {
        this.pathData = this.$global.deepcopyArray(
          tabs[this.$route.meta.parentPath]
        )
      } else {
        this.pathData = this.$global.deepcopyArray(tabs[this.$route.path])
      }
    }

    this.pathData.push({ name: '执行查询' })
    // this.handleTime()
    this.getlistCustomer()
    this.init()
  },
  mounted () { },
  methods: {
    init () {
      let row = this.rowData
      console.log('this.rowData==' + this.rowData)
      if (row.appCode) {
        this.searchForm.appCode = row.appCode
      }
      if (row.clientId) {
        this.searchForm.clientId = Number(row.clientId)
      }
      if (row.appCode) {
        this.searchForm.appCode = row.appCode
      }
      if (row.execStartTime) {
        this.searchForm.execStartTime = row.execStartTime
      }
      if (row.execEndTime) {
        this.searchForm.execEndTime = row.execEndTime
      }
      if (!row.execStartTime && !row.execEndTime) {
        let curDate = this.$moment().format('YYYY-MM-DD')
        this.searchForm.execStartTime = curDate
        this.searchForm.execEndTime = curDate
      }
      if (row.flowCodes) {
        this.searchForm.flowCodes = row.flowCodes
      }
      if (row.taskCode) {
        this.searchForm.taskCode = row.taskCode
      }
      this.getTableData({
        locateRecord: row.locateRecord,
        locating: row.locating,
        filterFlowCodes: row.filterFlowCodes,
        filterExecutionCode: row.filterExecutionCode
      })
      this.getAppList('init')
      this.getRobotTaskList('init')
    },
    getTableData (val) {
      let searchForm = this.searchForm
      let msg = ''
      if (searchForm.execStartTime && searchForm.execEndTime && (new Date(searchForm.execStartTime) > new Date(searchForm.execEndTime))) {
        msg = '执行开始时间不能大于结束时间'
      } else if (searchForm.execTimeStart && searchForm.execTimeEnd && (new Date('2023-01-01 ' + searchForm.execTimeStart) > new Date('2023-01-01 ' + searchForm.execTimeEnd))) {
        msg = '具体时段开始时间不能大于结束时间'
      } else if ((searchForm.execTimeStart || searchForm.execTimeEnd) && (!searchForm.execStartTime && !searchForm.execEndTime)) {
        msg = '选择具体时段必须选择执行时间'
      } else if (!searchForm.clientId && !searchForm.execStartTime && !searchForm.execEndTime && !searchForm.taskCode) {
        msg = '客户名称、执行时间和申报账户至少选择一个'
      }
      if (msg !== '') {
        this.$message.warning(msg)
        return
      }
      this.isloadingQuey = true
      this.$http({
        url: '/robot/app/client/executionList',
        method: 'post',
        data: {
          clientId: searchForm.clientId,
          execStartTime: searchForm.execStartTime,
          execEndTime: searchForm.execEndTime,
          execTimeStart: searchForm.execTimeStart,
          execTimeEnd: searchForm.execTimeEnd,
          appCode: searchForm.appCode,
          taskCode: searchForm.taskCode,
          flowCodes: searchForm.flowCodes
        }
      }).then((data) => {
        this.tableData = data.data.data
        this.searchAccountNumber = ''
        this.searchFlowName = ''
        this.searchStatus = ''
        this.isloading = false
        this.isloadingQuey = false

        if (val.locating) {
          // console.log('当前tabl222e', this.tableData)
          const sortTableData = this.tableData.sort((a, b) => { return new Date(b.startTime) - new Date(a.startTime) })
          console.log('排序后', sortTableData)
          let latestErrorRecord = null
          if (val.filterFlowCodes) {
            latestErrorRecord = sortTableData.find(item => val.filterFlowCodes.indexOf(item.flowCode) > -1 && item.status === 0)
            if (!latestErrorRecord) {
              latestErrorRecord = sortTableData.find(item => val.filterFlowCodes.indexOf(item.flowCode) > -1)
              if (!latestErrorRecord) {
                return
              }
            }
          } else if (val.filterExecutionCode) {
            latestErrorRecord = sortTableData.find(item => val.filterExecutionCode.indexOf(item.executionCode) > -1 && item.status === 0)
            if (!latestErrorRecord) {
              return
            }
          } else {
            latestErrorRecord = sortTableData.find(item => item.status === 0)
          }
          console.log('最新的一个错误记录：', latestErrorRecord)

          this.implementationDetails(latestErrorRecord)
          this.$nextTick(() => {
            // 将最新的一个失败记录 设置为高亮
            this.$refs.table.$refs.palTable.setCurrentRow(latestErrorRecord)
          })
        } else if (val.locateRecord) {
          // 从操作记录 - 执行查询中跳转过来 需要找到最新的一条执行记录 并加载其执行明细 并且该行需要高亮
          const sortTableData = this.tableData.sort((a, b) => { return new Date(b.startTime) - new Date(a.startTime) })
          this.implementationDetails(sortTableData[0])
          console.log('排序后', sortTableData)
          sortTableData[0].highlight = true
          this.$nextTick(() => {
            // 将最新的一个执行记录 设置为高亮
            this.$refs.table.$refs.palTable.setCurrentRow(sortTableData[0])
          })
        }
      }).catch((err) => {
        this.isloading = false
      })
    },
    exportTableData (val) {
      let searchForm = this.searchForm
      let msg = ''
      if (searchForm.execStartTime && searchForm.execEndTime && (new Date(searchForm.execStartTime) > new Date(searchForm.execEndTime))) {
        msg = '执行开始时间不能大于结束时间'
      } else if (searchForm.execTimeStart && searchForm.execTimeEnd && (new Date('2023-01-01 ' + searchForm.execTimeStart) > new Date('2023-01-01 ' + searchForm.execTimeEnd))) {
        msg = '具体时段开始时间不能大于结束时间'
      } else if ((searchForm.execTimeStart || searchForm.execTimeEnd) && (!searchForm.execStartTime && !searchForm.execEndTime)) {
        msg = '选择具体时段必须选择执行时间'
      } else if (!searchForm.clientId && !searchForm.execStartTime && !searchForm.execEndTime && !searchForm.taskCode) {
        msg = '客户名称、执行时间和申报账户至少选择一个'
      }
      if (msg !== '') {
        this.$message.warning(msg)
        return
      }
      this.isloadingQuey = true
      this.$downloadFile(
        '/robot/app/client/executionExport',
        'post',
        {
          clientId: searchForm.clientId,
          execStartTime: searchForm.execStartTime,
          execEndTime: searchForm.execEndTime,
          execTimeStart: searchForm.execTimeStart,
          execTimeEnd: searchForm.execTimeEnd,
          appCode: searchForm.appCode,
          taskCode: searchForm.taskCode,
          flowCodes: searchForm.flowCodes
        },
        this.$global.EXCEL
      )
      this.isloading = false
      this.isloadingQuey = false
    },
    getlistCustomer () {
      this.$http({
        url: '/robot/app/client/listCustomer',
        method: 'post'
      }).then((data) => {
        if (data.data.code == 200) {
          this.listCustomerOption = data.data.data
        }
      }).catch((err) => { })
    },
    getAppList (type) {
      this.$http({
        url: '/robot/app/client/getRobotAppList',
        method: 'post',
        params: {
          clientId: this.searchForm.clientId
        }
      }).then((data) => {
        if (data.data.code == 200) {
          if (type !== 'init') {
            this.searchForm.appCode = ''
          }
          this.appList = data.data.data
        }
      }).catch(() => {
      })
    },
    getRobotTaskList (type) {
      this.$http({
        url: '/robot/app/client/getRobotTaskList',
        method: 'post',
        params: {
          appCode: this.searchForm.appCode,
          clientId: this.searchForm.clientId
        }
      }).then((data) => {
        if (data.data.code == 200) {
          if (type !== 'init') {
            this.searchForm.taskCode = ''
          }

          this.taskList = data.data.data
        }
      }).catch(() => {
      })
    },
    handleOnlineStatus (appCode) {
      if (appCode) {
        for (let i = 0; i < this.appList.length; i++) {
          if (appCode === this.appList[i].appCode) {
            return this.appList[i].onlineStatusName
          }
        }
      }
      return ''
    },
    handleTaskStatus (taskCode) {
      if (taskCode) {
        for (let i = 0; i < this.taskList.length; i++) {
          if (taskCode === this.taskList[i].taskCode) {
            return this.taskList[i].taskStatus
          }
        }
      }
      return ''
    },
    handleTableData () {
      console.log('handleTableData')
      let tableData = this.tableData
      let searchAccountNumber = this.searchAccountNumber
      let searchFlowName = this.searchFlowName
      let searchStatus = this.searchStatus
      this.tableData2 = tableData.filter(data =>
        (searchAccountNumber === '' || (data.accountNumber && data.accountNumber.toLowerCase().includes(searchAccountNumber.toLowerCase()))) &&
        (searchFlowName === '' || data.flowName.includes(searchFlowName.toLowerCase())) && (searchStatus === '' || data.status === searchStatus))
      console.log(this.tableData2)
    },
    seeDeatil (val) {
      this.setpList = JSON.parse(JSON.stringify(val))
      this.visible2 = true
    },
    confirmDialog () {
      this.visible2 = false
    },
    selectedRowStyle ({ row, rowIndex }) {
      // 关键代码
      if (this.getRowIndex === rowIndex) {
        document.getElementById(rowIndex).className = 'dis'
      } else {
        if (document.getElementById(rowIndex)) {
          document.getElementById(rowIndex).className = 'dis2'
        }
      }
    },
    tableRowClassName ({ row, rowIndex }) {
      if (row.highlight) {
        return 'checked-row'
      }
    },
    clickDetailsFun (val) {
      this.getRowIndex = val.rowIndex
    },
    rowClick (row, event, column) {
      console.log('row, event, column', row, event, column)
      // this.$emit('row-click', row, event, column)
      // if (this.clickAndSelect) {
      //   this.$refs.palTable.toggleRowSelection(row)
      // }
    },
    /* handleTime () {
      if (this.timeArrList) {
        let start = this.$moment(this.timeArrList[0]).format('YYYY-MM-DD')
        let end = this.$moment(this.timeArrList[1]).format('YYYY-MM-DD')
        this.searchForm.execStartTime = start
        this.searchForm.execEndTime = end
      } else {
        this.searchForm.execStartTime = undefined
        this.searchForm.execEndTime = undefined
      }
    }, */
    implementationDetails (val) {
      this.isLoadingDetails = true
      this.$http({
        url: '/robot/app/client/client/execution/detail',
        method: 'post',
        params: {
          executionCode: val.executionCode,
          flowCode: val.flowCode
        }
      })
        .then((data) => {
          this.implementationDetailsArr = data.data.data
          this.isLoadingDetails = false
        })
        .catch((err) => {
          this.isloading = false
          this.isLoadingDetails = false
          this.$message.error('接口出错，请稍后再试')
        })
    },
    toBack () {
      this.$emit('back')
    },
    handleView (row, type) {
      // type: 1、RPA流程版本-查看；2、组件版本-查看 3、异常-查看
      this.viewInfo.type = type
      this.viewInfo.show = true
    },
    screenshots (robotExecutionFileInfos) {
      this.screenshotsUrl =
        robotExecutionFileInfos && robotExecutionFileInfos.length > 0
          ? robotExecutionFileInfos[0].fileFullPath
          : ''
      this.srcList = [this.screenshotsUrl]
      this.$nextTick((item) => {
        this.showViewer = true
      })
    },
    closeViewer () {
      this.showViewer = false
    },
    clearAppId () {
      this.form.appCode = null
    },
    // 预览申报文件
    viewReportFile (row) {
      if (!row.reportFile || row.reportFile.length == 0) return
      row.reportFile.forEach(element => {
        window.open(element.fileFullPath, '_blank')
      })
    }
  }
}
</script>
<style lang="less" scoped>
.text-blue2 {
  &:hover {
    text-decoration: underline;
  }
}

.search-row {
  .flex1 {
    word-break: break-all;
  }
}

.detail-box {
  border: 1px solid #dbdbdb;
  margin-left: 20px;

  .title {
    height: 35px;
    line-height: 35px;
    background-color: #eef0f7;
    border-bottom: 1px solid #dbdbdb;
    padding-left: 15px;
    font-weight: bold;
  }

  .view-content {
    padding: 20px;
    overflow-y: auto;
  }

  .step-item {
    color: #151515;
    /*border-left: 1px solid #DDDDDD;*/
    padding-left: 15px;
    padding-bottom: 25px;
    position: relative;
  }

  .step-item:before {
    display: inline-block;
    content: '';
    width: 10px;
    height: 10px;
    background: #dddddd;
    border-radius: 50%;
    position: absolute;
    top: 3px;
    left: -5px;
  }

  .step-item:after {
    display: inline-block;
    content: '';
    width: 1px;
    height: 100%;
    background: #dddddd;
    position: absolute;
    top: 3px;
    left: 0px;
  }

  .step-item:last-of-type:after {
    display: none;
  }

  .step-title {
    /*margin-top: -10px;*/
  }

  .step-name {
    display: inline-block;
    color: #458ae6;
    margin-right: 10px;
  }

  .reason {
    color: #e15e63;
    margin-top: 10px;
    word-break: break-all;
    word-wrap: break-word;
    max-width: 1800px;
  }
}

.mt-10 {
  margin-top: 10px;
}

.cur-router {
  float: left;
  color: #888888;
}

.back {
  font-size: 14px;
  font-family: Microsoft YaHei;
  font-weight: 400;
  color: #333333;
  cursor: pointer;
}

.ic-breadcrumb-home {
  display: inline-block;
  width: 24px;
  height: 20px;
  margin-top: 19px;
  background: url('../../../assets/images/icons/ic-breadcrumb-home.png');
  margin-right: 9px;
}

.ic-breadcrumb-back {
  display: inline-block;
  width: 16px;
  height: 10px;
  margin-top: 19px;
  background: url('../../../assets/images/icons/ic-breadcrumb-back.png');
  margin-right: 9px;
}

/*//面包屑*/
.spl-breadcrumb {
  position: fixed;
  top: 70px;
  /*width: calc(100% - 420px);*/
  width: 80%;
  max-width: 1920px;
  min-width: 1200px;
  // height: 120px;
  background-color: @bodyBaseBgc;
  /*margin-left: -20px;*/
  z-index: 9;
  padding-top: 20px;

  /deep/ .el-breadcrumb {
    padding: 0 30px;
    height: 60px;
    background-color: #F8F8F8;
    box-shadow: 0px 0px 10px 0px rgba(135, 175, 228, 0.1);
    border-radius: 6px 6px 0px 0px;
    margin-bottom: 0px;
    font-family: Microsoft YaHei;

    /*font-size: 15px;*/
    a {
      color: #888888;
      font-weight: normal;
      line-height: 66px;
    }
  }

  /deep/.el-breadcrumb__item {
    float: none;
  }

  /deep/.el-breadcrumb__item .el-breadcrumb__inner a {
    font-size: 16px;
    font-weight: bold;
  }

  /deep/.el-breadcrumb__item:last-child .el-breadcrumb__inner a {
    color: #333333;
  }

  /deep/.el-breadcrumb__separator[class*=icon] {
    padding: 0 10px;
  }

  .spl-index {
    background-color: #fff;
    padding: 20px 240px 20px 40px;
    margin-bottom: 20px;
  }

  .breadcrumb-btn {
    position: absolute;
    top: 35px;
    right: 20px;
  }
}
//高亮
/deep/.el-table__body tr.current-row>td {
  background-color: #EEF0F6 !important;
}

/deep/ .el-table .highlight {
  font-size: 15px;
  font-weight: 500;
  background: #abc4f5;
}

//圆点样式
.dis {
  border-radius: 100px;
  width: 10px;
  height: 10px;
  background: #EEF0F6;
}

.dis2 {
  display: none;
}

.step-dis {
  padding: 10px 0;
}
</style>
<style>
.visi-box {
  padding: 10px 29px;
}

/* 弹窗内的步骤条 */
.step-item {
  color: #151515;
  /*border-left: 1px solid #DDDDDD;*/
  padding-left: 15px;
  padding-bottom: 25px;
  position: relative;
}

.step-item:before {
  display: inline-block;
  content: '';
  width: 10px;
  height: 10px;
  background: #dddddd;
  border-radius: 50%;
  position: absolute;
  top: 3px;
  left: -5px;
}

.step-item:after {
  display: inline-block;
  content: '';
  width: 1px;
  height: 100%;
  background: #dddddd;
  position: absolute;
  top: 3px;
  left: 0px;
}

.step-item:last-of-type:after {
  display: none;
}

.flowName:hover {
  color: #3E82FF;
}

.text-red {
  color: #F56C6C;
}
</style>
