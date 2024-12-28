<template>
  <div class="spl-container">
    <breadcrumb :data="pathData">
      <el-button type="primary" size="small" slot="breadcrumb-btn" @click="setForm.show=true" class="ml-20">定制服务维护</el-button>
    </breadcrumb>
    <div class="pl-20 pr-20">
      <el-row>
        <el-col :span="24">
          <div class="search-row">
            <el-row>
              <el-col :span="10">
                <el-input @keyup.enter.native="search" v-model.trim="search.keyword" clearable placeholder="请输入参保城市/服务代码/定制内容/业务类型关键字" style="width: 400px" ></el-input>
              </el-col>
              <el-col :span="8">
                <span class="lab">申报账户：</span>
                <el-select v-model="search.declareAccount" class="w-300" clearable filterable placeholder="请选择">
                  <el-option v-for="(it, ind) in options.declareAccount" :key="ind" :label="it.name" :value="it.name"></el-option>
                </el-select>
              </el-col>
            </el-row>
            <el-row class="mt-15">
              <el-col :span="10">
                <span class="lab">客户名称：</span>
                <el-select v-model="search.customerId" style="width: 330px;" clearable filterable placeholder="请选择">
                  <el-option v-for="(item,index) in options.customer"
                             :key="index"
                             :label="item.customerName"
                             :value="item.id"></el-option>
                </el-select>
              </el-col>
              <el-col :span="8" class="display-flex">
                <span class="lab pt-5">服务状态：</span>
                <el-checkbox-group v-model="search.serviceStatus" size="medium" class="checkbox-group">
                  <el-checkbox-button label="2">已上线</el-checkbox-button>
                  <el-checkbox-button label="1">未上线</el-checkbox-button>
                </el-checkbox-group>
              </el-col>
              <el-col :span="6" class="text-right">
                <el-button type="primary" class="w-60" @click="doSearch">查询</el-button>
              </el-col>
            </el-row>
          </div>
        </el-col>
      </el-row>
      <div>
        <dTable @fetch="getTableData" ref="table" style="margin-top: 0;" :tableHeight="tableHeight"  :showIndex='true'
                :showSelection='false' row-key="sid" row-id="sid" @row-click="handleView">
          <u-table-column prop="customerName" label="客户名称" min-width="100" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="declareAccount" label="申报账户" min-width="150" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="addrName" label="参保城市" min-width="100" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="businessType" label="业务类型" min-width="100" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <p>{{scope.row.businessType=='1'?'社保':'公积金'}}</p>
            </template>
          </u-table-column>
          <u-table-column prop="name" label="服务代码" min-width="100" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <p class="text-blue f-cursor">{{scope.row.serviceCode}}</p>
            </template>
          </u-table-column>
          <u-table-column prop="content" label="定制内容" min-width="200" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="serviceStatus" label="服务状态" min-width="100" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <!--服务状态 1--未上线 2--已上线-->
              <p>{{scope.row.serviceStatus=='1'?'未上线':'已上线'}}</p>
            </template>
          </u-table-column>
        </dTable>
      </div>
    </div>

    <!-- 定制服务维护 -->
    <el-drawer :title="setForm.isAdd?'添加定制服务':'定制服务维护'" :visible.sync="setForm.show" :wrapperClosable="false" custom-class="spl-filter-drawer set-drawer" :show-close="true" :before-close="clearSetForm">
      <div class="drawer-body pt-20 pb-20" v-show="setForm.show">
        <el-form ref="setForm" :model="setForm" class="filter-form">

          <p class="fw-blod mb-10 clearfix" style="margin-left: -20px;">服务内容
            <span class="fr text-blue f-cursor" style="font-weight: normal" v-show="!setForm.isAdd" @click="handleAdd"><i class="el-icon-plus fw-blod"></i> 添加定制服务</span></p>

          <label class="required item-label">定制内容</label>
          <!--编辑-->
          <el-form-item v-if="!setForm.isAdd" label="" prop="contentEdit" :rules="[{ required: true, message: '请输入定制内容',trigger:'change' }]">
            <el-select
              class="w-p100"
              v-model="setForm.contentEdit"
              clearable
              filterable
              remote
              placeholder="请输入关键词"
              :remote-method="remoteMethodContent"
              :loading="setForm.contentLoading"
              @change="changeContentEdit">
              <el-option
                v-for="item in options.content"
                :key="item"
                :label="item"
                :value="item">
              </el-option>
            </el-select>
          </el-form-item>

          <!--添加-->
          <el-form-item v-else label="" prop="contentAdd" :rules="[{ required: true, message: '请输入定制内容',trigger:'blur' }, { validator: checkContentExist, trigger:'blur'}]">
            <el-input v-model.trim="setForm.contentAdd" class="w-p100" maxlength="255" clearable placeholder="请输入"></el-input>
          </el-form-item>

          <label class="required item-label">服务代码</label>
          <el-form-item label="" prop="serviceCode" :rules="[{ required: true, message: '请输入服务代码',trigger: ['change']}]">
            <el-input v-model.trim="setForm.serviceCode" class="w-p40" maxlength="255" disabled></el-input>
          </el-form-item>

          <label class="required item-label">服务板块</label>
          <el-form-item label="" prop="serviceBlockCode" :rules="[{ required: true, message: '请选择服务板块',trigger: ['change']}]">
            <el-select v-model="setForm.serviceBlockCode" class="w-p40" placeholder="请选择" clearable filterable :disabled="!setForm.isAdd">
              <el-option v-for="it in options.serviceBlockCode" :key="it.dictCode" :label="it.dictName" :value="it.dictCode"></el-option>
            </el-select>
          </el-form-item>

          <label class="required item-label">数据范围</label>
          <el-form-item label="" prop="dataRange" :rules="[{ required: true, message: '请选择数据范围',trigger: ['change']}]">
            <el-select v-model="setForm.dataRange" class="w-p40" placeholder="请选择" clearable filterable :disabled="!setForm.isAdd">
              <el-option label="标记定制的数据" :value="2"></el-option>
              <el-option label="该户全部数据" :value="1"></el-option>
            </el-select>
          </el-form-item>

          <label class="required item-label">参保城市</label>
          <el-form-item label="" prop="addrName" :rules="[{ required: true, message: '请选择参保城市',trigger: ['blur', 'change']}]">
            <addrSelector v-model="setForm.addrName" :addrArr="options.addrArr" @changeAddrSelect="getAddrId" width="276px" :disabled="!setForm.isAdd"></addrSelector>
          </el-form-item>

          <label class="required item-label">业务类型</label>
          <el-form-item label="" prop="businessType" :rules="[{ required: true, message: '请选择业务类型',trigger: ['blur', 'change']}]">
            <el-select v-model="setForm.businessType" class="w-p40" placeholder="请选择" clearable filterable @change="getContentDeclareAccount" :disabled="!setForm.isAdd">
              <el-option label="社保" :value="1"></el-option>
              <el-option label="公积金" :value="2"></el-option>
            </el-select>
          </el-form-item>

          <label class="item-label">备注</label>
          <el-form-item label="" prop="comments">
            <el-input type="textarea" :rows="2" v-model.trim="setForm.comments" class="w-p100" maxlength="255" clearable placeholder="请输入"></el-input>
          </el-form-item>

          <label class="item-label">异常原因反馈</label>
          <el-form-item label="" prop="errorMessage">
            <el-input v-model.trim="setForm.errorMessage" class="w-p100" maxlength="255" clearable placeholder="请输入"></el-input>
          </el-form-item>

          <p class="fw-blod mb-10 mt-20" style="margin-left: -20px;">添加定制客户</p>

          <el-form-item label="" prop="contentAccountObj">
            <el-select v-model="setForm.contentAccountObj" value-key="name" class="w-p40 placeholder-red" placeholder="请先选择申报账户" clearable filterable>
              <el-option v-for="it in setForm.declareAccountOptions" :key="it.accountNumber" :label="it.name" :value="it"></el-option>
            </el-select>
            <el-button type="primary" icon="el-icon-plus" class="s-btn" @click="addDeclareAccountItems">添加</el-button>
          </el-form-item>

          <div>
            <div class="mr-15 mt-5 mb-5 inlineBlock" v-for="(tag, tagIndex) in setForm.declareAccountItems" :key="tagIndex">
              <el-tag
                @close="removeDeclareAccount(tagIndex)"
                closable>
                {{tag.name}}
              </el-tag>
            </div>
          </div>

          <el-row class="mt-30" v-show="!setForm.isAdd">
            <el-col :span="8">定制服务数：{{setForm.serviceCount}}</el-col>
            <el-col :span="8" class="text-center">申报流程板块数：{{setForm.declareCount}}</el-col>
            <el-col :span="8" class="text-right">数据校验板块：{{setForm.checkCount}}</el-col>
          </el-row>

        </el-form>
      </div>
      <div class="drawer-footer-buts">
        <el-button class="mr-12" size="small" @click="clearSetForm()">取 消</el-button>
        <el-button size="small" type="primary" @click="vaildSetForm()" :loading="setForm.saveLoading">确定</el-button>
      </div>
    </el-drawer>

    <!-- 查看定制服务 -->
    <el-drawer title="查看" :visible.sync="viewInfo.show" :wrapperClosable="false" custom-class="spl-filter-drawer set-drawer" :show-close="true">
      <div class="drawer-body pt-20 pb-20" v-show="viewInfo.show" v-if="viewInfo.rowData">
        <p class="fw-blod mb-10 clearfix" style="margin-left: -20px;">服务内容</p>
        <div class="view-row">
          <label class="required item-label">定制内容</label>
          <label class="item-sel">{{viewInfo.rowData.content}}</label>
        </div>
        <div class="view-row">
          <label class="required item-label">服务代码</label>
          <label class="item-sel">{{viewInfo.rowData.serviceCode}}</label>
        </div>
        <div class="view-row">
          <label class="required item-label">服务板块</label>
          <label class="item-sel">{{viewInfo.rowData.serviceBlockName}}</label>
        </div>
        <div class="view-row">
          <label class="required item-label">数据范围</label>
          <label class="item-sel">{{viewInfo.rowData.dataRangeName}}</label>
        </div>
        <div class="view-row">
          <label class="required item-label">参保城市</label>
          <label class="item-sel">{{viewInfo.rowData.addrName}}</label>
        </div>
        <div class="view-row">
          <label class="required item-label">业务类型</label>
          <label class="item-sel">{{viewInfo.rowData.businessTypeName}}</label>
        </div>
        <div class="view-row">
          <label class="item-label">备注&nbsp;&nbsp;</label>
          <label class="item-sel">{{viewInfo.rowData.comments}}</label>
        </div>
        <div class="view-row">
          <label class="item-label">异常原因反馈&nbsp;&nbsp;</label>
          <label class="item-sel">{{viewInfo.rowData.errorMessage}}</label>
        </div>
        <div class="view-row">
          <label class="required item-label">定制客户</label>
          <label class="item-sel">{{viewInfo.rowData.declareAccount}}</label>
        </div>
        <div class="view-row" v-if="viewInfo.rowData.serviceStatus=='1'">
          <label class="required item-label" style="padding-top: 3px;">服务状态</label>
          <label class="item-sel" style="flex: none">未上线<el-button class="ml-20" type="primary" size="small" @click="handleAdjustStatus">调整已上线</el-button>
          </label>
        </div>
        <div class="view-row" v-else>
          <label class="required item-label">服务状态</label>
          <label class="item-sel">已上线</label>
        </div>
      </div>
      <div class="drawer-footer-buts">
        <el-button class="mr-12" type="primary" size="small" @click="viewInfo.show = false">关 闭</el-button>
      </div>
    </el-drawer>

  </div>
