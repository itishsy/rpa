<template>
  <div>
    <el-dialog
      :visible.sync="localVisible"
      width="700px"
      :close-on-click-modal="false"
      title="添加客户应用"
    >
      <div class="form-fiv old">
        <el-form
          style="margin-top: 10px"
          ref="localCustomForm"
          :label-position="labelPosition"
          label-width="100px"
          :model="localCustomForm"
          :rules="rules"
        >
          <el-form-item prop="pingtai" label="平台：">
            <span>{{ pingtai }}</span>
          </el-form-item>
          <el-form-item prop="customerName" label="客户：">
            <el-select
              v-model="localCustomForm.customerName"
              placeholder="请选择"
              class="content-len"
              ref="customerName"
              @change="handleClientId"
              value-key="id"
              filterable
              :disabled="disabled"
            >
              <el-option
                v-for="item in listCustomerOption"
                :key="item.id"
                :label="item.customerName"
                :value="item"
              >
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item prop="appName" label="应用名称：">
            <el-select
              v-model="localCustomForm.appName"
              placeholder="请选择"
              class="content-len"
              @change="handleApp"
              value-key="appCode"
              filterable
              :disabled="disabled"
            >
              <el-option
                v-for="item in appList"
                :key="item.appCode"
                :label="item.appName"
                :value="item"
              >
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item prop="machineCode" label="设备编号：">
            <el-select
              v-model="localCustomForm.machineCode"
              placeholder="请选择"
              class="content-len"
              filterable
              :disabled="disabled"
            >
              <el-option
                v-for="item in machineCodeList"
                :key="item.machineCode"
                :label="item.machineName+'（'+item.machineCode+'）'"
                :value="item.machineCode"
              >
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item prop="fixMachine" label="" style="margin-bottom: 10px;margin-top: -10px;">
            <el-checkbox v-model="localCustomForm.fixMachine" :true-label="1" :false-label="0">指定执行原因</el-checkbox>
          </el-form-item>
          <el-form-item prop="fixRemark" label="原因：" v-if="localCustomForm.fixMachine===1">
            <el-input v-model="localCustomForm.fixRemark" autocomplete="off" clearable></el-input>
          </el-form-item>
        </el-form>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="cancel()">取消</el-button>
        <el-button type="primary" @click="confirm()">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
export default {
  props: {
    appList: {
      type: Array,
      default: () => []
    },
    visible: {
      type: Boolean,
      default: false
    },
    addAppData: {
      type: Object,
      default: () => {}
    },
    listCustomerOption: {
      type: Array,
      default: () => []
    }
  },
  computed: {
    localVisible: {
      get () {
        return this.visible
      },
      set (val) {
        this.$emit('update:visible', val)
      }
    }
  },
  data () {
    return {
      inputVisible: false,
      inputValue: undefined,
      pingtai: undefined,
      localCustomForm: {
        id: '',
        appName: '',
        customerName: '',
        machineCode: '',
        clientId: '',
        fixMachine: 0,
        fixRemark: ''
      },
      labelPosition: 'right',
      rules: {
        appName: [
          { required: true, message: '请选择', trigger: 'change' }
          //   { min: 6, max: 6, message: '验证码应为6位字符', trigger: 'blur' },
        ],
        customerName: [{ required: true, message: '请选择', trigger: 'change' }],
        machineCode: [{ required: true, message: '请选择', trigger: 'change' }],
        fixRemark: [{ required: true, message: '请输入原因', trigger: 'blur' }]
      },
      machineCodeList: [],
      disabled: false
    }
  },
  created () {},
  mounted () {
  },
  watch: {
    addAppData: {
      handler (val) {
        if (val.id) {
          this.disabled = true
          this.localCustomForm.id = val.id
          this.localCustomForm.appName = val.appName
          this.localCustomForm.customerName = val.customerName
          this.localCustomForm.machineCode = val.machineCode
          this.localCustomForm.fixMachine = val.fixMachine
          this.localCustomForm.fixRemark = val.fixRemark
          var obj = this.listCustomerOption.find(
            (item) => item.customerName === val.customerName
          )
          console.log(obj)
          if (obj) {
            this.handleClientId(obj, 'echo')
          }
        } else {
          this.disabled = false
        }
      },
      deep: true,
      immediate: true
    }
  },
  methods: {
    getMachineCodeList (val, type) {
      if (type !== 'echo') {
        this.localCustomForm.machineCode = ''
      }
      if (!val) {
        this.machineCodeList = []
        return false
      }
      this.$http({
        url: '/robot/version/client/getByClientId/' + val.id,
        method: 'post',
        data: {}
      })
        .then((data) => {
          this.machineCodeList = data.data.data
        })
        .catch((err) => {})
    },
    handleClientId (val, type) {
      if (val) {
        this.localCustomForm.clientId = val.id
        this.pingtai = val.platform
        this.localCustomForm.customerName = val.customerName
      }
      this.getMachineCodeList(val, type)
    },
    handleApp (val) {
      if (val) {
        this.localCustomForm.appName = val.appName
        this.localCustomForm.appCode = val.appCode
      }
    },
    cancel () {
      this.localVisible = false
      // this.$emit('refresh-list')
      this.resetloacalForm()
    },
    confirm () {
      let that = this
      this.$refs.localCustomForm.validate((valid) => {
        if (valid) {
          that.$global.showLoading()
          if (that.localCustomForm.fixMachine === 0) {
            that.localCustomForm.fixRemark = ''
          }
          let url = '/robot/app/client/addRobotClientApp'
          if (that.localCustomForm.id) {
            url = '/robot/app/client/editRobotClientApp'
          }
          that.$http({
            url: url,
            method: 'post',
            data: that.localCustomForm
          }).then(({ data }) => {
            that.$global.closeLoading()
            that.localVisible = false
            that.$emit('refresh-list')
            that.resetloacalForm()
          }).catch((err) => {
            that.$global.closeLoading()
          })
        }
      })
    },
    resetloacalForm () {
      this.pingtai = ''
      this.localCustomForm = {
        id: '',
        appName: '',
        customerName: '',
        machineCode: '',
        clientId: '',
        fixMachine: 0,
        fixRemark: ''
      }
      this.$refs.localCustomForm.resetFields()
    }
  }
}
</script>
    <style lang="less" scoped>
.text-flex {
  display: flex;
  justify-content: start;
  .text-de {
    margin-left: 10px;
    color: #3f91ff;
  }
}
.dialog-name-line {
  border: 1px solid #3a8dff;
  border-radius: 2px 2px 2px 2px;
  background: #3a8dff;
  float: left;
  margin-left: -13px;
}
.dialog-name {
  font-size: 17px;
  // margin-left: -12px;
}
.dialog-footer {
  display: flex;
  justify-content: center;
}
/deep/ .el-form-item {
  margin-bottom: 20px;
}
/deep/ .el-dialog__header {
  border-bottom: none !important;
}
.form-fiv {
  padding: 0px 37px;
  margin-bottom: 37px;
  margin-top: -20px;
}
.flex-sex {
  display: flex;
  justify-content: start;
}
.height11 {
  height: 8px;
}
.content-len {
  width: 450px;
}
.marin-le {
  margin-left: 10px;
}
.marin-ri {
  margin-right: 5px;
}
.execCycle {
  /deep/ .el-form-item__error {
    // margin-top: -19px !important;
  }
}
.execCycleTo {
  /deep/ .el-form-item__error {
    margin-top: 0px !important;
  }
}
/deep/ .el-form-item__label {
  width: 100px !important;
}
</style>
