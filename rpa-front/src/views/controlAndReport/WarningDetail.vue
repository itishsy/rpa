<template>
  <div class="spl-container">
    <!-- 监控面板 -->
    <breadcrumb :data="pathData"></breadcrumb>
    <div style="padding: 0px 20px 0 20px">
      <div class="search-row clearfix">
        <el-row type="flex" justify="space-between">
          <el-col :span="7" class="display-flex">
            <span class="label">处理状态：</span>
            <div style="display: flex; align-items: center; padding-right: 0px">
              <el-radio-group size="medium" v-model="searchData.handleStatus">
                <el-radio-button v-for="(item, index) in statusArr" :key="index" :label="item.id" class="radio-button">
                  <div class="tabs-sty-all">
                    {{ item.name }}
                  </div>
                </el-radio-button>
              </el-radio-group>
            </div>
          </el-col>
          <el-col :span="7" class="display-flex">
            <span class="label">上报时间：</span>
            <div style="display: flex; align-items: center; padding-right: 0px">
              <el-date-picker v-model="searchData.startDate" type="date" value-format="yyyy-MM-dd" placeholder="开始时间"
                style="width: 150px"></el-date-picker>
              <span style="margin: 0 5px">至</span>
              <el-date-picker v-model="searchData.endDate" type="date" value-format="yyyy-MM-dd" placeholder="结束时间"
                style="width: 150px"></el-date-picker>
            </div>

          </el-col>
          <el-col :span="7"></el-col>
          <el-col :span="1" class="text-right">
          </el-col>
          <el-col :span="1" class="text-right">
          </el-col>


        </el-row>
        <el-row class="mt-10" type="flex" justify="space-between">
          <el-col :span="7" class="display-flex">
            <span class="label">客户名称：</span>
            <el-select v-model="searchData.clientName" class="search-row-item" clearable filterable placeholder="请选择"
              @change="handleClientId">
              <el-option v-for="item in listCustomerOption" :key="item.id" :label="item.customerName"
                :value="item.id"></el-option>
            </el-select>
          </el-col>
          <el-col :span="7" class="display-flex">
            <span class="label">预警项目：</span>
            <el-select v-model="searchData.warnType" class="search-row-item" clearable filterable placeholder="请选择"
              @change="handlewarnType">
              <el-option v-for="item in warnTypeList" :key="item.id" :label="item.dictName" :value="item.dictName">
              </el-option>
            </el-select>
          </el-col>
          <el-col :span="7" class="display-flex">
            <!-- <span class="label">预警项目：</span> -->
            <el-input v-model="searchData.keyword" placeholder="请输入关键字查询" class="w-300"></el-input>
          </el-col>
          <el-col :span="1" class="text-right">
            <el-button class="ml-25" size="small" type="primary" @click="getTableData(true)">查询</el-button>
          </el-col>
          <el-col :span="1" class="text-right">
            <el-button class="ml-15" size="small" type="primary" plain @click="doBusiness">办结</el-button>
          </el-col>
        </el-row>
      </div>
      <!-- 表格 -->
      <dTable @fetch="getTableData" ref="dTable" :tableHeight="tableHeight" :paging="true" :showSelection="true"
        :showIndex="true" rowKey="id" @selection-change="selectChange">
        <u-table-column prop="warnType" label="预警项目" min-width="160" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <span class="view-text" @click="checkDetail(scope.row)">{{ scope.row.warnType || '-'
            }}</span>
          </template>
        </u-table-column>
        <u-table-column prop="reportTime" label="上报时间" min-width="160" :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="clientName" label="客户名称" min-width="140" :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="machineCode" label="设备编号" min-width="140" :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="addrName" label="参保城市" min-width="110"></u-table-column>
        <u-table-column prop="serviceName" label="业务" min-width="110"></u-table-column>
        <u-table-column prop="accountNumber" label="申报账户" min-width="110" :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="warnCount" label="预警次数" min-width="110"></u-table-column>
      </dTable>
    </div>
    <!-- 办结drawer -->
    <el-drawer title="办结" :visible.sync="fieldDrawer.show" :wrapperClosable="false" custom-class="spl-filter-drawer"
      :before-close="cancelFieldDrawer" :close-on-press-escape="false">
      <div class="formDrawer-content">
        <p>已选择{{ selectRows.length }}条数据</p>
        <el-form :model="fieldDrawer" ref="fieldDrawer" label-width="0">
          <el-form-item label="" prop="handleType" :rules="[{ required: true, message: '请选择原因归类', trigger: ['change'] }]">
            <p class="item-lab required-pre">原因归类</p>
            <el-select placeholder="请选择" v-model="fieldDrawer.handleType" clearable filterable class="w-p100">
              <el-option v-for="item in handleTypeList" :key="item.dictCode" :label="item.dictName"
                :value="item.dictCode"></el-option>
            </el-select>
            <p class="text-606 text-right" style="line-height: normal">网站页面元素变更，逻辑变更或出现新弹框等</p>
          </el-form-item>
          <el-form-item label="" prop="handleLink">
            <p class="item-lab  clearfix">补充说明</p>
            <el-input v-model.trim="fieldDrawer.handleRemark" type="textarea" :autosize="{ minRows: 4, maxRows: 46 }"
              placeholder="请输入具体原因，及处置方式" clearable></el-input>
          </el-form-item>
          <el-form-item label="" prop="handleLink">
            <p class="item-lab clearfix">关联链接</p>
            <el-input v-model.trim="fieldDrawer.handleLink" placeholder="请输入对应的jira号" clearable></el-input>
          </el-form-item>


        </el-form>
      </div>

      <div class="drawer-footer-buts">
        <el-button class="btn--border-blue s-btn" @click="cancelFieldDrawer">取消</el-button>
        <el-button type="primary" class="s-btn ml-12" @click="confirmFieldDrawer">确定</el-button>
      </div>
    </el-drawer>
    <!-- 查看drawer -->
    <el-drawer title="查看" :visible.sync="detailDrawer.show" :wrapperClosable="false"
      custom-class="spl-filter-drawer detail-drawer">
      <div class="pt-20 pl-20 pr-20">
        <p class="fw-blod pb-10">状态</p>
        <el-row type="flex" style="flex-wrap:wrap;">
          <el-col :span="8">
            <div class="mb-20">处理状态：{{ currentRow.handleStatus === 1 ? '已办结' : '未处理' }}</div>
          </el-col>
          <el-col :span="8">
            <div class="mb-20">办结时间：{{ currentRow.handleDate }}</div>
          </el-col>
          <el-col :span="8">
            <div class="mb-20"></div>
          </el-col>
          <el-col :span="8">
            <div class="mb-20">原因归类：{{ currentRow.handleType }}</div>
          </el-col>
          <el-col :span="8">
            <div class="mb-20">补充说明：{{ currentRow.handleRemark }}</div>
          </el-col>
          <el-col :span="8">
            <div class="mb-20"></div>
          </el-col>
          <el-col :span="8">
            <div class="mb-20">关联链接：{{ currentRow.handleLink }}</div>
          </el-col>
        </el-row>

        <p class="fw-blod pb-10 pt-10">内容</p>
        <el-row type="flex" style="flex-wrap:wrap;">
          <el-col :span="8">
            <div class="mb-20">预警项目： {{ currentRow.warnType }}</div>
          </el-col>
          <el-col :span="8">
            <div class="mb-20">上报时间：{{ currentRow.reportTime }}</div>
          </el-col>
          <el-col :span="8">
            <div class="mb-20">客户名称： {{ currentRow.clientName }}</div>
          </el-col>
          <el-col :span="8">
            <div class="mb-20">设备编号： {{ currentRow.machineCode }}</div>
          </el-col>
          <el-col :span="8">
            <div class="mb-20">参保城市： {{ currentRow.addrName }}</div>
          </el-col>
          <el-col :span="8">
            <div class="mb-20">业务： {{ currentRow.serviceName }}</div>
          </el-col>
          <el-col :span="8">
            <div class="mb-20">申报账户： {{ currentRow.accountNumber }}</div>
          </el-col>
          <el-col :span="8">
            <div class="mb-20">消息主题：{{ currentRow.messageTopic }}</div>
          </el-col>
          <el-col :span="24">
            <div class="mb-10">消息内容：</div>
            <p v-html="currentRow.messageContent" class="mb-20"></p>
          </el-col>
        </el-row>
      </div>
      <div class="drawer-footer-buts">
        <!--编辑： 待提交，待申报，申报失败数据显示-->
        <el-button size="small" class="w-80" type="primary" @click="cancelDetailDrawer">关闭</el-button>
      </div>
    </el-drawer>
  </div>
