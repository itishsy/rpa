<template>
  <div>
    <!--关联客户模板-->
    <el-dialog title="关联客户模板" :visible.sync="show" class="cust-dialog" width="960px"
               :close-on-click-modal="false" @close="doClose">
      <div style="padding: 0 10px 20px 10px;">
        <el-form ref="tempForm" :model="tempForm">
          <p class="font-16 fw-blod mb-15">{{addressName}}-{{businessType == 1 ? '社保':'公积金'}}</p>
          <div class="text-gray">
            <p>1、请导入客户要求的模板，单元格以#字段#标识，如：#身份证号#</p>
            <p>2、#字段#中的字段，请采用本地字段名</p>
          </div>
          <div class="display-flex mt-15">
            <span class="required-pre lh-com">模板类型：</span>
            <el-form-item prop="tplType" :rules="[{ required: true, message: '请选择模板类型', trigger: 'change' }]">
              <el-select v-model="tempForm.tplType" placeholder="请选择" class="w-300" filterable>
                <el-option v-for="item in options.tplType" :key="item.dictCode" :label="item.dictName" :value="item.dictCode"></el-option>
              </el-select>
            </el-form-item>
          </div>
          <div class="display-flex">
            <span class="required-pre lh-com">客户名称：</span>
            <el-form-item prop="custId" :rules="[{ required: true, message: '请选择客户名称', trigger: 'change' }]">
              <el-select v-model="tempForm.custId" placeholder="请选择" class="w-300" filterable>
                <el-option
                  v-for="item in options.customer"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                ></el-option>
              </el-select>
            </el-form-item>
          </div>
          <div class="display-flex">
            <span class="lh-com">导出方式：</span>
            <el-form-item prop="businessType">
              <el-checkbox style="line-height: 32px;" v-model="tempForm.businessType" :true-label="0" :false-label="businessType">社保公积金同表展示</el-checkbox>
            </el-form-item>

          </div>
          <div class="display-flex fileName">
            <el-input v-model="tempForm.fileName" readonly :placeholder="tempForm.filePlaceholder"
                      style="width: 430px;margin-left: 77px;" size="small"></el-input>
            <el-upload
              ref="upload"
              :headers="$global.setUploadHeaders()"
              :action="$global.uploadFileUrl"
              :auto-upload="true"
              :show-file-list="false"
              :multiple="false"
              accept=".xls,.xlsx,.XLS,.XLSX"
              :before-upload="onBeforeUpload"
              :on-success="onSuccessFile"
            >
              <el-button plain class="s-btn" style="color: #3E82FF;">选择文件</el-button>
            </el-upload>
            <el-button type="primary" class="s-btn" style="margin-left: 20px;" @click="comfirmLink">确定关联</el-button>
          </div>

          <div class="text-red mt-20" v-if="tempForm.errorList.length>0">
            <p><i class="el-icon-error font-20 mr-5" style="vertical-align: middle"></i>出现错误：</p>
            <p class="mt-5" v-for="(item, index) in tempForm.errorList" :key="index">{{item}}</p>
          </div>

          <div class="mt-20" style="border-top: 1px dashed #dbdbdb">
            <p class="fw-blod mt-15 mb-15">已关联：</p>
            <div>
              <table class="cust-table-border w-p100">
                <thead>
                <tr>
                  <th style="width: 50px">序号</th>
                  <th>模板类型</th>
                  <th>客户名称</th>
                  <th>导出方式</th>
                  <th>已关联文件</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="(item, index) in tempList" :key="index">
                  <td>{{index+1}}</td>
                  <td>{{item.tplTypeName}}</td>
                  <td>{{item.custName}}</td>
                  <td>{{item.businessTypeName}}</td>
                  <td>
                    <div class="file-list">
                      <div class="file " @click="downLoadFile(item)">
                        {{item.fileName}}
                        <span class="file-remove" @click.stop="removeFile(item)"></span>
                      </div>
                    </div>
                  </td>
                </tr>
                </tbody>
              </table>
              <p class="text-gray mt-20 text-center" v-if="tempList.length===0">暂无数据</p>
            </div>
          </div>
        </el-form>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import JSZip from 'jszip'
import FileSaver from 'file-saver'

