<template>
  <div class="spl-container pb-30">
    <breadcrumb :data="pathData"></breadcrumb>
    <el-form ref="formData" :model="formData" class="mt-25" label-width="180px">
      <p class="fw-blod font-16 pl-80 pt-20">信息内容</p>
      <el-row style="padding: 20px 20px 0px 20px;">
        <el-col :span="8" class="input-item">
          <div style="display: flex">
            <el-form-item label="参保城市：" prop="addrName" :rules="[{ required: true, message: '请选择', trigger: 'change' }]">
              <addrSelector v-model="formData.addrName" :addrArr="options.addrArr" @changeAddrSelect="getAddrId" :disabled="isEdit"></addrSelector>
            </el-form-item>
          </div>
        </el-col>
        <el-col :span="7" class="input-item">
          <div style="display: flex">
            <el-form-item label="业务类型：" prop="businessType" :rules="[{ required: true, message: '请选择', trigger: 'change' }]">
              <el-radio-group v-model="formData.businessType" @change="getDeclareWebsite" :disabled="isEdit && isLive===1">
                <el-radio :label="1">社保</el-radio>
                <el-radio :label="2">公积金</el-radio>
              </el-radio-group>
            </el-form-item>
          </div>
        </el-col>
        <el-col :span="8" class="input-item">
          <div style="display: flex">
            <el-form-item label="申报网站：" prop="declareWebsite" :rules="[{ required: true, message: '请选择', trigger: 'change' }]">
              <el-select v-model="formData.declareWebsite" placeholder="请选择" filterable>
                <el-option
                  v-for="item in options.declareWebsite"
                  :key="item.tplType"
                  :label="item.tplTypeName"
                  :value="item.tplType"
                >
                </el-option>
              </el-select>
            </el-form-item>
          </div>
        </el-col>
        <el-col :span="8" class="input-item">
          <div style="display: flex">
            <el-form-item label="申报类型：" prop="declareType" :rules="[{ required: true, message: '请选择', trigger: 'change' }]">
              <el-radio-group v-model="formData.declareType" @change="getFunctionPoints" :disabled="isEdit && isLive===1">
                <el-radio :label="1">增员</el-radio>
                <el-radio :label="2">减员</el-radio>
                <el-radio :label="3">调基</el-radio>
                <el-radio :label="5">补缴</el-radio>
              </el-radio-group>
            </el-form-item>
          </div>
        </el-col>
        <el-col :span="7" class="input-item">
          <div style="display: flex">
            <el-form-item label="场景类型：" prop="sceneType" :rules="[{ required: true, message: '请选择', trigger: 'change' }]">
              <el-radio-group v-model="formData.sceneType" :disabled="isEdit && isLive===1">
                <el-radio :label="0">来自网站</el-radio>
                <el-radio :label="2">来自程序</el-radio>
              </el-radio-group>
            </el-form-item>
          </div>
        </el-col>
      </el-row>

      <p class="fw-blod font-16 pl-80 pt-20">开发者参数</p>
      <el-row style="padding: 20px 20px 0px 20px;">
        <el-col :span="8" class="input-item">
          <div style="display: flex">
            <el-form-item label="当前节点：" prop="functionPoints" :rules="[{ required: true, message: '请选择', trigger: 'change' }]">
              <el-select v-model="formData.functionPoints" placeholder="请选择" filterable>
                <el-option
                  v-for="item in options.functionPoints"
                  :key="item.code"
                  :label="item.name"
                  :value="item.code">
                </el-option>
              </el-select>
            </el-form-item>
          </div>
        </el-col>
        <el-col :span="16" class="input-item">
          <div style="display: flex">
            <div>
              <el-form-item label="提示信息传参：" prop="matchRule" :rules="[{ required: true, message: '请选择', trigger: 'change' }]">
                <el-select v-model="formData.matchRule" placeholder="请选择" filterable class="w-150">
                  <el-option label="完全等于" :value="1"></el-option>
                  <el-option label="包含" :value="2"></el-option>
                  <el-option label="以此信息开头" :value="3"></el-option>
                  <el-option label="以此信息结尾" :value="4"></el-option>
                </el-select>
              </el-form-item>
            </div>
            <div class="flex1" style="padding-top: 3px;">
              <el-form-item label="" label-width="10px" style="width: 82%;" prop="resultMsg" :rules="[{ required: true, message: '输入', trigger: 'blur' }]">
                <el-input type="textarea" :rows="3" v-model="formData.resultMsg" maxlength="500" placeholder="提示信息内容"></el-input>
              </el-form-item>
            </div>
          </div>
        </el-col>

        <el-col :span="8">
          <div style="display: flex">
            <el-form-item label="申报回盘：" prop="declareStatus" :rules="[{ required: true, message: '请选择', trigger: 'change' }]">
              <el-radio-group v-model="formData.declareStatus">
                <el-radio :label="2">申报中</el-radio>
                <el-radio :label="4">申报成功</el-radio>
                <el-radio :label="5">申报失败</el-radio>
              </el-radio-group>
            </el-form-item>
          </div>
        </el-col>
        <el-col :span="16">
          <div style="display: flex">
            <el-form-item label="回盘险种：" prop="addrTplItems">
              <el-checkbox-group disabled="true"  v-model="formData.addrTplItems">
                <el-checkbox
                  v-for="item in options.addrTplItems.tplItems"
                  :key="item.tplType"
                  :label="item.tplTypeName"
                  :value="item.tplType"></el-checkbox>
              </el-checkbox-group>
            </el-form-item>
          </div>
        </el-col>
        <el-col :span="22">
          <div style="display: flex">
            <el-form-item label="替换提示：" prop="replaceWarn">
              <el-input v-model="formData.replaceWarn" maxlength="500" placeholder="当信息不可读时，可替换成此提示信息" clearable></el-input>
            </el-form-item>
          </div>
        </el-col>
        <el-col :span="8" class="input-item">
          <div style="display: flex">
            <el-form-item label="下一申报节点：" prop="nextDeclare">
              <el-select v-model="formData.nextDeclare" placeholder="请选择" filterable>
                <el-option
                  v-for="item in options.functionPoints"
                  :key="item.code"
                  :label="item.name"
                  :value="item.code">
                </el-option>
              </el-select>
            </el-form-item>
          </div>
        </el-col>
        <el-col :span="8" class="input-item">
          <div style="display: flex">
            <el-form-item label="是否新场景：" prop="isLive" :rules="[{ required: true, message: '请选择', trigger: 'change' }]">
              <el-radio-group v-model="formData.isLive">
                <el-radio :label="1">否（已上线）</el-radio>
                <el-radio :label="0">是（未上线）</el-radio>
              </el-radio-group>
            </el-form-item>
          </div>
        </el-col>
      </el-row>

      <p class="fw-blod font-16 pl-80 pt-20">完整场景
