<template>
  <div>
    <!--设置规则-->
    <el-drawer title="设置规则" :visible.sync="show" :wrapperClosable="false"
               custom-class="spl-filter-drawer detail-drawer"
               :show-close="true" size="100%">
      <div class="drawer-body" ref="drawerBody" style="padding: 20px;margin: 0;" v-if="offerInfo && show">
        <el-form ref="setForm" :model="setForm" label-width="0px" :key="ruleKey">
          <div class="item-row">
            <span class="required label mr-10">当前字段:</span>
            <el-form-item prop="uuid" :rules="[{ required: true, message: '请选择当前字段', trigger: 'change' }]">
              <el-select v-model="setForm.uuid" placeholder="请选择" filterable class="w-250" :disabled="isEdit"
                         @change="changeCurDeclareColumn">
                <el-option v-for="item in offerInfo.columnSettings" :key="item.uuid" :label="item.declareColumnName"
                           :value="item.uuid"></el-option>
              </el-select>
            </el-form-item>
          </div>
          <div v-if="setForm.uuid">
            <div v-for="(item, index) in setForm.rules" :key="index">
              <div class="item-row">
                <el-tooltip v-show="setForm.uuid" effect="dark" content="移除校验规则" placement="top">
                  <i class="el-icon-remove font-20 text-red label f-cursor mr-5" @click="delRule(index)"></i>
                </el-tooltip>
                <span class="label mr-10">校验{{ index + 1 }}</span>
                <el-form-item :prop="'rules.'+index+'.comment'"
                              :rules="[{ required: true, message: '请输入规则描述', trigger: 'blur' }]" class="flex1">
                  <el-input v-model.trim="item.comment"
                            placeholder="规则描述必填，如：开始日期>结束日期，校验失败，提示：日期大小不合法"
                            clearable></el-input>
                </el-form-item>
                <i class="el-icon-circle-plus font-20 text-blue label f-cursor ml-10" @click="addRule(index+1)"></i>
              </div>
              <div class="pl-30 pr-30">
                <p class="fw-blod mt-5">如果</p>
                <div class="pl-10 mt-10" :key="conditionKey">
                  <el-form-item :prop="'rules.'+index+'.relation'"
                                :rules="[{ required: true, message: '请选择条件关系', trigger: 'change' }]">
                    <el-select v-model="item.relation" placeholder="条件关系" filterable class="w-150">
                      <el-option v-for="item in options.relation" :key="item.code" :label="item.name"
                                 :value="item.code"></el-option>
                    </el-select>
                  </el-form-item>
                  <div class="item-row" v-for="(row, rowIndex) in item.ruleConditionDetails" :key="rowIndex">
                    <i class="el-icon-delete f-cursor text-red label mr-5 font-18"
                       @click="delCondition(index, rowIndex)"></i>
                    <span class="label mr-10">条件{{ rowIndex + 1 }}:</span>
                    <el-form-item :prop="'rules.'+index+'.ruleConditionDetails.'+rowIndex+'.declareColumnName'"
                                  :rules="[{ required: true, message: '请选择', trigger: 'change' }]">
                      <el-select v-model="row.declareColumnName" placeholder="条件字段" filterable class="w-150"
                                 @change="changeDeclareColumnName(index, rowIndex)">
                        <el-option v-for="item in offerInfo.columnSettings" :key="item.uuid" v-if="item.declareColumnName!==curDeclareColumnName"
                                   :label="item.declareColumnName" :value="item.declareColumnName"></el-option>
                      </el-select>
                    </el-form-item>
                    <el-form-item :prop="'rules.'+index+'.ruleConditionDetails.'+rowIndex+'.valueType'" class="ml-10"
                                  :rules="[{ required: true, message: '请选择', trigger: 'change' }]">
                      <el-select v-model="row.valueType" placeholder="属性" filterable class="w-120">
                        <el-option v-for="item in handleValueTypeOptions(index, rowIndex)" :key="item.code"
                                   :label="item.name"
                                   :value="item.code"></el-option>
                      </el-select>
                    </el-form-item>
                    <el-form-item :prop="'rules.'+index+'.ruleConditionDetails.'+rowIndex+'.contrastMode'" class="ml-10"
                                  :rules="[{ required: true, message: '请选择', trigger: 'change' }]">
                      <el-select v-model="row.contrastMode" placeholder="比较符" filterable class="w-100"
                                 @change="changeContrastMode(index, rowIndex)">
                        <el-option v-for="item in options.contrastMode" :key="item.code" :label="item.name"
                                   :value="item.code"></el-option>
                      </el-select>
                    </el-form-item>

                    <!-- 当比较符选【为空、不为空】、此字段不出现 -->
                    <div v-if="row.contrastMode!==9 && row.contrastMode!==10" class="item-row">
                      <el-form-item :prop="'rules.'+index+'.ruleConditionDetails.'+rowIndex+'.compareType'"
                                    class="ml-10"
                                    :rules="[{ required: true, message: '请选择', trigger: 'change' }]">
                        <el-select v-model="row.compareType" placeholder="比较对象" filterable class="w-150"
                                   @change="changeCompareType(index, rowIndex)">
                          <el-option v-for="item in options.compareType" :key="item.code" :label="item.name"
                                     :value="item.code"></el-option>
                        </el-select>
                      </el-form-item>
                      <!-- 比较对象为： 1输入值-显示文本框 2报盘字段-显示下拉框 3其他字段-显示下拉框 -->
                      <el-form-item v-if="row.compareType===1"
                                    :prop="'rules.'+index+'.ruleConditionDetails.'+rowIndex+'.compareObj'" class="ml-10"
                                    :rules="[{ required: true, message: '请输入', trigger: 'blur' }]">
                        <el-input v-model.trim="row.compareObj" placeholder="请输入" maxlength="100"></el-input>
                      </el-form-item>
                      <el-form-item v-else-if="row.compareType===2 || row.compareType===3"
                                    :prop="'rules.'+index+'.ruleConditionDetails.'+rowIndex+'.compareObj'" class="ml-10"
                                    :rules="[{ required: true, message: '请选择', trigger: 'change' }]">
                        <el-select v-model="row.compareObj" placeholder="请选择" filterable class="w-150"
                                   @change="row.compareAttribute=1">
                          <el-option v-if="row.compareType===2 && item.declareColumnName!==row.declareColumnName" v-for="item in offerInfo.columnSettings"
                                     :key="item.uuid"
                                     :label="item.declareColumnName" :value="item.declareColumnName"></el-option>
                          <el-option v-if="row.compareType===3" label="当前日期" value="当前日期"></el-option>
                        </el-select>
                      </el-form-item>
                      <!-- 比较对象为： 1输入值-显示文本框 2报盘字段-显示下拉框 3其他字段-显示下拉框 -->

                      <el-form-item :prop="'rules.'+index+'.ruleConditionDetails.'+rowIndex+'.compareAttribute'"
                                    class="ml-10" :rules="[{ required: true, message: '请选择', trigger: 'change' }]">
                        <el-select v-model="row.compareAttribute" placeholder="比较属性" filterable class="w-100">
                          <el-option v-for="item in handleCompareAttributeOptions(index, rowIndex)" :key="item.code"
                                     :label="item.name" :value="item.code"></el-option>
                        </el-select>
                      </el-form-item>
                    </div>

                  </div>
                  <div>
                    <span class="a-aim f-cursor text-blue" @click="addCondition(index)">添加条件</span>
                  </div>
                </div>
                <p class="fw-blod mt-20">那么</p>
                <div class="pl-10 mt-10" :key="conclusionsKey">
                  <div class="item-row" v-for="(row, rowIndex) in item.ruleConclusions" :key="rowIndex">
