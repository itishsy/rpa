<template>
  <div class="content" :class="{ 'spl-container': !isComponent }">
    <!-- 导航 -->
    <breadcrumb :data="pathData" v-if="!isComponent">
      <el-button type="plain" slot="breadcrumb-btn" style="color: #3e82ff; border-color: #3e82ff" size="mini"
        @click="handleAddDevice">添加设备
      </el-button>
    </breadcrumb>
    <div style="padding: 0px 20px 0 20px">
      <div class="search-row clearfix">
        <el-row>
          <el-col :span="7" class="display-flex">
            <span class="label">客户：</span>
            <el-select v-model="searchData.clientId" class="search-row-item" clearable filterable placeholder="请选择客户">
              <el-option v-for="item in listCustomerOption" :key="item.id" :label="item.customerName"
                :value="item.id"></el-option>
            </el-select>
          </el-col>
          <el-col :span="7" class="display-flex">
            <span class="label">设备：</span>
            <div class="date-editor-div search-row-item" style="display: flex; align-items: center; padding-right: 30px">
              <el-input v-model.trim="searchData.machineName" placeholder="请输入设备名称" clearable size
                class="width-260"></el-input>
            </div>
          </el-col>
          <el-col :span="7" class="display-flex">
            <span class="label">设备状态：</span>
            <div class="date-editor-div search-row-item" style="display: flex; align-items: center; padding-right: 0px">
              <el-select v-model="searchData.status" class="search-row-item" multiple clearable filterable
                placeholder="请选择设备状态">
                <el-option v-for="it in statusList" :key="it.id" :label="it.statusName" :value="it.id"></el-option>
              </el-select>
            </div>
          </el-col>
          <el-col :span="3" class="text-right">
            <el-button class="ml-15 w-60" type="primary" @click="getTableData">查询</el-button>
          </el-col>
        </el-row>
      </div>
      <!-- 表格 -->
      <dTable @fetch="getTableData" ref="deviceTable" :tableHeight="tableHeight" :paging="true" :showSelection="true"
        :showIndex="true" rowKey="id" :cancelMinHeight="isComponent">
        <u-table-column prop="clientName" label="客户名称" width="200" :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="machineName" label="设备名称" width="150" :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="machineCode" label="设备编号" width="200" :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="statusName" label="设备状态" width="100" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <span :style="{ color: getStatusColor(scope.row.statusName) }">{{
              getStatusType(scope.row.status)
            }}</span>
          </template>
        </u-table-column>
        <u-table-column prop="machineFactory" label="设备厂商" min-width="180" :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="type" label="设备类型" min-width="180" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <span>{{ scope.row.type == 2 ? "费用机器" : "数据机器" }}</span>
          </template>
        </u-table-column>
        <u-table-column prop="lastMaintenanceDate" label="最新维护日期" min-width="160" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <span>{{
              scope.row.lastMaintenanceDate
              ? $moment(scope.row.lastMaintenanceDate).format(
                'YYYY-MM-DD HH:mm'
              )
              : ''
            }}</span>
          </template>
        </u-table-column>
        <u-table-column prop="userName" label="维护负责人" min-width="100" :show-overflow-tooltip="true"></u-table-column>
        <u-table-column fixed="right" label="操作" :width="150">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="getDeviceInfo(scope.row)">设备信息</el-button>
            <el-button type="text" size="small" class="ml-25" @click="openLog(scope.row)">维护日志</el-button>
          </template>
        </u-table-column>
      </dTable>
    </div>

    <el-drawer :title="deviceData.id ? '设备信息' : '添加设备'" :visible.sync="dialogVisible" :wrapperClosable="false"
      custom-class="spl-filter-drawer add-drawer" :show-close="true" :before-close="doClose" size="20%">
      <div>
        <div class="drawerForm" style="padding: 15px 20px 20px 20px">
          <el-form :inline="true" :model="deviceData" ref="deviceForm" label-width="110px" :rules="rules">
            <el-form-item label="客户：" prop="clientId" style="width: 100%">
              <el-select v-model="deviceData.clientId" class="search-row-item" clearable filterable style="width: 250px"
                :disabled="deviceData.disabled">
                <el-option v-for="item in listCustomerOption" :key="item.id" :label="item.customerName"
                  :value="item.id"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="设备厂商：" prop="machineFactory" style="width: 100%">
              <el-input v-model.trim="deviceData.machineFactory" placeholder="请输入设备厂商" style="width: 250px"
                clearable></el-input>
            </el-form-item>
            <el-form-item label="设备状态：" prop="status" style="width: 100%">
              <el-select v-if="deviceData.id" v-model="deviceData.status" class="search-row-item" clearable
                filterable style="width: 250px" >
                <el-option v-for="item in machineStatusOptions" :key="item.status" :label="item.statusName"
                  :value="item.status"></el-option>
              </el-select>
              <span v-else>闲置</span>
            </el-form-item>
            <el-form-item label="设备类型：" prop="status" style="width: 100%">
              <el-select v-model="deviceData.type" class="search-row-item" clearable filterable style="width: 250px" >
                <el-option v-for="item in typeList" :key="item.value" :label="item.name" :value="item.value"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="设备名称：" prop="machineName" style="width: 100%">
              <el-input v-model.trim="deviceData.machineName" placeholder="请输入设备名称" style="width: 250px"
                clearable></el-input>
            </el-form-item>
            <el-form-item label="设备编号：" prop="machineCode" style="width: 100%">
              <el-input v-model.trim="deviceData.machineCode" placeholder="请输入设备编号" style="width: 250px"
                readonly></el-input>
              <!-- <el-button
                size="small"
                style="margin-left:10px;"
                @click="generateCode"
                v-if="deviceData.id"
              >重新生成</el-button> -->
            </el-form-item>
            <el-form-item label="设备费用：" prop="machineAmount" style="width: 100%">
              <el-input v-model.trim="deviceData.machineAmount" placeholder="请输入设备费用" style="width: 250px"
                clearable></el-input>
            </el-form-item>
            <el-form-item label="设备信息补充：" prop="remark" style="width: 100%">
              <el-input type="textarea" v-model.trim="deviceData.remark" placeholder="请输入设备信息补充"
                :autosize="{ minRows: 5, maxRows: 5 }" style="width: 250px; position: relative" clearable></el-input>
              <span class="text-position" :style="{
                color: deviceData.remark.length > 300 ? '#F56C6C' : '',
              }">{{ deviceData.remark.length }}/300</span>
            </el-form-item>
          </el-form>
        </div>
        <div class="drawer-footer-buts">
          <el-button class="btn--border-blue s-btn mr-0" @click="doClose">取消</el-button>
          <el-button type="primary" class="s-btn ml-12" @click="handleValid">确认</el-button>
        </div>
      </div>
    </el-drawer>
    <el-drawer title="维护日志" :visible.sync="dialogVisible2" :wrapperClosable="false"
      custom-class="spl-filter-drawer add-drawer" :show-close="true" :before-close="doCloseLog" size="30%">
      <div style="height: 100%">
        <div style="
            display: flex;
            flex-wrap: wrap;
            padding: 20px;
            box-sizing: border-box;
          ">
          <div class="lab-block">
            <span class="lab">客户：</span>
            <span class="content">
              <p v-if="maintenanceLog.clientName &&
                maintenanceLog.clientName.length < 20
                ">
                {{ maintenanceLog.clientName }}
              </p>
              <el-tooltip class="item" effect="dark" placement="top-start" v-else>
                <div slot="content">{{ maintenanceLog.clientName }}</div>
                <p class="conent-long">{{ maintenanceLog.clientName }}</p>
              </el-tooltip>
            </span>
          </div>
          <div class="lab-block">
            <span class="lab">应用名称：</span>
            <span class="content">
              <p v-if="maintenanceLog.machineFactory &&
                maintenanceLog.machineFactory.length < 20
                ">
                {{ maintenanceLog.machineFactory }}
              </p>
              <el-tooltip v-else class="item" effect="dark" placement="top-start">
                <div slot="content">{{ maintenanceLog.machineFactory }}</div>
                <p class="conent-long">{{ maintenanceLog.machineFactory }}</p>
              </el-tooltip>
            </span>
          </div>
          <div class="lab-block">
            <span class="lab">设备名称：</span>
            <span class="content">
              <p v-if="maintenanceLog.machineName &&
                maintenanceLog.machineName.length < 20
                ">
                {{ maintenanceLog.machineName }}
              </p>
              <el-tooltip v-else class="item" effect="dark" placement="top-start">
                <div slot="content">{{ maintenanceLog.machineName }}</div>
                <p class="conent-long">{{ maintenanceLog.machineName }}</p>
              </el-tooltip>
            </span>
          </div>
          <div class="lab-block">
            <span class="lab">设备编号：</span>
            <span class="content">
              <p v-if="maintenanceLog.machineCode &&
                maintenanceLog.machineCode.length < 20
                ">
                {{ maintenanceLog.machineCode }}
              </p>
              <el-tooltip v-else class="item" effect="dark" placement="top-start">
                <div slot="content">{{ maintenanceLog.machineCode }}</div>
                <p class="conent-long">{{ maintenanceLog.machineCode }}</p>
              </el-tooltip>
            </span>
          </div>
        </div>
        <div v-loading="isTuLoading" element-loading-text="拼命加载中..." element-loading-spinner="el-icon-loading"
          element-loading-background="rgba(244, 248, 248, 0.5)" class="drawerForm" style="
            padding: 15px 20px 20px 20px;
            position: relative;
            height: 80%;
            overflow-y: auto;
          ">
          <!-- 步骤条 -->
          <el-steps align-center direction="vertical" class="steps-box" v-if="maintenanceLog.stepbarList.length > 0">
            <i class="el-icon-top icon-top" v-show="maintenanceLog.stepbarList.length > 0"></i>
            <el-step :class="'step-item redNo'" v-show="maintenanceLog.stepbarList !== [] && maintenanceLog.stepbarList[0] &&  maintenanceLog.stepbarList[0].runStatus == 0">
              <template slot="icon">
                <span>未发布</span>
              </template>
              <template slot="title">
                <span class="click-pointer" @click="handleLog(1, 'publish')">发布</span>
              </template>
              <template slot="description">
                <el-divider class="step-line"></el-divider>
              </template>
            </el-step>
            <el-step :class="item.status == 1
                ? 'step-item blue'
                : item.status == 2
                  ? 'step-item redNo'
                  : 'step-item pupleNo'
              " v-for="(item, i) in maintenanceLog.stepbarList" :key="i">
              <template slot="icon">
                <span v-if="item.status == 1">正常</span>
                <span v-if="item.status == 2">已废弃</span>
                <span v-if="item.status == 3">需升级</span>
              </template>
              <template slot="title">
                <span v-if="i == 0" style="margin-left: 10px" class="click-pointer" @click="handAddLog()">添加日志</span>
                <span style="height: 19px; display: inline-block"></span>
              </template>
              <template slot="description">
                <el-divider class="step-line"></el-divider>
                <div class="content-border" v-if="item.openRemark">
                  {{ item.remark }}
                </div>
                <div class="step-left-time" v-if="item.status == 1 || item.status == 2 || item.status == 3
                  ">
                  {{ $moment(item.createTime).format('YYYY-MM-DD HH:mm:ss') }}
                  {{ item.userName }}
                </div>
                <div class="step-right-time" style="top: 46px">
                  {{ item.jiangetime }}
                </div>
                <!-- <div class="step-left-time" style="top: 33px">
                  {{ item.releaseName ? '发布人：' + item.releaseName : '' }}
                </div> -->
                <div class="right-icon">
                  <i class="el-icon-arrow-down" style="font-size: 14px; cursor: pointer"
                    @click="openRemark(true, maintenanceLog.stepbarList, i)" v-if="!item.openRemark"></i>
                  <i class="el-icon-arrow-up" style="font-size: 14px; cursor: pointer"
                    @click="openRemark(false, maintenanceLog.stepbarList, i)" v-else></i>
                </div>
              </template>
            </el-step>
          </el-steps>
          <div v-else>
            <el-empty>
              <span style="margin-left: 10px" class="click-pointer" @click="handAddLog()">添加日志</span>
              <!-- <el-button type="primary"> </el-button> -->
            </el-empty>
            <!-- <el-empty description="暂无维护日志"></el-empty> -->
          </div>
        </div>
        <div class="drawer-footer-buts">
          <el-button class="btn--border-blue mr-0" @click="doCloseLog">返回列表</el-button>
          <!-- <el-button type="primary" class="s-btn ml-12" @click="handleValid"
            >确认</el-button
          > -->
        </div>
      </div>
    </el-drawer>
    <el-dialog :close-on-click-modal="false" title="添加日志" :visible.sync="dialogVisibleAdd" width="30%"
      :before-close="closeLog">
      <div>
        <el-form :inline="true" :model="addLogForm" ref="addLogForm" label-width="110px" :rules="rulesAdd">
          <el-form-item label="设备状态" prop="status" style="width: 100%">
            <el-radio-group v-model="addLogForm.status">
              <el-radio :label="1">正常使用</el-radio>
              <el-radio :label="3">需升级</el-radio>
              <el-radio :label="2">已废弃</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="日志内容" prop="remark" style="width: 100%">
            <el-input type="textarea" :rows="3" placeholder="请输入内容" v-model="addLogForm.remark">
            </el-input>
          </el-form-item>
        </el-form>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="closeLog()">取 消</el-button>
        <el-button type="primary" @click="submitLog()">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import dTable from '../../components/common/table'
