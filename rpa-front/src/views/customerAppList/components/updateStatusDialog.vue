<template>
  <div>
    <el-dialog :visible.sync="updateStatusForm.show" width="500px" :close-on-click-modal="false" :title="updateStatusForm.updateStatus===1 ? '启用账户' : '停用账户'" class="cust-dialog">
      <el-form style="margin-top: 10px;padding: 0px 10px 10px 10px;" ref="updateStatusForm" label-width="55px" :model="updateStatusForm">
        <p class="mb-15">{{updateStatusForm.updateStatus===1 ? '是否确定启用账号？' : '停用此账户下的自动任务'}}</p>
        <el-form-item prop="comment" :label="updateStatusForm.updateStatus===1 ? '备注:' : '原因:'" :rules="[{ required: updateStatusForm.updateStatus===0, message: '请输入原因',trigger:'blur' }]">
          <el-input type="textarea" :rows="4" v-model="updateStatusForm.comment" maxlength="500" :placeholder="updateStatusForm.updateStatus===1 ? '可输入启用的理由，选填' : '请输入原因'" style="margin-top: 5px;"></el-input>
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
        row: null,
        updateStatus: null
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
    init (row, updateStatus) {
      this.updateStatusForm.updateStatus = updateStatus
      this.updateStatusForm.comment = ''
      this.updateStatusForm.row = row
      this.$nextTick(() => {
        this.$refs.updateStatusForm.clearValidate()
      })
      this.updateStatusForm.show = true
    },
    updateTaskFlowStatus (row) {
      let status = this.updateStatusForm.updateStatus
      let comment = this.updateStatusForm.comment
      this.$http({
        url: `/robot/app/client/updateTaskStatus/${row.appCode}/${row.taskCode}/${status}?comment=${comment}`,
        method: 'post'
      }).then((data) => {
        if (data.data.code == 200) {
          this.updateStatusForm.show = false
          this.$message.success('操作成功')
          this.$emit('refreshTable')
        }
      }).catch(() => {
      })
    },
    confirmStop () {
      this.$refs.updateStatusForm.validate((valid) => {
        if (valid) {
          this.updateTaskFlowStatus(this.updateStatusForm.row)
        }
      })
    }
  }
}
</script>
<style lang="less" scoped>
</style>
