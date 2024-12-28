<template>
  <div class="spl-container">
    <breadcrumb :data="pathData"></breadcrumb>
    <div class="pl-20 pr-20">
      <div class="search-row display-flex">
        <el-row class="flex1">
          <el-col :span="5" style="margin-left: 20px;">
            <el-input v-model.trim="search.keyWord" placeholder="请输入地区/应用等关键字搜索" style="width: 300px" @keyup.enter.native="getTableData" clearable></el-input>
          </el-col>
        </el-row>
        <el-button type="primary" @click="getTableData">查询</el-button>
        <el-button type="primary" @click="setForm.show=true" class="w-60" style="margin-left: 20px;">添加</el-button>
        <el-button type="primary" @click="againAllSync" class="w-100" style="margin-left: 20px;">全部重新同步</el-button>
      </div>
      <div>
        <dTable @fetch="getTableData" ref="dTable" :tableHeight="tableHeight" :paging="true" :showSelection="false" :showIndex="true" rowKey="id">
          <u-table-column prop="appName" label="应用名称" min-width="60" :show-overflow-tooltip="true"/>
          <u-table-column prop="declareSystemName" label="申报系统" min-width="100" :show-overflow-tooltip="true"/>
          <u-table-column prop="fileName" label="执行文件名称" min-width="100" :show-overflow-tooltip="true"/>
          <u-table-column prop="remark" label="备注" min-width="100" :show-overflow-tooltip="true"/>
          <u-table-column prop="disabled" label="状态" min-width="60" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <el-switch v-model="scope.row.disabled" @change="changeReceiveStatus($event,scope.row.id)" :active-value="1" :inactive-value="0" active-color="#13ce66" inactive-color="#e4e4e4"></el-switch>
            </template>
          </u-table-column>
          <u-table-column label="操作" align="left" fixed="right" width="150">
            <template slot-scope="scope">
              <div class="jkh-flex">
                <el-button type="text" size="small" class="text-blue" @click="editCertificate(scope.row)">编辑</el-button>
                <el-button type="text" size="small" class="text-blue" @click="againSync(scope.row)">重新同步</el-button>
              </div>
            </template>
          </u-table-column>
        </dTable>
      </div>
    </div>
    <!-- 添加 -->
    <el-dialog :visible.sync="setForm.show" :title="dialogTitle" width="550px" class="cust-dialog" :show-close="true" :close-on-click-modal="false" :before-close="doClose">
      <div class="template-dialog-box">
        <el-form :model="setForm" ref="setForm" label-width="130px">
          <el-form-item label="应用：" prop="appCode" class="flex1" :rules="[{ required: true, message: '请选择应用', trigger: 'change' }]">
            <el-select v-model="setForm.appCode" @change="changeApp" clearable filterable style="width:300px">
              <el-option v-for="it in options.appList" :key="it.appCode" :label="it.appName" :value="it.appCode"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="申报系统：" prop="declareSystem" class="flex1" :rules="[{ required: true, message: '请选择申报系统', trigger: 'change' }]">
            <el-select v-model="setForm.declareSystem" clearable filterable style="width:300px">
              <el-option v-for="it in options.declareSystemSelects" :key="it.dictCode" :label="it.dictName" :value="it.dictCode"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="执行文件名称：" prop="fileName" class="flex1" :rules="[{ required: true, message: '请输入执行文件名称', trigger: 'change' }]">
            <el-input v-model="setForm.fileName" clearable filterable type="textarea" :autosize="{ minRows: 4, maxRows: 46 }" style="width:300px"></el-input>
          </el-form-item>
          <el-form-item label="备注：" prop="remark" class="flex1">
            <el-input v-model="setForm.remark" clearable filterable type="textarea" :autosize="{ minRows: 4, maxRows: 46 }" style="width:300px"></el-input>
          </el-form-item>
        </el-form>
        <div class="text-right mt-30">
          <el-button size="small" class="mr-15 w-80" @click="doClose()">取消</el-button>
          <el-button size="small" class="w-80" type="primary" @click="doSumbit()">确定</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>
