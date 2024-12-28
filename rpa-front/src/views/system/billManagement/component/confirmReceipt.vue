<template>
  <div>
    <!--确认收款-->
    <el-dialog title="确认收款" :visible.sync="show" class="cust-dialog" width="600px" :close-on-click-modal="false">
      <div style="padding: 0 10px 20px 10px;" v-if="rowData">
        <span class="font-18">该收款费用是否成功到账？</span>
        <div class="row mt-10">
          <span class="lab">收款单金额：</span>
          <span class="sel text-red" style="height:32px">{{ rowData.payCount }}</span>
        </div>
        <div class="row">
          <span class="lab required">实收金额：</span>
          <span class="sel">
              <el-input v-model.trim="setForm.amount" placeholder="请输入" style="width:250px" @change="onChangeAmount" oninput="if(isNaN(value)) { value = null } if(value.indexOf('.')>0){value=value.slice(0,value.indexOf('.')+3)}"></el-input>
              <p class="text-red" v-if="showError">实收金额与收款单金额不一致！是否确认收款？</p>
          </span>
        </div>
        <div class="row" v-if="showError">
          <span class="lab required">备注：</span>
          <span class="sel">
            <el-input v-model.trim="setForm.comment" placeholder="请输入" type="textarea"  style="width:250px"></el-input>
          </span>
        </div>
        <div class="row mt-20">
          <span class="lab">
            <el-upload ref="upload" :headers="$global.setUploadHeaders()" :action="$global.uploadFileUrl"
              :auto-upload="true" :show-file-list="false" :multiple="false" :before-upload="onBeforeUpload"
              :on-success="onSuccessFile">
              <el-button plain class="s-btn" style="color: #3E82FF;" >到款回单+</el-button>
            </el-upload>
          </span>
          <div class="sel">
            <div class="file-list">
              <div class="file" @click="downLoadFile(item)" v-for="(item, index) in fileList" :key="item.fileId">
                {{ item.fileName }}
                <span class="file-remove" @click.stop="removeFile(index)"></span>
              </div>
            </div>
          </div>
        </div>

        <div class="text-center mt-30">
          <el-button size="small" class="mr-15 w-80" @click="show = false">取消</el-button>
          <el-button size="small" class="w-80" type="primary" @click="comfirmUpload()">确定</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  components: {},
  props: [],
  data() {
    return {
      showError:false,    //当输入实收金额与收款单金额不一致时弹出，然后需要输入备注
      show: false,
      rowData: null,
      fileType: 2,
      fileList: [],
      businessType: '',
      setForm: {
        amount: '' ,    //开票金额
        comment:''      //备注
      }
    }
  },
  computed: {},
  created() {
  },
  mounted() {
  },
  methods: {
    init(obj) {
      this.fileList = []
      this.rowData = {
        ...obj,
        payCount:obj.payCount?parseFloat(obj.payCount).toFixed(2):0.00
      }
      this.show = true
      this.showError = false
      this.setForm.amount = null
      this.setForm.comment = ""
    },
    // 上传文件之前
    onBeforeUpload(file) {
      this.showLoading('正在上传...')
    },
    // 上传文件成功之后
    onSuccessFile(response) {
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
    comfirmUpload() {
      let that = this
      if(this.setForm.amount == "" || !this.setForm.amount){
        this.$message.warning('请填写实收金额')
        return
      }
      if(this.showError && this.setForm.comment.length == 0){
        this.$message.warning('请输入备注')
        return
      }
      // if (this.fileList.length === 0) {
      //   this.$message.warning('请先上传文件')
      //   return
      // }
      // console.log(this.setForm)
      // return
      that.showLoading()
      that.$http({
        url: `policy/bill/receiptHandle`,
        method: 'post',
        data:{
          _id:this.rowData._id,
          actualAmount:this.setForm.amount,
          payRemark:this.setForm.comment,
          fileList:this.fileList
        } 
      }).then(data => {
        that.closeLoading()
        if (data.data.code === 200) {
          that.$message.success('操作成功')
          that.show = false
          that.$emit('onSuccess',this.rowData)
        }
      }).catch(() => {
        this.closeLoading()
      })
    },
    onChangeAmount(e){
      console.log(e,parseFloat(e) != this.rowData.payCount)
      this.showError = parseFloat(e) != this.rowData.payCount
    },
    downLoadFile(item) {
      this.$downloadFile(`/policy/sys/file/download/${item.fileId}`, 'get')
    },
    removeFile(index) {
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
    showLoading(msg) {
      this.loading = this.$loading({
        target: document.body,
        lock: true,
        text: msg || '请稍候...',
        spinner: 'el-icon-loading',
        background: 'rgba(255, 255, 255, 0.7)'
      })
    },
    closeLoading() {
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
    line-height:32px;
  }

  .sel {
    flex: 1;
    line-height:32px;
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
/deep/.el-checkbox__input.is-disabled+.el-checkbox__label {
  color: #ffffff !important;
}

/deep/.el-checkbox__input.is-disabled.is-checked+.el-checkbox__label {
  color: @blueColor !important;
}

/deep/.el-checkbox__input.is-disabled.is-checked .el-checkbox__inner {
  background-color: @blueColor !important;
  border-color: @blueColor !important;
}
</style>