<!--                    <i class="el-icon-delete f-cursor text-red label mr-5 font-18" @click="delConclusion(index, rowIndex)"></i>-->
                    <span class="label mr-10">结论{{ rowIndex + 1 }}:</span>
                    <div class="flex1 item-row" :key="actionKey">
                      <el-form-item :prop="'rules.'+index+'.ruleConclusions.'+rowIndex+'.module'"
                                    :rules="[{ required: true, message: '请选择', trigger: 'change' }]">
                        <el-select v-model="row.module" placeholder="校验板块" filterable class="w-150"
                                   @change="changeModule(index, rowIndex)">
                          <el-option v-for="item in options.module" :key="item.code" :label="item.name"
                                     :value="item.code"></el-option>
                        </el-select>
                      </el-form-item>
                      <el-form-item v-if="row.module===2"
                                    :prop="'rules.'+index+'.ruleConclusions.'+rowIndex+'.tplTypeCode'" class="ml-10">
                        <el-select v-model="row.tplTypeCode" placeholder="申报系统" filterable class="w-150" clearable>
                          <el-option v-for="item in options.addrTplItems" :key="item.code" :label="item.name"
                                     :value="item.code"></el-option>
                        </el-select>
                      </el-form-item>
                      <el-form-item :prop="'rules.'+index+'.ruleConclusions.'+rowIndex+'.targetAudience'"
                                    class="ml-10 mr-10"
                                    :rules="[{ required: true, message: '请选择', trigger: 'change' }]">
                        <el-select v-model="row.targetAudience" placeholder="作用对象" filterable class="w-150">
                          <el-option v-for="item in options.targetAudience" :key="item.code"
                                     v-if="(row.module===1&&item.code===1) || (row.module===2&&item.code===2)"
                                     :label="item.name" :value="item.code"></el-option>
                        </el-select>
                      </el-form-item>

                      <el-form-item v-if="row.targetAudience===1"
                                    :prop="'rules.'+index+'.ruleConclusions.'+rowIndex+'.declareColumnName'"
                                    class="mr-10"
                                    :rules="[{ required: true, message: '请选择', trigger: 'change' }]">
                        <el-select v-model="row.declareColumnName" placeholder="请选择" filterable class="w-150">
                          <el-option :label="row.declareColumnName" :value="row.declareColumnName"></el-option>
                        </el-select>
                      </el-form-item>

                      <div v-for="(li, liIndex) in row.ruleConclusionDetails" :key="liIndex" class="action-item">
                        <i class="el-icon-circle-close text-red ic-del" v-if="row.targetAudience===1 && row.ruleConclusionDetails.length>1"
                           @click="delAction(index, rowIndex, liIndex)"></i>
                        <el-form-item
                          :prop="'rules.'+index+'.ruleConclusions.'+rowIndex+'.ruleConclusionDetails.'+liIndex+'.type'"
                          :rules="[{ required: true, message: '请选择', trigger: 'change' }]">
                          <el-select v-model="li.type" placeholder="添加动作" filterable class="w-120"
                                     @change="changeType(index, rowIndex, liIndex)">
                            <el-option v-for="item in handleTypeOptions(index, rowIndex, liIndex)" :key="item.code" :label="item.name"
                                       :value="item.code"></el-option>
                          </el-select>
                        </el-form-item>
                        <!--显隐、必选、赋值、下拉选项-->
                        <el-form-item v-if="li.type && li.type!==5"
                                      :prop="'rules.'+index+'.ruleConclusions.'+rowIndex+'.ruleConclusionDetails.'+liIndex+'.result'"
                                      class="ml-5" :rules="[{ required: true, message: li.type===1||li.type===2?'请选择':'请输入', trigger: ['change', 'blur'] }
                                      ,{validator: (rule, value, callback) => validActioResult(rule, value, callback, li.type), trigger:'blur'}]">
                          <el-select v-if="li.type===1" v-model="li.result" placeholder="请选择" filterable
                                     class="w-100">
                            <el-option v-for="item in options.result1" :key="item.code" :label="item.name"
                                       :value="item.code"></el-option>
                          </el-select>
                          <el-select v-else-if="li.type===2" v-model="li.result" placeholder="请选择" filterable
                                     class="w-100">
                            <el-option v-for="item in options.result2" :key="item.code" :label="item.name"
                                       :value="item.code"></el-option>
                          </el-select>
                          <el-input v-else-if="li.type===3 || li.type===6" v-model.trim="li.result" placeholder="请输入"
                                    maxlength="100" class="w-280" clearable></el-input>
                          <el-input v-else-if="li.type===4" v-model="li.result" placeholder="一行输入一个选项"
                                    maxlength="500" class="w-250" type="textarea"
                                    :rows="3" clearable></el-input>
                        </el-form-item>
                      </div>

                      <!--作用对象选【数据】，不可添加多个动作-->
                      <span class="a-aim f-cursor text-blue pt-10 ml-10 mb-30" style="height: 20px;"
                            v-if="row.targetAudience===1 && row.ruleConclusionDetails.length<4" @click="addAction(index, rowIndex)">添加动作</span>
                    </div>

                  </div>
                  <!--目前仅支持设置1份结论-->
                  <div v-show="item.ruleConclusions.length<1" class="mb-30">
                    <span class="a-aim f-cursor text-blue" @click="addConclusion(index)">添加结论</span>
                  </div>
                </div>
              </div>
            </div>

            <div class="mt-20">
              <span v-if="setForm.rules.length===0" class="text-blue label f-cursor ml-5" @click="addRule(0)">添加校验</span>
            </div>
          </div>
        </el-form>

        <div class="text-red pl-30 mt-10" v-if="errorMsgList.length>0">
          <p>出现错误：</p>
          <p v-for="(item, index) in errorMsgList" :key="index" class="mt-5">{{ item }}</p>
        </div>
      </div>
      <div class="drawer-footer-buts">
        <el-button class="w-80 s-btn" v-show="setForm.uuid && isExistRule && setForm.rules.length>0" @click="handleClearRule">清空校验</el-button>
        <el-tooltip v-show="setForm.uuid && !isExistRule && !isImportRule" effect="dark" content="未设置时，可引入同申报网站同字段规则" placement="top">
          <el-button class="w-80 btn--border-blue s-btn" @click="handleImportRule">引入规则</el-button>
        </el-tooltip>
        <el-button class="w-80 btn--border-blue s-btn" @click="show = false">取消</el-button>
        <el-button type="primary" class="w-80 s-btn" @click="handleConfirmSave()">确定保存</el-button>
      </div>
    </el-drawer>

    <importRuleDialog ref="importRuleDialog" :offerInfo="offerInfo" @success="handleImportRuleSuccess"></importRuleDialog>
  </div>