</template>
<script>
import dTable from '@/components/common/table'
export default {
  name: '',
  components: { dTable },
  props: [''],
  data() {
    return {
      pathData: [],
      searchData: {
        keyword:'',               //关键字
        clientId: '',            //客户id
        clientName: '',         //客户名称
        startDate: '',          //上报开始时间
        endDate: '',            //上报结束时间
        machineCode: '',        //设备id
        handleStatus: '0',      //处理状态
      },
      listCustomerOption: [],     //客户列表
      warnTypeList: [],       //预警项目list
      statusArr: [
        { id: '0', name: '未处理' },
        { id: '1', name: '已办结' },
        { id: '', name: '全部' },
      ],
      fieldDrawer: {
        show: false,
        handleType: '',       //原因归类
        handleRemark: '',        //补充说明
        handleRLink: ''      //关联链接
      },
      handleTypeList: [],       //原因归类列表
      detailDrawer: {
        show: false,
      },
      currentRow: {},           //当前点击的row
      selectRows: [],               //当前选择的集合
      bussinessParams:{}      //用于业务办结的参数
    }
  },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 270 + 'px'
    }
  },
  watch: {},
  created() {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = this.$global.deepcopyArray(tabs[this.$route.meta.parentPath])
    }
    this.pathData.push({ name: '预警明细' })
    this.getWarnTypeList()
    this.getlistCustomer()
    this.getHandleTypeList()
    if (!this.$route.query.warnType) {
      this.$nextTick(() => {

        this.getTableData(false, 'route')
        // this.searchData.warnType = this.$route.query.warnType

      })
    }


    // setTimeout(()=>{

    // },500)
  },
  beforeMount() { },
  mounted() { },
  methods: {
    getTableData(isClickSearch = false, type = 'default') {
      let searchData = this.searchData
      console.log('searchData-------->', searchData)
      // if(!searchData.startDate || !searchData.endDate){
      //     this.$message.warning('请先选择上报时间')
      //     return
      // }
      if (searchData.startDate != '' && searchData.endDate != '' && new Date(searchData.startDate) > new Date(searchData.endDate)) {
        this.$message.warning('上报开始时间不能大于结束时间')
        return
      }
      let params = [
        { property: 'clientName', value: searchData.clientName },
        { property: 'clientId', value: searchData.clientId || this.$route.query.clientId },
        { property: 'reportStartTime', value: searchData.startDate },
        { property: 'reportEndTime', value: searchData.endDate },
        { property: 'machineCode', value: searchData.machineCode || this.$route.query.machineCode },
        { property: 'handleStatus', value: searchData.handleStatus },
        { property: 'warnTypeCode', value: searchData.warnTypeCode },
        { property: 'keyword', value: searchData.keyword },
      ]

      if (isClickSearch) {
        params = params.filter(item => item.property !== 'machineCode' && item.property !== 'clientId')
      }
      if (type === 'route') {
        params.push({ property: 'reportTime', value: this.$route.query.reportTime })
      }
      this.$refs.dTable.fetch({
        query: params,
        method: 'post',
        url: '/robot/operation/operationDetail',
      })
    },
    getlistCustomer() {
      this.$http({
        url: '/robot/app/client/listCustomer',
        method: 'post',
      })
        .then((data) => {
          this.listCustomerOption = data.data.data
        })
        .catch((err) => { })
    },
    handleClientId(val) {
      if (val) {
        this.searchData.clientName = this.listCustomerOption.find(item => item.id === val).customerName
      } else {
        this.searchData.clientName = null
      }
    },
    handlewarnType(val) {
      console.log('handlewarnType', val)
      if (val) {
        this.searchData.warnTypeCode = this.warnTypeList.find(item => item.dictName === val).dictCode
        // this.searchData.warnType = val
        this.$set(this.searchData, 'warnType', val)
      } else {
        this.searchData.warnTypeCode = null
        // this.searchData.warnType = null
        this.$set(this.searchData, 'warnType', null)
      }
    },
    //获取预警项目列表
    getWarnTypeList() {
      this.$http({
        url: '/robot/data/dict/10017',
        method: 'post',
        data: {}
      })
        .then((data) => {
          this.warnTypeList = data.data.data
          if (this.$route.query.warnType) {
            this.$set(this.searchData, 'warnType', this.$route.query.warnType)
            this.searchData.warnTypeCode = this.warnTypeList.find(item => item.dictName === this.$route.query.warnType).dictCode
            this.searchData.clientName = this.$route.query.clientName
            console.log('现在------', this.searchData.warnTypeCode)
            this.getTableData()
          }
        })
        .catch((err) => { })
    },
    //获取原因归类
    getHandleTypeList() {
      this.$http({
        url: '/robot/data/dict/10016',
        method: 'post',
        data: {}
      })
        .then((data) => {
          this.handleTypeList = data.data.data
        })
        .catch((err) => { })
    },
    //办结
    doBusiness() {
      //判断当前选择的数据项 必须先选择一项数据
      if (!this.selectRows || this.selectRows.length === 0) {
        this.$message.warning('请先选择需要办结的数据！')
        return
      }
      //算出当前勾选的项目的最大上报时间
      const maxReportTime = this.$moment.max(this.selectRows.map(item => this.$moment(item.reportTime)))
      console.log('maxReportTime', maxReportTime._i)
      let searchData = this.searchData
      this.bussinessParams = {
        clientName: searchData.clientName,
        clientId: searchData.clientId || this.$route.query.clientId,
        reportStartTime: maxReportTime._i,
        machineCode: searchData.machineCode || this.$route.query.machineCode,
        handleStatus: searchData.handleStatus,
        warnTypeCode: searchData.warnTypeCode
      }

      this.fieldDrawer.show = true
    },
    //drawer取消
    cancelFieldDrawer() {
      this.fieldDrawer = this.$options.data().fieldDrawer
      this.$refs.fieldDrawer.resetFields()
    },
    //drawer确认 - 提交办结业务
    confirmFieldDrawer() {
      //判断是否已经选择原因归类
      // console.log('获取到所有需要办结的ids',this.selectRows.map(item => item.groupIds).flat())

      this.$refs.fieldDrawer.validate((valid) => {
        if (valid) {
          this.$http({
            url: '/robot/operation/updateHandleMessage',
            method: 'post',
            data: {
              ...this.bussinessParams,
              // ids: this.selectRows.map(item => item.groupIds).flat(),
              handleType: this.fieldDrawer.handleType,
              handleRemark: this.fieldDrawer.handleRemark,
              handleLink: this.fieldDrawer.handleLink
            }

          })
            .then((data) => {
              // this.handleTypeList = data.data.data
              if (data) this.$message.success('操作成功')
              this.fieldDrawer.show = false
              this.getTableData()
            })
            .catch((err) => { })
        } else {
          // this.$message.warning('请先选择原因归类！')
        }
      })


    },
    cancelDetailDrawer() {
      this.detailDrawer.show = false
    },
    //查看详情
    checkDetail(row) {
      console.log('checkDetail------', row)
      this.currentRow = row
      if (row.warnType === 'RPA执行异常') {
        this.$router.push({
          path: '/customerAppList/customerIndex',
          query: {
            appCode: row.appCode,
            taskCode: row.taskCode,
            clientId: row.clientId,
            machineCode: row.machineCode
          }
        })
      } else {
        this.detailDrawer.show = true
      }
    },
    //表格选择回调
    selectChange(data) {
      this.selectRows = data
      console.log('selectChange', data)
    }
  },
}
</script>
<style lang="less" scoped>
.search-row-item {
  width: 80%;
  min-width: 150px;
  max-width: 330px;
}

.view-text {
  // text-decoration: underline;
  cursor: pointer;
  color: @blueColor;
}

.red {
  color: @redColor;
}


// /deep/ .el-radio-button__inner {
//   width: 130px;
// }

.formDrawer-content {
  padding: 15px 30px 30px 30px;
  position: relative;
  min-height: 100%;

  .msg-tip {
    position: absolute;
    bottom: 10px;
    left: 30px;
    color: @redColor;
    background-color: #fff;
    z-index: 5;
  }
}

/deep/.detail-drawer {
  width: 60% !important;
  max-width: 1000px !important;

  .pal-tabs {
    padding-left: 0;
    height: 75px;
    line-height: 62px;
    position: relative;
    top: -10px;
  }

  .span-tabs {
    height: 71px;
    line-height: 80px;
  }
}
</style>
