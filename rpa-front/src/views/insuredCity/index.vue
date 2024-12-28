<template>
  <!-- 参保城市 -->
  <div class="spl-container">
    <div v-show="isList == 'list'">
      <breadcrumb :data="pathData">
        <!-- <el-button  type="primary" slot="breadcrumb-btn" size="mini" @click="goAdd">新增</el-button> -->
      </breadcrumb>
      <div class="search-row">
        <el-form :model="formData" :rules="rules" ref="headerFormData" label-width="0px" class="demo-ruleForm" @submit.native.prevent>
          <el-row type="flex" align="middle">
            <el-col :span="10">
              <div class="display-flex">
                <!--<el-form-item prop="addrName" label="">-->
                <span class="ml-20 lh-com">参保城市：</span>
                <!-- <addrSelector
                    v-model="formData.keyWord"
                    :addrArr="allAddr"
                    style="line-height: 0px"
                    @changeAddrSelect="changeAddrSelect"
                  ></addrSelector> -->
                <el-input v-model="formData.keyWord" style="width: 240px;" pLaceholder="请输入关键字查询"
                @keyup.enter.native="search" clearable></el-input>
                <!--</el-form-item>-->
              </div>
            </el-col>
            <el-col :span="14">
              <div class="display-flex;" style="text-align: right; ">
                <el-button type="primary" class="mr-10" size="small" @click="search">查询</el-button>
                <el-button type="primary" size="small" @click="goAdd">新增参保城市</el-button>
              </div>
            </el-col>
          </el-row>
        </el-form>
      </div>
      <div class="pl-20 pr-20">
        <dTable @fetch="getTableData" ref="table" style="margin-top: 0" :tableHeight="tableHeight" :showIndex="true"
          :showSelection="false" row-key="id" row-id="id">
          <u-table-column prop="provinceName" label="省份" :show-overflow-tooltip="true" min-width="200"></u-table-column>
          <u-table-column prop="cityName" label="参保城市" :show-overflow-tooltip="true" min-width="200"></u-table-column>
          <u-table-column prop="addrName" label="参保城市（区）" :show-overflow-tooltip="true" min-width="200"></u-table-column>
          <u-table-column label="操作" align="left" width="350" fixed="right">
            <template slot-scope="scope">
              <div>
                <el-button type="text" size="small" class="text-blue" @click="routeToPolicy(scope.row)">申报政策</el-button>
                <el-button type="text" size="small" class="text-blue"
                  @click="openAccfundRule(scope.row, 1)">公积金补缴规则</el-button>
                <div class="opt-btn-split"></div>
                <el-button type="text" size="small" class="text-blue"
                  @click="openSocialRule(scope.row)">社保补缴规则</el-button>
                <el-button type="text" size="small" class="text-blue"
                  @click="declareMaintenance(scope.row)">申报附件维护</el-button>
              </div>
            </template>
          </u-table-column>
        </dTable>
      </div>


      <!-- 社保添加弹窗 -->
      <el-dialog title="调整险种申报组合" :visible.sync="dialogVisible" width="500px" :before-close="cancalEditSocial"
        :close-on-click-modal="false">
      </el-dialog>
      <AccumulationFundDrawer :payOfoptions="payOfoptions" :payOfoptionsDanger="payOfoptionsDanger"
        :visible.sync="accumulationFundVisible" :newAccumuFundForm="JSON.stringify(newAccumuFundForm)"
        @getTable-data="search()" @handleNull-newFrom="handleNullNewFrom" />
      <AddCityDrawer :visible.sync="addSocialVisible" @getTable-data="search()" />
      <PaymentOfRulesDrawer :visible.sync="SocialRuleVisible" :payOfoptions="payOfoptions"
        :payOfoptionsDanger="payOfoptionsDanger" :newSocivalForm="JSON.stringify(newSocivalForm)"
        @handleNew-socivalForm="handleNewSocivalForm" @getTable-data="search()" />
    </div>
    <div v-show="isList == 'IndexDeckare'">
      <div class="spl-breadcrumb clearfix">
        <el-breadcrumb separator-class="el-icon-caret-right">
          <span @click="goBack" class="cur-router mt-10 back"><i class="ic-breadcrumb-back"></i>返回<span
              style="color: #c0c4cc; padding: 0 22px">|</span>
          </span>
          <!--<span class="cur-router"><i class="ic-breadcrumb-home"></i>当前位置：</span>-->
          <el-breadcrumb-item class="f-cursor" v-for="item in pathDataTwo" :key="item.path">
            <router-link v-if="item.path" :to="item.path" v-text="item.name"></router-link>
            <a v-else style="cursor: initial">
              {{ item.name }}
            </a>
          </el-breadcrumb-item>
        </el-breadcrumb>
        <div class="breadcrumb-btn">
          <slot name="breadcrumb-btn"></slot>
        </div>
      </div>
      <IndexDeckare ref="indexDeckare" :singleListRow="singleListRow" :filestoreRuleData="filestoreRuleData" @refresh-list="refreshList" />
    </div>
  </div>