</template>
<script>

import dTable from '../../components/common/table'
import addrSelector from '../../components/common/addrSelector'
export default {
  components: { dTable, addrSelector },
  data () {
    return {
      pathData: [],
      search: {
        keyword: '',
        declareAccount: '',
        customerId: '',
        serviceStatus: []
      },
      options: {
        customer: [],
        declareAccount: [],
        content: [],
        serviceBlockCode: [],
        addrArr: []
      },
      platform: '',
      allRoleList: [],
      setForm: {
        show: false,
        isAdd: false,
        contentLoading: false,
        saveLoading: false,
        contentEdit: '',
        contentAdd: '',
        serviceCode: '',
        serviceBlockCode: '',
        dataRange: '',
        addrName: '',
        addrId: '',
        businessType: '',
        comments: '',
        errorMessage: '',
        declareAccountItems: [],

        contentAccountObj: null,
        declareAccountOptions: [],

        serviceCount: 0,
        declareCount: 0,
        checkCount: 0
      },
      viewInfo: {
        show: false,
        rowData: null
      }
    }
  },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 320 + 'px'
    }
  },
  watch: {

  },
  created () {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = this.$global.deepcopyArray(tabs[this.$route.meta.parentPath])
    }
    this.pathData.push({name: '定制服务'})

    this.getListCustomer() // 获取客户
    this.getDeclareAccount() // 获取申报账户
    this.getAddrArr() // 获取参保地
    this.getDictByKey('10018', 'serviceBlockCode') // 获取字典值-服务板块
  },
  mounted () {
  },
  methods: {
    getListCustomer() {
      this.$http({
        url: '/robot/app/client/listCustomer',
        method: 'post',
      })
        .then((data) => {
          this.options.customer = data.data.data
        })
        .catch((err) => { })
    },
    getDeclareAccount() {
      this.$http({
        url: '/policy/declareAccount/seeMainBody',
        method: 'post',
        params: {
          custLimit: 2
        }
      })
        .then((data) => {
          console.log(data)
          this.options.declareAccount = data.data.data
        })
        .catch((err) => { })
    },
    // 获取参保地
    getAddrArr() {
      let that = this;
      this.$http({
        url: '/policy/declareAccount/availableAddress',
        method: 'post'
      }).then(({data}) => {
        that.options.addrArr = data.data
      }).catch((data) => {
      })
    },
    doSearch () {
      this.$nextTick(() => {
        this.getTableData()
      })
    },
    getTableData () {
      var params = [
        { property: 'declareAccount', value: this.search.declareAccount },
        { property: 'customerId', value: this.search.customerId },
        { property: 'serviceStatus', value: this.search.serviceStatus },
        { property: 'keyword', value: this.search.keyword }
      ]
      this.$refs.table.fetch({
        query: params,
        method: 'post',
        url: '/policy/serviceCustomized/listServiceCustomizedConsumption'
      })
    },
    // 获取字典值
    getDictByKey (key, field) {
      let that = this
      this.$http({
        url: 'policy/sys/dict/getDictByKey',
        method: 'get',
        params: {
          dataKey: key
        }
      }).then(res => {
        that.options[field] = res.data.data
      }).catch()
    },

    // 新增客户
    clearSetForm (tab) {
      this.setForm = {
        show: false,
          isAdd: false,
          contentLoading: false,
          saveLoading: false,
          contentEdit: '',
          contentAdd: '',
          serviceCode: '',
          serviceBlockCode: '',
          dataRange: '',
          addrName: '',
          addrId: '',
          businessType: '',
          comments: '',
          errorMessage: '',
          declareAccountItems: [],

          contentAccountObj: null,
          declareAccountOptions: [],

          serviceCount: 0,
          declareCount: 0,
          checkCount: 0
      }
      this.$refs.setForm.resetFields()
    },
    vaildSetForm () {
      var that = this
      this.$refs.setForm.validate((valid) => {
        if (valid) {
          that.setForm.saveLoading = true
          var formData = this.setForm
          var url = ''
          if(formData.isAdd){
            url = '/policy/serviceCustomized/addServiceCustomized'
          }else{
            url = '/policy/serviceCustomized/editServiceCustomer'
          }
          that.$http({
            url: url,
            method: 'POST',
            data: {
              content: formData.isAdd ? formData.contentAdd : formData.contentEdit,
              serviceCode: formData.serviceCode,
              serviceBlockCode: formData.serviceBlockCode,
              dataRange: formData.dataRange,
              addrName: formData.addrName,
              addrId: formData.addrId,
              businessType: formData.businessType,
              comments: formData.comments,
              errorMessage: formData.errorMessage,
              declareAccountItems: formData.declareAccountItems
            }
          }).then(({ data }) => {
            that.setForm.saveLoading = false
            that.$message.success('操作成功')
            that.getTableData()
            that.clearSetForm()
          }).catch(() => {
            that.setForm.saveLoading = false
          })
        }
      })
    },

    handleAdd(){
      this.setForm.isAdd = true;
      this.setForm.contentAdd = ''
      this.setForm.serviceCode = ''
      this.setForm.serviceBlockCode = ''
      this.setForm.dataRange = ''
      this.setForm.addrName = ''
      this.setForm.addrId = ''
      this.setForm.businessType = ''
      this.setForm.comments = ''
      this.setForm.errorMessage = ''
      this.setForm.declareAccountItems = []
      this.setForm.contentAccountObj = null
      this.setForm.declareAccountOptions = []
      this.$refs.setForm.resetFields()
      // 获取服务码
      this.$http({
        url: '/policy/serviceCustomized/getRandomCode',
        method: 'post'
      }).then(res => {
        this.setForm.serviceCode = res.data.data
      }).catch()
    },

    // 远程搜索定制内容
    remoteMethodContent(query) {
      if (query.trim() !== '') {
        this.setForm.contentLoading = true;
        this.$http({
          url: '/policy/serviceCustomized/listContent?content=' + query.trim(),
          method: 'post'
        }).then(res => {
          this.setForm.contentLoading = false;
          this.options.content = res.data.data;
        }).catch()
      } else {
        this.options.content = [];
      }
    },
    changeContentEdit(){
      var val = this.setForm.contentEdit
      if(val==''){
        return
      }
      this.getServiceCustCount(val)
      this.$http({
        url: '/policy/serviceCustomized/getServiceCustomizedByContent?content=' + val,
        method: 'post'
      }).then(data => {
        var res = data.data.data
        this.setForm.serviceCode = res.serviceCode
        this.setForm.serviceBlockCode = res.serviceBlockCode
        this.setForm.dataRange = res.dataRange
        this.setForm.addrName = res.addrName
        this.setForm.addrId = res.addrId
        this.setForm.businessType = res.businessType
        this.setForm.comments = res.comments
        this.setForm.errorMessage = res.errorMessage
        this.setForm.declareAccountItems = res.declareAccountItems
        this.getContentDeclareAccount('init')
      }).catch()
    },
    getServiceCustCount(val){
      let that = this
      this.$http({
        url: '/policy/serviceCustomized/serviceCustCount?content=' + val,
        method: 'post'
      }).then(data => {
        var res = data.data.data
        that.setForm.serviceCount = res['定制服务数']
        that.setForm.declareCount = res['申报流程板块数']
        that.setForm.checkCount = res['数据校验板块数']
      }).catch()
    },
    // 改变社保-参保地
    getAddrId (item) {
      if(this.setForm.addrId==item.id){
        return
      }
      this.setForm.addrId = item.id
      this.getContentDeclareAccount()
    },

    getContentDeclareAccount (type) {
      var businessType = this.setForm.businessType
      var addrId = this.setForm.addrId
      this.setForm.contentAccountObj = null
      if(type!='init'){
        this.setForm.declareAccountItems = []
      }
      if(businessType=='' || addrId==''){
        return
      }
      this.$http({
        url: '/policy/declareAccount/seeMainBody',
        method: 'post',
        params: {
          custLimit: 2,
          bussinessType: businessType,
          addressId: addrId
        }
      }).then((data) => {
        this.setForm.declareAccountOptions = data.data.data
      }).catch((err) => { })
    },
    addDeclareAccountItems () {
      var obj = this.setForm.contentAccountObj
      if(!obj){
        return
      }
      var declareAccountItems = this.setForm.declareAccountItems
      for(let i=0;i<declareAccountItems.length;i++){
        if(declareAccountItems[i].accountNumber == obj.accountNumber){
          this.$message.warning('已存在')
          return
        }
      }
      declareAccountItems.push({
        baseId: obj.id,
        name: obj.name,
        accountNumber: obj.accountNumber
      })

    },
    removeDeclareAccount(index){
      this.setForm.declareAccountItems.splice(index, 1)
    },

    checkContentExist(rule, value, callback) {
      this.$http({
        url: '/policy/serviceCustomized/verifServiceCust',
        method: 'post',
        data: {
          content: value
        },
        headers: {
          customSuccess: true
        }
      }).then((data) => {
        console.log(data)
        if(data.data.code=='200'){
          callback()
        }else{
          callback(new Error(data.data.message?data.data.message:'定制内容已存在，请勿重复添加'))
        }
      }).catch((err) => { })
    },

  //  查看服务
    handleView (row) {
      this.viewInfo.rowData = row
      this.viewInfo.show = true
    },
  //  调整已上线
    handleAdjustStatus () {
      var row = this.viewInfo.rowData
      this.$confirm('是否确定调整已上线？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url: '/policy/serviceCustomized/changeServiceStatus',
            method: 'post',
            data: {
              sid: row.sid
            }
          }).then((res) => {
            if(res.data.code == 200){
              this.getTableData()
              this.viewInfo.rowData.serviceStatus = 2
              this.$message.success('操作成功')
            }
          }).catch((err) => {
          })
        }).catch(() => {})
    }
  }
}
</script>
<style lang="less" scoped>
  /deep/.el-dialog__header{
    padding:10px 20px;
  }
  /deep/.el-dialog__body{
    padding:10px 10px;
  }
  /deep/.el-form-item__content{
    line-height: 0px;
  }

  /deep/.el-form-item{
    margin-bottom: 10px;
  }

  /deep/.checkbox-group{
    .el-checkbox-button .el-checkbox-button__inner{
      width: 150px;
    }
  }
  /deep/.set-drawer{
    width: 800px!important;
  }
  /deep/.placeholder-red{
    input::input-placeholder{
      color: red;
    }
    input::-webkit-input-placeholder{	//兼容WebKit browsers（Chrome的内核）
      color: red;
    }
    input::-moz-placeholder{			//Mozilla Firefox 4 to 18
      color: red;
    }
    input::-moz-placeholder{			//Mozilla Firefox 19+
      color: red;
    }
    input::-ms-input-placeholder{		//Internet Explorer 10+
      color: red;
    }
  }
  .view-row{
    display: flex;
    margin: 20px;
    .item-label{
      line-height: normal;
      color: #606266;
      min-width: 95px;
      text-align: right;
      vertical-align: top;
    }
    .item-sel{
      flex: 1;
      padding-left: 20px;

    }
  }

</style>