<script>

import dTable from '../../../components/common/table'

export default {
  components: { dTable },
  data () {
    return {
      dialogTitle: '添加',
      pathData: [],
      search: {
        status: null,
        keyWord: ''
      },
      setForm: {
        show: false,
        appCode: '',
        declareSystem: '',
        fileName: '',
        remark: ''
      },
      options: {
        appList: [],
        declareSystemSelects: []
      }
    }
  },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 270 + 'px'
    }
  },
  created () {
    let that = this
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = tabs[this.$route.path]
    }
    this.$nextTick(() => {
      that.getTableData()
    })
    this.changeCust()
  },
  mounted () {
  },
  methods: {
    getTableData () {
      let search = this.search
      var params = [
        { property: 'keyword', value: search.keyWord },
        { property: 'status', value: search.status }
      ]
      this.$refs.dTable.fetch({
        query: params,
        method: 'post',
        url: '/robot/appCa/page'
      })
    },
    editCertificate (row) {
      this.dialogTitle = '编辑'
      this.isEdit = true
      let that = this
      this.$http({
        url: '/robot/appCa/getById',
        method: 'get',
        params: {
          id: row.id
        }
      }).then((resp) => {
        if (resp.data.code == '200') {
          that.setForm = resp.data.data
          that.setForm.show = true
          that.changeCust()
          that.changeApp()
        }
      }).catch((err) => {
        this.isloading = false
        this.$message.error('接口出错，请稍后再试')
      })
    },
    againSync (row) {
      let that = this
      this.$confirm('是否确定重新同步？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: '/robot/appCa/againSync',
          method: 'post',
          params: {
            id: row.id
          }
        }).then((resp) => {
          if (resp.data.code == '200') {
            that.$message.success('操作成功')
            this.getTableData()
          }
        }).catch((err) => {
          this.isloading = false
          this.$message.error('接口出错，请稍后再试')
        })
      })
    },
    againAllSync () {
      let that = this
      this.$confirm('是否确定重新同步？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: '/robot/appCa/againSync',
          method: 'post'
        }).then((resp) => {
          if (resp.data.code == '200') {
            that.$message.success('操作成功')
            this.getTableData()
          }
        }).catch((err) => {
          this.isloading = false
          this.$message.error('接口出错，请稍后再试')
        })
      })
    },
    getDeclareSystem () {
      let appCode = this.setForm.appCode ? this.setForm.appCode : ''
      this.$http({
        url: '/robot/sessionKeep/listDeclareSystem?appCode=' + appCode,
        method: 'get'
      }).then((data) => {
        if (data.data.code == '200') {
          this.options.declareSystemSelects = data.data.data
        }
      })
    },
    changeCust () {
      this.$http({
        url: '/robot/app/list',
        method: 'post'
      }).then((data) => {
        this.options.appList = data.data.data
      })
    },
    changeApp () {
      this.getDeclareSystem()
    },
    doClose () {
      this.setForm = {
        show: false
      }
      this.$nextTick(() => {
        this.$refs.setForm.clearValidate()
      })
    },
    doSumbit () {
      let that = this
      this.$refs.setForm.validate((valid) => {
        if (valid) {
          that.$global.showLoading()
          that.$http({
            url: '/robot/appCa/save',
            method: 'post',
            data: that.setForm
          }).then((data) => {
            that.$global.closeLoading()
            that.doClose()
            if (data.data.code == '200') {
              that.$message.success('操作成功')
              that.getTableData()
            }
          }).catch(() => {
            that.$global.closeLoading()
          })
        }
      })
    },
    changeReceiveStatus (val, id) {
      this.$global.showLoading()
      this.$http({
        url: `/robot/appCa/disabled`,
        method: 'post',
        params: {
          id: id,
          disabled: val
        }
      }).then((res) => {
        this.$global.closeLoading()
        this.getTableData()
      })
    }
  }
}
</script>
<style lang="less" scoped>
/deep/ .el-form-item {
  margin-bottom: 15px;
}
</style>
