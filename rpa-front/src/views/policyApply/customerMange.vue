<template>
  <div class="spl-container">
    <!-- 导航 -->
    <breadcrumb :data="pathData">
      <template #breadcrumb-btn>
        <el-button type="primary" class="s-btn" @click="add">新增客户</el-button>
      </template>
    </breadcrumb>
    <div style="padding: 20px 20px 0px 20px">
      <el-row style="padding-bottom: 20px;">
        <el-col :span="6" class="display-flex">
          <span class="lab">客户名称：</span>
          <div class="sel">
            <el-input
              style="width: 215px;"
              v-model="searchData.custName"
              clearable
              placeholder="请输入客户名称"
            ></el-input>
          </div>
        </el-col>
        <el-col :span="6" class="display-flex">
          <span class="lab">销售人员：</span>
          <div class="sel">
            <el-input
              style="width: 215px;"
              v-model="searchData.saleName"
              clearable
              placeholder="请输入销售人员名称"
            ></el-input>
          </div>
        </el-col>
        <el-col :span="8" class="display-flex">
          <span class="lab">申报状态：</span>
          <div class="sel">
            <el-checkbox-group v-model="searchData.status">
              <el-checkbox :label="item.dictCode"  v-for="(item,index) in statusList" :key="index">{{ item.dictName }}</el-checkbox>
            </el-checkbox-group>
          </div>
        </el-col>
        <el-col :span="4" class="display-flex" style="justify-content: end;">
          <el-button type="primary" class="search-btn w-60" style="padding: 8px 10px;" @click="getTableData" @keyup.enter.native="getTableData">查询</el-button>
        </el-col>
      </el-row>
      <!-- 表格 -->
      <dTable @fetch="getTableData" ref="table" :tableHeight="tableHeight" :paging="true" :showSelection="false"
              :showIndex="false" rowKey="id">
        <u-table-column prop="custName" label="客户名称" min-width="200" :show-overflow-tooltip="true" fixed="left"></u-table-column>
        <u-table-column prop="saleName" label="销售人员" min-width="200" :show-overflow-tooltip="true" fixed="left"></u-table-column>
        <!-- <u-table-column prop="totalPersonNum" label="筛查总数人数" min-width="120" :show-overflow-tooltip="true"></u-table-column> -->
        <!-- <u-table-column prop="totalMatchinghMonthNum" label="筛查符合月份" min-width="120" :show-overflow-tooltip="true"></u-table-column> -->
        <!-- <u-table-column prop="totalMatchinghNum" label="符合人员数" min-width="120" :show-overflow-tooltip="true"></u-table-column> -->
        <!-- <u-table-column prop="refundTaxAmt" label="退税金额" min-width="120" :show-overflow-tooltip="true"></u-table-column> -->
        <u-table-column prop="createTime" label="创建日期" min-width="140" :show-overflow-tooltip="true">
          <template scope="scope">
            <span>{{ $moment(scope.row.createTime).format('YYYY-MM-DD HH:mm') }}</span>
          </template>
        </u-table-column>
        <u-table-column prop="customerName" label="申报状态" min-width="100" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <span>{{ getStatusListName(scope.row.status) }}</span>
          </template>
        </u-table-column>
        <u-table-column fixed="right" label="操作" width="150">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleView(scope.row)">查看</el-button>
            <el-button type="text" size="small" @click="handleEdit(scope.row)">修改</el-button>
            <el-button type="text" size="small" @click="handleRemove(scope.row)">删除</el-button>
          </template>
        </u-table-column>
      </dTable>
    </div>

    <!-- 添加、编辑 -->
    <el-dialog :title="setForm.id ===''?'新增客户':'修改客户'" :visible.sync="setForm.show" :before-close="handleCancel" width="30%" :close-on-click-modal="false" custom-class="cust-dialog" >
      <el-form :model="setForm" ref="setForm" label-width="100px" :rules="rules">
        <el-form-item prop="customerName">
          <template #label>
            <span>客户名称</span>
            <el-tooltip class="ml-5" effect="dark" content="输入企业的全称" placement="top">
              <i class="el-icon-warning-outline f-cursor font-16"></i>
            </el-tooltip>
          </template>
          <el-input v-model.trim="setForm.customerName" autocomplete="off" placeholder="请输入" maxlength="50" clearable></el-input>
        </el-form-item>
        <el-form-item label="销售人员" prop="saleName">
          <el-input v-model.trim="setForm.saleName" autocomplete="off" placeholder="请输入" maxlength="20" clearable></el-input>
        </el-form-item>
        <el-form-item label="申报状态" prop="status" v-if="setForm.id!==''">
          <el-select v-model="setForm.status" placeholder="请选择" class="w-p100">
            <el-option v-for="it in statusList" :key="it.dictCode" :label="it.dictName" :value="it.dictCode"></el-option>
          </el-select>
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button class="s-btn" @click="handleCancel">取 消</el-button>
        <el-button type="primary" class="s-btn" @click="handleConfirm">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import dTable from '@/components/common/table'
