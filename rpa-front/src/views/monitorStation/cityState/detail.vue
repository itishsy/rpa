<template>
  <div class="spl-container">
    <div v-show="isShowMainPage">
      <breadcrumb :data="pathData">
        <el-button type="primary" slot="breadcrumb-btn" size="small" @click="handleEdit">编辑信息</el-button>
      </breadcrumb>
      <backtop></backtop>
      <div class="pl-20 pr-20">
        <div class="pt-20 pb-20" style="border-bottom: 1px solid #dbdbdb" v-if="cityInfo">
          <div class="display-flex pl-10 pr-10">
            <i class="el-icon-office-building text-blue font-24"></i>
            <div class="flex1 ml-10">
              <el-row>
                <el-col :span="span">
                  <p>城市：{{cityInfo.addrName}}</p>
                </el-col>
                <el-col :span="span">
                  <p>应用：{{cityInfo.appName}}</p>
                </el-col>
                <el-col :span="span">
                  <p>上线阶段： {{handleOnlineStatus(cityInfo.onlineStatus)}}</p>
                </el-col>
                <el-col :span="span">
                  <p>最迟申报时间：{{cityInfo.latestDeclarationTime?('每月'+cityInfo.latestDeclarationTime+'号'):''}}</p>
                </el-col>
              </el-row>
              <el-row class="mt-15">
                <el-col :span="span">
                  <p>应用状态：<span>{{cityInfo.runStatus==1?'已上架':'下架'}}</span></p>
                </el-col>
                <el-col :span="span">
                  <div class="display-flex">网站链接：<div class="flex1 websiteLinks" v-html="handleWebsiteLinks(cityInfo.websiteLinks)"></div></div>
                </el-col>
                <el-col :span="span">
                  <p>当前版本号：<span class="text-blue">{{cityInfo.version}}</span></p>
                </el-col>
                <el-col :span="span">
                  <p>当前版本状态：
                    <span v-if="cityInfo.status === 1">已发布</span>
                    <span class="text-gray" v-else-if="cityInfo.status === 0">未发布</span>
                    <span class="text-red" v-else-if="cityInfo.status === 2">有更新未发布</span></p>
                </el-col>
              </el-row>
            </div>
          </div>
        </div>
        <div class="pt-20 pb-20" v-if="statistics">
          <div class="display-flex pl-10 pr-10">
            <i class="el-icon-s-data text-blue font-24"></i>
            <div class="flex1 ml-10">
              <el-row>
                <el-col :span="24">
                  <p>运行总数据：{{statistics.waitCount+statistics.doingCount+statistics.successCount+statistics.failCount}}</p>
                </el-col>
              </el-row>
              <el-row class="mt-15">
                <el-col :span="span">
                  <p>待申报：{{statistics.waitCount}}</p>
                </el-col>
                <el-col :span="span">
                  <p>申报中：{{statistics.doingCount}}</p>
                </el-col>
                <el-col :span="span">
                  <p>申报成功：{{statistics.successCount}}</p>
                </el-col>
                <el-col :span="span">
                  <p>申报失败：{{statistics.failCount}}</p>
                </el-col>
              </el-row>
            </div>
          </div>
        </div>
      </div>

      <div class="bottom-area">
        <palTab :tabs="tabs" v-model="tabActive" :type="2" :key="tabRefreshKey">
        </palTab>
      </div>

      <!-- 编辑 -->
      <editCityInfo ref="editCityInfo" :editData="editForm" @success="editSuccess"></editCityInfo>
    </div>
    <div v-show="tabActive===0">
      <deviceManagement v-if="deviceManagementConfig.id" :configData="deviceManagementConfig"></deviceManagement>
    </div>
    <div v-show="tabActive===1">
      <customerAppList v-if="customerAppListConfig.id" :configData="customerAppListConfig" @changePage="customerAppListChangePage"></customerAppList>
    </div>
    <div v-show="tabActive===2">
      <mainSummary v-if="mainSummaryConfig.id" :configData="mainSummaryConfig" @tabCountCallback="mainSummaryTabCountCallback"></mainSummary>
    </div>
  </div>
</template>
<script>

