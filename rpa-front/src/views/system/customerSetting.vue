<template>
  <div class="spl-container">
    <breadcrumb :data="pathData">
      <el-button type="primary" slot="breadcrumb-btn" size="mini" @click="addOrUpdate">确定保存</el-button>
    </breadcrumb>
    <div class="pl-20 pr-20 pt-20 pb-20">
      <el-form ref="formData" :model="formData" label-width="185px" :rules="rules">
        <p class="top-title">基础信息</p>
        <el-row style="padding: 10px 20px;">
          <el-col :span="8">
            <el-form-item prop="name" label="客户名称：">
              <el-input
                v-model.trim="formData.name"
                placeholder="请输入"
                style="width: 212px;"
                :disabled="Boolean(type)"
                clearable
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="businessType" label="业务类型：">
              <el-select v-model="formData.businessType" placeholder="请选择" clearable>
                <el-option
                  v-for="item in options['businessType']"
                  :key="item.dictCode"
                  :label="item.dictName"
                  :value="item.dictCode"
                ></el-option>
              </el-select>
              <el-tooltip
                placement="top"
                effect="light"
                :content="handleBusinessType(formData.businessType)"
                v-if="handleBusinessType(formData.businessType)"
                class="tooltip"
              >
                <i
                  class="el-icon-question"
                  style="color:#FE8C40;cursor: pointer;font-size: 16px;margin-left:5px;"
                ></i>
              </el-tooltip>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="platform" label="平台方：">
              <el-select v-model="formData.platform" placeholder="请选择" clearable>
                <el-option
                  v-for="item in options['platform']"
                  :key="item.dictCode"
                  :label="item.dictName"
                  :value="item.dictCode"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row style="padding: 0px 20px;">
          <el-col :span="8">
            <el-form-item prop="nature" label="企业性质：">
              <el-select v-model="formData.nature" placeholder="请选择" clearable>
                <el-option
                  v-for="item in options['nature']"
                  :key="item.dictCode"
                  :label="item.dictName"
                  :value="item.dictCode"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="industry" label="所属行业：">
              <el-select v-model="formData.industry" placeholder="请选择" clearable>
                <el-option
                  v-for="item in options['industry']"
                  :key="item.dictCode"
                  :label="item.dictName"
                  :value="item.dictCode"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="custAddress" label="企业地址：">
              <el-input
                v-model.trim="formData.custAddress"
                placeholder="请输入"
                style="width: 212px;"
                clearable
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row style="padding: 0px 20px;">
          <el-col :span="8">
            <el-form-item prop="category" label="企业类别：">
              <el-select v-model="formData.category" placeholder="请选择" clearable>
                <el-option
                  v-for="item in options['category']"
                  :key="item.dictCode"
                  :label="item.dictName"
                  :value="item.dictCode"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="16">
            <el-form-item label="客方对接人：">
              <div
                style="display: flex;align-items: center;"
                v-for="(item,index) in formData.contactPersonList"
                :key="index"
              >
                <el-form-item :prop="`contactPersonList.${index}.name`">
                  <el-input
                    v-model.trim="item.name"
                    placeholder="请输入姓名"
                    style="width: 212px;"
                    clearable
                  ></el-input>
                </el-form-item>
                <el-form-item
                  :prop="`contactPersonList.${index}.phoneNumber`"
                  style="margin-left: 10px;"
                  :rules="rules.phoneNumber"
                >
                  <el-input
                    v-model.trim="item.phoneNumber"
                    placeholder="请输入手机号"
                    style="width: 212px;"
                    clearable
                  ></el-input>
                </el-form-item>
                <el-form-item
                  :prop="`contactPersonList.${index}.email`"
                  style="margin-left: 10px;"
                  :rules="rules.email"
                >
                  <el-autocomplete
                    class="inline-input"
                    v-model="item.email"
                    :fetch-suggestions="querySearch"
                    placeholder="请输入电子邮箱"
                  ></el-autocomplete>
                </el-form-item>
                <i
                  class="el-icon-remove-outline"
                  style="font-size: 24px;margin:0 5px;cursor: pointer;color:#FD6154;"
                  v-if="formData.contactPersonList.length > 1"
                  @click="removeContactPersonList(index)"
                ></i>
                <i
                  class="el-icon-circle-plus-outline"
                  style="font-size: 24px;margin:0 5px;cursor: pointer;color: #3E82FF;"
                  @click="addContactPersonList"
                  v-if="index == formData.contactPersonList.length - 1"
                ></i>
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row style="padding: 0px 20px;">
          <el-col :span="16">
            <el-form-item label="备注：" prop="comment">
              <el-input
                v-model.trim="formData.comment"
                placeholder="请输入"
                style="width: 100%;"
                clearable
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <p class="top-title">脱敏配置</p>
        <el-row style="padding: 10px 20px;">
          <el-col :span="8">
            <el-form-item prop="desensitization" label="关键信息脱敏：">
              <el-switch
                v-model="formData.desensitization"
                :active-value="1"
                :inactive-value="0"
                active-color="#13ce66">
              </el-switch>
            </el-form-item>
          </el-col>
        </el-row>

        <p class="top-title">数据申报前开启校验控制</p>
        <el-row style="padding: 10px 20px;">
          <el-col :span="8">
            <el-form-item prop="declareValidateRule" label="数据申报前开启校验：">
              <el-switch
                v-model="formData.declareValidateRule"
                :active-value="1"
                :inactive-value="0"
                active-color="#13ce66">
              </el-switch>
              <span class="pl-10" style="color: sandybrown; font-style:italic;">开启时在爬回在册数据时，自动校验数据是否可申报</span>
            </el-form-item>
          </el-col>
        </el-row>

        <p class="top-title">报盘导出控制</p>
        <el-row style="padding: 10px 20px;">
          <el-col :span="8">
            <el-form-item prop="declareExpRule" label="根据申报期导出报盘：">
              <el-switch
                v-model="formData.declareExpRule"
                :active-value="1"
                :inactive-value="0"
                active-color="#13ce66">
              </el-switch>
              <span class="pl-10" style="color: sandybrown; font-style:italic;">开启则根据申报期规则进行报盘数据导出，关闭则对传入月份的整月数据报盘导出</span>
            </el-form-item>
          </el-col>
        </el-row>

        <p class="top-title">初始化账号</p>
        <el-row style="padding: 10px 20px;">
          <el-col :span="8">
            <el-form-item prop="user.username" label="登录账户：" :rules="rules.username">
              <el-input
                v-model.trim="formData.user.username"
                placeholder="请输入数字和英文的组合"
                style="width: 212px;"
                clearable
                @change="changeUserName"
                :disabled="Boolean(type)"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="登录密码：" v-if="type">
              <p>******</p>
            </el-form-item>
            <el-form-item prop="user.password" label="登录密码：" :rules="rules.password" v-if="!type">
              <el-input
                v-model.trim="formData.user.password"
                placeholder="自动生成"
                style="width: 212px;"
                clearable
                :disabled="true"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="role" label="分配角色：">
              <el-tag type="info">客户管理员</el-tag>
            </el-form-item>
          </el-col>
        </el-row>
        <p class="top-title">合同内容</p>
        <el-row style="padding: 10px 20px;">
          <el-col :span="8">
            <el-form-item prop="moduleListCache" label="服务模块：">
              <el-checkbox-group v-model="formData.moduleListCache">
                <el-checkbox
                  :label="item.dictCode"
                  v-for="(item,index) in options['moduleList']"
                  :key="index"
                  v-show="item.dictCode !='10024004' && item.dictCode != '10024005'"
                >{{ item.dictName }}</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="cityNumber" label="预计开通城市数：" :rules="rules.checkNumber">
              <el-input
                v-model.trim="formData.cityNumber"
                placeholder="请输入整数"
                style="width: 212px;"
                clearable
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="accountNumber" label="预计开通账户数：" :rules="rules.checkNumber">
              <el-input
                v-model.trim="formData.accountNumber"
                placeholder="请输入整数"
                style="width: 212px;"
                clearable
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row style="padding: 0 20px;">
          <el-col :span="8">
            <el-form-item prop="accountNumber" label="约定制单日：">
              <div style="display: flex;">
                <el-select
                  v-model="formData.billMonth"
                  placeholder="请选择"
                  style="width: 100px;"
                  clearable
                >
                  <el-option label="当月" :value="1"></el-option>
                  <el-option label="下月" :value="2"></el-option>
                </el-select>
                <el-select v-model="formData.billDay" placeholder="请选择">
                  <el-option
                    v-for="(item,index) in 31"
                    :key="index"
                    :label="item + '日'"
                    :value="item"
                  ></el-option>
                </el-select>
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="accountNumber" label="约定到款日：">
              <div style="display: flex;">
                <el-select
                  v-model="formData.accountMonth"
                  placeholder="请选择"
                  style="width: 100px;"
                  clearable
                >
                  <el-option label="当月" :value="1"></el-option>
                  <el-option label="下月" :value="2"></el-option>
                </el-select>
                <el-select v-model="formData.accountDay" placeholder="请选择">
                  <el-option
                    v-for="(item,index) in 31"
                    :key="index"
                    :label="item + '日'"
                    :value="item"
                  ></el-option>
                </el-select>
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <p style="color:#F56C6C;padding:10px;" v-if="type && !formData.status">合作终止原因：{{ formData.terminationReason ? formData.terminationReason : '-' }}</p>
        <el-row style="padding: 20px 20px;background: #f4f4f5;position: relative;">
          <el-col :span="24">
            <el-form-item label="合同期：" label-width="100px">
              <div
                style="display: flex;align-items: center;"
                v-for="(item,index) in formData.contractList"
                :key="index"
              >
                <el-form-item :prop="`contractList.${index}.signingDate`">
                  <el-date-picker
                    v-model="item.signingDate"
                    type="date"
                    placeholder="合同签订日期"
                    value-format="yyyy-MM-dd"
                    :disabled="formData.status == 0"
                  ></el-date-picker>
                </el-form-item>
                <el-form-item :prop="`contractList.${index}.code`" style="margin-left: 10px;">
                  <el-input
                    v-model.trim="item.code"
                    placeholder="合同编号"
                    style="width: 212px;"
                    clearable
                    :disabled="formData.status == 0"
                  ></el-input>
                </el-form-item>
                <el-form-item :prop="`contractList.${index}.beginDate`" style="margin-left: 10px;">
                  <el-date-picker
                    v-model="item.beginDate"
                    type="date"
                    placeholder="合同开始日期"
                    value-format="yyyy-MM-dd"
                    :disabled="formData.status == 0"
                  ></el-date-picker>
                </el-form-item>
                <el-form-item
                  :prop="`contractList.${index}.endDate`"
                  style="margin-left: 10px;"
                  :rules="rules.date"
                >
                  <el-date-picker
                    v-model="item.endDate"
                    type="date"
                    placeholder="合同结束日期"
                    value-format="yyyy-MM-dd"
                    :disabled="formData.status == 0"
                  ></el-date-picker>
                </el-form-item>
                <i
                  class="el-icon-remove-outline"
                  style="font-size: 24px;margin:0 5px;cursor: pointer;color:#FD6154;"
                  v-if="formData.contractList.length > 1 && formData.status != 0"
                  @click="removeContractList(index)"
                ></i>
                <i
                  class="el-icon-circle-plus-outline"
                  style="font-size: 24px;margin:0 5px;cursor: pointer;color: #3E82FF;"
                  @click="addContractList"
                  v-if="formData.status != 0 && index == formData.contractList.length - 1"
                ></i>
              </div>
              <img src="../../assets/images/on.png" alt="" v-if="type && this.formData.status == 1" style="position: absolute;right: -15px;top: -65px;">
              <img src="../../assets/images/off.png" alt="" v-if="type && !this.formData.status" style="position: absolute;right: -15px;top: -65px;">
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      type: '',
      formData: {
        desensitization:1,
        declareValidateRule: 0,
        declareExpRule: 1,
        contactPersonList: [{ email: '', name: '', phoneNumber: '' }],
        contractList: [{ beginDate: '', code: '', endDate: '', signingDate: '' }],
        user: {username:'',password:''},
        moduleListCache: ['10024001'],
        moduleList: [],
        cityNumber: '',
        accountNumber: '',
      },
      options: {
        businessType: [],
        platform: [],
        industry: [],
        nature: [],
        category: [
          {dictCode:1,dictName:"saas客户"},
          {dictCode:2,dictName:"独立部署客户"}
        ]
      },
      id: '',
      loading: null,
      rules: {
        name: [
          { required: true, message: '请输入客户名称', trigger: 'blur' },
          { required: true, validator: this.checkName, trigger: 'blur' },
        ],
        businessType: [{ required: true, message: '请选择业务类型', trigger: 'change' }],
        password: [{ required: true, validator: this.validatePassword, trigger: 'change' }],
        username: [
          { required: true, message: '请输入登录账户', trigger: 'blur' },
          { required: true, validator: this.checkUsername, trigger: 'blur' },
        ],
        category: [{ required: true, message: '请选择企业类别', trigger: 'change' }],
        moduleListCache: [{ type: 'array', required: true, message: '请选择服务模块', trigger: 'change' }],
        email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }],
        phoneNumber: [{ required: true, validator: this.checkPhone, trigger: 'change' }],
        checkNumber: [{ validator: this.checkNumber, trigger: 'blur' }],
        date: [{ validator: this.checkDate, trigger: 'change' }],
      },
      emailList: [
        { value: '@qq.com' },
        { value: '@163.com' },
        { value: '@126.com' },
        { value: '@139.com' },
        { value: '@sohu.com' },
        { value: '@sina.com' },
        { value: '@aliyun.com' },
      ],
    }
  },
  computed: {
    handleBusinessType() {
      return function (val) {
        var str = ''
        this.options['businessType'].forEach((item) => {
          if (item.dictCode == val) {
            str = item.comment
          }
        })
        return str
      }
    },
  },
  created() {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = this.$global.deepcopyArray(tabs[this.$route.meta.parentPath])
    }
    this.type = this.$route.query.type
    this.id = this.$route.query.id
    if (this.type == 'edit') {
      this.pathData.push({ name: '编辑客户' })
    } else {
      this.pathData.push({ name: '新增客户' })
    }
    this.getDictByKey('10024', 'moduleList') //获取字典值-业务类型
    this.getDictByKey('10025', 'businessType') //获取字典值-业务类型
    this.getDictByKey('10011', 'platform') // 获取字典值-平台方
    this.getDictByKey('10012', 'industry') // 获取字典值-所属行业
    this.getDictByKey('10013', 'nature') // 获取字典值-企业性质
    if (this.id) {
      this.getCustomerById()
    }
  },
  methods: {
    async checkUsername(rule, value, callback) {
      if (this.type) {
        return callback()
      }
      var err = await this.userValidate(value, this.type ? this.formData.id : '')
      if (err) {
        return callback(new Error(err))
      }
      return callback()
    },
    async checkName(rule, value, callback) {
      var err = await this.customerValidate(value, this.type ? this.formData.id : '')
      if (err) {
        return callback(new Error(err))
      }
      return callback()
    },
    validatePassword(rule, value, callback) {
      var pattern = /^.*(?=.{6,}).*$/
      if (!value && this.type) {
        return callback()
      }
      // if (!value) {
      //   return callback(new Error('请输入登录密码'))
      // }
      // if (!pattern.test(value)) {
      //   return callback('请输入至少6位的密码')
      // }
      return callback()
    },
    checkDate(rule, value, callback) {
      var index = rule.field.split('.')[1]
      if (!value) {
        return callback()
      }
      if (new Date(this.formData.contractList[index].endDate).getTime() < new Date(this.formData.contractList[index].beginDate).getTime()) {
        return callback(new Error('合同结束日期需大于合同开始日期'))
      }
      return callback()
    },
    checkPhone(rule, value, callback) {
      if (!value) {
        return callback()
      }
      if (value.length < 11 || value.length > 11) {
        return callback(new Error('请输入正确的手机号码'))
      }
      return callback()
    },
    checkNumber(rule, value, callback) {
      if (!value) {
        return callback()
      } else if (isNaN(value)) {
        return callback(new Error('请输入正整数'))
      } else if (String(value).indexOf('.') > -1) {
        return callback(new Error('请输入正整数'))
      } else if (value <= 0 && value != '') {
        return callback(new Error('请输入大于0的数'))
      }
      return callback()
    },
    // 获取字典值
    getDictByKey(key, field) {
      let that = this
      this.$http({
        url: 'policy/sys/dict/getDictByKey',
        method: 'get',
        params: {
          dataKey: key,
        },
      })
        .then((res) => {
          that.options[field] = res.data.data
        })
        .catch()
    },
    removeContactPersonList(index) {
      let that = this
      this.$confirm('是否确定移除当前行信息？', '提示', {
        confirmButtonText: '移除',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning',
      })
        .then(() => {
          that.formData.contactPersonList.splice(index, 1)
        })
        .catch(() => {})
    },
    addContactPersonList() {
      this.formData.contactPersonList.push({ email: '', name: '', phoneNumber: '' })
    },
    removeContractList(index) {
      let that = this
      this.$confirm('是否确定移除当前行信息？', '提示', {
        confirmButtonText: '移除',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning',
      })
        .then(() => {
          that.formData.contractList.splice(index, 1)
        })
        .catch(() => {})
    },
    addContractList() {
      this.formData.contractList.push({ beginDate: '', code: '', endDate: '', signingDate: '' })
    },
    getCustomerById() {
      this.showLoading()
      let that = this
      this.$http({
        url: `policy/sys/customer/getCustomerById/${this.id}`,
        method: 'post',
      })
        .then((res) => {
          this.formData = res.data.data
          this.$set(
            this.formData,
            'moduleListCache',
            res.data.data.moduleList.map((item) => {
              return item.moduleCode
            })
          )
          if(this.formData.contactPersonList.length <= 0){
            this.formData.contactPersonList =[{ email: '', name: '', phoneNumber: '' }]
          }
          if(this.formData.contractList.length <= 0){
            this.formData.contractList = [{ email: '', name: '', phoneNumber: '' }]
          }
          this.$nextTick(() => {
            this.$refs.formData.clearValidate()
          })
          this.closeLoading()
        })
        .catch((err) => {
          this.closeLoading()
        })
    },
    addOrUpdate() {
      let that = this
      this.$refs.formData.validate((valid) => {
        if (valid) {
          this.formData.moduleList = []
          this.options['moduleList'].forEach((item) => {
            if (this.formData.moduleListCache.indexOf(item.dictCode) > -1) {
              this.formData.moduleList.push({
                moduleCode: item.dictCode,
                moduleName: item.dictName,
              })
            }
          })
          if (!this.type) {
            //上传前将密码进行加密传输
            var cryptoJS = require('crypto-js')
            var key = cryptoJS.enc.Utf8.parse(this.$global.AES_KEY)

            var encryptedData = cryptoJS.AES.encrypt(this.formData.user.password, key, {
              mode: cryptoJS.mode.ECB,
              padding: cryptoJS.pad.Pkcs7,
            })
            this.formData.user.password = encryptedData.toString()
          }
          this.formData.contactPersonList = this.formData.contactPersonList.filter((item) => {
            return item.email || item.phoneNumber || item.name
          })
          this.formData.contractList = this.formData.contractList.filter((item) => {
            return item.signingDate || item.code || item.beginDate || item.endDate
          })
          this.showLoading()
          this.$http({
            url: `policy/sys/customer/addOrUpdate`,
            method: 'post',
            data: this.formData,
          })
            .then((res) => {
              this.closeLoading()
              this.$message.success('保存成功')
              setTimeout(() => {
                this.$router.push('/system/customerMange')
              }, 2000)
            })
            .catch((err) => {
              console.log(err)
              this.closeLoading()
            })
        }
      })
    },
    showLoading(target) {
      this.loading = this.$loading({
        target: target ? target : document.body,
        lock: true,
        text: '加载中',
        spinner: 'el-icon-loading',
        background: 'rgba(255, 255, 255, 0.7)',
      })
    },
    closeLoading() {
      if (this.loading && this.loading.close) {
        this.loading.close()
      }
    },
    querySearch(queryString, cb) {
      var restaurants = this.emailList
      var re =
        /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]+\.)+[a-zA-Z\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]{2,}))$/
      console.log(re.test(queryString))
      if (re.test(queryString)) {
        return cb([])
      }
      var results = queryString
        ? restaurants
            .filter((item) => {
              return item.value
            })
            .map((item) => {
              return { value: queryString + item.value }
            })
        : restaurants
      // 调用 callback 返回建议列表的数据
      cb(results)
    },
    async customerValidate(validateValue, id) {
      return this.$http({
        url: `policy/sys/customer/validate/${validateValue}`,
        method: 'post',
        params: {
          id: id,
        },
      })
        .then((res) => {
          return res.data.data
        })
        .catch((err) => {
          console.log(err)
        })
    },
    async userValidate(validateValue, id) {
      return this.$http({
        url: `/sys/user/validate/${validateValue}`,
        method: 'post',
        params: {
          id: id,
        },
      })
        .then((res) => {
          return res.data.data
        })
        .catch((err) => {
          console.log(err)
        })
    },
    changeUserName(val) {
      console.log(val)
      this.$set(this.formData.user, 'password', `${val}@121`)
      this.formData.user.password = `${val}@121`
    },
  },
}
</script>

<style lang="less" scoped>
.top-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}
/deep/.el-form-item__error {
  position: inherit !important;
}
</style>
