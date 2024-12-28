<template>
  <!-- 报盘文件-新增 -->
  <div class="spl-container">
    <breadcrumb :data="pathData"></breadcrumb>
    <div class="pl-20 pr-20">
      <el-form
        :model="formData"
        :rules="rules"
        ref="headerFormData"
        label-width="130px"
        class="demo-ruleForm"
      >
        <el-row style="padding-top: 20px">
          <el-col :span="8">
            <div class="display-flex">
              <el-form-item prop="addrName" label="参保城市：">
                <!-- <span class="ml-20 label">参保城市：</span> -->
                <addrSelector
                  v-model="formData.addrName"
                  :addrArr="allAddr"
                  style="line-height: 0px"
                  @changeAddrSelect="changeAddrSelect"
                ></addrSelector>
                <!-- <el-button size="small" type="primary" class="ml-20 w-60" @click="getSocialTableData">查询</el-button> -->
              </el-form-item>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="display-flex" style="align-items: center">
              <el-form-item prop="bussinssType" label="业务类型：">
                <el-radio-group
                  v-model="formData.bussinssType"
                  @change="changeBussinssType"
                >
                  <el-radio label="1"><span>社保</span></el-radio>
                  <el-radio label="2"><span>公积金</span></el-radio>
                </el-radio-group>
              </el-form-item>
            </div>
          </el-col>
          <el-col :span="9">
            <div class="display-flex" style="align-items: center">
              <!-- <span class="ml-20 label">申报类型：</span> -->
              <el-form-item prop="changeType" label="申报类型：">
                <el-radio-group
                  v-model="formData.changeType"
                  @change="changeTypeRadioChange"
                >
                  <el-radio label="1"><span>增员</span></el-radio>
                  <el-radio label="2"><span>减员</span></el-radio>
                  <el-radio label="3"><span>调基</span></el-radio>
                  <el-radio label="5"><span>补缴</span></el-radio>
                </el-radio-group>
              </el-form-item>
            </div>
          </el-col>

          <el-col :span="8" v-if="formData.changeType == 5">
            <div class="display-flex" style="align-items: center">
              <!-- <span class="ml-20 label"> 补缴月份显示：</span> -->
              <el-form-item prop="fyRuleType" label="补缴月份显示：">
                <el-select v-model="formData.fyRuleType" placeholder="请选择">
                  <el-option
                    v-for="item in selectOption"
                    :key="item.value"
                    :label="item.name"
                    :value="item.value"
                  >
                  </el-option>
                </el-select>
              </el-form-item>
            </div>
          </el-col>
          <el-col :span="8" v-show="formData.bussinssType=='2'">
            <div class="display-flex" style="align-items: center">
              <el-form-item prop="showType" label="数据显示：">
                <template slot="label">
                  <span style="padding-right: 10px;">数据显示：</span>
                </template>
                <el-checkbox v-model="formData.showType" :true-label="1" :false-label="0">按险种分行显示</el-checkbox>
              </el-form-item>
            </div>
          </el-col>
        </el-row>
      </el-form>
      <div class="table-box">
        <div class="table-header">模板设置</div>
        <div class="table-content" :style="{ height: 479 + 'px' }">
          <div class="table-content-list" @click="customMode">
            <!-- <i class="icon-add"></i> -->
            <span class="icon-add">+</span>
            <div class="table-content-text">
              <i class="icon-file"></i>导入自定义模板
            </div>
            <div
              class="table-content-tips"
              style="font-size: 12px; color: #666"
            >
              <p>
                1、如需参考请点击<a
                  href="static/自定义报盘模板.xlsx"
                  download="自定义报盘模板.xlsx"
                  >下载模板范例</a
                >
              </p>
              <p>2、导入前请确保页面上必填字段已填</p>
            </div>
          </div>
          <div class="table-content-list" @click="importMode">
            <i class="icon-document"></i>
            <div class="table-content-text">
              <i class="icon-file"></i>引入地区模板
            </div>
            <div
              class="table-content-tips"
              style="font-size: 12px; color: #666"
            >
              <p>1、通常用于地区参保字段类似的情况</p>
              <p>2、引入前请确保页面上必填字段已填</p>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- 上传文件 -->
    <el-dialog
      title="导入自定义模板"
      :visible.sync="dialogVisible"
      width="500px"
      :close-on-click-modal="false"
      :before-close="custonHandleClose"
    >
      <span slot="title" class="dialog-footer">
        <div>导入自定义模板</div>
      </span>
      <div id="upload">
        <div class="file-content-box">
          <el-form
            :model="uploadFileData"
            :rules="rules"
            ref="custonMode"
            label-width="100px"
            class="demo-ruleForm"
          >
            <div class="upload-file-box">
              <div style="display: flex">
                <el-form-item prop="uploadFileName" label-width="0">
                  <div style="display: flex; align-item: center">
                    <input
                      type="text"
                      v-model="uploadFileData.uploadFileName"
                    />
                    <div class="upload-file">
                      <input
                        type="file"
                        style="height: 30px"
                        @change="custonModechangeFile"
                        ref="uploadFile"
                        accept=".xls,.xlsx,.XLS,.XLSX"
                      />选择文件
                    </div>
                  </div>
                </el-form-item>
                <div class="upload-file-btn" @click="custonModeUpload">
                  上传
                </div>
              </div>
              <!-- <div style="color:#F56C6C;font-size:12px;margin-top:-20px;">{{uploadErrMsg}}</div> -->
            </div>
          </el-form>
          <div class="upload-tips">
            <p>备注：请上传以.xlsx为后缀名的表格文件</p>
          </div>
          <div class="error" v-if="uploadErrMsg.length > 0">
            <div>
              <i class="el-icon-error" style="color: red"></i> 出现错误：
            </div>
            <div>{{ uploadErrMsg }}</div>
          </div>
        </div>
      </div>
    </el-dialog>
    <!-- 引入地区模板 -->
    <el-dialog
      title="引入地区模板"
      :visible.sync="dialogVisible2"
      width="500px"
      :close-on-click-modal="false"
      :before-close="handleCloseImportMode"
    >
      <span slot="title" class="dialog-footer">
        <div>引入地区模板</div>
      </span>
      <div class="file-content-box">
        <el-form
          :model="formData"
          :rules="rules"
          ref="importMode"
          label-width="100px"
          class="demo-ruleForm"
        >
          <div class="upload-file-box">
            <div style="display: flex">
              <el-form-item prop="quoteAddrName" label="选择源地区">
                <addrSelector
                  style="line-height: 0px"
                  v-model="formData.quoteAddrName"
                  :addrArr="allAddr"
                  @changeAddrSelect="changeImportAddr"
                ></addrSelector>
              </el-form-item>
              <div style="line-height: 40px">
                <div class="upload-file-btn" @click="confirmImportMode">
                  引入
                </div>
              </div>
            </div>
            <div class="error" v-if="uploadErrMsg.length > 0">
              <div>
                <i class="el-icon-error" style="color: red"></i> 出现错误：
              </div>
              <div>{{ uploadErrMsg }}</div>
            </div>
          </div>
        </el-form>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import dTable from '../../components/common/table'
