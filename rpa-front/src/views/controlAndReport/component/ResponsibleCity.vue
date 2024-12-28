<template>
  <div>
    <div>
      <div class="search-row clearfix">
        <el-row>
          <el-col :span="6" class="display-flex">
            <span class="label">参保城市：</span>
            <div class="search-row-item">
              <addrSelector
                v-model="formData.addrName"
                :addrArr="allAddr"
                width="100%"
                placeholder="请输入"
                @changeAddrSelect="changeAddrSelect"></addrSelector>
            </div>
          </el-col>
          <el-col :span="6" class="display-flex">
            <span class="label" style="white-space:nowrap">客户名称：</span>
            <el-select v-model="formData.customerId" class="search-row-item" clearable filterable placeholder="请选择">
              <el-option
                v-for="item in listCustomerOption"
                :key="item.id"
                :label="item.customerName"
                :value="item.id"
              ></el-option>
            </el-select>
          </el-col>
          <el-col :span="6" class="display-flex">
            <span class="label" style="white-space:nowrap;">上线阶段：</span>
            <el-select v-model="formData.stateCode" class="search-row-item" clearable filterable placeholder="请选择">
              <el-option v-for="(item, index) in onlineStatus" :key="index" :label="item.dictName"
                         :value="item.dictCode"></el-option>
            </el-select>
          </el-col>
          <el-col :span="6" class="display-flex">
            <span class="label" style="white-space:nowrap;">业务类型：</span>
            <el-select v-model="formData.businessType" class="search-row-item" clearable filterable
                       placeholder="请选择">
              <el-option label="社保" value="社保"></el-option>
              <el-option label="公积金" value="公积金"></el-option>
            </el-select>
          </el-col>
        </el-row>
        <el-row class="mt-15">
          <el-col :span="6" class="display-flex">
            <span class="label" style="white-space:nowrap;">开发人员：</span>
            <el-input style="width: 213px;" v-model="formData.devUserName" clearable placeholder="请输入"></el-input>
          </el-col>
          <el-col :span="6" class="display-flex">
            <span class="label" style="white-space:nowrap;">测试人员：</span>
            <el-input style="width: 213px;" v-model="formData.testUserName" clearable placeholder="请输入"></el-input>
          </el-col>
          <el-col :span="6" class="display-flex">
            <span class="label" style="white-space:nowrap;">运维人员：</span>
            <el-input style="width: 213px;" v-model="formData.ywUserName" clearable placeholder="请输入"></el-input>
          </el-col>
          <el-col :span="6" class="text-right">
            <el-button type="primary" class="w-60" @click="getTableData">查询</el-button>
            <el-button class="btn--border-blue w-60" style="margin-left: 15px;" @click="setForm.show = true">添加
            </el-button>
          </el-col>
        </el-row>
      </div>
      <div>
        <dTable
          :tableHeight="tableHeight"
          :showIndex="true"
          rowKey="itemId"
          :data="tableData"
          :paging="false"
          ref="dTable"
        >
          <u-table-column prop="addrName" label="参保城市" width="80" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="customerName" label="客户名称" min-width="150" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="businessType" label="业务类型" width="80" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="stage" label="上线阶段" width="80" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="coreNum" label="Core未申报" width="125" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <el-tooltip class="item" effect="light" placement="top" :disabled="!scope.row.coreChangeNum">
                <div slot="content">{{ scope.row.coreChangeNum }}</div>
                <span :style="{color:scope.row.coreNum >0?'#FD6154': ''}">{{ scope.row.coreNum>0?scope.row.coreNum:'-' }}</span>
              </el-tooltip>
            </template>
          </u-table-column>
          <u-table-column prop="rpaNum" label="RPA未申报" :show-overflow-tooltip="true" width="120">
            <template slot-scope="scope">
              <el-tooltip class="item" effect="light" placement="top" :disabled="!scope.row.rpaChangeNum">
                <div slot="content">{{ scope.row.rpaChangeNum }}</div>
                <span :style="{color:scope.row.rpaNum >0?'#FD6154': ''}">{{ scope.row.rpaNum?scope.row.rpaNum:'-' }}</span>
              </el-tooltip>
            </template>
          </u-table-column>
          <u-table-column prop="xqUserName" label="调研人员" width="80" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              {{scope.row.xqUserName?scope.row.xqUserName:'-'}}
            </template>
          </u-table-column>
          <u-table-column prop="devUserName" label="开发人员" width="80" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              {{scope.row.devUserName?scope.row.devUserName:'-'}}
            </template>
          </u-table-column>
          <u-table-column prop="testUserName" label="测试人员" :show-overflow-tooltip="true" width="80">
            <template slot-scope="scope">
              {{scope.row.testUserName?scope.row.testUserName:'-'}}
            </template>
          </u-table-column>
          <u-table-column prop="ywUserName" label="运维人员" :show-overflow-tooltip="true" width="80">
            <template slot-scope="scope">
              {{scope.row.ywUserName?scope.row.ywUserName:'-'}}
            </template>
          </u-table-column>
          <u-table-column prop="remark" label="备注" :show-overflow-tooltip="true" min-width="180">
            <template slot-scope="scope">
              {{scope.row.remark?scope.row.remark:'-'}}
            </template>
          </u-table-column>
          <u-table-column label="操作" fixed="right" width="60">
            <template slot-scope="scope">
              <el-button type="text" size="small" @click="doEdit(scope.row)">编辑</el-button>
            </template>
          </u-table-column>
        </dTable>
      </div>
    </div>

    <!--新增、编辑-->
    <el-drawer :title="setForm.id===''?'新增':'编辑'" :visible.sync="setForm.show" :wrapperClosable="false"
               custom-class="spl-filter-drawer detail-drawer" :show-close="true" size="100%" @close="cancelSetForm">
      <div style="padding: 10px 20px;">
        <el-form :model="setForm" ref="setForm">
          <!--          编辑-->
          <div v-if="setForm.id!==''" class="mb-15">
            <div class="row-item mb-5">
              <label class="lab">参保城市：</label>
              <div class="sel">
                <p class="pt-10">{{ setForm.addrName }}</p>
              </div>
            </div>
            <div class="row-item mb-5">
              <label class="lab">客户名称：</label>
              <div class="sel">
                <p class="pt-10">{{ setForm.customerName }}</p>
              </div>
            </div>
            <div class="row-item">
              <label class="lab">业务类型：</label>
              <div class="sel">
                <p class="pt-10">{{ setForm.businessType }}</p>
              </div>
            </div>
          </div>
          <!--          新增-->
          <div v-else>
            <div class="row-item">
              <label class="required-pre lab">参保城市：</label>
              <div class="sel">
                <el-form-item label="" prop="addrName"
                              :rules="[{ required: true, message: '请输入参保城市',trigger: ['blur', 'change'] }]">
                  <addrSelector
                    v-model="setForm.addrName"
                    :addrArr="allAddr"
                    width="100%"
                    placeholder="请输入"></addrSelector>
                </el-form-item>
              </div>
            </div>
            <div class="row-item">
              <label class="required-pre lab">业务类型：</label>
              <div class="sel">
                <el-form-item label="" prop="businessType"
                              :rules="[{ required: true, message: '请选择业务类型',trigger:'change' }]">
                  <el-select v-model="setForm.businessType" clearable filterable placeholder="请选择" class="w-p100">
                    <el-option label="社保" value="社保"></el-option>
                    <el-option label="公积金" value="公积金"></el-option>
                  </el-select>
                </el-form-item>
              </div>
            </div>
          </div>

          <div class="row-item">
            <label class="lab">调研人员：</label>
            <div class="sel">
              <el-form-item label="" prop="xqUserName">
                <el-input v-model="setForm.xqUserName" clearable placeholder="请输入姓名"></el-input>
              </el-form-item>
            </div>
          </div>
          <div class="row-item">
            <label class="lab">运维人员：</label>
            <div class="sel">
              <el-form-item label="" prop="ywUserName">
                <el-input v-model="setForm.ywUserName" clearable placeholder="请输入姓名"></el-input>
              </el-form-item>
            </div>
          </div>
          <div class="row-item">
            <label class="lab">开发人员：</label>
            <div class="sel">
              <el-form-item label="" prop="devUserName">
                <el-input v-model="setForm.devUserName" clearable placeholder="请输入姓名"></el-input>
              </el-form-item>
            </div>
          </div>
          <div class="row-item">
            <label class="lab">测试人员：</label>
            <div class="sel">
              <el-form-item label="" prop="testUserName">
                <el-input v-model="setForm.testUserName" clearable placeholder="请输入姓名"></el-input>
              </el-form-item>
            </div>
          </div>
          <div class="row-item mt-5" v-if="setForm.id!==''">
            <label class="lab" style="padding-top: 5px;">备注：</label>
            <div class="sel">
              <el-form-item label="" prop="testUserName">
                <el-input v-model.trim="setForm.remark" type="textarea" rows="4" resize="none"
                          placeholder="请输入"></el-input>
              </el-form-item>
            </div>
          </div>
        </el-form>
        <div class="drawer-footer-buts">
          <el-button class="btn--border-blue s-btn mr-0" @click="cancelSetForm">取消</el-button>
          <el-button type="primary" class="s-btn" style="margin-left: 20px;" @click="doSave">保存</el-button>
        </div>
      </div>
    </el-drawer>

  </div>
