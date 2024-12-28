<template>
  <div class="spl-container">
    <breadcrumb :data="pathData"></breadcrumb>
    <backtop></backtop>
    <div class="pl-20 pr-20">
      <div class="pt-20 pb-20" style="border-bottom: 1px solid #dbdbdb">
        <el-row class="font-16 fw-blod">
          <el-col :span="12">
            <i class="el-icon-office-building text-orange font-24"></i>
            <span class="ml-5">已上线城市应用数：<span class="text-green" v-if="statistics">{{statistics.onlineRobotCityCount}}</span></span>
            <i class="el-icon-office-building text-gray font-24 ml-30"></i>
            <span class="ml-5">下线城市应用数：<span class="text-red" v-if="statistics">{{statistics.offlineRobotCityCount}}</span></span>
          </el-col>
          <el-col :span="12" class="text-right">
            <i class="el-icon-s-data text-blue font-24"></i>
            <span class="ml-5">调研：<span class="text-red" v-if="statistics">{{statistics.surveyCount}}</span></span>
            <span class="ml-30">基础配置：<span class="text-red" v-if="statistics">{{statistics.configurationCount}}</span></span>
            <span class="ml-30">上线：<span class="text-red" v-if="statistics">{{statistics.onlineCount}}</span></span>
            <span class="ml-30">运维：<span class="text-red" v-if="statistics">{{statistics.maintenanceCount}}</span></span>
          </el-col>
        </el-row>
      </div>
      <div class="search-row">
        <el-row>
          <el-col :span="7">
            <span class="label">城市：</span>
            <addrSelector v-model="search.addrName" :addrArr="options.allAddr" @changeAddrSelect="changeAddrSelect"></addrSelector>
          </el-col>
          <el-col :span="8">
            <span class="label">上线阶段：</span>
            <el-select placeholder="请选择" filterable v-model="search.onlineStatus" style="width:260px;" clearable>
              <el-option v-for="(item, index) in options.onlineStatus" :key="index" :label="item.dictName"
                         :value="item.dictCode"></el-option>
            </el-select>
          </el-col>
          <el-col :span="7">
            <span class="label">应用：</span>
            <el-select placeholder="请选择" filterable v-model="search.appType" style="width:260px;" clearable>
              <el-option label="社保" value="1"></el-option>
              <el-option label="公积金" value="2"></el-option>
            </el-select>
          </el-col>
          <el-col :span="2" class="text-right">
            <el-button type="primary" size="small" @click="doSearch" class="ml-20 w-60">查询</el-button>
          </el-col>
        </el-row>
      </div>

      <div>
        <dTable @fetch="getTableData" ref="table" :cancelMinHeight="true" :showIndex='false' :showSelection='false' row-key="id" row-id="id">
          <u-table-column prop="appName" label="城市" fixed="left" header-align="center" align="center" min-width="120" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="runStatus" label="状态" fixed="left" header-align="center" align="center" min-width="70" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <span>{{scope.row.runStatus==1?'已上架':'下架'}}</span>
            </template>
          </u-table-column>
          <u-table-column prop="onlineStatus" label="上线阶段" header-align="center" align="center" min-width="80" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <span>{{handleOnlineStatus(scope.row.onlineStatus)}}</span>
            </template>
          </u-table-column>
          <u-table-column prop="releaseTime" label="已配置模块"  header-align="center" align="center" min-width="240" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <el-tag :type="scope.row.configuredAdd>0?'':'info'">增员</el-tag>
              <el-tag :type="scope.row.configuredDel>0?'':'info'" class="ml-10">减员</el-tag>
              <el-tag :type="scope.row.configuredAdjust>0?'':'info'" class="ml-10">调基</el-tag>
              <el-tag :type="scope.row.configuredRepair>0?'':'info'" class="ml-10">补缴</el-tag>
            </template>
          </u-table-column>
          <u-table-column prop="version" label="当前版本号" header-align="center" align="center" min-width="100" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <span class="text-blue">{{scope.row.version}}</span>
            </template>
          </u-table-column>

          <u-table-column prop="status" label="当前版本状态" header-align="center" align="center" min-width="110" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <!-- 1 已发布，0 未发布 , 2 有更新未发布-->
              <span v-if="scope.row.status === 1">已发布</span>
              <span class="text-red" v-else-if="scope.row.status === 0">有新版本未发布</span>
              <span class="text-red" v-else-if="scope.row.status === 2">有更新未发布</span>
            </template>
          </u-table-column>
          <u-table-column prop="websiteLinks" label="网站链接" header-align="center" align="center" min-width="200" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <div class="over-ell" v-html="handleWebsiteLinks(scope.row.websiteLinks)"></div>
            </template>
          </u-table-column>
          <u-table-column prop="latestDeclarationTime" label="最迟申报时间" header-align="center" align="center" min-width="110" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <span>{{scope.row.latestDeclarationTime?('每月'+scope.row.latestDeclarationTime+'号'):''}}</span>
            </template>
          </u-table-column>
          <u-table-column prop="execTotalCount" label="运行总数据" header-align="center" align="center" min-width="100" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="execTotalTime" label="执行时间" header-align="center" align="center" min-width="80" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <span>{{scope.row.execTotalTime}} h</span>
            </template>
          </u-table-column>
          <u-table-column prop="configuredBoxCount" label="配置盒子总数" header-align="center" align="center" min-width="110" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="configuredCustomCount" label="配置客户总数" header-align="center" align="center" min-width="110" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="configuredPrincipalCount" label="配置主体总数" header-align="center" align="center" min-width="110" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="remark" label="备注" header-align="center" align="center" min-width="200" :show-overflow-tooltip="true"></u-table-column>

          <u-table-column label="操作" align="left" width="150" fixed="right">
            <template slot-scope="scope">
              <el-button type="text" size="small" class="text-blue" @click="goView(scope.row)">查看</el-button>
              <div class="opt-btn-split"></div>
              <el-button type="text" size="small" class="text-blue" @click="handleEdit(scope.row)">编辑</el-button>
              <div class="opt-btn-split"></div>
              <el-button v-if="scope.row.runStatus==1" type="text" size="small" class="text-red" @click="handleOff(scope.row)">下架</el-button>
              <span v-else class="text-green font-12 f-cursor" @click="handleOn(scope.row)">上架</span>
            </template>
          </u-table-column>
        </dTable>
      </div>
    </div>
    <!-- 编辑 -->
    <editCityInfo ref="editCityInfo" :editData="editForm" @success="editSuccess"></editCityInfo>

    <!-- 下架 -->
    <el-dialog
      title="是否下架"
      :visible.sync="offForm.show"
      width="600px"
      class="cust-dialog"
      :before-close="cancelOffForm"
      :close-on-click-modal="false">
      <div class="">
        <p class="text-center fw-blod">该城市应用所有运行数据将终止！</p>
        <el-form :model="offForm" ref="offForm" label-width="100px">
          <div class="mt-20">
            <el-form-item prop="reason" label="原因：" :rules="[{ required: true, message: '请选择', trigger: 'blur' }]">
              <el-input class="" size="medium" type="textarea" :rows="3" placeholder="请输入" style="width:400px;" v-model.trim="offForm.reason"></el-input>
            </el-form-item>
          </div>
        </el-form>
      </div>
      <div class="text-right pt-20 pb-10">
        <el-button @click="cancelOffForm" size="small" class="w-60">取 消</el-button>
        <el-button type="primary" @click="confirmOffForm" style="margin-left: 20px;" size="small" class="w-60">确定</el-button>
      </div>
    </el-dialog>

  </div>
