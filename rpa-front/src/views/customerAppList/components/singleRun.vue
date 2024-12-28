<template>
  <div>
    <!--单独执行-->
    <div :style="{padding: isComponent?'0px':'20px'}" v-loading="loading">
      <el-row v-if="infoData && !isComponent">
        <el-col :span="12" class="row-item">
          <p class="lab">应用名称：</p>
          <p class="sel">{{ infoData.appName }}</p>
        </el-col>
        <el-col :span="12" class="row-item">
          <p class="lab">客户名称：</p>
          <p class="sel">{{ infoData.customerName }}</p>
        </el-col>
        <el-col :span="24" class="row-item">
          <p class="lab">申报账户：</p>
          <p class="sel">{{ infoData.orgName }}-{{ infoData.accountNumber }}</p>
        </el-col>
      </el-row>

      <div class="flow-list">
        <div class="li" :class="{'li-stop': item.status===0}" v-for="(item, index) in infoList" :key="index">
          <div class="num">{{ index + 1 }}</div>
          <div class="content">
            <div class="type">{{ item.serviceItemName }}
              <el-tooltip v-if="item.status===0" effect="dark" :content="item.comment" placement="top">
                <i class="el-icon-question text-orange font-18" style="vertical-align: middle;margin-top: -2px;"></i>
              </el-tooltip>
            </div>
            <div class="count noDeclaredCount text-red">
              <el-tooltip effect="dark" content="本月未完成/网站审核中" placement="top">
                <span v-if="['增员','减员','调基', '补缴'].indexOf(item.serviceItemName)>=0">{{ item.noDeclaredCount + '/' +  item.auditCount}}</span>
              </el-tooltip>
            </div>
            <div class="execPlant">
              <el-tooltip effect="dark" :content="item.execPlant" placement="top">
                <div class="execPlant-text">{{ item.execPlant }}</div>
              </el-tooltip>
            </div>
            <div class="count declaredCount text-green">
              <el-tooltip effect="dark" content="今日已完成" placement="top">
                <span>{{ item.todayDeclaredCount!=null ? '+'+item.todayDeclaredCount : ''}}</span>
              </el-tooltip>
            </div>
            <div class="count execCount">
              <el-tooltip effect="dark" content="今日执行次数" placement="top">
                <span>{{ item.execCount!=null?item.execCount : 0 }}次</span>
              </el-tooltip>
            </div>
            <div class="opt">
              <i class="el-icon-search text-blue f-cursor fw-blod font-16 mr-20" title="查记录" @click="handleExecutionQuery(item)"></i>
              <i v-if="item.status===1" class="el-icon-video-play text-blue f-cursor fw-blod font-16 mr-20" title="执行" @click="handleRun(item)"></i>
              <i v-if="item.status===0" class="el-icon-s-goods text-blue f-cursor fw-blod font-16" title="启用" @click="handleTaskFlowStatus(item)"></i>
              <i v-if="item.status===1" class="el-icon-s-goods text-gray f-cursor fw-blod font-16" title="停用" @click="handleTaskFlowStatus(item)"></i>
            </div>
          </div>
        </div>
      </div>

    </div>
    <!--    停用流程-->
    <el-dialog :visible.sync="stopForm.show" width="500px" :close-on-click-modal="false" title="停用账户" class="cust-dialog" :modal-append-to-body="false" :append-to-body="true">
      <el-form style="margin-top: 10px;padding: 0px 10px 10px 10px;" ref="stopForm" label-width="55px" :model="stopForm">
        <p class="mb-15">停用此服务项目下的所有流程自动任务</p>
        <el-form-item prop="comment" label="原因:" :rules="[{ required: true, message: '请输入原因',trigger:'blur' }]">
          <el-input type="textarea" :rows="4" v-model="stopForm.comment" maxlength="500" placeholder="请输入原因" style="margin-top: 5px;"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button class="s-btn" @click="stopForm.show = false">取消</el-button>
        <el-button type="primary" class="s-btn" @click="confirmStop()">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
