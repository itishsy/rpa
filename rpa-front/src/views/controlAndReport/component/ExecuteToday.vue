<template>
  <div>
    <div class="search-row">
      <el-row>
        <el-col :span="6">
          <span class="lab">参保城市：</span>
          <div class="input-item">
            <addrSelector
              v-model="formData.addrName"
              :addrArr="options.allAddr"
              width="100%"
              placeholder="请输入"
              @changeAddrSelect="changeAddrSelect"></addrSelector>
          </div>

        </el-col>
        <el-col :span="6">
          <span class="lab">客户名称：</span>
          <el-select placeholder="请选择" v-model="formData.custId" class="input-item" clearable filterable
                     @change="getRobotTaskList">
            <el-option v-for="(item, index) in options.listCustomerOption" :key="index" :label="item.customerName"
                       :value="item.id"></el-option>
          </el-select>
        </el-col>
        <el-col :span="6">
          <span class="lab">申报账户：</span>
          <el-select placeholder="请选择" v-model="formData.taskCode" class="input-item" clearable filterable>
            <el-option v-for="(item, index) in options.taskList" :key="index"
                       :label="item.orgName + '-' +item.accountNumber"
                       :value="item.taskCode"></el-option>
          </el-select>
        </el-col>
        <el-col :span="6">
          <span class="lab">上线阶段：</span>
          <el-select placeholder="请选择" v-model="formData.state" class="input-item" clearable filterable>
            <el-option v-for="(item, index) in options.onlineStatus" :key="index" :label="item.dictName"
                       :value="item.dictCode"></el-option>
          </el-select>
        </el-col>
      </el-row>
      <el-row class="mt-15">
        <el-col :span="6">
          <span class="lab">开发人员：</span>
          <el-input v-model="formData.devUserName" placeholder="请输入" class="input-item" clearable></el-input>
        </el-col>
        <el-col :span="6">
          <span class="lab">测试人员：</span>
          <el-input v-model="formData.testUserName" placeholder="请输入" class="input-item" clearable></el-input>
        </el-col>
        <el-col :span="6">
          <span class="lab">运维人员：</span>
          <el-input v-model="formData.ywUserName" placeholder="请输入" class="input-item" clearable></el-input>
        </el-col>
        <el-col :span="6" class="opt-col">
          <div class="count-box">
            <span>临期</span>
            <span class="count">{{ noDeclaredCount }}</span>
          </div>
          <el-button size="small" type="primary" class="ml-20 w-60" @click="getTableData">查询</el-button>
        </el-col>
      </el-row>
    </div>
    <div>
      <div>
        <dTable :data="tableData" v-loading="isLoadingTable" element-loading-text="拼命加载中"
                element-loading-spinner="el-icon-loading" style="margin-top: 0" :tableHeight="tableHeight"
                :showIndex="false" :showSelection="false" :paging="false">
          <u-table-column prop="addrName" label="参保城市" fixed="left" min-width="110" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <span>{{scope.row.addrName}}</span>
              <span class="flag flag-orange" v-if="scope.row.flag===1">临</span>
              <span class="flag" v-else-if="scope.row.flag===0">未</span>
            </template>
          </u-table-column>
          <u-table-column prop="businessTypeName" fixed="left" label="业务类型" min-width="80"
                          :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="orgName" label="申报单位" min-width="150"
                          :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="accountNumber" label="申报账户" min-width="250">
            <template slot-scope="scope">
              <div class="display-flex">
                <el-tooltip class="item" effect="dark" :disabled="scope.row.taskStatus!=='已停用'" :content="'操作人：'+scope.row.taskEditName+'。'+'停用原因：'+scope.row.taskComment" placement="top">
                  <span v-if="scope.row.taskStatus" class="tag" :class="{'tag-red': scope.row.taskStatus==='已停用',
                 'tag-blue': scope.row.taskStatus==='已启用'}">{{scope.row.taskStatus}}</span>
                </el-tooltip>
                <el-tooltip effect="dark" :content="scope.row.accountNumber" placement="top" class="flex1">
                  <div style=" white-space: nowrap;overflow: hidden;text-overflow: ellipsis;">
                    {{scope.row.accountNumber}}
                  </div>
                </el-tooltip>
              </div>
            </template>
          </u-table-column>
          <u-table-column prop="noDeclaredCount" label="未申报" min-width="80"
                          :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <span v-if="scope.row.noDeclaredCount!=null && scope.row.noDeclaredCount>0" class="text-red f-cursor a-aim" @click="goCurMonthInsurance(scope.row)">{{scope.row.noDeclaredCount}}</span>
              <span v-else>-</span>
            </template>
          </u-table-column>
          <u-table-column prop="noDeclareTimeLong" label="未报时长" min-width="80"
                          :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <span v-if="scope.row.noDeclareTimeLong!=null">{{scope.row.noDeclareTimeLong}}</span>
              <span v-else>-</span>
            </template>
          </u-table-column>
          <u-table-column prop="adventDays" label="临期天数" min-width="80"
                          :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <span class="text-red" v-if="scope.row.adventDays===0">{{scope.row.adventDays}}天</span>
              <span v-else>{{scope.row.adventDays}}{{scope.row.adventDays==null?'':'天'}}</span>
            </template>
          </u-table-column>
          <u-table-column prop="declareCycle" label="申报期" min-width="210"
                          :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="execPlant" label="执行计划" min-width="210" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="validateFailCount" label="校验失败(当日)" min-width="120"
                          :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <span v-if="scope.row.validateFailCount!=null" class="text-red">{{scope.row.validateFailCount}}</span>
              <span v-else>-</span>
            </template>
          </u-table-column>
          <u-table-column prop="machineCode" label="所在设备" min-width="220"
                          :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <span v-if="scope.row.machineStatus" class="tag" :class="{'tag-red': machineStatusTag.red.indexOf(scope.row.machineStatus)>-1 ,
                 'tag-blue': machineStatusTag.blue.indexOf(scope.row.machineStatus)>-1}">{{scope.row.machineStatus }}</span>
              <el-tooltip class="item" effect="dark" :disabled="!scope.row.machineFactory" :content="'设备厂商：'+scope.row.machineFactory" placement="left">
                <span>{{scope.row.machineCode}}</span>
              </el-tooltip>
            </template>
          </u-table-column>
          <u-table-column prop="remark" label="备注" min-width="150" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              {{scope.row.remark?scope.row.remark:'-'}}
            </template>
          </u-table-column>
          <u-table-column prop="failCount" label="申报失败（当月）" min-width="140" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              {{scope.row.failCount>0?scope.row.failCount:'-'}}
            </template>
          </u-table-column>
          <u-table-column prop="successCount" label="申报成功（当月）" min-width="140" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              {{scope.row.successCount>0?scope.row.successCount:'-'}}
            </template>
          </u-table-column>
          <u-table-column prop="customerName" label="客户名称" min-width="150"
                          :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="state" label="上线阶段" min-width="80" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="ywUserName" label="运维人员" min-width="80"
                          :show-overflow-tooltip="true">
            <template slot-scope="scope">
              {{scope.row.ywUserName?scope.row.ywUserName:'-'}}
            </template>
          </u-table-column>
          <u-table-column prop="testUserName" label="测试人员" min-width="80"
                          :show-overflow-tooltip="true">
            <template slot-scope="scope">
              {{scope.row.testUserName?scope.row.testUserName:'-'}}
            </template>
          </u-table-column>
          <u-table-column prop="devUserName" label="开发人员" min-width="80"
                          :show-overflow-tooltip="true">
            <template slot-scope="scope">
              {{scope.row.devUserName?scope.row.devUserName:'-'}}
            </template>
          </u-table-column>
          <u-table-column label="操作" align="left" fixed="right" width="60">
            <template slot-scope="scope">
              <span class="text-blue a-aim f-cursor" @click="handleView(scope.row)">查看</span>
            </template>
          </u-table-column>
        </dTable>
      </div>
    </div>

    <!--查看-->
    <el-drawer title="查看" :visible.sync="detailDrawer.show" :wrapperClosable="false"
               custom-class="spl-filter-drawer detail-drawer"
               :show-close="true" size="100%">
      <div style="padding: 20px;" v-if="detailDrawer.rowData">
        <el-row>
          <el-col :span="10" class="row-item">
            <p class="lab">参保城市：</p>
            <p class="sel">{{detailDrawer.rowData.addrName}} <span class="flag flag-orange" v-if="detailDrawer.rowData.flag===1">临</span>
              <span class="flag" v-else-if="detailDrawer.rowData.flag===0">未</span></p>
          </el-col>
          <el-col :span="7" class="row-item">
            <p class="lab">业务类型：</p>
            <p class="sel">{{detailDrawer.rowData.businessTypeName}}</p>
          </el-col>
          <el-col :span="7" class="row-item">
            <p class="lab">客户名称：</p>
            <p class="sel">{{detailDrawer.rowData.customerName}}</p>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="10" class="row-item">
            <p class="lab">所在设备：</p>
            <p class="sel">{{detailDrawer.rowData.machineCode}} <span v-if="detailDrawer.rowData.machineStatus" class="tag" :class="{'tag-red': machineStatusTag.red.indexOf(detailDrawer.rowData.machineStatus)>-1 ,
                 'tag-blue': machineStatusTag.blue.indexOf(detailDrawer.rowData.machineStatus)>-1}">{{detailDrawer.rowData.machineStatus }}</span></p>
          </el-col>
          <el-col :span="14" class="row-item">
            <p class="lab">申报账户：</p>
            <p class="sel">{{detailDrawer.rowData.orgName}}-{{detailDrawer.rowData.accountNumber}}</p>
          </el-col>
        </el-row>
        <div class="row-title">申报期</div>
        <el-row>
          <el-col :span="10" class="row-item">
            <p class="lab">增员：</p>
            <p class="sel">{{ handleDeclarePolicy(1) }}</p>
          </el-col>
          <el-col :span="14" class="row-item">
            <p class="lab">减员：</p>
            <p class="sel">{{ handleDeclarePolicy(2) }}</p>
          </el-col>
          <el-col :span="10" class="row-item">
            <p class="lab">补缴：</p>
            <p class="sel">{{ handleDeclarePolicy(5) }}</p>
          </el-col>
          <el-col :span="14" class="row-item">
            <p class="lab">调基：</p>
            <p class="sel">{{ handleDeclarePolicy(3) }}</p>
          </el-col>
        </el-row>

        <div class="row-title">执行计划</div>
        <table class="cust-table-border w-p100">
          <thead>
          <tr>
            <th style="width: 100px">执行顺序</th>
            <th>流程名称</th>
            <th>执行计划</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="(row, rowInd) in detailDrawer.rowData.schedules?detailDrawer.rowData.schedules:[]" :key="rowInd">
            <td>{{row.execOrder}}</td>
            <td>{{row.flowName}}</td>
            <td>{{row.execPlant}}</td>
          </tr>
          </tbody>
        </table>
        <p class="text-center text-gray mt-20" v-if="detailDrawer.rowData.schedules.length===0">暂无数据</p>

        <div class="row-title" style="margin-top: 20px;">未申报数据</div>
        <table class="cust-table-border w-p100">
          <thead>
          <tr>
            <th>申报类型</th>
            <th>未申报 <span v-if="detailDrawer.rowData.noDeclaredCount!=null">（{{detailDrawer.rowData.noDeclaredCount}}）</span></th>
            <th>操作</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="(row, rowInd) in detailDrawer.rowData.noDeclareList" :key="rowInd">
            <td>{{row.changeTypeName}}</td>
            <td>{{row.empCount}} <span class="tag tag-red ml-20" v-if="row.error===1">异常</span></td>
            <td><span class="text-blue a-aim f-cursor" @click="onDoQuery(row)">执行查询</span></td>
          </tr>
          </tbody>
        </table>
        <p class="text-center text-gray mt-20" v-if="detailDrawer.rowData.noDeclareList.length===0">暂无数据</p>

        <div class="drawer-footer-buts">
          <el-button class="btn--border-blue s-btn mr-0" @click="detailDrawer.show = false">关闭</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>
