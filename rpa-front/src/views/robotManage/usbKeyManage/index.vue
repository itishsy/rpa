<template>
  <div class="spl-container">
    <breadcrumb :data="pathData"></breadcrumb>
    <div class="pl-20 pr-20">
      <div class="search-row display-flex">
        <el-row class="flex1" :gutter="20">
          <el-col :span="5" class="display-flex">
            <span class="label">客户：</span>
            <el-select placeholder="请选择" filterable v-model="search.clientId" clearable class="flex1">
              <el-option v-for="(item, index) in options.custList" :key="index" :label="item.customerName" :value="item.id"></el-option>
            </el-select>
          </el-col>
          <el-col :span="5" class="display-flex">
            <span class="label">状态：</span>
            <el-select placeholder="请选择" filterable v-model="search.status" clearable class="flex1">
              <el-option v-for="(item, index) in options.statusList" :key="index" :label="item.name" :value="item.value"></el-option>
            </el-select>
          </el-col>
          <el-col :span="4">
            <el-input v-model.trim="search.idVendor" placeholder="请输入idVendor" @keyup.enter.native="getTableData" clearable></el-input>
          </el-col>
          <el-col :span="4">
            <el-input v-model.trim="search.idProduct" placeholder="请输入idProduct" @keyup.enter.native="getTableData" clearable></el-input>
          </el-col>
        </el-row>
        <el-button type="primary" @click="getTableData">查询</el-button>
        <el-button type="primary" @click="goAccount">KEY与账户</el-button>
      </div>
      <div>
        <dTable @fetch="getTableData" ref="dTable" :tableHeight="tableHeight" :paging="true" :showSelection="false" :showIndex="true" rowKey="id">
          <u-table-column prop="companyName" label="客户" min-width="200" :show-overflow-tooltip="true"/>
          <u-table-column prop="idVendor" label="idVendor" min-width="100" :show-overflow-tooltip="true"/>
          <u-table-column prop="idProduct" label="idProduct" min-width="100" :show-overflow-tooltip="true"/>
          <u-table-column prop="idVendorStr" label="idVendor(16进制)" min-width="150" :show-overflow-tooltip="true"/>
          <u-table-column prop="idProductStr" label="idProduct(16进制)" min-width="150" :show-overflow-tooltip="true"/>
          <u-table-column prop="status" label="状态" min-width="150" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <el-tooltip class="item" effect="dark" :disabled="scope.row.status!=4" content="请联系供应商开发" placement="top">
                <span v-if="scope.row.status!=4"> {{ statusFun(scope.row.status) }}</span>
                <span v-if="scope.row.status==4" style="color:red"> {{ statusFun(scope.row.status) }}</span>
              </el-tooltip>
            </template>
          </u-table-column>
          <u-table-column prop="baseHash" label="基础标识" min-width="100" :show-overflow-tooltip="true"/>
          <u-table-column prop="contextHash" label="内容标识" min-width="120" :show-overflow-tooltip="true"/>
          <u-table-column prop="declareAccounts" label="申报账户" min-width="200" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <span>{{ scope.row.declareAccounts }}</span>
            </template>
          </u-table-column>
          <u-table-column prop="busNumber" label="busNumber" min-width="110" :show-overflow-tooltip="true"/>
          <u-table-column prop="deviceAddress" label="deviceAddress" min-width="130" :show-overflow-tooltip="true"/>
          <u-table-column prop="portNumber" label="portNumber" min-width="120" :show-overflow-tooltip="true"/>
          <u-table-column prop="busId" label="busId" min-width="100" :show-overflow-tooltip="true"/>
          <u-table-column prop="hostIpAddress" label="宿主机ip" min-width="100" :show-overflow-tooltip="true"/>
          <u-table-column prop="hostName" label="宿主机hostName" min-width="150" :show-overflow-tooltip="true"/>
          <u-table-column prop="hostMacAddress" label="宿主机macAddress" min-width="180" :show-overflow-tooltip="true"/>
          <u-table-column prop="attachTime" label="插入时间" min-width="160" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <span>{{ scope.row.attachTime ? $moment(scope.row.attachTime).format('YYYY-MM-DD HH:mm:ss') : '' }}</span>
            </template>
          </u-table-column>
          <u-table-column prop="updateTime" label="最后上报时间" min-width="160" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <span>{{ scope.row.updateTime ? $moment(scope.row.updateTime).format('YYYY-MM-DD HH:mm:ss') : '' }}</span>
            </template>
          </u-table-column>
          <u-table-column prop="bingNum" label="绑定账户数" min-width="100" :show-overflow-tooltip="true"/>
          <u-table-column label="操作" align="left" fixed="right" width="80">
            <template slot-scope="scope">
              <div class="jkh-flex">
                <el-button type="text" size="small" class="text-blue" @click="bindAccount(scope.row)">绑定</el-button>
              </div>
            </template>
          </u-table-column>
        </dTable>
      </div>
    </div>
    <!--绑定-->
    <BindAccount ref="BindAccount" @refreshTable="getTableData"/>
  </div>
</template>
<script>

import dTable from '../../../components/common/table'
import BindAccount from "@/views/robotManage/usbKeyManage/components/BindAccount";
import indexAccount from "@/views/robotManage/usbKeyManage/indexAccount";

export default {
  components: {dTable, BindAccount, indexAccount},
  data() {
    return {
      pathData: [],
      search: {
        clientId: null,
        status: null,
        idProduct: '',
        idVendor: '',
        keyWord: ''
      },
      options: {
        custList: [],
        statusList: [{'name': '已插入未初始化', 'value': 1}, {'name': '可用', 'value': 2},
          {'name': '已被挂载', 'value': 3}, {'name': '重复KEY不可用', 'value': 4},
          {'name': '未插入', 'value': 5}],
      }
    }
  },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 270 + 'px'
    }
  },
  created() {
    let that = this
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = tabs[this.$route.path]
    }
    this.listCustomer();
    this.$nextTick(() => {
      that.getTableData()
    });
  },
  methods: {
    getTableData() {
      let search = this.search
      var params = [
        {property: 'clientId', value: search.clientId},
        {property: 'idProduct', value: search.idProduct},
        {property: 'idVendor', value: search.idVendor},
        {property: 'keyword', value: search.keyWord},
        {property: 'status', value: search.status}
      ]
      this.$refs.dTable.fetch({
        query: params,
        method: 'post',
        url: '/robot/usbKey/usbKeyPage'
      })
    },
    listCustomer() {
      this.$http({
        url: '/robot/app/client/listCustomer',
        method: 'post'
      }).then((data) => {
        this.options.custList = data.data.data
      })
    },
    bindAccount(row) {
      this.$refs.BindAccount.init(row)
    },
    statusFun(val) {
      let item = this.options.statusList.find(item => item.value == val)
      if (item) {
        return item.name;
      }
      return "";
    },
    goAccount() {
      this.$router.push('/robotManage/usbKeyAccount')
    },
  }
}
</script>
<style lang="less" scoped>
/deep/ .el-form-item {
  margin-bottom: 15px;
}
</style>