<!--        <span class="font-14 text-gray ml-10" style="font-weight: normal;">出现该提示作出下一步的处理</span>-->
      </p>
      <el-row style="padding: 20px 20px 0px 20px;">
        <el-col :span="22" class="input-item">
          <div style="display: flex">
            <el-form-item label="提示信息：" prop="warnMessage">
              <el-input type="textarea" :rows="3" v-model="formData.warnMessage" maxlength="500" placeholder="请输入"></el-input>
            </el-form-item>
          </div>
        </el-col>
        <el-col :span="22">
          <div style="display: flex">
            <el-form-item label="注解：" prop="annotate">
              <el-input type="textarea" :rows="3" v-model="formData.annotate" maxlength="500" placeholder="当信息提示难懂时，注解有助用户理解和处置"></el-input>
            </el-form-item>
          </div>
        </el-col>
        <el-col :span="22" class="input-item">
          <div style="display: flex">
            <el-form-item label="场景说明：" prop="scenarioDescription">
              <el-input type="textarea" :rows="3" v-model="formData.scenarioDescription" maxlength="500" placeholder="备注说明该场景"></el-input>
            </el-form-item>
          </div>
        </el-col>
        <el-col :span="22">
          <div style="display: flex">
            <el-form-item label="处置动作：" prop="handleAction">
              <el-input v-model="formData.handleAction" maxlength="500" placeholder="请输入" clearable></el-input>
            </el-form-item>
          </div>
        </el-col>
      </el-row>

    </el-form>

    <div class="text-center mt-10">
      <el-button class="btn--border-blue s-btn w-110" @click="goListPage">回盘配置列表</el-button>
      <el-button type="primary" size="medium" class="w-110 s-btn" style="margin-left: 30px;" v-loading="saveBtnLoading" @click="handleSave()">确定保存</el-button>
    </div>
  </div>
</template>

<script>
import addrSelector from '../../../components/common/addrSelector'

