<template>
  <!-- 报盘文件设置 -->
  <div class="spl-container">
    <breadcrumb :data="pathData">
      <div slot="breadcrumb-btn">
        <el-button type="primary" class="mr-15" size="medium" v-if="$global.hasPermission('declareAccountAdd')" @click="handleAddOrUpdateOrView('add')">新增虚拟账户</el-button>
      </div>
    </breadcrumb>
    <div class="pl-20 pr-20">
      <div class="search-row">
        <el-row>
          <el-col :span="24">
            <el-input v-model.trim="formData.searchText" placeholder="请输入城市/客户业务类型/申报账户关键字"
                      style="width: 95%;max-width: 400px" @keyup.enter.native="getTableData" clearable></el-input>
            <el-checkbox style="padding-left: 100px" class="pt-5" v-model="formData.virtually" @change="getTableData" :true-label="1" :false-label="0">只看虚拟账户</el-checkbox>
            <el-button style="margin-left: 100px;" type="primary" size="small" @click="getTableData">查询</el-button>
          </el-col>
        </el-row>
      </div>
      <div>
        <dTable @fetch="getTableData" ref="table" style="margin-top: 0;" :tableHeight="tableHeight"
                :showIndex='false' :showNotFixedIndex="true" :showSelection='false' row-key="id" row-id="id">
          <u-table-column prop="custName" label="客户名称" min-width="180" header-align="center" align="left" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="addrName" label="参保城市" min-width="100" header-align="center" align="left" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="businessTypeName" label="业务类型" min-width="80" header-align="center" align="left" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="orgName" label="申报单位"  min-width="180" header-align="center" align="left" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="accountNumber" label="申报账户" min-width="150"  header-align="center" align="left" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="taxNumber" label="纳税人识别号"  min-width="150" header-align="center" align="left" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="createTime" label="创建时间" min-width="150"  header-align="center" align="center" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="updateTime" label="更新时间" min-width="150"  header-align="center" align="center" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column label="操作" align="left" width="180">
            <template slot-scope="scope">
              <div>
                <el-button v-if="scope.row.virtually == 1 && $global.hasPermission('declareAccountEdit')" type="text" size="small" class="text-blue" @click="getEdit('edit', scope.row.baseId)">编辑虚拟账户</el-button>
                <el-button v-if="$global.hasPermission('declareAccountView')" type="text" size="small" class="text-blue" @click="getEdit('view', scope.row.baseId)">查看</el-button>
              </div>
            </template>
          </u-table-column>
        </dTable>
      </div>
    </div>

    <!--新增、编辑-->
    <viewEditDeclareAccount ref="viewEditDeclareAccount" :detailData="setForm" @success="getTableData"></viewEditDeclareAccount>
  </div>
