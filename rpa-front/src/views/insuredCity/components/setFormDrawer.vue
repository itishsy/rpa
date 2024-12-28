<template>
  <div>
    <el-drawer
      title="设置附件字段"
      :visible.sync="setLocalVisible"
      :wrapperClosable="false"
      custom-class="spl-filter-drawer add-drawer"
      :show-close="true"
      :before-close="doClose"
      size="20%"
    >
      <div>
        <div class="drawerForm" style="padding: 15px 20px 20px 20px">
          <el-form
            :rules="rules"
            :label-position="labelPosition"
            label-width="80px"
            :inline="true"
            :model="addCityFileForm"
            ref="addCityFileForm"
          >
            <div style="padding: 10px">
              <el-form-item label="字段名称" prop="fileTypeName">
                <div class="display-flex">
                  <el-select
                    v-model="addCityFileForm.fileTypeName"
                    placeholder="请选择"
                    @change="handleFileTypeName"
                    filterable
                    class="flex1"
                  >
                    <el-option
                      v-for="item in fileNameList"
                      :key="item.dictCode"
                      :label="item.dictName"
                      :value="
                      JSON.stringify({
                        dictName: item.dictName,
                        dictCode: item.dictCode,
                        flag: item.flag
                      })
                    "
                    >
                    </el-option>
                  </el-select>
                  <i class="el-icon-s-tools text-blue font-18 ml-10 mt-10 mr-5 f-cursor" @click="toUpdateFileName"></i>
                </div>
              </el-form-item>
              <el-form-item
                label="注解"
                prop="comment"
                style="margin-top: 10px"
              >
                <el-input
                  placeholder="建议填写，帮助用户识别并上传有效附件内容"
                  v-model="addCityFileForm.comment"
                ></el-input>
              </el-form-item>
            </div>
            <p class="font-s-w">要求</p>
            <div style="padding: 10px">
              <el-form-item label="格式" prop="format">
                <el-select
                  v-model="addCityFileForm.format"
                  placeholder="请选择"
                  class="content-len"
                >
                  <el-option
                    v-for="item in formatList"
                    :key="item.dictCode"
                    :label="item.dictName"
                    :value="item.dictCode"
                  >
                    <!--  :value="
                      JSON.stringify({
                        dictName: item.dictName,
                        dictCode: item.dictCode,
                      })
                    " -->
                  </el-option>
                </el-select>
              </el-form-item>
              <div class="flex-checkbox">
                <el-checkbox class="check-sty" v-model="isbackgroundColor"
                >底色</el-checkbox
                >
                <el-form-item
                  label=""
                  prop="backgroundColor"
                  :rules="
                    isbackgroundColor ? rules.isRequired : [{ required: false }]
                  "
                  v-show="isbackgroundColor"
                >
                  <el-select
                    v-model="addCityFileForm.backgroundColor"
                    placeholder="请选择"
                    class="content-len"
                  >
                    <el-option
                      v-for="item in backgroundColorList"
                      :key="item.dictCode"
                      :label="item.dictName"
                      :value="item.dictCode"
                    >
                    </el-option>
                  </el-select>
                </el-form-item>
              </div>
              <div class="flex-checkbox">
                <el-checkbox class="check-sty" v-model="isSize">
                  大小</el-checkbox
                >
                <div v-show="isSize" class="felx-check-ww">
                  <el-form-item
                    label=""
                    :rules="isSize ? rules.isRequired : [{ required: false }]"
                    prop="minSize"
                  >
                    <el-input
                      @blur="blurMesCheck('min', 'Size')"
                      @input="numberFnBse('minSize')"
                      class="input-leng"
                      placeholder="请输入"
                      v-model="addCityFileForm.minSize"
                    >
                      <span slot="suffix">KB</span>
                    </el-input>
                  </el-form-item>
                  <span class="center-icon">至</span>
                  <el-form-item
                    label=""
                    :rules="isSize ? rules.isRequired : [{ required: false }]"
                    prop="maxSize"
                  >
                    <el-input
                      @blur="blurMesCheck('max', 'Size')"
                      @input="numberFnBse('maxSize')"
                      class="input-leng"
                      placeholder="请输入"
                      v-model="addCityFileForm.maxSize"
                    >
                      <span slot="suffix">KB</span>
                    </el-input>
                  </el-form-item>
                </div>
              </div>
              <div class="flex-checkbox">
                <el-checkbox class="check-sty" v-model="iswidth"
                >宽度</el-checkbox
                >
                <div class="felx-check-ww" v-show="iswidth">
                  <el-form-item
                    label=""
                    :rules="iswidth ? rules.isRequired : [{ required: false }]"
                    prop="minWidth"
                  >
                    <el-input
                      @blur="blurMesCheck('min', 'Width')"
                      @input="numberFnBse('minWidth')"
                      class="input-leng"
                      placeholder="请输入"
                      v-model="addCityFileForm.minWidth"
                    >
                      <span slot="suffix">PX</span>
                    </el-input>
                  </el-form-item>
                  <span class="center-icon">至</span>
                  <el-form-item
                    label=""
                    :rules="iswidth ? rules.isRequired : [{ required: false }]"
                    prop="maxWidth"
                  >
                    <el-input
                      @blur="blurMesCheck('max', 'Width')"
                      @input="numberFnBse('maxWidth')"
                      class="input-leng"
                      placeholder="请输入"
                      v-model="addCityFileForm.maxWidth"
                    >
                      <span slot="suffix">PX</span>
                    </el-input>
                  </el-form-item>
                </div>
              </div>
              <div class="flex-checkbox">
                <el-checkbox class="check-sty" v-model="isHeight">
                  高度</el-checkbox
                >
                <div v-show="isHeight" class="felx-check-ww">
                  <el-form-item
                    :rules="isHeight ? rules.isRequired : [{ required: false }]"
                    label=""
                    prop="minHeight"
                  >
                    <el-input
                      @blur="blurMesCheck('min', 'Height')"
                      @input="numberFnBse('minHeight')"
                      class="input-leng"
                      placeholder="请输入"
                      v-model="addCityFileForm.minHeight"
                    >
                      <span slot="suffix">PX</span>
                    </el-input>
                  </el-form-item>
                  <span class="center-icon">至</span>
                  <el-form-item
                    :rules="isHeight ? rules.isRequired : [{ required: false }]"
                    label=""
                    prop="maxHeight"
                  >
                    <el-input
                      @blur="blurMesCheck('max', 'Height')"
                      @input="numberFnBse('maxHeight')"
                      class="input-leng"
                      placeholder="请输入"
                      v-model="addCityFileForm.maxHeight"
                    >
                      <span slot="suffix">PX</span>
                    </el-input>
                  </el-form-item>
                </div>
              </div>
              <div class="flex-checkbox">
                <el-checkbox class="check-sty" v-model="isCount"
                >份数</el-checkbox
                >
                <div v-show="isCount" class="felx-check-ww">
                  <el-form-item
                    label=""
                    :rules="isCount ? rules.isRequired : [{ required: false }]"
                    prop="minCount"
                  >
                    <el-input
                      @blur="blurMesCheck('min', 'Count')"
                      oninput="value=value.replace(/[^\d]/g,'')"
                      class="input-leng"
                      placeholder="请输入"
                      v-model="addCityFileForm.minCount"
                    >
                      <span slot="suffix">份</span>
                    </el-input>
                  </el-form-item>
                  <span class="center-icon">至</span>
                  <el-form-item
                    label=""
                    :rules="isCount ? rules.isRequired : [{ required: false }]"
                    prop="maxCount"
                  >
                    <el-input
                      @blur="blurMesCheck('max', 'Count')"
                      oninput="value=value.replace(/[^\d]/g,'')"
                      class="input-leng"
                      placeholder="请输入"
                      v-model="addCityFileForm.maxCount"
                    >
                      <span slot="suffix">份</span>
                    </el-input>
                  </el-form-item>
                </div>
              </div>
            </div>
            <p class="font-s-w">适用 <span class="text-gray ml-10" style="font-weight: normal;">请根据实际情况正确勾选</span></p>
            <div style="padding: 15px 23px">
              <div class="felx-kl">
                <p class="mr-10 text-right" style="width: 65px;">社保：</p>
                <el-checkbox
                  class="check-sty"
                  v-model="addCityFileForm.socialAdd"
                  :disabled="socialDisabled"
                >增员</el-checkbox
                >
                <el-checkbox
                  class="check-sty"
                  v-model="addCityFileForm.socialSuppment"
                  :disabled="socialDisabled"
                >补缴</el-checkbox
                >
                <el-checkbox
                  class="check-sty"
                  v-model="addCityFileForm.socialStop"
                  :disabled="socialDisabled"
                >减员</el-checkbox
                >
                <el-checkbox
                  class="check-sty"
                  v-model="addCityFileForm.socialAdjust"
                  :disabled="socialDisabled"
                >调基</el-checkbox
                >
              </div>
              <div class="felx-kl">
                <p class="mr-10 text-right" style="width: 65px;">公积金：</p>
                <el-checkbox
                  class="check-sty"
                  v-model="addCityFileForm.accfundAdd"
                  :disabled="accfundDisabled"
                >增员</el-checkbox
                >
                <el-checkbox
                  class="check-sty"
                  v-model="addCityFileForm.accfundSuppment"
                  :disabled="accfundDisabled"
                >补缴</el-checkbox
                >
                <el-checkbox
                  class="check-sty"
                  v-model="addCityFileForm.accfundStop"
                  :disabled="accfundDisabled"
                >减员</el-checkbox
                >
                <el-checkbox
                  class="check-sty"
                  v-model="addCityFileForm.accfundAdjust"
                  :disabled="accfundDisabled"
                >调基</el-checkbox
                >
              </div>
            </div>
            <div class="felx-bet">
              <p class="font-s-w">示例</p>
              <p
                class="hovePoin"
                @click="showUpload1"
                v-show="fileList && fileList.length < 1"
              >
                +上传
              </p>
            </div>
            <div class="felx-bet-uploa">
              <el-form-item>
                <el-upload
                  ref="upload1"
                  :headers="myHeaders"
                  :action="UploadUrl()"
                  :on-remove="handleRemove"
                  :before-remove="beforeRemove"
                  :on-change="onChange"
                  :file-list="fileList"
                  :auto-upload="false"
                  :show-file-list="false"
                >
                </el-upload>
                <div
                  v-for="(item, inIndex) in fileList"
                  :key="inIndex"
                  class="byselfList"
                >
                  <li tabindex="0" class="el-upload-list__item is-ready">
                    <a
                      class="el-upload-list__item-name"
                      @click="handlePreview(item)"
                    >
                      <i class="el-icon-document"></i>
                      {{ item.name }}
                    </a>
                    <label class="el-upload-list__item-status-label">
                      <i
                        class="el-icon-upload-success el-icon-circle-check"
                      ></i>
                    </label>
                    <i class="el-icon-close" @click="handledel(item)"></i>
                    <i class="el-icon-close-tip"></i>
                  </li>
                </div>
              </el-form-item>
            </div>
          </el-form>
        </div>
        <div style="margin-bottom: 100px"></div>
        <div class="drawer-footer-buts">
          <el-button class="btn--border-blue s-btn mr-0" @click="doClose"
          >取消</el-button
          >
          <el-button
            type="primary"
            class="s-btn ml-12"
            :loading="addCityFileForm.btnLoading"
            @click="handleValid"
          >确定</el-button
          >
        </div>
      </div>
    </el-drawer>
  </div>