export default {
  components: { addrSelector },
  data () {
    return {
      pathData: [],
      formData: {
        id: '',
        addrName: '',
        addrId: '',
        businessType: 1,
        declareWebsite: '',
        declareType: 1,
        declareStatus: 5,
        addrTplItems: [],
        matchRule: 1,
        resultMsg: '',
        sceneType: 0,
        functionPoints: '',
        nextDeclare: '',
        warnMessage: '',
        scenarioDescription: '',
        handleAction: '',
        annotate: '',
        replaceWarn: '',
        isLive: 0
      },
      options: {
        addrArr: [],
        declareWebsite: [],
        functionPoints: [],
        addrTplItems: []
      },
      saveBtnLoading: false,
      isEdit: true,
      isLive: 1
    }
  },
  computed: {

  },
  created () {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = this.$global.deepcopyArray(
        tabs[this.$route.meta.parentPath]
      )
    }
    this.pathData.push({ name: '业务场景提示', path: '/basicConfig/businessSceneTip' })
    if (this.$route.query.id) {
      this.pathData.push({ name: '编辑' })
      this.formData.id = this.$route.query.id
      this.getDetail()
    } else {
      this.pathData.push({ name: '添加' })
      this.isEdit = false
    }
    this.getAddrArr() // 获取参保地
  },
  methods: {
    // 获取参保地
    getAddrArr () {
      let that = this
      this.$http({
        url: '/policy/declareAccount/availableAddress',
        method: 'post'
      }).then(({ data }) => {
        that.options.addrArr = data.data
      }).catch((data) => {
      })
    },
    // 改变社保-参保地
    getAddrId (item) {
      if (this.formData.addrId == item.id) {
        return
      }
      this.formData.addrId = item.id
      this.getDeclareWebsite()
    },
    // 获取申报网站
    getDeclareWebsite (type) {
      let that = this
      if (type != 'init') {
        this.formData.declareWebsite = ''
        this.options.declareWebsite = []
      }
      if (!this.formData.addrId || !this.formData.businessType) {
        return
      }
      this.$http({
        url: '/policy/offerSettings/findAddrTplItems/' + this.formData.addrId + '/' + this.formData.businessType,
        method: 'post'
      }).then(({ data }) => {
        that.options.declareWebsite = data.data.tplItems
      }).catch((data) => {
      })
      this.getFunctionPoints(type)
      this.getAddrTplItems()
    },
    // 获取功能点
    getFunctionPoints (type) {
      let that = this
      if (type != 'init') {
        this.formData.functionPoints = ''
        this.options.functionPoints = []
      }
      if (!this.formData.addrId || !this.formData.businessType) {
        return
      }
      let changeTypes = []
      changeTypes.push(this.formData.declareType)
      this.$http({
        url: '/policy/offerSettings/getOperationTypes',
        method: 'post',
        params: {
          businessType: this.formData.businessType,
          changeType: changeTypes.join(',')
        }
      }).then(({ data }) => {
        that.options.functionPoints = data.data
        if (type != 'init' && data.data.length > 0) {
          this.formData.functionPoints = data.data[0].code
        }
      }).catch((data) => {
      })
    },
    getAddrTplItems () {
      let that = this
      this.options.addrTplItems = []
      if (!this.formData.addrId || !this.formData.businessType) {
        return
      }
      let addrId = this.formData.addrId
      let bussinssType = this.formData.businessType
      this.$http({
        url: `policy/offerSettings/findAddrTplItems/${addrId}/${bussinssType}`,
        method: 'post'
      }).then(({ data }) => {
        that.options.addrTplItems = data.data
      })
    },
    // 获取详情
    getDetail () {
      let that = this
      this.$http({
        url: '/policy/businessWarn/queryBusinessWarn',
        method: 'post',
        data: { 'page': 1, 'start': 0, 'size': 20, 'query': [{ 'property': 'id', 'value': this.formData.id }], 'sidx': '', 'sort': '' }
      }).then((data) => {
        console.log(data)
        if (data.data.code == '200') {
          console.log(data.data.data.rows)
          if (data.data.data.rows.length > 0) {
            var res = data.data.data.rows[0]
            res.declareWebsite = res.declareWebsite ? String(res.declareWebsite) : ''
            that.isLive = res.isLive
            that.formData = res
            that.getDeclareWebsite('init')
            that.getFunctionPoints('init')
          } else {
            that.$message.warning('未找到该数据')
          }
        }
      }).catch((err) => { })
    },
    handleSave () {
      let that = this
      that.saveBtnLoading = true
      let setObj = { ...that.formData }
      // setObj.warnName = this.$refs.warnType.selected.label
      // setObj.sendWay = setObj.sendWay.join(',')
      console.log(setObj)
      // return
      this.$refs.formData.validate((valid) => {
        if (valid) {
          this.$http({
            url: '/policy/businessWarn/insertOrUpdateBusinessWarn',
            method: 'post',
            data: setObj
          }).then((data) => {
            if (data && data.data.code == '200') {
              that.$message.success('操作成功')
              that.$router.push('/basicConfig/businessSceneTip')
            } else {
              that.saveBtnLoading = false
            }
          }).catch((err) => {
            that.saveBtnLoading = false
          })
        } else {
          that.saveBtnLoading = false
        }
      })
    },
    onEditorBlur () {
      this.$refs.formData.validateField('emailContent')
    },
    goListPage () {
      this.$router.push('/basicConfig/businessSceneTip')
    }
  }
}
</script>

<style lang="less" scoped>
.input-item{
  .el-select, /deep/.el-input{
    width: 100%;
    max-width: 250px;
  }
  /deep/.ic-addr{
    margin-top: 13px!important;
  }

}
.el-form-item {
  flex: 1;
  margin-bottom: 20px;
}
</style>
