<template>
  <div class="spl-container">
    <breadcrumb :data="pathData"></breadcrumb>
    <div class="pl-20 pr-20">
      <el-row>
        <el-col :span="24">
          <div class="search-row display-flex">
            <div style="display: flex;width: 100%;">
              <el-input
                @keyup.enter.native="search"
                v-model.trim="keyword"
                clearable
                placeholder="请输入客户/业务/服务/状态关键字查询"
                style="width:250px;"
              ></el-input>
              <!-- <el-select v-model="platform" class="w-250 ml-10" clearable filterable placeholder="请选择平台方">
                  <el-option v-for="it in option.platform" :key="it.dictCode" :label="it.dictName" :value="it.dictCode"></el-option>
              </el-select>-->
              <div class="flex1 text-right">
                <el-button type="primary" size="mini" @click="search" class="ml-20">查询</el-button>
                <el-button type="primary" size="mini" @click="addCustomer" class="ml-20">新增客户</el-button>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
      <div>
        <dTable
          @fetch="getTableData"
          ref="table"
          style="margin-top: 0;"
          :tableHeight="tableHeight"
          :showIndex="true"
          :showSelection="false"
        >
          <u-table-column prop="name" label="客户名称" min-width="150" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column
            prop="moduleList"
            label="服务模块"
            min-width="150"
            :show-overflow-tooltip="true"
          >
            <template slot-scope="scope">
              <p
                style="overflow: hidden;text-overflow: ellipsis;"
              >{{ moduleName(scope.row.moduleList) }}</p>
            </template>
          </u-table-column>
          <u-table-column
            prop="businessTypeName"
            label="业务类型"
            min-width="150"
            :show-overflow-tooltip="true"
          ></u-table-column>
          <u-table-column
            prop="cityNumber"
            label="开通城市（实/估）"
            min-width="150"
            :show-overflow-tooltip="true"
          >
            <template slot-scope="scope">
              <p
                style="overflow: hidden;text-overflow: ellipsis;"
                v-html="handleNumber(scope.row.activeCityNumber,scope.row.cityNumber)"
              ></p>
            </template>
          </u-table-column>
          <u-table-column
            prop="activeAccountNumber"
            label="开通账户（实/估）"
            min-width="150"
            :show-overflow-tooltip="true"
          >
            <template slot-scope="scope">
              <p
                style="overflow: hidden;text-overflow: ellipsis;"
                v-html="handleNumber(scope.row.activeAccountNumber,scope.row.accountNumber)"
              ></p>
            </template>
          </u-table-column>
          <u-table-column prop="date" label="合同期" min-width="180" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <p style="overflow: hidden;text-overflow: ellipsis;">{{contractDate(scope.row)}}</p>
            </template>
          </u-table-column>
          <u-table-column prop="status" label="状态" min-width="150" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <p
                style="overflow: hidden;text-overflow: ellipsis;"
              >{{ scope.row.status == '1' ? '合作中' : '终止服务' }}</p>
            </template>
          </u-table-column>
          <u-table-column label="操作" align="left" width="130" fixed="right">
            <template slot-scope="scope">
              <el-button
                type="text"
                size="small"
                class="text-blue"
                @click="handleEdit(scope.row)"
              >编辑</el-button>
              <!-- <el-button type="text" size="small" class="text-blue ml-20" style="margin-left: 20px;" @click="continueCooperate(scope.row)" v-if="!scope.row.status">继续合作</el-button> -->
              <el-button
                type="text"
                size="small"
                class="text-blue ml-20"
                style="margin-left: 20px;"
                @click="handleCooperate(scope.row)"
                v-if="scope.row.status == 1"
              >终止合作</el-button>
            </template>
          </u-table-column>
          <template slot="pagination-btns">
            <div class="display-flex">
              <el-button
                size="small"
                icon="icon ic-export-blue"
                class="btn--border-blue s-btn"
                @click="exportData"
              >导出</el-button>
            </div>
          </template>
        </dTable>
      </div>
    </div>
    <el-dialog
      title="终止合作"
      :visible.sync="formData.show"
      width="600px"
      :before-close="cancel"
      :close-on-click-modal="false"
      class="cust-dialog"
    >
      <div id="upload-dialog">
        <div class="file-content-box">
          <el-form
            :model="formData"
            :rules="rules"
            ref="formData"
            label-width="130px"
            class="demo-ruleForm"
          >
            <div class="upload-file-box" style="padding: 20px;">
              <p style="padding-bottom: 20px;">是否确认与该客户终止合作？</p>
              <div>
                <el-form-item
                  prop="endDate"
                  label="合同结束日期："
                >
                  <el-date-picker
                    v-model="formData.endDate"
                    type="date"
                    placeholder="选择日期"
                    value-format="yyyy-MM-dd"
                  ></el-date-picker>
                </el-form-item>
              </div>
              <div>
                <el-form-item
                  prop="terminationReason"
                  style="margin-bottom:0;"
                  label="终止原因："
                >
                  <el-input
                    type="textarea"
                    :rows="3"
                    placeholder="请输入原因"
                    style="width:70%"
                    v-model.trim="formData.terminationReason"
                  ></el-input>
                <div style="text-align:right;position:absolute;bottom:-10px;right:30%;">{{formData.terminationReason.length}} / 200</div>
                </el-form-item>
              </div>
            </div>
          </el-form>
          <div class="text-center mt-20">
            <el-button @click="cancel" class="footer-btn">取 消</el-button>
            <el-button type="primary" @click="confirm" class="ml-20 footer-btn">确定</el-button>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>
  <script>
