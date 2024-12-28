<template>
  <div>
    <el-drawer title="Ukey绑定申报账户" :visible.sync="show" :wrapperClosable="false" size="100%"
               custom-class="spl-filter-drawer detail-drawer" :show-close="true">
      <div style="padding: 0px 20px 20px 20px;">
        <div class="type-title clearfix" style="margin-top: 10px;"><span class="text">Ukey信息</span></div>
        <div class="pl-10 pt-20">
          <el-row>
            <el-col :span="12" class="row-item">
              <p class="lab">idVendor：</p>
              <p class="sel">{{ rowData.idVendor }}</p>
            </el-col>
            <el-col :span="12" class="row-item">
              <p class="lab">idProduct：</p>
              <p class="sel">{{ rowData.idProduct }}</p>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12" class="row-item">
              <p class="lab">baseHash：</p>
              <p class="sel">{{ rowData.baseHash }}</p>
            </el-col>
            <el-col :span="12" class="row-item">
              <p class="lab">contextHash：</p>
              <p class="sel">{{ rowData.contextHash }}</p>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12" class="row-item">
              <p class="lab">busNumber：</p>
              <p class="sel">{{ rowData.busNumber }}</p>
            </el-col>
            <el-col :span="12" class="row-item">
              <p class="lab">deviceAddress：</p>
              <p class="sel">{{ rowData.deviceAddress }}</p>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12" class="row-item">
              <p class="lab">portNumber：</p>
              <p class="sel">{{ rowData.portNumber }}</p>
            </el-col>
            <el-col :span="12" class="row-item">
              <p class="lab">busId：</p>
              <p class="sel">{{ rowData.busId }}</p>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24" class="row-item" style="align-items: center">
              <span class="label" style="flex-shrink: 0;width: 100px;text-align: right;">证书有效期：</span>
              <div style="display:flex;max-width:300px">
                <el-date-picker v-model="formData.certEffeStartTime" value-format="yyyy-MM-dd" style="width:50%;"
                                type="date" placeholder="选择时间"
                                :picker-options="getPickerOption('start', 'formData')" clearable></el-date-picker>
                <span class="lh-com" style="padding: 0 5px;">~</span>
                <el-date-picker v-model="formData.certEffeEndTime" value-format="yyyy-MM-dd" style="width:50%;"
                                type="date" placeholder="选择时间" :picker-options="getPickerOption('end', 'formData')"
                                clearable></el-date-picker>
              </div>
            </el-col>
            <el-col :span="24" class="row-item" style="align-items: center">
              <span class="label" style="flex-shrink: 0;width: 100px;text-align: right;">备注：</span>
              <el-input v-model.trim="formData.remark" placeholder="请输入备注" style="max-width: 300px" class="flex1"
                        clearable></el-input>
            </el-col>
          </el-row>
        </div>
        <div class="type-title mt-0 clearfix"><span class="text">申报账户信息</span></div>

        <div class="pl-10 pt-10">
          <el-form :inline="true" :model="formData" ref="tableData" label-width="100px">
            <div style="padding: 10px 20px;">
              <div class="mt-10">
                <table class="cust-table-border w-p100">
                  <thead>
                  <tr>
                    <th>应用名称</th>
                    <th>申报账户</th>
                    <th>申报系统</th>
                    <th>操作</th>
                  </tr>
                  </thead>
                  <tbody>
                  <tr v-for="(row, rowInd) in formData.tableData" :key="rowInd">
                    <td style="position: relative">
                      <el-form-item v-if="row.isNew || row.isEdit" label-width="0"
                                    :prop="'tableData.'+rowInd+'.appCode'"
                                    :rules="[{ required: true, message: '请选择', trigger: 'change' }]" class="w-p100">
                        <el-select v-model="row.appCode" placeholder="请选择应用名称" class="w-p100"
                                   @change="changeListTask(row.appCode,rowInd)" filterable clearable>
                          <el-option :label="item.appName" v-for="(item,index) in appList" :key="index"
                                     :value="item.appCode"></el-option>
                        </el-select>
                      </el-form-item>
                      <p class="row-text" v-else>
                        <span>{{ row.appName }}</span>
                      </p>
                    </td>
                    <td>
                      <el-form-item v-if="row.isNew || row.isEdit" label-width="0"
                                    :prop="'tableData.'+rowInd+'.taskCode'"
                                    :rules="[{ required: true, message: '请选择', trigger: 'change' }]" class="w-p100">
                        <el-select v-model="row.taskCode" placeholder="请选择申报账户" class="w-p100" filterable
                                   clearable>
                          <el-option :label="item.accountAndOrgName"
                                     v-for="(item,index) in declareAccountList[row.appCode]"
                                     :key="index" :value="item.taskCode"></el-option>
                        </el-select>
                      </el-form-item>
                      <p class="row-text" v-else>{{ row.companyName }}-{{ row.declareAccount }}</p>
                    </td>
                    <td>
                      <el-form-item v-if="row.isNew || row.isEdit" label-width="0"
                                    :prop="'tableData.'+rowInd+'.declareSystem'"
                                    :rules="[{ required: true, message: '请选择', trigger: 'change' }]" class="w-p100">
                        <el-select v-model="row.declareSystem" placeholder="请选择申报系统" class="w-p100" filterable
                                   clearable>
                          <el-option :label="item.dictName" v-for="(item,index) in declareSystemList[row.appCode]"
                                     :key="index" :value="item.dictCode"></el-option>
                        </el-select>
                      </el-form-item>
                      <p class="row-text" v-else>{{ row.declareSystemName }}</p>
                    </td>
                    <td>
                      <div>
                        <el-button type="text" size="small" class="text-red" @click="doRowDel(row, rowInd, 'next')">
                          解绑
                        </el-button>
                        <el-button type="text" size="small" class="text-blue" @click="doRowEdit('', rowInd, 'next')">
                          修改
                        </el-button>
                      </div>
                    </td>
                  </tr>
                  </tbody>
                </table>
                <div class="text-center pb-30">
                  <el-button class="btn--border-blue mt-20 w-200" @click="doRowAdd('', 'next')"><i
                    class="el-icon-plus"></i>添加
                  </el-button>
                </div>
              </div>
            </div>
          </el-form>
        </div>
        <div class="text-center pt-10 pb-30" style="position: absolute;bottom: 0;width: 100%;">
          <el-button class="btn--border-blue s-btn" @click="goBack">取消</el-button>
          <el-button type="primary" class="s-btn" style="margin-left: 30px;" @click="handleValid">保存</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>
