<template>
  <div class="content spl-container">
    <!-- 导航 -->
    <breadcrumb :data="pathData">
    </breadcrumb>
    <div style="padding: 20px 20px 0 20px">
      <div class="mb-20">
        <el-input placeholder="请输入名称回车搜索" v-model="searchText" clearable
                  @keyup.enter.native="getTableData" @clear="getTableData"
                  style="display: inline-block;width: 300px;">
          <i @click="getTableData" slot="suffix" class="el-icon-search f-cursor font-16 fw-blod text-gray mr-5"
             style="color: #dbdbdb;vertical-align: middle"></i></el-input>
      </div>
      <!-- 表格 -->
      <dTable
        @fetch="getTableData"
        ref="dTable"
        :tableHeight="tableHeight"
        :paging="true"
        :showSelection="false"
        :showIndex="false"
        rowKey="id"
      >
        <u-table-column prop="id" label="Id" width="60" :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="name" label="名称" min-width="150" :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="addressName" label="地区" min-width="100" :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="businessType" label="业务类型" min-width="100" :show-overflow-tooltip="true">
          <template slot-scope="scope">
          <!--社保: 1001001  公积金: 1001002-->
            <span v-if="scope.row.businessType=='1001001'">社保</span>
            <span v-else-if="scope.row.businessType=='1001002'">公积金</span>
          </template>
        </u-table-column>
        <u-table-column prop="typeName" label="操作类型" min-width="100" :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="createTime" label="创建时间" min-width="100" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <span>{{$moment(scope.row.createTime).format('YYYY-MM-DD HH:mm:ss')}}</span>
          </template>
        </u-table-column>
        <u-table-column prop="creatorName" label="创建人" min-width="80" :show-overflow-tooltip="true"></u-table-column>
        <u-table-column fixed="right" label="操作" width="110">
          <template slot-scope="scope">
            <el-button type="text" size="medium" @click="goEdit(scope.row.id)">编辑</el-button>
            <el-button type="text" class="text-red" style="margin-left: 20px;" @click="doDel(scope.row.id)">删除</el-button>
          </template>
        </u-table-column>
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
      searchText: ''
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
    this.$nextTick(() => {
      this.getTableData()
    })
  },
  mounted () {
  },
  methods: {
    getTableData () {
      var params = [
        { property: 'name', value: this.searchText }
      ]
      this.$refs.dTable.fetch({
        query: params,
        method: 'post',
        url: '/policy/sys/desktopApplications/getAllPage'
      })
    },
    // 编辑
    goEdit (id) {
      this.$router.push(`/record/edit?id=${id}`)
    },
    doDel (id) {
      let that = this
      this.$confirm('是否确认删除？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning'
      }).then(() => {
        that.$http({
          url: '/policy/sys/desktopApplications/deleteById?id=' + id,
          method: 'post'
        }).then((data) => {
          if (data.data.code === 200) {
            that.$message.success('操作成功')
            this.getTableData()
          }
        }).catch(() => {
        })
      }).catch(() => {
      })
    }
  }
}
</script>

<style lang="less" scoped>
</style>