</template>
<script>

import dTable from '../../../components/common/table'
import addrSelector from '../../../components/common/addrSelector'
import editCityInfo from '../components/editCityInfo'
export default {
  components: { addrSelector, dTable, editCityInfo },
  data () {
    return {
      pathData: [],
      search: {
        addrId: '',
        addrName: '',
        onlineStatus: '',
        appType: ''
      },
      options: {
        allAddr: [],
        onlineStatus: [
          { dictCode: 0, dictName: '调研' },
          { dictCode: 1, dictName: '基础配置' },
          { dictCode: 2, dictName: '上线' },
          { dictCode: 3, dictName: '运维' }
        ],
        reason: []
      },
      editForm: null,
      offForm: {
        show: false,
        reason: '',
        id: ''
      },
      loading: null,
      statistics: null
    }
  },
  computed: {
  },
  watch: {
  },
  created () {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = tabs[this.$route.path]
    }
    this.getAddr()
  },
  mounted () {
    this.getStatistics()
    this.$nextTick(() => {
      this.getTableData()
    })
  },
  methods: {
    getTableData () {
      var params = [
        { property: 'addrName', value: this.search.addrName },
        { property: 'onlineStatus', value: String(this.search.onlineStatus) },
        { property: 'appType', value: this.search.appType }
      ]
      this.$refs.table.fetch({
        query: params,
        method: 'post',
        url: '/robot/monitor/city/page'
      })
    },
    getStatistics () {
      let that = this
      this.$http({
        url: '/robot/monitor/city/statistics',
        method: 'get'
      }).then(({ data }) => {
        that.statistics = data.data
      })
    },
    // 获取参保地
    getAddr (type) {
      let that = this
      this.$http({
        url: 'policy/declareAddr/addrList',
        method: 'post'
      }).then(({ data }) => {
        that.options.allAddr = data.data
      })
    },
    doSearch () {
      this.getTableData()
    },
    // 改变地区
    changeAddrSelect (item) {
      if (item.id !== this.search.addrId) {
        this.search.addrId = item.id
      }
    },
    // 去查看页面
    goView (row) {
      this.$router.push('/cityState/detail?id=' + row.id)
    },
    // 编辑
    handleEdit (row) {
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
      this.getStatistics()
      this.doSearch()
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
        arr.push("<a href='" + item + "' target='_blank' class='a-aim text-blue'>" + item + '</a>')
      })
      return arr.join('；')
    },

    // 上架
    handleOn (row) {
      let that = this
      this.$confirm('是否确定上架？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning'
      }).then(() => {
        that.showLoading()
        that.$http({
          url: '/robot/monitor/city/up',
          method: 'put',
          data: {
            appId: row.id
          }
        }).then(({ data }) => {
          that.closeLoading()
          that.$message.success('操作成功')
          that.doSearch()
          that.getStatistics()
        })
      }).catch(() => {
        that.closeLoading()
      })
    },
    // 下架
    handleOff (row) {
      this.offForm.id = row.id
      this.offForm.show = true
    },
    // 下架-取消
    cancelOffForm () {
      this.offForm = {
        show: false,
        reason: '',
        id: ''
      }
      this.$nextTick(() => {
        this.$refs.offForm.resetFields()
      })
    },
    // 下架-确定
    confirmOffForm () {
      let that = this
      this.$refs.offForm.validate((valid) => {
        if (valid) {
          that.showLoading()
          that.$http({
            url: '/robot/monitor/city/down',
            method: 'put',
            data: {
              appId: that.offForm.id,
              remark: that.offForm.reason
            }
          }).then(({ data }) => {
            that.cancelOffForm()
            that.closeLoading()
            that.$message.success('操作成功')
            that.doSearch()
            that.getStatistics()
          }).catch(() => {
            that.closeLoading()
          })
        }
      })
    },
    showLoading (target) {
      this.loading = this.$loading({
        target: target || document.body,
        lock: true,
        text: '请稍等...',
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
/deep/.el-form-item{
  margin-bottom: 15px;
}
</style>