import addrSelector from '../../components/common/addrSelector'
import { MessageBox } from 'element-ui'
export default {
  components: { addrSelector, dTable },
  data() {
    return {
      pathData: [],
      formData: {
        bussinssType: '', //1-社保或2-公积金
        addrId: '', //城市id
        quoteAddrId: '', //源地区
        quoteAddrName: '',
        addrName: '', //城市名字
        bussinssTypeName: '', //社保或公积金名字
        changeType: '', //申报类型
        changeTypeName: '', //申报类型名字
        columnSettings: [], //解析 excel 的字段集合
        fyRuleType: '', //费用年月显示规则
        showType: '', //数据显示
        status: '1', //有效无效
      },
      dialogVisible: false,
      dialogVisible2: false,
      allAddr: [],
      uploadFileData: {
        uploadFileName: '',
        fileID: '',
        fileData: null,
      },
      selectOption: [
        { name: '请选择', value: '' },
        { name: '合并连续月显示', value: '1' },
        { name: '月份单条显示', value: '2' },
      ],
      rules: {
        uploadFileName: [
          { required: true, message: '请先上传文件', trigger: 'change' },
        ],
        addrName: [
          { required: true, message: '请选择参保城市', trigger: 'change' },
        ],
        bussinssType: [
          { required: true, message: '请选择业务类型', trigger: 'change' },
        ],
        changeType: [
          { required: true, message: '请选择申报类型', trigger: 'change' },
        ],
        fyRuleType: [
          { required: true, message: '请选择补缴月份', trigger: 'change' },
        ],
        quoteAddrName: [
          { required: true, message: '请选择源地区', trigger: 'change' },
          {
            required: true,
            message: '不可选择同一个地区引入',
            trigger: 'change',
            validator: this.checkRepeatAddr,
          },
        ],
      },
      headerHeight: 0,
      uploadErrMsg: '',
      loading: null,
    }
  },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 330 - this.headerHeight + 'px'
    },
  },
  created() {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = this.$global.deepcopyArray(
        tabs[this.$route.meta.parentPath]
      )
    }
    this.pathData.push({ name: '新增' })
    this.getAddr(1)
  },
  mounted() {
    this.headerHeight = document.getElementsByClassName('el-row')[0]
      ? document.getElementsByClassName('el-row')[0].clientHeight
      : 0
  },
  methods: {
    //改变社保或公积金
    changeBussinssType(val) {
      var arr = ['社保', '公积金']
      this.formData.bussinssTypeName = arr[val - 1]
      this.formData.showType = 0
    },
    //改变城市
    changeAddrSelect(item) {
      this.formData.addrName = item.name
      this.formData.addrId = item.id
    },
    customMode(event) {
      if (event.target.nodeName == 'A') {
        return
      }
      this.$refs.headerFormData.validate((vaild) => {
        if (vaild) {
          this.formData.quoteAddrId = ''
          this.formData.quoteAddrName = ''
          this.dialogVisible = true
        }
      })
    },
    importMode() {
      this.$refs.headerFormData.validate((vaild) => {
        if (vaild) {
          this.dialogVisible2 = true
        }
      })
    },
    changeTypeRadioChange(val) {
      this.headerHeight = document.getElementsByClassName('el-row')[0]
        ? document.getElementsByClassName('el-row')[0].clientHeight
        : 0
      var arr = ['增员', '减员', '调基', '变更', '补缴']
      this.formData.changeTypeName = arr[val - 1]
    },
    // 获取参保地
    getAddr(type) {
      let that = this
      this.$http({
        url: 'policy/declareAddr/addrList',
        method: 'post',
      }).then(({ data }) => {
        this.allAddr = data.data
      })
    },
    //去新增
    goAdd() {
      this.$router.push({})
    },
    //上传文件导入自定义模板
    custonModeUpload() {
      console.log(this.formData)
      if (this.uploadErrMsg) {
        return
      }
      var timer = null
      this.$refs.custonMode.validate((vaild) => {
        if (vaild) {
          var uuid = ''
          this.showLoading(document.getElementById('upload'))
          this.$http({
            url: '/policy/offerSettings/addOrUpdate',
            method: 'post',
            data: {
              explain:'',
              id:0,
              oldVersions:'',
              versions:'',
              policyDeclareBaseSettingVO:this.formData
            },
            headers: {
              customSuccess: true,
            },
          }).then((res) => {
            if (res.data.code == 200) {
              this.dialogVisible = false
              this.closeLoading()
              this.uploadErrMsg = ''
              uuid = res.data.data.data
              MessageBox.confirm(
                '操作成功，请前往报盘信息维护页面，完善设置',
                '提示',
                {
                  confirmButtonText: '立即前往',
                  cancelButtonText: '取消',
                  showCancelButton: false,
                  center: true,
                  showClose: false,
                  customClass: 'pal-confirm',
                  type: 'success',
                }
              )
                .then(() => {
                  this.$router.push('/offerFile/offerInfo?uuid=' + uuid)
                  clearTimeout(timer)
                })
                .catch(() => {
                  console.log('取消')
                  this.closeLoading()
                })
              timer = setTimeout(() => {
                this.$router.push('/offerFile/offerInfo?uuid=' + uuid)
                MessageBox.close()
              }, 2000)
            } else {
              this.uploadErrMsg = res.data.message
              this.closeLoading()
            }
          })
        }
      })
    },
    //上传文件
    custonModechangeFile(e) {
      console.log(e.target.files)
      var name = e.target.files[0].name.split('.')
      var list = ['xlsx', 'xls', 'XLSX', 'XLS']
      if (list.indexOf(name[1]) <= -1) {
        this.uploadFileData.uploadFileName = e.target.files[0].name
        this.uploadErrMsg = '请选择excel格式文件'
        return
      } else {
        this.uploadErrMsg = ''
      }
      if (e.target.files.length > 0) {
        this.uploadFileData.uploadFileName = e.target.files[0].name
        this.fileData = new FormData()
        this.fileData.append('file', e.target.files[0])
        this.fileData.append('bussinssType',this.formData.bussinssType)
        this.fileData.append('addrId',this.formData.addrId)
        console.log('e.target.files[0]', e.target.files[0])
        this.showLoading(document.getElementById('upload'))
        this.$http({
          url: '/policy/offerSettings/getColumnByFile',
          method: 'post',
          data: this.fileData,
          headers: {
            customSuccess: true,
          },
        })
          .then((res) => {
            if (res.data.code == 200) {
              this.formData.columnSettings = res.data.data
              this.formData.columnSettings.forEach((item) => {
                if (!item.columnConstantSettings) {
                  item.columnConstantSettings = []
                }
                if (!item.columnScopes) {
                  item.columnScopes = []
                }
              })
              this.uploadErrMsg = ''
            } else {
              this.uploadErrMsg = res.data.message
            }
            this.closeLoading()
            this.$refs.uploadFile.value = ''
            this.$refs.custonMode.validateField('uploadFileName')
          })
          .catch((err) => {
            this.closeLoading()
          })
      }
    },
    //关闭上传文件弹窗
    custonHandleClose() {
      this.$refs.custonMode.clearValidate()
      this.uploadFileData.uploadFileName = ''
      this.uploadFileData.fileID = ''
      this.dialogVisible = false
      this.closeLoading()
      this.uploadErrMsg = ''
    },
    //关闭引入地区模板弹窗
    handleCloseImportMode() {
      this.dialogVisible2 = false
      this.formData.quoteAddrId = ''
      this.formData.quoteAddrName = ''
      this.$nextTick(() => {
        this.$refs.importMode.clearValidate()
      })
      this.uploadErrMsg = ''
    },
    //确定引入地区模板弹窗
    confirmImportMode() {
      this.$refs.importMode.validate((vaild) => {
        if (vaild) {
          console.log('地区模板')
          this.showLoading()
          var uuid = ''
          var timer = null
          this.$http({
            url: '/policy/offerSettings/addOrUpdate',
            method: 'post',
            data: {
              explain:'',
              id:"",
              oldVersions:'',
              versions:'',
              policyDeclareBaseSettingVO:this.formData
            },
            headers: {
              customSuccess: true,
            },
          }).then((res) => {
            if (res.data.code == 200) {
              this.dialogVisible2 = false
              this.closeLoading()
              this.uploadErrMsg = ''
              uuid = res.data.data.data
              MessageBox.confirm(
                '操作成功，请前往报盘信息维护页面，完善设置',
                '提示',
                {
                  confirmButtonText: '立即前往',
                  cancelButtonText: '取消',
                  showCancelButton: false,
                  center: true,
                  showClose: false,
                  customClass: 'pal-confirm',
                  type: 'success',
                }
              )
                .then(() => {
                  this.$router.push('/offerFile/offerInfo?uuid=' + uuid)
                  clearTimeout(timer)
                })
                .catch(() => {
                  console.log('取消')
                  this.closeLoading()
                })
              timer = setTimeout(() => {
                this.$router.push('/offerFile/offerInfo?uuid=' + uuid)
                MessageBox.close()
              }, 2000)
            } else {
              this.uploadErrMsg = res.data.message
              this.closeLoading()
            }
          })
        }
      })
    },
    showLoading(target) {
      this.loading = this.$loading({
        target: target ? target : document.body,
        lock: true,
        text: '加载中',
        spinner: 'el-icon-loading',
        background: 'rgba(255, 255, 255, 0.7)',
      })
    },
    closeLoading() {
      if (this.loading && this.loading.close) {
        this.loading.close()
      }
    },
    changeImportAddr(item) {
      this.formData.quoteAddrName = item.name
      this.formData.quoteAddrId = item.id
    },
    checkRepeatAddr(rule, value, callback) {
      if (value == this.formData.addrName) {
        return callback(new Error('不可选择同一个地区引入'))
      }
      return callback()
    },
  },
}
</script>