</template>
<script>
import dTable from '../../components/common/table'
import addrSelector from '../../components/common/addrSelector'
import AccumulationFundDrawer from './components/accumulationFundDrawer.vue'
import PaymentOfRulesDrawer from './components/paymentOfRulesDrawer.vue'
import AddCityDrawer from './components/addCityDrawer.vue'
import IndexDeckare from './indexDeckare.vue'
export default {
  components: {
    addrSelector,
    dTable,
    PaymentOfRulesDrawer,
    AddCityDrawer,
    AccumulationFundDrawer,
    IndexDeckare,
  },
  data() {
    return {
      singleListRow: {},
      filestoreRuleData: [],
      isList: 'list',
      newAccumuFundForm: {},
      newSocivalForm: {},
      accumulationFundVisible: false,
      addSocialVisible: false,
      SocialRuleVisible: false,
      allAddr: [],
      formData: {
        bussinssType: ['1', '2'],
        status: '1',
        addrId: '',
        keyWord: '',
        showDrawer: false,
        value: '',
      },
      dialogVisible: false,
      pathDataTwo: [{ name: '参保城市' }, { name: '申报附件维护' }],
      associatedFileList: [],
      socialListData: {
        socialList: [],
      },
      rules: {},
      payOfoptions: [],
      payOfoptionsDanger: [],
      associatedFileErrMsg: [],
      rowData: {},
      loading: null,
    }
  },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 270 + 'px'
    },
  },
  created() {
    let that = this
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = tabs[this.$route.path]
    }
    this.$nextTick(() => {
      that.getTableData() //表格数据
      this.getDictByKey() //参保字典
      this.getDictByKeyDanger() //社保险种
      this.getAddr() //获取所有参保地
    })
  },
  mounted() { },
  methods: {
    refreshList(val) {
      this.getAllFilestoreRule(val)
    },
    goBack() {
      this.isList = 'list'
    },
    handleNullNewFrom() {
      this.newAccumuFundForm = {}
    },
    handleNewSocivalForm() {
      this.newSocivalForm = {}
    },
    changeAddrSelect(val) {
      this.formData.value = val.id
    },
    search() {
      this.getTableData()
    },
    //去新增
    goAdd() {
      this.addSocialVisible = true
    },
    //方案变动时
    socialChange(list) {
      var productNameList = []
      this.socialListData.socialList.forEach((item) => {
        productNameList.push(item.dictName)
      })
      this.socialListData.productNameList = productNameList.join('|')
    },
    //取消调整
    cancalEditSocial() {
      this.dialogVisible = false
      this.$refs.ruleForm.resetFields()
    },
    //确定调整
    confirmEditSocial() {
      var self = this
    },
    //开启公积金 补缴规则
    openAccfundRule(row) {
      if (row.addrId) {
        this.$http({
          url: 'policy/declareAddr/getAccfundSuppRule',
          method: 'post',
          params: {
            addrId: row.addrId,
          },
        }).then(({ data }) => {
          this.newAccumuFundForm = {}
          this.accumulationFundVisible = true
          Object.assign(this.newAccumuFundForm, data.data)
        })
      }
    },
    //开启社保补缴规则
    openSocialRule(row) {
      if (row.addrId) {
        this.$http({
          url: 'policy/declareAddr/getSocialSuppRule',
          method: 'post',
          params: {
            addrId: row.addrId,
          },
        })
          .then(({ data }) => {
            if (data.data !== null) {
              this.newSocivalForm = {}
              this.SocialRuleVisible = true
              Object.assign(this.newSocivalForm, data.data)
            } else {
              this.newSocivalForm = {}
              this.SocialRuleVisible = true
              Object.assign(this.newSocivalForm, row)
            }
          })
          .catch((err) => { })
      }
    },
    declareMaintenance(row) {
      this.$refs.indexDeckare.formData1 = {
        businessType: [],
        declareTypes: [],
        statuses: []
      }
      this.getAllFilestoreRule(row)
    },
    getAllFilestoreRule(val) {
      this.singleListRow = val
      this.$http({
        url: 'policy/declareAddr/getAllFilestoreRule',
        method: 'post',
        params: {
          addrId: val.addrId,
          businessType:val.businessType?val.businessType:null,
          declareTypes:val.declareTypes?val.declareTypes:null,
          statuses:val.statuses?val.statuses:null,
        },
      })
        .then((data) => {
          this.isList = 'IndexDeckare'
          this.filestoreRuleData = data.data.data
          this.pathDataTwo[1].name = `申报附件维护（${this.singleListRow.addrName == null ? this.singleListRow.cityName : this.singleListRow.addrName}）`
        })
        .catch((err) => {
          this.isloading = false
          this.$message.error('接口出错，请稍后再试')
        })
    },

    //列表查询
    getTableData(pageChange, callback) {
      // var params = [{ property: 'addrId', value: this.formData.value }]
      var params = [{ property: 'keyword', value: this.formData.keyWord }]
      var self = this
      this.$refs.table.fetch({
        pageChange: pageChange ? pageChange : '',
        query: params,
        method: 'post',
        url: '/policy/declareAddr/page',
        callback: callback
          ? callback
          : function (res) {
            setTimeout(() => {
              self.$refs.table.doLayout()
            }, 2000)
          },
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
      if (this.loading.close) {
        this.loading.close()
      }
    },
    //获取 允许补缴类型 字典值
    getDictByKey() {
      this.$http({
        url: 'policy/sys/dict/getDictByKey',
        method: 'get',
        params: {
          dataKey: '10005',
        },
      })
        .then((res) => {
          this.payOfoptions = res.data.data
        })
        .catch((err) => {
          this.$message.error('字典接口出错，请稍后再试')
        })
    },
    // 险种
    getDictByKeyDanger() {
      this.$http({
        url: 'policy/sys/dict/getDictByKey',
        method: 'get',
        params: {
          dataKey: '10003',
        },
      })
        .then((res) => {
          this.payOfoptionsDanger = res.data.data
        })
        .catch((err) => {
          this.$message.error('字典接口出错，请稍后再试')
        })
    },
    // 获取参保地
    getAddr(type) {
      let that = this
      this.$http({
        url: 'policy/policyAddr/addrList',
        method: 'post',
      }).then(({ data }) => {
        this.allAddr = data.data
      })
    },
    doClose() {
      this.formData.showDrawer = false
    },
    //验证，确定提交保存
    handleValid() { },
    //深克隆
    deepCopy(obj) {
      var result = Array.isArray(obj) ? [] : {}
      for (var key in obj) {
        if (Object.prototype.hasOwnProperty.call(obj, key)) {
          if (typeof obj[key] === 'object' && obj[key] !== null) {
            result[key] = this.deepCopy(obj[key]) //递归复制
          } else {
            result[key] = obj[key]
          }
        }
      }
      return result
    },
    //跳转至申报政策
    routeToPolicy(row) {
      console.log('即将跳转至 --- 申报政策', row)
      this.$router.push({
        path: '/insuredCity/policy',
        query: {
          cityName: row.cityName,
          addrId: row.addrId,
          addrName: row.addrName || row.cityName
        }
      })
    }
  },
}
</script>
<style lang="less" scoped>
/deep/.el-dialog__header {
  padding: 10px 20px;
}

/deep/.el-dialog__body {
  padding: 10px 10px;
}

/deep/.el-drawer__body {
  padding-bottom: 0;
}

/deep/.el-dialog__header {
  border-bottom: none !important;
}

.text-blue {
  &:hover {
    text-decoration: underline;
  }
}

/deep/ .el-radio__input.is-checked .el-radio__inner::after {
  content: '';
  width: 3px;
  height: 7px;
  border: 1px solid white;
  border-top: transparent;
  border-left: transparent;
  text-align: center;
  display: block;
  position: absolute;
  top: 20%;
  left: 50%;
  vertical-align: middle;
  transform: rotate(45deg) translateX(-50%);
  border-radius: 0px;
  background: none;
}

/deep/ .el-radio__inner {
  border-radius: 2px;
  width: 14px;
  height: 14px;
}

/deep/.drawerForm {
  .item-label {
    margin: 5px 0;
  }

  .el-form-item {
    width: 100%;
    margin-right: 0;
    margin-bottom: 0;

    .el-form-item__content,
    .addr-main,
    .el-select {
      width: 100%;
    }
  }

  .el-form-item__error {
    position: relative;
  }

  .opt-icon {
    margin-left: 15px;
    margin-top: 8px;
    cursor: pointer;
    font-size: 28px;
  }

  .ic-addr {
    margin-top: 13px !important;
  }
}

// 面包屑
/*//面包屑*/
.spl-breadcrumb {
  position: fixed;
  top: 70px;
  /*width: calc(100% - 420px);*/
  width: 80%;
  max-width: 1920px;
  min-width: 1200px;
  // height: 120px;
  background-color: @bodyBaseBgc;
  /*margin-left: -20px;*/
  z-index: 9;
  padding-top: 20px;

  /deep/ .el-breadcrumb {
    padding: 0 30px;
    height: 60px;
    background-color: #F8F8F8;
    box-shadow: 0px 0px 10px 0px rgba(135, 175, 228, 0.1);
    border-radius: 6px 6px 0px 0px;
    margin-bottom: 0px;
    font-family: Microsoft YaHei;

    /*font-size: 15px;*/
    a {
      color: #888888;
      font-weight: normal;
      line-height: 66px;
    }
  }

  /deep/.el-breadcrumb__item {
    float: none;
  }

  /deep/.el-breadcrumb__item .el-breadcrumb__inner a {
    font-size: 16px;
    font-weight: bold;
  }

  /deep/.el-breadcrumb__item:last-child .el-breadcrumb__inner a {
    color: #333333;
  }

  /deep/.el-breadcrumb__separator[class*=icon] {
    padding: 0 10px;
  }

  .spl-index {
    background-color: #fff;
    padding: 20px 240px 20px 40px;
    margin-bottom: 20px;
  }

  .breadcrumb-btn {
    position: absolute;
    top: 35px;
    right: 20px;
  }
}

.mt-10 {
  margin-top: 10px;
}

.cur-router {
  float: left;
  color: #888888;
}

.back {
  font-size: 14px;
  font-family: Microsoft YaHei;
  font-weight: 400;
  color: #333333;
  cursor: pointer;
}

.ic-breadcrumb-home {
  display: inline-block;
  width: 24px;
  height: 20px;
  margin-top: 19px;
  background: url('../../assets/images/icons/ic-breadcrumb-home.png');
  margin-right: 9px;
}

.ic-breadcrumb-back {
  display: inline-block;
  width: 16px;
  height: 10px;
  margin-top: 19px;
  background: url('../../assets/images/icons/ic-breadcrumb-back.png');
  margin-right: 9px;
}

// 面包屑
</style>
<style scoped>
body .el-table th.gutter {
  display: table-cell !important;
}

.error {
  margin-top: 10px;
  /* position: absolute; */
  left: 0;
  /* top: 120%; */
  color: #f56c6c;
  font-size: 12px;
}
</style>
