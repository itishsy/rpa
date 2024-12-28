<template>
  <div>
    <el-dialog :visible.sync="updateStatusForm.show" width="500px" :close-on-click-modal="false"
               :title="updateStatusForm.updateStatus===1 ? '批量启用账户' : '批量停用账户'" class="cust-dialog">
      <el-form style="padding: 0px 10px 10px 10px;" ref="updateStatusForm" label-width="90px" :model="updateStatusForm">
        <div class="mb-5" style="background-color: #fff6f6;color: #ff804f;padding: 6px 10px 8px 10px;">
          <p v-if="updateStatusForm.updateStatus===1">一且确定，所选的已停用账户都将启用</p>
          <div v-else>
            <p>①一旦确定，所选账户都将停用</p>
            <p class="mt-5">②执行中的账户，将在执行结束后才停用</p>
          </div>
        </div>
        <el-form-item prop="comment" label="申报账户数:" style="margin-bottom: 10px;">
          <p>{{updateStatusForm.countText}}</p>
        </el-form-item>
        <el-form-item v-if="updateStatusForm.updateStatus===0" prop="comment" label="停用原因:"
                      :rules="[{ required: updateStatusForm.updateStatus===0, message: '请输入原因',trigger:'blur' }]">
          <el-input type="textarea" :rows="4" v-model="updateStatusForm.comment" maxlength="500"
                    placeholder="请输入原因" style="margin-top: 5px;"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button class="s-btn" @click="updateStatusForm.show = false">取消</el-button>
        <el-button type="primary" class="s-btn" @click="confirmStop()">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
export default {
  props: {},
  data () {
    return {
      updateStatusForm: {
        show: false,
        comment: '',
        taskCodes: null,
        updateStatus: null,
        countText: ''
      }
    }
  },
  watch: {},
  created () {
  },
  mounted () {
  },
  methods: {
    //   停用、启用
    init (selections, updateStatus) {
      let taskCodes = []
      let stopCount = 0
      let startCount = 0
      let runCount = 0
      selections.map(item => {
        if (updateStatus === 1) {
          //   启用
          if (item.taskStatus === '已停用') {
            stopCount++
            taskCodes.push(item.taskCode)
          }
        } else {
          // 停用
          taskCodes.push(item.taskCode)
          if (item.taskStatus === '正在执行') {
            runCount++
          } else if (item.taskStatus === '已启用') {
            startCount++
          }
        }
      })
      if (updateStatus === 1 && stopCount === 0) {
        this.$message.warning('选中的数据都为启用状态，无需操作')
        return
      }
      this.updateStatusForm.countText = updateStatus === 1 ? (stopCount + '个已停用') : (startCount + '个未运行，' + runCount + '个运行中')
      this.updateStatusForm.taskCodes = taskCodes
      this.updateStatusForm.updateStatus = updateStatus
      this.updateStatusForm.comment = ''
      this.$nextTick(() => {
        this.$refs.updateStatusForm.clearValidate()
      })
      this.updateStatusForm.show = true
    },
    updateTaskFlowStatus () {
      this.$global.showLoading('请稍候...')
      let status = this.updateStatusForm.updateStatus
      let comment = this.updateStatusForm.comment
      let taskCodes = this.updateStatusForm.taskCodes
      this.$http({
        url: `/robot/app/client/batchEnableOrStop`,
        method: 'post',
        data: {
          taskCodes: taskCodes,
          status: status,
          comment: comment
        }
      }).then((data) => {
        this.$global.closeLoading()
        if (data.data.code == 200) {
          this.updateStatusForm.show = false
          this.$message.success('操作成功')
          this.$emit('refreshTable')
        }
      }).catch(() => {
        this.$global.closeLoading()
      })
    },
    confirmStop () {
      this.$refs.updateStatusForm.validate((valid) => {
        if (valid) {
          this.updateTaskFlowStatus()
        }
      })
    }
  }
}
</script>
<style lang="less" scoped>
</style>