import palTab from 'components/common/pal-tab.vue'
import deviceManagement from '@/views/deviceManagement/index.vue'
import customerAppList from '@/views/customerAppList/customerIndex'
import mainSummary from '../components/mainSummary'
import editCityInfo from '../components/editCityInfo'
export default {
  components: { palTab, editCityInfo, deviceManagement, customerAppList, mainSummary },
  data () {
    return {
      pathData: [],
      span: 6,
      tabActive: 0,
      tabs: ['盒子汇总', '客户汇总', '主体汇总'],
      tabRefreshKey: 0,
      loading: null,
      id: '',
      cityInfo: null,
      statistics: null,
      editForm: null,
      deviceManagementConfig: {
        id: '',
        tableUrl: '/robot/monitor/city/boxPage',
        customerOptions: []
      },
      customerAppListConfig: {
        id: '',
        appCode: '',
        customerOptions: [],
        maCodeOptions: []
      },
      mainSummaryConfig: {
        id: '',
        addrName: ''
      },
      isShowMainPage: true
    }
  },
  computed: {
  },
  watch: {
  },
  created () {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = this.$global.deepcopyArray(tabs[this.$route.meta.parentPath])
    }
    this.pathData.push({ name: '城市详情' })
    let id = this.$route.query.id
    if (id) {
      this.id = id
      this.deviceManagementConfig.id = id
      this.getDetail()
      this.getStatistics()
    }
  },
  mounted () {
  },
  methods: {
    getDetail () {
      let that = this
      this.$http({
        url: '/robot/monitor/city/detail?id=' + this.id,
        method: 'get'
      }).then(({ data }) => {
        that.cityInfo = data.data
        that.mainSummaryConfig.id = that.id
        that.mainSummaryConfig.addrName = data.data.addrName
        this.customerAppListConfig.id = that.id
        this.customerAppListConfig.appCode = data.data.appCode
        that.getClientAndMaCode(1)
        that.getClientAndMaCode(2)
      })
    },
    getStatistics () {
      let that = this
      this.$http({
        url: '/robot/monitor/city/declareStatistics?id=' + this.id,
        method: 'get'
      }).then(({ data }) => {
        that.statistics = data.data
      })
    },
    // 获取客户和盒子下拉
    getClientAndMaCode (number) {
      let that = this
      this.$http({
        url: '/robot/monitor/city/getClientAndMaCode?id=' + that.id + '&number=' + number,
        method: 'post'
      }).then(({ data }) => {
        // 客户汇总的客户下拉值的获取需要传参数number=2
        var clientList = data.data.clientList
        clientList.map(item => {
          item.id = item.clientId
          item.customerName = item.clientName
        })
        if (number === 2) {
          that.customerAppListConfig.customerOptions = data.data.clientList
          that.tabs[1] = '客户汇总(' + clientList.length + ')'
        }else{
          that.deviceManagementConfig.customerOptions = clientList
          that.customerAppListConfig.maCodeOptions = data.data.maCodeList
          that.tabs[0] = '盒子汇总(' + data.data.maCodeList.length + ')'
        }
        that.tabRefreshKey = new Date().getTime()
      })
    },
    // 编辑
    handleEdit () {
      var row = this.cityInfo
      this.editForm = {
        show: true,
        id: row.id,
        addrName: row.addrName,
        appCode: row.appCode,
        appName: row.appName,
        onlineStatus: row.onlineStatus,
        websiteLinks: row.websiteLinks,
        latestDeclarationTime: row.latestDeclarationTime
      }
      this.$refs.editCityInfo.editForm.show = true
    },
    editSuccess () {
      this.getDetail()
    },
    handleOnlineStatus (status) {
      if (status === 0) {
        return '调研'
      } else if (status === 1) {
        return '基础配置'
      } else if (status === 2) {
        return '上线'
      } else if (status === 3) {
        return '运维'
      }
      return ''
    },
    handleWebsiteLinks (links) {
      var arr = []
      var linkArr = links ? links.split(',') : []
      linkArr.map(item => {
        arr.push("<p class='mb-10'><a href='" + item + "' target='_blank' class='a-aim text-blue'>" + item + '</a></p>')
      })
      return arr.join('')
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
    mainSummaryTabCountCallback (obj) {
      this.tabs[2] = '主体汇总(' + obj.totalCount + ')'
      this.tabRefreshKey = new Date().getTime()
    },
    customerAppListChangePage (val) {
      console.log("val=="+val)
      this.isShowMainPage = val
    }
  }
}
</script>
<style lang="less" scoped>
.bottom-area{
  padding: 20px 20px 0px 20px;
  border-top: 20px solid @bodyBaseBgc;
}
.websiteLinks{
  word-break: break-all;
  padding-right: 15px;
}
</style>
