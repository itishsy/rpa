<template>
  <!-- 报盘文件设置 -->
  <div class="spl-container">
    <breadcrumb :data="pathData">
      <!-- <el-button type="primary" slot="breadcrumb-btn" size="mini" @click="goAdd">新增</el-button> -->
    </breadcrumb>
    <div class="pl-20 pr-20">
      <el-row>
        <el-col :span="10">
          <div class="search-row display-flex">
            <!-- <span class="ml-20 label">参保地：</span> -->
            <el-input
              v-model.trim="keyWord"
              placeholder="请输入关键字搜索"
              style="width: 300px"
              @change="changeSearch"
              clearable
              v-filter-symbols
            ></el-input>
          </div>
        </el-col>
        <el-col :span="14">
          <div class="search-row display-flex" style="justify-content: flex-end;">
            <el-button type="primary" size="mini" @click="getTableData">查询</el-button>
            <el-button type="primary" size="mini" @click="importExcel">导入</el-button>
          </div>
        </el-col>
      </el-row>
      <div>
        <dTable
          @fetch="getTableData"
          ref="table"
          style="margin-top: 0"
          :tableHeight="tableHeight"
          :showIndex="true"
          :showSelection="false"
          row-key="id"
          row-id="id"
        >
          <u-table-column
            prop="customerName"
            label="客户名称"
            width="200"
            header-align="center"
            align="center"
            :show-overflow-tooltip="true"
          ></u-table-column>
          <u-table-column
            prop="insuredName"
            label="参保主体"
            width="200"
            header-align="center"
            align="center"
            :show-overflow-tooltip="true"
          ></u-table-column>
          <u-table-column
            prop="businessName"
            label="业务类型"
            width="100"
            header-align="center"
            align="center"
            :show-overflow-tooltip="true"
          ></u-table-column>
          <u-table-column
            prop="declareUnit"
            label="申报单位"
            header-align="center"
            align="center"
            width="200"
            :show-overflow-tooltip="true"
          ></u-table-column>
          <u-table-column
            prop="declareAccount"
            label="申报账号"
            width="200"
            header-align="center"
            align="center"
            :show-overflow-tooltip="true"
          ></u-table-column>
          <u-table-column label="操作" align="left">
            <template slot-scope="scope">
              <el-button type="text" size="small" class="text-blue" @click="edit(scope.row)">编辑</el-button>
              <div class="opt-btn-split"></div>
              <el-button type="text" size="small" class="text-red" @click="disableRow(scope.row)">删除</el-button>
            </template>
          </u-table-column>
        </dTable>
      </div>
    </div>
    <el-drawer
      :title="'编辑'"
      :visible.sync="dialogVisible"
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
            ref="ruleForm"
            label-width="100px"
            :rules="rules"
          >
            <div style="padding: 10px">
              <el-form-item
                :prop="item.prop"
                :label="item.label + '：'"
                v-for="item in formList"
                :key="item.label"
              >
                <!-- <el-select
                  style="width: 250px;"
                  v-model="formData[item.prop]"
                  placeholder="请选择"
                  v-if="item.type == 'select'"
                  filterable
                  clearable
                  :ref="item.ref"
                >
                  <el-option
                    v-for="item1 in item.options"
                    :key="item1[getSelectProp(item.prop).key]"
                    :label="item1[getSelectProp(item.prop).label]"
                    :value="item1[getSelectProp(item.prop).value]"
                  ></el-option>
                </el-select>-->
                <el-input
                  style="width: 250px;"
                  v-model="formData[item.prop]"
                  placeholder="请输入"
                  v-if="item.type == 'input'"
                  clearable
                  :disabled="item.disabled"
                ></el-input>
                <el-input
                  style="width: 250px;"
                  v-model="formData[item.prop]"
                  placeholder="请输入"
                  type="textarea"
                  :rows="3"
                  v-if="item.type == 'textarea'"
                  clearable
                ></el-input>
              </el-form-item>
            </div>
          </el-form>
        </div>
        <div class="drawer-footer-buts">
          <el-button class="btn--border-blue s-btn mr-0" @click="doClose">取消</el-button>
          <el-button type="primary" class="s-btn ml-12" @click="confirmEdit">确认</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>
  <script>
