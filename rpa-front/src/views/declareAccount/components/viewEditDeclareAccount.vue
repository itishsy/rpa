<template>
  <div>
    <el-drawer title="账户信息" :visible.sync="setForm.show" :wrapperClosable="false"
               custom-class="spl-filter-drawer add-drawer" :show-close="true" :before-close="doClose">
      <div>
        <div class="drawerForm" style="padding: 15px 20px 20px 20px;">
          <el-form :inline="true" :model="setForm" ref="setForm" label-width="0">
            <div class="type-title mt-0 clearfix"><span class="text">基本信息</span></div>
            <div style="padding: 10px;">

              <p class="item-label required-pre">客户名称</p>
              <el-form-item label="" prop="custId" :rules="[{ required: true, message: '请选择客户名称', trigger: 'change' }]">
                <el-select v-model="setForm.custId" placeholder="请选择客户名称" clearable filterable :disabled="setForm.isDisabled">
                  <el-option v-for="it in options.customerArr" :key="it.id" :label="it.name" :value="it.id"></el-option>
                </el-select>
              </el-form-item>

              <p class="item-label required-pre">组织名称</p>
              <el-form-item label="" prop="orgName" :rules="[{ required: true, message: '请输入组织名称', trigger: 'blur' },
              { validator: (rule, value, callback) => checkUnique(rule, value, callback, '1'), trigger:'blur'}]">
                <el-input v-model.trim="setForm.orgName" placeholder="请输入" clearable :disabled="setForm.isDisabled"></el-input>
              </el-form-item>
              <p class="item-label">纳税人识别号</p>
              <el-form-item label="" prop="taxNum">
                <el-input v-model.trim="setForm.taxNum" placeholder="请输入" clearable :disabled="setForm.isDisabled"></el-input>
              </el-form-item>
              <p class="item-label required-pre">参保城市</p>
              <el-form-item label="" prop="addrName" :rules="[{ required: true, message: '请输入参保城市', trigger: 'blur' }]">
                <addrSelector v-model="setForm.addrName" :addrArr="options.addrArr" width="100%" placeholder="请输入"
                              @changeAddrSelect="getAddrId" :disabled="setForm.isDisabled"></addrSelector>
              </el-form-item>
            </div>

            <!--社保-->
            <div class="type-title clearfix" style="margin-top: 10px;"><span class="text">社保信息</span>
              <span class="fr mt-16 mr-20"><el-checkbox v-model="setForm.socialChecked" v-if="!setForm.isDisabled">暂不处理</el-checkbox></span></div>

            <div style="padding: 10px;" v-if="!setForm.socialChecked">
              <p class="item-label required-pre">单位社保号</p>
              <div class="display-flex mb-10" v-for="(item, index) in setForm.socialList" :key="index">
                <el-form-item label="" :prop="'socialList.'+index+'.accountNumber'"
                              :rules="[{ validator: (rule, value, callback) => checkSocialForm(rule, value, callback, index), trigger:'blur'},
                                 { validator: (rule, value, callback) => checkUnique(rule, value, callback, '2', '1'), trigger:'blur'}]" class="flex1">
                  <el-input v-model.trim="item.accountNumber" placeholder="请输入" clearable :disabled="setForm.isDisabled" @input="replaceStr('setForm', 'socialList', index, 'accountNumber', '-')"></el-input>
                </el-form-item>
                <div v-if="!setForm.isDisabled">
                  <i class="el-icon-circle-plus text-green opt-icon" v-if="index==0" @click="addAccountCode('social')"></i>
                  <i class="el-icon-remove text-red opt-icon" v-else @click="delAccountCode('social', index)"></i>
                </div>
              </div>
            </div>
            <!--社保-->

            <!--公积金-->
            <div class="type-title clearfix" style="margin-top: 20px;"><span class="text">公积金信息</span>
              <span class="fr mt-16 mr-20"><el-checkbox v-model="setForm.accfundChecked" v-if="!setForm.isDisabled">暂不处理</el-checkbox></span></div>

            <div style="padding: 10px 10px 100px 10px;" v-if="!setForm.accfundChecked">
              <p class="item-label required-pre">单位公积金号</p>
              <div class="display-flex mb-10" v-for="(item, index) in setForm.accfundList" :key="index">
                <el-form-item label="" :prop="'accfundList.'+index+'.accountNumber'"
                              :rules="[{ validator: (rule, value, callback) => checkaccfundForm(rule, value, callback, index), trigger:'blur'},
                               { validator: (rule, value, callback) => checkUnique(rule, value, callback, '2', '2'), trigger:'blur'}]" class="flex1">
                  <el-input v-model.trim="item.accountNumber" placeholder="请输入" clearable :disabled="setForm.isDisabled" @input="replaceStr('setForm', 'accfundList', index, 'accountNumber', '-')"></el-input>
                </el-form-item>
                <div v-if="!setForm.isDisabled">
                  <i class="el-icon-circle-plus text-green opt-icon" v-if="index==0" @click="addAccountCode('accfund')"></i>
                  <i class="el-icon-remove text-red opt-icon" v-else @click="delAccountCode('accfund', index)"></i>
                </div>
              </div>

            </div>
            <!--公积金-->

          </el-form>
        </div>

        <div class="drawer-footer-buts" v-if="!setForm.isDisabled">
          <el-button class="btn--border-blue s-btn mr-0" @click="doClose">取消</el-button>
          <el-button type="primary" class="s-btn ml-12" :loading="btnLoading" @click="handleValid">确认</el-button>
        </div>
      </div>

    </el-drawer>

  </div>
