<template>
  <div class="content spl-container">
    <!-- 导航 -->
    <breadcrumb :data="pathData">
    </breadcrumb>
    <div style="padding: 0px 20px 0 20px">
      <div class="search-row clearfix">
        <el-row>
          <el-col :span="7" class="display-flex">
            <span class="label">客户：</span>
            <el-select
              v-model="searchData.clientId"
              class="search-row-item"
              clearable
              filterable
              placeholder="请选择"
            >
              <el-option
                v-for="item in listCustomerOption"
                :key="item.id"
                :label="item.customerName"
                :value="item.id"
              ></el-option>
            </el-select>
          </el-col>
          <el-col :span="9" class="display-flex">
            <span class="label">上报时间：</span>
            <div class="flex1" style="display: flex; align-items: center; padding-right: 0px">
              <el-date-picker
                v-model="searchData.startDate"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="开始时间"
                style="width: 150px"
              ></el-date-picker>
              <span style="margin: 0 5px">至</span>
              <el-date-picker
                v-model="searchData.endDate"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="结束时间"
                style="width: 150px"
              ></el-date-picker>
            </div>
          </el-col>
          <el-col :span="6" class="display-flex">
            <span class="label">盒子id：</span>
            <el-select
              v-model="searchData.boxId"
              class="search-row-item"
              clearable
              filterable
              placeholder="请选择"
            >
              <el-option
                v-for="it in statusList"
                :key="it.id"
                :label="it.name"
                :value="it.id"
              ></el-option>
            </el-select>
          </el-col>
          <el-col :span="2" class="text-right">
           
            <el-button class="ml-15" size="small" type="primary" @click="getTableData">查询</el-button>
          </el-col>
        </el-row>
      </div>
      <!-- 表格 -->
      <dTable
        @fetch="getTableData"
        ref="dTable"
        :tableHeight="tableHeight"
        :paging="true"
        :showSelection="true"
        :showIndex="true"
        rowKey="id"
      >
        <u-table-column prop="clientName" label="上报时间" min-width="0" :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="clientName" label="客户" min-width="200" :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="clientName" label="盒子id" min-width="140" :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="clientName" label="cpu使用占比" min-width="110"
                        :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="clientName" label="内存使用占比" min-width="110" :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="clientName" label="服务失联次数" min-width="110" :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="clientName" label="心跳异常次数" min-width="110" :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="clientName" label="盒子失联次数" min-width="110" :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="clientName" label="盒子心跳异常次数" min-width="140" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <span class="view-text">{{scope.row.statusName}}</span>
          </template>
        </u-table-column>
        <u-table-column prop="clientName" label="rpa执行异常次数" min-width="130"
                        :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="clientName" label="升级失败次数" min-width="110" :show-overflow-tooltip="true"></u-table-column>
      </dTable>
    </div>

  </div>
</template>

<script>
import dTable from '../../components/common/table'

export default {
  components: {
    dTable
  },
  data () {
    return {
      pathData: [],
      searchData: {
        clientId: '',
        startDate: '',
        endDate: '',
        boxId: ''
      },
      listCustomerOption: [],
      statusList: [
        { name: 'New', id: 0 },
        { name: 'Runnable', id: 1 },
        { name: 'Running', id: 2 },
        { name: 'Closed', id: 3 },
        { name: 'RobotError', id: 4 },
        { name: 'ClientError', id: 5 },
        { name: 'Upgrading', id: 6 },
        { name: 'UpgradeFailed', id: 7 },
        { name: 'Offline', id: 8 }
      ]
    }
  },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 270 + 'px'
    }
  },
  created () {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = tabs[this.$route.path]
    }
    this.getlistCustomer()
    this.$nextTick(() => {
      this.getTableData()
    })
  },
  mounted () {
  },
  methods: {
    getTableData () {
      let searchData = this.searchData
      if (searchData.startDate != '' && searchData.endDate != '' && new Date(searchData.startDate) > new Date(searchData.endDate)) {
        this.$message.warning('上报开始时间不能大于结束时间')
        return
      }
      var params = [
        { property: 'clientId', value: searchData.clientId },
        { property: 'startDate', value: searchData.startDate },
        { property: 'endDate', value: searchData.endDate },
        { property: 'boxId', value: searchData.boxId }
      ]
      this.$refs.dTable.fetch({
        query: params,
        method: 'post',
        url: '/robot/version/client/list'
      })
    },
    getlistCustomer () {
      this.$http({
        url: '/robot/app/client/listCustomer',
        method: 'post'
      })
        .then((data) => {
          this.listCustomerOption = data.data.data
        })
        .catch((err) => {
        })
    }
  }
}
</script>

<style lang="less" scoped>
  .search-row-item {
    width: 80%;
    min-width: 150px;
    max-width: 300px;
  }

  .view-text {
    color: @blueColor;
    text-decoration: underline;
    cursor: pointer;
  }
</style>