</template>
<script>
export default {
  name: 'setFormDrawer',
  components: { },
  props: {
    isOption: {
      type: String,
      default: ''
    },
    newAddCityFileForm: {
      type: String,
      default: ''
    },
    singleListRow: {
      type: Object,
      default: () => {}
    },
    visible: {
      type: Boolean,
      default: false
    }
  },
  computed: {
    setLocalVisible: {
      get () {
        if (this.visible) {
          this.getDictsByBusinessIndustry()
        }
        return this.visible
      },
      set (val) {
        this.$emit('update:visible', val)
      }
    },
    socialDisabled () {
      // 1：社保公积金都适用，0：只适用其中某一个类型
      let item = this.addCityFileForm
      let socialCheck = item.socialAdd || item.socialSuppment || item.socialStop || item.socialAdjust
      let accfundCheck = item.accfundAdd || item.accfundSuppment || item.accfundStop || item.accfundAdjust
      if (this.fileTypeFlag === 0 && socialCheck && accfundCheck) {
        return false
      }
      return this.fileTypeFlag === 0 && accfundCheck
    },
    accfundDisabled () {
      // 1：社保公积金都适用，0：只适用其中某一个类型
      let item = this.addCityFileForm
      let socialCheck = item.socialAdd || item.socialSuppment || item.socialStop || item.socialAdjust
      let accfundCheck = item.accfundAdd || item.accfundSuppment || item.accfundStop || item.accfundAdjust
      if (this.fileTypeFlag === 0 && socialCheck && accfundCheck) {
        return false
      }
      return this.fileTypeFlag === 0 && socialCheck
    }
  },
  data () {
    return {
      isbackgroundColor: false,
      isSize: false,
      iswidth: false,
      isHeight: false,
      isCount: false,
      formatList: [
        { dictName: '文件类型不限', dictCode: 1 },
        { dictName: '图片类型不限', dictCode: 2 },
        { dictName: 'JPG图片', dictCode: 3 }
      ],
      backgroundColorList: [
        { dictName: '白色', dictCode: 1 },
        { dictName: '蓝色', dictCode: 2 },
        { dictName: '红色', dictCode: 3 }
      ],
      fileUrList: [],
      fileUrListIds: [],
      fileList: [],
      myHeaders: { Authorization: this.$store.state.token },
      labelPosition: 'right',
      fileNameList: [],
      addCityFileForm: {
        fileTypeName: undefined, // 字段名称
        fileType: undefined,
        comment: undefined,
        format: undefined, // 格式
        backgroundColor: undefined,
        minSize: undefined,
        maxSize: undefined,
        minWidth: undefined,
        maxWidth: undefined,
        minHeight: undefined,
        maxHeight: undefined,
        minCount: undefined,
        maxCount: undefined,
        socialAdd: false,
        socialSuppment: false,
        socialStop: false,
        socialAdjust: false,
        accfundAdd: false,
        accfundSuppment: false,
        accfundStop: false,
        accfundAdjust: false,
        exampleFileId: undefined
      },
      rules: {
        fileTypeName: [{ required: true, message: '请选择', trigger: 'change' }],
        format: [{ required: true, message: '请选择', trigger: 'change' }],
        backgroundColor: [
          { required: true, message: '请选择', trigger: 'change' }
        ],
        minSize: [
          { required: true, message: '请输入', trigger: 'change' },
          { validator: this.checekMinSize, trigger: 'change' }
        ],
        maxSize: [
          { required: true, message: '请输入', trigger: 'change' },
          { validator: this.checekMinSize, trigger: 'change' }
        ],
        minWidth: [
          { required: true, message: '请输入', trigger: 'change' },
          { validator: this.checekMinSize, trigger: 'change' }
        ],
        maxWidth: [
          { required: true, message: '请输入', trigger: 'change' },
          { validator: this.checekMinSize, trigger: 'change' }
        ],
        minHeight: [
          { required: true, message: '请输入', trigger: 'change' },
          { validator: this.checekMinSize, trigger: 'change' }
        ],
        maxHeight: [
          { required: true, message: '请输入', trigger: 'change' },
          { validator: this.checekMinSize, trigger: 'change' }
        ],
        minCount: [
          { required: true, message: '请输入', trigger: 'change' },
          { validator: this.checekMinSize, trigger: 'change' }
        ],
        maxCount: [
          { required: true, message: '请输入', trigger: 'change' },
          { validator: this.checekMinSize, trigger: 'change' }
        ]
      },
      fileTypeFlag: null, // 字段的适用限制，1：社保公积金都适用，0：只适用其中某一个类型
      updateFileNameVisible: false
    }
  },
  created () {},
  mounted () {
    this.getDictsByBusinessIndustry()
  },
  watch: {
    newAddCityFileForm: {
      handler (val) {
        if (val) {
          console.log('监听！', JSON.parse(val))
          if (this.isOption == 'edit') {
            this.echaData(val)
          }
        }
      },
      deep: true,
      immediate: true
    }
  },
  methods: {
    checekMinSize (rule, value, callback) {
      let minOrMax = rule.field.substring(0, 3)
      let type = rule.field.substring(3, rule.field.length)
      let typeName = ''
      if (type == 'Width') {
        typeName = '宽度'
      } else if (type == 'Size') {
        typeName = '大小'
      } else if (type == 'Height') {
        typeName = '高度'
      } else if (type == 'Count') {
        typeName = '份数'
      }
      if (minOrMax == 'min') {
        var p = `max${type}`
        if (!value || !this.addCityFileForm[p]) {
          this.$refs.addCityFileForm.clearValidate(`max${type}`)
          return callback()
        } else if (Number(value) > Number(this.addCityFileForm[p])) {
          return callback(
            new Error(`文件最小${typeName}不能大于文件最大${typeName}`)
          )
        } else {
          this.$refs.addCityFileForm.clearValidate(`max${type}`)
          callback()
        }
      } else if (minOrMax == 'max') {
        var p = `min${type}`
        if (!value || !this.addCityFileForm[p]) {
          this.$refs.addCityFileForm.clearValidate(`min${type}`)
          return callback()
        } else if (Number(value) < Number(this.addCityFileForm[p])) {
          callback(new Error(`文件最大${typeName}不能小于文件最小${typeName}`))
        } else {
          this.$refs.addCityFileForm.clearValidate(`min${type}`)
          callback()
        }
      }
    },
    handlePreview (item) {
      console.log('FILE', item)
      this.$http({
        url: `api/policy/sys/file/download/${item.id}`,
        responseType: 'blob',
        baseURL: ''
      }).then((res) => {
        console.log('es.status', res)
        if (res.status === 200) {
          const blob = new Blob([res.data])
          const a = document.createElement('a')
          const URL = window.URL || window.webkitURL
          const herf = URL.createObjectURL(blob)
          a.href = herf
          a.download = item.name
          document.body.appendChild(a)
          a.click()
          document.body.removeChild(a)
          window.URL.revokeObjectURL(herf)
        }
      })
    },
    blurMesCheck (val, val2) {
      // let typeName = ''
      // if (val2 == 'Width') {
      //   typeName = '宽度'
      // } else if (val2 == 'Size') {
      //   typeName = '大小'
      // } else if (val2 == 'Height') {
      //   typeName = '高度'
      // } else if (val2 == 'Count') {
      //   typeName = '份数'
      // }
      // Number(this.addCityFileForm['max' + val2]) >=
      // Number(this.addCityFileForm['min' + val2])
      //   ? ''
      //   : this.$message.error(`文件最小${typeName}不能大于文件最大${typeName}`)
    },
    echaData (val) {
      if (val !== '{}') {
        this.addCityFileForm = JSON.parse(val)
        this.addCityFileForm.socialAdd =
          this.addCityFileForm.socialAdd != 0
        this.addCityFileForm.socialSuppment =
          this.addCityFileForm.socialSuppment != 0
        this.addCityFileForm.socialStop =
          this.addCityFileForm.socialStop != 0
        this.addCityFileForm.socialAdjust =
          this.addCityFileForm.socialAdjust != 0
        this.addCityFileForm.accfundAdd =
          this.addCityFileForm.accfundAdd != 0
        this.addCityFileForm.accfundSuppment =
          this.addCityFileForm.accfundSuppment != 0
        this.addCityFileForm.accfundStop =
          this.addCityFileForm.accfundStop != 0
        this.addCityFileForm.accfundAdjust =
          this.addCityFileForm.accfundAdjust != 0
        if (JSON.parse(val).exampleFileId) {
          let obj = {
            name: JSON.parse(val).clientFileName,
            exampleFileId: JSON.parse(val).exampleFileId
          }
          this.fileList.push(obj)
          this.fileUrListIds.push(JSON.parse(val).exampleFileId)
        }

        this.isbackgroundColor = !!this.addCityFileForm.backgroundColor
        this.isSize = !!this.addCityFileForm.minSize
        this.iswidth = !!this.addCityFileForm.minWidth
        this.isHeight = !!this.addCityFileForm.minHeight
        this.isCount = !!this.addCityFileForm.minCount
      } else {
        this.fileList = []
        this.fileUrListIds = []
      }
      for (let i = 0; i < this.fileNameList.length; i++) {
        if (this.fileNameList[i].dictCode === this.addCityFileForm.fileType) {
          this.fileTypeFlag = this.fileNameList[i].flag
          break
        }
      }
    },
    numberFnBse (p) {
      this.addCityFileForm[p] =
        this.addCityFileForm[p]
          .replace(/[^\d^\.]+/g, '')
          .replace(/^0+(\d)/, '$1')
          .replace(/^\./, '0.')
          .match(/^\d*(\.?\d{0,2})/g)[0] || ''
    },
    handleFileTypeName (val) {
      if (val) {
        this.addCityFileForm.fileType = JSON.parse(val).dictCode
        this.addCityFileForm.fileTypeName = JSON.parse(val).dictName
        this.fileTypeFlag = JSON.parse(val).flag

        let item = this.addCityFileForm
        if (this.fileTypeFlag === 0 && (item.accfundAdd || item.accfundSuppment || item.accfundStop || item.accfundAdjust) && (item.socialAdd || item.socialSuppment || item.socialStop || item.socialAdjust)) {
          item.accfundAdd = false
          item.accfundSuppment = false
          item.accfundStop = false
          item.accfundAdjust = false
          item.socialAdd = false
          item.socialSuppment = false
          item.socialStop = false
          item.socialAdjust = false
        }
      }
    },
    getDictsByBusinessIndustry () {
      this.$http({
        url: '/policy/sys/dict/getDictByKey',
        method: 'get',
        params: {
          dataKey: '10014'
        }
      }).then(({ data }) => {
        this.fileNameList = data.data
      })
    },
    onChange (file, fileList) {
      // 判断是否超过30M 超过就删掉当前的文件
      if (file.size / (1024 * 1024) > 30) {
        fileList.forEach((val, index) => {
          if (file.uid == val.uid) {
            this.fileUrListIds.splice(index, 1)
            this.fileList.splice(index, 1)
          }
        })
        return this.$message.warning('文件大小不能超过30M')
      }

      this.fileList = fileList
      console.log('fileList', this.fileList)
      let formdata = new FormData()
      const cc = file.raw
      formdata.append('file', cc)
      this.$http
        .post('/policy/sys/file/fileUpload', formdata, {
          headers: {
            'Content-Type': 'multipart/form-data',
            customSuccess: true
          }
        })
        .then(({ data }) => {
          this.fileUrList.push(data.data[0])
          this.fileUrListIds.push(data.data[0].fileID)
          console.log('fileUrListIds', this.fileUrListIds)
        })
    },
    handledel (item) {
      console.log('item,index,inIndex', item)
      this.$confirm(`确定移除${item.name}？`, {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.fileList = []
        this.fileUrListIds = []
      })
    },
    // /sys/file/upload
    // 删除文件
    handleRemove (file, fileList) {
      console.log('file', file)
      this.fileList = []
      this.fileUrListIds = []
      // this.fileList = fileList
      // this.fileUrList.forEach((val, index) => {
      //   if (file.name == val.fileName) {
      //     // this.fileUrList.splice(index, 1)
      //     this.fileUrListIds.splice(index, 1)
      //   }
      // })
      // console.log('fileUrListIds', this.fileUrListIds)
    },
    // 移除前的校验
    beforeRemove (file, fileList) {
      return this.$confirm(`确定移除 ${file.name}？`)
    },
    UploadUrl () {
      const baseUrl = window.location.origin
      return baseUrl + '111'
    },
    showUpload1 () {
      this.$refs['upload1'].$refs['upload-inner'].handleClick()
    },
    doClose () {
      this.setLocalVisible = false
      this.fileTypeFlag = null
      this.handleNullFrom()
      this.$emit('makeNull-form')
    },
    handleNullFrom () {
      this.addCityFileForm = {
        fileTypeName: undefined, // 字段名称
        fileType: undefined,
        comment: undefined,
        format: undefined, // 格式
        backgroundColor: undefined,
        minSize: undefined,
        maxSize: undefined,
        minWidth: undefined,
        maxWidth: undefined,
        minHeight: undefined,
        maxHeight: undefined,
        minCount: undefined,
        maxCount: undefined,
        socialAdd: false,
        socialSuppment: false,
        socialStop: false,
        socialAdjust: false,
        accfundAdd: false,
        accfundSuppment: false,
        accfundStop: false,
        accfundAdjust: false,
        exampleFileId: undefined
      }
      this.$refs.addCityFileForm.resetFields()
      this.fileUrListIds = []
      this.fileList = []
      this.isbackgroundColor = false
      this.isSize = false
      this.iswidth = false
      this.isHeight = false
      this.isCount = false
      this.fileTypeFlag = null
      console.log(' this.fileList', this.fileList)
      this.$forceUpdate()
    },
    handleData () {
      let params = {}
      Object.assign(params, {
        fileTypeName: this.addCityFileForm.fileTypeName, // 字段名称 fileType
        fileType: this.addCityFileForm.fileType, // 字段名称
        comment: this.addCityFileForm.comment,
        format: this.addCityFileForm.format, // 格式
        backgroundColor: this.isbackgroundColor ? this.addCityFileForm.backgroundColor : null,
        minSize: this.isSize ? this.addCityFileForm.minSize : null,
        maxSize: this.isSize ? this.addCityFileForm.maxSize : null,
        minWidth: this.iswidth ? this.addCityFileForm.minWidth : null,
        maxWidth: this.iswidth ? this.addCityFileForm.maxWidth : null,
        minHeight: this.isHeight ? this.addCityFileForm.minHeight : null,
        maxHeight: this.isHeight ? this.addCityFileForm.maxHeight : null,
        minCount: this.isCount ? this.addCityFileForm.minCount : null,
        maxCount: this.isCount ? this.addCityFileForm.maxCount : null,
        socialAdd: this.addCityFileForm.socialAdd == false ? 0 : 1,
        socialSuppment: this.addCityFileForm.socialSuppment == false ? 0 : 1,
        socialStop: this.addCityFileForm.socialStop == false ? 0 : 1,
        socialAdjust: this.addCityFileForm.socialAdjust == false ? 0 : 1,
        accfundAdd: this.addCityFileForm.accfundAdd == false ? 0 : 1,
        accfundSuppment: this.addCityFileForm.accfundSuppment == false ? 0 : 1,
        accfundStop: this.addCityFileForm.accfundStop == false ? 0 : 1,
        accfundAdjust: this.addCityFileForm.accfundAdjust == false ? 0 : 1,
        exampleFileId:
          (this.fileUrListIds && this.fileUrListIds.join(',')) || '',
        addrId: this.singleListRow.addrId,
        addrName: this.singleListRow.addrName == null ? this.singleListRow.cityName : this.singleListRow.addrName,
        status: 1
      })
      if (this.isOption == 'edit') {
        params.id = this.addCityFileForm.id
      }
      return params
    },
    // 验证，确定提交保存
    handleValid () {
      let params = this.handleData()
      console.log('params', params)
      // return
      this.$refs.addCityFileForm.validate((valid) => {
        if (valid) {
          this.$http({
            url: 'policy/declareAddr/saveOrUpdateFilestoreRule',
            method: 'post',
            data: params
          })
            .then(({ data }) => {
              this.setLocalVisible = false
              this.handleNullFrom()
              this.$emit('refresh-list', this.singleListRow) // 刷新列表
            })
            .catch(() => {
              this.$message.error(data.message)
            })
        } else {
          return false
        }
      })
    },
    toUpdateFileName () {
      this.setLocalVisible = false
      this.$emit('toUpdateFileNameDrawer')
    }
  }
}
</script>
<style lang="less" scoped>
.drawerForm {
  .el-form-item {
    margin-bottom: 4px !important;
    .el-form-item__label {
      // width: 80px;
      margin-bottom: 10px;
    }
    /deep/ .el-form-item__content {
      width: 82% !important;
      position: relative;
    }
    /deep/ .el-form-item__error {
      position: absolute;
      bottom: 0px;
      left: 0px;
      // float: left !important;
    }
  }
  /deep/ .content-len {
    width: 180px !important;
  }
}
.font-s-w {
  font-weight: 600;
}
.flex-checkbox {
  display: flex;
  justify-content: start;
  align-items: center;
  height: 50px;
  margin-bottom: 10px;
  /deep/ .el-form-item__label {
    width: 50px !important;
    margin-bottom: 10px;
  }
  .check-sty {
    margin-left: 19px;
    margin-right: 10px;
  }
  .felx-min-max {
    display: flex;
    justify-content: start;
    .min-max {
      position: relative;
      /deep/ .unit {
        // border: 1px solid red;
        position: absolute !important;
        right: 11px !important;
        // display: block;

        // padding: 0 2px;
      }
    }
    .maxin {
      margin: 0 10px;
    }
  }
  /deep/ .el-input-group__append {
    background: none !important;
    border-left: none !important;
  }
}
.felx-kl {
  display: flex;
  margin: 10px 0px;
}
.felx-bet {
  display: flex;
  justify-content: space-between;
}
.hovePoin {
  cursor: pointer;
  color: #5080f7;
}
.felx-check-ww {
  display: flex;
  justify-content: start;
  align-items: center;
  /deep/ .el-form-item {
    margin-right: -33px !important;
  }
  .center-icon {
    margin: 10px;
  }
}
.center-icon {
}
.felx-bet-uploa {
  /deep/.el-form-item__content {
    line-height: 6px;
  }
}
</style>
