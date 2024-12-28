<template>
  <div>
    <el-drawer
      title="社保补缴规则"
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
            :model="formData"
            ref="formData"
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
                  v-model="formData.addrName"
                  :addrArr="allAddr"
                  width="100%"
                  placeholder="请输入"
                  :disabled="true"
                ></addrSelector>
              </el-form-item>

              <p class="item-label required-pre">是否允许补缴</p>
              <el-form-item
                label=""
                prop="socialAllowSupp"
                :rules="[
                  {
                    required: true,
                    message: '请选择是否允许补缴',
                    trigger: 'change',
                  },
                ]"
              >
                <el-radio-group v-model="formData.socialAllowSupp">
                  <el-radio :label="1"><span>允许</span></el-radio>
                  <el-radio :label="0"><span>不允许</span></el-radio>
                </el-radio-group>
              </el-form-item>
              <p
                style="text-align: right"
                v-show="this.formData.socialAllowSupp == 1"
              >
                <el-button type="primary" size="small" @click="handleAddDrawer"
                  >添加</el-button
                >
              </p>
              <!-- custom-class="clite-drawer" -->
              <el-drawer
                title="添加社保补缴规则"
                :append-to-body="true"
                :visible.sync="addDrawerRules"
                custom-class="new-loader add-drawer"
                :wrapperClosable="false"
              >
                <!-- as -->
                <div class="drawerForm" style="padding: 15px 20px 20px 20px">
                  <el-form
                    :inline="true"
                    :model="sacivalRulesForm"
                    ref="sacivalRulesForm"
                    label-width="0"
                  >
                    <div style="padding: 10px">
                      <p class="item-label required-pre">险种：</p>
                      <el-form-item
                        prop="itemName"
                        label=""
                        :rules="[
                          {
                            required: true,
                            message: '请选择险种',
                            trigger: 'blur',
                          },
                        ]"
                      >
                        <el-select
                          style="width: 220px"
                          placeholder="请选择"
                          v-model="sacivalRulesForm.itemName"
                          @change="changeSacivalRulesForm"
                        >
                          <el-option
                            v-for="item in payOfoptionsDanger"
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
                      <p class="item-label required-pre">可允许补缴类型</p>
                      <el-form-item
                        label=""
                        prop="allowSuppTypeName"
                        :rules="[
                          {
                            required: true,
                            message: '请选择可允许补缴类型',
                            trigger: 'blur',
                          },
                        ]"
                      >
                        <el-select
                          style="width: 220px"
                          placeholder="请选择"
                          v-model="sacivalRulesForm.allowSuppTypeName"
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
                        class="item-label required-pre"
                        v-show="
                          sacivalRulesForm.allowSuppTypeCode == '10005001'
                        "
                      >
                        往前补缴月数
                      </p>

                      <el-form-item
                        v-show="
                          sacivalRulesForm.allowSuppTypeCode == '10005001'
                        "
                        label=""
                        prop="allowMonths"
                        :rules="[
                          sacivalRulesForm.allowSuppTypeCode == '10005001'
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
                          style="width: 220px"
                          maxlength="8"
                          oninput="value=value.replace(/[^\d]/g,'').replace(/^0{1,}/g,'')"
                          v-model.trim="sacivalRulesForm.allowMonths"
                          placeholder="请输入内容"
                        ></el-input>
                      </el-form-item>
                      <p
                        class="item-label required-pre"
                        v-show="
                          sacivalRulesForm.allowSuppTypeCode == '10005002'
                        "
                      >
                        最早可补缴年月
                      </p>
                      <el-form-item
                        v-show="
                          sacivalRulesForm.allowSuppTypeCode == '10005002'
                        "
                        label=""
                        prop="earliestAllowYearMonth"
                        :rules="[
                          allowMonthsDate == null &&
                          sacivalRulesForm.allowSuppTypeCode == '10005002'
                            ? {
                                required: true,
                                message: '请选择最早可补缴年月',
                                trigger: ['blur', 'change'],
                              }
                            : {
                                required: false,
                                message: '请选择最早可补缴年月',
                                trigger: ['blur', 'change'],
                              },
                        ]"
                      >
                        <el-date-picker
                          style="width: 220px"
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
                          :checked="isJoin"
                          v-model="isDefaultInsured"
                          @change="handleIsJoin"
                          >默认不参保</el-checkbox
                        >
                        <el-checkbox
                          :checked="isAcrossMontChecked"
                          v-model="isAcrossMonth"
                          @change="handleIsAcrossMonth"
                          >不允许跨月补缴</el-checkbox
                        >
                      </el-form-item>
                    </div>
                  </el-form>
                </div>
                <div class="text-button" v-if="!sacivalRulesForm.isDisabled">
                  <el-button
                    class="btn--border-blue s-btn mr-0"
                    @click="handleCancel"
                    >关闭</el-button
                  >
                  <el-button
                    type="primary"
                    class="s-btn ml-12"
                    :loading="sacivalRulesForm.btnLoading"
                    @click="handleAdd"
                    >确认</el-button
                  >
                </div>
                <!-- 2212 -->
              </el-drawer>
              <el-table
                v-show="this.formData.socialAllowSupp == 1"
                class="box-table"
                :data="formData.rules"
                style="width: 100%"
              >
                <el-table-column prop="itemName" label="险种" width="80">
                </el-table-column>
                <el-table-column
                  prop="allowSuppTypeName"
                  label="可允许缴补类型"
                  width="120"
                >
                </el-table-column>
                <el-table-column
                  prop="allowMonths"
                  label="允许补缴时间"
                  width="120"
                >
                  <template slot-scope="scope">
                    <span v-show="scope.row.allowSuppTypeCode == '10005001'">{{
                      scope.row.allowMonths
                    }}</span>
                    <span v-show="scope.row.allowSuppTypeCode == '10005002'">{{
                      scope.row.earliestAllowYearMonth
                    }}</span>
                  </template>
                </el-table-column>
                <el-table-column
                  prop="defaultInsured"
                  label="默认不参保"
                  width="120"
                >
                  <template slot-scope="scope">
                    <span v-show="scope.row.defaultInsured == '1'">是</span>
                    <span v-show="scope.row.defaultInsured == '0'">否</span>
                  </template>
                </el-table-column>
                <el-table-column
                  prop="acrossMonth"
                  label="不允许跨月缴补"
                  width="120"
                >
                  <template slot-scope="scope">
                    <span v-show="scope.row.acrossMonth == '0'">是</span>
                    <span v-show="scope.row.acrossMonth == '1'">否</span>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="80">
                  <template slot-scope="scope">
                    <el-popconfirm
                      @confirm="delRow(scope.row, scope.$index)"
                      title="确定删除吗？"
                    >
                      <el-button slot="reference" type="text" class="del--text"
                        >删除</el-button
                      >
                    </el-popconfirm>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-form>
          <div class="drawer-footer-buts" v-if="!formData.isDisabled">
            <el-button class="btn--border-blue s-btn mr-0" @click="doClose"
              >取消</el-button
            >
            <el-button
              type="primary"
              class="s-btn ml-12"
              :loading="formData.btnLoading"
              @click="handleValid"
              >确认</el-button
            >
          </div>
        </div>
      </div>
    </el-drawer>
  </div>
