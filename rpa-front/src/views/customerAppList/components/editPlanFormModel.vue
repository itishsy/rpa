<template>
  <div>
    <el-dialog title="编辑计划"
               :visible.sync="show"
               width="600px"
               :close-on-click-modal="false"
               :before-close="cancel"
               class="cust-dialog" :modal-append-to-body="false" :append-to-body="true">
      <div @click="closeTag">
        <div>
          <el-form ref="popoverForm" :model="formData" label-width="120px" :rules="rules">
            <el-form-item label="流程名称:" prop="flowName">
              <div style="min-width:260px;">{{ formData.flowName }}</div>
            </el-form-item>
            <el-form-item label="计划类型:" prop="planType">
              <el-radio-group v-model="formData.planType">
                <el-radio :label="2">自定义</el-radio>
                <el-radio :label="1">引用上级计划</el-radio>
              </el-radio-group>
            </el-form-item>
            <div v-if="this.formData.planType !== 1">
              <div style="display: flex">
                <el-form-item label="执行周期:" prop="date1">
                  <el-select v-model="formData.date1" placeholder="请选择" style="width: 100px" value-key="id"
                             clearable filterable>
                    <el-option :label="item + '号'" :value="item" v-for="(item, index) in getDate1"
                               :key="index"></el-option>
                  </el-select>
                </el-form-item>
                <span style="padding: 0px 20px; margin-top: 10px">至</span>
                <el-form-item prop="date2" label-width="0px">
                  <el-select v-model="formData.date2" placeholder="请选择" style="width: 100px" value-key="id"
                             clearable filterable>
                    <el-option :label="item + '号'" :value="item" v-for="(item, index) in getDate2"
                               :key="index"></el-option>
                  </el-select>
                </el-form-item>
              </div>
              <el-form-item label="执行时间:" prop="timeType">
                <el-radio-group v-model="formData.timeType">
                  <el-radio :label="1">连续时间区间</el-radio>
                  <el-radio :label="2">固定时间点</el-radio>
                  <el-radio :label="3">实时</el-radio>
                </el-radio-group>
              </el-form-item>
              <div v-if="formData.timeType !== 3">
                <div v-if="formData.timeType === 1" style="display: flex">
                  <el-form-item label="选择区间:" prop="time1">
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
                  <el-form-item label="添加时间点:" prop="timeArr">
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
                      <el-popover placement="bottom" width="300" v-model="visible">
                        <div style="display:flex;align-items:center">
                          <el-select v-model="formData.selectTimeArr" placeholder="请选择" style="width: 180px" multiple
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
                            display: inline-block;margin-left: 10px;
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
        <el-button @click="cancel" size="small" class="w-60 mr-10">取 消</el-button>
        <el-button type="primary" @click="confirm" size="small" class="w-60">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
