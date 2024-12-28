<template>
  <div>
    <!--新增、编辑-->
    <el-drawer
      title="公积金补缴规则"
      :visible.sync="localVisible"
      :wrapperClosable="false"
      custom-class="spl-filter-drawer add-drawer"
      :show-close="true"
      :before-close="doClose"
      size="20%"
    >
      <div>
        <div class="drawerForm" style="padding: 15px 20px 20px 20px">
          <el-form
            :inline="true"
            :model="accumuFundForm"
            ref="accumuFundForm"
            label-width="0"
          >
            <div style="padding: 10px">
              <p class="item-label required-pre">参保城市</p>
              <el-form-item
                label=""
                prop="addrName"
                :rules="[
                  {
                    required: true,
                    message: '请选择参保城市',
                    trigger: 'blur',
                  },
                ]"
              >
                <addrSelector
                  style="width: 250px"
                  v-model="accumuFundForm.addrName"
                  :addrArr="allAddr"
                  width="100%"
                  placeholder="请输入"
                  @changeAddrSelect="changeAddrSelect"
                  :disabled="true"
                ></addrSelector>
              </el-form-item>

              <p class="item-label required-pre">是否允许补缴</p>
              <el-form-item
                label=""
                prop="accfundAllowSupp"
                :rules="[
                  {
                    required: true,
                    message: '请选择是否允许补缴',
                    trigger: 'change',
                  },
                ]"
              >
                <el-radio-group v-model="accumuFundForm.accfundAllowSupp">
                  <el-radio :label="1"><span>允许</span></el-radio>
                  <el-radio :label="0"><span>不允许</span></el-radio>
                </el-radio-group>
              </el-form-item>
              <div v-if="this.accumuFundForm.accfundAllowSupp == 1">
                <p class="item-label required-pre">可允许补缴类型</p>
                <el-form-item
                  label=""
                  prop="allowSuppTypeName"
                  :rules="[
                    {
                      required: true,
                      message: '请选择可允许补缴类型',
                      trigger: 'change',
                    },
                  ]"
                >
                  <el-select
                    style="width: 250px"
                    placeholder="请选择"
                    v-model="accumuFundForm.allowSuppTypeName"
                    @change="changeAllowSuppType"
                  >
                    <el-option
                      v-for="item in payOfoptions"
                      :key="item.dictCode"
                      :label="item.dictName"
                      :value="
                        JSON.stringify({
                          dictCode: item.dictCode,
                          dictName: item.dictName,
                        })
                      "
                    ></el-option>
                  </el-select>
                </el-form-item>
                <p
                  v-show="accumuFundForm.allowSuppTypeCode == '10005001'"
                  class="item-label required-pre"
                >
                  往前补缴月数
                </p>
                <el-form-item
                  v-if="accumuFundForm.allowSuppTypeCode == '10005001'"
                  label=""
                  prop="allowMonths"
                  :rules="[
                    accumuFundForm.allowSuppTypeCode == '10005001'
                      ? {
                          required: true,
                          message: '请输入往前补缴月数',
                          trigger: 'blur',
                        }
                      : {
                          required: false,
                          message: '请输入往前补缴月数',
                          trigger: 'blur',
                        },
                  ]"
                >
                  <el-input
                    style="width: 250px"
                    maxlength="8"
                    oninput="value=value.replace(/[^\d]/g,'').replace(/^0{1,}/g,'')"
                    v-model="accumuFundForm.allowMonths"
                    placeholder="请输入内容"
                  ></el-input>
                </el-form-item>
                <p
                  class="item-label required-pre"
                  v-show="accumuFundForm.allowSuppTypeCode == '10005002'"
                >
                  最早可补缴年月
                </p>
                <el-form-item
                  v-if="accumuFundForm.allowSuppTypeCode == '10005002'"
                  label=""
                  prop="earliestAllowYearMonth"
                  :rules="[
                    (this.allowMonthsDate !== null &&
                      accumuFundForm.allowSuppTypeCode == '10005002') ||
                    this.accumuFundForm.accfundAllowSupp == 0
                      ? {
                          required: false,
                          message: '请选择最早补缴年月',
                          trigger: ['blur', 'change'],
                        }
                      : {
                          required: true,
                          message: '请选择最早补缴年月',
                          trigger: ['blur', 'change'],
                        },
                  ]"
                >
                  <el-date-picker
                    style="width: 250px"
                    v-model="allowMonthsDate"
                    type="month"
                    placeholder="选择日期"
                    @change="handleAllowMonths()"
                  >
                  </el-date-picker>
                </el-form-item>
                <p class="item-label">其他</p>
                <el-form-item label="">
                  <el-checkbox
                    v-model="isAcrossMonth"
                    @change="handleIsAcrossMonth"
                    >不允许跨月补缴</el-checkbox
                  >
                </el-form-item>
              </div>
            </div>
          </el-form>
        </div>
        <div class="drawer-footer-buts" v-if="!accumuFundForm.isDisabled">
          <el-button class="btn--border-blue s-btn mr-0" @click="doClose"
            >取消</el-button
          >
          <el-button
            type="primary"
            class="s-btn ml-12"
            :loading="accumuFundForm.btnLoading"
            @click="handleValid"
            >确认</el-button
          >
        </div>
      </div>
    </el-drawer>
  </div>
