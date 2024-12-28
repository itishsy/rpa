<template>
  <div class="spl-container">
    <breadcrumb :data="pathData"></breadcrumb>
    <div class="pl-20 pr-20">
      <div class="search-row display-flex">
        <el-row class="flex1" :gutter="20">
          <el-col :span="6" class="display-flex">
            <span class="label">客户：</span>
            <el-select placeholder="请选择" filterable v-model="search.clientId" clearable @change="getMachineByClientId" class="flex1">
              <el-option v-for="(item, index) in options.custList" :key="index" :label="item.customerName"
                         :value="item.id"></el-option>
            </el-select>
          </el-col>
          <el-col :span="6" class="display-flex">
            <span class="label">所在设备：</span>
            <el-select placeholder="请选择" filterable v-model="search.machineCode" clearable class="flex1">
              <el-option v-for="(item, index) in options.machineCodeList" :key="index" :label="item.machineCode"
                         :value="item.machineCode"></el-option>
            </el-select>
          </el-col>
          <el-col :span="6" class="display-flex">
            <span class="label">维持状态：</span>
            <el-select placeholder="请选择" filterable v-model="search.status" clearable class="flex1">
              <el-option v-for="(item, index) in options.statusList" :key="index" :label="item.name"
                         :value="item.value"></el-option>
            </el-select>
          </el-col>
          <el-col :span="6" style="padding-right: 20px;">
            <el-input v-model.trim="search.keyWord" placeholder="请输入地区/应用等关键字搜索"
                      @keyup.enter.native="getTableData" clearable style="max-width: 260px;"></el-input>
          </el-col>
        </el-row>
        <el-button type="primary" @click="getTableData">查询</el-button>
        <el-button type="primary" @click="setForm.show=true" class="w-60" style="margin-left: 20px;">添加</el-button>
        <el-button type="primary" @click="showConfigFun" class="w-100" style="margin-left: 20px;">维持参数设置</el-button>
        <el-button type="primary" @click="againAllSync" class="w-100" style="margin-left: 20px;">全部重新同步</el-button>
      </div>
      <div>
        <dTable @fetch="getTableData" ref="dTable" :tableHeight="tableHeight" :paging="true" :showSelection="false" :showIndex="true" rowKey="id">
          <u-table-column prop="customerName" label="客户名称" min-width="150" :show-overflow-tooltip="true"/>
          <u-table-column prop="appName" label="应用名称" min-width="80" :show-overflow-tooltip="true"/>
          <u-table-column prop="accountAndOrgName" label="申报账户" min-width="200" :show-overflow-tooltip="true"/>
          <u-table-column prop="declareSystemName" label="申报系统" width="100" :show-overflow-tooltip="true"/>
          <u-table-column prop="port" label="远程端口号" width="120" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <span v-if="scope.row.sharePort">{{ scope.row.port }}/{{ scope.row.sharePort }}</span>
              <span v-if="!scope.row.sharePort">{{ scope.row.port }}</span>
            </template>
          </u-table-column>
          <u-table-column prop="status" label="维持状态" min-width="100" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <span v-if="scope.row.status == 1">浏览器关闭</span>
              <span v-if="scope.row.status == 2">会话丢失</span>
              <span v-if="scope.row.status == 3">会话维持</span>
            </template>
          </u-table-column>
          <u-table-column prop="machineCode" label="维持机器标识码" min-width="120" :show-overflow-tooltip="true"/>
          <u-table-column prop="startKeepTime" label="维持开始时间" min-width="130" :show-overflow-tooltip="true"/>
          <u-table-column prop="endKeepTime" label="维持失效时间" min-width="130" :show-overflow-tooltip="true"/>
          <u-table-column prop="fileId" label="会话失效截图" min-width="120" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <el-button v-if="scope.row.fileId" type="text" size="small" class="text-blue" @click="viewImage(scope.row)">查看</el-button>
            </template>
          </u-table-column>
          <u-table-column prop="disabled" label="状态" min-width="80" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <el-switch v-model="scope.row.disabled" @change="changeReceiveStatus($event,scope.row.id)" :active-value="1" :inactive-value="0" active-color="#13ce66" inactive-color="#e4e4e4"></el-switch>
            </template>
          </u-table-column>
          <u-table-column label="操作" align="left" fixed="right" width="150">
            <template slot-scope="scope">
              <div class="jkh-flex">
                <el-button type="text" size="small" class="text-blue" v-if="scope.row.status!=3" @click="handleLogin(scope.row)">登录</el-button>
                <el-button type="text" size="small" class="text-blue" @click="againSync(scope.row)">重新同步</el-button>
                <el-button type="text" size="small" class="text-blue" @click="editCertificate(scope.row)">编辑</el-button>
              </div>
            </template>
          </u-table-column>
        </dTable>
      </div>
    </div>

    <!-- 添加 -->
    <el-dialog :visible.sync="setForm.show" :title="dialogTitle" width="550px" class="cust-dialog" :show-close="true"
               :close-on-click-modal="false" :before-close="doClose">
      <div class="template-dialog-box">
        <el-form :model="setForm" ref="setForm" label-width="170px">
          <el-form-item label="客户：" prop="clientId" class="flex1" :rules="[{ required: true, message: '请选择客户', trigger: 'change' }]">
            <el-select v-model="setForm.clientId" @change="changeCust" clearable filterable style="width:300px">
              <el-option v-for="it in options.custList" :key="it.itemId" :label="it.customerName" :value="it.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="应用：" prop="appCode" class="flex1" :rules="[{ required: true, message: '请选择应用', trigger: 'change' }]">
            <el-select v-model="setForm.appCode" @change="changeApp" clearable filterable style="width:300px">
              <el-option v-for="it in options.appList" :key="it.appCode" :label="it.appName" :value="it.appCode"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="申报账户：" prop="taskCode" class="flex1" :rules="[{ required: true, message: '请选择申报账户', trigger: 'change' }]">
            <el-select v-model="setForm.taskCode" clearable filterable style="width:300px">
              <el-option v-for="it in options.accountNumberList" :key="it.taskCode" :label="it.accountAndOrgName" :value="it.taskCode"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="申报系统：" prop="declareSystem" class="flex1" :rules="[{ required: true, message: '请选择申报系统', trigger: 'change' }]">
            <el-select v-model="setForm.declareSystem" clearable filterable style="width:300px">
              <el-option v-for="it in options.declareSystemSelects" :key="it.dictCode" :label="it.dictName" :value="it.dictCode"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="刷新重试次数：" prop="retryNum" class="flex1">
            <el-input v-model="setForm.retryNum" clearable filterable style="width:300px" type="number" placeholder="默认3次"></el-input>
          </el-form-item>
          <el-form-item label="最长维持时间：" prop="maxKeepTime" class="flex1">
            <el-input v-model="setForm.maxKeepTime" clearable filterable style="width:300px" type="number" placeholder="单位小时(默认24小时)"></el-input>
          </el-form-item>
          <el-form-item label="访问维持链接：" prop="url" class="flex1 mt-20" style="margin-bottom: 20px;">
            <el-input v-model="setForm.url" type="textarea" :autosize="{ minRows: 4, maxRows: 46 }" clearable filterable style="width:300px">
            </el-input>
          </el-form-item>
          <el-form-item label="会话维持xpath：" prop="xpath" class="flex1" :rules="[{ required: true, message: '请输入会话维持xpath', trigger: 'change' }]">
            <el-input v-model="setForm.xpath" type="textarea" :autosize="{ minRows: 4, maxRows: 46 }" clearable filterable style="width:300px">
            </el-input>
          </el-form-item>
          <el-form-item label="会话丢失xpath：" prop="loseXpath" class="flex1">
            <el-input v-model="setForm.loseXpath" type="textarea" :autosize="{ minRows: 4, maxRows: 46 }" clearable filterable style="width:300px">
            </el-input>
          </el-form-item>
          <el-form-item label="匹配表达式：" prop="matchExpress" class="flex1">
            <el-input v-model="setForm.matchExpress" type="textarea" :autosize="{ minRows: 4, maxRows: 46 }" clearable filterable style="width:300px"
                      placeholder="获取会话维持xpath的文本值,变量为：elementText，通过jstl执行表达式,例如:${elementText.contains('34342342342')}" ></el-input>
          </el-form-item>
        </el-form>
        <div class="text-right mt-30">
          <el-button size="small" class="mr-15 w-80" @click="doClose()">取消</el-button>
          <el-button size="small" class="w-80" type="primary" @click="doSumbit()">确定</el-button>
        </div>
      </div>
    </el-dialog>

    <el-dialog :visible.sync="showImg" title="失效截图" width="550px" class="cust-dialog" :show-close="true" :close-on-click-modal="false" :before-close="doClose3">
      <div class="template-dialog-box">
        <img :src="showImgSrc" alt="" style="width:500px;height: 500px;">
      </div>
    </el-dialog>

    <el-dialog :visible.sync="showConfig" title="维持参数设置" width="650px" class="cust-dialog" :show-close="true"
               :close-on-click-modal="false" :before-close="doClose2">
      <div class="template-dialog-box">
        <dTable @fetch="getTableData2" ref="deviceTable" :tableHeight="tableHeight" :paging="true" :showSelection="false"
                :showIndex="false" rowKey="id">
          <u-table-column prop="machineName" label="设备名称" min-width="130" :show-overflow-tooltip="true"/>
          <u-table-column prop="machineCode" label="设备编号" min-width="130" :show-overflow-tooltip="true"/>
          <u-table-column prop="maxKeepNum" label="维持的最大数量" min-width="130" :show-overflow-tooltip="true"/>
          <u-table-column label="操作" align="left" fixed="right" width="80">
            <template slot-scope="scope">
              <div class="jkh-flex">
                <el-button type="text" size="small" class="text-blue" @click="updateConfig(scope.row)">编辑</el-button>
              </div>
            </template>
          </u-table-column>
        </dTable>
      </div>
    </el-dialog>

    <el-dialog :visible.sync="showConfig2" title="维持参数设置" width="550px" class="cust-dialog" :show-close="true"
               :close-on-click-modal="false" :before-close="doClose4">
      <div class="template-dialog-box">
        <el-form :model="setForm2" ref="setForm" label-width="170px">
          <el-form-item label="刷新重试次数：" prop="maxKeepNum" class="flex1">
            <el-input v-model="setForm2.maxKeepNum" clearable filterable style="width:300px" type="number" placeholder="维持的最大数量"></el-input>
          </el-form-item>
        </el-form>
        <div class="text-right mt-30">
          <el-button size="small" class="mr-15 w-80" @click="doClose4()">取消</el-button>
          <el-button size="small" class="w-80" type="primary" @click="updateConfigById()">确定</el-button>
        </div>
      </div>
    </el-dialog>

    <!--  登录弹窗  -->
    <loginDialog ref="loginDialog"></loginDialog>
  </div>
