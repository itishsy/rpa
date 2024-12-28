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
                         :value="item.id"></el-option>
            </el-select>
          </el-col>
          <el-col :span="6" class="display-flex">
            <span class="label">设备类型：</span>
            <el-select placeholder="请选择" filterable v-model="search.type" clearable class="flex1">
              <el-option v-for="(item, index) in options.typeList" :key="index" :label="item.name"
                         :value="item.value"></el-option>
            </el-select>
          </el-col>
        </el-row>
        <el-button type="primary" @click="getTableData">查询</el-button>
      </div>
      <div>
        <dTable @fetch="getTableData" ref="dTable" :tableHeight="tableHeight" :paging="true" :showSelection="false"
                :showIndex="true" rowKey="id">
          <u-table-column prop="companyName" label="租户名称" min-width="120" :show-overflow-tooltip="true"/>
          <u-table-column prop="type" label="类型" min-width="80" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <span v-if="scope.row.type == 0">usb-server</span>
              <span v-if="scope.row.type == 1">盒子</span>
            </template>
          </u-table-column>
          <u-table-column prop="macAddress" label="mac地址" min-width="100" :show-overflow-tooltip="true"/>
          <u-table-column prop="hostname" label="hostname" min-width="120" :show-overflow-tooltip="true"/>
          <u-table-column prop="createTime" label="创建时间" min-width="150" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <span>{{ scope.row.createTime ? $moment(scope.row.createTime).format('YYYY-MM-DD HH:mm:ss') : '' }}</span>
            </template>
          </u-table-column>
          <u-table-column prop="updateTime" label="最后更新时间" min-width="150" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <span>{{ scope.row.updateTime ? $moment(scope.row.updateTime).format('YYYY-MM-DD HH:mm:ss') : '' }}</span>
            </template>
          </u-table-column>
          <u-table-column prop="ipAddress" label="ip地址" min-width="120" :show-overflow-tooltip="true"/>
          <u-table-column prop="version" label="版本" min-width="130" :show-overflow-tooltip="true"/>
          <u-table-column prop="stautus" label="状态" min-width="80" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <span v-if="scope.row.version">运行中</span>
              <span v-if="!scope.row.version">离线</span>
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
        clientId: '',
        type: null,
      },
      options: {
        custList: [],
        typeList: [{'name': 'usb-server', 'value': 0}, {'name': '盒子', 'value': 1}],
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
    this.listCustomer()
  },
  methods: {
    getTableData() {
      let search = this.search
      var params = [
        {property: 'companyId', value: search.clientId},
        {property: 'type', value: search.type},
      ]
      if (!search.clientId) {
        this.$message.error('请选择客户')
        return;
      }
      this.$refs.dTable.fetch({
        query: params,
        method: 'post',
        url: '/robot/device/list'
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
  }
}
</script>
<style lang="less" scoped>
/deep/ .el-form-item {
  margin-bottom: 15px;
}
</style>