import dTable from '../../../components/common/table'
export default {
  components: { dTable },
  data() {
    return {
      keyWord: '',
      formData: {
        customerName: '',
        insuredName: '',
        businessName: '',
        declareUnit: '',
        declareAccount: '',
        remark: '',
      },
      dialogVisible: false,
      pathData: [],
      rules: {
        customerName: [{ required: true, message: '请输入客户名称', trigger: 'change' }],
        insuredName: [{ required: true, message: '请输入参保主体', trigger: 'change' }],
        businessName: [{ required: true, message: '请输入业务类型', trigger: 'change' }],
        declareUnit: [{ required: true, message: '请输入申报单位', trigger: 'change' }],
        declareAccount: [{ required: true, message: '请输入申报账号', trigger: 'change' }],
      },
      options: [],
      loading: null,
      formList: [
        { label: '客户名称', type: 'input', required: true, message: '请输入客户名称', prop: 'customerName', disabled: true },
        { label: '参保主体', type: 'input', required: true, message: '请输入参保主体', prop: 'insuredName', disabled: true },
        { label: '业务类型', type: 'input', required: true, message: '请输入业务类型', prop: 'businessName', disabled: true },
        { label: '申报单位', type: 'input', required: true, message: '请输入申报单位', prop: 'declareUnit', disabled: true },
        { label: '申报账号', type: 'input', required: true, message: '请输入申报账号', prop: 'declareAccount', disabled: false },
        { label: '备注', type: 'input', required: false, message: '请输入备注', prop: 'remark', disabled: false },
      ],
    }
  },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 270 + 'px'
    },
    getProductNameList() {
      return function (row) {
        var arr = []
        var productNameList = row.productName.split('|')
        this.options.forEach((item) => {
          productNameList.forEach((item2) => {
            if (item.dictName == item2) {
              arr.push(item2)
            }
          })
        })
        return arr.join('|')
      }
    },
  },
  created() {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = this.deepCopy(tabs[this.$route.meta.parentPath])
      this.pathData.push({name:'初始化申报账号'})
    }
    this.$nextTick(() => {
      this.getTableData()
      // this.getDictByKey()
    })
  },
  mounted() {},
  methods: {
    changeSearch() {
      this.getTableData()
    },
    //去新增 社保方案
    goAdd() {
      this.$router.push('/insuranceScheme/addSocial')
    },
    //方案变动时
    socialChange(list) {
      var productNameList = []
      var arr = []
      this.options.forEach((item) => {
        this.socialListData.socialList.forEach((item2) => {
          if (item.dictName == item2.dictName) {
            arr.push(item2)
          }
        })
      })
      this.socialListData.socialList = arr
      this.socialListData.socialList.forEach((item) => {
        productNameList.push(item.dictName)
      })
      this.socialListData.productNameList = productNameList.join(' | ')
    },
    //取消调整
    cancalEdit() {
      this.dialogVisible = false
      this.$refs.ruleForm.resetFields()
    },
    //确定调整
    confirmEdit() {
      var self = this
      this.$refs.ruleForm.validate((vaild) => {
        if (vaild) {
          this.showLoading()
          this.$http({
            url: 'policy/declareAccount/updateAiPerson',
            method: 'post',
            data: this.formData,
          })
            .then((res) => {
              this.closeLoading()
              if(res.data.code == 200){
                this.$message.success('操作成功')
                this.getTableData()
                this.dialogVisible = false
              }
            })
            .catch((err) => {
              this.closeLoading()
            })
        }
      })
    },
    //禁用方案
    disableRow(row) {
      this.$confirm('一旦删除，相关功能将无法引用此记录，是否继续？', '提示', {
        confirmButtonText: '确认删除',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning',
      })
        .then(() => {
          this.showLoading()
          this.$http({
            url: 'policy/declareAccount/deleteAiPerson?id=' + row.id,
            method: 'post',
          })
            .then(({ data }) => {
              this.getTableData()
              this.$message.success('操作成功')
              this.closeLoading()
            })
            .catch(() => {
              this.closeLoading()
            })
        })
        .catch(() => {})
    },
    //列表查询
    async getTableData(pageChange, callback) {
      var params = [{ property: 'keyword', value: this.keyWord }]
      var self = this
      this.$refs.table.fetch({
        pageChange: pageChange ? pageChange : '',
        query: params,
        method: 'post',
        url: '/policy/declareAccount/queryAiPerson',
        callback: callback
          ? callback
          : function (res) {
              setTimeout(() => {
                self.$refs.table.doLayout()
              }, 2000)
            },
      })
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
      if (this.loading.close) {
        this.loading.close()
      }
    },
    //获取字典值
    async getDictByKey() {
      return this.$http({
        url: 'policy/sys/dict/getDictByKey',
        method: 'get',
        params: {
          dataKey: '10003',
        },
      })
        .then((res) => {
          this.options = res.data.data
        })
        .catch((err) => {
          this.$message.error('字典接口出错，请稍后再试')
        })
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
    importExcel() {
      this.$router.push('/basicConfig/initDeclareAccountImport')
    },
    doClose() {
      this.resetDetail()
      this.formData = this.$options.data().formData
      this.dialogVisible = false
    },
    resetDetail() {
      this.$refs.ruleForm.resetFields()
      this.formData = this.$options.data().formData
    },
    edit(row) {
      this.dialogVisible = true
      this.formData = this.deepCopy(row)
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
/deep/.el-drawer__body {
  padding-bottom: 0;
}
/deep/.el-dialog__header {
  border-bottom: none !important;
}

.file-content-box {
  padding: 20px 20px;
  .file-list {
    display: flex;
    padding: 10px 0;
    .file {
      background: #f1f8ff;
      border-radius: 10px;
      padding: 10px;
      position: relative;
      cursor: pointer;
      margin-right: 15px;
      &:hover {
        color: #3e82ff;
        text-decoration: underline;
      }
      .file-remove {
        position: absolute;
        right: -10px;
        top: -10px;
        width: 20px;
        height: 20px;
        background: red;
        border-radius: 50%;
        display: none;
        &::before {
          content: '';
          display: inline-block;
          width: 10px;
          height: 2px;
          background: white;
          position: absolute;
          top: 50%;
          left: 50%;
          transform: translate(-50%, -50%);
        }
      }
      &:hover .file-remove {
        display: block;
      }
    }
  }
  .upload-tips {
    margin-top: 20px;
    font-size: 12px;
    color: #797979;
  }
}
.tag {
  display: inline-block;
  padding: 0 15px;
  height: 30px;
  line-height: 30px;
  background: rgba(255, 255, 255, 1);
  border: 1px solid rgba(206, 206, 206, 1);
  border-radius: 0px 2px 2px 0px;
  margin-right: 10px;
  cursor: pointer;
}
.tag:hover,
.tag.active {
  background-color: #dddddd;
}
.table-fileName-list {
  cursor: pointer;
  &:hover {
    color: #3e82ff;
    text-decoration: underline;
  }
}
.text-blue {
  &:hover {
    text-decoration: underline;
  }
}
</style>
  <style scoped>
body .el-table th.gutter {
  display: table-cell !important;
}
.error {
  margin-top: 10px;
  /* position: absolute; */
  left: 0;
  /* top: 120%; */
  color: #f56c6c;
  font-size: 12px;
}
</style>
  