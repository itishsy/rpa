<template>
  <div class="spl-container">
    <breadcrumb :data="pathData"></breadcrumb>
    <div class="pl-20 pr-20">
      <div class="search-row display-flex">
        <el-row class="flex1" :gutter="20">
          <el-col :span="6" style="padding-right: 20px;">
            <el-input v-model.trim="search.idVendor" placeholder="请输入idVendor" @keyup.enter.native="getTableData" clearable></el-input>
          </el-col>
          <el-col :span="6" style="padding-right: 20px;">
            <el-input v-model.trim="search.idProduct" placeholder="请输入idProduct" @keyup.enter.native="getTableData" clearable></el-input>
          </el-col>
        </el-row>
        <el-button type="primary" @click="getTableData">查询</el-button>
        <el-button type="primary" @click="bindAccount">新增指令</el-button>
      </div>
      <div>
        <dTable @fetch="getTableData" ref="dTable" :tableHeight="tableHeight" :paging="true" :showSelection="false" :showIndex="true" rowKey="id">
          <u-table-column prop="idVendor" label="idVendor" min-width="100" :show-overflow-tooltip="true"/>
          <u-table-column prop="idProduct" label="idProduct" min-width="100" :show-overflow-tooltip="true"/>
          <u-table-column prop="usbInterface" label="usbInterface" min-width="130" :show-overflow-tooltip="true"/>
          <u-table-column prop="createTime" label="创建时间" min-width="160" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <span>{{ scope.row.createTime ? $moment(scope.row.createTime).format('YYYY-MM-DD HH:mm:ss') : '' }}</span>
            </template>
          </u-table-column>
          <u-table-column label="操作" align="left" fixed="right" width="100">
            <template slot-scope="scope">
              <div class="jkh-flex">
                <el-button type="text" size="small" class="text-blue" @click="bindAccount(scope.row)">查看</el-button>
                <el-button type="text" size="small" class="text-blue" @click="deleteCert(scope.row)">删除</el-button>
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
import BindAccount from "@/views/robotManage/usbCertManage/components/BindAccount";

export default {
  components: {dTable, BindAccount},
  data() {
    return {
      pathData: [],
      search: {
        idProduct: '',
        idVendor: '',
      },
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
    this.$nextTick(() => {
      that.getTableData()
    });
  },
  methods: {
    getTableData() {
      let search = this.search
      var params = [
        {property: 'idProduct', value: search.idProduct},
        {property: 'idVendor', value: search.idVendor},
      ]
      this.$refs.dTable.fetch({
        query: params,
        method: 'post',
        url: '/robot/readUsbContent/page'
      })
    },
    bindAccount(row) {
      this.$refs.BindAccount.init(row)
    },
    deleteCert(item) {
      let that = this
      this.$confirm('是否确定删除？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: '/robot/readUsbContent/deleteById?id=' + item.id,
          method: 'post'
        }).then((data) => {
          that.getTableData()
        })
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