<style lang="less" scoped>
/deep/ .el-radio__input.is-checked .el-radio__inner::after {
  content: '';
  width: 3px;
  height: 7px;
  border: 1px solid white;
  border-top: transparent;
  border-left: transparent;
  text-align: center;
  display: block;
  position: absolute;
  top: 20%;
  left: 50%;
  vertical-align: middle;
  transform: rotate(45deg) translateX(-50%);
  border-radius: 0px;
  background: none;
}
/deep/ .el-radio__inner {
  border-radius: 2px;
  width: 14px;
  height: 14px;
}

// /deep/.el-form-item__label{
//   line-height:30px;
// }

.table-box {
  border-radius: 5px;
  border: 1px solid #a6d3ed;
  overflow: hidden;
  margin-top: 20px;
  .table-header {
    height: 30px;
    color: #ccecff;
    background: #458ae6;
    line-height: 30px;
    padding: 0 15px;
    font-size: 14px;
  }
  .table-content {
    background: #fff;
    display: flex;
    justify-content: space-around;
    margin-top: 100px;
  }
}
.table-content-list {
  width: 350px;
  height: 240px;
  border: 1px dashed #bbbbbb;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  padding: 10px;
  box-sizing: border-box;
}
.icon-add {
  // display: inline-block;
  // width: 70px;
  // height: 70px;
  // background: url('../../assets/images/seal/add-seal.png') no-repeat;
  // position: absolute;
  // top:25%;
  // left:50%;
  // transform: translateX(-50%);
  position: absolute;
  font-family: 'Arial Negreta', Arial;
  font-weight: 700;
  left: 50%;
  top: 15%;
  font-size: 70px;
  transform: translateX(-50%);
  color: #999;
}
.icon-document {
  position: absolute;
  left: 50%;
  top: 20%;
  -webkit-transform: translateX(-50%);
  transform: translateX(-50%);
  background: url(../../assets/images/add-document.png) no-repeat;
  width: 50px;
  height: 50px;
  background-size: 100%;
}
/*.icon-file{*/
/*display: inline-block;*/
/*width: 15px;*/
/*height: 15px;*/
/*background: url('../../assets/images/icons/ic-account-type30.png') no-repeat;*/
/*margin-right:20px;*/
/*}*/
.table-content-text {
  position: absolute;
  top: 65%;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  align-items: center;
}
.table-content-tips {
  position: absolute;
  bottom: 5%;
  white-space: nowrap;
}

