<template>
  <div>
    <!--执行情况-->
    <el-drawer title="执行控制" :visible.sync="show" :wrapperClosable="false"
               custom-class="spl-filter-drawer detail-drawer"
               :show-close="true" size="100%">
      <div style="padding: 0px 20px 20px 20px;">
        <div class="type-title clearfix" style="margin-top: 10px;"><span class="text">执行计划</span></div>
        <div :key="refreshExecutionPlan">
          <executionPlan :infoData="rowData" :isComponent="true" @refreshTable="refreshTable"></executionPlan>
        </div>
        <div class="mt-10 mb-20">
          <el-button size="small" type="primary" class="w-70" @click="handleAllExecutionPlan">查看所有账户执行计划</el-button>
        </div>

        <div class="type-title mt-0 clearfix"><span class="text">单独执行</span></div>
        <div class="pl-10 pt-10">
          <!--单独执行-->
          <singleRun ref="singleRun" :isComponent="true" @handleExecutionQuery="handleExecutionQuery"></singleRun>
        </div>

        <div class="mt-20">
<!--          <el-button size="small" type="primary" class="w-70" @click="handleAllRun">全执行</el-button>-->
          <el-button size="small" type="primary" class="w-70" @click="handleExecutionQuery">查看执行记录</el-button>
        </div>
      </div>
    </el-drawer>

    <!--    执行查询（查记录）-->
    <ExecuteQuery :rowData="rowData" v-if="showIndex == 1" @back="toIndex" />
    <!--    全部执行计划 -->
    <executionPlan :infoData="infoData" v-if="showIndex == 2" @to-index="toIndex" @refreshTable="refreshTable2"></executionPlan>
  </div>
</template>
<script>
import ExecutionPlan from '../executionPlan'
import singleRun from './singleRun'
import ExecuteQuery from '@/views/customerAppList/components/executeQuery.vue'
export default {
  name: 'ExecutionPlanAll',
  props: [],
  components: { ExecuteQuery, ExecutionPlan, singleRun },
  computed: {},
  watch: {},
  data () {
    return {
      show: false,
      rowData: null,
      infoData: null,
      customerName: '',
      appName: '',
      infoList: [],
      row: null,
      declarePolicyData: null,
      showIndex: 0, // 0-执行计划弹窗  1-执行查询页面  2-全部执行计划
      refreshExecutionPlan: 0
    }
  },
  created () {
  },
  mounted () {
  },
  methods: {
    init (obj) {
      this.show = true
      this.rowData = this.$global.deepCopyObj(obj)
      this.rowData.isSearchAccNumber = true
      this.infoData = this.$global.deepCopyObj(obj)
      this.infoData.isSearchAccNumber = false
      this.refreshExecutionPlan = new Date().getTime()
      this.handleSingleRun()
    },
    // 单独执行
    handleSingleRun () {
      let row = this.rowData
      let obj = {
        customerName: row.customerName,
        appName: row.appName,
        businessType: row.businessType,
        accountNumber: row.accNumber,
        orgName: row.orgName,
        taskCode: row.taskCode,
        appCode: row.appCode,
        taskStatus: row.taskStatus,
        taskComment: row.taskComment
      }
      this.$nextTick(() => {
        this.$refs.singleRun.init(obj)
      })
    },
    //   全执行
    handleAllRun () {
      let that = this
      let text = '是否确定执行此流程？'
      if (this.rowData.taskStatus === '已停用') {
        text = '当前账户停用，原因：' + this.rowData.taskComment + '，是否继续？'
      }
      this.$confirm(text, '提示', {
        confirmButtonText: '继续',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning'
      }).then(() => {
        that.doAllRun(true)
      }).catch(() => {
      })
    },
    doAllRun (checkSchedule) {
      let that = this
      let row = this.rowData
      that.$http({
        url: `/robot/app/client/runTask/${row.appCode}/${row.taskCode}/${row.machineCode}?flowCodes=&serviceItemName=&checkSchedule=${checkSchedule}`,
        method: 'post',
        headers: {
          customSuccess: true
        }
      }).then((data) => {
        if (data.data.code === 200) {
          that.$message.success('操作成功')
        } else if (data.data.code === 500 && data.data.data === '该任务不在执行计划期内') {
          that.$confirm('该任务不在执行计划期内，确定执行？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            showClose: false,
            customClass: 'pal-confirm',
            type: 'warning'
          }).then(() => {
            that.doAllRun(false)
          }).catch(() => {
          })
        } else {
          that.$message.error('操作失败')
        }
      }).catch(() => {
      })
    },
    refreshTable () {
      this.$emit('refreshTable')
    },
    refreshTable2 () {
      this.refreshExecutionPlan = new Date().getTime()
      this.$emit('refreshTable')
    },
    handleExecutionQuery () {
      this.show = false
      this.showIndex = 1
      this.$emit('to-index', -1)
    },
    handleAllExecutionPlan () {
      this.show = false
      this.showIndex = 2
      this.$emit('to-index', -1)
    },
    toIndex () {
      this.show = true
      this.showIndex = 0
      this.$emit('to-index', 0)
    }
  }
}
</script>
<style lang="less" scoped>
/deep/ .detail-drawer {
  width: 1200px !important;
}
</style>