export default {
  name: '',
  components: { dTable },
  props: [''],
  data () {
    return {
      pathData: [],
      setForm: {
        show: false,
        id: '',
        customerName: '',
        saleName: '',
        status: ''
      },
      searchData:{
        status:[],
        saleName:"",
        custName:''
      },
      statusList: [
        { dictName: '筛选', dictCode: 1 },
        { dictName: '已签合同', dictCode: 2 },
        { dictName: '申报中', dictCode: 3 },
        { dictName: '申报完成', dictCode: 4 }
      ],
      rules: {
        customerName: [
          { required: true, message: '请输入客户名称', trigger: 'blur' }
        ],
        saleName: [
          { required: true, message: '请输入销售人员', trigger: 'blur' }
        ],
        status: [
          { required: true, message: '请选择申报状态', trigger: 'change' }
        ]
      }
    }
  },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 300 + 'px'
    },
    getStatusListName(){
      return function(dictCode){
        var str = ''
        this.statusList.forEach(item => {
          if(item.dictCode == dictCode){
            str = item.dictName
          }
        });
        return str
      }
    }
  },
  watch: {},
  created () {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = tabs[this.$route.path]
    }
  },
  beforeMount () { },
  mounted () {
    document.addEventListener('keydown',(e)=>{
      let key = e.keyCode
      if(key == 13){
        this.getTableData()
      }
    })
    this.getTableData()
  },
  methods: {
    add(){
      this.setForm = this.$options.data().setForm
      this.setForm.show = true
    },
    getTableData () {
      var params = [
        { property: 'cust_name', value: this.searchData.custName ? this.searchData.custName  : null },
        { property: 'sale_name', value: this.searchData.saleName ? this.searchData.saleName : null },
        { property: 'status', value: this.searchData.status.length > 0 ? this.searchData.status : null },
      ]
      this.$refs.table.fetch({
        query: params,
        method: 'post',
        url: '/subsidy/social/socialCost/custManage'
      })
    },
    handleView (row) {
      this.$router.push('/policyApply/detail?id=' + row.id)
    },
    handleEdit (row) {
      this.setForm.id = row.id
      this.setForm.customerName = row.custName
      this.setForm.saleName = row.saleName
      this.setForm.status = row.status
      this.setForm.show = true
    },
    handleConfirm () {
      this.$refs['setForm'].validate((valid) => {
        if (valid) {
          this.addOrUpadteCustomer()
        }
      })
    },
    handleCancel () {
      this.setForm = this.$options.data().setForm
      this.$refs.setForm.resetFields()
    },
    //新增或修改客户
    addOrUpadteCustomer(){
      this.$global.showLoading()
      this.$http({
        url: `/subsidy/social/socialCost/addCust`,
        method: 'post',
        data: {
          custName:this.setForm.customerName,
          saleName:this.setForm.saleName,
          id:this.setForm.id,
          status:this.setForm.status
        }
      }).then((data) => {
          this.$global.closeLoading()
          this.getTableData()
          this.$message.success('操作成功')
          this.handleCancel()
      }).catch(() => {
        this.fileList = []
        this.$refs.upload.clearFiles()
        this.$global.closeLoading()
      })
    },
    handleRemove(row){
      this.$confirm('是否删除此客户', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning',
      })
        .then(() => {
          this.removeCust(row.id)
        })
        .catch(() => {})
    },
    removeCust(id){
      this.$global.showLoading()
      this.$http({
        url: `/subsidy/social/socialCost/deleteCust`,
        method: 'post',
        data: {
          custId:id
        }
      }).then((data) => {
          this.$global.closeLoading()
          this.getTableData()
          this.$message.success('操作成功')
      }).catch(() => {
        this.$global.closeLoading()
      })
    }
  }
}
</script>
<style lang='less' scoped>
.display-flex{
  display: flex;
  align-items: center;
}
</style>