.file-content-box {
  padding: 10px 20px;
  font-size: 12px;
  .file-list {
    display: flex;
    padding: 10px 0;
    .file {
      background: #f1f8ff;
      border-radius: 10px;
      padding: 10px;
      position: relative;
      cursor: pointer;
      margin-right: 15px;
      .file-remove {
        position: absolute;
        right: -10px;
        top: -10px;
        width: 20px;
        height: 20px;
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
  .upload-tips {
    // margin-top:20px;
    font-size: 12px;
    color: #797979;
  }
}
.upload-file-box {
  // margin-top:10px;
  input[type='text'] {
    border: 1px solid #ddd;
    border-width: 1px;
    height: 26px;
    width: 250px;
  }
  .upload-file-btn {
    height: 30px;
    background: #3e82ff;
    color: white;
    padding: 0 10px;
    box-sizing: border-box;
    font-size: 12px;
    cursor: pointer;
    line-height: 30px;
    margin-left: 10px;
    display: inline-block;
  }
  .upload-file {
    padding: 4px 10px;
    height: 30px;
    line-height: 20px;
    position: relative;
    cursor: pointer;
    color: #888;
    background: #fafafa;
    border: 1px solid #ddd;
    display: inline-block;
    box-sizing: border-box;
    input[type='file'] {
      position: absolute;
      font-size: 18px;
      right: 0;
      top: 0;
      opacity: 0;
      filter: alpha(opacity=0);
      cursor: pointer;
      &:hover {
        color: #444;
        background: #eee;
        border-color: #ccc;
        text-decoration: none;
      }
    }
  }
}
.error {
  margin-top: 10px;
  /* position: absolute; */
  left: 0;
  /* top: 120%; */
  color: #f56c6c;
  font-size: 12px;
}
</style>