</template>

<script>
import dTable from '../../../components/common/table'
import addrSelector from 'components/common/addrSelector.vue'

export default {
  components: { addrSelector, dTable },
  data () {
    return {
      formData: {
        addrId: '',
        addrName: '',
        customerId: '',
        businessType: '',
        stateCode: '',
        devUserName: '',
        testUserName: '',
        ywUserName: ''
      },
      tableData: [],
      allAddr: [],
      listCustomerOption: [],
      accountList: [],
      onlineStatus: [
        { dictCode: 0, dictName: '调研' },
        { dictCode: 1, dictName: '配置' },
        { dictCode: 2, dictName: '上线' },
        { dictCode: 3, dictName: '运维' }
      ],
      setForm: {
        show: false,
        id: '',
        addrName: '',
        customerName: '',
        businessType: '',
        xqUserName: '',
        ywUserName: '',
        devUserName: '',
        testUserName: '',
        remark: ''
      },
      loading: null
    }
  },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 300 + 'px'
    }
  },
  created () {
  },
  async mounted () {
    this.getAddrArr()
    this.getListCustomer()
    await this.getCurrentInfo()
    this.getTableData()
  },
  methods: {
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
        this.listCustomerOption = data.data.data
      })
    },
    getTableData () {
      this.$refs.dTable.loading = true
      var roles = []
      if (Object.prototype.toString.call(this.$store.state.userData.roles) == '[object Array]') {
        roles = this.$store.state.userData.roles.filter((item) => {
          return item != 'client'
        })
      }
      if (roles.length <= 0) {
        this.$refs.dTable.loading = false
        return
      }
      this.$http({
        url: 'agent/dev/msg/listAddrMsg',
        method: 'get',
        params: this.formData
      }).then((res) => {
        if (res.data.code == 200) {
          this.tableData = res.data.data
          this.$refs.dTable.loading = false
        }
      }).catch((err) => {
        this.$refs.dTable.loading = false
        this.$message.error('获取消息列表出错，请稍后再试')
      })
    },
    // 获取当前登录用户的信息
    getCurrentInfo () {
      return this.$http({
        url: '/api/user/current',
        method: 'post',
        data: {}
      }).then(({ data }) => {
        this.$store.commit('updateCompanyInfo', data.data)
        this.$store.commit('updateUserData', data.data)
      })
    },
    doEdit (row) {
      this.setForm.id = row.id
      this.setForm.addrName = row.addrName
      this.setForm.customerName = row.customerName
      this.setForm.businessType = row.businessType
      this.setForm.xqUserName = row.xqUserName
      this.setForm.ywUserName = row.ywUserName
      this.setForm.devUserName = row.devUserName
      this.setForm.testUserName = row.testUserName
      this.setForm.remark = row.remark
      this.setForm.show = true
    },
    doSave () {
      let that = this
      this.$refs.setForm.validate((valid) => {
        if (valid) {
          this.showLoading()
          let setObj = {}
          let url = ''
          let setForm = this.setForm
          if (setForm.id === '') {
            setObj = {
              addrName: setForm.addrName,
              businessType: setForm.businessType,
              xqUserName: setForm.xqUserName,
              ywUserName: setForm.ywUserName,
              devUserName: setForm.devUserName,
              testUserName: setForm.testUserName
            }
            url = '/policy/dev/userAddr/addDevUserAddr'
          } else {
            setObj = {
              id: setForm.id,
              addrName: setForm.addrName,
              businessType: setForm.businessType,
              xqUserName: setForm.xqUserName,
              ywUserName: setForm.ywUserName,
              devUserName: setForm.devUserName,
              testUserName: setForm.testUserName,
              remark: setForm.remark
            }
            url = '/agent/dev/msg/updateAddrMsg'
          }
          this.$http({
            url: url,
            method: 'post',
            data: setObj
          }).then(({ data }) => {
            that.closeLoading()
            if (data.code == 200) {
              that.cancelSetForm()
              that.$message.success('操作成功')
              that.getTableData()
            }
          }).catch(() => {
            that.closeLoading()
          })
        }
      })
    },
    cancelSetForm () {
      this.setForm = this.$options.data().setForm
      this.$nextTick(() => {
        this.$refs.setForm.clearValidate()
      })
    },
    showLoading (msg) {
      this.loading = this.$loading({
        target: document.body,
        lock: true,
        text: msg || '保存中',
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
.netAddress {
  text-decoration: underline;
  color: #3e82ff;
}

.label {
  width: 80px;
  text-align: right;
}

.row-item {
  display: flex;

  .lab {
    padding-top: 10px;
    width: 80px;
    text-align: right;
  }

  .sel {
    flex: 1;
  }

  /deep/ .addr-main {
    width: 100%;

    .ic-addr {
      margin-TOP: 14px !important;
    }
  }
}

/deep/ .el-form-item {
  margin-bottom: 10px;
}
</style>
