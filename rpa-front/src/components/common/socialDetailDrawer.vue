<template>
  <!--查看社保详情-->
  <el-drawer title="社保参保详情" :visible="show" :wrapperClosable="false" custom-class="spl-filter-drawer detail-drawer"
    :show-close="true" @close="cancel" size="1200px">
    <template slot="title">
      <palTab :tabs="tabs" :type="2" @change="changeTab" v-model="tabActive"></palTab>
    </template>
    <div v-if="tabActive == 0">
      <div class="pt-20">
        <descriptionsList>
          <descriptionsItem :column="8"
            v-for="(item, index) in this.type == '1' ? templateData.social : templateData.accfund" :key="index"
            :label="item.name">
            <span slot="label" v-if="item.formatName && !item.name" class="lab">{{ formatFN(item.formatName) + '：' }}</span>
            <span slot="value" v-if="item.format" class="val">{{ formatFN(item.format) }}</span>
            <span slot="value" v-else class="val">{{ getData[item.code] }}</span>
          </descriptionsItem>
          <descriptionsItem :column="8" v-for="(item, index) in this.type == '1' ? getData.details : getData.suppDetails"
            :key="index" :label="item.columName">{{ item.columValue }}</descriptionsItem>
          <descriptionsItem :column="8" label="创建日期">
            <span slot="value" class="val">{{ $moment(this.getData.createTime).format("YYYY-MM-DD HH:mm") }}</span>
          </descriptionsItem>
          <descriptionsItem :column="8" label="创建人">{{ this.getData.createName }}</descriptionsItem>
        </descriptionsList>

        <descriptionsList v-if="getData.empFiles">
          <descriptionsItem :column="24" :label="item.fileTypeName" v-for="(item, index) in getData.empFiles" :key="index">
            <div slot="value" style="display:flex;flex-wrap:wrap;align-items:center;">
              <fileTag :fileName="item2.clientFileName" v-for="(item2, index) in item.files" :key="index" :file="item2">
              </fileTag>
            </div>
          </descriptionsItem>
        </descriptionsList>
      </div>
      <!-- v-show="getData.status == 5 || getData.status == 3" -->
      <div class="pl-20 pr-20 mb-20">
        <div class="fail-title">申报详情</div>
        <div class="pt-10">
          <el-table :data="errorData" border header-cell-class-name="table-header" style="width: 100%"
            :max-height="tableHeight2">
            <el-table-column :fixed="item.fixed" v-for="(item, index) in templateData.errorTable" :key="index"
              :prop="item.prop" :label="item.label" :max-width="item.width">
              <template slot-scope="scope">
                <div v-if="item.format">{{ format(item, scope.row) }} <span v-if="item.prop==='sameStatusName' && scope.row.ignoreFlag===1" class="causeTip ml-5">忽</span></div>
                <div v-else>{{ scope.row[item.prop] }}</div>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>
    <div class="pl-20 pr-20 pb-20 pt-20" v-show="tabActive === 1">
      <el-table :data="recordData" border header-cell-class-name="table-header" style="width: 100%"
        :max-height="tableHeight2">
        <el-table-column :fixed="item.fixed" v-for="(item, index) in templateData.logTable" :key="index"
                         v-if="item.prop!='tplTypeName' || (type == '1' && item.prop=='tplTypeName')" :prop="item.prop"
          :label="item.label" :max-width="item.width">
          <template slot-scope="scope">
            <div v-if="item.format">{{ format(item, scope.row) }}</div>
            <div v-else>
              <el-tooltip class="item" effect="dark" :content="scope.row.adjustReason" placement="top" v-if="scope.row[item.prop] == '调整'">
                <div style="color:#3E82FF;">{{ scope.row[item.prop] }}</div>
              </el-tooltip>
              <div v-else>{{ scope.row[item.prop] }}</div>
            </div>
          </template>
        </el-table-column>
        <!-- 多加一行 操作列 当存在 robotExecutionVos时,并且是非客户页面（运维） 代表可以“执行查询” -->
        <el-table-column   label="操作"  v-if="isOpera">
          <template slot-scope="scope">
            <span v-if="scope.row.robotExecutionVos " style="color:#3e82ff;cursor: pointer;" @click="emitDoQuery(scope.row.robotExecutionVos)">执行查询</span>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="pl-20 pr-20 pb-20 pt-20" v-show="tabActive === 2">
      <div>
        <!--异常表格 -->
        <el-row type="flex" class="mb-10">
          <el-col>
            <div>客户名称：广州仕邦人力资源有限公司</div>
          </el-col>
          <el-col>
            <div>应用名称：广州-公积金</div>
          </el-col>
        </el-row>
        <dTable @fetch="getErrorTable" ref="errorTable" :cancelMinHeight="false" :paging="true" :showSelection="false"
          :showIndex="true" rowKey="id" :rowHeight="45">
          <u-table-column prop="employeeName" label="预警项目" min-width="160" :show-overflow-tooltip="true"
            fixed="left"></u-table-column>
          <u-table-column prop="employeeName" label="任务名称" min-width="160" :show-overflow-tooltip="true"
            fixed="left"></u-table-column>
          <u-table-column prop="idCard" label="执行时间" min-width="160" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="addrName" label="流程名称" min-width="160" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="changeType" label="附件" min-width="200" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <span>{{ handleChangeType(scope.row.changeType) }}</span>
            </template>
          </u-table-column>
          <u-table-column prop="idCard" label="执行指令" min-width="160" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="failCase" label="异常原因" min-width="200" :show-overflow-tooltip="true"></u-table-column>

        </dTable>
      </div>
    </div>
    <div class="drawer-footer-buts">
      <!--编辑： 待提交，待申报，申报失败数据显示-->
      <el-button size="small" type="primary" style="margin-right: 20px;"
        v-if="(getData.status == '6' || getData.status == '1' || getData.status == '5' || getData.status == '3') && edit"
        @click="toEdit(getData, type, type == 1 ? 'Social' : 'Accfund')">编辑参保</el-button>
      <el-button size="small" class="w-80" type="primary" @click="cancel">关闭</el-button>
    </div>
  </el-drawer>
