<template>
  <div>
    <!--上传发票-->
    <el-dialog title="上传发票" :visible.sync="show" class="cust-dialog" width="600px" :close-on-click-modal="false">
      <div style="padding: 0 10px 20px 10px;" v-if="rowData">
        <div class="row">
          <span class="lab">实收金额：</span>
          <span class="sel text-red" style="height:32px">{{ rowData.actualAmount || '0.00' }}</span>
        </div>
        <div class="row">
          <span class="lab required">开票方式：</span>
          <span class="sel">
            <el-select v-model="setForm.invoiceStatus" placeholder="请选择" @change="onChangeInvoiceStatus">
              <el-option label="全额开票" :value="1" :disabled="rowData.invoiceStatus == 2"></el-option>
              <el-option label="部分开票" :value="2"></el-option>
            </el-select>
          </span>
        </div>
        <div class="row" v-if="setForm.invoiceStatus == 2">
          <span class="lab">已开票金额：</span>
          <span class="sel">
            <el-input v-model="setForm.kpAmount" placeholder="请输入" style="width:212px" disabled></el-input>
          </span>
        </div>
        <div class="row">
          <span class="lab required">开票金额：</span>
          <span class="sel">
            <el-input v-model.trim="setForm.singleAmount" placeholder="请输入" style="width:212px"
              oninput="if(isNaN(value)) { value = null } if(value.indexOf('.')>0){value=value.slice(0,value.indexOf('.')+3)}"></el-input>
            <p class="text-red" v-if="showError">开票金额已超过总额，请重新确认！</p>
          </span>
        </div>
        <div class="row" v-if="showError">
          <span class="lab required">备注：</span>
          <span class="sel">
            <el-input v-model.trim="setForm.comment" placeholder="请输入" type="textarea" style="width:250px"></el-input>
          </span>
        </div>
        <div class="row mt-20">
          <span class="lab required">发票：</span>
          <span class="lab" style="text-align:left">
            <el-upload ref="upload" :headers="$global.setUploadHeaders()" :action="$global.uploadFileUrl"
              :auto-upload="true" :show-file-list="false" :multiple="false" :before-upload="onBeforeUpload"
              :on-success="onSuccessFile">
              <el-button plain class="s-btn" style="color: #3E82FF;" :disabled="fileList.length == 1">上传发票+</el-button>
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
import { accAdd2 } from '@/utils/socialAccfundProduct'
export default {
  components: {},
  props: [],
  data() {
    return {
      show: false,
      rowData: null,
      fileType: 2,
      fileList: [],
      businessType: '',
      showError: false,    //部分开票时 超过额定金额显示
      setForm: {
        singleAmount: '',
        invoiceStatus: '',      //开票方式
        kpAmount: '',     //开票金额
        comment: ''      //部分开票强制提交时备注
      }
    }
  },
  computed: {},
  created() {
  },
  mounted() {
  },
  watch: {
    'setForm.singleAmount': {
      handler(newValue, oldValue) {
        //如果是部分开票 则匹对 实收金额 = 单次开票金额+已开票金额
        // console.log(newValue, this.setForm, this.rowData.handleStatus !== 1,this.setForm.invoiceStatus == 2,this.rowData,parseFloat(newValue + this.setForm.kpAmount) > parseFloat(this.rowData.actualAmount))
        // console.log('singel', newValue, oldValue)
        if(!newValue && oldValue){
          this.showError = false
          return
        }
        if (this.rowData.handleStatus !== 1 && this.setForm.invoiceStatus == 2 && parseFloat(accAdd2(newValue, this.setForm.kpAmount)) > parseFloat(this.rowData.actualAmount)) {
          this.showError = true
        } else {
          //handleStatus 为1时 代表当前是未到账 不校验直接可以提交
          this.showError = false
        }
      },
      immediate: false,
      deep: false,
    },
  },
  methods: {
    onChangeInvoiceStatus(e) {
      //切换时校验 部分开票 是否合法
      if (this.rowData.handleStatus !== 1 && this.setForm.invoiceStatus == 2 && parseFloat(accAdd2(this.setForm.singleAmount, this.setForm.kpAmount)) > parseFloat(this.rowData.actualAmount)) {
        this.showError = true
      } else {
        //handleStatus 为1时 代表当前是未到账 不校验直接可以提交
        this.showError = false
      }
    },
    init(obj) {
      this.fileList = []
      this.rowData = {
        ...obj,
        actualAmount: obj.actualAmount ? parseFloat(obj.actualAmount).toFixed(2) : 0.00
      }
      this.setForm.invoiceStatus = obj.invoiceStatus || null
      this.setForm.kpAmount = obj.kpAmount || 0.00
      this.setForm.singleAmount = ""
      this.setForm.comment = ""
      this.showError = false
      this.show = true
      console.log('rowData', obj, this.setForm)
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
      if (!this.setForm.invoiceStatus) {
        this.$message.warning('请填写开票方式')
        return
      }
      if (!this.setForm.singleAmount) {
        this.$message.warning('请填写开票金额')
        return
      }
      if (this.showError && this.setForm.comment.length == 0) {
        this.$message.warning('请输入备注')
        return
      }
      if (this.fileList.length === 0) {
        this.$message.warning('请先上传文件')
        return
      }

      // 、若“处理状态”为”未到账“；上传发票无需判断实收金额即可上传。 handleStatus = 1时为“未到账”
      if (this.rowData.handleStatus !== 1) {
        //判断当前是否是全额开票，全额开票需要 开票金额 = 实收金额
        if (this.setForm.invoiceStatus == 1 && Number(this.setForm.singleAmount) != Number(this.rowData.actualAmount)) {
          this.$message.warning('开票金额与实收金额不一致！')
          console.log(this.setForm)
          return
        }
      }
      that.showLoading()
      console.log('parseFloat(accAdd2(this.setForm.kpAmount,this.setForm.singleAmount))', parseFloat(accAdd2(this.setForm.kpAmount, this.setForm.singleAmount)), this.setForm);
      that.$http({
        url: `policy/bill/uploadInvoice`,
        method: 'post',
        data: {
          _id: this.rowData._id,
          actualAmount: this.rowData.actualAmount,
          kpAmount: this.setForm.invoiceStatus == 1 ? parseFloat(this.setForm.singleAmount) : parseFloat(accAdd2(this.setForm.kpAmount, this.setForm.singleAmount)),
          fileList: this.fileList,
          invoiceStatus: this.setForm.invoiceStatus,
          payRemark: this.setForm.comment,
        }
      }).then(data => {
        that.closeLoading()
        if (data.data.code === 200) {
          that.$message.success('操作成功')

          that.showError = false
          that.setForm = {
            singleAmount: '',
            invoiceStatus: '',      //开票方式
            kpAmount: '',     //开票金额
            comment: ''      //部分开票强制提交时备注
          }
          that.$emit('onSuccess', this.rowData)
          that.show = false
        }
      }).catch(() => {
        this.closeLoading()
      })
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
    width: 100px;
    text-align: right;
    line-height: 32px;
  }

  .sel {
    flex: 1;
    line-height: 32px;
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