</template>
<script>
import moment from 'moment'
import addrSelector from '../../../components/common/addrSelector.vue'
export default {
  name: 'addCityDrawer',
  components: {
    addrSelector,
  },
  props: {
    payOfoptionsDanger: {
      type: Array,
      default: () => [],
    },
    payOfoptions: {
      type: Array,
      default: () => [],
    },
    visible: {
      type: Boolean,
      default: false,
    },
    newAccumuFundForm: {
      type: String,
      default: '',
    },
  },
  computed: {
    localVisible: {
      get() {
        return this.visible
      },
      set(val) {
        this.$emit('update:visible', val)
      },
    },
  },
  watch: {
    newAccumuFundForm: {
      handler(val) {
        if (val) {
          Object.assign(this.accumuFundForm, JSON.parse(val))
          this.accumuFundForm.acrossMonth = this.accumuFundForm.acrossMonth == null ? 1:this.accumuFundForm.acrossMonth
          if (this.accumuFundForm.acrossMonth == '1') {
            this.isAcrossMonth = false
          } else {
            this.isAcrossMonth = true
          }
          this.allowMonthsDate = this.accumuFundForm.earliestAllowYearMonth
          // this.accumuFundForm.allowSuppTypeCode = jsonVal.dictCode
          // this.accumuFundForm.allowSuppTypeName = jsonVal.dictName
        }
      },
      deep: true,
      immediate: true,
    },
  },
  data() {
    return {
      allowMonthsDate: undefined,
      isAcrossMonth: false,
      accumuFundForm: {
        earliestAllowYearMonth: undefined,
        addrName: undefined,
        accfundAllowSupp: undefined,
        allowSuppTypeCode: '10005001',
        allowSuppTypeName: undefined,
        allowMonths: undefined,
        acrossMonth: undefined,
      },
      allAddr: [],
    }
  },
  created() {},
  mounted() {
    this.getAddr()
  },

  methods: {
    handleAllowMonths() {
      if (this.allowMonthsDate) {
        this.accumuFundForm.earliestAllowYearMonth = moment(
          this.allowMonthsDate
        ).format('YYYY-MM')
        // this.$refs.accumuFundForm.clearValidate('earliestAllowYearMonth')
      } else {
        this.accumuFundForm.earliestAllowYearMonth = undefined
      }
    },
    handleIsAcrossMonth(val) {
      if (val == true) {
        this.accumuFundForm.acrossMonth = 0
      } else {
        this.accumuFundForm.acrossMonth = 1
      }
    },
    changeAllowSuppType(val) {
      if (val) {
        // this.accumuFundForm.allowMonths = undefined
        let jsonVal = JSON.parse(val)
        this.accumuFundForm.allowSuppTypeCode = jsonVal.dictCode
        this.accumuFundForm.allowSuppTypeName = jsonVal.dictName
        this.$refs.accumuFundForm.clearValidate('earliestAllowYearMonth')
        this.$refs.accumuFundForm.clearValidate('allowMonths')
      }
    },
    // 获取参保地
    getAddr(type) {
      let that = this
      this.$http({
        url: 'policy/policyAddr/addrList',
        method: 'post',
      }).then(({ data }) => {
        this.allAddr = data.data
      })
    },
    changeAddrSelect(val) {
      this.accumuFundForm.addrName = val.name
      this.accumuFundForm.addrId = val.id
    },
    doClose() {
      this.localVisible = false
      this.$emit('handleNull-newFrom')
      this.$refs.accumuFundForm.resetFields()
    },
    handleNullFrom() {
      this.accumuFundForm = {
        earliestAllowYearMonth: undefined,
        addrName: undefined,
        accfundAllowSupp: undefined,
        allowSuppTypeCode: '10005001',
        allowSuppTypeName: undefined,
        allowMonths: undefined,
        acrossMonth: undefined,
      }
      this.$refs.accumuFundForm.resetFields()
    },
    //验证，确定提交保存
    handleValid() {
      this.$refs.accumuFundForm.validate((valid) => {
        if (valid) {
          this.$http({
            url: 'policy/declareAddr/saveAccfundSuppRule',
            method: 'post',
            data: this.accumuFundForm,
          }).then(({ data }) => {
              this.localVisible = false
              this.handleNullFrom()
              this.$emit('getTable-data') //刷新列表
              this.$emit('handleNull-newFrom') //制空newForm
          })
        } else {
          return false
        }
      })
    },
  },
}
</script>
<style lang="less" scoped>
</style>
