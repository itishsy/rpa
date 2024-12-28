<template>
  <div>
    <el-dialog title="引入规则" :visible.sync="show" width="500px" class="cust-dialog"
               :close-on-click-modal="false" :show-close="true">
      <el-form :model="setForm" ref="setForm" class="pb-30">
        <p class="required mb-5">选择源地区</p>
        <el-form-item prop="addrName" :rules="[{ required: true, message: '请选择', trigger: 'change' }, {validator: validAddrName, trigger:'blur'}]">
          <addrSelector v-model="setForm.addrName" :addrArr="allAddr" width="300px" class="search-row-item"></addrSelector>
          <el-button type="primary" class="w-80 s-btn mt-0 ml-15" @click="handleImport()">引入</el-button>
        </el-form-item>
        <p v-if="offerInfo">
          <span>{{ offerInfo.bussinssType === 1 ? '社保' : '公积金' }}</span>
          <span class="ml-20">{{ handlechangeTypeStr(offerInfo.changeType) }}</span>
        </p>

        <div class="text-red mt-20" v-if="errorMsgList.length>0">
          <p>出现错误：</p>
          <p v-for="(item, index) in errorMsgList" :key="index" class="mt-5">{{ item }}</p>
        </div>
      </el-form>
    </el-dialog>
  </div>
</template>
<script>
import addrSelector from '@/components/common/addrSelector'
export default {
  name: 'importRuleDialog',
  props: ['offerInfo'],
  components: { addrSelector },
  computed: {},
  watch: {
    show (newValue, oldValue) {
      if (newValue && this.allAddr.length === 0) {
        this.getAddr()
      }
      if (!newValue) {
        this.errorMsgList = []
        this.setForm.addrName = ''
        this.$nextTick(() => {
          this.$refs.setForm.clearValidate()
        })

      }
    }
  },
  data () {
    return {
      show: false,
      uuid: '',
      setForm: {
        addrName: ''
      },
      allAddr: [],
      errorMsgList: []
    }
  },
  created () {
  },
  mounted () {
  },
  methods: {
    init (uuid) {
      this.uuid = uuid
      this.show = true
    },
    handleImport () {
      this.$refs.setForm.validate((valid) => {
        if (valid) {
          this.showLoading()
          this.$http({
            url: `policy/offerSettings/validateIntroduceRules/${this.uuid}/${this.setForm.addrName}`,
            method: 'post'
          }).then(({ data }) => {
            this.errorMsgList = data.data
            if (data.data.length === 0) {
              this.doImport()
            } else {
              this.closeLoading()
            }
          }).catch(() => {
            this.closeLoading()
          })
        }
      })
    },
    doImport () {
      this.$http({
        url: `policy/offerSettings/introduceRules/${this.uuid}/${this.setForm.addrName}`,
        method: 'post'
      }).then(({ data }) => {
        this.closeLoading()
        this.show = false
        this.$emit('success', data.data)
      }).catch(() => {
        this.closeLoading()
      })
    },
    getAddr (type) {
      this.$http({
        url: 'policy/declareAddr/addrList',
        method: 'post'
      }).then(({ data }) => {
        this.allAddr = data.data
      }).catch(() => {
        this.closeLoading()
      })
    },
    handlechangeTypeStr (code) {
      switch (code) {
        case 1:
          return '增员'
        case 2:
          return '减员'
        case 3:
          return '调基'
        case 5:
          return '补缴'
      }
      return ''
    },
    // 校验是否当前城市
    validAddrName (rule, value, callback) {
      if (this.offerInfo.addrName === value) {
        callback(new Error('不能引入当前参保城市'))
      } else {
        callback()
      }
    },
    showLoading (msg) {
      this.loading = this.$loading({
        target: document.body,
        lock: true,
        text: msg || '请稍候...',
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
/deep/.ic-addr{
  margin-top: 14px!important;
}
</style>
