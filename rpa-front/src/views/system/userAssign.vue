<template>
  <div class="spl-container">
    <breadcrumb :data="pathData"></breadcrumb>
    <div class="pl-20 pr-20">
      <el-row>
        <el-col :span="24">
          <div class="search-row display-flex">
            <div style="display: flex;width: 100%;align-items: center;">
              <el-input @keyup.enter.native="search" v-model.trim="keyword" clearable placeholder="请输入关键字搜索"
                style="width:250px;" @clear="search">
                <i @click="search" slot="suffix" class="el-input__icon el-icon-search f-cursor font-16 fw-blod"
                  style="color: #dbdbdb;"></i>
              </el-input>

              <div class="display-flex" style="align-items: center;">
                <span class="ml-200 label">账号状态：</span>
                <el-checkbox-group v-model="status" @change="changeStatus">
                  <el-checkbox :label="1">使用中</el-checkbox>
                  <el-checkbox :label="0">已注销</el-checkbox>
                </el-checkbox-group>
              </div>


              <div class="flex1 text-right">
                <el-button type="primary" size="mini" @click="addCustomer" class="ml-20">添加后台用户</el-button>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
      <div>
        <dTable @fetch="getTableData" ref="table" style="margin-top: 0;" :tableHeight="tableHeight" :showIndex='true'
          :showSelection='false'>
          <u-table-column prop="name" label="用户姓名" min-width="150" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="phoneNumber" label="手机号" min-width="150" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="email" label="邮箱" min-width="150" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="username" label="登录账号" min-width="150" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="roles" label="分配角色" min-width="180" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <p style="overflow: hidden;text-overflow: ellipsis;">{{ handleRoleName(scope.row) }}</p>
            </template>
          </u-table-column>
          <u-table-column prop="customerNumber" label="分配客户" min-width="100" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <p>{{ scope.row.customerNumber=='0'?'-':scope.row.customerNumber }}</p>
            </template>
          </u-table-column>
          <u-table-column prop="cityNumber" label="分配城市" min-width="100" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <p>{{ scope.row.cityNumber=='0'?'-':scope.row.cityNumber}}</p>
            </template>
          </u-table-column>
          <u-table-column prop="socialAccountNumber" label="社保申报账户" min-width="100" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <p>{{ scope.row.socialAccountNumber=='0'?'-':scope.row.socialAccountNumber}}</p>
            </template>
          </u-table-column>
          <u-table-column prop="accfundAccountNumber" label="公积金申报账户" min-width="100" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <p>{{ scope.row.accfundAccountNumber=='0'?'-': scope.row.accfundAccountNumber}}</p>
            </template>
          </u-table-column>
          <u-table-column prop="status" label="账号状态" min-width="100" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <span>{{ scope.row.status ? '使用中' : '已注销' }}</span>
            </template>
          </u-table-column>

          <u-table-column label="操作" align="left" width="150" fixed="right">
            <template slot-scope="scope">
              <el-button type="text" size="small" class="text-blue" @click="handleEdit(scope.row)">编辑</el-button>
              <el-button type="text" size="small" class="text-blue" style="margin-left: 20px;"
                @click="handleSetPermissions(scope.row)">权限</el-button>
              <el-button v-if="!scope.row.status" type="text" size="small" class="text-grey"
                @click="changeUserStatus(scope.row, 1)">激活</el-button>
              <el-button v-else type="text" size="small" class="text-grey"
                @click="changeUserStatus(scope.row, 0)">注销</el-button>
            </template>
          </u-table-column>
        </dTable>
      </div>
    </div>


    <!-- 添加用户 -->
    <el-drawer :title="addCustomerForm.id == '' ? '添加用户' : '编辑用户'" :visible.sync="addCustomerForm.show"
      :wrapperClosable="false" custom-class="spl-filter-drawer" :show-close="true" :before-close="clearAddCustomerForm">
      <div class="drawer-body pt-20 pb-20">
        <el-form ref="addCustomerForm" :model="addCustomerForm" class="filter-form" :rules="rules">
          <label class="required item-label">用户姓名</label>
          <el-form-item label="" prop="name">
            <el-input v-model.trim="addCustomerForm.name" class="w-p100" maxlength="255" clearable
              placeholder="请输入"></el-input>
          </el-form-item>


          <label class="required item-label">手机号码</label>
          <el-form-item label="" prop="phoneNumber">
            <el-input v-model.trim="addCustomerForm.phoneNumber" class="w-p100" maxlength="255" clearable
              placeholder="请输入(用于接收提醒)"></el-input>
          </el-form-item>
          <label class="required item-label">邮箱</label>
          <el-form-item label="" prop="email">
            <el-input v-model.trim="addCustomerForm.email" class="w-p100" maxlength="255" clearable
              placeholder="请输入(用于接收提醒)"></el-input>
          </el-form-item>
          <!-- <p class="fw-blod mb-10 mt-20" style="margin-left: -20px;">登录账号</p> -->

          <label class="required item-label">登录账号</label>
          <el-form-item label="" prop="username" :rules="[{ required: true, message: '请输入登录账号', trigger: 'blur' }]">
            <el-input v-model.trim="addCustomerForm.username" class="w-p100" maxlength="50" clearable
              placeholder="请输入"></el-input>
          </el-form-item>

          <label class="item-label required">密码 </label>
          <el-button type="text" v-if="!showPwd" @click="clickResetPwd">忘记密码重置</el-button>
          <el-form-item label="" prop="password" v-else>
            <el-input v-model="addCustomerForm.password" placeholder="请输入密码"
              :style="{ width: addCustomerForm.edit ? '80%' : '100%' }"></el-input>
            <el-button type="text" v-if="showPwd && addCustomerForm.edit" @click="passwordCancel"
              style="margin-left:10px;">取消重置</el-button>
          </el-form-item>
          <!-- <el-form-item label="" prop="password" :rules="[{ required: true, message: '请输入密码', trigger: 'blur' }]">
            <el-input v-model.trim="addCustomerForm.password" class="w-p100" maxlength="50" clearable placeholder="请输入"
              show-password></el-input>
          </el-form-item> -->
          <label class="item-label required">账号状态</label>
          <el-form-item label="" prop="status">
            <!-- <label class="item-label2 required mr-20" style="line-height: 30px;">账号状态</label> -->

            <el-radio-group v-model="addCustomerForm.status" style="margin-top: 10px;">
              <el-radio :label="1">使用中</el-radio>
              <el-radio :label="0">已注销</el-radio>
            </el-radio-group>
          </el-form-item>

          <label class="item-label required">分配角色</label>
          <el-form-item label="" prop="roleIds">
            <el-select v-model="addCustomerForm.roleIds" class="w-p100" placeholder="请选择" clearable filterable multiple
              @change="onChangeRoles">
              <el-option v-for="it in allRoleList" :key="it.id" :label="it.name" :value="it.id"></el-option>
            </el-select>
          </el-form-item>


          <label class="item-label">备注</label>
          <el-form-item label="" prop="comment">
            <el-input v-model.trim="addCustomerForm.comment" class="w-p100" maxlength="255" clearable
              placeholder="请输入"></el-input>
          </el-form-item>



        </el-form>
      </div>
      <div class="drawer-footer-buts">
        <el-button class="mr-12" size="small" @click="clearAddCustomerForm()">取 消</el-button>
        <el-button size="small" type="primary" @click="vaildAddCustomerForm()"
          :loading="addCustomerForm.saveLoading">确定</el-button>
      </div>
    </el-drawer>

  </div>