<script>
import dTable from '../../../components/common/table'
import addrSelector from '../../../components/common/addrSelector.vue'
import { parseDeclarePolicy } from '@/utils/socialAccfundProduct'

export default {
  components: { addrSelector, dTable },
  props: [],
  data () {
    return {
      formData: {
        addrId: '',
        addrName: '',
        custId: '',
        taskCode: '',
        state: '',
        devUserName: '',
        testUserName: '',
        ywUserName: ''
      },
      options: {
        allAddr: [],
        listCustomerOption: [],
        taskList: [],
        state: [],
        onlineStatus: [
          { dictCode: 0, dictName: '调研' },
          { dictCode: 1, dictName: '配置' },
          { dictCode: 2, dictName: '上线' },
          { dictCode: 3, dictName: '运维' }
        ]
      },
      isLoadingTable: false,
      tableData: [],
      noDeclaredCount: '', // 临期
      machineStatusTag: {
        red: ['已关闭', '程序异常', '客户端异常', '升级失败', '离线'],
        blue: ['初始化', '准备就绪', '正在升级']
      },
      detailDrawer: {
        show: false,
        rowData: null
      }
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
      console.log('this.tabs', this.pathData)
    }
    this.getAddrArr()
    this.getListCustomer()
    this.getRobotTaskList('init')
    this.$nextTick(() => {
      this.getTableData()
    })
  },

  mounted () {
  },
  methods: {
    getTableData () {
      this.isLoadingTable = true
      this.$http({
        url: '/robot/data/monitor/today/exec/list',
        method: 'post',
        data: {
          addrId: this.formData.addrId,
          custId: this.formData.custId,
          taskCode: this.formData.taskCode,
          state: this.formData.state,
          devUserName: this.formData.devUserName,
          testUserName: this.formData.testUserName,
          ywUserName: this.formData.ywUserName
        }
      }).then(({ data }) => {
        if (data.code == 200) {
          this.isLoadingTable = false
          this.tableData = data.data.list
          this.noDeclaredCount = data.data.noDeclaredCount
        }
      }).catch((data) => {
        this.isLoadingTable = false
      })
    },
    // 获取参保城市
    getAddrArr () {
      this.$http({
        url: '/policy/declareAddr/addrList',
        method: 'post'
      }).then(({ data }) => {
        if (data.code == 200) {
          this.options.allAddr = data.data
        }
      }).catch((data) => {
      })
    },
    // 改变参保地
    changeAddrSelect (item) {
      if (this.formData.addrId == item.id) {
        return
      }
      this.formData.addrId = item.id
    },
    getListCustomer () {
      this.$http({
        url: '/robot/app/client/listCustomer',
        method: 'post'
      }).then((data) => {
        if (data.data.code == 200) {
          this.options.listCustomerOption = data.data.data
        }
      }).catch((err) => {
      })
    },
    getRobotTaskList (type) {
      this.$http({
        url: '/robot/app/client/getRobotTaskList',
        method: 'post',
        params: {
          clientId: this.formData.custId,
          appCode: ''
        }
      }).then((data) => {
        if (data.data.code == 200) {
          if (type !== 'init') {
            this.formData.taskCode = ''
          }

          this.options.taskList = data.data.data
        }
      }).catch(() => {
      })
    },

    //   查看
    handleView (row) {
      row.deadlineSettings = row.deadlineSettings ? row.deadlineSettings : []
      row.schedules = row.schedules ? row.schedules : []
      row.noDeclareList = row.noDeclareList ? row.noDeclareList : []
      this.detailDrawer.rowData = row
      this.detailDrawer.show = true
    },
    handleDeclarePolicy (declareType) {
      let list = this.detailDrawer.rowData.deadlineSettings
      return parseDeclarePolicy(list, declareType)
    },
    onDoQuery (row) {
      // 传参【客户，应用，申报账户】跳转执行查询页，并定位到该类型主流程执行过程中，最新一次发生异常的执行记录。并将执行记录定位到发生异常的节点
      let rowData = this.detailDrawer.rowData
      let routeData = this.$router.resolve({
        path: '/customerAppList/customerIndex', // 需要打开的路由名称
        query: {
          clientId: rowData.clientId,
          appCode: rowData.appCode,
          taskCode: rowData.taskCode,
          filterFlowCodes: row.flowCodes != null ? row.flowCodes.join(',') : null,
          goExecutionQuery: true
        }
      })
      window.open(routeData.href, '_blank')
    },

    //   传参【地区、申报账户、申报状态（待申报、申报中）】进入当月参保情况
    goCurMonthInsurance (row) {
      this.$router.push({
        path: '/socialAccfund/curMonthInsurance',
        query: {
          businessType: row.businessTypeName === '社保' ? 1 : 2,
          addrId: row.addrId,
          addrName: row.addrName,
          accountNumber: row.accountNumber,
          clearCreateTime: true,
          statusList: '1,2'
        }
      })
    }

  }
}
</script>
<style lang="less" scoped>
.lab {
  display: inline-block;
  width: 70px;
  text-align: right;
}

