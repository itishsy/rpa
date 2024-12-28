<template>
  <div class="spl-container">
    <breadcrumb :data="pathData"></breadcrumb>
    <div class="pl-20 pr-20"></div>
    <div class="table-box">
      <div>
        <div style="display: flex; justify-content: space-between">
          <div style="font-size: 18px; font-weight: bold">
            通用计划
            <span style="font-size: 12px; font-weight: normal; color: #333">条件下所有的流程统一时间执行</span>
          </div>
        </div>
        <div class="border-box">
          <div v-if="!currencyPlanData.id">请先设置通用计划</div>
          <div class="border-box" v-else>{{ setCurrencyData }}</div>
        </div>
      </div>
      <div style="margin-top: 20px">
        <div style="display: flex; justify-content: space-between">
          <div style="font-size: 18px; font-weight: bold">
            流程计划
            <span style="font-size: 12px; font-weight: normal; color: #333">维护主流程的执行计划</span>
          </div>
          <div @click="addPlan" style="color: #3e82ff; cursor: pointer" attr="popover">+ 添加</div>
        </div>
        <div class="border-box">
          <el-form ref="tableForm" :model="table" label-width="0px" :rules="rules">
            <el-table
              :data="table.tableData"
              border
              header-cell-class-name="table-header"
              style="width: 100%"
              :max-height="tableHeight"
              ref="table"
            >
              <el-table-column fixed="left" label="执行顺序" width="80" align="center" prop="execOrder">
                <template scope="scope"><span>{{ scope.row.execOrder }}</span></template>
              </el-table-column>
              <el-table-column prop="flowName" label="流程名称"></el-table-column>
              <el-table-column prop="schedule" label="执行计划">
                <template scope="scope">
                  <span>{{ scope.row.robotAppSchedule.schedule }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="customerName" label="编辑人" width="100"></el-table-column>
              <el-table-column prop="editTme" label="编辑时间" width="200">
                <template scope="scope">
                  <span>{{
                      scope.row.robotAppSchedule.editTime ? moment(scope.row.robotAppSchedule.editTime).format('YYYY-MM-DD HH:mm:ss') : ''
                    }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="control" label="操作" min-width="150">
                <template scope="scope">
                  <div style="display: flex">
                    <div class="control-btn" style="color: #6b6b6b" @click="moveDown(scope.row, scope.$index)"
                         v-if="scope.$index != table.tableData.length - 1">
                      下移
                    </div>
                    <div class="control-btn" style="color: #6b6b6b" @click="moveUp(scope.row, scope.$index)"
                         v-if="scope.$index != 0">
                      上移
                    </div>
                    <div class="control-btn" style="color: #3e82ff" @click="edit(scope.row, scope.$index)">
                      编辑
                    </div>
                    <div class="control-btn" style="color: red" @click="remove(scope.row, scope.$index)">
                      移除
                    </div>
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </el-form>
        </div>
      </div>
    </div>

    <el-dialog :title="planType == 'currencyProcess'
              ? '设置通用计划'
              : planType == 'addProcess'
              ? '添加计划'
              : '编辑计划'"
               :visible.sync="visible2"
               width="600px"
               :close-on-click-modal="false"
               :before-close="cancel"
               class="cust-dialog">
      <div @click="closeTag">
        <div style="margin-top: 20px">
          <el-form ref="popoverForm" :model="formData" label-width="120px" :rules="rules">
            <el-form-item label="流程名称：" prop="flowCode" v-if="planType == 'editProcess'">
              <div style="min-width:260px;">{{ formData.title }}</div>
            </el-form-item>
            <el-form-item label="流程名称：" prop="flowCode" v-if="planType == 'addProcess'">
              <el-select v-model="formData.flowCode" placeholder="请选择" value-key="id" style="width: 255px" clearable
                         filterable>
                <el-option :label="item.flowName" :value="item.flowCode" v-for="(item, index) in processData"
                           :key="index"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="计划类型：" prop="planType" v-if="planType == 'addProcess' || planType == 'editProcess'">
              <el-radio-group v-model="formData.planType">
                <el-radio label="1">通用计划</el-radio>
                <el-radio label="2">自定义计划</el-radio>
              </el-radio-group>
            </el-form-item>
            <div v-if="this.formData.planType != 1">
              <div style="display: flex">
                <el-form-item label="执行周期：" prop="date1">
                  <el-select v-model="formData.date1" placeholder="请选择" style="width: 100px" value-key="id" clearable filterable>
                    <el-option :label="item + '号'" :value="item" v-for="(item, index) in getDate1"
                               :key="index"></el-option>
                  </el-select>
                </el-form-item>
                <span style="padding: 0px 20px; margin-top: 10px">至</span>
                <el-form-item prop="date2" label-width="0px">
                  <el-select v-model="formData.date2" placeholder="请选择" style="width: 100px" value-key="id" clearable filterable>
                    <el-option :label="item + '号'" :value="item" v-for="(item, index) in getDate2"
                               :key="index"></el-option>
                  </el-select>
                </el-form-item>
              </div>
              <el-form-item label="执行时间：" prop="timeType">
                <el-radio-group v-model="formData.timeType">
                  <el-radio label="1">连续时间区间</el-radio>
                  <el-radio label="2">固定时间点</el-radio>
                  <el-radio label="3">实时</el-radio>
                </el-radio-group>
              </el-form-item>
              <div v-if="formData.timeType != '3'">
                <div v-if="formData.timeType == '1'" style="display: flex">
                  <el-form-item label="选择区间：" prop="time1">
                    <el-select v-model="formData.time1" placeholder="请选择" style="width: 100px" clearable filterable>
                      <el-option :label="item + '时'" :value="item" v-for="(item, index) in getTime1"
                                 :key="index"></el-option>
                    </el-select>
                  </el-form-item>
                  <span style="padding: 0px 20px; margin-top: 10px">至</span>
                  <el-form-item label-width="0px" prop="time2">
                    <el-select v-model="formData.time2" placeholder="请选择" style="width: 100px" clearable filterable>
                      <el-option
                        :label="item + '时'"
                        :value="item"
                        v-for="(item, index) in getTime2"
                        :key="index"
                      ></el-option>
                    </el-select>
                  </el-form-item>
                </div>
                <div v-else>
                  <el-form-item label="添加时间点：" prop="timeArr">
                    <div style="
                        display: flex;
                        align-items: center;
                        flex-wrap: wrap;
                      ">
                      <div v-for="(item, index) in formData.timeArr" :key="item" class="tag-box">
                        <div style="
                            padding: 5px;
                            border: 1px solid #ddd;
                            line-height: 1;
                            margin: 3px 5px;
                          ">
                          {{ (item < 10 ? '0' + item : item) + ':00' }}
                        </div>
                        <i class="el-icon-remove remove-tag" @click="removeTime(index)"></i>
                      </div>
                      <el-popover placement="bottom" width="250" v-model="visible">
                        <div style="display:flex;align-items:center">
                          <el-select v-model="formData.selectTimeArr" placeholder="请选择" style="width: 120px" multiple
                                     collapse-tags>
                            <el-option :label="item + '时'" :value="item" v-for="(item, index) in timeArr"
                                       :key="index"></el-option>
                          </el-select>
                          <div style="
                              text-align: right;
                              display:flex;
                              margin-left:10px;
                            ">
                            <el-button size="mini" @click="cancalSelectTimeArr">取消</el-button>
                            <el-button type="primary" size="mini" @click="confirmSelectTimeArr">确定</el-button>
                          </div>
                        </div>
                        <span slot="reference" style="
                            color: #3e82ff;
                            font-size: 12px;
                            cursor: pointer;
                            display: inline-block;
                          " @click.stop="openAddTag">添加</span>
                      </el-popover>
                    </div>
                  </el-form-item>
                </div>
              </div>
            </div>
          </el-form>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="cancel" size="small" class="w-60">取 消</el-button>
        <el-button type="primary" @click="confirm" size="small" class="w-60">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import moment from 'moment'

export default {
  data() {
    return {
      moment,
      visible: false,
      visible2: false,
      pathData: [],
      table: {
        tableData: []
      },
      rules: {
        processName: [
          {required: true, message: '请输入流程名称', trigger: 'change'},
          {
            required: true,
            trigger: 'change',
            validator: this.checkProcessName
          }
        ],
        timeType: [
          {required: true, message: '请选择执行时间', trigger: 'change'}
        ],
        flowType: [
          {required: true, message: '请选择流程类型', trigger: 'change'}
        ],
        date1: [
          {required: true, message: '请选择执行周期', trigger: 'change'}
        ],
        date2: [
          {required: true, message: '请选择执行周期', trigger: 'change'}
        ],
        time1: [{required: true, message: '请选择区间', trigger: 'change'}],
        time2: [{required: true, message: '请选择区间', trigger: 'change'}],
        timeArr: [
          {
            required: true,
            message: '请选择时间点',
            trigger: 'change',
            type: 'array'
          }
        ],
        planType: [
          {required: true, message: '请选择计划类型', trigger: 'change'}
        ],
        flowCode: [
          {required: true, message: '请选择流程名称', trigger: 'change'}
        ]
      },
      formData: {
        date1: '',
        date2: '',
        timeType: '1',
        time1: '',
        time2: '',
        title: '',
        processName: '',
        timeArr: [],
        selectTimeArr: [],
        planType: '',
        flowCode: '',
        id: ''
      },
      currencyPlanData: {},
      tableIndex: 0,
      loading: null,
      uploadErrMsg: '',
      dialogVisible: false,
      appCode: '',
      taskCode: '',
      processOption: [],
      disabledBussinssType: false,
      dateArr: [],
      timeArr: [],
      planType: '', // 设置通用计划，还是添加流程计划currencyProcess通用 addProcess添加流程计划，editProcess编辑通用计划
      processData: [] // 流程配置列表
    }
  },
  computed: {
    tableHeight() {
      return this.$global.bodyHeight - 330 + 'px'
    },
    getDate1() {
      if (this.formData.date2 != '') {
        var index = this.dateArr.indexOf(this.formData.date2)
        return this.dateArr.slice(0, index)
      }
      return this.dateArr
    },
    getDate2() {
      if (this.formData.date1 != '') {
        var index = this.dateArr.indexOf(this.formData.date1)
        return this.dateArr.slice(index, this.dateArr.length)
      }
      return this.dateArr
    },
    getTime1() {
      if (this.formData.time2 !== '') {
        var index = this.timeArr.indexOf(this.formData.time2)
        if (index == 0) {
          index = 1
        }
        return this.timeArr.slice(0, index)
      }
      return this.timeArr
    },
    getTime2() {
      if (this.formData.time1 !== '') {
        var index = this.timeArr.indexOf(this.formData.time1)
        return this.timeArr.slice(index, this.timeArr.length)
      }
      return this.timeArr
    },
    getTime3() {
      var arr = this.timeArr
      if (this.formData.timeArr.length > 0) {
        for (let i = 0; i < arr.length; i++) {
          const item = arr[i]
          var index = this.formData.timeArr.indexOf(item)
          arr.splice(index, 1)
          i--
        }
        return arr
      }
      return this.timeArr
    },
    setCurrencyData() {
      if (this.currencyPlanData.id) {
        var str = '当月' + this.currencyPlanData.date1 + '号~' + this.currencyPlanData.date2 + '号：'
        var execAreaTime = ''
        if (this.currencyPlanData.timeType == 1) {
          var time1 = this.currencyPlanData.time1 < 10 ? '0' + this.currencyPlanData.time1 + ':00' : this.currencyPlanData.time1 + ':00'
          var time2 = this.currencyPlanData.time2 < 10 ? '0' + this.currencyPlanData.time2 + ':00' : this.currencyPlanData.time2 + ':00'
          str += time1 + '-' + time2
        } else if (this.currencyPlanData.timeType == 2) {
          execAreaTime = this.deepCopy(this.currencyPlanData.timeArr)
          execAreaTime.forEach((item, index) => {
            execAreaTime[index] = item < 10 ? '0' + item + ':00' : item + ':00'
          })
          execAreaTime = execAreaTime.join('，')
          str += execAreaTime
        } else {
          str += '实时'
        }
        return str
      } else {
        return ''
      }
    }
  },
  created() {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = this.$global.deepcopyArray(tabs[this.$route.meta.parentPath])
    }
    this.pathData.push({name: '执行计划'})
    if (this.$route.query.taskCode) {
      this.taskCode = this.$route.query.taskCode
    }
    if (this.$route.query.appCode) {
      this.appCode = this.$route.query.appCode
    }

    this.getPlanInfo()
    this.getCurrencyPlan()
    this.getAllFlow()
    for (let i = 1; i <= 31; i++) {
      this.dateArr.push(i)
    }
    for (let i = 0; i <= 23; i++) {
      this.timeArr.push(i)
    }
    this.getProcessInfo()
  },
  methods: {
    removeTime(index) {
      this.formData.timeArr.splice(index, 1)
      this.formData.selectTimeArr = this.deepCopy(this.formData.timeArr)
      this.$refs.popoverForm.validateField('timeArr')
    },
    cancalSelectTimeArr() {
      this.visible = false
    },
    confirmSelectTimeArr() {
      this.formData.selectTimeArr.sort((a, b) => {
        return a - b
      })
      this.formData.timeArr = this.deepCopy(this.formData.selectTimeArr)
      this.$refs.popoverForm.validateField('timeArr')
      this.visible = false
    },
    // 设置通用计划
    async currencyPlan(event) {
      await this.getCurrencyPlan()
      // 方法
      this.planType = 'currencyProcess'
      this.visible2 = true
    },
    // 添加流程计划
    addPlan() {
      this.planType = 'addProcess'
      this.$nextTick(() => {
        this.cancel()
        this.visible2 = true
      })
    },
    openAddTag() {
      setTimeout(() => {
        this.visible = true
      }, 200)
    },
    closeTag() {
      this.visible = false
    },
    // 获取流程计划列表
    getPlanInfo() {
      this.showLoading()
      this.$http({
        url: 'robot/flow/taskScheduleList',
        method: 'get',
        params: {
          appCode: this.appCode,
          taskCode: this.taskCode
        }
      }).then((res) => {
        this.closeLoading()
        res.data.data.sort((a, b) => {
          return a.execOrder - b.execOrder
        })
        this.table.tableData = res.data.data
      })
        .catch((err) => {
          this.closeLoading()
        })
    },
    // 获取通用计划
    async getCurrencyPlan() {
      this.showLoading()
      await this.$http({
        url: 'robot/flow/generalPlanEcho',
        method: 'post',
        params: {
          appCode: this.appCode
        }
      }).then(({data}) => {
        this.closeLoading()
        if (data.data && data.code == 200) {
          this.formData.timeType = data.data.execStyle + ''
          var arr1 = data.data.execCycle.split('-')
          this.formData.date1 = Number(arr1[0])
          this.formData.date2 = Number(arr1[1])
          if (this.formData.timeType == 1) {
            var arr2 = data.data.execAreaTime.split('-')
            this.formData.time1 = parseInt(arr2[0].split(':')[0], 10)
            this.formData.time2 = parseInt(arr2[1].split(':')[0], 10)
          } else if (this.formData.timeType == 2) {
            var arr2 = data.data.execAreaTime.split(',')
            this.formData.timeArr = []
            this.formData.selectTimeArr = []
            arr2.forEach(item => {
              try {
                this.formData.timeArr.push(parseInt(item.split(':')[0], 10))
              } catch (err) {
                console.log(err)
              }
            })
            this.formData.selectTimeArr = this.formData.timeArr
          }
          this.formData.id = data.data.id
          this.currencyPlanData = this.deepCopy(this.formData)
          // console.log(this.formData)
        }
      }).catch((err) => {
        console.log(err)
        this.closeLoading()
        this.$message.error('获取通用计划错误，请稍后再试')
      })
    },
    getAllFlow() {
      this.$http({
        url: 'robot/flow/allFlow',
        method: 'post'
      })
        .then((res) => {
          this.closeLoading()
          this.processOption = res.data.data
        })
        .catch((err) => {
          this.closeLoading()
        })
    },
    // 添加流程
    addProcess() {
      this.dialogVisible = true
    },
    // 取消添加流程
    cancel() {
      this.formData = this.$options.data().formData
      this.visible = false
      this.visible2 = false
      this.$nextTick(() => {
        this.$refs.popoverForm.clearValidate()
      })
    },
    // 确定添加流程
    confirm(type) {
      this.$refs.popoverForm.validate((valid) => {
        console.log(valid)
        if (valid) {
          console.log('添加流程')
          console.log(this.formData)
          if (this.planType == 'currencyProcess') {
            this.setCurrencyPlan()
          } else if (this.planType == 'addProcess') {
            this.setProcess()
          } else if (this.planType == 'editProcess') {
            this.editProcess()
          }
        } else {
          console.log(valid)
        }
      })
    },
    // 设置通用计划
    setCurrencyPlan() {
      var execAreaTime = ''
      if (this.formData.timeType == 1) {
        var time1 = this.formData.time1 < 10 ? '0' + this.formData.time1 + ':00' : this.formData.time1 + ':00'
        var time2 = this.formData.time2 < 10 ? '0' + this.formData.time2 + ':00' : this.formData.time2 + ':00'
        execAreaTime = time1 + '-' + time2
      } else if (this.formData.timeType == 2) {
        execAreaTime = this.deepCopy(this.formData.timeArr)
        execAreaTime.forEach((item, index) => {
          execAreaTime[index] = item < 10 ? '0' + item + ':00' : item + ':00'
        })
        execAreaTime = execAreaTime.join(',')
      }
      this.showLoading()
      this.$http({
        url: '/robot/flow/updateGeneralPlan',
        method: 'post',
        params: {
          appCode: this.appCode,
          execCycle: this.formData.date1 + '-' + this.formData.date2,
          execAreaTime,
          execStyle: this.formData.timeType
        }
      })
        .then((res) => {
          this.closeLoading()
          if (res.data.code == 200) {
            this.$message.success('操作成功')
            this.visible2 = false
            this.$nextTick(function () {
              this.cancel()
            })
            this.getCurrencyPlan()
          }
        })
        .catch((err) => {
          this.closeLoading()
          this.$message.error('接口错误，请稍后再试')
        })
    },
    // 添加流程计划
    setProcess() {
      var formData = new FormData()
      var obj = {}
      if (this.formData.planType == '1') {
        obj = {
          taskType: this.formData.planType,
          flowCode: this.formData.flowCode,
          taskCode: this.taskCode
        }
      } else if (this.formData.timeType == '3') {
        obj = {
          execCycle: this.formData.date1 + '-' + this.formData.date2,
          execStyle: this.formData.timeType,
          flowCode: this.formData.flowCode,
          taskType: this.formData.planType,
          taskCode: this.taskCode
        }
      } else {
        var execAreaTime = ''
        if (this.formData.timeType == 1) {
          var time1 = this.formData.time1 < 10 ? '0' + this.formData.time1 + ':00' : this.formData.time1 + ':00'
          var time2 = this.formData.time2 < 10 ? '0' + this.formData.time2 + ':00' : this.formData.time2 + ':00'
          execAreaTime = time1 + '-' + time2
        } else if (this.formData.timeType == 2) {
          execAreaTime = this.deepCopy(this.formData.timeArr)
          execAreaTime.forEach((item, index) => {
            execAreaTime[index] = item < 10 ? '0' + item + ':00' : item + ':00'
          })
          execAreaTime = execAreaTime.join(',')
        }
        obj = {
          execAreaTime,
          execCycle: this.formData.date1 + '-' + this.formData.date2,
          execStyle: this.formData.timeType,
          flowCode: this.formData.flowCode,
          taskType: this.formData.planType,
          taskCode: this.taskCode
        }
      }
      this.showLoading()
      this.$http({
        url: '/robot/flow/addTaskSchedule?appCode=' + this.appCode,
        method: 'post',
        data: obj
      })
        .then((res) => {
          this.closeLoading()
          if (res && res.data.code == 200) {
            this.visible2 = false
            this.$message.success('操作成功')
            this.$nextTick(function () {
              this.cancel()
            })
            this.getPlanInfo()
          }
        })
        .catch((err) => {
          console.log(err)
          this.closeLoading()
        })
    },
    editProcess() {
      var formData = new FormData()
      var obj = {}
      if (this.formData.planType == '1') {
        obj = {
          taskType: this.formData.planType,
          flowCode: this.formData.flowCode,
          id: this.formData.id
        }
      } else if (this.formData.timeType == '3') {
        obj = {
          execCycle: this.formData.date1 + '-' + this.formData.date2,
          execStyle: this.formData.timeType,
          flowCode: this.formData.flowCode,
          taskType: this.formData.planType,
          id: this.formData.id,
          taskCode: this.taskCode
        }
      } else {
        var execAreaTime = ''
        if (this.formData.timeType == 1) {
          var time1 = this.formData.time1 < 10 ? '0' + this.formData.time1 + ':00' : this.formData.time1 + ':00'
          var time2 = this.formData.time2 < 10 ? '0' + this.formData.time2 + ':00' : this.formData.time2 + ':00'
          execAreaTime = time1 + '-' + time2
        } else if (this.formData.timeType == 2) {
          execAreaTime = this.deepCopy(this.formData.timeArr)
          execAreaTime.forEach((item, index) => {
            execAreaTime[index] = item < 10 ? '0' + item + ':00' : item + ':00'
          })
          execAreaTime = execAreaTime.join(',')
        }
        obj = {
          execAreaTime,
          execCycle: this.formData.date1 + '-' + this.formData.date2,
          execStyle: this.formData.timeType,
          flowCode: this.formData.flowCode,
          taskType: this.formData.planType,
          id: this.formData.id,
          taskCode: this.taskCode
        }
      }
      this.showLoading()
      this.$http({
        url: `/robot/flow/editTaskSchedule/${this.appCode}`,
        method: 'post',
        data: obj
      }).then((res) => {
        console.log(res)
        this.closeLoading()
        if (res.data.code == 200) {
          this.visible2 = false
          this.$message.success('操作成功')
          this.$nextTick(function () {
            this.cancel()
          })
          this.getPlanInfo()
        }
      })
        .catch((err) => {
          this.closeLoading()
          this.$message.error('接口错误，请稍后再试')
        })
    },
    // 操作栏，删除
    remove(row, index) {
      this.$confirm('是否确认删除该流程？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning'
      })
        .then(() => {
          this.showLoading()
          this.$http({
            url: '/robot/flow/removeTaskSchedule',
            method: 'post',
            params: {
              taskCode: this.taskCode,
              flowCode: row.robotAppSchedule.flowCode
            }
          }).then((res) => {
            this.closeLoading()
            if (res.data.code == 200) {
              this.getPlanInfo()
              this.$message.success(res.message ? res.message : '操作成功')
            }
          }).catch((err) => {
            this.closeLoading()
          })
        })
        .catch(() => {
        })
    },
    // 操作栏，上挪
    moveUp(row, index) {
      console.log(row)
      this.$nextTick(() => {
        this.$refs.table.doLayout()
        this.showLoading()
        this.$http({
          url: '/robot/flow/task/upOrDownMove',
          method: 'post',
          params: {
            appCode: this.appCode,
            taskCode: this.taskCode,
            flowCode: row.robotAppSchedule.flowCode,
            flag: 1,
          }
        })
          .then((res) => {
            this.closeLoading()
            if (res.data.code != 200) {
              return
            }
            if (index != 0) {
              this.table.tableData[index].execOrder -= 1
              this.table.tableData[index - 1].execOrder += 1
              this.table.tableData[index] = this.table.tableData.splice(
                index - 1,
                1,
                this.table.tableData[index]
              )[0]
            } else {
              this.table.tableData.push(this.table.tableData.shift())
            }
          })
          .catch((err) => {
            this.closeLoading()
          })
      })
    },
    // 操作栏，下移
    moveDown(row, index) {
      this.$nextTick(() => {
        this.showLoading()
        this.$refs.table.doLayout()
        this.$http({
          url: '/robot/flow/task/upOrDownMove',
          method: 'post',
          params: {
            appCode: this.appCode,
            taskCode: this.taskCode,
            flowCode: row.robotAppSchedule.flowCode,
            flag: 2,
          }
        })
          .then((res) => {
            this.closeLoading()
            if (res.data.code != 200) {
              return
            }
            if (index != this.table.tableData.length - 1) {
              this.table.tableData[index].execOrder += 1
              this.table.tableData[index + 1].execOrder -= 1
              this.table.tableData[index] = this.table.tableData.splice(
                index + 1,
                1,
                this.table.tableData[index]
              )[0]
            } else {
              this.table.tableData.unshift(
                this.table.tableData.splice(index, 1)[0]
              )
            }
          })
          .catch((err) => {
            this.closeLoading()
          })
      })
    },
    // 编辑
    edit(row, index) {
      this.cancel()
      this.showLoading()
      this.planType = 'editProcess'
      this.$http({
        url: '/robot/flow/getTaskSchedule',
        method: 'post',
        params: {
          taskCode: this.taskCode,
          flowCode: row.robotAppSchedule.flowCode
        }
      }).then(({data}) => {
        this.closeLoading()
        if (data.data && data.code == 200) {
          this.formData.planType = data.data.robotTaskSchedule.taskType + ''
          this.formData.timeType = data.data.robotTaskSchedule.execStyle + ''
          this.formData.id = data.data.robotTaskSchedule.id
          var arr1 = data.data.robotTaskSchedule.execCycle.split('-')
          this.formData.date1 = Number(arr1[0])
          this.formData.date2 = Number(arr1[1])
          if (this.formData.timeType == 1) {
            var arr2 = data.data.robotTaskSchedule.execAreaTime.split('-')
            this.formData.time1 = parseInt(arr2[0].split(':')[0], 10)
            this.formData.time2 = parseInt(arr2[1].split(':')[0], 10)
          } else if (this.formData.timeType == 2) {
            var arr2 = data.data.robotTaskSchedule.execAreaTime.split(',')
            this.formData.timeArr = []
            this.formData.selectTimeArr = []
            arr2.forEach(item => {
              try {
                this.formData.timeArr.push(parseInt(item.split(':')[0], 10))
              } catch (err) {
                console.log(err)
              }
            })
            this.formData.selectTimeArr = this.formData.timeArr
          }
          this.formData.id = data.data.robotTaskSchedule.id
          this.formData.flowCode = data.data.robotTaskSchedule.flowCode
          this.formData.title = data.data.flowName
          this.$nextTick(() => {
            this.visible2 = true
          })
        }
      }).catch((err) => {
        this.closeLoading()
      })
    },
    // 深克隆
    deepCopy(obj) {
      var result = Array.isArray(obj) ? [] : {}
      for (var key in obj) {
        if (Object.prototype.hasOwnProperty.call(obj, key)) {
          if (typeof obj[key] === 'object' && obj[key] !== null) {
            result[key] = this.deepCopy(obj[key]) // 递归复制
          } else {
            result[key] = obj[key]
          }
        }
      }
      return result
    },
    showLoading(target) {
      this.loading = this.$loading({
        target: target || document.body,
        lock: true,
        text: '加载中',
        spinner: 'el-icon-loading',
        background: 'rgba(255, 255, 255, 0.7)'
      })
    },
    closeLoading() {
      if (this.loading && this.loading.close) {
        this.loading.close()
      }
    },
    // 设置时间,时间转换
    formatDateTime(date, params, separator) {
      if (date == undefined || date == null || date == '') {
        return ''
      }
      var date = new Date(date.substring(0, 19))
      var Year = date.getFullYear()
      var Month =
        date.getMonth() + 1 < 10
          ? '0' + (date.getMonth() + 1)
          : date.getMonth() + 1
      var d = date.getDate() < 10 ? '0' + date.getDate() : date.getDate()
      var Hours = date.getHours() < 10 ? '0' + date.getHours() : date.getHours()
      var Minutes =
        date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes()
      var Seconds =
        date.getSeconds() < 10 ? '0' + date.getSeconds() : date.getSeconds()
      var over_time =
        Year +
        '/' +
        Month +
        '/' +
        d +
        ' ' +
        Hours +
        ':' +
        Minutes +
        ':' +
        Seconds
      //* **至此以上是将时间2020-03-18T01:57:23.000+0000转为正常时间格式，以下为将时间进行增加8小时解决时区差异的操作***
      var time = new Date(over_time)
      time.setTime(time.setHours(time.getHours()))
      var y = time.getFullYear()
      var m = this.addZero(time.getMonth() + 1)
      var d = this.addZero(time.getDate())
      var h = this.addZero(time.getHours() + 8)
      var minute = this.addZero(time.getMinutes())
      var second = this.addZero(time.getSeconds())
      var symbol = separator || '-'

      if (!params) {
        return y + '_' + m + '_' + d + '_' + h + ':' + minute + ':' + second
      }

      var arr = params.split('-')

      var result = ''
      for (var i = 0; i < arr.length; i++) {
        switch (arr[i]) {
          case 'y':
            result += y + (i != arr.length - 1 ? symbol : '')
            break
          case 'M':
            result += m + (i != arr.length - 1 ? symbol : '')
            break
          case 'd':
            result += d + (i != arr.length - 1 ? ' ' : '')
            break
          case 'h':
            result += h + (i != arr.length - 1 ? ':' : '')
            break
          case 'm':
            result += minute + (i != arr.length - 1 ? ':' : '')
            break
          case 's':
            result += second
            break
        }
      }
      return result
    },
    addZero(num) {
      return num < 10 ? '0' + num : num
    },
    // 流程配置，
    getProcessInfo() {
      this.$http({
        url: 'robot/flow/getScheduleFlow',
        method: 'post',
        params: {
          appCode: this.appCode
        }
      })
        .then((res) => {
          this.closeLoading()
          this.processData = res.data.data
        })
        .catch((err) => {
          this.closeLoading()
        })
    }
  }
}
</script>