import dTable from '../../components/common/table'
export default {
  components: { dTable },
  data() {
    return {
      pathData: [],
      keyword: '',
      platform: '',
      formData: {
        id: '',
        show: false,
        endDate: '',
        terminationReason: '',
        status: '',
      },
      rules: {
        terminationReason: [
          { required: true, message: '请输入原因', trigger: 'blur' },
          { required: true, validator: this.terminationReason, trigger: 'blur' },
        ],
        endDate: [{ required: true, message: '请选择日期', trigger: 'blur' }],
      },
      rowId: '',
    }
  },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 270 + 'px'
    },
    contractDate() {
      return function (row) {
        if (row.contractList) {
          var max = 0
          var min = 0
          row.contractList.forEach((item) => {
            if (item.beginDate) {
              min = item.beginDate
            }
            if(new Date(item.beginDate).getTime() < new Date(min).getTime()){
              min = item.beginDate
            }
            if (new Date(item.endDate).getTime() > new Date(max).getTime()) {
              max = item.endDate
            }
          })
          if(!min && !max){
            return '-'
          }
          return `${min ? min : ''} ~ ${max ? max : ''}`
        }
        return ''
      }
    },
    handleNumber() {
      return function (a, b) {
        var num1 = a ? a : 0
        var num2 = b ? b : 0
        return `<span style="font-size:16px;">${num1}</span> / ${num2}`
      }
    },
    moduleName() {
      return function (row) {
        var str = []
        row.forEach((item) => {
          str.push(item.moduleName)
        })
        return str.join('，')
      }
    },
  },
  watch: {},
  created() {
    let that = this
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = tabs[this.$route.path]
    }
    this.$nextTick(() => {
      that.getTableData()
    })
  },
  mounted() {},
  methods: {
    addCustomer() {
      this.$router.push('/system/customerSetting')
    },
    terminationReason(rule, value, callback) {
      if (!value) {
        return
      }
      if (value.trim().length > 200) {
        return callback(new Error('原因不得大于200字，请修正'))
      }
      return callback()
    },
    getTableData() {
      var params = [{ property: 'searchText', value: this.keyword }]
      this.$refs.table.fetch({
        query: params,
        method: 'post',
        url: '/policy/sys/customer/page',
      })
    },
    search() {
      this.getTableData()
    },
    // 获取字典值
    getDictByKey(key, field) {
      let that = this
      this.$http({
        url: 'policy/sys/dict/getDictByKey',
        method: 'get',
        params: {
          dataKey: key,
        },
      })
        .then((res) => {
          that.option[field] = res.data.data
        })
        .catch()
    },
    // 终止合作
    handleCooperate(row) {
      this.formData.id = row.id
      this.formData.status = row.status ? 0 : 1
      this.formData.show = true
    },
    handleEdit(row) {
      this.$router.push(`/system/customerSetting?id=${row.id}&type=edit`)
    },
    cancel() {
      this.formData = this.$options.data().formData
      this.formData.show = false
      this.$nextTick(() => {
        this.$refs.formData.clearValidate()
      })
    },
    confirm() {
      this.$refs.formData.validate((valid) => {
        if (valid) {
          this.stopCooperate(this.formData.id, this.formData.status)
        }
      })
    },
    stopCooperate(id, status) {
      this.$http({
        url: `policy/sys/customer/updateStatus/${id}/${status}`,
        method: 'post',
        params: {
          terminationReason: this.formData.terminationReason,
          endDate: this.formData.endDate,
        },
      })
        .then((res) => {
          this.$message.success('操作成功')
          this.getTableData()
          this.cancel()
        })
        .catch()
    },
    exportData() {
      var params = this.$refs.table.getParamsQuery()
      if (!params) {
        this.$message.warning('请先查询数据')
        return
      }
      var params2 = this.$global.deepcopyArray(params)
      this.$downloadFile(
        'policy/sys/customer/export',
        'post',
        {
          query: params2,
        },
        this.$global.EXCEL
      )
    },
  },
}
</script>
  <style lang="less" scoped>
/deep/.el-dialog__header {
  padding: 10px 20px;
}
/deep/.el-dialog__body {
  padding: 10px 10px;
}
/deep/.el-form-item__content {
  line-height: 0px;
}

/deep/.el-form-item {
  margin-bottom: 10px;
}
</style>
  