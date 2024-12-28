<template>
  <div>
    <!--批量下载凭证-->
    <el-dialog title="批量下载凭证" :visible.sync="show" class="cust-dialog" width="600px"
               :close-on-click-modal="false">
      <div style="padding: 0 10px 20px 10px;">
        <div class="text-center mb-20">
          <i class="el-icon-warning font-60 text-orange" style="font-size: 46px"></i>
        </div>
        <div class="row">
          <span class="sel">请确定是否下载当前条件下的凭证？</span>
        </div>
        <div class="row">
          <span class="lab">参保城市：</span>
          <span class="sel">{{addrNames}}</span>
        </div>
        <div class="row">
          <span class="lab required-pre">凭证类型：</span>
          <div class="sel">
<!--            1：数据源文件，2：完税证明，3：费用明细-->
            <el-checkbox-group v-model="fileTypes">
              <el-checkbox label="3">费用明细</el-checkbox>
              <el-checkbox label="1">数据源文件</el-checkbox>
              <el-checkbox label="2">完税证明</el-checkbox>
            </el-checkbox-group>
          </div>
        </div>

        <div class="text-center mt-50">
          <el-button size="small" class="mr-15 w-80" @click="show = false">取消</el-button>
          <el-button size="small" class="w-80" type="primary" @click="comfirmDownload()">确认下载</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  components: {},
  props: [],
  data () {
    return {
      show: false,
      addrNames: '',
      ids: '',
      fileTypes: ['1', '2', '3']
    }
  },
  computed: {},
  created () {
  },
  mounted () {
  },
  methods: {
    init (selectList) {
      this.fileTypes = ['1', '2', '3']
      let addrNameArr = []
      let idArr = []
      selectList.map(item => {
        if (addrNameArr.indexOf(item.addrName) === -1) {
          addrNameArr.push(item.addrName)
        }
        idArr.push(item.id)
      })
      this.addrNames = addrNameArr.join('，')
      this.ids = idArr.join(',')
      this.show = true
    },
    // 确认下载
    comfirmDownload () {
      let self = this
      if (this.fileTypes.length === 0) {
        this.$message.warning('请先选择凭证类型')
        return
      }
      this.$message.success('正在下载')
      let query = '?fileTypes=' + this.fileTypes.join(',') + '&ids=' + this.ids
      this.$downloadFile(`/agent/customer/payFee/batchDownload` + query, 'post', {},this.$global.ZIP, null, function () {
        self.$message.close()
      })
      this.show = false
    },
    showLoading (msg) {
      this.loading = this.$loading({
        target: document.body,
        lock: true,
        text: msg || '请稍候...',
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
.row {
  display: flex;
  margin-bottom: 20px;
  .sel {
    flex: 1;
  }
}

</style>
