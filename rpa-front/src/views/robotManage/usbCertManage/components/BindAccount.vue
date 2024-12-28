<template>
  <div>
    <el-drawer title="添加指令" :visible.sync="show" :wrapperClosable="false" size="100%" custom-class="spl-filter-drawer detail-drawer" :show-close="true">
      <div style="padding: 0px 20px 20px 20px;">
        <div class="type-title clearfix" style="margin-top: 10px;"><span class="text">uKey信息</span></div>
        <el-form :inline="true" :model="formData" ref="tableData" label-width="100px">
          <div class="pl-10 pt-20">
            <el-row>
              <el-col :span="24" class="row-item" style="align-items: center">
                <el-form-item label="idVendor：" prop="idVendor" class="flex1" :rules="[{ required: true, message: '请输入idVendor', trigger: 'change' }]">
                  <el-input v-model.trim="formData.idVendor" placeholder="请输入idVendor" style="max-width: 300px" class="flex1" clearable></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="24" class="row-item" style="align-items: center">
                <el-form-item label="idProduct：" prop="idProduct" class="flex1" :rules="[{ required: true, message: '请输入idProduct', trigger: 'change' }]">
                  <el-input v-model.trim="formData.idProduct" placeholder="请输入idProduct" style="max-width: 300px" class="flex1" clearable></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="24" class="row-item" style="align-items: center">
                <el-form-item label="usbInterface：" prop="usbInterface" class="flex1" :rules="[
                  { required: true, message: '请输入usbInterface', trigger: 'change' },
                  { pattern: /^[0-9]$/, message: '只能输入0-9的数字', trigger: 'change' }
                  ]">
                  <el-input v-model.trim="formData.usbInterface" placeholder="请输入usbInterface" style="max-width: 300px" class="flex1" clearable></el-input>
                </el-form-item>
              </el-col>
            </el-row>
          </div>
          <div class="type-title mt-0 clearfix"><span class="text">读取指令</span></div>
          <div class="pl-10 pt-10">
            <div>
              <div class="mt-10">
                <table class="cust-table-border w-p100">
                  <tbody>
                  <tr v-for="(row, rowInd) in formData.tableData" :key="rowInd">
                    <td style="position: relative;width: 200px">
                      <el-form-item v-if="row.isNew || row.isEdit" label-width="0" :prop="'tableData.'+rowInd+'.type'" :rules="[{ required: true, message: '请选择', trigger: 'change' }]">
                        <el-select v-model.trim="row.type" placeholder="请选择"  class="flex1" clearable>
                          <el-option value="in">in</el-option>
                          <el-option value="out">out</el-option>
                        </el-select>
                      </el-form-item>
                      <p class="row-text" v-else>
                        <span>{{ row.type }}</span>
                      </p>
                    </td>
                    <td style="min-width: 500px">
                      <el-form-item v-if="row.isNew || row.isEdit" label-width="0" :prop="'tableData.'+rowInd+'.data'" :rules="[{ required: true, message: '请输入', trigger: 'change' }]" class="w-p100">
                        <el-input v-model.trim="row.data" placeholder="请输入"  class="flex1" clearable></el-input>
                      </el-form-item>
                      <p class="row-text" v-else>{{ row.data }}</p>
                    </td>
                    <td>
                      <div>
                        <el-button type="text" size="small" class="text-red" @click="doRowDel(row, rowInd, 'next')">删除</el-button>
                      </div>
                    </td>
                  </tr>
                  </tbody>
                </table>
                <div class="text-center pb-30">
                  <el-button class="btn--border-blue mt-20 w-200" @click="doRowAdd('', 'next')"><i class="el-icon-plus"></i>添加</el-button>
                </div>
              </div>
            </div>
          </div>
          <div class="type-title clearfix" style="margin-top: 10px;"><span class="text">测试</span></div>
          <div class="pl-10 pt-20">
            <el-row>
              <el-col :span="24" class="row-item el-form-item is-required" style="align-items: center">
                <span class="label el-form-item__label" style="flex-shrink: 0;width: 100px;text-align: right;">UsbServer：</span>
                <el-select v-model="testForm.usbServer" @change="getBusIdList" clearable filterable style="width:300px" value-key="macAddress">
                  <el-option v-for="it in deviceList" :key="it.macAddress" :label="it.id+'-'+it.hostname+'-'+it.ipAddress" :value="it"></el-option>
                </el-select>
              </el-col>
              <el-col :span="24" class="row-item el-form-item is-required" style="align-items: center">
                <span class="label el-form-item__label" style="flex-shrink: 0;width: 100px;text-align: right;">busId：</span>
                <el-select v-model="testForm.busId" clearable filterable style="width:300px">
                  <el-option v-for="it in busIdList" :key="it.busId" :label="it.busId" :value="it.busId"></el-option>
                </el-select>
              </el-col>
            </el-row>
          </div>
          <div class="text-center pt-10 pb-30" style="position: absolute;bottom: 0;width: 100%;">
            <el-button type="primary" class="s-btn" style="margin-left: 30px;" @click="handleTest">测试</el-button>
            <el-button type="primary" class="s-btn" style="margin-left: 30px;" @click="handleValid">保存</el-button>
            <el-button class="btn--border-blue s-btn" @click="goBack">取消</el-button>
          </div>
        </el-form>
      </div>
    </el-drawer>

    <el-dialog :visible.sync="showTest" title="测试反馈" width="550px" class="cust-dialog" :show-close="true" :close-on-click-modal="false" :before-close="doClose">
      <div class="template-dialog-box">
        <el-form model="setForm" ref="setForm" label-width="170px">
          <el-form-item label="内容标识：" prop="content" class="flex1">
            <el-input v-model="resultForm.content" clearable filterable style="width:300px" placeholder="内容标识"></el-input>
          </el-form-item>
          <el-form-item label="最后读取数据：" prop="lastReadData" class="flex1">
            <el-input v-model="resultForm.lastReadData" clearable filterable style="width:300px" placeholder="最后读取数据"></el-input>
          </el-form-item>
        </el-form>
        <div class="text-right mt-30">
          <el-button size="small" class="mr-15 w-80" @click="doClose()">关闭</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import palTab from "components/common/pal-tab.vue";