<script>
import addrSelector from "components/common/addrSelector.vue";
import palTab from "components/common/pal-tab.vue";

export default {
  name: 'BindAccount',
  props: [],
  components: {palTab, addrSelector},
  computed: {
    getPickerOption() {
      return function (time, key) {
        var obj = {}
        var self = this
        if (time == 'start') {
          return {
            disabledDate(time) {
              if (!self[key].createEndTime) {
                return false
              }
              let curDate = new Date(self[key].createEndTime).getTime()
              let three = new Date(
                self
                  .$moment(self[key].createEndTime)
                  .subtract(6, 'months')
                  .format('YYYY-MM-DD')
              ).getTime()
              // let threeMonths = three - curDate
              return time.getTime() > curDate || time.getTime() < three
            }
          }
        } else {
          return {
            disabledDate(time) {
              if (!self[key].createStartTime) {
                return false
              }
              let curDate = new Date(self[key].createStartTime).getTime()
              let three = new Date(
                self
                  .$moment(self[key].createStartTime)
                  .add(6, 'months')
                  .format('YYYY-MM-DD')
              ).getTime()
              // let threeMonths = curDate + three
              return (
                time.getTime() <= curDate - 86400000 ||
                time.getTime() > three - 86400000
              )
            }
          }
        }
      }
    },
    getListTask() {
      return function (appCode, taskCode) {
        var arr = this.declareAccountList[appCode].filter(item => {
          return item.taskCode == taskCode
        })
        return arr
      }
    },
  },
  watch: {},
  data() {
    return {
      show: false,
      rowData: null,
      formData: {
        certEffeStartTime: '',
        certEffeEndTime: '',
        remark: '',
        tableData: [],
      },
      appList: [],
      declareAccountList: {},
      declareSystemList: {},
      row: null,
      addItem: {
        appCode: '',
        taskCode: '',
        declareSystem: '',
        isNew: true
      },
    }
  },
  methods: {
    init(obj) {
      this.show = true
      this.rowData = this.$global.deepCopyObj(obj)
      this.listApp();
      this.getByUsbKeyId(obj.id)
      this.formData.certEffeStartTime = ""
      this.formData.certEffeEndTime = ""
      this.formData.remark = ""
    },
    listApp() {
      this.$http({
        url: '/robot/sessionKeep/listApp?clientId=' + this.rowData.companyId + '&runSupport=',
        method: 'get'
      }).then((data) => {
        this.appList = data.data.data
      })
    },
    changeListTask(appCode,index){
      this.formData.tableData[index].taskCode = ''
      this.formData.tableData[index].declareSystem = ''
      this.listTask(appCode,index)
    },
    listTask(appCode,index) {
      var self = this
      this.$http({
        url: '/robot/sessionKeep/listTask?clientId=&appCode=' + appCode,
        method: 'post'
      }).then((data) => {
        self.$set(self.declareAccountList, appCode, data.data.data)
      })
      self.getDeclareSystem(appCode);
    },
    getDeclareSystem(appCode) {
      var self = this
      this.$http({
        url: '/robot/sessionKeep/listDeclareSystem?appCode=' + appCode,
        method: 'get'
      }).then((data) => {
        if (data.data.code == '200') {
          self.$set(self.declareSystemList, appCode, data.data.data)
        }
      })
    },
    refreshTable() {
      this.$emit('refreshTable')
    },
    //添加
    doRowAdd(index, type) {
      let addItem = {...this.addItem}
      this.formData.tableData.push(addItem)
    },
    //修改
    doRowEdit(index, rowInd, type) {
      let rowData = this.formData.tableData[rowInd];
      this.listTask(rowData.appCode, rowInd)
      this.$set(rowData, 'isEdit', true)
    },
    //解绑
    doRowDel(item, rowInd, type) {
      this.$confirm('是否确定解绑？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning'
      }).then(() => {
        this.formData.tableData.splice(rowInd, 1);
        if (item.id) {
          this.$http({
            url: '/robot/usbKey/deleteById?id=' + item.id,
            method: 'post'
          })
        }
      })
    },
    getByUsbKeyId(usbKeyId) {
      this.$http({
        url: '/robot/usbKey/getByUsbKeyId?usbKeyId=' + usbKeyId,
        method: 'get'
      }).then((data) => {
        this.formData.tableData = data.data.data
        this.formData.tableData.forEach(item => {
          this.listTask(item.appCode)
        })
        if (data.data.data) {
          this.formData.certEffeStartTime = data.data.data[0].certEffeStartTime
          this.formData.certEffeEndTime = data.data.data[0].certEffeEndTime
          this.formData.remark = data.data.data[0].remark
        }
      })
    },
    //保存校验
    handleValid() {
      if (this.formData.tableData.length == 0) {
        this.$message.error('请先添加申报账户')
        return false
      }
      this.formData.tableData.forEach((item, index) => {
        item.remark = this.formData.remark
        item.certEffeStartTime = this.formData.certEffeStartTime
        item.certEffeEndTime = this.formData.certEffeEndTime
        item.usbKeyId = this.rowData.id;
        item.clientId = this.rowData.companyId;
        // 添加验证触发逻辑
        this.$refs.tableData.validateField('tableData.' + index + '.appCode');
        this.$refs.tableData.validateField('tableData.' + index + '.taskCode');
        this.$refs.tableData.validateField('tableData.' + index + '.declareSystem');
      });
      this.$refs.tableData.validate(valid => {
        if (valid) {
          this.$global.showLoading()
          console.log(this.formData.tableData)
          this.formData.tableData.forEach(item => {
            item.companyName = this.getListTask(item.appCode, item.taskCode)[0].orgName
            item.declareAccount = this.getListTask(item.appCode, item.taskCode)[0].accountNumber
          })
          this.$http({
            url: '/robot/usbKey/save',
            method: 'post',
            data: this.formData.tableData,
          }).then((data) => {
            this.$global.closeLoading()
            this.$message.success('操作成功')
          })
        } else {
          this.$global.closeLoading()
        }
      })
    },
    goBack() {
      this.show = false
    }
  }
}
</script>
<style lang="less" scoped>
/deep/ .detail-drawer {
  width: 1200px !important;
}

.row-title {
  font-weight: bold;
  margin-bottom: 15px;
  margin-top: 10px;
}

.row-item {
  display: flex;
  margin-bottom: 15px;

  .lab {
    width: 120px;
    text-align: right;
  }

  .sel {
    flex: 1;
    margin-left: 5px;
    word-break: break-all;
  }
}

.row {
  margin-top: 10px;
}

</style>