// 检测长度
const checkLength = function (str) {
  return function (rules, value, callback, info) {
    if (value.length > 50) {
      return callback(new Error(str + '不能大于50个字'))
    }
    return callback()
  }
}
const checkLength2 = function (rules, value, callback) {
  if (value.length > 300) {
    return callback(new Error('设备补充信息不能大于300个字'))
  }
  return callback()
}
export default {
  components: {
    dTable
  },
  props: ['configData'],
  data () {
    return {
      singleLog: {},
      addLogForm: {
        status: undefined,
        remark: undefined
      },
      dialogVisibleAdd: false,
      rulesAdd: {
        status: [{ required: true, message: '请选择', trigger: 'change' }],
        remark: [{ required: true, message: '请输入', trigger: 'blur' }]
      },
      isTuLoading: false,
      pathData: [],
      searchData: {
        clientId: '',
        machineName: '',
        status: []
      },
      statusList: [
        { name: 'New', id: 0, statusName: '初始化' },
        { name: 'Runnable', id: 1, statusName: '准备就绪' },
        { name: 'Running', id: 2, statusName: '正在执行' },
        { name: 'Closed', id: 3, statusName: '已关闭' },
        { name: 'RobotError', id: 4, statusName: '程序异常' },
        { name: 'ClientError', id: 5, statusName: '客户端异常' },
        { name: 'Upgrading', id: 6, statusName: '正在升级' },
        { name: 'UpgradeFailed', id: 7, statusName: '升级失败' },
        { name: 'Offline', id: 8, statusName: '离线' },
        { name: 'Unused', id: 9, statusName: '闲置' }
      ],
      typeList: [
        {value: 1, name: '数据机器'},
        {value: 2, name: '费用机器'},
      ],
      dialogVisible: false,
      dialogVisible2: false,
      deviceData: {
        id: '',
        clientId: '',
        type: '',
        machineFactory: '',
        machineName: '',
        machineCode: '',
        machineAmount: '',
        remark: '',
        status: 9 // 9 默认为闲置
      },
      maintenanceLog: {
        clientName: undefined,
        machineName: undefined,
        machineCode: undefined,
        machineFactory: undefined,
        stepbarList: []
      },
      loading: null,
      listCustomerOption: [],
      machineStatusOptions: [], // 编辑设备信息时 会读取当前设备状态 下拉值多加一个【闲置】
      rules: {
        clientId: [
          { required: true, message: '请选择客户', trigger: 'change' }
        ],
        machineFactory: [
          { required: true, message: '请输入设备厂商', trigger: 'change' },
          {
            validator: checkLength('设备厂商'),
            trigger: 'change',
            required: true
          }
        ],
        machineName: [
          { required: true, message: '请输入设备名称', trigger: 'change' },
          {
            validator: checkLength('设备名称'),
            trigger: 'change',
            required: true
          }
        ],
        machineCode: [
          { required: true, message: '请输入设备编号', trigger: 'change' }
        ],
        status: [
          { required: true, message: '请选择设备状态', trigger: 'change' }
        ],
        machineAmount: [
          { required: false, message: '请输入设备费用', trigger: 'change' },
          { validator: this.checkAmount, trigger: 'change', required: false }
        ],
        remark: [
          { required: false, message: '请输入设备补充信息', trigger: 'change' },
          { validator: checkLength2, trigger: 'change', required: false }
        ]
      },
      isComponent: false,
      tableUrl: '/robot/version/client/list'
    }
  },
  computed: {
    tableHeight: function () {
      if (this.isComponent) {
        return 0
      }
      return this.$global.bodyHeight - 270 + 'px'
    },
    getStatusType () {
      return function (status) {
        if (status == 0) {
          // 初始化状态、用户未登录
          // return 'New'
          return '初始化'
        }
        if (status == 1) {
          // 机器人已启动，准备就绪
          return '准备就绪'
          // return 'Runnable'
        }
        if (status == 2) {
          // 机器人正在执行任务中
          return '正在执行'
          // return 'Running'
        }
        if (status == 3) {
          // 关闭
          return '已关闭'
          // return 'Closed'
        }
        if (status == 4) {
          // 机器人程序内部异常
          return '程序异常'
          // return 'RobotError'
        }
        if (status == 5) {
          // 客户端内部异常
          return '客户端异常'
          // return 'ClientError'
        }
        if (status == 6) {
          // 机器人正在升级
          // return 'Upgrading'
          return '正在升级'
        }
        if (status == 7) {
          // 机器人升级失败
          return '升级失败'
          // return 'UpgradeFailed'
        }
        if (status == 8) {
          // 离线状态，超过10分钟没有心跳
          return '离线'
          // return 'Offline'
        }
        if (status == 9) {
          return '闲置'
          // return 'unused'
        }
        return ''
      }
    },
    getStatusColor () {
      return function (name) {
        if (
          name == 'RobotError' ||
          name == 'ClientError' ||
          name == 'UpgradeFailed' ||
          name == 'Offline'
        ) {
          return '#f56c6c'
        }
        if (name == 'Runnable') {
          return '#67c23a'
        }
        if (name == 'New' || name == 'Closed') {
          return '#959595'
        }
        if (name == 'Running' || name == 'Upgrading') {
          return '#409eff'
        }
        if (name == 'Unused') {
          return '#909090'
        }
        return '#303133'
      }
    }
  },
  watch: {
    'configData.customerOptions': {
      handler (val) {
        if (this.isComponent) {
          this.listCustomerOption = val
        }
      },
      deep: true,
      immediate: true
    }
  },
  created () {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = this.$global.deepcopyArray(tabs[this.$route.meta.parentPath])
    }
    this.pathData.push({ name: '客户设备维护' })
  },
  mounted () {
    if (this.configData) {
      this.isComponent = true
      this.tableUrl = this.configData.tableUrl
    } else {
      this.getlistCustomer()
      let status = this.$route.query.status
      if (status) {
        status = status.split(',')
        let statusArr = []
        status.map(item => {
          statusArr.push(Number(item))
        })
        this.searchData.status = statusArr
      }
    }
    this.$nextTick(() => {
      this.getTableData()
    })
  },
  methods: {
    handAddLog () {
      this.dialogVisibleAdd = true
    },
    submitLog () {
      this.$refs.addLogForm.validate((valid) => {
        if (valid) {
          this.$http({
            url: '/robot/version/client/log/save',
            method: 'post',
            data: {
              machineCode: this.singleLog.machineCode,
              ...this.addLogForm
            }
          }).then((res) => {
            if (res.data && res.data.code == 200) {
              this.$message.success(
                res.data.message ? res.data.message : '服务出错，请稍后再试'
              )
              this.getversionLog(this.singleLog)
              this.closeLog()
            }
          })
        } else {
          return false
        }
      })
    },
    closeLog () {
      this.dialogVisibleAdd = false
      this.addLogForm = {
        status: undefined,
        remark: undefined
      }
      this.$refs.addLogForm.resetFields()
    },
    getTableData () {
      let that = this
      let searchData = this.searchData
      var params = [
        { property: 'clientId', value: searchData.clientId },
        { property: 'machineName', value: searchData.machineName },
        { property: 'status', value: searchData.status.join(',') }
      ]
      if (that.isComponent) {
        params.push({ property: 'id', value: that.configData.id })
      }
      this.$refs.deviceTable.fetch({
        query: params,
        method: 'post',
        url: that.tableUrl
      })
    },
    // 添加设备信息
    handleAddDevice () {
      this.dialogVisible = true
      this.generateCode()
    },
    // 确定 修改/添加设备信息
    handleValid () {
      this.$refs.deviceForm.validate((valid) => {
        if (valid) {
          this.addDevice()
        }
      })
    },
    addDevice () {
      this.$http({
        url: '/robot/version/client/add',
        method: 'post',
        data: this.deviceData
      })
        .then((res) => {
          if (res.data && res.data.code == 200) {
            this.$message.success(
              res.data.message ? res.data.message : '服务出错，请稍后再试'
            )
            this.getTableData()
            this.$refs.deviceForm.resetFields()
            this.deviceData = this.$options.data().deviceData
            this.$nextTick(() => {
              this.dialogVisible = false
            })
          } else {
            this.$message.error(
              res.data.message ? res.data.message : '服务出错，请稍后再试'
            )
          }
        })
        .catch((err) => { })
    },
    // 打开列表设备信息
    getDeviceInfo (row) {
      this.dialogVisible = true
      this.machineStatusOptions = [
        { status: row.status, statusName: this.getStatusType(row.status) }
      ]
      if (row.status != 9) {
        this.machineStatusOptions.push({ status: 9, statusName: '闲置' })
      }
      this.deviceData = {
        id: row.id,
        clientId: row.clientId,
        type: row.type,
        machineFactory: row.machineFactory,
        machineName: row.machineName,
        machineCode: row.machineCode,
        machineAmount: row.machineAmount,
        remark: row.remark ? row.remark : '',
        status: row.status || 9,
        disabled: true
      }
    },
    // 关闭设备信息
    doClose () {
      this.deviceData = this.$options.data().deviceData
      this.$refs.deviceForm.resetFields()
      this.$nextTick(() => {
        this.dialogVisible = false
      })
    },
    openLog (row) {
      Object.assign(this.maintenanceLog, {
        clientName: row.clientName,
        machineName: row.machineName,
        machineCode: row.machineCode,
        machineFactory: row.machineFactory
      })
      this.dialogVisible2 = true
      Object.assign(this.singleLog, row)
      this.getversionLog(row)
    },
    getversionLog (row) {
      this.isTuLoading = true
      this.$http({
        url: '/robot/version/client/log/list',
        method: 'post',
        data: {
          page: 1,
          start: 0,
          size: 100,
          query: [{ property: 'machineCode', value: row.machineCode }],
          sidx: '',
          sort: ''
        }
      })
        .then((res) => {
          this.closeLoading()
          this.isTuLoading = false
          if (res.data && res.data.code == 200) {
            // this.$set(this.maintenanceLog, res.data)
            let newdata = res.data.data
            newdata.forEach((el) => {
              el.openRemark = false
            })
            this.maintenanceLog.stepbarList = JSON.parse(
              JSON.stringify(newdata)
            )
          }
        })
        .catch((err) => {
          this.closeLoading()
          console.log(err)
        })
    },

    // 打开添加维护日志
    handleLog () { },
    // 关闭维护日志
    doCloseLog () {
      this.dialogVisible2 = false
      this.maintenanceLog = {
        clientName: undefined,
        machineName: undefined,
        machineCode: undefined,
        machineFactory: undefined,
        stepbarList: []
      }
    },
    // 维护日志--展开/关闭日志信息
    openRemark (type, data, i) {
      console.log('data[i]', data[i])
      if (type) {
        this.$set(data[i], 'openRemark', true)
      } else {
        this.$set(data[i], 'openRemark', false)
      }
    },
    // 请求后端生成设备码，添加设备时
    generateCode () {
      this.showLoading()
      this.$http({
        url: '/robot/version/client/generateCode',
        method: 'post'
      })
        .then((res) => {
          this.closeLoading()
          if (res.data && res.data.code == 200) {
            this.deviceData.machineCode = res.data.data
          } else {
            this.$message.error(
              res.data.message ? res.data.message : '设备码生成出错，请稍后再试'
            )
          }
        })
        .catch((err) => {
          this.closeLoading()
          console.log(err)
        })
    },
    getlistCustomer () {
      this.$http({
        url: '/robot/app/client/listCustomer',
        method: 'post'
      }).then((data) => {
        this.listCustomerOption = data.data.data
      }).catch((err) => { })
    },
    // 检测金额
    checkAmount (rules, value, callback, info) {
      var reg = /^(([1-9]{1}\d*)|(0{1}))(\.\d{1,2})?$/g
      if (value === '' || value == null) {
        return callback()
      }
      if (!reg.test(value)) {
        return callback(new Error('请输入正确金额，且只能保留2位小数'))
      }
      if (value == 0) {
        return callback(new Error('金额不能为0，请输入大于0的数字'))
      }
      var arr = String(value).split('.')
      if (arr[0].length > 10) {
        return callback(new Error('金额整数位不能超过10位，请修正'))
      }
      return callback()
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
    }
  }
}
</script>