.input-item {
  display: inline-block;
  width: 90%;
  max-width: 200px;

  /deep/ .addr-main {
    width: 100%;
  }
}

.opt-col {
  display: flex;
  padding-left: 70px;

  .count-box {
    flex: 1;
    max-width: 200px;
    box-sizing: border-box;
    border: 1px solid #dbdbdb;
    border-radius: 6px;
    padding: 6px 10px;
    display: flex;
  }

  .count {
    font-weight: bold;
    color: @orangeColor;
    flex: 1;
    text-align: right;
  }

}
.flag{
  display: inline-block;
  width: 22px;
  height: 22px;
  line-height: 22px;
  text-align: center;
  color: #606266;
  margin-left: 5px;
  border-radius: 30px;
  font-size: 12px;
  background-color: #e4e4e4;
}
.flag-orange{
  background-color: @orangeColor;
  color: #ffffff;
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
  margin-right: 5px;
}

.tag-red {
  background-color: #fde2e2;
  color: #CC0000;
}

.tag-blue {
  background-color: #d9ecff;
  color: @blueColor;
}

/deep/ .detail-drawer {
  width: 800px !important;
}
.row-title {
  font-weight: bold;
  margin-bottom: 15px;
  margin-top: 10px;
}

.row-item {
  display: flex;
  margin-bottom: 15px;

  .lab {
    width: 80px;
    text-align: right;
  }

  .sel {
    flex: 1;
    margin-left: 5px;
    word-break: break-all;
  }
}
</style>