</template>
<script>

import dTable from '../../../components/common/table'
import loginDialog from '@/views/robotManage/sessionManage/components/loginDialog.vue'

export default {
  components: { dTable, loginDialog },
  data () {
    return {
      dialogTitle: '添加',
      pathData: [],
      showImg: false,
      showConfig: false,
      showConfig2: false,
      showImgSrc: '',
      search: {
        clientId: '',
        machineCode: '',
        status: null,
        keyWord: ''
      },
      setForm: {
        show: false,
        clientId: '',
        appCode: '',
        taskCode: '',
        port: '',
        url: '',
        xpath: '',
        loseXpath: '',
        retryNum: '',
        maxKeepTime: '',
        declareSystem: '',
        matchExpress: ''
      },
      setForm2: {
        id: null,
        maxKeepNum: ''
      },
      options: {
        custList: [],
        machineCodeList: [],
        statusList: [{ 'name': '浏览器关闭', 'value': 1 }, { 'name': '会话丢失', 'value': 2 }, { 'name': '会话维持', 'value': 3 }],
        appList: [],
        accountNumberList: [],
        declareSystemSelects: []
      }
    }
  },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 270 + 'px'
    }
  },
  created () {
    let that = this
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = tabs[this.$route.path]
    }
    this.getlistCustomer()
    this.$nextTick(() => {
      that.getTableData()
    })
    // this.getDictByKey(10007)
  },
  mounted () {
  },
  methods: {
    // 列表查询
    getTableData () {
      let search = this.search
      var params = [
        { property: 'clientId', value: search.clientId },
        { property: 'machineCode', value: search.machineCode },
        { property: 'keyword', value: search.keyWord },
        { property: 'status', value: search.status }
      ]
      this.$refs.dTable.fetch({
        query: params,
        method: 'post',
        url: '/robot/sessionKeep/page'
      })
    },
    getTableData2 () {
      var params = []
      this.$nextTick(() => {
        this.$refs.deviceTable.fetch({
          query: params,
          method: 'post',
          url: '/robot/version/client/list'
        })
      })
    },
    viewImage (row) {
      this.$http({
        url: '/policy/sys/file/getFileHttpUrl/' + row.fileId,
        method: 'get'
      }).then((data) => {
        this.showImg = true
        this.showImgSrc = data.data.data
      }).catch((err) => {
        this.showImg = false
        this.showImgSrc = ''
      })
    },
    // 编辑证书
    editCertificate (row) {
      this.dialogTitle = '编辑'
      this.isEdit = true
      let that = this
      this.$http({
        url: '/robot/sessionKeep/getById',
        method: 'get',
        params: {
          id: row.id
        }
      }).then((resp) => {
        if (resp.data.code == '200') {
          that.setForm = resp.data.data
          that.setForm.show = true
          that.changeCust()
          that.changeApp()
        }
      }).catch((err) => {
        this.isloading = false
        this.$message.error('接口出错，请稍后再试')
      })
    },
    updateConfig (row) {
      this.showConfig2 = true
      this.setForm2 = row
    },
    updateConfigById () {
      let that = this
      if (that.setForm2.maxKeepNum == '' || that.setForm2.maxKeepNum > 1000) {
        this.$message.error('维持最大数量不能为空且不为超过1000')
        return
      }
      this.$http({
        url: '/robot/version/client/updateById',
        method: 'post',
        params: {
          id: that.setForm2.id,
          maxKeepNum: that.setForm2.maxKeepNum
        }
      }).then((resp) => {
        if (resp.data.code == '200') {
          that.showConfig2 = false
          that.getTableData2()
        }
      }).catch((err) => {
        this.isloading = false
        this.$message.error('接口出错，请稍后再试')
      })
    },
    showConfigFun () {
      this.showConfig = true
      this.getTableData2()
    },
    againSync (row) {
      let that = this
      this.$confirm('是否确定重新同步？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: '/robot/sessionKeep/againSync',
          method: 'post',
          params: {
            id: row.id
          }
        }).then((resp) => {
          if (resp.data.code == '200') {
            that.$message.success('操作成功')
            this.getTableData()
          }
        }).catch((err) => {
          this.isloading = false
          this.$message.error('接口出错，请稍后再试')
        })
      })
    },
    againAllSync () {
      let that = this
      this.$confirm('是否确定重新同步？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: '/robot/sessionKeep/againSync',
          method: 'post'
        }).then((resp) => {
          if (resp.data.code == '200') {
            that.$message.success('操作成功')
            this.getTableData()
          }
        }).catch((err) => {
          this.isloading = false
          this.$message.error('接口出错，请稍后再试')
        })
      })
    },
    // 获取字典值
    async getDictByKey (key) {
      return this.$http({
        url: 'policy/sys/dict/getDictByKey',
        method: 'get',
        params: {
          dataKey: key
        }
      }).then((res) => {
        if (key == 10007) {
          // 申报系统下拉
          this.options.declareSystemSelects = res.data.data
        }
      }).catch((err) => {
        if (err.response.status == 401) {
          return
        }
        this.$message.error('字典接口出错，请稍后再试')
      })
    },
    getlistCustomer () {
      this.$http({
        url: '/robot/app/client/listCustomer',
        method: 'post'
      }).then((data) => {
        this.options.custList = data.data.data
      }).catch(() => {})
    },
    getMachineByClientId () {
      let clientId = this.search.clientId ? this.search.clientId : ''
      this.$http({
        url: '/robot/app/client/getMachineByClientId?clientId=' + clientId,
        method: 'get'
      }).then((data) => {
        if (data.data.code == '200') {
          this.search.machineCode = ''
          this.options.machineCodeList = data.data.data
        }
      }).catch(() => {
      })
    },
    getDeclareSystem () {
      let appCode = this.setForm.appCode ? this.setForm.appCode : ''
      this.$http({
        url: '/robot/sessionKeep/listDeclareSystem?appCode=' + appCode,
        method: 'get'
      }).then((data) => {
        if (data.data.code == '200') {
          this.options.declareSystemSelects = data.data.data
        }
      })
    },
    changeCust () {
      let clientId = this.setForm.clientId ? this.setForm.clientId : ''
      this.$http({
        url: '/robot/sessionKeep/listApp?clientId=' + clientId + '&runSupport=',
        method: 'get'
      }).then((data) => {
        this.options.appList = data.data.data
      })
    },
    changeApp () {
      let clientId = this.setForm.clientId ? this.setForm.clientId : ''
      let appCode = this.setForm.appCode ? this.setForm.appCode : ''
      this.$http({
        url: '/robot/sessionKeep/listTask?clientId=' + clientId + '&appCode=' + appCode,
        method: 'post'
      }).then((data) => {
        this.options.accountNumberList = data.data.data
      })
      this.getDeclareSystem()
    },
    doClose () {
      this.setForm = {
        show: false
      }
      this.$nextTick(() => {
        this.$refs.setForm.clearValidate()
      })
    },
    doClose2 () {
      this.showConfig = false
    },
    doClose3 () {
      this.showImg = false
    },
    doClose4 () {
      this.showConfig2 = false
    },
    doSumbit () {
      let that = this
      this.$refs.setForm.validate((valid) => {
        if (valid) {
          that.$global.showLoading()
          that.$http({
            url: '/robot/sessionKeep/save',
            method: 'post',
            data: that.setForm
          }).then((data) => {
            that.$global.closeLoading()
            that.doClose()
            that.$message.success('操作成功')
            that.getTableData()
          }).catch(() => {
            that.$global.closeLoading()
          })
        }
      })
    },
    changeReceiveStatus (val, id) {
      this.$global.showLoading()
      this.$http({
        url: `/robot/sessionKeep/disabled`,
        method: 'post',
        params: {
          id: id,
          disabled: val
        }
      }).then((res) => {
        this.$global.closeLoading()
        this.getTableData()
      })
    },
    handleLogin (row) {
      this.$refs.loginDialog.init(row)
    }
  }
}
</script>
<style lang="less" scoped>
/deep/.el-form-item{
  margin-bottom: 15px;
}
</style>