</template>
<script>
import addrSelector from 'components/common/addrSelector.vue'

export default {
  inject: ['reload'],
  components: { addrSelector },
  props: ['detailData'],
  data () {
    return {
      setForm: {
        show: false,
        orgName: '',
        taxNum: '',
        addrName: '',
        addrId: '',
        socialChecked: false,
        accfundChecked: false,
        socialList: [],
        accfundList: [],
        custId: '',
        virtually: 1,
        id: '',
        isDisabled: false
      },
      options: {
        addrArr: [],
        customerArr: []
      },
      btnLoading: false
    }
  },
  watch: {
    'setForm.show' (newValue, oldValue) {
      if (newValue) {
        //  打开
        var data = this.detailData
        this.setForm.id = data.id
        if (data.type == 'edit') {
          if (data.socialList.length == 0) {
            data.socialList = [this.addAccountNumberObj(1)]
          }
          if (data.accfundList.length == 0) {
            data.accfundList = [this.addAccountNumberObj(2)]
          }
        } else if (data.type == 'add') {
          // bussinssType--业务类型 1：社保，2：公积金
          data.socialList = [this.addAccountNumberObj(1)]
          data.accfundList = [this.addAccountNumberObj(2)]
        }
        this.setForm = data
        console.log(this.setForm)
        if (this.options.addrArr.length === 0) {
          this.getAddrArr()
        }
        if (this.options.customerArr.length === 0) {
          this.getCustomerArr()
        }
      }
    },
    'setForm.orgName' (newValue, oldValue) {
      if (newValue) {
        this.setForm.orgName = newValue.replace('-', '')
      }
    }

  },
  filters: {

  },
  computed: {},
  created () {
  },
  mounted () {

  },
  methods: {
    // 获取参保地
    getAddrArr () {
      let that = this
      this.$http({
        url: '/policy/declareAddr/addrList',
        method: 'post'
      }).then(({ data }) => {
        that.options.addrArr = data.data
      }).catch((data) => {
      })
    },
    replaceStr (dataObj, listName, index, keyName, replaceStr) {
      this[dataObj][listName][index][keyName] = this[dataObj][listName][index][keyName].replace(replaceStr, '')
    },
    getCustomerArr () {
      let that = this
      this.$http({
        url: '/policy/sys/customer/listAllCustomer?filter=true&status=',
        method: 'post'
      }).then(({ data }) => {
        if (data.code == '200') {
          that.options.customerArr = data.data
        }
      }).catch((data) => { })
    },
    // 改变参保地
    getAddrId (item) {
      if (this.setForm.addrId == item.id) {
        return false
      }
      this.setForm.addrId = item.id
      this.$refs.setForm.validate('addrName')
    },
    // 添加社保、公积金社保号
    addAccountNumberObj (bussinssType) {
      return { accountNumber: '', bussinssType: bussinssType, baseId: this.setForm.id }
    },
    addAccountCode (type) {
      if (type == 'social') {
        this.setForm.socialList.push(this.addAccountNumberObj(1))
      } else if (type == 'accfund') {
        this.setForm.accfundList.push(this.addAccountNumberObj(2))
      }
    },
    delAccountCode (type, index) {
      if (type == 'social') {
        this.setForm.socialList.splice(index, 1)
      } else if (type == 'accfund') {
        this.setForm.accfundList.splice(index, 1)
      }
    },
    handleValid: function () {
      let that = this
      that.btnLoading = true
      that.$refs.setForm.validate((valid) => {
        if (valid) {
          that.doSave()
        } else {
          that.btnLoading = false
        }
      })
    },
    doSave: function () {
      let that = this
      let setForm = that.setForm
      this.$http({
        url: '/policy/declareAccount/addOrUpdate',
        method: 'post',
        data: {
          addressId: setForm.addrId,
          addressName: setForm.addrName,
          custId: setForm.custId,
          id: setForm.id,
          orgName: setForm.orgName,
          taxNumber: setForm.taxNum,
          virtually: setForm.virtually,
          socialAccounts: setForm.socialChecked ? [] : setForm.socialList,
          accfundAccounts: setForm.accfundChecked ? [] : setForm.accfundList
        }
      }).then(({ data }) => {
        that.$message.success('操作成功')
        that.doClose()
        that.$emit('success')
      }).catch((data) => {
        that.btnLoading = false
      })
    },
    doClose: function () {
      this.setForm.show = false
      this.setForm.orgName = ''
      this.setForm.taxNum = ''
      this.setForm.addrName = ''
      this.setForm.addrId = ''
      this.setForm.socialChecked = false
      this.setForm.accfundChecked = false
      this.setForm.socialList = []
      this.setForm.accfundList = []
      this.setForm.custId = ''
      this.setForm.id = ''
      this.setForm.isDisabled = false
      this.btnLoading = false
      this.$nextTick(() => {
        this.$refs.setForm.clearValidate()
      })
    },
    checkUnique (rule, value, callback, validType, bussinssType) {
      // callback()
      // return
      let that = this
      // bussinssType--业务类型1：社保,2：公积金
      if ((bussinssType == 1 && that.setForm.socialChecked) || (bussinssType == 2 && that.setForm.accfundChecked)) {
        callback()
        return
      }
      if ((bussinssType == 1 || bussinssType == 2 || validType == '1') && !that.setForm.addrId) {
        this.$refs.setForm.validateField('addrName')
        return
      }
      this.$http({
        url: '/policy/declareAccount/validate',
        method: 'post',
        data: this.$qs.stringify({
          baseId: that.setForm.id,
          validType: Number(validType), // validType--校验类型1：组织名称唯一性校验,2：社保号/公积金号唯一性校验
          value: value,
          bussinssType: bussinssType || '',
          addrId: Number(that.setForm.addrId)
        })
      }).then(({ data }) => {
        if (data.data) {
          callback(new Error(data.data))
        } else {
          callback()
        }
      }).catch((data) => {
        callback(new Error('校验唯一性失败'))
      })
    },
    checkSocialForm (rule, value, callback, index) {
      if (!this.setForm.socialChecked && this.$lodash.isEmpty(value)) {
        callback(new Error('请输入单位社保号'))
      } else {
        let checkRepeatIndex = -1
        for (let i = 0; i < this.setForm.socialList.length; i++) {
          if (index != i && this.setForm.socialList[i].accountNumber == value) {
            checkRepeatIndex = i
            break
          }
        }
        if (checkRepeatIndex != -1) {
          callback('跟第' + (checkRepeatIndex + 1) + '个单位社保号重复')
        } else {
          callback()
        }
      }
    },
    checkaccfundForm (rule, value, callback, index) {
      if (!this.setForm.accfundChecked && this.$lodash.isEmpty(value)) {
        callback(new Error('请输入单位公积金号'))
      } else {
        let checkRepeatIndex = -1
        for (let i = 0; i < this.setForm.accfundList.length; i++) {
          if (index != i && this.setForm.accfundList[i].accountNumber == value) {
            checkRepeatIndex = i
            break
          }
        }
        if (checkRepeatIndex != -1) {
          callback('跟第' + (checkRepeatIndex + 1) + '个单位社保号重复')
        } else {
          callback()
        }
      }
    }
  }
}
</script>
<style lang="less" scoped>
/deep/.drawerForm{
  .item-label{
    margin: 5px 0;
  }
  .el-form-item{
    width: 100%;
    margin-right: 0;
    margin-bottom: 0;
    .el-form-item__content, .addr-main, .el-select{
      width: 100%;
    }
  }
  .el-form-item__error{
    position: relative;
  }
  .opt-icon{
    margin-left: 15px;
    margin-top: 8px;
    cursor: pointer;
    font-size: 28px;
  }
}
</style>