<style lang="less" scoped>
.steps-box {
  position: absolute;
  left: 48%;
}

.lab-block {
  display: flex;
  align-items: center;
  width: 50%;
  margin-bottom: 10px;
  margin-top: 10px;

  .lab {
    white-space: nowrap;
  }
}

.click-pointer {
  color: #5a9cf8;
  cursor: pointer;
  font-size: 14px;
}

.content-border {
  position: absolute;
  top: 24%;
  /* margin-top: 25px; */
  border: 1px solid #ddd;
  border-radius: 5px;
  width: 200px;
  color: #666;
  // white-space: pre-wrap;
  overflow-y: scroll;
  height: 100px;
  padding: 5px;
  font-size: 12px;
  left: 35%;
}

.icon-top {
  font-size: 30px;
  color: #c1c4cb;
  margin-left: -4px;
}

/deep/ .el-step.is-vertical .el-step__head {
  width: 39px;
  height: 168px;
}

/deep/ .opopp .el-step__icon.is-text {
  border-radius: 50%;
  border: 2px solid;
  border-color: inherit;
  width: 50px;
  height: 50px;
  position: relative;
  left: -13px;
  font-size: 13px;
}

/deep/ .blue .el-step__icon.is-text {
  border-radius: 50%;
  border: 2px solid;
  border-color: #5a9cf8;
  width: 50px;
  height: 50px;
  position: relative;
  left: -13px;
  font-size: 13px;
  color: #5a9cf8;
}

