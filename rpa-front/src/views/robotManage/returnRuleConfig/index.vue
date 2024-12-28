<template>
  <div class="spl-container">
    <!-- 导航 -->
    <breadcrumb :data="pathData"></breadcrumb>
    <div style="padding: 0px 20px 0 20px">
      <div class="search-row clearfix">
        <el-row>
          <el-col :span="8" class="display-flex">
            <span class="label">参保城市：</span>
            <addrSelector v-model="search.addrName" :addrArr="allAddr" @changeAddrSelect="changeAddrSelect"></addrSelector>
          </el-col>
          <el-col :span="8" class="display-flex">
            <span class="label">业务类型：</span>
            <el-checkbox-group v-model="search.businessType" size="medium">
              <el-checkbox-button :label="1">社保</el-checkbox-button>
              <el-checkbox-button :label="2">公积金</el-checkbox-button>
            </el-checkbox-group>
          </el-col>
          <el-col :span="8" class="display-flex">
            <span class="label">申报类型：</span>
            <el-checkbox-group v-model="search.declareType" size="medium">
              <el-checkbox-button
                v-for="item in typeArr" :key="item.id" :label="item.id">{{ item.name }}
              </el-checkbox-button>
            </el-checkbox-group>
          </el-col>
        </el-row>
        <el-row class="mt-10">
          <el-col :span="8" class="display-flex">
            <el-input v-model="search.keyword" placeholder="回盘信息关键字" clearable style="width: 330px;"></el-input>
          </el-col>
          <el-col :span="16" class="text-right">
            <el-button class="w-60 mr-10" type="primary" @click="getTableData">查询</el-button>
            <el-button class="btn--border-blue w-60" @click="goAddOrEdit()">新建</el-button>
          </el-col>
        </el-row>
      </div>
      <!-- 表格 -->
      <dTable @fetch="getTableData" ref="dTable" :tableHeight="tableHeight" :paging="true" :showSelection="false" :showIndex="true" rowKey="id">
        <u-table-column prop="addrName" label="参保城市" min-width="120" :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="businessType" label="业务类型" min-width="120" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <p>{{scope.row.businessType == 1 ? '社保':'公积金'}}</p>
          </template>
        </u-table-column>
        <u-table-column prop="declareType" label="申报类型" min-width="120" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <p v-if="scope.row.declareType===1">增员</p>
            <p v-else-if="scope.row.declareType===2">减员</p>
            <p v-else-if="scope.row.declareType===3">调基</p>
            <p v-else-if="scope.row.declareType===5">补缴</p>
          </template>
        </u-table-column>
        <u-table-column prop="declareWebsiteName" label="申报网站" min-width="120" :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="currentNodeName" label="当前节点" min-width="120" :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="status" label="状态" min-width="120" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <p>{{scope.row.status == 1 ? '启用':'禁用'}}</p>
          </template>
        </u-table-column>
        <u-table-column fixed="right" label="操作" :width="120">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="goAddOrEdit(scope.row.id)">编辑规则</el-button>
          </template>
        </u-table-column>
      </dTable>
    </div>
  </div>
</template>

<script>
import dTable from 'components/common/table'
import addrSelector from 'components/common/addrSelector.vue'

export default {
  components: { addrSelector, dTable },
  data () {
    return {
      pathData: [],
      allAddr: [],
      typeArr: [
        // 变更类型（1增，2减，3调基，5补缴）
        { id: '1', name: '增员' },
        { id: '2', name: '减员' },
        { id: '3', name: '调基' },
        { id: '5', name: '补缴' }
      ],
      search: {
        addrId: '',
        addrName: '',
        businessType: [],
        declareType: [],
        keyword: ''
      }
    }
  },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 270 + 'px'
    }
  },
  watch: {},
  created () {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = tabs[this.$route.path]
    }
  },
  mounted () {
    this.getAddr()
    this.$nextTick(() => {
      this.getTableData()
    })
  },
  methods: {
    // 获取参保地
    getAddr (type) {
      this.$http({
        url: 'policy/declareAddr/addrList',
        method: 'post'
      }).then(({ data }) => {
        this.allAddr = data.data
      })
    },
    // 改变参保地
    changeAddrSelect (item) {
      if (this.search.addrId == item.id) {
        return
      }
      this.search.addrId = item.id
    },
    getTableData () {
      let search = this.search
      var params = [
        { property: 'addrId', value: search.addrId },
        { property: 'businessType', value: search.businessType },
        { property: 'declareType', value: search.declareType },
        { property: 'keyword', value: search.keyword }
      ]
      this.$refs.dTable.fetch({
        query: params,
        method: 'post',
        url: '/robot/offerRule/page'
      })
    },
    goAddOrEdit (id) {
      let query = ''
      if (id) {
        query = '?id=' + id
      }
      this.$router.push('/returnRuleConfig/addOrEdit' + query)
    }
  }
}
</script>

<style lang="less" scoped>

</style>