export default {
  props: {},
  data () {
    return {
      show: false,
      visible: false,
      rules: {
        timeType: [
          { required: true, message: '请选择执行时间', trigger: 'change' }
        ],
        flowType: [
          { required: true, message: '请选择流程类型', trigger: 'change' }
        ],
        date1: [
          { required: true, message: '请选择执行周期', trigger: 'change' }
        ],
        date2: [
          { required: true, message: '请选择执行周期', trigger: 'change' }
        ],
        time1: [{ required: true, message: '请选择区间', trigger: 'change' }],
        time2: [{ required: true, message: '请选择区间', trigger: 'change' }],
        timeArr: [
          {
            required: true,
            message: '请选择时间点',
            trigger: 'change',
            type: 'array'
          }
        ],
        planType: [
          { required: true, message: '请选择计划类型', trigger: 'change' }
        ]
      },
      formData: {
        date1: '',
        date2: '',
        timeType: 1,
        time1: '',
        time2: '',
        flowName: '',
        timeArr: [],
        selectTimeArr: [],
        planType: ''
      },
      dateArr: [],
      timeArr: [],
      rowData: null
    }
  },
  computed: {
    getDate1 () {
      if (this.formData.date2 != '') {
        var index = this.dateArr.indexOf(this.formData.date2)
        return this.dateArr.slice(0, index)
      }
      return this.dateArr
    },
    getDate2 () {
      if (this.formData.date1 != '') {
        var index = this.dateArr.indexOf(this.formData.date1)
        return this.dateArr.slice(index, this.dateArr.length)
      }
      return this.dateArr
    },
    getTime1 () {
      if (this.formData.time2 !== '') {
        var index = this.timeArr.indexOf(this.formData.time2)
        if (index == 0) {
          index = 1
        }
        return this.timeArr.slice(0, index)
      }
      return this.timeArr
    },
    getTime2 () {
      if (this.formData.time1 !== '') {
        var index = this.timeArr.indexOf(this.formData.time1)
        return this.timeArr.slice(index, this.timeArr.length)
      }
      return this.timeArr
    }
  },
  watch: {},
  created () {
    for (let i = 1; i <= 31; i++) {
      this.dateArr.push(i)
    }
    for (let i = 0; i <= 23; i++) {
      this.timeArr.push(i)
    }
  },
  mounted () {
  },
  methods: {
    init (row) {
      this.rowData = row
      this.cancel()
      this.formData.planType = row.taskType
      this.formData.timeType = row.execStyle
      this.formData.id = row.id
      var arr1 = row.execCycle.split('-')
      this.formData.date1 = Number(arr1[0])
      this.formData.date2 = Number(arr1[1])
      var arr2 = []
      if (this.formData.timeType === 1) {
        arr2 = row.execAreaTime.split('-')
        this.formData.time1 = parseInt(arr2[0].split(':')[0], 10)
        this.formData.time2 = parseInt(arr2[1].split(':')[0], 10)
      } else if (this.formData.timeType === 2) {
        arr2 = row.execAreaTime.split(',')
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
      this.formData.flowName = row.flowName
      this.$nextTick(() => {
        this.show = true
      })
    },
    closeTag () {
      this.visible = false
    },
    // 添加流程
    addProcess () {
      this.dialogVisible = true
    },
    // 取消添加流程
    cancel () {
      this.formData = this.$options.data().formData
      this.visible = false
      this.show = false
      this.$nextTick(() => {
        if (this.$refs.popoverForm) {
          this.$refs.popoverForm.clearValidate()
        }
      })
    },
    // 确定添加流程
    confirm (type) {
      this.$refs.popoverForm.validate((valid) => {
        if (valid) {
          this.editProcess()
        }
      })
    },
    editProcess () {
      var obj = {}
      let row = this.rowData
      if (this.formData.planType === 1) {
        // 引用上级计划
        obj = {
          appCode: row.appCode,
          flowCode: row.flowCode,
          taskCode: row.taskCode,
          taskType: this.formData.planType
        }
      } else if (this.formData.timeType === 3) {
        // 自定义-实时
        obj = {
          appCode: row.appCode,
          flowCode: row.flowCode,
          taskCode: row.taskCode,
          taskType: this.formData.planType,
          execCycle: this.formData.date1 + '-' + this.formData.date2,
          execStyle: this.formData.timeType
        }
      } else {
        var execAreaTime = ''
        if (this.formData.timeType === 1) {
          var time1 = this.formData.time1 < 10 ? '0' + this.formData.time1 + ':00' : this.formData.time1 + ':00'
          var time2 = this.formData.time2 < 10 ? '0' + this.formData.time2 + ':00' : this.formData.time2 + ':00'
          execAreaTime = time1 + '-' + time2
        } else if (this.formData.timeType === 2) {
          execAreaTime = this.$global.deepcopyArray(this.formData.timeArr)
          execAreaTime.forEach((item, index) => {
            execAreaTime[index] = item < 10 ? '0' + item + ':00' : item + ':00'
          })
          execAreaTime = execAreaTime.join(',')
        }
        obj = {
          appCode: row.appCode,
          flowCode: row.flowCode,
          taskCode: row.taskCode,
          execAreaTime,
          execCycle: this.formData.date1 + '-' + this.formData.date2,
          execStyle: this.formData.timeType,
          taskType: this.formData.planType
        }
      }
      this.showLoading()
      this.$http({
        url: `/robot/app/client/editRobotTaskSchedule`,
        method: 'post',
        data: obj
      }).then((res) => {
        console.log(res)
        this.closeLoading()
        if (res.data.code == 200) {
          this.show = false
          this.$message.success('操作成功')
          this.$nextTick(function () {
            this.cancel()
          })
          this.$emit('refesh')
        }
      })
        .catch((err) => {
          this.closeLoading()
          this.$message.error('接口错误，请稍后再试')
        })
    },
    openAddTag () {
      setTimeout(() => {
        this.visible = true
      }, 200)
    },
    removeTime (index) {
      this.formData.timeArr.splice(index, 1)
      this.formData.selectTimeArr = this.$global.deepcopyArray(this.formData.timeArr)
      this.$refs.popoverForm.validateField('timeArr')
    },
    cancalSelectTimeArr () {
      this.visible = false
    },
    confirmSelectTimeArr () {
      this.formData.selectTimeArr.sort((a, b) => {
        return a - b
      })
      this.formData.timeArr = this.$global.deepcopyArray(this.formData.selectTimeArr)
      this.$refs.popoverForm.validateField('timeArr')
      this.visible = false
    },
    showLoading (target) {
      this.loading = this.$loading({
        target: target || document.body,
        lock: true,
        text: '加载中',
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
