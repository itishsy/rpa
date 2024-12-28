<template>
  <div>
    <!--上传凭证-->
    <el-dialog title="上传凭证" :visible.sync="show" class="cust-dialog" width="600px" :close-on-click-modal="false">
      <div style="padding: 0 10px 20px 10px;" v-if="rowData">
        <div class="row">
          <span class="lab">参保城市：</span>
          <span class="sel">{{rowData.addrName}}</span>
        </div>
        <div class="row" v-if="businessType===1">
          <span class="lab">申报系统：</span>
          <span class="sel">{{rowData.systemTypeName}}</span>
        </div>
        <div class="row" v-if="businessType===2">
          <span class="lab">公积金类型：</span>
          <span class="sel">{{rowData.systemTypeName}}</span>
        </div>
        <div class="row">
          <span class="lab">费款所属期：</span>
          <span class="sel">{{rowData.periodOfPayment}}</span>
        </div>
        <div class="row">
          <span class="lab">凭证类型：</span>
          <!--1：数据源文件，2：完税证明  目前只有完税证明-->
          <span class="sel"><el-checkbox :true-label="2" v-model="fileType" disabled>完税证明</el-checkbox></span>
        </div>
        <div class="row mt-20">
          <span class="lab">
            <el-upload
              ref="upload"
              :headers="$global.setUploadHeaders()"
              :action="$global.uploadFileUrl"
              :auto-upload="true"
              :show-file-list="false"
              :multiple="false"
              :before-upload="onBeforeUpload"
              :on-success="onSuccessFile"
            >
              <el-button plain class="s-btn" style="color: #3E82FF;">选择文件</el-button>
            </el-upload>
          </span>
          <div class="sel">
            <div class="file-list">
              <div class="file" @click="downLoadFile(item)" v-for="(item, index) in fileList" :key="item.fileId">
                {{item.fileName}}
                <span class="file-remove" @click.stop="removeFile(index)"></span>
              </div>
            </div>
          </div>
        </div>

        <div class="text-center mt-30">
          <el-button size="small" class="mr-15 w-80" @click="show = false">取消</el-button>
          <el-button size="small" class="w-80" type="primary" @click="comfirmUpload()">确认上传</el-button>
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
      rowData: null,
      fileType: 2,
      fileList: [],
      businessType: ''
    }
  },
  computed: {},
  created () {
  },
  mounted () {
  },
  methods: {
    init (type, obj) {
      this.businessType = Number(type)
      this.fileList = []
      this.rowData = obj
      this.show = true
    },
    // 上传文件之前
    onBeforeUpload (file) {
      this.showLoading('正在上传...')
    },
    // 上传文件成功之后
    onSuccessFile (response) {
      this.closeLoading()
      if (response.code === 200) {
        let item = response.data[0]
        this.fileList.push({
          fileId: item.fileID,
          fileName: item.fileName,
          fileType: 2
        })
      } else {
        this.$message.error(response.message ? response.message : '上传失败请重试')
      }
    },
    // 确认上传
    comfirmUpload () {
      let that = this
      if (this.fileList.length === 0) {
        this.$message.warning('请先上传文件')
        return
      }
      that.showLoading()
      that.$http({
        url: `policy/customer/payFee/savePayFeeFiles/${this.rowData.uuid}`,
        method: 'post',
        data: this.fileList
      }).then(data => {
        that.closeLoading()
        if (data.data.code === 200) {
          that.$message.success('操作成功')
          that.show = false
        }
      }).catch(() => {
        this.closeLoading()
      })
    },
    downLoadFile (item) {
      this.$downloadFile(`/policy/sys/file/download/${item.fileId}`, 'get')
    },
    removeFile (index) {
      this.$confirm('是否确定删除？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning'
      }).then(() => {
        this.fileList.splice(index, 1)
      }).catch(() => {
      })
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
  margin-bottom: 15px;

  .lab {
    width: 90px;
    text-align: right;
  }

  .sel {
    flex: 1;
  }
}

.file-list {
  margin-left: 10px;
  .file {
    display: inline-block;
    background: #f1f8ff;
    border-radius: 10px;
    padding: 5px 10px;
    position: relative;
    cursor: pointer;
    margin-right: 15px;
    margin-bottom: 10px;

    &:hover {
      color: #3e82ff;
      text-decoration: underline;
    }

    .file-remove {
      position: absolute;
      right: -5px;
      top: -10px;
      width: 18px;
      height: 18px;
      background: red;
      border-radius: 50%;
      display: none;

      &::before {
        content: '';
        display: inline-block;
        width: 10px;
        height: 2px;
        background: white;
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
      }
    }

    &:hover .file-remove {
      display: block;
    }
  }
}
//checkBox自定义禁用样式
/deep/.el-checkbox__input.is-disabled + .el-checkbox__label {
  color: #ffffff !important;
}

/deep/.el-checkbox__input.is-disabled.is-checked + .el-checkbox__label {
  color: @blueColor !important;
}

/deep/.el-checkbox__input.is-disabled.is-checked .el-checkbox__inner {
  background-color: @blueColor !important;
  border-color: @blueColor !important;
}

</style>