</template>
<script>
  import dTable from '../../components/common/table'
  import viewEditDeclareAccount from './components/viewEditDeclareAccount'
  export default {
    components: {dTable, viewEditDeclareAccount},
    data() {
      return {
        pathData: [],
        formData: {
          // addrId:'',
          searchText: '',
          virtually: 0,
        },
        setForm: null,
      }
    },
    computed: {
      tableHeight: function () {
        return this.$global.bodyHeight - 310 + 'px'
      }
    },
    created() {
      let tabs = this.$store.state.tabs
      if (tabs) {
        this.pathData = tabs[this.$route.path]
      }
      this.$nextTick(()=>{
        this.getTableData()
      })
    },
    mounted() {
    },
    methods: {
      getTableData() {
        var params = [
          { property: 'searchText', value: this.formData.searchText },
          { property: 'virtually', value: this.formData.virtually }
        ]
        this.$refs.table.fetch({
          query: params,
          method: 'post',
          url: '/policy/declareAccount/page'
        })
      },
      getEdit(type, baseId) {
        let that = this
        this.$http({
          url: `/policy/declareAccount/detail/${baseId}`,
          method: 'post'
        }).then(({ data }) => {
          let item = data.data
          that.handleAddOrUpdateOrView(type, item)
        }).catch((data) => {
        })
      },
      // 新增、编辑、查看
      handleAddOrUpdateOrView (type, item) {
        if (type == 'edit' || type == 'view') {
          this.setForm = {
            show: true,
            type: type,
            customerName: item.customerName,
            orgName: item.orgName,
            taxNum: item.taxNumber,
            addrName: item.addressName,
            addrId: item.addressId,
            socialChecked: !(item.socialAccounts && item.socialAccounts.length > 0),
            accfundChecked: !(item.accfundAccounts && item.accfundAccounts.length > 0),
            socialList: item.socialAccounts,
            accfundList: item.accfundAccounts,
            virtually: item.virtually,
            custId: item.custId,
            id: '',
            isDisabled: false
          }
          if (type == 'edit') {
            this.setForm.id = item.id
          } else {
            this.setForm.isDisabled = true
          }
        } else if (type == 'add') {
          this.setForm = {
            show: true,
            type: type,
            orgName: '',
            taxNum: '',
            addrName: '',
            addrId: '',
            socialChecked: false,
            accfundChecked: false,
            socialList: [],
            accfundList: [],
            virtually: 1,
            custId: '',
            id: '',
            isDisabled: false
          }
        }
        this.$refs.viewEditDeclareAccount.setForm.show = true
      },
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

  .file-content-box{
    padding:10px 20px;
    font-size: 12px;
    .file-list{
      display: flex;
      padding: 10px 0;
      border-bottom:1px dashed #ddd;
      .file{
        background: #f1f8ff;
        border-radius: 10px;
        padding:3px 10px;
        position: relative;
        cursor: pointer;
        margin-right:15px;
        font-size: 12px;
        &:hover{
          color: #3e82ff;
          text-decoration: underline;
        }
        .file-remove{
          position: absolute;
          right: -10px;
          top:-10px;
          width: 20px;
          height: 20px;
          background: red;
          border-radius: 50%;
          display: none;
          &::before{
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
        &:hover .file-remove{
          display: block;
        }
      }
    }
    .upload-tips{
      margin-top:20px;
      font-size: 12px;
      color:#797979;
    }
  }
  .upload-file-box{
    margin-top:10px;
    border-bottom:1px dashed rgb(221, 221, 221);
    padding-bottom: 10px;
    input[type=text]{
      border: 1px solid #ddd;
      height: 28px;
      width: 250px;
      outline: none;
    }
    .upload-file-btn{
      height: 32px;
      background: #3e82ff;
      color: white;
      padding:0 10px;
      box-sizing: border-box;
      font-size: 12px;
      cursor: pointer;
      line-height: 32px;
      margin-left:10px;
    }
    .upload-file{
      padding: 4px 10px;
      height: 32px;
      line-height: 20px;
      position: relative;
      cursor: pointer;
      color: #888;
      background: #fafafa;
      border: 1px solid #ddd;
      display: inline-block;
      box-sizing: border-box;
      input[type=file] {
        position: absolute;
        font-size: 17px;
        right: 0;
        top: 0;
        opacity: 0;
        width: 295px;
        filter: alpha(opacity=0);
        cursor: pointer;
        &:hover {
          color: #444;
          background: #eee;
          border-color: #ccc;
          text-decoration: none
        }
      }
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
  .tag:hover, .tag.active {
    background-color: #dddddd;
  }
  .table-fileName-list{
    cursor: pointer;
    &:hover{
      color:#3e82ff;
      text-decoration:underline;
    }
  }
  .text-blue{
    &:hover{
      text-decoration: underline;
    }
  }
</style>
<style lang="less" scoped>
  body .el-table th.gutter {
    display: table-cell !important;
  }
  .error{
    margin-top:10px;
    /* position: absolute; */
    left: 0;
    /* top: 120%; */
    color:#F56C6C;
    font-size: 12px;
  }
  /deep/.el-drawer__body{
    padding-bottom: 0;
  }
  /deep/.el-dialog__header{
    border-bottom: none!important;
  }
  .file-content-box /deep/.table-header {
    background: #f5f5f5 !important;
    color: #444;
  }
  .file-content-box /deep/.el-table__cell {
    border-bottom: 1px solid #ddd;
    border-right: 1px solid #ddd;
  }
  .file-content-box /deep/.el-table__row {
    border-bottom: 1px solid #ddd;
    border-right: 1px solid #ddd;
  }
  .file-content-box /deep/.el-table--border {
    border: 1px solid #ddd;
  }
  /deep/.el-table .el-table__cell {
    padding: 0px 0;
  }
  .dialog-footer{
    display: flex;
    justify-content: center;
  }
  .placeholderStyle{
    padding-left:10px;
    padding-right:10px;
  }
  .placeholderStyle::-webkit-input-placeholder {
    font-size: 12px;
    color: #C0C4CC;
  }
  .cust-dialog /deep/.el-table__body tr.hover-row>td.el-table__cell{
    background-color: rgba(0, 0, 0, 0) !important;
  }

  /deep/.table-header{
    background-color: #f2f2f2 !important;
    padding: 6px 0;
    border-bottom: 1px solid #e2e2e2 !important;
    border-top: 1px solid #e2e2e2 !important;
    border-right: 1px solid #e2e2e2 !important;
    border-right: transparent;
    text-align: left;
  }
  /deep/.table-header .cell{
    font-weight: bold;
    color: #303133;
  }
  .cust-dialog /deep/.el-table .el-table__cell{
    padding: 6px 0;
  }

</style>