<style lang="less" scoped>
.search-row {
  padding: 10px;
}

.table-box {
  padding: 20px;
}

/deep/ .table-header {
  background: #f5f5f5 !important;
  color: #444;
}

/deep/ .el-table__cell {
  border-bottom: 1px solid #ddd;
  border-right: 1px solid #ddd;
}

/deep/ .el-table__row {
  border-bottom: 1px solid #ddd;
  border-right: 1px solid #ddd;
}

/deep/ .el-table--border {
  border: 1px solid #ddd;
}

/deep/ .el-table .el-table__cell {
  padding: 9px 0;
}

.control-btn {
  margin-left: 10px;
  cursor: pointer;
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
.upload-file-box {
  // margin-top:10px;
  input[type='text'] {
    border: 1px solid #ddd;
    border-width: 1px;
    height: 26px;
    width: 285px;
    // position: absolute;
    // left: 50%;
    // transform: translateX(-50%);
    cursor: pointer;
    border-radius: 5px;
    box-sizing: border-box;
    padding: 0 10px;

    &:focus {
      outline: none;
    }
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
    cursor: pointer;
    color: #888;
    background: #fafafa;
    border: 1px solid #ddd;
    display: inline-block;
    box-sizing: border-box;
    position: absolute;
    opacity: 0;
    width: 280px;
    left: 50%;
    transform: translateX(-50%);
    display: none;

    input[type='file'] {
      position: absolute;
      font-size: 18px;
      right: 0;
      top: 0;
      opacity: 0;
      cursor: pointer;
      width: 280px;

      &:hover {
        color: #444;
        background: #eee;
        border-color: #ccc;
        text-decoration: none;
      }
    }
  }
}

.icon-query {
  position: absolute;
  right: 15px;
  cursor: pointer;
  height: 100%;
  font-size: 16px;
  top: 50%;
  transform: translateY(-50%);
}

/deep/ .el-drawer__body {
  padding-bottom: 0;
}

/deep/ .el-dialog__header {
  border-bottom: none !important;
}

.border-box {
  padding: 10px;
  border: 1px solid #ddd;
  margin-top: 10px;
}

.box-title {
  border-bottom: 1px solid #ddd;
  padding: 5px;
}

.tag-box {
  position: relative;
  cursor: pointer;

  .remove-tag {
    position: absolute;
    top: -6px;
    right: -3px;
    font-size: 18px;
    color: #f56c6c;
    cursor: pointer;
    display: none;
  }

  &:hover .remove-tag {
    display: block;
  }
}
</style>