</template>
<script>
import { format } from 'path'
import addrSelector from '../../../components/common/addrSelector.vue'
import dTable from '../../../components/common/table'
import moment from 'moment'
export default {
  name: 'paymentOfRulesDrawer',
  components: {
    addrSelector,
    dTable,
  },
  props: {
    newSocivalForm: {
      type: String,
      default: '',
    },
    payOfoptions: {
      type: Array,
      default: () => [],
    },
    payOfoptionsDanger: {
      type: Array,
      default: () => [],
    },
    visible: {
      type: Boolean,
      default: false,
    },
  },
  watch: {
    newSocivalForm: {
      handler(val) {
        if (val) {
          Object.assign(this.formData, JSON.parse(val))
        }
      },
      deep: true,
      immediate: true,
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
  data() {
    return {
      allowMonthsDate: null,
      popVisible: false,
      formData: {
        addrId: undefined,
        addrName: undefined,
        socialAllowSupp: undefined,
        rules: [],
      },
      isAcrossMonth: false,
      isDefaultInsured: false,
      isAcrossMontChecked: false,
      isJoin: false,
      addDrawerRules: false,
      allAddr: [],
      sacivalRulesForm: {
        itemName: undefined,
        itemCode: undefined,
        allowSuppTypeName: undefined,
        allowSuppTypeCode: '10005001',
        allowMonths: undefined,
        defaultInsured: 0,
        acrossMonth: 1,
        earliestAllowYearMonth: undefined,
      },
      opopList: [],
    }
  },
  created() {},
  mounted() {
    this.getAddr()
  },

  methods: {
    handleAllowMonths() {
      if (this.allowMonthsDate) {
        this.sacivalRulesForm.earliestAllowYearMonth = moment(
          this.allowMonthsDate
        ).format('YYYY-MM')
        // this.$refs.sacivalRulesForm.clearValidate('earliestAllowYearMonth')
      }
    },
    delRow(row, index) {
      this.formData.rules.splice(index, 1)
    },
    beforeAdd() {
      if (this.sacivalRulesForm.allowSuppTypeCode == '10005003') {
        this.sacivalRulesForm.allowMonths = undefined
        this.$refs.sacivalRulesForm.clearValidate('allowMonths')
      }
      let newArray = JSON.parse(JSON.stringify(this.formData.rules))
      this.opopList = []
      newArray.forEach((val) => {
        this.opopList.push(val.itemName)
      })
      if (this.opopList.indexOf(this.sacivalRulesForm.itemName) > -1) {
        this.$message.warning(`已存在此险种的补缴规则，不允许重复`)
        return false
      } else {
        Object.assign(this.sacivalRulesForm, { addrId: this.formData.addrId })
        this.formData.rules.push(
          JSON.parse(JSON.stringify(this.sacivalRulesForm))
        )
        this.addDrawerRules = false
        setTimeout(() => {
          this.handleFormNull()
          this.$refs.sacivalRulesForm.resetFields()
        }, 1000)
      }
    },
    handleAdd() {
      this.$refs.sacivalRulesForm.validate((valid) => {
        if (valid) {
          this.beforeAdd()
        } else {
          return false
        }
      })
    },
    handleCancel() {
      this.addDrawerRules = false
      setTimeout(() => {
        this.handleFormNull()
        this.$refs.sacivalRulesForm.resetFields()
      }, 1000)
    },
    handleIsAcrossMonth(val) {
      if (val == true) {
        this.sacivalRulesForm.acrossMonth = 0
        this.isAcrossMontChecked = true
      } else {
        this.sacivalRulesForm.acrossMonth = 1
        this.isAcrossMontChecked = false
      }
    },
    handleIsJoin(val) {
      if (val == true) {
        this.sacivalRulesForm.defaultInsured = 1
        this.isJoin = true
      } else {
        this.sacivalRulesForm.defaultInsured = 0
        this.isJoin = false
      }
    },
    handleFormNull() {
      this.sacivalRulesForm = {
        itemName: undefined,
        itemCode: undefined,
        allowSuppTypeName: undefined,
        allowSuppTypeCode: '10005001',
        allowMonths: undefined,
        defaultInsured: 0,
        acrossMonth: 1,
        earliestAllowYearMonth: undefined,
      }
      this.isJoin = false
      this.isAcrossMontChecked = false
      this.isDefaultInsured = false
      this.isAcrossMonth = false
    },
    changeAllowSuppType(val) {
      if (val) {
        let jsonVal = JSON.parse(val)
        this.sacivalRulesForm.allowSuppTypeCode = jsonVal.dictCode
        this.sacivalRulesForm.allowSuppTypeName = jsonVal.dictName
        // if (this.sacivalRulesForm.allowSuppTypeCode !== '10005001') {
        //   this.$refs.sacivalRulesForm.clearValidate('allowMonths')
        // }
      }
    },
    changeSacivalRulesForm(val) {
      if (val) {
        let jsonVal = JSON.parse(val)
        this.sacivalRulesForm.itemCode = jsonVal.dictCode
        this.sacivalRulesForm.itemName = jsonVal.dictName
      }
    },
    handleAddDrawer() {
      this.addDrawerRules = true
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
    handleFormdata() {
      //清空表单
      this.formData = {
        addrId: undefined,
        addrName: undefined,
        socialAllowSupp: undefined,
        rules: [],
      }
      this.$refs.formData.resetFields()
    },
    doClose() {
      this.localVisible = false
      this.handleFormNull()
      this.handleFormdata()
      this.$emit('handleNew-socivalForm')
    },
    //验证，确定提交保存
    handleValid() {
      this.$refs.formData.validate((valid) => {
        if (valid) {
          if (
            this.formData.socialAllowSupp == 1 &&
            this.formData.rules.length == 0
          ) {
            this.$message.warning('请添加社保补缴规则')
            return false
          }
          if (this.formData.socialAllowSupp == 0) {
            this.formData.rules = []
          }
          this.$http({
            url: 'policy/declareAddr/saveSocialSuppRule',
            method: 'post',
            data: this.formData,
          }).then(({ data }) => {
            // this.$message.success('保存成功')
            this.localVisible = false
            this.$emit('handleNew-socivalForm')
            this.handleFormNull()
            this.handleFormdata()
            this.$emit('getTable-data') //刷新列表
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
// .clite-drawer {
//   /deep/ .el-drawer__header {
//     display: inline-block;
//     position: relative;
//     height: 64px;
//     line-height: 64px;
//     padding: 0;
//     margin-bottom: 0;
//     font-family: Microsoft YaHei;
//     font-weight: 400;
//     color: #606266;
//     font-size: 18px;
//     background-color: #edf0f7;
//   }
// }
/deep/ .spl-filter-drawer {
  width: 720px !important;
}
/deep/ .new-loader .el-drawer__header {
  display: flex;
  position: relative;
  height: 64px;
  line-height: 64px;
  padding: 0px;
  margin-bottom: 0;
  padding-left: 32px;
  font-family: Microsoft YaHei;
  font-weight: 400;
  color: #606266;
  font-size: 18px;
  background-color: #edf0f7;
  // display: none;
}
/deep/ .el-drawer__close-btn {
  position: absolute;
  height: 100%;
  right: 20px;
  top: -1px;
  .el-dialog__close {
    font-size: 22px;
    font-weight: bold;
  }
}
.text-button {
  display: flex;
  justify-content: center;
}
.del--text {
  cursor: pointer;
  color: red;
}
</style>
