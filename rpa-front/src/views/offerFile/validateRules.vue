<template>
  <!-- 报盘文件设置 -->
  <div class="spl-container">
    <breadcrumb :data="pathData"></breadcrumb>
    <div style="padding: 20px 30px 10px 30px;">
      <el-row v-if="offerInfo">
        <el-col :span="6">
          <div class="display-flex">
            <span class="label required">参保城市：</span>
            <span class="flex1">{{ offerInfo.addrName }}</span>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="display-flex">
            <span class="label required">业务类型：</span>
            <span class="flex1">{{ offerInfo.bussinssType === 1 ? '社保' : '公积金' }}</span>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="display-flex">
            <span class="label required">申报类型：</span>
            <span class="flex1">{{ handlechangeTypeStr(offerInfo.changeType) }}</span>
          </div>
        </el-col>
      </el-row>
      <div class="display-flex mt-20">
        <div class="flex1 mr-20">
          <!--          字段-->
          <div>
            <div class="field-item" :class="{'active': selectIndex === index}" v-for="(item, index) in ruleList"
                 :key="item.uuid" @click="selectIndex=index">
              <div class="sign-view">
                <span class="sign" v-if="item.inFlag===1">入</span>
                <span class="sign" v-if="item.outFlag===1">盘</span>
              </div>
              <span>{{ item.declareColumnName }}</span>
            </div>
          </div>
          <!--          条件结论-->
          <div class="pl-20 pr-20" v-if="ruleList.length>0">
            <div class="mb-30" v-for="(item, index) in ruleList[selectIndex].rules" :key="item.id">
              <p class="text-blue">
                <span>校验{{ index + 1 }}&nbsp;&nbsp;&nbsp;{{ item.comment }}</span>
                <i class="el-icon-edit f-cursor ml-30 font-16 fw-blod" style="color: #1698db;" title="编辑" @click="handleSetRule('edit')"></i></p>
              <div class="pl-20 pr-20">
                <p class="fw-blod mt-20">如果</p>
                <div class="pl-30">
                  <!--relation: 条件间的关系，1：与，2：或-->
                  <p class="condition-row">{{ item.relation === 1 ? '并且' : '或者' }}</p>
                  <p class="condition-row" v-for="(it, ind) in item.ruleConditionDetails" :key="it.id">
                    条件{{ ind + 1 }}：{{ it.conclusion }}</p>
                </div>
                <p class="fw-blod mt-20">那么</p>
                <div class="pl-30">
                  <p class="condition-row" v-for="(it, ind) in item.ruleConclusions" :key="it.id">
                    结论{{ ind + 1 }}：{{ it.conclusion }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
        <el-button type="primary" size="small" style="height: 32px;" @click="handleSetRule('add')">添加规则</el-button>
        <div style="width: 200px" class="ml-10">
          <el-input v-model.trim="searchText" @keyup.enter.native="getRuleList()" placeholder="关键字回车搜索字段">
            <i @click="getRuleList()" slot="suffix" class="el-input__icon el-icon-search f-cursor font-16 fw-blod"
               style="color: #dbdbdb;"></i>
          </el-input>
        </div>
      </div>

      <div>
        <el-empty v-if="ruleList.length===0" description="暂无数据" class="mt-20"></el-empty>
      </div>

      <!--设置规则-->
      <setValidateRules ref="setValidateRules" :offerInfo="offerInfo" @success="getRuleList"></setValidateRules>

    </div>
  </div>
</template>
<script>
import setValidateRules from './component/setValidateRules'

export default {
  components: { setValidateRules },
  data () {
    return {
      pathData: [],
      searchText: '',
      uuid: '',
      offerInfo: null,
      ruleList: [],
      selectIndex: 0
    }
  },
  computed: {},
  created () {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = this.$global.deepcopyArray(tabs[this.$route.meta.parentPath])
    }
    this.pathData.push({ name: '校验规则' })
    if (this.$route.query.uuid) {
      this.uuid = this.$route.query.uuid
      this.getOfferInfo() // 获取报盘设置信息
      this.getRuleList() // 获取校验规则
    }
  },
  mounted () {
  },
  methods: {
    // 获取报盘设置信息
    getOfferInfo () {
      this.$http({
        url: 'policy/offerSettings/findByUuid/' + this.uuid,
        method: 'post'
      }).then(({ data }) => {
        this.offerInfo = data.data
        this.getAddrTplItems()
      }).catch(() => {
      })
    },
    // 获取校验规则
    getRuleList (uuid) {
      this.showLoading()

      this.$http({
        url: 'policy/offerSettings/columnRuleList/' + this.uuid + '?searchText=' + this.searchText,
        method: 'post'
      }).then(({ data }) => {
        this.closeLoading()
        this.ruleList = data.data
        if (!(data.data[this.selectIndex] && uuid === data.data[this.selectIndex].uuid)) {
          this.selectIndex = 0
        }
      }).catch(() => {
        this.closeLoading()
      })
    },
    handleSetRule (type) {
      let ruleList = this.$global.deepcopyArray(this.ruleList)
      if (type === 'edit') {
        let ruleEdit = this.$global.deepCopyObj(this.ruleList[this.selectIndex])
        this.$refs.setValidateRules.init({
          ruleEdit: ruleEdit,
          ruleList: ruleList
        })
      } else {
        this.$refs.setValidateRules.init({
          ruleEdit: null,
          ruleList: ruleList
        })
      }
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
    showLoading (msg) {
      this.loading = this.$loading({
        target: document.body,
        lock: true,
        text: msg || '加载中',
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
.field-item {
  display: inline-block;
  margin-right: 15px;
  min-width: 100px;
  text-align: center;
  padding: 0 10px;
  height: 36px;
  line-height: 36px;
  background-color: #eff3fc;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  margin-bottom: 20px;

  .sign-view {
    position: absolute;
    right: 0;
    top: -20px;

    .sign {
      display: inline-block;
      width: 18px;
      height: 18px;
      line-height: 18px;
      border-radius: 10px;
      background-color: #ff9933;
      color: #ffffff;
      font-size: 10px;
      margin-left: 10px;
    }
  }

  &.active {
    background-color: @blueColor;
    color: #ffffff;
  }
}

.condition-row {
  margin-top: 10px;
}
</style>