</template>
<script>
import importRuleDialog from './importRuleDialog'
export default {
  name: 'setValidateRules',
  props: ['offerInfo'],
  components: { importRuleDialog },
  computed: {},
  watch: {
    show (newValue, oldValue) {
      if (newValue && this.options.addrTplItems.length === 0) {
        this.getAddrTplItems()
      }
    }
  },
  data () {
    return {
      show: false,
      options: {
        relation: [ // 条件间的关系，1：与，2：或
          { code: 1, name: '并且' },
          { code: 2, name: '或者' }
        ],
        addrTplItems: [], // 社保申报系统
        valueType: [ // 条件字段属性，1：值，2：年月，3：年，4：天数-距当日
          { code: 1, name: '值' },
          { code: 2, name: '年月' },
          { code: 3, name: '年' },
          { code: 4, name: '天数-距当日' },
          { code: 5, name: '日' }
        ],
        contrastMode: [ // 条件满足关系 1：等于，2：不等于，3：包含，4：不包含，5：大于，6：大于等于，7：小于，8：小于等于，9：为空，10：不为空
          { code: 1, name: '等于' },
          { code: 2, name: '不等于' },
          { code: 3, name: '包含' },
          { code: 4, name: '不包含' },
          { code: 5, name: '大于' },
          { code: 6, name: '大于等于' },
          { code: 7, name: '小于' },
          { code: 8, name: '小于等于' },
          { code: 9, name: '为空' },
          { code: 10, name: '不为空' }
        ],
        compareType: [ // 比较对象类型，1：输入值，2：报盘字段，3：其他字段
          { code: 1, name: '输入值' },
          { code: 2, name: '报盘字段' },
          { code: 3, name: '其他字段' }
        ],
        module: [ // 校验板块 1：入数据，2：导报盘
          { code: 1, name: '入数据' },
          { code: 2, name: '导报盘' }
        ],
        targetAudience: [ // 作用对象 1：报盘字段，2：数据
          { code: 1, name: '报盘字段' },
          { code: 2, name: '数据' }
        ],
        type: [ // 执行动作，1：显隐性，2：必选，3：赋值，4,：下拉选项，5：报盘不导出，6：回盘失败
          { code: 1, name: '显隐性', targetAudience: 1 },
          { code: 2, name: '必选', targetAudience: 1 },
          { code: 3, name: '赋值', targetAudience: 1 },
          { code: 4, name: '下拉选项', targetAudience: 1 },
          { code: 5, name: '报盘不导出', targetAudience: 2 },
          { code: 6, name: '回盘失败', targetAudience: 2 }
        ],
        result1: [ // 执行动作结果，1：显示，2：隐藏
          { code: '1', name: '显示' },
          { code: '2', name: '隐藏' }
        ],
        result2: [ // 执行动作结果，1：选填 2：必填
          { code: '1', name: '选填' },
          { code: '2', name: '必填' }
        ]
      },
      isEdit: false,
      setForm: {
        uuid: '', // 当前字段
        rules: []
      },
      curDeclareColumnName: '', // 当前字段name
      ruleItem: {
        comment: '',
        relation: 2,
        ruleConditionDetails: [],
        ruleConclusions: []
      },
      conditionItem: { // 条件
        declareColumnName: '',
        valueType: 1,
        contrastMode: 1,
        compareType: '',
        compareObj: '',
        compareAttribute: ''
      },
      conclusionItem: { // 结论
        module: '',
        tplTypeCode: '',
        targetAudience: '',
        declareColumnName: '',
        ruleConclusionDetails: []
      },
      actionItem: { // 添加动作
        type: '',
        result: ''
      },
      ruleKey: 0,
      conditionKey: 1,
      conclusionsKey: 2,
      actionKey: 3,
      ruleList: [],
      isExistRule: false, // 字段是否存在校验规则
      isImportRule: false, // 是否已经引入了规则
      errorMsgList: []
    }
  },
  created () {
  },
  mounted () {
  },
  methods: {
    init (obj) {
      if (obj.ruleEdit) {
        this.isEdit = true
        this.isExistRule = true
        this.setForm = obj.ruleEdit
      } else {
        this.isEdit = false
        this.setForm = this.$options.data().setForm
      }

      this.ruleList = obj.ruleList
      console.log(obj)
      this.changeCurDeclareColumn()
      this.show = true
    },

    // 获取申报系统(公积金不需要)
    getAddrTplItems () {
      let that = this
      if (this.offerInfo.bussinssType !== 1) {
        return
      }
      let addrId = this.offerInfo.addrId
      let bussinssType = this.offerInfo.bussinssType
      this.$http({
        url: `policy/offerSettings/findAddrTplItems/${addrId}/${bussinssType}`,
        method: 'post'
      }).then(({ data }) => {
        let list = data.data.tplItems ? data.data.tplItems : []
        list.map(item => {
          item.code = item.tplType
          item.name = item.tplTypeName
        })
        that.options.addrTplItems = list
      }).catch(() => {
      })
    },

    // 校验-添加
    addRule (index) {
      let row = this.setForm
      let item = this.$global.deepCopyObj(this.ruleItem)
      row.rules.splice(index, 0, item)
      console.log(row.rules)
      this.addCondition(index)
      this.addConclusion(index)
    },
    // 校验-删除
    delRule (index) {
      this.$confirm('是否确定移除此校验规则？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning'
      }).then(() => {
        let row = this.setForm
        row.rules.splice(index, 1)
        if (row.rules.length === 0) {
          this.addRule(0)
        }
        this.ruleKey = new Date().getTime()
      }).catch(() => {
      })
    },
    // 条件-添加
    addCondition (index) {
      let row = this.setForm.rules[index]
      row.ruleConditionDetails.push(this.$global.deepCopyObj(this.conditionItem))
    },
    // 条件-删除
    delCondition (index, rowIndex) {
      this.$confirm('是否确定移除此条件？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning'
      }).then(() => {
        let row = this.setForm.rules[index]
        row.ruleConditionDetails.splice(rowIndex, 1)
        this.conditionKey = new Date().getTime()
      }).catch(() => {
      })
    },
    // 结论-添加
    addConclusion (index) {
      let row = this.setForm.rules[index]
      let item = this.$global.deepCopyObj(this.conclusionItem)
      item.declareColumnName = this.curDeclareColumnName
      row.ruleConclusions.push(item)
      this.addAction(index, row.ruleConclusions.length - 1)
    },
    // 结论-删除
    delConclusion (index, rowIndex) {
      this.$confirm('是否确定移除此结论？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning'
      }).then(() => {
        let row = this.setForm.rules[index]
        row.ruleConclusions.splice(rowIndex, 1)
        this.conclusionsKey = new Date().getTime()
      }).catch(() => {
      })
    },
    // 添加动作-添加
    addAction (index, rowIndex) {
      let row = this.setForm.rules[index].ruleConclusions[rowIndex]
      row.ruleConclusionDetails.push(this.$global.deepCopyObj(this.actionItem))
    },
    // 添加动作-删除
    delAction (index, rowIndex, liIndex) {
      let row = this.setForm.rules[index].ruleConclusions[rowIndex]
      row.ruleConclusionDetails.splice(liIndex, 1)
      this.actionKey = new Date().getTime()
    },

    // 清空校验
    handleClearRule () {
      this.$confirm('是否清空该字段所有的校验？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning'
      }).then(() => {
        this.setForm.rules = []
        this.errorMsgList = []
      }).catch(() => {
      })
    },

    // 引入规则
    handleImportRule () {
      this.$refs.importRuleDialog.init(this.setForm.uuid)
    },
    handleImportRuleSuccess (res) {
      this.setForm.rules = this.$global.deepcopyArray(res)
      this.ruleKey = new Date().getTime()
      this.isImportRule = true
    },

    // 确定保存
    handleConfirmSave () {
      let that = this
      this.$refs.setForm.validate((valid) => {
        if (valid) {
          this.showLoading()
          let uuid = this.setForm.uuid
          let setData = []
          let rules = this.$global.deepCopyObj(this.setForm.rules)
          rules.map(item => {
            item.declareColumnUuid = uuid
            item.ruleConditionDetails.map(it => {
              it.conclusion = that.handleConditionText(it)
            })
            // 处理结论文本
            item.ruleConclusions.map(it => {
              it.ruleConclusionDetails.map(li => {
                if (li.type === 4) {
                  //   处理下拉选项
                  let arr = this.getInputDataRow(li.result)
                  li.result = arr.join(',')
                }
              })
              it.conclusion = that.handleConclusionText(it)
            })
            setData.push(item)
          })
          // 校验规则
          this.$http({
            url: `policy/offerSettings/validateRules/${uuid}`,
            method: 'post',
            data: setData
          }).then(({ data }) => {
            that.errorMsgList = data.data
            if (data.data.length > 0) {
              that.closeLoading()
              that.$nextTick(() => {
                const myDiv = that.$refs.drawerBody
                myDiv.scrollTop = myDiv.scrollHeight - myDiv.clientHeight
              })
            } else {
              that.doSave(setData)
            }
          }).catch(() => {
            this.closeLoading()
          })
        }
      })
    },
    doSave (setData) {
      let that = this
      let uuid = this.setForm.uuid
      this.$http({
        url: `policy/offerSettings/saveColumnRules/${uuid}`,
        method: 'post',
        data: setData
      }).then(({ data }) => {
        that.show = false
        that.closeLoading()
        that.$message.success('保存成功')
        that.$emit('success', uuid)
      }).catch(() => {
        this.closeLoading()
      })
    },
    // 处理条件文本
    handleConditionText (it) {
      let valueType = this.handleConditionStr('valueType', it.valueType)
      let contrastMode = this.handleConditionStr('contrastMode', it.contrastMode)
      // let compareType = this.handleConditionStr('compareType', it.compareType)
      let compareObj = ''
      if (it.compareType === 1) {
        compareObj = '[' + it.compareObj + ']'
      } else {
        compareObj = it.compareObj
      }
      let compareAttribute = this.handleConditionStr('valueType', it.compareAttribute)
      let text = it.declareColumnName + ' ' + valueType + ' ' + contrastMode + ' ' + compareObj + (it.compareType === 1 ? '' : ' ' + compareAttribute)
      console.log('条件==' + text)
      return text
    },
    // 处理结论文本
    handleConclusionText (it) {
      let module = this.handleConditionStr('module', it.module)
      let tplTypeCode = this.handleConditionStr('addrTplItems', it.tplTypeCode)
      let targetAudience = this.handleConditionStr('targetAudience', it.targetAudience)
      let declareColumnName = it.declareColumnName
      let actionArr = []
      let actionText = ''
      let type = ''
      it.ruleConclusionDetails.map(item => {
        actionText = ''
        type = this.handleConditionStr('type', item.type)
        if (item.type === 1) {
          actionText = this.handleConditionStr('result1', item.result)
        } else if (item.type === 2) {
          actionText = this.handleConditionStr('result2', item.result)
        } else if (item.type === 3 || item.type === 6) {
          actionText = type + ' [' + item.result + ']'
        } else if (item.type === 4) {
          actionText = type + ' [' + item.result + ']'
        } else if (item.type === 5) {
          actionText = type
        }
        actionArr.push(actionText)
      })
      let actionArrText = actionArr.join(' ')
      let text = module + (tplTypeCode ? ' ' + tplTypeCode : '') +  ' ' + targetAudience + (declareColumnName ? ' ' + declareColumnName : '') + ' ' + actionArrText
      console.log('结论==' + text)
      return text
    },
    // 处理条件字段code转name
    handleConditionStr (type, code) {
      let options = this.options[type]
      for (let i = 0; i < options.length; i++) {
        if (options[i].code === code) {
          return options[i].name
        }
      }
      return ''
    },

    // 改变【当前字段】的处理
    changeCurDeclareColumn () {
      this.isImportRule = false
      this.errorMsgList = []
      this.curDeclareColumnName = ''
      let uuid = this.setForm.uuid
      if (!uuid) {
        return
      }
      //   获取【当前字段】的name
      for (let i = 0; i < this.offerInfo.columnSettings.length; i++) {
        if (this.offerInfo.columnSettings[i].uuid === uuid) {
          this.curDeclareColumnName = this.offerInfo.columnSettings[i].declareColumnName
          break
        }
      }

      //   获取字段的校验规则
      let setForm = null
      console.log(uuid)
      for (let j = 0; j < this.ruleList.length; j++) {
        if (this.ruleList[j].uuid === uuid) {
          setForm = this.$global.deepCopyObj(this.ruleList[j])
          setForm.rules = setForm.rules ? setForm.rules : []
          setForm.rules.map(item => {
            item.ruleConclusions = item.ruleConclusions ? item.ruleConclusions : []
            item.ruleConclusions.map(it => {
              it.ruleConclusionDetails = it.ruleConclusionDetails ? it.ruleConclusionDetails : []
              it.ruleConclusionDetails.map(li => {
                if (li.type === 4) {
                  li.result = li.result.replaceAll(/,/g, '\n')
                }
              })
            })
          })
          break
        }
      }
      if (setForm) {
        this.isExistRule = true
        this.setForm = setForm
      } else {
        this.isExistRule = false
        this.setForm = this.$options.data().setForm
        this.setForm.uuid = uuid
        console.log(setForm)
        this.addRule(0)
      }
      this.ruleKey = new Date().getTime()
    },
    // 改变【条件字段】的处理
    changeDeclareColumnName (index, rowIndex) {
      let row = this.setForm.rules[index].ruleConditionDetails[rowIndex]
      row.valueType = 1
      if (row.compareType === 2 && row.declareColumnName === row.compareObj) {
        row.compareObj = ''
      }
    },
    // 改变【比较符】的处理
    changeContrastMode (index, rowIndex) {
      let row = this.setForm.rules[index].ruleConditionDetails[rowIndex]
      if (row.contrastMode === 9 || row.contrastMode === 10) {
        row.compareType = ''
        row.compareObj = ''
        row.compareAttribute = ''
      }
    },
    // 改变【比较对象】的处理
    changeCompareType (index, rowIndex) {
      let row = this.setForm.rules[index].ruleConditionDetails[rowIndex]
      row.compareAttribute = 1
      if (row.compareType === 3) {
        row.compareObj = '当前日期'
      } else {
        row.compareObj = ''
      }
      this.$refs.setForm.clearValidate('rules.' + index + '.ruleConditionDetails.' + rowIndex + '.compareObj')
    },

    // 处理【属性】的下拉选项
    handleValueTypeOptions (index, rowIndex) {
      let row = this.setForm.rules[index].ruleConditionDetails[rowIndex]
      let declareColumnName = row.declareColumnName
      let inputType = ''
      for (let i = 0; i < this.offerInfo.columnSettings.length; i++) {
        if (this.offerInfo.columnSettings[i].declareColumnName === declareColumnName) {
          inputType = this.offerInfo.columnSettings[i].inputType
          break
        }
      }

      let options = []
      if (inputType === 4 || inputType === 5) {
        options = this.$global.deepcopyArray(this.options.valueType)
      } else {
        options.push(this.options.valueType[0])
      }

      return options
    },
    // 处理【比较属性】的下拉选项
    handleCompareAttributeOptions (index, rowIndex) {
      // 比较对象类型，1：输入值，2：报盘字段，3：其他字段
      // 比较对象为日期(inputType==4||inputType==5)类，能出现【值、年月、年】选项。如：本次参保日期
      // 比较对象非日期类，只能出现【值】选项。如：姓名
      let row = this.setForm.rules[index].ruleConditionDetails[rowIndex]
      let compareType = row.compareType
      let compareObj = row.compareObj
      let inputType = ''
      if (compareType === 2 && compareObj) {
        for (let i = 0; i < this.offerInfo.columnSettings.length; i++) {
          if (this.offerInfo.columnSettings[i].declareColumnName === compareObj) {
            inputType = this.offerInfo.columnSettings[i].inputType
            break
          }
        }
      }

      let options = []
      if (compareType === 3 || inputType === 4 || inputType === 5) {
        this.options.valueType.map(it => {
          if (it.code !== 4) {
            options.push(it)
          }
        })
      } else {
        options.push(this.options.valueType[0])
        row.compareAttribute = 1
      }

      return options
    },
    // 处理【添加动作】的下拉选项
    handleTypeOptions (index, rowIndex, liIndex) {
      // 每种动作只能添加一次
      /* if (this.setForm.rules.length === 0 || this.setForm.rules[index].ruleConclusions.length === 0) {
        return []
      } */
      let row = this.setForm.rules[index].ruleConclusions[rowIndex]
      let li = this.setForm.rules[index].ruleConclusions[rowIndex].ruleConclusionDetails[liIndex]

      let actionTypeArr = []
      row.ruleConclusionDetails.map(item => {
        actionTypeArr.push(item.type)
      })
      let options = []
      this.options.type.map(item => {
        if (item.targetAudience === row.targetAudience && (actionTypeArr.indexOf(item.code) === -1 || item.code === li.type)) {
          options.push(item)
        }
      })

      return options
    },
    // 改变【校验板块】的处理
    changeModule (index, rowIndex) {
      let row = this.setForm.rules[index].ruleConclusions[rowIndex]
      /* 校验版块选【入数据】，对象可选【报盘字段】，选报盘字段时，只出来一个【当前字段】的报盘字段的选项
      校验版块选【导报盘】，对象可选【数据】 */
      if (row.module === 1) {
        row.tplTypeCode = ''
        row.declareColumnName = this.curDeclareColumnName
        row.targetAudience = 1
      } else if (row.module === 2) {
        row.targetAudience = 2
        row.declareColumnName = ''
      }

      row.ruleConclusionDetails = []
      this.addAction(index, rowIndex)
      this.conclusionsKey = new Date().getTime()
    },
    // 改变【添加动作】的处理
    changeType (index, rowIndex, liIndex) {
      let row = this.setForm.rules[index].ruleConclusions[rowIndex].ruleConclusionDetails[liIndex]
      // 报盘不导出、回盘失败 时清空动作内容
      row.result = ''
      this.$refs.setForm.clearValidate('rules.' + index + '.ruleConclusions.' + rowIndex + '.ruleConclusionDetails.' + liIndex + '.result')
    },
    // 处理下拉选项的数据
    getInputDataRow (value) {
      var obj = this.$global.getInputDataRow(value)
      let arr = []
      obj.value.map(item => {
        if (item.replaceAll(/\s+/g, '')) {
          arr.push(item.trim())
        }
      })
      return arr
    },
    // 校验下拉选项输入
    validActioResult (rule, value, callback, type) {
      if (type === 4) {
        let arr = this.getInputDataRow(value)
        if (arr.length === 0) {
          callback(new Error('请输入'))
        } else {
          callback()
        }
      } else {
        callback()
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
/deep/ .detail-drawer {
  width: 1100px !important;
}

.drawer-body {
  height: 100%;
  overflow-y: auto;
}

.item-row {
  display: flex;
  flex-wrap: wrap;

  .label {
    line-height: 36px;
  }
}

/deep/ .el-form-item {
  margin-bottom: 10px;
}

.action-item {
  display: flex;
  border: 1px dashed #dbdbdb;
  margin-right: 10px;
  padding: 3px 5px;
  margin-top: -5px;
  border-radius: 6px;
  position: relative;
  margin-bottom: 20px;

  /deep/ .el-form-item {
    margin-bottom: 0px;
  }

  .ic-del {
    position: absolute;
    right: -8px;
    top: -8px;
    font-size: 16px;
    cursor: pointer;
  }
}
</style>