</template>
<script>
import _ from 'lodash'
import dTable from '../../components/common/table'

export default {
  components: { dTable },
  data() {
    return {
      pathData: [],
      keyword: '',
      status: [1],
      platform: '',
      allRoleList: [],
      setForm: {
        show: false,
        saveLoading: false,
        id: '',
        roleList: []
      },
      showPwd: true,
      addCustomerForm: {
        show: false,
        saveLoading: false,
        id: '',
        name: '', // 公司名称
        email: '',
        roleIds: [],
        roles: [],
        password: '', // 用户密码
        platform: '', // 平台方
        username: '', // 用户名
        status: null,
        edit: false,
        passwordReset: false,
        phoneNumber: '' // 对接人联系手机号

      },
      option: {
        platform: []
      },
      rules: {
        name: [
          { required: true, message: '请输入用户姓名', trigger: 'blur' },
          // { validator: this.validateNameRepeat, trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入电子邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
        ],
        status: [
          {
            required: true, message: '请选择账号状态', trigger: 'blur'
          }
        ],
        roleIds: [
          {
            required: true, message: '请选择用户角色', trigger: 'blur'
          }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'change' },
          { validator: this.validatePassword, trigger: 'change' }
        ],
        phoneNumber: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1\d{10}$/, message: '请输入正确的手机号', trigger: ['blur', 'change'] }
        ]
      },
      rowId: '',
      formData: {
        bussinssType: ''
      }
    }
  },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 270 + 'px'
    }
  },
  watch: {

  },
  created() {
    let that = this
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = tabs[this.$route.path]
    }
    this.$nextTick(() => {
      that.getTableData()
    })
    this.getAllRole() // 获取全部启用的角色
  },
  mounted() {
  },
  methods: {
    validatePassword(rule, value, callback) {
      var pattern = /^.*(?=.{6,}).*$/
      if (!pattern.test(value)) {
        return callback('请输入至少6位的密码')
      }
      return callback()
    },
    clickResetPwd() {
      this.showPwd = true
      this.$nextTick(() => {

        this.$set(this.addCustomerForm, 'passwordReset', true)
        console.log('000000000000000000', this.addCustomerForm.passwordReset)
      })
    },
    passwordCancel() {
      this.addCustomerForm.password = ''
      this.showPwd = false
      this.addCustomerForm.passwordReset = false
    },
    changeUserStatus(row, status) {

      let that = this
      this.$confirm(status ? '账号激活后，可正常登录系统使用服务，是否继续？' : '账号注销，将无法在登录系统，是否继续？', '提示', {
        confirmButtonText: status ? '确定激活' : '确定注销',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning',
      })
        .then(() => {
          that.$http({
            url: `/sys/user/updateUserStatus/${row.id}/${status}`,
            method: 'post',
          }).then(res => {
            if (res.data.code == 200) {
              that.$message.success('操作成功')
              that.getTableData()
            } else {

            }
          })
        })
        .catch(() => { })
    },
    onChangeRoles(e) {
      console.log(e)
      this.$set(this.addCustomerForm, 'roles', e)
      // this.addCustomerForm.roleIds = e
      this.addCustomerForm.roles = this.allRoleList.filter(item => e.some(x => {
        return x === item.id
      }))
    },
    changeStatus(e) {
      console.log(e, 'chanageStatus')
      this.getTableData()
    },
    addCustomer() {
      this.addCustomerForm.show = true
      this.rowId = ''
    },
    validateNameRepeat(rule, value, callback) {
      this.$http({
        url: `/policy/sys/customer/validate/${value}`,
        method: 'post',
        params: {
          id: this.rowId
        }
      }).then(res => {
        if (res.data.code == 200) {
          if (!res.data.data) {
            return callback()
          } else {
            return callback(res.data.data)
          }
        }
      })
    },
    getTableData() {
      var params = [
        { property: 'keyword', value: this.keyword },
        { property: 'status', value: this.status }
      ]
      this.$refs.table.fetch({
        query: params,
        method: 'post',
        url: '/policy/sys/user/page'
      })
    },
    search() {
      this.getTableData()
    },
    // 获取全部启用的角色
    getAllRole() {
      this.$http({
        url: '/sys/role/getAll/1',
        method: 'post'
      }).then(res => {
        this.allRoleList = res.data.data
      })
    },
    // 获取字典值
    getDictByKey(key, field) {
      let that = this
      this.$http({
        url: 'policy/sys/dict/getDictByKey',
        method: 'get',
        params: {
          dataKey: key
        }
      }).then(res => {
        that.option[field] = res.data.data
      }).catch()
    },

    // 权限配置
    handleSetPermissions(row) {
      this.$router.push(`/system/userPermissions?userId=${row.id}`)
    },
    clearSetForm(tab) {
      this.setForm = {
        show: false,
        saveLoading: false,
        id: '',
        roleList: [],

      }
      this.$refs.setForm.resetFields()
    },

    // 新增客户
    clearAddCustomerForm(tab) {
      this.addCustomerForm = {
        show: false,
        saveLoading: false,
        id: '',
        name: '', // 公司名称
        email: '',
        roleIds: [],
        roles: [],
        password: '', // 用户密码
        platform: '', // 平台方
        username: '', // 用户名
        status: null,
        passwordReset: false,
        edit: false,
        phoneNumber: '' // 对接人联系手机号
      }
      this.showPwd = true
      this.$refs.addCustomerForm.resetFields()
    },
    handleEdit(row) {
      this.rowId = row.id
      // this.addCustomerForm = _.cloneDeep(row)
      this.getCustomerById(row.id)

      // this.addCustomerForm.show = true
    },

    vaildAddCustomerForm() {
      var that = this
      console.log('vaildAddCustomerForm')
      this.$refs.addCustomerForm.validate((valid) => {
        if (valid) {
          that.addCustomerForm.saveLoading = true
          var formData = _.cloneDeep(this.addCustomerForm)
          //上传前将密码进行加密传输
          if (formData.password) {
            var cryptoJS = require("crypto-js");
            var key = cryptoJS.enc.Utf8.parse(this.$global.AES_KEY)

            var encryptedData = cryptoJS.AES.encrypt(formData.password, key, {
              mode: cryptoJS.mode.ECB,
              padding: cryptoJS.pad.Pkcs7
            })
            formData.password = encryptedData.toString()
          }

          console.log(formData)
          that.$http({
            url: '/policy/sys/user/saveAdminUser',
            method: 'POST',
            data: formData
          }).then(({ data }) => {
            that.addCustomerForm.saveLoading = false
            that.$message.success('操作成功')
            that.getTableData()
            that.clearAddCustomerForm()
          }).catch(() => {
            that.addCustomerForm.saveLoading = false
          })
        }
      })
    },
    getCustomerById(id) {
      let that = this
      that.$http({
        url: `/sys/user/getAdminUser/${id}`,
        method: 'POST',
      }).then(({ data }) => {
        this.addCustomerForm = data.data
        this.$set(this.addCustomerForm, 'roleIds', data.data.roles.map(item => item.id))
        // this.addCustomerForm.roleIds = data.data.roles.map(item => item.id)
        this.addCustomerForm.show = true
        this.addCustomerForm.edit = true
        this.addCustomerForm.passwordReset = false
        this.showPwd = false
      }).catch(() => {
        // that.addCustomerForm.saveLoading = false
      })
    },
    handleRoleName(row) {
      var arr = row.roles ? row.roles : []
      var res = []
      arr.map(item => {
        res.push(item.name)
      })
      return res.join('，')
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

/deep/.el-form-item__content {
  line-height: 0px;
}

/deep/.el-form-item {
  margin-bottom: 10px;
}

.text-grey {
  color: #666666;
  margin-left: 20px;
}

/*.tag {*/
/*display: inline-block;*/
/*padding: 0 15px;*/
/*height: 30px;*/
/*line-height: 30px;*/
/*background: rgba(255, 255, 255, 1);*/
/*border: 1px solid rgba(206, 206, 206, 1);*/
/*border-radius: 0px 2px 2px 0px;*/
/*margin-right: 10px;*/
/*cursor: pointer;*/
/*}*/
/*.tag:hover, .tag.active {*/
/*background-color: #dddddd;*/
/*}*/
/*.table-fileName-list{*/
/*cursor: pointer;*/
/*&:hover{*/
/*color:#3e82ff;*/
/*text-decoration:underline;*/
/*}*/
/*}*/
/*.text-blue{*/
/*&:hover{*/
/*text-decoration: underline;*/
/*}*/
/*}*/
</style>