</template>

<script>
import palTab from '../../components/common/pal-tab'
import descriptionsList from './descriptionsList.vue'
import descriptionsItem from './descriptionsItem'
import fileTag from './fileTag.vue'
import templateData from './socialDetailData.js'
import dTable from '@/components/common/table'
export default {
  inject: ['getInsuranceList'],
  props: {
    isOpera: { // 是否是运维页面，运维页面才可以“执行查询”
      type: Boolean,
      default: false
    },
    show: {
      type: [String, Number, Boolean],
      default: false
    },
    tabs: {
      type: Array,
      default: function () {
        return ['社保参保详情', '操作记录']
      }
    },
    detailData: {
      type: Object,
      default: () => {
        return {}
      }
    },
    type: {
      type: [String, Number],
      default: '1'
    },
    row: {
      type: Object,
      default: () => {
        return {}
      }
    },
    requestType: {
      type: [String],
      default: 'employeeInsurance'
    },
    edit: {
      type: [Boolean],
      default: false
    }
  },
  components: {
    palTab,
    descriptionsList,
    descriptionsItem,
    fileTag,
    dTable
  },
  watch: {
    // show(newVal, oldVal) {
    //   if (newVal) {
    //     if (this.type == '1') {
    //       this.viewSocialInfo(this.row)
    //     } else {
    //       this.viewAccfundInfo(this.row)
    //     }
    //   }
    // },
  },
  created () {
    console.log(this.edit)
    // this.getDictByKey('1003')
  },
  data () {
    return {
      templateData: templateData,
      loading: null,
      recordData: [],
      myDetailData: {},
      errorData: [],
      tabActive: 0,
      insuranceList: []
    }
  },
  destroyed () { },
  computed: {
    ...templateData.computed,
    getData () {
      return JSON.stringify(this.detailData) != '{}'
        ? this.detailData
        : this.myDetailData
    }
  },
  methods: {
    changeTab (index) {
      this.tabActive = index
    },
    request (type, row) {
      if (type == 1) {
        this.viewSocialInfo(row)
      } else {
        this.viewAccfundInfo(row)
      }
    },
    // 查看社保详情
    viewSocialInfo (row) {
      let that = this
      this.showLoading()
      this.tabActive = 0
      this.getRecord(row, '1')
      var url = ''
      if (this.requestType == 'employeeInsurance') {
        // url = '/agent/declareChange/listNewEmpInsuredOne'
        url = '/agent/declareChange/getInsuredDetail'
      } else {
        url = '/agent/declareChange/lookUp/' + row.itemId
        // url = '/agent/declareChange/getInsuredDetail'
      }
      this.$http({
        url: url,
        method: 'post',
        params: {
          uuid: row.uuid,
          employeeFileStoreId: row.employeeFileStoreId,
          addrId: row.addrId
        },
        data: {
          itemId: row.itemId
        }
      })
        .then(({ data }) => {
          this.closeLoading()
          data.data.employeeFileStoreId = data.data.id
          if (data.data.empFiles && data.data.empFiles.length > 0) {
            var obj = {}
            data.data.empFiles = data.data.empFiles.reduce((prev, item) => {
              if (obj[item.fileTypeName]) {
                prev.forEach((item2) => {
                  if (item2.fileTypeName == item.fileTypeName) {
                    item.files.forEach((item3) => {
                      if (!obj[item.id]) {
                        item2.files.push(item3)
                      }
                    })
                  }
                })
              } else {
                prev.push(item)
                prev.forEach((item) => {
                  item.files.forEach((item) => {
                    obj[item.id] = item.id
                  })
                })
                obj[item.fileTypeName] = item.fileTypeName
              }
              console.log(prev)
              return prev
            }, [])
          }
          that.myDetailData = data.data
          // console.log('data.data', data.data)
          this.listNewEmpInsuredItemBaseOne(row, 'social')
          this.getPolicyOfferSettingInner()
          // this.socialDetail.show = true
        })
        .catch((data) => {
          console.log(data)
          this.closeLoading()
        })
    },
    // 查看公积金详情
    viewAccfundInfo (row) {
      let that = this
      this.showLoading()
      this.tabActive = 0
      this.getRecord(row, '2')
      var errorData = []
      if (row.declareStatus != row.suppDeclareStatus || row.failCause != row.suppFailCause) {
        if (row.declareStatus != null && row.declareStatus !== '') {
          errorData.push({
            sameStatusName: '公积金',
            status: row.declareStatus,
            robotExecStatus: row.robotExecStatus,
            failCase: row.failCause,
            ignoreFlag: row.ignoreFlag
          })
        }
        if (row.suppDeclareStatus != null && row.suppDeclareStatus !== '') {
          errorData.push({
            sameStatusName: '补充公积金',
            status: row.suppDeclareStatus,
            robotExecStatus: row.robotExecStatus,
            failCase: row.suppFailCause,
            ignoreFlag: row.ignoreFlag
          })
        }
      } else {
        if (row.declareStatus != null && row.declareStatus !== '') {
          errorData.push({
            sameStatusName: '公积金|补充公积金',
            status: row.declareStatus,
            robotExecStatus: row.robotExecStatus,
            failCase: row.failCause,
            ignoreFlag: row.ignoreFlag
          })
        }
      }
      this.errorData = errorData
      var url = ''
      if (this.requestType == 'employeeInsurance') {
        // url = '/agent/declareChange/queryNewAccfundOne'
        url = '/agent/declareChange/getAccfundDetail'
      } else {
        url = '/agent/declareChange/checkAccount/' + row.id
        // url = '/agent/declareChange/getAccfundDetai'
      }
      this.$http({
        url: url,
        method: 'post',
        params: {
          id: row.id
        }
      })
        .then(({ data }) => {
          this.closeLoading()
          if (data.data.empFiles && data.data.empFiles.length > 0) {
            var obj = {}
            data.data.empFiles = data.data.empFiles.reduce((prev, item) => {
              if (obj[item.fileTypeName]) {
                prev.forEach((item2) => {
                  if (item2.fileTypeName == item.fileTypeName) {
                    item.files.forEach((item3) => {
                      if (!obj[item.id]) {
                        item2.files.push(item3)
                      }
                    })
                  }
                })
              } else {
                prev.push(item)
                prev.forEach((item) => {
                  item.files.forEach((item) => {
                    obj[item.id] = item.id
                  })
                })
                obj[item.fileTypeName] = item.fileTypeName
              }
              console.log(prev)
              return prev
            }, [])
          }
          that.myDetailData = data.data
          that.myDetailData.uuid = row.uuid
          that.myDetailData.employeeFileStoreId = row.employeeFileStoreId
          this.getPolicyOfferSettingInner()
        })
        .catch((err) => {
          console.log(err)
          this.closeLoading()
        })
    },
    getRecord (row, type) {
      // console.log(row)
      var url = ''
      if (type == '1') {
        url = '/agent/declareChange/listSocialProcess?uuid=' + row.uuid + '&type=' + (this.isOpera ? '1' : '2')
      } else {
        url = '/agent/declareChange/listAccfundProcess?uuid=' + row.uuid + '&type=' + (this.isOpera ? '1' : '2')
      }
      this.$http({
        url: url,
        method: 'post'
      })
        .then(({ data }) => {
          if (type == '1') {
            this.recordData = data.data
          } else {
            this.recordData = data.data
          }
        })
        .catch((item) => { })
    },
    toEdit (row, num, val) {
      console.log('row', row)
      let rowObject = {
        uuid: row.uuid,
        employeeFileStoreId: row.employeeFileStoreId,
        addrId: row.addrId,
        itemId: row.itemId,
        id: row.id,
        compAccount: row.compAccount,
        idCard: row.idCard,
        cardType: row.cardType,
        insuredDate: row.insuredDate
      }
      this.$store.commit('updateInsuranceRow', JSON.stringify(rowObject))
      this.$router.push(
        `/employeeInsurance/addSocial?bussinessType=${num}&type=edit&ssType=${val}`
      )
    },
    // 获取详情 参保状态
    listNewEmpInsuredItemBaseOne (row, type) {
      this.errorData = []
      // this.showLoading()
      this.$http({
        url: '/agent/declareChange/listNewEmpInsuredItemBaseOne',
        method: 'post',
        params: {
          itemId: row.itemId,
          uuid: row.uuid
        }
      }).then(({ data }) => {
        this.closeLoading()
        // console.log(data)
        if (type == 'social') {
          this.errorData = data.data
        } else {
          this.accfundTable = data.data
        }
      })
    },
    cancel () {
      this.myDetailData = {}
      this.$emit('update:detailData', {})
      this.$emit('update:show', false)
    },
    showLoading (target) {
      this.loading = this.$loading({
        target: target || document.body,
        lock: true,
        text: '加载中',
        spinner: 'el-icon-loading',
        background: 'rgba(255, 255, 255, 0.7)'
      })
    },
    closeLoading () {
      if (this.loading && this.loading.close) {
        this.loading.close()
      }
    },
    // 获取报盘字段，参保时间和缴纳基数 要和报盘字段一样
    getPolicyOfferSettingInner () {
      // ${this.formData.addrName}/${businessType}/${this.formData.declareType}
      var type = this.type == '1' ? 'social' : 'accfund'
      if (type == 'social') {
        this.$set(this.templateData[type][6], 'name', '参保时间')
        this.$set(this.templateData[type][8], 'name', '缴纳基数')
      } else {
        this.$set(this.templateData[type][7], 'name', '参保时间')
        this.$set(this.templateData[type][8], 'name', '缴纳基数')
      }
      this.$http({
        url: `/policy/declareRule/policyOfferSettingInner/${this.getData.addrName}/${this.type}/${this.getData.changeType}?accountNumber=`,
        method: 'post'
      }).then(({ data }) => {
        // console.log(data)
        for (let i = data.data.policyFields.length - 1; i >= 0; i--) {
          const item = data.data.policyFields[i]
          if (item.fieldCode == '20000002' || item.fieldCode == '20000005') {
            this.templateData[type].forEach((item1, index) => {
              if (item1.code == 'roundEmpTbBase' && item.fieldCode == '20000005') {
                item1.name = item.fieldName
                this.$set(this.templateData[type][index], 'name', item.fieldName)
              }
              if (item1.nameCode == 'changeType' && item.fieldCode == '20000002') {
                item1.name = item.fieldName
                this.$set(this.templateData[type][index], 'name', item.fieldName)
              }
            })
          }
        }
        var details = this.type == '1' ? this.deepCopy(this.myDetailData.details) : this.deepCopy(this.myDetailData.suppDetails)
        this.templateData[type].forEach((item, index) => {
          for (let i = details.length - 1; i >= 0; i--) {
            const item1 = details[i]
            // details 里面有一样的字段，就以details里面的值为准
            if (item.name == item1.columName) {
              this.getData[item.code] = item1.columValue
              details.splice(i, 1)
            }
          }
        })
        if (this.type == '1') {
          this.myDetailData.details = details
        } else {
          this.myDetailData.suppDetails = details
        }
        this.$emit('update:show', true)
      })
    },
    // 获取险种字典，用与参保险种，按返回数据排序
    async getDictByKey () {
      return this.$http({
        url: 'policy/sys/dict/getDictByKey',
        method: 'get',
        params: {
          dataKey: '10003'
        }
      })
        .then((res) => {
          this.insuranceList = res.data.data
        })
        .catch((err) => {
          this.$message.error('字典接口出错，请稍后再试')
        })
    },
    // 深克隆
    deepCopy (obj) {
      var result = Array.isArray(obj) ? [] : {}
      for (var key in obj) {
        if (Object.prototype.hasOwnProperty.call(obj, key)) {
          if (typeof obj[key] === 'object' && obj[key] !== null) {
            result[key] = this.deepCopy(obj[key]) // 递归复制
          } else {
            result[key] = obj[key]
          }
        }
      }
      return result
    },
    // 获取异常表格
    getErrorTable () {
      let that = this
      //   let search = this.socialSearch
      //   if(!search.addrName){
      //     this.$message.warning('请先选择参保城市')
      //     return
      //   }
      var params = [
        // { property: 'addrId', value: search.addrId },
        // { property: 'id', value: search.company },
        // { property: 'accountNumber', value: search.accountNumber },
        // { property: 'createStartTime', value: search.createStartTime },
        // { property: 'createEndTime', value: search.createEndTime },
        // { property: 'keyword', value: search.keyword },
        // { property: 'insuredStartDate', value: search.insuredStartDate },
        // { property: 'insuredEndDate', value: search.insuredEndDate }
      ]

      this.$refs.errorTable.fetch({
        query: params,
        method: 'post',
        url: '/agent/declareChange/ListEmployeeSocialInsured'
      })
    },
    // 操作记录 - 执行查询
    emitDoQuery (queryVo) {
      this.$emit('do-query', queryVo)
    }
  }
}
</script>
<style lang="less" scoped>
/deep/.detail-drawer {
  width: 95% !important;
  max-width: 1200px !important;

  .pal-tabs {
    padding-left: 0;
    height: 75px;
    line-height: 62px;
    position: relative;
    top: -10px;
  }

  .span-tabs {
    height: 71px!important;
    line-height: 71px!important;
  }
}

.lab1 {
  margin-top: 10px;
  background: red;
}

.fail-title {
  font-size: 16px;
  color: #303133;
  padding-bottom: 10px;
  border-bottom: 1px dashed #ddd;
}

.lab {
  width: 160px;
  text-align: right;
  flex-shrink: 0;
}

.val {
  flex: 1;
  text-align: left;
}

/deep/.table-header {
  background-color: #f2f2f2 !important;
  padding: 6px 0;
  border-bottom: 1px solid #e2e2e2 !important;
  border-top: 1px solid #e2e2e2 !important;
  border-right: 1px solid #e2e2e2 !important;
  border-right: transparent;
  text-align: left;
}

/deep/.table-header .cell {
  font-weight: bold;
  color: #303133;
}

.cust-dialog /deep/.el-table .el-table__cell {
  padding: 6px 0;
}
.causeTip{
  display: inline-block;
  width: 24px;
  height: 24px;
  line-height: 24px;
  font-size: 12px;
  text-align: center;
  background-color: #FF6600;
  color: #fff;
  margin-right: 5px;
  border-radius: 50%;
  cursor: default;
}
</style>