export default {
  components: {},
  props: [],
  data () {
    return {
      show: false,
      uuid: '',
      tempForm: {
        tplType: '',
        custId: '',
        fileId: '',
        fileName: '',
        businessType: '',
        filePlaceholder: '请先选择文件',
        errorList: []
      },
      tempList: [],
      options: {
        tplType: [],
        customer: []
      },
      isEdit: false, // 用于关掉弹窗之后，判断是否需要刷新表格
      addrId: '',
      addressName: '',
      businessType: ''
    }
  },
  computed: {},
  created () {
  },
  mounted () {
  },
  methods: {
    init (obj) {
      this.isEdit = false
      this.tempForm = this.$options.data().tempForm
      this.$nextTick(() => {
        this.$refs.tempForm.clearValidate()
      })
      this.uuid = obj.uuid
      this.addrId = obj.addrId
      this.addressName = obj.addressName
      this.businessType = obj.businessType
      this.tempForm.tplType = '10022001' // 默认选中费用类型
      this.tempForm.businessType = obj.businessType
      this.getTplList()
      if (this.options.tplType.length === 0) {
        this.getDictByKey('10022', 'tplType') // 获取模板类型
      }
      if (this.options.customer.length === 0) {
        this.getlistCustomer()
      }
    },
    getTplList () {
      this.$http({
        url: `/policy/paidIn/getTplList/${this.uuid}`,
        method: 'post',
        data: {}
      }).then((data) => {
        if (data.data.code === 200) {
          this.tempList = data.data.data
          this.show = true
        }
      }).catch(() => {
      })
    },
    // 获取客户
    getlistCustomer () {
      this.$http({
        url: '/policy/sys/customer/listAllCustomer?filter=true&status=1',
        method: 'post'
      }).then((data) => {
        this.options.customer = data.data.data
      }).catch((err) => {
      })
    },
    // 获取字典值
    getDictByKey (key, field) {
      this.$http({
        url: `policy/sys/dict/getDictByKey`,
        method: 'get',
        params: {
          dataKey: '10022'
        }
      }).then(res => {
        this.options[field] = res.data.data
      }).catch(() => { })
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
        this.tempForm['fileId'] = item.fileID
        this.tempForm['fileName'] = item.fileName
      } else {
        this.$message.error(response.message ? response.message : '上传失败请重试')
      }
    },
    // 确认关联
    comfirmLink () {
      let that = this
      this.$refs.tempForm.validate((valid) => {
        console.log(valid)
        if (valid) {
          if (!this.tempForm.fileId) {
            return
          }
          that.showLoading()
          let tempForm = that.tempForm
          that.$http({
            url: `policy/paidIn/addTpl`,
            method: 'post',
            data: {
              uuid: that.uuid,
              addrId: that.addrId,
              businessType: tempForm.businessType,
              custId: tempForm.custId,
              fileId: tempForm.fileId,
              tplType: tempForm.tplType
            }
          }).then(data => {
            that.closeLoading()
            if (data.data.code === 200) {
              tempForm.errorList = data.data.data
              if (data.data.data.length === 0) {
                that.$message.success('操作成功')
                that.getTplList()
                tempForm.fileId = ''
                tempForm.fileName = ''
                that.isEdit = true
              }
            }
          }).catch(() => {
            this.closeLoading()
          })
        }
      })
    },
    downLoadFile (item) {
      this.$downloadFile(`/policy/sys/file/download/${item.fileId}`, 'get')
    },
    removeFile (item) {
      this.$confirm('一旦取消关联，不可逆，请谨慎操作', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: `/policy/paidIn/removeTpl/${item.id}`,
          method: 'post'
        }).then(res => {
          this.$message.success('操作成功')
          this.isEdit = true
          this.getTplList()
        })
      }).catch(() => {
      })
    },
    doClose () {
      if (this.isEdit) {
        this.$emit('close')
      }
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
/deep/ .fileName input::input-placeholder {
  color: #f56c6c !important;
}

/deep/ .fileName input::-moz-placeholder {
  color: #f56c6c !important;
}

/deep/ .fileName input::-webkit-input-placeholder {
  color: #f56c6c !important;
}
.file-list{
  .file{
    display: inline-block;
    background: #f1f8ff;
    border-radius: 10px;
    padding: 5px 10px;
    position: relative;
    cursor: pointer;
    margin-right:15px;
    &:hover{
      color: #3e82ff;
      text-decoration: underline;
    }
    .file-remove{
      position: absolute;
      right: -5px;
      top: -10px;
      width: 18px;
      height: 18px;
      background: red;
      border-radius: 50%;
      display: none;
      &::before{
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
    &:hover .file-remove{
      display: block;
    }
  }
}
</style>
