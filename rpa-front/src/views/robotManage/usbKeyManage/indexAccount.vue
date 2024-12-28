<template>
  <div class="spl-container">
    <breadcrumb :data="pathData"></breadcrumb>
    <div class="pl-20 pr-20">
      <div class="search-row display-flex">
        <el-row class="flex1" :gutter="20">
          <el-col :span="6" class="display-flex">
            <span class="label">客户：</span>
            <el-select placeholder="请选择" filterable v-model="search.clientId" clearable class="flex1">
              <el-option v-for="(item, index) in options.custList" :key="index" :label="item.customerName"
                         :value="item.id" style="max-width: 260px;"></el-option>
            </el-select>
          </el-col>
          <el-col :span="6" class="display-flex">
            <span class="label">申报系统：</span>
            <el-select placeholder="请选择" filterable v-model="search.declareSystem" clearable class="flex1">
              <el-option v-for="(item, index) in options.declareSystemList" :key="index" :label="item.dictName"
                         :value="item.dictCode"></el-option>
            </el-select>
          </el-col>
          <el-col :span="6" style="padding-right: 20px;">
            <el-input v-model.trim="search.keyWord" placeholder="城市应用\申报账户搜索"
                      @keyup.enter.native="getTableData" clearable></el-input>
          </el-col>
        </el-row>
        <el-button type="primary" @click="getTableData">查询</el-button>
      </div>
      <div>
        <dTable @fetch="getTableData" ref="dTable" :tableHeight="tableHeight" :paging="true" :showSelection="false"
                :showIndex="true" rowKey="id">
          <u-table-column prop="customerName" label="客户" min-width="200" :show-overflow-tooltip="true"/>
          <u-table-column prop="appName" label="城市应用" min-width="100" :show-overflow-tooltip="true"/>
          <u-table-column prop="declareAccount" label="申报账户" min-width="250" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <span>{{ scope.row.companyName }}-{{ scope.row.declareAccount }}</span>
            </template>
          </u-table-column>
          <u-table-column prop="declareSystemName" label="申报系统" min-width="100" :show-overflow-tooltip="true"/>
          <u-table-column prop="baseHash" label="基础标识" min-width="100" :show-overflow-tooltip="true"/>
          <u-table-column prop="contextHash" label="内容标识" min-width="120" :show-overflow-tooltip="true"/>
          <u-table-column prop="hostMacAddress" label="宿主机macAddress" min-width="130" :show-overflow-tooltip="true"/>
          <u-table-column prop="status" label="状态" min-width="100" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <el-tooltip class="item" effect="dark" :disabled="scope.row.status!=4" content="请联系供应商开发" placement="top">
                <span v-if="scope.row.status!=4"> {{ statusFun(scope.row.status) }}</span>
                <span v-if="scope.row.status==4" style="color:red"> {{ statusFun(scope.row.status) }}</span>
              </el-tooltip>
            </template>
          </u-table-column>
          <u-table-column prop="updateTime" label="上报时间" min-width="160" :show-overflow-tooltip="true"/>
          <u-table-column label="操作" align="left" fixed="right" width="80">
            <template slot-scope="scope">
              <div class="jkh-flex">
                <el-button type="text" size="small" class="text-blue" @click="unBind(scope.row)">解绑</el-button>
              </div>
            </template>
          </u-table-column>
        </dTable>
      </div>
    </div>
  </div>
</template>
<script>

import dTable from '../../../components/common/table'

export default {
  components: {dTable},
  data() {
    return {
      pathData: [],
      search: {
        status: null,
        declareSystem: '',
        keyWord: ''
      },
      options: {
        custList: [],
        declareSystemList: [],
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
    this.getDictByKey(10007)
    this.getDictByKey(10008)
  },
  methods: {
    getTableData() {
      let search = this.search
      var params = [
        {property: 'clientId', value: search.clientId},
        {property: 'declareSystem', value: search.declareSystem},
        {property: 'keyword', value: search.keyWord},
        {property: 'status', value: search.status}
      ]
      this.$refs.dTable.fetch({
        query: params,
        method: 'post',
        url: '/robot/usbKey/usbAccountPage'
      })
    },
    async getDictByKey(key) {
      let that = this;
      return this.$http({
        url: '/policy/sys/dict/getDictByKey',
        method: 'get',
        params: {
          dataKey: key
        }
      }).then((res) => {
        that.options.declareSystemList.push(...res.data.data)
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
    unBind(item) {
      this.$confirm('是否确定解绑？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: '/robot/usbKey/deleteById?id=' + item.id,
          method: 'post'
        }).then((data) => {
          console.log(data)
          this.$message.success('操作成功')
          this.getTableData()
        })
      })
    },
    statusFun(val) {
      let item = this.options.statusList.find(item => item.value == val)
      if (item) {
        return item.name;
      }
      return "";
    },
  }
}
</script>
<style lang="less" scoped>
/deep/ .el-form-item {
  margin-bottom: 15px;
}
</style>