export default {
  name: 'BindAccount',
  props: [],
  components: {palTab},
  watch: {},
  data() {
    return {
      show: false,
      showTest: false,
      rowData: null,
      row: null,
      formData: {
        id: '',
        idVendor: '',
        idProduct: '',
        usbInterface: '',
        tableData: [],
      },
      addItem: {
        type: '',
        data: '',
        isNew: true
      },
      testForm: {
        busId: '',
        usbServer: {},
      },
      resultForm:{
        content: '',
        lastReadData: ''
      },
      deviceList: [],
      busIdList: [],
    }
  },
  methods: {
    init(obj) {
      this.show = true
      this.rowData = this.$global.deepCopyObj(obj)
      this.formData.id = ""
      this.formData.idVendor = ""
      this.formData.idProduct = ""
      this.formData.usbInterface = ""
      this.formData.tableData = []
      this.testForm.busId = ""
      this.testForm.usbServer = {}
      if (obj.id) {
        this.getById(obj.id)
      }
      this.getDeviceList();
    },
    getDeviceList() {
      this.$http({
        url: '/robot/device/list',
        method: 'post',
        data: {
          "query": [{"property": "type", "value": 0}]
        },
      }).then((data) => {
        this.deviceList = data.data.data.rows.filter(it => it.version != null)
      })
    },
    getBusIdList(item) {
      if (item.companyId) {
        this.$http({
          url: '/robot/readUsbContent/getBusIdList?companyId=' + item.companyId + '&macAddress=' + item.macAddress,
          method: 'get',
        }).then((data) => {
          this.busIdList = data.data.data
        })
      }
    },
    refreshTable() {
      this.$emit('refreshTable')
    },
    //添加
    doRowAdd(index, type) {
      let addItem = {...this.addItem}
      this.formData.tableData.push(addItem)
    },
    //删除
    doRowDel(item, rowInd, type) {
      this.$confirm('是否确定删除？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning'
      }).then(() => {
        this.formData.tableData.splice(rowInd, 1);
      })
    },
    getById(id) {
      this.$http({
        url: '/robot/readUsbContent/getById?id=' + id,
        method: 'get'
      }).then((data) => {
        this.formData = data.data.data
        if (!this.formData.tableData) {
          this.formData.tableData = []
        }
      })
    },
    handleTest() {
      if (!this.testForm.usbServer || !this.testForm.busId) {
        this.$message.error('请选择usbServer设备与busId')
        return;
      }
      console.log(this.testForm)
      let data = {
        companyId: this.testForm.usbServer.companyId,
        macAddress: this.testForm.usbServer.macAddress,
        busId: this.testForm.busId,
      }
      console.log(data)
      this.$global.showLoading()
      this.$http({
        url: '/robot/readUsbContent/testUserServer',
        method: 'post',
        data: data,
      }).then((data) => {
        console.log(data)
        this.showTest = true;
        this.$global.closeLoading()
        this.$message.success('操作成功')
      })
    },
    handleValid() {
      if (this.formData.tableData.length == 0) {
        this.$message.error('请先添加指令')
        return false
      }
      let flag = true;
      this.formData.tableData.forEach((item, index) => {
        if (item.type == "in") {
          if (!this.validateValue(item.data)) {
            this.$message.error('in指令数据必须是正整数');
            flag = false;
          }
        }
        this.$refs.tableData.validateField('tableData.' + index + '.type');
        this.$refs.tableData.validateField('tableData.' + index + '.data');
      });
      if (!flag) {
        return
      }
      this.$refs.tableData.validate(valid => {
        if (valid) {
          this.$global.showLoading()
          console.log(this.formData)
          this.$http({
            url: '/robot/readUsbContent/save',
            method: 'post',
            data: this.formData,
          }).then((data) => {
            this.$global.closeLoading()
            this.$message.success('操作成功')
          })
        } else {
          this.$global.closeLoading()
        }
      })
    },
    validateValue(value) {
      const num = Number(value);
      return Number.isInteger(num) && num > 0;
    },
    goBack() {
      this.show = false
    },
    doClose() {
      this.showTest = false
    },
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
