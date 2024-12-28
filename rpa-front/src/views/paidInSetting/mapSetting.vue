<template>
  <div class="spl-container">
    <breadcrumb :data="pathData">
      <el-button type="primary" slot="breadcrumb-btn" size="mini" @click="checkCityProduct"
        :disabled="disabled">获取网站字段</el-button>
      <el-button type="primary" slot="breadcrumb-btn" size="mini" @click="save" :disabled="disabled">确定保存</el-button>
    </breadcrumb>
    <el-form ref="tableForm" :model="table" label-width="0px" :rules="rules">
      <div class="pl-20 pr-20 pt-10">
        <el-row>
          <el-col :span="table.businessType == 1?6:8">
            <div class="search-row display-flex" v-if="!edit">
              <span class="ml-20 label required-pre">参保城市：</span>
              <el-form-item prop="addrName">
                <addrSelector v-model="table.addressName" :addrArr="allAddr" @changeAddrSelect="changeCity"
                  :disabled="request"></addrSelector>
              </el-form-item>
            </div>
            <div v-else class="search-row display-flex" style="align-items:center;">
              <span class="ml-20 label">参保城市：</span>
              {{ table.addressName }}
            </div>
          </el-col>
          <el-col :span="table.businessType == 1?6:8">
            <div class="search-row display-flex" v-if="!edit">
              <span class="ml-20 label required-pre">业务类型：</span>
              <el-form-item prop="businessType">
                <el-radio-group v-model="table.businessType" @change="changeBusinessType" :disabled="request">
                  <el-radio :label="1">社保</el-radio>
                  <el-radio :label="2">公积金</el-radio>
                </el-radio-group>
              </el-form-item>
            </div>
            <div v-else class="search-row display-flex" style="align-items:center;">
              <span class="ml-20 label">业务类型：</span>
              {{ table.businessType == 1 ? '社保' : '公积金' }}
            </div>
          </el-col>
          <el-col :span="6" v-if="table.businessType == 1">
            <div class="search-row display-flex" style="align-items:center;">
              <span class="ml-20 label">参保险种：</span>
              {{itemText}}
            </div>
          </el-col>
          <el-col :span="table.businessType == 1?6:8">
            <div class="search-row display-flex" style="justify-content: flex-end;">
              <div class="display-flex" style="flex-direction: column;">
                <el-button size="mini" plain @click="autoSort" style="width: 100px;align-self: flex-end;">自动排序</el-button>
                <p class="tips" v-if="editText.length > 0">编辑：{{ editText }}</p>
              </div>
            </div>
          </el-col>
          <!-- <el-col :span="8">
            <div class="search-row display-flex" style="align-items: center;">
              <span class="ml-20 label">实缴模板：</span>
              <el-upload
                class="upload-demo"
                action="api/policy/paidIn/readExcel"
                :headers="this.$global.setUploadHeaders()"
                :before-upload="beforeUpload"
                :on-success="handleSuccess"
                :show-file-list="false"
                :on-error="uploadError"
              >
                <span style="font-size:12px;color:#606266;line-height:32px;margin-right:10px;" v-if="table.fileData.name">{{this.table.fileData.name}}</span>
                <span style="font-size:12px;color:#3E82FF;line-height:32px;">上传</span>
              </el-upload>
            </div>
          </el-col>-->
        </el-row>
      </div>
      <div class="table-box">
        <!-- <el-button
          type="primary"
          slot="breadcrumb-btn"
          size="mini"
          @click="add({}, 0)"
          style="margin-bottom: 10px; float: right"
          v-if="table.tableData.length <= 0"
        >添加</el-button>-->
        <el-table :data="table.tableData" border header-cell-class-name="table-header" style="width: 100%;"
          :max-height="tableHeight" ref="table">
          <el-table-column label="序号" width="65" align="center" prop="date">
            <template scope="scope">
              <span>{{ scope.$index + 1 }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="prefixName" label="前缀" width="120"></el-table-column>
          <el-table-column prop="webPaidInField" label="网站实缴字段">
            <template scope="scope">
              <div>{{ scope.row.webPaidInField }}</div>
            </template>
          </el-table-column>
          <el-table-column prop="localPaidInField" label="本地实缴字段">
            <template scope="scope">
              <div style="display:flex;align-items:center;">
                <el-form-item :prop="'tableData.' + scope.$index + '.localPaidInField'" :rules="rules.localPaidInField">
                  <el-select v-model="scope.row.localPaidInField" placeholder="请选择" size="mini"
                    @change="changelocalPaidInField($event, scope.$index)" filterable clearable
                    :disabled="scope.row.isDelete?true:false">
                    <el-option v-for="item in options.local" :key="item.fieldCode" :label="item.fieldName"
                      :value="item.fieldCode"></el-option>
                  </el-select>
                </el-form-item>
                <i class="el-icon-edit local-edit"
                  v-if="scope.row.localPaidInField == '10001078' || scope.row.localPaidInField == '10001079'"
                  @click="localPaidInFieldEdit($event, scope)"></i>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="date" label="是否显示" width="120">
            <template scope="scope">
              <div v-if="scope.row.isDelete == 0">
                <el-switch v-model="scope.row.requireShow" active-color="#13ce66" inactive-color="#e4e4e4"
                  :active-value="1" :inactive-value="2" @change="changeRequireShow($event, scope.$index)"></el-switch>
              </div>
              <div v-else style="color:#969696">已删</div>
            </template>
          </el-table-column>
          <el-table-column prop="control" label="操作">
            <template scope="scope">
              <div style="display: flex">
                <el-popover placement="top" ref="popper" v-model="scope.row.sortShow" trigger="manual">
                  <div style="display:flex">
                    <div style="display: inline-block;width: 120px;" class="sortForm">
                      <el-input v-model.trim="sortForm.sortNumber" placeholder="请输入新序号"
                        style="display: block;"></el-input>
                      <p v-if="sortForm.error" style="font-size:12px;color:#FD6154">{{ sortForm.error }}</p>
                    </div>
                    <div>
                      <el-button type="text" style="margin-left:5px;padding:8px 5px"
                        @click="cancelSort(scope.row)">取消</el-button>
                      <el-button type="primary" size="mini" @click="confirmSort(scope.row, scope.$index)">确定</el-button>
                    </div>
                  </div>
                  <div class="control-btn" slot="reference" v-if="scope.row.isDelete == 0"
                    @click.stop="showSort(scope.row, scope.$index)">排序</div>
                </el-popover>
                <div class="control-btn red" v-if="scope.row.isDelete == 0" @click="remove(scope.row, scope.$index)">移除
                </div>
                <!-- <div class="control-btn color-6b6b6b" @click="moveDown(scope.row, scope.$index)"
                  v-if="scope.$index != table.tableData.length - 1">下移</div>
                <div class="control-btn color-6b6b6b" @click="moveUp(scope.row, scope.$index)" v-if="scope.$index != 0">上挪
                </div> -->
                <!-- <div
                  class="control-btn"
                  style="color: #3e82ff"
                  @click="add(scope.row, scope.$index)"
                >添加</div> -->
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-form>
    <el-dialog :title="dialogTile" :visible.sync="dialogVisible" width="600px" :close-on-click-modal="false">
      <el-form ref="localPaidInFieldEdit" :model="table">
        <el-table :data="table.dialogTabel" border header-cell-class-name="table-header" style="width: 100%;"
          :max-height="tableHeight" ref="table">
          <el-table-column prop="webPaidInField" label="网站字段值">
            <template scope="scope">
              <div style="display: flex; align-items: center">
                <el-form-item label :prop="'dialogTabel.' + scope.$index + '.webItemFieldName'"
                  :rules="rules.webPaidInField" style="width: 100%">
                  <div>
                    <el-input v-model.trim="scope.row.webItemFieldName" placeholder="请输入网站字段值"></el-input>
                  </div>
                </el-form-item>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="localPaidInField" label="本地字段值">
            <template scope="scope">
              <div style="display: flex; align-items: center">
                <el-form-item label :prop="'dialogTabel.' + scope.$index + '.localItemFieldName'"
                  :rules="rules.localPaidInField" style="width: 100%">
                  <div>
                    <el-select v-model="scope.row.localItemFieldName" placeholder="请选择" size="mini"
                      @change="changeDialogLocalPaidInField($event, scope.$index)" filterable clearable>
                      <el-option v-for="item in options[optionsKey]" :key="item.dictCode" :label="item.dictName"
                        :value="item.dictName"></el-option>
                    </el-select>
                  </div>
                </el-form-item>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="control" label="操作" width="120">
            <template scope="scope">
              <div style="display: flex">
                <div class="control-btn red" v-if="table.dialogTabel.length > 1" @click="removeDialogTable(scope.$index)">
                  移除</div>
                <!-- <div
                  class="control-btn color-6b6b6b"
                  @click="moveDown(scope.row, scope.$index)"
                  v-if="scope.$index != table.tableData.length - 1"
                >下移</div>
                <div
                  class="control-btn color-6b6b6b"
                  @click="moveUp(scope.row, scope.$index)"
                  v-if="scope.$index != 0"
                >上挪</div>-->
                <div class="control-btn" style="color: #3e82ff" @click="addDialogTable(scope.row, scope.$index)">添加</div>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="localPaidInFieldEditCancel" size="small" class="w-60">取 消</el-button>
        <el-button type="primary" @click="localPaidInFieldEditconfirm" size="small" class="w-60">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import addrSelector from '../../components/common/addrSelector'
export default {
  components: { addrSelector },
  data() {
    return {
      dialogTile: '险种类型',
      pathData: [],
      table: {
        addressName: '',
        businessType: 1,
        uuid: null,
        custId: 1,
        uuid: '',
        prefix: '',
        fields: {},
        id: '',
        fileData: {
          name: '',
        },
        tableData: [
          // {
          //   localPaidInField:"",webPaidInField:"",businessType:'',address:""
          // },
        ],
        dialogTabel: [],
      },
      oldBusinessType: '',
      allAddr: [],
      rules: {
        addressName: [
          { required: true, message: '请选择参保城市', trigger: 'change' },
        ],
        businessType: [
          { required: true, message: '请选择业务类型', trigger: 'change' },
        ],
        localPaidInField: [
          { required: false, message: '请选择本地实缴字段', trigger: 'change' },
          { required: false, trigger: 'change', validator: this.checkRepeat1 },
        ],
        webPaidInField: [
          { required: true, message: '请输入网站实缴字段', trigger: 'change' },
          { required: true, trigger: 'change', validator: this.checkRepeat2 },
        ],

      },
      loading: null,
      timer: null,
      edit: false,
      disabled: false,
      dialogVisible: false,
      options: {
        local: [],
        10001078: [],
        10001079: [],
      },
      tableIndex: 0,
      optionsKey: '10001078',
      request: false,
      sortForm: {
        sortNumber: '',
        error: ''
      },
      sortIndex: 0,
      editText: '',
      itemText: ''
    }
  },
  computed: {
    tableHeight() {
      return this.$global.bodyHeight - 200 + 'px'
    },
    tableHeight1() {
      return this.$global.bodyHeight / 2 - 200
    },
    tableHeight2() {
      return this.$global.bodyHeight / 2 - 200
    },
    //业务类型
    bussinssType() {
      return function (val) {
        var arr = ['社保', '公积金']
        if (val != '' || val != null) {
          return arr[val - 1]
        } else {
          return '暂无'
        }
      }
    },

  },
  watch: {
    // 'table.businessType'(newVal, oldVal) {
    //   console.log(
    //     '新:' + newVal,
    //     '旧：' + oldVal,
    //     'cache:' + this.oldBusinessType
    //   )
    //   if (this.oldBusinessType == newVal) {
    //     return
    //   }
    //   if (oldVal != '' && newVal != oldVal) {
    //     this.$confirm('切换条件，下方设置内容将重置，是否继续切换', '提示', {
    //       confirmButtonText: '确定',
    //       cancelButtonText: '取消',
    //       showClose: false,
    //       customClass: 'pal-confirm',
    //       type: 'warning',
    //     })
    //       .then(() => {
    //         this.table.tableData = []
    //         this.table.fileData.name = ''
    //         this.oldBusinessType = newVal
    //       })
    //       .catch(() => {
    //         this.table.businessType = oldVal
    //         this.oldBusinessType = oldVal
    //         return false
    //       })
    //   }
    // },
    'sortForm.sortNumber': function (newVal, oldVal) {
      this.sortForm.error = ''
      if (this.sortForm.sortNumber == '') {
        this.sortForm.error = '请输入正整数序号'
      } else if (isNaN(this.sortForm.sortNumber)) {
        this.sortForm.error = '请输入正整数序号'
      } else if (this.sortForm.sortNumber.indexOf('.') > -1) {
        this.sortForm.error = '请输入正整数序号'
      } else if (this.sortForm.sortNumber <= 0 && this.sortForm.sortNumber != '') {
        this.sortForm.error = '请输入大于0的序号'
      } else if (this.sortForm.sortNumber > this.table.tableData.length) {
        this.sortForm.error = '序号不得大于列表序号最大值'
      }
    }
  },
  created() {

    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = this.$global.deepcopyArray(
        tabs[this.$route.meta.parentPath]
      )
    }
    this.pathData.push({ name: '设置' })
    this.getAddr()
    this.getDictByKey('10003', '10001078')
    this.getDictByKey('10019', '10001079')
    if (this.$route.query.address && this.$route.query.businessType) {
      this.table.address = this.$route.query.address
      this.table.businessType = Number(this.$route.query.businessType)
      this.table.addressName = this.$route.query.addressName
      this.table.uuid = this.$route.query.uuid
      this.requestInfo()
      if (this.table.businessType == 1) {
        this.getAddrItems()
      }
    }
    this.getLocationCode()
    // this.pathData.push({ name: '设置' })
    // this.requestInfo().catch((err) => {
    //   console.log(err)
    // })
  },
  methods: {
    checkSortNumber(val) {
      if (this.sortForm.sortNumber == '') {
        this.sortForm.error = '请输入正整数序号'
      } else if (isNaN(this.sortForm.sortNumber)) {
        this.sortForm.error = '请输入正整数序号'
      } else if (this.sortForm.sortNumber.indexOf('.') > -1) {
        this.sortForm.error = '请输入正整数序号'
      } else if (this.sortForm.sortNumber <= 0 && this.sortForm.sortNumber != '') {
        this.sortForm.error = '请输入大于0的序号'
      } else if (this.sortForm.sortNumber > this.table.tableData.length) {
        this.sortForm.error = '序号不得大于列表序号最大值'
      }
      this.sortForm.error = ''
    },
    changeRequireShow(val, index) {
      if (val == 2) {
        // this.table.tableData.push(...this.table.tableData.splice(index, 1))
      }
    },
    checkRepeat1(rule, value, callback, info) {
      var key = Object.keys(info)[0]
      var key2 = key.split('.')[0]
      var key3 = key.split('.')[key.split('.').length - 1]
      var arr = []
      if (!value) {
        return callback()
      }
      this.table[key2].forEach((item, index) => {
        if (value == item[key3]) {
          arr.push(index)
        }
      })
      if (arr.length >= 2) {
        if (key3 == 'localPaidInField') {
          return callback()//
        } else {
          return callback(new Error('本地字段重复，请修正'))
        }
      }
      return callback()
    },
    checkRepeat2(rule, value, callback) {
      if (!value) {
        return callback()
      }
      return callback()
      var arr = []
      this.table.tableData.forEach((item, index) => {
        if (value == item.webPaidInField) {
          arr.push(index)
        }
      })
      if (arr.length >= 2) {
        return callback(new Error('网站实缴字段重复，请修正'))
      }
      return callback()
    },
    //改变 业务类型，需提示
    changeBusinessType(val, a, b) {
      if (this.table.businessType!=null && this.table.businessType === 1 && this.table.address) {
        this.getAddrItems()
      }
    },
    //改变城市
    changeCity(obj) {
      clearTimeout(this.timer)
      this.timer = setTimeout(() => {
        this.table.address = obj.id
        this.table.addressName = obj.name
        if (this.table.businessType!=null && this.table.businessType === 1) {
          this.getAddrItems()
        }
      }, 100)

    },
    changelocalPaidInField(e, index) {
      console.log(e, this.tableIndex, index)
      this.table.tableData[this.tableIndex].items = []
      if (e == 10001080 || e == 10001081 || e == 10001082 || e == 10001090) {
        this.table.tableData[index].requireShow = 0
      }
      this.$refs.tableForm.validate()
    },
    getAddrItems() {
      let that = this
      this.$http({
        url: '/policy/product/getItemsByAddrId/' + this.table.address,
        method: 'post',
        data: {}
      }).then(async ({ data }) => {
          that.itemText = data.data
      }).catch(() => {
      })
    },
    //编辑回显数据
    async requestInfo() {
      this.showLoading()
      this.$http({
        url: '/policy/paidIn/queryPaidInField',
        method: 'post',
        params: {
          uuid: this.table.uuid
        },
      })
        .then(async ({ data }) => {
          this.closeLoading()
          if (data && data.code == 200) {
            this.table.tableData = data.data.fields.map((item) => {
              this.$set(item, 'sortShow', false)
              return {
                ...item,
                prefix: item.prefix,
                requireShow: item.requireShow,
                items: item.items && item.items.filter(item1 => {
                  return item1.localParentFieldName == item.localPaidFieldName
                }),
                sortShow: false,
                webPaidInField: item.webPaidInField.replace(/\#/g, '').trim(),
              }
            })
            this.edit = true
            this.disabled = false
            //设置编辑人 与 编辑时间
            this.editText = `${data.data.updateName} ${data.data.updateTime}`
          } else {
            this.disabled = true
          }
          this.$nextTick(() => {
            this.initSort()
          })
        })
        .catch(() => {
          this.closeLoading()
          this.disabled = true
        })
    },
    //自动排序
    autoSort() {
      let that = this
      this.$refs.tableForm.validate((vaild) => {
        if (vaild) {
          if (this.table.tableData.length <= 0) {
            this.$message.error('请至少设置一条数据')
            return
          }
          var url = 'policy/paidIn/autoSort/' + this.table.businessType
          var arr = []
          var arr2 = this.options.local.map(item => item.fieldCode)
          let tempNoDelData = this.table.tableData
          arr = tempNoDelData.map((item, ind) => {
            var index = arr2.indexOf(item.localPaidInField)
            return {
              ...item,
              id: item.id,
              localPaidInField: item.localPaidInField,
              webPaidInField: item.webPaidInField,
              businessType: this.table.businessType,
              address: this.table.address,
              sort: ind + 1,
              prefix: item.prefix,
              items: item.items,
              localPaidFieldName: index > -1 ? this.options.local[index].fieldName : '',
              requireShow: item.requireShow
            }
          })
          this.showLoading()
          this.$http({
            url: url,
            method: 'post',
            data: arr,
          })
            .then((res) => {
              this.closeLoading()
              if (!res) {
                return
              }
              if (res.data.code == 200) {
                console.log(res.data)
                this.table.tableData = res.data.data
              }
            })
            .catch((err) => {
              console.log(err)
              this.closeLoading()
            })
        }
      })
    },
    //确定保存
    save() {
      let that = this
      this.$refs.tableForm.validate((vaild) => {
        if (vaild) {
          if (this.table.tableData.length <= 0) {
            this.$message.error('请至少设置一条数据')
            return
          }
          var url = 'policy/paidIn/savePaidInField'
          if (this.edit) {
            url = '/policy/paidIn/editSavePaidInField/' + this.table.uuid
          } else {
            url = 'policy/paidIn/savePaidInField'
          }
          var arr = []
          var arr2 = this.options.local.map(item => item.fieldCode)
          let tempNoDelData = this.table.tableData.filter(item => item.isDelete == 0)
          arr = tempNoDelData.map((item, ind) => {
            var index = arr2.indexOf(item.localPaidInField)
            return {
              id: item.id,
              localPaidInField: item.localPaidInField,
              webPaidInField: item.webPaidInField,
              businessType: this.table.businessType,
              address: this.table.address,
              sort: ind + 1,
              prefix: item.prefix,
              items: item.items,
              localPaidFieldName: index > -1 ? this.options.local[index].fieldName : '',
              requireShow: item.requireShow
            }
          })
          this.showLoading()
          this.$http({
            url: url,
            method: 'post',
            data: arr,
          })
            .then((res) => {
              this.closeLoading()
              if (!res) {
                return
              }
              if (res.data.code == 200) {
                if (!that.edit) {
                  that.table.uuid = res.data.data
                }
                this.$confirm('保存成功', '', {
                  confirmButtonText: '继续设置',
                  cancelButtonText: '返回',
                  center: true,
                  type: 'success',
                })
                  .then(() => {
                    this.requestInfo()
                  })
                  .catch(() => {
                    this.$router.push('/paidInSetting/index')
                  })
              }
            })
            .catch((err) => {
              console.log(err)
              this.closeLoading()
            })
        }
      })
    },
    //操作栏，删除
    remove(row, index) {
      this.$confirm('是否确定移除该字段？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning',
      })
        .then(() => {
          // this.table.tableData.splice(index, 1)
          //不要将其删掉 而是将其的是否显示 变为 已删
          this.table.tableData[index].isDelete = 1
        })
        .catch(() => { })
    },
    //操作栏，上挪
    moveUp(row, index) {
      if (index != 0) {
        this.table.tableData[index] = this.table.tableData.splice(
          index - 1,
          1,
          this.table.tableData[index]
        )[0]
      } else {
        this.table.tableData.push(this.table.tableData.shift())
      }
      this.$nextTick(() => {
        this.$refs.table.doLayout()
      })
    },
    //操作栏，下移
    moveDown(row, index) {
      if (index != this.table.tableData.length - 1) {
        this.table.tableData[index] = this.table.tableData.splice(
          index + 1,
          1,
          this.table.tableData[index]
        )[0]
      } else {
        this.table.tableData.unshift(this.table.tableData.splice(index, 1)[0])
      }
      this.$nextTick(() => {
        this.$refs.table.doLayout()
      })
    },
    //操作栏，添加
    add(row, index) {
      if (index == this.table.tableData.length - 1) {
        this.table.tableData.push({
          localPaidInField: '',
          webPaidInField: '',
          businessType: '',
          address: '',
        })
      } else {
        this.table.tableData.splice(index + 1, 0, {
          localPaidInField: '',
          webPaidInField: '',
          businessType: '',
          address: '',
        })
      }
    },
    //深克隆
    deepCopy(obj) {
      var result = Array.isArray(obj) ? [] : {}
      for (var key in obj) {
        if (Object.prototype.hasOwnProperty.call(obj, key)) {
          if (typeof obj[key] === 'object' && obj[key] !== null) {
            result[key] = this.deepCopy(obj[key]) //递归复制
          } else {
            result[key] = obj[key]
          }
        }
      }
      return result
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
    // 获取参保城市
    getAddr(type) {
      let that = this
      this.$http({
        url: 'policy/declareAddr/addrList',
        method: 'post',
      }).then(({ data }) => {
        this.allAddr = data.data
      })
    },
    //获取本地字段下拉
    getLocationCode() {
      console.log(Number(this.table.businessType))
      this.$http({
        url: '/policy/paidIn/listPaidFieldDict?businessType=' + Number(this.table.businessType),
        method: 'post',
      })
        .then((res) => {
          if (res.data.code == 200) {
            this.options.local = res.data.data
          } else {
            this.$message.error('获取本地实缴字典出错')
          }
          // console.log(this.valueTypeOption2Item)
        })
        .catch((err) => {
          this.$message.error('获取本地实缴字典出错')
        })
    },
    beforeUpload(file) {
      console.log(file)
      var arr = file.name.split('.')
      var type = arr[arr.length - 1]
      var typeList = ['XLSX', 'xlsx', 'XLS', 'xls']
      if (!typeList.includes(type)) {
        this.$message.error('请上传excel格式文件')
        return false
      }
      this.table.fileData.name = file.name
      this.showLoading()
    },
    uploadError(err) {
      console.log(err)
      this.closeLoading()
      this.$message.error('服务出错，请稍后再试')
    },
    handleSuccess(response) {
      console.log(response)
      this.closeLoading()
      if (response.code == 200) {
        var arr = []
        var arr1 = []
        var obj = {}
        arr.push(...this.table.tableData, ...response.data)
        arr.forEach((item) => {
          item.webPaidInField = item.webPaidInField.replace(/\#/g, '').trim()
          if (!obj[item.webPaidInField]) {
            arr1.push({
              ...item,
              webPaidInField: item.webPaidInField.replace(/\#/g, '').trim(),
            })
            obj[item.webPaidInField] = true
          }
        })
        this.table.tableData = arr1
      } else {
        this.$message.error(
          response.message ? response.message : '服务出错，请稍后再试'
        )
      }
    },
    changeWebPaidInField() {
      this.$refs.tableForm.validate()
    },
    changeDialogLocalPaidInField() {
      this.$refs.localPaidInFieldEdit.validate()
    },
    //本地字段，额外编辑
    localPaidInFieldEdit(a, scope) {
      console.log(a, scope)
      this.table.dialogTabel = []
      this.tableIndex = scope.$index
      this.optionsKey = scope.row.localPaidInField
      this.dialogVisible = true
      if (this.optionsKey == '10001078') {
        this.dialogTile = '险种类型'
      } else if (this.optionsKey == '10001079') {
        this.dialogTile = '费用类型'
      }

      this.$nextTick(() => {
        if (
          this.table.tableData[this.tableIndex].items &&
          this.table.tableData[this.tableIndex].items.length > 0
        ) {
          this.table.dialogTabel = this.deepCopy(this.table.tableData[this.tableIndex].items)
        } else {
          this.table.dialogTabel = [
            { webItemFieldName: '', localItemFieldName: '' },
          ]
        }
      })

    },
    //弹出设置
    addDialogTable() {
      this.table.dialogTabel.push({
        webItemFieldName: '',
        localItemFieldName: '',
      })
    },
    //删除弹窗中的
    removeDialogTable(index) {
      this.$confirm('是否确定移除该字段？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning',
      })
        .then(() => {
          this.table.dialogTabel.splice(index, 1)
        })
        .catch(() => { })
    },
    //弹窗取消
    localPaidInFieldEditCancel() {
      this.dialogVisible = false
    },
    //弹窗确定
    localPaidInFieldEditconfirm() {
      this.$refs.localPaidInFieldEdit.validate((valid) => {
        if (valid) {
          this.dialogVisible = false
          this.table.tableData[this.tableIndex].items = this.deepCopy(this.table.dialogTabel)
        }
      })
    },
    //获取网站字段前 要先检验该城市是否设置了参保险种
    async checkCityProduct() {
      if (this.table.businessType != 1) {
        //公积金不需要校验险种
        this.getWebPaidInFields()
        this.getLocationCode()
        return
      }
      const { data: { code, data: { records, rows } } } = await this.$http({
        url: '/policy/product/page',
        method: 'post',
        data: {
          page: 1,
          start: 0,
          size: 20,
          query: [
            { property: 'keyWord', value: this.table.addressName },
            { property: 'status', value: 1 },
          ],
          sidx: '',
          sort: '',
        }
      })
      if (code === 200 && records === 0) {
        console.log('checkCityProduct', code, records, rows)
        //当前城市没有设置参保险种
        this.$message.warning(`${this.table.addressName}社保未添加参保险种，请先添加`)
      } else if (code === 200 && records > 0) {
        this.getWebPaidInFields()
        this.getLocationCode()
      }

    },
    //获取本地字段
    getWebPaidInFields() {
      this.showLoading()
      this.$http({
        url: '/agent/paidIn/getWebPaidInFields',
        method: 'post',
        data: {
          page: 1,
          start: 0,
          size: 20,
          query: [
            {
              property: 'addressName',
              value: this.table.addressName,
            },
            {
              property: 'businessType',
              value: Number(this.table.businessType),
            },
            {
              property: 'flag',
              value: this.edit ? 1 : 0
            }
          ],
          sidx: '',
          sort: '',
        },
      })
        .then((res) => {
          this.closeLoading()
          if (res.data.code == 200) {
            var arr = []
            if (res.data.data.length > 0) {
              this.table.tableData = []
              /* var data = res.data.data.rows
              for (const key in data[0]) {
                this.table[key] = data[0][key]
              }
              data.forEach((item) => {
                for (const key in item.fields) {
                  arr.push({
                    name: key,
                    webPaidInField: key,
                    requireShow: 1,
                    prefixName: item.prefixName,
                    prefix: item.prefix,
                    localPaidInField: '',
                  })
                }
              }) */
              arr = res.data.data
              this.table.tableData.push(...arr)
              this.$message.success('获取成功').doClose(1500)
              this.request = true
            } else {
              this.request = false
              this.$message.warning('未找到该地区网站字段')
            }
          } else {
            this.request = false
            // this.$message.error('获取网站字段出错')
          }
        })
        .catch((err) => {
          console.log(err)
          this.request = false
          this.closeLoading()
          // this.$message.error('获取网站字段出错')
        })
    },
    //获取字典值
    async getDictByKey(dataKey, key) {
      await this.$http({
        url: '/policy/sys/dict/getDictByKey',
        method: 'get',
        params: {
          dataKey: dataKey,
        },
      }).then((res) => {
        this.options[key] = res.data.data
        // console.log(this.valueTypeOption2Item)
      })
    },
    showSort(row, index) {
      this.$set(row,'sortShow',true)
      // row.sortShow = true
      this.sortIndex = index
      this.sortForm.show = true
      this.sortForm.error = ''
      console.log('showSort',row,index)
    },
    cancelSort(row) {
      this.sortForm.sortNumber = ''
      this.sortForm.error = ''
      this.$nextTick(() => {
        row.sortShow = false
      })
    },
    confirmSort(row) {
      if (this.sortForm.sortNumber == '') {
        this.sortForm.error = '请输入正整数序号'
        return
      }
      if (this.sortForm.error) {
        return
      }
      var arr = this.table.tableData.splice(Number(this.sortIndex), 1)
      this.table.tableData.splice(this.sortForm.sortNumber - 1, 0, arr[0])
      this.cancelSort(row)
    },
    initSort() {
      var self = this
      document.addEventListener('click', self.eventListener('click'), true)
      document.addEventListener('scroll', self.eventListener('scroll'))
      document.querySelector('.el-table__body-wrapper').addEventListener('scroll', self.eventListener('scroll'))
    },
    eventListener(type) {
      var self = this
      return function (e) {
        if (type == 'click') {
          // console.log(self.$el,e.target,self.$el.contains(e.target))
          if (!document.querySelector('#app').contains(e.target))
            return;
        }
        self.$nextTick(() => {
          self.sortForm.sortNumber = ''
          self.sortForm.error = ''
          self.table.tableData[self.sortIndex].sortShow = false
        })
      }
    },
    removeEvent() {
      document.removeEventListener('click', this.eventListener)
      document.removeEventListener('scroll', this.eventListener)
      document.querySelector('.el-table__body-wrapper').removeEventListener('scroll', this.eventListener)
    }
  },
  destroyed() {
    this.removeEvent()
  }
}
</script>

<style lang="less" scoped>
.search-row {
  padding: 5px 10px;
}

.table-box {
  padding: 5px 20px;
}

/deep/.table-header {
  background: #f5f5f5 !important;
  color: #444;
}

/deep/.el-table__cell {
  border-bottom: 1px solid #ddd;
  border-right: 1px solid #ddd;
}

/deep/.el-table__row {
  border-bottom: 1px solid #ddd;
  border-right: 1px solid #ddd;
}

/deep/.el-table--border {
  border: 1px solid #ddd;
}

/deep/.el-table .el-table__cell {
  padding: 5px;
}

.control-btn {
  margin-left: 10px;
  cursor: pointer;
}

.red {
  color: red;
}

.color-6b6b6b {
  color: #6b6b6b;
}

.control-btn:not(.red):hover {
  text-decoration: underline;
  color: #3e82ff;
}

.output-content {
  text-align: center;
  width: 100%;
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
  padding: 0 10px;
}

.edit-icon {
  position: absolute;
  top: 50%;
  right: 10px;
  transform: translateY(-50%);
  cursor: pointer;
}

.header-box {
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #fff;
  background: #3e82ff;
}

.gray-btn {
  padding: 5px 10px;
  background: #fff;
  color: #949494;
  border: 1px solid #949494;
  font-size: 12px;
  border-radius: 5px;
  cursor: pointer;
}

.footer-btn {
  position: absolute;
  // bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
}

.block {
  background: rgb(0, 102, 255);
  width: 5px;
  height: 15px;
  display: inline-block;
  margin-right: 3px;
}

// .dialog-footer{
//   // display: flex;
//   // justify-content: center;
//   // margin-top:20px;
//   // text-align: right;
// }
.icon-query {
  position: absolute;
  right: 15px;
  cursor: pointer;
  height: 50%;
  font-size: 16px;
  top: 50%;
  transform: translateY(-50%);
}

.formDrawer-content {
  padding: 15px 30px 30px 30px;
  position: relative;
  min-height: 100%;

  .msg-tip {
    position: absolute;
    bottom: 10px;
    left: 30px;
    color: @redColor;
    background-color: #fff;
    z-index: 5;
  }
}

/deep/.condition-drawer {
  width: 700px !important;
}

/deep/.spl-filter-drawer {
  width: 620px !important;
}

.table-title {
  display: flex;
  align-items: center;

  &::before {
    content: '';
    display: inline-block;
    height: 100%;
    margin-right: 5px;
    width: 5px;
    background: #3e82ff;
  }
}

.mb-0 {
  margin-bottom: 0px !important;
}

.ellipsis {
  cursor: pointer;
  width: 80%;
  -webkit-line-clamp: 3;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 20px;
  -webkit-box-orient: vertical;
  display: -webkit-box;
}

/deep/.el-form-item {
  margin-bottom: 10px;
}

/deep/.el-form-item__content {
  line-height: 32px;
}

/deep/.el-form-item__error {
  position: relative;
}

.local-edit {
  font-size: 16px;
  cursor: pointer;
  margin-left: 10px;
  color: #3e82ff;
}

/deep/.el-dialog__header {
  border-bottom: none !important;
}

.sortForm /deep/.el-form-item {
  margin-bottom: 0;
}

.tips {
  color: #949494;
  padding-top: 10px;
}
</style>