export default {
  name: 'singleRun',
  props: {
    isComponent: {
      type: Boolean,
      default: false
    }
  },
  components: {},
  computed: {},
  watch: {},
  data () {
    return {
      infoData: null,
      infoList: [],
      loading: false,
      stopForm: {
        show: false,
        comment: '',
        row: null
      }
    }
  },
  created () {
  },
  mounted () {
  },
  methods: {
    init (obj) {
      this.infoData = obj
      this.getData()
    },
    getData () {
      let obj = this.infoData
      this.infoList = []
      this.loading = true
      this.$http({
        url: `/robot/app/client/showServiceItemFlow/${obj.appCode}/${obj.taskCode}/${obj.accountNumber}`,
        method: 'post'
      }).then((data) => {
        if (data.data.code == 200) {
          this.infoList = data.data.data
          this.loading = false
        }
      }).catch(() => {
        this.loading = false
      })
    },
    //   执行
    handleRun (row) {
      let that = this
      let text = '是否确定执行此流程？'
      if (this.infoData.taskStatus === '已停用') {
        text = '当前账户停用，原因：' + (this.infoData.taskComment ? this.infoData.taskComment : '') + '，是否继续？'
      }
      this.$confirm(text, '提示', {
        confirmButtonText: '继续',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning'
      }).then(() => {
        that.doRun(row, true)
      }).catch(() => {
      })
    },
    doRun (row, checkSchedule) {
      let that = this
      let flowCodes = row.flowCodes.join(',')
      that.$http({
        url: `/robot/app/client/runTask/${row.appCode}/${row.taskCode}/${row.machineCode}?flowCodes=${flowCodes}&serviceItemName=${row.serviceItemName}&checkSchedule=${checkSchedule}`,
        method: 'post',
        headers: {
          customSuccess: true
        }
      }).then((data) => {
        if (data.data.code === 200) {
          that.$message.success('操作成功')
          that.getData()
        } else if (data.data.code === 500 && data.data.data === '该任务不在执行计划期内') {
          that.$confirm('该任务不在执行计划期内，确定执行？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            showClose: false,
            customClass: 'pal-confirm',
            type: 'warning'
          }).then(() => {
            that.doRun(row, false)
          }).catch(() => {
          })
        } else {
          that.$message.error('操作失败')
        }
      }).catch(() => {
      })
    },
    //   停用、启用
    handleTaskFlowStatus (row) {
      let that = this
      if (row.status === 1) {
        this.stopForm.comment = ''
        this.stopForm.row = row
        this.$nextTick(() => {
          if (this.$refs.stopForm) {
            this.$refs.stopForm.clearValidate()
          }
        })
        this.stopForm.show = true
      } else {
        this.$confirm('是否确定启用此流程？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          showClose: false,
          customClass: 'pal-confirm',
          type: 'warning'
        }).then(() => {
          that.updateTaskFlowStatus(row)
        }).catch(() => {
        })
      }
    },
    updateTaskFlowStatus (row) {
      let status = row.status === 1 ? 0 : 1
      let comment = row.status === 1 ? this.stopForm.comment : ''
      let flowCodes = row.flowCodes.join(',')
      this.$http({
        url: `/robot/app/client/updateTaskFlowStatus/${row.appCode}/${row.taskCode}/${status}?flowCodes=${flowCodes}&comment=${comment}`,
        method: 'post'
      }).then((data) => {
        if (data.data.code == 200) {
          this.stopForm.show = false
          this.$message.success('操作成功')
          this.getData()
        }
      }).catch(() => {
      })
    },
    confirmStop () {
      this.$refs.stopForm.validate((valid) => {
        if (valid) {
          this.updateTaskFlowStatus(this.stopForm.row)
        }
      })
    },
    //   查记录
    handleExecutionQuery (row) {
      // 传参【客户，应用，账户，主流程】，跳转执行查询页
      this.$emit('handleExecutionQuery', {
        clientId: row.clientId,
        appCode: row.appCode,
        taskCode: row.taskCode,
        flowCodes: row.flowCodes
      }, 'singleRun')
    }
  }
}
</script>
<style lang="less" scoped>
.row-item {
  display: flex;
  margin-bottom: 15px;

  .lab {
    width: 80px;
    text-align: right;
  }

  .sel {
    flex: 1;
    margin-left: 5px;
    word-break: break-all;
  }
}

.flow-list {
  margin-top: 10px;

  .li {
    margin-bottom: 15px;
    display: flex;
    width: 100%;
    height: 32px;
    line-height: 32px;
    align-items: center;
  }
  .li.li-stop{
    background-color: #eeeeee;
  }

  .num {
    width: 35px;
    height: 100%;
    border: 1px solid #dbdbdb;
    border-radius: 4px;
    text-align: center;
  }

  .content {
    flex: 1;
    height: 100%;
    display: flex;
    border: 1px solid #dbdbdb;
    border-radius: 4px;
    padding-left: 10px;
  }

  .type {
    font-weight: bold;
    width: 80px;
  }

  .count {
    width: 100px;
  }

  .noDeclaredCount {
    width: 80px;
  }

  .execPlant {
    width: 230px;
    margin-right: 10px;
    .execPlant-text{
      width: 100%;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
  }

  .opt {
    flex: 1;
    padding-top: 2px;
  }
}
</style>