/deep/ .redNo .el-step__icon.is-text {
  border-radius: 50%;
  border: 2px solid;
  border-color: red;
  width: 50px;
  height: 50px;
  position: relative;
  left: -13px;
  font-size: 13px;
  color: red;
}

/deep/ .pupleNo .el-step__icon.is-text {
  border-radius: 50%;
  border: 2px solid;
  border-color: #6a6793;
  width: 50px;
  height: 50px;
  position: relative;
  left: -13px;
  font-size: 13px;
  color: #6a6793;
}

/deep/ .el-step.is-vertical .el-step__line {
  margin-top: -18px;
}

/deep/ .el-step__line {
  height: 291px;
}

/deep/ .icon-top {
  margin-bottom: -8px;
}

.step-item {
  position: relative;

  .step-line {
    width: 141px;
    position: absolute;
    top: 0px;
    left: 40px;
  }

  .right-icon {
    position: absolute;
    top: 14px;
    left: 186px;
  }

  .step-left-time {
    position: absolute;
    top: 17px;
    left: -183px;
  }

  .step-right-time {
    position: absolute;
    top: 31px;
    right: 131px;
    white-space: nowrap;
  }

  .version-line {
    white-space: nowrap;
    position: absolute;
    top: 32px;
  }
}

.text-position {
  position: absolute;
  bottom: -6px;
  right: 0;
  font-size: 12px;
  line-height: 0;
}

.conent-long {
  overflow: hidden; //隐藏文字
  text-overflow: ellipsis; //显示 ...
  white-space: nowrap; //不换行
  width: 160px;
}
</style>
